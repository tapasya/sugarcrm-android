package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON_EXCEPTION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RESULT_COUNT;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SBParser {

    private JSONArray mEntryListJson;

    // TODO: realtionship_list also has to be looked at
    // private JSONArray mRelationshipListJson;

    private int resultCount;

    public SBParser(String jsonText) throws JSONException {
        Log.i("SBParser", jsonText);
        JSONObject responseObj = new JSONObject(jsonText);
        this.resultCount = responseObj.getInt(RESULT_COUNT);
        this.mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
        // this.mRelationshipListJson = responseObj.getJSONArray(RELATIONSHIP_LIST);
    }

    public int getSize() {
        return resultCount;
    }

    public SugarBean[] getSugarBeans() throws SugarCrmException {
        SugarBean[] sugarBeans = new SugarBean[mEntryListJson.length()];
        for (int i = 0; i < mEntryListJson.length(); i++) {
            sugarBeans[i] = new SugarBean();
            try {
                JSONObject jsonObject = (JSONObject) mEntryListJson.get(i);
                sugarBeans[i].setBeanId(jsonObject.get("id").toString());
                sugarBeans[i].setEntryList(SBParseHelper.getNameValuePairs(jsonObject));
            } catch (JSONException e) {
                throw new SugarCrmException(JSON_EXCEPTION, e.getMessage());
            }
        }
        return sugarBeans;
    }
}
