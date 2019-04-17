package com.hex.bigdata.udsp.mm.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hex.bigdata.udsp.common.util.JSONUtil;
import com.hex.bigdata.udsp.mm.provider.model.MmRequest;
import com.hex.bigdata.udsp.mm.provider.model.MmResponse;
import com.hex.bigdata.udsp.mm.provider.model.MmResponseData;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * 远程调用服务接口
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/5/3
 * TIME:9:32
 */
@Service
public class MmProviderService {

    private static Logger logger = LogManager.getLogger (MmProviderService.class);

    private static final String APPLICATION_JSON = "application/json";

    private static final String CONTEXT_TYPE_TEXT_JSON = "text/json";

    /**
     * 请求调用模型接口
     *
     * @param mmRequest
     * @param url
     */
    public MmResponse request(MmRequest mmRequest, String url) throws Exception {
        JSONObject jsonObject = (JSONObject) JSON.toJSON (mmRequest);
        logger.info ("请求地址：" + url);
        logger.info ("请求参数：" + jsonObject.toJSONString ());
        DefaultHttpClient httpClient = new DefaultHttpClient ();
        HttpPost httpPost = new HttpPost (url);
        httpPost.addHeader (HTTP.CONTENT_TYPE, APPLICATION_JSON);
        StringEntity entity = new StringEntity (jsonObject.toJSONString ());
        entity.setContentType (CONTEXT_TYPE_TEXT_JSON);
        entity.setContentEncoding (new BasicHeader (HTTP.CONTENT_TYPE, APPLICATION_JSON));
        httpPost.setEntity (entity);
        HttpResponse response = httpClient.execute (httpPost);
        String returnString = analysis (response);
        logger.info ("模型返回信息：" + returnString);
        MmResponse mmResponse = JSONUtil.parseJSON2Obj (returnString, MmResponse.class);
        JSONObject dataObject = JSON.parseObject (returnString).getJSONObject ("data");
        if (dataObject != null) {
            MmResponseData mmResponseData = JSONUtil.parseJSON2Obj (dataObject.toJSONString (), MmResponseData.class);
            mmResponse.setData (mmResponseData);
        }
        return mmResponse;
    }

    /**
     * 解析返回的http请求
     *
     * @param httpResponse
     * @return
     */
    private String analysis(HttpResponse httpResponse) throws Exception {
        if (httpResponse == null) {
            throw new Exception ("httpResponse为空");
        }
        int statusCode = httpResponse.getStatusLine ().getStatusCode ();
        if (HttpStatus.SC_OK != statusCode) {
            throw new Exception (httpResponse.toString ());
        }
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder buffer = new StringBuilder ();
        HttpEntity entity = httpResponse.getEntity ();
        String returnString = "";
        try {
            is = entity.getContent ();
            br = new BufferedReader (new InputStreamReader (is));
            String line = null;
            while ((line = br.readLine ()) != null) {
                buffer.append (line);
            }
            returnString = buffer.toString ();
            returnString = StringEscapeUtils.unescapeJava (returnString);
        } catch (Exception e) {
            throw new Exception ("解析返回结果失败!" + e.toString ());
        } finally {
            if (is != null) {
                try {
                    is.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            if (br != null) {
                try {
                    br.close ();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
        return returnString;
    }

    public static void main(String[] args) {
        String json = "{\n" +
                "\"status\": \"SUCCESS\",\n" +
                "    \"uuid\": \"453dft5465fdg456546fdsd4343dsd\",\n" +
                "    \"message\": \"调用成功\",\n" +
                "    \"data\": {\n" +
                "        \"file\":\"/rootPath/CORE/BJ/20170411/mode1_XXXXXX.txt\"\n" +
                "    }\n" +
                "}";
        MmResponse mmResponse = JSONUtil.parseJSON2Obj (json, MmResponse.class);
        JSONObject dataObject = JSON.parseObject (json).getJSONObject ("data");
        if (dataObject != null) {
            MmResponseData mmResponseData = JSONUtil.parseJSON2Obj (dataObject.toJSONString (), MmResponseData.class);
            mmResponse.setData (mmResponseData);
        }
        System.out.println (JSONUtil.parseObj2JSON (mmResponse));
    }
}
