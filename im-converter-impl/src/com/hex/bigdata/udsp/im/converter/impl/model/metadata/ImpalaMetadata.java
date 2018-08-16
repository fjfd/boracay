package com.hex.bigdata.udsp.im.converter.impl.model.metadata;

import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.model.Metadata;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2018/8/10.
 */
public class ImpalaMetadata extends JdbcMetadata {
    public ImpalaMetadata() {
    }

    public ImpalaMetadata(List<Property> properties) {
        super(properties);
    }

    public ImpalaMetadata(Map<String, Property> propertyMap) {
        super(propertyMap);
    }

    public ImpalaMetadata(Metadata metadata) {
        super(metadata);
    }
}
