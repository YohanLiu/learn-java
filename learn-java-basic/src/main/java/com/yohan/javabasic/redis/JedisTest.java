package com.yohan.javabasic.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.List;

/**
 * jedis 操作 redis 测试类.
 *
 * @author yinhou.liu
 * @Date 2024/01/12
 */
public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        // 密码
        //jedis.auth("123456");

        System.out.println(jedis.ping());
        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        // 1000W数据不带过期时间占用内存 1.1G
        // 1000W数据带一小时过期时间占用内存 1.7G
        for (int i = 1000000; i < 10000000; i++) {
            pipeline.setex("yewukeyzhemechangkeyiba1478c816efb66239e71c83c3f377818e" + i, 3600, "1");
        }
        List<Object> results = pipeline.syncAndReturnAll();
        long end = System.currentTimeMillis();
        System.out.println("Pipelined SET: " + ((end - start) / 1000.0) + " seconds");

        jedis.disconnect();


    }
}
