package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.config.AutowireCapableBeanFactory;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;

        Class<?> clz = bean.getClass();
        Field[] fields = clz.getDeclaredFields();
        if(fields != null && fields.length > 0) {
            // 对每一个属性进行判断，如果带有 @Autowired 注解则进行处理
            for(Field field : fields) {
                boolean isAutowired = field.isAnnotationPresent(Autowired.class);
                if(isAutowired) {
                    // 根据属性名查找同名的 bean
                    String fieldName = field.getName();
                    Object autowireObj = this.getBeanFactory().getBean(fieldName);
                    // 设置属性值，完成注入
                    try {
                        field.setAccessible(true);
                        field.set(bean, autowireObj);
                        System.out.println("autowire " + fieldName + " for bean " + beanName);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
