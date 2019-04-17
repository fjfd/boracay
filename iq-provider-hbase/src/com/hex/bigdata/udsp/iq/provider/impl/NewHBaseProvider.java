package com.hex.bigdata.udsp.iq.provider.impl;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.iq.provider.Provider;
import com.hex.bigdata.udsp.iq.provider.model.IqRequest;
import com.hex.bigdata.udsp.iq.provider.model.IqResponse;
import com.hex.bigdata.udsp.iq.provider.model.MetadataCol;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;

import java.util.List;

/**
 * Created by JunjieM on 2019-1-29.
 */
public class NewHBaseProvider implements Provider {
    @Override
    public IqResponse query(IqRequest request) {
        return null;
    }

    @Override
    public IqResponse query(IqRequest request, Page page) {
        return null;
    }

    @Override
    public boolean testDatasource(Datasource datasource) {
        return false;
    }

    @Override
    public List<MetadataCol> columnInfo(Datasource datasource, String schemaName) {
        return null;
    }

    @Override
    public IqDslResponse select(IqDslRequest request) {
        return null;
    }
}
