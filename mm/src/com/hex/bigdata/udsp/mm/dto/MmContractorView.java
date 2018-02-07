package com.hex.bigdata.udsp.mm.dto;

import com.hex.bigdata.udsp.mm.model.MmContractor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA
 * Author: tomnic.wang
 * DATE:2017/4/12
 * TIME:19:08
 */
public class MmContractorView extends MmContractor implements Serializable {
    /**
     * 创建时间查询时间范围-开始时间
     */
    private String crtTimeStart;
    /**
     * 创建时间查询时间范围-结束时间
     */
    private String crtTimeEnd;
    /**
     * 更新时间查询时间范围-开始时间
     */
    private String uptTimeStart;
    /**
     * 更新时间查询时间范围-结束时间
     */
    private String uptTimeEnd;

    public String getCrtTimeStart() {
        return crtTimeStart;
    }

    public void setCrtTimeStart(String crtTimeStart) {
        this.crtTimeStart = crtTimeStart;
    }

    public String getCrtTimeEnd() {
        return crtTimeEnd;
    }

    public void setCrtTimeEnd(String crtTimeEnd) {
        this.crtTimeEnd = crtTimeEnd;
    }

    public String getUptTimeStart() {
        return uptTimeStart;
    }

    public void setUptTimeStart(String uptTimeStart) {
        this.uptTimeStart = uptTimeStart;
    }

    public String getUptTimeEnd() {
        return uptTimeEnd;
    }

    public void setUptTimeEnd(String uptTimeEnd) {
        this.uptTimeEnd = uptTimeEnd;
    }
}
