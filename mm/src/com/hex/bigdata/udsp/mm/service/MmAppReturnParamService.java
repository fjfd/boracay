package com.hex.bigdata.udsp.mm.service;

import com.hex.bigdata.udsp.mm.dao.MmAppReturnParamMapper;
import com.hex.bigdata.udsp.mm.model.MmAppReturnParam;
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
 * TIME:9:16
 */
@Service
public class MmAppReturnParamService extends BaseService {

    @Autowired
    private MmAppReturnParamMapper returnParamMapper;

    @Transactional
    public String insert(MmAppReturnParam returnParam) {
        String pkId = Util.uuid();
        returnParam.setPkId(pkId);
        if (returnParamMapper.insert(returnParam.getPkId(), returnParam)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(MmAppReturnParam returnParam) {
        return returnParamMapper.update(returnParam.getPkId(), returnParam);
    }

    @Transactional
    public boolean delete(String pkId) {
        return returnParamMapper.delete(pkId);
    }

    public MmAppReturnParam select(String pkId) {
        return returnParamMapper.select(pkId);
    }

    public List<MmAppReturnParam> selectByFkId(String fkId) {
        return returnParamMapper.selectList(fkId);
    }

    @Transactional
    public boolean deleteByFkId(String fkId) {
        return returnParamMapper.deleteList(fkId);
    }

    @Transactional
    public boolean insertList(String modelId, List<MmAppReturnParam> returnParams) {
        for (MmAppReturnParam returnParam : returnParams) {
            returnParam.setPkId(Util.uuid());
            returnParam.setAppId(modelId);
        }
        return returnParamMapper.insertList(modelId, returnParams);
    }

    @Transactional
    public boolean updateList(String modelId, List<MmAppReturnParam> returnParams) {
        for (MmAppReturnParam returnParam : returnParams) {
            returnParam.setAppId(modelId);
        }
        return returnParamMapper.updateList(modelId, returnParams);
    }
}
