package com.example.javabasic.java.file;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileApiTest {
    @Test
    public void test01() {
        // 判断制定文件夹下是否有某个后缀的文件
        File file = new File("F:\\Pictures\\Wallpaper");
        String[] list = file.list();
        for (String l : list) {
            if (l.endsWith(".jpg")) {
                System.out.println("有指定文件");
                break;
            }
        }
    }

    @Test
    public void test02() {
        // 测试在测试类中用相对路径文件创建的位置
        File file = new File("测试类相对路径文件.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test03() {
        // 测试文件输入
        File file = new File("E:\\work_study\\my-project\\java-basic\\src\\main\\java\\com\\example\\javabasic\\file\\io输入.txt");
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("yohan 帅哥，加个微信吧！");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
