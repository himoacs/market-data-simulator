package com.marketdatasimulator.exchanges;

import com.marketdatasimulator.Country;

import java.time.OffsetTime;
import java.time.ZoneOffset;

public class NYSE extends Exchange {

    /**
     * Class for NYSE exchange.
     */

    public NYSE() {
        super("NYSE", new Country("US"), "EST",
                OffsetTime.of(9,30,0,0, ZoneOffset.of("-4")),
                OffsetTime.of(16,0,0,0, ZoneOffset.of("-4")));
    }

}
