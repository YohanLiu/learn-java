package com.yohan.xxljob;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Slf4j
public class SimpleXxlJob {

    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        log.info("执行定时任务,执行时间:" + new Date());
    }
}