package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.goframe.util.WebApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 异步交互查询的服务
 */
public class IqAsyncService implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(IqAsyncService.class);

    private IqSyncService iqSyncService;
    private WaitingService waitingService;
    private ConsumeRequest consumeRequest;
    private Map<String, String> paraMap;
    private String appId;
    private Page page;
    private String fileName;
    private long bef;


    public IqAsyncService(ConsumeRequest consumeRequest, String appId, Map<String, String> paraMap, Page page, String fileName, long bef) {
        this.iqSyncService = (IqSyncService) WebApplicationContextUtil.getBean("iqSyncService");
        this.waitingService = (WaitingService) WebApplicationContextUtil.getBean("waitingService");
        this.consumeRequest = consumeRequest;
        this.appId = appId;
        this.paraMap = paraMap;
        this.page = page;
        this.fileName = fileName;
        this.bef = bef;
    }

    @Override
    public void run() {
        try {
            logger.debug("IQ START 线程调用开始");
            // 没有进入等待队列或从等待队列中出来，则进入执行队列中执行任务
            iqSyncService.asyncStartForTimeout(consumeRequest, bef, appId, paraMap, page, fileName);
            logger.debug(" IQ START 线程调用结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
