package com.lidroid.xutils.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogUtils {
	private static final Log logger = LogFactory.getLog(LogUtils.class);

	public static void e(String message, Exception e) {
		logger.error(message, e);
	}

	public static void d(String message) {
		logger.debug(message);
	}

	public static void w(String message) {
		logger.warn(message);
	}

}
