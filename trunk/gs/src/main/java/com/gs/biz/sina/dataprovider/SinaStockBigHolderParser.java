package com.gs.biz.sina.dataprovider;

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

import com.gs.biz.common.bean.StockBigHolder;
import com.gs.common.exception.StockHistoryDataParseException;
import com.gs.common.util.DateUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;

public class SinaStockBigHolderParser {

	private static final String DATA_URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CirculateStockHolder/stockid/%s.phtml";

	
	public static void saveDatas(String stockCode) {
		List<StockBigHolder> results = doParse(stockCode);
		
		AllDataProvider.DBInstance.createTableIfNotExist(StockBigHolder.class);
		
		
		Collections.sort(results, new Comparator<StockBigHolder>() {
			@Override
			public int compare(StockBigHolder o1, StockBigHolder o2) {
				return o1.date.after(o2.date)?1:-1;
			}
		});
		
		StockBigHolder lastestData = AllDataProvider.DBInstance.findFirst(Selector.from(StockBigHolder.class)
				.where(WhereBuilder.b("code", "=", stockCode)).orderBy("date", true));
		
		for (StockBigHolder data : results) {
			if(lastestData!=null){
				if(data.date.after(lastestData.date)){
					AllDataProvider.DBInstance.save(data);
				}
			}else{
				AllDataProvider.DBInstance.save(data);
			}
		}
	}
	
	
	/**
	 * 提取最近5年交易数据
	 */
	private static List<StockBigHolder> doParse(String stockCode) {
		String url = null;
		try {
			
			String shortStockCode = stockCode;
			
			if(stockCode.startsWith("sh")){
				shortStockCode = stockCode.replace("sh", "");
			}else if(stockCode.startsWith("sz")){
				shortStockCode = stockCode.replace("sz", "");
			}
			
			url = String.format(DATA_URL, shortStockCode);
			Parser myParser = new Parser(url);

			// 设置编码
			myParser.setEncoding("GBK");
			// String filterStr = "table";//<table id="CirculateShareholderTable">
			NodeFilter filter = new HasAttributeFilter("id", "CirculateShareholderTable");
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			TableTag tabletag = (TableTag) nodeList.elementAt(0);

			return parseRows(tabletag, stockCode);
			//System.out.println(tabletag.toHtml());

			//System.out.println("==============");
		} catch (Exception e) {
			throw new StockHistoryDataParseException(url,e);
		}

	}

	private static List<StockBigHolder> parseRows(TableTag tabletag, String stockCode) {
		List<StockBigHolder> results = new ArrayList<StockBigHolder>();
		if(tabletag!=null){
			TableRow tableRow = tabletag.getRows()[0];
			String stockName = tableRow.toPlainTextString().trim();
			stockName = stockName.substring(0, stockName.indexOf("("));
			
			Date date = null;
			TableRow[] rows = tabletag.getRows();
			for (int i = 1; i < rows.length; i++) {
				TableRow row = rows[i];
				TableColumn[] columns = row.getColumns();
				if(columns.length==2 && columns[0].toPlainTextString().trim().equals("截止日期")){
					date = DateUtils.parseDate(columns[1].toPlainTextString().trim());
				}else if(columns.length==5 && !columns[0].toPlainTextString().trim().equals("编号")){
					StockBigHolder data = new StockBigHolder();
					data.code = stockCode;
					data.date = date;
					
					int j = 0;
					data.paiMing = Integer.valueOf(columns[j++].toPlainTextString().trim());
					data.holderName = columns[j++].toPlainTextString().trim();
					data.stocks = Long.valueOf(columns[j++].toPlainTextString().trim());
					data.stockPercent = Float.valueOf(columns[j++].toPlainTextString().trim());
					data.type = columns[j++].toPlainTextString().trim();
					results.add(data);
				}
			}
		}
		
		return results;
	}

	public static void main(String[] args) {
		List<StockBigHolder> parseHistoryTradeData = doParse("600611");
		for (StockBigHolder historyTradeData : parseHistoryTradeData) {
			System.out.println(historyTradeData.code);
			System.out.println(historyTradeData.holderName);
		}
	}
}
