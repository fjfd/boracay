package com.hex.bigdata.udsp.im.converter.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by JunjieM on 2017-9-5.
 */
public class HiveDatasource extends JdbcDatasource {

    public HiveDatasource(Datasource datasource) {
        super(datasource);
    }

    @Override
    public String getDriverClass() {
        String value = getProperty("driver.class").getValue();
        if (StringUtils.isBlank(value))
            value = "org.apache.hive.jdbc.HiveDriver";
        return value;
    }
}
