package com.hex.bigdata.udsp.mm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.mm.constant.MmConstant;
import com.hex.bigdata.udsp.mm.dto.MmFullAppInfoView;
import com.hex.bigdata.udsp.mm.dto.MmRequest;
import com.hex.bigdata.udsp.mm.dto.MmResponse;
import com.hex.bigdata.udsp.mm.dto.MmResponseData;
import com.hex.bigdata.udsp.mm.model.MmAppExecuteParam;
import com.hex.bigdata.udsp.mm.model.MmAppReturnParam;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.goframe.util.DateUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 远程调用服务接口
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/5/3
 * TIME:9:32
 */
@Service
public class MmProviderService {

    private static Logger logger = LogManager.getLogger(MmProviderService.class);

    private static final String APPLICATION_JSON = "application/json";

    private static final String CONTEXT_TYPE_TEXT_JSON = "text/json";

    private static final Map<String, String> typeMap = new HashMap<String, String>() {{
        put("sync", "1");
        put("async", "2");
        put("batch", "3");
    }};

    @Autowired
    private MmApplicationService mmApplicationService;

    /**
     * 模型调度最大返回记录数
     */
    @Value("${udsp.mm.return.maxnum}")
    private long returnLimit;

    /**
     * @param appId     应用ID
     * @param consumeId 消费ID
     * @param request   请求实体
     * @return
     */
    public MmResponse start(String appId, String consumeId, Request request) {
        MmFullAppInfoView appInfoView = mmApplicationService.selectFullAppInfo(appId);
        if (appInfoView == null) {
            return null;
        }
        //请求参数
        MmRequest mmRequest = null;
        //返回对象
        MmResponse response = null;
        Map<String, Object> resultMap = new HashMap<>();
        response = this.initResponse(request);
        try {
            resultMap = initRequest(appInfoView, request);
            String errorCode = (String) resultMap.get("errorCode");
            //如果有错误
            if (StringUtils.isNotBlank(errorCode)) {
                response.setSystemStatus(Status.DEFEAT);
                response.setStatusCode(StatusCode.DEFEAT);
                if (ErrorCode.ERROR_000009.getValue().equals(errorCode)) {
                    response.setErrorCode(ErrorCode.ERROR_000009.getValue());
                    response.setMessage(ErrorCode.ERROR_000009.getName());
                } else if (ErrorCode.ERROR_200002.getValue().equals(errorCode)) {
                    response.setErrorCode(ErrorCode.ERROR_200002.getValue());
                    response.setMessage(ErrorCode.ERROR_200002.getName());
                } else {
                    response.setErrorCode(ErrorCode.ERROR_000099.getValue());
                    response.setMessage(ErrorCode.ERROR_000099.getName());
                }
                return response;
            }
        } catch (Exception e) {
            response.setSystemStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setErrorCode(ErrorCode.ERROR_000099.getValue());
            response.setMessage(ErrorCode.ERROR_000099.getName());
        }
        mmRequest = (MmRequest) resultMap.get("request");
        //设置消费id为uuid
        mmRequest.setUuid(consumeId);

        try {
            response = this.requestModelService(mmRequest, appInfoView.getHttpUrl());
        }catch (ConnectException e){
            response.setErrorCode(ErrorCode.ERROR_200001.getValue());
            response.setMessage(ErrorCode.ERROR_200001.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isNotBlank(response.getStatus()) && Status.SUCCESS.getValue().equals(response.getStatus())) {
            response.setSystemStatus(Status.SUCCESS);
            response.setStatusCode(StatusCode.SUCCESS);
        } else {
            response.setSystemStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
        }
        return response;
    }

    /**
     * 初始化返回参数
     * @param request
     * @return
     */
    private MmResponse initResponse(Request request) {
        MmResponse mmResponse = new MmResponse();
        mmResponse.setServiceName(request.getServiceName());
        mmResponse.setEntity(request.getEntity());
        mmResponse.setUuid(request.getConsumeId());
        mmResponse.setType(request.getType());
        return mmResponse;
    }

    /**
     * 初始化请求参数
     *
     * @param appInfoView
     * @return
     */
    private Map<String, Object> initRequest(MmFullAppInfoView appInfoView, Request request) {
        Map<String, String> params = request.getData();
        Map<String, Object> resultMap = new HashMap<>();
        String errorCode = "";

        //判断模型是否支持请求中的业务类型
        String modelType = appInfoView.getModelType();
        String requestType = request.getType().toLowerCase();
        String typeKey = typeMap.get(requestType);
        if (modelType.indexOf(typeKey) == -1) {
            resultMap.put("errorCode", ErrorCode.ERROR_200002.getValue());
            return resultMap;
        }
        MmRequest mmRequest = new MmRequest();
        mmRequest.setEntity(request.getEntity());
        mmRequest.setAppUser(request.getAppUser());
        mmRequest.setLimit(returnLimit);
        mmRequest.setServiceName(request.getServiceName());
        mmRequest.setType(request.getType());
        mmRequest.setUuid(request.getConsumeId());
        //mmRequest.setAppId(appInfoView.getAppId());
        mmRequest.setModelName(appInfoView.getModelName());

        List<MmAppReturnParam> returnParams = appInfoView.getReturnParams();
        if (returnParams != null && returnParams.size() != 0) {
            StringBuffer returnString = new StringBuffer();
            for (MmAppReturnParam returnParam : returnParams) {
                returnString.append(returnParam.getName()).append(",");
            }
            mmRequest.setReponseField(returnString.substring(0, returnString.length() - 1));
        }

        //执行参数解析、设置
        List<MmAppExecuteParam> executeParams = appInfoView.getExecuteParams();
        if (executeParams != null || executeParams.size() != 0) {
            Map<String, String> conditions = new HashMap<>();
            for (MmAppExecuteParam executeParam : executeParams) {
                String paramName = executeParam.getName();
                String value = params.get(paramName);
                //参数为必输参数，但是参数为空则报错
                if (MmConstant.MM_PARAM_IS_NEED_YES.equals(executeParam.getIsNeed()) && StringUtils.isBlank(value)) {
                    resultMap.put("errorCode", ErrorCode.ERROR_000009.getValue());
                    return resultMap;
                }
                conditions.put(paramName, value);
            }
            mmRequest.setRequest(conditions);
        }

        //如果是异步调用则设置path
        if (MmConstant.MM_TYPE_ASYNC.equalsIgnoreCase(request.getType())) {
            String path = CreateFileUtil.getFtpFileDir(request.getUdspUser()) + "/" +
                    appInfoView.getContractorName() + "/"+ DateUtil.format(new Date(), "yyyyMMdd");
            mmRequest.setPath(path);
        }

        resultMap.put("request", mmRequest);
        resultMap.put("errorCode", errorCode);

        return resultMap;
    }

    /**
     * 模型调用-查看模型运行状态
     * @param request
     * @return
     */
    public MmResponse status(Request request,String appId) throws Exception {
        MmFullAppInfoView appInfoView = mmApplicationService.selectFullAppInfo(appId);
        if (appInfoView == null) {
            return null;
        }
        //请求参数
        MmRequest mmRequest = new MmRequest();
        //返回对象
        MmResponse response = null;
        mmRequest.setModelName(appInfoView.getModelName());
        mmRequest.setUuid(request.getConsumeId());
        mmRequest.setAppUser(request.getAppUser());
        mmRequest.setServiceName(request.getServiceName());
        mmRequest.setEntity(request.getEntity());
        mmRequest.setType(request.getType());

        response = this.requestModelService(mmRequest, appInfoView.getHttpUrl());
        return response;
    }

    /**
     * 公共方法
     *
     * @param mmRequest
     * @param url
     */
    private MmResponse requestModelService (MmRequest mmRequest, String url)throws Exception {

        JSONObject jsonObject = (JSONObject) JSON.toJSON(mmRequest);

        logger.info("请求地址："+url);
        //打印json参数
        logger.info("请求参数："+jsonObject.toJSONString());

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
        InputStream resultStream = null;
        MmResponse responseObject = null;
        StringEntity se = new StringEntity(jsonObject.toJSONString());
        se.setContentType(CONTEXT_TYPE_TEXT_JSON);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
        httpPost.setEntity(se);
        HttpResponse response = httpClient.execute(httpPost);
        String returnString = analysisResponse(response);

        logger.warn("模型返回信息：" + returnString);

        JSONObject returnObject=JSON.parseObject(returnString);

        //获取data信息
        JSONObject dataObject=returnObject.getJSONObject("data");
        responseObject = JSONUtil.parseJSON2Obj(returnString, MmResponse.class);

        //返回数据可能为空
        if (dataObject != null){
            MmResponseData mmResponseData=JSONUtil.parseJSON2Obj(dataObject.toJSONString(),MmResponseData.class);
            responseObject.setData(mmResponseData);
        }
        return responseObject;
    }

    /**
     * 解析返回的http请求
     *
     * @param httpResponse
     * @return
     */
    private String analysisResponse(HttpResponse httpResponse) {
        if (httpResponse == null) {
            return null;
        }
        String returnString = "";
        int statuscode = httpResponse.getStatusLine().getStatusCode();
        if (statuscode == HttpStatus.SC_NOT_FOUND) {
            throw new RuntimeException("请检查URL!");
        } else if (statuscode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            throw new RuntimeException("服务器内部错误!");
        } else if (statuscode == HttpStatus.SC_BAD_REQUEST) {
            throw new RuntimeException("错误的请求!");
        } else {
            InputStream resultStream = null;
            BufferedReader br = null;
            StringBuffer buffer = new StringBuffer();
            HttpEntity responseEntity = httpResponse.getEntity();
            try {
                resultStream = responseEntity.getContent();
                br = new BufferedReader(new InputStreamReader(resultStream));
                String tempStr;
                while ((tempStr = br.readLine()) != null) {
                    buffer.append(tempStr);
                }
                returnString = buffer.toString();
                returnString = StringEscapeUtils.unescapeJava(returnString);
            } catch (Exception e) {
                throw new RuntimeException("解析返回结果失败!");
            }
            return returnString;
        }
    }
}
