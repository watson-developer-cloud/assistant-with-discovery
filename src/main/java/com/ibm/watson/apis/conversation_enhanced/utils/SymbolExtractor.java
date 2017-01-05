package com.ibm.watson.apis.conversation_enhanced.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class SymbolExtractor {

	public String getSymbol(String sampleJSON) {
		ObjectMapper mapper = new ObjectMapper();
		StockQuote obj;
		try {
			String substringBetween = StringUtils.substringBetween(sampleJSON, "[", "]");
			obj = mapper.readValue(substringBetween, StockQuote.class);
		} catch (IOException e) {
			return null;
		}
		return obj.getSymbol();
	}
}

class StockQuote {
	String type;
	String symbol;

	public String getType() {
		return type;
	}

	public void setType(String stock) {
		this.type = stock;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}