package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.constant.DataType;
import com.hex.bigdata.udsp.common.constant.Stats;
import org.apache.commons.lang.StringUtils;

/**
 * Created by junjiem on 2017-3-2.
 */
public class ReturnColumn {
    private String name;

    private String describe;

    private DataType type;

    private Stats stats;

    private String label;

    private Short seq;

    private String length;

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

    public Stats getStats() {
        if (stats == null)
            throw new IllegalArgumentException("stats不能为空");
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getLabel() {
        if (StringUtils.isBlank(label))
            throw new IllegalArgumentException("label不能为空");
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Short getSeq() {
        if (seq == null)
            throw new IllegalArgumentException("seq不能为空");
        return seq;
    }

    public void setSeq(Short seq) {
        this.seq = seq;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
