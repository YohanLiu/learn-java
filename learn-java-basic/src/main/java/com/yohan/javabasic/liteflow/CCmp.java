package com.yohan.javabasic.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

@LiteflowComponent("c")
public class CCmp extends NodeComponent {

    @Override
    public void process() {
        // do your business
        System.out.println(
            "c component process, " + this.getClass().getName() + " threadName = " + Thread.currentThread().getName());
    }
}
