package com.minis.aop;

public class NameMatchMethodPointcutAdvisor implements PointcutAdvisor {
    private Advice advice;
    private MethodInterceptor methodInterceptor;
    private String mappedName;
    private final NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();

    public NameMatchMethodPointcutAdvisor() {}

    public NameMatchMethodPointcutAdvisor(Advice advice) {
        this.advice = advice;
    }

    public Advice getAdvice() {
        return advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
        MethodInterceptor methodInterceptor = null;
        if(advice instanceof BeforeAdvice) {
            methodInterceptor = new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice);
        } else if(advice instanceof AfterAdvice) {
            methodInterceptor = new AfterReturningAdviceInterceptor((AfterReturningAdvice) advice);
        } else if(advice instanceof MethodInterceptor) {
            methodInterceptor = (MethodInterceptor) advice;
        }
        setMethodInterceptor(methodInterceptor);
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    @Override
    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public String getMappedName() {
        return mappedName;
    }

    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
        this.pointcut.setMappedName(mappedName);
    }
}
