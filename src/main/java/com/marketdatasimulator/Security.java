package com.marketdatasimulator;

import java.util.List;

public class Security {

    /**
     * The Security class is meant to define generic securities and is extended by Stock class.
     */

    private String name;
    private Exchange exchange;
    private AssetClass assetClass;
    private float lastPrice;
    private float lastAskPrice;
    private float lastBidPrice;


    public Security() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public AssetClass getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(AssetClass assetClass) {
        this.assetClass = assetClass;
    }

    public float getLastTradePrice() {
        return lastPrice;
    }

    public void setLastTradePrice(float lastPrice) {
        this.lastPrice = lastPrice;
    }

    public float getLastAskPrice() {
        return lastAskPrice;
    }

    public void setLastAskPrice(float lastAskPrice) {
        this.lastAskPrice = lastAskPrice;
    }

    public float getLastBidPrice() {
        return lastBidPrice;
    }

    public void setLastBidPrice(float lastBidPrice) {
        this.lastBidPrice = lastBidPrice;
    }

    public List<Object> generatePriceAndSize() {
        return null;
    }

}
