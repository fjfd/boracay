package com.hex.bigdata.udsp.iq.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.iq.model.IqMetadataCol;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IqMetadataColMapper extends SyncMapper<IqMetadataCol> {

    protected boolean insertExe(IqMetadataCol iqMetadataCol) {
        return sqlSessionTemplate.insert("com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.insert", iqMetadataCol) == 1;
    }

    protected boolean updateExe(IqMetadataCol iqMetadataCol) {
        return sqlSessionTemplate.update("com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.updateByPrimaryKey", iqMetadataCol) == 1;
    }

    protected boolean deleteExe(String id) {
        return sqlSessionTemplate.delete("com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.deleteByPrimaryKey", id) == 1;
    }

    protected IqMetadataCol selectExe(String id) {
        return sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.selectByPrimaryKey", id);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return sqlSessionTemplate.delete("com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.deleteByMdId", id) >= 0;
    }

    @Override
    protected List<IqMetadataCol> selectListExe(String fkId) {
        String[] fkIds = fkId.split("_");
        IqMetadataCol iqMetadataCol = new IqMetadataCol();
        iqMetadataCol.setMdId(fkIds[0]);
        iqMetadataCol.setType(fkIds[1]);
        return select(iqMetadataCol);
    }

    public List<IqMetadataCol> select(IqMetadataCol iqMetadataCol, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.select", iqMetadataCol,
                page.toPageBounds());
    }

    public List<IqMetadataCol> select(IqMetadataCol iqMetadataCol) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.iq.dao.IqMetadataColMapper.select", iqMetadataCol);
    }

    public boolean deleteByMdId(String mdId) {
        return this.deleteList(mdId);
    }
}