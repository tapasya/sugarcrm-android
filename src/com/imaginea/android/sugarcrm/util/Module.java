package com.imaginea.android.sugarcrm.util;

import java.util.List;

public class Module {

    private String mModuleName;

    private List<ModuleField> mModuleFields;

    private List<LinkField> mLinkFields;

    public Module() {

    }

    public Module(String moduleName, List<ModuleField> moduleFields, List<LinkField> linkFields) {
        super();
        this.mModuleName = moduleName;
        this.mModuleFields = moduleFields;
        this.mLinkFields = linkFields;
    }

    public List<ModuleField> getModuleFields() {
        return mModuleFields;
    }

    public void setModuleFields(List<ModuleField> moduleFields) {
        this.mModuleFields = moduleFields;
    }

    public List<LinkField> getLinkFields() {
        return mLinkFields;
    }

    public void setLinkFields(List<LinkField> linkFields) {
        this.mLinkFields = linkFields;
    }

    public String getModuleName() {
        return mModuleName;
    }

}
