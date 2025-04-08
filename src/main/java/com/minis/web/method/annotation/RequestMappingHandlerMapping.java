package com.minis.web.method.annotation;

import com.minis.beans.BeansException;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.context.WebApplicationContext;
import com.minis.web.method.HandlerMethod;
import com.minis.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class RequestMappingHandlerMapping implements HandlerMapping {
    WebApplicationContext wac;
    private MappingRegistry mappingRegistry;

    public RequestMappingHandlerMapping(WebApplicationContext wac) {
        this.wac = wac;

//        initMapping();
    }

    public RequestMappingHandlerMapping(WebApplicationContext wac, MappingRegistry mappingRegistry) {}

    // 初始化 URL 映射，找到使用了注解 @RequestMapping 的方法，URL 存放到 urlMappingNames 里，
    // 映射的对象存放到 mappingObjs 里，映射的方法存放到 mappingMethods 里。用这个方法取代了过去解析 Bean 得到的映射。
    private void initMapping() {
        Class<?> clz = null;
        Object obj = null;
        String[] controllerNames = this.wac.getBeanDefinitionNames();
        for(String controllerName : controllerNames) {
            try {
                clz = Class.forName(controllerName);
                obj = this.wac.getBean(controllerName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }

            Method[] methods = clz.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    // 检查所有的方法
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    if (isRequestMapping) { // 有 RequestMapping 注解
                        String methodName = method.getName();
                        // 建立方法名和URL的映射
                        String urlMapping = method.getAnnotation(RequestMapping.class).url();
                        // TODO: 支持 post 方式
                        String requestMethod = method.getAnnotation(RequestMapping.class).method();
                        this.mappingRegistry.getUrlMappingNames().add(urlMapping);
                        this.mappingRegistry.getMappingObjs().put(urlMapping, obj);
                        this.mappingRegistry.getMappingMethods().put(urlMapping, method);
                        this.mappingRegistry.getMappingMethodNames().put(urlMapping, methodName);
                        this.mappingRegistry.getMappingClasses().put(urlMapping, clz);
                    }
                }
            }
        }
    }

    @Override
    public HandlerMethod getHandler(HttpServletRequest request) throws Exception {
        if(this.mappingRegistry == null) { // to do initialization
            this.mappingRegistry = new MappingRegistry();
            initMapping();
        }

        String sPath = request.getServletPath();
        if(!this.mappingRegistry.getUrlMappingNames().contains(sPath)) {
            return null;
        }

        Method method = this.mappingRegistry.getMappingMethods().get(sPath);
        Object obj = this.mappingRegistry.getMappingObjs().get(sPath);
        Class<?> clz = this.mappingRegistry.getMappingClasses().get(sPath);
        String methodName = this.mappingRegistry.getMappingMethodNames().get(sPath);
        HandlerMethod handlerMethod = new HandlerMethod(method, obj, clz, methodName);
        return handlerMethod;
    }
}
