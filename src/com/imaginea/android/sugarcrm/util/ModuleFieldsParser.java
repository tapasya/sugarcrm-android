package com.imaginea.android.sugarcrm.util;

import android.util.Log;

import com.imaginea.android.sugarcrm.RestUtilConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModuleFieldsParser {

    private final String LOG_TAG = "ModuleFieldsParser";

    private List<ModuleField> moduleFields;

    public ModuleFieldsParser(String jsonResponse) throws JSONException {
        JSONObject responseObj = new JSONObject(jsonResponse);
        String moduleName = responseObj.get("module_name").toString();
        Log.i(LOG_TAG, moduleName);

        JSONObject moduleFieldsJSON = (JSONObject) responseObj.get("module_fields");
        setModuleFields(moduleFieldsJSON);

    }

    private void setModuleFields(JSONObject moduleFieldsJSON) throws JSONException {
        moduleFields = new ArrayList<ModuleField>();
        Iterator iterator = moduleFieldsJSON.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Log.i(LOG_TAG, key);
            JSONObject nameValuePairsJson = (JSONObject) moduleFieldsJSON.get(key);
            moduleFields.add(getModuleField(nameValuePairsJson));
        }
    }

    private ModuleField getModuleField(JSONObject nameValuePairsJson) throws JSONException {
        String name = nameValuePairsJson.getString(RestUtilConstants.NAME);
        String type = nameValuePairsJson.getString(RestUtilConstants.TYPE);
        String label = nameValuePairsJson.getString(RestUtilConstants.LABEL);
        int required = nameValuePairsJson.getInt(RestUtilConstants.REQUIRED);
        boolean isRequired = required == 0 ? false : true;
        return new ModuleField(name, type, label, isRequired);
    }

    public List<ModuleField> getModuleFields() {
        return moduleFields;
    }

}
