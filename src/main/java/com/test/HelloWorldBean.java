package com.test;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.RequestMapping;
import com.test.service.BaseService;

import java.util.Date;

public class HelloWorldBean {
    @Autowired
    BaseService baseservice;
//    public String doGet() {
//        return "hello world!";
//    }
//    public String doPost() {
//        return "hello world!";
//    }

    @RequestMapping("/test")
    public String doTest() {
        return "hello world for doGet!";
    }

    @RequestMapping("/test2")
    public String doTest2() {
        return "test 2, hello world!";
    }

    @RequestMapping("/test3")
    public String doTest3() {
        return baseservice.getHello();
    }

    @RequestMapping("/date")
    public String date(TestProperty testProperty, TestProperty2 testProperty2) {
        return testProperty.getI() + ", " + testProperty.getDate().toString() + "," + testProperty2.getC() + "," + testProperty2.getDate2();
    }
}
