-- 修改实现类路径
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.rts.executor.impl.KafkaExecutor'
where dict_type_id='RTS_IMPL_CLASS' and dict_id='KAFKA';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.HBaseConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='HBASE';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.HiveConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='HIVE';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.KafkaConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='KAFKA';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.MysqlConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='MYSQL';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.OracleConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='ORACLE';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.SolrConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='SOLR';
update T_GF_DICT set dict_name='com.hex.bigdata.udsp.im.convertor.impl.SolrHBaseConvertor'
where dict_type_id='IM_IMPL_CLASS' and dict_id='SOLR_HBASE';
