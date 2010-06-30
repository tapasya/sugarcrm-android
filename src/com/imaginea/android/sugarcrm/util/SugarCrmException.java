package com.imaginea.android.sugarcrm.util;

public class SugarCrmException extends Exception {

    private String mName;

    private String mDescription;

    public SugarCrmException(String name, String desc) {
        mName = name;
        mDescription = desc;
    }

    public SugarCrmException(String desc) {
        mDescription = desc;
    }

    @Override
    public String toString() {
        return mName + " : " + mDescription;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

}
