package com.gs.common.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
	
	// 构造一个线程池
	private static final ThreadPoolExecutor dataThreadPool = new ThreadPoolExecutor(
			3, 6, 600l, TimeUnit.SECONDS,
			new ArrayBlockingQueue<Runnable>(1000),
			new ThreadPoolExecutor.CallerRunsPolicy());
	
	public static void execute(Runnable runnable){
		dataThreadPool.execute(runnable);
	}
	

}
