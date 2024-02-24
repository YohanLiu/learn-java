package com.yohan.xxljob;

import org.springframework.stereotype.Service;

/**
 * <p>xxl-job GLUE(Java) 模式.<br>
 * 可以在项目运行期间动态的添加定时任务.
 *
 * @author yohan
 * @Date 2023/12/24
 */
@Service
public class GlueDemo {

    public void methodA(){
        System.out.println("执行MethodA的方法");
    }
    public void methodB(){
        System.out.println("执行MethodB的方法");
    }
}
