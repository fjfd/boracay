package com.hex.bigdata.udsp.olq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2018/1/11.
 */
public class JdbcDatasource extends Datasource {

    public JdbcDatasource(List<Property> properties) {
        super (properties);
    }

    public JdbcDatasource(Map<String, Property> propertieMap) {
        super (propertieMap);
    }

    public JdbcDatasource(Datasource datasource) {
        super (datasource);
    }

    public String getDriverClass() {
        String value = getProperty ("driver.class").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("driver.class不能为空");
        }
        return value;
    }

    public String getJdbcUrl() {
        String value = getProperty ("jdbc.url").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("jdbc.url不能为空");
        }
        return value;
    }

    public String getUsername() {
        return getProperty ("username").getValue ();
    }

    public String getPassword() {
        return getProperty ("password").getValue ();
    }

    public String getInitialSize() {
        return getProperty ("initial.size").getValue ();
    }

    public String getMinIdle() {
        return getProperty ("min.idle").getValue ();
    }

    public String getMaxIdle() {
        return getProperty ("max.idle").getValue ();
    }

    public String getMaxWait() {
        return getProperty ("max.wait").getValue ();
    }

    public String getMaxActive() {
        return getProperty ("max.active").getValue ();
    }

    public String getValidationQueryTimeout() {
        return getProperty ("validation.query.timeout").getValue ();
    }

    public String getTimeBetweenEvictionRunsMillis() {
        return getProperty ("time.between.eviction.runs.millis").getValue ();
    }

    public String getMinEvictableIdleTimeMillis() {
        return getProperty ("min.evictable.idle.time.millis").getValue ();
    }

    public String getValidationQuery() {
        return getProperty ("validation.query").getValue ();
    }

    public String getTestWhileIdle() {
        return getProperty ("test.while.idle").getValue ();
    }

    public String getTestOnBorrow() {
        return getProperty ("test.on.borrow").getValue ();
    }

    public String getTestOnReturn() {
        return getProperty ("test.on.return").getValue ();
    }

    public String getPoolPreparedStatements() {
        return getProperty ("pool.prepared.statements").getValue ();
    }

    public String getMaxOpenPreparedStatements() {
        return getProperty ("max.open.prepared.statements").getValue ();
    }

    public String getRemoveAbandoned() {
        return getProperty ("remove.abandoned").getValue ();
    }

    public String getRemoveAbandonedTimeout() {
        return getProperty ("remove.abandoned.timeout").getValue ();
    }

    public int getMaxSize() {
        String value = getProperty ("max.data.size").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "65535";
        }
        return Integer.valueOf (value);
    }

    public boolean getMaxSizeAlarm() {
        String value = getProperty ("max.data.size.alarm").getValue ();
        if (StringUtils.isBlank (value)) {
            return true;
        }
        return Boolean.valueOf (value);
    }
}
