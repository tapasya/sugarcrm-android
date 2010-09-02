package com.imaginea.android.sugarcrm.util;

/**
 * <p>
 * LinkField class.
 * </p>
 * 
 */
public class LinkField {

    private String mName;

    private String mType;

    private String mRelationship;

    private String mModule;

    private String mBeanName;

    /**
     * <p>
     * Constructor for LinkField.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @param type
     *            a {@link java.lang.String} object.
     * @param relationship
     *            a {@link java.lang.String} object.
     * @param module
     *            a {@link java.lang.String} object.
     * @param beanName
     *            a {@link java.lang.String} object.
     */
    public LinkField(String name, String type, String relationship, String module, String beanName) {
        super();
        this.mName = name;
        this.mType = type;
        this.mRelationship = relationship;
        this.mModule = module;
        this.mBeanName = beanName;
    }

    /**
     * <p>
     * getName
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return mName;
    }

    /**
     * <p>
     * setName
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * <p>
     * getType
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return mType;
    }

    /**
     * <p>
     * setType
     * </p>
     * 
     * @param type
     *            a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.mType = type;
    }

    /**
     * <p>
     * getRelationship
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getRelationship() {
        return mRelationship;
    }

    /**
     * <p>
     * setRelationship
     * </p>
     * 
     * @param relationship
     *            a {@link java.lang.String} object.
     */
    public void setRelationship(String relationship) {
        this.mRelationship = relationship;
    }

    /**
     * <p>
     * getModule
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getModule() {
        return mModule;
    }

    /**
     * <p>
     * setModule
     * </p>
     * 
     * @param module
     *            a {@link java.lang.String} object.
     */
    public void setModule(String module) {
        this.mModule = module;
    }

    /**
     * <p>
     * getBeanName
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getBeanName() {
        return mBeanName;
    }

    /**
     * <p>
     * Setter for the field <code>mBeanName</code>.
     * </p>
     * 
     * @param mBeanName
     *            a {@link java.lang.String} object.
     */
    public void setmBeanName(String mBeanName) {
        this.mBeanName = mBeanName;
    }

}
