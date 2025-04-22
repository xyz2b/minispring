package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.BeanPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.beans.factory.support.DefaultListableBeanFactory;
import com.minis.beans.factory.xml.XmlBeanDefinitionsReader;
import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

import java.util.ArrayList;
import java.util.List;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {
    DefaultListableBeanFactory beanFactory;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<BeanFactoryPostProcessor>();

    public ClassPathXmlApplicationContext(String filename) {
        this(filename, true);
    }

    // context 负责整合容器的启动过程，读外部配置，解析 Bean 定义，创建 BeanFactory
    public ClassPathXmlApplicationContext(String filename, boolean isRefresh) {
        // 将 XML 配置文件抽象成 Resource
        Resource resource = new ClassPathXmlResource(filename);
        // 将 Bean 容器抽象成 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionsReader reader = new XmlBeanDefinitionsReader(beanFactory);
        // 从 XML Resource 中读取 Bean 定义，并存入 BeanFactory 中，以供之后创建 Bean 实例使用
        reader.loadBeanDefinitions(resource);
        this.beanFactory = beanFactory;
        if (isRefresh) {
            try {
                refresh();
            } catch (IllegalStateException e) {
                throw new RuntimeException(e);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void refresh() throws BeansException, IllegalStateException {
        // Register bean processors that intercept bean creation.
        registerBeanPostProcessors(this.beanFactory);

        // Initialize other special beans in specific context subclasses.
        onRefresh();
    }

    @Override
    public void registerListeners() {
        String[] bdNames = this.beanFactory.getBeanDefinitionNames();
        for (String bdName : bdNames) {
            Object bean = null;
            try {
                bean = getBean(bdName);
            } catch (BeansException e1) {
                e1.printStackTrace();
            }

            if (bean instanceof ApplicationListener) {
                this.getApplicationEventPublisher().addApplicationListener((ApplicationListener<?>) bean);
            }
        }
    }

    @Override
    public void initApplicationEventPublisher() {
        ApplicationEventPublisher aep = new SimpleApplicationEventPublisher();
        this.setApplicationEventPublisher(aep);
    }

    @Override
    public void addApplicationListener(ApplicationListener listener) {
        this.getApplicationEventPublisher().addApplicationListener(listener);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        String[] bdNames = this.beanFactory.getBeanDefinitionNames();
        for (String bdName : bdNames) {
            BeanDefinition bd = this.beanFactory.getBeanDefinition(bdName);
            String clzName = bd.getClassName();
            Class<?> clz = null;
            try {
                clz = Class.forName(clzName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if(BeanFactoryPostProcessor.class.isAssignableFrom(clz)) {
                try {
                    this.beanFactoryPostProcessors.add((BeanFactoryPostProcessor) clz.newInstance());
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for(BeanFactoryPostProcessor processor : this.beanFactoryPostProcessors) {
            try {
                processor.postProcessBeanFactory(beanFactory);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerBeanPostProcessors(ConfigurableListableBeanFactory bf) {
        System.out.println("try to registerBeanPostProcessors");
        String[] bdNames = this.beanFactory.getBeanDefinitionNames();
        for (String bdName : bdNames) {
            BeanDefinition bd = this.beanFactory.getBeanDefinition(bdName);
            String clzName = bd.getClassName();
            Class<?> clz = null;
            try {
                clz = Class.forName(clzName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if(BeanPostProcessor.class.isAssignableFrom(clz)) {
                System.out.println("registerBeanPostProcessors : " + clzName);
                try {
                    this.beanFactory.addBeanPostProcessor((BeanPostProcessor)(this.beanFactory.getBean(bdName)));
                } catch (BeansException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected void onRefresh() {
        this.beanFactory.refresh();
    }

    @Override
    protected void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    // context再对外提供一个 getBean，底下就是调用的 BeanFactory 对应的方法
    public Object getBean(String beanName) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public boolean isSingleton(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        return this.beanFactory;
    }


    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }

    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.getApplicationEventPublisher().publishEvent(event);
    }
}
