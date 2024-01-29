package com.example.javabasic.java.reflect;

import com.example.javabasic.java.reflect.PersonForReflect;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionTest {
    @Test
    public void test01() {
        // 正常调用对象的构造器等方法
        PersonForReflect personForReflect = new PersonForReflect();
        personForReflect.setName("yohan");
        personForReflect.setAge(23);
        System.out.println(personForReflect);
        personForReflect.show();
    }

    @Test
    public void test02() throws Exception {
        // 利用反射调用类的一些非私有的属性及方法
        Class<PersonForReflect> personClass = PersonForReflect.class;
        PersonForReflect personForReflect = personClass.newInstance();
        System.out.println(personForReflect);

        Field age = personClass.getField("age");
        age.set(personForReflect, 18);
        System.out.println(age.get(personForReflect));
        System.out.println(personForReflect);

        Method show = personClass.getMethod("show");
        show.invoke(personForReflect);


    }

    @Test
    public void test03() throws Exception {
        // 利用反射调用类的一些私有的属性及方法
        Class<PersonForReflect> personClass = PersonForReflect.class;
        Constructor<PersonForReflect> declaredConstructor = personClass.getDeclaredConstructor(String.class, int.class);
        declaredConstructor.setAccessible(true);

        PersonForReflect personForReflect = declaredConstructor.newInstance("yohan", 46);
        System.out.println(personForReflect);

        Field name = personClass.getDeclaredField("name");
        name.setAccessible(true);
        name.set(personForReflect, "yohanNew");
        System.out.println(personForReflect);

        Method showNation = personClass.getDeclaredMethod("showNation", String.class);
        showNation.setAccessible(true);
        System.out.println(showNation.invoke(personForReflect, "中国"));
    }
}
