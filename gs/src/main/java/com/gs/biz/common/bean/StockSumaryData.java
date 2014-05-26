package com.gs.biz.common.bean;

import java.util.List;

import com.gs.common.bean.BaseModel;

public class StockSumaryData extends BaseModel{
	public String code;
	public String name;
	public Double totalCap;//市值:59.09亿元
	public Double currCap;//流通:59.09 
	

	public Long totalCapital;//总股本 总股数
	public Long currCapital;//流通股数
	public Float currPrice;
	
//	var lta = 56116.3988;//流通A股,老数据保留
//	var lastfive = 108.4106;//过去5个交易日平均每分钟成交量
//	var flag = 1; //判断标志
//	var totalcapital = 56116.3988; //总股本
//	var currcapital = 56116.3988; //流通股本
//	var curracapital = 0; //流通A股
//	var currbcapital = 0; //流通B股
//	var a_code = 'sh600638'; //流通A股代码
//	var b_code = ''; //流通B股代码
//	var papercode = 'sh600638'; //当前页面个股代码
//	var exchangerate = 0; //汇率
//	var fourQ_mgsy = 0.2269;//最近四个季度每股收益和
//	var lastyear_mgsy = 0.3710;//前一年每股收益和
//	var price_5_ago = 10.950;//5日前收盘价格
//	var price_10_ago = 12.140;//10日前收盘价格
//	var price_20_ago = 12.030;//20日前收盘价格
//	var price_60_ago = 13.200;//60日前收盘价格
//	var price_120_ago = 12.130;//120日前收盘价格
//	var price_250_ago = 9.610;//250日前收盘价格
//	var mgjzc = 5.8908;//最近报告的每股净资产
//	var stock_state = 1;//个股状态（0:无该记录; 1:上市正常交易; 2:未上市; 3:退市）
//	var trans_flag = 1;//是否显示涨跌停价（1:显示 0:不显示）
//	var profit = 2.0802;//最近年度净利润
//	var profit_four = 1.2739;//最近四个季度净利润
//	var stockType = 'A'; //股票类型  A-A股 B-B股  I-指数
//	var stockname = '新黄浦'; //股票名称
//	var corr_hkstock = ''; //相关港股代码
//	var corr_bdc = ''; //相关债券可转换债
//	var corr_bde = 'sh122040'; //相关债券普通企业债
	public StockSumaryData(){
	}
	
	public StockSumaryData(List<String> datas, String stockCode){
		this.code = stockCode;
		for (String data : datas) {
			if(data!=null){
				if(data.contains("totalcapital")){
					totalCapital = getLong(data) * 10000;
				}else if(data.contains("currcapital")){
					currCapital = getLong(data) * 10000;
				}else if(data.contains("stockname")){
					name = getString(data);
				}
			}
		}
		
		//System.out.println(datas);
	}

	private String getString(String data) {
		data = data.substring(data.indexOf("=")+1);
		data = data.substring(0,data.indexOf(";")).replace("'", "").trim();
		return data;
	}

	private Long getLong(String data) {
		data = data.substring(data.indexOf("=")+1);
		data = data.substring(0,data.indexOf(";")).trim();
		return Float.valueOf(data).longValue();
	}
	
	

	public Double getTotalCap() {
		if(totalCapital!=null && currPrice!=null)
			return totalCap = Double.valueOf((totalCapital * currPrice));
		
		return null;
	}


	public Double getCurrCap() {
		if(currCapital!=null && currPrice!=null)
			return currCap = Double.valueOf((currCapital * currPrice));
		
		return null;
	}

	
}
