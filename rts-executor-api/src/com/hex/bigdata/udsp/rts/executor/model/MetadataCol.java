package com.hex.bigdata.udsp.rts.executor.model;

import java.io.Serializable;

/**
 * Created by tomnic on 2017/3/3.
 */
public class MetadataCol implements Serializable {

    private String name;

    private String value;

    private Short seq;

    private String describe;

    private String type;

    public Short getSeq() {
        return seq;
    }

    public void setSeq(Short seq) {
        this.seq = seq;
    }

    public String getName() {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
