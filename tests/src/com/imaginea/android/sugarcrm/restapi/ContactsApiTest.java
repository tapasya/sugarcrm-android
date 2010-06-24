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
	String[] customFields = new String[] { "a", "b" };

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

		String[] fields = new String[] {};
		RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
		String query = "", orderBy = "";
		int offset = 0;
		String[] selectFields = new String[] {};
		String[] linkNameToFieldsArray = new String[] {};
		int maxResults = 10, deleted = 0;

		
		RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy,
				offset, selectFields, linkNameToFieldsArray, maxResults,
				deleted);		
	}
}
