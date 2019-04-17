package com.hex.bigdata.udsp.dsl.constant;

/**
 * 逻辑操作符
 */
public enum LogicalOperator {
    AND("和", "AND"),
    OR("或", "OR");

    private String value;
    private String name;

    private LogicalOperator(String name, String value) {
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
