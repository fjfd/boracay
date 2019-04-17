package com.hex.bigdata.udsp.im.converter.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.model.Model;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JunjieM on 2018-11-28.
 */
public class Kafka1Model extends MqModel implements Serializable {
    public Kafka1Model() {
    }

    public Kafka1Model(Model model) {
        super(model);
    }

    public Kafka1Model(List<Property> properties, Datasource srcDatasource) {
        super(properties, srcDatasource);
    }

    // 消费组ID
    public String getGroupId() {
        String value = getProperty("group.id").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("group.id不能为空");
        return value;
    }
}
