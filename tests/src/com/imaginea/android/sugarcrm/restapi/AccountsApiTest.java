package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.SBList;
import com.imaginea.android.sugarcrm.util.SugarBean;

public class AccountsApiTest extends RestAPITest{
	String moduleName = "Accounts";
	
	@SmallTest
	public void testGetEntryList() throws Exception{
		String query = "", orderBy = "";
		int offset = 0, maxResults = 5, deleted = 0;
		String[] selectFields = {"name", "email1"};
		String[] linkNameToFieldsArray = {};
		
		SBList sbList = RestUtil.getEntryList(url, mSessionId, moduleName, query, orderBy, offset, selectFields, linkNameToFieldsArray, maxResults, deleted);
		SugarBean[] sBeans = sbList.getSBEntryList();
		for(SugarBean sBean : sBeans){
			System.out.println(sBean.getBeanId());
			System.out.println(sBean.getFieldValue("email1"));
		}
	}
}
