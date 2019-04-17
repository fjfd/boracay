package com.hex.bigdata.udsp.rts.executor;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.rts.executor.model.ConsumerRequest;
import com.hex.bigdata.udsp.rts.executor.model.ConsumerResponse;
import com.hex.bigdata.udsp.rts.executor.model.ProducerRequest;
import com.hex.bigdata.udsp.rts.executor.model.ProducerResponse;

/**
 * Created by junjiem on 2017-2-20.
 */
public interface Executor {

    // 推
    ProducerResponse push(ProducerRequest producerRequest);

    // 拉
    ConsumerResponse pull(ConsumerRequest consumerRequest);

    // 测试数据源
    boolean testDatasource(Datasource datasource);
}
