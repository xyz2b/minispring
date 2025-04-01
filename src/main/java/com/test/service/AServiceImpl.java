package com.test.service;

import com.minis.beans.factory.annotation.Autowired;

public class AServiceImpl implements AService {
    private String property1;
    private String property2;

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public String getProperty1() {
        return property1;
    }

    public String getProperty2() {
        return property2;
    }

    private String name;
    private int level;
    private ConstructorArgService constructorArgService;

    public AServiceImpl() {
    }

    public AServiceImpl(String name, int level, ConstructorArgService constructorArgService) {
        this.name = name;
        this.level = level;
        this.constructorArgService = constructorArgService;
        System.out.println(this.name + "," + this.level);
        constructorArgService.sayHello();
    }

    @Override
    public void sayHello() {
        System.out.println(this.property1 + "," + this.property2);
        ref1.sayHello();
    }

    private BaseService ref1;

    public void setRef1(BaseService ref1) {
        this.ref1 = ref1;
    }

    public BaseService getRef1() {
        return this.ref1;
    }
}
