package com.imaginea.android.sugarcrm;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LoginActivity extends Activity{
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = "http://192.168.1.83/sugarcrm/service/v2/rest.php";
        String username = "will";
        String password = "will";
        String md5Password;
		try {
			md5Password = MD5(password);
			loginToSugarCRM(url, username, md5Password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

        setContentView(R.layout.main);
    }


	private void loginToSugarCRM(String url, String username, String password) throws Exception {
		//http://192.168.1.83/sugarcrm/service/v2/rest.php?method=login&input_type=json&response_type=json&rest_data={"user_auth":{"password":"18218139eec55d83cf82679934e5cd75","username":"will"}}
		
		JSONObject credentials = new JSONObject();
		credentials.put("user_name", username);
		credentials.put("password", password);
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(credentials);
		
		JSONObject userAuth = new JSONObject();
		userAuth.put("user_auth", credentials);
		
		Log.i("msg", userAuth+"");
		
		StringWriter out = new StringWriter();
		userAuth.writeJSONString(out);
		String jsonText = out.toString();
		//String reqUrl = url + "?method=login&input_type=json&response_type=json&rest_data=" + URLEncoder.encode(jsonText);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost reqLogin = new HttpPost(url);
		// Add your data  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("method", "login"));
        nameValuePairs.add(new BasicNameValuePair("input_type", "json" ));
        nameValuePairs.add(new BasicNameValuePair("response_type", "json" ));
        nameValuePairs.add(new BasicNameValuePair("rest_data", jsonText ));
        nameValuePairs.add(new BasicNameValuePair("application", "" ));
        nameValuePairs.add(new BasicNameValuePair("name_value_list", "" ));
          
        reqLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                

        // Send POST request
        HttpResponse resLogin = httpClient.execute(reqLogin);

        if (resLogin.getEntity() == null) {
			System.out.println("FAILED TO CONNECT.");
			Log.i("msg", "FAILED TO CONNECT!");
			return;
		}
        int read = 0;
        byte buffer[] = new byte[8192]; 
        InputStream responseBodyStream = resLogin.getEntity().getContent();
        StringBuffer responseBody = new StringBuffer();
        while ((read = responseBodyStream.read(buffer)) != -1)
        {
            responseBody.append(new String(buffer, 0, read));
        }
        
        String res = responseBody.toString();
        Log.i("msg", res);
		
	}
	
	public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }
	
	private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

}
