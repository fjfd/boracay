package com.hex.bigdata.udsp.im.converter;

import com.hex.bigdata.udsp.common.api.model.Datasource;

/**
 * Created by JunjieM on 2017-9-5.
 */
public interface Converter {
    /**
     * 测试
     *
     * @param datasource
     * @return
     */
    boolean testDatasource(Datasource datasource);
}
