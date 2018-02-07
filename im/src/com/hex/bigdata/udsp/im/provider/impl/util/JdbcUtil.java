package com.hex.bigdata.udsp.im.provider.impl.util;

import com.hex.bigdata.udsp.im.provider.impl.model.datasource.JdbcDatasource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2017-9-11.
 */
public class JdbcUtil {
    private static Logger logger = LogManager.getLogger(JdbcUtil.class);
    private static Map<String, BasicDataSource> dataSourcePool;

    public static synchronized BasicDataSource getDataSource(JdbcDatasource datasource) {
        String dsId = datasource.getId();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<>();
        }
        BasicDataSource dataSource = dataSourcePool.get(dsId);
        if (dataSource == null) {
            dataSource = new BasicDataSource();
            /**
             * 基础配置
             */
            if (StringUtils.isNotBlank(datasource.getDriverClass()))
                dataSource.setDriverClassName(datasource.getDriverClass());
            if (StringUtils.isNotBlank(datasource.getJdbcUrl()))
                dataSource.setUrl(datasource.getJdbcUrl());
            if (StringUtils.isNotBlank(datasource.getUsername()))
                dataSource.setUsername(datasource.getUsername());
            if (StringUtils.isNotBlank(datasource.getPassword()))
                dataSource.setPassword(datasource.getPassword());
            /**
             * 资源配置
             */
            // 连接池启动时创建的初始化连接数量（默认值为0，推荐为1）。
            if (StringUtils.isNotBlank(datasource.getInitialSize()))
                dataSource.setInitialSize(Integer.valueOf(datasource.getInitialSize()));
            // 连接池中最小的空闲连接数，低于这个数量会被创建新的连接（默认为0，推荐为5）。
            // 该参数越接近maxIdle性能越好，因为连接的创建和销毁都是需要消耗资源的；
            // 但是不能太大，因为在机器很空闲时也会创建低于minIdle个数的连接，类似JVM参数中的Xmn设置。
            if (StringUtils.isNotBlank(datasource.getMinIdle()))
                dataSource.setMinIdle(Integer.valueOf(datasource.getMinIdle()));
            // 连接池中最大的空闲连接数，超过的空闲连接将被释放，如果设置为负数表示不限制（默认为8）。
            // maxIdel不能设置太小，因为假如在高负载情况下连接打开时间比关闭时间快，
            // 会引起连接池中idle的个数上升超过maxIdle，而造成频繁的连接销毁和创建，类似于JVM参数中的Xmx设置。
            if (StringUtils.isNotBlank(datasource.getMaxIdle()))
                dataSource.setMaxIdle(Integer.valueOf(datasource.getMaxIdle()));
            // 连接池中可同时连接的最大连接数（默认为8），具体设置根据应用场景来定。
            if (StringUtils.isNotBlank(datasource.getMaxActive()))
                dataSource.setMaxActive(Integer.valueOf(datasource.getMaxActive()));
            // 最大等待时间，当没有可用连接时，连接池等待连接释放的最大时间，超过该时间限制会抛出异常，
            // 如果设置-1表示无限等待（默认为-1，推荐60000（毫秒），避免因线程池不够用而导致请求被无限挂起）。
            if (StringUtils.isNotBlank(datasource.getMaxWait()))
                dataSource.setMaxWait(Integer.valueOf(datasource.getMaxWait()));
//            // 开启池的准备Statements（默认false，推荐false，经过测试开启后性能没有关闭的好）
//            if(StringUtils.isNotBlank(datasource.getPoolPreparedStatements()))
//                dataSource.setPoolPreparedStatements(Boolean.valueOf(datasource.getPoolPreparedStatements()));
//            // 开启池的准备Statements最大链接数（默认无限制）
//            if(StringUtils.isNotBlank(datasource.getMaxOpenPreparedStatements()))
//                dataSource.setMaxOpenPreparedStatements(Integer.valueOf(datasource.getMaxOpenPreparedStatements()));
            // 连接池中连接在时间段内一直空闲，被逐出链接池的时间（默认30分钟，可以适当做调整，需要和后端服务端的策略配置相关）
            if (StringUtils.isNotBlank(datasource.getMinEvictableIdleTimeMillis()))
                dataSource.setMinEvictableIdleTimeMillis(Integer.valueOf(datasource.getMinEvictableIdleTimeMillis()));
            // 超过时间限制，回收没用的连接（默认为300000毫秒，推荐180000毫秒）
            if (StringUtils.isNotBlank(datasource.getRemoveAbandonedTimeout()))
                dataSource.setRemoveAbandonedTimeout(Integer.valueOf(datasource.getRemoveAbandonedTimeout()));
            // 超过removeAbandonedTimeout时间后是否进行没用连接的回收（默认为false，推荐true）
            if (StringUtils.isNotBlank(datasource.getRemoveAbandoned()))
                dataSource.setRemoveAbandoned(Boolean.valueOf(datasource.getRemoveAbandoned()));
            /**
             * 验证配置
             */
            // 是否被无效连接销毁器进行检验（推荐true）
            if (StringUtils.isNotBlank(datasource.getTestWhileIdle()))
                dataSource.setTestWhileIdle(Boolean.valueOf(datasource.getTestWhileIdle()));
            // 无效连接的检测间隔时间（毫秒）
            if (StringUtils.isNotBlank(datasource.getTimeBetweenEvictionRunsMillis()))
                dataSource.setTimeBetweenEvictionRunsMillis(Integer.valueOf(datasource.getTimeBetweenEvictionRunsMillis()));
            // 验证链接的SQL语句，必须能返回一行及以上数据
            if (StringUtils.isNotBlank(datasource.getValidationQuery()))
                dataSource.setValidationQuery(datasource.getValidationQuery());
            // 验证有效连接的超时时间（已无效）
//            if (StringUtils.isNotBlank(datasource.getValidationQueryTimeout()))
//                dataSource.setValidationQueryTimeout(Integer.valueOf(datasource.getValidationQueryTimeout()));
            // 是否从池中取出连接前进行检验（推荐false），为true时会很影响性能
            if (StringUtils.isNotBlank(datasource.getTestOnBorrow()))
                dataSource.setTestOnBorrow(Boolean.valueOf(datasource.getTestOnBorrow()));
            // 是否在归还到池中前进行检验（推荐false），为true时会很影响性能
            if (StringUtils.isNotBlank(datasource.getTestOnReturn()))
                dataSource.setTestOnReturn(Boolean.valueOf(datasource.getTestOnReturn()));
            /**
             * 其他配置
             */
            // 解决oracle获取元数据时获取字段注释
            if (StringUtils.isNotBlank(datasource.getRemarksReporting()))
                dataSource.addConnectionProperty("remarksReporting", datasource.getRemarksReporting());
            // 解决mysql获取元数据时获取字段注释
            if (StringUtils.isNotBlank(datasource.getUserInformationSchema()))
                dataSource.addConnectionProperty("userInformationSchema", datasource.getUserInformationSchema());
            dataSourcePool.put(dsId, dataSource);
        }
        return dataSource;
    }

    public static Connection getConnection(JdbcDatasource datasource) throws SQLException {
        Connection conn = null;
        BasicDataSource dataSource = getDataSource(datasource);
        if (dataSource != null) {
            conn = dataSource.getConnection();
        }
        return conn;
    }

    public static void createEngineSchema(JdbcDatasource datasource, String dbName, String updateSql) throws SQLException {
        String sql = HiveSqlUtil.createDatabase(true, dbName);
        executeUpdate(datasource, sql);
        executeUpdate(datasource, updateSql);
    }

    public static int executeUpdate(JdbcDatasource datasource, String updateSql) throws SQLException {
        logger.info("JDBC EXECUTE UPDATE SQL [START]");
        logger.info("UPDATE SQL: \n" + updateSql);
        Connection conn = null;
        Statement stmt = null;
        int rs = -1;
        try {
            conn = JdbcUtil.getConnection(datasource);
            stmt = conn.createStatement();
            rs = stmt.executeUpdate(updateSql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException(e);
        } finally {
            com.hex.bigdata.metadata.db.util.JdbcUtil.close(stmt);
            com.hex.bigdata.metadata.db.util.JdbcUtil.close(conn);
            logger.info("JDBC EXECUTE UPDATE SQL [END]");
        }
        return rs;
    }

    public static void executeUpdate(JdbcDatasource datasource, List<String> updateSqls) throws SQLException {
        for (String updateSql : updateSqls) {
            executeUpdate(datasource, updateSql);
        }
    }
}
