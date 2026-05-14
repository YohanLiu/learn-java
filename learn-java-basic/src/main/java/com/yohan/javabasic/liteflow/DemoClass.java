package com.yohan.javabasic.liteflow;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;

@Component
public class DemoClass {

    @Resource
    private FlowExecutor flowExecutor;

    public void testConfig() {
        LiteflowResponse response = flowExecutor.execute2Resp("chain1", "arg", DefaultContext.class);
        System.out.println(response.isSuccess());
    }
}
