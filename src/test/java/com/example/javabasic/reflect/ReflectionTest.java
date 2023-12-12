package com.example.javabasic.reflect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SpringBootTest
public class ReflectionTest {
    @Test
    public void test01() {
        // 正常调用对象的构造器等方法
        Person person = new Person();
        person.setName("yohan");
        person.setAge(23);
        System.out.println(person);
        person.show();
    }

    @Test
    public void test02() throws Exception {
        // 利用反射调用类的一些非私有的属性及方法
        Class<Person> personClass = Person.class;
        Person person = personClass.newInstance();
        System.out.println(person);

        Field age = personClass.getField("age");
        age.set(person, 18);
        System.out.println(age.get(person));
        System.out.println(person);

        Method show = personClass.getMethod("show");
        show.invoke(person);


    }

    @Test
    public void test03() throws Exception {
        // 利用反射调用类的一些私有的属性及方法
        Class<Person> personClass = Person.class;
        Constructor<Person> declaredConstructor = personClass.getDeclaredConstructor(String.class, int.class);
        declaredConstructor.setAccessible(true);

        Person person = declaredConstructor.newInstance("yohan", 46);
        System.out.println(person);

        Field name = personClass.getDeclaredField("name");
        name.setAccessible(true);
        name.set(person, "yohanNew");
        System.out.println(person);

        Method showNation = personClass.getDeclaredMethod("showNation", String.class);
        showNation.setAccessible(true);
        System.out.println(showNation.invoke(person, "中国"));
    }
}
