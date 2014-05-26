package com.gs.biz.common.bean;

import java.util.Date;

import com.gs.common.bean.BaseModel;

public class StockBigHolder extends BaseModel{
	
	public Integer paiMing;//排名
	public String holderName;//股东名称
	public Long stocks;//持股数量(股)
	public Float stockPercent;//持股比例(%)
	public String type;//股本性质
	public Date date;//公布日期
	public String code;//股票代码
	
	
	
	
	
	public Integer getPaiMing() {
		return paiMing;
	}
	public void setPaiMing(Integer paiMing) {
		this.paiMing = paiMing;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public Long getStocks() {
		return stocks;
	}
	public void setStocks(Long stocks) {
		this.stocks = stocks;
	}
	public Float getStockPercent() {
		return stockPercent;
	}
	public void setStockPercent(Float stockPercent) {
		this.stockPercent = stockPercent;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	

}
