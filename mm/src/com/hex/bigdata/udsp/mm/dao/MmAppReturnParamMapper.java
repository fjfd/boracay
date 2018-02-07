package com.hex.bigdata.udsp.mm.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.mm.model.MmAppReturnParam;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MmAppReturnParamMapper extends SyncMapper<MmAppReturnParam> {

    @Override
    protected boolean insertExe(MmAppReturnParam mmAppReturnParam) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper.insert", mmAppReturnParam) == 1;
    }

    @Override
    protected boolean updateExe(MmAppReturnParam mmAppReturnParam) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper.updateByPrimaryKey", mmAppReturnParam) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper.deleteByPrimaryKey", id) == 1;
    }

    @Override
    protected MmAppReturnParam selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper.selectByPrimaryKey", id);
    }


    @Override
    protected boolean deleteListExe(String fkId) {
        this.sqlSessionTemplate.delete("com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper.deleteListByFkId",fkId);
        return true;
    }

    @Override
    protected List<MmAppReturnParam> selectListExe(String fkId) {
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper.selectListByFkId",fkId);
    }
}