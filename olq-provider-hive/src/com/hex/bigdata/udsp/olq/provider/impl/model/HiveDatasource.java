package com.hex.bigdata.udsp.olq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2017/3/10.
 */
public class HiveDatasource extends JdbcDatasource {

    public HiveDatasource(List<Property> properties) {
        super (properties);
    }

    public HiveDatasource(Map<String, Property> propertieMap) {
        super (propertieMap);
    }

    public HiveDatasource(Datasource datasource) {
        super (datasource);
    }

    @Override
    public String getDriverClass() {
        String value = getProperty ("driver.class").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "org.apache.hive.jdbc.HiveDriver";
        }
        return value;
    }

}
