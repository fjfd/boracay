package com.hex.bigdata.udsp.olq.service;

import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.service.ComDatasourceService;
import com.hex.bigdata.udsp.common.service.ComPropertiesService;
import com.hex.bigdata.udsp.common.util.DatasourceUtil;
import com.hex.bigdata.udsp.common.util.ObjectUtil;
import com.hex.bigdata.udsp.olq.provider.Provider;
import com.hex.bigdata.udsp.olq.provider.model.OlqRequest;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponse;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponseFetch;
import com.hex.goframe.dao.GFDictMapper;
import com.hex.goframe.model.GFDict;
import com.hex.goframe.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by junjiem on 2017-3-2.
 */
@Service
public class OlqProviderService extends BaseService {
    private static final String OLQ_IMPL_CLASS = "OLQ_IMPL_CLASS";

    @Autowired
    private ComDatasourceService comDatasourceService;

    @Autowired
    private ComPropertiesService comPropertiesService;

    @Autowired
    private GFDictMapper gfDictMapper;

    /**
     * 查询
     *
     * @param consumeId
     * @param dsId
     * @param sql
     * @param page
     * @return
     */
    public OlqResponse select(String consumeId, String dsId, String sql, Page page) {
        if (StringUtils.isBlank (sql)) {
            throw new IllegalArgumentException ("sql参数不能为空!");
        }
        Datasource datasource = getDatasource (dsId);
        OlqRequest request = new OlqRequest (datasource, sql, page);
        Provider provider = getProviderImpl (datasource);
        return provider.execute (consumeId, request);
    }

    /**
     * 查询（结果集一批批抽取）
     *
     * @param dsId
     * @param sql
     * @param page
     * @return
     */
    public OlqResponseFetch selectFetch(String consumeId, String dsId, String sql, Page page) {
        if (StringUtils.isBlank (sql)) {
            throw new IllegalArgumentException ("sql参数不能为空!");
        }
        Datasource datasource = getDatasource (dsId);
        OlqRequest request = new OlqRequest (datasource, sql, page);
        Provider provider = getProviderImpl (datasource);
        return provider.executeFetch (consumeId, request);
    }

    /**
     * 获取数据源
     *
     * @param dsId
     * @return
     */
    private Datasource getDatasource(String dsId) {
        ComDatasource comDatasource = comDatasourceService.select (dsId);
        List<ComProperties> comPropertiesList = comPropertiesService.selectList (dsId);
        return DatasourceUtil.getDatasource (comDatasource, comPropertiesList);
    }

    /**
     * 测试数据源
     *
     * @param datasource
     * @return
     */
    public boolean testDatasource(Datasource datasource) {
        Provider provider = getProviderImpl (datasource);
        return provider.testDatasource (datasource);
    }

    private Provider getProviderImpl(Datasource datasource) {
        return (Provider) ObjectUtil.newInstance (getImplClass (datasource));
    }

    private String getImplClass(Datasource datasource) {
        String implClass = datasource.getImplClass ();
        if (StringUtils.isBlank (implClass)) {
            GFDict gfDict = gfDictMapper.selectByPrimaryKey (OLQ_IMPL_CLASS, datasource.getType ());
            implClass = gfDict.getDictName ();
        }
        return implClass;
    }
}
