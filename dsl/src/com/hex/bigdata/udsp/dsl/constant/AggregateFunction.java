package com.hex.bigdata.udsp.dsl.constant;

/**
 * 聚合函数
 */
public enum AggregateFunction {
    SUM("合计", "SUM"),
    MIN("最小", "MIN"),
    MAX("最大", "MAX"),
    AVG("平均", "AVG"),
    COUNT("数量", "COUNT"),
    COUNT_DISTINCT("去重数量", "COUNT_DISTINCT");

    private String value;
    private String name;

    private AggregateFunction(String name, String value) {
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
