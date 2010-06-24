package com.imaginea.android.sugarcrm.restapi;

import com.imaginea.android.sugarcrm.util.RestUtil;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

/**
 * ContactsApiTest, tests the rest api calls
 * 
 * @author chander
 *
 */
public class ContactsApiTest extends RestAPITest {
	String moduleName = "Contacts";
	String[] fields = new String[] {};
	String[] customFields = new String[]{"a","b"};

	@SmallTest
	public void testGetAllModuleFields() throws Exception {

		RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
	}
	
	@SmallTest
	public void testGetCustomModuleFields() throws Exception {

		RestUtil.getModuleFields(url, mSessionId, moduleName, customFields);
	}

	@SmallTest
	public void testContactsList() throws Exception {
		Log.d("Test", "hello");
	}
}
