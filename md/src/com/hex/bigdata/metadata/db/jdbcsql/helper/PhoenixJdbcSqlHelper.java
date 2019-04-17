package com.hex.bigdata.metadata.db.jdbcsql.helper;

import com.hex.bigdata.metadata.db.jdbcsql.BaseJdbcSqlHelper;
import com.hex.bigdata.metadata.db.util.Constant;
import com.hex.bigdata.metadata.db.util.DBType;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;

/**
 * Created by junjiem on 2018-10-08.
 */
public class PhoenixJdbcSqlHelper extends BaseJdbcSqlHelper {

    public PhoenixJdbcSqlHelper(Connection conn) {
        super(conn);
    }

    @Override
    public String getCurrentDbNameSql() {
        return null;
    }

    @Override
    public String getColumnTypeSql() {
        return null;
    }

    @Override
    public String getDatabasesSql() {
        return "select"
                + " TABLE_SCHEM " + Constant.DB_NAME // 数据库名称
                + " from SYSTEM.CATALOG"
                + " where TABLE_TYPE != 's'"
                + " group by TABLE_SCHEM";
    }

    @Override
    public String getTablesSql(String dbName) {
        String filter = "TABLE_SCHEM is null";
        if (StringUtils.isNotBlank(dbName)) {
            filter = "TABLE_SCHEM = '" + dbName.toUpperCase() + "'";
        }
        return "select"
                + " TABLE_NAME " + Constant.TB_NAME // 表名称
                + " from SYSTEM.CATALOG"
                + " where " + filter;
    }

    @Override
    public String getColumnsSql(String dbName, String tbName) {
        return null;
    }

    @Override
    public String getDbType() {
        return DBType.PHOENIX.getValue();
    }
}
