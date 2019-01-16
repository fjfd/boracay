package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.iq.provider.model.*;
import com.hex.bigdata.udsp.iq.service.IqProviderService;
import com.hex.bigdata.udsp.rc.model.RcService;
import com.hex.bigdata.udsp.rc.service.RcServiceService;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费的服务
 */
@Service
public class ExternalConsumerService {
    private static Logger logger = LogManager.getLogger (ExternalConsumerService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RcServiceService rcServiceService;
    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private IqProviderService iqProviderService;

    /**
     * 外部请求消费
     *
     * @param request 外部请求内容
     * @return
     */
    public Response consume(Request request) {
        logger.debug ("Request=" + JSONUtil.parseObj2JSON (request));
        long bef = System.currentTimeMillis ();

        ConsumeRequest consumeRequest = checkConsume (request, bef);
        logger.debug ("检查耗时：" + (System.currentTimeMillis () - bef) + "ms");

        long bef2 = System.currentTimeMillis ();
        Response response = consumerService.consume (consumeRequest, bef);
        logger.debug ("执行耗时：" + (System.currentTimeMillis () - bef2) + "ms");

        long now = System.currentTimeMillis ();
        long consumeTime = now - bef;
        response.setConsumeTime (consumeTime);
        logger.debug ("请求耗时：" + consumeTime + "ms");

        return response;
    }

    /**
     * 外部消费前检查
     */
    private ConsumeRequest checkConsume(Request request, long bef) {
        ConsumeRequest consumeRequest = new ConsumeRequest ();
        consumeRequest.setRequest (request); // 必须先设置request
        request.setRequestType (RequestType.OUTER.getValue ());
        String serviceName = request.getServiceName ();
        String udspUser = request.getUdspUser ();
        String udspPass = request.getToken ();
        String entity = request.getEntity ();
        String type = request.getType ();
        //外部调用必输参数检查
        if (StringUtils.isBlank (serviceName)
                || StringUtils.isBlank (udspUser) || StringUtils.isBlank (udspPass)
                || StringUtils.isBlank (entity) || StringUtils.isBlank (type)) {
            consumeRequest.setError (ErrorCode.ERROR_000009);
            return consumeRequest;
        }
        //消费前公共输入参数检查
        //异同步类型检查和entity类型检查
        if (!(
                ConsumerType.SYNC.getValue ().equalsIgnoreCase (type)
                        || ConsumerType.ASYNC.getValue ().equalsIgnoreCase (type)
        ) || !(
                ConsumerEntity.STATUS.getValue ().equalsIgnoreCase (entity)
                        || ConsumerEntity.START.getValue ().equalsIgnoreCase (entity)
                        || ConsumerEntity.STOP.getValue ().equalsIgnoreCase (entity)
        )) {
            consumeRequest.setError (ErrorCode.ERROR_000010);
            return consumeRequest;
        }
        //检查用户身份合法性
        MessageResult messageResult = userService.validateUser (udspUser, udspPass);
        if (!messageResult.isStatus ()) {
            consumeRequest.setError (ErrorCode.ERROR_000002);
            return consumeRequest;
        }
        //检查授权访问信息
        RcService rcService = rcServiceService.selectByServiceName (serviceName);
        return consumerService.checkConsume (request, udspUser, rcService, bef);
    }

    // -----------------------------------针对自定义SQL提供如下方法-----------------------------------------

    /**
     * 获取自定义SQL支持的服务列表
     *
     * @return
     */
    public Response showServices(String likeName) {
        Response response = new Response ();
        List<Map<String, String>> records = new ArrayList<> ();
        Map<String, String> record = null;
        List<RcService> iqServices = rcServiceService.selectStartByTypeAndName (ServiceType.IQ.getValue (), likeName);
        List<RcService> iqDslServices = rcServiceService.selectStartByTypeAndName (ServiceType.IQ_DSL.getValue (), likeName);
        if (iqServices == null) iqServices = new ArrayList<> ();
        iqServices.addAll (iqDslServices);
        if (iqServices.size () != 0) {
            for (RcService service : iqServices) {
                record = new HashMap<> ();
                record.put ("name", service.getName ());
                record.put ("type", service.getType ());
                record.put ("comment", service.getDescribe ());
                records.add (record);
            }
        }
        response.setRecords (records);
        response.setStatus (Status.SUCCESS.getValue ());
        response.setStatusCode (StatusCode.SUCCESS.getValue ());
        return response;
    }

    /**
     * 获取自定义SQL支持的服务字段信息
     *
     * @param serviceName
     * @return
     */
    public Response describeService(String serviceName) {
        Response response = new Response ();
        List<Map<String, String>> records = new ArrayList<> ();
        Map<String, String> record = null;
        RcService service = rcServiceService.selectByName (serviceName);
        if (service == null) {
            response.setStatus (Status.DEFEAT.getValue ());
            response.setStatusCode (StatusCode.DEFEAT.getValue ());
            response.setErrorCode (ErrorCode.ERROR_000099.getValue ());
            response.setMessage ("服务名错误或不存在");
            return response;
        }
        try {
            String appType = service.getType ();
            String appId = service.getAppId ();
            if (ServiceType.IQ.getValue ().equals (appType)) { // 交互查询应用的自定义SQL
                Application application = iqProviderService.getApplication (appId);
                List<QueryColumn> queryColumns = application.getQueryColumns ();
                List<ReturnColumn> returnColumns = application.getReturnColumns ();
                List<OrderColumn> orderColumns = application.getOrderColumns ();
                if (queryColumns != null && queryColumns.size () != 0) {
                    record = new HashMap<> ();
                    record.put ("name", "query:");
                    /*
                    为空时也必须都设置，否则jdbc客户端可能无法获取到元字段信息。
                     */
                    record.put ("operator", "");
                    record.put ("type", "");
                    record.put ("required", "");
                    //record.put ("default", "");
                    record.put ("comment", "");
                    records.add (record);
                    for (QueryColumn column : queryColumns) {
                        record = new HashMap<> ();
                        record.put ("name", column.getLabel ());
                        record.put ("operator", column.getOperator ().getValue ());
                        record.put ("type", StringUtils.isBlank (column.getLength ()) ? column.getType ().getValue ()
                                : column.getType ().getValue () + "(" + column.getLength () + ")");
                        record.put ("required", String.valueOf (column.isNeed ()));
                        //record.put ("default", column.getDefaultVal ());
                        record.put ("comment", column.getDescribe ());
                        records.add (record);
                    }
                }
                if (returnColumns != null && returnColumns.size () != 0) {
                    record = new HashMap<> ();
                    record.put ("name", "return:");
                    /*
                    为空时也必须都设置，否则jdbc客户端可能无法获取到元字段信息。
                     */
                    record.put ("operator", "");
                    record.put ("type", "");
                    record.put ("required", "");
                    //record.put ("default", "");
                    record.put ("comment", "");
                    records.add (record);
                    for (ReturnColumn column : returnColumns) {
                        record = new HashMap<> ();
                        record.put ("name", column.getLabel ());
                        record.put ("operator", "");
                        record.put ("type", StringUtils.isBlank (column.getLength ()) ? column.getType ().getValue ()
                                : column.getType ().getValue () + "(" + column.getLength () + ")");
                        record.put ("required", "");
                        //record.put ("default", "");
                        record.put ("comment", column.getDescribe ());
                        records.add (record);
                    }
                }
                if (orderColumns != null && orderColumns.size () != 0) {
                    record = new HashMap<> ();
                    record.put ("name", "order:");
                    /*
                    为空时也必须都设置，否则jdbc客户端可能无法获取到元字段信息。
                     */
                    record.put ("operator", "");
                    record.put ("type", "");
                    record.put ("required", "");
                    //record.put ("default", "");
                    record.put ("comment", "");
                    records.add (record);
                    for (OrderColumn column : orderColumns) {
                        record = new HashMap<> ();
                        record.put ("name", column.getName ());
                        record.put ("operator", column.getOrder ().getValue ());
                        record.put ("type", column.getType ().getValue ());
                        record.put ("required", "");
                        //record.put ("default", "");
                        record.put ("comment", column.getDescribe ());
                        records.add (record);
                    }
                }
            } else if (ServiceType.IQ_DSL.getValue ().equals (appType)) { // 交互查询的自定义SQL
                Metadata metadate = iqProviderService.getMetadata (appId);
                List<DataColumn> queryColumns = metadate.getQueryColumns ();
                List<DataColumn> returnColumns = metadate.getReturnColumns ();
                if (queryColumns != null && queryColumns.size () != 0) {
                    record = new HashMap<> ();
                    record.put ("name", "query:");
                    /*
                    为空时也必须都设置，否则jdbc客户端可能无法获取到元字段信息。
                     */
                    record.put ("type", "");
                    record.put ("comment", "");
                    records.add (record);
                    for (DataColumn column : queryColumns) {
                        record = new HashMap<> ();
                        record.put ("name", column.getName ());
                        record.put ("type", StringUtils.isBlank (column.getLength ()) ? column.getType ().getValue ()
                                : column.getType ().getValue () + "(" + column.getLength () + ")");
                        record.put ("comment", column.getDescribe ());
                        records.add (record);
                    }
                }
                if (returnColumns != null && returnColumns.size () != 0) {
                    record = new HashMap<> ();
                    record.put ("name", "return:");
                    /*
                    为空时也必须都设置，否则jdbc客户端可能无法获取到元字段信息。
                     */
                    record.put ("type", "");
                    record.put ("comment", "");
                    records.add (record);
                    for (DataColumn column : returnColumns) {
                        record = new HashMap<> ();
                        record.put ("name", column.getName ());
                        record.put ("type", StringUtils.isBlank (column.getLength ()) ? column.getType ().getValue ()
                                : column.getType ().getValue () + "(" + column.getLength () + ")");
                        record.put ("comment", column.getDescribe ());
                        records.add (record);
                    }
                }
            }
            response.setRecords (records);
            response.setStatus (Status.SUCCESS.getValue ());
            response.setStatusCode (StatusCode.SUCCESS.getValue ());
        } catch (Exception e) {
            response.setStatus (Status.DEFEAT.getValue ());
            response.setStatusCode (StatusCode.DEFEAT.getValue ());
            response.setErrorCode (ErrorCode.ERROR_000007.getValue ());
            response.setMessage (e.getMessage ());
        }
        return response;
    }

    /**
     * 通过服务名称获取服务类型
     *
     * @param serviceName
     * @return
     */
    public String getServiceType(String serviceName) {
        RcService service = rcServiceService.selectByName (serviceName);
        if (service == null) {
            return null;
        }
        return service.getType ();
    }

}
