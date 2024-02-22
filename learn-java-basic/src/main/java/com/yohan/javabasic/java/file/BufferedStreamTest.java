package com.yohan.javabasic.java.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BufferedStreamTest {
    public static void main(String[] args) {
        File srcFile = new File("src\\main\\java\\com\\example\\javabasic\\file\\playgirl.jpg");
        File destFile = new File("src\\main\\java\\com\\example\\javabasic\\file\\playgirl_buffered_stream.jpg");

        try (
                BufferedInputStream srcBufferedInputStream = new BufferedInputStream(new FileInputStream(srcFile));
                BufferedOutputStream destBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destFile))
        ) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = srcBufferedInputStream.read(buffer)) != -1) {
                destBufferedOutputStream.write(buffer, 0, len);
            }
            // 刷新，将缓冲区数据写入目的地
            destBufferedOutputStream.flush();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
