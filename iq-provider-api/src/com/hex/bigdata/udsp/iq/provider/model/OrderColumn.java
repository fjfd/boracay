package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.Order;
import org.apache.commons.lang.StringUtils;

/**
 * Created by junjiem on 2017-3-2.
 */
public class OrderColumn {
    private Short seq;

    private String name;

    private String describe;

    private DataType type;

    private Order order;

    public Short getSeq() {
        if (seq == null)
            throw new IllegalArgumentException("seq不能为空");
        return seq;
    }

    public void setSeq(Short seq) {
        this.seq = seq;
    }

    public String getName() {
        if (StringUtils.isBlank(name))
            throw new IllegalArgumentException("name不能为空");
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public DataType getType() {
        if (type == null)
            throw new IllegalArgumentException("type不能为空");
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public Order getOrder() {
        if (order == null)
            throw new IllegalArgumentException("order不能为空");
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
