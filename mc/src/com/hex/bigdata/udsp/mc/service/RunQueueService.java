package com.hex.bigdata.udsp.mc.service;

import com.hex.bigdata.udsp.common.lock.RedisDistributedLock;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.mc.dao.RunQueueMapper;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.model.RunQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 运行队列信息的服务
 */
@Service
public class RunQueueService {
    private static Logger logger = LoggerFactory.getLogger(RunQueueService.class);

    /**
     * 运行队列信息的KEY
     */
    private static final String MC_RUNQUEUE_KEY = "RUNQUEUE";

    @Autowired
    private RunQueueMapper runQueueMapper;

    @Autowired
    private CurrentService mcCurrentService;

    @Autowired
    private InitParamService initParamService;

    /**
     * Redis 分布式锁
     */
    @Autowired
    private RedisDistributedLock redisLock;

    public boolean update(String key, RunQueue runQueue) {
        return this.runQueueMapper.update(key, runQueue);
    }

    public boolean insert(String key, RunQueue runQueue) {
        return this.runQueueMapper.insert(key, runQueue);
    }

    public RunQueue select(String key) {
        return runQueueMapper.select(key);
    }

    public boolean delete(String key) {
        return runQueueMapper.delete(key);
    }

    public boolean removeCacheLike(String key) {
        return runQueueMapper.removeCacheLike(key);
    }

    /**
     * 清空运行队列
     *
     * @return
     */
    public boolean emptyCache() {
        return this.removeCacheLike(MC_RUNQUEUE_KEY + ":");
    }

    /**
     * 增加并发
     *
     * @param mcCurrent
     * @return
     */
    public boolean addCurrent(Current mcCurrent) {
        String key = this.getKey(mcCurrent);
        synchronized (key.intern()) {
            if (initParamService.isUseClusterRedisLock())
                redisLock.lock(key);
            try {
                RunQueue runQueue = this.select(key);
                if (runQueue == null) runQueue = this.initRunQueue(mcCurrent);
                if (runQueue.getCurrentNum() < runQueue.getMaxNum()) {
                    runQueue.setCurrentNum(runQueue.getCurrentNum() + 1);
                    logger.debug("增加" + key + "并发后并发数==>" + runQueue.getCurrentNum());
                    this.insert(key, runQueue);
                    logger.debug("增加" + key + "并发！");
                    mcCurrentService.insert(mcCurrent);
                    return true;
                }
                logger.info(key + "并发已满！");
                return false;
            } finally {
                if (initParamService.isUseClusterRedisLock())
                    redisLock.unlock(key);
            }
        }
    }

    /**
     * 减少并发
     *
     * @param mcCurrent
     * @return
     */
    public boolean reduceCurrent(Current mcCurrent) {
        String key = this.getKey(mcCurrent);
        synchronized (key.intern()) {
            if (initParamService.isUseClusterRedisLock())
                redisLock.lock(key);
            try {
                RunQueue runQueue = this.select(key);
                if (runQueue != null && runQueue.getCurrentNum() >= 1) {
                    runQueue.setCurrentNum(runQueue.getCurrentNum() - 1);
                    logger.debug("减少" + key + "并发后并发数==>" + runQueue.getCurrentNum());
                    if (runQueue.getCurrentNum() == 0) {
                        this.delete(key);
                    } else {
                        this.insert(key, runQueue);
                    }
                    logger.debug("减少" + key + "并发！");
                    this.mcCurrentService.delete(mcCurrent.getPkId());
                    return true;
                }
                return false;
            } finally {
                if (initParamService.isUseClusterRedisLock())
                    redisLock.unlock(key);
            }
        }
    }

    private RunQueue initRunQueue(Current mcCurrent) {
        logger.debug("初始化并发数控制实体！");
        RunQueue runQueue = new RunQueue();
        runQueue.setUserId(mcCurrent.getUserName());
        runQueue.setAppId(mcCurrent.getAppId());
        runQueue.setAppType(mcCurrent.getAppType().toUpperCase());
        runQueue.setSyncType(mcCurrent.getSyncType().toUpperCase());
        runQueue.setMaxNum(mcCurrent.getMaxCurrentNum());
        return runQueue;
    }

    /**
     * 检查执行队列是否满
     *
     * @param mcCurrent 不为空
     * @return false:未满，true：满
     */
    public boolean runQueueFull(Current mcCurrent) {
        String key = this.getKey(mcCurrent);
        synchronized (key.intern()) {
            if (initParamService.isUseClusterRedisLock())
                redisLock.lock(key);
            try {
                RunQueue runQueue = this.select(key);
                return runQueue != null && runQueue.getCurrentNum() >= runQueue.getMaxNum();
            } finally {
                if (initParamService.isUseClusterRedisLock())
                    redisLock.unlock(key);
            }
        }
    }

    private String getKey(Current mcCurrent) {
        return MC_RUNQUEUE_KEY + ":" + mcCurrent.getUserName() + ":" + mcCurrent.getAppId()
                + ":" + mcCurrent.getAppType().toUpperCase() + ":" + mcCurrent.getSyncType().toUpperCase();
    }

}
