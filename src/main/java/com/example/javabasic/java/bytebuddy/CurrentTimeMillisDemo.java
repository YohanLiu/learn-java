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

import static net.bytebuddy.matcher.ElementMatchers.none;

/**
 * 参照链接: @see <a href="https://github.com/raphw/byte-buddy/issues/451">https://github.com/raphw/byte-buddy/issues/451</a>
 * <p>如果下面的代码想把常量 123 换成从外部拿(不是常量,从其他类取),那么代码就会报错,推测原因可能是跟类加载器不同导致的.
 */
class CurrentTimeMillisDemo {
    @Advice.OnMethodExit
    public static void millis(@Advice.Return(readOnly = false) long x) {
        x = 123;

//        Exception in thread "main" java.lang.NoClassDefFoundError: com/example/javabasic/java/bytebuddy/Fruit
//        at java.base/java.lang.System.currentTimeMillis(System.java)
//        at com.example.javabasic.java.bytebuddy.CurrentTimeMillisDemo.main(CurrentTimeMillisDemo.java:45)
        // 报错如上
        // x = Fruit.getSellByDate();
    }

    public static void main(String[] args) {
        new AgentBuilder.Default()
                .enableNativeMethodPrefix("wmsnative")
                .with(new ByteBuddy().with(Implementation.Context.Disabled.Factory.INSTANCE))
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                .ignore(none())
                .type(ElementMatchers.named("java.lang.System"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
                        return builder.method(ElementMatchers.named("currentTimeMillis")).intercept(Advice.to(CurrentTimeMillisDemo.class).wrap(StubMethod.INSTANCE));
                    }

                }).installOn(ByteBuddyAgent.install());

        System.out.println(System.currentTimeMillis());
    }
}