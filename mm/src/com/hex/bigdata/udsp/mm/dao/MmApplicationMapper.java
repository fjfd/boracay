package com.hex.bigdata.udsp.mm.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.mm.dto.MmApplicationView;
import com.hex.bigdata.udsp.mm.dto.MmFullAppInfoView;
import com.hex.bigdata.udsp.mm.model.MmApplication;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MmApplicationMapper extends SyncMapper<MmApplication> {

    @Override
    protected boolean insertExe(MmApplication mmApplication) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.insert", mmApplication) == 1;
    }

    @Override
    protected boolean updateExe(MmApplication mmApplication) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.updateByPrimaryKey", mmApplication) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.deleteByPrimaryKeyFake", id) == 1;
    }

    @Override
    protected MmApplication selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.selectByPrimaryKey", id);
    }


    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<MmApplication> selectListExe(String fkId) {
        return null;
    }

    /**
     * @param mmApplicationView
     * @param page
     * @return
     */
    public List<MmApplicationView> selectPage(MmApplicationView mmApplicationView, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.selectPage", mmApplicationView,
                page.toPageBounds());
    }

    /**
     * 根据名称查找
     *
     * @param name
     * @return
     */
    public MmApplication selectByName(String name) {
        return sqlSessionTemplate.selectOne(
                "com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.selectByName", name);
    }

    /**
     * 根据应用Ip查询应用信息
     * @param pkId
     * @return
     */
    public MmFullAppInfoView selectFullAppInfo(String pkId) {
        return sqlSessionTemplate.selectOne(
                "com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.selectFullAppInfo", pkId);
    }

    /**
     * 查询所有
     * @return
     */
    public List<MmApplication> selectAll() {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.selectAll");
    }

    /**
     * 根据模型id查询应用信息
     * @param modelId
     * @return
     */
    public List<MmApplication> selectByModelId(String modelId) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmApplicationMapper.selectByModelId",modelId);
    }
}