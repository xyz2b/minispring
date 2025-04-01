package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.AbstractAutowireCapableBeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory {
    ConfigurableListableBeanFactory parentBeanFactory;


    public void setParent(ConfigurableListableBeanFactory beanFactory) {
        this.parentBeanFactory = beanFactory;
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return (String[])this.beanDefinitionNames.toArray(new String[this.beanDefinitionNames.size()]);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> type) {
        List<String> result = new ArrayList<>();
        for(String beanName : this.beanDefinitionNames) {
            BeanDefinition mbd = this.getBeanDefinition(beanName);
            try {
                Class<?> classToMatch = mbd.getBeanClass();
                if(type.isAssignableFrom(classToMatch)) {
                    result.add(beanName);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return (String[]) result.toArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        String[] beanNames = getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap<>(beanNames.length);
        for(String beanName : beanNames) {
            Object beanInstance = getBean(beanName);
            result.put(beanName, (T) beanInstance);
        }
        return result;
    }

    @Override
    public void registerDependentBean(String beanName, String dependentBeanName) {

    }

    @Override
    public String[] getDependentBeans(String beanName) {
        return new String[0];
    }

    @Override
    public String[] getDependenciesForBean(String beanName) {
        return new String[0];
    }

    public Object getBean(String beanName) throws BeansException {
        // 先从 管理 WEB 容器的 工厂中获取 Bean
        Object result = super.getBean(beanName);
        if (result == null && this.parentBeanFactory != null) {
            // 获取不到 再从 管理原始 IOC 容器的工厂里获取 Bean
            result = this.parentBeanFactory.getBean(beanName);
        }
        return result;
    }
}
