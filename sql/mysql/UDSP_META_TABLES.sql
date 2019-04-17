/*
Navicat MySQL Data Transfer

Source Server         : edh@goupwith.mysql.rds.aliyuncs.com_edh
Source Server Version : 50518
Source Host           : goupwith.mysql.rds.aliyuncs.com:3306
Source Database       : edh

Target Server Type    : MYSQL
Target Server Version : 50518
File Encoding         : 65001

Date: 2018-04-27 10:23:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_gf_application
-- ----------------------------
CREATE TABLE `t_gf_application` (
`APP_CODE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`APP_NAME`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`APP_COMMENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`APP_STATUS`  int(65) NULL DEFAULT NULL ,
PRIMARY KEY (`APP_CODE`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_auth_right
-- ----------------------------
CREATE TABLE `t_gf_auth_right` (
`ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`AUTH_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`USER_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`AUTH_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_dict
-- ----------------------------
CREATE TABLE `t_gf_dict` (
`DICT_TYPE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`DICT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`DICT_NAME`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`STATUS`  int(65) NULL DEFAULT NULL ,
`SORT_NO`  int(65) NULL DEFAULT NULL ,
`PARENT_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`SEQNO`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`APPID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`FILTER`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`DICT_TYPE_ID`, `DICT_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_dict_type
-- ----------------------------
CREATE TABLE `t_gf_dict_type` (
`DICT_TYPE_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`DICT_TYPE_NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APPID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`DICT_TYPE_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_employee
-- ----------------------------
CREATE TABLE `t_gf_employee` (
`EMP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`JOB_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`USER_NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`SEX`  int(65) NULL DEFAULT NULL ,
`BIRTHDAY`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`STATUS`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CARD_NO`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CARD_TYPE`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`INDATE`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`OUTDATE`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`OTEL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MOBILE_NO`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`HTEL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`HADDRESS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`HZIPCODE`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PEMAIL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_DATE`  datetime NULL DEFAULT NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ORGID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`EMP_COMMENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`OEMAIL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MANAGERID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MANAGERNAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`EMP_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_funcation
-- ----------------------------
CREATE TABLE `t_gf_funcation` (
`FUNC_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`FUNC_CODE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`FUNC_NAME`  varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`IS_FUNC`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DISPLAYORDER`  int(65) NULL DEFAULT NULL ,
`URL_ACCTION`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PARENT_FUNC_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APPID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`FUNC_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_log
-- ----------------------------
CREATE TABLE `t_gf_log` (
`LOG_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ACTION_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ACTION_URL`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`CREATE_USER_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`IS_FUNC`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`CREATE_DATE`  datetime NOT NULL ,
`CREATE_USERNAME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LOG_BODY`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`LOG_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_loginuser
-- ----------------------------
CREATE TABLE `t_gf_loginuser` (
`ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`EMP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`USER_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`USER_NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PASSWORD`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`STATUS`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MENU_TYPE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`CREATE_DATE`  datetime NULL DEFAULT NULL ,
`UPDATE_USERID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`USER_COMMENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`VALID_STARTDATE`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`VALID_ENDDATE`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ERROR_COUNT`  decimal NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_menu
-- ----------------------------
CREATE TABLE `t_gf_menu` (
`MENUID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`MENUNAME`  varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`MENULABEL`  varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MENUCODE`  varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ISLEAF`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PARAMETER`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`DISPLAYORDER`  int(65) NULL DEFAULT NULL ,
`APP_ID`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`MENU_ACTION`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`PARENTMENUID`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MENU_ICON`  varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`MENUID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_nextid
-- ----------------------------
CREATE TABLE `t_gf_nextid` (
`SEQ_TYPE`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`NEXT_ID`  decimal(19,0) NULL DEFAULT NULL ,
`LAST_TIME`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`SEQ_TYPE`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_org
-- ----------------------------
CREATE TABLE `t_gf_org` (
`ORGID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ORGNAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ORGCODE`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`ORG_LEVEL`  int(65) NULL DEFAULT NULL ,
`ORG_SEQ`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ORG_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`ORG_ADDRESS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`ZIPCODE`  varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LINKMAN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LINKTEL`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CREATE_DATE`  datetime NULL DEFAULT NULL ,
`UPDATE_DATE`  datetime NULL DEFAULT NULL ,
`DISPLAY_ORDER`  int(65) NULL DEFAULT NULL ,
`ORG_COMMENT`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PARENT_ORGID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ORGID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_quartz
-- ----------------------------
CREATE TABLE `t_gf_quartz` (
`PK_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`CLASS_NAME`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`METHOD`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`START_TIME`  datetime NULL DEFAULT NULL ,
`END_TIME`  datetime NULL DEFAULT NULL ,
`HAS_END_TIME`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TASK_NAME`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`STATUS`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`INTERVAL_TIME`  decimal(65,0) NULL DEFAULT NULL ,
`EXEC_NUM`  decimal(65,0) NULL DEFAULT NULL ,
`TRIGGER_MODEL`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`TRIGGER_TIME`  varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`CIRCULATE_MODEL`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`WEEK_MODEL`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`MONTH`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`DAY`  varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PERIOD`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`WEEK`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`PERIOD_MODEL`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`OWNER_SERVER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`PK_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_res_auth
-- ----------------------------
CREATE TABLE `t_gf_res_auth` (
`ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`AUTH_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`AUTH_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`RES_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`RES_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_role
-- ----------------------------
CREATE TABLE `t_gf_role` (
`ROLEID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`APP_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ROLENAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ROLE_DESC`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`ROLEID`, `APP_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_schd_job
-- ----------------------------
CREATE TABLE `t_gf_schd_job` (
`JOB_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`JOB_NAME`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`JOB_TYPE`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`COMMAND`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ARGS`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`SCHEDULE_TYPE`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`TRIGGER_TIME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ENABLED`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`RUNNING`  char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LAST_ELAPSED_TIME`  decimal(18,0) NULL DEFAULT NULL ,
`ERROR_COUNT`  int(65) NULL DEFAULT NULL ,
`LAST_ERROR`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`OWNER_SERVER`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LAST_RUN_TIME`  datetime NULL DEFAULT NULL ,
`ACTIVE_TIME`  datetime NULL DEFAULT NULL ,
`LAST_RUN_SERVER`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`UPDATE_TIME`  datetime NOT NULL ,
`UPDATE_USER`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`START_DATE`  datetime NULL DEFAULT NULL ,
`END_DATE`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`JOB_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_task_pool
-- ----------------------------
CREATE TABLE `t_gf_task_pool` (
`ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`MAX_ACTIVE`  int(65) NOT NULL ,
`MIN_IDLE`  int(65) NOT NULL ,
`IDLE_TIMEOUT`  int(65) NOT NULL ,
`INIT_COUNT`  int(65) NOT NULL ,
`DAEMON`  int(65) NOT NULL ,
`THREAD_PRIORITY`  int(65) NOT NULL ,
`TASK_PRODUCER_NAME`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`FETCH_BATCH_SIZE`  int(65) NOT NULL ,
`SLEEP_ON_IDLE`  int(65) NOT NULL ,
`SLEEP_ON_WORK_AFTER`  int(65) NOT NULL ,
PRIMARY KEY (`ID`),
UNIQUE INDEX `IX_GF_TASK_POOL_NAME` (`NAME`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for t_gf_user_session
-- ----------------------------
CREATE TABLE `t_gf_user_session` (
`USER_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`CLIENT_IP`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`LOGIN_TIME`  datetime NULL DEFAULT NULL ,
PRIMARY KEY (`USER_ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;
