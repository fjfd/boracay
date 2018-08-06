-- 消费日志表中添加是否从缓存获取字段
ALTER TABLE MC_CONSUME_LOG ADD IS_CACHE CHAR(1) COMMENT '是否从缓存获取（0：是，1：否）';
