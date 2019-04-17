package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.lock.RedisDistributedLock;
import com.hex.bigdata.udsp.common.model.QueueIsFullResult;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.service.RunQueueService;
import com.hex.bigdata.udsp.mc.service.WaitQueueService;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 等待队列服务
 */
@Service
public class WaitingService {
    private static Logger logger = LoggerFactory.getLogger (WaitingService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool (new ThreadFactory () {
        private AtomicInteger id = new AtomicInteger (0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread (r);
            thread.setName ("wait-queue-service-" + id.addAndGet (1));
            return thread;
        }
    });

    /**
     * 同步循环时间间隔，单位毫秒
     */
    @Value("${sync.cycle.time.interval:10}")
    private long syncCycleTimeInterval;

    /**
     * 异步循环时间间隔，单位毫秒
     */
    @Value("${async.cycle.time.interval:100}")
    private long asyncCycleTimeInterval;

    @Autowired
    private LoggingService loggingService;
    @Autowired
    private InitParamService initParamService;
    @Autowired
    private RunQueueService runQueueService;
    @Autowired
    private WaitQueueService waitQueueService;
    @Autowired
    private RedisDistributedLock redisDistributedLock;

    /**
     * 判断是否进入等待队列，如果进入则等待其满足条件出来
     *
     * @param consumeRequest
     * @param bef
     * @return
     */
    public boolean isWaiting(ConsumeRequest consumeRequest, long bef) {
        final Current mcCurrent = consumeRequest.getMcCurrent ();
        QueueIsFullResult isFullResult = consumeRequest.getQueueIsFullResult ();
        long maxWaitTimeout = 0;
        RcUserService rcUserService = consumeRequest.getRcUserService ();
        if (ConsumerType.SYNC.getValue ().equalsIgnoreCase (mcCurrent.getSyncType ())) {
            maxWaitTimeout = (rcUserService == null || rcUserService.getMaxSyncWaitTimeout () == 0) ?
                    initParamService.getMaxSyncWaitTimeout () : rcUserService.getMaxSyncWaitTimeout ();
        } else {
            maxWaitTimeout = (rcUserService == null || rcUserService.getMaxAsyncWaitTimeout () == 0) ?
                    initParamService.getMaxAsyncWaitTimeout () : rcUserService.getMaxAsyncWaitTimeout ();
        }
        boolean passFlg = true;
        if (isFullResult != null && !isFullResult.isWaitQueueIsFull ()) { // 任务进入等待队列
            final String waitQueueTaskId = isFullResult.getWaitQueueTaskId ();
            passFlg = false;
            try {
                // 开启一个新的线程，其内部循环判断是否可以执行，可以执行时或者等待超时时向下走
                Future<Boolean> futureTask = executorService.submit (new Callable<Boolean> () {
                    @Override
                    public Boolean call() throws Exception {
                        String key = mcCurrent.getUserName () + ":" + mcCurrent.getAppId ()
                                + ":" + mcCurrent.getAppType ().toUpperCase () + ":" + mcCurrent.getSyncType ().toUpperCase ();
                        long cycleTimeInterval = 0;
                        if (ConsumerType.SYNC.getValue ().equalsIgnoreCase (mcCurrent.getSyncType ())) {
                            cycleTimeInterval = syncCycleTimeInterval;
                        } else {
                            cycleTimeInterval = asyncCycleTimeInterval;
                        }
                        while (true) {
                            synchronized (key.intern ()) {
                                if (initParamService.isUseClusterRedisLock ()) {
                                    redisDistributedLock.lock (key);
                                }
                                try {
                                    // 检查执行队列是否满
                                    if (runQueueService.runQueueFull (mcCurrent)) continue; // 已满则继续循环
                                    // 检查任务是否是等待队列中的第一个
                                    if (waitQueueService.checkWaitQueueIsFirst (mcCurrent, waitQueueTaskId)) {
                                        return true;
                                    }
                                } finally {
                                    if (initParamService.isUseClusterRedisLock ()) {
                                        redisDistributedLock.unlock (key);
                                    }
                                }
                            }
                            Thread.sleep (cycleTimeInterval);
                        }
                    }
                });
                passFlg = futureTask.get (maxWaitTimeout, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                loggingService.writeResponseLog (null, consumeRequest, bef, 0, ErrorCode.ERROR_000014, e.toString ());
            } catch (Exception e) {
                e.printStackTrace ();
                loggingService.writeResponseLog (null, consumeRequest, bef, 0, ErrorCode.ERROR_000007, e.toString ());
            }
        }
        return passFlg;
    }
}
