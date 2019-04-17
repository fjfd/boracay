-- 添加OLQ的数据源类型
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_TYPE', 'ORACLE', 'ORACLE', null, 3, null, null, 'default', null);

-- 添加OLQ的数据源配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('OLQ_DS_PROPS_ORACLE', '联机查询-数据源配置-ORACLE参数', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'driver.class', '驱动类', null, 1, null, null, 'default', 'oracle.jdbc.OracleDriver');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'jdbc.url', 'JDBC URL，如：jdbc:oracle:thin:@${ip}:${port}/${model}', null, 2, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'username', '用户名', null, 3, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'password', '密码', null, 4, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'initial.size', '初始连接数', null, 5, null, null, 'default', '1');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'min.idle', '最小空闲连接数', null, 6, null, null, 'default', '10');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'max.idle', '最大空闲连接数', null, 7, null, null, 'default', '20');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'max.active', '最大并发数', null, 8, null, null, 'default', '25');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'max.wait', '最长等待时间，单位毫秒', null, 9, null, null, 'default', '60000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'validation.query', '验证链接的SQL语句，必须能返回一行及以上数据', null, 10, null, null, 'default', 'select 1 from dual');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'validation.query.timeout', '验证有效连接的超时时间', null, 11, null, null, 'default', '0');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'time.between.eviction.runs.millis', 'N毫秒检测一次是否有死掉的线程', null, 12, null, null, 'default', '30000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'min.evictable.idle.time.millis', '空闲连接N毫秒中后释放', null, 13, null, null, 'default', '1800000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'test.while.idle', '是否被无效链接销毁器进行检验', null, 14, null, null, 'default', 'true');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'test.on.borrow', '是否从池中取出链接前进行检验', null, 15, null, null, 'default', 'false');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'test.on.return', '是否在归还到池中前进行检验', null, 16, null, null, 'default', 'false');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'remove.abandoned.timeout', '回收没用的连接超时时间', null, 17, null, null, 'default', '180000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'remove.abandoned', '是否进行没用连接的回收', null, 18, null, null, 'default', 'true');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'max.data.size', '最大数据返回条数', null, 19, null, null, 'default', '65535');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_DS_PROPS_ORACLE', 'max.data.size.alarm', '超过最大返回数是否告警', null, 20, null, null, 'default', 'true');

-- 添加OLQ的接口实现类
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('OLQ_IMPL_CLASS', 'ORACLE', 'com.hex.bigdata.udsp.olq.provider.impl.OracleProvider', null, 3, null, null, 'default', '联机查询的Oracle接口实现类');

commit;
