package com.yohan.javabasic.java.juc;

import org.openjdk.jol.info.ClassLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 利用JOL工具查看对象内存布局.
 */
public class JOLDemo {
    public static void main(String[] args) {
        System.out.println("Object：----------------------------------------------------------------------------------");
        Object o = new Object();//16 bytes
        System.out.println(ClassLayout.parseInstance(o).toPrintable());

        System.out.println("Customer：--------------------------------------------------------------------------------");

        // 1 第一种情况，只有对象头，没有其它任何实例数据
        // 只有一个对象头的实例对象，16字节
        Customer c1 = new Customer();//16 bytes
        System.out.println(ClassLayout.parseInstance(c1).toPrintable());

        System.out.println("CustomerField：---------------------------------------------------------------------------");

        // 2 第二种情况，int + boolean，默认满足对其填充，24 bytes
        CustomerField customerField = new CustomerField();//16 bytes
        System.out.println(ClassLayout.parseInstance(customerField).toPrintable());

        System.out.println("List<Customer>：--------------------------------------------------------------------------");
        List<Customer> customers = new ArrayList<>();
        System.out.println(ClassLayout.parseInstance(customers).toPrintable());

        System.out.println("List<CustomerField>：---------------------------------------------------------------------");
        List<CustomerField> customerFields = new ArrayList<>();
        System.out.println(ClassLayout.parseInstance(customerFields).toPrintable());

    }
}


class Customer {
}

class CustomerField {
    int id;
    boolean status = true;
    Float price;

    String name;
}
