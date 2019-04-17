package com.hex.bigdata.udsp.dsl.model;

import com.hex.bigdata.udsp.dsl.constant.OrderExpression;

/**
 * 排序类
 */
public class Order {
    private String name; // 字段名称
    private OrderExpression orderExp; // 排序表达式

    public Order() {
    }

    public Order(String name) {
        this.name = name;
    }

    public Order(String name, OrderExpression orderExp) {
        this.name = name;
        this.orderExp = orderExp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderExpression getOrderExp() {
        return orderExp;
    }

    public void setOrderExp(OrderExpression orderExp) {
        this.orderExp = orderExp;
    }
}
