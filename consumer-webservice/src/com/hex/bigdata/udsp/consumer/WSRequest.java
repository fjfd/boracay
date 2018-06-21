package com.hex.bigdata.udsp.consumer;

import com.hex.bigdata.udsp.common.api.model.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * WebService Request
 */
public class WSRequest implements Serializable {
    private String appUser;
    private String serviceName;
    private String udspUser;
    private String appType;
    private String appName;
    private String appId;
    private String requestType;

    private String type; // sync、async
    private String entity; // start、status、stop
    private Page page;
    private String sql;
    private long timeout;
    private String consumeId; // 消费ID
    private HashMap<String, String> data;
    private ArrayList<HashMap<String, String>> dataStream;
    private String requestIp;//请求IP

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(String consumeId) {
        this.consumeId = consumeId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAppUser() {
        return appUser;
    }

    public void setAppUser(String appUser) {
        this.appUser = appUser;
    }

    public String getUdspUser() {
        return udspUser;
    }

    public void setUdspUser(String udspUser) {
        this.udspUser = udspUser;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public ArrayList<HashMap<String, String>> getDataStream() {
        return dataStream;
    }

    public void setDataStream(ArrayList<HashMap<String, String>> dataStream) {
        this.dataStream = dataStream;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }
}
