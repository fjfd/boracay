package com.hex.bigdata.udsp.im.converter.impl.model.datasource;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by PC on 2018/8/10.
 */
public class ImpalaDatasource extends JdbcDatasource {
    public ImpalaDatasource(Datasource datasource) {
        super(datasource);
    }

    public String getDriverClass() {
        String value = getProperty("driver.class").getValue();
        if (StringUtils.isBlank(value))
            value = "com.cloudera.impala.jdbc41.Driver";
        return value;
    }
}
