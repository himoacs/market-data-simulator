package com.marketdatasimulator;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import com.solacesystems.jms.SupportedProperty;
import org.yaml.snakeyaml.Yaml;
import javax.jms.*;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class SampleConsumer {

    /**
     * This is a simply consumer which listens to a topic and prints out the received messages.
     * You can use this consumer to verify that the Main class is publishing market data.
     */

    public static void main(String args[]) throws Exception {

        // load broker properties file
        Yaml yamlBroker = new Yaml();
        InputStream inputStreamBroker = yamlBroker.getClass().getClassLoader().getResourceAsStream("broker.yaml");
        Map<String, Object> brokerConfig = yamlBroker.load(inputStreamBroker);

        // Connect to Solace PubSub+ broker using JMS
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost((String) brokerConfig.get("host"));
        connectionFactory.setVPN((String) brokerConfig.get("vpn"));
        connectionFactory.setUsername((String) brokerConfig.get("user"));
        connectionFactory.setPassword((String) brokerConfig.get("pass"));
        connectionFactory.setDynamicDurables(true);

        Connection connection = connectionFactory.createConnection();

        Session session = connection.createSession(false, SupportedProperty.SOL_CLIENT_ACKNOWLEDGE);

        // Set the topic you would like to subscribe to. You can use Solace's wildcards (* and >) here.
        final String TOPIC_NAME = "EQ/>";

        Topic topic = session.createTopic(TOPIC_NAME);

        // Latch used for synchronizing between threads
        final CountDownLatch latch = new CountDownLatch(1);

        // From the session, create a consumer for the destination.
        MessageConsumer messageConsumer = session.createConsumer(topic);

        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    if (message instanceof BytesMessage) {
                        BytesMessage bytesMessage = (BytesMessage) message;
                        byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                        bytesMessage.readBytes(data);
                        System.out.println(("Message received " + new String(data)));
                    } else {
                        System.out.println("Message of a different format received.");
                    }

                    message.acknowledge();

                    CountDownLatch latch = null;
                    latch.countDown(); // unblock the main thread
                } catch (JMSException ex) {
                    System.out.println("Error processing incoming message.");
                    ex.printStackTrace();
                }
            }
        });

        // Start receiving messages
        connection.start();
        System.out.println("Awaiting message...");

        // the main thread blocks at the next statement until a message received
        latch.await();

        connection.stop();

        // Close everything in the order reversed from the opening order
        messageConsumer.close();
        session.close();
        connection.close();

    }
}
