package com.hex.bigdata.udsp.olq.provider.impl;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.olq.provider.model.OlqQuerySql;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by junjiem on 2017-2-15.
 */
public class ImpalaProvider extends JdbcProvider {

    private static Logger logger = LogManager.getLogger(ImpalaProvider.class);

    @Override
    protected OlqQuerySql getPageSql(String sql, Page page) {
        OlqQuerySql olqQuerySql = new OlqQuerySql(sql);
        if (page == null || !sql.toUpperCase().contains("SELECT")) {
            return olqQuerySql;
        }
        // 分页sql组装
        int pageSize = page.getPageSize();
        int pageIndex = page.getPageIndex();
        pageIndex = (pageIndex == 0 ? 1 : pageIndex);
        String pageSql = null;
        // TODO 以下方式实际是错误的！Impala分页必须指定唯一字段集进行排序，否则分页结果不正确。
        if (pageIndex == 1) {
            pageSql = "SELECT * FROM (" + sql + ") UDSP_VIEW LIMIT " + pageSize;
        } else {
            Integer startRow = (pageIndex - 1) * pageSize;
            pageSql = "SELECT * FROM (" + sql + ") UDSP_VIEW ORDER BY 1 LIMIT " + pageSize + " OFFSET " + startRow;
        }
        olqQuerySql.setPageSql(pageSql);
        // 总记录数查询SQL组装
        String totalSql = "SELECT COUNT(1) FROM (" + sql + ") UDSP_VIEW";
        olqQuerySql.setTotalSql(totalSql);
        // page设置
        olqQuerySql.setPage(page);
        logger.debug("配置的源SQL:\n" + olqQuerySql.getOriginalSql());
        logger.debug("分页查询SQL:\n" + olqQuerySql.getPageSql());
        logger.debug("查询总数SQL:\n" + olqQuerySql.getTotalSql());
        return olqQuerySql;
    }
}
