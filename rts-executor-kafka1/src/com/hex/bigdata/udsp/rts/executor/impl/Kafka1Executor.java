package com.hex.bigdata.udsp.rts.executor.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.rts.executor.Executor;
import com.hex.bigdata.udsp.rts.executor.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * 0.9版本及之后的kafka，支持Kerberos安全认证
 */
//@Component("com.hex.bigdata.udsp.rts.executor.impl.Kafka1Executor")
public class Kafka1Executor implements Executor {

    private static Logger logger = LogManager.getLogger (Kafka1Executor.class);

    //-------------------------------------------Producer---------------------------------------------

    private Properties getProducerConfig(Kafka1ProducerDatasource datasource) {
        Properties props = new Properties ();
        List<Property> properties = datasource.getProperties ();
        for (Property property : properties) {
            props.put (property.getName (), property.getValue ());
        }
        return props;
    }

    /**
     * 生产数据
     *
     * @param producerRequest
     * @return
     */
    @Override
    public ProducerResponse push(ProducerRequest producerRequest) {
        long bef = System.currentTimeMillis ();
        ProducerResponse producerResponse = new ProducerResponse ();
        producerResponse.setProducerRequest (producerRequest);
        Metadata producerMetadata = producerRequest.getApplication ().getMetadata ();
        Datasource datasource = producerMetadata.getDatasource ();
        Kafka1ProducerDatasource producerDatasource = new Kafka1ProducerDatasource (datasource.getProperties ());
        KafkaProducer<String, String> producer = null;
        try {
            producer = new KafkaProducer<> (getProducerConfig (producerDatasource));
            String topic = producerMetadata.getTopic ();
            List<Map<String, String>> dataMap = producerRequest.getMessageDatas ();
            List<String> messageList = new ArrayList<> ();
            for (Map<String, String> data : dataMap) {
                messageList.add (JSONUtil.parseObj2JSON (data));
            }
            send (producer, topic, messageList);
            producerResponse.setStatus (Status.SUCCESS);
            producerResponse.setStatusCode (StatusCode.SUCCESS);
        } catch (Exception e) {
            producerResponse.setStatus (Status.DEFEAT);
            producerResponse.setStatusCode (StatusCode.DEFEAT);
            producerResponse.setMessage (e.getMessage ());
            e.printStackTrace ();
        } finally {
            if (producer != null) {
                producer.close ();
            }
        }
        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        producerResponse.setConsumeTime (consumeTime);
        return producerResponse;
    }

