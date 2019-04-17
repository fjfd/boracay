package com.hex.bigdata.udsp.dsl.model;

/**
 * 字段类
 */
public class Column {
    private String alias; // 别名
    private Aggregate aggregate; // 聚合类型

    public Column(String alias) {
        this.alias = alias;
    }

    public Column(String alias, Aggregate aggregate) {
        this.alias = alias;
        this.aggregate = aggregate;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }
}
