package com.minis.web.servlet;

import com.sun.org.apache.bcel.internal.classfile.MethodParameter;

import java.lang.reflect.Method;

public class HandlerMethod {
    private Object bean;
    private Class<?> beanType;
    private Method method;
    private MethodParameter[] parameters;
    private Class<?> returnType;
    private String description;
    private String className;
    private String methodName;

    public HandlerMethod(Method method, Object obj) {
        this.method = method;
        this.bean = obj;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }
}
