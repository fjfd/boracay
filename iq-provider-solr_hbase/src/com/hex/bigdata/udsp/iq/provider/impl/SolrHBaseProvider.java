package com.hex.bigdata.udsp.iq.provider.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.impl.factory.HBaseConnectionPoolFactory;
import com.hex.bigdata.udsp.iq.provider.impl.model.*;
import com.hex.bigdata.udsp.iq.provider.impl.util.SolrUtil;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by junjiem on 2017-2-15.
 */
public class SolrHBaseProvider implements Provider {

    private static final int HBASE_GET_BATCH_SIZE = 1024;

    private static Logger logger = LoggerFactory.getLogger (SolrHBaseProvider.class);

    private static Map<String, HBaseConnectionPoolFactory> dataSourcePool;

    @Override
    public IqResponse query(IqRequest request) {
        logger.debug ("request=" + JSONUtil.parseObj2JSON (request));
        long bef = System.currentTimeMillis ();
        IqResponse response = new IqResponse ();

        try {
            Application application = request.getApplication ();
            Metadata metadata = application.getMetadata ();
            List<QueryColumn> queryColumns = application.getQueryColumns ();
            List<ReturnColumn> returnColumns = application.getReturnColumns ();
            List<OrderColumn> orderColumns = application.getOrderColumns ();
            String tbName = metadata.getTbName ();
            SolrHBaseMetadata solrHBaseMetadata = new SolrHBaseMetadata (metadata.getPropertyMap ());
            List<DataColumn> metaReturnColumns = metadata.getReturnColumns ();
            SolrHBaseDatasource solrHBaseDatasource = new SolrHBaseDatasource (metadata.getDatasource ());
            String primaryKey = solrHBaseMetadata.getSolrPrimaryKey ();
            SolrQuery query = getSolrQuery (queryColumns, orderColumns, primaryKey, solrHBaseDatasource.getMaxSize ());
            Map<Integer, String> colMap = getColMap (metaReturnColumns);
            List<Map<String, String>> list = search (solrHBaseDatasource, tbName, query, colMap, solrHBaseMetadata);
            list = orderBy (list, queryColumns, orderColumns); // 排序处理
            response.setRecords (getRecords (list, returnColumns));
            response.setStatus (Status.SUCCESS);
            response.setStatusCode (StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace ();
            response.setStatus (Status.DEFEAT);
            response.setStatusCode (StatusCode.DEFEAT);
            response.setMessage (e.toString ());
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
            List<ReturnColumn> returnColumns = application.getReturnColumns ();
            List<OrderColumn> orderColumns = application.getOrderColumns ();
            String tbName = metadata.getTbName ();
            SolrHBaseMetadata solrHBaseMetadata = new SolrHBaseMetadata (metadata.getPropertyMap ());
            List<DataColumn> metaReturnColumns = metadata.getReturnColumns ();
            SolrHBaseDatasource solrHBaseDatasource = new SolrHBaseDatasource (metadata.getDatasource ());
            SolrQuery query = getSolrQuery (queryColumns, orderColumns, solrHBaseMetadata.getSolrPrimaryKey (), page.getPageIndex (), page.getPageSize ());
            Map<Integer, String> colMap = getColMap (metaReturnColumns);
            HBasePage hbasePage = searchPage (solrHBaseDatasource, tbName, query, page.getPageIndex (), page.getPageSize (), colMap, solrHBaseMetadata);
            List<Map<String, String>> list = orderBy (hbasePage.getRecords (), queryColumns, orderColumns); // 排序处理
            response.setRecords (getRecords (list, returnColumns));
            page.setTotalCount (hbasePage.getTotalCount ());
            response.setPage (page);
            response.setStatus (Status.SUCCESS);
            response.setStatusCode (StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace ();
            response.setStatus (Status.DEFEAT);
            response.setStatusCode (StatusCode.DEFEAT);
            response.setMessage (e.toString ());
        }

        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        response.setConsumeTime (consumeTime);

        logger.debug ("consumeTime=" + response.getConsumeTime ());
        return response;
    }

    private synchronized HBaseConnectionPoolFactory getDataSource(SolrHBaseDatasource datasource) {
        HBaseDatasource hBaseDatasource = new HBaseDatasource (datasource);
        String dsId = datasource.getId ();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<> ();
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
            factory = new HBaseConnectionPoolFactory (config, hBaseDatasource);
        }
        dataSourcePool.put (dsId, factory);
        return factory;
    }

    private HConnection getConnection(SolrHBaseDatasource datasource) throws Exception {
        return getDataSource (datasource).getConnection ();
    }

    private void release(SolrHBaseDatasource datasource, HConnection conn) {
        getDataSource (datasource).releaseConnection (conn);
    }

    private SolrQuery getSolrQuery(List<QueryColumn> queryColumns, List<OrderColumn> orderColumns, String primaryKey, int rows) {
        return new SolrQuery () //
                .setQuery (getQuery (queryColumns)) //
                .setStart (0) //
                .setRows (rows) //
                .setSorts (getSort (queryColumns, orderColumns)) //
                .setFields (primaryKey);
    }

    private SolrQuery getSolrQuery(List<QueryColumn> queryColumns, List<OrderColumn> orderColumns,
                                   String primaryKey, int pageIndex, int pageSize) {
        return new SolrQuery () //
                .setQuery (getQuery (queryColumns)) //
                .setStart ((pageIndex - 1) * pageSize) //
                .setRows (pageSize) //
                .setSorts (getSort (queryColumns, orderColumns)) //
                .setFields (primaryKey);
    }

    private String getQuery(List<QueryColumn> queryColumns) {
        Collections.sort (queryColumns, new Comparator<QueryColumn> () {
            public int compare(QueryColumn obj1, QueryColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        StringBuffer sb = new StringBuffer ("*:*");
        for (QueryColumn queryColumn : queryColumns) {
            String name = queryColumn.getName ();
            String value = queryColumn.getValue ();
            DataType type = queryColumn.getType ();
            Operator operator = queryColumn.getOperator ();
            if (StringUtils.isNotBlank (value)) {
                if (Operator.EQ.equals (operator)) {
                    sb.append (" AND " + name + ":" + getValue (type, value));
                } else if (Operator.GT.equals (operator)) {
                    sb.append (" AND " + name + ":[" + getValue (type, value) + " TO *] AND " + name + ":(* NOT " + getValue (type, value) + ")");
                } else if (Operator.LT.equals (operator)) {
                    sb.append (" AND " + name + ":[* TO " + getValue (type, value) + "] AND " + name + ":(* NOT " + getValue (type, value) + ")");
                } else if (Operator.GE.equals (operator)) {
                    sb.append (" AND " + name + ":[" + getValue (type, value) + " TO *]");
                } else if (Operator.LE.equals (operator)) {
                    sb.append (" AND " + name + ":[* TO " + getValue (type, value) + "]");
                } else if (Operator.NE.equals (operator)) {
                    sb.append (" AND " + name + ":(* NOT " + getValue (type, value) + ")"); // sb.append(" AND " + name + ":(-" + getValue(type, value) + ")");
                } else if (Operator.LK.equals (operator)) {
                    sb.append (" AND " + name + ":*" + getValue (value) + "*");
                } else if (Operator.RLIKE.equals (operator)) {
                    sb.append (" AND " + name + ":" + getValue (value) + "*");
                } else if (Operator.IN.equals (operator)) {
                    sb.append (" AND " + name + ":(");
                    String[] values = value.split (",");
                    for (int i = 0; i < values.length; i++) {
                        if (StringUtils.isBlank (values[i])) {
                            continue;
                        }
                        sb.append (getValue (type, values[i]));
                        if (i < values.length - 1) {
                            sb.append (" or ");
                        }
                        if (i == values.length - 1) {
                            sb.append (")");
                        }
                    }
                }
            }
        }
        logger.debug ("q=" + sb.toString ());
        return sb.toString ();
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

    private List<Map<String, String>> orderBy(List<Map<String, String>> records, List<QueryColumn> queryColumns,
                                              final List<OrderColumn> orderColumns) {
        Map<String, QueryColumn> map = new HashMap<> ();
        for (QueryColumn queryColumn : queryColumns) {
            map.put (queryColumn.getName (), queryColumn);
        }
        //  排序字段不在查询字段中，只能自己实现排序
        final List<OrderColumn> newOrderColumns = new ArrayList<> ();
        for (OrderColumn orderColumn : orderColumns) {
            if (map.get (orderColumn.getName ()) == null) {
                newOrderColumns.add (orderColumn);
            }
        }
        // 排序字段按照序号排序
        Collections.sort (newOrderColumns, new Comparator<OrderColumn> () {
            @Override
            public int compare(OrderColumn obj1, OrderColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        // 多字段混合排序
        Collections.sort (records, new Comparator<Map<String, String>> () {
            public int compare(Map<String, String> obj1, Map<String, String> obj2) {
                int flg = 0;
                for (OrderColumn orderColumn : newOrderColumns) {
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
        return records;
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

    private List<SolrQuery.SortClause> getSort(List<QueryColumn> queryColumns, List<OrderColumn> orderColumns) {
        Map<String, QueryColumn> map = new HashMap<> ();
        for (QueryColumn queryColumn : queryColumns) {
            map.put (queryColumn.getName (), queryColumn);
        }
        // 排序字段按照序号排序
        Collections.sort (orderColumns, new Comparator<OrderColumn> () {
            @Override
            public int compare(OrderColumn obj1, OrderColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        // 排序字段集合
        List<SolrQuery.SortClause> list = new ArrayList<> ();
        for (OrderColumn orderColumn : orderColumns) {
            String colName = orderColumn.getName ();
            // 排序字段在查询字段中，可以使用solr自带的排序方式
            if (map.get (colName) != null) {
                Order order = orderColumn.getOrder ();
                if (order != null && Order.DESC.equals (order)) {
                    list.add (new SolrQuery.SortClause (colName, SolrQuery.ORDER.desc));
                } else {
                    list.add (new SolrQuery.SortClause (colName, SolrQuery.ORDER.asc));
                }
            }
        }
        return list;
    }

    private Map<Integer, String> getColMap(List<DataColumn> returnColumns) {
        Map<Integer, String> colMap = new HashMap<> ();
        Collections.sort (returnColumns, new Comparator<DataColumn> () {
            @Override
            public int compare(DataColumn obj1, DataColumn obj2) {
                return new Integer (obj1.getSeq ()).compareTo (new Integer (obj2.getSeq ()));
            }
        });
        for (int i = 0; i < returnColumns.size (); i++) {
            colMap.put (i + 1, returnColumns.get (i).getName ());
        }
        return colMap;
    }

    private HBasePage searchPage(SolrHBaseDatasource datasource, String tableName, SolrQuery query, int pageIndex, int pageSize,
                                 Map<Integer, String> colMap, SolrHBaseMetadata metadata) throws Exception {
        List<Map<String, String>> records = new ArrayList<> ();
        SolrHBasePage solrHBasePage = searchPage (datasource, tableName, query, pageIndex, pageSize, metadata.getSolrPrimaryKey ());

        // HBase单条查询
//        for (String id : solrHBasePage.getRecords()) {
//            records.add(get(datasource, tableName, id, colMap));
//        }

        // HBase批量查询
        List<String> ids = solrHBasePage.getRecords ();
        if (ids != null && ids.size () > 0) {
            if (ids.size () <= HBASE_GET_BATCH_SIZE) {
                records.addAll (gets (datasource, tableName, ids, colMap, metadata));
            } else {
                List<String> rowkeys = new ArrayList<> ();
                for (int i = 0; i < ids.size (); i++) {
                    rowkeys.add (ids.get (i));
                    if (i + 1 == HBASE_GET_BATCH_SIZE) {
                        records.addAll (gets (datasource, tableName, rowkeys, colMap, metadata));
                        rowkeys = new ArrayList<> ();
                    }
                }
                if (rowkeys.size () > 0) {
                    records.addAll (gets (datasource, tableName, rowkeys, colMap, metadata));
                }
            }
        }

        return new HBasePage (records, solrHBasePage.getPageIndex (), solrHBasePage.getPageSize (), solrHBasePage.getTotalCount ());
    }

    private List<Map<String, String>> search(SolrHBaseDatasource datasource, String tableName, SolrQuery query,
                                             Map<Integer, String> colMap, SolrHBaseMetadata metadata) throws Exception {
        List<String> ids = search (datasource, tableName, query, metadata.getSolrPrimaryKey ());
        List<Map<String, String>> records = new ArrayList<> ();

        // HBase单条查询
//        for (String id : ids) {
//            records.add(get(datasource, tableName, id, colMap));
//        }

        // HBase批量查询
        if (ids != null && ids.size () > 0) {
            if (ids.size () <= HBASE_GET_BATCH_SIZE) {
                records.addAll (gets (datasource, tableName, ids, colMap, metadata));
            } else {
                List<String> rowkeys = new ArrayList<> ();
                for (int i = 0; i < ids.size (); i++) {
                    rowkeys.add (ids.get (i));
                    if (i + 1 == HBASE_GET_BATCH_SIZE) {
                        records.addAll (gets (datasource, tableName, rowkeys, colMap, metadata));
                        rowkeys = new ArrayList<> ();
                    }
                }
                if (rowkeys.size () > 0) {
                    records.addAll (gets (datasource, tableName, rowkeys, colMap, metadata));
                }
            }
        }

        return records;
    }

    // ---------------------------Solr------------------------------
    private SolrHBasePage searchPage(SolrHBaseDatasource datasource, String collectionName, SolrQuery query,
                                     int pageIndex, int pageSize, String solrPrimaryKey) throws Exception {
        // 获取QueryResponse
        QueryResponse rsp = getQueryResponse (datasource, collectionName, query);
        // 总行数
        SolrDocumentList results = rsp.getResults ();
        long totalCount = results.getNumFound ();
        // 唯一键集合
        List<String> records = new ArrayList<> ();
        for (SolrDocument result : results) {
            String id = (String) result.get (solrPrimaryKey);
            if (StringUtils.isNotBlank (id)) {
                records.add (id);
            }
        }
        return new SolrHBasePage (records, pageIndex, pageSize, totalCount);
    }

    public List<String> search(SolrHBaseDatasource datasource, String collectionName, SolrQuery query, String solrPrimaryKey) throws Exception {
        // 获取QueryResponse
        QueryResponse rsp = getQueryResponse (datasource, collectionName, query);
        // ID集合
        SolrDocumentList results = rsp.getResults ();
        List<String> records = new ArrayList<> ();
        for (SolrDocument result : results) {
            String id = (String) result.get (solrPrimaryKey);
            if (StringUtils.isNotBlank (id)) {
                records.add (id);
            }
        }
        return records;
    }

    private QueryResponse getQueryResponse(SolrHBaseDatasource datasource, String collectionName, SolrQuery query) throws Exception {
        return getSolrServer (datasource, collectionName).query (query);
    }

    private SolrServer getSolrServer(SolrHBaseDatasource datasource, String collectionName) throws MalformedURLException {
        if (StringUtils.isBlank (collectionName)) {
            throw new IllegalArgumentException ("collection name不能为空");
        }
        String[] tempServers = datasource.getSolrServers ().split (",");
        String[] servers = new String[tempServers.length];
        for (int i = 0; i < tempServers.length; i++) {
            servers[i] = "http://" + tempServers[i] + "/solr/" + collectionName;
        }
        return new LBHttpSolrServer (servers);
    }

    // ------------------------------HBase----------------------------------
    private Map<String, String> get(SolrHBaseDatasource datasource, String tableName, String rowkey,
                                    Map<Integer, String> colMap, SolrHBaseMetadata metadata) throws Exception {
        HConnection conn = null;
        HTableInterface table = null;
        Map<String, String> map = null;
        try {
            conn = getConnection (datasource);
            table = conn.getTable (tableName);
            map = get (table, rowkey, colMap, metadata.getFamilyName (),
                    metadata.getQualifierName (), metadata.getDsvSeparator (), metadata.getFqDataType ());
        } finally {
            if (table != null) {
                table.close ();
            }
            if (conn != null) {
                release (datasource, conn);
            }
        }
        return map;
    }

    private List<Map<String, String>> gets(SolrHBaseDatasource datasource, String tableName, List<String> rowkeys,
                                           Map<Integer, String> colMap, SolrHBaseMetadata metadata) throws Exception {
        HConnection conn = null;
        HTableInterface table = null;
        List<Map<String, String>> list = null;
        try {
            conn = getConnection (datasource);
            table = conn.getTable (tableName);
            list = gets (table, rowkeys, colMap, metadata.getFamilyName (),
                    metadata.getQualifierName (), metadata.getDsvSeparator (), metadata.getFqDataType ());
        } finally {
            if (table != null) {
                table.close ();
            }
            if (conn != null) {
                release (datasource, conn);
            }
        }
        return list;
    }

    private List<Map<String, String>> gets(HTableInterface table, List<String> rowkeys, Map<Integer, String> colMap,
                                           byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        List<Get> gets = new ArrayList<> ();
        for (String rowkey : rowkeys) {
            Get get = new Get (Bytes.toBytes (rowkey));
            addColumn (get, family, qualifier);
            gets.add (get);
        }
        return getMaps (table, gets, colMap, family, qualifier, separator, dataType);
    }

    private List<Map<String, String>> getMaps(HTableInterface table, List<Get> gets, Map<Integer, String> colMap,
                                              byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        Result[] rs = table.get (gets);
        return getMaps (rs, colMap, family, qualifier, separator, dataType);
    }

    private List<Map<String, String>> getMaps(Result[] rs, Map<Integer, String> colMap,
                                              byte[] family, byte[] qualifier, String separator, String dataType) {
        List<Map<String, String>> list = null;
        if (rs != null && rs.length != 0) {
            list = new ArrayList<> ();
            for (Result r : rs) {
                list.add (getMap (r, colMap, family, qualifier, separator, dataType));
            }
        }
        return list;
    }

    private Map<String, String> get(HTableInterface table, String rowkey, Map<Integer, String> colMap,
                                    byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        Get get = new Get (Bytes.toBytes (rowkey));
        addColumn (get, family, qualifier);
        return get (table, get, colMap, family, qualifier, separator, dataType);
    }

    private Map<String, String> get(HTableInterface table, Get get, Map<Integer, String> colMap,
                                    byte[] family, byte[] qualifier, String separator, String dataType) throws Exception {
        Result r = table.get (get);
        return getMap (r, colMap, family, qualifier, separator, dataType);
    }

    private void addColumn(Get get, byte[] family, byte[] qualifier) {
        get.addColumn (family, qualifier);
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

    @Override
    public boolean testDatasource(Datasource datasource) {
        boolean HbaseCanConnection = false;
        boolean SolrCanConnection = false;
        SolrHBaseDatasource solrHBaseDatasource = new SolrHBaseDatasource (datasource);
        HConnection hConnection = null;
        HttpURLConnection connection = null;
        URL url = null;
        try {
            Configuration conf = HBaseConfiguration.create ();
            conf.set ("hbase.zookeeper.quorum", solrHBaseDatasource.getZkQuorum ());
            conf.set ("hbase.zookeeper.property.clientPort", solrHBaseDatasource.getZkPort ());
            conf.set ("hbase.rpc.timeout", "2");
            conf.set ("hbase.client.retries.number", "3");
            conf.set ("zookeeper.recovery.retry", "1");
            hConnection = HConnectionManager.createConnection (conf);
            if (hConnection != null && !hConnection.isAborted ()) {
                //尝试获取当中的表，如果获取抛异常则获取连接失败
                hConnection.getAdmin ().tableExists (TableName.valueOf ("TEST"));
                HbaseCanConnection = true;
            }
            //测试获取solr连接
            String[] servers = solrHBaseDatasource.getSolrServers ().split (",");
            for (String server : servers) {
                try {
                    url = new URL ("http://" + server + "/solr");
                    connection = (HttpURLConnection) url.openConnection ();
                    connection.setDoInput (true);
                    connection.setDoOutput (true);
                    connection.setUseCaches (false);
                    connection.setInstanceFollowRedirects (true);
                    connection.connect ();
                    SolrCanConnection = true;
                    break;
                } catch (Exception e) {
                    e.printStackTrace ();
                    logger.warn ("获取solr连接失败的地址为：" + (url == null ? "" : url.toString ()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            HbaseCanConnection = false;
        } finally {
            if (hConnection != null) {
                try {
                    hConnection.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            if (connection != null) {
                connection.disconnect ();
            }
        }
        return HbaseCanConnection && SolrCanConnection;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        SolrDatasource solrDatasource = new SolrDatasource (datasource);
        String solrServers = solrDatasource.getSolrServers ();
        return getColumns (schemaName, solrServers);
    }

    @Override
    public IqDslResponse select(IqDslRequest request) {
        throw new RuntimeException ("Solr+HBase目前暂时不支持DSL");
    }

    private List<MetadataCol> getColumns(String collectionName, String solrServers) {
        if (StringUtils.isEmpty (collectionName) || StringUtils.isEmpty (solrServers)) {
            return null;
        }
        String response = "";
        String[] addresses = solrServers.split (",");
        for (String solrServer : addresses) {
            String url = "http://" + solrServer + "/solr/" + collectionName + "/schema/fields";
            response = SolrUtil.sendGet (url, "");
            if (StringUtils.isNotBlank (response)) {
                break;
            }
        }
        if (StringUtils.isBlank (response)) {
            throw new RuntimeException (collectionName + "表名不存在");
        }
        JSONObject rs = JSONObject.parseObject (response);
        JSONArray fields = (JSONArray) rs.get ("fields");
        List<MetadataCol> metadataCols = new ArrayList<> ();
        MetadataCol mdCol = null;
        for (int i = 0; i < fields.size (); i++) {
            mdCol = new MetadataCol ();
            mdCol.setSeq ((short) i);
            mdCol.setName ((String) fields.getJSONObject (i).get ("name"));
            mdCol.setDescribe ((String) fields.getJSONObject (i).get ("name"));
            mdCol.setType (SolrUtil.getColType ((String) fields.getJSONObject (i).get ("type")));
            mdCol.setIndexed ((boolean) fields.getJSONObject (i).get ("indexed"));
            mdCol.setStored ((boolean) fields.getJSONObject (i).get ("stored"));
            mdCol.setPrimary (fields.getJSONObject (i).get ("uniqueKey") == null ? false : true);
            metadataCols.add (mdCol);
        }
        return metadataCols;
    }

    private String getValue(String value) {
        if (value.startsWith ("-")) {
            value = "\\" + value;
        }
        return value;
    }

    private String getValue(DataType type, String value) {
        if (DataType.STRING.equals (type) || DataType.VARCHAR.equals (type) || DataType.CHAR.equals (type)) {
            value = "\"" + value + "\"";
        } else {
            value = getValue (value);
        }
        return value;
    }
}
