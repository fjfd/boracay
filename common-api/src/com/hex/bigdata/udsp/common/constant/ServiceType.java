package com.hex.bigdata.udsp.common.constant;

/**
 * 服务类型
 */
public enum ServiceType {
    IQ("交互查询", "IQ"),
    OLQ("联机查询", "OLQ"),
    OLQ_APP("联机查询应用", "OLQ_APP"),
    MM("模型管理", "MM"),
    RTS_PRODUCER("实时流-生产者服务", "RTS_PRODUCER"),
    RTS_CONSUMER("实时流-消费者服务", "RTS_CONSUMER"),
    IM("交互建模", "IM"),
    IQ_DSL("交互查询自定义SQL", "IQ_DSL");

    private String value;
    private String name;

    private ServiceType(String name, String value) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
