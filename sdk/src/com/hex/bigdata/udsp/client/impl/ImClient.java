package com.hex.bigdata.udsp.client.impl;

import com.hex.bigdata.udsp.client.ConsumerClient;
import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.api.model.BaseRequest;
import com.hex.bigdata.udsp.model.request.ImRequest;
import com.hex.bigdata.udsp.model.response.SyncResponse;
import com.hex.bigdata.udsp.model.response.SyncPackResponse;
import com.hex.bigdata.udsp.util.SdkHttpUtil;

import java.util.Map;

/**
 * 交互建模客户端
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
        this.checkBasicParams(request, ConsumerType.SYNC, ConsumerEntity.START);
        this.checkStartBusinessParams(request);
        SyncResponse response = SdkHttpUtil.requestUdsp(request, this.getRequestUrl(), SyncResponse.class);
        return this.transSyncPackResponse(response);
    }

    /**
     * 检查start业务参数
     */
    protected void checkStartBusinessParams(BaseRequest baseRequest) {
//        ImRequest request = (ImRequest) baseRequest;
//        Map<String, String> data = request.getData();
//        if (data == null || data.size() == 0) {
//            throw new IllegalArgumentException("参数data不能为空!");
//        }
    }

}
