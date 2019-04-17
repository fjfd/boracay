package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.util.Util;
import com.hex.bigdata.udsp.rts.executor.model.ConsumerResponse;
import com.hex.bigdata.udsp.rts.executor.model.ProducerResponse;
import com.hex.bigdata.udsp.rts.service.RtsExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 同步实时流的服务（包括生产者和消费者）
 */
@Service
public class RtsSyncService {
    @Autowired
    private RtsExecutorService rtsExecutorService;

    /**
     * 同步运行生产者
     *
     * @param appId
     * @param datas
     * @return
     */
    public Response startProducer(String appId, List<Map<String, String>> datas) {
        Response response = new Response ();
        try {
            ProducerResponse producerResponse = rtsExecutorService.producer (appId, datas);
            response.setConsumeTime (producerResponse.getConsumeTime ());
            response.setMessage (producerResponse.getMessage ());
            response.setStatus (producerResponse.getStatus ().getValue ());
            response.setStatusCode (producerResponse.getStatusCode ().getValue ());
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }
        return response;
    }

    /**
     * 同步运行消费者
     *
     * @param appId
     * @param timeout
     * @return
     */
    public Response startConsumer(String appId, long timeout) {
        Response response = new Response ();
        try {
            ConsumerResponse consumerResponse = rtsExecutorService.consumer (appId, timeout);
            response.setConsumeTime (consumerResponse.getConsumeTime ());
            response.setMessage (consumerResponse.getMessage ());
            response.setStatus (consumerResponse.getStatus ().getValue ());
            response.setStatusCode (consumerResponse.getStatusCode ().getValue ());
            response.setReturnColumns (consumerResponse.getColumns ());
            response.setRecords (consumerResponse.getRecords ());
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }
        return response;
    }
}
