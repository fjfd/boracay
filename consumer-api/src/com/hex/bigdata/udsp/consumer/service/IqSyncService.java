package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.EnumTrans;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.common.util.CreateFileUtil;
import com.hex.bigdata.udsp.common.util.FTPClientConfig;
import com.hex.bigdata.udsp.common.util.FTPHelper;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.iq.model.IqAppQueryCol;
import com.hex.bigdata.udsp.iq.provider.model.IqResponse;
import com.hex.bigdata.udsp.iq.service.IqAppQueryColService;
import com.hex.bigdata.udsp.iq.service.IqProviderService;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步交互查询的服务
 */
@Service
public class IqSyncService {

    private static Logger logger = LoggerFactory.getLogger(IqSyncService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        private AtomicInteger id = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("iq-service-" + id.addAndGet(1));
            return thread;
        }
    });

    static {
        FTPClientConfig.loadConf("goframe/udsp/udsp.config.properties");
    }

    @Autowired
    private IqProviderService iqProviderService;
    @Autowired
    private IqAppQueryColService iqAppQueryColService;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private InitParamService initParamService;
    @Autowired
    private IqSyncService iqSyncService;

    /**
     * 同步运行（添加了超时机制）
     *
     * @param consumeRequest
     * @param bef
     * @return
     */
    public Response syncStartForTimeout(ConsumeRequest consumeRequest, long bef) {
        long runBef = System.currentTimeMillis();
        Response response = new Response();
        try {
            final Request request = consumeRequest.getRequest();
            RcUserService rcUserService = consumeRequest.getRcUserService();
            long maxSyncExecuteTimeout = (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout() == 0) ?
                    initParamService.getMaxSyncExecuteTimeout() : rcUserService.getMaxSyncExecuteTimeout();
            // 开启一个新的线程，其内部执行交互查询任务，执行成功时或者执行超时时向下走
            Future<Response> futureTask = executorService.submit(new Callable() {
                @Override
                public Response call() throws Exception {
                    return iqSyncService.syncStart(request.getAppId(), request.getData(), request.getPage());
                }
            });
            response = futureTask.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName() + ":" + e.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), null);
        }
        return response;
    }

    public void asyncStartForTimeout(ConsumeRequest consumeRequest, final String fileName, long bef) {
        long runBef = System.currentTimeMillis();
        try {
            final Request request = consumeRequest.getRequest();
            Current mcCurrent = consumeRequest.getMcCurrent();
            String consumeId = (StringUtils.isNotBlank(request.getConsumeId()) ? request.getConsumeId() : mcCurrent.getPkId());
            final String userName = consumeRequest.getMcCurrent().getUserName();
            RcUserService rcUserService = consumeRequest.getRcUserService();
            long maxAsyncExecuteTimeout = (rcUserService == null || rcUserService.getMaxAsyncExecuteTimeout() == 0) ?
                    initParamService.getMaxAsyncExecuteTimeout() : rcUserService.getMaxAsyncExecuteTimeout();
            // 开启一个新的线程，其内部执行交互查询任务，执行成功时或者执行超时时向下走
            Future<Response> futureTask = executorService.submit(new Callable<Response>() {
                @Override
                public Response call() throws Exception {
                    return iqSyncService.asyncStart(request.getAppId(), request.getData(), request.getPage(), fileName, userName);
                }
            });
            Response response = futureTask.get(maxAsyncExecuteTimeout, TimeUnit.SECONDS);
            loggingService.writeResponseLog(consumeId, bef, runBef, request, response, false);
        } catch (TimeoutException e) {
            loggingService.writeResponseLog(null, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName() + ":" + e.toString(), null);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(null, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), null);
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
        Response response = new Response();
        try {
            response = run(appId, paraMap, page);
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            response.setStatus(Status.DEFEAT.getValue());
            response.setErrorCode(ErrorCode.ERROR_000007.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
        }
        return response;
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
        Response response = new Response();
        try {
            checkParam(appId, paraMap);
        } catch (Exception e) {
            response.setStatus(Status.DEFEAT.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
            response.setMessage(ErrorCode.ERROR_000009.getName() + ":" + e.toString());
            return response;
        }
        IqResponse iqResponse = null;
        if (page != null && page.getPageIndex() > 0) {
            iqResponse = iqProviderService.select(appId, paraMap, page);
        } else {
            iqResponse = iqProviderService.select(appId, paraMap);
        }
        response.setPage(iqResponse.getPage());
        response.setMessage(iqResponse.getMessage());
        response.setConsumeTime(iqResponse.getConsumeTime());
        response.setStatus(iqResponse.getStatus().getValue());
        response.setStatusCode(iqResponse.getStatusCode().getValue());
        response.setRecords(iqResponse.getRecords());
        response.setReturnColumns(iqResponse.getColumns());
        return response;
    }

    /**
     * 检查输入的参数
     */
    private void checkParam(String appId, Map<String, String> paraMap) throws Exception {
        if (paraMap != null && paraMap.size() != 0) {
            boolean isError = false;
            String message = "";
            int count = 0;
            for (IqAppQueryCol iqAppQueryCol : iqAppQueryColService.selectByAppId(appId)) {
                if (EnumTrans.transTrue(iqAppQueryCol.getIsNeed())) {
                    String name = iqAppQueryCol.getLabel();
                    String value = paraMap.get(name);
                    if (StringUtils.isBlank(value)) { // 没有传入值
                        message += (count == 0 ? "" : ", ") + name;
                        isError = true;
                        count++;
                    }
                }
            }
            if (isError) {
                throw new Exception(message + "参数不能为空!");
            }
        }
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
        Status status = Status.SUCCESS;
        StatusCode statusCode = StatusCode.SUCCESS;
        String message = "成功";
        String filePath = "";
        Response response = run(appId, paraMap, page);
        if (Status.SUCCESS.getValue().equals(response.getStatus())) {
            List<Map<String, String>> records = response.getRecords();
            // 写数据文件和标记文件到本地，并上传至FTP服务器
            CreateFileUtil.createDelimiterFile(records, true, fileName);
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
        response.setResponseContent(filePath);
        response.setStatus(status.getValue());
        response.setStatusCode(statusCode.getValue());
        response.setMessage(message);
        return response;
    }

}
