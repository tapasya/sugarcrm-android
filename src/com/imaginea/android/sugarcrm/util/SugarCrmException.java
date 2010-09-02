package com.imaginea.android.sugarcrm.util;

/**
 * <p>
 * SugarCrmException class.
 * </p>
 * 
 */
public class SugarCrmException extends Exception {

    private String mName;

    private String mDescription;

    /**
     * <p>
     * Constructor for SugarCrmException.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @param desc
     *            a {@link java.lang.String} object.
     */
    public SugarCrmException(String name, String desc) {
        mName = name;
        mDescription = desc;
    }

    /**
     * <p>
     * Constructor for SugarCrmException.
     * </p>
     * 
     * @param desc
     *            a {@link java.lang.String} object.
     */
    public SugarCrmException(String desc) {
        mDescription = desc;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return mName + " : " + mDescription;
    }

    /**
     * <p>
     * getName
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return mName;
    }

    /**
     * <p>
     * getDescription
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return mDescription;
    }

}
