package com.hex.bigdata.udsp.iq.provider.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.impl.factory.RedisConnectionPoolFactory;
import com.hex.bigdata.udsp.iq.provider.impl.model.RedisDatasource;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * redis接口实现类
 */
public class RedisProvider implements Provider {
    private static Logger logger = LogManager.getLogger (RedisProvider.class);
    private static Map<String, RedisConnectionPoolFactory> dataSourcePool;
    private static final String rkSep = "|";
    private static final String tableColumnSeq = ":";

    @Override
    public IqResponse query(IqRequest request) {
        return query (request, null);
    }

    private String getRedisQuery(List<DataColumn> metaDataQueryColumns, List<QueryColumn> queryColumns, String tableName) {
        Map<Short, String> canQueryColumn = new HashMap ();
        for (DataColumn metaDataQueryColumn : metaDataQueryColumns) {
            canQueryColumn.put (metaDataQueryColumn.getSeq (), metaDataQueryColumn.getName ());
        }
        Map<String, QueryColumn> queryColumnValueMap = new HashMap ();
        for (QueryColumn queryColumn : queryColumns) {
            queryColumnValueMap.put (queryColumn.getName (), queryColumn);
        }
        StringBuffer queryString = new StringBuffer ("");
        queryString.append (tableName);
        queryString.append (tableColumnSeq);
        Operator operator;
        for (Short i = 1; canQueryColumn.get (i) != null; i++) {

            QueryColumn queryColumn = queryColumnValueMap.get (canQueryColumn.get (i));
            if (queryColumn == null || StringUtils.isBlank (queryColumn.getValue ())) {
                queryString.append ("*");
                queryString.append (rkSep);
                continue;
            }
            operator = queryColumn.getOperator ();
            if (Operator.LK.equals (operator)) {
                queryString.append ("*");
                queryString.append (queryColumn.getValue ());
                queryString.append ("*");
            } else if (Operator.RLIKE.equals (operator)) {
                queryString.append (queryColumn.getValue ());
                queryString.append ("*");
            } else if (Operator.EQ.equals (operator)) {
                queryString.append (queryColumn.getValue ());
            } else {
                throw new IllegalArgumentException ("redis不支持" + operator.getValue () + "操作类型！");
            }
            //最后一个|不用去除的，防止模糊匹配时匹配到错误数据
            queryString.append (rkSep);

        }
        return queryString.toString ();
    }

