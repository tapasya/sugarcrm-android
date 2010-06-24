package com.imaginea.android.sugarcrm.restapi;

import junit.framework.TestCase;

import com.imaginea.android.sugarcrm.util.RestUtil;
import com.imaginea.android.sugarcrm.util.Util;

/**
 * 
 * Since this test doesn't need a {@link android.content.Context}, or any other
 * dependencies injected, it simply extends the standard {@link TestCase}.
 * 
 * See {@link com.imaginea.android.sugarcrm.AllTests} for documentation on
 * running all tests and individual tests in this application.
 */
public class RestAPITest extends TestCase {

	protected String mSessionId;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		authenticate();
	}

	public void authenticate() throws Exception {
		String url = "http://192.168.1.83/sugarcrm/service/v2/rest.php";
		String userName = "will";
		String password = "will";
		String md5Password;

		md5Password = Util.MD5(password);

		mSessionId = RestUtil.loginToSugarCRM(url, userName, md5Password);
		assertNotNull(mSessionId);
	}
}
