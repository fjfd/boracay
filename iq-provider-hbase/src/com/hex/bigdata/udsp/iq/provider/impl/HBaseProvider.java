package com.hex.bigdata.udsp.iq.provider.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.ExceptionUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.dsl.constant.ComparisonOperator;
import com.hex.bigdata.udsp.dsl.model.Component;
import com.hex.bigdata.udsp.dsl.model.Composite;
import com.hex.bigdata.udsp.dsl.model.Dimension;
import com.hex.bigdata.udsp.dsl.model.DslRequest;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.impl.factory.HBaseAggregationClientPoolFactory;
import com.hex.bigdata.udsp.iq.provider.impl.factory.HBaseConnectionPoolFactory;
import com.hex.bigdata.udsp.iq.provider.impl.model.HBaseDatasource;
import com.hex.bigdata.udsp.iq.provider.impl.model.HBaseMetadata;
import com.hex.bigdata.udsp.iq.provider.impl.model.HBasePage;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by junjiem on 2017-3-3.
 */
public class HBaseProvider implements Provider {

    private static final int HBASE_SCAN_CACHING_SIZE = 1024; // 每次RPC请求记录数
    private static final int HBASE_SCAN_BATCH_SIZE = 1024; // 每一批获取记录数

    private static Logger logger = LogManager.getLogger (HBaseProvider.class);
    private static final FastDateFormat format8 = FastDateFormat.getInstance ("yyyyMMdd");
    private static final FastDateFormat format10 = FastDateFormat.getInstance ("yyyy-MM-dd");
    private static final FastDateFormat format17 = FastDateFormat.getInstance ("yyyyMMdd HH:mm:ss");
    private static final FastDateFormat format19 = FastDateFormat.getInstance ("yyyy-MM-dd HH:mm:ss");
    private static final String rkSep = "|";
    private static final String startStr = "";
    private static final String stopStr = "|";

    private static Map<String, HBaseConnectionPoolFactory> dataSourcePool;
    private static Map<String, HBaseAggregationClientPoolFactory> aggregationClientDataSourcePool;

