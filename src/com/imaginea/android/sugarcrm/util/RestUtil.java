package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.APPLICATION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.BEAN_ID;
import static com.imaginea.android.sugarcrm.RestUtilConstants.BEAN_IDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.DELETED;
import static com.imaginea.android.sugarcrm.RestUtilConstants.DESCRIPTION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_AVAILABLE_MODULES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_ENTRIES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_ENTRY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_MODULE_FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_RELATIONSHIPS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.ID;
import static com.imaginea.android.sugarcrm.RestUtilConstants.IDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.INPUT_TYPE;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON_EXCEPTION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.LINK_FIELD_NAME;
import static com.imaginea.android.sugarcrm.RestUtilConstants.LINK_FIELD_NAMES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.LINK_NAME_TO_FIELDS_ARRAY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.LOGIN;
import static com.imaginea.android.sugarcrm.RestUtilConstants.MAX_RESULTS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.METHOD;
import static com.imaginea.android.sugarcrm.RestUtilConstants.MODULES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.MODULE_NAME;
import static com.imaginea.android.sugarcrm.RestUtilConstants.MODULE_NAMES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.NAME;
import static com.imaginea.android.sugarcrm.RestUtilConstants.NAME_VALUE_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.NAME_VALUE_LISTS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.OFFSET;
import static com.imaginea.android.sugarcrm.RestUtilConstants.ORDER_BY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.PASSWORD;
import static com.imaginea.android.sugarcrm.RestUtilConstants.QUERY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATED_FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATED_IDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RELATED_MODULE_QUERY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RESPONSE_TYPE;
import static com.imaginea.android.sugarcrm.RestUtilConstants.REST_DATA;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SELECT_FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SESSION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SET_ENTRIES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SET_ENTRY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SET_RELATIONSHIP;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SET_RELATIONSHIPS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.USER_AUTH;
import static com.imaginea.android.sugarcrm.RestUtilConstants.USER_NAME;

