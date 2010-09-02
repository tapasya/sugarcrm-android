/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imaginea.android.sugarcrm;

import android.content.Context;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Modified version of AsyncTask borrowed from Android open source which does not post to UI but
 * offers benefits in thread pooling etc, and additionaly handles wake locks for subclasses,
 * creates, aquires and releases the lock once the task is done
 * <p>
 * AsyncServiceTask enables proper and easy use of the UI thread from a Service. This is similar to
 * AsyncTask in many respects but allows to be run from a Service and uses the supplied Messenger
 * class to communicate to a UI Thread. If no Messenger is supplied by the subclasses, then no
 * messages are sent to the handler. This class allows to perform background operations in a Service
 * and publish results on the UI thread without having to manipulate threads and/or handlers.
 * </p>
 * 
 * <p>
 * An asynchronous task is defined by a computation that runs on a background thread and whose
 * result is published on the UI thread.
 * </p>
 * 
 * <h2>Usage</h2>
 * <p>
 * AsyncServiceTask must be subclassed to be used. The subclass will override at least one method (
 * {@link #doInBackground})
 * </p>
 * 
 * 
 * <p>
 * Once created, a task is executed very simply:
 * </p>
 * 
 * <pre class="prettyprint">
 * new DownloadFilesTask().execute(url1, url2, url3);
 * </pre>
 * 
 * <h2>AsyncServiceTask's generic types</h2>
 * <p>
 * The three types used by an asynchronous task are the following:
 * </p>
 * <ol>
 * <li><code>Params</code>, the type of the parameters sent to the task upon execution.</li>
 * <li><code>Progress</code>, the type of the progress units published during the background
 * computation.</li>
 * <li><code>Result</code>, the type of the result of the background computation.</li>
 * </ol>
 * <p>
 * Not all types are always used by am asynchronous task. To mark a type as unused, simply use the
 * type {@link Void}:
 * </p>
 * 
 * <pre>
 * private class MyTask extends AsyncServiceTask&lt;Void, Void, Void&gt; { ... }
 * </pre>
 * 
 * <h2>The 4 steps</h2>
 * <p>
 * When an asynchronous task is executed, the task goes through 4 steps:
 * </p>
 * <ol>
 * 
 * <li>{@link #doInBackground}, invoked on the background thread immediately after
 * {@link #onPreExecute()} finishes executing. This step is used to perform background computation
 * that can take a long time. The parameters of the asynchronous task are passed to this step. The
 * result of the computation must be returned by this step and will be passed back to the last step.
 * </li>
 * 
 * </ol>
 * 
 * <h2>Threading rules</h2>
 * <p>
 * There are a few threading rules that must be followed for this class to work properly:
 * </p>
 * <ul>
 * 
 * <li>{@link #execute} must be invoked on the main Service thread.</li>
 * <li>Do not call {@link #doInBackground} manually.</li>
 * <li>The task can be executed only once (an exception will be thrown if a second execution is
 * attempted.)</li>
 * </ul>
 */
public abstract class AsyncServiceTask<Params, Progress, Result> {

    private static PowerManager.WakeLock mWakeLock = null;

    private static final int CORE_POOL_SIZE = 5;

    private static final int MAXIMUM_POOL_SIZE = 32;

    private static final int KEEP_ALIVE = 10;

    private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>(10);

    private static final String TAG = "AsyncServiceTask";

    /**
     * define our custom thread factory for creating threads for the pool
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncServiceTask #" + mCount.getAndIncrement());
        }
    };

    /**
     * Thread pool for our Service threads
     */
    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);

    private final WorkerRunnable<Params, Result> mWorker;

    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;

    /**
     * Indicates the current status of the task. Each status will be set only once during the
     * lifetime of a task.
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncServiceTask#onPostExecute} has finished.
         */
        FINISHED,
    }

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     * 
     * @param context
     *            a {@link android.content.Context} object.
     */
    public AsyncServiceTask(Context context) {

        createWakeLock(context);
        // acquire the wake lock before running
        acquireWakeLock();

        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                return doInBackground(mParams);
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                // Message message;
                Result result = null;

                try {
                    result = get();
                } catch (InterruptedException e) {
                    android.util.Log.w(TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
                } catch (CancellationException e) {
                    // send the cancellation message
                    onCancelled();
                    return;
                } catch (Throwable t) {
                    throw new RuntimeException("An error occured while executing "
                                                    + "doInBackground()", t);
                } finally {
                    // we are reference counted wake lock, release it once we
                    // are done with the task
                    releaseWakeLock();
                    finish(result);
                }
                // AsyncTaskResult res = new
                // AsyncTaskResult<Result>(AsyncServiceTask.this, result);
                // finish(result);
            }
        };
    }

    /**
     * Returns the current status of this task.
     * 
     * @return The current status.
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * Override this method to perform a computation on a background thread. The specified
     * parameters are the parameters passed to {@link #execute} by the caller of this task.
     * 
     * This method can call {@link #publishProgress} to publish updates on the UI thread.
     * 
     * @param params
     *            The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * <p>
     * requiresNotification
     * </p>
     * 
     * @return a boolean.
     */
    protected boolean requiresNotification() {
        return false;
    }

    /**
     * <p>
     * runInBackground
     * </p>
     * 
     * @param flag
     *            a boolean.
     */
    protected void runInBackground(boolean flag) {

    }

    /**
     * <p>
     * isRunningInBackground
     * </p>
     * 
     * @return a boolean.
     */
    public synchronized boolean isRunningInBackground() {
        return true;
    }

    /**
     * Runs on the UI thread after {@link #cancel(boolean)} is invoked.
     * 
     * @see #cancel(boolean)
     * @see #isCancelled()
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed normally.
     * 
     * @return <tt>true</tt> if task was cancelled before it completed
     * @see #cancel(boolean)
     */
    public final boolean isCancelled() {
        return mFuture.isCancelled();
    }

    /**
     * Attempts to cancel execution of this task. This attempt will fail if the task has already
     * completed, already been cancelled, or could not be cancelled for some other reason. If
     * successful, and this task has not started when <tt>cancel</tt> is called, this task should
     * never run. If the task has already started, then the <tt>mayInterruptIfRunning</tt> parameter
     * determines whether the thread executing this task should be interrupted in an attempt to stop
     * the task.
     * 
     * @param mayInterruptIfRunning
     *            <tt>true</tt> if the thread executing this task should be interrupted; otherwise,
     *            in-progress tasks are allowed to complete.
     * @return <tt>false</tt> if the task could not be cancelled, typically because it has already
     *         completed normally; <tt>true</tt> otherwise
     * @see #isCancelled()
     * @see #onCancelled()
     * @see #isCancelled()
     * @see #onCancelled()
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     * 
     * @return The computed result.
     * @throws CancellationException
     *             If the computation was cancelled.
     * @throws java.util.concurrent.ExecutionException
     *             If the computation threw an exception.
     * @throws java.lang.InterruptedException
     *             If the current thread was interrupted while waiting.
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then
     * retrieves its result.
     * 
     * @param timeout
     *            Time to wait before cancelling the operation.
     * @param unit
     *            The time unit for the timeout.
     * @return The computed result.
     * @throws CancellationException
     *             If the computation was cancelled.
     * @throws java.util.concurrent.ExecutionException
     *             If the computation threw an exception.
     * @throws java.lang.InterruptedException
     *             If the current thread was interrupted while waiting.
     * @throws java.util.concurrent.TimeoutException
     *             If the wait timed out.
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
                                    ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * Executes the task with the specified parameters. The task returns itself (this) so that the
     * caller can keep a reference to it.
     * 
     * @param params
     *            The parameters of the task.
     * @return This instance of AsyncServiceTask.
     * @throws java.lang.IllegalStateException
     *             If {@link #getStatus()} returns either {@link AsyncServiceTask.Status#RUNNING} or
     *             {@link AsyncServiceTask.Status#FINISHED}.
     */
    public final AsyncServiceTask<Params, Progress, Result> execute(Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
            case RUNNING:
                throw new IllegalStateException("Cannot execute task:"
                                                + " the task is already running.");
            case FINISHED:
                throw new IllegalStateException("Cannot execute task:"
                                                + " the task has already been executed "
                                                + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING;

        mWorker.mParams = params;
        sExecutor.execute(mFuture);

        return this;
    }

    /**
     * finish
     * 
     * @param result
     */
    private void finish(Result result) {
        // onPostExecute(result);
        mStatus = Status.FINISHED;
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    /**
     * Create a new wake lock if we haven't made one yet and disable ref-count
     */
    private synchronized void createWakeLock(Context context) {

        if (mWakeLock == null) {
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.d(TAG, "Creating WakeLock");
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            mWakeLock.setReferenceCounted(true);
        }
    }

    /**
     * It's okay to acquire multiple times as we are using it in reference-counted mode.make sure to
     * release it always so its counted down and donot hold any wake locks when no tasks are running
     */
    private void acquireWakeLock() {
        if (Log.isLoggable(TAG, Log.VERBOSE))
            Log.d(TAG, "Acquiring WakeLock");
        mWakeLock.acquire();
    }

    /**
     * Don't release the wake lock if it hasn't been created and acquired.
     */
    private void releaseWakeLock() {

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.d(TAG, "Releaseing WakeLock");
            // if(mWakeLock.isHeld() == false)
            // {
            // send a intent to service to stop itself
            // }
        }
    }
}
