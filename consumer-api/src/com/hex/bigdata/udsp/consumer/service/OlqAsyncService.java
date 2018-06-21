package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.goframe.util.WebApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步联机查询的服务
 */
public class OlqAsyncService implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(IqAsyncService.class);

    private OlqSyncService olqSyncService;
    private WaitingService waitingService;
    private ConsumeRequest consumeRequest;
    private String appId;
    private String sql;
    private Page page;
    private String fileName;
    private long bef;

    public OlqAsyncService(ConsumeRequest consumeRequest, String appId, String sql, Page page, String fileName, long bef) {
        this.olqSyncService = (OlqSyncService) WebApplicationContextUtil.getBean("olqSyncService");
        this.waitingService = (WaitingService) WebApplicationContextUtil.getBean("waitingService");
        this.consumeRequest = consumeRequest;
        this.appId = appId;
        this.sql = sql;
        this.page = page;
        this.fileName = fileName;
        this.bef = bef;
    }

    @Override
    public void run() {
        try {
            logger.debug("OLQ START 线程调用开始");
            // 没有进入等待队列或从等待队列中出来，则进入执行队列中执行任务
            olqSyncService.asyncStartForTimeout(consumeRequest, bef, appId, sql, page, fileName);
            logger.debug(" OLQ START 线程调用结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
