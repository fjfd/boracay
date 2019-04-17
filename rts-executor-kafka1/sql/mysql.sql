-- 添加RTS的数据源类型
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_DS_TYPE', 'KAFKA1', 'KAFKA1', null, 2, null, null, 'default', null);

-- 添加RTS的数据源配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('RTS_DS_PROPS_KAFKA1', '实时流-数据源配置-KAFKA1', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_DS_PROPS_KAFKA1', 'bootstrap.servers', 'Kafka集群的IP和端口地址，多个地址用逗号分隔，如：10.1.97.1:9092,10.1.97.2:9092,10.1.97.3:9092', null, 1, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_DS_PROPS_KAFKA1', 'security.protocol', '安全协议', null, 2, null, null, 'default', 'SASL_PLAINTEXT');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_DS_PROPS_KAFKA1', 'sasl.kerberos.service.name', 'Kerberos服务名', null, 3, null, null, 'default', 'kafka');

-- 添加RTS-PRODUCER的数据源配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('RTS_PRODUCER_PROPS_KAFKA1', '实时流-生产者配置-KAFKA1', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_PRODUCER_PROPS_KAFKA1', 'key.serializer', 'Key的序列化类', null, 1, null, null, 'default', 'org.apache.kafka.common.serialization.StringSerializer');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_PRODUCER_PROPS_KAFKA1', 'value.serializer', 'Value的序列化类', null, 2, null, null, 'default', 'org.apache.kafka.common.serialization.StringSerializer');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_PRODUCER_PROPS_KAFKA1', 'acks', '请求确认模式，0、1、2、all', null, 3, null, null, 'default', 'all');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_PRODUCER_PROPS_KAFKA1', 'retries', '失败重试次数', null, 4, null, null, 'default', '3');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_PRODUCER_PROPS_KAFKA1', 'retry.backoff.ms', '失败重试间隔（毫秒）', null, 5, null, null, 'default', '1000');

-- 添加RTS-CONSUMER的数据源配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('RTS_CONSUMER_PROPS_KAFKA1', '实时流-消费者配置-KAFKA1', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'group.id', '组ID', null, 1, null, null, 'default', 'group1');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'key.deserializer', 'Key的反序列化类', null, 2, null, null, 'default', 'org.apache.kafka.common.serialization.StringDeserializer');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'value.deserializer', 'Value的反序列化类', null, 3, null, null, 'default', 'org.apache.kafka.common.serialization.StringDeserializer');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'enable.auto.commit', '如果为true消费者会定期在后台提交offset偏移量', null, 4, null, null, 'default', 'true');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'auto.commit.interval.ms', '如果enable.auto.commit=true，消费者向kafka自动提交offsets的频率', null, 5, null, null, 'default', '1000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'auto.offset.reset', '在kafka中没有初始的offset或者当前的offset不存在将返回的offset值，latest、earliest', null, 6, null, null, 'default', 'latest');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_CONSUMER_PROPS_KAFKA1', 'max.poll.records', '在一次调用poll()中返回的最大记录数', null, 7, null, null, 'default', '500');

-- 添加RTS的接口实现类
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('RTS_IMPL_CLASS', 'KAFKA1', 'com.hex.bigdata.udsp.rts.executor.impl.Kafka1Executor', null, 2, null, null, 'default', '实时流的Kafka1接口实现类');
