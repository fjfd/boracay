package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.*;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.util.Util;
import com.hex.bigdata.udsp.mm.dto.MmFullAppInfoView;
import com.hex.bigdata.udsp.mm.model.MmAppExecuteParam;
import com.hex.bigdata.udsp.mm.model.MmAppReturnParam;
import com.hex.bigdata.udsp.mm.provider.model.MmRequest;
import com.hex.bigdata.udsp.mm.provider.model.MmResponse;
import com.hex.bigdata.udsp.mm.provider.model.MmResponseData;
import com.hex.bigdata.udsp.mm.service.MmApplicationService;
import com.hex.bigdata.udsp.mm.service.MmProviderService;
import com.hex.goframe.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型请求的服务
 */
@Service
public class MmSyncService {
    private static final Map<String, String> typeMap = new HashMap<String, String> () {{
        put ("sync", "1");
        put ("async", "2");
    }};

    /**
     * 模型调度最大返回记录数
     */
    @Value("${udsp.mm.return.maxnum}")
    private long returnLimit;

    /**
     * 模型调用服务
     */
    @Autowired
    private MmProviderService mmProviderService;
    @Autowired
    private MmApplicationService mmApplicationService;

    /**
     * 调用（同步、异步）
     *
     * @param request 请求实体
     * @return
     */
    public Response start(Request request) {
        long bef = System.currentTimeMillis ();
        Response response = new Response ();
        try {
            checkParam (request.getAppId (), request.getData ());
        } catch (Exception e) {
            return Util.errorResponse (ErrorCode.ERROR_200003, e.toString ());
        }
        MmFullAppInfoView appInfoView = mmApplicationService.selectFullAppInfo (request.getAppId ());
        if (appInfoView == null) {
            return Util.errorResponse (ErrorCode.ERROR_200004, "获取不到模型应用信息!");
        }
        String modelType = appInfoView.getModelType ();
        String type = request.getType ().toLowerCase ();
        String typeKey = typeMap.get (type);
        if (!modelType.contains (typeKey)) {
            return Util.errorResponse (ErrorCode.ERROR_200005, "该模型不支持" + type + "类型!");
        }
        MmRequest mmRequest = getMmRequest (appInfoView, request);
        try {
            MmResponse mmResponse = mmProviderService.request (mmRequest, appInfoView.getHttpUrl ());
            response.setStatus (mmResponse.getStatus ());
            response.setStatusCode (Status.SUCCESS.getValue ().equals (mmResponse.getStatus ())
                    ? StatusCode.SUCCESS.getValue () : StatusCode.DEFEAT.getValue ());
            response.setErrorCode (Status.DEFEAT.getValue ().equals (mmResponse.getStatus ())
                    ? ErrorCode.ERROR_200004.getValue () : "");
            response.setMessage (mmResponse.getMessage ());
            if (Status.SUCCESS.getValue ().equals (mmResponse.getStatus ())) {
                MmResponseData mmResponseData = mmResponse.getData ();
                if (mmResponseData != null) {
                    response.setRecords (mmResponseData.getRecords ());
                    response.setResponseContent (mmResponseData.getFile ());
                }
            }
            response.setConsumeTime (System.currentTimeMillis () - bef);
            return response;
        } catch (ConnectException e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_200001, e.toString ());
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_200004, e.toString ());
        }
    }

    private MmRequest getMmRequest(MmFullAppInfoView appInfoView, Request request) {
        MmRequest mmRequest = new MmRequest ();
        mmRequest.setType (request.getType ());
        mmRequest.setEntity (request.getEntity ());
        mmRequest.setModelName (appInfoView.getModelName ());
        mmRequest.setAppUser (request.getAppUser ());
        mmRequest.setLimit (returnLimit);
        mmRequest.setUuid (request.getConsumeId ());
        List<MmAppReturnParam> returnParams = appInfoView.getReturnParams ();
        if (returnParams != null && returnParams.size () != 0) {
            String responseField = "";
            int count = 0;
            for (MmAppReturnParam returnParam : returnParams) {
                responseField += (count == 0 ? "" : ",") + returnParam.getName ();
                count++;
            }
            mmRequest.setResponseField (responseField);
        }
        Map<String, String> paraMap = request.getData ();
        List<MmAppExecuteParam> executeParams = appInfoView.getExecuteParams ();
        if (executeParams != null && executeParams.size () != 0) {
            Map<String, String> map = new HashMap<> ();
            for (MmAppExecuteParam executeParam : executeParams) {
                String name = executeParam.getName ();
                String value = (paraMap != null ? paraMap.get (name) : null);
                map.put (name, value);
            }
            mmRequest.setRequest (map);
        }
        if (ConsumerType.ASYNC.getValue ().equalsIgnoreCase (request.getType ())) {
            String path = CreateFileUtil.getFtpFileDir (request.getUdspUser ()) + "/" +
                    appInfoView.getContractorName () + "/" + DateUtil.format (new Date (), "yyyyMMdd");
            mmRequest.setPath (path);
        }
        return mmRequest;
    }

    /**
     * 查看状态
     *
     * @param request
     * @return
     */
    public Response status(Request request) {
        long bef = System.currentTimeMillis ();
        Response response = new Response ();
        MmFullAppInfoView appInfoView = mmApplicationService.selectFullAppInfo (request.getAppId ());
        if (appInfoView == null) {
            return Util.errorResponse (ErrorCode.ERROR_200004, "获取不到模型应用信息!");
        }
        MmRequest mmRequest = new MmRequest ();
        mmRequest.setModelName (appInfoView.getModelName ());
        mmRequest.setType (request.getType ());
        mmRequest.setEntity (request.getEntity ());
        mmRequest.setUuid (request.getConsumeId ());
        try {
            MmResponse mmResponse = mmProviderService.request (mmRequest, appInfoView.getHttpUrl ());
            response.setStatus (mmResponse.getStatus ());
            response.setStatusCode (Status.SUCCESS.getValue ().equals (mmResponse.getStatus ())
                    ? StatusCode.SUCCESS.getValue () : StatusCode.DEFEAT.getValue ());
            response.setErrorCode (Status.DEFEAT.getValue ().equals (mmResponse.getStatus ())
                    ? ErrorCode.ERROR_200004.getValue () : "");
            response.setMessage (mmResponse.getMessage ());
            response.setConsumeTime (System.currentTimeMillis () - bef);
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }
        return response;
    }

    /**
     * 检查输入的参数
     *
     * @param appId
     * @param paraMap
     * @return
     */
    private void checkParam(String appId, Map<String, String> paraMap) throws Exception {
        boolean isError = false;
        String message = "";
        int count = 0;
        for (MmAppExecuteParam mmAppExecuteParam : mmApplicationService.selectFullAppInfo (appId).getExecuteParams ()) {
            if (EnumTrans.transTrue (mmAppExecuteParam.getIsNeed ())) {
                String name = mmAppExecuteParam.getName ();
                String value = paraMap.get (name);
                if (StringUtils.isBlank (value)) {
                    message += (count == 0 ? "" : ", ") + name;
                    isError = true;
                    count++;
                }
            }
        }
        if (isError) {
            throw new Exception (message + "参数不能为空!");
        }
    }
}
