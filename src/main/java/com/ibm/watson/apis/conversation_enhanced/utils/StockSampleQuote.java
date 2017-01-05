package com.ibm.watson.apis.conversation_enhanced.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class StockSampleQuote {
	private HttpClient getHttpClient() {
		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		return client;
	}

	private HttpMethod getHttpMethod(String url) {
		HttpMethod method = new GetMethod(url);
		method.setFollowRedirects(true);
		return method;
	}

	private String getQuickQuoteResponse(String stockSymbol) throws HttpException, IOException {
		String url = "http://ws.cdyne.com/delayedstockquote/delayedstockquote.asmx/GetQuickQuote?StockSymbol="
				+ stockSymbol + "&LicenseKey=0";

		HttpMethod httpMethod = getHttpMethod(url);
		try {
			getHttpClient().executeMethod(httpMethod);
			return httpMethod.getResponseBodyAsString();
		} finally {
			httpMethod.releaseConnection();
		}

	}

	public String getQuickQuote(String stockSymbol) throws HttpException, IOException {
		return StringUtils.substringBetween(getQuickQuoteResponse(stockSymbol), "<decimal xmlns=\"http://ws.cdyne.com/\">",
				"</decimal>");
	}

}
