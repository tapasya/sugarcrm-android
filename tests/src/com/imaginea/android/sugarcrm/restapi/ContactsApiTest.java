package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SBList;
import com.imaginea.android.sugarcrm.util.SugarBean;

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

    public final static String LOG_TAG = "ContactsApiTest";

    @SmallTest
    public void testGetAllModuleFields() throws Exception {

        // RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
    }

    @SmallTest
    public void testGetCustomModuleFields() throws Exception {

        // RestUtil.getModuleFields(url, mSessionId, moduleName, customFields);
    }

    @SmallTest
    public void testContactsList() throws Exception {

        String[] fields = new String[] {};
        // RestUtil.getModuleFields(url, mSessionId, moduleName, fields);
        String query = "", orderBy = "";
        int offset = 0;
        // String[] selectFields = new String[] {};
        String[] selectFields = { ModuleFields.NAME, ModuleFields.EMAIL1 };
        String[] linkNameToFieldsArray = new String[] {};
        int maxResults = 10, deleted = 0;

        SBList sbList = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
        SugarBean[] sBeans = sbList.getSBEntryList();
        assertTrue(sBeans.length > 0);

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            for (SugarBean sBean : sBeans) {
                ;
                Log.d(LOG_TAG, sBean.getBeanId());
                Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.EMAIL1));
            }
        }
    }
}
