package com.hex.bigdata.udsp.common.dao.cache;

import com.hex.bigdata.udsp.common.util.ObjectUtil;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junjiem on 2017-2-16.
 */
@Repository("ehCache")
public class EhCache<T> implements Cache<T> {

    private static final String UDSP_EHCACHE_NAME = "udspCache";

    @Autowired
    private CacheManager cacheManager;

    private boolean insert(String key, Object obj) {
        if (StringUtils.isNotBlank(key) && obj != null) {
            net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
            cache.acquireWriteLockOnKey(key); // 读锁与读锁不互斥，读锁与写锁互斥，写锁与写锁互斥。
            try {
                cache.put(new Element(key, obj));
            } finally {
                cache.releaseWriteLockOnKey(key);
            }
        }
        return true;
    }

    private boolean update(String key, Object obj) {
        if (StringUtils.isNotBlank(key) && obj != null) {
            net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
            cache.acquireWriteLockOnKey(key);
            cache.remove(key);
            try {
                cache.put(new Element(key, obj));
            } finally {
                cache.releaseWriteLockOnKey(key);
            }
        }
        return true;
    }

    private boolean delete(String key) {
        net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
        cache.acquireWriteLockOnKey(key);
        try {
            cache.remove(key);
        } finally {
            cache.releaseWriteLockOnKey(key);
        }
        return true;
    }

    private Object select(String key) {
        if (StringUtils.isNotBlank(key)) {
            net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
            cache.acquireReadLockOnKey(key);
            try {
                Element element = cache.get(key);
                if (element == null) {
                    return null;
                }
                return element.getObjectValue();
            } finally {
                cache.releaseReadLockOnKey(key);
            }
        }
        return null;
    }

    public boolean insertCache(String key, T t) {
        return insert(key, cloneObj(t));
    }

    public boolean updateCache(String key, T t) {
        return update(key, cloneObj((T) t));
    }

    public boolean deleteCache(String key) {
        return delete(key);
    }

    @Override
    public T selectCache(String key) {
        Object obj = select(key);
        return cloneObj((T) obj);
    }

    @Override
    public boolean insertListCache(String key, List<T> list) {
        return insert(key, cloneList(list));
    }

    @Override
    public boolean updateListCache(String key, List<T> list) {
        return update(key, cloneList((List<T>) list));
    }

    @Override
    public boolean deleteListCache(String key) {
        return delete(key);
    }

    @Override
    public List<T> selectListCache(String key) {
        Object obj = select(key);
        return cloneList((List<T>) obj);
    }

    @Override
    public List<T> selectCacheLike(String likeKey) {
        net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
        cache.acquireReadLockOnKey(likeKey);
        try {
            Attribute<String> keyObject = cache.getSearchAttribute("key");
            Query query = cache.createQuery();
            query.addCriteria(keyObject.ilike(likeKey + "*"));
            query = query.includeValues();
            query = query.includeKeys();
            Results results = query.execute();
            List<Result> resultList = results.all();
            List<T> list = null;
            if (resultList != null && resultList.size() != 0) {
                list = new ArrayList<>();
                for (Result result : resultList) {
                    list.add((T) result.getValue());
                }
            }
            return cloneList(list);
        } finally {
            cache.releaseReadLockOnKey(likeKey);
        }
    }

    @Override
    public boolean deleteCacheLike(String likeKey) {
        net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
        cache.acquireWriteLockOnKey(likeKey);
        try {
            Attribute<String> keyObject = cache.getSearchAttribute("key");
            Query query = cache.createQuery();
            query.addCriteria(keyObject.ilike(likeKey + "*"));
            query = query.includeKeys();
            Results results = query.execute();
            List<Result> resultList = results.all();
            List<String> keyList = new ArrayList<String>();
            for (Result result : resultList) {
                keyList.add((String) result.getKey());
            }
            cache.removeAll(keyList);
            return true;
        } finally {
            cache.releaseWriteLockOnKey(likeKey);
        }
    }

    @Override
    public boolean insertTimeoutCache(String key, T t, long timeout) {
        Element element = new Element(key, cloneObj(t));
        element.setTimeToIdle((int) timeout / 1000);
        if (StringUtils.isNotBlank(key) && t != null) {
            net.sf.ehcache.Cache cache = cacheManager.getCache(UDSP_EHCACHE_NAME);
            cache.acquireWriteLockOnKey(key);
            try {
                cache.put(element);
            } finally {
                cache.releaseWriteLockOnKey(key);
            }
        }
        return true;
    }

    /**
     * 克隆对象
     *
     * @param obj
     * @return
     */
    private T cloneObj(T obj) {
        T t = null;
        if (obj != null) {
            try {
                if (obj instanceof Integer) {
                    t = (T) new Integer((Integer) obj);
                } else if (obj instanceof Long) {
                    t = (T) new Long((Long) obj);
                } else if (obj instanceof Short) {
                    t = (T) new Short((Short) obj);
                } else if (obj instanceof Double) {
                    t = (T) new Double((Double) obj);
                } else if (obj instanceof Float) {
                    t = (T) new Float((Float) obj);
                } else {
                    t = (T) obj.getClass().newInstance();
                    ObjectUtil.copyObject(obj, t);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     * 克隆集合
     *
     * @param objs
     * @return
     */
    private List<T> cloneList(List<T> objs) {
        List<T> list = null;
        if (objs != null) {
            list = new ArrayList<>();
            T t = null;
            for (T obj : objs) {
                t = cloneObj(obj);
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

}
