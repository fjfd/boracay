package com.hex.bigdata.metadata.db;

import com.hex.bigdata.metadata.db.model.Column;
import com.hex.bigdata.metadata.db.model.ColumnType;
import com.hex.bigdata.metadata.db.model.Database;
import com.hex.bigdata.metadata.db.model.Table;
import com.hex.bigdata.metadata.db.util.DBType;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by junjiem on 2016-6-23.
 */
public interface Client {

    Helper createHelper(DBType dbType) throws SQLException;

    /**
     * 获取当前库名
     *
     * @return
     * @throws SQLException
     */
    String getCurrentDbName() throws SQLException;

    /**
     * 切换库
     *
     * @param dbName
     * @return
     */
    Client use(String dbName);

    /**
     * 获取库集合
     *
     * @return
     * @throws SQLException
     */
    List<Database> getDatabases() throws SQLException;

    /**
     * 获取库表的字段集合
     *
     * @param dbName
     * @param tbName
     * @return
     * @throws SQLException
     */
    List<Column> getColumns(String dbName, String tbName) throws SQLException;

    /**
     * 获取当前库中表的字段集合
     *
     * @param tbName
     * @return
     * @throws SQLException
     */
    List<Column> getColumns(String tbName) throws SQLException;

    /**
     * 获取库的表集合
     *
     * @param dbName
     * @return
     * @throws SQLException
     */
    List<Table> getTables(String dbName) throws SQLException;

    /**
     * 获取当前库的表集合
     *
     * @return
     * @throws SQLException
     */
    List<Table> getTables() throws SQLException;

    /**
     * 获取库表信息
     *
     * @param dbName
     * @param tbName
     * @return
     * @throws SQLException
     */
    Table getTable(String dbName, String tbName) throws SQLException;

    /**
     * 获取当前库的表信息
     *
     * @param tbName
     * @return
     * @throws SQLException
     */
    Table getTable(String tbName) throws SQLException;

    /**
     * 获取库信息
     *
     * @param dbName
     * @return
     * @throws SQLException
     */
    Database getDatabase(String dbName) throws SQLException;

    /**
     * 获取当前库信息
     *
     * @return
     * @throws SQLException
     */
    Database getDatabase() throws SQLException;

    /**
     * 获取字段类型集合
     *
     * @return
     * @throws SQLException
     */
    List<ColumnType> getColumnTypes() throws SQLException;

    /**
     * 关闭链接
     */
    void close();

}
