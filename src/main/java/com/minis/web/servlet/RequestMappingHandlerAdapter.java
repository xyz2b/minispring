package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.WebApplicationContext;
import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;
import com.minis.web.WebDataBinderFactory;
import javafx.util.Pair;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    WebApplicationContext wac;
    WebBindingInitializer webBindingInitializer;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) {
        this.wac = wac;
        try {
            this.webBindingInitializer = (WebBindingInitializer) wac.getBean("webBindingInitializer");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        handlerInternal(request, response, (HandlerMethod) handler);
    }

    private void handlerInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        try {
            invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected void invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        WebDataBinderFactory binderFactory = new WebDataBinderFactory();
        Parameter[] methodParameters = handler.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];
        int i = 0;
        // 对调用方法里的每一个参数，处理绑定
        for(Parameter methodParameter : methodParameters) {
            Object methodParamObj = methodParameter.getType().newInstance();
            // 给这个参数创建 WebDataBinder
            WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
            webBindingInitializer.initBinder(wdb);
            wdb.bind(request);
            methodParamObjs[i] = methodParamObj;
            i++;
        }
        Method invocableMethod = handler.getMethod();
        Object returnObj = invocableMethod.invoke(handler.getBean(), methodParamObjs);
        response.getWriter().write(returnObj.toString());
    }

    public WebBindingInitializer getWebBindingInitializer() {
        return webBindingInitializer;
    }

    public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }
}
