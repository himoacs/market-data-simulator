package com.marketdatasimulator;

import org.json.simple.JSONObject;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Stock extends Security {

    /**
     * Stock class extends generic Security class and defines an equity stock such as AAPL or IBM.
     */

    public Stock() {
        super();
    }

    @Override
    public List<Object> generatePriceAndSize() {

        /**
         * Method for generating random prices and sizes for trades and quotes
         * Once the different prices are generated, they are cached to be used
         * when we generate prices next time. This is to ensure we don't see spikes in prices.
         */

        // Generate a random number and use it as a threshold
        // The threshold can be positive or negative
        Random rand = new Random();
        float threshold = (float) (rand.nextInt(21) - 10)/800;

        float lastTradePrice = this.getLastTradePrice();
        float tradePrice = lastTradePrice+(lastTradePrice*threshold);
        this.setLastTradePrice(tradePrice);

        float askPrice = tradePrice+(tradePrice*Math.abs(threshold));
        float bidPrice = tradePrice-(tradePrice*Math.abs(threshold));
        this.setLastAskPrice(askPrice);
        this.setLastBidPrice(bidPrice);

        Random randSize = new Random();
        int tradeSize = randSize.nextInt(51)*10;
        int askSize = randSize.nextInt(81)*10;
        int bidSize = randSize.nextInt(81)*10;

        return Arrays.asList(tradePrice, tradeSize, askPrice, askSize, bidPrice, bidSize);
    }

    public String generateTopic() {

        /**
         * Method for generating topic  to which the message will be published.
         * The TOPIC structure we are using is:
         * <assetClass>/marketData/v1/<country>/<exchange>/<symbol>
         */

        String assetClass = this.getAssetClass().getName();
        String symbol = this.getSymbol();
        String exchange = this.getExchange().getName();
        String country = this.getExchange().getCountry().getName();

        return assetClass+"/marketData/v1/"+country+"/"+exchange+"/"+symbol;
    }

    public JSONObject generateMessage() {

        /**
         * This method is for generating the JSON payload which will be published
         * with all the prices and sizes.
         */

        List<Object> priceAndSize = this.generatePriceAndSize();

        float tradePrice = (float) priceAndSize.get(0);
        int tradeSize = (int) priceAndSize.get(1);
        float askPrice = (float) priceAndSize.get(2);
        int askSize = (int) priceAndSize.get(3);
        float bidPrice = (float) priceAndSize.get(4);
        int bidSize = (int) priceAndSize.get(5);
        OffsetDateTime currentDateTime = OffsetDateTime.now();

        JSONObject messageJSON = new JSONObject();
        messageJSON.put("timestamp", currentDateTime);
        messageJSON.put("symbol", this.getSymbol());
        messageJSON.put("tradePrice", tradePrice);
        messageJSON.put("tradeSize", tradeSize);
        messageJSON.put("askPrice", askPrice);
        messageJSON.put("askSize", askSize);
        messageJSON.put("bidPrice", bidPrice);
        messageJSON.put("bidSize", bidSize);
        messageJSON.put("exchange", this.getExchange().getName());
        messageJSON.put("currency", this.getCurrency());

        return messageJSON;
    }
}
