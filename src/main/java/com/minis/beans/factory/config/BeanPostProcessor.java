package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;

public interface BeanPostProcessor {
    // Bean 初始化之前
    Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;
    // Bean 初始化之后
    Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;
    void setBeanFactory(BeanFactory beanFactory);
}
