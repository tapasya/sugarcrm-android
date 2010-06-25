package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATIONSHIP_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RESULT_COUNT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SBList {

    private JSONArray mEntryListJson;
    private JSONArray mRelationshipListJson;
    private int resultCount;
    private SugarBean[] sugarBeans;

    public SBList(String jsonText) throws JSONException {
        JSONObject responseObj = new JSONObject(jsonText);
        this.resultCount = responseObj.getInt(RESULT_COUNT);
        this.mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
        this.mRelationshipListJson = responseObj.getJSONArray(RELATIONSHIP_LIST);
    }
    
    public int getSize(){
        return resultCount;
    }

    public SugarBean[] getSBEntryList() throws JSONException {
        sugarBeans = new SugarBean[mEntryListJson.length()];
        for (int i = 0; i < mEntryListJson.length(); i++) {
            sugarBeans[i] = new SugarBean();
            sugarBeans[i].setBeanId( ((JSONObject) mEntryListJson.get(i)).get("id").toString() );
            
            String nameValueList = ((JSONObject) mEntryListJson.get(i)).get("name_value_list").toString();
            JSONObject nameVal = new JSONObject(nameValueList);
            Iterator iter = nameVal.keys();
            Map<String, String> fields = new HashMap<String, String>();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String val = ((JSONObject) (nameVal.get(key))).get("value").toString();
                fields.put(key, val);
            }
            sugarBeans[i].setEntryList(fields);
        }
        return sugarBeans;
    }
}
