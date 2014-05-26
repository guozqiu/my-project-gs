package com.gs.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gs.common.exception.BusinessException;


public class CollectionUtils {

	/**
	 * 从一个LIST中拷数据到另一个LIST中，
	 * 下标从beginIndex到endIndex
	 * 包括beginIndex，但不包括endIndex
	 * 2008-8-6
	 * @author fuqu
	 * @param src 原始集合
	 * @param beginIndex 开始下标（含）
	 * @param endIndex 结束的下标（不含)
	 * @return
	 */
	public static <T> List<T> copyList(List<T> src, int beginIndex, int endIndex) {
		if (src == null || src.size() == 0) {
			return null;
		}

		if (beginIndex < 0) {
			throw new BusinessException("集合下标越界:" + beginIndex);
		}
		if (beginIndex > endIndex) {
			throw new BusinessException("集合下标设置错误:" + beginIndex);
		}
		int size = src.size();
		if (endIndex >= size) {
			endIndex = size;
		}
		List<T> result = new ArrayList<T>(endIndex - beginIndex);
		for (int i = beginIndex; i < endIndex; i++) {
			result.add(src.get(i));
		}
		return result;
	}

	/**
	 * 从一个数组中拷数据到另一个数组中
	 * 下标从beginIndex到endIndex
	 * 包括beginIndex，但不包括endIndex
	 * 2008-8-6
	 * @author fuqu
	 * @param src 原始集合
	 * @param beginIndex 开始下标（含）
	 * @param endIndex 结束的下标（不含）
	 * @return
	 */
	public static Object[] copyArray(Object[] src, int beginIndex, int endIndex) {
		if (src == null || src.length == 0) {
			return null;
		}

		if (beginIndex < 0) {
			throw new BusinessException("集合下标越界:" + beginIndex);
		}
		if (beginIndex > endIndex) {
			throw new BusinessException("集合下标设置错误:" + beginIndex);
		}

		if (endIndex >= src.length) {
			endIndex = src.length;
		}
		Object[] result = new Object[endIndex - beginIndex];
		for (int i = beginIndex; i < endIndex; i++) {
			result[i - beginIndex] = src[i];
		}
		return result;
	}
	
	/**
	 * 判断集合是否为空，如果集合没有元素返回真，如果有NULL元素，返回FALSE
	 * 否则返假
	 * 2008-8-19
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static <T> boolean isEmpty(Collection<T> src){
		if(src==null)
			return true;
		return src.isEmpty();
	}
	
	/**
	 * 判断MAP是否为空，如果集合没有元素返回真  或都每个元素都是空，
	 * 否则返假
	 * 2008-8-19
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static <K,V> boolean isEmpty(Map<K,V> src){
		if(src==null)
			return true;
		return src.isEmpty();
	}
	
	/**
	 * 与isEmpty(Map)相反
	 * 2008-8-19
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static <K,V> boolean isNotEmpty(Map<K,V> src){
		return !isEmpty(src);
	}
	
	/**
	 * 集合是否至少有一个非空元素
	 * 2008-8-19
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static <T> boolean hasNotNullElement(Collection<T> src){
		if(isEmpty(src))
			return false;
		for (Object object : src) {
			if(object!=null)
				return true;
		}
		return false;
	}
	
	
	/**
	 * 是否所有元素为空或没有元素或集合为空
	 * 2008-8-19
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static <T> boolean isAllElementsNull(Collection<T> src){
		return !hasNotNullElement(src);
	}
	
	
	/**
	 * 与isEmpty方法相反
	 * 2008-8-19
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static <T> boolean isNotEmpty(Collection<T> src){
		return !isEmpty(src);
	}
	
	/**
	 * 只要有一个非空项即返回FALSE
	 * 2008-10-24
	 * @author fuqu
	 * @param src
	 * @return
	 */
	public static boolean isEmpty(Object[] src){
		if(src==null)
			return true;
		if(src.length==0)
			return true;
		for (int i=0; i<src.length;i++) {
			if(src[i]!=null)
				return false;
		}
		return true;
	}
	
	public static boolean isNotEmpty(Object[] src){
		return !isEmpty(src);
	}
	
	/**
	 * 将数组的内容分解
	 * 2008-10-09
	 * @author fuqu
	 * @param obj
	 * @return Object
	 */
	public static Object fromArrayToObject(Object obj){
		StringBuffer stringBuffer=new StringBuffer();
		if(obj != null){
			if(obj instanceof Object[]){
				if(isNotEmpty(((Object[])obj))){
					for(Object object:(Object[])obj){
						stringBuffer.append(object);
						}
					return stringBuffer;
				}
			}
		}
		return obj;
	}

	/**
	 * 复制集合数据
	 * 2008-12-3
	 * @author fuqu
	 * @param src 原集合
	 * @param from 开始位置
	 * @param length 
	 * @return
	 */
	public static <T> List<T> copySet(Set<T> src, int from, int length) {
		if(src==null){
			return null;
		}
		List<T> result = new ArrayList<T>(length-from);
		int i = 0;
		for (T object : src) {
			if(i>=from && i<length){
				result.add(object);
			}
			if(i>=length){
				return result;
			}
			i++;
		}
		return result;
	}
	
	
	/**
	* 只要有一个非空与不等于空白项即返回FALSE
	* Dec 6, 2008
	* @author fuqu
	* @param src
	* @return
	*/
	public static boolean isBlank(Object[] src) {
		if (src == null)
			return true;
		if (src.length == 0)
			return true;
		for (int i = 0; i < src.length; i++) {
			if (src[i] != null && src[i] != "")
				return false;
		}
		return true;
	}

	/**
	 * 判断字符串数组是否至少有1个不为空并且不是空字符串
	 * 2009-1-4
	 * @author fuqu
	 * @param propValue
	 * @return
	 */
	public static boolean isEmpty(String[] propValue) {
		if(propValue==null)
			return true;
		if(propValue.length==0)
			return true;
		for (int i = 0; i < propValue.length; i++) {
			if(StringUtils.isNotEmpty(propValue[i])){
				return false;
			}
		}
		return true;
	}
	
	public static <T> List<T> removeDuplicate(List<T> list){
		if(null!=list){
	        Set<T> hashSet = new HashSet<T>(list); 
	        list.clear(); 
	        list.addAll(hashSet); 
		}
        return list; 
    } 
	
	public static List<Integer> stringToInt(String[] src) {
		List<Integer> results = new ArrayList<Integer>(src.length);
		for (int i = 0; i < src.length; i++) {
			results.add(Integer.parseInt(src[i]));
		}
		
		return results;
	}

	
}
