package com.imaginea.android.sugarcrm.restapi;

import java.util.List;

public class ModuleFieldsSortOrderSQLGenerationTest extends RestAPITest {

    private final String TAG = ModuleFieldsSortOrderSQLGenerationTest.class.getSimpleName();

    private List<String> billingAddressGroup;

    private List<String> shippingAddressGroup;

    private List<String> durationGroup;

    private final String sqlFile = "sortOrderAndGroup.sql";

    /*
     * generates sortOrderAndGroup.sql file in the /data/data/com.imaginea.android.sugarcrm/files/
     */
    // @SmallTest
    // public void testGeneratingSQLForModuleFieldsOrder() throws Exception {
    // DatabaseHelper dbHelper = new DatabaseHelper(getContext());
    //
    // // get the modules that are displayed in dashboard
    // List<String> moduleNames = dbHelper.getModuleList();
    //
    // // the grouping are retrieved from dbHelper
    // billingAddressGroup = dbHelper.getBillingAddressGroup();
    // shippingAddressGroup = dbHelper.getShippingAddressGroup();
    // durationGroup = dbHelper.getDurationGroup();
    //
    // SQLiteDatabase db = dbHelper.getWritableDatabase();
    //
    // int updatedGroupId = 0;
    //
    // /*
    // * use the openFileOutput() method the ActivityContext provides to protect the file from
    // * others and this is done for security-reasons. We chose MODE_WORLD_READABLE, because we
    // * have nothing to hide in our file
    // */
    // FileOutputStream fOut = getContext().openFileOutput(sqlFile, Context.MODE_WORLD_READABLE);
    // OutputStreamWriter out = new OutputStreamWriter(fOut);
    //
    // for (String moduleName : moduleNames) {
    // String[] moduleProjections = dbHelper.getModuleProjections(moduleName);
    //
    // String selection = ModuleColumns.MODULE_NAME + "='" + moduleName + "'";
    // // using the DETAILS_PROJECTION here to select the columns
    // Cursor cursor = db.query(DatabaseHelper.MODULES_TABLE_NAME,
    // com.imaginea.android.sugarcrm.provider.SugarCRMContent.Modules.DETAILS_PROJECTION, selection,
    // null, null, null, null);
    // cursor.moveToFirst();
    // int moduleId = cursor.getInt(0);
    // cursor.close();
    //
    // for (int i = 2; i < moduleProjections.length; i++) {
    // String fieldName = moduleProjections[i];
    // int groupId = 0;
    //
    // Log.i(TAG, "fieldName - " + fieldName);
    // selection = "(" + ModuleFieldColumns.NAME + "='" + fieldName + "'" + " AND "
    // + ModuleFieldColumns.MODULE_ID + "=" + moduleId
    // + ")";
    // // using the DETAILS_PROJECTION here to select the columns
    // cursor = db.query(DatabaseHelper.MODULE_FIELDS_TABLE_NAME,
    // com.imaginea.android.sugarcrm.provider.SugarCRMContent.ModuleField.DETAILS_PROJECTION,
    // selection, null, null, null, null);
    // cursor.moveToFirst();
    // int moduleFieldId = cursor.getInt(0);
    // cursor.close();
    //
    // if (billingAddressGroup.contains(fieldName)) {
    // if (fieldName.equals(ModuleFields.BILLING_ADDRESS_STREET)) {
    // updatedGroupId++;
    // String sql = "INSERT INTO " + DatabaseHelper.MODULE_FIELDS_GROUP_TABLE_NAME
    // + "(" + ModuleFieldGroupColumns.GROUP_ID
    // + ", " + ModuleFieldGroupColumns.TITLE
    // + ")" + " VALUES (" + updatedGroupId + ", "
    // + "'Billing Address'" + ");";
    // out.write(sql + "\n");
    // }
    // groupId = updatedGroupId;
    // } else if (shippingAddressGroup.contains(fieldName)) {
    // if (fieldName.equals(ModuleFields.SHIPPING_ADDRESS_STREET)) {
    // updatedGroupId++;
    // String sql = "INSERT INTO " + DatabaseHelper.MODULE_FIELDS_GROUP_TABLE_NAME
    // + "(" + ModuleFieldGroupColumns.GROUP_ID
    // + ", " + ModuleFieldGroupColumns.TITLE
    // + ")" + " VALUES (" + updatedGroupId + ", "
    // + "'Shipping Address'" + ");";
    // out.write(sql + "\n");
    // }
    // groupId = updatedGroupId;
    // } else if (durationGroup.contains(fieldName)) {
    // if (fieldName.equals(ModuleFields.DURATION_HOURS)) {
    // updatedGroupId++;
    // String sql = "INSERT INTO " + DatabaseHelper.MODULE_FIELDS_GROUP_TABLE_NAME
    // + "(" + ModuleFieldGroupColumns.GROUP_ID
    // + ", " + ModuleFieldGroupColumns.TITLE
    // + ")" + " VALUES (" + updatedGroupId + ", "
    // + "'Duration'" + ");";
    // out.write(sql + "\n");
    // }
    // groupId = updatedGroupId;
    // }
    //
    // Log.i(TAG, moduleName + " " + fieldName + " : itemId - " + i + " groupId - "
    // + groupId + "");
    // String sql = "INSERT INTO " + DatabaseHelper.MODULE_FIELDS_SORT_ORDER_TABLE_NAME
    // + "(" + ModuleFieldSortOrderColumns.FIELD_SORT_ID
    // + "," + ModuleFieldSortOrderColumns.GROUP_ID + ","
    // + ModuleFieldSortOrderColumns.MODULE_FIELD_ID + ","
    // + ModuleFieldSortOrderColumns.MODULE_ID + ")"
    // + " VALUES (" + i + ", " + groupId + ", "
    // + moduleFieldId + ", " + moduleId + ");";
    // out.write(sql + "\n");
    // }
    // }
    //
    // out.flush();
    // out.close();
    // db.close();
    // }
    //
    // @SmallTest
    // public void testInsertFromSQLFile() throws Exception {
    // DatabaseHelper dbHelper = new DatabaseHelper(getContext());
    // SQLiteDatabase db = dbHelper.getWritableDatabase();
    // db.beginTransaction();
    //
    // /*
    // * Use the openFileInput() method the ActivityContext provides. Again for security reasons
    // * with openFileInput(...)
    // */
    // FileInputStream fis = getContext().openFileInput(sqlFile);
    // BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    // String sql;
    // while ((sql = br.readLine()) != null) {
    // db.execSQL(sql);
    // Log.v(TAG, "read from file: " + sql);
    // }
    // db.setTransactionSuccessful();
    // db.endTransaction();
    // db.close();
    // }
}
