package com.gs.biz.sina.bean;

import java.util.List;

import com.gs.biz.common.bean.RealTimeData;
import com.gs.common.util.DateUtils;

public class SinaRealTimeData extends RealTimeData {

	public SinaRealTimeData(){
	}

	// 接口：
	// http://hq.sinajs.cn/list=sh601006
	// 这个url会返回一串文本，例如：
	// var hq_str_sh601006="大秦铁路, 27.55, 27.25, 26.91, 27.55, 26.20, 26.91,
	// 26.92,
	// 22114263, 589824680, 4695, 26.91, 57590, 26.90, 14700, 26.89, 14300,
	// 26.88, 15100, 26.87, 3100, 26.92, 8900, 26.93, 14230, 26.94, 25150,
	// 26.95, 15220, 26.96, 2008-01-11, 15:05:32";
	// 这个字符串由许多数据拼接在一起，不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。
	// 0：”大秦铁路”，股票名字；
	// 1：”27.55″，今日开盘价；
	// 2：”27.25″，昨日收盘价；
	// 3：”26.91″，当前价格；
	// 4：”27.55″，今日最高价；
	// 5：”26.20″，今日最低价；
	// 6：”26.91″，竞买价，即“买一”报价；
	// 7：”26.92″，竞卖价，即“卖一”报价；
	// 8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
	// 9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
	// 10：”4695″，“买一”申请4695股，即47手；
	// 11：”26.91″，“买一”报价；
	// 12：”57590″，“买二”
	// 13：”26.90″，“买二”
	// 14：”14700″，“买三”
	// 15：”26.89″，“买三”
	// 16：”14300″，“买四”
	// 17：”26.88″，“买四”
	// 18：”15100″，“买五”
	// 19：”26.87″，“买五”
	// 20：”3100″，“卖一”申报3100股，即31手；
	// 21：”26.92″，“卖一”报价
	// (22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖四的情况”
	// 30：”2008-01-11″，日期；
	// 31：”15:05:32″，时间；
	
	public SinaRealTimeData setRealTimeData(List<String> resultData, String stockCode){
		this.code = stockCode;
		String data = resultData.toString().split("=")[1].replace("\"", "");
		String[] datas = data.split(",");
		int i = 0;
		name = datas[i++];
		todayKaiPanPrice = Float.valueOf(datas[i++]);
		yestodayShouPanPrice = Float.valueOf(datas[i++]);
		currentPrice = Float.valueOf(datas[i++]);
		todayMaxPrice = Float.valueOf(datas[i++]);
		todayMinPrice = Float.valueOf(datas[i++]);
		buyPrice = Float.valueOf(datas[i++]);
		sellPrice = Float.valueOf(datas[i++]);
		tradedStock = Long.valueOf(datas[i++]);//已成交股数
		tradedMoney = Double.valueOf(datas[i++]);//成交金额
		buyStock1 = Long.valueOf(datas[i++]);//买一 申请股数
		buyPrice1 = Float.valueOf(datas[i++]);
		
		buyStock2 = Long.valueOf(datas[i++]);
		buyPrice2 = Float.valueOf(datas[i++]);
		
		buyStock3 = Long.valueOf(datas[i++]);
		buyPrice3 = Float.valueOf(datas[i++]);
		
		buyStock4 = Long.valueOf(datas[i++]);
		buyPrice4 = Float.valueOf(datas[i++]);
		
		buyStock5 = Long.valueOf(datas[i++]);
		buyPrice5 = Float.valueOf(datas[i++]);
		
		sellStock1 = Long.valueOf(datas[i++]);
		sellPrice1 = Float.valueOf(datas[i++]);
		
		sellStock2 = Long.valueOf(datas[i++]);
		sellPrice2 = Float.valueOf(datas[i++]);
		
		sellStock3 = Long.valueOf(datas[i++]);
		sellPrice3 = Float.valueOf(datas[i++]);
		
		sellStock4 = Long.valueOf(datas[i++]);
		sellPrice4 = Float.valueOf(datas[i++]);
		
		sellStock5 = Long.valueOf(datas[i++]);
		sellPrice5 = Float.valueOf(datas[i++]);
		
		time = DateUtils.parse("yyyy-MM-ddHH:mm:ss", datas[i++]+datas[i++]);
		
		return this;
	}
	
}
