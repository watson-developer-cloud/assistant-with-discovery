package com.time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeService {

  public static String format(Date date, TimeZone timezone) {
    SimpleDateFormat format = new SimpleDateFormat();
    format.setTimeZone(timezone);
    return format.format(date);
  }

  public String time(Date date, City city) {
    return time(date, city.timezone);
  }

  public String time(Date date, TimeZone timezone) {
    return TimeService.format(date, timezone);
  }

  public String time(Date date, String city) {
    return time(date, City.from(city));
  }

}
