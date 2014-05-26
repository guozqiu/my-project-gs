package com.gs.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gs.common.exception.BusinessException;

/**
 * 字符串工具类
 * 
 * @author fuqu
 * 
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

    // 判断字符是否有内容
    public static boolean isEmpty(String src) {
        return org.apache.commons.lang.StringUtils.isBlank(src);
    }

    public static boolean isNotEmpty(String src) {
        return !isEmpty(src);
    }

    public static String encode(String src, String charset) {
        try {
            return URLEncoder.encode(src, charset);
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("转码失败", e);
        }
    }

    /**
     * 取得当前操作系统的行分割符
     * 2009-5-13
     * @author fuqu
     */
    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 去掉字符串中html代码 
     * 2009-09-01
     * @author fuqu
     * @param htmlstr
     * @return
     */

    public static String removeHtmlTag(String htmlstr) {

        if (!"".equals(htmlstr) && htmlstr != null) {
            Pattern pat = Pattern.compile("\\s*<.*?>\\s*", Pattern.DOTALL | Pattern.MULTILINE
                                                           | Pattern.CASE_INSENSITIVE);
            Matcher m = pat.matcher(htmlstr);
            // 去掉所有html标记
            String rs = m.replaceAll("");
            rs = rs.replaceAll("&nbsp;", "");
            rs = rs.replaceAll("&lt;", "<");
            rs = rs.replaceAll("&gt;", ">");
            return rs;
        } else {
            return "";
        }

    }

    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = str.length(); --i >= 0;) {
            if (str.charAt(i) == '.') {
                continue;
            }
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String trimToNull(String idColumn) {
        if (idColumn == null) {
            return null;
        }
        idColumn = idColumn.trim();
        if (idColumn.equals("")) {
            return null;
        }
        return idColumn;

    }

    /**
     * 把前端传回的list格式的string，变成List<String>
     * @param listString
     * @return
     */
    public static List<String> stringTolist(String listString) {
        String replace = listString.replace("[", "");
        String replace1 = replace.replace("]", "");
        return new ArrayList<String>(Arrays.asList(replace1.split(",")));
    }
    
    /**
	 * 将字符串数组拼装成用","隔开的字符串,
	 * 
	 * @param str
	 *            需要拼装的数组
	 * @return 需要返回的字符串
	 * 
	 * 
	 */
	public static String getLinkString(String[] array) {
		String str = "";
		StringBuilder sb = new StringBuilder();
		if (null != array && array.length > 0) {
			for (String s : array) {
				sb.append(s).append(",");
			}
			str = sb.toString();
		}
		if (str.indexOf(",") != -1) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
}
