package com.hex.bigdata.udsp.mm.service;

import com.hex.bigdata.udsp.mm.dao.MmModelParamMapper;
import com.hex.bigdata.udsp.mm.model.MmModelParam;
import com.hex.goframe.model.Page;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/4/19
 * TIME:14:43
 */
@Service
public class MmModelParamService extends BaseService {

    @Autowired
    private MmModelParamMapper modelParamMapper;

    @Transactional
    public String insert(MmModelParam modelParam) {
        String pkId = Util.uuid();
        modelParam.setPkId(pkId);
        if (modelParamMapper.insert(modelParam.getPkId(), modelParam)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(MmModelParam modelParam) {
        return modelParamMapper.update(modelParam.getPkId(), modelParam);
    }

    @Transactional
    public boolean delete(String pkId) {
        return modelParamMapper.delete(pkId);
    }

    @Transactional
    public boolean deleteByMmId(String mmId) {
        return modelParamMapper.deleteByMmId(mmId);
    }

    public MmModelParam select(String pkId) {
        return modelParamMapper.select(pkId);
    }

    public List<MmModelParam> select(MmModelParam modelParam, Page page) {
        return modelParamMapper.select(modelParam, page);
    }

    public List<MmModelParam> select(String mmId, String type) {
        return modelParamMapper.select(mmId, type);
    }

    public List<MmModelParam> select(MmModelParam modelParam) {
        return modelParamMapper.select(modelParam);
    }

    public List<MmModelParam> selectByMmId(String mmId) {
        return modelParamMapper.selectByMmId(mmId);
    }

    @Transactional
    public boolean insertQueryColList(String mmId, List<MmModelParam> modelParams) {
        for (MmModelParam modelParam : modelParams) {
            modelParam.setPkId(Util.uuid());
            modelParam.setMmId(mmId);
            modelParam.setType("1");
        }
        return modelParamMapper.insertList(mmId, modelParams);
    }

    @Transactional
    public boolean insertReturnColList(String mmId, List<MmModelParam> modelParams) {
        if (modelParams == null) {
            return true;
        }
        for (MmModelParam modelParam : modelParams) {
            modelParam.setPkId(Util.uuid());
            modelParam.setMmId(mmId);
            modelParam.setType("2");
        }
        return modelParamMapper.insertList(mmId, modelParams);
    }

}
