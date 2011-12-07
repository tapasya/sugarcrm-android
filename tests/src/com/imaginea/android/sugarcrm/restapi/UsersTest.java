package com.imaginea.android.sugarcrm.restapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.Users;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.Util;

public class UsersTest extends RestAPITest {

    String moduleName = Util.USERS;

    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    @SmallTest
    public void testUsersInsertion() throws Exception {
        SugarBean[] userBeans = getUsers();
        assertNotNull(userBeans);
        assertTrue(userBeans.length > 0);

        Map<String, Map<String, String>> usersMap = new TreeMap<String, Map<String, String>>();
        for (SugarBean userBean : userBeans) {
        	assertNotNull(userBean);
            Map<String, String> userBeanValues = getUserBeanValues(userBean);
            assertNotNull(userBeanValues);
            String userName = userBean.getFieldValue(ModuleFields.USER_NAME);
            assertNotNull(userName);
            if (userBeanValues != null & userBeanValues.size() > 0)
                usersMap.put(userName, userBeanValues);
        }

        // this is not a test, so commenting for now, till a set of users can be cleanly inserted
        // into the system and the result asserted
        // DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        // dbHelper.insertUsers(usersMap);
    }

    private Map<String, String> getUserBeanValues(SugarBean userBean) {
        Map<String, String> userBeanValues = new TreeMap<String, String>();
        for (String fieldName : Users.INSERT_PROJECTION) {
            String fieldValue = userBean.getFieldValue(fieldName);
            userBeanValues.put(fieldName, fieldValue);
        }
        
        return userBeanValues;
    }

    private SugarBean[] getUsers() throws Exception {
        return RestUtil.getEntryList(url, mSessionId, moduleName, null, null, "0", Users.INSERT_PROJECTION, linkNameToFieldsArray, null, "0");
    }
}
