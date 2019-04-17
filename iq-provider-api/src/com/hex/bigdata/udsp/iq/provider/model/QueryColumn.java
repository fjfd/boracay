package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.Operator;
import org.apache.commons.lang.StringUtils;

/**
 * Created by junjiem on 2017-3-2.
 */
public class QueryColumn {
    private Short seq;

    private String name;

    private String describe;

    private DataType type;

    private String length;

    private boolean isNeed;

    private String defaultVal;

    private Operator operator;

    private String label;

    private boolean isOfferOut;

    private String value;

    public Short getSeq() {
        if (seq == null) {
            throw new IllegalArgumentException ("查询字段序号不能为空");
        }
        return seq;
    }

    public void setSeq(Short seq) {
        this.seq = seq;
    }

    public String getName() {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException ("查询字段名称不能为空");
        }
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
        if (type == null) {
            throw new IllegalArgumentException ("查询字段类型不能为空");
        }
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public boolean isNeed() {
        return isNeed;
    }

    public void setNeed(boolean need) {
        isNeed = need;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }

    public Operator getOperator() {
        if (operator == null) {
            throw new IllegalArgumentException ("查询字段操作符不能为空");
        }
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getLabel() {
        if (StringUtils.isBlank(label)) {
            throw new IllegalArgumentException ("查询字段不能为空");
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isOfferOut() {
        return isOfferOut;
    }

    public void setOfferOut(boolean offerOut) {
        isOfferOut = offerOut;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
