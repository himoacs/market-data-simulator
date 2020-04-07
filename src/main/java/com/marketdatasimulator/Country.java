package com.marketdatasimulator;

public class Country {

    /**
     * Class for defining the Country that an exchange belongs to.
     */

    private String name;

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
