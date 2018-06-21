package com.hex.bigdata.udsp.consumer.thread;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.IqSyncService;
import com.hex.goframe.util.WebApplicationContextUtil;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 联机查询同步服务的Callable异步类
 *
 * @param <T>
 */
public class IqSyncServiceCallable<T> implements Callable<Response> {

    private IqSyncService iqSyncService;
    private Map<String, String> data;
    private String appId;
    private Page page;

    public IqSyncServiceCallable(Map<String, String> data, String appId, Page page) {
        this.iqSyncService = (IqSyncService) WebApplicationContextUtil.getBean("iqSyncService");
        this.data = data;
        this.appId = appId;
        this.page = page;
    }

    @Override
    public Response call() throws Exception {
        return iqSyncService.syncStart(appId, data, page);
    }

}
