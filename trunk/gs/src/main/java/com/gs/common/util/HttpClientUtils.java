package com.gs.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.gs.common.exception.BusinessException;
import com.gs.common.exception.UnknownException;

/**
 * http请求发送
 * 
 * @author fuqu
 * 
 */
public class HttpClientUtils {

	public static final int HTTP_POST_TIMEOUT = 5000;
	
	public static final int HTTP_POST_TIMEOUT_LONG = 30 * 60 * 1000;
	
	private static final HttpClient	HTTP_CLIENT = createHttpClient();
	

	// 创建HttpClient对象
	private static HttpClient createHttpClient() {
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schReg.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(schReg, HTTP_POST_TIMEOUT, TimeUnit.SECONDS);

		return new DefaultHttpClient(conMgr, getHttpParams(HTTP_POST_TIMEOUT));
	}
	

	private static HttpEntity dynamickParamToEntity(Object... params) throws UnsupportedEncodingException {
		if (params == null) {
			return null;
		}
		
		//如果是map，转换成数组
		if(params.length==1 && params[0] instanceof Map){
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String, Object>) params[0];
			params = new Object[map.size()*2];
			int i = 0;
			for (Entry<String,Object> entry : map.entrySet()) {
				params[i++] = entry.getKey();
				params[i++] = entry.getValue();
			}
		}
		
		boolean hasFile = false;
		for (int i = 0; i < params.length; i++) {
			if(params[i]!=null && params[i] instanceof File){
				hasFile = true;
			}
		}
		
		if(hasFile){
			MultipartEntity formEntity = new MultipartEntity();
			for (int i = 0; i < params.length; i++) {
				String key = (String) params[i++];
				if (i >= params.length) {
					throw new BusinessException("参数不成对，参数个数为：" + params.length);
				}
				Object value = params[i];
				if (value != null) {
					if(value instanceof File){
						formEntity.addPart(key, new FileBody((File)value));
					}else{
						formEntity.addPart(key, new StringBody(value.toString(), Charset.forName(HTTP.UTF_8)));
					}
					
				}
			}
			return formEntity;
		}else{
			List<NameValuePair> postPara = new ArrayList<NameValuePair>();
			for (int i = 0; i < params.length; i++) {
				String key = (String) params[i++];
				if (i >= params.length) {
					throw new BusinessException("参数不成对，参数个数为：" + params.length);
				}
				Object value = params[i];
				if (value != null) {
					postPara.add(new BasicNameValuePair(key, value.toString()));
				}
			}
			return new UrlEncodedFormEntity(postPara, HTTP.UTF_8);
		}
		
	}

	/**
	 * 默认编码UTF-8
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static ResultObject sendPost(String url, Object... params) {
		try {
			return sendPost(url, HTTP_POST_TIMEOUT, dynamickParamToEntity(params));
		} catch (UnsupportedEncodingException e) {
			throw new UnknownException(e);
		}
	}
	
	
//	public static ResultObject sendPost(String url, int timeount, Map<String, Object> params) {
//		try {
//			return sendPost(url, timeount, dynamickParamToMap(params));
//		} catch (UnsupportedEncodingException e) {
//			throw new UnknownException(e);
//		}
//	}

	private static HttpParams getHttpParams(int timeout){
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		return params;
	}
	
	private static ResultObject sendPost(String url, int timeout, HttpEntity formEntity) {

		BufferedReader bufferedReader = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			
			
			httpPost.setParams(getHttpParams(timeout));

			httpPost.setEntity(formEntity);
			
			HttpResponse httpResponse = HTTP_CLIENT.execute(httpPost);

			InputStream inputStream = httpResponse.getEntity().getContent();

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, HTTP.UTF_8), 8192);

			List<String> results = new ArrayList<String>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				results.add(line);
			}

			return new ResultObject(results, httpResponse.getStatusLine().getStatusCode());

		} catch (Throwable e) {
			throw new UnknownException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Throwable e) {
				}
			}
			if (httpPost != null) {
				try {
					httpPost.abort();
				} catch (Throwable e1) {
				}
			}
		}
	}


	public static class ResultObject {
		final List<String>	content;
		final int		status;

		public ResultObject(List<String> content, int status) {
			this.content = content;
			this.status = status;
		}

		public List<String> getContent() {
			return content;
		}

		public int getStatus() {
			return status;
		}

		@Override
		public String toString() {
			return "ResultObject [status=" + status + ", content=" + content + "]";
		}

		public boolean isOk() {
			return status == 200;
		}
	}
	
	/**
	 * 发送 Get 请求.
	 */
	public static ResultObject sendGet(String url) {
		return sendGet(url, (Map<String, Object>)null);
	}
	
	public static ResultObject sendGet(String url, Object... params) {
		if(params==null){
			return sendGet(url, HTTP_POST_TIMEOUT);
		}else{
			Map<String,Object> map = new HashMap<String, Object>();
			for (int i = 0; i < params.length; i++) {
				map.put(params[i++].toString(), params[i]);
			}
			
			return sendGet(url, map);
		}
	}
	

	/**
	 * 发送 Get 请求.
	 */
	private static ResultObject sendGet(String url, Map<String, Object> paramMap) {
		StringBuilder b = new StringBuilder(url);
		if (paramMap != null) {

			b.append("?");
			Iterator<Entry<String, Object>> it = paramMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> next = it.next();
				b.append(next.getKey()).append("=").append(next.getValue()).append("&");
			}
		}
		return sendGet(b.toString(),HTTP_POST_TIMEOUT);
	}
	
	private static ResultObject sendGet(String url, int timeout) {
		BufferedReader bufferedReader = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet();

			httpGet.setURI(new URI(url));
			httpGet.setParams(getHttpParams(timeout));

			HttpResponse res = HTTP_CLIENT.execute(httpGet);

			String charset = getCharsetFromResponse(res);

			InputStream inputStream = res.getEntity().getContent();

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream,charset), 8192);

			List<String> results = new ArrayList<String>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				results.add(line);
			}

			return new ResultObject(results, res.getStatusLine().getStatusCode());

		} catch (Throwable e) {
			throw new UnknownException(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Throwable e) {
				}
			}
			if (httpGet != null) {
				try {
					httpGet.abort();
				} catch (Throwable e1) {
				}
			}
		}
	}
	

	/**
	 * 从Response Header 中获取编码.
	 * @param res
	 * @return
	 */
	private static String getCharsetFromResponse(HttpResponse res) {
		Header[] headers = res.getHeaders("Content-Type");
		if (headers != null) {
			for (Header h : headers) {
				if (h.getName().equals("Content-Type") && h.getValue().contains("charset=")) {
					// "charset=".leangth == 8
					return h.getValue().substring(h.getValue().indexOf("charset=") + 8);
				}
			}
		}
		// default UTF-8
		return "GBK";
	}
	
	
	

}
