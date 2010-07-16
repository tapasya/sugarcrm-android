package com.imaginea.android.sugarcrm.provider;

import android.net.Uri;

import com.imaginea.android.sugarcrm.ModuleFields;

/**
 * Convenience class to identify the selection arguments(projections) and provide a projection map
 * to retrieve column values by name instead of column number
 * 
 * @author chander
 * 
 */
public final class SugarCRMContent {
    public static final String AUTHORITY = SugarCRMProvider.AUTHORITY;

    public static final String RECORD_ID = "_id";

    public static final String SUGAR_BEAN_ID = ModuleFields.ID;

    public static final class Accounts implements AccountsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/account");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME, EMAIL1 };

        public static final String[] LIST_VIEW_PROJECTION = { NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, PARENT_NAME,
                PHONE_OFFICE, PHONE_FAX, EMAIL1, WEBSITE, EMPLOYEES, TICKER_SYMBOL, ANNUAL_REVENUE,
                ASSIGNED_USER_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    public interface AccountsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String EMAIL1 = ModuleFields.EMAIL1;

        public String PARENT_NAME = ModuleFields.PARENT_NAME;

        public String PHONE_OFFICE = ModuleFields.PHONE_OFFICE;

        public String PHONE_FAX = ModuleFields.PHONE_FAX;

        public String WEBSITE = ModuleFields.WEBSITE;

        public String EMPLOYEES = ModuleFields.EMPLOYEES;

        public String TICKER_SYMBOL = ModuleFields.TICKER_SYMBOL;

        public String ANNUAL_REVENUE = ModuleFields.ANNUAL_REVENUE;

        public String DELETED = ModuleFields.DELETED;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

    }

    public static final class Contacts implements ContactsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/contact");

        public static final int ID_COLUMN = 0;

        public static final int BEAN_ID_COLUMN = 1;

        public static final int FIRST_NAME_COLUMN = 2;

        public static final int LAST_NAME_COLUMN = 3;

        public static final int ACCOUNT_NAME_COLUMN = 4;

        public static final int PHONE_MOBILE_COLUMN = 5;

        public static final int PHONE_WORK_COLUMN = 6;

        public static final int CREATED_BY_COLUMN = 7;

        public static final int MODIFIED_BY_NAME_COLUMN = 8;

        public static final int EMAIL1_COLUMN = 9;

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = FIRST_NAME + " ASC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME, LAST_NAME,
                EMAIL1 };

        public static final String[] REST_LIST_PROJECTION = { ModuleFields.ID, FIRST_NAME,
                LAST_NAME, EMAIL1 };

        public static final String[] LIST_VIEW_PROJECTION = { FIRST_NAME, LAST_NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME,
                LAST_NAME, ACCOUNT_NAME, PHONE_MOBILE, PHONE_WORK, EMAIL1, DATE_ENTERED,
                DATE_MODIFIED, DELETED, ACCOUNT_ID };

    }

    public interface ContactsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String FIRST_NAME = ModuleFields.FIRST_NAME;

        public String LAST_NAME = ModuleFields.LAST_NAME;

        public String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        public String PHONE_MOBILE = ModuleFields.PHONE_MOBILE;

        public String PHONE_WORK = ModuleFields.PHONE_WORK;

        public String EMAIL1 = ModuleFields.EMAIL1;

        public String CREATED_BY = ModuleFields.CREATED_BY;

        public String MODIFIED_BY_NAME = ModuleFields.MODIFIED_BY_NAME;

        public String DELETED = ModuleFields.DELETED;

        // TODO: may move out to separate table having the contact beanId and accountId
        public String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;
    }

    public static final class Leads implements LeadsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/lead");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.NAME + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME, LAST_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { FIRST_NAME, LAST_NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME,
                LAST_NAME, LEAD_SOURCE, PHONE_WORK, PHONE_FAX, EMAIL1, ACCOUNT_NAME, TITLE,
                ASSIGNED_USER_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED, ACCOUNT_ID };

    }

    public interface LeadsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String FIRST_NAME = ModuleFields.FIRST_NAME;

        public String LAST_NAME = ModuleFields.LAST_NAME;

        public String LEAD_SOURCE = ModuleFields.LEAD_SOURCE;

        public String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        public String PHONE_MOBILE = ModuleFields.PHONE_MOBILE;

        public String EMAIL1 = ModuleFields.EMAIL1;

        public String PHONE_WORK = ModuleFields.PHONE_WORK;

        public String PHONE_FAX = ModuleFields.PHONE_FAX;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        public String TITLE = ModuleFields.TITLE;

        public String DELETED = ModuleFields.DELETED;

        public String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;
    }

    public static final class Opportunities implements OpportunitiesColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/opportunity");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME, OPPORTUNITY_TYPE };

        public static final String[] LIST_VIEW_PROJECTION = { NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, ACCOUNT_NAME,
                AMOUNT, DATE_CLOSED, OPPORTUNITY_TYPE, LEAD_SOURCE, SALES_STAGE, CAMPAIGN_NAME,
                PROBABILITY, ASSIGNED_USER_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED, ACCOUNT_ID };

    }

    public interface OpportunitiesColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String MODIFIED_USER_ID = ModuleFields.MODIFIED_USER_ID;

        public String MODIFIED_BY_NAME = ModuleFields.MODIFIED_BY_NAME;

        public String CREATED_BY = ModuleFields.CREATED_BY;

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

        public String DESCRIPTION = ModuleFields.DESCRIPTION;

        public String ASSIGNED_USER_ID = ModuleFields.ASSIGNED_USER_ID;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        // public String CREATED_BY_NAME = ModuleFields.T;
        //
        public String OPPORTUNITY_TYPE = ModuleFields.OPPORTUNITY_TYPE;

        public String ACCOUNT_NAME = ModuleFields.ACCOUNT_NAME;

        public String CAMPAIGN_NAME = ModuleFields.CAMPAIGN_NAME;

        public String LEAD_SOURCE = ModuleFields.LEAD_SOURCE;

        public String AMOUNT = ModuleFields.AMOUNT;

        public String AMOUNT_USDOLLAR = ModuleFields.AMOUNT_USDOLLAR;

        public String CURRENCY_ID = ModuleFields.CURRENCY_ID;

        public String CURRENCY_NAME = ModuleFields.CURRENCY_NAME;

        public String CURRENCY_SYMBOL = ModuleFields.CURRENCY_SYMBOL;

        public String DATE_CLOSED = ModuleFields.DATE_CLOSED;

        public String NEXT_STEP = ModuleFields.NEXT_STEP;

        public String SALES_STAGE = ModuleFields.SALES_STAGE;

        public String PROBABILITY = ModuleFields.PROBABILITY;

        public String DELETED = ModuleFields.DELETED;

        public String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

    }

}
