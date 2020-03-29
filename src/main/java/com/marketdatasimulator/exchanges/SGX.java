package com.marketdatasimulator.exchanges;

import com.marketdatasimulator.Country;
import java.time.OffsetTime;
import java.time.ZoneOffset;

public class SGX extends Exchange {

    /**
     * Class for Singapore Exchange (SGX).
     * SGX closes for 1 hour from noon to 1pm which is not implemented in market-data-simulator for simplicity.
     */

    public SGX() {
        super("SGX", new Country("Singapore"), "SGT",
                OffsetTime.of(9, 00, 0, 0, ZoneOffset.of("+8")),
                OffsetTime.of(17, 00, 0, 0, ZoneOffset.of("+8")));
    }

}
