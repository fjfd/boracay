package com.hex.bigdata.metadata.db.jdbcinfo.helper;

import com.hex.bigdata.metadata.db.jdbcinfo.BaseJdbcInfoHelper;
import com.hex.bigdata.metadata.db.model.ColumnType;
import com.hex.bigdata.metadata.db.util.DBType;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by junjiem on 2018-10-08.
 */
public class PhoenixJdbcInfoHelper extends BaseJdbcInfoHelper {

    public PhoenixJdbcInfoHelper(Connection conn) {
        super(conn);
    }

    @Override
    public String getCurrentDbNameSql() {
        return null;
    }

    @Override
    public String getDatabasesSql() {
        return "select TABLE_SCHEM from SYSTEM.CATALOG where TABLE_TYPE != 's' group by TABLE_SCHEM";
    }

    @Override
    public String getTablesSql(String dbName) {
        String filter = "TABLE_SCHEM is null";
        if (StringUtils.isNotBlank(dbName)) {
            filter = "TABLE_SCHEM = '" + dbName.toUpperCase() + "'";
        }
        return "select TABLE_NAME from SYSTEM.CATALOG where " + filter;
    }

    @Override
    public String getColumnsSql(String dbName, String tbName) {
        if (StringUtils.isNotBlank(dbName)) {
            tbName = dbName + "." + tbName;
        }
        return "select * from " + tbName + " where 1=0";
    }

    @Override
    public String getDbType() {
        return DBType.PHOENIX.getValue();
    }

    @Override
    public List<ColumnType> getColumnTypes() throws SQLException {
        return null;
    }
}
