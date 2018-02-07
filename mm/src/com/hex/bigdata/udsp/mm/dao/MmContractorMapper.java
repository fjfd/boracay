package com.hex.bigdata.udsp.mm.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.mm.dto.MmContractorView;
import com.hex.bigdata.udsp.mm.model.MmContractor;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class MmContractorMapper extends SyncMapper<MmContractor> {

    @Override
    protected boolean insertExe(MmContractor contractor) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.mm.dao.MmContractorMapper.insert", contractor) == 1;
    }

    @Override
    protected boolean updateExe(MmContractor contractor) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmContractorMapper.updateByPrimaryKey", contractor) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmContractorMapper.deleteByPrimaryKeyFake", id) == 1;
    }

    @Override
    protected MmContractor selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.mm.dao.MmContractorMapper.selectByPrimaryKey", id);
    }


    @Override
    protected boolean deleteListExe(String fkId) {
        return true;
    }

    @Override
    protected List<MmContractor> selectListExe(String fkId) {
        return null;
    }

    /**
     * 多田间分页查询
     * @param contractorView
     * @param page
     * @return
     */
    public List<MmContractorView> selectPage(MmContractorView contractorView, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmContractorMapper.selectPage", contractorView,
                page.toPageBounds());
    }

    /**
     * 查询所有厂商
     * @return
     */
    public List<MmContractor> selectAll(){
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.mm.dao.MmContractorMapper.selectAll");
    }

    /**
     * 根据名称查找
     * @param name
     * @return
     */
    public MmContractor selectByName(String name) {
        return sqlSessionTemplate.selectOne(
                "com.hex.bigdata.udsp.mm.dao.MmContractorMapper.selectByName",name);
    }


}