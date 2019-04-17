-- 添加IM的数据源类型
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_TYPE', 'KAFKA1', 'KAFKA1', null, 10, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_SOURCE_REALTIME_TYPE', 'KAFKA1', 'KAFKA1', null, 2, null, null, 'default', null);

-- 添加IM的数据源配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('IM_DS_PROPS_KAFKA1', '交互建模-数据源配置-KAFKA1', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'bootstrap.servers', 'Kafka集群的IP和端口地址，多个地址用逗号分隔，如：10.1.97.1:9092,10.1.97.2:9092,10.1.97.3:9092', null, 1, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'security.protocol', '安全协议', null, 2, null, null, 'default', 'SASL_PLAINTEXT');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'sasl.kerberos.service.name', 'Kerberos服务名', null, 3, null, null, 'default', 'kafka');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'key.deserializer', 'Key的反序列化类', null, 4, null, null, 'default', 'org.apache.kafka.common.serialization.StringDeserializer');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'value.deserializer', 'Value的反序列化类', null, 5, null, null, 'default', 'org.apache.kafka.common.serialization.StringDeserializer');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'enable.auto.commit', '如果为true消费者会定期在后台提交offset偏移量', null, 6, null, null, 'default', 'true');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'auto.commit.interval.ms', '如果enable.auto.commit=true，消费者向kafka自动提交offsets的频率', null, 7, null, null, 'default', '1000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'auto.offset.reset', '在kafka中没有初始的offset或者当前的offset不存在将返回的offset值，latest、earliest', null, 8, null, null, 'default', 'latest');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_DS_PROPS_KAFKA1', 'max.poll.records', '在一次调用poll()中返回的最大记录数', null, 9, null, null, 'default', '500');

-- 添加IM的（源）模型配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('IM_MODEL_PROPS_KAFKA1', '交互建模-模型参数-KAFKA1', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_MODEL_PROPS_KAFKA1', 'topic', '主题', null, 1, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_MODEL_PROPS_KAFKA1', 'group.id', '消费组ID', null, 2, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_MODEL_PROPS_KAFKA1', 'consumer.timeout.ms', '消费超时时间（毫秒）', null, 3, null, null, 'default', '5000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_MODEL_PROPS_KAFKA1', 'consumer.cron.expression', '消费计划任务表达式', null, 4, null, null, 'default', '0/10 * * * * ?');

-- 添加IM的接口实现类
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IM_IMPL_CLASS', 'KAFKA1', 'com.hex.bigdata.udsp.im.converter.impl.Kafka1Converter', null, 10, null, null, 'default', '交互建模的Kafka1接口实现类');

commit;
