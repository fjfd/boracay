package com.hex.bigdata.udsp.iq.provider.impl;

import com.alibaba.fastjson.JSONObject;
import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.Operator;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.impl.factory.ElasticSearchConnectionPoolFactory;
import com.hex.bigdata.udsp.iq.provider.impl.model.*;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.http.HttpHost;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.*;

public class ElasticSearchProvider implements Provider {

    private static Logger logger = LogManager.getLogger(ElasticSearchProvider.class);

    private static Map<String, ElasticSearchConnectionPoolFactory> dataSourcePool;

    @Override
    public IqResponse query(IqRequest request) {
        long bef = System.currentTimeMillis();
        IqResponse response = new IqResponse();

        try {
            Application application = request.getApplication();
            Metadata metadata = application.getMetadata();
            List<QueryColumn> queryColumns = application.getQueryColumns();
            List<ReturnColumn> returnColumns = application.getReturnColumns();
            List<OrderColumn> orderColumns = application.getOrderColumns();
            //表名，索引名称.类型名称
            String schemaName = metadata.getTbName();
            //数据源信息
            ELSearchDatasource elSearchDatasource = new ELSearchDatasource(metadata.getDatasource());
            //最大查询数量
            Page page = new Page();
            page.setPageIndex(0);
            page.setPageSize(elSearchDatasource.getMaxSize ());
            String queryString = getQueryString(queryColumns, orderColumns, returnColumns, page);
            ELSearchPage elSearchPage = search(schemaName, elSearchDatasource, queryString, returnColumns);
            response.setRecords(getRecords(elSearchPage.getRecords(), returnColumns));
            response.setStatus(Status.SUCCESS);
            response.setStatusCode(StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(e.toString());
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        logger.debug("consumeTime=" + response.getConsumeTime());
        return response;
    }

    @Override
    public IqResponse query(IqRequest request, Page page) {
        long bef = System.currentTimeMillis();
        IqResponse response = new IqResponse();

        try {
            Application application = request.getApplication();
            Metadata metadata = application.getMetadata();
            List<QueryColumn> queryColumns = application.getQueryColumns();
            List<ReturnColumn> returnColumns = application.getReturnColumns();
            List<OrderColumn> orderColumns = application.getOrderColumns();
            //表名，索引名称.类型名称
            String schemaName = metadata.getTbName();
            //数据源信息
            ELSearchDatasource elSearchDatasource = new ELSearchDatasource(metadata.getDatasource());
            String queryString = getQueryString(queryColumns, orderColumns, returnColumns, page);
            ELSearchPage elSearchPage = search(schemaName, elSearchDatasource, queryString, returnColumns);
            response.setRecords(getRecords(elSearchPage.getRecords(), returnColumns));
            response.setStatus(Status.SUCCESS);
            page.setTotalCount(elSearchPage.getTotalCount());
            response.setPage(page);
            response.setStatusCode(StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(e.toString());
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);

        logger.debug("consumeTime=" + response.getConsumeTime());
        return response;
    }

    // 字段名改别名
    private List<Map<String, String>> getRecords(List<Map<String, Object>> list, List<ReturnColumn> returnColumns) {
        List<Map<String, String>> records = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return records;
        }
        Map<String, String> result = null;
        for (Map<String, Object> map : list) {
            result = new HashMap<>();
            for (ReturnColumn item : returnColumns) {
                result.put(item.getLabel(), String.valueOf(map.get(item.getName())));
            }
            records.add(result);
        }
        return records;
    }

    private ELSearchPage search(String schemaName, ELSearchDatasource datasource, String queryString, List<ReturnColumn> returnColumns) throws Exception {
        ELSearchPage elSearchPage = new ELSearchPage();
        RestClient restClient = null;
        NStringEntity stringEntity = null;
        try {
            stringEntity = new NStringEntity(queryString, "utf-8");
            restClient = getConnection(datasource);
            logger.debug(queryString);
            String schemaUrl = schemaName;
            String[] schemaArray = schemaName.split("\\.");
            if (schemaArray.length == 2) {
                schemaUrl = schemaArray[0] + "/" + schemaArray[1];
            }
            Response response = restClient.performRequest("GET", "/" + schemaUrl + "/_search", Collections.<String, String>emptyMap(), stringEntity);
            String returnString = EntityUtils.toString(response.getEntity());
            JSONObject returnJsonObject = JSONUtil.parseJSON2Obj(returnString, JSONObject.class);
            JSONObject errorObject = (JSONObject) returnJsonObject.get("error");
            logger.debug("search_result:" + returnString);
            if (null != errorObject) {
                //查询报错抛出异常
                String errortype = (String) errorObject.get("type");
                String errorReason = (String) errorObject.get("reason");
                throw new RuntimeException(errortype + ":" + errorReason);
            }
            ELSearchResponse eLsearchResponse = JSONObject.parseObject(returnString, ELSearchResponse.class);
            ELOuterHits elOuterHits = eLsearchResponse.getHits();
            List<ELInnerHits> elInnerHits = elOuterHits.getHits();
            Map<String, String> colMap = getColMap(returnColumns);
            List<Map<String, Object>> recordes = new ArrayList<Map<String, Object>>();
            Map<String, Object> recorde = null;
            Map<String, Object> source = null;
            String label = null;
            for (ELInnerHits item : elInnerHits) {
                source = item.get_source();
                recorde = new HashMap<>();
                for (Map.Entry<String, Object> entity : source.entrySet()) {
                    label = colMap.get(entity.getKey());
                    if (StringUtils.isNotBlank(label)) {
                        recorde.put(label, entity.getValue());
                    }
                }
                recordes.add(recorde);
            }
            elSearchPage.setRecords(recordes);
            //设置总量
            elSearchPage.setTotalCount(elOuterHits.getTotal());
        } finally {
            if (restClient != null) {
                release(datasource, restClient);
            }
        }
        return elSearchPage;
    }

    private Map<String, String> getColMap(List<ReturnColumn> returnColumns) {
        Map<String, String> colMap = new HashMap<>();
        for (ReturnColumn column : returnColumns) {
            colMap.put(column.getName(), column.getLabel());
        }
        return colMap;
    }

    @Override
    public boolean testDatasource(Datasource datasource) {
        ELSearchDatasource elSearchDatasource = new ELSearchDatasource(datasource);
        String[] tempServers = elSearchDatasource.getElasticsearchServers().split(",");
        if (tempServers.length == 0) {
            return false;
        }
        HttpHost[] hosts = getHttpHosts(tempServers);
        RestClient restClient = RestClient.builder(hosts).build();
        Response response = null;
        try {
            response = restClient.performRequest("GET", "/", Collections.singletonMap("pretty", "true"));
            JSONObject responseObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            String tagline = (String) responseObject.get("tagline");
            if (StringUtils.isNotBlank(tagline)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        List<MetadataCol> list = null;
        MetadataCol col = null;
        ELSearchDatasource elSearchDatasource = new ELSearchDatasource(datasource);
        RestClient restClient = null;
        NStringEntity stringEntity = null;
        Response response = null;
        try {
            stringEntity = new NStringEntity("", "utf-8");
            restClient = getConnection(elSearchDatasource);
            String schemaUrl = schemaName;
            String[] schemaArray = schemaName.split("\\.");
            if (schemaArray.length == 2) {
                schemaUrl = schemaArray[0] + "/" + schemaArray[1];
            }
            response = restClient.performRequest("GET", "/" + schemaUrl, Collections.<String, String>emptyMap(), stringEntity);
            String returnStr = EntityUtils.toString(response.getEntity());
            JSONObject returnJsonObject = JSONUtil.parseJSON2Obj(returnStr, JSONObject.class);
            JSONObject errorObject = (JSONObject) returnJsonObject.get("error");
            logger.debug("search_result:" + returnStr);
            if (null != errorObject) {
                throw new RuntimeException(returnStr);
            }

            JSONObject defaultObj = null;
            if (schemaArray.length == 2) {
                JSONObject schemaObj = (JSONObject) returnJsonObject.get(schemaArray[0]);
                JSONObject mappingsObj = (JSONObject) schemaObj.get("mappings");
                defaultObj = (JSONObject) mappingsObj.get(schemaArray[1]);
            } else {
                JSONObject schemaObj = (JSONObject) returnJsonObject.get(schemaName);
                JSONObject mappingsObj = (JSONObject) schemaObj.get("mappings");
                defaultObj = (JSONObject) mappingsObj.get("default");
            }
            JSONObject propertiesObj = (JSONObject) defaultObj.get("properties");
            String propertiesStr = JSONUtil.parseObj2JSON(propertiesObj);
            Map<String, ELSearchProperty> properties = JSONUtil.parseJSON2Map(propertiesStr, ELSearchProperty.class);
            list = new ArrayList<>();
            short i = 0;
            for (Map.Entry<String, ELSearchProperty> entry : properties.entrySet()) {
                String name = entry.getKey();
                ELSearchProperty property = entry.getValue();
                String index = property.getIndex();
                String type = property.getType();
                boolean store = property.isStore();

                col = new MetadataCol();
                col.setSeq(i);
                col.setName(name);
                col.setType(getColType(type));
                col.setIndexed(StringUtils.isNotBlank(index) ? true : false);
                col.setStored(store);

                list.add(col);
                i++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (restClient != null) {
                release(elSearchDatasource, restClient);
            }
        }
        return list;
    }

    @Override
    public IqDslResponse select(IqDslRequest request) {
        throw new RuntimeException ("ES目前暂时不支持DSL");
    }

    private DataType getColType(String type) {
        type = type.toUpperCase();
        DataType dataType = null;
        switch (type) {
            case "STRING":
                dataType = DataType.STRING;
                break;
            case "INT":
                dataType = DataType.INT;
                break;
            case "FLOAT":
                dataType = DataType.FLOAT;
                break;
            case "DOUBLE":
                dataType = DataType.DOUBLE;
                break;
            case "DATE":
                dataType = DataType.TIMESTAMP;
                break;
            case "BOOLEAN":
                dataType = DataType.BOOLEAN;
                break;
            default:
                dataType = DataType.STRING;
        }
        return dataType;
    }

    private void release(ELSearchDatasource datasource, RestClient restClient) {
        getDataSource(datasource).releaseConnection(restClient);
    }

    private RestClient getConnection(ELSearchDatasource datasource) throws Exception {
        return getDataSource(datasource).getConnection();
    }

    private synchronized ElasticSearchConnectionPoolFactory getDataSource(ELSearchDatasource datasource) {
        String dsId = datasource.getId();
        if (dataSourcePool == null) {
            dataSourcePool = new HashMap<String, ElasticSearchConnectionPoolFactory>();
        }
        ElasticSearchConnectionPoolFactory factory = dataSourcePool.remove(dsId);
        if (factory == null) {
            GenericObjectPool.Config config = new GenericObjectPool.Config();
            config.lifo = true;
            config.minIdle = 1;
            config.maxActive = 10;
            config.maxWait = 3000;
            config.maxActive = 5;
            config.timeBetweenEvictionRunsMillis = 30000;
            config.testWhileIdle = true;
            config.testOnBorrow = false;
            config.testOnReturn = false;
            factory = new ElasticSearchConnectionPoolFactory(config, datasource.getElasticsearchServers());
        }
        dataSourcePool.put(dsId, factory);
        return factory;
    }

    /**
     * 获取HttpHost对象
     *
     * @param tempServers
     * @return
     */
    private HttpHost[] getHttpHosts(String[] tempServers) {
        HttpHost[] hosts = new HttpHost[tempServers.length];
        for (int i = 0; i < tempServers.length; i++) {
            String item = tempServers[i];
            String[] array = item.split(":");
            hosts[i] = new HttpHost(array[0], Integer.valueOf(array[1]), "http");
        }
        return hosts;
    }

    /**
     * 根据查询参数、排序参数，分页参数，获取查询实体
     *
     * @param queryColumns
     * @param orderColumns
     * @param page
     * @return
     */
    public static String getQueryString(List<QueryColumn> queryColumns, List<OrderColumn> orderColumns,
                                        List<ReturnColumn> returnColumns, Page page) {
        JSONObject topObject = new JSONObject();
        JSONObject queryObject = new JSONObject();
        JSONObject boolObject = new JSONObject();
        List<JSONObject> mustObjects = new ArrayList<JSONObject>();
        List<JSONObject> mustNotObjects = new ArrayList<JSONObject>();
        JSONObject termObject = null;
        JSONObject rangeObject = null;
        JSONObject itemObject = null;
        for (QueryColumn queryColumn : queryColumns) {
            String name = queryColumn.getName();
            String value = queryColumn.getValue();
            Operator operator = queryColumn.getOperator();
            if (StringUtils.isNotBlank(value)) {
                if (Operator.EQ.equals(operator)) {
                    termObject = new JSONObject();
                    itemObject = new JSONObject();
                    itemObject.put(name, value);
                    termObject.put("term", itemObject);
                    mustObjects.add(termObject);
                } else if (Operator.GT.equals(operator)) {
                    rangeObject = new JSONObject();
                    itemObject = new JSONObject();
                    JSONObject operateOject = new JSONObject();
                    operateOject.put("gt", value);
                    itemObject.put(name, operateOject);
                    rangeObject.put("range", itemObject);
                    mustObjects.add(rangeObject);
                } else if (Operator.LT.equals(operator)) {
                    rangeObject = new JSONObject();
                    itemObject = new JSONObject();
                    JSONObject operateOject = new JSONObject();
                    operateOject.put("lt", value);
                    itemObject.put(name, operateOject);
                    rangeObject.put("range", itemObject);
                    mustObjects.add(rangeObject);
                } else if (Operator.GE.equals(operator)) {
                    rangeObject = new JSONObject();
                    itemObject = new JSONObject();
                    JSONObject operateOject = new JSONObject();
                    operateOject.put("gte", value);
                    itemObject.put(name, operateOject);
                    rangeObject.put("range", itemObject);
                    mustObjects.add(rangeObject);
                } else if (Operator.LE.equals(operator)) {
                    rangeObject = new JSONObject();
                    itemObject = new JSONObject();
                    JSONObject operateOject = new JSONObject();
                    operateOject.put("lte", value);
                    itemObject.put(name, operateOject);
                    rangeObject.put("range", itemObject);
                    mustObjects.add(rangeObject);
                } else if (Operator.RLIKE.equals(operator)) {
                    JSONObject rLikeObject = new JSONObject();
                    itemObject = new JSONObject();
                    itemObject.put(name, value);
                    rLikeObject.put("match_phrase_prefix", itemObject);
                    mustObjects.add(rLikeObject);
                } else if (Operator.LK.equals(operator)) {
                    JSONObject likeObject = new JSONObject();
                    itemObject = new JSONObject();
                    itemObject.put(name, value + "*");
                    likeObject.put("wildcard", itemObject);
                    mustObjects.add(likeObject);
                } else if (Operator.IN.equals(operator)) {
                    JSONObject inObject = new JSONObject();
                    itemObject = new JSONObject();
                    itemObject.put(name, value.split(","));
                    inObject.put("terms", itemObject);
                    mustObjects.add(inObject);
                } else if (Operator.NE.equals(operator)) {
                    termObject = new JSONObject();
                    itemObject = new JSONObject();
                    itemObject.put(name, value);
                    termObject.put("term", itemObject);
                    mustNotObjects.add(termObject);
                }
            }
        }

        List<JSONObject> orderObjects = new ArrayList<JSONObject>();
        JSONObject orderObject = null;
        //排序
        for (OrderColumn orderColumn : orderColumns) {
            orderObject = new JSONObject();
            itemObject = new JSONObject();
            itemObject.put("order", orderColumn.getOrder().getValue());
            orderObject.put(orderColumn.getName(), itemObject);
            orderObjects.add(orderObject);
            topObject.put("sort", orderObjects);
        }
        boolObject.put("must", mustObjects);
        boolObject.put("must_not", mustNotObjects);
        queryObject.put("bool", boolObject);

        topObject.put("query", queryObject);

        //Elasticsearch深度分页应该被禁止，推荐使用scroll 的方式遍历数据。
        if (null != page) {
            int pageIndex = page.getPageIndex() == 0 ? 0 : page.getPageIndex() - 1;
            int pageSize = page.getPageSize();
            topObject.put("from", pageIndex * pageSize);
            topObject.put("size", pageSize);
        }
        if (null != returnColumns && returnColumns.size() > 0) {
            String[] rColumns = new String[returnColumns.size()];
            for (int i = 0; i < returnColumns.size(); i++) {
                rColumns[i] = returnColumns.get(i).getName();
            }
            topObject.put("_source", rColumns);
        }

        //System.out.println(JSONObject.toJSONString(topObject));
        return JSONObject.toJSONString(topObject);
    }
}
