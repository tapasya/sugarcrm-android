package com.imaginea.android.sugarcrm.restapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.RestUtilConstants;
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActionColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActions;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoleColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoles;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.SugarCrmException;
import com.imaginea.android.sugarcrm.util.Util;

public class AclTest extends RestAPITest {

    String TAG = AclTest.class.getSimpleName();

    HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();

    String[] userSelectFields = { ModuleFields.ID };

    @SmallTest
    public void testAclAccess() {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            String moduleName = Util.USERS;
            linkNameToFieldsArray.put("aclroles", Arrays.asList(ACLRoles.INSERT_PROJECTION));

            HashMap<String, List<String>> linkNameToFieldsArrayForActions = new HashMap<String, List<String>>();
            linkNameToFieldsArrayForActions.put(dbHelper.getLinkfieldName(Util.ACLACTIONS), Arrays.asList(ACLActions.INSERT_PROJECTION));
            // TODO: get the user name from Account Manager
            // String userName = SugarCrmSettings.getUsername(getContext());

            // this gives the user bean for the logged in user along with the acl roles associated
            SugarBean[] userBeans = RestUtil.getEntryList(url, mSessionId, moduleName, "Users.user_name='"
                                            + userName + "'", "", "", userSelectFields, linkNameToFieldsArray, "", "");
            // userBeans always contains only one bean as we use getEntryList with the logged in
            // user name as the query parameter
            for (SugarBean userBean : userBeans) {
                // get the acl roles
                SugarBean[] roleBeans = userBean.getRelationshipBeans("aclroles");
                List<String> roleIds = new ArrayList<String>();
                if (roleBeans != null) {
                    // get the beanIds of the roles that are inserted
                    roleIds = dbHelper.insertRoles(roleBeans);
                    
                 // get the acl actions for each roleId
                    for (String roleId : roleIds) {
                        if (Log.isLoggable(TAG, Log.DEBUG))
                            Log.d(TAG, "roleId - " + roleId);
                        // get the aclRole along with the acl actions associated
                        SugarBean roleBean = RestUtil.getEntry(url, mSessionId, Util.ACLROLES, roleId, ACLRoles.INSERT_PROJECTION, linkNameToFieldsArrayForActions);
                        SugarBean[] roleRelationBeans = roleBean.getRelationshipBeans("actions");
                        if (roleRelationBeans != null) {
                            dbHelper.insertActions(roleId, roleRelationBeans);
                        }
                    }
                }

            }
        } catch (SugarCrmException sce) {
            Log.e(TAG, "" + sce.getMessage());
        }
    }

}
