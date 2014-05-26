package com.gs.common.exception;

public class StockHistoryDataParseException extends BusinessException {

	public StockHistoryDataParseException(String url,Throwable e) {
		super(url,e);
	}

	private static final long serialVersionUID = 1L;

}
