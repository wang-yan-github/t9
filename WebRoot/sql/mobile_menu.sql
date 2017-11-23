/*
Navicat MySQL Data Transfer

Source Server         : t9
Source Server Version : 50513
Source Host           : 127.0.0.1:3306
Source Database       : t9

Target Server Type    : MYSQL
Target Server Version : 50513
File Encoding         : 65001

Date: 2017-04-17 10:42:10
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `mobile_menu`
-- ----------------------------
DROP TABLE IF EXISTS `mobile_menu`;
CREATE TABLE `mobile_menu` (
  `MENU_ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `MENU_NAME` varchar(100) DEFAULT '' COMMENT '菜单名称',
  `MENU_LOCATION` varchar(600) DEFAULT NULL COMMENT '菜单地址',
  `IMAGE` varchar(100) DEFAULT '' COMMENT '图片名',
  PRIMARY KEY (`MENU_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='系统菜单表';