    @Override
    public IqResponse query(IqRequest request) {
        logger.debug ("request=" + JSONUtil.parseObj2JSON (request));
        long bef = System.currentTimeMillis ();
        IqResponse response = new IqResponse ();

        try {
            Application application = request.getApplication ();
            Metadata metadata = application.getMetadata ();
            List<QueryColumn> queryColumns = application.getQueryColumns ();
            Collections.sort (queryColumns, new Comparator<QueryColumn> () {
                @Override
                public int compare(QueryColumn obj1, QueryColumn obj2) {
                    return obj1.getSeq ().compareTo (obj2.getSeq ());
                }
            });
            List<ReturnColumn> returnColumns = application.getReturnColumns ();
            List<OrderColumn> orderColumns = application.getOrderColumns ();
            List<DataColumn> metaReturnColumns = metadata.getReturnColumns ();
            String tbName = metadata.getTbName ();
            HBaseMetadata hbaseMetadata = new HBaseMetadata (metadata.getPropertyMap ());
            HBaseDatasource hbaseDatasource = new HBaseDatasource (metadata.getDatasource ());
            String startRow = getStartRow (queryColumns);
            String stopRow = getStopRow (queryColumns);
            logger.debug ("startRow:" + startRow + ", startRow:" + startRow);
            Map<Integer, String> colMap = getColMap (metaReturnColumns);
            List<Map<String, String>> list = scan (hbaseDatasource, tbName, startRow, stopRow, colMap, hbaseMetadata);
            list = orderBy (list, orderColumns); // 排序处理
            response.setRecords (getRecords (list, returnColumns)); // 字段过滤并字段名改别名
            response.setStatus (Status.SUCCESS);
            response.setStatusCode (StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace ();
            response.setStatus (Status.DEFEAT);
            response.setStatusCode (StatusCode.DEFEAT);
            response.setMessage (e.getMessage ());
            logger.warn (e.toString ());
        }

        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        response.setConsumeTime (consumeTime);

        logger.debug ("consumeTime=" + response.getConsumeTime ());
        return response;
    }

    @Override
    public IqResponse query(IqRequest request, Page page) {
        logger.debug ("request=" + JSONUtil.parseObj2JSON (request)
                + " pageIndex=" + page.getPageIndex () + " pageSize=" + page.getPageSize ());
        long bef = System.currentTimeMillis ();
        IqResponse response = new IqResponse ();

        try {
            Application application = request.getApplication ();
            Metadata metadata = application.getMetadata ();
            List<QueryColumn> queryColumns = application.getQueryColumns ();
            Collections.sort (queryColumns, new Comparator<QueryColumn> () {
                @Override
                public int compare(QueryColumn obj1, QueryColumn obj2) {
                    return obj1.getSeq ().compareTo (obj2.getSeq ());
                }
            });
            List<ReturnColumn> returnColumns = application.getReturnColumns ();
            List<OrderColumn> orderColumns = application.getOrderColumns ();
            List<DataColumn> metaReturnColumns = metadata.getReturnColumns ();
            String tbName = metadata.getTbName ();
            HBaseMetadata hbaseMetadata = new HBaseMetadata (metadata.getPropertyMap ());
            HBaseDatasource hbaseDatasource = new HBaseDatasource (metadata.getDatasource ());
            String startRow = getStartRow (queryColumns);
            String stopRow = getStopRow (queryColumns);
            logger.debug ("startRow:" + startRow + ", startRow:" + startRow);
            Map<Integer, String> colMap = getColMap (metaReturnColumns);
            HBasePage hbasePage = new HBasePage (page.getPageSize (), page.getPageIndex (), startRow, stopRow);
            hbasePage = scanPage (hbaseDatasource, tbName, hbasePage, colMap, hbaseMetadata);
            List<Map<String, String>> list = hbasePage.getRecords ();
            list = orderBy (list, orderColumns); // 排序处理
            response.setRecords (getRecords (list, returnColumns)); // 字段过滤并字段名改别名
            page.setTotalCount (hbasePage.getTotalCount ());
            response.setPage (page);
            response.setStatus (Status.SUCCESS);
            response.setStatusCode (StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace ();
            response.setStatus (Status.DEFEAT);
            response.setStatusCode (StatusCode.DEFEAT);
            response.setMessage (e.getMessage ());
            logger.warn (e.toString ());
        }

        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        response.setConsumeTime (consumeTime);

        logger.debug ("consumeTime=" + response.getConsumeTime ());
        return response;
    }

    private synchronized HBaseConnectionPoolFactory getDataSource(HBaseDatasource datasource) {
        String dsId = datasource.getId ();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<String, HBaseConnectionPoolFactory> ();
        }
        HBaseConnectionPoolFactory factory = dataSourcePool.remove (dsId);
        if (factory == null) {
            GenericObjectPool.Config config = new GenericObjectPool.Config ();
            config.lifo = true;
            config.minIdle = 1;
            config.maxIdle = 10;
            config.maxWait = 3000;
            config.maxActive = 5;
            config.timeBetweenEvictionRunsMillis = 30000;
            config.testWhileIdle = true;
            config.testOnBorrow = false;
            config.testOnReturn = false;
            factory = new HBaseConnectionPoolFactory (config, datasource);
        }
        dataSourcePool.put (dsId, factory);
        return factory;
    }

    private HConnection getConnection(HBaseDatasource datasource) throws Exception {
        return getDataSource (datasource).getConnection ();
    }

    private void release(HBaseDatasource datasource, HConnection conn) {
        getDataSource (datasource).releaseConnection (conn);
    }

    private synchronized HBaseAggregationClientPoolFactory getAggregationClientDataSource(HBaseDatasource datasource) {
        String dsId = datasource.getId ();
        if (aggregationClientDataSourcePool == null) {
            aggregationClientDataSourcePool = new HashMap<> ();
        }
        HBaseAggregationClientPoolFactory factory = aggregationClientDataSourcePool.remove (dsId);
        if (factory == null) {
            GenericObjectPool.Config config = new GenericObjectPool.Config ();
            config.lifo = true;
            config.minIdle = 1;
            config.maxIdle = 10;
            config.maxWait = 3000;
            config.maxActive = 5;
            config.timeBetweenEvictionRunsMillis = 30000;
            config.testWhileIdle = true;
            config.testOnBorrow = false;
            config.testOnReturn = false;
            factory = new HBaseAggregationClientPoolFactory (config, datasource);
        }
        aggregationClientDataSourcePool.put (dsId, factory);
        return factory;
    }

    private AggregationClient getAggregationClient(HBaseDatasource datasource) throws Exception {
        return getAggregationClientDataSource (datasource).getAggregationClient ();
    }

    private void release(HBaseDatasource datasource, AggregationClient client) {
        getAggregationClientDataSource (datasource).releaseAggregationClient (client);
    }

    // 字段过滤并字段名改别名
    private List<Map<String, String>> getRecords(List<Map<String, String>> list, List<ReturnColumn> returnColumns) {
        List<Map<String, String>> records = new ArrayList<> ();
        if (list == null || list.size () == 0) {
            return records;
        }
        Map<String, String> result = null;
        for (Map<String, String> map : list) {
            result = new HashMap<> ();
            for (ReturnColumn item : returnColumns) {
                result.put (item.getLabel (), map.get (item.getName ()));
            }
            records.add (result);
        }
        return records;
    }

    private List<Map<String, String>> orderBy(List<Map<String, String>> list, final List<OrderColumn> orderColumns) {
        if (list == null || list.size () == 0) {
            return list;
        }
        Collections.sort (orderColumns, new Comparator<OrderColumn> () {
            @Override
            public int compare(OrderColumn obj1, OrderColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        // 多字段混合排序
        Collections.sort (list, new Comparator<Map<String, String>> () {
            @Override
            public int compare(Map<String, String> obj1, Map<String, String> obj2) {
                int flg = 0;
                for (OrderColumn orderColumn : orderColumns) {
                    String colName = orderColumn.getName ();
                    Order order = orderColumn.getOrder ();
                    DataType dataType = orderColumn.getType ();
                    String val1 = obj1.get (colName);
                    String val2 = obj2.get (colName);
                    if (StringUtils.isNotBlank (val1) && StringUtils.isNotBlank (val2)) {
                        flg = compareTo (val1, val2, order, dataType);
                        if (flg != 0) {
                            break;
                        }
                    }
                }
                return flg;
            }
        });
        return list;
    }

    private int compareTo(String str1, String str2, Order order, DataType dataType) {
        if (dataType == null || DataType.STRING.equals (dataType) || DataType.VARCHAR.equals (dataType)
                || DataType.CHAR.equals (dataType) || DataType.TIMESTAMP.equals (dataType)) {
            if (order != null && Order.DESC.equals (order)) {
                if (str1.compareTo (str2) > 0) {
                    return -1;
                } else if (str1.compareTo (str2) == 0) {
                    return 0;
                }
                return 1;
            } else {
                if (str1.compareTo (str2) > 0) {
                    return 1;
                } else if (str1.compareTo (str2) == 0) {
                    return 0;
                }
                return -1;
            }
        } else {
            if (order != null && Order.DESC.equals (order)) {
                if (Double.valueOf (str1).compareTo (Double.valueOf (str2)) > 0) {
                    return -1;
                } else if (str1.compareTo (str2) == 0) {
                    return 0;
                }
                return 1;
            } else {
                if (Double.valueOf (str1).compareTo (Double.valueOf (str2)) > 0) {
                    return 1;
                } else if (str1.compareTo (str2) == 0) {
                    return 0;
                }
                return -1;
            }
        }
    }

    private String getStartRow(List<QueryColumn> queryColumns) {
        String startRow = getMd5Str (queryColumns);
        for (QueryColumn queryColumn : queryColumns) {
            Operator operator = queryColumn.getOperator ();
            boolean isNeed = queryColumn.isNeed ();
//            DataType dataType = queryColumn.getType();
            String value = queryColumn.getValue ();
            int length = getLen (queryColumn.getLength ());
            if (isNeed && StringUtils.isBlank (value)) {
                throw new IllegalArgumentException ("必输项值为空");
            }
            if (!Operator.EQ.equals (operator) && !Operator.GE.equals (operator) && !Operator.LE.equals (operator)) {
                throw new IllegalArgumentException ("只支持等于、大于等于和小于等于操作");
            }
            if (StringUtils.isNotBlank (value)) { // 不能为空
                // 只能是等于或大于等于操作
                if (Operator.EQ.equals (operator) || Operator.GE.equals (operator)) {
                    if (isNeed && Operator.EQ.equals (operator)) { // 必填且等于操作
                        value = realValue (value, length); // 右补齐空格
                    } else if (Operator.GE.equals (operator)) { // 大于等于操作
                        value = tarnDateStr (length, value); // 日期格式转换
                    }
                    if (StringUtils.isNotBlank (value)) {
                        startRow += (StringUtils.isBlank (startRow) ? value : rkSep + value);
                    }
                }
            }
            if (Operator.GE.equals (operator)) break; // 退出
        }
        return startRow;
    }

    private String getStopRow(List<QueryColumn> queryColumns) {
        String stopRow = getMd5Str (queryColumns);
        for (QueryColumn queryColumn : queryColumns) {
            Operator operator = queryColumn.getOperator ();
            boolean isNeed = queryColumn.isNeed ();
//            DataType dataType = queryColumn.getType();
            String value = queryColumn.getValue ();
            int length = getLen (queryColumn.getLength ());
            if (queryColumn.isNeed () && StringUtils.isBlank (value)) {
                throw new IllegalArgumentException ("必输项值为空");
            }
            if (!Operator.EQ.equals (operator) && !Operator.GE.equals (operator) && !Operator.LE.equals (operator)) {
                throw new IllegalArgumentException ("只支持等于、大于等于和小于等于操作");
            }
            if (StringUtils.isNotBlank (value)) { // 不能为空
                // 只能是等于或小于等于操作
                if (Operator.EQ.equals (operator) || Operator.LE.equals (operator)) {
                    if (isNeed && Operator.EQ.equals (operator)) { // 必填且等于操作
                        value = realValue (value, length); // 右补齐空格
                    } else if (Operator.LE.equals (operator)) { // 小于等于操作
                        value = tarnDateStr (length, value); // 日期格式转换
                    }
                    if (StringUtils.isNotBlank (value)) {
                        stopRow += (StringUtils.isBlank (stopRow) ? value : rkSep + value);
                    }
                }
            }
            if (Operator.LE.equals (operator)) break; // 退出
        }
        return stopRow;
    }

    private String tarnDateStr(int length, String value) {
        if (length == 8 || length == 10 || length == 17 || length == 19) {
            Date date = strToDate (value);
            if (date != null) {
                if (length == 8) {
                    value = format8.format (date);
                } else if (length == 10) {
                    value = format10.format (date);
                } else if (length == 17) {
                    value = format17.format (date);
                } else if (length == 19) {
                    value = format19.format (date);
                }
            }
        }
        return value;
    }

    private Date strToDate(String dataStr) {
        Date date = null;
        try {
            if (dataStr.length () == 8) {
                date = format8.parse (dataStr);
            } else if (dataStr.length () == 10) {
                date = format10.parse (dataStr.replaceAll ("/", "-"));
            } else if (dataStr.length () == 17) {
                date = format17.parse (dataStr);
            } else if (dataStr.length () == 19) {
                date = format19.parse (dataStr.replaceAll ("/", "-"));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException ("日期字段传入的不是日期格式字符串参数");
        }
        return date;
    }

    private int getLen(String length) {
        int len = 0;
        if (StringUtils.isNotBlank (length) && StringUtils.isNumeric (length)) {
            try {
                len = Integer.valueOf (length);
            } catch (Exception e) {
                logger.debug (ExceptionUtil.getMessage (e));
            }
        }
        return len;
    }

    private String getMd5Str(List<QueryColumn> queryColumns) {
        String str = "";
        int count = 0;
        for (QueryColumn queryColumn : queryColumns) {
            if (queryColumn.isNeed () && Operator.EQ.equals (queryColumn.getOperator ())) {
                String value = queryColumn.getValue ();
                if (StringUtils.isBlank (value)) {
                    throw new IllegalArgumentException ("必输项值为空");
                }
                str += (count == 0 ? value : rkSep + value);
                count++;
            } else {
                break;
            }
        }
        if (StringUtils.isNotBlank (str)) {
            str = md5_16 (str);
        }
        return str;
    }

    private Map<Integer, String> getColMap(List<DataColumn> returnColumns) {
        Map<Integer, String> colMap = new HashMap<Integer, String> ();
        Collections.sort (returnColumns, new Comparator<DataColumn> () {
            @Override
            public int compare(DataColumn obj1, DataColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        for (int i = 0; i < returnColumns.size (); i++) {
            colMap.put (i + 1, returnColumns.get (i).getName ());
        }
        return colMap;
    }

    private long count(HTableInterface table, AggregationClient client, String startRow, String stopRow) throws Exception {
        Scan scan = new Scan ();
        setRowScan (scan, startRow, stopRow);
        /*
        使用聚合协处理器获取总行数，如果该表没有注册聚合协处理器则使用scan的方式获取总行数
         */
        try {
            return client.rowCount (table, new LongColumnInterpreter (), scan);
        } catch (Throwable throwable) {
            //logger.warn(throwable.getMessage());
            logger.warn ("HBase表" + table.toString () + "没有注册集合协处理器，无法使用协处理器方式获取总行数！");
            return count (table, scan);
        }
    }

    private long count(HTableInterface table, Scan scan) throws Exception {
        scan.setCaching (500);
        scan.setCacheBlocks (false);
        scan.setFilter (new FirstKeyOnlyFilter ());
        ResultScanner rs = table.getScanner (scan);
        long count = 0;
        while (rs.next () != null) {
            count++;
        }
        rs.close ();
        return count;
    }

    private List<Map<String, String>> scan(HTableInterface table, Scan scan, Map<Integer, String> colMap,
                                           byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        ResultScanner rs = table.getScanner (scan);
        return getMaps (rs, colMap, family, qualifier, separator, dataType);
    }

    private List<Map<String, String>> scan(HBaseDatasource datasource, String tbName, String startRow, String stopRow,
                                           Map<Integer, String> colMap, HBaseMetadata metadata) throws Exception {
        List<Map<String, String>> list = null;
        HConnection conn = null;
        HTableInterface hTable = null;
        try {
            conn = getConnection (datasource);
            hTable = conn.getTable (tbName);
            list = scan (hTable, startRow, stopRow, colMap, datasource.getMaxSize (), metadata.getFamilyName (),
                    metadata.getQualifierName (), metadata.getDsvSeparator (), metadata.getFqDataType ());
        } finally {
            if (hTable != null) {
                try {
                    hTable.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            if (conn != null) {
                release (datasource, conn);
            }
        }
        return list;
    }

    private List<Map<String, String>> scan(HTableInterface table, String startRow, String stopRow, Map<Integer, String> colMap,
                                           long maxSize, byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        Scan scan = new Scan ();
        addColumn (scan, family, qualifier);
        setRowScan (scan, startRow, stopRow);
        scan.setFilter (new PageFilter (maxSize));
        return scan (table, scan, colMap, family, qualifier, separator, dataType);
    }

    private List<Map<String, String>> scanPage(HTableInterface table, HBasePage page,
                                               Map<Integer, String> colMap, byte[] family, byte[] qualifier,
                                               String separator, String dataType) throws Exception {
        Scan scan = new Scan ();
        addColumn (scan, family, qualifier);
        setRowScan (scan, page);
        return scanPage (table, scan, page, colMap, family, qualifier, separator, dataType);
    }

    private List<Map<String, String>> scanPage(HTableInterface table, Scan scan, HBasePage page,
                                               Map<Integer, String> colMap, byte[] family, byte[] qualifier,
                                               String separator, String dataType) throws Exception {
        ResultScanner rs = table.getScanner (scan);
        return getMapsPage (rs, page, colMap, family, qualifier, separator, dataType);
    }

    private HBasePage scanPage(HBaseDatasource datasource, String tbName, HBasePage page,
                               Map<Integer, String> colMap, HBaseMetadata metadata) throws Exception {
        HConnection conn = null;
        HTableInterface hTable = null;
        AggregationClient client = null;
        try {
            conn = getConnection (datasource);
            hTable = conn.getTable (tbName);
            client = getAggregationClient (datasource);
            page = scanPage (hTable, client, page, colMap, metadata.getFamilyName (),
                    metadata.getQualifierName (), metadata.getDsvSeparator (), metadata.getFqDataType ());
        } finally {
            if (hTable != null) {
                try {
                    hTable.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            if (client != null) {
                release (datasource, client);
            }
            if (conn != null) {
                release (datasource, conn);
            }
        }
        return page;
    }

    private HBasePage scanPage(HTableInterface table, AggregationClient client, HBasePage page, Map<Integer, String> colMap,
                               byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        page.setRecords (scanPage (table, page, colMap, family, qualifier, separator, dataType));
        page.setTotalCount (count (table, client, page.getStartRow (), page.getStopRow ()));
        return page;
    }

    private void addColumn(Scan scan, byte[] family, byte[] qualifier) {
        scan.addColumn (family, qualifier);
    }

    private void setRowScan(Scan scan, String startRow, String stopRow) {
        scan.setCaching (HBASE_SCAN_CACHING_SIZE); // 每次RPC请求记录数
        // Cannot set batch on a scan using a filter that returns true for filter.hasFilterRow
        //scan.setBatch(HBASE_SCAN_BATCH_SIZE); // 每一批获取记录数
        if (startRow != null) {
            startRow = startRow + rkSep + startStr;
            scan.setStartRow (Bytes.toBytes (startRow));
        }
        if (stopRow != null) {
            stopRow = stopRow + rkSep + stopStr;
            scan.setStopRow (Bytes.toBytes (stopRow));
        }
    }

    private void setRowScan(Scan scan, HBasePage page) {
        // 添加行键范围
        String startRow = page.getStartRow ();
        String stopRow = page.getStopRow ();
        setRowScan (scan, startRow, stopRow);
        // 添加分页过滤器
        int pageIndex = page.getPageIndex ();
        int pageSize = page.getPageSize ();
        int scanSize = pageSize * pageIndex; // 扫描的数据条数
        Filter pageFilter = new PageFilter (scanSize);
        scan.setFilter (pageFilter);
    }

    private List<Map<String, String>> getMaps(ResultScanner rs, Map<Integer, String> colMap,
                                              byte[] family, byte[] qualifier, String separator, String dataType) {
        List<Map<String, String>> list = new ArrayList<> ();
        for (Result r : rs) {
            list.add (getMap (r, colMap, family, qualifier, separator, dataType));
        }
        rs.close ();
        return list;
    }

    private List<Map<String, String>> getMapsPage(ResultScanner rs, HBasePage page,
                                                  Map<Integer, String> colMap, byte[] family,
                                                  byte[] qualifier, String separator, String dataType) {
        int pageIndex = page.getPageIndex ();
        int pageSize = page.getPageSize ();
        long befNum = pageSize * (pageIndex - 1); // 不需要显示的数据条数
        long count = 0;
        List<Map<String, String>> list = new ArrayList<Map<String, String>> ();
        for (Result r : rs) {
            count++;
            if (count > befNum) {
                list.add (getMap (r, colMap, family, qualifier, separator, dataType));
            }
        }
        rs.close ();
        return list;
    }

    private Map<String, String> getMap(Result r, Map<Integer, String> colMap,
                                       byte[] family, byte[] qualifier, String separator, String dataType) {
        Map<String, String> map = new HashMap<String, String> ();
        String fqVal = Bytes.toString (r.getValue (family, qualifier));
        if (fqVal == null) fqVal = "";
        if (dataType.equalsIgnoreCase ("dsv")) { // 分隔符格式数据
            String[] fqVals = fqVal.split (separator, -1);
            // 注：如果上线后又修改需求，需要添加字段，则该检查需要注释掉
//        if (colMap.size() != fqVals.length) {
//            throw new RuntimeException("查询结果数与字段数不一致！");
//        }
            for (int i = 0; i < fqVals.length; i++) {
                String colName = colMap.get (i + 1);
                if (colName != null) {
                    map.put (colName, JSONUtil.encode (fqVals[i]));
                }
            }
        } else if (dataType.equalsIgnoreCase ("json")) { // JSON MAP格式数据
            Map<String, Object> result = JSONUtil.parseJSON2Map (fqVal);
            Set<Integer> keys = colMap.keySet ();
            for (Integer key : keys) {
                map.put (colMap.get (key), result.get (key).toString ());
            }
        }
        return map;
    }

    //得到16位的MD5
    private String md5_16(String str) {
        return DigestUtils.md5Hex (str).substring (8, 24);
    }

    //判断字符串长度，不足补空格
    private String realValue(String value, int length) {
        int len = 0;
        try {
            len = countCode (value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        if (len < length) {
            for (int i = 0; i < length - len; i++) {
                value = value + ' ';
            }
        }
        return value;
    }

    // 获取不同编码的字符串长度
    private int countCode(String str, String code) throws UnsupportedEncodingException {
        return str.getBytes (code).length;
    }

    @Override
    public boolean testDatasource(Datasource datasource) {
        HBaseDatasource hBaseDatasource = new HBaseDatasource (datasource);
        HConnection hConnection = null;
        try {
            hConnection = getConnection (hBaseDatasource);
            if (hConnection != null && !hConnection.isAborted ()) {
                // 尝试获取当中的表，如果获取抛异常则获取连接失败
                hConnection.getAdmin ().tableExists (TableName.valueOf ("TEST"));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            release (hBaseDatasource, hConnection);
        }
        return false;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        return null;
    }

    @Override
    public IqDslResponse select(IqDslRequest request) {
        return null;
    }

//    @Override
//    public IqDslResponse select(IqDslRequest request) {
//        logger.debug ("select=" + JSONUtil.parseObj2JSON (request));
//        Metadata metadata = request.getMetadata ();
//        String tbName = metadata.getTbName ();
//        Map<String, DataColumn> queryMap = new HashMap<> ();
//        List<DataColumn> metaQueryColumns = metadata.getQueryColumns ();
//        for (DataColumn column : metaQueryColumns) {
//            queryMap.put (column.getName (), column);
//        }
//        List<DataColumn> metaReturnColumns = metadata.getReturnColumns ();
//        HBaseMetadata hbaseMetadata = new HBaseMetadata (metadata.getPropertyMap ());
//        HBaseDatasource hbaseDatasource = new HBaseDatasource (metadata.getDatasource ());
//
//        DslRequest dslRequest = request.getRequest ();
//        Component where = dslRequest.getWhere ();
//        dslRequest.getLimit ();
//        List<QueryColumn> queryColumns = checkWhere (queryMap, where, new ArrayList<QueryColumn> ());
//        String startRow = getStartRow (queryColumns);
//        String stopRow = getStopRow (queryColumns);
//        logger.debug ("startRow:" + startRow + ", startRow:" + startRow);
//        Map<Integer, String> colMap = getColMap (metaReturnColumns);
//        HBasePage hbasePage = new HBasePage (page.getPageSize (), page.getPageIndex (), startRow, stopRow);
//        hbasePage = scanPage (hbaseDatasource, tbName, hbasePage, colMap, hbaseMetadata);
//        List<Map<String, String>> list = hbasePage.getRecords ();
//        response.setRecords (getRecords (list, returnColumns)); // 字段过滤并字段名改别名
//        page.setTotalCount (hbasePage.getTotalCount ());
//        response.setPage (page);
//        response.setStatus (Status.SUCCESS);
//        response.setStatusCode (StatusCode.SUCCESS);
//        return null;
//    }
//
//    private List<QueryColumn> checkWhere(Map<String, DataColumn> queryMap, Component component,
//                                         List<QueryColumn> queryColumns) {
//        if (component instanceof Composite) {
//            Composite composite = (Composite) component;
//            List<Component> components = composite.getComponents ();
//            for (Component c : components) {
//                queryColumns = checkWhere (queryMap, c, queryColumns);
//            }
//        } else if (component instanceof Dimension) {
//            Dimension dimension = (Dimension) component;
//            String name = dimension.getColumnName ();
//            DataColumn column = queryMap.get (name);
//            QueryColumn queryColumn = new QueryColumn ();
//            queryColumn.setName (name);
//            queryColumn.setSeq (column.getSeq ());
//            queryColumn.setDescribe (column.getDescribe ());
//            queryColumn.setType (column.getType ());
//            queryColumn.setLabel (name);
//            queryColumn.setLength (column.getLength ());
//            queryColumn.setOperator (tranOperator (dimension.getCompOper ()));
//            queryColumn.setValue (tranValue (dimension.getValues ()));
//            if (DataType.TIMESTAMP == column.getType ()) {
//                queryColumn.setNeed (false);
//            } else {
//                queryColumn.setNeed (true);
//            }
//            queryColumns.add (queryColumn);
//        }
//        return queryColumns;
//    }
//
//    private String tranValue(List<String> values) {
//        String value = "";
//        for (String val : values) {
//            value += (StringUtils.isBlank (value) ? "" : ",") + val;
//        }
//        return value;
//    }
//
//    private Operator tranOperator(ComparisonOperator compOper) {
//        if (ComparisonOperator.EQ == compOper) {
//            return Operator.EQ;
//        } else if (ComparisonOperator.NE == compOper) {
//            return Operator.NE;
//        } else if (ComparisonOperator.GT == compOper) {
//            return Operator.GT;
//        } else if (ComparisonOperator.LT == compOper) {
//            return Operator.LT;
//        } else if (ComparisonOperator.GE == compOper) {
//            return Operator.GE;
//        } else if (ComparisonOperator.LE == compOper) {
//            return Operator.LE;
//        } else if (ComparisonOperator.IN == compOper) {
//            return Operator.IN;
//        } else if (ComparisonOperator.LIKE == compOper) {
//            return Operator.LK;
//        } else {
//            throw new IllegalArgumentException ("不支持" + compOper.getValue () + "操作类型");
//        }
//    }

}
