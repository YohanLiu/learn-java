package com.yohan.javabasic.java.bytebuddy;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 每次重新load以后的bean,并未被spring管理起来,因此也就没有生效.
 * <p>所以添加了这样的方法,在每次spring容器启动起来后,优先做byteBuddy的加载.
 *
 * @author yinhou.liu
 * @Date 2024/01/26
 */
@Slf4j
public class ByteBuddyAspect implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("ByteBuddyAspect start");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(classLoader);
        TypePool typePool = TypePool.Default.of(classLoader);
        ByteBuddy byteBuddy = new ByteBuddy();

        // Fruit.getPrice 方法进行代理
        byteBuddy
                .redefine(typePool.describe("com.yohan.javabasic.java.bytebuddy.Fruit").resolve(), classFileLocator)
                .visit(Advice.to(FruitInterceptor.class).on(ElementMatchers.named("getPrice"))).make()
                .load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();

        // ServiceImpl.serviceMethod 方法进行代理
        byteBuddy
                .redefine(typePool.describe("com.yohan.javabasic.java.bytebuddy.ServiceImpl").resolve(), classFileLocator)
                .visit(Advice.to(ServiceImplInterceptor.class).on(ElementMatchers.named("serviceMethod")))
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();

        byteBuddy
                .redefine(typePool.describe("com.yohan.javabasic.utils.TimeUtil").resolve(),
                        classFileLocator)
                .visit(Advice.to(TimeUtilNewGetNowDateTimeInterceptor.class).on(ElementMatchers.named("getNowDateTime")))
                .visit(Advice.to(TimeUtilNewGetCurrentTimeMillisInterceptor.class).on(ElementMatchers.named("getCurrentTimeMillis")))
                .make().load(classLoader, ClassLoadingStrategy.Default.INJECTION).getLoaded();


        log.info("ByteBuddyAspect end");
    }
}
