package com.hex.bigdata.udsp.common.aggregator;

import com.google.common.base.Stopwatch;
import com.hex.bigdata.udsp.common.aggregator.util.H2SqlUtil;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.dao.base.AsyncDeleteMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

/**
 * Created by JunjieM on 2019-1-29.
 */
@Repository
public class H2Aggregator {

    private static Logger logger = LogManager.getLogger (H2Aggregator.class);

    @Autowired
    @Qualifier("h2DataSource")
    private BasicDataSource h2DataSource;

    @Value("${aggregator.h2.data.timeout:43200}")
    private int aggregatorH2DataTimeout;

    private static final String TBL_PREFIX = "TMP_";

    private static Map<String, Long> h2AggMetaCacher = new HashMap<> ();

    private static final int BATCH_SIZE = 20000;

    /**
     * 加载数据前操作
     */
    public void beforeLoad(String[] header) {
        try (Connection conn = h2DataSource.getConnection ();
             Statement stmt = conn.createStatement ();) {
            String tableName = getTmpTblName ();
            String dropTableSql = H2SqlUtil.dropTable (tableName);
            logger.info ("Execute: {}", dropTableSql);
            stmt.execute (dropTableSql);
            String createTableSql = H2SqlUtil.createTable (tableName);
            logger.info ("Execute: {}", createTableSql);
            stmt.execute (createTableSql);
        } catch (SQLException e) {
            logger.error ("", e);
        }
    }

    /**
     * 加载数据操作
     *
     * @param header
     * @param data
     */
    public void loadBatch(String[] header, String[][] data) {
        long bef = System.currentTimeMillis ();
        int count = 0;
        if (data != null && data.length > 0) {
            try (Connection conn = h2DataSource.getConnection ();
                 PreparedStatement ps = conn.prepareStatement (buildPreparedInsertSql (header));) {
                for (int i = 0; i < data.length; i++) {
                    for (int j = 1; j <= header.length; j++) {
                        ps.setString (j, data[i][j - 1]);
                    }
                    ps.addBatch ();
                    if (++count % BATCH_SIZE == 0) {
                        ps.executeBatch ();
                        logger.info ("Thread id: {}, H2 load batch {}", Thread.currentThread ().getName (), count);
                    }
                }
                ps.executeBatch ();
            } catch (SQLException e) {
                logger.error ("", e);
            }
        }
        logger.info ("H2 Database loadBatch using time: {} ms", System.currentTimeMillis () - bef);
    }

    /**
     * 加载数据后操作
     */
    public void afterLoad() {
        h2AggMetaCacher.put (getTmpTblName (), System.currentTimeMillis ());
    }

    /**
     * 加载数据操作
     *
     * @param data
     */
    public void loadData(String[][] data) {
        long bef = System.currentTimeMillis ();
        int count = 0;
        if (data != null && data.length > 1) {
            String[] header = data[0];
            String tableName = getTmpTblName ();
            beforeLoad (header); // 加载数据前操作
            synchronized (tableName.intern ()) {
                try (Connection conn = h2DataSource.getConnection ();
                     PreparedStatement ps = conn.prepareStatement (buildPreparedInsertSql (header));) {
                    for (int i = 1; i < data.length; i++) {
                        for (int j = 1; j <= data[i].length; j++) {
                            ps.setString (j, data[i][j - 1]);
                        }
                        ps.addBatch ();
                        if (++count % BATCH_SIZE == 0) {
                            ps.executeBatch ();
                            logger.info ("H2 load batch {}", count);
                        }
                    }
                    ps.executeBatch ();
                } catch (SQLException e) {
                    logger.error ("", e);
                }
            }
        }
        afterLoad (); // 加载数据后操作
        logger.info ("H2 Database loading using time: {} ms", System.currentTimeMillis () - bef);
    }

//    public AggregateResult queryAggData(AggConfig config) throws Exception {
//        long bef = System.currentTimeMillis ();
//        List<String[]> list = new LinkedList<> ();
//        ResultSet rs = null;
//        try (Connection conn = h2DataSource.getConnection ();
//             Statement stat = conn.createStatement ();) {
//            stat.execute (H2SqlUtil.createStringToDoubleFunction ());
//            String sql = H2SqlUtil.queryAggData (getTmpTblName ());
//            logger.info (sql);
//            rs = stat.executeQuery (sql);
//            ResultSetMetaData metaData = rs.getMetaData ();
//            int columnCount = metaData.getColumnCount ();
//            while (rs.next ()) {
//                String[] row = new String[columnCount];
//                for (int j = 0; j < columnCount; j++) {
//                    row[j] = rs.getString (j + 1);
//                }
//                list.add (row);
//            }
//        } catch (Exception e) {
//            logger.error ("ERROR:" + e.getMessage ());
//            throw new Exception ("ERROR:" + e.getMessage (), e);
//        } finally {
//            if (rs != null) {
//                rs.close ();
//            }
//        }
//        logger.info ("H2 Database queryAggData using time: {} ms", System.currentTimeMillis () - bef);
//        return null;
//    }

    private String buildPreparedInsertSql(String[] header) {
        String tableName = getTmpTblName ();
//        StringJoiner insertJoiner = new StringJoiner (", ", "INSERT INTO " + tableName + " VALUES (", ");");
//        IntStream.range (0, header.length).forEach (i -> insertJoiner.add ("?"));
//        return insertJoiner.toString ();
        return "";
    }

    public Map<String, DataType> getColumnType() throws Exception {
        Map<String, DataType> map = new HashedMap ();
        String template = "SELECT column_name, type_name FROM INFORMATION_SCHEMA.columns WHERE table_name = upper('%s')";
        String sql = String.format (template, getTmpTblName ());
        try (Connection conn = h2DataSource.getConnection ();
             Statement stat = conn.createStatement ();
             ResultSet rs = stat.executeQuery (sql)) {
            while (rs.next ()) {
                String columnName = rs.getString ("column_name");
                String typeName = rs.getString ("type_name");
                // TODO ...
                map.put (columnName, DataType.VARCHAR);
            }
        }
        return map;
    }

    /**
     * 是否可以重新加载
     *
     * @return
     */
    public boolean isReload() {
        return isTimeout () || !isTableExists ();
    }

    /**
     * 判断h2数据是否超时
     *
     * @return
     */
    private boolean isTimeout() {
        Long createTimeStamp = h2AggMetaCacher.get (getTmpTblName ());
        return createTimeStamp == null || System.currentTimeMillis () - createTimeStamp > (aggregatorH2DataTimeout * 1000);
    }

    /**
     * 检查临时表是否存在于h2数据库中
     *
     * @return
     */
    private boolean isTableExists() {
        boolean exists = false;
        String sql = H2SqlUtil.tablesInfo (getTmpTblName ());
        try (Connection conn = h2DataSource.getConnection ();
             Statement stat = conn.createStatement ();
             ResultSet rs = stat.executeQuery (sql)) {
            int count = 0;
            while (rs.next ()) {
                count = rs.getInt (1);
            }
            if (count > 0) {
                exists = true;
            }
        } catch (Exception e) {
            logger.error ("ERROR:" + e.getMessage ());
        }
        return exists;
    }

    private String getTmpTblName() {
        return TBL_PREFIX + getCacheKey ();
    }

    private String getCacheKey() {
        // TODO ...
        return "";
    }
}
