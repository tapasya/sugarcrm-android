package com.imaginea.android.sugarcrm.util;

import static com.imaginea.android.sugarcrm.RestUtilConstants.APPLICATION;
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

    public static void getEntryList(String url, String sessionId, String moduleName, String query,
                                    String orderBy, int offset, String[] selectFields,
                                    String[] linkNameToFieldsArray, int maxResults, int deleted)
                                    throws JSONException, ClientProtocolException, IOException {
        JSONArray arr = new JSONArray();
        arr.put("name");
        arr.put("date_modified");

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);
        data.put(QUERY, "");
        data.put(ORDER_BY, "");
        data.put(OFFSET, "");
        data.put(SELECT_FIELDS, arr);

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
            // return Collections.EMPTY_LIST;
            return;
        }
        final String response = EntityUtils.toString(res.getEntity());
        Log.i(LOG_TAG, "entryList : " + response);

        // JSONObject responseObj = new JSONObject(response);
        // return new
        // ArrayList<String>(Arrays.asList(responseObj.get(ENTRY_LIST).toString().split(",")));
    }

    public static void getModuleFields(String url, String sessionId, String moduleName,
                                    String[] fields) throws JSONException, ClientProtocolException,
                                    IOException {
        Map<String, String> data = new LinkedHashMap<String, String>();
        data.put(SESSION, sessionId);
        data.put(MODULE_NAME, moduleName);

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
            return;
        }

        final String response = EntityUtils.toString(res.getEntity());
        Log.i(LOG_TAG, "moduleFields : " + response);

        // JSONObject responseObj = new JSONObject(response);
        // return new
        // ArrayList<String>(Arrays.asList(responseObj.get(MODULE_FIELDS).toString().split(",")));
    }

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

}
