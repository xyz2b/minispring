package com.minis.beans.factory.config;

import com.minis.beans.PropertyValues;

import java.util.Objects;

public class BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    private boolean lazyInit = true;
    private String[] dependsOn;
    // 构造器注入
    private ConstructorArgumentValues constructorArgumentValues;
    // Setter 注入
    private PropertyValues propertyValues;
    private String initMethodName;
    private volatile Object beanClass;
    private String id;
    private String className;
    private String scope = SCOPE_SINGLETON;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws ClassNotFoundException {
        if (hasBeanClass()) {
            return (Class<?>) this.beanClass;
        } else {
            this.beanClass = Class.forName(this.className);
            return (Class<?>) this.beanClass;
        }
    }

    public String getId() {
        return id;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getInitMethodName() {
        return initMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public boolean isSingleton() {
        return scope.equals(SCOPE_SINGLETON);
    }

    public boolean isPrototype() {
        return Objects.equals(scope, SCOPE_PROTOTYPE);
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues = constructorArgumentValues;
    }

    public ConstructorArgumentValues getConstructorArgumentValues() {
        return constructorArgumentValues;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String[] getDependsOn() {
        return this.dependsOn;
    }
}
