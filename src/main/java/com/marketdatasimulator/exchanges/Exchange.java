package com.marketdatasimulator.exchanges;

import com.marketdatasimulator.Country;

import java.time.OffsetTime;
import java.util.Calendar;
import java.util.Date;

public class Exchange {

    /**
     * This is Exchange super Class which will create an Exchange for a security. Exchange class
     * is extended by specific exchange classes such as NYSE and NASDAQ classes.
     */

    private String name;
    private Country country;
    private String timezone;
    private OffsetTime openTime;
    private OffsetTime closeTime;

    public Exchange(String name, Country country, String timezone, OffsetTime openTime, OffsetTime closeTime) {
        this.name = name;
        this.country = country;
        this.timezone = timezone;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public OffsetTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(OffsetTime openTime) {
        this.openTime = openTime;
    }

    public OffsetTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(OffsetTime closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isExchangeOpen() {
        /**
         * This method is for checking if at the current time, exchange is open or not.
         * This method is used to determine whether we should be publishing market data for a security or not.
         */
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        OffsetTime currentTime = OffsetTime.now();

        if (dayOfWeek>1 && dayOfWeek<7) {
            if ((currentTime.compareTo(openTime)>0) && (currentTime.compareTo(closeTime)<0)) {
                return true;
            }
        }

        return false;
    }
}
