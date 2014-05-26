package com.gs.biz.sina.dataprovider;

import com.gs.biz.classCreator.SinaRealTimeDataModelCreator;
import com.gs.biz.common.bean.RealTimeData;
import com.gs.biz.sina.bean.SinaRealTimeData;
import com.gs.common.exception.UnknownException;
import com.gs.common.util.ExceptionUtils;
import com.gs.common.util.HttpClientUtils;
import com.gs.common.util.HttpClientUtils.ResultObject;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;

public class SinaRealTimeDataParser{
	private static final String real_time_data_api = "http://hq.sinajs.cn/list=%s";

	private static RealTimeData getRealTimeData(String stockCode) {
		String url = String.format(real_time_data_api, stockCode);
		try {
			ResultObject sendGet = HttpClientUtils.sendGet(url);
			if(sendGet.isOk()){
				Class<? extends SinaRealTimeData> dataClass = SinaRealTimeDataModelCreator.createClass(stockCode);
				
					return dataClass.newInstance().setRealTimeData(sendGet.getContent(), stockCode);
				
			}
		} catch (Exception e) {
			throw new UnknownException(url, e);
		}
		return ExceptionUtils.throwE(url);
	}

	public static void saveData(String stockCode) {
		RealTimeData realTimeData = getRealTimeData(stockCode);
		
		Class<? extends SinaRealTimeData> dataClass = SinaRealTimeDataModelCreator.createClass(stockCode);
		AllDataProvider.DBInstance.createTableIfNotExist(dataClass);
		
		RealTimeData lastestData = AllDataProvider.DBInstance.findFirst(Selector.from(dataClass)
				.where(WhereBuilder.b("time", "=", realTimeData.time)));
		
		if(lastestData==null){
			AllDataProvider.DBInstance.save(realTimeData);
		}else{
			realTimeData.id = lastestData.getId();
			AllDataProvider.DBInstance.update(realTimeData);
		}
	}

}
