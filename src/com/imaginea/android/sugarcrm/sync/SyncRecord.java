package com.imaginea.android.sugarcrm.sync;

/**
 * <p>
 * SyncRecord class.
 * </p>
 * 
 */
public class SyncRecord {
    public long _id;

    public long syncId;

    public long syncRelatedId;

    public int syncCommand;

    public String moduleName;

    public String relatedModuleName;

    public int status;
}
