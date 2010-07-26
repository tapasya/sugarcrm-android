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
import com.imaginea.android.sugarcrm.provider.DatabaseHelper;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLActionColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoleColumns;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ACLRoles;
import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SugarBean;
import com.imaginea.android.sugarcrm.util.SugarCrmException;

public class AclTest extends RestAPITest {

	String TAG = AclTest.class.getSimpleName();
	
	HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();
	
	String[] userSelectFields = { ModuleFields.ID };
	
	String[] aclRolesSelectFields = { ModuleFields.ID, ModuleFields.NAME, ModuleFields.TYPE,
            ModuleFields.DESCRIPTION };

    String[] aclActionsSelectFields = { ModuleFields.ID, ModuleFields.NAME, "category", "aclaccess",
            "acltype" };
    
	@SmallTest
	public void testAclAccess(){
		try{
		    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
			String moduleName = "Users"; 
			linkNameToFieldsArray.put("aclroles", Arrays.asList(aclRolesSelectFields));
			// TODO: get the user name from Account Manager
	        //String userName = SugarCrmSettings.getUsername(getContext());
			
			// this gives the user bean for the logged in user along with the acl roles associated
			SugarBean[] userBeans = RestUtil.getEntryList(url, mSessionId, moduleName, "Users.user_name='"+ userName + "'", "", "", userSelectFields, linkNameToFieldsArray, "", "");
			// userBeans always contains only one bean as we use getEntryList with the logged in user name as the query parameter 
			for(SugarBean userBean : userBeans){
			    // get the acl roles
				SugarBean[] roleBeans = userBean.getRelationshipBeans("aclroles");
				List<String> roleIds = new ArrayList<String>();
				if(roleBeans != null){
				    // get the beanIds of the roles that are inserted
				    roleIds = insertRoles(dbHelper, roleBeans);
				}
				
				for(String roleId : roleIds){
				    if(Log.isLoggable(TAG, Log.DEBUG))
				        Log.d(TAG, "roleId - " + roleId);
				    
					HashMap<String, List<String>> linkNameToFieldsArray = new HashMap<String, List<String>>();
					linkNameToFieldsArray.put("actions", Arrays.asList(aclActionsSelectFields));
					
					// get the aclRole along with the acl actions associated
					SugarBean roleBean = RestUtil.getEntry(url, mSessionId, "ACLRoles", roleId, aclRolesSelectFields, linkNameToFieldsArray);
					SugarBean[] roleRelationBeans = roleBean.getRelationshipBeans("actions");
					if(roleRelationBeans != null){
					    insertActions(dbHelper, roleId, roleRelationBeans);
					}
				}
			}
		} catch (SugarCrmException sce) {
			//Log.e(TAG, "" + sce.getMessage());
		}
	}


    private void insertActions(DatabaseHelper dbHelper, String roleId, SugarBean[] roleRelationBeans) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        for (SugarBean actionBean : roleRelationBeans) {
            
            ContentValues values = new ContentValues();
            for (int i = 0; i < aclActionsSelectFields.length; i++) {
                if(Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, actionBean.getFieldValue(aclActionsSelectFields[i]));
                
                values.put(aclActionsSelectFields[i], actionBean.getFieldValue(aclActionsSelectFields[i]));
            }
            
            // get the row id of the role
            String selection = ACLRoleColumns.ROLE_ID + "='" + roleId + "'";
            Cursor cursor = db.query(DatabaseHelper.ACL_ROLES_TABLE_NAME, ACLRoles.DETAILS_PROJECTION, selection, null, null, null, null);
            cursor.moveToFirst();
            int roleRowId = cursor.getInt(0);
            cursor.close();
            
            values.put(ACLActionColumns.ROLE_ID, roleRowId);
            db.insert(DatabaseHelper.ACL_ACTIONS_TABLE_NAME, "", values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    private List<String> insertRoles(DatabaseHelper dbHelper, SugarBean[] roleBeans) {
        List<String> roleIds = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(int i=0; i<roleBeans.length; i++){
            ContentValues values = new ContentValues();
            for(String fieldName : aclRolesSelectFields){
                if(Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, fieldName + " : " + roleBeans[i].getFieldValue(fieldName));
                
                if(fieldName.equals(ModuleFields.ID)){
                    roleIds.add(roleBeans[i].getFieldValue(fieldName));
                }
                values.put(fieldName, roleBeans[i].getFieldValue(fieldName));
            }
            db.insert(DatabaseHelper.ACL_ROLES_TABLE_NAME, "", values);
        }
        db.close();
        
        return roleIds;
    }
}
