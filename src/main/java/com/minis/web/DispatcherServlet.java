package com.minis.web;

import com.minis.beans.BeansException;
import com.minis.web.servlet.HandlerAdapter;
import com.minis.web.servlet.HandlerMethod;
import com.minis.web.servlet.RequestMappingHandlerAdapter;
import com.minis.web.servlet.RequestMappingHandlerMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispatcherServlet extends HttpServlet {
    String WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName();

//    private Map<String, MappingValue> mappingValues;
//    private Map<String, Class<?>> mappingClz = new HashMap<>();

    // 用于存储需要扫描的 package 列表
    private List<String> packageNames = new ArrayList<>();
    // 用于存储 controller 的名称与对象的映射关系
    private Map<String, Object> controllerObjs = new HashMap<>();
    // 用于存储 controller 名称数组列表
    private List<String> controllerNames = new ArrayList<>();
    // 用于存储 controller 名称与类的映射关系
    private Map<String, Class<?>> controllerClasses = new HashMap<>();
    // 是保存自定义的 @RequestMapping 名称（URL的名称）的列表
    private List<String> urlMappingNames = new ArrayList<>();
    // 保存 URL 名称与对象的映射关系
    private Map<String, Object> mappingObjs = new HashMap<>();
    // 保存 URL 名称与方法的映射关系
    private Map<String, Method> mappingMethods = new HashMap<>();
    // WEB 容器的 context
    private WebApplicationContext webApplicationContext;
    // 原始 IOC 容器的 context
    private WebApplicationContext parentApplicationContext;
    private RequestMappingHandlerMapping handlerMapping;
    private RequestMappingHandlerAdapter handlerAdapter;

    public DispatcherServlet() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.parentApplicationContext = (WebApplicationContext) this.getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        String sContextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;

        try {
            xmlPath = this.getServletContext().getResource(sContextConfigLocation);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(sContextConfigLocation, this.parentApplicationContext);
        refresh();
    }

    // 对所有的mappingValues中注册的类进行实例化，默认构造函数
    protected void refresh() {
        // 初始化 controller
        initController();
        // 初始化 url 映射
        initHandlerMappings(this.webApplicationContext);
        initHandlerAdapters(this.webApplicationContext);
    }

    protected void initHandlerMappings(WebApplicationContext wac) {
        this.handlerMapping = new RequestMappingHandlerMapping(wac);
    }
    protected void initHandlerAdapters(WebApplicationContext wac) {
        this.handlerAdapter = new RequestMappingHandlerAdapter(wac);
    }

    // 对扫描到的每一个类进行加载和实例化，与类的名字建立映射关系，分别存在 controllerClasses 和 controllerObjs 这两个 map 里，类名就是 key 的值。
    private void initController() {
        this.controllerNames = Arrays.asList(this.webApplicationContext.getBeanDefinitionNames());
        for(String controllerName : controllerNames) {
            try {
                this.controllerClasses.put(controllerName, Class.forName(controllerName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                this.controllerObjs.put(controllerName, this.webApplicationContext.getBean(controllerName));
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.webApplicationContext);
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerMethod handlerMethod = null;
        handlerMethod = this.handlerMapping.getHandler(processedRequest);
        if(handlerMethod == null) {
            return;
        }
        HandlerAdapter ha = this.handlerAdapter;
        ha.handle(processedRequest, response, handlerMethod);
    }
}
