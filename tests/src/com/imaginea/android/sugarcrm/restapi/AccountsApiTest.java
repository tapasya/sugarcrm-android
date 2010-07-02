package com.imaginea.android.sugarcrm.restapi;

import java.util.List;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.util.ModuleField;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;

public class AccountsApiTest extends RestAPITest {
	String moduleName = "Accounts";

	String[] fields = new String[] {};

	String[] customFields = new String[] { "a", "b" };

	String[] selectFields = { ModuleFields.NAME, ModuleFields.PARENT_NAME,
			ModuleFields.PHONE_OFFICE, ModuleFields.PHONE_FAX,
			ModuleFields.EMAIL1 };

	String[] linkNameToFieldsArray = new String[] {};

	public final static String LOG_TAG = "ContactsApiTest";

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

	public void testGetModuleFields() throws Exception {
		List<ModuleField> moduleFields = RestUtil.getModuleFields(url,
				mSessionId, moduleName, selectFields);
		for (ModuleField moduleField : moduleFields) {
			System.out.println("name :" + moduleField.getName());
			System.out.println("label :" + moduleField.getName());
			System.out.println("type :" + moduleField.getName());
			System.out.println("isReuired :" + moduleField.ismIsRequired());
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
				query, orderBy, offset, selectFields, linkNameToFieldsArray,
				maxResults, deleted);
		return sBeans;
	}
}
