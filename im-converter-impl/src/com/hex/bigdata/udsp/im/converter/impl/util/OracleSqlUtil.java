package com.hex.bigdata.udsp.im.converter.impl.util;

import com.hex.bigdata.udsp.im.converter.impl.util.model.TableColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.ValueColumn;
import com.hex.bigdata.udsp.im.converter.impl.util.model.WhereProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JunjieM on 2017-9-6.
 */
public class OracleSqlUtil {

    /**
     * 创建表
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static String createTable(String tableName, List<TableColumn> columns) {
        return "CREATE TABLE " + tableName + getColumns(columns);
    }

    /**
     * 添加多个字段注释
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static List<String> commentColumns(String tableName, List<TableColumn> columns) {
        List<String> list = new ArrayList<>();
        if (columns != null && columns.size() != 0) {
            for (TableColumn column : columns) {
                String colName = column.getColName();
                String colComment = column.getColComment();
                if (StringUtils.isNoneBlank(colName) && StringUtils.isNoneBlank(colComment)) {
                    list.add(commentColumn(tableName, colName, colComment));
                }
            }
        }
        return list;
    }

    /**
     * 添加多个字段
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static List<String> addColumns(String tableName, List<TableColumn> columns) {
        List<String> list = new ArrayList<>();
        if (columns != null && columns.size() != 0) {
            for (TableColumn column : columns) {
                String colName = column.getColName();
                String length = column.getLength();
                String dataType = column.getDataType();
                if (StringUtils.isNoneBlank(colName) && StringUtils.isNoneBlank(dataType)) {
                    list.add(addColumn(tableName, colName, getColType(dataType, length)));
                }
            }
        }
        return list;
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
     * @param tableName
     * @return
     */
    public static String dropTable(String tableName) {
        return "DROP TABLE " + tableName;
    }

    /**
     * 字段注释
     *
     * @param tableName
     * @param colName
     * @param colComment
     * @return
     */
    public static String commentColumn(String tableName, String colName, String colComment) {
        return "COMMENT ON COLUMN " + tableName + "." + colName + " IS '" + colComment + "'";
    }

    /**
     * 表注释
     *
     * @param tableName
     * @param tableComment
     * @return
     */
    public static String commentTable(String tableName, String tableComment) {
        return "COMMENT ON TABLE " + tableName + " IS '" + tableComment + "'";
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
     * 更新
     *
     * @param tableName
     * @param valueColumns
     * @param whereProperties
     * @return
     */
    public static String update(String tableName, List<ValueColumn> valueColumns, List<WhereProperty> whereProperties) {
        return "UPDATE " + tableName + SqlUtil.getSetValues(valueColumns) + SqlUtil.getWhere(whereProperties);
    }

    /**
     * 添加字段
     *
     * @param tableName
     * @param colName
     * @param dataType
     * @return
     */
    public static String addColumn(String tableName, String colName, String dataType) {
        return "ALTER TABLE " + tableName + " ADD (" + colName + " " + dataType + ")";
    }

    private static String getColumns(List<TableColumn> columns) {
        String sql = "";
        if (columns != null && columns.size() != 0) {
            sql = "\n (";
            for (int i = 0; i < columns.size(); i++) {
                TableColumn column = columns.get(i);
                String colName = column.getColName();
                String dataType = column.getDataType();
                String length = column.getLength();
                if (StringUtils.isNoneBlank(colName) && StringUtils.isNoneBlank(dataType)) {
                    dataType = getColType(dataType, length);
                    if (i == 0) {
                        sql += "\n" + colName + " " + dataType;
                    } else {
                        sql += "\n, " + colName + " " + dataType;
                    }
                }
            }
            sql += "\n)";
        }
        return sql;
    }

    public static String getColType(String dataType, String length) {
        if ("VARCHAR".equals(dataType)) {
            dataType = "VARCHAR2(" + length + ")";
        } else if ("CHAR".equals(dataType)) {
            dataType = "CHAR(" + length + ")";
        } else if ("DECIMAL".equals(dataType)) {
            dataType = "NUMBER(" + length + ")";
        } else if ("STRING".equals(dataType)) {
            dataType = "VARCHAR2(4000)";
        }
        return dataType;
    }

    /**
     * 添加主键
     *
     * @param tableName
     * @param columns
     * @return
     */
    public static String createPrimaryKey(String tableName, List<TableColumn> columns) {
        if (StringUtils.isEmpty(tableName)) {
            return "";
        }
        List<String> list = new ArrayList<>();
        for (TableColumn col : columns) {
            if (col.isPrimaryKey() && !"STRING".equals(col.getDataType())) {
                list.add(col.getColName());
            }
        }
        if (list.size() <= 0) {
            return "";
        } else {
            return "ALTER TABLE " + tableName + " ADD CONSTRAINT PRIMARYKEY PRIMARY KEY ("
                    + list.toString().replaceAll("(\\[|\\])", "") + ")";
        }
    }
}
