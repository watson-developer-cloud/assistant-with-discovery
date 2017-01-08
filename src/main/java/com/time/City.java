package com.time;

import java.util.TimeZone;

public enum City {
  TORONTO("est"),
  HONG_KONG("hkt"),
  SYDNEY("aedt"),
  TOKYO("jst"),
  NEW_YORK("est"),
  LONDON("gmt");

  public final TimeZone timezone;

  City(String code) {
    this.timezone = TimeZone.getTimeZone(code);
  }

  public static City from(String name) {
    return City.valueOf(name.toUpperCase().replaceAll(" ", "_"));
  }
}
