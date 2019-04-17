package com.hex.bigdata.udsp.rts.executor.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by junjiem on 2017-2-20.
 */
public class KafkaProducerDatasource extends Datasource {

    public KafkaProducerDatasource(List<Property> propertyList) {
        super(propertyList);
    }

    public KafkaProducerDatasource(Map<String, Property> propertyMap) {
        super(propertyMap);
    }

    // Kafka集群的IP和端口地址，多个地址用逗号分隔
    public String getMetadataBrokerList() {
        return getProperty("metadata.broker.list").getValue();
    }

    // Value的序列化类
    public String getSerializerClass() {
        String value = getProperty("serializer.class").getValue();
        if (StringUtils.isBlank(value))
            value = "kafka.serializer.StringEncoder";
        return value;
    }

    // Key的序列化类
    public String getKeySerializerClass() {
        String value = getProperty("key.serializer.class").getValue();
        if (StringUtils.isBlank(value))
            value = "kafka.serializer.StringEncoder";
        return value;
    }

    // 请求确认模式
    public String getRequestRequiredAcks() {
        String value = getProperty("request.required.acks").getValue();
        if (StringUtils.isBlank(value))
            value = "0";
        return value;
    }
}
