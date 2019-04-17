package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.common.util.FTPClientConfig;
import com.hex.bigdata.udsp.common.util.StatementUtil;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.util.Util;
import com.hex.bigdata.udsp.im.converter.impl.util.JdbcUtil;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponse;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponseFetch;
import com.hex.bigdata.udsp.olq.service.OlqProviderService;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步联机查询的服务
 */
@Service
public class OlqSyncService {
    private static Logger logger = LoggerFactory.getLogger (OlqSyncService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool (new ThreadFactory () {
        private AtomicInteger id = new AtomicInteger (0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread (r);
            thread.setName ("olq-service-" + id.addAndGet (1));
            return thread;
        }
    });

    static {
        FTPClientConfig.loadConf ("goframe/udsp/udsp.config.properties");
    }

    @Autowired
    private OlqProviderService olqProviderService;
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
            final String consumeId = request.getConsumeId ();
            RcUserService rcUserService = consumeRequest.getRcUserService ();
            if (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout () == 0) { // 不开启超时
                response = syncStart (consumeId, request.getAppId (), request.getSql (), request.getPage ());
            } else { // 开启一个新的线程，其内部执行联机查询任务，执行成功时或者执行超时时向下走
                Future<Response> futureTask = executorService.submit (new Callable () {
                    @Override
                    public Response call() throws Exception {
                        return syncStart (consumeId, request.getAppId (), request.getSql (), request.getPage ());
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
     * 异步运行（添加了超时机制）
     *
     * @param consumeRequest
     * @param fileName
     * @param bef
     */
    public void asyncStartForTimeout(ConsumeRequest consumeRequest, final String fileName, long bef) {
        long runBef = System.currentTimeMillis ();
        Response response = null;
        try {
            final Request request = consumeRequest.getRequest ();
            final String consumeId = request.getConsumeId ();
            RcUserService rcUserService = consumeRequest.getRcUserService ();
            if (rcUserService == null || rcUserService.getMaxAsyncExecuteTimeout () == 0) { // 不开启超时
                response = asyncStart (consumeId, request.getAppId (), request.getSql (), request.getPage (), fileName, request.getUdspUser ());
            } else { // 开启一个新的线程，其内部执行联机查询任务，执行成功时或者执行超时时向下走
                Future<Response> futureTask = executorService.submit (new Callable<Response> () {
                    @Override
                    public Response call() throws Exception {
                        return asyncStart (consumeId, request.getAppId (), request.getSql (), request.getPage (), fileName, request.getUdspUser ());
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
     * @param consumeId
     * @param dsId
     * @param sql
     * @param page
     * @return
     */
    public Response syncStart(String consumeId, String dsId, String sql, Page page) {
        try {
            OlqResponse olqResponse = olqProviderService.select (consumeId, dsId, sql, page);
            Response response = new Response ();
            response.setMessage (olqResponse.getMessage ());
            response.setConsumeTime (olqResponse.getConsumeTime ());
            response.setStatus (olqResponse.getStatus ().getValue ());
            response.setStatusCode (olqResponse.getStatusCode ().getValue ());
            response.setRecords (olqResponse.getRecords ());
            response.setReturnColumns (olqResponse.getColumns ());
            response.setPage (olqResponse.getPage ());
            return response;
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }

    }

    /**
     * 异步运行
     *
     * @param dsId
     * @param sql
     * @return
     */
    public Response asyncStart(String consumeId, String dsId, String sql, Page page, String fileName, String userName) {
        OlqResponseFetch responseFetch = null;
        try {
            responseFetch = olqProviderService.selectFetch (consumeId, dsId, sql, page);
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, e.toString ());
        }
        if (Status.DEFEAT == responseFetch.getStatus ()) {
            return Util.errorResponse (ErrorCode.ERROR_000007, responseFetch.getMessage ());
        }
        Connection conn = responseFetch.getConnection ();
        Statement stmt = responseFetch.getStatement ();
        ResultSet rs = responseFetch.getResultSet ();
        try {
            CreateFileUtil.createDelimiterFile (rs, true, fileName);
        } catch (Exception e) {
            e.printStackTrace ();
            return Util.errorResponse (ErrorCode.ERROR_000007, "生成文件失败！" + e.toString ());
        } finally {
            StatementUtil.removeStatement (consumeId);
            JdbcUtil.close (rs);
            JdbcUtil.close (stmt);
            JdbcUtil.close (conn);
        }
        return Util.uploadFtp (fileName, userName);
    }
}
