package com.hex.bigdata.udsp.client.impl;

import com.hex.bigdata.udsp.client.ConsumerClient;
import com.hex.bigdata.udsp.constant.SdkConstant;
import com.hex.bigdata.udsp.model.request.ImRequest;
import com.hex.bigdata.udsp.model.request.UdspRequest;
import com.hex.bigdata.udsp.model.response.origin.SyncResponse;
import com.hex.bigdata.udsp.model.response.pack.SyncPackResponse;
import com.hex.bigdata.udsp.util.SdkHttpUtil;

/**
 * 交互查询客户端
 */
public class ImClient extends ConsumerClient {

    private ImClient() {
    }

    private ImClient(String requestUrl) {
        super(requestUrl);
    }

    /**
     * 交互查询-同步start
     *
     * @param request
     * @return
     */
    public SyncPackResponse syncStart(ImRequest request) {
        //检查基础参数，参数错误则抛出异常
        this.checkBasicParams(request, SdkConstant.CONSUMER_TYPE_SYNC, SdkConstant.CONSUMER_ENTITY_START);
        //检查业务参数
        this.checkStartBusinessParams(request);
        SyncResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), SyncResponse.class);
        return this.transSyncPackResponse(response);
    }

    /**
     * 检查start业务参数
     */
    protected void checkStartBusinessParams(UdspRequest udspRequest) {
    }

}
