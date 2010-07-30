package com.imaginea.android.sugarcrm.provider;

import android.app.SearchManager;
import android.net.Uri;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.RestUtilConstants;
import com.imaginea.android.sugarcrm.util.Util;

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

    public static final String MODULE_ROW_ID = "module_id";

    public static final String ROLE_ROW_ID = "role_id";

    public static final class Accounts implements AccountsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.ACCOUNTS);

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " ASC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME, EMAIL1,
                CREATED_BY_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { NAME };

        public static final String[] SEARCH_PROJECTION = { RECORD_ID,
                NAME + " AS " + (SearchManager.SUGGEST_COLUMN_TEXT_1),
                RECORD_ID + " AS " + (SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID) };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, PARENT_NAME,
                PHONE_OFFICE, PHONE_FAX, EMAIL1, WEBSITE, EMPLOYEES, TICKER_SYMBOL, ANNUAL_REVENUE,
                BILLING_ADDRESS_STREET, BILLING_ADDRESS_STREET_2, BILLING_ADDRESS_STREET_3,
                BILLING_ADDRESS_STREET_4, BILLING_ADDRESS_CITY, BILLING_ADDRESS_STATE,
                BILLING_ADDRESS_POSTALCODE, BILLING_ADDRESS_COUNTRY, SHIPPING_ADDRESS_STREET,
                SHIPPING_ADDRESS_STREET_2, SHIPPING_ADDRESS_STREET_3, SHIPPING_ADDRESS_STREET_4,
                SHIPPING_ADDRESS_CITY, SHIPPING_ADDRESS_STATE, SHIPPING_ADDRESS_POSTALCODE,
                SHIPPING_ADDRESS_COUNTRY, ASSIGNED_USER_NAME, CREATED_BY_NAME, DATE_ENTERED,
                DATE_MODIFIED, DELETED };

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

        public String BILLING_ADDRESS_STREET = ModuleFields.BILLING_ADDRESS_STREET;

        public String BILLING_ADDRESS_STREET_2 = ModuleFields.BILLING_ADDRESS_STREET_2;

        public String BILLING_ADDRESS_STREET_3 = ModuleFields.BILLING_ADDRESS_STREET_3;

        public String BILLING_ADDRESS_STREET_4 = ModuleFields.BILLING_ADDRESS_STREET_4;

        public String BILLING_ADDRESS_CITY = ModuleFields.BILLING_ADDRESS_CITY;

        public String BILLING_ADDRESS_STATE = ModuleFields.BILLING_ADDRESS_STATE;

        public String BILLING_ADDRESS_POSTALCODE = ModuleFields.BILLING_ADDRESS_POSTALCODE;;

        public String BILLING_ADDRESS_COUNTRY = ModuleFields.BILLING_ADDRESS_COUNTRY;;

        public String SHIPPING_ADDRESS_STREET = ModuleFields.SHIPPING_ADDRESS_STREET;

        public String SHIPPING_ADDRESS_STREET_2 = ModuleFields.SHIPPING_ADDRESS_STREET_2;

        public String SHIPPING_ADDRESS_STREET_3 = ModuleFields.SHIPPING_ADDRESS_STREET_3;

        public String SHIPPING_ADDRESS_STREET_4 = ModuleFields.SHIPPING_ADDRESS_STREET_4;

        public String SHIPPING_ADDRESS_CITY = ModuleFields.SHIPPING_ADDRESS_CITY;

        public String SHIPPING_ADDRESS_STATE = ModuleFields.SHIPPING_ADDRESS_STATE;

        public String SHIPPING_ADDRESS_POSTALCODE = ModuleFields.SHIPPING_ADDRESS_POSTALCODE;

        public String SHIPPING_ADDRESS_COUNTRY = ModuleFields.SHIPPING_ADDRESS_COUNTRY;

        public String DELETED = ModuleFields.DELETED;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    public static final class Contacts implements ContactsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.CONTACTS);

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
                EMAIL1, CREATED_BY_NAME };

        public static final String[] REST_LIST_PROJECTION = { ModuleFields.ID, FIRST_NAME,
                LAST_NAME, EMAIL1 };

        public static final String[] LIST_VIEW_PROJECTION = { FIRST_NAME, LAST_NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME,
                LAST_NAME, ACCOUNT_NAME, PHONE_MOBILE, PHONE_WORK, EMAIL1, CREATED_BY_NAME,
                DATE_ENTERED, DATE_MODIFIED, DELETED, ACCOUNT_ID };

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

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;
    }

    public interface AccountsContactsColumns {
        public String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        public String CONTACT_ID = ModuleFields.CONTACT_ID;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;
    }

    public interface AccountsOpportunitiesColumns {
        public String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        public String OPPORTUNITY_ID = ModuleFields.OPPORTUNITY_ID;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;
    }

    public interface AccountsCasesColumns {
        public String ACCOUNT_ID = ModuleFields.ACCOUNT_ID;

        public String CASE_ID = Util.CASE_ID;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;
    }

    public interface ContactsOpportunitiesColumns {
        public String CONTACT_ID = ModuleFields.CONTACT_ID;

        public String OPPORTUNITY_ID = ModuleFields.OPPORTUNITY_ID;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;
    }

    public interface ContactsCasesColumns {
        public String CONTACT_ID = ModuleFields.CONTACT_ID;

        // TODO - not really a todo but a cross check to see
        public String CASE_ID = Util.CASE_ID;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;
    }

    public static final class Leads implements LeadsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.LEADS);

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.NAME + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME, LAST_NAME,
                CREATED_BY_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { FIRST_NAME, LAST_NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, FIRST_NAME,
                LAST_NAME, LEAD_SOURCE, PHONE_WORK, PHONE_FAX, EMAIL1, ACCOUNT_NAME, TITLE,
                ASSIGNED_USER_NAME, CREATED_BY_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED,
                ACCOUNT_ID };

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

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;
    }

    public static final class Opportunities implements OpportunitiesColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.OPPORTUNITIES);

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME,
                OPPORTUNITY_TYPE, CREATED_BY_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, ACCOUNT_NAME,
                AMOUNT, DATE_CLOSED, OPPORTUNITY_TYPE, LEAD_SOURCE, SALES_STAGE, CAMPAIGN_NAME,
                PROBABILITY, ASSIGNED_USER_NAME, CREATED_BY_NAME, DATE_ENTERED, DATE_MODIFIED,
                DELETED, ACCOUNT_ID };

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

    public static final class Cases implements CasesColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.CASES);

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = DATE_MODIFIED + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME, CASE_NUMBER,
                PRIORITY, DATE_MODIFIED, CREATED_BY_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { NAME, PRIORITY, DATE_MODIFIED };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, CASE_NUMBER,
                PRIORITY, ASSIGNED_USER_NAME, STATUS, DESCRIPTION, RESOLUTION, CREATED_BY_NAME,
                DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    public interface CasesColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String CASE_NUMBER = ModuleFields.CASE_NUMBER;

        public String PRIORITY = ModuleFields.PRIORITY;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        public String STATUS = ModuleFields.STATUS;

        public String DESCRIPTION = ModuleFields.DESCRIPTION;

        public String RESOLUTION = ModuleFields.RESOLUTION;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    public static final class Calls implements CallsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.CALLS);

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = START_DATE + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME, START_DATE,
                CREATED_BY_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { NAME, START_DATE };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, START_DATE,
                DURATION_HOURS, DURATION_MINUTES, ASSIGNED_USER_NAME, DESCRIPTION, CREATED_BY_NAME,
                DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    public interface CallsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String STATUS = ModuleFields.STATUS;

        public String START_DATE = ModuleFields.DATE_START;

        public String DURATION_HOURS = ModuleFields.DURATION_HOURS;

        public String DURATION_MINUTES = ModuleFields.DURATION_MINUTES;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        public String DESCRIPTION = ModuleFields.DESCRIPTION;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    public static final class Meetings implements MeetingsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/" + Util.MEETINGS);

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = START_DATE + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, BEAN_ID, NAME, START_DATE,
                CREATED_BY_NAME };

        public static final String[] LIST_VIEW_PROJECTION = { NAME, START_DATE };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, BEAN_ID, NAME, STATUS,
                LOCATION, START_DATE, DURATION_HOURS, DURATION_MINUTES, ASSIGNED_USER_NAME,
                DESCRIPTION, CREATED_BY_NAME, DATE_ENTERED, DATE_MODIFIED, DELETED };

    }

    public interface MeetingsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String STATUS = ModuleFields.STATUS;

        public String LOCATION = ModuleFields.LOCATION;

        public String START_DATE = ModuleFields.DATE_START;

        public String DURATION_HOURS = ModuleFields.DURATION_HOURS;

        public String DURATION_MINUTES = ModuleFields.DURATION_MINUTES;

        public String ASSIGNED_USER_NAME = ModuleFields.ASSIGNED_USER_NAME;

        public String DESCRIPTION = ModuleFields.DESCRIPTION;

        public String DATE_ENTERED = ModuleFields.DATE_ENTERED;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String DELETED = ModuleFields.DELETED;

        public String CREATED_BY_NAME = ModuleFields.CREATED_BY_NAME;

    }

    public static final class Modules implements ModuleColumns {

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, MODULE_NAME };

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = MODULE_NAME + " DESC";
    }

    public interface ModuleColumns {

        public String ID = RECORD_ID;

        public String MODULE_NAME = RestUtilConstants.NAME;
    }

    public static final class ModuleField implements ModuleFieldColumns {

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, NAME, LABEL, TYPE,
                IS_REQUIRED, MODULE_ID };

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " DESC";
    }

    public interface ModuleFieldColumns {
        public String ID = RECORD_ID;

        public String NAME = RestUtilConstants.NAME;

        public String LABEL = RestUtilConstants.LABEL;

        public String TYPE = RestUtilConstants.TYPE;

        public String IS_REQUIRED = RestUtilConstants.REQUIRED;

        public String MODULE_ID = MODULE_ROW_ID;
    }

    public static final class LinkFields implements LinkFieldColumns {

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, NAME, TYPE, RELATIONSHIP,
                MODULE, BEAN_NAME, MODULE_ID };

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " DESC";
    }

    public interface LinkFieldColumns {
        public String ID = RECORD_ID;

        public String NAME = RestUtilConstants.NAME;

        public String TYPE = RestUtilConstants.TYPE;

        public String RELATIONSHIP = RestUtilConstants.RELATIONSHIP;

        public String MODULE = RestUtilConstants.MODULE;

        public String BEAN_NAME = RestUtilConstants.BEAN_NAME;

        public String MODULE_ID = MODULE_ROW_ID;
    }

    public interface SyncColumns {
        public String ID = RECORD_ID;

        public String SYNC_ID = Util.SYNC_ID;

        public String SYNC_RELATED_ID = Util.SYNC_RELATED_ID;

        public String SYNC_COMMAND = Util.SYNC_COMMAND;

        public String MODULE = RestUtilConstants.MODULE;

        public String RELATED_MODULE = Util.RELATED_MODULE;

        public String DATE_MODIFIED = ModuleFields.DATE_MODIFIED;

        public String SYNC_STATUS = Util.STATUS;

    }

    public static final class Sync implements SyncColumns {

        // public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
        // + "/" + Util.LEADS);

        public static final int ID_COLUMN = 0;

        public static final int SYNC_ID_COLUMN = 1;

        public static final int SYNC_RELATED_ID_COLUMN = 2;

        public static final int SYNC_COMMAND_COLUMN = 3;

        public static final int MODULE_NAME_COLUMN = 4;

        public static final int RELATED_MODULE_NAME_COLUMN = 5;

        public static final int MODIFIED_DATE_COLUMN = 6;

        public static final int STATUS_COLUMN = 7;

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.DATE_MODIFIED + " DESC";

        public static final String[] DETAILS_PROJECTION = { ID, SYNC_ID, SYNC_RELATED_ID,
                SYNC_COMMAND, MODULE, RELATED_MODULE, DATE_MODIFIED, SYNC_STATUS };

    }

    public interface ACLRoleColumns {
        public String ID = RECORD_ID;

        public String ROLE_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String TYPE = ModuleFields.TYPE;

        public String DESCRIPTION = ModuleFields.DESCRIPTION;
    }

    public static final class ACLRoles implements ACLRoleColumns {
        public static final String[] INSERT_PROJECTION = { SUGAR_BEAN_ID, NAME, TYPE, DESCRIPTION };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, SUGAR_BEAN_ID, NAME, TYPE,
                DESCRIPTION };

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " DESC";
    }

    public interface ACLActionColumns {
        public String ID = RECORD_ID;

        public String ACTION_ID = SUGAR_BEAN_ID;

        public String NAME = ModuleFields.NAME;

        public String CATEGORY = "category";

        public String ACLACCESS = "aclaccess";

        public String ACLTYPE = "acltype";

        public String ROLE_ID = ROLE_ROW_ID;
    }

    public static final class ACLActions implements ACLActionColumns {
        public static final String[] INSERT_PROJECTION = { ACTION_ID, NAME, CATEGORY, ACLACCESS,
                ACLTYPE };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, ACTION_ID, NAME, CATEGORY,
                ACLACCESS, ACLTYPE };

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = NAME + " ASC";
    }

    public interface UserColumns {
        public String ID = RECORD_ID;

        public String USER_ID = ModuleFields.ID;

        public String USER_NAME = ModuleFields.USER_NAME;

        public String FIRST_NAME = ModuleFields.FIRST_NAME;

        public String LAST_NAME = ModuleFields.LAST_NAME;
    }

    public static final class Users implements UserColumns {
        public static final String[] INSERT_PROJECTION = { USER_ID, USER_NAME, FIRST_NAME,
                LAST_NAME };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, USER_ID, USER_NAME,
                FIRST_NAME, LAST_NAME };
    }

    public interface ModuleFieldGroupColumns {
        public String ID = RECORD_ID;

        public String TITLE = "title";

        public String GROUP_ID = "group_id";
    }

    public static final class ModuleFieldGroups implements ModuleFieldGroupColumns {
        public static final String[] INSERT_PROJECTION = { TITLE, GROUP_ID };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, TITLE, GROUP_ID };
    }

    public interface ModuleFieldSortOrderColumns {
        public String ID = RECORD_ID;

        public String FIELD_SORT_ID = "field_sort_id";

        public String GROUP_ID = "group_id";

        public String MODULE_FIELD_ID = "module_field_id";

        public String MODULE_ID = "module_id";
    }

    public static final class ModuleFieldSortOrder implements ModuleFieldSortOrderColumns {
        public static final String[] INSERT_PROJECTION = { FIELD_SORT_ID, GROUP_ID,
                MODULE_FIELD_ID, MODULE_ID };

        public static final String[] DETAILS_PROJECTION = { RECORD_ID, FIELD_SORT_ID, GROUP_ID,
                MODULE_FIELD_ID, MODULE_ID };
    }
}
