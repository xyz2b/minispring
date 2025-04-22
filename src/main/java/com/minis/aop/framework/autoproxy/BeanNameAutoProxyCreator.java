package com.minis.aop.framework.autoproxy;

import com.minis.aop.*;
import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.util.PatterMatchUtils;

public class BeanNameAutoProxyCreator implements BeanPostProcessor {
    String pattern; // 代理对象名称模式，如action*
    private BeanFactory beanFactory;
    private AopProxyFactory aopProxyFactory;
    private String interceptorName;
    private PointcutAdvisor advisor;

    public BeanNameAutoProxyCreator() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    // 核心方法。在bean实例化之后，init-method调用之前执行这个步骤。
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(isMatch(beanName, this.pattern)) {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setTarget(bean);
            proxyFactoryBean.setBeanFactory(this.beanFactory);
            proxyFactoryBean.setAopProxyFactory(aopProxyFactory);
            proxyFactoryBean.setInterceptorName(this.interceptorName);
            return proxyFactoryBean;
        } else {
            return bean;
        }
    }

    protected boolean isMatch(String beanName, String mappedName) {
        return PatterMatchUtils.simpleMatch(mappedName, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setAdvisor(PointcutAdvisor advisor) {
        this.advisor = advisor;
    }

    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    protected AopProxy createAopProxy(Object target) {
        return this.aopProxyFactory.createAopProxy(target,this.advisor);
    }
    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy();
    }
}
