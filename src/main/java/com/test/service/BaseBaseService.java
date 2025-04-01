package com.test.service;

import com.minis.beans.factory.annotation.Autowired;

public class BaseBaseService {
    private AServiceImpl as;

    public AServiceImpl getAs() {
        return as;
    }

    public void setAs(AServiceImpl as) {
        this.as = as;
    }

    public void sayHello() {
        System.out.println("Base Base Service says hello");
    }
}
