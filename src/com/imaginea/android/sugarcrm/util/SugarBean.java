package com.imaginea.android.sugarcrm.util;

import java.util.HashMap;
import java.util.Map;

public class SugarBean {

    private String beanId;

    private Map<String, String> entryList = new HashMap<String, String>();

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public Map<String, String> getEntryList() {
        return entryList;
    }

    public void setEntryList(Map<String, String> map) {
        this.entryList = map;
    }

    public String getFieldValue(String fieldName) {
        return entryList.get(fieldName);
    }

}
