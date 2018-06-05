package com.hex.bigdata.udsp.common.dao.base;

import com.hex.bigdata.udsp.common.dao.cache.Cache;
import com.hex.bigdata.udsp.common.model.ComOperationLog;
import com.hex.bigdata.udsp.common.util.CacheUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.common.util.MD5Util;
import com.hex.goframe.dao.BaseMapper;
import com.hex.goframe.util.Util;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

/**
 * 同步操作的抽象类，只对内存操作，DDL操作都记日志但不入库
 *
 * @param <T>
 */
public abstract class SyncCacheMapper<T> extends BaseMapper {
    private static Logger logger = LogManager.getLogger(SyncCacheMapper.class);
    private static final FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

    private String clazz = this.getClass().getName();

    /**
     * 缓存模式（none、local、ehcache、redis）
     */
    @Value("${cache.mode:ehcache}")
    private String cacheMode;

    protected Cache<T> cache;

    /**
     * 这里必须是protected，如子类需要使用其他缓存时可以自行重载
     *
     * @return
     */
    protected Cache<T> getCache() {
        if (this.cache == null) {
            this.cache = CacheUtil.getCache2(cacheMode);
        }
        return this.cache;
    }

    // ----------------------------insert----------------------------------------

    /**
     * 插入
     *
     * @param id
     * @param t
     * @return
     */
    public boolean insert(String id, T t) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().insertCache(key, t);
        insertLog("1", "Class=" + clazz + " Object=" + t.getClass().getName());
        return status;
    }

    //------------------------------update------------------------------------------

    /**
     * 更新
     *
     * @param id
     * @param t
     * @return
     */
    public boolean update(String id, T t) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().updateCache(key, t);
        insertLog("2", "Class=" + clazz + " Object=" + t.getClass().getName());
        return status;
    }

    //---------------------------------delete--------------------------------------

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public boolean delete(String id) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().deleteCache(key);
        insertLog("3", "Class=" + clazz + " PkId=" + id);
        return status;
    }

    //------------------------------------select------------------------------------

    /**
     * 查询
     *
     * @param id
     * @return
     */
    public T select(String id) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        T t = getCache().selectCache(key);
        logger.debug("查询 Class=" + clazz + " PkId=" + id);
        return t;
    }

    //-----------------------------------insertList---------------------------------------

    /**
     * 插入集合
     *
     * @param id
     * @param list
     * @return
     */
    public boolean insertList(String id, List<T> list) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().insertListCache(key, list);
        insertLog("1", "Class=" + clazz + " ListSize=" + list.size());
        return status;
    }

    //------------------------------updateList------------------------------------------

    /**
     * 更新集合
     *
     * @param id
     * @param list
     * @return
     */
    public boolean updateList(String id, List<T> list) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().updateListCache(key, list);
        insertLog("2", "Class=" + clazz + " ListSize=" + list.size());
        return status;
    }

    //---------------------------------deleteList--------------------------------------

    /**
     * 删除集合
     *
     * @param id
     * @return
     */
    public boolean deleteList(String id) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().deleteListCache(key);
        insertLog("3", "Class=" + clazz + " FkId=" + id);
        return status;
    }

    //-----------------------------------selectList---------------------------------------

    /**
     * 查询集合
     *
     * @param id
     * @return
     */
    public List<T> selectList(String id) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        List<T> list = getCache().selectListCache(key);
        logger.debug("批量查询 Class=" + clazz + " FkId=" + id);
        return list;
    }

    //-----------------------------------selectLike---------------------------------------

    /**
     * 模糊查询集合
     *
     * @param likeId
     * @return
     */
    public List<T> selectLike(String likeId) {
        String key = MD5Util.MD5_16(clazz) + ":" + likeId;
        List<T> list = getCache().selectCacheLike(key);
        logger.debug("批量模糊查询 Class=" + clazz + " FkId=" + likeId);
        return list;
    }

    // ----------------------------deleteLike----------------------------------------

    /**
     * 模糊删除集合
     *
     * @param likeId
     * @return
     */
    public boolean deleteLike(String likeId) {
        String key = MD5Util.MD5_16(clazz) + ":" + likeId;
        boolean status = getCache().deleteCacheLike(key);
        insertLog("3", "Class=" + clazz + " LikeId=" + likeId);
        return status;
    }


    // ----------------------------insertTimeout----------------------------------------

    /**
     * 插入并设置超时时间
     *
     * @param id
     * @param t
     * @return
     */
    public boolean insertTimeout(String id, T t, long timeout) {
        String key = MD5Util.MD5_16(clazz) + ":" + id;
        boolean status = getCache().insertTimeoutCache(key, t, timeout);
        insertLog("1", "Class=" + clazz + " Object=" + t.getClass().getName() + " Timeout=" + timeout);
        return status;
    }

    // --------------------------------------------------------------------------------------

    /**
     * 写日志
     *
     * @param type
     * @param log
     */
    private void insertLog(String type, String log) {
        try {
            ComOperationLog comOperationLog = new ComOperationLog();
            comOperationLog.setPkId(Util.uuid());
            comOperationLog.setActionType(type);
            //comOperationLog.setActionUser(WebUtil.getCurrentUserName());
            //comOperationLog.setActionUrl(WebUtil.getRequest().getRequestURL().toString());
            comOperationLog.setActionTime(format.format(new Date()));
            comOperationLog.setActionContent(log);
            logger.debug(JSONUtil.parseObj2JSON(comOperationLog));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
