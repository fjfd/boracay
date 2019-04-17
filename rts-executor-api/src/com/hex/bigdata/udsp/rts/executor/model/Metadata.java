package com.hex.bigdata.udsp.rts.executor.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tomnic on 2017/3/3.
 */
public class Metadata implements Serializable {

    private String name;

    private String describe;

    private String note;

    private String topic;

    private List<MetadataCol> columns;

    private Datasource datasource;

    public List<MetadataCol> getColumns() {
        return columns;
    }

    public void setColumns(List<MetadataCol> columns) {
        this.columns = columns;
    }

    public Datasource getDatasource() {
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTopic() {
        if (StringUtils.isBlank(topic))
            throw new IllegalArgumentException("topic不能为空");
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


}
