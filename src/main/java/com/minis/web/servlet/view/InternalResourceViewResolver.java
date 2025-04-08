package com.minis.web.servlet.view;

import com.minis.web.servlet.View;
import com.minis.web.servlet.ViewResolver;

public class InternalResourceViewResolver implements ViewResolver {
    private Class<?> viewClass = null;
    private String viewClassName = "";
    private String prefix = "";
    private String suffix = "";
    private String contentType;

    public InternalResourceViewResolver() {
        if(getViewClass() == null) {
            setViewClass(JstlView.class);
        }
    }

    public void setViewClassName(String viewClassName) {
        this.viewClassName = viewClassName;
        Class<?> clz = null;
        try {
            clz = Class.forName(viewClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        setViewClass(clz);
    }

    public String getViewClassName() {
        return viewClassName;
    }

    public Class<?> getViewClass() {
        return viewClass;
    }

    public void setViewClass(Class<?> viewClass) {
        this.viewClass = viewClass;
    }

    public void setPrefix(String prefix) {
        this.prefix = (prefix != null) ? prefix : "";
    }

    public String getPrefix() {
        return prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = (suffix != null) ? suffix : "";
    }

    public String getSuffix() {
        return suffix;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public View resolveViewName(String viewName) throws Exception {
        return buildView(viewName);
    }

    protected View buildView(String viewName) throws Exception {
        Class<?> viewClass = getViewClass();

        View view = (View) viewClass.newInstance();
        view.setUrl(getPrefix() + viewName + getSuffix());
        String contentType = getContentType();
        view.setContentType(contentType);

        return view;
    }
}
