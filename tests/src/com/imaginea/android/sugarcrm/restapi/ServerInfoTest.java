package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.util.RestUtil;

import org.json.JSONObject;

public class ServerInfoTest extends RestAPITest {
    
    @SmallTest
    public void testGetServerInfo() throws Exception {
        String response = RestUtil.getServerInfo(url);
        System.out.println("getServerInfo : " + response);

        JSONObject jsonResponse = new JSONObject(response);
        assertNotNull(jsonResponse.get("flavor").toString());
        assertNotNull(jsonResponse.get("version").toString());
        assertNotNull(jsonResponse.get("gmt_time").toString());
    }
}
