package com.marketdatasimulator;

/** Market Data Simulator by Himanshu Gupta
 *
 * Description: As you can tell by the name, this code will generate market data for some securities you define
 * in the securities.yaml file. To make the simulator as realistic as possible, each security is given lastTradePrice,
 * lastAskPrice, and lastSizePrice so we can generate random data from these initial data points. Each security also
 * has an associated exchange which as openTime and closeTime. Market Data Simulator will only publish data for
 * securities when there exchanges are open. For example, you will not receive market data for a US security after 4pm.
 *
 * Market data is published to a specific topic per security using Solace's rich topical hierarchy. They topic structure
 * is <assetClass>/<country>/<exchange>/<name>. For example, AAPL's market data will be published to: EQ/US/NASDAQ/AAPL
 * The payload is in JSON and contains both trade and quote data. Here is what a sample payload looks like:
 * {"symbol":"AAPL","askPrice":249.99023,"bidSize":290,"tradeSize":480,"exchange":"NASDAQ","currency":"USD",
 * "tradePrice":248.4375,"askSize":210,"bidPrice":246.88477,"timestamp":2020-03-20T13:26:30.733592-04:00}
 *
 */

import com.marketdatasimulator.exchanges.Exchange;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import org.yaml.snakeyaml.Yaml;
import javax.jms.*;
import java.io.InputStream;
import java.util.*;

public class Main {

    public static void main(String args[]) throws Exception {

        /**
         * We will first be loading 2 yaml config files: broker.yaml and securities.yaml
         * broker.yaml contains properties for your Solace PubSub+ broker
         * securities.yaml contains securities for which you would like to generate market data and some additional
         * details about them such as last (trade/quote) prices, exchange, and asset class.
         *
         * Once these configs have been loaded, we will create the securities and generate market data for them.
         *
         * Finally, we will publish market data for each security when their respective exchanges are open.
         *
         */

        // load broker properties file

        Yaml yamlBroker = new Yaml();
        InputStream inputStreamBroker = yamlBroker.getClass().getClassLoader().getResourceAsStream("broker.yaml");
        Map<String, Object> brokerConfig = yamlBroker.load(inputStreamBroker);

        //load securities properties file

        Yaml yamlSecurities = new Yaml();
        InputStream inputStreamSecurities = yamlSecurities.getClass().getClassLoader().getResourceAsStream("securities.yaml");
        Map<String, Object> securitiesConfig = yamlSecurities.load(inputStreamSecurities);

        // Create Stocks and set their properties
        Stock[] stocks = new Stock[securitiesConfig.keySet().size()];
        List<String> securities = new ArrayList<>(securitiesConfig.keySet());

        // For each security, create a Stock object and set its properties.
        for (int i=0; i<securitiesConfig.keySet().size();i++) {

            Map<String, Object> stockInfo = (Map<String, Object>) securitiesConfig.get(securities.get(i));

            stocks[i] = new Stock();
            stocks[i].setSymbol(securities.get(i));
            stocks[i].setName((String) stockInfo.get("name"));
            stocks[i].setLastTradePrice(Float.parseFloat((String) stockInfo.get("lastTradePrice")));
            stocks[i].setLastAskPrice(Float.parseFloat((String) stockInfo.get("lastAskPrice")));
            stocks[i].setLastBidPrice(Float.parseFloat((String) stockInfo.get("lastBidPrice")));
            stocks[i].setCurrency((String) stockInfo.get("currency"));

            // Dynamically instantiate specific exchange classes based on config
            String className = "com.marketdatasimulator.exchanges."+stockInfo.get("exchange");

            stocks[i].setExchange((Exchange) Class.forName(className).getConstructor().newInstance());
            stocks[i].setAssetClass(new AssetClass((String) stockInfo.get("assetClass")));

        }

        // Connect to Solace PubSub+ broker using JMS and send payload for each security when markets are open

        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost((String) brokerConfig.get("host"));
        connectionFactory.setVPN((String) brokerConfig.get("vpn"));
        connectionFactory.setUsername((String) brokerConfig.get("user"));
        connectionFactory.setPassword((String) brokerConfig.get("pass"));

        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        while(true) {
            for (int i = 0; i < stocks.length; i++) {

                // Check if exchange is open
                if (stocks[i].getExchange().isExchangeOpen()) {

                    // Get market data for this stock and convert it to JSON string
                    String textMessage = stocks[i].generateMessage().toJSONString();
                    TextMessage message = session.createTextMessage(textMessage);
                    System.out.println(textMessage);

                    // Generate topic for this security
                    String TOPIC_NAME = stocks[i].generateTopic();
                    System.out.println("Publishing to topic: " + TOPIC_NAME);
                    Topic topic = session.createTopic(TOPIC_NAME);

                    // Publish message to the topic
                    MessageProducer messageProducer = session.createProducer(topic);
                    messageProducer.send(topic, message, DeliveryMode.NON_PERSISTENT,
                            message.DEFAULT_PRIORITY, message.DEFAULT_TIME_TO_LIVE);


                }
                else {
                    System.out.println(stocks[i].getExchange().getName() + " is closed at this time!");
                }

            }

            Thread.sleep((1000));


        }


    }
}
