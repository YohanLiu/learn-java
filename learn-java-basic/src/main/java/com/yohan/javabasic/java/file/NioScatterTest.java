package com.yohan.javabasic.java.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioScatterTest {
    public static void main(String[] args) {
        try (FileInputStream fileInputStream = new FileInputStream("learn-java-basic/src/main/java/com/yohan" +
                "/javabasic/java/file/NioScatter.text");
             FileChannel fileChannel = fileInputStream.getChannel()) {

            // 创建两个缓冲区
            ByteBuffer headerBuffer = ByteBuffer.allocate(7); // 假设头部长度为10字节
            ByteBuffer bodyBuffer = ByteBuffer.allocate(100); // 假设主体长度为100字节

            // 将缓冲区放入数组
            ByteBuffer[] buffers = {headerBuffer, bodyBuffer};

            // 从文件通道中读取数据到缓冲区数组
            long bytesRead = fileChannel.read(buffers);

            // 切换到读模式
            for (ByteBuffer buffer : buffers) {
                buffer.flip();
            }

            // 处理数据
            System.out.println("Header: " + new String(headerBuffer.array()));
            System.out.println("Body: " + new String(bodyBuffer.array()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
