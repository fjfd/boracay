package com.hex.bigdata.udsp.mc.service;

import com.hex.bigdata.udsp.common.lock.RedisDistributedLock;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.consumer.model.QueueIsFullResult;
import com.hex.bigdata.udsp.mc.dao.WaitQueueMapper;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.model.WaitQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 等待队列信息的服务
 */
@Service
public class WaitQueueService {

    private static Logger logger = LoggerFactory.getLogger(WaitQueueService.class);

    /**
     * 等待队列信息的KEY
     */
    private static final String MC_WAITQUEUE_KEY = "WAITQUEUE";

    @Autowired
    private WaitQueueMapper waitQueueMapper;

    @Autowired
    private CurrentService currentService;

    @Autowired
    private RunQueueService runQueueService;

    /**
     * Redis 分布式锁
     */
    @Autowired
    private RedisDistributedLock redisLock;

    @Autowired
    private InitParamService initParamService;

    public WaitQueue select(String key) {
        return waitQueueMapper.select(key);
    }

    public boolean insert(String key, WaitQueue waitQueue) {
        return waitQueueMapper.insert(key, waitQueue);
    }

    public boolean delete(String key) {
        return waitQueueMapper.delete(key);
    }

    public boolean deleteLike(String key) {
        return waitQueueMapper.deleteLike(key);
    }

    /**
     * 清空等待队列
     *
     * @return
     */
    public boolean emptyCache() {
        return this.deleteLike(MC_WAITQUEUE_KEY + ":")
                && currentService.emptyCacheWait();
    }

    /**
     * 清空本机等待队列
     *
     * @return
     */
    public boolean emptyLocalCache() {
        for (Current current : currentService.getLocalWait()) {
            String key = this.getKey(current);
            WaitQueue waitQueue = this.select(key);
            if (waitQueue != null && waitQueue.getCurrentNum() >= 1) {
                waitQueue.setCurrentNum(waitQueue.getCurrentNum() - 1);
                if (waitQueue.getCurrentNum() == 0) {
                    this.delete(key);
                } else {
                    this.insert(key, waitQueue);
                }
                currentService.deleteWait(current.getPkId());
            }
        }
        return true;
    }

    /**
     * 检查等待队列是否满了，未满则添加到等待队列。满了则不做操作，
     * 无论是否满都记录是否满状态和任务ID
     *
     * @param mcCurrent
     * @param maxWaitNum
     * @return
     */
    public QueueIsFullResult checkWaitQueueIsFull(Current mcCurrent, int maxWaitNum) {
        String key = this.getKey(mcCurrent);
        QueueIsFullResult isFullResult = new QueueIsFullResult();
        isFullResult.setWaitQueueIsFull(true);
        synchronized (key.intern()) {
            if (initParamService.isUseClusterRedisLock())
                redisLock.lock(key);
            try {
                WaitQueue waitQueue = this.select(key);
                if (waitQueue == null) waitQueue = initWaitQueue(mcCurrent);
                // 若是等待队列未满，则请求进入
                if (waitQueue.getCurrentNum() < waitQueue.getMaxNum()) {
                    logger.debug(key + "等待队列最大长度：" + maxWaitNum + "，等待队列长度："
                            + waitQueue.getCurrentNum() + "，" + Thread.currentThread().getName() + "进入等待队列！");
                    // 本次请求加入队列
                    String pkId = mcCurrent.getPkId();
                    isFullResult.setWaitQueueTaskId(pkId);
                    isFullResult.setWaitQueueIsFull(false);
                    waitQueue.offerElement(pkId); // 加入队尾
                    // 增加等待队列的大小
                    waitQueue.setCurrentNum(waitQueue.getCurrentNum() + 1);
                    this.insert(key, waitQueue);
                    // 添加一条等待的并发信息
                    currentService.insertWait(mcCurrent);
                }
                return isFullResult;
            } finally {
                if (initParamService.isUseClusterRedisLock())
                    redisLock.unlock(key);
            }
        }
    }

    /**
     * 检查waitQueueTaskId是否是等待队列中第一个（排在队列第一个的才允许先出去），
     * 如果是则从等待队列中移除并返回true,如果不是的返回false
     *
     * @param mcCurrent
     * @param waitQueueTaskId
     * @return
     */
    public boolean checkWaitQueueIsFirst(Current mcCurrent, String waitQueueTaskId) {
        String key = this.getKey(mcCurrent);
        synchronized (key.intern()) {
            if (initParamService.isUseClusterRedisLock())
                redisLock.lock(key);
            try {
                WaitQueue waitQueue = this.select(key);
                // 判断key是不是队列第一个，如果是第一个则移除并返回true，如果不是第一个则返回false
                if (waitQueue.isFirstElement(waitQueueTaskId)) {
                    logger.debug("将" + key + "任务从等待队列中移除：" + Thread.currentThread().getName());
                    // 减少等待队列的大小
                    waitQueue.setCurrentNum(waitQueue.getCurrentNum() - 1);
                    if (waitQueue.getCurrentNum() == 0) {
                        this.delete(key);
                    } else {
                        this.insert(key, waitQueue);
                    }
                    // 删除一条等待的并发信息
                    currentService.deleteWait(waitQueueTaskId);
                    // 增加运行的并发信息和队列大小
                    runQueueService.addCurrent(mcCurrent);
                    return true;
                }
                return false;
            } finally {
                if (initParamService.isUseClusterRedisLock())
                    redisLock.unlock(key);
            }
        }
    }

    private WaitQueue initWaitQueue(Current current) {
        WaitQueue waitQueue = new WaitQueue();
        waitQueue.setQueueName(getKey(current));
        waitQueue.setMaxNum(current.getMaxCurrentNum());
        return waitQueue;
    }

    private String getKey(Current mcCurrent) {
        return MC_WAITQUEUE_KEY + ":" + mcCurrent.getUserName() + ":" + mcCurrent.getAppId()
                + ":" + mcCurrent.getAppType().toUpperCase() + ":" + mcCurrent.getSyncType().toUpperCase();
    }

}
