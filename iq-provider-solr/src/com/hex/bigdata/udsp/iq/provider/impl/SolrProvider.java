package com.hex.bigdata.udsp.iq.provider.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.impl.model.SolrDatasource;
import com.hex.bigdata.udsp.iq.provider.impl.model.SolrPage;
import com.hex.bigdata.udsp.iq.provider.impl.util.SolrUtil;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by junjiem on 2017-3-3.
 */
public class SolrProvider implements Provider {
    private static Logger logger = LogManager.getLogger (SolrProvider.class);

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
            SolrDatasource solrDatasource = new SolrDatasource (metadata.getDatasource ());
            SolrQuery query = getSolrQuery (queryColumns, orderColumns, returnColumns, solrDatasource.getMaxSize ());
            List<Map<String, Object>> resultList = search (tbName, query, solrDatasource);
            response.setRecords (getRecords (resultList, returnColumns));
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
            SolrDatasource solrDatasource = new SolrDatasource (metadata.getDatasource ());
            SolrQuery query = getSolrQuery (queryColumns, orderColumns, returnColumns, page.getPageIndex (), page.getPageSize ());
            SolrPage solrPage = searchPage (tbName, query, page.getPageIndex (), page.getPageSize (), solrDatasource);
            response.setRecords (getRecords (solrPage.getRecords (), returnColumns));
            page.setTotalCount (solrPage.getTotalCount ());
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

    private SolrServer getSolrServer(String solrServices, String collectionName) throws MalformedURLException {
        String[] tempServers = solrServices.split (",");
        String[] servers = new String[tempServers.length];
        for (int i = 0; i < tempServers.length; i++) {
            servers[i] = "http://" + tempServers[i] + "/solr/" + collectionName;
        }
        return new LBHttpSolrServer (servers);
    }

    private SolrQuery getSolrQuery(List<QueryColumn> queryColumns, List<OrderColumn> orderColumns,
                                   List<ReturnColumn> returnColumns, int rows) {
        return new SolrQuery ().setQuery (getQuery (queryColumns)) //
                .setStart (0) //
                .setRows (rows) //
                .setSorts (getSort (orderColumns)) //
                .setFields (getFields (returnColumns));
    }

    private SolrQuery getSolrQuery(List<QueryColumn> queryColumns, List<OrderColumn> orderColumns,
                                   List<ReturnColumn> returnColumns, int pageIndex, int pageSize) {
        return new SolrQuery ().setQuery (getQuery (queryColumns)) //
                .setStart ((pageIndex - 1) * pageSize) //
                .setRows (pageSize) //
                .setSorts (getSort (orderColumns)) //
                .setFields (getFields (returnColumns));
    }

    // 字段名改别名
    private List<Map<String, String>> getRecords(List<Map<String, Object>> list, List<ReturnColumn> returnColumns) {
        List<Map<String, String>> records = new ArrayList<> ();
        if (list == null || list.size () == 0) {
            return records;
        }
        Map<String, String> result = null;
        for (Map<String, Object> map : list) {
            result = new HashMap<> ();
            for (ReturnColumn item : returnColumns) {
                result.put (item.getLabel (), String.valueOf (map.get (item.getName ())));
            }
            records.add (result);
        }
        return records;
    }

