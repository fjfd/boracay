package com.hex.bigdata.udsp.iq.provider.model.dsl;

import com.hex.bigdata.udsp.dsl.model.DslRequest;
import com.hex.bigdata.udsp.iq.provider.model.Metadata;

/**
 * 自定义SQL请求类
 */
public class IqDslRequest {

    private DslRequest request; // 请求信息

    private Metadata metadata; // 元数据

    public IqDslRequest() {
    }

    public IqDslRequest(DslRequest request, Metadata metadata) {
        this.request = request;
        this.metadata = metadata;
    }

    public DslRequest getRequest() {
        return request;
    }

    public void setRequest(DslRequest request) {
        this.request = request;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
