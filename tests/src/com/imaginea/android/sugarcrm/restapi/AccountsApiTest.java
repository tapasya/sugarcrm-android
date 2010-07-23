package com.imaginea.android.sugarcrm.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.util.LinkField;
import com.imaginea.android.sugarcrm.util.Module;
import com.imaginea.android.sugarcrm.util.ModuleField;
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

	HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

	public final static String LOG_TAG = "AccountsApiTest";

	@SmallTest
	public void testGetAccountsList() throws Exception {
		int offset = 15;
		int maxResults = 2;
		SugarBean[] sBeans = getSugarBeans(offset, maxResults);
		assertTrue(sBeans.length > 0);
		for (SugarBean sBean : sBeans) {
			Log.d(LOG_TAG, sBean.getBeanId());
			Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.NAME));
			SugarBean[] relationshipBeans = sBean
					.getRelationshipBeans("contacts");
			Log.d(LOG_TAG, "relationshipBeans size : "
					+ relationshipBeans.length);
			if (relationshipBeans != null) {
				for (SugarBean relationshipBean : relationshipBeans) {
					Log.d(LOG_TAG, ""
							+ relationshipBean
									.getFieldValue(ModuleFields.FIRST_NAME));
				}
			}
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
		linkNameToFieldsArray.put("contacts", Arrays.asList(new String[] {
				"first_name", "last_name" }));
		SugarBean sBean = RestUtil.getEntry(url, mSessionId, moduleName,
				beanId, selectFields, linkNameToFieldsArray);
		Log.d(LOG_TAG, "Account Name : " + sBean.getFieldValue("name"));
		Log.d(LOG_TAG, "Account email : " + sBean.getFieldValue("email1"));
		Log.d(LOG_TAG, "Phone office : "
				+ sBean.getFieldValue(ModuleFields.PHONE_OFFICE));
		Log.d(LOG_TAG, "Account deleted ? "
				+ sBean.getFieldValue(ModuleFields.DELETED));
	}

	@SmallTest
	public void testGetEntriesCount() throws Exception {
		String query = "";
		int deleted = 0;
		int entriesCount = RestUtil.getEntriesCount(url, mSessionId,
				moduleName, query, deleted);
		Log.d(LOG_TAG, "entriesCount = " + entriesCount);
		assertNotNull(entriesCount);
	}

	@SmallTest
	public void testSetEntry() throws Exception {
		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
		nameValuePairs.put(ModuleFields.ID, beanId);
		nameValuePairs.put(ModuleFields.NAME, "R R Advertising Inc."); //
		nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
		String _beanId = RestUtil.setEntry(url, mSessionId, moduleName,
				nameValuePairs);
		Log.d(LOG_TAG, "setEntry response : " + _beanId);
		assertNotNull(_beanId);
		// if update is successful we get the same beanId returned
		assertEquals(beanId, _beanId);
	}

	@SmallTest
	public void testSetEntries() throws Exception {
		List<Map<String, String>> beanNameValuePairs = new ArrayList<Map<String, String>>();

		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
		nameValuePairs.put(ModuleFields.ID, beanId);
		nameValuePairs.put(ModuleFields.NAME, "R Advertising Inc."); //
		nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");

		beanNameValuePairs.add(nameValuePairs);
		List<String> beanIds = RestUtil.setEntries(url, mSessionId, moduleName,
				beanNameValuePairs);
		for (String _beanId : beanIds) {
			Log.d(LOG_TAG, _beanId);
		}
	}

	@SmallTest
	public void testDeleteEntry() throws Exception {

		String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
		Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
		nameValuePairs.put(ModuleFields.ID, beanId);
		nameValuePairs.put(ModuleFields.DELETED, "1"); //
		nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
		String _beanId = RestUtil.setEntry(url, mSessionId, moduleName,
				nameValuePairs);
		Log.d(LOG_TAG, "setEntry response : " + _beanId);
		assertNotNull(_beanId);
		assertEquals(beanId, _beanId);
		SugarBean sBean = RestUtil.getEntry(url, mSessionId, moduleName,
				beanId, selectFields, linkNameToFieldsArray);
		Log.d(LOG_TAG, "Deleted:" + sBean.getFieldValue(ModuleFields.DELETED));
	}

	@SmallTest
	public void testGetRelationships() throws Exception {
		String beanId = "63f10b82-7aa0-5105-1038-4c1f268ae69b";
		String linkFieldName = "contacts";
		String relatedModuleQuery = "";
		String[] relatedFields = { "id", "first_name", "account_id" };
		Map<String, List<String>> relatedModuleLinkNameToFieldsArray = new HashMap<String, List<String>>();
		relatedModuleLinkNameToFieldsArray.put("contacts", Arrays
				.asList(new String[] { "id", "first_name", "last_name" }));
		int deleted = 0;
		SugarBean[] sBeans = RestUtil.getRelationships(url, mSessionId,
				moduleName, beanId, linkFieldName, relatedModuleQuery,
				relatedFields, relatedModuleLinkNameToFieldsArray, deleted);
		for (SugarBean sBean : sBeans) {
			Log.i("LOG_TAG", "BeanId - " + sBean.getBeanId());
		}
	}

	@SmallTest
	public void testSetRelationship() throws Exception {
		String beanId = "63f10b82-7aa0-5105-1038-4c1f268ae69b";
		String linkFieldName = "contacts";
		String[] relatedIds = { "8ed461ad-808b-fa49-cf48-4c3c5a14bad6" };
		Map<String, String> nameValueList = new LinkedHashMap<String, String>();
		nameValueList.put(ModuleFields.ID, relatedIds[0]);
		nameValueList.put(ModuleFields.FIRST_NAME, "Nekkanti");
		nameValueList.put(ModuleFields.LAST_NAME, "Vasu");
		nameValueList.put(ModuleFields.EMAIL1, "vasu1705@gmail.com");

		int delete = 0;
		RelationshipStatus response = RestUtil.setRelationship(url, mSessionId,
				moduleName, beanId, linkFieldName, relatedIds, null, delete);
		System.out.println("setRelationship : " + response.getCreatedCount()
				+ "-" + response.getFailedCount() + "-"
				+ response.getDeletedCount());
		assertEquals(response.getCreatedCount(), 1);
	}

	@SmallTest
	public void testSetRelationships() throws Exception {
		List<Map<String, String>> nameValueLists = new ArrayList<Map<String, String>>();

		String[] moduleNames = { "Contacts" };
		String[] beanIds = { "1e9d5cb4-1972-28a4-7b36-4c1f261afd48" };
		String[] linkFieldNames = { "Contacts" };
		String[] relatedIds = { "c2503633-fdb7-2cee-d8ad-4c1f265a9ffd" };

		Map<String, String> nameValueList = new LinkedHashMap<String, String>();
		nameValueList.put(ModuleFields.ID, beanIds[0]);

		int deleted = 0;

		nameValueLists.add(nameValueList);
		RelationshipStatus response = RestUtil.setRelationships(url,
				mSessionId, moduleNames, beanIds, linkFieldNames, relatedIds,
				nameValueLists, deleted);
		assertNotNull(response.getCreatedCount());
		assertNotNull(response.getFailedCount());
		assertNotNull(response.getDeletedCount());
	}

	@SmallTest
	public void testGetEntryListWithNoSelectFields() throws Exception {
		String query = "", orderBy = "";
		String offset = 0 + "", maxResults = 5+"", deleted = 0+"";

		SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName,
				query, orderBy, offset, selectFields, linkNameToFieldsArray,
				maxResults, deleted);
		for (SugarBean sBean : sBeans) {
			System.out.println(sBean.getBeanId());
		}
	}

	public void testGetModuleFields() throws Exception {
		Module module = RestUtil.getModuleFields(url, mSessionId, moduleName,
				new String[] {});
		List<ModuleField> moduleFields = module.getModuleFields();
		for (ModuleField moduleField : moduleFields) {
			Log.d(LOG_TAG, "name :" + moduleField.getName());
			Log.d(LOG_TAG, "label :" + moduleField.getLabel());
			Log.d(LOG_TAG, "type :" + moduleField.getType());
			Log.d(LOG_TAG, "isReuired :" + moduleField.isRequired());
		}

		List<LinkField> linkFields = module.getLinkFields();
		for (LinkField linkField : linkFields) {
			Log.d(LOG_TAG, "name :" + linkField.getName());
			Log.d(LOG_TAG, "type :" + linkField.getType());
			Log.d(LOG_TAG, "relationship :" + linkField.getRelationship());
			Log.d(LOG_TAG, "module :" + linkField.getModule());
			Log.d(LOG_TAG, "beanName :" + linkField.getBeanName());
		}
	}

	@SmallTest
	public void testSearchByModule() throws Exception {
		String searchString = "beans.the.vegan";
		String[] modules = { "Accounts" };
		int offset = 0;
		int maxResults = 20;
		Map<String, SugarBean[]> searchResults = RestUtil.searchByModule(url,
				mSessionId, searchString, modules, offset, maxResults);
		for (Entry<String, SugarBean[]> entry : searchResults.entrySet()) {
			System.out.println("Module Name : " + entry.getKey());
			SugarBean[] sugarBeans = entry.getValue();
			for (int i = 0; i < sugarBeans.length; i++) {
				System.out.println("ID : "
						+ sugarBeans[i].getFieldValue(ModuleFields.ID));
				System.out.println("NAME : "
						+ sugarBeans[i].getFieldValue(ModuleFields.NAME));
				System.out
						.println("Billing address city : "
								+ sugarBeans[i]
										.getFieldValue(ModuleFields.BILLING_ADDRESS_CITY));
				System.out.println("Phone office : "
						+ sugarBeans[i]
								.getFieldValue(ModuleFields.PHONE_OFFICE));
				System.out
						.println("Assigned user name : "
								+ sugarBeans[i]
										.getFieldValue(ModuleFields.ASSIGNED_USER_NAME));
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
		linkNameToFieldsArray.put("contacts", Arrays.asList(new String[] {
				"id", "first_name", "last_name" }));
		linkNameToFieldsArray.put("opportunities", Arrays.asList(new String[] {
				"id", "name" }));
		linkNameToFieldsArray
				.put("leads", Arrays.asList(new String[] { "id" }));

		SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName,
				query, orderBy, offset + "", selectFields,
				linkNameToFieldsArray, maxResults + "", "");
		return sBeans;
	}

	private SugarBean[] getRelationshipBeans(String relationshipModule)
			throws Exception {
		int offset = 0;
		int maxResults = 5;
		SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId,
				relationshipModule, "", "", offset + "", DatabaseHelper
						.getModuleProjections(relationshipModule),
				linkNameToFieldsArray, maxResults + "", "");
		return sBeans;
	}
}
