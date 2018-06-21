package com.hex.bigdata.udsp.consumer.thread;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponse;
import com.hex.bigdata.udsp.consumer.service.OlqSyncService;
import com.hex.goframe.util.WebApplicationContextUtil;

import java.util.concurrent.Callable;

/**
 * 联机查询异步处理的Callable异步类
 *
 * @param <T>
 */
public class OlqAsyncCallable<T> implements Callable<OlqResponse> {

    private String consumeId;
    private String userName;
    private String dsId;
    private String sql;
    private Page page;
    private String fileName;

    private OlqSyncService olqSyncService;

    public OlqAsyncCallable(String consumeId, String userName, String dsId, String sql, Page page, String fileName) {
        this.olqSyncService = (OlqSyncService) WebApplicationContextUtil.getBean("olqSyncService");
        this.consumeId = consumeId;
        this.userName = userName;
        this.dsId = dsId;
        this.sql = sql;
        this.page = page;
        this.fileName = fileName;
    }

    @Override
    public OlqResponse call() throws Exception {
        return this.olqSyncService.asyncStart(this.consumeId, this.dsId, this.sql, this.page, this.fileName, userName);
    }
}
