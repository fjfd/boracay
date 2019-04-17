package com.hex.bigdata.udsp.iq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.iq.provider.model.IqDatasource;
import org.apache.commons.lang.StringUtils;

/**
 * Solr的数据源配置
 */
public class SolrDatasource extends IqDatasource {

    public SolrDatasource(Datasource datasource) {
        super (datasource);
    }

    public String getSolrServers() {
        String value = getProperty ("solr.servers").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("solr.servers不能为空");
        }
        return value;
    }

}
