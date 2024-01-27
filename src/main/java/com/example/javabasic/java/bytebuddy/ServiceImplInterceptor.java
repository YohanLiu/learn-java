package com.example.javabasic.java.bytebuddy;

import com.example.javabasic.utils.SpringUtil;
import net.bytebuddy.asm.Advice;

import java.util.Objects;

/**
 * bytebuddy 针对类中的方法进行增强.
 *
 * <p>如果拦截器中调用了本类的方法(如本例的serviceStaticMethod),那么切记方法访问权限要为 public.
 * <p>因为 bytebuddy 增强是把方法织入到被增强类中,实际在被增强类中,调用的是ServiceImplInterceptor.serviceStaticMethod.
 * <p>所以访问类型要为 public.
 * <p>当然,如果你不想复制一份同样的方法,你也可以利用反射来调用被增强类的方法.
 * <p>反射调用虽然可读性差,但是相较于复制一份代码,还是比较易于维护.
 *
 * @author yinhou.liu
 * @Date 2024/01/27
 */
public class ServiceImplInterceptor {
    @Advice.OnMethodExit
    public static void selectAvailable(@Advice.Argument(0) Integer trueOrFalse, @Advice.Return(readOnly = false) boolean returned) {
        System.out.println("bytebuddy---方法返回值:" + returned);
        boolean bytebuddyStatic;
        bytebuddyStatic = serviceStaticMethod(trueOrFalse);
        System.out.println("bytebuddy调用自己拦截器静态方法---返回值:" + bytebuddyStatic);

        ServiceImpl serviceImpl = SpringUtil.getBean(ServiceImpl.class);

        // 反射的方式调用被增强类中的方法
//        try {
//            Method serviceStaticMethod = serviceImpl.getClass().getDeclaredMethod("serviceStaticMethod", Integer.class);
//            serviceStaticMethod.setAccessible(true);
//            bytebuddyStatic = (boolean) serviceStaticMethod.invoke(serviceImpl, trueOrFalse);
//            System.out.println("bytebuddy拦截器通过反射调用被增强类静态方法---返回值:" + bytebuddyStatic);
//        } catch (Exception e) {
//            System.out.println("bytebuddy 反射调用被增强类中方法异常:" + e.getMessage());
//        }
        returned = !bytebuddyStatic;
        System.out.println("bytebuddy---结束了返回值:" + returned);
    }


    public static boolean serviceStaticMethod(Integer trueOrFalse) {
        System.out.println("ServiceImplInterceptor拦截器ServiceImpl的serviceStaticMethod静态方法入参:" + trueOrFalse);
        boolean staticResult = Objects.equals(trueOrFalse, 1);
        System.out.println("ServiceImplInterceptor拦截器ServiceImpl的serviceStaticMethod静态方法返回值:" + staticResult);
        return staticResult;
    }
}
