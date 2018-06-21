package com.hex.bigdata.udsp.aop;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.consumer.constant.ConsumerConstant;
import com.hex.bigdata.udsp.consumer.model.Request;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.dao.ConsumeStatusMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查看消费状态的拦截
 * Created by PC on 2017/5/25.
 */
@Component
@Aspect
public class ConsumeStatusInterceptor {
    @Autowired
    private ConsumeStatusMapper consumeStatusMapper;

    @Pointcut("execution(* com.hex.bigdata.udsp.consumer.service.ExternalConsumerService.externalConsume(..))")
    private void externalConsume() {
    }

    @Pointcut("execution(* com.hex.bigdata.udsp.service.InnerConsumerService.innerConsume(..))")
    private void innerConsume() {
    }

    @Around("externalConsume() || innerConsume()")
    public Response checkTimes(JoinPoint joinPoint) {
        Response response = null;
        try {
            Object[] objects = joinPoint.getArgs();
            for (Object object : objects) {
                if (object instanceof Request) {
                    String consumeId = ((Request) object).getConsumeId();
                    String entity = ((Request) object).getEntity();
                    if (ConsumerConstant.CONSUMER_ENTITY_STATUS.equalsIgnoreCase(entity)
                            && StringUtils.isNotBlank(consumeId)) {
                        response = getResponse(joinPoint, consumeId);
                        break;
                    } else {
                        response = (Response) ((ProceedingJoinPoint) joinPoint).proceed();
                    }
                }
            }
        } catch (Throwable e) {
            response = new Response();
            response.setStatus(Status.DEFEAT.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
            response.setMessage(ErrorCode.ERROR_000007.getName() + "：" + e.getMessage());
            response.setErrorCode(ErrorCode.ERROR_000007.getValue());
            response.setConsumeId("");
            response.setConsumeTime(0);
            e.printStackTrace();
        }
        return response;
    }

    private Response getResponse(JoinPoint joinPoint, String consumeId) throws Throwable {
        Response response = null;
        if (consumeStatusMapper.checkConsumeStatusTime(consumeId)) { // 放行
            consumeStatusMapper.insertConsumeStatusTime(consumeId);
            response = (Response) ((ProceedingJoinPoint) joinPoint).proceed();
        } else { // 不放行
            response = new Response();
            response.setStatus(Status.DEFEAT.getValue());
            response.setStatusCode(StatusCode.DEFEAT.getValue());
            response.setMessage(ErrorCode.ERROR_000012.getName());
            response.setErrorCode(ErrorCode.ERROR_000012.getValue());
            response.setConsumeId(consumeId);
            response.setConsumeTime(0);
        }
        return response;
    }
}
