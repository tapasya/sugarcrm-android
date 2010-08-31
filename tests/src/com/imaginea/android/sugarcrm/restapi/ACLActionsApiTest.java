package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;

import java.util.HashMap;
import java.util.List;

/**
 * ACLActionsApiTest, tests the rest api calls
 * 
 * @author chander
 * 
 */
public class ACLActionsApiTest extends RestAPITest {
    String moduleName = "ACL";

    String[] selectFields = { ModuleFields.ID, ModuleFields.NAME, "category", "aclaccess",
            "acltype" };

    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    public final static String LOG_TAG = ACLActionsApiTest.class.getSimpleName();

    @SmallTest
    public void testACLList() throws Exception {
        int offset = 0;
        int maxResults = 10;

        // linkNameToFieldsArray.put(key, value)
        SugarBean[] sBeans = getSugarBeans(offset, maxResults);
        assertTrue(sBeans.length > 0);

        if (Log.isLoggable(LOG_TAG, Log.INFO)) {
            for (SugarBean sBean : sBeans) {
                Log.d(LOG_TAG, sBean.getBeanId());
                Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.NAME));
                Log.d(LOG_TAG, sBean.getFieldValue(ModuleFields.DESCRIPTION));
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

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset
                                        + "", selectFields, linkNameToFieldsArray, maxResults + "", deleted
                                        + "");
        return sBeans;
    }

}
