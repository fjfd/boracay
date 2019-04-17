package com.hex.bigdata.udsp.dsl.model;


import com.hex.bigdata.udsp.dsl.constant.LogicalOperator;

import java.util.List;

/**
 * 复合类
 */
public class Composite extends Component {
    private LogicalOperator logiOper; // 逻辑操作符
    private List<Component> components; // 组件集合

    public Composite() {
    }

    public Composite(LogicalOperator logiOper) {
        this.logiOper = logiOper;
    }

    public Composite(LogicalOperator logiOper, List<Component> components) {
        this.logiOper = logiOper;
        this.components = components;
    }

    public LogicalOperator getLogiOper() {
        return logiOper;
    }

    public void setLogiOper(LogicalOperator logiOper) {
        this.logiOper = logiOper;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }
}
