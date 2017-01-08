package com.ibm.watson.apis.conversation_enhanced.filters;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class LookUp {
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

	private String sendGetRequest(String url) throws HttpException, IOException {
		HttpMethod httpMethod = getHttpMethod(url);
		try {
			getHttpClient().executeMethod(httpMethod);
			return httpMethod.getResponseBodyAsString();
		} finally {
			httpMethod.releaseConnection();
		}

	}

	public String lookUpQuickQuote(String stockSymbol) throws HttpException, IOException {
		String url = "http://ws.cdyne.com/delayedstockquote/delayedstockquote.asmx/GetQuickQuote?StockSymbol="
				+ stockSymbol + "&LicenseKey=0";
		return StringUtils.substringBetween(sendGetRequest(url), "<decimal xmlns=\"http://ws.cdyne.com/\">",
				"</decimal>");
	}

	public String lookUpWeather(String city, String country) throws HttpException, IOException {
		String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID=d2168ab7520e1bcb8174223e4584d095";
		String result = sendGetRequest(url);
//		System.out.println("sending "+url);
//		System.out.println("getting "+result);
		
		return result;
	}
	
}
