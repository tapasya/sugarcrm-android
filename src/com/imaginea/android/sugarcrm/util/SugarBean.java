package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON_EXCEPTION;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SugarBean {

    private String beanId;

    private Map<String, String> entryList = new HashMap<String, String>();

    public SugarBean() {
    }

    public SugarBean(String jsonResponse) throws SugarCrmException {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            JSONArray entryListJson = responseObj.getJSONArray(ENTRY_LIST);

            // TODO: relationship_list also has to be looked at
            // JSONArray mRelationshipListJson = responseObj.getJSONArray(RELATIONSHIP_LIST);

            JSONObject jsonObject = (JSONObject) entryListJson.get(0);
            setBeanId(jsonObject.get("id").toString());
            setEntryList(SBParseHelper.getNameValuePairs(jsonObject));
        } catch (JSONException e) {
            throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
        }
    }

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
