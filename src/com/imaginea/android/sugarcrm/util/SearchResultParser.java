package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.NAME;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RECORDS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * SearchResultParser class.
 * </p>
 * 
 */
public class SearchResultParser {

    private Map<String, SugarBean[]> mSearchResults;

    /**
     * <p>
     * Constructor for SearchResultParser.
     * </p>
     * 
     * @param jsonText
     *            a {@link java.lang.String} object.
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public SearchResultParser(String jsonText, String moduleName) throws SugarCrmException {
        mSearchResults = new HashMap<String, SugarBean[]>();
        try {
            JSONObject responseObj = new JSONObject(jsonText);
            JSONArray mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
            for (int i = 0; i < mEntryListJson.length(); i++) {
                JSONObject moduleResultJson = new JSONObject(mEntryListJson.get(i).toString());
                if (moduleName.equals(moduleResultJson.get(NAME).toString())) {
                    String recordsJson = moduleResultJson.get(RECORDS).toString();
                    SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                    mSearchResults.put(moduleName, sugarBeans);
                }
            }
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * <p>
     * Constructor for SearchResultParser.
     * </p>
     * 
     * @param jsonText
     *            a {@link java.lang.String} object.
     * @throws com.imaginea.android.sugarcrm.util.SugarCrmException
     *             if any.
     */
    public SearchResultParser(String jsonText) throws SugarCrmException {
        mSearchResults = new HashMap<String, SugarBean[]>();
        try {
            JSONObject responseObj = new JSONObject(jsonText);
            JSONArray mEntryListJson = responseObj.getJSONArray(ENTRY_LIST);
            for (int i = 0; i < mEntryListJson.length(); i++) {
                JSONObject moduleResultJson = new JSONObject(mEntryListJson.get(i).toString());
                String moduleName = moduleResultJson.get(NAME).toString();
                String recordsJson = moduleResultJson.get(RECORDS).toString();
                SugarBean[] sugarBeans = getSugarBeans(recordsJson);
                mSearchResults.put(moduleName, sugarBeans);
            }
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
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
     * <p>
     * getSearchResults
     * </p>
     * 
     * @return a {@link java.util.Map} object.
     */
    public Map<String, SugarBean[]> getSearchResults() {
        return mSearchResults;
    }

}
