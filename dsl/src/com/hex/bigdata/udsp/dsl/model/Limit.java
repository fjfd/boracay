package com.hex.bigdata.udsp.dsl.model;

/**
 * 限制类
 */
public class Limit {
    private int limit;
    private int offset = 0;

    public Limit() {
    }

    public Limit(int limit) {
        this.limit = limit;
    }

    public Limit(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
