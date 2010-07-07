package com.imaginea.android.sugarcrm.provider;

import android.net.Uri;

import com.imaginea.android.sugarcrm.ModuleFields;

/**
 * convinience class to identify the selection arguments(projections) and provide a projection map
 * to retrieve column values by name instead of column number
 * 
 * @author chander
 * 
 */
public final class SugarCRMContent {
    public static final String AUTHORITY = SugarCRMProvider.AUTHORITY;

    public static final String RECORD_ID = "_id";

    public static final class Accounts implements AccountsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/account");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.NAME + " DESC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, ModuleFields.ID,
                ModuleFields.NAME, ModuleFields.EMAIL1 };

        public static final String[] DETAILS_PROJECTION = { ModuleFields.NAME,
                ModuleFields.PARENT_NAME, ModuleFields.PHONE_OFFICE, ModuleFields.PHONE_FAX,
                ModuleFields.EMAIL1 };

    }

    public interface AccountsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = ModuleFields.ID;

        public String NAME = ModuleFields.NAME;

        public String EMAIL1 = ModuleFields.EMAIL1;

    }

    public static final class Contacts implements ContactsColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SugarCRMProvider.AUTHORITY
                                        + "/contact");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = ModuleFields.FIRST_NAME + " ASC";

        public static final String[] LIST_PROJECTION = { RECORD_ID, ModuleFields.ID,
                ModuleFields.FIRST_NAME, ModuleFields.LAST_NAME, ModuleFields.EMAIL1 };

        public static final String[] DETAILS_PROJECTION = { ModuleFields.FIRST_NAME,
                ModuleFields.LAST_NAME, ModuleFields.ACCOUNT_NAME, ModuleFields.PHONE_MOBILE,
                ModuleFields.PHONE_WORK, ModuleFields.EMAIL1 };

    }

    public interface ContactsColumns {
        public String ID = RECORD_ID;

        public String BEAN_ID = ModuleFields.ID;

        public String FIRST_NAME = ModuleFields.FIRST_NAME;

        public String LAST_NAME = ModuleFields.LAST_NAME;

        public String EMAIL1 = ModuleFields.EMAIL1;

        public String CREATED_BY = ModuleFields.CREATED_BY;

        public String MODIFIED_BY_NAME = ModuleFields.MODIFIED_BY_NAME;

    }
}
