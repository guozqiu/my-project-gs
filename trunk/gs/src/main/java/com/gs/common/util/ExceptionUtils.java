package com.gs.common.util;

import java.util.Collection;

import com.gs.common.exception.BusinessException;
import com.gs.common.exception.UnknownException;

public class ExceptionUtils {

	/**
	 * 如果目标为空则抛出异常
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwIfNull(Object target,String errorMessage){
		if(target==null){
			throw new BusinessException(errorMessage);
		}
	}
	
	/**
	 * 如果目标为空则抛出异常
	 * 本方法空指针安全
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwIfEmpty(String target,String errorMessage)
	{
		if(StringUtils.isEmpty(target)){
			throw new BusinessException(errorMessage);
		}
	}
	
	/**
	 * 如果目标为空则抛出异常
	 * 本方法空指针安全
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwIfEmpty(Collection<?> target,String errorMessage)
	{
		if(CollectionUtils.isEmpty(target)){
			throw new BusinessException(errorMessage);
		}
	}
	
	/**
	 * 如果目标为空则抛出异常
	 * 本方法空指针安全
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwIfEmpty(Object[] target,String errorMessage)
	{
		if(CollectionUtils.isEmpty(target)){
			throw new BusinessException(errorMessage);
		}
	}
	
	
	/**
	 * 如果目标为空则抛出异常
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwUnknowExceptionIfNull(Object target,String errorMessage){
		if(target==null){
			throw new UnknownException(errorMessage);
		}
	}
	
	/**
	 * 如果目标为空则抛出异常
	 * 本方法空指针安全
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwUnknowExceptionIfEmpty(String target,String errorMessage)
	{
		if(StringUtils.isEmpty(target)){
			throw new UnknownException(errorMessage);
		}
	}
	
	/**
	 * 如果目标为空则抛出异常
	 * 本方法空指针安全
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwUnknowExceptionIfEmpty(Collection<?> target,String errorMessage)
	{
		if(CollectionUtils.isEmpty(target)){
			throw new UnknownException(errorMessage);
		}
	}
	
	/**
	 * 如果目标为空则抛出异常
	 * 本方法空指针安全
	 * @author fuqu
	 * 2009-11-3
	 * @param target
	 * @param errorMessage
	 */
	public static void throwUnknowExceptionIfEmpty(Object[] target,String errorMessage)
	{
		if(CollectionUtils.isEmpty(target)){
			throw new UnknownException(errorMessage);
		}
	}


	public static <T> T throwUnknowE(String message, RuntimeException e) {
		throw new UnknownException(message,e);
	}
	
	public static <T> T throwE(String message, RuntimeException e) {
		throw new BusinessException(message,e);
	}
	
	public static <T> T throwE(String message, Throwable e) {
		throw new BusinessException(message,e);
	}
	
	public static <T> T throwE(String message) {
		throw new BusinessException(message);
	}
	
	public static String getExceptionMessage(Throwable t){
		if(t==null){
			return null;
		}
		if(t instanceof BusinessException){
			return t.getMessage();
		}
		while(t.getCause()!=null){
			if(t.getCause() instanceof BusinessException){
				return t.getCause().getMessage();
			}/*else if(t.getCause() instanceof CsrfTokenCheckException){
				return "重复提交了表单，请刷新页面后再提交";
			}*/
			t = t.getCause();
		}
		return new StringBuilder("发生未知错误：").append(t.getClass().getName()).append(":").append(t.getMessage()).toString();
	}
	
	private static final String SEPRETOR = "\r\n";
	private static final String CAUSE_BY = "Cause By：\r\n";
	public static StringBuilder appendStackTrace(StringBuilder content,Throwable throwable){
		if(throwable==null || content==null){
			return content;
		}
		content.append(SEPRETOR).append(CAUSE_BY);
		content.append(throwable.toString()).append(SEPRETOR);
		content.append(SEPRETOR);
		
		StackTraceElement[] stackTrace = throwable.getStackTrace();
		if(stackTrace!=null){
			for (StackTraceElement stack : stackTrace) {
				if(stack!=null){
					content.append(stack.toString());
					content.append(SEPRETOR);
				}
			}
			appendStackTrace(content, throwable.getCause());
		}
		return content;
	}
	
}
