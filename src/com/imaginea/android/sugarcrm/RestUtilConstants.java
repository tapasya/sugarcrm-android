package com.imaginea.android.sugarcrm;

public interface RestUtilConstants {

    // request params
    public static final String METHOD = "method";

    public static final String INPUT_TYPE = "input_type";

    public static final String RESPONSE_TYPE = "response_type";

    public static final String REST_DATA = "rest_data";

    public static final String APPLICATION = "application";

    public static final String NAME_VALUE_LIST = "name_value_list";

    // input and response types for the request and response
    public static final String JSON = "json";

    // methods
    public static final String LOGIN = "login";

    public static final String GET_AVAILABLE_MODULES = "get_available_modules";

    public static final String GET_ENTRY_LIST = "get_entry_list";

    public static final String GET_ENTRIES = "get_entries";

    public static final String GET_ENTRY = "get_entry";

    public static final String GET_MODULE_FIELDS = "get_module_fields";
    
    public static final String SET_ENTRY = "set_entry";

    // module names
    public static final String ACCOUNTS_MODULE = "Accounts";

    public static final String CONTACTS_MODULE = "Contacts";

    public static final String LEADS_MODULE = "Leads";

    public static final String OPPORTUNITIES_MODULE = "Opportunities";

    public static final String MEETINGS_MODULE = "Meetings";

    // params for the input JSON
    public static final String USER_AUTH = "user_auth";

    public static final String USER_NAME = "user_name";

    public static final String PASSWORD = "password";

    public static final String SESSION = "session";

    public static final String MODULE_NAME = "module_name";

    public static final String QUERY = "query";

    public static final String ORDER_BY = "order_by";

    public static final String OFFSET = "offset";

    public static final String SELECT_FIELDS = "select_fields";

    public static final String FIELDS = "fields";

    // params for the output JSON
    public static final String ID = "id";

    public static final String IDS = "ids";

    public static final String MODULES = "modules";

    public static final String MODULE_FIELDS = "module_fields";

    public static final String ENTRY_LIST = "entry_list";

    public static final String RELATIONSHIP_LIST = "relationship_list";

    public static final String RESULT_COUNT = "result_count";

    public static final String NAME = "name";

    public static final String TYPE = "type";

    public static final String LABEL = "label";

    public static final String REQUIRED = "required";

    public static final String DESCRIPTION = "description";

    public static final String LOGIN_FAILED = "Login Failed!";

    // Exceptions
    public static final String EXCEPTION = "Exception";

    public static final String JSON_EXCEPTION = "JSONException";

}
