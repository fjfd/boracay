package com.hex.bigdata.udsp.rts.executor.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2018-11-27.
 */
public class Kafka1ProducerDatasource extends Datasource {

    public Kafka1ProducerDatasource(List<Property> propertyList) {
        super (propertyList);
    }

    public Kafka1ProducerDatasource(Map<String, Property> propertyMap) {
        super (propertyMap);
    }

    // Kafka集群的IP和端口地址，多个地址用逗号分隔
    public String getBootstrapServers() {
        String value = getProperty ("bootstrap.servers").getValue ();
        if (StringUtils.isBlank (value))
            throw new IllegalArgumentException ("bootstrap.servers不能为空");
        return value;
    }

    // Key的序列化类
    public String getKeySerializer() {
        String value = getProperty ("key.serializer").getValue ();
        if (StringUtils.isBlank (value))
            value = "org.apache.kafka.common.serialization.StringSerializer";
        return value;
    }

    // Value的序列化类
    public String getValueSerializer() {
        String value = getProperty ("value.serializer").getValue ();
        if (org.apache.commons.lang.StringUtils.isBlank (value))
            value = "org.apache.kafka.common.serialization.StringSerializer";
        return value;
    }

    // 请求确认模式，0、1、2、all
    public String getAcks() {
        return getProperty ("acks").getValue ();
    }

    // 失败重试次数
    public String getRetries() {
        return getProperty ("retries").getValue ();
    }

    // 失败重试间隔（毫秒）
    public String getRetryBackoffMs() {
        return getProperty ("retry.backoff.ms").getValue ();
    }

    // 安全协议
    public String getSecurityProtocol() {
        return getProperty ("security.protocol").getValue ();
    }

    // Kerberos服务名
    public String getSaslKerberosServiceName() {
        return getProperty ("sasl.kerberos.service.name").getValue ();
    }
}
