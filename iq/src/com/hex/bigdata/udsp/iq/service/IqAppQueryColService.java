package com.hex.bigdata.udsp.iq.service;

import com.hex.bigdata.udsp.iq.dao.IqAppQueryColMapper;
import com.hex.bigdata.udsp.iq.model.IqAppQueryCol;
import com.hex.goframe.model.Page;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by junjiem on 2017-2-27.
 */
@Service
public class IqAppQueryColService extends BaseService {
    @Autowired
    private IqAppQueryColMapper iqAppQueryColMapper;

    @Transactional
    public String insert(IqAppQueryCol iqAppQueryCol) {
        String pkId = Util.uuid();
        iqAppQueryCol.setPkId(pkId);
        if (iqAppQueryColMapper.insert(iqAppQueryCol.getPkId(), iqAppQueryCol)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(IqAppQueryCol iqAppQueryCol) {
        return iqAppQueryColMapper.update(iqAppQueryCol.getPkId(), iqAppQueryCol);
    }

    @Transactional
    public boolean delete(String pkId) {
        return iqAppQueryColMapper.delete(pkId);
    }

    @Transactional
    public boolean deleteByAppId(String appId) {
        return iqAppQueryColMapper.deleteByAppId(appId);
    }

    public IqAppQueryCol select(String pkId) {
        return iqAppQueryColMapper.select(pkId);
    }

    public List<IqAppQueryCol> select(IqAppQueryCol iqAppQueryCol, Page page) {
        return iqAppQueryColMapper.select(iqAppQueryCol, page);
    }

    public List<IqAppQueryCol> select(IqAppQueryCol iqAppQueryCol) {
        return iqAppQueryColMapper.select(iqAppQueryCol);
    }

    public List<IqAppQueryCol> selectByAppId(String appId) {
        return iqAppQueryColMapper.selectByAppId(appId);
    }

    @Transactional
    public boolean insertList(String appId, List<IqAppQueryCol> iqAppQueryColList) {
        if (iqAppQueryColList == null) return true;
        for (IqAppQueryCol iqAppQueryCol : iqAppQueryColList) {
            if("是".equals(iqAppQueryCol.getIsNeed())){
                iqAppQueryCol.setIsNeed("0");
            }else if("否".equals(iqAppQueryCol.getIsNeed()) || "".equals(iqAppQueryCol.getIsNeed()) || null == iqAppQueryCol.getIsNeed()){
                iqAppQueryCol.setIsNeed("1");
            }
            if("是".equals(iqAppQueryCol.getIsOfferOut()) || "".equals(iqAppQueryCol.getIsOfferOut()) || null == iqAppQueryCol.getIsOfferOut()){
                iqAppQueryCol.setIsOfferOut("0");
            }else if("否".equals(iqAppQueryCol.getIsOfferOut()) ) {
                iqAppQueryCol.setIsOfferOut("1");
            }
            if("".equals(iqAppQueryCol.getLabel()) || null == iqAppQueryCol.getLabel()){
                iqAppQueryCol.setLabel(iqAppQueryCol.getName());
            }
            iqAppQueryCol.setPkId(Util.uuid());
            iqAppQueryCol.setAppId(appId);
        }
        return iqAppQueryColMapper.insertList(appId, iqAppQueryColList);
    }
}
