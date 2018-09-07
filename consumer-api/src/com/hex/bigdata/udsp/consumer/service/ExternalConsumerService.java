package com.hex.bigdata.udsp.consumer.service;

import com.hex.bigdata.udsp.common.constant.ConsumerEntity;
import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.ConsumerType;
import com.hex.bigdata.udsp.common.constant.RequestType;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.rc.model.RcService;
import com.hex.bigdata.udsp.rc.service.RcServiceService;
import com.hex.goframe.model.MessageResult;
import com.hex.goframe.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消费的服务
 */
@Service
public class ExternalConsumerService {
    private static Logger logger = LogManager.getLogger(ExternalConsumerService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RcServiceService rcServiceService;
    @Autowired
    private ConsumerService consumerService;

    /**
     * 外部请求消费
     *
     * @param request 外部请求内容
     * @return
     */
    public Response externalConsume(Request request) {
        logger.debug("Request=" + JSONUtil.parseObj2JSON(request));
        long bef = System.currentTimeMillis();

        ConsumeRequest consumeRequest = checkBeforExternalConsume(request, bef);
        logger.debug("检查耗时：" + (System.currentTimeMillis() - bef) + "ms");

        Response response = consumerService.consume(consumeRequest, bef);

        long now = System.currentTimeMillis();
        long consumeTime = now - bef;
        response.setConsumeTime(consumeTime);
        logger.debug("请求耗时：" + consumeTime + "ms");

        return response;
    }

    /**
     * 外部消费前检查
     */
    private ConsumeRequest checkBeforExternalConsume(Request request, long bef) {
        ConsumeRequest consumeRequest = new ConsumeRequest();
        consumeRequest.setRequest(request);
        request.setRequestType(RequestType.OUTER.getValue());
        String serviceName = request.getServiceName();
        String udspUser = request.getUdspUser();
        String udspPass = request.getToken();
        String entity = request.getEntity();
        String type = request.getType();
        //外部调用必输参数检查
        if (StringUtils.isBlank(serviceName) || StringUtils.isBlank(udspUser) || StringUtils.isBlank(udspPass)
                || StringUtils.isBlank(entity) || StringUtils.isBlank(type)) {
            consumeRequest.setError(ErrorCode.ERROR_000009);
            return consumeRequest;
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
        //检查用户身份合法性
        MessageResult messageResult = userService.validateUser(udspUser, udspPass);
        if (!messageResult.isStatus()) {
            consumeRequest.setError(ErrorCode.ERROR_000002);
            return consumeRequest;
        }
        //检查授权访问信息
        RcService rcService = rcServiceService.selectByServiceName(serviceName);
        return consumerService.checkBeforConsume(request, udspUser, rcService, bef);
    }
}
