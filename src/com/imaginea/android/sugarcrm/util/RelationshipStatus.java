package com.imaginea.android.sugarcrm.util;

/*
 * set_relationships, set_relationship methods in the REST calls, 
 * give the number of relationships created, number of relationships failed and 
 * number of relationships deleted. This class can be used to get that status.
 * */
/**
 * <p>RelationshipStatus class.</p>
 *
 */
public class RelationshipStatus {

    private int mCreatedCount;

    private int mFailedCount;

    private int mDeletedCount;

    /**
     * <p>Constructor for RelationshipStatus.</p>
     *
     * @param createdCount a int.
     * @param failedCount a int.
     * @param deletedCount a int.
     */
    public RelationshipStatus(int createdCount, int failedCount, int deletedCount) {
        this.mCreatedCount = createdCount;
        this.mFailedCount = failedCount;
        this.mDeletedCount = deletedCount;
    }

    /**
     * <p>getCreatedCount</p>
     *
     * @return a int.
     */
    public int getCreatedCount() {
        return mCreatedCount;
    }

    /**
     * <p>getFailedCount</p>
     *
     * @return a int.
     */
    public int getFailedCount() {
        return mFailedCount;
    }

    /**
     * <p>getDeletedCount</p>
     *
     * @return a int.
     */
    public int getDeletedCount() {
        return mDeletedCount;
    }

}
