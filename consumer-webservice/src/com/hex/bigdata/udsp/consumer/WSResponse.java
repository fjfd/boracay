package com.hex.bigdata.udsp.consumer;

import com.hex.bigdata.udsp.common.api.model.Page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * WebService Response
 */
@XmlRootElement(name = "udspResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class WSResponse implements Serializable {
    private ArrayList<HashMap<String, String>> records; // 记录集合

    private Page page;

    private long consumeTime; // 持续时间

    private String status; // 状态

    private String statusCode; // 状态码

    private String message; // 错误信息

    private String consumeId; // 消费ID

    private String errorCode; //错误编码

    public String responseContent;//返回信息

    public String getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(String consumeId) {
        this.consumeId = consumeId;
    }

    public ArrayList<HashMap<String, String>> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<HashMap<String, String>> records) {
        this.records = records;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public long getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(long consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }
}
