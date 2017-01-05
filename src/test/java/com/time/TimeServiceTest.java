package com.time;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class TimeServiceTest {

  private TimeService timeService;
  private Date now;

  @Before
  public void setup() {
    this.timeService = new TimeService();
    now = new Date();
  }

  @Test
  public void timeByCity() {
    String expectedTime = TimeService.format(now, TimeZone.getTimeZone("est"));

    assertEquals(expectedTime, timeService.time(now, City.TORONTO));
  }

  @Test
  public void timeByTimezone() {
    String expectedTime = TimeService.format(now, TimeZone.getTimeZone("jst"));

    assertEquals(expectedTime, timeService.time(now, TimeZone.getTimeZone("jst")));
  }

  @Test
  public void cityTimeZoneMappedCorrectly() {
    assertEquals(
        timeService.time(now, City.SYDNEY),
        timeService.time(now, TimeZone.getTimeZone("aedt"))
    );
  }

  @Test
  public void timeByCityName() {
    assertEquals(
        timeService.time(now, "hong kong"),
        timeService.time(now, City.HONG_KONG)
    );
  }

}