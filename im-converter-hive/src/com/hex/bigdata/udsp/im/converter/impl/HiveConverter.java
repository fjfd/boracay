package com.hex.bigdata.udsp.im.converter.impl;

import com.hex.bigdata.metadata.db.ClientFactory;
import com.hex.bigdata.metadata.db.model.Column;
import com.hex.bigdata.metadata.db.util.AcquireType;
import com.hex.bigdata.metadata.db.util.DBType;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.im.converter.impl.util.HiveSqlUtil;
import com.hex.bigdata.udsp.im.converter.impl.wrapper.JdbcWrapper;
import com.hex.bigdata.udsp.im.converter.model.FileFormat;
import com.hex.bigdata.udsp.im.converter.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.model.ValueColumn;
import com.hex.bigdata.udsp.im.converter.model.WhereProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JunjieM on 2017-9-5.
 */
//@Component("com.hex.bigdata.udsp.im.converter.impl.HiveConverter")
public class HiveConverter extends JdbcWrapper {
    private static Logger logger = LogManager.getLogger(HiveConverter.class);

    @Override
    protected List<String> createSchemaSqls(String tableName, List<TableColumn> columns, String tableComment) {
        String[] sqls = {HiveSqlUtil.createTable(false, false, tableName,
                columns, tableComment, null, null, FileFormat.HIVE_FILE_FORMAT_PARQUET)};
        return Arrays.asList(sqls);
    }

    @Override
    protected String dropSchemaSql(String tableName) {
        return HiveSqlUtil.dropTable(true, tableName);
    }

    @Override
    protected List<String> addColumnSqls(String tableName, List<TableColumn> addColumns) {
        String[] sqls = {HiveSqlUtil.addColumns(tableName, addColumns)};
        return Arrays.asList(sqls);
    }

    @Override
    protected DataType getColType(String type) {
        type = type.toUpperCase();
        DataType dataType = null;
        switch (type) {
            case "VARCHAR":
                dataType = DataType.VARCHAR;
                break;
            case "STRING":
                dataType = DataType.STRING;
                break;
            case "DECIMAL":
                dataType = DataType.DECIMAL;
                break;
            case "CHAR":
                dataType = DataType.CHAR;
                break;
            case "FLOAT":
                dataType = DataType.FLOAT;
                break;
            case "DOUBLE":
                dataType = DataType.DOUBLE;
                break;
            case "TIMESTAMP":
            case "DATE":
                dataType = DataType.TIMESTAMP;
                break;
            case "INT":
                dataType = DataType.INT;
                break;
            case "BIGINT":
                dataType = DataType.BIGINT;
                break;
            case "TINYINT":
                dataType = DataType.TINYINT;
                break;
            case "SMALLINT":
                dataType = DataType.SMALLINT;
                break;
            case "BOOLEAN":
                dataType = DataType.BOOLEAN;
                break;
            default:
                dataType = DataType.STRING;
        }
        return dataType;
    }

    @Override
    protected List<Column> getColumns(Connection conn, String dbName, String tbName) throws SQLException {
        return ClientFactory.createMetaClient(AcquireType.JDBCAPI, DBType.HIVE, conn)
                .getColumns(dbName, tbName);
    }

    @Override
    protected String insertSql(String tableName, List<ValueColumn> valueColumns) {
        try {
            throw new Exception("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String updateSql(String tableName, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) {
        try {
            throw new Exception("不支持该方法");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
