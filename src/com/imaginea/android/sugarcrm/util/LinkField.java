package com.imaginea.android.sugarcrm.util;

public class LinkField {
    
    private String mName;
    
    private String mType;
    
    private String mRelationship;
    
    private String mModule;
    
    private String mBeanName;

    public LinkField(String name, String type, String relationship, String module,
                                    String beanName) {
        super();
        this.mName = name;
        this.mType = type;
        this.mRelationship = relationship;
        this.mModule = module;
        this.mBeanName = beanName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getRelationship() {
        return mRelationship;
    }

    public void setRelationship(String relationship) {
        this.mRelationship = relationship;
    }

    public String getModule() {
        return mModule;
    }

    public void setModule(String module) {
        this.mModule = module;
    }

    public String getBeanName() {
        return mBeanName;
    }

    public void setmBeanName(String mBeanName) {
        this.mBeanName = mBeanName;
    }
    
}