    private void send(KafkaProducer<String, String> producer, String topic, String message) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (message == null) {
            throw new RuntimeException ("send message is null!");
        }
        producer.send (new ProducerRecord<String, String> (topic, message));
    }

    private void send(KafkaProducer<String, String> producer, String topic, List<String> messages) {
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

    private void send(KafkaProducer<String, String> producer, String topic, String key, String message) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (message == null) {
            throw new RuntimeException ("send message is null!");
        }
        if (key == null) {
            throw new RuntimeException ("send key is null!");
        }
        producer.send (new ProducerRecord<> (topic, key, message));
    }

    private void send(KafkaProducer<String, String> producer, String topic, Map<String, List<String>> messages) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (messages == null || messages.isEmpty ()) {
            throw new RuntimeException ("send messages can not be empty!");
        }
        for (Map.Entry<String, List<String>> entry : messages.entrySet ()) {
            String key = entry.getKey ();
            List<String> value = entry.getValue ();
            for (String message : value) {
                producer.send (new ProducerRecord<> (topic, key, message));
            }
        }
    }

    //-------------------------------------------Consumer---------------------------------------------

    private Properties getCnsumerConfig(Kafka1ConsumerDatasource datasource) {
        Properties props = new Properties ();
        List<Property> properties = datasource.getProperties ();
        for (Property property : properties) {
            props.put (property.getName (), property.getValue ());
        }
        return props;
    }

    /**
     * 消费数据
     *
     * @param consumerRequest
     * @return
     */
    @Override
    public ConsumerResponse pull(ConsumerRequest consumerRequest) {
        long bef = System.currentTimeMillis ();
        ConsumerResponse consumerResponse = new ConsumerResponse ();
        consumerResponse.setConsumerRequest (consumerRequest);
        Metadata metadata = consumerRequest.getApplication ().getMetadata ();
        String topic = metadata.getTopic ();
        Datasource datasource = metadata.getDatasource ();
//        Map<String, Property> propertyMap = datasource.getPropertyMap ();
//        propertyMap.put (ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG,
//                new Property (ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, Long.toString (consumerRequest.getTimeout ())));
//        Kafka1ConsumerDatasource consumerDatasource = new Kafka1ConsumerDatasource (propertyMap);
        Kafka1ConsumerDatasource consumerDatasource = new Kafka1ConsumerDatasource (datasource.getProperties ());
        KafkaConsumer<String, String> consumer = null;
        List<Map<String, String>> records = null;
        try {
            consumer = new KafkaConsumer<> (getCnsumerConfig (consumerDatasource));
            records = receive (consumer, topic, consumerRequest.getTimeout ());
            consumerResponse.setRecords (records);
            consumerResponse.setTotalCount (records.size ());
            consumerResponse.setStatus (Status.SUCCESS);
            consumerResponse.setStatusCode (StatusCode.SUCCESS);
        } catch (Exception e) {
            consumerResponse.setStatus (Status.DEFEAT);
            consumerResponse.setStatusCode (StatusCode.DEFEAT);
            consumerResponse.setMessage (e.getMessage ());
            e.printStackTrace ();
        } finally {
            // 非自动提交offset且获取的数据大于0
            if ("false".equals (consumerDatasource.getEnableAutoCommit ())
                    && records != null && records.size () > 0) {
                consumer.commitSync (); // 手动同步提交offset
            }
            if (consumer != null) {
                consumer.close ();
            }
        }
        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        consumerResponse.setConsumeTime (consumeTime);
        return consumerResponse;
    }

    /*
     TODO 经测试consumer.poll(timeout)的超时时间必须大于3秒，否则无法消费到数据，尝试各种感觉是超时设置的参数都没有效果。
     */
    private List<Map<String, String>> receive(KafkaConsumer<String, String> consumer, String topic, long timeout) {
        if (StringUtils.isBlank (topic)) {
            throw new RuntimeException ("kafka topic can not be empty!");
        }
        if (timeout <= 3000) {
            throw new RuntimeException ("kafka consumer timeout must greater than 3000 ms!");
        }
        List<Map<String, String>> records = new ArrayList<> ();
        consumer.subscribe (Collections.singletonList (topic));
        long bef = System.currentTimeMillis ();
        ConsumerRecords<String, String> crs = consumer.poll (timeout);
        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        logger.info ("kafka consumer poll consume time ================================> " + consumeTime);
        if (crs == null || crs.isEmpty ()) {
            return records;
        }
        Map<String, Object> map = null;
        Map<String, String> result = null;
        Iterator<ConsumerRecord<String, String>> iterator = crs.iterator ();
        while (iterator.hasNext ()) {
            ConsumerRecord<String, String> cr = iterator.next ();
            String message = cr.value ();
            logger.debug ("kafka接收的信息为：" + message);
            try {
                map = JSONUtil.parseJSON2Map (message, String.class);
                result = new HashMap<> ();
                for (Map.Entry<String, Object> entry : map.entrySet ()) {
                    result.put (entry.getKey (), String.valueOf (entry.getValue ()));
                }
            } catch (Exception e) {
                logger.warn (e.getMessage ());
                continue;
            }
            records.add (result);
        }
        return records;
    }

    /**
     * 测试数据源
     *
     * @param datasource
     * @return
     */
    @Override
    public boolean testDatasource(Datasource datasource) {
        boolean canConnection = true;
        KafkaProducer<String, String> producer = null;
        try {
            List<Property> propertyList = datasource.getProperties ();
            Property prop = null;

            prop = new Property ();
            prop.setName (ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
            prop.setValue ("org.apache.kafka.common.serialization.StringSerializer");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
            prop.setValue ("org.apache.kafka.common.serialization.StringSerializer");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName (ProducerConfig.ACKS_CONFIG);
            prop.setValue ("0");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName (ProducerConfig.RETRIES_CONFIG);
            prop.setValue ("3");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName (ProducerConfig.RETRY_BACKOFF_MS_CONFIG);
            prop.setValue ("1000");
            propertyList.add (prop);

            Kafka1ProducerDatasource producerDatasource = new Kafka1ProducerDatasource (propertyList);
            producer = new KafkaProducer<> (getProducerConfig (producerDatasource));

            send (producer, "udsp-rts-ds-test", "udsp rts datasource test info");
        } catch (Exception e) {
            canConnection = false;
            e.printStackTrace ();
        } finally {
            if (producer != null) {
                producer.close ();
            }
        }
        return canConnection;
    }
}
