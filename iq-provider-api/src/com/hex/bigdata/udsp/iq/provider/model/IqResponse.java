package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by junjiem on 2017-3-2.
 */
public class IqResponse {
//    private IqRequest request; // 请求信息
    private List<Map<String,String>> records; // 响应结果集
    private Page page; // 分页信息
    private long consumeTime; // 持续时间(毫秒)
    private Status status; // 状态
    private StatusCode statusCode; // 状态码
    private String message; // 信息
    private String filePath; // 用于异步调用时存储文件路径
    private LinkedHashMap<String,String> columns; // 返回字段信息

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

//    public IqRequest getRequest() {
//        return request;
//    }
//
//    public void setRequest(IqRequest request) {
//        this.request = request;
//    }

    public List<Map<String, String>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, String>> records) {
        this.records = records;
    }

    public long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(long consumeTime) {
        this.consumeTime = consumeTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LinkedHashMap<String, String> getColumns() {
        return columns;
    }

    public void setColumns(LinkedHashMap<String, String> columns) {
        this.columns = columns;
    }
}
