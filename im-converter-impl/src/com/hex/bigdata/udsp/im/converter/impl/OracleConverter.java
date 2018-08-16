package com.hex.bigdata.udsp.im.converter.impl;

import com.hex.bigdata.metadata.db.ClientFactory;
import com.hex.bigdata.metadata.db.model.Column;
import com.hex.bigdata.metadata.db.util.AcquireType;
import com.hex.bigdata.metadata.db.util.DBType;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.im.converter.RealtimeTargetConverter;
import com.hex.bigdata.udsp.im.converter.impl.util.OracleSqlUtil;
import com.hex.bigdata.udsp.im.converter.impl.util.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.ValueColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.WhereProperty;
import com.hex.bigdata.udsp.im.converter.impl.wrapper.JdbcWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
//@Component("com.hex.bigdata.udsp.im.converter.impl.OracleConverter")
public class OracleConverter extends JdbcWrapper implements RealtimeTargetConverter {
    private static Logger logger = LogManager.getLogger(OracleConverter.class);

    @Override
    protected List<String> createSchemaSqls(String tableName, List<TableColumn> columns, String tableComment) {
        List<String> sqls = new ArrayList<>();
        String createTableSql = OracleSqlUtil.createTable(tableName, columns);
        if (StringUtils.isNotBlank(createTableSql)) sqls.add(createTableSql);
        String commentTableSql = OracleSqlUtil.commentTable(tableName, tableComment);
        if (StringUtils.isNotBlank(commentTableSql)) sqls.add(commentTableSql);
        List<String> commentColumnsSqls = OracleSqlUtil.commentColumns(tableName, columns);
        if (commentColumnsSqls != null && commentColumnsSqls.size() != 0) sqls.addAll(commentColumnsSqls);
        String createPrimaryKeySql = OracleSqlUtil.createPrimaryKey(tableName, columns);
        if (StringUtils.isNotBlank(createPrimaryKeySql)) sqls.add(createPrimaryKeySql);
        return sqls;
    }

    @Override
    protected String dropSchemaSql(String tableName) {
        return OracleSqlUtil.dropTable(tableName);
    }

    @Override
    protected List<String> addColumnSqls(String tableName, List<TableColumn> addColumns) {
        List<String> sqls = new ArrayList<>();
        List<String> addColumnSqls = OracleSqlUtil.addColumns(tableName, addColumns);
        if (addColumnSqls != null && addColumnSqls.size() != 0) sqls.addAll(addColumnSqls);
        List<String> commentColumnsSqls = OracleSqlUtil.commentColumns(tableName, addColumns);
        if (commentColumnsSqls != null && commentColumnsSqls.size() != 0) sqls.addAll(commentColumnsSqls);
        return sqls;
    }

    @Override
    protected DataType getColType(String type) {
        type = type.toUpperCase();
        DataType dataType = null;
        switch (type) {
            case "VARCHAR":
            case "VARCHAR2":
            case "NVARCHAR2":
                dataType = DataType.VARCHAR;
                break;
            case "CLOB":
            case "LONG":
            case "BLOB":
                dataType = DataType.STRING;
                break;
            case "NUMBER":
                dataType = DataType.DECIMAL;
                break;
            case "CHAR":
            case "NCHAR":
                dataType = DataType.CHAR;
                break;
            case "BINARY_FLOAT":
                dataType = DataType.FLOAT;
                break;
            case "BINARY_DOUBLE":
                dataType = DataType.DOUBLE;
                break;
            case "TIMESTAMP":
            case "TIMESTAMP(6)":
            case "DATE":
                dataType = DataType.TIMESTAMP;
                break;
            default:
                dataType = null;
        }
        return dataType;
    }

    @Override
    protected List<Column> getColumns(Connection conn, String dbName, String tbName) throws SQLException {
        // 方式一：通过JDBCAPI方式获取字段信息
        // 通过JDBC的API接口获取，可以拿到字段名、类型、长度、注释、主键、索引、分区等信息
        return ClientFactory.createMetaClient(AcquireType.JDBCAPI, DBType.ORACLE, conn)
                .getColumns(dbName, tbName);
//        // 方式二：通过JDBCINFO方式获取字段信息
//        // 通过select * from dbName.tbName获取，只能拿到字段名、类型、长度等信息
//        return ClientFactory.createMetaClient(AcquireType.JDBCINFO, DBType.HIVE, conn)
//                .getColumns(dbName, tbName);
//        // 方式三：通过JDBCAPI方式获取字段信息
//        // 查询元数据表，可以获取最为详细的字段信息
//        return ClientFactory.createMetaClient(AcquireType.JDBCSQL, DBType.HIVE, conn)
//                .getColumns(dbName, tbName);
    }

    @Override
    protected String insertSql(String tableName, List<ValueColumn> valueColumns) {
        return OracleSqlUtil.insert(tableName, valueColumns);
    }

    @Override
    protected String updateSql(String tableName, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) {
        return OracleSqlUtil.update(tableName, valueColumns, whereProperties);
    }
}
