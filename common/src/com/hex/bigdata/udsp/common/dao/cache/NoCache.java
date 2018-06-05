package com.hex.bigdata.udsp.common.dao.cache;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by junjiem on 2017-2-16.
 */
@Repository("noCache")
public class NoCache<T> implements Cache<T> {

    @Override
    public boolean insertCache(String key, T t) {
        return false;
    }

    @Override
    public boolean updateCache(String key, T t) {
        return false;
    }

    @Override
    public boolean deleteCache(String key) {
        return false;
    }

    @Override
    public T selectCache(String key) {
        return null;
    }

    @Override
    public boolean insertListCache(String key, List<T> list) {
        return false;
    }

    @Override
    public boolean updateListCache(String key, List<T> list) {
        return false;
    }

    @Override
    public boolean deleteListCache(String key) {
        return false;
    }

    @Override
    public <T1> List<T1> selectListCache(String key) {
        return null;
    }

    @Override
    public <T1> List<T1> selectCacheLike(String likeKey) {
        return null;
    }

    @Override
    public boolean deleteCacheLike(String likeKey) {
        return false;
    }

    @Override
    public boolean insertTimeoutCache(String key, T t, long timeout) {
        return false;
    }
}
