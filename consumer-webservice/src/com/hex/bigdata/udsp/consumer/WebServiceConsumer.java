package com.hex.bigdata.udsp.consumer;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.common.util.ObjectUtil;
import com.hex.bigdata.udsp.consumer.constant.ConsumerConstant;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.ExternalRequest;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.ExternalConsumerService;
import com.hex.bigdata.udsp.consumer.service.LoggingService;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * WebService Controller
 */
@Component
@WebService(serviceName = "ws", endpointInterface = "com.hex.bigdata.udsp.consumer.WebServiceInterface")
public class WebServiceConsumer implements WebServiceInterface {
    private static Logger logger = LogManager.getLogger(WebServiceConsumer.class);

    // 有问题无效
//    @Resource(name = "org.apache.cxf.jaxws.context.WebServiceContextImpl")
//    private WebServiceContext context;

    @Autowired
    private ExternalConsumerService consumerService;
    @Autowired
    private LoggingService loggingService;

    /**
     * 欢迎信息
     */
    @Override
    public String welcome() {
        return "Welcome to use UDSP consuming webService service!";
    }

    /**
     * 外部请求
     * <p>
     * 这里用WSRequest对象是因为WebService客户端可以通过WSDL生成WSRequest等JavaBean。
     */
    @Override
    public WSResponse consume(@RequestBody WSRequest wsRequest) {
        ExternalRequest externalRequest = new ExternalRequest();
        ObjectUtil.copyObject(wsRequest, externalRequest);

        //获取并设置客户端请求的IP
        externalRequest.setRequestIp(getClientIp());
        Response response = consumerService.externalConsume(externalRequest);

        WSResponse wsResponse = new WSResponse();
        ObjectUtil.copyObject(response, wsResponse);

        return wsResponse;
    }

    /**
     * 外部请求
     * <p>
     * 这里用json字符串作为请求参数是为了自己转换成Request，出错时把信息返回给请求方。
     */
    public String consumeJson(@RequestBody String json) {
        Response response = new Response();
        long bef = System.currentTimeMillis();
        try {
            ExternalRequest externalRequest = jsonToRequest(json);
            externalRequest.setRequestIp(getClientIp()); //获取并设置客户端请求的IP
            response = consumerService.externalConsume(externalRequest);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, new ConsumeRequest(), bef, 0,
                    ErrorCode.ERROR_000005.getValue(), e.getMessage(), null);
        }
        return JSONUtil.parseObj2JSON(response);
    }

//    private String getClientIp() {
//        MessageContext mc = context.getMessageContext();
//        HttpServletRequest request = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
//        String clientIp = request.getRemoteAddr();
//        logger.debug("client IP: " + clientIp);
//        return clientIp;
//    }

    private String getClientIp() {
        Message message = PhaseInterceptorChain.getCurrentMessage();
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        String clientIp = request.getRemoteAddr();
        logger.debug("client IP: " + clientIp);
        return clientIp;
    }

    private ExternalRequest jsonToRequest(String json) {
        Map<String, Class> classMap = new HashMap<String, Class>();
        classMap.put(ConsumerConstant.CONSUME_RTS_DATASTREAM, Map.class);
        return JSONUtil.parseJSON2Obj(json, ExternalRequest.class, classMap);
    }

}