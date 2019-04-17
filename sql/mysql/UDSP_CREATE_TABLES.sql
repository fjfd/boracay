/*
Navicat MySQL Data Transfer

Source Server         : edh@goupwith.mysql.rds.aliyuncs.com_edh
Source Server Version : 50518
Source Host           : goupwith.mysql.rds.aliyuncs.com:3306
Source Database       : edh

Target Server Type    : MYSQL
Target Server Version : 50518
File Encoding         : 65001

Date: 2018-04-27 09:51:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for com_datasource
-- ----------------------------
CREATE TABLE `com_datasource` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源类型' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`IMPL_CLASS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '接口实现类' ,
`MODEL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属模块（IQ、OLQ、RTS、IM）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_COM_DS_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_COM_DS_DELFLG_MODEL_NAME` (`MODEL`, `NAME`, `DEL_FLG`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for com_operation_log
-- ----------------------------
CREATE TABLE `com_operation_log` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`ACTION_TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作类型（1添加 2更新 3删除 4查询）' ,
`ACTION_URL`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作URL' ,
`ACTION_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作用户' ,
`ACTION_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作时间' ,
`ACTION_CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作信息' ,
PRIMARY KEY (`PK_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for com_properties
-- ----------------------------
CREATE TABLE `com_properties` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`FK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '外键ID' ,
`NAME`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称' ,
`VALUE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数值' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '说明' ,
PRIMARY KEY (`PK_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for im_metadata
-- ----------------------------
CREATE TABLE `im_metadata` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`DS_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称/表名((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明/表说明' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`TB_NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '库表' ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态（1：未建，2：已建）' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型（0：内表，1：外表）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IM_MD_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_IM_MD_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互建模-元数据配置'

;

-- ----------------------------
-- Table structure for im_metadata_column
-- ----------------------------
CREATE TABLE `im_metadata_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '元数据ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称（唯一、英文）' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长度' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`INDEXED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '索引（0：是；1：否）' ,
`UNIQUEKEY`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键（0：是；1：否）' ,
`STORED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储（0：是；1：否）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IM_MD_COL_MDID` (`MD_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互建模-元数据字段'

;

-- ----------------------------
-- Table structure for im_model
-- ----------------------------
CREATE TABLE `im_model` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`S_DS_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '源数据源ID' ,
`T_MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目标元数据ID' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型（1：批量 2：实时）' ,
`BUILD_MODE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '构建策略（1：增量，2：全量）' ,
`UPDATE_MODE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新策略（1、匹配更新 2、更新、插入 3、增量插入，默认：2）' ,
`E_DS_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '引擎数据源ID' ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态（1：未建，2：已建）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IM_MODEL_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_IM_MODEL_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互建模-模型配置'

;

-- ----------------------------
-- Table structure for im_model_filter_column
-- ----------------------------
CREATE TABLE `im_model_filter_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MODEL_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长度' ,
`IS_NEED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否必输（0：是；1：否）' ,
`DEFAULT_VAL`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值' ,
`OPERATOR`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作符' ,
`LABEL`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '别名' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IM_MODEL_FILTER_MODELID` (`MODEL_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互模型-模型过滤字段'

;

-- ----------------------------
-- Table structure for im_model_mapping
-- ----------------------------
CREATE TABLE `im_model_mapping` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MODEL_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '源元数据字段名称' ,
`COL_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目标元数据字段ID' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '源元数据字段类型' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '源元数据字段长度' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '源元数据字段说明' ,
`INDEXED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引（0：是；1：否）' ,
`UNIQUEKEY`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主键（0：是；1：否）' ,
`STORED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储（0：是；1：否）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IM_MODEL_MAPPING_MODELID` (`MODEL_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互建模-模型字段映射'

;

-- ----------------------------
-- Table structure for im_model_update_key
-- ----------------------------
CREATE TABLE `im_model_update_key` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`COL_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目标元数据字段ID' ,
`MODEL_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型ID' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IM_MODEL_UPDATEKEY_MODELID` (`MODEL_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互建模-模型更新键值'

;

-- ----------------------------
-- Table structure for iq_application
-- ----------------------------
CREATE TABLE `iq_application` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '元数据ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称(唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IQ_APP_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_IQ_APP_DELFLG_MDID` (`DEL_FLG`, `MD_ID`) USING BTREE ,
INDEX `IDX_IQ_APP_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互查询-应用表'

;

-- ----------------------------
-- Table structure for iq_application_order_column
-- ----------------------------
CREATE TABLE `iq_application_order_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型' ,
`ORDER_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '排序类型(ASC、DESC)' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IQ_APP_ORDER_COL_APPID` (`APP_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互查询-排序参数'

;

-- ----------------------------
-- Table structure for iq_application_query_column
-- ----------------------------
CREATE TABLE `iq_application_query_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长度' ,
`IS_NEED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否必输（0：是；1：否）' ,
`DEFAULT_VAL`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值' ,
`OPERATOR`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作符' ,
`LABEL`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名' ,
`IS_OFFER_OUT`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否开放(0：是；1：否)' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IQ_APP_QUERY_COL_APPID` (`APP_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互查询-查询参数表'

;

-- ----------------------------
-- Table structure for iq_application_return_column
-- ----------------------------
CREATE TABLE `iq_application_return_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型' ,
`STATS`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '统计函数' ,
`LABEL`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '别名' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长度' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IQ_APP_RETURN_COL_APPID` (`APP_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互查询-返回参数表'

;

-- ----------------------------
-- Table structure for iq_metadata
-- ----------------------------
CREATE TABLE `iq_metadata` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`DS_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称/表名((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明/表说明' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`TB_NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表名' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IQ_MD_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_IQ_MD_DELFLG_DSID` (`DEL_FLG`, `DS_ID`) USING BTREE ,
INDEX `IDX_IQ_MD_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互式查询-元数据集'

;

-- ----------------------------
-- Table structure for iq_metadata_column
-- ----------------------------
CREATE TABLE `iq_metadata_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '元数据ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号（和类型一起确定唯一）' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型(1、查询字段；2、返回字段)' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长度' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`COL_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_IQ_MD_COL_MDID` (`MD_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='交互查询-元数据字段列表'

;

-- ----------------------------
-- Table structure for mc_consume_log
-- ----------------------------
CREATE TABLE `mc_consume_log` (
`PK_ID`  varchar(68) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`USER_NAME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名' ,
`SERVICE_NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务名' ,
`REQUEST_CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求内容' ,
`REQUEST_START_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求开始时间' ,
`REQUEST_END_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求结束时间' ,
`RUN_START_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行开始时间' ,
`RUN_END_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行结束时间' ,
`RESPONSE_CONTENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '响应内容（文件路径、少量的消息信息）' ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '结果状态(0：成功1：失败)' ,
`ERROR_CODE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '错误编码，与UDSP错误编码相同' ,
`APP_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用类型（IQ、OLQ、MM、RTS_PRODUCER、RTS_CONSUMER）' ,
`REQUEST_TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '请求类型（0：内部请求 1：外部请求）' ,
`APP_NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用名' ,
`MESSAGE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '错误信息或其他消息提示' ,
`SYNC_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '同步/异步' ,
PRIMARY KEY (`PK_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='监控中心-消费日志'

;

-- ----------------------------
-- Table structure for mm_application
-- ----------------------------
CREATE TABLE `mm_application` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MODEL_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文、唯一)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志(0：未删除，1：删除)' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_MM_APP_DELFLG` (`DEL_FLG`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='模型管理-应用表'

;

-- ----------------------------
-- Table structure for mm_application_execute_param
-- ----------------------------
CREATE TABLE `mm_application_execute_param` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`IS_NEED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否必输（0:是；1：否）' ,
`DEFAULT_VAL`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '默认值' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型应用ID' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_MM_APP_EXE_APPID` (`APP_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='模型管理-执行参数表'

;

-- ----------------------------
-- Table structure for mm_application_return_param
-- ----------------------------
CREATE TABLE `mm_application_return_param` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型应用ID' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_MM_APP_RET_APPID` (`APP_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='模型管理-返回参数表'

;

-- ----------------------------
-- Table structure for mm_contractor
-- ----------------------------
CREATE TABLE `mm_contractor` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '英文名称' ,
`HTTP_URL`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '远程连接' ,
`PRINCIPAL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '负责人' ,
`MOBILE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '负责电话' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志(0：未删除，1：删除)' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`EXTEND_FIELD1`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预留字段1' ,
`EXTEND_FIELD2`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预留字段2' ,
`CN_NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '中文名称' ,
`FTP_PASSWORD`  varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ftp登陆密码' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_MM_CONT_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_MM_CONT_DELFLG_NAME` (`NAME`, `DEL_FLG`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='模型管理-厂商管理'

;

-- ----------------------------
-- Table structure for mm_model_info
-- ----------------------------
CREATE TABLE `mm_model_info` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((英文、唯一)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`VER_NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '版本注释' ,
`VER_NUM`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本号' ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布状态(1：待发布；2：已发布；3：归档)' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志(0：未删除，1：删除)' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`CONTRACTOR`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型开发厂商ID' ,
`MODEL_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '1：同步；2：异步；3：批处理，多个用逗号分隔' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_MM_MODEL_INFO_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_MM_MODEL_INFO_DELFLG_CONID` (`DEL_FLG`, `CONTRACTOR`) USING BTREE ,
INDEX `IDX_MM_MODEL_INFO_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='模型管理-模型信息表'

;

-- ----------------------------
-- Table structure for mm_model_param
-- ----------------------------
CREATE TABLE `mm_model_param` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MM_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号（和类型一起确定唯一）' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型(1、查询字段；2、返回字段)' ,
`LENGTH`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '长度' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`COL_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_MM_MODEL_PARAM_MMID` (`MM_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='模型管理-模型字段列表'

;

-- ----------------------------
-- Table structure for olq_application
-- ----------------------------
CREATE TABLE `olq_application` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`OLQ_SQL`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用配置的SQL语句' ,
`OLQ_DS_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联机查询数据源ID' ,
`MAX_NUM`  decimal(10,0) NULL DEFAULT NULL COMMENT '最大返回数' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志(0：未删除，1：删除)' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称' ,
`DESCRIPTION`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
PRIMARY KEY (`PK_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for olq_application_param
-- ----------------------------
CREATE TABLE `olq_application_param` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`PARAM_NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '参数名称' ,
`PARAM_DESC`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '参数说明' ,
`DEFAULT_VALUE`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值' ,
`IS_NEED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否必输，0：必输，1：不必输' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用ID' ,
`SEQ`  decimal(3,0) NULL DEFAULT NULL COMMENT '序号' ,
PRIMARY KEY (`PK_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for rc_service
-- ----------------------------
CREATE TABLE `rc_service` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型（IQ：交互查询；MM：模型管理；OLQ：联机查询；RTS-CUS：实时流消费者；RTS-PRO：实时流生产者）' ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用ID' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '启停标志（0：启动，1：停用）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_RC_SERVICE_DF` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_RC_SERVICE_DF_NAME` (`DEL_FLG`, `NAME`) USING BTREE ,
INDEX `IDX_RC_SERVICE_DF_TYPE` (`DEL_FLG`, `TYPE`) USING BTREE ,
INDEX `IDX_RC_SERVICE_DF_TYPE_APPID` (`DEL_FLG`, `TYPE`, `APP_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='注册中心-服务注册表'

;

-- ----------------------------
-- Table structure for rc_user_service
-- ----------------------------
CREATE TABLE `rc_user_service` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`USER_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID' ,
`SERVICE_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务ID' ,
`IP_SECTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'IP段' ,
`MAX_SYNC_NUM`  decimal(10,0) NOT NULL COMMENT '最大同步并发数' ,
`MAX_ASYNC_NUM`  decimal(10,0) NOT NULL COMMENT '最大异步并发数' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`MAX_SYNC_WAIT_NUM`  decimal(10,0) NOT NULL COMMENT '最大同步队列等待数' ,
`MAX_ASYNC_WAIT_NUM`  decimal(10,0) NOT NULL COMMENT '最大异步队列等待数' ,
`MAX_SYNC_WAIT_TIMEOUT`  decimal(10,0) NULL DEFAULT NULL COMMENT '同步最大等待超时时间' ,
`MAX_ASYNC_WAIT_TIMEOUT`  decimal(10,0) NULL DEFAULT NULL COMMENT '异步最大等待超时时间' ,
`MAX_SYNC_EXECUTE_TIMEOUT`  decimal(10,0) NULL DEFAULT NULL COMMENT '同步最大执行超时时间' ,
`MAX_ASYNC_EXECUTE_TIMEOUT`  decimal(10,0) NULL DEFAULT NULL COMMENT '异步最大执行超时时间' ,
`ALARM_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '告警方式（NONE、MAIL、...）' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_RC_USER_SERVICE_DF` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_RC_USER_SERVICE_DF_SID` (`DEL_FLG`, `SERVICE_ID`) USING BTREE ,
INDEX `IDX_RC_USER_SERVICE_DF_SID_UID` (`DEL_FLG`, `SERVICE_ID`, `USER_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for rts_customer_config
-- ----------------------------
CREATE TABLE `rts_customer_config` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '元数据ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`GROUP_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_RTS_CER_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_RTS_CER_DELFLG_MDID` (`DEL_FLG`, `MD_ID`) USING BTREE ,
INDEX `IDX_RTS_CER_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='实时流-消费者配置'

;

-- ----------------------------
-- Table structure for rts_metadata
-- ----------------------------
CREATE TABLE `rts_metadata` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`DS_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称(英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '说明' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
`TOPIC`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主题' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_RTS_MD_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_RTS_MD_DELFLG_DSID` (`DEL_FLG`, `DS_ID`) USING BTREE ,
INDEX `IDX_RTS_MD_DELFLG_NAME` (`NAME`, `DEL_FLG`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='实时流-元数据配置'

;

-- ----------------------------
-- Table structure for rts_metadata_column
-- ----------------------------
CREATE TABLE `rts_metadata_column` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '元数据ID' ,
`SEQ`  decimal(3,0) NOT NULL COMMENT '序号' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称((唯一、英文)' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类型' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_RTS_MD_COL_MDID` (`MD_ID`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='实时流-元数据字段表'

;

-- ----------------------------
-- Table structure for rts_producrer_config
-- ----------------------------
CREATE TABLE `rts_producrer_config` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键' ,
`MD_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '元数据ID' ,
`NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称（英文）' ,
`DESCRIPTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '说明' ,
`NOTE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注' ,
`DEL_FLG`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '删除标志（0：未删除，1：删除）' ,
`CRT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者' ,
`CRT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间' ,
`UPT_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新者' ,
`UPT_TIME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '更新时间' ,
PRIMARY KEY (`PK_ID`),
INDEX `IDX_RTS_PER_DELFLG` (`DEL_FLG`) USING BTREE ,
INDEX `IDX_RTS_PER_DELFLG_MDID` (`DEL_FLG`, `MD_ID`) USING BTREE ,
INDEX `IDX_RTS_PER_DELFLG_NAME` (`DEL_FLG`, `NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='实时流-生产者配置'

;

-- 往服务注册信息表中添加是否缓存和缓存时效两个字段
ALTER TABLE RC_SERVICE ADD IS_CACHE CHAR(1) default 1 COMMENT '是否缓存（0：是，1：否）';
ALTER TABLE RC_SERVICE ADD TIME_OUT DECIMAL(10) default 60 COMMENT '缓存时效（秒）';

-- 消费日志表中添加是否从缓存获取字段
ALTER TABLE MC_CONSUME_LOG ADD IS_CACHE CHAR(1) COMMENT '是否从缓存获取（0：是，1：否）';

-- 删除字段
alter table RTS_CUSTOMER_CONFIG drop column GROUP_ID;

-- 删除字段
alter table MM_CONTRACTOR drop column EXTEND_FIELD1;
alter table MM_CONTRACTOR drop column EXTEND_FIELD2;

-- 消费日志表中添加接口耗时字段
ALTER TABLE MC_CONSUME_LOG ADD CONSUME_TIME DECIMAL(10,0) COMMENT '接口耗时（ms）';
