package com.hex.bigdata.udsp.im.converter.impl.util;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.im.converter.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.model.ValueColumn;
import com.hex.bigdata.udsp.im.converter.model.WhereProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by JunjieM on 2017-9-6.
 */
public class MysqlSqlUtil {

    /**
     * 创建表
     *
     * @param ifNotExists
     * @param tableName
     * @param columns
     * @param tableComment
     * @return
     */
    public static String createTable(boolean ifNotExists, String tableName,
                                     List<TableColumn> columns, String tableComment) {
        return "CREATE TABLE " + getIfNotExists(ifNotExists) + " " + tableName
                + getColumns(columns) + getTableComment(tableComment);
    }

    /**
     * 清空表数据
     *
     * @param tableName
     * @return
     */
    public static String truncateTable(String tableName) {
        return "TRUNCATE TABLE " + tableName;
    }

    /**
     * 删除所有数据
     *
     * @param tableName
     * @return
     */
    public static String deleteAll(String tableName) {
        return "DELETE FROM " + tableName;
    }

    /**
     * 删除表
     *
     * @param ifExists
     * @param tableName
     * @return
     */
    public static String dropTable(boolean ifExists, String tableName) {
        return "DROP TABLE" + getIfExists(ifExists) + " " + tableName;
    }

    /**
     * 更新
     *
     * @param tableName
     * @param valueColumns
     * @param whereProperties
     * @return
     */
    public static String update(String tableName, List<ValueColumn> valueColumns,
                                List<WhereProperty> whereProperties) {
        return "UPDATE " + tableName + SqlUtil.getSetValues(valueColumns) + SqlUtil.getWhere(whereProperties);
    }

    /**
     * 插入
     *
     * @param tableName
     * @param valueColumns
     * @return
     */
    public static String insert(String tableName, List<ValueColumn> valueColumns) {
        return "INSERT INTO " + tableName + SqlUtil.getIntoNames(valueColumns)
                + " VALUES " + SqlUtil.getIntoValues(valueColumns);
    }

    /**
     * 添加多个字段
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static String addColumns(String tableName, List<TableColumn> columns) {
        return "ALTER TABLE " + tableName + " ADD" + getColumns(columns);
    }

    private static String getValue(DataType dataType, String value) {
        if (DataType.STRING == dataType || DataType.VARCHAR == dataType || DataType.CHAR == dataType
                || DataType.TIMESTAMP == dataType || DataType.BOOLEAN == dataType) {
            value = "'" + value + "'";
        }
        return value;
    }

    private static String getIfExists(boolean ifExists) {
        String sql = "";
        if (ifExists) {
            sql = " IF EXISTS";
        }
        return sql;
    }

    private static String getIfNotExists(boolean ifNotExists) {
        String sql = "";
        if (ifNotExists) {
            sql = " IF NOT EXISTS";
        }
        return sql;
    }

    private static String getColumns(List<TableColumn> columns) {
        String sql = "";
        String colName = "";
        String dataType = "";
        String colComment = "";
        String length = "";
        int count = 0;
        if (columns != null && columns.size() != 0) {
            sql = "\n (";
            for (TableColumn column : columns) {
                colName = column.getColName();
                dataType = column.getDataType();
                colComment = column.getColComment();
                length = column.getLength();
                if (StringUtils.isBlank(colName) || StringUtils.isBlank(dataType))
                    continue;
                sql += (count == 0 ? "\n" : "\n,") + colName + " " + getColType(dataType, length);
                sql += column.isPrimaryKey() ? " PRIMARY KEY" : "";
                if (column.isPrimaryKey()) {
                    sql += " PRIMARY KEY";
                }
                if (StringUtils.isNotBlank(colComment)) {
                    sql += " COMMENT '" + colComment + "'";
                }
                count++;
            }
            sql += "\n)";
        }
        return sql;
    }

    private static String getColType(String dataType, String length) {
        if (DataType.VARCHAR.getValue().equals(dataType)) {
            dataType = "VARCHAR(" + length + ")";
        } else if (DataType.CHAR.getValue().equals(dataType)) {
            dataType = "CHAR(" + length + ")";
        } else if (DataType.DECIMAL.getValue().equals(dataType)) {
            dataType = "DECIMAL(" + length + ")";
        }
        return dataType;
    }

    private static String getTableComment(String tableComment) {
        String sql = "";
        if (tableComment != null && !tableComment.trim().equals("")) {
            sql = "\n COMMENT '" + tableComment + "'";
        }
        return sql;
    }
}
