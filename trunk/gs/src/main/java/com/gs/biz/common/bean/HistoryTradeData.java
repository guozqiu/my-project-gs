package com.gs.biz.common.bean;

import com.gs.common.bean.BaseModel;

public class HistoryTradeData extends BaseModel{

	public String name;//股票名称
	public String code;//股票代码
	public java.util.Date date;
	public Float startPrice;
	public Float topPrice;
	public Float endPrice;
	public Float bottomPrice;
	public Long volumes;//成交量(股)
	public Long volumeMoney;////成交金额(元)
	
}
