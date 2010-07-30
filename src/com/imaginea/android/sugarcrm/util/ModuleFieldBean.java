package com.imaginea.android.sugarcrm.util;

public class ModuleFieldBean {

    private ModuleField mModuleField;

    private int mModuleFieldId;

    private int mFieldSortId;

    private int mGroupId;

    public ModuleFieldBean(ModuleField moduleField, int moduleFieldId, int fieldSortId, int groupId) {
        this.mModuleField = moduleField;
        this.mModuleFieldId = moduleFieldId;
        this.mFieldSortId = fieldSortId;
        this.mGroupId = groupId;
    }

    public ModuleField getModuleField() {
        return mModuleField;
    }

    public void setModuleField(ModuleField moduleField) {
        this.mModuleField = moduleField;
    }

    public int getModuleFieldId() {
        return mModuleFieldId;
    }

    public void setModuleFieldId(int moduleFieldId) {
        this.mModuleFieldId = moduleFieldId;
    }

    public int getFieldSortId() {
        return mFieldSortId;
    }

    public void setFieldSortId(int fieldSortId) {
        this.mFieldSortId = fieldSortId;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
    }

}
