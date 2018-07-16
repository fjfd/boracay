package com.hex.bigdata.udsp.iq.provider.impl;

import com.hex.bigdata.udsp.common.api.model.*;
import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.ExceptionUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.impl.factory.HBaseConnectionPoolFactory;
import com.hex.bigdata.udsp.iq.provider.impl.model.HBaseDatasource;
import com.hex.bigdata.udsp.iq.provider.impl.model.HBasePage;
import com.hex.bigdata.udsp.iq.provider.model.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by junjiem on 2017-3-3.
 */
//@Component("com.hex.bigdata.udsp.iq.provider.impl.HBaseProvider")
public class HBaseProvider implements Provider {

    static {
        // 解决winutils.exe不存在的问题
        try {
            File workaround = new File(".");
            System.getProperties().put("hadoop.home.dir",
                    workaround.getAbsolutePath());
            new File("./bin").mkdirs();
            new File("./bin/winutils.exe").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Logger logger = LogManager.getLogger(HBaseProvider.class);
    private static final FastDateFormat format8 = FastDateFormat.getInstance("yyyyMMdd");
    private static final FastDateFormat format10 = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final FastDateFormat format17 = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss");
    private static final FastDateFormat format19 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final String rkSep = "|";
    private static final String startStr = "";
    private static final String stopStr = "|";
    private static Map<String, HBaseConnectionPoolFactory> dataSourcePool;

    public IqResponse query(IqRequest request) {
        logger.debug("request=" + JSONUtil.parseObj2JSON(request));
        long bef = System.currentTimeMillis();
        IqResponse response = new IqResponse();
        response.setRequest(request);

        Application application = request.getApplication();
        int maxNum = application.getMaxNum();
        Metadata metadata = application.getMetadata();
        List<QueryColumn> queryColumns = application.getQueryColumns();
        Collections.sort(queryColumns, new Comparator<QueryColumn>() {
            public int compare(QueryColumn obj1, QueryColumn obj2) {
                return obj1.getSeq().compareTo(obj2.getSeq());
            }
        });
        List<ReturnColumn> returnColumns = application.getReturnColumns();
        List<OrderColumn> orderColumns = application.getOrderColumns();
        //获取元数据返回字段
        List<DataColumn> metaReturnColumns = metadata.getReturnColumns();

        String tbName = metadata.getTbName();
        Datasource datasource = metadata.getDatasource();

        HBaseDatasource hBaseDatasource = new HBaseDatasource(datasource.getPropertyMap());

        String startRow = getStartRow(queryColumns);
        String stopRow = getStopRow(queryColumns);
        logger.debug("startRow:" + startRow + ", startRow:" + startRow);
        Map<Integer, String> colMap = getColMap(metaReturnColumns);

        int maxSize = hBaseDatasource.getMaxNum();
        if (maxNum != 0) {
            maxSize = maxNum;
        }
        byte[] family = hBaseDatasource.getFamilyName();
        byte[] qualifier = hBaseDatasource.getQulifierName();
        String fqSep = hBaseDatasource.getDsvSeprator();
        String dataType = hBaseDatasource.getFqDataType();
        HConnection conn = null;
        HTableInterface hTable = null;
        try {
            conn = getConnection(hBaseDatasource);
            hTable = conn.getTable(tbName);
            List<Map<String, String>> list = scan(hTable, startRow, stopRow, colMap, maxSize, family, qualifier, fqSep, dataType);
            list = orderBy(list, orderColumns); // 排序处理
            response.setRecords(getRecords(list, returnColumns)); // 字段过滤并字段名改别名
            response.setStatus(Status.SUCCESS);
            response.setStatusCode(StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(e.getMessage());
            logger.warn(e.toString());
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                release(hBaseDatasource, conn);
            }
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        logger.debug("consumeTime=" + response.getConsumeTime());
        return response;
    }

    // 字段过滤并字段名改别名
    private List<com.hex.bigdata.udsp.common.api.model.Result> getRecords(List<Map<String, String>> resultList, List<ReturnColumn> returnColumns) {
        List<com.hex.bigdata.udsp.common.api.model.Result> records = null;
        if (resultList != null) {
            records = new ArrayList<com.hex.bigdata.udsp.common.api.model.Result>();
            for (Map<String, String> map : resultList) {
                com.hex.bigdata.udsp.common.api.model.Result result = new com.hex.bigdata.udsp.common.api.model.Result();
                Map<String, String> returnDataMap = new HashMap<String, String>();
                for (ReturnColumn item : returnColumns) {
                    String colName = item.getName();
                    String label = item.getLabel();
                    returnDataMap.put(label, map.get(colName));
                }
                result.putAll(returnDataMap);
                records.add(result);
            }
        }
        return records;
    }

    public IqResponse query(IqRequest request, int pageIndex, int pageSize) {
        logger.debug("request=" + JSONUtil.parseObj2JSON(request) + " pageIndex=" + pageIndex + " pageSize=" + pageSize);

        long bef = System.currentTimeMillis();
        IqResponse response = new IqResponse();
        response.setRequest(request);

        Application application = request.getApplication();
        int maxNum = application.getMaxNum();
        Metadata metadata = application.getMetadata();
        List<QueryColumn> queryColumns = application.getQueryColumns();
        Collections.sort(queryColumns, new Comparator<QueryColumn>() {
            public int compare(QueryColumn obj1, QueryColumn obj2) {
                return obj1.getSeq().compareTo(obj2.getSeq());
            }
        });
        List<ReturnColumn> returnColumns = application.getReturnColumns();
        List<OrderColumn> orderColumns = application.getOrderColumns();

        //获取元数据返回字段
        List<DataColumn> metaReturnColumns = metadata.getReturnColumns();

        String tbName = metadata.getTbName();
        Datasource datasource = metadata.getDatasource();

        HBaseDatasource hBaseDatasource = new HBaseDatasource(datasource.getPropertyMap());

        String startRow = getStartRow(queryColumns);
        String stopRow = getStopRow(queryColumns);
        logger.debug("startRow:" + startRow + ", startRow:" + startRow);
        Map<Integer, String> colMap = getColMap(metaReturnColumns);

        int maxSize = hBaseDatasource.getMaxNum();
        if (maxNum != 0) {
            maxSize = maxNum;
        }

        byte[] family = hBaseDatasource.getFamilyName();
        byte[] qualifier = hBaseDatasource.getQulifierName();
        String fqSep = hBaseDatasource.getDsvSeprator();
        String dataType = hBaseDatasource.getFqDataType();

        if (pageSize > maxSize) {
            pageSize = maxSize;
        }

        HBasePage hbasePage = new HBasePage();
        hbasePage.setPageIndex(pageIndex);
        hbasePage.setPageSize(pageSize);
        hbasePage.setStartRow(startRow);
        hbasePage.setStopRow(stopRow);

        HConnection conn = null;
        HTableInterface hTable = null;
        try {
            conn = getConnection(hBaseDatasource);
            hTable = conn.getTable(tbName);
            hbasePage = scanPage(hTable, hbasePage, colMap, family, qualifier, fqSep, dataType);
            List<Map<String, String>> list = hbasePage.getRecords();
            list = orderBy(list, orderColumns); // 排序处理
            response.setRecords(getRecords(list, returnColumns)); // 字段过滤并字段名改别名
            Page page = new Page();
            page.setPageIndex(pageIndex);
            page.setPageSize(pageSize);
            page.setTotalCount(hbasePage.getTotalCount());
            response.setPage(page);
            response.setStatus(Status.SUCCESS);
            response.setStatusCode(StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(e.getMessage());
            logger.warn(e.toString());
        } finally {
            if (hTable != null) {
                try {
                    hTable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                release(hBaseDatasource, conn);
            }
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        logger.debug("consumeTime=" + response.getConsumeTime());
        return response;
    }

    //-------------------------------------------分割线---------------------------------------------
    private synchronized HBaseConnectionPoolFactory getDataSource(HBaseDatasource datasource) {
        String dsId = datasource.getId();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<String, HBaseConnectionPoolFactory>();
        }
        HBaseConnectionPoolFactory factory = dataSourcePool.remove(dsId);
        if (factory == null) {
            GenericObjectPool.Config config = new GenericObjectPool.Config();
            config.lifo = true;
            config.minIdle = 1;
            config.maxIdle = 10;
            config.maxWait = 3000;
            config.maxActive = 5;
            config.timeBetweenEvictionRunsMillis = 30000;
            config.testWhileIdle = true;
            config.testOnBorrow = false;
            config.testOnReturn = false;
            factory = new HBaseConnectionPoolFactory(config, datasource);
        }
        dataSourcePool.put(dsId, factory);
        return factory;
    }

    private HConnection getConnection(HBaseDatasource datasource) {
        try {
            return getDataSource(datasource).getConnection();
        } catch (Exception e) {
            logger.warn(ExceptionUtil.getMessage(e));
            return null;
        }
    }

    private void release(HBaseDatasource datasource, HConnection conn) {
        getDataSource(datasource).releaseConnection(conn);
    }

    private Configuration getConfig(HBaseDatasource datasource) {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", datasource.getZkQuorum());
        conf.set("hbase.zookeeper.property.clientPort", datasource.getZkPort());
        return conf;
    }

    private List<Map<String, String>> orderBy(List<Map<String, String>> records, final List<OrderColumn> orderColumns) {
        Collections.sort(orderColumns, new Comparator<OrderColumn>() {
            public int compare(OrderColumn obj1, OrderColumn obj2) {
                return obj1.getSeq().compareTo(obj2.getSeq());
            }
        });
        // 多字段混合排序
        Collections.sort(records, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> obj1, Map<String, String> obj2) {
                int flg = 0;
                for (OrderColumn orderColumn : orderColumns) {
                    String colName = orderColumn.getName();
                    Order order = orderColumn.getOrder();
                    DataType dataType = orderColumn.getType();
                    String val1 = obj1.get(colName);
                    String val2 = obj2.get(colName);
                    if (StringUtils.isNotBlank(val1) && StringUtils.isNotBlank(val2)) {
                        flg = compareTo(val1, val2, order, dataType);
                        if (flg != 0) break;
                    }
                }
                return flg;
            }
        });
        return records;
    }

    private int compareTo(String str1, String str2, Order order, DataType dataType) {
        if (dataType == null || DataType.STRING.equals(dataType) || DataType.VARCHAR.equals(dataType)
                || DataType.CHAR.equals(dataType) || DataType.TIMESTAMP.equals(dataType)) {
            if (order != null && Order.DESC.equals(order)) {
                if (str1.compareTo(str2) > 0) {
                    return -1;
                } else if (str1.compareTo(str2) == 0) {
                    return 0;
                }
                return 1;
            } else {
                if (str1.compareTo(str2) > 0) {
                    return 1;
                } else if (str1.compareTo(str2) == 0) {
                    return 0;
                }
                return -1;
            }
        } else {
            if (order != null && Order.DESC.equals(order)) {
                if (Double.valueOf(str1).compareTo(Double.valueOf(str2)) > 0) {
                    return -1;
                } else if (str1.compareTo(str2) == 0) {
                    return 0;
                }
                return 1;
            } else {
                if (Integer.valueOf(str1).compareTo(Integer.valueOf(str2)) > 0) {
                    return 1;
                } else if (str1.compareTo(str2) == 0) {
                    return 0;
                }
                return -1;
            }
        }
    }

    private String getStartRow(List<QueryColumn> queryColumns) {
        String startRow = getMd5Str(queryColumns);
        for (QueryColumn queryColumn : queryColumns) {
            Operator operator = queryColumn.getOperator();
            boolean isNeed = queryColumn.isNeed();
            DataType dataType = queryColumn.getType();
            String value = queryColumn.getValue();
            int length = getLen(queryColumn.getLength());
            if (queryColumn.isNeed() && StringUtils.isBlank(value)) {
                throw new IllegalArgumentException("必输项值为空");
            }
            if (!Operator.EQ.equals(operator) && !Operator.GE.equals(operator) && !Operator.LE.equals(operator)) {
                throw new IllegalArgumentException("只支持等于、大于等于和小于等于操作");
            }
            // 只能是等于或大于等于
            if (Operator.EQ.equals(operator) || Operator.GE.equals(operator)) {
                if (isNeed && Operator.EQ.equals(operator)) { // 必填且是等于操作
                    value = realValue(value, length);
                } else if (Operator.GE.equals(operator)
                        || (!isNeed && DataType.TIMESTAMP.equals(dataType))) { // 大于等于操作 或者 选填或类型是TIMESTAMP
                    value = tarnDateStr(length, value);
                }
                if (StringUtils.isNotBlank(value)) {
                    startRow += (StringUtils.isBlank(startRow) ? value : this.rkSep + value);
                }
            }
            if (Operator.GE.equals(operator)) break; // 退出
        }
        return startRow;
    }

    private String getStopRow(List<QueryColumn> queryColumns) {
        String stopRow = getMd5Str(queryColumns);
        for (QueryColumn queryColumn : queryColumns) {
            Operator operator = queryColumn.getOperator();
            boolean isNeed = queryColumn.isNeed();
            DataType dataType = queryColumn.getType();
            String value = queryColumn.getValue();
            int length = getLen(queryColumn.getLength());
            if (queryColumn.isNeed() && StringUtils.isBlank(value)) {
                throw new IllegalArgumentException("必输项值为空");
            }
            if (!Operator.EQ.equals(operator) && !Operator.GE.equals(operator) && !Operator.LE.equals(operator)) {
                throw new IllegalArgumentException("只支持等于、大于等于和小于等于操作");
            }
            // 只能是等于或小于等于
            if (Operator.EQ.equals(operator) || Operator.LE.equals(operator)) {
                if (Operator.EQ.equals(operator)) { // 必填且是等于操作
                    value = realValue(value, length);
                } else if (Operator.LE.equals(operator)
                        || (!isNeed && DataType.TIMESTAMP.equals(dataType))) { // 小于等于操作 或者 选填或类型是TIMESTAMP
                    value = tarnDateStr(length, value);
                }
                if (StringUtils.isNotBlank(value)) {
                    stopRow += (StringUtils.isBlank(stopRow) ? value : this.rkSep + value);
                }
            }
            if (Operator.LE.equals(operator)) break; // 退出
        }
        return stopRow;
    }

    private String tarnDateStr(int length, String value) {
        if (length == 8 || length == 10 || length == 17 || length == 19) {
            Date date = strToDate(value);
            if (date != null) {
                if (length == 8) {
                    value = format8.format(date);
                } else if (length == 10) {
                    value = format10.format(date);
                } else if (length == 17) {
                    value = format17.format(date);
                } else if (length == 19) {
                    value = format19.format(date);
                }
            }
        }
        return value;
    }

    private Date strToDate(String dataStr) {
        Date date = null;
        try {
            if (dataStr.length() == 8) {
                date = format8.parse(dataStr);
            } else if (dataStr.length() == 10) {
                date = format10.parse(dataStr.replaceAll("/", "-"));
            } else if (dataStr.length() == 17) {
                date = format17.parse(dataStr);
            } else if (dataStr.length() == 19) {
                date = format19.parse(dataStr.replaceAll("/", "-"));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期字段传入的不是日期格式字符串参数");
        }
        return date;
    }

    private int getLen(String length) {
        int len = 0;
        if (StringUtils.isNotBlank(length) && StringUtils.isNumeric(length)) {
            try {
                len = Integer.valueOf(length);
            } catch (Exception e) {
                logger.debug(ExceptionUtil.getMessage(e));
            }
        }
        return len;
    }

    private String getMd5Str(List<QueryColumn> queryColumns) {
        String str = "";
        int count = 0;
        for (QueryColumn queryColumn : queryColumns) {
            if (queryColumn.isNeed() && Operator.EQ.equals(queryColumn.getOperator())) {
                String value = queryColumn.getValue();
                if (StringUtils.isBlank(value)) {
                    throw new IllegalArgumentException("必输项值为空");
                }
                str += (count == 0 ? value : this.rkSep + value);
                count++;
            } else {
                break;
            }
        }
        if (StringUtils.isNotBlank(str)) {
            str = md5_16(str);
        }
        return str;
    }

    private Map<Integer, String> getColMap(List<DataColumn> returnColumns) {
        Map<Integer, String> colMap = new HashMap<Integer, String>();
        Collections.sort(returnColumns, new Comparator<DataColumn>() {
            public int compare(DataColumn obj1, DataColumn obj2) {
                return obj1.getSeq().compareTo(obj2.getSeq());
            }
        });
        for (int i = 0; i < returnColumns.size(); i++) {
            colMap.put(i + 1, returnColumns.get(i).getName());
        }
        return colMap;
    }

    private int count(HTableInterface table, String startRow,
                      String stopRow) throws Exception {
        Scan scan = new Scan();
        setRowScan(scan, startRow, stopRow);
        return count(table, scan);
    }

    private int count(HTableInterface table, Scan scan) throws Exception {
        scan.setCaching(500);
        scan.setCacheBlocks(false);
        scan.setFilter(new FirstKeyOnlyFilter());
        ResultScanner rs = table.getScanner(scan);
        int count = 0;
        while (rs.next() != null) {
            count++;
        }
        rs.close();
        return count;
    }

    private List<Map<String, String>> scan(HTableInterface table, Scan scan, Map<Integer, String> colMap,
                                           byte[] family, byte[] qualifier, String fqSep, String dataType) throws Exception {
        ResultScanner rs = table.getScanner(scan);
        return getMaps(rs, colMap, family, qualifier, fqSep, dataType);
    }

    private List<Map<String, String>> scan(HTableInterface table, String startRow, String stopRow, Map<Integer, String> colMap,
                                           long maxSize, byte[] family, byte[] qualifier, String fqSep, String dataType) throws Exception {
        Scan scan = new Scan();
        addColumn(scan, family, qualifier);
        setRowScan(scan, startRow, stopRow);
        scan.setFilter(new PageFilter(maxSize));
        return scan(table, scan, colMap, family, qualifier, fqSep, dataType);
    }

    private HBasePage scanPage(HTableInterface table, Scan scan, HBasePage HBasePage,
                               Map<Integer, String> colMap, byte[] family, byte[] qualifier,
                               String fqSep, String dataType) throws Exception {
        int totalCount = count(table, HBasePage.getStartRow(), HBasePage.getStopRow());

        setRowScan(scan, HBasePage);
        ResultScanner rs = table.getScanner(scan);
        List<Map<String, String>> records = getMapsPage(rs, HBasePage, colMap, family, qualifier, fqSep, dataType);

        HBasePage.setRecords(records);
        HBasePage.setTotalCount(totalCount);
        return HBasePage;
    }

    private HBasePage scanPage(HTableInterface table, HBasePage HBasePage, Map<Integer, String> colMap,
                               byte[] family, byte[] qualifier, String fqSep, String dataType) throws Exception {
        Scan scan = new Scan();
        addColumn(scan, family, qualifier);
        return scanPage(table, scan, HBasePage, colMap, family, qualifier, fqSep, dataType);
    }

    private void addColumn(Scan scan, byte[] family, byte[] qualifier) {
        scan.addColumn(family, qualifier);
    }

    private void setRowScan(Scan scan, String startRow, String stopRow) {
        if (startRow != null) {
            startRow = startRow + this.rkSep + this.startStr;
            scan.setStartRow(Bytes.toBytes(startRow));
        }
        if (stopRow != null) {
            stopRow = stopRow + this.rkSep + this.stopStr;
            scan.setStopRow(Bytes.toBytes(stopRow));
        }
    }

    private void setRowScan(Scan scan, HBasePage HBasePage) {
        // 添加行键范围
        String startRow = HBasePage.getStartRow();
        String stopRow = HBasePage.getStopRow();
        setRowScan(scan, startRow, stopRow);
        // 添加分页过滤器
        int pageIndex = HBasePage.getPageIndex();
        int pageSize = HBasePage.getPageSize();
        int scanNum = pageSize * pageIndex; // 扫描的数据条数
        Filter pageFilter = new PageFilter(scanNum);
        scan.setFilter(pageFilter);
    }

    private List<Map<String, String>> getMaps(ResultScanner rs, Map<Integer, String> colMap,
                                              byte[] family, byte[] qualifier, String fqSep, String dataType) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Result r : rs) {
            list.add(getMap(r, colMap, family, qualifier, fqSep, dataType));
        }
        rs.close();
        return list;
    }

    private List<Map<String, String>> getMapsPage(ResultScanner rs, HBasePage HBasePage,
                                                  Map<Integer, String> colMap, byte[] family,
                                                  byte[] qualifier, String fqSep, String dataType) {
        int pageIndex = HBasePage.getPageIndex();
        int pageSize = HBasePage.getPageSize();
        int befNum = pageSize * (pageIndex - 1); // 不需要显示的数据条数
        int count = 0;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Result r : rs) {
            count++;
            if (count > befNum) {
                list.add(getMap(r, colMap, family, qualifier, fqSep, dataType));
            }
        }
        rs.close();
        return list;
    }

    private Map<String, String> getMap(Result r, Map<Integer, String> colMap,
                                       byte[] family, byte[] qualifier, String fqSep, String dataType) {
        Map<String, String> map = new HashMap<String, String>();
        String fqVal = Bytes.toString(r.getValue(family, qualifier));
        if (fqVal == null) fqVal = "";
        if (dataType.equalsIgnoreCase("dsv")) { // 分隔符格式数据
            String[] fqVals = fqVal.split(fqSep, -1);
            // 注：如果上线后又修改需求，需要添加字段，则该检查需要注释掉
//        if (colMap.size() != fqVals.length) {
//            throw new RuntimeException("查询结果数与字段数不一致！");
//        }
            for (int i = 0; i < fqVals.length; i++) {
                String colName = colMap.get(i + 1);
                if (colName != null) {
                    map.put(colName, JSONUtil.encode(fqVals[i]));
                }
            }
        } else if (dataType.equalsIgnoreCase("json")) { // JSON MAP格式数据
            Map<String, Object> result = JSONUtil.parseJSON2Map(fqVal);
            Set<Integer> keys = colMap.keySet();
            for (Integer key : keys) {
                map.put(colMap.get(key), result.get(key).toString());
            }
        }
        return map;
    }

    //得到16位的MD5
    private String md5_16(String str) {
        return DigestUtils.md5Hex(str).substring(8, 24);
    }

    //判断字符串长度，不足补空格
    private String realValue(String value, int length) {
        int len = 0;
        try {
            len = countCode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
        return str.getBytes(code).length;
    }

    public boolean testDatasource(Datasource datasource) {
        boolean canConnection = true;
        HBaseDatasource hBaseDatasource = new HBaseDatasource(datasource.getProperties());
        HConnection hConnection = null;
        try {
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", hBaseDatasource.getZkQuorum());
            conf.set("hbase.zookeeper.property.clientPort", hBaseDatasource.getZkPort());
            conf.set("hbase.rpc.timeout", "2");
            conf.set("hbase.client.retries.number", "3");
            conf.set("zookeeper.recovery.retry", "1");
            hConnection = HConnectionManager.createConnection(conf);
            if (hConnection == null || hConnection.isAborted()) {
                canConnection = false;
            } else {
                //尝试获取当中的表，如果获取抛异常则获取连接失败
                hConnection.getAdmin().tableExists(TableName.valueOf("TEST"));
            }
        } catch (Exception e) {
            //e.printStackTrace();
            canConnection = false;
        } finally {
            if (hConnection != null) {
                try {
                    hConnection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return canConnection;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        return null;
    }

    public static void main(String[] args){
        System.out.println(DigestUtils.md5Hex("9010901228600001").substring(8, 24));
    }

}
