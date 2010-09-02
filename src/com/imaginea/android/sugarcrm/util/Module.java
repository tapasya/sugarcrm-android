package com.imaginea.android.sugarcrm.util;

import java.util.List;

/**
 * <p>
 * Module class.
 * </p>
 * 
 */
public class Module {

    private String mModuleName;

    private List<ModuleField> mModuleFields;

    private List<LinkField> mLinkFields;

    /**
     * <p>
     * Constructor for Module.
     * </p>
     */
    public Module() {

    }

    /**
     * <p>
     * Constructor for Module.
     * </p>
     * 
     * @param moduleName
     *            a {@link java.lang.String} object.
     * @param moduleFields
     *            a {@link java.util.List} object.
     * @param linkFields
     *            a {@link java.util.List} object.
     */
    public Module(String moduleName, List<ModuleField> moduleFields, List<LinkField> linkFields) {
        super();
        this.mModuleName = moduleName;
        this.mModuleFields = moduleFields;
        this.mLinkFields = linkFields;
    }

    /**
     * <p>
     * getModuleFields
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<ModuleField> getModuleFields() {
        return mModuleFields;
    }

    /**
     * <p>
     * setModuleFields
     * </p>
     * 
     * @param moduleFields
     *            a {@link java.util.List} object.
     */
    public void setModuleFields(List<ModuleField> moduleFields) {
        this.mModuleFields = moduleFields;
    }

    /**
     * <p>
     * getLinkFields
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<LinkField> getLinkFields() {
        return mLinkFields;
    }

    /**
     * <p>
     * setLinkFields
     * </p>
     * 
     * @param linkFields
     *            a {@link java.util.List} object.
     */
    public void setLinkFields(List<LinkField> linkFields) {
        this.mLinkFields = linkFields;
    }

    /**
     * <p>
     * getModuleName
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getModuleName() {
        return mModuleName;
    }

}
