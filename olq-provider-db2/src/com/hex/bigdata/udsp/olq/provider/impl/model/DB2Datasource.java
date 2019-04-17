package com.hex.bigdata.udsp.olq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2017/3/10.
 */
public class DB2Datasource extends JdbcDatasource {

    public DB2Datasource(List<Property> properties) {
        super (properties);
    }

    public DB2Datasource(Map<String, Property> propertieMap) {
        super (propertieMap);
    }

    public DB2Datasource(Datasource datasource) {
        super (datasource);
    }

    @Override
    public String getDriverClass() {
        String value = getProperty ("driver.class").getValue ();
        if (StringUtils.isBlank (value)) {
            value = "com.ibm.db2.jcc.DB2Driver";
        }
        return value;
    }

}
