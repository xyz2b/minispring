package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;

public interface ConfigurableBeanFactory extends BeanFactory, SingletonBeanRegistry {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);
    int getBeanPostProcessorCount();
    void registerDependentBean(String beanName, String dependentBeanName);
    // Bean 所依赖的 Bean
    String[] getDependentBeans(String beanName);
    // 依赖该 Bean 的 Bean
    String[] getDependenciesForBean(String beanName);
}
