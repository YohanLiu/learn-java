package com.yohan.javabasic.java.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author yinhou.liu
 * @Date 2024/08/10
 */
public class NioTransferTo {

    public static void main(String[] args) throws IOException {
        File srcFile = new File("learn-java-basic/src/main/java/com/yohan/javabasic/java/file/playgirl.jpg");
        File destFile = new File("learn-java-basic/src/main/java/com/yohan/javabasic/java/file" +
                "/playgirl_copy_NIO_Transfer.jpg");
        copyFileByChannel(srcFile, destFile);
    }

    public static void copyFileByChannel(File source, File dest) throws
            IOException {
        try (FileChannel sourceChannel = new FileInputStream(source)
                .getChannel();
             FileChannel targetChannel = new FileOutputStream(dest).getChannel
                     ();) {
            for (long count = sourceChannel.size(); count > 0; ) {
                long transferred = sourceChannel.transferTo(
                        sourceChannel.position(), count, targetChannel);
                sourceChannel.position(sourceChannel.position() + transferred);
                count -= transferred;
            }
        }
    }

}
