package com.hex.bigdata.udsp.im.converter.impl.model.modeling;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.model.Model;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
public class SolrModel extends Model {

    public SolrModel(Model model) {
        super(model);
    }

    public SolrModel(List<Property> properties, Datasource srcDatasource) {
        super(properties, srcDatasource);
    }

    public String getCollectionName() {
        String value = getProperty("solr.collection.name").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("solr.collection.name不能为空");
        return value;
    }

//    public boolean getViolenceQuery() {
//        String value = getProperty("violence.query").getValue();
//        if (StringUtils.isBlank(value))
//            value = "true";
//        return Boolean.valueOf(value);
//    }
}
