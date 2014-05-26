package com.gs.biz.sina.dataprovider;

import com.lidroid.xutils.util.DBUtils;

/**
 * //http://finance.sina.com.cn/realstock/company/sh600638/jsvar.js //股票概况数据
 * //http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/
 * sz000750.phtml?year=2014&jidu=1 //历史交易数据 //http://hq.sinajs.cn/list=sh600638
 * //实时交易数据 //http://finance.sina.com.cn/iframe/hot_stock_list.js //热门股票
 * //http://finance.sina.com.cn/realstock/company/hotstock_daily_a.js //
 * //http:/
 * /hq.sinajs.cn/list=sh600638,s_sh000001,s_sh000300,s_sz399001,s_sz399106
 * ,s_sz395099 //
 * 
 * http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CirculateStockHolder/
 * stockid/600638.phtml ／／//流通大股东
 * http://stockhtm.finance.qq.com/sstock/quotpage/q/600638.htm#ltgd //流通大股东
 */
public class AllDataProvider {

	
	public static final DBUtils DBInstance = DBUtils.getInstance("lgd_gs.db");;

	public static void initStock(String... stockCodes) {

		if (stockCodes != null) {
			for (int i = 0; i < stockCodes.length; i++) {
				try {
					saveStockData(stockCodes[i]);
					Thread.sleep(3000);
				} catch (Exception e) {
				}
			}
		}
	}

	private static void saveStockData(String stockCode) {
		// 历史交易数据
		SinaHistoryDataParser.saveHistoryTradeData(stockCode, 2009, 1);

		// 保存实时数据
		SinaRealTimeDataParser.saveData(stockCode);

		// 概要数据，如流通总股数
		SinaStockSumaryDataParser.saveData(stockCode);

		// 大股东信息
		SinaStockBigHolderParser.saveDatas(stockCode);
	}

}
