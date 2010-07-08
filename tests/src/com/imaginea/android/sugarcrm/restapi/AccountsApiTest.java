package com.imaginea.android.sugarcrm.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.RestUtilConstants;
import com.imaginea.android.sugarcrm.util.RelationshipStatus;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;

public class AccountsApiTest extends RestAPITest {
	String moduleName = "Accounts";

	String[] fields = new String[] {};

	String[] customFields = new String[] { "a", "b" };

	String[] selectFields = { ModuleFields.NAME, ModuleFields.PARENT_NAME,
			ModuleFields.PHONE_OFFICE, ModuleFields.PHONE_FAX,
			ModuleFields.EMAIL1, ModuleFields.DELETED };

	String[] linkNameToFieldsArray = {"Contacts"};

	public final static String LOG_TAG = "AccountsApiTest";

	@SmallTest
	public void testGetAccountsList() throws Exception {
		int offset = 0;
		int maxResults = 5;

		SugarBean[] sBeans = getSugarBeans(offset, maxResults);
		assertTrue(sBeans.length > 0);

		for (SugarBean sBean : sBeans) {
			System.out.println(sBean.getBeanId());
			System.out.println(sBean.getFieldValue(ModuleFields.NAME));
		}
	}
	
	@SmallTest
	public void testEntireGetAccountsList() throws Exception {
		int offset = 0;
		int maxResults = 20;
		SugarBean[] sBeans = getSugarBeans(offset, maxResults);
		int totalRuns = 1;
		while (sBeans.length > 0) {
			offset += 20;
			sBeans = getSugarBeans(offset, maxResults);
			totalRuns++;
		}
		Log.d(LOG_TAG, "Total Runs:" + totalRuns);
	}

	@SmallTest
	public void testGetEntry() throws Exception {
		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		SugarBean sBean = RestUtil.getEntry(url, mSessionId, moduleName,
				beanId, selectFields, linkNameToFieldsArray);
		System.out.println("Account Name : " + sBean.getFieldValue("name"));
		System.out.println("Account email : " + sBean.getFieldValue("email1"));
		System.out.println("Phone office : "
				+ sBean.getFieldValue(ModuleFields.PHONE_OFFICE));
		System.out.println("Account deleted ? "
				+ sBean.getFieldValue(ModuleFields.DELETED));
	}
	
	@SmallTest
	public void testGetEntriesCount() throws Exception {
		String query = "";
		int deleted = 0;
		int entriesCount = RestUtil.getEntriesCount(url, mSessionId, moduleName, query, deleted);
		System.out.println("entriesCount = " + entriesCount);
		assertNotNull(entriesCount);
	}

	@SmallTest
	public void testSetEntry() throws Exception {
		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
		nameValuePairs.put(ModuleFields.ID, beanId);
		nameValuePairs.put(ModuleFields.NAME, "R R Advertising Inc.");
		// nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
		String _beanId = RestUtil.setEntry(url, mSessionId, moduleName,
				nameValuePairs);
		System.out.println("setEntry response : " + _beanId);
		assertNotNull(_beanId);
	}

	@SmallTest
	public void testSetEntries() throws Exception {
		List<Map<String, String>> beanNameValuePairs = new ArrayList<Map<String, String>>();

		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
		nameValuePairs.put(ModuleFields.ID, beanId);
		nameValuePairs.put(ModuleFields.NAME, "R Advertising Inc.");
		// nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");

		beanNameValuePairs.add(nameValuePairs);
		List<String> beanIds = RestUtil.setEntries(url, mSessionId, moduleName,
				beanNameValuePairs);
		for (String _beanId : beanIds) {
			System.out.println(_beanId);
		}
	}

	@SmallTest
	public void testGetRelationships() throws Exception {
		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		String linkFieldName = "Contacts";
		String relatedModuleQuery = "";
		String[] relatedFields = { "name" };
		Map<String, List<String>> relatedModuleLinkNameToFieldsArray = new HashMap<String, List<String>>();
		relatedModuleLinkNameToFieldsArray.put("email_addresses",Arrays.asList(new String[]{ "id", "first_name", "email1" }));
		int deleted = 0;
		String response = RestUtil.getRelationships(url, mSessionId,
				moduleName, beanId, linkFieldName, relatedModuleQuery,
				relatedFields, relatedModuleLinkNameToFieldsArray, deleted);
		JSONObject jsonResponse = new JSONObject(response);
		assertNotNull(jsonResponse.get(RestUtilConstants.ENTRY_LIST).toString());
		assertNotNull(jsonResponse.get(RestUtilConstants.RELATIONSHIP_LIST)
				.toString());
	}

