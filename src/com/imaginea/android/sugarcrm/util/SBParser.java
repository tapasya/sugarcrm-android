package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON_EXCEPTION;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SBParser {

    private JSONArray mEntryListJson;

    private static final String LOG_TAG = SBParser.class.getSimpleName();

    // TODO: realtionship_list also has to be looked at
    // private JSONArray mRelationshipListJson;

    public SBParser(String jsonText) throws JSONException {
        if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
            Log.v(LOG_TAG, jsonText);
        JSONObject responseObj = new JSONObject(jsonText);
        this.mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
    }

    public SugarBean[] getSugarBeans() throws SugarCrmException {
        SugarBean[] sugarBeans = new SugarBean[mEntryListJson.length()];
        for (int i = 0; i < mEntryListJson.length(); i++) {
            sugarBeans[i] = new SugarBean();
            try {
                JSONObject jsonObject = (JSONObject) mEntryListJson.get(i);
                sugarBeans[i].setBeanId(jsonObject.get("id").toString());
                sugarBeans[i].setModuleName(jsonObject.getString("module_name").toString());
                String nameValueList = jsonObject.get("name_value_list").toString();
                sugarBeans[i].setEntryList(SBParseHelper.getNameValuePairs(nameValueList));
            } catch (JSONException e) {
                throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
            }
        }
        return sugarBeans;
    }
}
