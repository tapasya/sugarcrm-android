package com.imaginea.android.sugarcrm.util;

import android.util.Log;

import com.imaginea.android.sugarcrm.RestUtilConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * ModuleFieldsParser class.
 * </p>
 * 
 */
public class ModuleFieldsParser {

    private final String LOG_TAG = ModuleFieldsParser.class.getSimpleName();

    private List<ModuleField> moduleFields;

    private List<LinkField> linkFields;

    /**
     * <p>
     * Constructor for ModuleFieldsParser.
     * </p>
     * 
     * @param jsonResponse
     *            a {@link java.lang.String} object.
     * @throws org.json.JSONException
     *             if any.
     */
    public ModuleFieldsParser(String jsonResponse) throws JSONException {
        JSONObject responseObj = new JSONObject(jsonResponse);
        String moduleName = responseObj.get("module_name").toString();

        JSONObject moduleFieldsJSON = (JSONObject) responseObj.get("module_fields");
        setModuleFields(moduleFieldsJSON);

        try {
            JSONObject linkFieldsJSON = (JSONObject) responseObj.get("link_fields");
            setLinkFields(linkFieldsJSON);
        } catch (ClassCastException cce) {
            // ignore : no linkFields
            linkFields = new ArrayList<LinkField>();
        }

    }

    private void setModuleFields(JSONObject moduleFieldsJSON) throws JSONException {
        moduleFields = new ArrayList<ModuleField>();
        Iterator iterator = moduleFieldsJSON.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
                Log.v(LOG_TAG, key);
            JSONObject nameValuePairsJson = (JSONObject) moduleFieldsJSON.get(key);
            moduleFields.add(getModuleField(nameValuePairsJson));
        }
    }

    private void setLinkFields(JSONObject linkFieldsJSON) throws JSONException {
        linkFields = new ArrayList<LinkField>();
        Iterator iterator = linkFieldsJSON.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (Log.isLoggable(LOG_TAG, Log.VERBOSE))
                Log.v(LOG_TAG, key);
            JSONObject nameValuePairsJson = (JSONObject) linkFieldsJSON.get(key);
            linkFields.add(getLinkFieldAttributes(nameValuePairsJson));
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

    private LinkField getLinkFieldAttributes(JSONObject nameValuePairsJson) throws JSONException {
        String name = nameValuePairsJson.getString(RestUtilConstants.NAME);
        String type = nameValuePairsJson.getString(RestUtilConstants.TYPE);
        String relationship = nameValuePairsJson.getString(RestUtilConstants.RELATIONSHIP);
        String module = nameValuePairsJson.getString(RestUtilConstants.MODULE);
        String beanName = nameValuePairsJson.getString(RestUtilConstants.BEAN_NAME);

        return new LinkField(name, type, relationship, module, beanName);
    }

    /**
     * <p>
     * Getter for the field <code>moduleFields</code>.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<ModuleField> getModuleFields() {
        return moduleFields;
    }

    /**
     * <p>
     * Getter for the field <code>linkFields</code>.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<LinkField> getLinkFields() {
        return linkFields;
    }

}
