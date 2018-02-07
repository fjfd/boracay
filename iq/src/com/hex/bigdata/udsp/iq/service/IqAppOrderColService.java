package com.hex.bigdata.udsp.iq.service;

import com.hex.bigdata.udsp.iq.dao.IqAppOrderColMapper;
import com.hex.bigdata.udsp.iq.model.IqAppOrderCol;
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
public class IqAppOrderColService extends BaseService {
    @Autowired
    private IqAppOrderColMapper iqAppOrderColMapper;

    @Transactional
    public String insert(IqAppOrderCol iqAppOrderColq) {
        String pkId = Util.uuid();
        iqAppOrderColq.setPkId(pkId);
        if (iqAppOrderColMapper.insert(iqAppOrderColq.getPkId(), iqAppOrderColq)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(IqAppOrderCol iqAppOrderCol) {
        return iqAppOrderColMapper.update(iqAppOrderCol.getPkId(), iqAppOrderCol);
    }

    @Transactional
    public boolean delete(String pkId) {
        return iqAppOrderColMapper.delete(pkId);
    }

    @Transactional
    public boolean deleteByAppId(String appId) {
        return iqAppOrderColMapper.deleteByAppId(appId);
    }

    public IqAppOrderCol select(String pkId) {
        return iqAppOrderColMapper.select(pkId);
    }

    public List<IqAppOrderCol> selectByAppId(String appId) {
        return iqAppOrderColMapper.selectByAppId(appId);
    }

    public List<IqAppOrderCol> select(IqAppOrderCol iqAppOrderCol, Page page) {
        return iqAppOrderColMapper.select(iqAppOrderCol, page);
    }

    public List<IqAppOrderCol> select(IqAppOrderCol iqAppOrderCol) {
        return iqAppOrderColMapper.select(iqAppOrderCol);
    }

    @Transactional
    public boolean insertList(String appId, List<IqAppOrderCol> iqAppOrderColList) {
        if (iqAppOrderColList == null) return true;
        for (IqAppOrderCol iqAppOrderCol : iqAppOrderColList) {
            iqAppOrderCol.setPkId(Util.uuid());
            iqAppOrderCol.setAppId(appId);
        }
        return iqAppOrderColMapper.insertList(appId, iqAppOrderColList);
    }
}
