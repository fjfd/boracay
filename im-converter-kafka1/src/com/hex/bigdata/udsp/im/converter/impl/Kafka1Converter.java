package com.hex.bigdata.udsp.im.converter.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.common.util.ExceptionUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.im.converter.RealtimeSourceConverter;
import com.hex.bigdata.udsp.im.converter.impl.model.Kafka1Datasource;
import com.hex.bigdata.udsp.im.converter.impl.model.Kafka1Model;
import com.hex.bigdata.udsp.im.converter.impl.util.Kafka1Util;
import com.hex.bigdata.udsp.im.converter.impl.wrapper.Wrapper;
import com.hex.bigdata.udsp.im.converter.model.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 0.9版本及之后的kafka，支持Kerberos安全认证
 */
//@Component("com.hex.bigdata.udsp.im.converter.impl.Kafka1Converter")
public class Kafka1Converter extends Wrapper implements RealtimeSourceConverter {

    private static Logger logger = LogManager.getLogger (Kafka1Converter.class);
    private static final int CONSUMER_TIMEOUT_MS = 5000;
    private static final String DEFAULT_GROUP_ID = "udsp-group";
    private static final String TEST_TOPIC = "udsp-im-ds-test";
    private static final String TEST_MESSAGE = "udsp im datasource test info";

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

            Kafka1Datasource kafka1Datasource = new Kafka1Datasource (propertyList);
            producer = Kafka1Util.getProducer (kafka1Datasource);

            Kafka1Util.send (producer, TEST_TOPIC, TEST_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace ();
            canConnection = false;
        } finally {
            Kafka1Util.close (producer);
        }
        return canConnection;
    }

    @Override
    public List<MetadataCol> columnInfo(Model model) {
        List<MetadataCol> metadataCols = null;
        Datasource datasource = model.getSourceDatasource ();
        Kafka1Model kafka1Model = new Kafka1Model (model.getProperties (), model.getSourceDatasource ());
        Map<String, Property> propertyMap = datasource.getPropertyMap ();
        propertyMap.put (ConsumerConfig.GROUP_ID_CONFIG,
                new Property (ConsumerConfig.GROUP_ID_CONFIG, DEFAULT_GROUP_ID));
        Kafka1Datasource kafka1Datasource = new Kafka1Datasource (propertyMap);
        KafkaConsumer<String, String> consumer = null;
        try {
            consumer = Kafka1Util.getConsumer (kafka1Datasource);
            List<String> list = Kafka1Util.receive (consumer, kafka1Model.getTopic (), CONSUMER_TIMEOUT_MS);
            for (String message : list) {
                logger.debug ("KAFKA1接收的信息为：" + message);
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
        } catch (Exception e) {
            e.printStackTrace ();
            //logger.warn (ExceptionUtil.getMessage (e));
        } finally {
            Kafka1Util.close (consumer);
        }
        return metadataCols;
    }

    @Override
    protected void insertInto(Metadata metadata, List<ModelMapping> modelMappings, List<ValueColumn> valueColumns) throws Exception {
        try {
            throw new Exception ("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    protected void updateInsert(Metadata metadata, List<ModelMapping> modelMappings, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) throws Exception {
        try {
            throw new Exception ("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    protected void matchingUpdate(Metadata metadata, List<ModelMapping> modelMappings, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) throws Exception {
        try {
            throw new Exception ("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    protected List<String> getSelectColumns(List<ModelMapping> modelMappings, Metadata metadata) {
        try {
            throw new Exception ("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }

    @Override
    protected void emptyDatas(Metadata metadata) throws IOException, Exception {
        try {
            throw new Exception ("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
