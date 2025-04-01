package com.minis.beans;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.*;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.support.BeanDefinitionRegistry;
import com.minis.beans.factory.support.DefaultSingletonBeanRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    // factory 存放 Bean 定义
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    // getBean 容器的核心方法
    @Override
    public Object getBean(String beanName) throws BeansException {
        // 先尝试直接拿 Bean 实例
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            // 如果此时还没有这个 Bean 的实例，则尝试从毛坯实例中获取
            singleton = this.earlySingletonObjects.get(beanName);
            if(singleton == null) {
                // 如果连毛坯都没有，则获取 Bean 定义进行实例创建以及注册
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if(beanDefinition == null) {
                    throw new BeansException("no such bean definition: " + beanName);
                } else {
                    singleton = createBean(beanDefinition);
                    // 注册 Bean 实例
                    this.registerSingleton(beanName, singleton);

                    // 预留 bean post processor 位置
                    // step1: postProcessBeforeInitialization
                    // step2: afterPropertiesSet
                    // step3: init-method
                    // step4: postProcessAfterInitialization
                }
            }

        }
        return singleton;
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);

        //beanpostprocessor
    }

    @Override
    public Boolean containsBean(String name) {
        return containsSingleton(name);
    }

//    @Override
//    public void registerBean(String beanName, Object obj) {
//        this.registerSingleton(beanName, obj);
//    }

    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);
        this.beanDefinitionNames.add(name);
        if(!beanDefinition.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    public BeanDefinition getBeanDefinition(String name) {
        return beanDefinitionMap.get(name);
    }

    public boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    public boolean isSingleton(String name) {
        return beanDefinitionMap.get(name).isSingleton();
    }

    public boolean isPrototype(String name) {
        return beanDefinitionMap.get(name).isPrototype();
    }

    public Class<?> getType(String name) {
        return beanDefinitionMap.get(name).getClass();
    }

    public Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        // 创建毛坯 Bean 实例
        Object obj = doCreateBean(beanDefinition);
        // 存放到毛坯实例缓存中
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        try {
            clz = Class.forName(beanDefinition.getClassName());
//            obj = handleConstructor(beanDefinition, clz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        handleProperties(beanDefinition, clz, obj);

        return obj;
    }

    // doCreateBean创建毛坯实例，仅仅调用构造方法，没有进行属性设置
    private Object doCreateBean(BeanDefinition beanDefinition) {
        Object obj = null;
        Constructor<?> constructor = null;
        Class<?> clz = null;
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // 处理构造器参数
        ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
        // 如果有参数
        if(!constructorArgumentValues.isEmpty()) {
            Class<?>[] paramTypes = new Class<?>[constructorArgumentValues.getArgumentCount()];
            Object[] paramValues = new Object[constructorArgumentValues.getArgumentCount()];
            // 对每一个参数，分数据类型进行处理
            for(int i = 0; i < constructorArgumentValues.getArgumentCount(); i++) {
                ConstructorArgumentValue constructorArgumentValue = constructorArgumentValues.getIndexedArgumentValue(i);
                boolean isRef = constructorArgumentValue.isRef();

                if(!isRef) {
                    if("String".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = constructorArgumentValue.getValue();
                    } else if("Integer".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) constructorArgumentValue.getValue());
                    } else if("int".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) constructorArgumentValue.getValue());
                    } else {
                        // 默认为 String
                        paramTypes[i] = String.class;
                        paramValues[i] = constructorArgumentValue.getValue();
                    }
                } else {
                    try {
                        paramTypes[i] = Class.forName(constructorArgumentValue.getType());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        // 再次调用 getBean 创建 ref 的 bean 实例
                        paramValues[i] = getBean((String) constructorArgumentValue.getValue());
                    } catch (BeansException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            try {
                // 按照特定构造器创建实例
                constructor = clz.getConstructor(paramTypes);
                obj = constructor.newInstance(paramValues);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                obj = clz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return obj;
    }

    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        // 处理属性
        System.out.println("handle properties for bean: " + bd.getId());
        PropertyValues propertyValues = bd.getPropertyValues();
        if(!propertyValues.isEmpty()) {
            for(int i = 0; i < propertyValues.size(); i++) {
                // 对每一个属性，分数据类型进行处理
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pType = propertyValue.getType();
                String pName = propertyValue.getName();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!isRef) {   // 如果不是ref，只是普通属性
                    // 对每一个属性，分数据类型分别处理
                    if("String".equals(pType)) {
                        paramTypes[0] = String.class;
                    } else if("Integer".equals(pType)) {
                        paramTypes[0] = Integer.class;
                    } else if("int".equals(pType)) {
                        paramTypes[0] = int.class;
                    } else {
                        // 默认为 String
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = pValue;
                } else {    // is ref, create the dependent beans
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        // 再次调用 getBean 创建 ref 的 bean 实例
                        paramValues[0] = getBean((String)pValue);
                    } catch (BeansException e) {
                        throw new RuntimeException(e);
                    }
                }


                // 按照 setXxxx 规范查找 setter 方法，调用 setter 方法设置属性
                String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                try {
                    method.invoke(obj, paramValues);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void refresh() {
        for(String beanName: beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
