/*
Navicat MySQL Data Transfer

Source Server         : t9
Source Server Version : 50513
Source Host           : 127.0.0.1:3306
Source Database       : t9

Target Server Type    : MYSQL
Target Server Version : 50513
File Encoding         : 65001

Date: 2017-03-30 12:16:02
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `attend_mobile`
-- ----------------------------
DROP TABLE IF EXISTS `attend_mobile`;
CREATE TABLE `attend_mobile` (
  `M_ID` int(11) NOT NULL AUTO_INCREMENT,
  `M_UID` smallint(6) NOT NULL,
  `M_TIME` datetime NOT NULL COMMENT '创建时间',
  `M_LNG` varchar(20) DEFAULT NULL COMMENT '地理坐标经度',
  `M_LAT` varchar(20) DEFAULT NULL COMMENT '地理坐标纬度',
  `M_LOCATION` varchar(100) DEFAULT '' COMMENT '定位地点名称',
  `M_REMARK` varchar(1000) DEFAULT '' COMMENT '备注',
  `M_ISFOOT` varchar(20) DEFAULT '0' COMMENT '是否为足迹',
  `ATTACHMENT_ID` text COMMENT '附件ID串',
  `ATTACHMENT_NAME` text COMMENT '附件名称',
  PRIMARY KEY (`M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk COMMENT='移动考勤表';

-- ----------------------------
-- Records of attend_mobile
-- ----------------------------
