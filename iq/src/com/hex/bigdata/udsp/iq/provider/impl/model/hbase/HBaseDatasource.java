package com.hex.bigdata.udsp.iq.provider.impl.model.hbase;

import com.hex.bigdata.udsp.common.provider.model.Datasource;
import com.hex.bigdata.udsp.common.provider.model.Property;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.Map;

/**
 * HBase的数据源配置
 */
public class HBaseDatasource extends Datasource {

    public HBaseDatasource(List<Property> properties) {
        super(properties);
    }

    public HBaseDatasource(Map<String, Property> propertieMap) {
        super(propertieMap);
    }

    public String getZkQuorum() {
        String value = getProperty("hbase.zk.quorum").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("hbase.zk.quorum不能为空");
        return value;
    }

    public String getZkPort() {
        String value = getProperty("hbase.zk.port").getValue();
        if (StringUtils.isBlank(value))
            value = "2181";
        return value;
    }

    public String getRpcTimeout() {
        return getProperty("hbase.rpc.timeout").getValue();
    }

    public String getClientRetriesNumber() {
        return getProperty("hbase.client.retries.number").getValue();
    }

    public String getClientPause() {
        return getProperty("hbase.client.pause").getValue();
    }

    public String getZkRecoveryRetry() {
        return getProperty("zookeeper.recovery.retry").getValue();
    }

    public String getZkRecoveryRetryIntervalmill() {
        return getProperty("zookeeper.recovery.retry.intervalmill").getValue();
    }

    public String getClientOperationTimeout() {
        return getProperty("hbase.client.operation.timeout").getValue();
    }

    public String getRegionserverLeasePeriod() {
        return getProperty("hbase.regionserver.lease.period").getValue();
    }

    public String getClientScannerTimeoutPeriod() {
        return getProperty("hbase.client.scanner.timeout.period").getValue();
    }

    public String getFqDataType() {
        String value = getProperty("hbase.fq.data.type").getValue();
        if (StringUtils.isBlank(value)) {
            value = "dsv";
        }
        return value;
    }

    public String getDsvSeprator() {
        String value = getProperty("hbase.fq.dsv.seprator").getValue();
        if (StringUtils.isBlank(value)) {
            value = "\\007";
        }
        return value;
    }

    public byte[] getFamilyName() {
        String value = getProperty("hbase.family.name").getValue();
        if (StringUtils.isBlank(value)) {
            value = "f";
        }
        return Bytes.toBytes(value);
    }

    public byte[] getQulifierName() {
        String value = getProperty("hbase.qulifier.name").getValue();
        if (StringUtils.isBlank(value)) {
            value = "q";
        }
        return Bytes.toBytes(value);
    }

    public int getMaxNum() {
        String value = getProperty("hbase.max.data.size").getValue();
        if (StringUtils.isBlank(value)) {
            value = "65535";
        }
        return Integer.valueOf(value);
    }
}
