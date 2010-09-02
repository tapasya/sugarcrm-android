package com.imaginea.android.sugarcrm.util;

/**
 * <p>
 * ModuleField class.
 * </p>
 * 
 */
public class ModuleField {

    private String mName;

    private String mType;

    private String mLabel;

    private boolean mIsRequired;

    // didn't include options as of now
    // private String[] mOptions

    /**
     * <p>
     * Constructor for ModuleField.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @param type
     *            a {@link java.lang.String} object.
     * @param label
     *            a {@link java.lang.String} object.
     * @param isRequired
     *            a boolean.
     */
    public ModuleField(String name, String type, String label, boolean isRequired) {
        mName = name;
        mType = type;
        mLabel = label;
        mIsRequired = isRequired;
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
     * getType
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return mType;
    }

    /**
     * <p>
     * getLabel
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * <p>
     * isRequired
     * </p>
     * 
     * @return a boolean.
     */
    public boolean isRequired() {
        return mIsRequired;
    }

}
