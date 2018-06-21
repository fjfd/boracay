package com.hex.bigdata.udsp.im.converter.impl.model.datasource;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by JunjieM on 2017-9-5.
 */
public class HBaseDatasource extends Datasource {

    public HBaseDatasource(Datasource datasource) {
        super(datasource);
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
}
