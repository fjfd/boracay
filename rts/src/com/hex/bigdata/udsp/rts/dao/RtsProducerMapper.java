package com.hex.bigdata.udsp.rts.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.rts.dto.RtsProducerView;
import com.hex.bigdata.udsp.rts.model.RtsProducer;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 实时流-生产者者配置Dao层服务
 * Created by tomnic on 2017/2/28.
 */
@Repository
public class RtsProducerMapper extends SyncMapper<RtsProducer> {

    @Override
    protected boolean insertExe(RtsProducer rtsProducer) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.insert", rtsProducer) == 1;
    }

    @Override
    protected boolean updateExe(RtsProducer rtsProducer) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.updateByPrimaryKey", rtsProducer) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.deleteByPrimaryKeyFake", id) == 1;
    }

    @Override
    protected RtsProducer selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.selectByPrimaryKey", id);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<RtsProducer> selectListExe(String fkId) {
        return null;
    }

    /**
     * @param rtsProConfigView
     * @param page
     * @return
     */
    public List<RtsProducerView> selectPage(RtsProducerView rtsProConfigView, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.selectPage", rtsProConfigView,
                page.toPageBounds());
    }

    /**
     * 根据名称查询数据源实体
     *
     * @param name 数据源名称
     * @return
     */
    public RtsProducer selectByName(String name) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.selectByName", name);
    }

    /**
     * 根据查询条件查询结果list，不分页
     *
     * @param rtsProConfigView 查询参数
     * @return
     */
    public List<RtsProducerView> select(RtsProducerView rtsProConfigView) {
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.selectPage", rtsProConfigView);
    }

    public List<RtsProducer> selectAll() {
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.rts.dao.RtsProducerMapper.selectAll");
    }
}