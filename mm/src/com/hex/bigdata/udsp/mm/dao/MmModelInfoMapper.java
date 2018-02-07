package com.hex.bigdata.udsp.mm.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.mm.dto.MmModelInfoView;
import com.hex.bigdata.udsp.mm.model.MmModelInfo;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MmModelInfoMapper extends SyncMapper<MmModelInfo> {

    @Override
    protected boolean insertExe(MmModelInfo modelInfo) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.insert", modelInfo) == 1;
    }

    @Override
    protected boolean updateExe(MmModelInfo modelInfo) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.updateByPrimaryKey", modelInfo) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.deleteByPrimaryKeyFake", id) == 1;
    }

    @Override
    protected MmModelInfo selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.selectByPrimaryKey", id);
    }


    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<MmModelInfo> selectListExe(String fkId) {
        return null;
    }

    /**
     * @param modelInfoView
     * @param page
     * @return
     */
    public List<MmModelInfoView> selectPage(MmModelInfoView modelInfoView, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.selectPage", modelInfoView,
                page.toPageBounds());
    }

    /**
     * 根据名称查找
     * @param name
     * @return
     */
    public MmModelInfo selectByName(String name) {
        return sqlSessionTemplate.selectOne(
                "com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.selectByName",name);
    }

    /**
     * 查询所有模型
     * @return
     */
    public List<MmModelInfo> selectAll() {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.selectAll");
    }

    public List<MmModelInfo> selectByContractorId(String contractorId) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmModelInfoMapper.selectByContractorId",contractorId);
    }
}