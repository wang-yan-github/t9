/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50513
Source Host           : 127.0.0.1:3306
Source Database       : t9

Target Server Type    : MYSQL
Target Server Version : 50513
File Encoding         : 65001

Date: 2017-03-14 13:53:30
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `flow_type`
-- ----------------------------
DROP TABLE IF EXISTS `flow_type`;
CREATE TABLE `flow_type` (
  `SEQ_ID` int(11) NOT NULL AUTO_INCREMENT,
  `FLOW_NAME` varchar(200) DEFAULT NULL,
  `FORM_SEQ_ID` int(11) DEFAULT '0',
  `FLOW_DOC` char(1) DEFAULT NULL,
  `FLOW_TYPE` char(1) DEFAULT NULL,
  `MANAGE_USER` text,
  `FLOW_NO` int(11) DEFAULT '1',
  `FLOW_SORT` int(11) DEFAULT '2',
  `AUTO_NAME` text,
  `AUTO_NUM` int(11) DEFAULT '0',
  `AUTO_LEN` int(11) DEFAULT '0',
  `QUERY_USER` text,
  `FLOW_DESC` text,
  `AUTO_EDIT` varchar(20) DEFAULT '1',
  `NEW_USER` text,
  `QUERY_ITEM` text,
  `COMMENT_PRIV` char(1) DEFAULT '3',
  `DEPT_ID` int(11) DEFAULT '0',
  `FREE_PRESET` char(1) DEFAULT '1',
  `FREE_OTHER` char(1) DEFAULT '2',
  `QUERY_USER_DEPT` text,
  `MANAGE_USER_DEPT` text,
  `EDIT_PRIV` text,
  `MODEL_ID` text,
  `MODEL_NAME` text,
  `LIST_FLDS_STR` text,
  `ALLOW_PRE_SET` char(1) DEFAULT '0',
  `IS_SYSTEM` int(10) unsigned DEFAULT '0',
  `VIEW_USER` text,
  `VIEW_PRIV` int(10) unsigned DEFAULT NULL,
  `ATTACHMENT_ID` text,
  `ATTACHMENT_NAME` text,
  PRIMARY KEY (`SEQ_ID`),
  UNIQUE KEY `SEQ_ID` (`SEQ_ID`),
  KEY `FLOW_TYPE_INDEX` (`FLOW_SORT`,`FLOW_TYPE`)
) ENGINE=MyISAM AUTO_INCREMENT=597 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flow_type
-- ----------------------------