package com.hex.bigdata.udsp.iq.provider.model;

import com.hex.bigdata.udsp.common.api.model.Base;
import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by junjiem on 2017-3-2.
 */
public class Metadata extends Base {

    private String name;

    private String describe;

    private String note;

    private String tbName; // 表名

    private List<DataColumn> queryColumns; // 查询字段集合

    private List<DataColumn> returnColumns; // 返回字段集合

    private Datasource datasource; // 数据源

    public Metadata() {
    }

    public Metadata(List<Property> properties) {
        super(properties);
    }

    public Metadata(Map<String, Property> propertyMap) {
        super(propertyMap);
    }

    public String getTbName() {
        if (StringUtils.isBlank(tbName))
            throw new IllegalArgumentException("type不能为空");
        return tbName;
    }

    public void setTbName(String tbName) {
        this.tbName = tbName;
    }

    public List<DataColumn> getQueryColumns() {
        if (queryColumns == null)
            throw new IllegalArgumentException("queryColumns不能为空");
        return queryColumns;
    }

    public void setQueryColumns(List<DataColumn> queryColumns) {
        this.queryColumns = queryColumns;
    }

    public List<DataColumn> getReturnColumns() {
        if (returnColumns == null)
            throw new IllegalArgumentException("returnColumns不能为空");
        return returnColumns;
    }

    public void setReturnColumns(List<DataColumn> returnColumns) {
        this.returnColumns = returnColumns;
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

    public Datasource getDatasource() {
        if (datasource == null)
            throw new IllegalArgumentException("datasource不能为空");
        return datasource;
    }

    public void setDatasource(Datasource datasource) {
        this.datasource = datasource;
    }
}
