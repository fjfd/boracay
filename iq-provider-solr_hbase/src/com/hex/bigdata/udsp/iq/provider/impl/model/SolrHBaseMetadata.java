package com.hex.bigdata.udsp.iq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.iq.provider.model.Metadata;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2018/8/20.
 */
public class SolrHBaseMetadata extends Metadata {
    public SolrHBaseMetadata() {
    }

    public SolrHBaseMetadata(List<Property> properties) {
        super(properties);
    }

    public SolrHBaseMetadata(Map<String, Property> propertyMap) {
        super(propertyMap);
    }

    public String getSolrPrimaryKey() {
        String value = getProperty("solr.primary.key").getValue();
        if (StringUtils.isBlank(value)) {
            value = "id";
        }
        return value;
    }

    public String getFqDataType() {
        String value = getProperty("hbase.fq.data.type").getValue();
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            value = "dsv";
        }
        return value;
    }

    public String getDsvSeparator() {
        String value = getProperty("hbase.fq.dsv.separator").getValue();
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            value = "\\007";
        }
        return value;
    }

    public byte[] getFamilyName() {
        String value = getProperty("hbase.family.name").getValue();
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            value = "f";
        }
        return Bytes.toBytes(value);
    }

    public byte[] getQualifierName() {
        String value = getProperty("hbase.qualifier.name").getValue();
        if (org.apache.commons.lang3.StringUtils.isBlank(value)) {
            value = "q";
        }
        return Bytes.toBytes(value);
    }
}
