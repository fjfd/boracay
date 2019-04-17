package com.hex.bigdata.udsp.rts.service;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.service.ComDatasourceService;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.bigdata.udsp.common.util.DatasourceUtil;
import com.hex.bigdata.udsp.common.util.ObjectUtil;
import com.hex.bigdata.udsp.rts.executor.Executor;
import com.hex.bigdata.udsp.rts.executor.model.*;
import com.hex.bigdata.udsp.rts.model.RtsConsumer;
import com.hex.bigdata.udsp.rts.model.RtsMetadata;
import com.hex.bigdata.udsp.rts.model.RtsMetadataCol;
import com.hex.bigdata.udsp.rts.model.RtsProducer;
import com.hex.goframe.dao.GFDictMapper;
import com.hex.goframe.model.GFDict;
import com.hex.goframe.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by tomnic on 2017/3/7.
 */
@Service
public class RtsExecutorService extends BaseService {

    private static Logger logger = LogManager.getLogger (RtsExecutorService.class);

    private static final String RTS_IMPL_CLASS = "RTS_IMPL_CLASS";

    @Autowired
    private ComPropertiesService comPropertiesService;
    @Autowired
    private RtsMetadataService rtsMetadataService;
    @Autowired
    private ComDatasourceService comDatasourceService;
    @Autowired
    private RtsMetadataColService rtsMetadataColService;
    @Autowired
    private RtsConsumerService rtsConsumerService;
    @Autowired
    private RtsProducerService rtsProducerService;
    @Autowired
    private GFDictMapper gfDictMapper;

