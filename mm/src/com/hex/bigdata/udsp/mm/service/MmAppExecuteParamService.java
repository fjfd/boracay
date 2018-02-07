package com.hex.bigdata.udsp.mm.service;

import com.hex.bigdata.udsp.mm.dao.MmAppExecuteParamMapper;
import com.hex.bigdata.udsp.mm.model.MmAppExecuteParam;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/4/6
 * TIME:9:15
 */
@Service
public class MmAppExecuteParamService extends BaseService {

    @Autowired
    private MmAppExecuteParamMapper executeParamMapper;

    @Transactional
    public String insert(MmAppExecuteParam executeParam) {
        String pkId = Util.uuid();
        executeParam.setPkId(pkId);
        if (executeParamMapper.insert(executeParam.getPkId(), executeParam)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(MmAppExecuteParam executeParam) {
        return executeParamMapper.update(executeParam.getPkId(), executeParam);
    }

    @Transactional
    public boolean delete(String pkId) {
        return executeParamMapper.delete(pkId);
    }

    public MmAppExecuteParam select(String pkId) {
        return executeParamMapper.select(pkId);
    }

    public List<MmAppExecuteParam> selectByFkId(String fkId) {
        return executeParamMapper.selectList(fkId);
    }

    @Transactional
    public boolean deleteByFkId(String fkId) {
        return executeParamMapper.deleteList(fkId);
    }

    @Transactional
    public boolean insertList(String modelId, List<MmAppExecuteParam> executeParams) {
        for (MmAppExecuteParam executeParam : executeParams) {
            executeParam.setPkId(Util.uuid());
            executeParam.setAppId(modelId);
        }
        return executeParamMapper.insertList(modelId, executeParams);
    }

    @Transactional
    public boolean updateList(String modelId, List<MmAppExecuteParam> executeParams) {
        for (MmAppExecuteParam executeParam : executeParams) {
            executeParam.setAppId(modelId);
        }
        return executeParamMapper.updateList(modelId, executeParams);
    }
}
