package com.imaginea.android.sugarcrm;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/**
 * A test suite containing all tests for SugarCRM Application.
 * 
 * To run all suites found in this apk: $ adb shell am instrument -w \
 * com.imaginea.android.sugarcrm/android.test.InstrumentationTestRunner
 * 
 * To run just this suite from the command line: $ adb shell am instrument -w \
 * -e class com.imaginea.android.sugarcrm.AllTests \
 * com.imaginea.android.sugarcrm/android.test.InstrumentationTestRunner
 * 
 * To run an individual test case, e.g.
 * {@link com.imaginea.android.sugarcrm.restapi.RestAPITest}: $ adb shell am
 * instrument -w \ -e class com.imaginea.android.sugarcrm.restapi.RestAPITest \
 * com.imaginea.android.sugarcrm/android.test.InstrumentationTestRunner
 * 
 * To run an individual test, e.g.
 * {@link com.imaginea.android.sugarcrm.restapi.RestAPITest#testContactList()}:
 * $ adb shell am instrument -w \ -e class
 * com.imaginea.android.sugarcrm.restapi.RestAPITest#testContactList \
 * com.imaginea.android.sugarcrm/android.test.InstrumentationTestRunner
 */
public class AllTests extends TestSuite {

	public static Test suite() {
		return new TestSuiteBuilder(AllTests.class)
				.includeAllPackagesUnderHere().build();
	}
}
