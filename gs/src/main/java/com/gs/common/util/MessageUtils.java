package com.gs.common.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.gs.common.util.HttpClientUtils.ResultObject;

/**
 * 旺旺消息发送组件 发送消息失败，是否抛出异常，一般来说，不要阻断主流程
 * 
 * @author fuqu
 * 
 */

public class MessageUtils {

    protected static Log  logger                  = LogFactory.getLog(MessageUtils.class);

//    private static String WANG_WANG_URL           = "http://kelude.taobao.net/api/admin/notice/wangwang";

    /**
     * 调用淘宝服务发送消息 配置
     */
    private static String INOTIFY_SERVICE_URL     = "http://sc.alibaba-inc.com/Taobao.Facades.INotifyService/Notify";

    private static String INOTIFY_SERVICE_SOURCE  = "alipay_aqc";

    private static String INOTIFY_SERVICE_AUTHKEY = "DBB36877CB18F6CD446C11F63DE6B90B";

    private static String INOTIFY_TEMPLATE_KEY    = "app_aqc_wangWangTemplate";

    private static String INOTIFY_DEFAULT_FROM    = "aqcdaily";

    private static String INOTIFY_TITLE           = "AQC";

    private static String INOTIFY_WW_WAY          = "2";

    private static String INOTIFY_SMS_WAY         = "1";

    // private static final boolean IS_PRODUCT_MODE = ContextUtils.isProEnv();
    /*  public static ResultObject sendWangWangMessage(String nickName, String subject, String content) {
          return HttpClientUtils.sendPost(WANG_WANG_URL, "nick", nickName, "context", content,
              "subject", subject, "auth", "fd16ab45fb34fd43fcbdece68da893af");
      }*/

    public static void main(String[] args) throws Exception {
        sendWangWangMessage("lize0909", "测试旺旺消息2", "测试旺旺消息2");
    }

    /**
     * 
     * 发送旺旺
     * @param nickName  旺旺昵称
     * @param subject  旺旺主题
     * @param content  旺旺内容
     * @category  消息内容最长支持500个字符
     * @return
     */
    public static ResultObject sendWangWangMessage(String nickName, String subject, String content) {
        return sendMessage(nickName, subject, content, INOTIFY_WW_WAY);
    }

    /**
     * 发送短信
     * 
     * @param phone 手机号码
     * @param subject 短信主题
     * @param content 短信内容
     * @category  消息内容最长支持500个字符
     * 该功能 不建议使用,涉及短信费用使用问题
     * @return
     */
    public static ResultObject sendSMS(String phone, String subject, String content) {
        return sendMessage(phone, subject, content, INOTIFY_SMS_WAY);
    }

    /**
     * 发送消息
     * @param nickName 消息昵称
     * @param subject 消息主题
     * @param content 消息内容
     * @return
     */
    private static ResultObject sendMessage(String nickName, String subject, String content,
                                            String sendWay) {
        ResultObject resultObject = null;
        try {
            Map<String, Object> clientParams = new HashMap<String, Object>();
            clientParams.put("source", INOTIFY_SERVICE_SOURCE);
            // 消息格式模板templateKey 
            clientParams.put("templateKey", INOTIFY_TEMPLATE_KEY);
            // 消息发送方式;1=短信;2=旺旺;3=邮件;4=贸易通   
            clientParams.put("way", sendWay);
            // 消息接受者
            clientParams.put("to", nickName);

            // 消息发送者
            clientParams.put("from", INOTIFY_DEFAULT_FROM);
            // 设置旺旺消息内容
            Map<String, String> contentMap = new HashMap<String, String>();
            contentMap.put("content", content);
            contentMap.put("title", subject);
            clientParams.put("data", JSON.toJSONString(contentMap));

            // 设置旺旺消息标题
            Map<String, String> titleMap = new HashMap<String, String>();
            titleMap.put("AliWMainTitle", INOTIFY_TITLE);
            clientParams.put("parameters", JSON.toJSONString(titleMap));
            clientParams.put("authkey", INOTIFY_SERVICE_AUTHKEY);
            // 消息显示名
            clientParams.put("displayName", INOTIFY_TITLE);
            resultObject = HttpClientUtils.sendPost(INOTIFY_SERVICE_URL, clientParams);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            resultObject = new ResultObject(Arrays.asList(new String[]{e.getMessage()}), 404);
        }
        return resultObject;
    }

}
