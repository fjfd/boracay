package com.hex.bigdata.udsp.controller;

import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.consumer.model.ExternalRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PC on 2017/5/2.
 */
public class SocketControllerTest {

    public void socketGetMassgae(String requestString){
        try{
            Socket socket = new Socket("10.1.40.127",1088);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(requestString);
            bw.flush();
            socket.shutdownOutput();
            BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            String getMassage = "";
            StringBuffer massage = new StringBuffer();
            while ((getMassage = br.readLine()) != null){
                massage.append(getMassage);
            }
            System.out.println("接收到的内容为："+ massage);
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void httpJsonTest_iqSyncStart(){
        ExternalRequest externalRequest=new ExternalRequest();
        externalRequest.setServiceName("smart01");
        externalRequest.setAppUser("CORE");
        externalRequest.setEntity("start");
        externalRequest.setType("sync");

        externalRequest.setUdspUser("admin");
        externalRequest.setToken("000000");
        externalRequest.setAppUser("tomnic");


        Map<String, String> data=new HashMap<>();
        data.put("client_no","1113829408");
        externalRequest.setData(data);

       new SocketControllerTest().socketGetMassgae(JSONUtil.parseObj2JSON(externalRequest));
    }


    /**
     * 交互查询异步start
     */
    public void httpJsonTest_iqAsyncStart(){
        ExternalRequest externalRequest=new ExternalRequest();
        externalRequest.setServiceName("smart01");
        externalRequest.setAppUser("CORE");
        externalRequest.setEntity("start");
        externalRequest.setType("async");

        externalRequest.setUdspUser("admin");
        externalRequest.setToken("000000");
        externalRequest.setAppUser("tomnic");


        Map<String, String> data=new HashMap<>();
        data.put("client_no","1113829408");
        externalRequest.setData(data);

        new SocketControllerTest().socketGetMassgae(JSONUtil.parseObj2JSON(externalRequest));

    }

    /**
     * 交互查询异步status
     */
    public void httpJsonTest_iqAsyncStatus(){
        ExternalRequest externalRequest=new ExternalRequest();
        externalRequest.setServiceName("smart01");
        externalRequest.setAppUser("CORE");
        externalRequest.setEntity("status");
        externalRequest.setType("async");

        externalRequest.setUdspUser("admin");
        externalRequest.setToken("000000");
        externalRequest.setAppUser("tomnic");
        externalRequest.setConsumeId("0194fc2e13f8cec55c3a7c06951136e6_fb77a5eb9afe40fbb2fbf88ea493d622");
        new SocketControllerTest().socketGetMassgae(JSONUtil.parseObj2JSON(externalRequest));
    }



    /**
     * 联机查询同步start
     */
    public void httpJsonTest_olpSyncStart(){
        ExternalRequest externalRequest=new ExternalRequest();
        externalRequest.setServiceName("core02");
        externalRequest.setAppUser("CORE");
        externalRequest.setEntity("START");
        externalRequest.setType("SYNC");

        externalRequest.setUdspUser("admin");
        externalRequest.setToken("000000");
        externalRequest.setAppUser("tomnic");
        externalRequest.setSql("select * from test_wang limit 10");
        new SocketControllerTest().socketGetMassgae(JSONUtil.parseObj2JSON(externalRequest));
    }

    /**
     * 联机查询异步start
     */
    public void httpJsonTest_olpAsyncStart(){
        ExternalRequest externalRequest=new ExternalRequest();
        externalRequest.setServiceName("core02");
        externalRequest.setAppUser("CORE");
        externalRequest.setEntity("START");
        externalRequest.setType("ASYNC");

        externalRequest.setUdspUser("admin");
        externalRequest.setToken("000000");
        externalRequest.setAppUser("tomnic");
        externalRequest.setSql("select * from test_wang limit 10");

        new SocketControllerTest().socketGetMassgae(JSONUtil.parseObj2JSON(externalRequest));
    }

    /**
     *  联机查询异步status
     */
    public void httpJsonTest_olpAsyncStatus(){
        ExternalRequest externalRequest=new ExternalRequest();
        externalRequest.setServiceName("core02");
        externalRequest.setAppUser("CORE");
        externalRequest.setEntity("STATUS");
        externalRequest.setType("ASYNC");

        externalRequest.setUdspUser("admin");
        externalRequest.setToken("000000");
        externalRequest.setAppUser("tomnic");
        externalRequest.setConsumeId("3521d29bedc0e063a7bc2e7a03f8f690_85b2b4d62eaa419e8bc36bd5d5f062ab");
        new SocketControllerTest().socketGetMassgae(JSONUtil.parseObj2JSON(externalRequest));

    }

    public  static  void  main(String[] args){
        SocketControllerTest test= new SocketControllerTest();
        //联机查询异步start
        test.httpJsonTest_olpAsyncStart();
        //联机查询异步status
        //test.httpJsonTest_olpAsyncStatus();
        //联机查询同步start
       // test.httpJsonTest_olpSyncStart();

        //交互查询同步start
        //test.httpJsonTest_iqSyncStart();
        //交互查询异步start
        //test.httpJsonTest_iqAsyncStart();
        //交互查询异步status
        //test.httpJsonTest_iqAsyncStatus();
    }
}
