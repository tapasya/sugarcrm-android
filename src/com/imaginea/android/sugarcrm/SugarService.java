package com.imaginea.android.sugarcrm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.CRMCustomLogFormatter;
import com.imaginea.android.sugarcrm.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SugarService, follows the APIDemos pattern of command handling example of a Service
 * 
 */
public class SugarService extends Service {

    static SugarService self;

    /**
     * wake lock so that we don't sleep when sync is going on
     */
    private WakeLock mWakeLock;

    /**
     * file handler for logging the sync/copy requests
     */
    private static FileHandler fileHandler;

    /**
	 * 
	 */
    private static HashMap<Integer, AsyncServiceTask> mTaskMap = new HashMap<Integer, AsyncServiceTask>();

    /**
	 * 
	 */
    private volatile Looper mServiceLooper;

    /**
	 * 
	 */
    private volatile ServiceHandler mServiceHandler;

    public static final int ONE_MINUTE = 60 * 1000;

    public static int mCurrentSyncEventIndex = 0;

    // use better constants herer
    public static int mStatus = 0;

    private static int mRecentStartId;

    private static Messenger mMessenger;

    // constants for syncing
    // public static final String SERVICECMD = "com.imaginea.android.synccommand";

    public static final String ACTION_START = "com.imaginea.action.ACTION_START";

    private static final String TAG = SugarService.class.getSimpleName();

