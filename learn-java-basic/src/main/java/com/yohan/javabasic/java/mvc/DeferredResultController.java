package com.yohan.javabasic.java.mvc;

import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

/**
 * 看apollo源码时候发现NotificationControllerV2 不会立即返回结果，而是通过 Spring DeferredResult 把请求挂起.
 * <p>原理参照链接：@see <a href="http://www.kailing.pub/article/index/arcid/163.html">http://www.kailing.pub/article/index/arcid/163.html</a>
 * <p>测试DeferredResult过程如下：
 * <p>先调用/async/longPolling接口，可以多开网页调用，此时并不会返回结果，而是一直在加载过程.
 * <p>然后调用/async/returnLongPollingValue,会发现网页加载完毕，并返回结果.
 *
 * @author yohan
 * @Date 2024/04/12
 */
@RestController
@RequestMapping("/async")
public class DeferredResultController {
    final Map<Integer, DeferredResult<String>> deferredResultMap = new ConcurrentReferenceHashMap<>();

    @GetMapping("/longPolling")
    public DeferredResult<String> longPolling() {
        DeferredResult<String> deferredResult = new DeferredResult<>(0L);
        deferredResultMap.put(deferredResult.hashCode(), deferredResult);
        deferredResult.onCompletion(() -> {
            deferredResultMap.remove(deferredResult.hashCode());
            System.err.println("还剩" + deferredResultMap.size() + "个deferredResult未响应");
        });
        return deferredResult;
    }

    @GetMapping("/returnLongPollingValue")
    public String returnLongPollingValue() {
        for (Map.Entry<Integer, DeferredResult<String>> entry : deferredResultMap.entrySet()) {
            entry.getValue().setResult("yohan");
        }
        return "调用成功";
    }
}
