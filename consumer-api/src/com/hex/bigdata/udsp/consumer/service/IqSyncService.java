package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.EnumTrans;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.common.util.FTPClientConfig;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.util.Util;
import com.hex.bigdata.udsp.iq.model.IqAppQueryCol;
import com.hex.bigdata.udsp.iq.provider.model.IqResponse;
import com.hex.bigdata.udsp.iq.service.IqAppQueryColService;
import com.hex.bigdata.udsp.iq.service.IqProviderService;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步交互查询的服务
 */
@Service
public class IqSyncService {

    private static Logger logger = LoggerFactory.getLogger (IqSyncService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool (new ThreadFactory () {
        private AtomicInteger id = new AtomicInteger (0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread (r);
            thread.setName ("iq-service-" + id.addAndGet (1));
            return thread;
        }
    });

    static {
        FTPClientConfig.loadConf ("goframe/udsp/udsp.config.properties");
    }

    @Autowired
    private IqProviderService iqProviderService;
    @Autowired
    private IqAppQueryColService iqAppQueryColService;
    @Autowired
    private LoggingService loggingService;

    /**
     * 同步运行（添加了超时机制）
     *
     * @param consumeRequest
     * @param bef
     * @return
     */
    public Response syncStartForTimeout(ConsumeRequest consumeRequest, long bef) {
        long runBef = System.currentTimeMillis ();
        Response response = new Response ();
        try {
            final Request request = consumeRequest.getRequest ();
            RcUserService rcUserService = consumeRequest.getRcUserService ();
            if (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout () == 0) { // 不开启超时
                response = syncStart (request.getAppId (), request.getData (), request.getPage ());
            } else { // 开启一个新的线程，其内部执行交互查询任务，执行成功时或者执行超时时向下走
                Future<Response> futureTask = executorService.submit (new Callable () {
                    @Override
                    public Response call() throws Exception {
                        return syncStart (request.getAppId (), request.getData (), request.getPage ());
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

    public void asyncStartForTimeout(ConsumeRequest consumeRequest, final String fileName, long bef) {
        long runBef = System.currentTimeMillis ();
        Response response = null;
        try {
            final Request request = consumeRequest.getRequest ();
            RcUserService rcUserService = consumeRequest.getRcUserService ();
            if (rcUserService == null || rcUserService.getMaxAsyncExecuteTimeout () == 0) { // 不开启超时
                response = asyncStart (request.getAppId (), request.getData (), request.getPage (), fileName, request.getUdspUser ());
            } else { // 开启一个新的线程，其内部执行交互查询任务，执行成功时或者执行超时时向下走
                Future<Response> futureTask = executorService.submit (new Callable<Response> () {
                    @Override
                    public Response call() throws Exception {
                        return asyncStart (request.getAppId (), request.getData (), request.getPage (), fileName, request.getUdspUser ());
                    }
                });
                response = futureTask.get (rcUserService.getMaxAsyncExecuteTimeout (), TimeUnit.SECONDS);
            }
            loggingService.writeResponseLog (request, response, bef, runBef, false);
        } catch (TimeoutException e) {
            loggingService.writeResponseLog (null, consumeRequest, bef, runBef, ErrorCode.ERROR_000015, e.toString ());
        } catch (Exception e) {
            e.printStackTrace ();
            loggingService.writeResponseLog (null, consumeRequest, bef, runBef, ErrorCode.ERROR_000007, e.toString ());
        }
    }

    /**
     * 同步运行
     *
     * @param appId
     * @param paraMap
     * @param page
     * @return
     */
    public Response syncStart(String appId, Map<String, String> paraMap, Page page) {
        return run (appId, paraMap, page);
    }

    /**
     * 异步运行
     *
     * @param appId
     * @param paraMap
     * @param page
     * @return
     */
    public Response asyncStart(String appId, Map<String, String> paraMap, Page page, String fileName, String userName) {
        Response response = run (appId, paraMap, page);
        if (Status.DEFEAT.getValue ().equals (response.getStatus ())) {
            return Util.errorResponse (ErrorCode.ERROR_000007, response.getMessage ());
        }
        try {
            CreateFileUtil.createDelimiterFile (response.getRecords (), true, fileName);
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, "生成文件失败！" + e.toString ());
        }
        return Util.uploadFtp (fileName, userName);
    }

    /**
     * 运行
     *
     * @param appId
     * @param paraMap
     * @param page
     * @return
     */
    private Response run(String appId, Map<String, String> paraMap, Page page) {
        try {
            IqResponse iqResponse = null;
            if (page != null && page.getPageIndex () > 0) {
                iqResponse = iqProviderService.select (appId, paraMap, page);
            } else {
                iqResponse = iqProviderService.select (appId, paraMap);
            }
            Response response = new Response ();
            response.setStatus (iqResponse.getStatus ().getValue ());
            response.setStatusCode (iqResponse.getStatusCode ().getValue ());
            response.setRecords (iqResponse.getRecords ());
            response.setReturnColumns (iqResponse.getColumns ());
            response.setPage (iqResponse.getPage ());
            response.setMessage (iqResponse.getMessage ());
            response.setConsumeTime (iqResponse.getConsumeTime ());
            return response;
        } catch (Exception e) {
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }
    }
}
