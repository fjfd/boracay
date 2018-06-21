package com.hex.goframe.dao;

import com.hex.bigdata.udsp.common.dao.base.SyncMapper;
import com.hex.goframe.model.GFDict;
import com.hex.goframe.model.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class GFDictMapper extends SyncMapper<GFDict> {

    public int deleteByPrimaryKey(String dictTypeId, String dictId) {
        String id = dictTypeId + "|" + dictId;
        return delete(id) ? 1 : 0;
    }

    public int insert(GFDict dict) {
        String id = dict.getDictTypeId() + "|" + dict.getDictId();
        return insert(id, dict) ? 1 : 0;
    }

    public GFDict selectByPrimaryKey(String dictTypeId, String dictId) {
        String id = dictTypeId + "|" + dictId;
        return select(id);
    }

    public List<GFDict> selectAll() {
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFDictMapper.selectAll");
    }

    public List<GFDict> selectDicts(String dictTypeId) {
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFDictMapper.selectDicts", dictTypeId);
    }

    public List<GFDict> getDictFilterData(GFDict dict) {
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFDictMapper.getDictFilterData", dict);
    }

    public int updateByPrimaryKey(GFDict dict) {
        String id = dict.getDictTypeId() + "|" + dict.getDictId();
        return update(id, dict) ? 1 : 0;
    }

    public List<GFDict> query(GFDict dict, Page page) {
        HashMap map = new HashMap();
        map.put("dictTypeId", dict.getDictTypeId());
        map.put("dictName", dict.getDictName());
        List list = this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFDictMapper.queryDicts", map, page.toPageBounds());
        page.totalCount(list);
        return list;
    }

    public List<GFDict> isExists(GFDict dict) {
        return this.sqlSessionTemplate.selectList("com.hex.goframe.dao.GFDictMapper.isExists", dict);
    }

    public int deleteByDictTypeId(String dictTypeId) {
        return this.sqlSessionTemplate.delete("com.hex.goframe.dao.GFDictMapper.deleteByDictTypeId", dictTypeId);
    }

    //  -----------------------------------------------------------------------------------------

    @Override
    protected boolean insertExe(GFDict dict) {
        return this.sqlSessionTemplate.insert("com.hex.goframe.dao.GFDictMapper.insert", dict) == 1;
    }

    @Override
    protected boolean updateExe(GFDict dict) {
        return this.sqlSessionTemplate.update("com.hex.goframe.dao.GFDictMapper.updateByPrimaryKey", dict) == 1;
    }

    @Override
    protected boolean deleteExe(String id) {
        String[] strings = id.split("\\|");
        String dictTypeId = strings[0];
        String dictId = strings[1];
        HashMap map = new HashMap();
        map.put("dictTypeId", dictTypeId);
        map.put("dictId", dictId);
        return this.sqlSessionTemplate.delete("com.hex.goframe.dao.GFDictMapper.deleteByPrimaryKey", map) == 1;
    }

    @Override
    protected GFDict selectExe(String id) {
        String[] strings = id.split("\\|");
        String dictTypeId = strings[0];
        String dictId = strings[1];
        HashMap map = new HashMap();
        map.put("dictTypeId", dictTypeId);
        map.put("dictId", dictId);
        return (GFDict) this.sqlSessionTemplate.selectOne("com.hex.goframe.dao.GFDictMapper.selectByPrimaryKey", map);
    }

    @Override
    protected boolean deleteListExe(String id) {
        return false;
    }

    @Override
    protected List<GFDict> selectListExe(String id) {
        return null;
    }

}
