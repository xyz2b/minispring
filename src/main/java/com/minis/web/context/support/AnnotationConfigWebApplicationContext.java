package com.minis.web.context.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.context.*;
import com.minis.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// 该类其实质就是我们 IoC 容器中的 ClassPathXmlApplicationContext，只是在此基础上增加了 servletContext 的属性，这样就成了一个适用于 Web 场景的上下文。
public class AnnotationConfigWebApplicationContext extends AbstractApplicationContext implements WebApplicationContext {
    // 原始 IOC 容器的 context
    private WebApplicationContext parentApplicationContext;
    private ServletContext servletContext;
    // 管理 WEB 容器的 Bean
    DefaultListableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();


    public AnnotationConfigWebApplicationContext(String fileName) {
        this(fileName, null);
    }

    public AnnotationConfigWebApplicationContext(String fileName, WebApplicationContext parentApplicationContext) {
        this.parentApplicationContext = parentApplicationContext;
        this.servletContext = this.parentApplicationContext.getServletContext();
        URL xmlPath = null;
        try {
            xmlPath = this.getServletContext().getResource(fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        List<String> packageNames = XmlScanComponentHelper.getNodeValue(xmlPath);
        List<String> controllerNames = scanPackages(packageNames);
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        this.beanFactory = bf;
        // 设置 管理原始 IOC 容器的 factory 为 管理 WEB 容器的 factory 的父工厂，获取 Bean 时，先从 管理 WEB 容器的 factory 中获取，再从 管理原始 IOC 容器的 factory 中获取
        this.beanFactory.setParent(this.parentApplicationContext.getBeanFactory());
        loadBeanDefinitions(controllerNames);

        if(true) {
            try {
                refresh();
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadBeanDefinitions(List<String> controllerNames) {
        for(String controller : controllerNames) {
            String beanID = controller;
            String beanClassName = controller;
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);
            this.beanFactory.registerBeanDefinition(beanID, beanDefinition);
        }
    }

    private List<String> scanPackages(List<String> packages) {
        List<String> tempControllerNames = new ArrayList<>();
        for(String packageName : packages) {
            tempControllerNames.addAll(scanPackage(packageName));
        }
        return tempControllerNames;
    }

    private List<String> scanPackage(String packageName) {
        List<String> tempControllerNames = new ArrayList<>();
        URI uri = null;
        // 将以 . 分隔的包名转换为以 / 分隔的 uri
        try {
            uri = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/")).toURI();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File dir = new File(uri);
        // 处理对应的文件目录
        for(File file : dir.listFiles()) { // 目录下面的文件或者子目录
            if(file.isDirectory()) { // 对子目录进行递归扫描
                tempControllerNames.addAll(scanPackage(packageName + "." + file.getName()));
            } else { // 类文件
                String controllerName = packageName + "." + file.getName().replace(".class", "");
                tempControllerNames.add(controllerName);
            }
        }
        return tempControllerNames;
    }

    public void setParent(WebApplicationContext parentApplicationContext) {
        this.parentApplicationContext = parentApplicationContext;
        this.beanFactory.setParent(this.parentApplicationContext.getBeanFactory());
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return beanFactory;
    }

    @Override
    protected void registerListeners() {
        ApplicationListener listener = new ApplicationListener();
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    protected void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    @Override
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
    }

    @Override
    protected void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    protected void finishRefresh() {

    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }

    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }
}
