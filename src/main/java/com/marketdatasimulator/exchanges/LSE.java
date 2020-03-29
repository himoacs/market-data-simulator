package com.marketdatasimulator.exchanges;

import com.marketdatasimulator.Country;
import java.time.OffsetTime;
import java.time.ZoneOffset;

public class LSE extends Exchange {

    /**
     * Class for London Stock Exchange (LSE).
     */

    public LSE() {
        super("LSE", new Country("UK"), "GMT",
                OffsetTime.of(8, 00, 0, 0, ZoneOffset.of("-0")),
                OffsetTime.of(16, 30, 0, 0, ZoneOffset.of("-0")));
    }

}