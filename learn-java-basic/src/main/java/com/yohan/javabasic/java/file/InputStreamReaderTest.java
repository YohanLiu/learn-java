package com.yohan.javabasic.java.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class InputStreamReaderTest {
    public static void main(String[] args) {
        // 将GBK格式的文件转换成utf8并保存到文件中
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream("src\\main\\java\\com\\example\\javabasic\\file" +
                "\\dbcp_gbk.txt"), "GBK");
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream("src\\main\\java\\com\\example\\javabasic\\file" +
                     "\\dbcp_utf8.txt"), "UTF-8")) {
            int len;
            char[] chars = new char[1024];

            while ((len = inputStreamReader.read(chars)) != -1) {
                outputStreamWriter.write(chars, 0, len);

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
