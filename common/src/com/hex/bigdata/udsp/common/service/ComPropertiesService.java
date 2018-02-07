package com.hex.bigdata.udsp.common.service;

import com.hex.bigdata.udsp.common.dao.ComPropertiesMapper;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.goframe.service.BaseService;
import com.hex.goframe.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by junjiem on 2017-2-23.
 */
@Service
public class ComPropertiesService extends BaseService {
    @Autowired
    private ComPropertiesMapper comPropertiesMapper;

    @Transactional
    public String insert(ComProperties comProperties) {
        String pkId = Util.uuid();
        comProperties.setPkId(pkId);
        if (comPropertiesMapper.insert(comProperties.getPkId(), comProperties)) {
            return pkId;
        }
        return "";
    }

    @Transactional
    public boolean update(ComProperties comProperties) {
        return comPropertiesMapper.update(comProperties.getPkId(), comProperties);
    }

    @Transactional
    public boolean delete(String pkId) {
        return comPropertiesMapper.delete(pkId);
    }

    public ComProperties select(String pkId) {
        return comPropertiesMapper.select(pkId);
    }

    public List<ComProperties> selectList(String fkId) {
        return comPropertiesMapper.selectList(fkId);
    }

    @Transactional
    public boolean deleteList(String fkId) {
        return comPropertiesMapper.deleteList(fkId);
    }

    @Transactional
    public boolean insertList(String fkId, List<ComProperties> comPropertiesList) {
        for (ComProperties comProperties : comPropertiesList) {
            comProperties.setPkId(Util.uuid());
            comProperties.setFkId(fkId);
        }
        return comPropertiesMapper.insertList(fkId, comPropertiesList);
    }

    @Transactional
    public boolean updateList(String fkId, List<ComProperties> comPropertiesList) {
        if (comPropertiesList == null) return true;
        for (ComProperties comProperties : comPropertiesList) {
            comProperties.setFkId(fkId);
        }
        return comPropertiesMapper.updateList(fkId, comPropertiesList);
    }
}
