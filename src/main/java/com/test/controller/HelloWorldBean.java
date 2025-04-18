package com.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;
import com.test.entity.TestProperty;
import com.test.entity.TestProperty2;
import com.test.entity.User;
import com.test.service.BaseService;
import com.test.service.UserService;

import java.util.Date;

public class HelloWorldBean {
    @Autowired
    BaseService baseservice;

    @Autowired
    UserService userService;

    @RequestMapping(url = "/test")
    public String doTest() {
        return "test";
    }

    @RequestMapping(url = "/test2")
    @ResponseBody
    public String doTest2() {
        return "test 2, hello world!";
    }

    @RequestMapping(url = "/test3")
    public String doTest3() {
        return baseservice.getHello();
    }

    @RequestMapping(url = "/date")
    public String date(TestProperty testProperty, TestProperty2 testProperty2) {
        return testProperty.getI() + ", " + testProperty.getDate().toString() + "," + testProperty2.getC() + "," + testProperty2.getDate2();
    }

    @RequestMapping(url = "/test7")
    @ResponseBody
    public User doTest7(User user) {
        user.setName(user.getName() + "---");
        user.setBirthday(new Date());
        return user;
    }

    @RequestMapping(url = "/test8")
    @ResponseBody
    public User doTest8(User user) {
        return userService.getUserInfo(user.getId());
    }
}
