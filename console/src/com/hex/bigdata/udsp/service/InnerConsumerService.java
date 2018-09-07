package com.hex.bigdata.udsp.service;

import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.RequestType;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.ConsumerService;
import com.hex.bigdata.udsp.mc.model.Current;
import com.hex.bigdata.udsp.mc.service.RunQueueService;
import com.hex.bigdata.udsp.rc.model.RcService;
import com.hex.bigdata.udsp.rc.service.RcServiceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 消费的服务
 */
@Service
public class InnerConsumerService {
    private static Logger logger = LogManager.getLogger(InnerConsumerService.class);

    @Autowired
    private RcServiceService rcServiceService;
    @Autowired
    private ConsumerService consumerService;
    @Autowired
    private RunQueueService runQueueService;

    /**
     * 管理员用户最大同步并发数
     */
    @Value("${admin.sync.count:100}")
    private Integer adminMaxSyncNum;

    /**
     * 管理员用户最大异步并发数
     */
    @Value("${admin.async.count:100}")
    private Integer adminMaxAsyncNum;

    /**
     * 内部请求消费
     *
     * @param request   内部请求内容
     * @param isAdmin   是否是管理员
     * @return
     */
    public Response innerConsume(Request request, boolean isAdmin) {
        logger.debug("Request=" + JSONUtil.parseObj2JSON(request));
        long bef = System.currentTimeMillis();

        ConsumeRequest consumeRequest = checkBeforInnerConsume(request, isAdmin, bef);
        logger.debug("检查耗时：" + (System.currentTimeMillis() - bef) + "ms");

        Response response = consumerService.consume(consumeRequest, bef);

        if (response.getPage() != null && response.getPage().getPageIndex() >= 1) {
            response.getPage().setPageIndex(response.getPage().getPageIndex() - 1);
        }

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);
        logger.debug("请求耗时：" + consumeTime + "ms");

        return response;
    }

    /**
     * 内部消费前检查
     */
    private ConsumeRequest checkBeforInnerConsume(Request request, boolean isAdmin, long bef) {
        ConsumeRequest consumeRequest = new ConsumeRequest();
        consumeRequest.setRequest(request);
        request.setRequestType(RequestType.INNER.getValue());
        String udspUser = request.getUdspUser();
        String appType = request.getAppType();
        String appId = request.getAppId();
        String appName = consumerService.getAppName(appType, appId);
        request.setAppName(appName);
        request.setAppUser(udspUser);
        Current mcCurrent = null;
        String type = request.getType() == null ? "" : request.getType().toUpperCase();
        String entity = request.getEntity() == null ? "" : request.getEntity().toUpperCase();
        // 分页参数处理
        if (request.getPage() != null && request.getPage().getPageIndex() >= 0) {
            request.getPage().setPageIndex(request.getPage().getPageIndex() + 1);
        }
        //消费前公共输入参数检查
        //异同步类型检查和entity类型检查
        if (!(
                ConsumerType.SYNC.getValue().equalsIgnoreCase(type)
                        || ConsumerType.ASYNC.getValue().equalsIgnoreCase(type)
        ) || !(
                ConsumerEntity.STATUS.getValue().equalsIgnoreCase(entity)
                        || ConsumerEntity.START.getValue().equalsIgnoreCase(entity)
                        || ConsumerEntity.STOP.getValue().equalsIgnoreCase(entity)
        )) {
            consumeRequest.setError(ErrorCode.ERROR_000010);
            return consumeRequest;
        }
        // 管理员用户，直接访问
        if (isAdmin) {
            mcCurrent = consumerService.getCurrent(request, adminMaxSyncNum, adminMaxAsyncNum);
            if (!runQueueService.addCurrent(mcCurrent)) { // 队列已满
                consumeRequest.setError(ErrorCode.ERROR_000003);
                return consumeRequest;
            }
            consumeRequest.setMcCurrent(mcCurrent);
            consumeRequest.setRequest(request);
            return consumeRequest;
        }

        // 非管理员用户，授权访问
        RcService rcService = rcServiceService.selectByAppTypeAndAppId(appType, appId);
        return consumerService.checkBeforConsume(request, udspUser, rcService, bef);
    }

}
