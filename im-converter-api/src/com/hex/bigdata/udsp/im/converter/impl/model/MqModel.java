package com.hex.bigdata.udsp.im.converter.impl.model;

import com.hex.bigdata.udsp.common.api.model.Datasource;
import com.hex.bigdata.udsp.common.api.model.Property;
import com.hex.bigdata.udsp.im.converter.model.Model;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by JunjieM on 2017-9-15.
 */
public class MqModel extends Model implements Serializable {

    // Redis使用Jackson进行序列化和反序列化时对于get开头的方法必须要有对应的变量
    private String topic;
    private String consumerTimeoutMs;
    private String consumerCronExpression;

    public MqModel() {
        // Redis使用Jackson进行序列化和反序列化时必须要有空构造函数
    }

    public MqModel(Model model) {
        super(model);
    }

    public MqModel(List<Property> properties, Datasource srcDatasource) {
        super(properties, srcDatasource);
    }

    // 主题
    public String getTopic() {
        String value = getProperty("topic").getValue();
        if (StringUtils.isBlank(value))
            throw new IllegalArgumentException("topic不能为空");
        return value;
    }

    // 消费超时时间（毫秒）
    public String getConsumerTimeoutMs() {
        String value = getProperty("consumer.timeout.ms").getValue();
        if (StringUtils.isBlank(value))
            value = "1000";
        return value;
    }

    // 消费计划任务表达式
    public String getConsumerCronExpression() {
        String value = getProperty("consumer.cron.expression").getValue();
        if (StringUtils.isBlank(value))
            value = "0/2 * * * * ?";
        return value;
    }
}
