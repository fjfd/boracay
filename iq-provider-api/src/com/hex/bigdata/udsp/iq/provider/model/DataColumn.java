package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.constant.DataType;
import org.apache.commons.lang.StringUtils;

public class DataColumn {

    private Short seq;

    private String name;

    private String describe;

    private String length;

    private String note;

    private DataType type;

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

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public DataType getType() {
        if (type == null)
            throw new IllegalArgumentException("type不能为空");
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }
}