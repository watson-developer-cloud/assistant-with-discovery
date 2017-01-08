package com.ibm.watson.apis.conversation_enhanced.filters;

public class StockQuoteFilter implements Filter {

	@Override
	public String filter(String message) {
		String symbol = new SymbolExtractor().getSymbol(message);
		if (symbol != null) {
			return getQuickQuote(symbol);
		}
		return message;
	}

	private String getQuickQuote(String stockSymbol) {
		try {
			return "$"+ new LookUp().lookUpQuickQuote(stockSymbol);
		} catch (Exception e) {
			return "Something went wrong, quote is unavalable.";
		}
	}
}
