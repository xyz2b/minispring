package com.test.service;

import com.minis.context.ClassPathXmlApplicationContext;
import com.minis.beans.BeansException;
import com.test.service.AService;

public class Test1 {
    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = (AService) ctx.getBean("aservice");
        aService.sayHello();
//        Test1 test1 = new Test1();
//        test1.test();
    }

//    public void test() {
//        System.out.println(this.getClass().getResource("/com/minis/test"));
//    }
}
