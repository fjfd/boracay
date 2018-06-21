package com.hex.bigdata.udsp.consumer.thread;

import com.hex.bigdata.udsp.common.lock.RedisDistributedLock;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.service.RunQueueService;
import com.hex.bigdata.udsp.mc.service.WaitQueueService;
import com.hex.goframe.util.WebApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * 等待队列循环判断是否可执行的Callable异步类
 */
public class WaitQueueCallable<T> implements Callable<Boolean> {

    private static Logger logger = LoggerFactory.getLogger(WaitQueueService.class);

    private Current mcCurrent;

    private String waitQueueTaskId;

    private RunQueueService runQueueService;

    private WaitQueueService waitQueueService;

    private InitParamService initParamService;

    private RedisDistributedLock redisDistributedLock;

    private long sleepTime = 200;

    public WaitQueueCallable(Current mcCurrent, String waitQueueTaskId, long sleepTime) {
        this.runQueueService = (RunQueueService) WebApplicationContextUtil.getBean("runQueueService");
        this.waitQueueService = (WaitQueueService) WebApplicationContextUtil.getBean("waitQueueService");
        this.redisDistributedLock = (RedisDistributedLock) WebApplicationContextUtil.getBean("redisDistributedLock");
        this.initParamService = (InitParamService) WebApplicationContextUtil.getBean("initParamService");
        this.mcCurrent = mcCurrent;
        this.waitQueueTaskId = waitQueueTaskId;
        this.sleepTime = sleepTime;
    }

    public Current getMcCurrent() {
        return mcCurrent;
    }

    public void setMcCurrent(Current mcCurrent) {
        this.mcCurrent = mcCurrent;
    }

    public RunQueueService getRunQueueService() {
        return runQueueService;
    }

    public void setRunQueueService(RunQueueService runQueueService) {
        this.runQueueService = runQueueService;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public Boolean call() throws Exception {
        String key = this.getKey(mcCurrent);
        while (true) {
            synchronized (key.intern()) {
                if (initParamService.isUseClusterRedisLock())
                    redisDistributedLock.lock(key);
                try {
                    // 检查执行队列是否满
                    if (runQueueService.runQueueFull(mcCurrent)) continue; // 已满则继续循环
                    // 检查任务是否是等待队列中的第一个
                    if (waitQueueService.checkWaitQueueIsFirst(mcCurrent, waitQueueTaskId)) {
                        return true;
                    }
                } finally {
                    if (initParamService.isUseClusterRedisLock())
                        redisDistributedLock.unlock(key);
                }
            }
            Thread.sleep(sleepTime);
        }
    }

    private String getKey(Current mcCurrent) {
        return mcCurrent.getUserName() + ":" + mcCurrent.getAppId()
                + ":" + mcCurrent.getAppType().toUpperCase() + ":" + mcCurrent.getSyncType().toUpperCase();
    }
}
