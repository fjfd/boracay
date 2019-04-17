package com.hex.bigdata.udsp.im.converter.impl.util;

import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.impl.model.Kafka1Datasource;
import com.hex.bigdata.udsp.im.converter.impl.model.Kafka1Model;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by JunjieM on 2018-11-28.
 */
public class Kafka1Util {
    private static Logger logger = LogManager.getLogger (Kafka1Util.class);

    public static List<String> buildRealtime(Kafka1Model model) {
        Map<String, Property> propertyMap = model.getSourceDatasource ().getPropertyMap ();
        propertyMap.put (ConsumerConfig.GROUP_ID_CONFIG,
                new Property (ConsumerConfig.GROUP_ID_CONFIG, model.getGroupId ()));
        Kafka1Datasource kafka1Datasource = new Kafka1Datasource (propertyMap);
        KafkaConsumer<String, String> consumer = null;
        List<String> list = null;
        try {
            consumer = getConsumer (kafka1Datasource);
            list = receive (consumer, model.getTopic (), Long.valueOf (model.getConsumerTimeoutMs ()));
        } finally {
            // 非自动提交offset且获取的数据大于0
            if ("false".equals (kafka1Datasource.getEnableAutoCommit ())
                    && list != null && list.size () > 0) {
                consumer.commitSync (); // 手动同步提交offset
            }
            close (consumer);
        }
        return list;
    }

    // ----------------------------------------------------Consumer-------------------------------------------------------------

    public static KafkaConsumer<String, String> getConsumer(Kafka1Datasource datasource) {
        return new KafkaConsumer<> (getConsumerConfig (datasource));
    }

    public static Properties getConsumerConfig(Kafka1Datasource datasource) {
        Properties props = new Properties ();
        List<Property> properties = datasource.getProperties ();
        for (Property property : properties) {
            props.put (property.getName (), property.getValue ());
        }
        return props;
    }

    public static List<String> receive(KafkaConsumer<String, String> consumer, String topic, long timeout) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (timeout <= 3000) {
            throw new RuntimeException ("kafka consumer timeout must greater than 3000 ms!");
        }
        List<String> list = new ArrayList<> ();
        consumer.subscribe (Collections.singletonList (topic));
        ConsumerRecords<String, String> crs = consumer.poll (timeout);
        if (crs == null || crs.isEmpty ()) {
            return list;
        }
        Iterator<ConsumerRecord<String, String>> iterator = crs.iterator ();
        while (iterator.hasNext ()) {
            list.add (iterator.next ().value ());
        }
        return list;
    }

    // ----------------------------------------------------Producer-------------------------------------------------------------

    public static KafkaProducer<String, String> getProducer(Kafka1Datasource datasource) {
        return new KafkaProducer<> (getProducerConfig (datasource));
    }

    public static Properties getProducerConfig(Kafka1Datasource datasource) {
        Properties props = new Properties ();
        List<Property> properties = datasource.getProperties ();
        for (Property property : properties) {
            props.put (property.getName (), property.getValue ());
        }
        return props;
    }

    public static void send(KafkaProducer<String, String> producer, String topic, String message) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (message == null) {
            throw new RuntimeException ("send message is null!");
        }
        producer.send (new ProducerRecord<String, String> (topic, message));
    }

    public static void send(KafkaProducer<String, String> producer, String topic, List<String> messages) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (messages == null || messages.isEmpty ()) {
            throw new RuntimeException ("send message can not be empty!");
        }
        for (String message : messages) {
            producer.send (new ProducerRecord<String, String> (topic, message));
        }
    }

    public static void close(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof KafkaProducer) {
                ((KafkaProducer) obj).close ();
            } else if (obj instanceof KafkaConsumer) {
                ((KafkaConsumer) obj).close ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
