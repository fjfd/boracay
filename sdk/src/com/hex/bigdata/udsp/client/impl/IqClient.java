package com.hex.bigdata.udsp.client.impl;

import com.hex.bigdata.udsp.client.ConsumerClient;
import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.api.model.BaseRequest;
import com.hex.bigdata.udsp.model.request.IqRequest;
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
 * 交互查询客户端
 */
@Deprecated
public class IqClient extends ConsumerClient {

    private IqClient() {
    }

    private IqClient(String requestUrl) {
        super(requestUrl);
    }

    /**
     * 交互查询-同步start
     *
     * @param request
     * @return
     */
    public SyncPackResponse syncStart(IqRequest request) {
        this.checkBasicParams(request, ConsumerType.SYNC, ConsumerEntity.START);
        this.checkStartBusinessParams(request);
        SyncResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), SyncResponse.class);
        return this.transSyncPackResponse(response);
    }

    /**
     * 交互查询-异步start
     *
     * @param request
     * @return
     */
    public AsyncPackResponse asyncStart(IqRequest request) {
        this.checkBasicParams(request, ConsumerType.ASYNC, ConsumerEntity.START);
        this.checkStartBusinessParams(request);
        AsyncResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), AsyncResponse.class);
        return this.transAsyncPackResponse(response);
    }

    /**
     * 交互查询-异步status
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
     * 检查start业务参数
     */
    @Override
    protected void checkStartBusinessParams(BaseRequest baseRequest) {
//        IqRequest request = (IqRequest) baseRequest;
//        Map<String, String> data = request.getData();
//        if (data == null || data.size() == 0) {
//            throw new IllegalArgumentException("参数data不能为空!");
//        }
    }

}
