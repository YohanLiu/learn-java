package com.yohan.xxljobNotice;

import com.xxl.job.core.context.XxlJobContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yinhou.liu
 * @Date 2025/06/12
 */
public class XxlJobContextTest {
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(2);

		for (int i = 0; i < 10; i++) {
			int jobId = i;
			XxlJobContext.setXxlJobContext(new XxlJobContext(jobId, "param:" + jobId, "handle" + jobId, 0, 1));
			executor.execute(() -> {
				try {

					XxlJobContext ctx = XxlJobContext.getXxlJobContext();
					System.out.println("Job ID in task: " + (ctx != null ? ctx.getJobId() : "null"));
				} catch (Exception e) {
					System.out.println("Error in thread: " + e.getMessage());
				}
			});
		}
	}
}
