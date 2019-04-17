package com.hex.bigdata.udsp.iq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.iq.provider.model.IqDatasource;
import org.apache.commons.lang.StringUtils;

/**
 * HBase的数据源配置
 */
public class HBaseDatasource extends IqDatasource {

    public HBaseDatasource(Datasource datasource) {
        super (datasource);
    }

    public String getZkQuorum() {
        String value = getProperty ("hbase.zk.quorum").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("hbase.zk.quorum不能为空");
        }
        return value;
    }

    public String getZkPort() {
        String value = getProperty ("hbase.zk.port").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "2181";
        }
        return value;
    }

    public String getRpcTimeout() {
        return getProperty ("hbase.rpc.timeout").getValue ();
    }

    public String getClientRetriesNumber() {
        return getProperty ("hbase.client.retries.number").getValue ();
    }

    public String getClientPause() {
        return getProperty ("hbase.client.pause").getValue ();
    }

    public String getZkRecoveryRetry() {
        return getProperty ("zookeeper.recovery.retry").getValue ();
    }

    public String getZkRecoveryRetryIntervalmill() {
        return getProperty ("zookeeper.recovery.retry.intervalmill").getValue ();
    }

    public String getClientOperationTimeout() {
        return getProperty ("hbase.client.operation.timeout").getValue ();
    }

    // 已被弃用
    @Deprecated
    public String getRegionserverLeasePeriod() {
        return getProperty ("hbase.regionserver.lease.period").getValue ();
    }

    public String getClientScannerTimeoutPeriod() {
        return getProperty ("hbase.client.scanner.timeout.period").getValue ();
    }

    public String getHbaseSecurityAuthentication() {
        return getProperty ("hbase.security.authentication").getValue ();
    }

    public String getHadoopSecurityAuthentication() {
        return getProperty ("hadoop.security.authentication").getValue ();
    }

    public String getHbaseMasterKerberosPrincipal() {
        return getProperty ("hbase.master.kerberos.principal").getValue ();
    }

    public String getHbaseRegionserverKerberosPrincipal() {
        return getProperty ("hbase.regionserver.kerberos.principal").getValue ();
    }

    public String getKerberosPrincipal() {
        return getProperty ("kerberos.principal").getValue ();
    }

    public String getKerberosKeytab() {
        return getProperty ("kerberos.keytab").getValue ();
    }

    public String getHBaseRootdir() {
        return getProperty ("hbase.rootdir").getValue ();
    }

    public String getZookeeperZnodeParent() {
        return getProperty ("zookeeper.znode.parent").getValue ();
    }
}
