package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;

public class AccountsApiTest extends RestAPITest {
    String moduleName = "Accounts";

    @SmallTest
    public void testGetEntryList() throws Exception {
        String query = "", orderBy = "";
        int offset = 0, maxResults = 5, deleted = 0;
        String[] selectFields = { "name", "email1" };
        String[] linkNameToFieldsArray = {};

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);

        for (SugarBean sBean : sBeans) {
            System.out.println(sBean.getBeanId());
            System.out.println(sBean.getFieldValue("email1"));
        }
    }

    @SmallTest
    public void testGetEntry() throws Exception {
        String beanId = "1e9d5cb4-1972-28a4-7b36-4c1f261afd48";
        String[] selectFields = { "name", "email1" };
        String[] linkNameToFieldsArray = {};

        SugarBean sBean = RestUtil.getEntry(url, mSessionId, moduleName, beanId, selectFields, linkNameToFieldsArray);
        System.out.println("Account Name : " + sBean.getFieldValue("name"));
        System.out.println("Account email : " + sBean.getFieldValue("email1"));
    }

    @SmallTest
    public void testGetEntryListWithNoSelectFields() throws Exception {
        String query = "", orderBy = "";
        int offset = 0, maxResults = 5, deleted = 0;
        String[] selectFields = {};
        String[] linkNameToFieldsArray = {};

        SugarBean[] sBeans = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
        for (SugarBean sBean : sBeans) {
            System.out.println(sBean.getBeanId());
        }
    }
}
