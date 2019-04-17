package com.hex.bigdata.udsp.common.constant;

/**
 * Redis模式
 */
public enum RedisMode {
    SINGLE("单机", "SINGLE"),
    CLUSTER("集群", "CLUSTER");

    private String value;
    private String name;

    private RedisMode(String name, String value) {
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
