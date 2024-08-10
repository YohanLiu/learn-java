package com.yohan.javabasic.java.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    public static void main(String[] args) {
        try (SocketChannel client = SocketChannel.open()) {
            System.out.println("Connecting to server on port 8888...");
            client.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8888));
            client.configureBlocking(true);
            System.out.println("Connected to server");

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int bytesRead = client.read(buffer);
            while (bytesRead != -1) {
                buffer.flip();
                System.out.println("Received: " + new String(buffer.array(), 0, bytesRead));
                buffer.clear();
                bytesRead = client.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}