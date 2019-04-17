package com.hex.bigdata.udsp.iq.provider.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.iq.provider.model.IqDatasource;
import org.apache.commons.lang.StringUtils;

/**
 * Solr的数据源配置
 */
public class ELSearchDatasource extends IqDatasource {

    public ELSearchDatasource(Datasource datasource) {
        super (datasource);
    }

    public String getElasticsearchServers() {
        String value = getProperty ("elasticsearch.servers").getValue ();
        if (StringUtils.isBlank (value)) {
            throw new IllegalArgumentException ("elasticsearch.servers不能为空");
        }
        return value;
    }
}
