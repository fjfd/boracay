package com.hex.bigdata.udsp.olq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;


/**
 * Created by JunjieM on 2019-1-8.
 */
public class DslDatasource extends JdbcDatasource {

    public DslDatasource(List<Property> properties) {
        super (properties);
    }

    public DslDatasource(Map<String, Property> propertieMap) {
        super (propertieMap);
    }

    public DslDatasource(com.hex.bigdata.udsp.common.api.model.Datasource datasource) {
        super (datasource);
    }

    @Override
    public String getDriverClass() {
        String value = getProperty ("driver.class").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "com.hex.bigdata.udsp.jdbc.UdspDriver";
        }
        return value;
    }
}
