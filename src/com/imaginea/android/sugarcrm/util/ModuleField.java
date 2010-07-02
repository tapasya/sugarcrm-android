package com.imaginea.android.sugarcrm.util;

public class ModuleField {

    private String mName;

    private String mType;

    private String mLabel;

    private boolean mIsRequired;

    // didn't include options as of now
    // private String[] mOptions

    public ModuleField(String name, String type, String label, boolean isRequired) {
        mName = name;
        mType = type;
        mLabel = label;
        mIsRequired = isRequired;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public String getLabel() {
        return mLabel;
    }

    public boolean ismIsRequired() {
        return mIsRequired;
    }

}
