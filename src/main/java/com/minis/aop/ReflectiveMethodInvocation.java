package com.minis.aop;

import java.lang.reflect.Method;

public class ReflectiveMethodInvocation implements MethodInvocation {
    private final Object proxy;
    private final Object target;
    private final Method method;
    protected Object[] args;
    private Class<?> targetClass;

    protected ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] args, Class<?> targetClass) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.args = args;
        this.targetClass = targetClass;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object proceed() throws Throwable {
        return this.method.invoke(this.target, this.args);
    }
}