    private String getFields(List<ReturnColumn> returnColumns) {
        Collections.sort (returnColumns, new Comparator<ReturnColumn> () {
            @Override
            public int compare(ReturnColumn obj1, ReturnColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        StringBuffer sb = new StringBuffer ();
        int count = 0;
        for (ReturnColumn returnColumn : returnColumns) {
            if (returnColumn != null && StringUtils.isNotBlank (returnColumn.getName ())) {
                if (count == 0) {
                    sb.append (returnColumn.getName ());
                } else {
                    sb.append ("," + returnColumn.getName ());
                }
                count++;
            }
        }
        return sb.toString ();
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

    private List<SolrQuery.SortClause> getSort(List<OrderColumn> orderColumns) {
        // 排序字段按照序号排序
        Collections.sort (orderColumns, new Comparator<OrderColumn> () {
            @Override
            public int compare(OrderColumn obj1, OrderColumn obj2) {
                return obj1.getSeq ().compareTo (obj2.getSeq ());
            }
        });
        // 排序字段集合
        List<SolrQuery.SortClause> list = new ArrayList<SolrQuery.SortClause> ();
        for (OrderColumn orderColumn : orderColumns) {
            String colName = orderColumn.getName ();
            Order order = orderColumn.getOrder ();
            if (order != null && Order.DESC.equals (order)) {
                list.add (new SolrQuery.SortClause (colName, SolrQuery.ORDER.desc));
            } else {
                list.add (new SolrQuery.SortClause (colName, SolrQuery.ORDER.asc));
            }
        }
        return list;
    }

    private SolrPage searchPage(String collectionName, SolrQuery query, int pageIndex, int pageSize, SolrDatasource datasource) {
        QueryResponse rsp = getQueryResponse (collectionName, query, datasource);
        if (rsp == null) {
            return null;
        }
        SolrDocumentList results = rsp.getResults ();
        long totalCount = results.getNumFound ();
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>> ();
        Map<String, Object> map = null;
        for (int i = 0; i < results.size (); i++) {
            map = new HashMap<> ();
            for (Map.Entry<String, Object> entry : results.get (i).entrySet ()) {
                map.put (entry.getKey (), entry.getValue ());
            }
            records.add (map);
            //records.add(results.get(i).getFieldValueMap());
        }
        return new SolrPage (records, pageIndex, pageSize, totalCount);
    }

    private List<Map<String, Object>> search(String collectionName, SolrQuery query, SolrDatasource datasource) {
        QueryResponse rsp = getQueryResponse (collectionName, query, datasource);
        if (rsp == null) {
            return null;
        }
        SolrDocumentList results = rsp.getResults ();
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>> ();
        for (int i = 0; i < results.size (); i++) {
            records.add (results.get (i).getFieldValueMap ());
        }
        return records;
    }

    private QueryResponse getQueryResponse(String collectionName, SolrQuery query, SolrDatasource datasource) {
        SolrServer solrServer = null;
        QueryResponse res = null;
        try {
            solrServer = getSolrServer (datasource.getSolrServers (), collectionName);
            res = solrServer.query (query);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return res;
    }

    @Override
    public boolean testDatasource(Datasource datasource) {
        HttpURLConnection connection = null;
        URL url = null;
        try {
            SolrDatasource solrDatasource = new SolrDatasource (datasource);
            String[] servers = solrDatasource.getSolrServers ().split (",");
            for (String server : servers) {
                try {
                    url = new URL ("http://" + server + "/solr");
                    connection = (HttpURLConnection) url.openConnection ();
                    connection.setDoInput (true);
                    connection.setDoOutput (true);
                    connection.setUseCaches (false);
                    connection.setInstanceFollowRedirects (true);
                    connection.connect ();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace ();
                    logger.warn ("获取solr连接失败的地址为：" + (url == null ? "" : url.toString ()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (connection != null) {
                connection.disconnect ();
            }
        }
        return false;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        SolrDatasource solrDatasource = new SolrDatasource (datasource);
        String solrServers = solrDatasource.getSolrServers ();
        return getColumns (schemaName, solrServers);
    }

    @Override
    public IqDslResponse select(IqDslRequest request) {
        throw new RuntimeException ("Solr目前暂时不支持DSL");
    }

    public List<MetadataCol> getColumns(String collectionName, String solrServers) {
        if (StringUtils.isEmpty (collectionName) || StringUtils.isEmpty (solrServers)) {
            return null;
        }
        String response = "";
        String[] addresses = solrServers.split (",");
        for (String solrServer : addresses) {
            String url = "http://" + solrServer + "/solr/" + collectionName + "/schema/fields";
            response = SolrUtil.sendGet (url, "");
            if (StringUtils.isEmpty (response)) {
                continue;
            } else {
                break;
            }
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
