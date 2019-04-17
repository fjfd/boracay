package com.hex.bigdata.udsp.iq.provider.impl.factory;

import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.iq.provider.impl.model.HBaseDatasource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

/**
 * Created by PC on 2018/8/17.
 */
public class HBaseAggregationClientPoolFactory {
    private GenericObjectPool pool;

    public HBaseAggregationClientPoolFactory(GenericObjectPool.Config config, HBaseDatasource datasource) {
        HBaseAggregationClientFactory factory = new HBaseAggregationClientFactory (datasource);
        pool = new GenericObjectPool (factory, config);
    }

    public AggregationClient getAggregationClient() throws Exception {
        return (AggregationClient) pool.borrowObject ();
    }

    public void releaseAggregationClient(AggregationClient client) {
        try {
            pool.returnObject (client);
        } catch (Exception e) {
            if (client != null) {
                try {
                    client.close ();
                } catch (IOException e1) {
                    e.printStackTrace ();
                }
            }
        }
    }

    public void closePool() {
        if (pool != null) {
            try {
                pool.close ();
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }
}

class HBaseAggregationClientFactory extends BasePoolableObjectFactory {

    private Configuration conf;

    public HBaseAggregationClientFactory(HBaseDatasource datasource) {
        conf = HBaseConfiguration.create ();
        for (Property property : datasource.getProperties ()) {
            conf.set (property.getName (), property.getValue ());
        }
        conf.set ("hbase.zookeeper.quorum", datasource.getZkQuorum ());
        conf.set ("hbase.zookeeper.property.clientPort", datasource.getZkPort ());
        if (StringUtils.isNotBlank (datasource.getRpcTimeout ()))
            conf.set ("hbase.rpc.timeout", datasource.getRpcTimeout ());
        if (StringUtils.isNotBlank (datasource.getClientRetriesNumber ()))
            conf.set ("hbase.client.retries.number", datasource.getClientRetriesNumber ());
        if (StringUtils.isNotBlank (datasource.getClientPause ()))
            conf.set ("hbase.client.pause", datasource.getClientPause ());
        if (StringUtils.isNotBlank (datasource.getZkRecoveryRetry ()))
            conf.set ("zookeeper.recovery.retry", datasource.getZkRecoveryRetry ());
        if (StringUtils.isNotBlank (datasource.getZkRecoveryRetryIntervalmill ()))
            conf.set ("zookeeper.recovery.retry.intervalmill", datasource.getZkRecoveryRetryIntervalmill ());
        if (StringUtils.isNotBlank (datasource.getClientOperationTimeout ()))
            conf.set ("hbase.client.operation.timeout", datasource.getClientOperationTimeout ());
//        if (StringUtils.isNotBlank(datasource.getRegionserverLeasePeriod()))
//            conf.set("hbase.regionserver.lease.period", datasource.getRegionserverLeasePeriod()); // 已被弃用
        if (StringUtils.isNotBlank (datasource.getClientScannerTimeoutPeriod ()))
            conf.set ("hbase.client.scanner.timeout.period", datasource.getClientScannerTimeoutPeriod ());
        if (StringUtils.isNotBlank (datasource.getHBaseRootdir ()))
            conf.set ("hbase.rootdir", datasource.getHBaseRootdir ());
        if (StringUtils.isNotBlank (datasource.getZookeeperZnodeParent ()))
            conf.set ("zookeeper.znode.parent", datasource.getZookeeperZnodeParent ());

        /*
        以下是HBase开启Kerberos认证后需要的配置
         */
        if (StringUtils.isNotBlank (datasource.getKerberosPrincipal ())
                && StringUtils.isNotBlank (datasource.getKerberosKeytab ())) {
            if (StringUtils.isNotBlank (datasource.getHbaseSecurityAuthentication ()))
                conf.set ("hbase.security.authentication", datasource.getHbaseSecurityAuthentication ());
            if (StringUtils.isNotBlank (datasource.getHadoopSecurityAuthentication ()))
                conf.set ("hadoop.security.authentication", datasource.getHadoopSecurityAuthentication ());
            if (StringUtils.isNotBlank (datasource.getHbaseMasterKerberosPrincipal ()))
                conf.set ("hbase.master.kerberos.principal", datasource.getHbaseMasterKerberosPrincipal ());
            if (StringUtils.isNotBlank (datasource.getHbaseRegionserverKerberosPrincipal ()))
                conf.set ("hbase.regionserver.kerberos.principal", datasource.getHbaseRegionserverKerberosPrincipal ());
            UserGroupInformation.setConfiguration (conf);
            try {
                UserGroupInformation.loginUserFromKeytab (datasource.getKerberosPrincipal (), datasource.getKerberosKeytab ());
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
        }
    }

    @Override
    public Object makeObject() throws Exception {
        return new AggregationClient (conf);
    }

    @Override
    public void destroyObject(Object obj) throws Exception {
        if (obj instanceof AggregationClient) {
            ((AggregationClient) obj).close ();
        }
    }

    @Override
    public boolean validateObject(Object obj) {
        if (obj instanceof AggregationClient) {
            return false;
        }
        return false;
    }
}
