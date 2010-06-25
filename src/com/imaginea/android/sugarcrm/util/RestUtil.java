package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.APPLICATION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_AVAILABLE_MODULES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_ENTRY_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.GET_MODULE_FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.ID;
import static com.imaginea.android.sugarcrm.RestUtilConstants.INPUT_TYPE;
import static com.imaginea.android.sugarcrm.RestUtilConstants.JSON;
import static com.imaginea.android.sugarcrm.RestUtilConstants.LOGIN;
import static com.imaginea.android.sugarcrm.RestUtilConstants.METHOD;
import static com.imaginea.android.sugarcrm.RestUtilConstants.MODULES;
import static com.imaginea.android.sugarcrm.RestUtilConstants.MODULE_NAME;
import static com.imaginea.android.sugarcrm.RestUtilConstants.NAME_VALUE_LIST;
import static com.imaginea.android.sugarcrm.RestUtilConstants.OFFSET;
import static com.imaginea.android.sugarcrm.RestUtilConstants.ORDER_BY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.PASSWORD;
import static com.imaginea.android.sugarcrm.RestUtilConstants.QUERY;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RESPONSE_TYPE;
import static com.imaginea.android.sugarcrm.RestUtilConstants.REST_DATA;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SELECT_FIELDS;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SESSION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.USER_AUTH;
import static com.imaginea.android.sugarcrm.RestUtilConstants.USER_NAME;

import android.util.Log;

import com.imaginea.android.sugarcrm.RestUtilConstants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestUtil {

    public final static String LOG_TAG = "RestUtil";

    private static HttpClient httpClient = new DefaultHttpClient();

    /**
     * Retrieve a list of beans. This is the primary method for getting list of SugarBeans from
     * Sugar using the SOAP API.
     * 
     * @param String
     *            $session -- Session ID returned by a previous call to login.
     * @param String
     *            $module_name -- The name of the module to return records from. This name should be
     *            the name the module was developed under (changing a tab name is studio does not
     *            affect the name that should be passed into this method)..
     * @param String
     *            $query -- SQL where clause without the word 'where'
     * @param String
     *            $order_by -- SQL order by clause without the phrase 'order by'
     * @param integer
     *            $offset -- The record offset to start from.
     * @param Array
     *            $select_fields -- A list of the fields to be included in the results. This
     *            optional parameter allows for only needed fields to be retrieved.
     * @param Array
     *            $link_name_to_fields_array -- A list of link_names and for each link_name, what
     *            fields value to be returned. For ex.'link_name_to_fields_array' =>
     *            array(array('name' => 'email_addresses', 'value' => array('id', 'email_address',
     *            'opt_out', 'primary_address')))
     * @param integer
     *            $max_results -- The maximum number of records to return. The default is the sugar
     *            configuration value for 'list_max_entries_per_page'
     * @param integer
     *            $deleted -- false if deleted records should not be include, true if deleted
     *            records should be included.
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
    public static SBList getEntryList(String url, String sessionId, String moduleName,
                                    String query, String orderBy, int offset,
                                    String[] selectFields, String[] linkNameToFieldsArray,
                                    int maxResults, int deleted) throws JSONException,
                                    ClientProtocolException, IOException {

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(QUERY, query);
        data.put(ORDER_BY, orderBy);
        data.put(OFFSET, offset);
        data.put(SELECT_FIELDS, new JSONArray(Arrays.asList(selectFields)));
        data.put("link_name_to_fields_array", linkNameToFieldsArray);
        data.put("max_results", maxResults);

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
            return null;
        }
        return new SBList(EntityUtils.toString(res.getEntity()).toString());
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
     * @exception 'SoapFault' -- The SOAP error, if any
     */
    public static String loginToSugarCRM(String url, String username, String password)
                                    throws JSONException, ClientProtocolException, IOException {
        JSONObject credentials = new JSONObject();
        credentials.put(USER_NAME, username);
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
        Log.i(LOG_TAG, EntityUtils.toString(reqLogin.getEntity()));

        // Send POST request
        HttpResponse resLogin = httpClient.execute(reqLogin);

        if (resLogin.getEntity() == null) {
            System.out.println("FAILED TO CONNECT.");
            Log.i(LOG_TAG, "FAILED TO CONNECT!");
            return "";
        }

        final String response = EntityUtils.toString(resLogin.getEntity());
        JSONObject responseObj = new JSONObject(response);
        return responseObj.get(ID).toString();
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
                                    throws JSONException, ClientProtocolException, IOException {
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
            return Collections.emptyList();
        }

        final String response = EntityUtils.toString(res.getEntity());
        Log.i(LOG_TAG, "available modules : " + response);
        JSONObject responseObj = new JSONObject(response);

        return new ArrayList<String>(Arrays.asList(responseObj.get(MODULES).toString().split(",")));
    }

    // TODO parse the response to get module fields and change the return type
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
    public static JSONObject getModuleFields(String url, String sessionId, String moduleName,
                                    String[] fields) throws JSONException, ClientProtocolException,
                                    IOException {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

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
            // return (ArrayList<String>) Collections.EMPTY_LIST;
            return null;
        }

        final String response = EntityUtils.toString(res.getEntity());
        Log.i(LOG_TAG, "moduleFields : " + response);
        JSONObject jsonResponse = new JSONObject(response);
        return jsonResponse.getJSONObject(RestUtilConstants.MODULE_FIELDS);
    }

    public static Object getValueFromMap(Map<String, Map<String, Object>> map, String beanId,
                                    String key) {
        Map<String, Object> nameValuePair = (Map<String, Object>) map.get(beanId);
        return nameValuePair.get(key).toString();
    }
}
