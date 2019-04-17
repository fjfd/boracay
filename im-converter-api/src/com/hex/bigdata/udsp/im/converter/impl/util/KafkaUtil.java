package com.hex.bigdata.udsp.im.converter.impl.util;

import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.impl.model.KafkaDatasource;
import com.hex.bigdata.udsp.im.converter.impl.model.KafkaModel;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by JunjieM on 2017-9-15.
 */
public class KafkaUtil {
    private static Logger logger = LogManager.getLogger (KafkaUtil.class);

    public static List<String> buildRealtime(KafkaModel model) {
        Map<String, Property> propertyMap = model.getSourceDatasource ().getPropertyMap ();
        propertyMap.put ("consumer.timeout.ms",
                new Property ("consumer.timeout.ms", model.getConsumerTimeoutMs ()));
        propertyMap.put ("group.id",
                new Property ("group.id", model.getGroupId ()));
        KafkaDatasource kafkaDatasource = new KafkaDatasource (propertyMap);
        ConsumerConnector consumer = null;
        List<String> list = new ArrayList<> ();
        try {
            consumer = getConsumer (kafkaDatasource);
            int threadNum = kafkaDatasource.getThreadNum ();
            List<KafkaStream<byte[], byte[]>> streams = receive (consumer, model.getTopic (), threadNum);
            for (KafkaStream<byte[], byte[]> stream : streams) {
                ConsumerIterator<byte[], byte[]> iterator = stream.iterator ();
                while (iterator.hasNext ()) {
                    String message = new String (iterator.next ().message ());
                    list.add (message);
                }
            }
        } catch (ConsumerTimeoutException e) {
            logger.debug ("KAFKA消费超时！");
        } finally {
            close (consumer);
        }
        return list;
    }

    // ----------------------------------------------------Consumer-------------------------------------------------------------

    public static ConsumerConnector getConsumer(KafkaDatasource datasource) {
        return Consumer.createJavaConsumerConnector (getConsumerConfig (datasource));
    }

    public static ConsumerConfig getConsumerConfig(KafkaDatasource datasource) {
        Properties props = new Properties ();
        List<Property> properties = datasource.getProperties ();
        for (Property property : properties) {
            props.put (property.getName (), property.getValue ());
        }
        return new ConsumerConfig (props);
    }

    public static List<KafkaStream<byte[], byte[]>> receive(ConsumerConnector consumer, String topic, Integer threadNum) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        Map<String, Integer> topicCountMap = new HashMap<> ();
        // threadNum是线程数
        // 一次从topic中获取threadNum个KafkaStream（一个KafkaStream可产生一个迭代器）
        String topicName = topic;
        topicCountMap.put (topicName, threadNum);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams (topicCountMap);
        return consumerMap.get (topicName);
    }

    // ----------------------------------------------------Producer-------------------------------------------------------------

    public static Producer<String, String> getProducer(KafkaDatasource datasource) {
        return new Producer<> (getProducerConfig (datasource));
    }

    public static ProducerConfig getProducerConfig(KafkaDatasource datasource) {
        Properties props = new Properties ();
        List<Property> properties = datasource.getProperties ();
        for (Property property : properties) {
            props.put (property.getName (), property.getValue ());
        }
        return new ProducerConfig (props);
    }

    public static void send(Producer<String, String> producer, String topic, String message) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (message == null) {
            throw new RuntimeException ("send message is null!");
        }
        // 如果具有多个partitions,请使用new KeyedMessage(String topicName, K key, V value).
        producer.send (new KeyedMessage<String, String> (topic, message));
    }

    public static void send(Producer<String, String> producer, String topic, List<String> messages) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (messages == null || messages.isEmpty ()) {
            throw new RuntimeException ("send message can not be empty!");
        }
        List<KeyedMessage<String, String>> kms = new ArrayList<> ();
        for (String message : messages) {
            kms.add (new KeyedMessage<String, String> (topic, message));
        }
        producer.send (kms);
    }

    public static void close(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof Producer) {
                ((Producer) obj).close ();
            } else if (obj instanceof ConsumerConnector) {
                ((ConsumerConnector) obj).shutdown ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
