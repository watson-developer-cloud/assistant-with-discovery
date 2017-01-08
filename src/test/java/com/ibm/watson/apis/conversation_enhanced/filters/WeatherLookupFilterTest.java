package com.ibm.watson.apis.conversation_enhanced.filters;

import static org.junit.Assert.*;

import org.junit.Test;

public class WeatherLookupFilterTest {

	private static final String CITY_LONDON_COUNTRY_CA = "{ \"city\": \"London\", \"country\": \"ca\" }";
	private static final String CITY_LONDON_COUNTRY_UK = "{ \"city\": \"London\", \"country\": \"uk\" }";
	private static final String CITY_WINDSOR_COUNTRY_CA = "{ \"city\": \"windsor\", \"country\": \"ca\" }";
	private static final String CITY_TORONTO_COUNTRY_CA = "{ \"city\": \"Toronto\", \"country\": \"ca\" }";
	private static final String CITY_MOSCOW_COUNTRY_RU = "{ \"city\": \"moscow\", \"country\": \"ru\" }";
	
	
	@Test
	public void testFilterAll() {
		System.out.println(new WeatherLookupFilter().filter(CITY_LONDON_COUNTRY_CA));
		System.out.println(new WeatherLookupFilter().filter(CITY_LONDON_COUNTRY_UK));
		System.out.println(new WeatherLookupFilter().filter(CITY_WINDSOR_COUNTRY_CA));
		System.out.println(new WeatherLookupFilter().filter(CITY_TORONTO_COUNTRY_CA));
		System.out.println(new WeatherLookupFilter().filter(CITY_MOSCOW_COUNTRY_RU));
		
	}
	
	@Test
	public void testFilter() {
		String observedWeather = new WeatherLookupFilter().filter(CITY_LONDON_COUNTRY_CA);
		System.out.println(observedWeather);
		assertNotNull(observedWeather);
	}

	@Test
	public void testGetWeatherParamData() {
		assertNull(new WeatherLookupFilter().getWeatherParamData("{ \"type\": \"type\", \"symbol\": \"ibm\" }"));
		WeatherParamData expectedWeatherParamData = new WeatherParamData();
		expectedWeatherParamData.city = "London";
		expectedWeatherParamData.country = "ca";
		assertEquals(expectedWeatherParamData, new WeatherLookupFilter().getWeatherParamData(CITY_LONDON_COUNTRY_CA));
	}

}
