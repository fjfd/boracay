package com.hex.bigdata.udsp.client.impl;

import com.hex.bigdata.udsp.client.ConsumerClient;
import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.api.model.BaseRequest;
import com.hex.bigdata.udsp.model.request.OlqAppRequest;
import com.hex.bigdata.udsp.model.request.StatusRequest;
import com.hex.bigdata.udsp.model.response.AsyncResponse;
import com.hex.bigdata.udsp.model.response.StatusResponse;
import com.hex.bigdata.udsp.model.response.SyncResponse;
import com.hex.bigdata.udsp.model.response.AsyncPackResponse;
import com.hex.bigdata.udsp.model.response.StatusPackResponse;
import com.hex.bigdata.udsp.model.response.SyncPackResponse;
import com.hex.bigdata.udsp.util.SdkHttpUtil;

import java.util.Map;

/**
 * 联机查询应用客户端
 */
@Deprecated
public class OlqAppClient extends ConsumerClient {

    private OlqAppClient() {
    }

    private OlqAppClient(String requestUrl) {
        super(requestUrl);
    }

    /**
     * 联机查询应用客户端-同步start
     *
     * @param request
     * @return
     */
    public SyncPackResponse syncStart(OlqAppRequest request) {
        this.checkBasicParams(request, ConsumerType.SYNC, ConsumerEntity.START);
        this.checkStartBusinessParams(request);
        SyncResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), SyncResponse.class);
        return this.transSyncPackResponse(response);
    }

    /**
     * 联机查询应用客户端-异步start
     *
     * @param request
     * @return
     */
    public AsyncPackResponse asyncStart(OlqAppRequest request) {
        this.checkBasicParams(request, ConsumerType.ASYNC, ConsumerEntity.START);
        this.checkStartBusinessParams(request);
        AsyncResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), AsyncResponse.class);
        return this.transAsyncPackResponse(response);
    }

    /**
     * 联机查询应用客户端-异步status
     *
     * @param request
     * @return
     */
    public StatusPackResponse asyncStatus(StatusRequest request) {
        this.checkBasicParams(request, ConsumerType.ASYNC, ConsumerEntity.STATUS);
        this.checkStatusBusinessParams(request.getConsumeId());
        StatusResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), StatusResponse.class);
        return this.transStatusResponse(response);
    }

    /**
     * 联机查询应用客户端-业务参数检查
     *
     * @param baseRequest
     */
    @Override
    protected void checkStartBusinessParams(BaseRequest baseRequest) {
//        OlqAppRequest request = (OlqAppRequest) baseRequest;
//        Map<String, String> data = request.getData();
//        if (data == null || data.size() == 0) {
//            throw new IllegalArgumentException("参数data不能为空!");
//        }
    }
}
