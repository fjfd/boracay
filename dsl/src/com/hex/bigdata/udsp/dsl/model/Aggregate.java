package com.hex.bigdata.udsp.dsl.model;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.dsl.constant.AggregateFunction;

/**
 * 聚合类
 */
public class Aggregate {
    private String name; // 字段名称
    private DataType dataType; // 字段类型
    private AggregateFunction aggFun; // 聚合函数

    public Aggregate(String name) {
        this.name = name;
    }

    public Aggregate(String name, DataType dataType) {
        this.name = name;
        this.dataType = dataType;
    }

    public Aggregate(String name, AggregateFunction aggFun) {
        this.name = name;
        this.aggFun = aggFun;
    }

    public Aggregate(String name, DataType dataType, AggregateFunction aggFun) {
        this.name = name;
        this.dataType = dataType;
        this.aggFun = aggFun;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AggregateFunction getAggFun() {
        return aggFun;
    }

    public void setAggFun(AggregateFunction aggFun) {
        this.aggFun = aggFun;
    }
}
