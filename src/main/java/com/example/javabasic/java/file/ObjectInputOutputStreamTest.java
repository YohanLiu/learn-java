package com.example.javabasic.java.file;

import java.io.*;

public class ObjectInputOutputStreamTest {
    public static void main(String[] args) {
        // 将一个对象序列化到文件中存储，并再从此文件读出并输出
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("src\\main\\java\\com\\example\\javabasic" +
                "\\file\\person.dat")));
             ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("src\\main\\java\\com\\example\\javabasic" +
                     "\\file\\person.dat")))) {
            PersonForFile personForFile = new PersonForFile("yohan", 23);
            objectOutputStream.writeObject(personForFile);

            objectOutputStream.flush();
            PersonForFile personForFileFromDat = (PersonForFile) objectInputStream.readObject();
            System.out.println(personForFileFromDat);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
