package com.hex.bigdata.udsp.iq.service;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.EnumTrans;
import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.service.ComDatasourceService;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.bigdata.udsp.common.util.DatasourceUtil;
import com.hex.bigdata.udsp.common.util.ObjectUtil;
import com.hex.bigdata.udsp.dsl.constant.AggregateFunction;
import com.hex.bigdata.udsp.dsl.model.*;
import com.hex.bigdata.udsp.iq.model.*;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import com.hex.goframe.dao.GFDictMapper;
import com.hex.goframe.model.GFDict;
import com.hex.goframe.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by junjiem on 2017-3-2.
 */
@Service
public class IqProviderService extends BaseService {
    private static Logger logger = LogManager.getLogger (IqProviderService.class);
    private static final String IQ_IMPL_CLASS = "IQ_IMPL_CLASS";

    private static final FastDateFormat format8 = FastDateFormat.getInstance ("yyyyMMdd");
    private static final FastDateFormat format10 = FastDateFormat.getInstance ("yyyy-MM-dd");
    private static final FastDateFormat format17 = FastDateFormat.getInstance ("yyyyMMdd HH:mm:ss");
    private static final FastDateFormat format19 = FastDateFormat.getInstance ("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private IqApplicationService iqApplicationService;
    @Autowired
    private IqMetadataService iqMetadataService;
    @Autowired
    private IqMetadataColService iqMetadataColService;
    @Autowired
    private ComDatasourceService comDatasourceService;
    @Autowired
    private ComPropertiesService comPropertiesService;
    @Autowired
    private IqAppQueryColService iqAppQueryColService;
    @Autowired
    private IqAppReturnColService iqAppReturnColService;
    @Autowired
    private IqAppOrderColService iqAppOrderColService;
    @Autowired
    private GFDictMapper gfDictMapper;

    /**
     * 获取字段信息
     *
     * @param dsId
     * @param schemaName
     * @return
     */
    public List<MetadataCol> getColumnInfo(String dsId, String schemaName) {
        ComDatasource comDatasource = comDatasourceService.select (dsId);
        List<ComProperties> comPropertiesList = comPropertiesService.selectList (dsId);
        Datasource datasource = DatasourceUtil.getDatasource (comDatasource, comPropertiesList);
        return getProviderImpl (datasource).columnInfo (datasource, schemaName);
    }

    /**
     * 查询
     *
     * @param appId
     * @param paraMap
     * @return
     */
    public IqResponse select(String appId, Map<String, String> paraMap) {
        Application application = getApplication (appId, paraMap);
        IqRequest request = new IqRequest (application);
        Datasource datasource = application.getMetadata ().getDatasource ();
        IqResponse response = getProviderImpl (datasource).query (request);
        response.setColumns (returnColumns (application.getReturnColumns ())); // 设置返回字段信息
        return response;
    }

    /**
     * 分页查询
     *
     * @param appId
     * @param paraMap
     * @param page
     * @return
     */
    public IqResponse select(String appId, Map<String, String> paraMap, Page page) {
        Application application = getApplication (appId, paraMap);
        Datasource datasource = application.getMetadata ().getDatasource ();
        page = getPage (page, datasource);
        IqRequest request = new IqRequest (application);
        IqResponse response = getProviderImpl (datasource).query (request, page);
        response.setColumns (returnColumns (application.getReturnColumns ())); // 设置返回字段信息
        return response;
    }

    /**
     * 自定义SQL查询
     *
     * @param mdId
     * @param dslRequest
     * @return
     */
    public IqDslResponse select(String mdId, DslRequest dslRequest) {
        Metadata metadata = getMetadata (mdId);
        checkDslRequest (dslRequest, metadata); // 检查和重构DslRequest
        IqDslRequest request = new IqDslRequest (dslRequest, metadata);
        Datasource datasource = metadata.getDatasource ();
        IqDslResponse response = getProviderImpl (datasource).select (request);
        response.getDslResponse ().setColumns (returnColumns (dslRequest)); // 设置返回字段信息
        return response;
    }

    private LinkedHashMap<String, String> returnColumns(DslRequest dslRequest) {
        List<Column> select = dslRequest.getSelect ();
        LinkedHashMap<String, String> columnMap = new LinkedHashMap<> ();
        for (Column column : select) {
            columnMap.put (column.getAggregate ().getName (), column.getAggregate ().getDataType ().getName ());
        }
        return columnMap;
    }

    private void checkDslRequest(DslRequest dslRequest, Metadata metadata) {
        Datasource datasource = metadata.getDatasource ();
        List<DataColumn> returnColumns = metadata.getReturnColumns ();
        Map<String, DataType> returnMap = new HashMap<> ();
        for (DataColumn col : returnColumns) {
            returnMap.put (col.getName (), col.getType ());
        }
        List<DataColumn> queryColumns = metadata.getQueryColumns ();
        Map<String, DataType> queryMap = new HashMap<> ();
        for (DataColumn col : queryColumns) {
            queryMap.put (col.getName (), col.getType ());
        }
        // 表名
        // 注：可能有多个表名比如HBase+Solr，所以这里还是使用服务名
        //dslRequest.setName (metadata.getTbName ());
        // 返回字段
        List<Column> select = dslRequest.getSelect ();
        if (select.size () == 1 && "*".equals (select.get (0).getAlias ())) {
            select = new ArrayList<> ();
            for (DataColumn col : returnColumns) {
                select.add (new Column (null, new Aggregate (col.getName (), col.getType ())));
            }
            dslRequest.setSelect (select);
        } else {
            checkSelect (select, returnMap);
        }
        // 查询字段
        Component where = dslRequest.getWhere ();
        if (where != null) {
            checkWhere (where, queryMap);
        }
        // 分组字段
        List<String> groupBy = dslRequest.getGroupBy ();
        if (groupBy != null && groupBy.size () != 0) {
            checkGroupBy (groupBy, returnMap);
        }
        // 排序字段
        List<Order> orderBy = dslRequest.getOrderBy ();
        if (orderBy != null && orderBy.size () != 0) {
            checkOrderBy (orderBy, returnMap);
        }
        // 限制
        Limit limit = dslRequest.getLimit ();
        if (limit != null) {
            checkLimit (limit, datasource);
        }
    }

    private void checkLimit(Limit limitObj, Datasource datasource) {
        IqDatasource iqDatasource = new IqDatasource (datasource);
        int maxSize = iqDatasource.getMaxSize ();
        boolean maxSizeAlarm = iqDatasource.getMaxSizeAlarm ();
        int limit = limitObj.getLimit ();
        if (limit > maxSize) {
            if (maxSizeAlarm) {
                throw new IllegalArgumentException ("Invalid limit value: the value exceeds the maximum number of data returned.");
            }
            limit = maxSize;
        }
        limitObj.setLimit (limit);
    }

    private void checkOrderBy(List<Order> orderBy, Map<String, DataType> returnMap) {
        // TODO 严格模式：判断是否有不存在的字段并抛异常
        String message = "";
        for (Order order : orderBy) {
            String name = order.getName ();
            if (returnMap.get (name) == null) {
                message += (StringUtils.isBlank (message) ? "" : ", ") + "`" + name + "`";
            }
        }
        if (StringUtils.isNotBlank (message)) {
            throw new IllegalArgumentException ("Invalid order by fields " + message + ".");
        }
    }

    private void checkGroupBy(List<String> groupBy, Map<String, DataType> returnMap) {
        // TODO 严格模式：判断是否有不存在的字段并抛异常
        String message = "";
        for (String name : groupBy) {
            if (returnMap.get (name) == null) {
                message += (StringUtils.isBlank (message) ? "" : ", ") + "`" + name + "`";
            }
        }
        if (StringUtils.isNotBlank (message)) {
            throw new IllegalArgumentException ("Invalid group by fields " + message + ".");
        }
    }

    private void checkWhere(Component where, Map<String, DataType> queryMap) {
        // TODO 严格模式：判断是否有不存在的字段并抛异常
        String message = checkWhere (queryMap, where, "");
        if (StringUtils.isNotBlank (message)) {
            throw new IllegalArgumentException ("Invalid where fields " + message + ".");
        }
    }

    private String checkWhere(Map<String, DataType> queryMap, Component component, String message) {
        if (component instanceof Composite) {
            Composite composite = (Composite) component;
            List<Component> components = composite.getComponents ();
            for (Component c : components) {
                message = checkWhere (queryMap, c, message);
            }
        } else if (component instanceof Dimension) {
            Dimension dimension = (Dimension) component;
            String name = dimension.getColumnName ();
            if (queryMap.get (name) == null) {
                message += (StringUtils.isBlank (message) ? "" : ", ") + "`" + name + "`";
            }
        }
        return message;
    }

    private void checkSelect(List<Column> select, Map<String, DataType> returnMap) {
        // TODO 严格模式：判断是否有不存在的字段并抛异常
        String message = "";
        for (Column col : select) {
            String name = col.getAggregate ().getName ();
            if (returnMap.get (name) == null) {
                message += (StringUtils.isBlank (message) ? "" : ", ") + "`" + name + "`";
            } else {
                col.getAggregate ().setDataType (returnMap.get (name));
            }
        }
        if (StringUtils.isNotBlank (message)) {
            throw new IllegalArgumentException ("Invalid select fields " + message + ".");
        }
    }

    private Page getPage(Page page, Datasource datasource) {
        IqDatasource iqDatasource = new IqDatasource (datasource);
        int maxSize = iqDatasource.getMaxSize ();
        boolean maxSizeAlarm = iqDatasource.getMaxSizeAlarm ();
        int pageSize = page.getPageSize ();
        if (pageSize > maxSize) {
            if (maxSizeAlarm) {
                throw new IllegalArgumentException ("每页返回数据大小超过了最大返回数据条数的限制");
            }
            pageSize = maxSize;
        }
        page.setPageSize (pageSize);
        return page;
    }

    private LinkedHashMap<String, String> returnColumns(List<ReturnColumn> returnColumns) {
        LinkedHashMap<String, String> columnMap = new LinkedHashMap<> ();
        for (ReturnColumn returnColumn : returnColumns) {
            columnMap.put (returnColumn.getLabel (), returnColumn.getType ().getValue ());
        }
        return columnMap;
    }

    private Application getApplication(String appId, Map<String, String> paraMap) {
        Application application = getApplication (appId);
        // TODO 宽松模式：只保留存在字段的值，不判断是否有不存在的字段并抛异常
        String message = "";
        for (QueryColumn queryColumn : application.getQueryColumns ()) {
            boolean isNeed = queryColumn.isNeed ();
            DataType type = queryColumn.getType ();
            int length = getLen (queryColumn.getLength ());
            String label = queryColumn.getLabel ();
            String value = (paraMap != null ? paraMap.get (label) : null);
            if (StringUtils.isBlank (value)) {
                value = queryColumn.getDefaultVal (); // 默认值
            }
            if (StringUtils.isNotBlank (value)) {
                if (DataType.TIMESTAMP.equals (type)) { // 字段类型是TIMESTAMP
                    value = tarnDateStr (length, value); // 日期格式转换
                }
            }
            queryColumn.setValue (value);
            if (isNeed && StringUtils.isBlank (value)) {
                message += (StringUtils.isBlank (message) ? "" : ", ") + label;
            }
        }
        if (StringUtils.isNotBlank (message)) {
            throw new IllegalArgumentException (message + "参数不能为空!");
        }
        return application;
    }

    public Application getApplication(String appId) {
        IqApplication iqApplication = iqApplicationService.select (appId);
        List<IqAppQueryCol> iqAppQueryColList = iqAppQueryColService.selectByAppId (appId);
        List<IqAppReturnCol> iqAppReturnColList = iqAppReturnColService.selectByAppId (appId);
        List<IqAppOrderCol> iqAppOrderColList = iqAppOrderColService.selectByAppId (appId);
        String mdId = iqApplication.getMdId ();

        List<ComProperties> appProperties = comPropertiesService.selectList (appId);
        List<Property> appPropertyList = new ArrayList<> (appProperties.size ());
        for (ComProperties properties : appProperties) {
            Property property = new Property ();
            property.setName (properties.getName ());
            property.setValue (properties.getValue ());
            property.setDescribe (properties.getDescribe ());
            appPropertyList.add (property);
        }

        // ----------------数据封装-----------------------
        Application application = new Application (appPropertyList);
        application.setName (iqApplication.getName ());
        application.setDescribe (iqApplication.getDescribe ());
        application.setNote (iqApplication.getNote ());
        List<QueryColumn> queryColumnList = new ArrayList<> ();
        for (IqAppQueryCol iqAppQueryCol : iqAppQueryColList) {
            QueryColumn queryColumn = new QueryColumn ();
            queryColumn.setSeq (iqAppQueryCol.getSeq ());
            queryColumn.setDescribe (iqAppQueryCol.getDescribe ());
            queryColumn.setName (iqAppQueryCol.getName ());
            queryColumn.setDefaultVal (iqAppQueryCol.getDefaultVal ());
            queryColumn.setLabel (iqAppQueryCol.getLabel ());
            queryColumn.setLength (iqAppQueryCol.getLength ());
            queryColumn.setNeed (EnumTrans.transTrue (iqAppQueryCol.getIsNeed ()));
            queryColumn.setOfferOut (EnumTrans.transTrue (iqAppQueryCol.getIsOfferOut ()));
            queryColumn.setOperator (EnumTrans.transOperator (iqAppQueryCol.getOperator ()));
            queryColumn.setType (EnumTrans.transDataType (iqAppQueryCol.getType ()));
            //queryColumn.setValue(paraMap.get(iqAppQueryCol.getName()));
            queryColumnList.add (queryColumn);
        }
        application.setQueryColumns (queryColumnList);
        List<ReturnColumn> returnColumnList = new ArrayList<> ();
        for (IqAppReturnCol iqAppReturnCol : iqAppReturnColList) {
            ReturnColumn returnColumn = new ReturnColumn ();
            returnColumn.setSeq (iqAppReturnCol.getSeq ());
            returnColumn.setDescribe (iqAppReturnCol.getDescribe ());
            returnColumn.setName (iqAppReturnCol.getName ());
            returnColumn.setLabel (iqAppReturnCol.getLabel ());
            returnColumn.setLength (iqAppReturnCol.getLength ());
            returnColumn.setStats (EnumTrans.transStats (iqAppReturnCol.getStats ()));
            returnColumn.setType (EnumTrans.transDataType (iqAppReturnCol.getType ()));
            returnColumnList.add (returnColumn);
        }
        application.setReturnColumns (returnColumnList);
        List<OrderColumn> orderColumnList = new ArrayList<> ();
        for (IqAppOrderCol iqAppOrderCol : iqAppOrderColList) {
            OrderColumn orderColumn = new OrderColumn ();
            orderColumn.setSeq (iqAppOrderCol.getSeq ());
            orderColumn.setDescribe (iqAppOrderCol.getDescribe ());
            orderColumn.setName (iqAppOrderCol.getName ());
            orderColumn.setType (EnumTrans.transDataType (iqAppOrderCol.getType ()));
            orderColumn.setOrder (EnumTrans.transOrder (iqAppOrderCol.getOrderType ()));
            orderColumnList.add (orderColumn);
        }
        application.setOrderColumns (orderColumnList);
        application.setMetadata (getMetadata (mdId));
        return application;
    }

    public Metadata getMetadata(String mdId) {
        IqMetadata iqMetadata = iqMetadataService.select (mdId);
        List<IqMetadataCol> iqMetadataQueryColList = iqMetadataColService.selectQueryColList (mdId);
        List<IqMetadataCol> iqMetadataReturnColList = iqMetadataColService.selectReturnColList (mdId);
        String dsId = iqMetadata.getDsId ();

        List<ComProperties> mdProperties = comPropertiesService.selectList (mdId);
        List<Property> mdPropertyList = new ArrayList<> (mdProperties.size ());
        for (ComProperties properties : mdProperties) {
            Property property = new Property ();
            property.setName (properties.getName ());
            property.setValue (properties.getValue ());
            property.setDescribe (properties.getDescribe ());
            mdPropertyList.add (property);
        }

        // ----------------数据封装-----------------------
        Metadata metadata = new Metadata (mdPropertyList);
        metadata.setName (iqMetadata.getName ());
        metadata.setDescribe (iqMetadata.getDescribe ());
        metadata.setNote (iqMetadata.getNote ());
        metadata.setTbName (iqMetadata.getTbName ());
        List<DataColumn> queryColumns = new ArrayList<> ();
        for (IqMetadataCol iqMetadataCol : iqMetadataQueryColList) {
            DataColumn dataColumn = new DataColumn ();
            dataColumn.setSeq (iqMetadataCol.getSeq ());
            dataColumn.setName (iqMetadataCol.getName ());
            dataColumn.setDescribe (iqMetadataCol.getDescribe ());
            dataColumn.setNote (iqMetadataCol.getNote ());
            dataColumn.setLength (iqMetadataCol.getLength ());
            dataColumn.setType (EnumTrans.transDataType (iqMetadataCol.getColType ()));
            queryColumns.add (dataColumn);
        }
        metadata.setQueryColumns (queryColumns);
        List<DataColumn> returnColumns = new ArrayList<> ();
        for (IqMetadataCol iqMetadataCol : iqMetadataReturnColList) {
            DataColumn dataColumn = new DataColumn ();
            dataColumn.setSeq (iqMetadataCol.getSeq ());
            dataColumn.setName (iqMetadataCol.getName ());
            dataColumn.setDescribe (iqMetadataCol.getDescribe ());
            dataColumn.setNote (iqMetadataCol.getNote ());
            dataColumn.setLength (iqMetadataCol.getLength ());
            dataColumn.setType (EnumTrans.transDataType (iqMetadataCol.getColType ()));
            returnColumns.add (dataColumn);
        }
        metadata.setReturnColumns (returnColumns);
        metadata.setDatasource (getDatasource (dsId));
        return metadata;
    }

    public Datasource getDatasource(String dsId) {
        ComDatasource comDatasource = comDatasourceService.select (dsId);
        List<ComProperties> comPropertiesList = comPropertiesService.selectList (dsId);
        return DatasourceUtil.getDatasource (comDatasource, comPropertiesList);
    }

    public boolean testDatasource(Datasource datasource) {
        Provider provider = getProviderImpl (datasource);
        return provider.testDatasource (datasource);
    }

    private Provider getProviderImpl(Datasource datasource) {
        return (Provider) ObjectUtil.newInstance (getImplClass (datasource));
    }

    private String getImplClass(Datasource datasource) {
        String implClass = datasource.getImplClass ();
        if (StringUtils.isBlank (implClass)) {
            GFDict gfDict = gfDictMapper.selectByPrimaryKey (IQ_IMPL_CLASS, datasource.getType ());
            implClass = gfDict.getDictName ();
        }
        return implClass;
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
            len = Integer.valueOf (length);
        }
        return len;
    }
}
