package com.imaginea.android.sugarcrm.util;

import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;

import java.util.HashMap;
import java.util.Map;

public class ImportContactsUtility {

    private static Map<String, String> getContactApiVsModuleFieldsMap() {
        Map<String, String> contactsApiNamesVsModuleFieldNames = new HashMap<String, String>();
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.NAME, ContactsColumns.FIRST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.EMAIL, ContactsColumns.EMAIL1);
        contactsApiNamesVsModuleFieldNames.put(ContactsApiConstants.PHNO, ContactsColumns.PHONE_MOBILE);

        return contactsApiNamesVsModuleFieldNames;
    }

    public static String getModuleFieldNameForContactsField(String contactsFieldName) {
        String moduleFieldName;
        Map<String, String> contactsApiNamesVsModuleFieldNames = getContactApiVsModuleFieldsMap();
        moduleFieldName = contactsApiNamesVsModuleFieldNames.get(contactsFieldName);
        return moduleFieldName;
    }

    public interface ContactsApiConstants {

        final String NAME = "name";

        final String EMAIL = "email";

        final String PHNO = "phno";
    }
}
