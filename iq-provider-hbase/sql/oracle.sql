-- 添加IQ的数据源类型
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_TYPE', 'HBASE', 'HBASE', null, 1, null, null, 'default', null);

-- 添加IQ的数据源配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('IQ_DS_PROPS_HBASE', '交互查询-数据源配置-HBASE', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.zk.quorum', 'HBase的Zookeeper的集群IP，多个IP用逗号分隔，如：10.1.97.1,10.1.97.2,10.1.97.3', null, 1, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.zk.port', 'HBase的Zookeeper的端口，如：2181', null, 2, null, null, 'default', '2181');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.rpc.timeout', '一次RPC请求的超时时间（毫秒）', null, 3, null, null, 'default', '5000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.client.retries.number', '客户端重试最大次数', null, 4, 'IQ_DS_PROPS_HBASE', null, 'default', '3');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.client.pause', '重试的休眠时间（毫秒）', null, 5, null, null, 'default', '100');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'zookeeper.recovery.retry', 'zookeeper的重试次数（zk的重试总次数是：hbase.client.retries.number * zookeeper.recovery.retry）', null, 6, null, null, 'default', '3');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'zookeeper.recovery.retry.intervalmill', 'zookeeper重试的休眠时间（毫秒）', null, 7, null, null, 'default', '200');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.client.operation.timeout', 'HBase客户端发起一次数据操作直至得到响应之间总的超时时间，数据操作类型包括get、append、increment、delete、put等（毫秒）', null, 8, null, null, 'default', '30000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.client.scanner.timeout.period', 'scan操作超时时间（毫秒）', null, 9, null, null, 'default', '60000');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.security.authentication', 'HBase的安全认证方式', null, 11, null, null, 'default', 'kerberos');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hadoop.security.authentication', 'Hadoop安全认证方式', null, 12, null, null, 'default', 'kerberos');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.master.kerberos.principal', 'HBase Master 的 Kerberos Principal，如：hbase/node1@BIGDATA.HEX.COM', null, 13, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.regionserver.kerberos.principal', 'HBase RegionServer 的 Kerberos Principal，如：hbase/_HOST@BIGDATA.HEX.COM', null, 14, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'kerberos.principal', 'Kerberos Principal，如：test@BIGDATA.HEX.COM', null, 15, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'kerberos.keytab', 'Kerberos Keytab 文件路径，如：C:/kerberos/test.keytab 或 /root/test.keytab', null, 16, null, null, 'default', null);
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'max.data.size', '最大返回数', null, 99, null, null, 'default', '65536');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'max.data.size.alarm', '超过最大返回数是否告警', null, 100, null, null, 'default', 'true');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'hbase.rootdir', 'HDFS根目录', null, 10, null, null, 'default', '/hbase');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_DS_PROPS_HBASE', 'zookeeper.znode.parent', 'ZooKeeper Znode 父级', null, 10, null, null, 'default', '/hbase');

-- 添加IQ的元数据配置
insert into T_GF_DICT_TYPE (dict_type_id, dict_type_name, appid)
values ('IQ_MD_PROPS_HBASE', '交互查询-元数据配置-HBASE', 'default');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_MD_PROPS_HBASE', 'hbase.family.name', 'hbase族名', null, 1, null, null, 'default', 'f');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_MD_PROPS_HBASE', 'hbase.qualifier.name', 'hbase列名', null, 2, null, null, 'default', 'q');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_MD_PROPS_HBASE', 'hbase.fq.data.type', '结果数据类型，可选dsv、json，如果为dsv时hbase.fq.dsv.separator生效', null, 3, null, null, 'default', 'dsv');
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_MD_PROPS_HBASE', 'hbase.fq.dsv.separator', '结果数据分隔符，如：|、||、\007、\t、\036', null, 4, null, null, 'default', '\007');

-- 添加IQ的接口实现类
insert into T_GF_DICT (dict_type_id, dict_id, dict_name, status, sort_no, parent_id, seqno, appid, filter)
values ('IQ_IMPL_CLASS', 'HBASE', 'com.hex.bigdata.udsp.iq.provider.impl.HBaseProvider', null, 2, null, null, 'default', '交互查询的HBase接口实现类');

commit;
