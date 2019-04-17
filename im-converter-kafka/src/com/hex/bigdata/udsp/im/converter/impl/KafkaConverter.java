package com.hex.bigdata.udsp.im.converter.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.common.util.ExceptionUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.im.converter.impl.model.KafkaDatasource;
import com.hex.bigdata.udsp.im.converter.impl.model.KafkaModel;
import com.hex.bigdata.udsp.im.converter.impl.util.KafkaUtil;
import com.hex.bigdata.udsp.im.converter.impl.wrapper.KafkaWrapper;
import com.hex.bigdata.udsp.im.converter.model.MetadataCol;
import com.hex.bigdata.udsp.im.converter.model.Model;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2017-9-4.
 */
//@Component("com.hex.bigdata.udsp.im.converter.impl.KafkaConverter")
public class KafkaConverter extends KafkaWrapper {
    private static Logger logger = LogManager.getLogger (KafkaConverter.class);
    private static final int CONSUMER_TIMEOUT_MS = 1000;
    private static final String DEFAULT_GROUP_ID = "udsp-group";
    private static final String TEST_TOPIC = "udsp-im-ds-test";
    private static final String TEST_MESSAGE = "udsp im datasource test info";

    @Override
    public List<MetadataCol> columnInfo(Model model) {
        List<MetadataCol> metadataCols = null;
        Datasource datasource = model.getSourceDatasource ();
        KafkaModel kafkaModel = new KafkaModel (model.getProperties (), model.getSourceDatasource ());
        String topic = kafkaModel.getTopic ();
        Map<String, Property> propertyMap = datasource.getPropertyMap ();
        propertyMap.put ("consumer.timeout.ms", new Property ("consumer.timeout.ms", Integer.toString (CONSUMER_TIMEOUT_MS)));
        propertyMap.put ("group.id", new Property ("group.id", DEFAULT_GROUP_ID));
        KafkaDatasource kafkaDatasource = new KafkaDatasource (propertyMap);
        ConsumerConnector consumer = null;
        try {
            consumer = KafkaUtil.getConsumer (kafkaDatasource);
            int threadNum = kafkaDatasource.getThreadNum ();
            List<KafkaStream<byte[], byte[]>> streams = KafkaUtil.receive (consumer, topic, threadNum);
            for (KafkaStream<byte[], byte[]> stream : streams) {
                ConsumerIterator<byte[], byte[]> iterator = stream.iterator ();
                while (iterator.hasNext ()) {
                    String message = new String (iterator.next ().message ());
                    logger.debug ("KAFKA接收的信息为：" + message);
                    try {
                        Map<String, Object> map = JSONUtil.parseJSON2Map (message);
                        metadataCols = new ArrayList<> ();
                        int count = 1;
                        for (Map.Entry<String, Object> entry : map.entrySet ()) {
                            MetadataCol metadataCol = new MetadataCol ();
                            String name = entry.getKey ();
                            Object value = entry.getValue ();
                            metadataCol.setSeq ((short) count++);
                            metadataCol.setName (name);
                            metadataCol.setType (javaTransDBType (value));
                            metadataCol.setDescribe (name);
                            metadataCol.setIndexed (false);
                            metadataCol.setPrimary (false);
                            metadataCol.setStored (true);
                            metadataCols.add (metadataCol);
                        }
                        return metadataCols;
                    } catch (Exception e) {
                        e.printStackTrace ();
                        //logger.warn (ExceptionUtil.getMessage (e));
                    }
                }
            }
        } catch (ConsumerTimeoutException e) {
            logger.debug ("KAFKA消费超时！");
            return metadataCols;
        } catch (Exception e) {
            e.printStackTrace ();
            //logger.warn (ExceptionUtil.getMessage (e));
        } finally {
            KafkaUtil.close (consumer);
        }
        return metadataCols;
    }

    @Override
    public boolean testDatasource(Datasource datasource) {
        boolean canConnection = true;
        Producer<String, String> producer = null;
        try {
            List<Property> propertyList = datasource.getProperties ();
            Property prop = null;

            prop = new Property ();
            prop.setName ("serializer.class");
            prop.setValue ("kafka.serializer.StringEncoder");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName ("key.serializer.class");
            prop.setValue ("kafka.serializer.StringEncoder");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName ("request.required.acks");
            prop.setValue ("0");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName ("message.send.max.retries");
            prop.setValue ("3");
            propertyList.add (prop);

            prop = new Property ();
            prop.setName ("retry.backoff.ms");
            prop.setValue ("1000");
            propertyList.add (prop);

            KafkaDatasource kafkaDatasource = new KafkaDatasource (propertyList);
            producer = KafkaUtil.getProducer (kafkaDatasource);

            KafkaUtil.send (producer, TEST_TOPIC, TEST_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace ();
            canConnection = false;
        } finally {
            KafkaUtil.close (producer);
        }
        return canConnection;
    }
}
