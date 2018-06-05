package com.hex.bigdata.udsp.common.dao.cache;

import java.util.List;

/**
 * Created by junjiem on 2017-2-16.
 */
public interface Cache<T> {

    /**
     * 根据key插入对应的对象
     *
     * @param key
     * @return
     */
    boolean insertCache(String key, T t);

    /**
     * 根据key更新对应的对象
     *
     * @param key
     * @return
     */
    boolean updateCache(String key, T t);

    /**
     * 根据key查询对应的对象
     *
     * @param key
     * @return
     */
    boolean deleteCache(String key);

    /**
     * 根据key查询对应的对象
     *
     * @param key
     * @return
     */
    T selectCache(String key);

    /**
     * 根据key插入对应的list对象
     *
     * @param key
     * @param list
     * @return
     */
    boolean insertListCache(String key, List<T> list);

    /**
     * 根据key更新对应的list对象
     *
     * @param key
     * @param list
     * @return
     */
    boolean updateListCache(String key, List<T> list);

    /**
     * 根据key删除对应的list对象
     *
     * @param key
     * @return
     */
    boolean deleteListCache(String key);

    /**
     * 根据key查询出对应的list对象
     *
     * @param key
     * @return
     */
    <T> List<T> selectListCache(String key);

    /**
     * 模糊匹配查询key
     *
     * @param likeKey
     * @return
     */
    <T> List<T> selectCacheLike(String likeKey);

    /**
     * 删除模糊匹配到的所有key
     *
     * @param likeKey
     * @return
     */
    boolean deleteCacheLike(String likeKey);

    /**
     * 插入带有过期时间的对象
     *
     * @param key     key值
     * @param t       value值
     * @param timeout 过期时间(毫秒)
     * @return
     */
    public boolean insertTimeoutCache(String key, T t, long timeout);
}
