package com.ibm.watson.apis.conversation_enhanced.filters;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.watson.apis.conversation_enhanced.utils.json.JSONObject;

public class WeatherLookupFilter implements Filter {

	@Override
	public String filter(String message) {
		WeatherParamData weatherParamData = getWeatherParamData(message);
		if (weatherParamData != null) {
			return lookup(weatherParamData.city, weatherParamData.country);
		}
		return null;
	}

	public String lookup(String city, String country) {
		try {
			String jsonWeatherReport = new LookUp().lookUpWeather(city, country);
			WeatherReport weatherReport = toWeatherReport(jsonWeatherReport);
			return weatherReport.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Well this is embarrassing... But I was unable to get you weather data at the moment.";
		}
	}

	private WeatherReport toWeatherReport(String jsonWeatherReport) {
		WeatherReport weatherReport = new WeatherReport();
		JSONObject weatherReportJSONObject = new JSONObject(jsonWeatherReport);

		weatherReport.temp = Math.round((weatherReportJSONObject.getJSONObject("main").getDouble("temp") - 273.0));
		weatherReport.description = ((JSONObject) weatherReportJSONObject.getJSONArray("weather").get(0))
				.getString("description");

		weatherReport.city = weatherReportJSONObject.getString("name");
		weatherReport.country = weatherReportJSONObject.getJSONObject("sys").getString("country");

		return weatherReport;

	}

	protected WeatherParamData getWeatherParamData(String sampleJSON) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(sampleJSON, WeatherParamData.class);
		} catch (IOException e) {
			return null;
		}
	}

}

class WeatherReport {
	String description;
	Long temp;
	String city;
	String country;

	public String toString() {
		return "Right in " + city + "," + country + " it is : " + description + " and " + temp + " degrees celsius";
	}

}

class WeatherParamData {
	String city;
	String country;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
