package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.util.Util;
import com.hex.bigdata.udsp.dsl.DslSqlAdaptor;
import com.hex.bigdata.udsp.dsl.model.DslRequest;
import com.hex.bigdata.udsp.iq.provider.model.dsl.IqDslResponse;
import com.hex.bigdata.udsp.iq.service.IqProviderService;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步交互查询自定义SQL的服务
 */
@Service
public class IqDslSyncService {

    private static Logger logger = LoggerFactory.getLogger (IqDslSyncService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool (new ThreadFactory () {
        private AtomicInteger id = new AtomicInteger (0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread (r);
            thread.setName ("iq-dsl-service-" + id.addAndGet (1));
            return thread;
        }
    });

    @Autowired
    private IqProviderService iqProviderService;
    @Autowired
    private LoggingService loggingService;

    /**
     * 运行（添加了超时机制）
     *
     * @return
     */
    public Response startForTimeout(ConsumeRequest consumeRequest, long bef) {
        long runBef = System.currentTimeMillis ();
        Response response = new Response ();
        try {
            final Request request = consumeRequest.getRequest ();
            RcUserService rcUserService = consumeRequest.getRcUserService ();
            if (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout () == 0) { // 不开启超时
                response = run (request.getAppId (), request.getSql ());
            } else { // 开启一个新的线程，其内部执行交互查询任务，执行成功时或者执行超时时向下走
                Future<Response> futureTask = executorService.submit (new Callable () {
                    @Override
                    public Response call() throws Exception {
                        return run (request.getAppId (), request.getSql ());
                    }
                });
                response = futureTask.get (rcUserService.getMaxSyncExecuteTimeout (), TimeUnit.SECONDS);
            }
        } catch (TimeoutException e) {
            loggingService.writeResponseLog (response, consumeRequest, bef, runBef, ErrorCode.ERROR_000015, e.toString ());
        } catch (Exception e) {
            e.printStackTrace ();
            loggingService.writeResponseLog (response, consumeRequest, bef, runBef, ErrorCode.ERROR_000007, e.toString ());
        }
        return response;
    }

    /**
     * 运行
     *
     * @param mdId
     * @param sql
     * @return
     */
    public Response run(String mdId, String sql) {
        try {
            DslRequest dslRequest = DslSqlAdaptor.selectSqlToDslRequest (sql);
            IqDslResponse iqDslResponse = iqProviderService.select (mdId, dslRequest);
            Response response = new Response ();
            response.setStatus (iqDslResponse.getStatus ().getValue ());
            response.setStatusCode (iqDslResponse.getStatusCode ().getValue ());
            response.setRecords (iqDslResponse.getDslResponse ().getRecords ());
            response.setReturnColumns (iqDslResponse.getDslResponse ().getColumns ());
            response.setMessage (iqDslResponse.getMessage ());
            response.setConsumeTime (iqDslResponse.getConsumeTime ());
            return response;
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }
    }
}
