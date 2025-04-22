package com.minis.aop;

// PointcutAdvisor 接口扩展了 Advisor，内部可以返回 Pointcut，也就是说这个 Advisor 有一个特性：能支持切点 Pointcut 了。
// 这也是一个常规的 Advisor，所以可以放到我们现有的 AOP 框架中，让它负责来增强。
public interface PointcutAdvisor extends Advisor {
    Pointcut getPointcut();
}
