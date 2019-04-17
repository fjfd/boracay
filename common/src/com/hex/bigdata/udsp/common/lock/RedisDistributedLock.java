package com.hex.bigdata.udsp.common.lock;

import com.hex.bigdata.udsp.common.constant.RedisMode;
import com.hex.bigdata.udsp.common.util.ExceptionUtil;
import com.hex.goframe.util.WebApplicationContextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁
 */
@Repository
public class RedisDistributedLock {
    private static Logger logger = LogManager.getLogger(RedisDistributedLock.class);

    @Value("${redis.mode:single}")
    private String redisMode;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplate redisClusterTemplate;

    private RedisTemplate getRedisTemplate() {
        if (RedisMode.CLUSTER.getValue ().equalsIgnoreCase (redisMode)) {
            return redisClusterTemplate;
        }
        return redisTemplate;
    }

    /**
     * 存储到redis中的标志
     */
    private static final String LOCKED = "LOCKED";

    /**
     * 检查锁是否解锁的休眠时间（ms）
     */
    private static final long SLEEP = 1;

    /**
     * 锁的有效时间(ms)，为了避免死锁问题发生
     */
    public static final int EXPIRE = 1000;

    public final static String LOCK_KEY_PREFIX = "lock:";

    /**
     * 上锁
     *
     * @param key
     */
    public void lock(String key) {
        logger.debug(key + "准备上锁！");
        String lockKey = LOCK_KEY_PREFIX + key;
        RedisConnection redisConnection = getRedisTemplate().getConnectionFactory().getConnection();
        try {
            long count = 0;
            long num = (SLEEP == 0 ? 2000 : EXPIRE / SLEEP);
            while (count < num) { // 为了避免死锁问题发生
                try {
                    logger.debug(key + "请求锁！");
                    /**
                     * TODO
                     * 理论上这里也会出现同时进入的问题，也需要上锁，但是上什么锁呢？
                     * 如果只上单机锁则只能锁本机，对于分布式服务无效；
                     * 而如果上分布式锁，这个方法就是为了解决分布式锁的！
                     * 本人无解。。。。。。。。！
                     */
                    synchronized (key.intern()) {
                        // 请求锁成功说明锁没被其他线程保持
                        if (redisConnection.setNX(lockKey.getBytes(), LOCKED.getBytes())) {
                            logger.debug(key + "上锁！");
                            // 上锁
                            getRedisTemplate().expire(lockKey, EXPIRE, TimeUnit.MILLISECONDS);
                            break;
                        }
                        // 请求锁失败说明锁被其它线程保持，等待几毫秒后继续请求锁
                    }
                } catch (Exception e) {
                    logger.error(ExceptionUtil.getMessage(e) + " key: " + key);
                }
                if (SLEEP > 0) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (Exception e) {
                        logger.error(ExceptionUtil.getMessage(e) + " key: " + key);
                    }
                }
                count++;
            }
            if (count >= num) {
                logger.error("key: " + key + " lock time out");
                unlock(key);
            }
        } finally {
            redisConnection.close();
        }
    }

    /**
     * 解锁
     *
     * @param key
     */
    public void unlock(String key) {
        logger.debug(key + "解锁！");
        String lockKey = LOCK_KEY_PREFIX + key;
        try {
            getRedisTemplate().delete(lockKey);
        } catch (Exception e) {
            logger.error(ExceptionUtil.getMessage(e) + " key: " + key);
        }
    }

}