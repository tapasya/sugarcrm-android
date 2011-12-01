package com.imaginea.android.sugarcrm.util;

import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;

import java.util.HashMap;
import java.util.Map;

public class ImportContactsUtility {

    private static Map<String, String> getContactApiVsModuleFieldsMap() {
        Map<String, String> contactsApiNamesVsModuleFieldNames = new HashMap<String, String>();
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.FIRST_NAME, ContactsColumns.FIRST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.LAST_NAME, ContactsColumns.LAST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.EMAIL, ContactsColumns.EMAIL1);
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.PHONE_MOBILE, ContactsColumns.PHONE_MOBILE);
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.PHONE_WORK, ContactsColumns.PHONE_WORK);

        return contactsApiNamesVsModuleFieldNames;
    }

    public static String getModuleFieldNameForContactsField(String contactsFieldName) {
        String moduleFieldName;
        Map<String, String> contactsApiNamesVsModuleFieldNames = getContactApiVsModuleFieldsMap();
        moduleFieldName = contactsApiNamesVsModuleFieldNames.get(contactsFieldName);
        return moduleFieldName;
    }

    public interface ContactsApiConstants {

        final String FIRST_NAME = "first_name";

        final String LAST_NAME = "last_name";

        final String EMAIL = "email";

        final String PHONE_MOBILE = "phone_mobile";

        final String PHONE_WORK = "phone_work";
    }
}
