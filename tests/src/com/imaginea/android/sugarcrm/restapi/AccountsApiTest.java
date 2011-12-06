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
import com.imaginea.android.sugarcrm.util.Util;

public class AccountsApiTest extends RestAPITest {
    String moduleName = Util.ACCOUNTS;

    String[] fields = new String[] {};

    String[] selectFields = { ModuleFields.NAME, ModuleFields.PARENT_NAME,
            ModuleFields.PHONE_OFFICE, ModuleFields.PHONE_FAX, ModuleFields.EMAIL1,
            ModuleFields.DELETED };

    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    public final static String LOG_TAG = AccountsApiTest.class.getSimpleName();

    @SmallTest
    public void testGetAccountsList() throws Exception {
        int offset = 15;
        int maxResults = 2;
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);
        for (SugarBean sBean : sBeans) {
            Log.d(LOG_TAG, sBean.getBeanId());
            Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.NAME));
            SugarBean[] relationshipBeans = sBean.getRelationshipBeans("contacts");
            if (relationshipBeans != null) {
                for (SugarBean relationshipBean : relationshipBeans) {
                    Log.d(LOG_TAG, "" + relationshipBean.getFieldValue(ModuleFields.FIRST_NAME));
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
        // get only one sugar bean
        SugarBean[] sBeans = getSugarBeans(0, 1);
        assertTrue(sBeans.length > 0);
        String beanId = sBeans[0].getBeanId();
        linkNameToFieldsArray.put("contacts", Arrays.asList(new String[] { "first_name",
                "last_name" }));
        SugarBean sBean = RestUtil.getEntry(url, mSessionId, moduleName, beanId, selectFields, linkNameToFieldsArray);
        Log.d(LOG_TAG, "Account Name : " + sBean.getFieldValue("name"));
        Log.d(LOG_TAG, "Account email : " + sBean.getFieldValue("email1"));
        Log.d(LOG_TAG, "Phone office : " + sBean.getFieldValue(ModuleFields.PHONE_OFFICE));
        Log.d(LOG_TAG, "Account deleted ? " + sBean.getFieldValue(ModuleFields.DELETED));
    }

    @SmallTest
    public void testGetEntriesCount() throws Exception {
        String query = "";
        int deleted = 0;
        int entriesCount = RestUtil.getEntriesCount(url, mSessionId, moduleName, query, deleted
                                        + "");
        Log.d(LOG_TAG, "entriesCount = " + entriesCount);
        assertNotNull(entriesCount);
    }

    @SmallTest
    public void testSetEntry() throws Exception {
        // create a sugar bean
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String beanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + beanId);
        assertNotNull(beanId);

        // modify the newly created bean
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.ID, beanId);
        nameValuePairs.put(ModuleFields.NAME, "R R Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String _beanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + _beanId);
        assertNotNull(_beanId);
        // if update is successful we get the same beanId returned
        assertEquals(beanId, _beanId);
    }

    @SmallTest
    public void testSetEntries() throws Exception {
        List<Map<String, String>> beanNameValuePairs = new ArrayList<Map<String, String>>();

        // create a sugar bean
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String beanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + beanId);
        assertNotNull(beanId);

        beanNameValuePairs.add(nameValuePairs);
        List<String> beanIds = RestUtil.setEntries(url, mSessionId, moduleName, beanNameValuePairs);
        for (String _beanId : beanIds) {
            Log.d(LOG_TAG, _beanId);
        }
    }

    @SmallTest
    public void testDeleteEntry() throws Exception {
        // create a new bean
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String beanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + beanId);
        assertNotNull(beanId);

        // delete the newly created bean
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.ID, beanId);
        nameValuePairs.put(ModuleFields.DELETED, "1"); //
        String _beanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + _beanId);
        assertNotNull(_beanId);
        assertEquals(beanId, _beanId);
    }

    @SmallTest
    public void testGetRelationships() throws Exception {
        // get only one sugar bean
        SugarBean[] sBeans = getSugarBeans(0, 1);
        assertTrue(sBeans.length > 0);
        String beanId = sBeans[0].getBeanId();
        String linkFieldName = "contacts";
        String relatedModuleQuery = "";
        String[] relatedFields = { "id", "first_name", "account_id" };
        Map<String, List<String>> relatedModuleLinkNameToFieldsArray = new HashMap<String, List<String>>();
        relatedModuleLinkNameToFieldsArray.put("contacts", Arrays.asList(new String[] { "id",
                "first_name", "last_name" }));
        int deleted = 0;
        SugarBean[] relationBeans = RestUtil.getRelationships(url, mSessionId, moduleName, beanId, linkFieldName, relatedModuleQuery, relatedFields, relatedModuleLinkNameToFieldsArray, deleted
                                        + "");
        for (SugarBean sBean : relationBeans) {
            Log.i("LOG_TAG", "BeanId - " + sBean.getBeanId());
        }
    }

    @SmallTest
    public void testSetRelationship() throws Exception {
        // create a new bean : Account
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String accountBeanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + accountBeanId);
        assertNotNull(accountBeanId);

        // create a new bean : Contact
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Contact"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String contactBeanId = RestUtil.setEntry(url, mSessionId, Util.CONTACTS, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + contactBeanId);
        assertNotNull(contactBeanId);

        String linkFieldName = "contacts";
        String[] relatedIds = { contactBeanId };
        Map<String, String> nameValueList = new LinkedHashMap<String, String>();
        nameValueList.put(ModuleFields.ID, relatedIds[0]);
        nameValueList.put(ModuleFields.FIRST_NAME, "Nekkanti");
        nameValueList.put(ModuleFields.LAST_NAME, "Vasu");
        nameValueList.put(ModuleFields.EMAIL1, "vasu1705@gmail.com");

        int delete = 0;
        RelationshipStatus response = RestUtil.setRelationship(url, mSessionId, moduleName, contactBeanId, linkFieldName, relatedIds, null, delete
                                        + "");
        Log.d(LOG_TAG, "setRelationship : " + response.getCreatedCount() + "-"
                                        + response.getFailedCount() + "-"
                                        + response.getDeletedCount());
        assertEquals(response.getCreatedCount(), 1);
    }

    @SmallTest
    public void testSetRelationships() throws Exception {
        // create a new bean : Account
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String accountBeanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + accountBeanId);
        assertNotNull(accountBeanId);

        // create a new bean : Contact
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Contact"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String contactBeanId = RestUtil.setEntry(url, mSessionId, Util.CONTACTS, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + contactBeanId);
        assertNotNull(contactBeanId);

        List<Map<String, String>> nameValueLists = new ArrayList<Map<String, String>>();

        String[] moduleNames = { "Accounts" };
        String[] beanIds = { accountBeanId };
        String[] linkFieldNames = { "Contacts" };
        String[] relatedIds = { contactBeanId };

        Map<String, String> nameValueList = new LinkedHashMap<String, String>();
        nameValueList.put(ModuleFields.ID, beanIds[0]);

        int deleted = 0;

        nameValueLists.add(nameValueList);
        RelationshipStatus response = RestUtil.setRelationships(url, mSessionId, moduleNames, beanIds, linkFieldNames, relatedIds, nameValueLists, deleted
                                        + "");
        assertNotNull(response.getCreatedCount());
        assertNotNull(response.getFailedCount());
        assertNotNull(response.getDeletedCount());
    }

    @SmallTest
    public void testGetEntryListWithNoSelectFields() throws Exception {
        String query = "", orderBy = "";
        String offset = 0 + "", maxResults = 5 + "", deleted = 0 + "";

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
        for (SugarBean sBean : sBeans) {
            Log.d(LOG_TAG, sBean.getBeanId());
        }
    }

    public void testGetModuleFields() throws Exception {
        Module module = RestUtil.getModuleFields(url, mSessionId, moduleName, new String[] {});
        List<ModuleField> moduleFields = module.getModuleFields();
        for (ModuleField moduleField : moduleFields) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "name :" + moduleField.getName());
                Log.d(LOG_TAG, "label :" + moduleField.getLabel());
                Log.d(LOG_TAG, "type :" + moduleField.getType());
                Log.d(LOG_TAG, "isReuired :" + moduleField.isRequired());
            }
        }

        List<LinkField> linkFields = module.getLinkFields();
        for (LinkField linkField : linkFields) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "name :" + linkField.getName());
                Log.d(LOG_TAG, "type :" + linkField.getType());
                Log.d(LOG_TAG, "relationship :" + linkField.getRelationship());
                Log.d(LOG_TAG, "module :" + linkField.getModule());
                Log.d(LOG_TAG, "beanName :" + linkField.getBeanName());
            }
        }
    }

    @SmallTest
    public void testSearchByModule() throws Exception {
        String searchString = "beans.the.vegan";
        String[] modules = { "Accounts" };
        int offset = 0;
        int maxResults = 20;
        Map<String, SugarBean[]> searchResults = RestUtil.searchByModule(url, mSessionId, searchString, modules, offset
                                        + "", maxResults + "");
        for (Entry<String, SugarBean[]> entry : searchResults.entrySet()) {
            Log.d(LOG_TAG, "Module Name : " + entry.getKey());
            SugarBean[] sugarBeans = entry.getValue();
            for (int i = 0; i < sugarBeans.length; i++) {
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.d(LOG_TAG, "ID : " + sugarBeans[i].getFieldValue(ModuleFields.ID));
                    Log.d(LOG_TAG, "NAME : " + sugarBeans[i].getFieldValue(ModuleFields.NAME));
                    Log.d(LOG_TAG, "Billing address city : "
                                                    + sugarBeans[i].getFieldValue(ModuleFields.BILLING_ADDRESS_CITY));
                    Log.d(LOG_TAG, "Phone office : "
                                                    + sugarBeans[i].getFieldValue(ModuleFields.PHONE_OFFICE));
                    Log.d(LOG_TAG, "Assigned user name : "
                                                    + sugarBeans[i].getFieldValue(ModuleFields.ASSIGNED_USER_NAME));
                }
            }
        }
    }

    /**
     * demonstrates the usage of RestUtil for contacts List. ModuleFields.NAME or FULL_NAME is not
     * returned by Sugar CRM. The fields that are not returned by SugarCRM can be automated, but not
     * yet generated
     * 
     * @param offset
     * @param maxResults
     * @return
     * @throws Exception
     */
    private SugarBean[] getSugarBeans(int offset, int maxResults) throws Exception {
        String query = "", orderBy = "";
        int deleted = 0;
        linkNameToFieldsArray.put("contacts", Arrays.asList(new String[] { "id", "first_name",
                "last_name" }));
        linkNameToFieldsArray.put("opportunities", Arrays.asList(new String[] { "id", "name" }));
        linkNameToFieldsArray.put("leads", Arrays.asList(new String[] { "id" }));

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset
                                        + "", selectFields, linkNameToFieldsArray, maxResults + "", ""); 
        return sBeans;
    }
}