    /**
     * 获取消费者应用信息
     *
     * @param consumerId
     * @param timeout
     * @return
     */
    public ConsumerResponse consumer(String consumerId, long timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException ("超时时间不能小于等于0");
        }
        Application application = getConsumerApplication (consumerId);
        Datasource datasource = application.getMetadata ().getDatasource ();
        ConsumerRequest consumerRequest = new ConsumerRequest (application, timeout);
        ConsumerResponse consumerResponse = getExecutorImpl (datasource).pull (consumerRequest);
        consumerResponse.setColumns (getColumnMap (application.getMetadata ().getColumns ())); // 设置返回字段信息
        return consumerResponse;
    }

    /**
     * 获取生产者应用信息
     *
     * @param producerId
     * @param datas
     * @return
     */
    public ProducerResponse producer(String producerId, List<Map<String, String>> datas) {
        Application application = getProducerApplication (producerId);
        List<Map<String, String>> messageDatas = getMessageDatas (application, datas);
        ProducerRequest producerRequest = new ProducerRequest (application, messageDatas);
        Datasource datasource = application.getMetadata ().getDatasource ();
        return getExecutorImpl (datasource).push (producerRequest);
    }

    private List<Map<String, String>> getMessageDatas(Application application, List<Map<String, String>> datas) {
        List<MetadataCol> metadataCols = application.getMetadata ().getColumns ();
        List<Map<String, String>> messageDatas = new ArrayList<> ();
        for (Map<String, String> data : datas) {
            Map<String, String> messageData = new HashMap<> ();
//            String message = "";
            for (MetadataCol col : metadataCols) {
                String name = col.getName ();
                String value = data.get (name);
//                if (StringUtils.isBlank (value)) {
//                    message += (StringUtils.isBlank (message) ? "" : ", ") + name;
//                }
                messageData.put (name, value);
            }
//            if (StringUtils.isNotBlank (message)) {
//                throw new IllegalArgumentException (message + "参数不能为空!");
//            }
            messageDatas.add (messageData);
        }
        return messageDatas;
    }

    /**
     * 测试数据源
     *
     * @param datasource
     * @return
     */
    public boolean testDatasource(com.hex.bigdata.udsp.common.api.model.Datasource datasource) {
        return getExecutorImpl (datasource).testDatasource (datasource);
    }

    /**
     * 得到生产接口的实例
     *
     * @param datasource
     * @return
     */
    private Executor getExecutorImpl(com.hex.bigdata.udsp.common.api.model.Datasource datasource) {
        return (Executor) ObjectUtil.newInstance (getImplClass (datasource));
    }

    private String getImplClass(com.hex.bigdata.udsp.common.api.model.Datasource datasource) {
        String implClass = datasource.getImplClass ();
        if (StringUtils.isBlank (implClass)) {
            GFDict gfDict = gfDictMapper.selectByPrimaryKey (RTS_IMPL_CLASS, datasource.getType ());
            implClass = gfDict.getDictName ();
        }
        return implClass;
    }

    private LinkedHashMap<String, String> getColumnMap(List<MetadataCol> metadataCols) {
        LinkedHashMap<String, String> columnMap = new LinkedHashMap<> ();
        for (MetadataCol col : metadataCols) {
            columnMap.put (col.getName (), col.getType ());
        }
        return columnMap;
    }

    private Application getConsumerApplication(String consumerId) {
        RtsConsumer rtsConsumer = rtsConsumerService.select (consumerId);
        RtsMetadata rtsMetadata = rtsMetadataService.select (rtsConsumer.getMdId ());
        List<RtsMetadataCol> rtsMetadataCols = rtsMetadataColService.selectByMdId (rtsMetadata.getPkId ());
        ComDatasource comDatasource = comDatasourceService.select (rtsMetadata.getDsId ());
        List<ComProperties> datasourceProperties = comPropertiesService.selectList (comDatasource.getPkId ());
        List<ComProperties> applicationProperties = comPropertiesService.selectList (rtsConsumer.getPkId ());
        datasourceProperties.addAll (applicationProperties);
        Datasource datasource = new Datasource (DatasourceUtil.getDatasource (comDatasource, datasourceProperties));
        Metadata metadata = getMetadata (datasource, rtsMetadata, rtsMetadataCols);
        Application application = new Application ();
        application.setMetadata (metadata);
        application.setName (rtsConsumer.getName ());
        application.setDescribe (rtsConsumer.getDescribe ());
        application.setName (rtsConsumer.getNote ());
        return application;
    }

    private Metadata getMetadata(Datasource datasource, RtsMetadata rtsMetadata, List<RtsMetadataCol> rtsMetadataCols) {
        Metadata metadata = new Metadata ();
        metadata.setDatasource (datasource);
        metadata.setName (rtsMetadata.getName ());
        metadata.setDescribe (rtsMetadata.getDescribe ());
        metadata.setNote (rtsMetadata.getNote ());
        metadata.setTopic (rtsMetadata.getTopic ());
        metadata.setColumns (getColumns (rtsMetadataCols));
        return metadata;
    }

    private Application getProducerApplication(String producerId) {
        RtsProducer rtsProducer = rtsProducerService.select (producerId);
        RtsMetadata rtsMetadata = rtsMetadataService.select (rtsProducer.getMdId ());
        List<RtsMetadataCol> rtsMetadataCols = rtsMetadataColService.selectByMdId (rtsMetadata.getPkId ());
        ComDatasource comDatasource = comDatasourceService.select (rtsMetadata.getDsId ());
        List<ComProperties> datasourceProperties = comPropertiesService.selectList (comDatasource.getPkId ());
        List<ComProperties> applicationProperties = comPropertiesService.selectList (rtsProducer.getPkId ());
        datasourceProperties.addAll (applicationProperties);
        Datasource datasource = new Datasource (DatasourceUtil.getDatasource (comDatasource, datasourceProperties));
        Metadata metadata = getMetadata (datasource, rtsMetadata, rtsMetadataCols);
        Application producerApplication = new Application ();
        producerApplication.setName (rtsProducer.getName ());
        producerApplication.setDescribe (rtsProducer.getDescribe ());
        producerApplication.setNote (rtsProducer.getNote ());
        producerApplication.setMetadata (metadata);
        return producerApplication;
    }

    private List<MetadataCol> getColumns(List<RtsMetadataCol> rtsMetadataCols) {
        List<MetadataCol> columns = new ArrayList<> ();
        for (RtsMetadataCol col : rtsMetadataCols) {
            MetadataCol column = new MetadataCol ();
            column.setName (col.getName ());
            column.setDescribe (col.getDescribe ());
            column.setSeq (col.getSeq ());
            column.setType (col.getType ());
        }
        return columns;
    }
}
