package com.hex.bigdata.udsp.consumer;

import com.hex.bigdata.udsp.common.constant.ErrorCode;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.constant.ConsumerConstant;
import com.hex.bigdata.udsp.consumer.model.ConsumeRequest;
import com.hex.bigdata.udsp.consumer.model.ExternalRequest;
import com.hex.bigdata.udsp.consumer.model.Response;
import com.hex.bigdata.udsp.consumer.service.ExternalConsumerService;
import com.hex.bigdata.udsp.consumer.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 2017/5/2.
 */
public class SocketRunnable implements Runnable {

    private Logger log = LoggerFactory.getLogger(SocketRunnable.class);

    private Socket server;

    private ExternalConsumerService consumerService;
    private LoggingService loggingService;

    public SocketRunnable(Socket server, ExternalConsumerService consumerService, LoggingService loggingService) {
        this.server = server;
        this.consumerService = consumerService;
        this.loggingService = loggingService;
    }

    public SocketRunnable() {
    }

    @Override
    public void run() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(server.getInputStream()));

            String getMassage = "";
            StringBuffer massage = new StringBuffer();
            while ((getMassage = br.readLine()) != null) {
                massage.append(getMassage);
            }
            log.info("socket接收到内容为：" + massage.toString());
            String reponseString = consume(massage.toString(), server.getInetAddress().getHostAddress());

            bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream(), "UTF-8"));
            bw.write(reponseString);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (server != null) {
                    server.close();
                }
                if (br != null) {
                    br.close();
                }
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String consume(String massage, String requestIp) {
        Response response = new Response();
        long bef = System.currentTimeMillis();
        try {
            Map<String, Class> classMap = new HashMap<String, Class>();
            classMap.put(ConsumerConstant.CONSUME_RTS_DATASTREAM, Map.class);
            ExternalRequest externalRequest = JSONUtil.parseJSON2Obj(massage, ExternalRequest.class, classMap);
            externalRequest.setRequestIp(requestIp);
            response = consumerService.externalConsume(externalRequest);
        } catch (Exception e) {
            e.printStackTrace();
            loggingService.writeResponseLog(response, new ConsumeRequest(), bef, 0,
                    ErrorCode.ERROR_000005.getValue(), ErrorCode.ERROR_000005.getName() + ":" + e.getMessage(), null);
        }
        return JSONUtil.parseObj2JSON(response);
    }

}
