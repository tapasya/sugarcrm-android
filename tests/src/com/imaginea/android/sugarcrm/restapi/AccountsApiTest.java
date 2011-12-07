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

/**
 * AccountssApiTest tests the REST API calls
 * 
 */
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
        // get the sugar beans
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);
        
        // retrieve the bean info 
        for (SugarBean sBean : sBeans) {
            Log.d(LOG_TAG, sBean.getBeanId());
            Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.NAME));
            assertNotNull(sBean.getBeanId());
            assertNotNull(sBean.getFieldValue(ModuleFields.NAME));
            
            // get the related beans
            SugarBean[] relationshipBeans = sBean.getRelationshipBeans("contacts");
            if (relationshipBeans != null) {
            	// retrieve the related bean info
                for (SugarBean relationshipBean : relationshipBeans) {
                    Log.d(LOG_TAG, "" + relationshipBean.getFieldValue(ModuleFields.FIRST_NAME));
                    assertNotNull(relationshipBean.getFieldValue(ModuleFields.FIRST_NAME));
                }
            }
        }
    }

    @SmallTest
    public void testEntireGetAccountsList() throws Exception {
        int offset = 0;
        int maxResults = 20;
        // get sugar beans
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertNotNull(sBeans);
        assertTrue(sBeans.length > 0);
        
        int totalRuns = 1;
        while (sBeans.length > 0) {
            offset += 20; // update the offset as we fetch the beans
            // get sugar beans based on the offset
            sBeans = getSugarBeans(offset, maxResults);
            assertNotNull(sBeans);
            totalRuns++;
        }
        Log.d(LOG_TAG, "Total Runs:" + totalRuns);
    }

    @SmallTest
    public void testGetEntry() throws Exception {
        // get only one sugar bean
    	int offset = 0;
    	int maxResults = 1;
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);
        assertNotNull(sBeans[0]);
        String beanId = sBeans[0].getBeanId();
        assertNotNull(sBeans[0].getBeanId());
        
        linkNameToFieldsArray.put("contacts", Arrays.asList(new String[] { "first_name",
                "last_name" }));
        
		// use the bean id obtained from one of the beans in getEntryList API
		// call's response with the getEntry API call to fetch a bean with id. 
        SugarBean sBean = RestUtil.getEntry(url, mSessionId, moduleName, beanId, selectFields, linkNameToFieldsArray);
        assertNotNull(sBean);
        
        Log.d(LOG_TAG, "Account Name : " + sBean.getFieldValue("name"));
        Log.d(LOG_TAG, "Account email : " + sBean.getFieldValue("email1"));
        Log.d(LOG_TAG, "Phone office : " + sBean.getFieldValue(ModuleFields.PHONE_OFFICE));
        Log.d(LOG_TAG, "Account deleted ? " + sBean.getFieldValue(ModuleFields.DELETED));
    }

    @SmallTest
    public void testGetEntriesCount() throws Exception {
        String query = "";
        int deleted = 0;
        // search the entries in the module for the keyword
        int entriesCount = RestUtil.getEntriesCount(url, mSessionId, moduleName, query, deleted
                                        + "");
        Log.d(LOG_TAG, "entriesCount = " + entriesCount);
        assertTrue(entriesCount > 0);   
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
            assertNotNull(_beanId);
        }
    }

    @SmallTest
    public void testDeleteEntry() throws Exception {
        //create a new bean
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
        // if deletion is successful we get the same beanId returned
        assertEquals(beanId, _beanId);
    }

    @SmallTest
    public void testGetRelationships() throws Exception {
        // get only one sugar bean
    	int offset = 0;
    	int maxResults = 1;
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);
        
        String beanId = sBeans[0].getBeanId();
        String linkFieldName = "contacts";
        String relatedModuleQuery = "";
        String[] relatedFields = { "id", "first_name", "account_id" };
        Map<String, List<String>> relatedModuleLinkNameToFieldsArray = new HashMap<String, List<String>>();
        relatedModuleLinkNameToFieldsArray.put("contacts", Arrays.asList(new String[] { "id",
                "first_name", "last_name" }));
        int deleted = 0;
        
        // get the related beans using the bean id obtained from one of the beans from the getEntryList API call
        SugarBean[] relationBeans = RestUtil.getRelationships(url, mSessionId, moduleName, beanId, linkFieldName, relatedModuleQuery, relatedFields, relatedModuleLinkNameToFieldsArray, deleted
                                        + "");
        assertNotNull(relationBeans);
        for (SugarBean sBean : relationBeans) {
            Log.i("LOG_TAG", "BeanId - " + sBean.getBeanId());
            assertNotNull(sBean);
        }
    }

    @SmallTest
    public void testSetRelationship() throws Exception {
        //create a new bean : Account
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Advertising Inc."); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String accountBeanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + accountBeanId);
        assertNotNull(accountBeanId);
        
        //create a new bean : Contact
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
        // use the beans created above to set one bean as a related bean for the other
        RelationshipStatus response = RestUtil.setRelationship(url, mSessionId, moduleName, contactBeanId, linkFieldName, relatedIds, null, delete
                                        + "");
        System.out.println("setRelationship : " + response.getCreatedCount() + "-"
                                        + response.getFailedCount() + "-"
                                        + response.getDeletedCount());
        assertEquals(response.getCreatedCount(), 1);
    }

    @SmallTest
    public void testSetRelationships() throws Exception {
        //create a new bean : Account
        Map<String, String> nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put(ModuleFields.NAME, "Test Account"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String accountBeanId = RestUtil.setEntry(url, mSessionId, moduleName, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + accountBeanId);
        assertNotNull(accountBeanId);
        
        //create a new bean : Contact
        nameValuePairs = new LinkedHashMap<String, String>();
        nameValuePairs.put("last_name", "Test Contact"); //
        nameValuePairs.put(ModuleFields.PHONE_OFFICE, "(078) 123-4567");
        String contactBeanId = RestUtil.setEntry(url, mSessionId, Util.CONTACTS, nameValuePairs);
        Log.d(LOG_TAG, "setEntry response : " + contactBeanId);
        assertNotNull(contactBeanId);

        List<Map<String, String>> nameValueLists = new ArrayList<Map<String, String>>();

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        String linkFieldName = dbHelper.getLinkfieldName(Util.CONTACTS);
        
        String[] moduleNames = { "Accounts" };
        String[] beanIds = { accountBeanId };
        String[] linkFieldNames = { linkFieldName };
        String[] relatedIds = { contactBeanId };

        Map<String, String> nameValueList = new LinkedHashMap<String, String>();
        nameValueList.put(ModuleFields.ID, beanIds[0]);

        int deleted = 0;

        nameValueLists.add(nameValueList);
        RelationshipStatus response = RestUtil.setRelationships(url, mSessionId, moduleNames, beanIds, linkFieldNames, relatedIds, nameValueLists, deleted
                                        + "");
        
        assertTrue(response.getCreatedCount() >= 1);
    }

    @SmallTest
    public void testGetEntryListWithNoSelectFields() throws Exception {
        String query = "", orderBy = "";
        String offset = 0 + "", maxResults = 5 + "", deleted = 0 + "";

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
        assertTrue(sBeans.length > 0);
        for (SugarBean sBean : sBeans) {
            System.out.println(sBean.getBeanId());
            assertNotNull(sBean);
        }
    }

    public void testGetModuleFields() throws Exception {
    	// obtain the modules fields of the module
        Module module = RestUtil.getModuleFields(url, mSessionId, moduleName, new String[] {});
        assertNotNull(module);
        List<ModuleField> moduleFields = module.getModuleFields();
        assertNotNull(moduleFields);
        assertTrue(moduleFields.size() > 0);
        
        // get the module fields
        for (ModuleField moduleField : moduleFields) {
            Log.d(LOG_TAG, "name :" + moduleField.getName());
            Log.d(LOG_TAG, "label :" + moduleField.getLabel());
            Log.d(LOG_TAG, "type :" + moduleField.getType());
            Log.d(LOG_TAG, "isReuired :" + moduleField.isRequired());
            assertNotNull(moduleField);
            assertNotNull(moduleField.getName());
        }

        // get the linked module fields
        List<LinkField> linkFields = module.getLinkFields();
        assertNotNull(linkFields);
        for (LinkField linkField : linkFields) {
            Log.d(LOG_TAG, "name :" + linkField.getName());
            Log.d(LOG_TAG, "type :" + linkField.getType());
            Log.d(LOG_TAG, "relationship :" + linkField.getRelationship());
            Log.d(LOG_TAG, "module :" + linkField.getModule());
            Log.d(LOG_TAG, "beanName :" + linkField.getBeanName());
            assertNotNull(linkField);
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
        assertNotNull(searchResults);
        for (Entry<String, SugarBean[]> entry : searchResults.entrySet()) {
            System.out.println("Module Name : " + entry.getKey());
            SugarBean[] sugarBeans = entry.getValue();
            for (int i = 0; i < sugarBeans.length; i++) {
                System.out.println("ID : " + sugarBeans[i].getFieldValue(ModuleFields.ID));
                System.out.println("NAME : " + sugarBeans[i].getFieldValue(ModuleFields.NAME));
                System.out.println("Billing address city : "
                                                + sugarBeans[i].getFieldValue(ModuleFields.BILLING_ADDRESS_CITY));
                System.out.println("Phone office : "
                                                + sugarBeans[i].getFieldValue(ModuleFields.PHONE_OFFICE));
                System.out.println("Assigned user name : "
                                                + sugarBeans[i].getFieldValue(ModuleFields.ASSIGNED_USER_NAME));
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
        //int deleted = 0;
        linkNameToFieldsArray.put("contacts", Arrays.asList(new String[] { "id", "first_name",
                "last_name" }));
        linkNameToFieldsArray.put("opportunities", Arrays.asList(new String[] { "id", "name" }));
        linkNameToFieldsArray.put("leads", Arrays.asList(new String[] { "id" }));

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset
                                        + "", selectFields, linkNameToFieldsArray, maxResults + "", "");
        return sBeans;
    }

    private SugarBean[] getRelationshipBeans(String relationshipModule) throws Exception {
        int offset = 0;
        int maxResults = 5;
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, relationshipModule, "", "", offset
                                        + "", dbHelper.getModuleProjections(relationshipModule), linkNameToFieldsArray, maxResults
                                        + "", "");
        return sBeans;
    }
}