    @Override
    public IqResponse query(IqRequest request, Page page) {
        logger.debug ("request=" + JSONUtil.parseObj2JSON (request));
        long bef = System.currentTimeMillis ();
        IqResponse response = new IqResponse ();

        try {
            Application application = request.getApplication ();
            Metadata metadata = application.getMetadata ();
            List<QueryColumn> queryColumns = application.getQueryColumns ();
            List<ReturnColumn> returnColumns = application.getReturnColumns ();
            List<OrderColumn> orderColumns = application.getOrderColumns ();
            List<DataColumn> metaReturnColumns = metadata.getReturnColumns ();
            RedisDatasource redisDatasource = new RedisDatasource (metadata.getDatasource ());
            String tableName = metadata.getTbName ();
            String query = getRedisQuery (metadata.getQueryColumns (), queryColumns, tableName);
            String fqSep = redisDatasource.getSeprator ();
            List<Map<String, String>> list = null;
            if (page != null) {
                page.setTotalCount (getCountNum (query, redisDatasource));
                list = search (fqSep, query, redisDatasource, metaReturnColumns, page);
            } else {
                list = search (fqSep, query, redisDatasource, metaReturnColumns, redisDatasource.getMaxSize ());
            }
            list = orderBy (list, orderColumns); // 排序
            response.setPage (page);
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

    private synchronized RedisConnectionPoolFactory getDataSource(RedisDatasource datasource) {
        String dsId = datasource.getId ();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<String, RedisConnectionPoolFactory> ();
        }
        RedisConnectionPoolFactory factory = dataSourcePool.remove (dsId);
        if (factory == null) {
            factory = new RedisConnectionPoolFactory (datasource);
        }
        dataSourcePool.put (dsId, factory);
        return factory;
    }

    private Jedis getConnection(RedisDatasource datasource) {
        return getDataSource (datasource).getConnection ();
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

    private List<Map<String, String>> search(String fqSep, String queryString, RedisDatasource datasource,
                                             List<DataColumn> returnColumns, Page page) {
        int pageIndex = page.getPageIndex ();
        int pageSize = page.getPageSize ();
        int startRow = (pageIndex - 1) * pageSize;
        int endRow = pageIndex * pageSize;
        return search (fqSep, queryString, datasource, returnColumns, startRow, endRow);
    }

    private List<Map<String, String>> search(String fqSep, String queryString, RedisDatasource datasource,
                                             List<DataColumn> returnColumns, int startRow, int endRow) {
        RedisConnectionPoolFactory redisConnectionPoolFactory = getDataSource (datasource);
        Jedis jedis = redisConnectionPoolFactory.getConnection ();

        List<Map<String, String>> records = new ArrayList<> ();
        String[] returnResults;
        Map<String, String> record;
        try {
            //获取模糊匹配的key
            Set<String> keys = jedis.keys (queryString);
            String[] results = new String[keys.size ()];
            results = keys.toArray (results);
            for (int i = 0; i < results.length && i <= endRow && i >= startRow; i++) {
                record = new HashMap<String, String> ();
                returnResults = jedis.get (results[i]).split (fqSep);
                for (DataColumn returnColumn : returnColumns) {
                    record.put (returnColumn.getName (), JSONUtil.encode (returnResults[returnColumn.getSeq () - 1]));
                }
                records.add (record);
            }
        } finally {
            if (jedis != null) {
                redisConnectionPoolFactory.release (jedis);
            }
        }
        return records;
    }


    private int getCountNum(String queryString, RedisDatasource datasource) {
        RedisConnectionPoolFactory redisConnectionPoolFactory = getDataSource (datasource);
        Jedis jedis = redisConnectionPoolFactory.getConnection ();
        //获取模糊匹配的key
        try {
            Set<String> keys = jedis.keys (queryString);
            return keys.size ();
        } finally {
            if (jedis != null) {
                redisConnectionPoolFactory.release (jedis);
            }
        }
    }

    private List<Map<String, String>> search(String fqSep, String queryString, RedisDatasource datasource, List<DataColumn> returnColumns, int maxNum) {
        RedisConnectionPoolFactory redisConnectionPoolFactory = getDataSource (datasource);
        Jedis jedis = redisConnectionPoolFactory.getConnection ();

        List<Map<String, String>> records = new ArrayList<> ();
        String[] returnResults;
        Map<String, String> record;
        try {
            //获取模糊匹配的key
            Set<String> keys = jedis.keys (queryString);

            String[] results = new String[keys.size ()];
            results = keys.toArray (results);
            for (int i = 0; i < results.length; i++) {
                record = new HashMap<String, String> ();
                returnResults = jedis.get (results[i]).split (fqSep);
                for (DataColumn returnColumn : returnColumns) {
                    record.put (returnColumn.getName (), returnResults[returnColumn.getSeq () - 1]);
                }
                records.add (record);
                if (records.size () >= maxNum) {
                    break;
                }
            }
        } finally {
            if (jedis != null) {
                redisConnectionPoolFactory.release (jedis);
            }
        }
        return records;
    }

    @Override
    public boolean testDatasource(Datasource datasource) {
        Jedis jedis = null;
        RedisDatasource redisDatasource = new RedisDatasource (datasource);
        try {
            jedis = getConnection (redisDatasource);
            return jedis.isConnected ();
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (jedis != null) {
                getDataSource (redisDatasource).release (jedis);
            }
        }
        return false;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        return null;
    }

    @Override
    public IqDslResponse select(IqDslRequest request) {
        throw new RuntimeException ("Redis目前暂时不支持DSL");
    }


    private List<Map<String, String>> orderBy(List<Map<String, String>> records, final List<OrderColumn> orderColumns) {
        Collections.sort (orderColumns, new Comparator<OrderColumn> () {
            @Override
            public int compare(OrderColumn obj1, OrderColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        // 多字段混合排序
        Collections.sort (records, new Comparator<Map<String, String>> () {
            @Override
            public int compare(Map<String, String> obj1, Map<String, String> obj2) {
                int flg = 0;
                for (OrderColumn orderColumn : orderColumns) {
                    String colName = orderColumn.getName ();
                    Order order = orderColumn.getOrder ();
                    DataType dataType = orderColumn.getType ();
                    String val1 = obj1.get (colName);
                    String val2 = obj2.get (colName);
                    flg = compareTo (val1, val2, order, dataType);
                    if (flg != 0) {
                        break;
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
                return 0 - str1.compareTo (str2);
            } else {
                return str1.compareTo (str2);
            }
        } else {
            if (order != null && Order.DESC.equals (order)) {
                if (DataType.INT.equals (dataType) && DataType.BIGINT.equals (dataType) && DataType.TINYINT.equals (dataType)) {
                    return 0 - Integer.valueOf (str1).compareTo (Integer.valueOf (str2));
                } else {
                    return 0 - Double.valueOf (str1).compareTo (Double.valueOf (str2));
                }
            } else {
                if (DataType.INT.equals (dataType) && DataType.BIGINT.equals (dataType) && DataType.TINYINT.equals (dataType)) {
                    return Integer.valueOf (str1).compareTo (Integer.valueOf (str2));
                } else {
                    return Double.valueOf (str1).compareTo (Double.valueOf (str2));
                }
            }
        }
    }
}
