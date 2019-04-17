package com.hex.bigdata.udsp.dsl.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JunjieM on 2019-1-8.
 */
public class DslResponse {

    private List<Map<String, String>> records; // 返回结果集
    private LinkedHashMap<String, String> columns; // 返回字段信息

    public List<Map<String, String>> getRecords() {
        return records;
    }

    public void setRecords(List<Map<String, String>> records) {
        this.records = records;
    }

    public LinkedHashMap<String, String> getColumns() {
        return columns;
    }

    public void setColumns(LinkedHashMap<String, String> columns) {
        this.columns = columns;
    }
}
