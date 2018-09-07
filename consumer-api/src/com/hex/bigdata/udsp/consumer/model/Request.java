package com.hex.bigdata.udsp.consumer.model;

import com.hex.bigdata.udsp.common.api.model.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by PC on 2017/4/17.
 */
public class Request extends BaseRequest implements Serializable{

    private String appType; // 应用类型
    private String appName; // 应用名称
    private String appId; // 应用ID

    private String requestType; // 请求类型
    private String requestIp; // 请求IP

    private Page page; // 分页信息
    private String sql; // SQL语句
    private Map<String, String> data; // 请求数据

    private long timeout; // 超时时间（毫秒）
    private List<Map<String, String>> dataStream; // 数据流集合

    private String consumeId; // 消费ID

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

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public List<Map<String, String>> getDataStream() {
        return dataStream;
    }

    public void setDataStream(List<Map<String, String>> dataStream) {
        this.dataStream = dataStream;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }
}
