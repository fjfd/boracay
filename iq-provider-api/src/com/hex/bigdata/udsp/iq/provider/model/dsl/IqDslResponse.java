package com.hex.bigdata.udsp.iq.provider.model.dsl;

import com.hex.bigdata.udsp.common.constant.Status;
import com.hex.bigdata.udsp.common.constant.StatusCode;
import com.hex.bigdata.udsp.dsl.model.DslResponse;

/**
 * 自定义SQL响应类
 */
public class IqDslResponse {

    private DslResponse dslResponse; // 响应信息
    private long consumeTime; // 持续时间(毫秒)
    private Status status; // 状态
    private StatusCode statusCode; // 状态码
    private String message; // 信息

    public DslResponse getDslResponse() {
        return dslResponse;
    }

    public void setDslResponse(DslResponse dslResponse) {
        this.dslResponse = dslResponse;
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
}