import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RestUtil {

    public final static String LOG_TAG = "RestUtil";

    private static HttpClient httpClient = new DefaultHttpClient();

    /**
     * Retrieve a list of beans. This is the primary method for getting list of SugarBeans
     * 
     * @param String
     *            session -- Session ID returned by a previous call to login.
     * @param String
     *            module_name -- The name of the module to return records from. This name should be
     *            the name the module was developed under (changing a tab name is studio does not
     *            affect the name that should be passed into this method)..
     * @param String
     *            query -- SQL where clause without the word 'where'
     * @param String
     *            order_by -- SQL order by clause without the phrase 'order by'
     * @param integer
     *            offset -- The record offset to start from.
     * @param Array
     *            select_fields -- A list of the fields to be included in the results. This optional
     *            parameter allows for only needed fields to be retrieved.
     * @param Array
     *            link_name_to_fields_array -- A list of link_names and for each link_name, what
     *            fields value to be returned. For ex.'link_name_to_fields_array' =>
     *            array(array('name' => 'email_addresses', 'value' => array('id', 'email_address',
     *            'opt_out', 'primary_address')))
     * @param integer
     *            max_results -- The maximum number of records to return. The default is the sugar
     *            configuration value for 'list_max_entries_per_page'
     * @param integer
     *            deleted -- false if deleted records should not be include, true if deleted records
     *            should be included.
     * @return Array 'result_count' -- integer - The number of records returned 'next_offset' --
     *         integer - The start of the next page (This will always be the previous offset plus
     *         the number of rows returned. It does not indicate if there is additional data unless
     *         you calculate that the next_offset happens to be closer than it should be.
     *         'entry_list' -- Array - The records that were retrieved 'relationship_list' -- Array
     *         - The records link field data. The example is if asked about accounts email address
     *         then return data would look like Array ( [0] => Array ( [name] => email_addresses
     *         [records] => Array ( [0] => Array ( [0] => Array ( [name] => id [value] =>
     *         3fb16797-8d90-0a94-ac12-490b63a6be67 ) [1] => Array ( [name] => email_address [value]
     *         => hr.kid.qa@example.com ) [2] => Array ( [name] => opt_out [value] => 0 ) [3] =>
     *         Array ( [name] => primary_address [value] => 1 ) ) [1] => Array ( [0] => Array (
     *         [name] => id [value] => 403f8da1-214b-6a88-9cef-490b63d43566 ) [1] => Array ( [name]
     *         => email_address [value] => kid.hr@example.name ) [2] => Array ( [name] => opt_out
     *         [value] => 0 ) [3] => Array ( [name] => primary_address [value] => 0 ) ) ) ) )
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static SugarBean[] getEntryList(String url, String sessionId, String moduleName,
                                    String query, String orderBy, int offset,
                                    String[] selectFields, String[] linkNameToFieldsArray,
                                    int maxResults, int deleted) throws SugarCrmException {

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(QUERY, query);
        data.put(ORDER_BY, orderBy);
        data.put(OFFSET, offset);
        data.put(SELECT_FIELDS, new JSONArray(Arrays.asList(selectFields)));
        data.put(LINK_NAME_TO_FIELDS_ARRAY, linkNameToFieldsArray);
        data.put(MAX_RESULTS, maxResults);
        data.put(DELETED, deleted);

        try {
            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRY_LIST));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            SugarBean[] beans = new SBParser(EntityUtils.toString(res.getEntity()).toString()).getSugarBeans();
            return beans;
        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    public static SugarBean[] getEntries(String url, String sessionId, String moduleName,
                                    String[] ids, String[] selectFields,
                                    String[] linkNameToFieldsArray) throws SugarCrmException {

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(IDS, ids);
        data.put(SELECT_FIELDS, new JSONArray(Arrays.asList(selectFields)));
        data.put("link_name_to_fields_array", linkNameToFieldsArray);

        try {
            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRIES));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            SugarBean[] beans = new SBParser(EntityUtils.toString(res.getEntity()).toString()).getSugarBeans();
            return beans;
        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    public static SugarBean getEntry(String url, String sessionId, String moduleName, String id,
                                    String[] selectFields, String[] linkNameToFieldsArray)
                                    throws SugarCrmException {

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(ID, id);
        data.put(SELECT_FIELDS, new JSONArray(Arrays.asList(selectFields)));
        data.put("link_name_to_fields_array", linkNameToFieldsArray);

        try {
            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRY));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            return new SugarBean(EntityUtils.toString(res.getEntity()).toString());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Update or create a single SugarBean.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param String
     *            $module_name -- The name of the module to return records from. This name should be
     *            the name the module was developed under (changing a tab name is studio does not
     *            affect the name that should be passed into this method)..
     * @param Array
     *            $name_value_list -- The keys of the array are the SugarBean attributes, the values
     *            of the array are the values the attributes should have.
     * @return Array 'id' -- the ID of the bean that was written to (-1 on error)
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static String setEntry(String url, String sessionId, String moduleName,
                                    Map<String, String> nameValueList) throws SugarCrmException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

        try {
            JSONArray nameValueArray = new JSONArray();
            for (Entry<String, String> entry : nameValueList.entrySet()) {
                JSONObject nameValue = new JSONObject();
                nameValue.put("name", entry.getKey());
                nameValue.put("value", entry.getValue());
                nameValueArray.put(nameValue);
            }
            data.put(NAME_VALUE_LIST, nameValueArray);

            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_ENTRY));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            String response = EntityUtils.toString(res.getEntity()).toString();
            JSONObject jsonResponse = new JSONObject(response);

            // TODO: have to see how the JSON response will be when it doesn't return beanId
            return jsonResponse.get(ModuleFields.ID).toString();
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * Update or create a list of SugarBeans
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param String
     *            $module_name -- The name of the module to return records from. This name should be
     *            the name the module was developed under (changing a tab name is studio does not
     *            affect the name that should be passed into this method)..
     * @param Array
     *            $name_value_lists -- Array of Bean specific Arrays where the keys of the array are
     *            the SugarBean attributes, the values of the array are the values the attributes
     *            should have.
     * @return Array 'ids' -- Array of the IDs of the beans that was written to (-1 on error)
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static List<String> setEntries(String url, String sessionId, String moduleName,
                                    List<Map<String, String>> nameValueLists)
                                    throws SugarCrmException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

        try {
            JSONArray nameValueArray = new JSONArray();
            for (Map<String, String> nameValueList : nameValueLists) {
                JSONArray beanNameValueArray = new JSONArray();
                for (Entry<String, String> entry : nameValueList.entrySet()) {
                    JSONObject nameValue = new JSONObject();
                    nameValue.put("name", entry.getKey());
                    nameValue.put("value", entry.getValue());
                    beanNameValueArray.put(nameValue);
                }
                nameValueArray.put(beanNameValueArray);
            }
            data.put(NAME_VALUE_LISTS, nameValueArray);

            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "setEntries restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_ENTRIES));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            String response = EntityUtils.toString(res.getEntity()).toString();
            Log.i(LOG_TAG, "setEntries response : " + response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray beanIdsArray = new JSONArray(jsonResponse.get(IDS).toString());

            List<String> beanIds = new ArrayList<String>();
            for (int i = 0; i < beanIdsArray.length(); i++) {
                String module = beanIdsArray.getString(i).toString();
                beanIds.add(module);
            }

            return beanIds;
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * Retrieve a collection of beans that are related to the specified bean and optionally return
     * relationship data for those related beans. So in this API you can get contacts info for an
     * account and also return all those contact's email address or an opportunity info also.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param String
     *            $module_name -- The name of the module that the primary record is from. This name
     *            should be the name the module was developed under (changing a tab name is studio
     *            does not affect the name that should be passed into this method)..
     * @param String
     *            $module_id -- The ID of the bean in the specified module
     * @param String
     *            $link_field_name -- The name of the lnk field to return records from. This name
     *            should be the name the relationship.
     * @param String
     *            $related_module_query -- A portion of the where clause of the SQL statement to
     *            find the related items. The SQL query will already be filtered to only include the
     *            beans that are related to the specified bean.
     * @param Array
     *            $related_fields - Array of related bean fields to be returned.
     * @param Array
     *            $related_module_link_name_to_fields_array - For every related bean returrned,
     *            specify link fields name to fields info for that bean to be returned. For
     *            ex.'link_name_to_fields_array' => array(array('name' => 'email_addresses', 'value'
     *            => array('id', 'email_address', 'opt_out', 'primary_address'))).
     * @param Number
     *            $deleted -- false if deleted records should not be include, true if deleted
     *            records should be included.
     * @return Array 'entry_list' -- Array - The records that were retrieved 'relationship_list' --
     *         Array - The records link field data. The example is if asked about accounts contacts
     *         email address then return data would look like Array ( [0] => Array ( [name] =>
     *         email_addresses [records] => Array ( [0] => Array ( [0] => Array ( [name] => id
     *         [value] => 3fb16797-8d90-0a94-ac12-490b63a6be67 ) [1] => Array ( [name] =>
     *         email_address [value] => hr.kid.qa@example.com ) [2] => Array ( [name] => opt_out
     *         [value] => 0 ) [3] => Array ( [name] => primary_address [value] => 1 ) ) [1] => Array
     *         ( [0] => Array ( [name] => id [value] => 403f8da1-214b-6a88-9cef-490b63d43566 ) [1]
     *         => Array ( [name] => email_address [value] => kid.hr@example.name ) [2] => Array (
     *         [name] => opt_out [value] => 0 ) [3] => Array ( [name] => primary_address [value] =>
     *         0 ) ) ) ) )
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static String getRelationships(String url, String sessionId, String moduleName,
                                    String beanId, String linkFieldName, String relatedModuleQuery,
                                    String[] relatedFields,
                                    String[] relatedModuleLinkNameToFieldsArray, int deleted)
                                    throws SugarCrmException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(BEAN_ID, beanId);
        data.put(LINK_FIELD_NAME, linkFieldName);
        data.put(RELATED_MODULE_QUERY, relatedModuleQuery);
        data.put(RELATED_FIELDS, new JSONArray(Arrays.asList(relatedFields)));
        data.put(RELATED_MODULE_LINK_NAME_TO_FIELDS_ARRAY, new JSONArray(Arrays.asList(relatedModuleLinkNameToFieldsArray)));
        data.put(DELETED, deleted);

        try {
            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "getRelationships restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_RELATIONSHIPS));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            String response = EntityUtils.toString(res.getEntity()).toString();
            Log.i(LOG_TAG, "getRelationships response : " + response);
            JSONObject jsonResponse = new JSONObject(response);
            // TODO: parse the JSON response
            return response;
        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Set a single relationship between two beans. The items are related by module name and id.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param array
     *            $module_names -- Array of the name of the module that the primary record is from.
     *            This name should be the name the module was developed under (changing a tab name
     *            is studio does not affect the name that should be passed into this method)..
     * @param array
     *            $module_ids - The array of ID of the bean in the specified module_name
     * @param array
     *            $link_field_names -- Array of the name of the link field which relates to the
     *            other module for which the relationships needs to be generated.
     * @param array
     *            $related_ids -- array of an array of related record ids for which relationships
     *            needs to be generated
     * @param array
     *            $name_value_lists -- Array of Array. The keys of the inner array are the SugarBean
     *            attributes, the values of the inner array are the values the attributes should
     *            have.
     * @param array
     *            int $delete_array -- Optional, array of 0 or 1. If the value 0 or nothing is
     *            passed then it will add the relationship for related_ids and if 1 is passed, it
     *            will delete this relationship for related_ids
     * @return Array - created - integer - How many relationships has been created - failed -
     *         integer - How many relationsip creation failed - deleted - integer - How many
     *         relationships were deleted
     * 
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static String setRelationships(String url, String sessionId, String[] moduleNames,
                                    String[] beanIds, String[] linkFieldNames, String[] relatedIds,
                                    List<Map<String, String>> nameValueLists, int deleted)
                                    throws SugarCrmException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAMES, new JSONArray(Arrays.asList(moduleNames)));
        data.put(BEAN_IDS, new JSONArray(Arrays.asList(beanIds)));
        data.put(LINK_FIELD_NAMES, new JSONArray(Arrays.asList(linkFieldNames)));
        data.put(RELATED_IDS, new JSONArray(Arrays.asList(relatedIds)));

        try {
            JSONArray nameValueArray = new JSONArray();
            for (Map<String, String> nameValueList : nameValueLists) {
                JSONArray beanNameValueArray = new JSONArray();
                for (Entry<String, String> entry : nameValueList.entrySet()) {
                    JSONObject nameValue = new JSONObject();
                    nameValue.put("name", entry.getKey());
                    nameValue.put("value", entry.getValue());
                    beanNameValueArray.put(nameValue);
                }
                nameValueArray.put(beanNameValueArray);
            }
            data.put(NAME_VALUE_LISTS, nameValueArray);
            data.put(DELETED, deleted);

            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_RELATIONSHIPS));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            String response = EntityUtils.toString(res.getEntity()).toString();
            JSONObject jsonResponse = new JSONObject(response);
            // TODO: parse the JSON response
            return response;
        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Set a single relationship between two beans. The items are related by module name and id.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param String
     *            $module_name -- name of the module that the primary record is from. This name
     *            should be the name the module was developed under (changing a tab name is studio
     *            does not affect the name that should be passed into this method)..
     * @param String
     *            $module_id - The ID of the bean in the specified module_name
     * @param String
     *            link_field_name -- name of the link field which relates to the other module for
     *            which the relationship needs to be generated.
     * @param array
     *            related_ids -- array of related record ids for which relationships needs to be
     *            generated
     * @param array
     *            $name_value_list -- The keys of the array are the SugarBean attributes, the values
     *            of the array are the values the attributes should have.
     * @param integer
     *            $delete -- Optional, if the value 0 or nothing is passed then it will add the
     *            relationship for related_ids and if 1 is passed, it will delete this relationship
     *            for related_ids
     * @return Array - created - integer - How many relationships has been created - failed -
     *         integer - How many relationsip creation failed - deleted - integer - How many
     *         relationships were deleted
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static String setRelationship(String url, String sessionId, String moduleName,
                                    String beanId, String linkFieldName, String[] relatedIds,
                                    Map<String, String> nameValueList, int delete)
                                    throws SugarCrmException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(BEAN_ID, beanId);
        data.put(LINK_FIELD_NAME, linkFieldName);
        data.put(RELATED_IDS, new JSONArray(Arrays.asList(relatedIds)));

        try {
            JSONArray nameValueArray = new JSONArray();
            for (Entry<String, String> entry : nameValueList.entrySet()) {
                JSONObject nameValue = new JSONObject();
                nameValue.put("name", entry.getKey());
                nameValue.put("value", entry.getValue());
                nameValueArray.put(nameValue);
            }
            data.put(NAME_VALUE_LIST, nameValueArray);
            data.put(DELETED, delete);

            String restData = org.json.simple.JSONValue.toJSONString(data);
            Log.i(LOG_TAG, "restData : " + restData);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_RELATIONSHIP));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            String response = EntityUtils.toString(res.getEntity()).toString();
            JSONObject jsonResponse = new JSONObject(response);
            // TODO: parse the JSON response
            return response;
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

    /**
     * Log the user into the application
     * 
     * @param UserAuth
     *            array $user_auth -- Set user_name and password (password needs to be in the right
     *            encoding for the type of authentication the user is setup for. For Base sugar
     *            validation, password is the MD5 sum of the plain text password.
     * @param String
     *            $application -- The name of the application you are logging in from. (Currently
     *            unused).
     * @param array
     *            $name_value_list -- Array of name value pair of extra parameters. As of today only
     *            'language' and 'notifyonsave' is supported
     * @return Array - id - String id is the session_id of the session that was created. -
     *         module_name - String - module name of user - name_value_list - Array - The name value
     *         pair of user_id, user_name, user_language, user_currency_id, user_currency_name
     * @throws SugarCrmException
     * @exception 'SoapFault' -- The SOAP error, if any
     */

    public static String loginToSugarCRM(String url, String username, String password)
                                    throws SugarCrmException {
        JSONObject credentials = new JSONObject();
        try {
            credentials.put(USER_NAME, username);
            password = Util.MD5(password);
            credentials.put(PASSWORD, password);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(credentials);

            JSONObject userAuth = new JSONObject();
            userAuth.put(USER_AUTH, credentials);

            HttpPost reqLogin = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, LOGIN));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, userAuth.toString()));
            nameValuePairs.add(new BasicNameValuePair(APPLICATION, ""));
            nameValuePairs.add(new BasicNameValuePair(NAME_VALUE_LIST, ""));

            reqLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Log.i(LOG_TAG, EntityUtils.toString(reqLogin.getEntity()));

            // Send POST request
            HttpResponse resLogin = httpClient.execute(reqLogin);

            if (resLogin.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }

            final String response = EntityUtils.toString(resLogin.getEntity());
            JSONObject responseObj = new JSONObject(response);
            Log.i(LOG_TAG, "loginResponse : " + response);
            try {
                String sessionId = responseObj.get(ID).toString();
                return sessionId;
            } catch (JSONException e) {
                throw new SugarCrmException(responseObj.get(NAME).toString(), responseObj.get(DESCRIPTION).toString());
            }
        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }

    }

    /**
     * Retrieve the list of available modules on the system available to the currently logged in
     * user.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @return Array 'modules' -- Array - An array of module names
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static List<String> getAvailableModules(String url, String sessionId)
                                    throws SugarCrmException {
        try {
            JSONObject sessId = new JSONObject();
            sessId.put(SESSION, sessionId);

            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_AVAILABLE_MODULES));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, sessId.toString()));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);

            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }

            final String response = EntityUtils.toString(res.getEntity());
            Log.i(LOG_TAG, "available modules : " + response);
            JSONObject responseObj = new JSONObject(response);
            JSONArray modulesArray = responseObj.getJSONArray(MODULES);
            List<String> modules = new ArrayList<String>();
            for (int i = 0; i < modulesArray.length(); i++) {
                String module = modulesArray.getString(i).toString();
                modules.add(module);
            }

            return modules;
        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Retrieve vardef information on the fields of the specified bean.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param String
     *            $module_name -- The name of the module to return records from. This name should be
     *            the name the module was developed under (changing a tab name is studio does not
     *            affect the name that should be passed into this method)..
     * @param Array
     *            $fields -- Optional, if passed then retrieve vardef information on these fields
     *            only.
     * @return Array 'module_fields' -- Array - The vardef information on the selected fields.
     *         'link_fields' -- Array - The vardef information on the link fields
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static List<ModuleField> getModuleFields(String url, String sessionId,
                                    String moduleName, String[] fields) throws SugarCrmException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

        try {
            JSONArray arr = new JSONArray(Arrays.asList(fields));
            data.put(FIELDS, arr);

            String restData = org.json.simple.JSONValue.toJSONString(data);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, GET_MODULE_FIELDS));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData.toString()));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.i(LOG_TAG, EntityUtils.toString(req.getEntity()));

            // Send POST request
            HttpResponse res = httpClient.execute(req);

            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }

            final String response = EntityUtils.toString(res.getEntity());
            Log.i(LOG_TAG, "moduleFields : " + response);
            return new ModuleFieldsParser(response).getModuleFields();

        } catch (JSONException jo) {
            throw new SugarCrmException(JSON_EXCEPTION, jo.getMessage());
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage(), ioe.getMessage());
        }
    }

    /**
     * Gets server info. This will return information like version, flavor and gmt_time.
     * 
     * @return Array - flavor - String - Retrieve the specific flavor of sugar. - version - String -
     *         Retrieve the version number of Sugar that the server is running. - gmt_time - String
     *         - Return the current time on the server in the format 'Y-m-d H:i:s'. This time is in
     *         GMT.
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static void getServerInfo(String url) throws SugarCrmException {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost req = new HttpPost(url);
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(METHOD, SET_ENTRY));
            nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
            nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
            // nameValuePairs.add(new BasicNameValuePair(REST_DATA, restData));
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Send POST request
            HttpResponse res = httpClient.execute(req);
            if (res.getEntity() == null) {
                Log.i(LOG_TAG, "FAILED TO CONNECT!");
                throw new SugarCrmException("FAILED TO CONNECT!");
            }
            String response = EntityUtils.toString(res.getEntity()).toString();
            JSONObject jsonResponse = new JSONObject(response);

            // TODO: have to parse the response
        } catch (IOException ioe) {
            throw new SugarCrmException(ioe.getMessage());
        } catch (JSONException jsone) {
            throw new SugarCrmException(jsone.getMessage());
        }
    }

}
