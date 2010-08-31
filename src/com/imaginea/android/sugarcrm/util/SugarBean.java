package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON_EXCEPTION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RECORDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATIONSHIP_LIST;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SugarBean {

    private String beanId;

    private String moduleName;

    private Map<String, String> entryList;

    private Map<String, SugarBean[]> relationshipList;

    public SugarBean() {
    }

    public SugarBean(String jsonResponse) throws SugarCrmException {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);
            JSONArray entryListJson = responseObj.getJSONArray(ENTRY_LIST);
            JSONArray relationshipListJson = responseObj.getJSONArray(RELATIONSHIP_LIST);

            JSONObject jsonObject = (JSONObject) entryListJson.get(0);
            setBeanId(jsonObject.get("id").toString());
            String nameValueList = jsonObject.get("name_value_list").toString();
            setEntryList(SBParseHelper.getNameValuePairs(nameValueList));

            Map<String, SugarBean[]> relationshipList = getRelationshipBeans(relationshipListJson);
            setRelationshipList(relationshipList);
        } catch (JSONException e) {
            throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
        }
    }

    private Map<String, SugarBean[]> getRelationshipBeans(JSONArray mRelationshipListJson)
                                    throws SugarCrmException {
        Map<String, SugarBean[]> relationshipList = new HashMap<String, SugarBean[]>();
        try {
            if (mRelationshipListJson.length() != 0) {
                JSONArray relationshipJson = mRelationshipListJson.getJSONArray(0);
                if (relationshipJson.length() != 0) {
                    for (int i = 0; i < relationshipJson.length(); i++) {
                        JSONObject relationshipModule = relationshipJson.getJSONObject(i);
                        String linkFieldName = relationshipModule.getString("name");
                        String recordsJson = relationshipModule.get(RECORDS).toString();
                        SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                        relationshipList.put(linkFieldName, sugarBeans);
                    }
                }
            }
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
        return relationshipList;
    }

    private SugarBean[] getSugarBeans(String recordsJson) throws SugarCrmException {
        try {
            JSONArray recordsArray = new JSONArray(recordsJson);
            SugarBean[] sugarBeans = new SugarBean[recordsArray.length()];
            for (int i = 0; i < recordsArray.length(); i++) {
                sugarBeans[i] = new SugarBean();
                sugarBeans[i].setEntryList(SBParseHelper.getNameValuePairs(recordsArray.get(i).toString()));
            }
            return sugarBeans;
        } catch (JSONException e) {
            throw new SugarCrmException(e.getMessage());
        }
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public Map<String, SugarBean[]> getRelationshipList() {
        return relationshipList;
    }

    public void setRelationshipList(Map<String, SugarBean[]> relationshipList) {
        this.relationshipList = relationshipList;
    }

    public SugarBean[] getRelationshipBeans(String linkField) {
        return relationshipList.get(linkField);
    }

}
