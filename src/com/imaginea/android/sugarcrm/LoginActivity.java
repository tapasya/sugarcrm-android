package com.imaginea.android.sugarcrm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.imaginea.android.sugarcrm.util.Util;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import static com.imaginea.android.sugarcrm.RestUtilConstants.*;

public class LoginActivity extends Activity{
	
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
			Log.i(LOG_TAG, sessionId);
			List<String> modules = getAvailableModules(url, sessionId);
			Log.i(LOG_TAG, modules.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

        setContentView(R.layout.main);
    }


	private List<String> getAvailableModules(String url, String sessionId) throws Exception {
		
		JSONObject sessId = new JSONObject();
		sessId.put(SESSION, sessionId);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost req = new HttpPost(url);
		
		// Add your data  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(METHOD, GET_AVAILABLE_MODULES));
        nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON ));
        nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON ));
        nameValuePairs.add(new BasicNameValuePair(REST_DATA, sessId.toString()));
        
        req.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        
        // Send POST request
        HttpResponse resLogin = httpClient.execute(req);

        if (resLogin.getEntity() == null) {
			Log.i(LOG_TAG, "FAILED TO CONNECT!");
			return Collections.EMPTY_LIST;
		}
        
        final String response = EntityUtils.toString(resLogin.getEntity());
        Log.i(LOG_TAG, response);
        JSONObject responseObj = new JSONObject(response);

        return new ArrayList<String>(Arrays.asList(responseObj.get(MODULES).toString().split(",")));
	}


	private String loginToSugarCRM(String url, String username, String password) throws Exception {
		
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
        nameValuePairs.add(new BasicNameValuePair(INPUT_TYPE, JSON ));
        nameValuePairs.add(new BasicNameValuePair(RESPONSE_TYPE, JSON ));
        nameValuePairs.add(new BasicNameValuePair(REST_DATA, userAuth.toString() ));
        nameValuePairs.add(new BasicNameValuePair(APPLICATION, "" ));
        nameValuePairs.add(new BasicNameValuePair(NAME_VALUE_LIST, "" ));
          
        reqLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        
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
