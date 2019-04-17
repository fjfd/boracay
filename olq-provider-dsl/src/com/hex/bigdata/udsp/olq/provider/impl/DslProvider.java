package com.hex.bigdata.udsp.olq.provider.impl;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.olq.provider.model.OlqQuerySql;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by JunjieM on 2019-1-8.
 */
public class DslProvider extends JdbcProvider {

    private static Logger logger = LogManager.getLogger(DslProvider.class);

    @Override
    protected OlqQuerySql getPageSql(String sql, Page page) {
        // 没有分页SQL
        return new OlqQuerySql(sql);
    }
}
