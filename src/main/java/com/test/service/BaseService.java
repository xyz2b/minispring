package com.test.service;

import com.minis.beans.factory.annotation.Autowired;
import com.test.service.BaseBaseService;

public class BaseService {
    private BaseBaseService bbs;

    public BaseBaseService getBbs() {
        return this.bbs;
    }

    public void setBbs(BaseBaseService bbs) {
        this.bbs = bbs;
    }

    public void sayHello() {
        System.out.println("Base Service says hello");
        bbs.sayHello();
    }

    public String getHello() {
        return "Base Service get Hello.";
    }
}
