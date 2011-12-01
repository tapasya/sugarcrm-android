package com.imaginea.android.sugarcrm.restapi;

import android.test.suitebuilder.annotation.SmallTest;

import com.imaginea.android.sugarcrm.util.RestUtil;

public class ServerInfoTest extends RestAPITest {

	@SmallTest
	public void testGetServerInfo() throws Exception {
		String serverVersion = RestUtil.getServerInfo(url);
		assertNotNull(serverVersion);
		assertEquals("6.3.0", serverVersion);
	}
}
