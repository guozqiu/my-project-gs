package com.gs.common.util;

import java.util.List;

import com.alibaba.fastjson.JSON;


/**
 * 文档：http://code.alibabatech.com/wiki/pages/viewpage.action?pageId=2424946
 */
public class JsonXmlUtils {
	/*
	 * public static String convertXMLtoJSON(String xml) { XMLSerializer
	 * xmlSerializer = new XMLSerializer(); JSON json = xmlSerializer.read(xml);
	 * return json.toString(1); // System.out.println(json.toString(1)); }
	 */

	public static String toJSONString(Object obj) {
		return JSON.toJSONString(obj);
	}

	public static JSON parse(String jsonString) {
		return (JSON) JSON.parse(jsonString);
	}

	public static<T> T parseObject(String jsonString,Class<T> clazz) {
		return JSON.parseObject(jsonString, clazz);
	}
	
	public static<T> List<T> parseArray(String jsonString,Class<T> clazz) {
		return JSON.parseArray(jsonString, clazz);
	}

	
}
