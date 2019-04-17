package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.mc.service.RunQueueService;
import com.hex.goframe.util.WebApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异步联机查询的服务
 */
public class OlqAsyncService implements Runnable {

    private static Logger logger = LoggerFactory.getLogger (OlqAsyncService.class);

    private OlqSyncService olqSyncService;
    private RunQueueService runQueueService;
    private ConsumeRequest consumeRequest;
    private String fileName;
    private long bef;

    public OlqAsyncService(ConsumeRequest consumeRequest, String fileName, long bef) {
        this.olqSyncService = (OlqSyncService) WebApplicationContextUtil.getBean ("olqSyncService");
        this.runQueueService = (RunQueueService) WebApplicationContextUtil.getBean ("runQueueService");
        this.consumeRequest = consumeRequest;
        this.fileName = fileName;
        this.bef = bef;
    }

    @Override
    public void run() {
        try {
            olqSyncService.asyncStartForTimeout (consumeRequest, fileName, bef);
        } finally {
            runQueueService.reduceCurrent (consumeRequest.getMcCurrent ());
        }
    }
}
