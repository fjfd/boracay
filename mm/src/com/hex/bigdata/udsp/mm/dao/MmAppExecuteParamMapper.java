package com.hex.bigdata.udsp.mm.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.mm.model.MmAppExecuteParam;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class MmAppExecuteParamMapper extends SyncMapper<MmAppExecuteParam> {

    @Override
    protected boolean insertExe(MmAppExecuteParam mmAppReturnParam) {
        return this.sqlSessionTemplate.insert("com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper.insert", mmAppReturnParam) == 1;
    }

    @Override
    protected boolean updateExe(MmAppExecuteParam mmAppReturnParam) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper.updateByPrimaryKey", mmAppReturnParam) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return this.sqlSessionTemplate.update("com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper.deleteByPrimaryKey", id) == 1;
    }

    @Override
    protected MmAppExecuteParam selectExe(String id) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper.selectByPrimaryKey", id);
    }


    @Override
    protected boolean deleteListExe(String fkId) {
        this.sqlSessionTemplate.delete("com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper.deleteListByFkId", fkId);
        return true;
    }

    @Override
    protected List<MmAppExecuteParam> selectListExe(String fkId) {
        return this.sqlSessionTemplate.selectList("com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper.selectListByFkId",fkId);
    }
}