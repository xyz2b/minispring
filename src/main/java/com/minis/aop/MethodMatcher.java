package com.minis.aop;

import java.lang.reflect.Method;

// MethodMatcher 这个接口代表的是方法的匹配算法，内部的实现就是看某个名是不是符不符合某个模式。
public interface MethodMatcher {
    boolean matches(Method method, Class<?> targetClass);
}
