package com.minis.beans;

public abstract class AbstractPropertyAccessor extends PropertyEditorRegistrySupport{

    PropertyValues pvs;

    public AbstractPropertyAccessor() {
        super();

    }

    // 绑定参数值
    public void setPropertyValues(PropertyValues pvs) {
        this.pvs = pvs;
        for (PropertyValue pv : this.pvs.getPropertyValues()) {
            try {
                setPropertyValue(pv);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void setPropertyValue(PropertyValue pv) ;

}