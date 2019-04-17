package com.hex.bigdata.udsp.iq.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.bigdata.udsp.iq.dto.IqMetadataView;
import com.hex.bigdata.udsp.iq.model.IqMetadata;
import com.hex.goframe.model.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IqMetadataMapper extends SyncMapper<IqMetadata> {

    @Override
    protected boolean insertExe(IqMetadata iqMetadata) {
        return sqlSessionTemplate.insert("com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.insert", iqMetadata) == 1;
    }

    @Override
    protected boolean updateExe(IqMetadata iqMetadata) {
        return sqlSessionTemplate.update("com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.updateByPrimaryKey", iqMetadata) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        return sqlSessionTemplate.update("com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.deleteByPrimaryKeyFake", id) == 1;
    }

    @Override
    protected IqMetadata selectExe(String id) {
        return sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.selectByPrimaryKey", id);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<IqMetadata> selectListExe(String fkId) {
        return null;
    }

    public List<IqMetadataView> select(IqMetadataView iqMetadataView, Page page) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.select", iqMetadataView,
                page.toPageBounds());
    }

    public List<IqMetadataView> select(IqMetadataView iqMetadataView) {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.select", iqMetadataView);
    }

    public List<IqMetadata> selectAll() {
        return sqlSessionTemplate.selectList(
                "com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.selectAll");
    }

    public IqMetadata selectByName(String name) {
        return this.sqlSessionTemplate.selectOne("com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.selectByName", name);
    }

    public List<IqMetadata> selectListByDsid(String dsId){
        return sqlSessionTemplate.selectList( "com.hex.bigdata.udsp.iq.dao.IqMetadataMapper.selectListByDsid",dsId);
    }
}