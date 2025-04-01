package com.minis.web;

import com.minis.beans.AbstractPropertyAccessor;
import com.minis.beans.PropertyEditorRegistrySupport;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// 利用反射对 Bean 属性值进行读写，具体是通过 setter 和 getter 方法。
public class BeanWrapperImpl extends AbstractPropertyAccessor {
    Object wrappedObject; // 目标对象
    Class<?> clz;

    public BeanWrapperImpl(Object target) {
        super();
        this.wrappedObject = target;
        this.clz = target.getClass();
    }

    // 绑定具体某个参数
    public void setPropertyValue(PropertyValue pv) {
        // 拿到参数处理器
        BeanPropertyHandler propertyHandler = new BeanPropertyHandler(pv.getName());
        // 找到对应参数类型的Editor，先获取 CustomEditor ，如果它不存在，再获取 DefaultEditor
        PropertyEditor pe = this.getCustomEditor(propertyHandler.getPropertyClz());
        if(pe == null) {
            pe = this.getDefaultEditor(propertyHandler.getPropertyClz());
        }
        // 设置参数值
        pe.setAsText((String) pv.getValue());
        propertyHandler.setValue(pe.getValue());
    }

    class BeanPropertyHandler {
        Method writeMethod = null;
        Method readMethod = null;
        Class<?> propertyClz = null;

        public Class<?> getPropertyClz() {
            return propertyClz;
        }

        public BeanPropertyHandler(String propertyName) {
            try {
                // 获取参数对应的属性及类型
                Field field = clz.getDeclaredField(propertyName);
                propertyClz = field.getType();
                // 获取设置属性的方法，按照约定setXXX()
                this.writeMethod = clz.getDeclaredMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1).toLowerCase(), propertyClz);
                // 获取读属性的方法，按照约定getXXX()
                this.readMethod = clz.getDeclaredMethod("get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1).toLowerCase());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        // 调用getter读属性值
        public Object getValue() {
            Object result = null;
            readMethod.setAccessible(true);
            try {
                result = readMethod.invoke(wrappedObject);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return result;
        }

        // 调用setter设置属性值
        public void setValue(Object value) {
            writeMethod.setAccessible(true);
            try {
                writeMethod.invoke(wrappedObject, value);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
