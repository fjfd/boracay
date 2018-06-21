package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.consumer.constant.ConsumerConstant;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.QueueIsFullResult;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import com.hex.bigdata.udsp.consumer.thread.WaitQueueCallable;
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
    private static Logger logger = LoggerFactory.getLogger(WaitingService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        private AtomicInteger id = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("wait-queue-service-" + id.addAndGet(1));
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

    /**
     * 判断是否进入等待队列，如果进入则等待其满足条件出来
     *
     * @param consumeRequest
     * @param bef
     * @return
     */
    public boolean isWaiting(ConsumeRequest consumeRequest, long bef) {
        Current mcCurrent = consumeRequest.getMcCurrent();
        String consumeId = mcCurrent.getPkId();
        long cycleTimeInterval = syncCycleTimeInterval;
        if (ConsumerConstant.CONSUMER_TYPE_ASYNC.equalsIgnoreCase(mcCurrent.getSyncType())) {
            cycleTimeInterval = asyncCycleTimeInterval;
        }
        QueueIsFullResult isFullResult = consumeRequest.getQueueIsFullResult();
        RcUserService rcUserService = consumeRequest.getRcUserService();
        long maxAsyncWaitTimeout = (rcUserService == null || rcUserService.getMaxAsyncWaitTimeout() == 0) ?
                initParamService.getMaxAsyncWaitTimeout() : rcUserService.getMaxAsyncWaitTimeout();
        boolean passFlg = true;
        if (isFullResult != null && !isFullResult.isWaitQueueIsFull()) { // 任务进入等待队列
            passFlg = false;
            try {
                // 开启一个新的线程，其内部循环判断是否可以执行，可以执行时或者等待超时时向下走
                Future<Boolean> waitFutureTask = executorService.submit(new WaitQueueCallable(mcCurrent, isFullResult.getWaitQueueTaskId(), cycleTimeInterval));
                passFlg = waitFutureTask.get(maxAsyncWaitTimeout, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                loggingService.writeResponseLog(null, consumeRequest, bef, 0,
                        ErrorCode.ERROR_000014.getValue(), ErrorCode.ERROR_000014.getName() + ":" + e.toString(), consumeId);
            } catch (Exception e) {
                e.printStackTrace();
                loggingService.writeResponseLog(null, consumeRequest, bef, 0,
                        ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
            }
        }
        return passFlg;
    }
}
