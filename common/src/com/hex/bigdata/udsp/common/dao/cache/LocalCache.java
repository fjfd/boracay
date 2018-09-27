package com.hex.bigdata.udsp.common.dao.cache;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.hex.bigdata.udsp.common.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by junjiem on 2017-2-16.
 */
@Repository("localCache")
public class LocalCache<T> implements Cache<T> {

    @Value("${local.cache.maximum.size:100000}")
    private long maximumSize;

    @Value("${local.cache.expire.after.access.time.ms:3600000}")
    private long expireAfterAccessTimeMs;

    private static com.google.common.cache.Cache<Object, Object> cache;

    private static Map<Long, com.google.common.cache.Cache<Object, Object>> cacheMap = new ConcurrentHashMap<>();

    // 读锁与读锁不互斥，读锁与写锁互斥，写锁与写锁互斥。
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.writeLock();

    private com.google.common.cache.Cache<Object, Object> getCache() {
        if (cache == null) {
            cache = CacheBuilder.newBuilder()//
                    .maximumSize(maximumSize)//
                    .expireAfterAccess(expireAfterAccessTimeMs, TimeUnit.MILLISECONDS)//
                    .ticker(Ticker.systemTicker())//
                    .build();
        }
        return cache;
    }

    private boolean insert(String key, Object obj) {
        w.lock();
        try {
            getCache().put(key, obj);
        } finally {
            w.unlock();
        }
        return true;
    }

    private boolean update(String key, Object obj) {
        w.lock();
        try {
            getCache().invalidate(key);
            getCache().put(key, obj);
        } finally {
            w.unlock();
        }
        return true;
    }

    private boolean delete(String key) {
        w.lock();
        try {
            getCache().invalidate(key);
        } finally {
            w.unlock();
        }
        return true;
    }

    private Object select(String key) {
        r.lock();
        try {
            return getCache().getIfPresent(key);
        } finally {
            r.unlock();
        }
    }

    @Override
    public boolean insertCache(String key, T t) {
        return insert(key, ObjectUtil.cloneObj(t));
    }

    @Override
    public boolean updateCache(String key, T t) {
        return update(key, ObjectUtil.cloneObj(t));
    }

    @Override
    public boolean deleteCache(String key) {
        return delete(key);
    }

    @Override
    public T selectCache(String key) {
        Object obj = select(key);
        if (obj == null && cacheMap != null) {
            r.lock();
            try {
                for (Map.Entry<Long, com.google.common.cache.Cache<Object, Object>> entry : cacheMap.entrySet()) {
                    obj = entry.getValue().getIfPresent(key);
                    if (obj != null) {
                        break;
                    }
                }
            } finally {
                r.unlock();
            }
        }
        return ObjectUtil.cloneObj((T) obj);
    }

    @Override
    public boolean insertListCache(String key, List<T> list) {
        return insert(key, ObjectUtil.cloneList(list));
    }

    @Override
    public boolean updateListCache(String key, List<T> list) {
        return update(key, ObjectUtil.cloneList(list));
    }

    @Override
    public boolean deleteListCache(String key) {
        return delete(key);
    }

    @Override
    public List<T> selectListCache(String key) {
        Object obj = select(key);
        return ObjectUtil.cloneList((List<T>) obj);
    }

    @Override
    @Deprecated
    public <T> List<T> selectCacheLike(String likeKey) {
        return null;
    }

    @Override
    @Deprecated
    public boolean deleteCacheLike(String likeKey) {
        return false;
    }

    @Override
    public boolean insertTimeoutCache(String key, T t, long timeout) {
        com.google.common.cache.Cache<Object, Object> cache = cacheMap.get(timeout);
        if (cache == null) {
            cache = CacheBuilder.newBuilder() //
                    .maximumSize(maximumSize) //
                    //.expireAfterWrite(timeout, TimeUnit.MILLISECONDS) // 当缓存项在指定的时间段内没有更新就会被回收
                    //.expireAfterAccess(timeout, TimeUnit.MICROSECONDS) // 当缓存项在指定的时间段内没有被读或写就会被回收
                    .refreshAfterWrite(timeout, TimeUnit.MICROSECONDS) // 当缓存项上一次更新操作之后的多久会被刷新
                    .ticker(Ticker.systemTicker()) //
                    .build();
            cacheMap.put(timeout, cache);
        }
        w.lock();
        try {
            cache.put(key, ObjectUtil.cloneObj(t));
        } finally {
            w.unlock();
        }
        return true;
    }
}
