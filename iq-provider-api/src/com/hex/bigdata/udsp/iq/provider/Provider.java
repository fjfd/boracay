package com.hex.bigdata.udsp.iq.provider;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.iq.provider.model.IqRequest;
import com.hex.bigdata.udsp.iq.provider.model.IqResponse;
import com.hex.bigdata.udsp.iq.provider.model.MetadataCol;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;

import java.util.List;

/**
 * Created by junjiem on 2017-2-15.
 */
public interface Provider {

    /**
     * 查询
     *
     * @param request
     * @return
     */
    IqResponse query(IqRequest request);

    /**
     * 分页查询
     *
     * @param request
     * @param page
     * @return
     */
    IqResponse query(IqRequest request, Page page);

    /**
     * 测试
     *
     * @param datasource
     * @return
     */
    boolean testDatasource(Datasource datasource);

    /**
     * 获取字段信息
     *
     * @param datasource
     * @param schemaName
     * @return
     */
    List<MetadataCol> columnInfo(Datasource datasource, String schemaName);

    /**
     * 自定义SQL查询
     *
     * @param request
     * @return
     */
    IqDslResponse select(IqDslRequest request);

}