    @Override
    public void onCreate() {

        Log.i(TAG, "OnCreate: ");
        self = this;
        // initLogger();

        // create a wake lock if not created already
        createWakeLock();

        /**
         * Start up the thread running the service. Note that we create a separate thread because
         * the service normally runs in the process's main thread, which we don't want to block.
         */
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();

        /**
         * temporary hack to stop the service, If you call Service.stopSelfResult(startId) function
         * with the most-recently received start ID before you have called it for previously
         * received IDs, the service will be immediately stopped anyway. If you may end up
         * processing IDs out of order (such as by dispatching them on separate threads), then you
         * are responsible for stopping them in the same order you received them
         */
        Thread serviceStopThread = new Thread(serviceStopperRunnable);
        serviceStopThread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    /**
     * Once the user gets his job done, this thread after a minute checks to see if any tasks are
     * running and calls stop on service If a new startId comes in, then the taskSize will be
     * greater than 1 as we assign the startId to recentStartId after we create anew task. If before
     * adding the task, the stop executes, then the stopSelfResult will return false, if a new
     * startId is started. Worst case a new service is created
     */
    Runnable serviceStopperRunnable = new Runnable() {

        @Override
        public void run() {
            while (true) {
                try {
                    SystemClock.sleep(ONE_MINUTE);
                    if (Log.isLoggable(TAG, Log.VERBOSE))
                        Log.d(TAG, "Task size:" + mTaskMap.size());
                    if (mTaskMap.size() == 0) {
                        boolean stopped = stopSelfResult(mRecentStartId);
                        if (Log.isLoggable(TAG, Log.VERBOSE))
                            Log.d(TAG, "Service stopped:" + stopped);
                        if (stopped == true)
                            break;
                    } else {
                        Set<Integer> keys = mTaskMap.keySet();
                        for (Integer taskId : keys) {
                            // a check for isRunning automatically removes it
                            // from tje task list
                            if (isRunning(taskId))
                                if (Log.isLoggable(TAG, Log.VERBOSE))
                                    Log.v(TAG, "still running" + taskId);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    };

    /**
     * isRunning returns if the transaction is running or not
     * 
     * @param transactionId
     * @return
     */
    public synchronized static boolean isRunning(long transactionId) {

        AsyncServiceTask task = mTaskMap.get(transactionId);
        // /Log.d(TAG, "task " + task);
        if (task == null)
            return false;
        if (Log.isLoggable(TAG, Log.VERBOSE))
            Log.d(TAG, "Task Status:" + task.getStatus().name());
        if (task.getStatus() == AsyncServiceTask.Status.FINISHED) {
            mTaskMap.remove(transactionId);
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.d(TAG, "task removed");
            return false;
        }
        return true;
    }

    /**
     * handles REST API operations
     * 
     * @author chander
     * 
     */
    private final class ServiceHandler extends Handler {

        public int cancelStartId;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.v(TAG, "Message: " + msg + ", ");
            // TODO
            mStatus = 1;

            switch (msg.what) {
            // TODO cleanup the commands
            case Util.GET:
                if (Log.isLoggable(TAG, Log.VERBOSE))
                    Log.i(TAG, "REST API -GET received:");
                EntryListServiceTask entryListServiceTask = new EntryListServiceTask(getBaseContext(), intent);
                mTaskMap.put(Util.getId(), entryListServiceTask);
                entryListServiceTask.execute(null);
                break;
            case Util.UPDATE:
                if (Log.isLoggable(TAG, Log.VERBOSE))
                    Log.i(TAG, "REST API -Update received:");
                UpdateServiceTask updateServiceTask = new UpdateServiceTask(getBaseContext(), intent);
                mTaskMap.put(Util.getId(), updateServiceTask);
                updateServiceTask.execute(null);
                break;
            case Util.DELETE:
                if (Log.isLoggable(TAG, Log.VERBOSE))
                    Log.i(TAG, "REST API -Delete received:");
                updateServiceTask = new UpdateServiceTask(getBaseContext(), intent);
                mTaskMap.put(Util.getId(), updateServiceTask);
                updateServiceTask.execute(null);
                break;
            case Util.INSERT:
                if (Log.isLoggable(TAG, Log.VERBOSE))
                    Log.i(TAG, "REST API -Insert received:");
                updateServiceTask = new UpdateServiceTask(getBaseContext(), intent);
                mTaskMap.put(Util.getId(), updateServiceTask);
                updateServiceTask.execute(null);
                break;
            default:
                if (Log.isLoggable(TAG, Log.VERBOSE))
                    Log.i(TAG, "Unknown REST API received:");
                break;
            }

            mRecentStartId = msg.arg1;

            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.v(TAG, "Done with #" + msg.arg1);
            // stopSelfResult(msg.arg1);
            // stopSelf();
        }
    };

    /**
     * cancels any of the Async ServiceTasks based on the requestId
     * 
     * @param requestId
     */
    private synchronized void cancelServiceTask(int requestId) {
        // AsyncServiceTask task = mTaskMap.get(transactionId);
        AsyncServiceTask task = mTaskMap.get(requestId);
        if (task != null) {
            task.cancel(true);
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.d(TAG, "cancelServiceTask:Task is cancelled:" + task.isCancelled());
        }
        // task = null;
    }

    /**
     * isCancelled
     * 
     * @param transactionId
     * @return
     */
    public synchronized static boolean isCancelled(long transactionId) {
        // AsyncServiceTask task = mTaskMap.get(transactionId);
        AsyncServiceTask task = mTaskMap.get(transactionId);
        if (task != null) {
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.d(TAG, "isCancelled:Task is cancelled:" + task.isCancelled());
            return task.isCancelled();
        }
        return false;
    }

    /*
     * This is the old onStart method that will be called on the pre-2.0 // platform. On 2.0 or
     * later we override onStartCommand() so this // method will not be called.
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handleStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleStart(intent, startId);
        return START_NOT_STICKY;
    }

    /*
     * common method to handle intents for pre2.0 and post 2.0 devices
     */
    void handleStart(Intent intent, int startId) {
        if (Log.isLoggable(TAG, Log.VERBOSE))
            Log.v(TAG, "onStart: " + System.currentTimeMillis());
        // set the service to foreground so that we do not get killed while we
        // this is sort of deprecated in Android 2.0
        // setForeground(true);
        if (Log.isLoggable(TAG, Log.VERBOSE))
            Log.v(TAG, "Starting #" + startId + ": " + intent.getExtras());
        Message msg = mServiceHandler.obtainMessage();
        Bundle extras = intent.getExtras();

        int command = extras.getInt(Util.COMMAND);

        msg.what = command;
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        if (Log.isLoggable(TAG, Log.VERBOSE))
            Log.v(TAG, "Sending: " + msg);

    }

    /**
     * initLogger
     */
    private void initLogger() {
        try {
            File storeLog = new File(Environment.getExternalStorageDirectory(), "SugarCRM"
                                            + File.separatorChar + "Cache");
            // Log.d(TAG, "Files Dir:" + storeLog.get());

            if (storeLog.exists() == false) {
                storeLog.mkdirs();
                // FileOutputStream fs = new FileOutputStream(logFile);
                // fs.close();
            }
            File logFile = new File(storeLog, "CRMLog.txt");
            String name = logFile.getAbsolutePath();
            // if(logFile.exists())
            // logFile.renameTo(dest)
            fileHandler = new FileHandler(name, 10 * 1024, 1, false);
            fileHandler.setFormatter(new CRMCustomLogFormatter());
            // Logger.getLogger(TAG).addHandler(fileHandler);
            Logger.getLogger("CRM").addHandler(fileHandler);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * closeLogger
     */
    private void closeLogger() {
        if (fileHandler != null) {
            fileHandler.close();
        }
    }

    /**
     * logStatus
     * 
     * @param str
     */
    public static void logStatus(String str) {
        if (fileHandler != null) {
            Logger logger = Logger.getLogger("CRM");
            logger.log(Level.INFO, str);
        }
    }

    /**
     * logError
     * 
     * @param str
     */
    public static void logError(String str) {
        if (fileHandler != null) {
            Logger logger = Logger.getLogger("CRM");
            logger.log(Level.SEVERE, str);
        }
    }

    @Override
    public void onDestroy() {

        if (Log.isLoggable(TAG, Log.VERBOSE))
            Log.v(TAG, "onDestroy");
        mStatus = 0;
        releaseWakeLock();
        closeLogger();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Create a new wake lock if we haven't made one yet and disable ref-count
     */
    private synchronized void createWakeLock() {

        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            mWakeLock.setReferenceCounted(false);
        }
    }

    /**
     * It's okay to acquire multiple times as we are not using it in reference-counted mode.
     */
    private void acquireWakeLock() {
        mWakeLock.acquire();
    }

    /**
     * Don't release the wake lock if it hasn't been created and acquired.
     */
    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    /**
     * We only register one Messenger as only one Activity is connected to us at given time.modify
     * to add to a list if we need multiple activity support -?? this will never happen in current
     * android architecture; will happen if single screen holds two activities ???
     * 
     * @param messenger
     */
    public static void registerMessenger(Messenger messenger) {

        // messengerList.add(messenger);
        mMessenger = messenger;
    }

    /**
     * keeping the messenger in case we go ahead with multiple listeners Activities should
     * unregister in onPause and register in onResume so that they continue to receive messages
     * specific to them, further filtering can be done while sending messages so that unwanted
     * messages are not sent.
     * 
     * @param messenger
     */
    public static void unregisterMessenger(Messenger messenger) {
        mMessenger = null;
    }

    /**
     * messages will be sent to the activity or any component that is currently registered with this
     * service made static so can directly call this to display the status of the
     * 
     * @param what
     * @param obj
     */
    public static synchronized void sendMessage(int what, Object obj) {

        if (mMessenger == null) {
            if (Log.isLoggable(TAG, Log.VERBOSE))
                Log.v(TAG, "Messenger is null ");
            return;
        }
        try {
            if (mMessenger != null) {
                if (Log.isLoggable(TAG, Log.VERBOSE))
                    Log.v(TAG, "Sending Message using Messenger" + mMessenger.toString());
                Message message = Message.obtain();
                message.what = what;
                message.obj = obj;
                mMessenger.send(message);
            }
        } catch (RemoteException e) {
            // This should hopefullly not happen ? as we are not remote but
            // using it within
            // same process - but some error message needs to be sent to UI
            // -
            // TBD
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
