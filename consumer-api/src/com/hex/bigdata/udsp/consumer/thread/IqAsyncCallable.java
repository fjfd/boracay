package com.hex.bigdata.udsp.consumer.thread;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.iq.provider.model.IqResponse;
import com.hex.bigdata.udsp.consumer.service.IqSyncService;
import com.hex.goframe.util.WebApplicationContextUtil;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 交互查询异步处理的Callable异步类
 *
 * @param <T>
 */
public class IqAsyncCallable<T> implements Callable<IqResponse> {

    private IqSyncService iqSyncService;
    private String userName;
    private String appId;
    private Map<String, String> paraMap;
    private Page page;
    private String fileName;

    public IqAsyncCallable(String userName, String appId, Map<String, String> paraMap, Page page, String fileName) {
        this.iqSyncService = (IqSyncService) WebApplicationContextUtil.getBean("iqSyncService");
        this.userName = userName;
        this.appId = appId;
        this.paraMap = paraMap;
        this.page = page;
        this.fileName = fileName;
    }

    @Override
    public IqResponse call() throws Exception {
        return this.iqSyncService.asyncStart(appId, paraMap, page, fileName, userName);
    }
}
