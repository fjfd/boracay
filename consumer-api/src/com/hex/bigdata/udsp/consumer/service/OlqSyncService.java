package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.common.util.*;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.service.RunQueueService;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponse;
import com.hex.bigdata.udsp.olq.provider.model.OlqResponseFetch;
import com.hex.bigdata.udsp.olq.service.OlqProviderService;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import com.hex.bigdata.udsp.consumer.thread.OlqAsyncCallable;
import com.hex.bigdata.udsp.consumer.thread.OlqSyncServiceCallable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步联机查询的服务
 */
@Service
public class OlqSyncService {
    private static Logger logger = LoggerFactory.getLogger(OlqSyncService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        private AtomicInteger id = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("olq-service-" + id.addAndGet(1));
            return thread;
        }
    });

    static {
        FTPClientConfig.loadConf("goframe/udsp/udsp.config.properties");
    }

    @Autowired
    private OlqProviderService olqProviderService;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private InitParamService initParamService;
    @Autowired
    private RunQueueService runQueueService;

    /**
     * 同步运行（添加了超时机制）
     *
     * @param consumeRequest
     * @param appId
     * @param sql
     * @param bef
     * @return
     */
    public Response syncStartForTimeout(ConsumeRequest consumeRequest, String appId, String sql, long bef) {
        Request request = consumeRequest.getRequest();
        Current mcCurrent = consumeRequest.getMcCurrent();
        String consumeId = mcCurrent.getPkId();
        RcUserService rcUserService = consumeRequest.getRcUserService();
        long maxSyncExecuteTimeout = (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout() == 0) ?
                initParamService.getMaxSyncExecuteTimeout() : rcUserService.getMaxSyncExecuteTimeout();
        Response response = new Response();
        long runBef = System.currentTimeMillis();
        try {
            // 开启一个新的线程，其内部执行联机查询任务，执行成功时或者执行超时时向下走
            Future<Response> futureTask = executorService.submit(new OlqSyncServiceCallable(consumeId, appId, sql, request.getPage()));
            response = futureTask.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName() + ":" + e.toString(), consumeId);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
        }
        return response;
    }

    public void asyncStartForTimeout(ConsumeRequest consumeRequest, long bef,
                                     String appId, String sql, Page page, String fileName) {

        Current mcCurrent = consumeRequest.getMcCurrent();
        String consumeId = mcCurrent.getPkId();
        String userName = consumeRequest.getMcCurrent().getUserName();
        Request request = consumeRequest.getRequest();
        RcUserService rcUserService = consumeRequest.getRcUserService();
        long maxAsyncExecuteTimeout = (rcUserService == null || rcUserService.getMaxAsyncExecuteTimeout() == 0) ?
                initParamService.getMaxAsyncExecuteTimeout() : rcUserService.getMaxAsyncExecuteTimeout();
        Response response = new Response();
        long runBef = System.currentTimeMillis();
        try {
            // 开启一个新的线程，其内部执行联机查询任务，执行成功时或者执行超时时向下走
            Future<OlqResponse> olqFutureTask = executorService.submit(new OlqAsyncCallable(consumeId, userName, appId, sql, page, fileName));
            OlqResponse olqResponse = olqFutureTask.get(maxAsyncExecuteTimeout, TimeUnit.SECONDS);
            response.setResponseContent(olqResponse.getFilePath());
            loggingService.writeResponseLog(consumeId, bef, runBef, request, response);
        } catch (TimeoutException e) {
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName() + ":" + e.toString(), consumeId);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), consumeId);
        } finally {
            runQueueService.reduceCurrent(mcCurrent);
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
        Response response = checkResponseParam(sql);
        if (response != null) return response;

        response = new Response();
        try {
            OlqResponse olqResponse = olqProviderService.select(consumeId, dsId, sql, page);
            response.setMessage(olqResponse.getMessage());
            response.setConsumeTime(olqResponse.getConsumeTime());
            response.setStatus(olqResponse.getStatus().getValue());
            response.setStatusCode(olqResponse.getStatusCode().getValue());
            response.setRecords(olqResponse.getRecords());
            response.setReturnColumns(olqResponse.getColumns());
            response.setPage(olqResponse.getPage());
        } catch (Exception e) {
            logger.error(ExceptionUtil.getMessage(e));
            e.printStackTrace();
            response.setMessage(ErrorCode.ERROR_000007.getName() + "：" + e.getMessage());
            response.setStatus(Status.DEFEAT.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
            response.setErrorCode(ErrorCode.ERROR_000007.getValue());
        }
        return response;
    }

    private Response checkResponseParam(String sql) {
        Response response = null;
        if (StringUtils.isBlank(sql)) {
            response = new Response();
            response.setStatus(Status.DEFEAT.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
            response.setErrorCode(ErrorCode.ERROR_000009.getValue());
            response.setMessage(ErrorCode.ERROR_000009.getName());
        }
        return response;
    }

    private OlqResponse checkOlqResponseParam(String sql) {
        OlqResponse response = null;
        if (StringUtils.isBlank(sql)) {
            response = new OlqResponse();
            response.setStatus(Status.DEFEAT);
            response.setStatusCode(StatusCode.DEFEAT);
            response.setMessage(ErrorCode.ERROR_000009.getName());
        }
        return response;
    }

    /**
     * 异步运行
     *
     * @param dsId
     * @param sql
     * @return
     */
    public OlqResponse asyncStart(String consumeId, String dsId, String sql, Page page, String fileName, String userName) {
        OlqResponse response = checkOlqResponseParam(sql);
        if (response != null) return response;

        Status status = Status.SUCCESS;
        StatusCode statusCode = StatusCode.SUCCESS;
        String message = "成功";
        String filePath = "";
        OlqResponseFetch responseFetch = olqProviderService.selectFetch(consumeId, dsId, sql, page);
        Connection conn = responseFetch.getConnection();
        Statement stmt = responseFetch.getStatement();
        ResultSet rs = responseFetch.getResultSet();
        try {
            if (Status.SUCCESS == responseFetch.getStatus()) {
                // 写数据文件和标记文件到本地，并上传至FTP服务器
                CreateFileUtil.createDelimiterFile(rs, true, fileName);
                String dataFileName = CreateFileUtil.getDataFileName(fileName);
                String flgFileName = CreateFileUtil.getFlgFileName(fileName);
                String localDataFilePath = CreateFileUtil.getLocalDataFilePath(fileName);
                String localFlgFilePath = CreateFileUtil.getLocalFlgFilePath(fileName);
                String ftpFileDir = CreateFileUtil.getFtpFileDir(userName);
                String ftpDataFilePath = ftpFileDir + "/" + dataFileName;
                FTPHelper ftpHelper = new FTPHelper();
                try {
                    ftpHelper.connectFTPServer();
                    ftpHelper.uploadFile(localDataFilePath, dataFileName, ftpFileDir);
                    ftpHelper.uploadFile(localFlgFilePath, flgFileName, ftpFileDir);
                    //filePath = "ftp://" + FTPClientConfig.getHostname() + ":" + FTPClientConfig.getPort() + ftpFilePath;
                    filePath = ftpDataFilePath;
                    message = localDataFilePath;
                } catch (Exception e) {
                    status = Status.DEFEAT;
                    statusCode = StatusCode.DEFEAT;
                    message = "FTP上传失败！" + e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        ftpHelper.closeFTPClient();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                status = Status.DEFEAT;
                statusCode = StatusCode.DEFEAT;
                message = response.getMessage();
            }
        } catch (Exception e) {
            status = Status.DEFEAT;
            statusCode = StatusCode.DEFEAT;
            message = e.getMessage();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            StatementUtil.removeStatement(consumeId);
        }

        response = new OlqResponse();
        response.setFilePath(filePath);
        response.setMessage(message);
        response.setStatus(status);
        response.setStatusCode(statusCode);

        return response;
    }

}
