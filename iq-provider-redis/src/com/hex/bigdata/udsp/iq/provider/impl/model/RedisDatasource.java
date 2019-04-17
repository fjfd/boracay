package com.hex.bigdata.udsp.iq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.iq.provider.model.IqDatasource;
import org.apache.commons.lang.StringUtils;

/**
 * Created by PC on 2017/6/28.
 */
public class RedisDatasource extends IqDatasource {

    public RedisDatasource(Datasource datasource) {
        super (datasource);
    }

    public String getIp() {
        String value = getProperty ("redis.connection.ip").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("redis.connection.ip不能为空");
        }
        return value;
    }


    public int getPort() {
        String value = getProperty ("redis.connection.port").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("redis.connection.port不能为空");
        }
        return Integer.valueOf (value);
    }

    public String getUserName() {
        return getProperty ("redis.connection.user").getValue ();

    }

    public int getMaxIdle() {
        String value = getProperty ("redis.max.idle").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("redis.max.idle不能为空");
        }
        return Integer.valueOf (value);
    }


    public int getMaxWait() {
        String value = getProperty ("redis.max.wait").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("redis.max.wait不能为空");
        }
        return Integer.valueOf (value);
    }

    public int getTimeOut() {
        String value = getProperty ("redis.max.timeOut").getValue ();
        if (StringUtils.isBlank (value)) {
            return 600000;
        }
        return Integer.valueOf (value);
    }

    public boolean isTestOnBrrow() {
        String value = getProperty ("redis.test.on.brrow").getValue ();
        if (StringUtils.isBlank (value)) {
            return false;
        }
        return Boolean.valueOf (value);
    }

    public int getMaxTotal() {
        String value = getProperty ("redis.max.total").getValue ();
        if (StringUtils.isBlank (value)) {
            return 10;
        }
        return Integer.valueOf (value);
    }

    public String getPassword() {
        return getProperty ("redis.connection.password").getValue ();
    }

    public String getSeprator() {
        String value = getProperty ("redis.seprator").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "\\007";
        }
        return value;
    }
}
