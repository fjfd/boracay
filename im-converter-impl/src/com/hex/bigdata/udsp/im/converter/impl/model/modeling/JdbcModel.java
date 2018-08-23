package com.hex.bigdata.udsp.im.converter.impl.model.modeling;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.model.Model;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by JunjieM on 2017-9-6.
 */
public class JdbcModel extends Model {

    public JdbcModel(Model model) {
        super(model);
    }

    public JdbcModel(List<Property> properties, Datasource srcDatasource) {
        super(properties, srcDatasource);
    }

    public String getDatabaseName() {
        return getProperty("database.name").getValue();
    }

    public String getTableName() {
        return getProperty("table.name").getValue();
    }

    public String getSelectSql() {
        return getProperty("select.sql").getValue();
    }
}
