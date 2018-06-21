package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.common.service.InitParamService;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.im.constant.ModelType;
import com.hex.bigdata.udsp.im.converter.model.Model;
import com.hex.bigdata.udsp.im.service.BatchJobService;
import com.hex.bigdata.udsp.im.service.ImModelService;
import com.hex.bigdata.udsp.im.service.RealtimeJobService;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.rc.model.RcUserService;
import com.hex.bigdata.udsp.consumer.thread.ImSyncServiceCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步交互建模的服务
 */
@Service
public class ImSyncService {
    private static Logger logger = LoggerFactory.getLogger(IqSyncService.class);

    private static final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        private AtomicInteger id = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("im-service-" + id.addAndGet(1));
            return thread;
        }
    });

    @Autowired
    private BatchJobService batchJobService;
    @Autowired
    private RealtimeJobService realtimeJobService;
    @Autowired
    private ImModelService imModelService;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private InitParamService initParamService;

    /**
     * 同步运行（添加了超时机制）
     *
     * @param consumeRequest
     * @param bef
     * @return
     */
    public Response startForTimeout(ConsumeRequest consumeRequest, long bef) {
        Request request = consumeRequest.getRequest();
        RcUserService rcUserService = consumeRequest.getRcUserService();
        long maxSyncExecuteTimeout = (rcUserService == null || rcUserService.getMaxSyncExecuteTimeout() == 0) ?
                initParamService.getMaxSyncExecuteTimeout() : rcUserService.getMaxSyncExecuteTimeout();
        Response response = new Response();
        long runBef = System.currentTimeMillis();
        try {
            // 开启一个新的线程，其内部执行交互建模任务，执行成功时或者执行超时时向下走
            Future<Response> futureTask = executorService.submit(new ImSyncServiceCallable(request.getAppId(), request.getData()));
            response = futureTask.get(maxSyncExecuteTimeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000015.getValue(), ErrorCode.ERROR_000015.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, consumeRequest, bef, runBef,
                    ErrorCode.ERROR_000007.getValue(), ErrorCode.ERROR_000007.getName() + ":" + e.toString(), null);
        }
        return response;
    }

    /**
     * 启动
     *
     * @param appId
     * @param data
     * @return
     */
    public Response start(String appId, Map<String, String> data) {
        Response response = new Response();
        try {
            Model model = imModelService.getModel(appId, data);
            if (ModelType.REALTIME == model.getType()) {
                realtimeJobService.start(model);
            } else {
                batchJobService.start(model);
            }
            response.setStatus(Status.SUCCESS.getValue());
            response.setStatusCode(StatusCode.SUCCESS.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage(e.getMessage());
            response.setStatus(Status.DEFEAT.getValue());
            response.setErrorCode(ErrorCode.ERROR_000007.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
        }
        return response;
    }
}
