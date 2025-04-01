package com.minis.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// 把容器的启动放到 Web 应用的 Listener 中。Spring MVC 就是这么设计的，它按照这个规范，用 ContextLoaderListener 来启动容器。
public class ContextLoaderListener implements ServletContextListener {
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    private WebApplicationContext context;

    public ContextLoaderListener() {

    }

    public ContextLoaderListener(WebApplicationContext context) {
        this.context = context;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }

    private void initWebApplicationContext(ServletContext servletContext) {
        // 通过配置文件参数从 web.xml 中得到配置文件路径，如 applicationContext.xml
        String sContextLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        // 然后用这个配置文件创建了 XmlWebApplicationContext 这一对象，我们叫 WAC，这就成了新的上下文。
        // 原始 IOC 容器的 context
        WebApplicationContext wac = new XmlWebApplicationContext(sContextLocation);
        // XmlWebApplicationContext 引用 servletContext
        wac.setServletContext(servletContext);
        this.context = wac;
        // 然后调用 servletContext.setAttribute() 方法，按照默认的属性值将 WAC 设置到 servletContext 里。
        // 这样 XmlWebApplicationContext 和 servletContext 就能够互相引用了
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }
}
