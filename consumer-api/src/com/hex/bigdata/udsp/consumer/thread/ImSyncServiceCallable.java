package com.hex.bigdata.udsp.consumer.thread;

import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.ImSyncService;
import com.hex.goframe.util.WebApplicationContextUtil;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 交互建模同步服务的Callable异步类
 *
 * @param <T>
 */
public class ImSyncServiceCallable<T> implements Callable<Response> {

    private ImSyncService imSyncService;

    private Map<String, String> data;

    private String appId;

    public ImSyncServiceCallable(String appId, Map<String, String> data) {
        this.appId = appId;
        this.data = data;
        this.imSyncService = (ImSyncService) WebApplicationContextUtil.getBean("imSyncService");
    }

    @Override
    public Response call() throws Exception {
        return imSyncService.start(appId, data);
    }
}
