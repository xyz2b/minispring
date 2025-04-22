package com.minis.aop;

// Pointcut 接口定义了切点，也就是返回一条匹配规则。
public interface Pointcut {
    MethodMatcher getMethodMatcher();
}
