package com.hex.bigdata.udsp.consumer.thread;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.OlqSyncService;
import com.hex.goframe.util.WebApplicationContextUtil;

import java.util.concurrent.Callable;

/**
 * 联机查询同步服务的Callable异步类
 *
 * @param <T>
 */
public class OlqSyncServiceCallable<T> implements Callable<Response> {

    private OlqSyncService olqSyncService;
    private String consumeId;
    private String appId;
    private String sql;
    private Page page;

    public OlqSyncServiceCallable(String consumeId, String appId, String sql, Page page) {
        this.olqSyncService = (OlqSyncService) WebApplicationContextUtil.getBean("olqSyncService");
        this.consumeId = consumeId;
        this.appId = appId;
        this.sql = sql;
        this.page = page;
    }

    @Override
    public Response call() throws Exception {
        return olqSyncService.syncStart(this.consumeId, this.appId, this.sql, this.page);
    }
}
