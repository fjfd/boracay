package com.hex.bigdata.udsp.dsl.constant;

/**
 * 排序表达式
 */
public enum OrderExpression {
    ASC("正序", "ASC"),
    DESC("倒序", "DESC");

    private String value;
    private String name;

    private OrderExpression(String name, String value) {
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
