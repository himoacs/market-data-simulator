# market-data-simulator

`market-data-simulator` is a lightweight application designed to connect to an event broker via JMS ([Solace PubSub+](https://solace.com/products/event-broker/software/), free) and publish sample market (pricing) data (L1 quotes and trades) for securities. It can be extremely difficult to get market data for free, especially real-time market data. And when you do get it, it is usually throttled for the free tier. `market-data-simulator` is meant to be used as a building block for interesting financial applications such as P&L calculator, TCA application, Portfolio analysis amongst others. 

## So, what does it do?
`market-data-simulator` publishes market data for securities in JSON format. Here are two published messages for `AAPL` and `IBM`:

    {
    "symbol":"AAPL",
    "askPrice":250.3121,
    "bidSize":630,
    "tradeSize":180,
    "exchange":"NASDAQ",
    "currency":"USD",
    "tradePrice":249.9996,
    "askSize":140,
    "bidPrice":249.6871,
    "timestamp":2020-03-23T09:32:10.610764-04:00
    }
    
    {
    "symbol":"IBM",
    "askPrice":101.0025,
    "bidSize":720,
    "tradeSize":490,
    "exchange":"NYSE",
    "currency":"USD",
    "tradePrice":100.5,
    "askSize":340,
    "bidPrice":99.9975,
    "timestamp":2020-03-23T09:32:09.609035-04:00
    }


The data is published to a topic of this structure:

    <assetClass>/<country>/<exchange>/<name>
In the example above, data would be published to:

    EQ/US/NASDAQ/AAPL

This specific topic hierarchy is used to take full advantages of Solace PubSub+'s rich hierarchial topics which provide strong wildcard support and advance filtering logic. You can read more about that [here](https://docs.solace.com/Best-Practices/Topic-Architecture-Best-Practices.htm#mc-main-content). 

## Configurations
There are two main configurations file:

 - src/main/resources/broker.yaml
 - src/main/resources/securities.yaml

`broker.yaml` contains connection properties for your event broker. You need to populate the necessary fields with your specific broker properties. Those fields are: `host`, `vpn`, `user`, and `password`. 

In my case, I am using free service on Solace Cloud which lets me quickly sping up a broker on AWS. Here are step-by-step [instructions](https://solace.com/cloud-learning/group_getting_started/ggs_signup.html) on how to create your own service and find connection details. This is what my sample `broker.yaml` file looks like:

    host: <unique_host_name>.messaging.solace.cloud:55555  
    vpn: <vpn_name>  
    user: <username> 
    pass: <password>

`securities.yaml` file contains useful information about securities for which you would like to generate sample market data. Usually, in companies, you would have a separate time which stores and maintains all this data for all the securities that your company is interested in. However, in our case, we need to provide this ourselves. For now, you need to provide: `exchange`, `assetClass`, `currency`, `lastTradePrice`, `lastAskPrice`, and `lastBidPrice`. 

`exchange`, `assetClass` and `currency` data is used to provide some context about the security. Which exchange does this security primarily trade on? Which asset class (EQ, FX etc) does it belong to? Which currency are the prices quoted in? Exchange information is used to link securities to exchanges and their corresponding market hours so that we only publish data when the markets are open. For example, US equities data will only be published from 09:30am to 04:00pm. In future, I would like to add support for other asset classes as well, which is why I added the `assetClass` property.

If you would like to add support for a new Exchange besides the two that are currently there by default (NYSE and NASDAQ), you would need to create a new class for each exchange (i.e. SGX.java) and invoke super `Exchange.java` class with the necessary information such as `name`, `country`, `timezone`, `openTime` and `closeTime`.  

`lastTradePrice`, `lastAskPrice`, and `lastBidPrice` are all used to provide baseline for random prices which will be generated. You can enter whatever values you like here but to be a bit realistic, it is recommended that you use last values for this fields. The code will generate random `askPrice` and `bidPrice` and a `tradePrice` that falls between those two values (no crossed markets here ). 

## Getting Started
So how do you get started with this code? Follow these simple steps:

 1. Clone the repo locally
 2. Spin up an instance of PubSub+ broker
	 - [Software broker via docker](https://docs.solace.com/Solace-SW-Broker-Set-Up/Docker-Containers/Set-Up-Docker-Container-Image.htm)
	 - [Cloud service via Solace Cloud](https://solace.com/cloud-learning/group_getting_started/ggs_signup.html) 
 3. Update `broker.yaml` with your connection settings
 4. [Optional] Update `securities.yaml` with the securities you want to publish sample data for and their corresponding last (trade/ask/bid) prices.
 5. Run `Main.java` and watch data flow. Note that data will only be published during market hours. 


