package com.imaginea.android.sugarcrm.util;

import java.util.HashMap;
import java.util.Map;

import com.imaginea.android.sugarcrm.ModuleFields;
import com.imaginea.android.sugarcrm.provider.SugarCRMContent.ContactsColumns;

public class ImportContactsUtility {

    private static Map<String, String> contactsApiNamesVsModuleFieldNames;

    static {
        contactsApiNamesVsModuleFieldNames = new HashMap<String, String>();
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.FIRST_NAME, ContactsColumns.FIRST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.LAST_NAME, ContactsColumns.LAST_NAME);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.EMAIL1, ContactsColumns.EMAIL1);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.PHONE_MOBILE, ContactsColumns.PHONE_MOBILE);
        contactsApiNamesVsModuleFieldNames.put(ModuleFields.PHONE_WORK, ContactsColumns.PHONE_WORK);
    }

    public static String getModuleFieldNameForContactsField(String contactsFieldName) {
        String moduleFieldName;
        moduleFieldName = contactsApiNamesVsModuleFieldNames.get(contactsFieldName);
        return moduleFieldName;
    }
}
