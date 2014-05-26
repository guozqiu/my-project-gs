package com.gs.biz.sina.dataprovider;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

import com.gs.biz.classCreator.SinaHistoryTradeDataModelCreator;
import com.gs.biz.common.bean.HistoryTradeData;
import com.gs.common.exception.ClassCreateFailException;
import com.gs.common.util.DateUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;

public class SinaHistoryDataParser {

	private static final String HISTORY_DATA_URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%d&jidu=%d";

	
	public static void saveHistoryTradeData(String stockCode, int fromYear, int fromSeason) {
		if(fromSeason>4 || fromSeason<1){
			throw new InvalidParameterException("季节必须是从1到4之间的数字");
		}
		
		
		Date now = new Date();
		int year = Integer.valueOf(DateUtils.format("yyyy", now));
		int month = Integer.valueOf(DateUtils.format("MM", now));
		int season = (month-1) / 3 + 1;
		
		List<HistoryTradeData> results = new ArrayList<HistoryTradeData>();
		while(true){
			if(fromYear>year || (fromYear==year && fromSeason>season)){
				break;
			}
			results.addAll(parseHistoryTradeData(stockCode, fromYear, fromSeason));
			if(fromSeason==4){
				fromSeason = 0;
				fromYear++;
			}else{
				fromSeason++;
			}
		}
		
		Class<? extends HistoryTradeData> dataClass = SinaHistoryTradeDataModelCreator.createClass(stockCode);
		AllDataProvider.DBInstance.createTableIfNotExist(dataClass);
		HistoryTradeData lastestData = AllDataProvider.DBInstance.findFirst(Selector.from(dataClass)
				.where(WhereBuilder.b("code", "=", stockCode)).orderBy("id", true));
		
		Collections.sort(results, new Comparator<HistoryTradeData>() {
			@Override
			public int compare(HistoryTradeData o1, HistoryTradeData o2) {
				return o1.date.after(o2.date)?1:-1;
			}
		});
		
		for (HistoryTradeData historyTradeData : results) {
			if(lastestData!=null){
				if(historyTradeData.date.after(lastestData.date)){
					AllDataProvider.DBInstance.save(historyTradeData);
				}
			}else{
				AllDataProvider.DBInstance.save(historyTradeData);
			}
		}
	}
	
	
	/**
	 * 提取最近5年交易数据
	 */
	private static List<HistoryTradeData> parseHistoryTradeData(String stockCode, int year, int season) {
		String url = null;
		try {
			
			String shortStockCode = stockCode;
			
			if(stockCode.startsWith("sh")){
				shortStockCode = stockCode.replace("sh", "");
			}else if(stockCode.startsWith("sz")){
				shortStockCode = stockCode.replace("sz", "");
			}
			
			url = String.format(HISTORY_DATA_URL, shortStockCode, year, season);
			Parser myParser = new Parser(url);

			// 设置编码
			myParser.setEncoding("GBK");
			// String filterStr = "table";//<table id="FundHoldSharesTable">
			NodeFilter filter = new HasAttributeFilter("id", "FundHoldSharesTable");
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			TableTag tabletag = (TableTag) nodeList.elementAt(0);

			return parseEveryDay(tabletag, stockCode);
			//System.out.println(tabletag.toHtml());

			//System.out.println("==============");
		} catch (Exception e) {
			//有些年份未上市
			return new ArrayList<HistoryTradeData>();
		}

	}

	private static List<HistoryTradeData> parseEveryDay(TableTag tabletag, String stockCode) {
		List<HistoryTradeData> results = new ArrayList<HistoryTradeData>();
		if(tabletag!=null){
			TableRow tableRow = tabletag.getRows()[0];
			String stockName = tableRow.toPlainTextString().trim();
			stockName = stockName.substring(0, stockName.indexOf("("));
			
			Class<? extends HistoryTradeData> dataClass = SinaHistoryTradeDataModelCreator.createClass(stockCode);
			
			TableRow[] rows = tabletag.getRows();
			for (int i = 2; i < rows.length; i++) {
				TableRow row = rows[i];
				HistoryTradeData data;
				try {
					data = dataClass.newInstance();
				} catch (Exception e) {
					throw new ClassCreateFailException(e);
				}
				data.code = stockCode;
				data.name = stockName;
				
				TableColumn[] columns = row.getColumns();
				int j = 0;
				data.date = DateUtils.parseDate(columns[j++].toPlainTextString().trim());
				data.startPrice = Float.valueOf(columns[j++].toPlainTextString().trim());
				data.topPrice = Float.valueOf(columns[j++].toPlainTextString().trim());
				data.endPrice = Float.valueOf(columns[j++].toPlainTextString().trim());
				data.bottomPrice = Float.valueOf(columns[j++].toPlainTextString().trim());
				data.volumes = Long.valueOf(columns[j++].toPlainTextString().trim());
				data.volumeMoney = Long.valueOf(columns[j++].toPlainTextString().trim());
				
				results.add(data);
			}
		}
		
		return results;
	}

//	public static void main(String[] args) {
//		List<HistoryTradeData> parseHistoryTradeData = parseHistoryTradeData("600611", 2007, 4);
//		for (HistoryTradeData historyTradeData : parseHistoryTradeData) {
//			System.out.println(historyTradeData.volumes);
//			System.out.println(historyTradeData.volumeMoney);
//		}
//	}
}
