package com.hex.bigdata.udsp.dsl.constant;

/**
 * 比较操作符
 */
public enum ComparisonOperator {
    EQ("等于", "="),
    NE("不等于", "!="),
    GT("大于", ">"),
    LT("小于", "<"),
    GE("大于等于", ">="),
    LE("小于等于", "<="),
    IN("IN", "IN"),
    NOT_IN("NOT IN", "NOT IN"),
    LIKE("LIKE", "LIKE"),
    BETWEEN_AND("全开", "BETWEEN AND");

    private String value;
    private String name;

    private ComparisonOperator(String name, String value) {
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
