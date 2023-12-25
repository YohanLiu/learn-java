package com.example.javabasic.java.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectInputOutputStreamTest {
    public static void main(String[] args) {
        // 将一个对象序列化到文件中存储，并再从此文件读出并输出
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("src\\main\\java\\com\\example\\javabasic" +
                "\\file\\person.dat")));
             ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("src\\main\\java\\com\\example\\javabasic" +
                     "\\file\\person.dat")))) {
            Person person = new Person("yohan", 23);
            objectOutputStream.writeObject(person);

            objectOutputStream.flush();
            Person personFromDat = (Person)objectInputStream.readObject();
            System.out.println(personFromDat);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
