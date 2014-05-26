package com.gs.biz.sina.dataprovider;

import com.gs.biz.common.bean.StockSumaryData;
import com.gs.common.exception.UnknownException;
import com.gs.common.util.ExceptionUtils;
import com.gs.common.util.HttpClientUtils;
import com.gs.common.util.HttpClientUtils.ResultObject;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;

public class SinaStockSumaryDataParser {
	private static final String real_time_data_api = "http://finance.sina.com.cn/realstock/company/%s/jsvar.js";
	//var lta = 56116.3988;//流通A股,老数据保留var lastfive = 79.6661;//过去5个交易日平均每分钟成交量var flag = 1; //判断标志var totalcapital = 56116.3988; //总股本var currcapital = 56116.3988; //流通股本var curracapital = 0; //流通A股var currbcapital = 0; //流通B股var a_code = 'sh600638'; //流通A股代码var b_code = ''; //流通B股代码var papercode = 'sh600638'; //当前页面个股代码var exchangerate = 0; //汇率var fourQ_mgsy = 0.2269;//最近四个季度每股收益和var lastyear_mgsy = 0.3710;//前一年每股收益和var price_5_ago = 10.610;//5日前收盘价格var price_10_ago = 11.790;//10日前收盘价格var price_20_ago = 12.080;//20日前收盘价格var price_60_ago = 13.000;//60日前收盘价格var price_120_ago = 12.140;//120日前收盘价格var price_250_ago = 9.700;//250日前收盘价格var mgjzc = 5.8908;//最近报告的每股净资产var stock_state = 1;//个股状态（0:无该记录; 1:上市正常交易; 2:未上市; 3:退市）var trans_flag = 1;//是否显示涨跌停价（1:显示 0:不显示）var profit = 2.0802;//最近年度净利润var profit_four = 1.2739;//最近四个季度净利润var stockType = 'A'; //股票类型  A-A股 B-B股  I-指数var stockname = '新黄浦'; //股票名称var corr_hkstock = ''; //相关港股代码var corr_bdc = ''; //相关债券可转换债var corr_bde = 'sh122040'; //相关债券普通企业债/* BHPsnK7Cm94I1m1LT9oBbUxsAQI/tgPKy65jyFVorJxI+1EIO93Qt424Ixf9wBWPIGXcpKaSbXdJW/qND1DBRMwXtjHUVq5WkIPxRu8dYiHSMhK2rd+G4J8fJTsDMDuXXBGaU/JHe5/+DqKHxzt6MVAozqAWiOvIC008Tg== */

	private static StockSumaryData parseSumaryData(String stockCode) {
		String url = String.format(real_time_data_api, stockCode);
		try {
			ResultObject sendGet = HttpClientUtils.sendGet(url);
			if(sendGet.isOk()){
				return new StockSumaryData(sendGet.getContent(), stockCode);
			}
		} catch (Exception e) {
			throw new UnknownException(url, e);
		}
		return ExceptionUtils.throwE(url);
	}

	public static void saveData(String stockCode) {
		AllDataProvider.DBInstance.createTableIfNotExist(StockSumaryData.class);
		
		StockSumaryData realTimeData = parseSumaryData(stockCode);
		StockSumaryData lastestData = AllDataProvider.DBInstance.findFirst(Selector.from(StockSumaryData.class)
				.where(WhereBuilder.b("code", "=", realTimeData.code)));
		
		if(lastestData==null){
			AllDataProvider.DBInstance.save(realTimeData);
		}else{
			realTimeData.id = lastestData.id;
			AllDataProvider.DBInstance.update(realTimeData);
		}
	}
}
