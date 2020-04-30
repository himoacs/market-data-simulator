package com.marketdatasimulator.exchanges;

import com.marketdatasimulator.Country;
import java.time.OffsetTime;
import java.time.ZoneOffset;

public class NASDAQ extends Exchange {

    /**
     * Class for NASDAQ exchange.
     */

    public NASDAQ() {
        super("NASDAQ", new Country("US"), "EST",
                OffsetTime.of(9, 30, 0, 0, ZoneOffset.of("-4")),
                OffsetTime.of(16, 0, 0, 0, ZoneOffset.of("-4")));
    }

}
