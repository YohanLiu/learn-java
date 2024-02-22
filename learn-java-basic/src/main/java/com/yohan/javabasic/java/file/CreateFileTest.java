package com.yohan.javabasic.java.file;

import java.io.File;
import java.io.IOException;

public class CreateFileTest {
    public static void main(String[] args) {
        // 测试在main中用相对路径文件创建的位置
        File file = new File("main相对路径文件.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
