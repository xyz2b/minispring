package com.minis.web.method.annotation;

import com.minis.beans.BeansException;
import com.minis.http.converter.HttpMessageConverter;
import com.minis.web.bind.WebDataBinder;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.bind.support.WebDataBinderFactory;
import com.minis.web.context.WebApplicationContext;
import com.minis.web.method.HandlerMethod;
import com.minis.web.servlet.HandlerAdapter;
import com.minis.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    private WebApplicationContext wac;
    private WebBindingInitializer webBindingInitializer;
    private HttpMessageConverter messageConverter;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) {
        this.wac = wac;
        try {
            this.webBindingInitializer = (WebBindingInitializer) wac.getBean("webBindingInitializer");
            this.messageConverter = (HttpMessageConverter) wac.getBean("messageConverter");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return handlerInternal(request, response, (HandlerMethod) handler);
    }

    private ModelAndView handlerInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        try {
            return invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {
        WebDataBinderFactory binderFactory = new WebDataBinderFactory();
        Parameter[] methodParameters = handler.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];
        int i = 0;
        // 对调用方法里的每一个参数，处理绑定
        for(Parameter methodParameter : methodParameters) {
            if(methodParameter.getType() != HttpServletRequest.class && methodParameter.getType() != HttpServletResponse.class) {
                Object methodParamObj = methodParameter.getType().newInstance();
                // 给这个参数创建 WebDataBinder
                WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
                webBindingInitializer.initBinder(wdb);
                wdb.bind(request);
                methodParamObjs[i] = methodParamObj;
            } else if (methodParameter.getType() == HttpServletRequest.class) {
                methodParamObjs[i] = request;
            } else if (methodParameter.getType() == HttpServletResponse.class) {
                methodParamObjs[i] = response;
            }

            i++;
        }

        Method invocableMethod = handler.getMethod();
        Object returnObj = invocableMethod.invoke(handler.getBean(), methodParamObjs);
        Class<?> returnType = invocableMethod.getReturnType();

        ModelAndView mav = null;
        // 如果是 ResponseBody 注解，仅仅返回值，则转换数据格式后直接写到 response
        if(invocableMethod.isAnnotationPresent(ResponseBody.class)) {
            this.messageConverter.write(returnObj, response);
        } else if(returnType == void.class) {

        } else {    // 返回的是前端页面
            if(returnObj instanceof ModelAndView) {
                mav = (ModelAndView) returnObj;
            } else if (returnObj instanceof String) { // 字符串也认为是前端页面
                String sTarget = (String) returnObj;
                mav = new ModelAndView();
                mav.setViewName(sTarget);
            }
        }

        return mav;
    }

    public WebBindingInitializer getWebBindingInitializer() {
        return webBindingInitializer;
    }

    public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }
}
