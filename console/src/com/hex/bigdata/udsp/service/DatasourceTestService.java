package com.hex.bigdata.udsp.service;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.constant.DatasourceMode;
import com.hex.bigdata.udsp.common.dto.ComDatasourcePropsView;
import com.hex.bigdata.udsp.common.model.ComDatasource;
import com.hex.bigdata.udsp.common.model.ComProperties;
import com.hex.bigdata.udsp.common.util.DatasourceUtil;
import com.hex.bigdata.udsp.im.service.ImConverterService;
import com.hex.bigdata.udsp.iq.service.IqProviderService;
import com.hex.bigdata.udsp.olq.service.OlqProviderService;
import com.hex.bigdata.udsp.rts.service.RtsExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试数据源的服务
 */
@Service
public class DatasourceTestService {
    @Autowired
    private IqProviderService iqProviderService;
    @Autowired
    private OlqProviderService olqProviderService;
    @Autowired
    private RtsExecutorService rtsExecutorService;
    @Autowired
    private ImConverterService imConverterService;

    /**
     * 测试数据源
     *
     * @param comDatasourcePropsView
     * @return
     */
    public boolean testDatasource(ComDatasourcePropsView comDatasourcePropsView) {
        ComDatasource comDatasource = comDatasourcePropsView.getComDatasource();
        List<ComProperties> comPropertiesList = comDatasourcePropsView.getComPropertiesList();
        Datasource datasource = DatasourceUtil.getDatasource(comDatasource, comPropertiesList);
        String model = comDatasource.getModel();
        if (DatasourceMode.IQ.getValue().equalsIgnoreCase(model)) {
            return iqProviderService.testDatasource(datasource);
        } else if (DatasourceMode.OLQ.getValue().equalsIgnoreCase(model)) {
            return olqProviderService.testDatasource(datasource);
        } else if (DatasourceMode.RTS.getValue().equalsIgnoreCase(model)) {
            return rtsExecutorService.testDatasource(datasource);
        } else if (DatasourceMode.IM.getValue().equalsIgnoreCase(model)) {
            return imConverterService.testDatasource(datasource);
        }
        return false;
    }
}
