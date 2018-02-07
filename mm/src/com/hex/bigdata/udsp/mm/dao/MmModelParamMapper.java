package com.hex.bigdata.udsp.mm.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.mm.model.MmModelParam;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MmModelParamMapper extends SyncMapper<MmModelParam> {

    protected boolean insertExe(MmModelParam modelParam) {

        return sqlSessionTemplate.insert("com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.insert", modelParam) == 1;
    }

    protected boolean updateExe(MmModelParam modelParam) {
        return sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.updateByPrimaryKey", modelParam) == 1;
    }

    protected boolean deleteExe(String id) {
        return sqlSessionTemplate.delete("com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.deleteByPrimaryKey", id) == 1;
    }

    protected MmModelParam selectExe(String id) {
        return sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.selectByPrimaryKey", id);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return sqlSessionTemplate.delete("com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.deleteByMmId", id) >= 0;
    }

    protected List<MmModelParam> selectListExe(String fkId) {
        MmModelParam modelParam = new MmModelParam();
        modelParam.setMmId(fkId);
        return select(modelParam);
    }

    public List<MmModelParam> select(String mmId, String type) {
        MmModelParam modelParam = new MmModelParam();
        modelParam.setMmId(mmId);
        modelParam.setType(type);
        return select(modelParam);
    }

    public List<MmModelParam> selectByMmId(String mmId) {
        return this.selectList(mmId);
    }

    public List<MmModelParam> select(MmModelParam modelParam, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.select", modelParam,
                page.toPageBounds());
    }

    public List<MmModelParam> select(MmModelParam modelParam) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.mm.dao.MmModelParamMapper.select", modelParam);
    }

    public boolean deleteByMmId(String mmId) {
        return this.deleteList(mmId);
    }
}