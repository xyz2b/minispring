package com.minis.web.bind;

import com.minis.beans.AbstractPropertyAccessor;
import com.minis.beans.PropertyValues;
import com.minis.util.WebUtils;
import com.minis.beans.BeanWrapperImpl;
import com.minis.beans.PropertyEditor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebDataBinder {
    private Object target;
    private Class<?> clz;
    private String ObjectName;

    AbstractPropertyAccessor propertyAccessor;

    public WebDataBinder(Object target) {
        this(target, "");
    }

    public WebDataBinder(Object target, String targetName) {
        this.target = target;
        this.ObjectName = targetName;
        this.clz = this.target.getClass();
        this.propertyAccessor = new BeanWrapperImpl(this.target);
    }

    // 核心绑定方法，将 request 里面的参数值绑定到目标对象的属性上
    public void bind(HttpServletRequest request) {
        // 拿到request中所有参数
        PropertyValues pvs = assignParameters(request);
        addBindValues(pvs, request);
        // 将request中的参数绑定到参数对象的属性中
        doBind(pvs);
    }

    private void doBind(PropertyValues pvs) {
        applyPropertyValues(pvs);
    }

    // 实际将参数值与对象属性进行绑定的方法
    protected void applyPropertyValues(PropertyValues pvs) {
        getPropertyAccessor().setPropertyValues(pvs);
    }

    // 设置属性值的工具
    protected AbstractPropertyAccessor getPropertyAccessor() {
        return this.propertyAccessor;
    }

    private void addBindValues(PropertyValues pvs, HttpServletRequest request) {

    }

    // 将 request 参数解析成 PropertyValues
    private PropertyValues assignParameters(HttpServletRequest request) {
        Map<String, Object> map = WebUtils.getParametersStartingWith(request, "");
        return new PropertyValues(map);
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        getPropertyAccessor().registerCustomEditor(requiredType, propertyEditor);
    }
}
