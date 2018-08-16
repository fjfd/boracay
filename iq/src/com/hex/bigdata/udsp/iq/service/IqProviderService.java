package com.hex.bigdata.udsp.iq.service;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.EnumTrans;
import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.common.service.ComDatasourceService;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.bigdata.udsp.common.util.DatasourceUtil;
import com.hex.bigdata.udsp.common.util.ObjectUtil;
import com.hex.bigdata.udsp.iq.model.*;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.model.*;
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
    private static Logger logger = LogManager.getLogger(IqProviderService.class);
    private static final String IQ_IMPL_CLASS = "IQ_IMPL_CLASS";

    private static final FastDateFormat format8 = FastDateFormat.getInstance("yyyyMMdd");
    private static final FastDateFormat format10 = FastDateFormat.getInstance("yyyy-MM-dd");
    private static final FastDateFormat format17 = FastDateFormat.getInstance("yyyyMMdd HH:mm:ss");
    private static final FastDateFormat format19 = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

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
        ComDatasource comDatasource = comDatasourceService.select(dsId);
        List<ComProperties> comPropertiesList = comPropertiesService.selectList(dsId);
        Datasource datasource = DatasourceUtil.getDatasource(comDatasource, comPropertiesList);
        Provider provider = getProviderImpl(datasource);
        return provider.columnInfo(datasource, schemaName);
    }

    /**
     * 查询
     *
     * @param appId
     * @param paraMap
     * @return
     */
    public IqResponse select(String appId, Map<String, String> paraMap) {
        Application application = getApplication(appId, paraMap);
        IqRequest request = new IqRequest(application);
        Datasource datasource = application.getMetadata().getDatasource();
        Provider provider = getProviderImpl(datasource);
        IqResponse response = provider.query(request);
        response.setColumns(getColumns(application.getReturnColumns())); // 设置返回字段信息
        return response;
    }

    /**
     * 分页查询
     *
     * @param appId
     * @param paraMap
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public IqResponse select(String appId, Map<String, String> paraMap, int pageIndex, int pageSize) {
        Application application = getApplication(appId, paraMap);
        IqRequest request = new IqRequest(application);
        Datasource datasource = application.getMetadata().getDatasource();
        Provider provider = getProviderImpl(datasource);
        IqResponse response = provider.query(request, pageIndex, pageSize);
        response.setColumns(getColumns(application.getReturnColumns())); // 设置返回字段信息
        return response;
    }

    /**
     * 返回字段信息插入到Map
     *
     * @param returnColumns
     * @return
     */
    private LinkedHashMap<String, String> getColumns(List<ReturnColumn> returnColumns) {
        LinkedHashMap<String, String> columnMap = new LinkedHashMap<>();
        for (ReturnColumn returnColumn : returnColumns) {
            columnMap.put(returnColumn.getLabel(), returnColumn.getType().getValue());
        }
        return columnMap;
    }

    /**
     * 获取应用
     *
     * @param appId
     * @param paraMap
     * @return
     */
    private Application getApplication(String appId, Map<String, String> paraMap) {
        Application application = getApplication(appId);
//        List<QueryColumn> queryColumns = application.getQueryColumns();
        for (QueryColumn queryColumn : application.getQueryColumns()) {
            boolean isNeed = queryColumn.isNeed();
            DataType type = queryColumn.getType();
            int length = getLen(queryColumn.getLength());
            String label = queryColumn.getLabel();
            String value = (paraMap != null ? paraMap.get(label) : null);
            if (StringUtils.isNotBlank(value)) {
                if (DataType.TIMESTAMP.equals(type)) { // 字段类型是TIMESTAMP
                    value = tarnDateStr(length, value); // 日期格式转换
                }
            }
            if (isNeed) { // 必填
                if (StringUtils.isBlank(value)) {
                    throw new IllegalArgumentException(label + "不能为空");
                }
                queryColumn.setValue(value);
            } else { // 选填
                if (StringUtils.isNotBlank(value)) {
                    queryColumn.setValue(value);
                }
            }
        }
//        application.setQueryColumns(queryColumns);
        return application;
    }

    /**
     * 获取应用
     *
     * @param appId
     * @return
     */
    private Application getApplication(String appId) {
        IqApplication iqApplication = iqApplicationService.select(appId);
        List<IqAppQueryCol> iqAppQueryColList = iqAppQueryColService.selectByAppId(appId);
        List<IqAppReturnCol> iqAppReturnColList = iqAppReturnColService.selectByAppId(appId);
        List<IqAppOrderCol> iqAppOrderColList = iqAppOrderColService.selectByAppId(appId);
        String mdId = iqApplication.getMdId();

        List<ComProperties> appProperties = comPropertiesService.selectList(appId);
        List<Property> appPropertyList = new ArrayList<>(appProperties.size());
        for (ComProperties properties : appProperties) {
            Property property = new Property();
            property.setName(properties.getName());
            property.setValue(properties.getValue());
            property.setDescribe(properties.getDescribe());
            appPropertyList.add(property);
        }

        IqMetadata iqMetadata = iqMetadataService.select(mdId);
        List<IqMetadataCol> iqMetadataQueryColList = iqMetadataColService.selectQueryColList(mdId);
        List<IqMetadataCol> iqMetadataReturnColList = iqMetadataColService.selectReturnColList(mdId);
        String dsId = iqMetadata.getDsId();

        List<ComProperties> mdProperties = comPropertiesService.selectList(mdId);
        List<Property> mdPropertyList = new ArrayList<>(mdProperties.size());
        for (ComProperties properties : mdProperties) {
            Property property = new Property();
            property.setName(properties.getName());
            property.setValue(properties.getValue());
            property.setDescribe(properties.getDescribe());
            mdPropertyList.add(property);
        }

        ComDatasource comDatasource = comDatasourceService.select(dsId);
        List<ComProperties> comPropertiesList = comPropertiesService.selectList(dsId);

        // ----------------数据封装-----------------------
        Application application = new Application(appPropertyList);
        application.setName(iqApplication.getName());
        if (iqApplication.getMaxNum() != null)
            application.setMaxNum(Integer.valueOf(iqApplication.getMaxNum().toString()));
        application.setDescribe(iqApplication.getDescribe());
        application.setNote(iqApplication.getNote());
        List<QueryColumn> queryColumnList = new ArrayList<>();
        for (IqAppQueryCol iqAppQueryCol : iqAppQueryColList) {
            QueryColumn queryColumn = new QueryColumn();
            queryColumn.setSeq(iqAppQueryCol.getSeq());
            queryColumn.setDescribe(iqAppQueryCol.getDescribe());
            queryColumn.setName(iqAppQueryCol.getName());
            queryColumn.setDefaultVal(iqAppQueryCol.getDefaultVal());
            queryColumn.setLabel(iqAppQueryCol.getLabel());
            queryColumn.setLength(iqAppQueryCol.getLength());
            queryColumn.setNeed(EnumTrans.transTrue(iqAppQueryCol.getIsNeed()));
            queryColumn.setOfferOut(EnumTrans.transTrue(iqAppQueryCol.getIsOfferOut()));
            queryColumn.setOperator(EnumTrans.transOperator(iqAppQueryCol.getOperator()));
            queryColumn.setType(EnumTrans.transDataType(iqAppQueryCol.getType()));
            //queryColumn.setValue(paraMap.get(iqAppQueryCol.getName()));
            queryColumnList.add(queryColumn);
        }
        application.setQueryColumns(queryColumnList);
        List<ReturnColumn> returnColumnList = new ArrayList<>();
        for (IqAppReturnCol iqAppReturnCol : iqAppReturnColList) {
            ReturnColumn returnColumn = new ReturnColumn();
            returnColumn.setSeq(iqAppReturnCol.getSeq());
            returnColumn.setDescribe(iqAppReturnCol.getDescribe());
            returnColumn.setName(iqAppReturnCol.getName());
            returnColumn.setLabel(iqAppReturnCol.getLabel());
            returnColumn.setLength(iqAppReturnCol.getLength());
            returnColumn.setStats(EnumTrans.transStats(iqAppReturnCol.getStats()));
            returnColumn.setType(EnumTrans.transDataType(iqAppReturnCol.getType()));
            returnColumnList.add(returnColumn);
        }
        application.setReturnColumns(returnColumnList);
        List<OrderColumn> orderColumnList = new ArrayList<>();
        for (IqAppOrderCol iqAppOrderCol : iqAppOrderColList) {
            OrderColumn orderColumn = new OrderColumn();
            orderColumn.setSeq(iqAppOrderCol.getSeq());
            orderColumn.setDescribe(iqAppOrderCol.getDescribe());
            orderColumn.setName(iqAppOrderCol.getName());
            orderColumn.setType(EnumTrans.transDataType(iqAppOrderCol.getType()));
            orderColumn.setOrder(EnumTrans.transOrder(iqAppOrderCol.getOrderType()));
            orderColumnList.add(orderColumn);
        }
        application.setOrderColumns(orderColumnList);

        Metadata metadata = new Metadata(mdPropertyList);
        metadata.setName(iqMetadata.getName());
        metadata.setDescribe(iqMetadata.getDescribe());
        metadata.setNote(iqMetadata.getNote());
        metadata.setTbName(iqMetadata.getTbName());
        List<DataColumn> queryColumns = new ArrayList<>();
        for (IqMetadataCol iqMetadataCol : iqMetadataQueryColList) {
            DataColumn dataColumn = new DataColumn();
            dataColumn.setSeq(iqMetadataCol.getSeq());
            dataColumn.setName(iqMetadataCol.getName());
            dataColumn.setDescribe(iqMetadataCol.getDescribe());
            dataColumn.setNote(iqMetadataCol.getNote());
            dataColumn.setLength(iqMetadataCol.getLength());
            dataColumn.setType(EnumTrans.transDataType(iqMetadataCol.getColType()));
            queryColumns.add(dataColumn);
        }
        metadata.setQueryColumns(queryColumns);
        List<DataColumn> returnColumns = new ArrayList<>();
        for (IqMetadataCol iqMetadataCol : iqMetadataReturnColList) {
            DataColumn dataColumn = new DataColumn();
            dataColumn.setSeq(iqMetadataCol.getSeq());
            dataColumn.setName(iqMetadataCol.getName());
            dataColumn.setDescribe(iqMetadataCol.getDescribe());
            dataColumn.setNote(iqMetadataCol.getNote());
            dataColumn.setLength(iqMetadataCol.getLength());
            dataColumn.setType(EnumTrans.transDataType(iqMetadataCol.getColType()));
            returnColumns.add(dataColumn);
        }
        metadata.setReturnColumns(returnColumns);

        Datasource datasource = DatasourceUtil.getDatasource(comDatasource, comPropertiesList);

        metadata.setDatasource(datasource);
        application.setMetadata(metadata);

        return application;
    }

    public boolean testDatasource(Datasource datasource) {
        Provider provider = getProviderImpl(datasource);
        return provider.testDatasource(datasource);
    }

    private Provider getProviderImpl(Datasource datasource) {
        return (Provider) ObjectUtil.newInstance(getImplClass(datasource));
    }

    private String getImplClass(Datasource datasource) {
        String implClass = datasource.getImplClass();
        if (StringUtils.isBlank(implClass)) {
            GFDict gfDict = gfDictMapper.selectByPrimaryKey(IQ_IMPL_CLASS, datasource.getType());
            implClass = gfDict.getDictName();
        }
        return implClass;
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
            len = Integer.valueOf(length);
        }
        return len;
    }
}
