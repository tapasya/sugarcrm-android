package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON_EXCEPTION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RECORDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATIONSHIP_LIST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>SugarBean class.</p>
 *
 */
public class SugarBean {

    private String beanId;

    private String moduleName;

    private Map<String, String> entryList;

    private Map<String, SugarBean[]> relationshipList;

    /**
     * <p>Constructor for SugarBean.</p>
     */
    public SugarBean() {
    }

    /**
     * <p>Constructor for SugarBean.</p>
     *
     * @param jsonResponse a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException if any.
     */
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

    /**
     * <p>Getter for the field <code>beanId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getBeanId() {
        return beanId;
    }

    /**
     * <p>Setter for the field <code>beanId</code>.</p>
     *
     * @param beanId a {@link java.lang.String} object.
     */
    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    /**
     * <p>Getter for the field <code>moduleName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * <p>Setter for the field <code>moduleName</code>.</p>
     *
     * @param moduleName a {@link java.lang.String} object.
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * <p>Getter for the field <code>entryList</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getEntryList() {
        return entryList;
    }

    /**
     * <p>Setter for the field <code>entryList</code>.</p>
     *
     * @param map a {@link java.util.Map} object.
     */
    public void setEntryList(Map<String, String> map) {
        this.entryList = map;
    }

    /**
     * <p>getFieldValue</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getFieldValue(String fieldName) {
        return entryList.get(fieldName);
    }

    /**
     * <p>Getter for the field <code>relationshipList</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, SugarBean[]> getRelationshipList() {
        return relationshipList;
    }

    /**
     * <p>Setter for the field <code>relationshipList</code>.</p>
     *
     * @param relationshipList a {@link java.util.Map} object.
     */
    public void setRelationshipList(Map<String, SugarBean[]> relationshipList) {
        this.relationshipList = relationshipList;
    }

    /**
     * <p>getRelationshipBeans</p>
     *
     * @param linkField a {@link java.lang.String} object.
     * @return an array of {@link com.imaginea.android.sugarcrm.util.SugarBean} objects.
     */
    public SugarBean[] getRelationshipBeans(String linkField) {
        return relationshipList.get(linkField);
    }

}
