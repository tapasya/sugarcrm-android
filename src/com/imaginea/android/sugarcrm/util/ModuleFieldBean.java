package com.imaginea.android.sugarcrm.util;

/**
 * <p>
 * ModuleFieldBean class.
 * </p>
 * 
 */
public class ModuleFieldBean {

    private ModuleField mModuleField;

    private int mModuleFieldId;

    private int mFieldSortId;

    private int mGroupId;

    /**
     * <p>
     * Constructor for ModuleFieldBean.
     * </p>
     * 
     * @param moduleField
     *            a {@link com.imaginea.android.sugarcrm.util.ModuleField} object.
     * @param moduleFieldId
     *            a int.
     * @param fieldSortId
     *            a int.
     * @param groupId
     *            a int.
     */
    public ModuleFieldBean(ModuleField moduleField, int moduleFieldId, int fieldSortId, int groupId) {
        this.mModuleField = moduleField;
        this.mModuleFieldId = moduleFieldId;
        this.mFieldSortId = fieldSortId;
        this.mGroupId = groupId;
    }

    /**
     * <p>
     * getModuleField
     * </p>
     * 
     * @return a {@link com.imaginea.android.sugarcrm.util.ModuleField} object.
     */
    public ModuleField getModuleField() {
        return mModuleField;
    }

    /**
     * <p>
     * setModuleField
     * </p>
     * 
     * @param moduleField
     *            a {@link com.imaginea.android.sugarcrm.util.ModuleField} object.
     */
    public void setModuleField(ModuleField moduleField) {
        this.mModuleField = moduleField;
    }

    /**
     * <p>
     * getModuleFieldId
     * </p>
     * 
     * @return a int.
     */
    public int getModuleFieldId() {
        return mModuleFieldId;
    }

    /**
     * <p>
     * setModuleFieldId
     * </p>
     * 
     * @param moduleFieldId
     *            a int.
     */
    public void setModuleFieldId(int moduleFieldId) {
        this.mModuleFieldId = moduleFieldId;
    }

    /**
     * <p>
     * getFieldSortId
     * </p>
     * 
     * @return a int.
     */
    public int getFieldSortId() {
        return mFieldSortId;
    }

    /**
     * <p>
     * setFieldSortId
     * </p>
     * 
     * @param fieldSortId
     *            a int.
     */
    public void setFieldSortId(int fieldSortId) {
        this.mFieldSortId = fieldSortId;
    }

    /**
     * <p>
     * getGroupId
     * </p>
     * 
     * @return a int.
     */
    public int getGroupId() {
        return mGroupId;
    }

    /**
     * <p>
     * setGroupId
     * </p>
     * 
     * @param groupId
     *            a int.
     */
    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

}
