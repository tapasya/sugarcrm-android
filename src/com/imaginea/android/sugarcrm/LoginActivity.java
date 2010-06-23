package com.imaginea.android.sugarcrm;

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
import static com.imaginea.android.sugarcrm.RestUtilConstants.PASSWORD;
import static com.imaginea.android.sugarcrm.RestUtilConstants.RESPONSE_TYPE;
import static com.imaginea.android.sugarcrm.RestUtilConstants.REST_DATA;
import static com.imaginea.android.sugarcrm.RestUtilConstants.SESSION;
import static com.imaginea.android.sugarcrm.RestUtilConstants.USER_AUTH;
import static com.imaginea.android.sugarcrm.RestUtilConstants.USER_NAME;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.Util;

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
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {

	public final static String LOG_TAG = "LoginActivity";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String url = "http://192.168.1.83/sugarcrm/service/v2/rest.php";
		String username = "will";
		String password = "will";
		String md5Password;
		try {
			md5Password = Util.MD5(password);
			String sessionId = loginToSugarCRM(url, username, md5Password);
			Log.i(LOG_TAG, "sessionId : " + sessionId);

			SugarCrmApp app = ((SugarCrmApp) getApplicationContext());
			app.setmSessionId(sessionId);
			
			List<String> modules = getAvailableModules(url, app.getmSessionId());
			Log.i(LOG_TAG, modules.toString());
			
			/*String moduleName = "Accounts";
			String[] fields = new String[] {};
			getModuleFields(url, app.getmSessionId(), moduleName, fields);*/
			
			/*String moduleName = "Accounts";
			String query = "";
			String orderBy = "";
			int offset = 0;
			String[] selectFields = new String[] {};
			String[] linkNameToFieldsArray = new String[] {};
			int maxResults = 10;
			int deleted = 0;*/

			//List<String> entryList = 
			//getEntryList(url, app.getmSessionId(), moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
			//Log.i(LOG_TAG, entryList.toString());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}

		setContentView(R.layout.main);
	}

	private void getModuleFields(String url, String sessionId, String moduleName,
			String[] fields) throws JSONException, ClientProtocolException, IOException {
		
		/*JSONObject restData = new JSONObject();
		restData.put(SESSION, sessionId);
		restData.put(MODULE_NAME, moduleName);*/
		Map data = new LinkedHashMap();
		data.put(MODULE_NAME, moduleName);
		data.put(SESSION, sessionId);
		JSONObject restData = new JSONObject(data);
		Log.i(LOG_TAG, restData.toString());
		
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
			//return Collections.EMPTY_LIST;
		}

		final String response = EntityUtils.toString(res.getEntity());
		Log.i(LOG_TAG, "moduleFields : " + response);
		//JSONObject responseObj = new JSONObject(response);

		//return new ArrayList<String>(Arrays.asList(responseObj.get(ENTRY_LIST).toString().split(",")));
		
	}

	private void getEntryList(String url, String sessionId, String moduleName,
			String query, String orderBy, int offset, String[] selectFields,
			String[] linkNameToFieldsArray, int maxResults, int deleted) throws JSONException, ClientProtocolException, IOException {
		
		JSONObject sessId = new JSONObject();
		sessId.put(SESSION, sessionId);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost req = new HttpPost(url);

		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(METHOD, GET_ENTRY_LIST));
		nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON));
		nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON));
		nameValuePairs.add(new BasicNameValuePair(REST_DATA, sessId.toString()));

		req.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// Send POST request
		HttpResponse res = httpClient.execute(req);

		if (res.getEntity() == null) {
			Log.i(LOG_TAG, "FAILED TO CONNECT!");
			//return Collections.EMPTY_LIST;
		}

		final String response = EntityUtils.toString(res.getEntity());
		Log.i(LOG_TAG, "entryList : " + response);
		//JSONObject responseObj = new JSONObject(response);

		//return new ArrayList<String>(Arrays.asList(responseObj.get(ENTRY_LIST).toString().split(",")));
	}

	private List<String> getAvailableModules(String url, String sessionId) throws JSONException, ClientProtocolException, IOException {

		JSONObject sessId = new JSONObject();
		sessId.put(SESSION, sessionId);

		HttpClient httpClient = new DefaultHttpClient();
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
			return Collections.EMPTY_LIST;
		}

		final String response = EntityUtils.toString(res.getEntity());
		Log.i(LOG_TAG, response);
		JSONObject responseObj = new JSONObject(response);

		return new ArrayList<String>(Arrays.asList(responseObj.get(MODULES).toString().split(",")));
	}

	private String loginToSugarCRM(String url, String username, String password) throws JSONException, ClientProtocolException, IOException {

		JSONObject credentials = new JSONObject();
		credentials.put(USER_NAME, username);
		credentials.put(PASSWORD, password);

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(credentials);

		JSONObject userAuth = new JSONObject();
		userAuth.put(USER_AUTH, credentials);

		HttpClient httpClient = new DefaultHttpClient();
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
