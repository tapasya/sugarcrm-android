package com.imaginea.android.sugarcrm;

import android.app.Application;

public class SugarCrmApp extends Application {

	/*
	 * sessionId is obtained after successful login into the Sugar CRM instance Now, sessionId will
	 * be available to the entire application Access the sessionId from any part of the application
	 * as follows : SugarCrmApp app = ((SugarCrmApp) getApplicationContext()); 
	 * app.getmSessionId();
	 */
	private String mSessionId;

	public String getmSessionId() {
		return mSessionId;
	}

	public void setmSessionId(String mSessionId) {
		this.mSessionId = mSessionId;
	}

}
