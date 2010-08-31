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

public class SBParser {

    private static final String LOG_TAG = SBParser.class.getSimpleName();

    private JSONArray mEntryListJson;

    private JSONArray mRelationshipListJson;

    public SBParser(String jsonText) throws JSONException {
        JSONObject responseObj = new JSONObject(jsonText);
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            Log.v(LOG_TAG, jsonText);
            Log.v(LOG_TAG, "length" + responseObj.length());
        }
        if (responseObj.has(ENTRY_LIST))
            this.mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
        if (responseObj.has(RELATIONSHIP_LIST))
            this.mRelationshipListJson = responseObj.getJSONArray(RELATIONSHIP_LIST);
    }

    public SugarBean[] getSugarBeans() throws SugarCrmException {
        if (mEntryListJson == null)
            return null;
        SugarBean[] sugarBeans = new SugarBean[mEntryListJson.length()];
        for (int i = 0; i < mEntryListJson.length(); i++) {
            sugarBeans[i] = new SugarBean();
            try {
                JSONObject jsonObject = (JSONObject) mEntryListJson.get(i);
                sugarBeans[i].setBeanId(jsonObject.get("id").toString());
                sugarBeans[i].setModuleName(jsonObject.getString("module_name").toString());
                String nameValueList = jsonObject.get("name_value_list").toString();
                sugarBeans[i].setEntryList(SBParseHelper.getNameValuePairs(nameValueList));

                Map<String, SugarBean[]> relationshipList = getRelationshipBeans(i);
                sugarBeans[i].setRelationshipList(relationshipList);

            } catch (JSONException e) {
                throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
            }
        }
        return sugarBeans;
    }

    public Map<String, SugarBean[]> getRelationshipBeans(int index) throws SugarCrmException {
        if (mRelationshipListJson == null)
            return null;
        Map<String, SugarBean[]> relationshipList = new HashMap<String, SugarBean[]>();
        try {
            if (index >= mRelationshipListJson.length())
                return relationshipList;
            JSONArray relationshipJson = mRelationshipListJson.getJSONArray(index);
            if (relationshipJson.length() != 0) {
                for (int i = 0; i < relationshipJson.length(); i++) {
                    JSONObject relationshipModule = relationshipJson.getJSONObject(i);
                    String linkFieldName = relationshipModule.getString("name");
                    String recordsJson = relationshipModule.get(RECORDS).toString();
                    SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                    relationshipList.put(linkFieldName, sugarBeans);
                }
            }
        } catch (JSONException jsone) {
            Log.e(LOG_TAG, jsone.getMessage(), jsone);
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

}
