package com.hex.bigdata.udsp.iq.service;

import com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper;
import com.hex.bigdata.udsp.iq.model.IqMetadataCol;
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
public class IqMetadataColService extends BaseService {
    @Autowired
    private IqMetadataColMapper iqMetadataColMapper;

    @Transactional
    public String insert(IqMetadataCol iqMetadataCol) {
        String pkId = Util.uuid();
        iqMetadataCol.setPkId(pkId);
        if (iqMetadataColMapper.insert(iqMetadataCol.getPkId(), iqMetadataCol)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(IqMetadataCol iqMetadataCol) {
        return iqMetadataColMapper.update(iqMetadataCol.getPkId(), iqMetadataCol);
    }

    @Transactional
    public boolean delete(String pkId) {
        return iqMetadataColMapper.delete(pkId);
    }

    @Transactional
    public boolean deleteByMdId(String mdId) {
        return iqMetadataColMapper.deleteByMdId(mdId);
    }

    public IqMetadataCol select(String pkId) {
        return iqMetadataColMapper.select(pkId);
    }

    public List<IqMetadataCol> select(IqMetadataCol iqMetadataCol, Page page) {
        return iqMetadataColMapper.select(iqMetadataCol, page);
    }

    public List<IqMetadataCol> select(IqMetadataCol iqMetadataCol) {
        return iqMetadataColMapper.select(iqMetadataCol);
    }

    public List<IqMetadataCol> selectQueryColList(String mdId) {
        return iqMetadataColMapper.selectList(mdId + "_1");
    }

    public List<IqMetadataCol> selectReturnColList(String mdId) {
        return iqMetadataColMapper.selectList(mdId + "_2");
    }

    public boolean insertQueryColList(String mdId, List<IqMetadataCol> iqMetadataQueryColList) {
        if (iqMetadataQueryColList == null) return true;
        for (IqMetadataCol iqMetadataCol : iqMetadataQueryColList) {
            iqMetadataCol.setPkId(Util.uuid());
            iqMetadataCol.setMdId(mdId);
            iqMetadataCol.setType("1");
        }
        return iqMetadataColMapper.insertList(mdId + "_1", iqMetadataQueryColList);
    }

    public boolean insertReturnColList(String mdId, List<IqMetadataCol> iqMetadataQueryColList) {
        if (iqMetadataQueryColList == null) return true;
        for (IqMetadataCol iqMetadataCol : iqMetadataQueryColList) {
            iqMetadataCol.setPkId(Util.uuid());
            iqMetadataCol.setMdId(mdId);
            iqMetadataCol.setType("2");
        }
        return iqMetadataColMapper.insertList(mdId + "_2", iqMetadataQueryColList);
    }
}
