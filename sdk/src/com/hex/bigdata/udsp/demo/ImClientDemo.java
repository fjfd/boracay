package com.hex.bigdata.udsp.demo;

import com.hex.bigdata.udsp.client.factory.ConsumerClientFactory;
import com.hex.bigdata.udsp.client.impl.ImClient;
import com.hex.bigdata.udsp.client.impl.IqClient;
import com.hex.bigdata.udsp.constant.SdkConstant;
import com.hex.bigdata.udsp.constant.StatusCode;
import com.hex.bigdata.udsp.model.Page;
import com.hex.bigdata.udsp.model.request.ImRequest;
import com.hex.bigdata.udsp.model.request.IqRequest;
import com.hex.bigdata.udsp.model.request.StatusRequest;
import com.hex.bigdata.udsp.model.response.pack.AsyncPackResponse;
import com.hex.bigdata.udsp.model.response.pack.StatusPackResponse;
import com.hex.bigdata.udsp.model.response.pack.SyncPackResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 交互建模调用示例
 */
public class ImClientDemo {
    /**
     * 日志记录
     */
    private static Logger logger = LogManager.getLogger(ImClientDemo.class);

    /**
     * 交互建模-同步start接口示例
     */
    public void syncStart() {
        //创建自定义客户端
//        String url = "http://127.0.0.1:8088/udsp/http/consume";
//        ImClient client = ConsumerClientFactory.createCustomClient(ImClient.class, url);
        //创建默认客户端,根据udsp.config.properties配置文件获取地址
        ImClient client = ConsumerClientFactory.createCustomClient(ImClient.class);

        //创建请求实体
        ImRequest request = new ImRequest();
        //基础参数设置-设置调用服务的名称
        request.setServiceName("soa_jyls_app1");
        //基础参数设置-上层应用系统使用者工号
        request.setAppUser("10940");
        //基础参数设置-设置调用start接口
        request.setEntity(SdkConstant.CONSUMER_ENTITY_START);
        //基础参数设置-设置同步调用，同步调用为sync，异步调用为async
        request.setType(SdkConstant.CONSUMER_TYPE_SYNC);
        //基础参数设置-设置UDSP校验用户信息，用户名及token，用户校验信息需UDSP下发
        request.setUdspUser("CRM");
        request.setToken("000000");

        //设置业务参数-查询参数设置
        Map<String, String> data = new HashMap<>();
        data.put("bizDate", "20161231");
        request.setData(data);

        //发起动用
        SyncPackResponse response = client.syncStart(request);

        // 拆包响应对象
        if (response == null) {
            logger.error("客户端异常");
        } else {
            if (StatusCode.SUCCESS == response.getStatusCode()) {
                // 耗时
                logger.debug("耗时：" + response.getConsumeTime());
                // 消费ID
                logger.debug("消费ID：" + response.getConsumeId());
            } else {
                logger.error("状态：" + response.getStatus());
                logger.error("状态码：" + response.getStatusCode());
                logger.error("错误码：" + response.getErrorCode());
                logger.error("错误信息：" + response.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ImClientDemo demo = new ImClientDemo();
        demo.syncStart();
    }
}
