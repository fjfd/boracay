package com.hex.bigdata.udsp;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.io.IOException;

/**
 * Created by JunjieM on 2018-11-21.
 */
public class HBaseKerberosTest {
    static {
        // 解决winutils.exe不存在的问题
        try {
            File workaround = new File(".");
            System.getProperties().put("hadoop.home.dir",
                    workaround.getAbsolutePath());
            new File("./bin").mkdirs();
            new File("./bin/winutils.exe").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.security.krb5.conf", "A:/kerberos/krb5.conf");

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "172.18.21.61");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.rpc.timeout", "5000");
        conf.set("hbase.client.retries.number", "3");
        conf.set("hbase.client.pause", "100");
        conf.set("zookeeper.recovery.retry", "3");
        conf.set("zookeeper.recovery.retry.intervalmill", "200");
        conf.set("hbase.client.operation.timeout", "30000");
        conf.set("hbase.client.scanner.timeout.period", "60000");
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hbase.master.kerberos.principal", "hbase/node1@BIGDATA.HEX.COM");
        conf.set("hbase.regionserver.kerberos.principal", "hbase/_HOST@BIGDATA.HEX.COM");

        try {
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("test@BIGDATA.HEX.COM", "A:/kerberos/test.keytab");

            HBaseAdmin admin = new HBaseAdmin(conf);
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                System.out.println(tableName.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
