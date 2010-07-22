package com.imaginea.android.sugarcrm.util;

/*
 * set_relationships, set_relationship methods in the REST calls, 
 * give the number of relationships created, number of relationships failed and 
 * number of relationships deleted. This class can be used to get that status.
 * */
public class RelationshipStatus {

    private int mCreatedCount;

    private int mFailedCount;

    private int mDeletedCount;

    public RelationshipStatus(int createdCount, int failedCount, int deletedCount) {
        this.mCreatedCount = createdCount;
        this.mFailedCount = failedCount;
        this.mDeletedCount = deletedCount;
    }

    public int getCreatedCount() {
        return mCreatedCount;
    }

    public int getFailedCount() {
        return mFailedCount;
    }

    public int getDeletedCount() {
        return mDeletedCount;
    }

}
