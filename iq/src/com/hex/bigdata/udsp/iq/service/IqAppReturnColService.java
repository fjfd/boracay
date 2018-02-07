package com.hex.bigdata.udsp.iq.service;

import com.hex.bigdata.udsp.iq.dao.IqAppReturnColMapper;
import com.hex.bigdata.udsp.iq.model.IqAppReturnCol;
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
public class IqAppReturnColService extends BaseService {
    @Autowired
    private IqAppReturnColMapper iqAppReturnColMapper;

    @Transactional
    public String insert(IqAppReturnCol iqAppReturnCol) {
        String pkId = Util.uuid();
        iqAppReturnCol.setPkId(pkId);
        if (iqAppReturnColMapper.insert(iqAppReturnCol.getPkId(), iqAppReturnCol)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(IqAppReturnCol iqAppReturnCol) {
        return iqAppReturnColMapper.update(iqAppReturnCol.getPkId(), iqAppReturnCol);
    }

    @Transactional
    public boolean delete(String pkId) {
        return iqAppReturnColMapper.delete(pkId);
    }

    @Transactional
    public boolean deleteByAppId(String appId) {
        return iqAppReturnColMapper.deleteByAppId(appId);
    }

    public IqAppReturnCol select(String pkId) {
        return iqAppReturnColMapper.select(pkId);
    }

    public List<IqAppReturnCol> select(IqAppReturnCol iqAppReturnCol, Page page) {
        return iqAppReturnColMapper.select(iqAppReturnCol, page);
    }

    public List<IqAppReturnCol> select(IqAppReturnCol iqAppReturnCol) {
        return iqAppReturnColMapper.select(iqAppReturnCol);
    }

    public List<IqAppReturnCol> selectByAppId(String appId) {
        return iqAppReturnColMapper.selectByAppId(appId);
    }

    @Transactional
    public boolean insertList(String appId, List<IqAppReturnCol> iqAppReturnColList) {
        if (iqAppReturnColList == null) return true;
        for (IqAppReturnCol iqAppReturnCol : iqAppReturnColList) {
            if("".equals(iqAppReturnCol.getStats()) || null == iqAppReturnCol.getStats()){
                iqAppReturnCol.setStats("none");
            }
            if("".equals(iqAppReturnCol.getLabel()) || null == iqAppReturnCol.getLabel()){
                iqAppReturnCol.setLabel(iqAppReturnCol.getName());
            }
            iqAppReturnCol.setPkId(Util.uuid());
            iqAppReturnCol.setAppId(appId);
        }
        return iqAppReturnColMapper.insertList(appId, iqAppReturnColList);
    }
}
