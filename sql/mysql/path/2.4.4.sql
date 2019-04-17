-- 更新交互建模-数据源配置-IMPALA的字典信息
update T_GF_DICT set filter='org.apache.hive.jdbc.HiveDriver'
where dict_type_id='IM_DS_PROPS_IMPALA' and dict_id='driver.class';
update T_GF_DICT set dict_name='JDBC URL，如：无密码jdbc:hive2://${ip}:${port}/${database};auth=noSasl  有密码jdbc:hive2://${ip}:${port}/${database}'
where dict_type_id='IM_DS_PROPS_IMPALA' and dict_id='jdbc.url';
