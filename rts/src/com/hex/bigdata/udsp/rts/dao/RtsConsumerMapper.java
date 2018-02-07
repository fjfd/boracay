package com.hex.bigdata.udsp.rts.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.rts.dto.RtsConsumerView;
import com.hex.bigdata.udsp.rts.model.RtsConsumer;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 实时流-消费者配置Dao层服务
 * Created by tomnic on 2017/2/28.
 */
@Repository
public class RtsConsumerMapper extends SyncMapper<RtsConsumer> {

    @Override
    protected boolean insertExe(RtsConsumer rtsConsumer) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.insert", rtsConsumer) == 1;
    }

    @Override
    protected boolean updateExe(RtsConsumer rtsConsumer) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.updateByPrimaryKey", rtsConsumer) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.deleteByPrimaryKeyFake", id) == 1;
    }

    @Override
    protected RtsConsumer selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.selectByPrimaryKey", id);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<RtsConsumer> selectListExe(String fkId) {
        return null;
    }

    /**
     * @param rtsConsumerView
     * @param page
     * @return
     */
    public List<RtsConsumerView> selectPage(RtsConsumerView rtsConsumerView, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.selectPage", rtsConsumerView,
                page.toPageBounds());
    }

    /**
     * 根据名称查询数据源实体
     *
     * @param name 数据源名称
     * @return
     */
    public RtsConsumer selectByName(String name) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.selectByName", name);
    }

    /**
     * 根据查询条件查询结果list，不分页
     * @param rtsDatasourceView 查询参数
     * @return
     */
    public List<RtsConsumerView> select(RtsConsumerView rtsDatasourceView) {
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.selectPage",rtsDatasourceView);
    }

    public List<RtsConsumer> selectAll(){
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.rts.dao.RtsConsumerMapper.selectAll");
    }
}