	@SmallTest
	public void testSetRelationship() throws Exception {
		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		String linkFieldName = "Contacts";
		String[] relatedIds = {"c2503633-fdb7-2cee-d8ad-4c1f265a9ffd"};
		Map<String, String> nameValueList = new LinkedHashMap<String, String>();
		nameValueList.put(ModuleFields.ID, beanId);
		int delete = 0;
		RelationshipStatus response = RestUtil.setRelationship(url, mSessionId, moduleName,
				beanId, linkFieldName, relatedIds, nameValueList, delete);
		System.out.println("setRelationship : " + response.getCreatedCount() + "-" + response.getFailedCount() + "-" + response.getDeletedCount());
		assertNotNull(response.getCreatedCount());
		assertNotNull(response.getFailedCount());
		assertNotNull(response.getDeletedCount());
	}

	@SmallTest
	public void testSetRelationships() throws Exception {
		List<Map<String, String>> nameValueLists = new ArrayList<Map<String, String>>();

		String[] moduleNames = { "Contacts" };
		String[] beanIds = { "1e9d5cb4-1972-28a4-7b36-4c1f261afd48" };
		String[] linkFieldNames = { "Contacts" };
		String[] relatedIds = {"c2503633-fdb7-2cee-d8ad-4c1f265a9ffd"};

		Map<String, String> nameValueList = new LinkedHashMap<String, String>();
		nameValueList.put(ModuleFields.ID, beanIds[0]);

		int deleted = 0;

		nameValueLists.add(nameValueList);
		RelationshipStatus response = RestUtil.setRelationships(url, mSessionId,
				moduleNames, beanIds, linkFieldNames, relatedIds,
				nameValueLists, deleted);
		assertNotNull(response.getCreatedCount());
		assertNotNull(response.getFailedCount());
		assertNotNull(response.getDeletedCount());
	}

	/*
	 * @SmallTest public void testGetEntryListWithNoSelectFields() throws
	 * Exception { String query = "", orderBy = ""; int offset = 0, maxResults =
	 * 5, deleted = 0; String[] selectFields = {}; String[]
	 * linkNameToFieldsArray = {};
	 * 
	 * SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName,
	 * query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults,
	 * deleted); for (SugarBean sBean : sBeans) {
	 * System.out.println(sBean.getBeanId()); } }
	 */

	/*
	 * public void testGetModuleFields() throws Exception { List<ModuleField>
	 * moduleFields = RestUtil.getModuleFields(url, mSessionId, moduleName,
	 * selectFields); for (ModuleField moduleField : moduleFields) {
	 * System.out.println("name :" + moduleField.getName());
	 * System.out.println("label :" + moduleField.getName());
	 * System.out.println("type :" + moduleField.getName());
	 * System.out.println("isReuired :" + moduleField.ismIsRequired()); } }
	 */
	
	@SmallTest
	public void testSearchByModule() throws Exception{
		String searchString = "beans.the.vegan";
		String[] modules = {"Accounts"};
		int offset = 0;
		int maxResults = 20;
		Map<String, SugarBean[]> searchResults = RestUtil.searchByModule(url, mSessionId, searchString, modules, offset, maxResults);
		for(Entry<String, SugarBean[]> entry : searchResults.entrySet()){
			System.out.println("Module Name : " + entry.getKey());
			SugarBean[] sugarBeans = entry.getValue();
			for(int i=0; i<sugarBeans.length; i++){
				System.out.println("ID : " + sugarBeans[i].getFieldValue(ModuleFields.ID));
				System.out.println("NAME : " + sugarBeans[i].getFieldValue(ModuleFields.NAME));
				System.out.println("Billing address city : " + sugarBeans[i].getFieldValue(ModuleFields.BILLING_ADDRESS_CITY));
				System.out.println("Phone office : " + sugarBeans[i].getFieldValue(ModuleFields.PHONE_OFFICE));
				System.out.println("Assigned user name : " + sugarBeans[i].getFieldValue(ModuleFields.ASSIGNED_USER_NAME));
			}
		}
		
	}

	/**
	 * demonstrates the usage of RestUtil for contacts List. ModuleFields.NAME
	 * or FULL_NAME is not returned by Sugar CRM. The fields that are not
	 * returned by SugarCRM can be automated, but not yet generated
	 * 
	 * @param offset
	 * @param maxResults
	 * @return
	 * @throws Exception
	 */
	private SugarBean[] getSugarBeans(int offset, int maxResults)
			throws Exception {
		String query = "", orderBy = "";
		int deleted = 0;
		SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName,
				query, orderBy, offset+"", selectFields, linkNameToFieldsArray,
				maxResults+"", deleted+"");
		return sBeans;
	}
}
