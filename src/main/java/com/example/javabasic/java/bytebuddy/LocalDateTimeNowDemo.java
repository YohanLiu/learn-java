package com.example.javabasic.java.bytebuddy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static net.bytebuddy.matcher.ElementMatchers.none;

/**
 * 参照链接: @see <a href="https://github.com/raphw/byte-buddy/issues/451">https://github.com/raphw/byte-buddy/issues/451</a>
 *
 * <p>代理LocalDateTime.now()这个无参方法. LocalDateTime.now(ZoneId.systemDefault()),这种有参方法,是不影响的.LocalDateTime 的其他方法也不影响.
 * <p>如果下面的代码想把常量 10 换成从外部拿(不是常量,从其他类取),那么代码就会报错,推测原因可能是跟类加载器不同导致的.
 */
class LocalDateTimeNowDemo {
    @Advice.OnMethodExit
    public static void now(@Advice.Return(readOnly = false) LocalDateTime x) {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        System.out.println("新得到的：" + localDateTime);

//        Exception in thread "main" java.lang.NoClassDefFoundError: com/example/javabasic/java/bytebuddy/Fruit
//        at java.base/java.time.LocalDateTime.now(LocalDateTime.java)
//        at com.example.javabasic.java.bytebuddy.LocalDateTimeNowDemo.main(LocalDateTimeNowDemo.java:62)
        // 报错如上
        // LocalDateTime processedLocalDateTime = localDateTime.plusDays(Fruit.getSellByDate());

        LocalDateTime processedLocalDateTime = localDateTime.plusDays(10);
        System.out.println("+10天，得到的：" + processedLocalDateTime);
        x = processedLocalDateTime;
        System.out.println("改后的时间：" + x);
    }


    public static void main(String[] args) {
        new AgentBuilder.Default()
                .enableNativeMethodPrefix("wmsnative")
                .with(new ByteBuddy().with(Implementation.Context.Disabled.Factory.INSTANCE))
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .ignore(none())
                .type(ElementMatchers.named("java.time.LocalDateTime"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                        return builder.method(ElementMatchers.named("now")).intercept(Advice.to(LocalDateTimeNowDemo.class).wrap(StubMethod.INSTANCE));
                    }
                }).installOn(ByteBuddyAgent.install());

        System.out.println("调用方法返回：" + LocalDateTime.now());
    }
}