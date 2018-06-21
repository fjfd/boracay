package com.hex.bigdata.udsp.consumer;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.util.FTPClientConfig;
import com.hex.bigdata.udsp.common.util.HostUtil;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.constant.ConsumerConstant;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.ExternalRequest;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.ExternalConsumerService;
import com.hex.bigdata.udsp.consumer.service.LoggingService;
import com.hex.goframe.controller.BaseController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JunjieM on 2018-6-20.
 */
@RequestMapping("/http")
@Controller
public class HttpConsumer extends BaseController {
    private static Logger logger = LogManager.getLogger(HttpConsumer.class);

    static {
        FTPClientConfig.loadConf("goframe/udsp/udsp.config.properties");
    }

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private ExternalConsumerService consumerService;

    /**
     * 欢迎信息
     */
    @RequestMapping(value = {"/welcome"}, method = {RequestMethod.GET})
    @ResponseBody
    public String welcome() {
        return "Welcome to use UDSP consuming http service!";
    }

    /**
     * 外部请求
     * <p/>
     * 这里用json字符串作为请求参数是为了自己转换成Request，出错时把信息返回给请求方。
     */
    @RequestMapping(value = {"/consume"}, method = {RequestMethod.POST})
    @ResponseBody
    public Response consume(@RequestBody String json, HttpServletRequest request) {
        Response response = new Response();
        long bef = System.currentTimeMillis();
        try {
            ExternalRequest externalRequest = jsonToRequest(json);
            externalRequest.setRequestIp(HostUtil.getRealRequestIp(request)); // 获取并设置客户端请求的IP
            response = consumerService.externalConsume(externalRequest);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, new ConsumeRequest(), bef, 0,
                    ErrorCode.ERROR_000005.getValue(), ErrorCode.ERROR_000005.getName() + ":" + e.getMessage(), null);
        }
        return response;
    }

    private ExternalRequest jsonToRequest(String json) {
        Map<String, Class> classMap = new HashMap<String, Class>();
        classMap.put(ConsumerConstant.CONSUME_RTS_DATASTREAM, Map.class);
        return JSONUtil.parseJSON2Obj(json, ExternalRequest.class, classMap);
    }
}
