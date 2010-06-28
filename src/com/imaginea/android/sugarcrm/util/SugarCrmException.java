package com.imaginea.android.sugarcrm.util;

public class SugarCrmException extends Exception {

    private String mName;

    private String mDescription;

    public SugarCrmException(String name, String desc) {
        mName = name;
        mDescription = desc;
    }

    @Override
    public String getMessage() {
        return mName + " : " + mDescription;
    }

}
