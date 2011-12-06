package com.imaginea.android.sugarcrm.provider;

import android.content.Context;
import android.test.ProviderTestCase2;

/**
 * Tests of the SugarCRM Provider.
 * 
 * You can run this entire test case with: runtest -c
 * com.imaginea.android.sugarcrm.provider.ProviderTests crm
 */
public class ProviderTests extends ProviderTestCase2<SugarCRMProvider> {

    SugarCRMProvider mProvider;

    Context mMockContext;

    public ProviderTests() {
        super(SugarCRMProvider.class, SugarCRMProvider.AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mMockContext = getMockContext();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test simple account save/retrieve
     */
    public void testAccountSave() {

    }

    /**
     * 
     */
    public void testContactSave() {

    }
}
