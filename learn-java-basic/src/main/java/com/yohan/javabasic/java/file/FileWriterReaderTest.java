package com.yohan.javabasic.java.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterReaderTest {
    public static void main(String[] args) {
        // 相对路径的方式
        // idea中的java文件比较特殊，是以src这个文件夹为主位置
        File srcFile = new File("learn-java-basic/src/main/java/com/yohan/javabasic/java/file/io输入.txt");
        // 绝对路径的方式
        File destFile = new File("/Users/yohan/myProject/learn-java/learn-java-basic/src/main/java/com/yohan/javabasic/java/file/io输出.txt");

        try (FileReader srcFileReader = new FileReader(srcFile);
             FileWriter destFileWriter = new FileWriter(destFile)) {

            // 从文件中读取内容并写入另一个文件中
            char[] chars = new char[3];
            int read = srcFileReader.read(chars);
            while (read != -1) {
                destFileWriter.write(chars, 0, read);

                read = srcFileReader.read(chars);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

