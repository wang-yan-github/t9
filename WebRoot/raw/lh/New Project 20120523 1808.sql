-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.76-enterprise-gpl-nt-log


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema `t9-product`
--

CREATE DATABASE IF NOT EXISTS `t9-product`;
USE `t9-product`;
CREATE TABLE `cms_column` (
  `SEQ_ID` int(10) unsigned NOT NULL auto_increment,
  `COLUMN_NAME` varchar(100) default NULL,
  `COLUMN_TITLE` varchar(100) default NULL,
  `STATION_ID` int(10) unsigned default NULL,
  `PARENT_ID` int(10) unsigned default NULL,
  `COLUMN_PATH` varchar(100) default NULL,
  `ARCHIVE` int(10) unsigned default NULL,
  `TEMPLATE_INDEX_ID` int(10) unsigned default NULL,
  `TEMPLATE_ARTICLE_ID` int(10) unsigned default NULL,
  `CREATE_ID` int(10) unsigned default NULL,
  `CREATE_TIME` datetime default NULL,
  `COLUMN_INDEX` double default NULL,
  `PAGING` int(2) unsigned default NULL,
  `MAX_INDEX_PAGE` int(10) unsigned default NULL,
  `PAGING_NUMBER` int(10) unsigned default NULL,
  `URL` varchar(200) default NULL,
  `SHOW_MAIN` int(2) unsigned default NULL,
  `VISIT_USER` text,
  `EDIT_USER` text,
  `NEW_USER` text,
  `DEL_USER` text,
  `REL_USER` text,
  `EDIT_USER_CONTENT` text,
  `APPROVAL_USER_CONTENT` text,
  `RELEASE_USER_CONTENT` text,
  `RECEVIE_USER_CONTENT` text,
  `ORDER_CONTENT` text,
  PRIMARY KEY  (`SEQ_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=63 DEFAULT CHARSET=utf8;
INSERT INTO `cms_column` (`SEQ_ID`,`COLUMN_NAME`,`COLUMN_TITLE`,`STATION_ID`,`PARENT_ID`,`COLUMN_PATH`,`ARCHIVE`,`TEMPLATE_INDEX_ID`,`TEMPLATE_ARTICLE_ID`,`CREATE_ID`,`CREATE_TIME`,`COLUMN_INDEX`,`PAGING`,`MAX_INDEX_PAGE`,`PAGING_NUMBER`,`URL`,`SHOW_MAIN`,`VISIT_USER`,`EDIT_USER`,`NEW_USER`,`DEL_USER`,`REL_USER`,`EDIT_USER_CONTENT`,`APPROVAL_USER_CONTENT`,`RELEASE_USER_CONTENT`,`RECEVIE_USER_CONTENT`,`ORDER_CONTENT`) VALUES 
 (44,'教育','',11,0,'jy',0,21,20,1,'2012-03-14 11:57:38',13,0,0,0,NULL,0,'0||','0||',NULL,'|1|','||1',NULL,NULL,NULL,NULL,NULL),
 (13,'体育','',11,0,'ty',0,21,20,0,'2012-02-15 11:16:22',9,0,0,0,NULL,0,'0||','0||',NULL,'|1|','||1',NULL,NULL,NULL,NULL,NULL),
 (12,'新闻','',11,0,'xw',0,21,20,0,'2012-02-14 11:57:20',10,0,0,0,NULL,0,'0||','0||',NULL,'|1|','||1','0||','||1','|1|','1||','0||'),
 (16,'NBA','',11,13,'nba',0,21,20,0,'2012-02-15 11:53:45',1,1,5,3,NULL,0,'0||','0||',NULL,'|1|','||',NULL,NULL,NULL,NULL,NULL),
 (33,'财经','',11,0,'cj',0,21,20,0,'2012-02-17 14:34:07',4,0,0,0,NULL,0,'0||','0||',NULL,'|1|','||1',NULL,NULL,NULL,NULL,NULL),
 (36,'软件','',11,0,'rj',0,21,20,0,'2012-02-27 15:18:04',11,0,0,0,NULL,0,'0||','0||',NULL,'|1|','||1',NULL,NULL,NULL,NULL,NULL),
 (41,'英超','',11,13,'yc',0,21,20,0,'2012-02-29 13:11:37',2,0,0,0,NULL,0,'0||','0||',NULL,'||','||1',NULL,NULL,NULL,NULL,NULL),
 (43,'音乐','',11,0,'yy',0,21,20,1,'2012-03-07 17:32:25',12,0,0,0,NULL,0,'0||','||','||1','||','||',NULL,NULL,NULL,NULL,NULL);
CREATE TABLE `cms_content` (
  `SEQ_ID` int(10) unsigned NOT NULL auto_increment,
  `CONTENT_NAME` varchar(200) default NULL,
  `CONTENT_TITLE` varchar(200) default NULL,
  `CONTENT_ABSTRACT` text,
  `KEYWORD` varchar(100) default NULL,
  `CONTENT_SOURCE` varchar(100) default NULL,
  `CONTENT_FILE_NAME` varchar(100) default NULL,
  `CONTENT_AUTHOR` varchar(100) default NULL,
  `CONTENT_DATE` datetime default NULL,
  `STATION_ID` int(10) unsigned default NULL,
  `COLUMN_ID` int(10) unsigned default NULL,
  `CONTENT` text,
  `CREATE_ID` int(10) unsigned default NULL,
  `CREATE_TIME` datetime default NULL,
  `CONTENT_TYPE` int(10) unsigned default NULL,
  `CONTENT_STATUS` int(10) unsigned default NULL,
  `CONTENT_TOP` int(10) unsigned default NULL,
  `CONTENT_INDEX` double default NULL,
  `URL` varchar(200) default NULL,
  `ATTACHMENT_ID` text,
  `ATTACHMENT_NAME` text,
  PRIMARY KEY  (`SEQ_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;
CREATE TABLE `cms_station` (
  `SEQ_ID` int(10) unsigned NOT NULL auto_increment,
  `STATION_NAME` varchar(100) default NULL,
  `STATION_DOMAIN_NAME` varchar(100) default NULL,
  `TEMPLATE_ID` int(10) unsigned default NULL,
  `STATION_FILE_NAME` varchar(100) default NULL,
  `EXTEND_NAME` varchar(100) default NULL,
  `ARTICLE_EXTEND_NAME` varchar(100) default NULL,
  `CREATE_ID` int(10) unsigned default NULL,
  `CREATE_TIME` datetime default NULL,
  `STATION_PATH` varchar(100) default NULL,
  `URL` varchar(200) default NULL,
  `VISIT_USER` text,
  `EDIT_USER` text,
  `NEW_USER` text,
  `DEL_USER` text,
  `REL_USER` text,
  PRIMARY KEY  (`SEQ_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
INSERT INTO `cms_station` (`SEQ_ID`,`STATION_NAME`,`STATION_DOMAIN_NAME`,`TEMPLATE_ID`,`STATION_FILE_NAME`,`EXTEND_NAME`,`ARTICLE_EXTEND_NAME`,`CREATE_ID`,`CREATE_TIME`,`STATION_PATH`,`URL`,`VISIT_USER`,`EDIT_USER`,`NEW_USER`,`DEL_USER`,`REL_USER`) VALUES 
 (11,'站点八','zd8',24,NULL,'html','html',0,'2012-02-14 11:35:05','zd8',NULL,'0||','0||',NULL,'|1|','||1');
CREATE TABLE `cms_template` (
  `SEQ_ID` int(10) unsigned NOT NULL auto_increment,
  `TEMPLATE_NAME` varchar(100) default NULL,
  `TEMPLATE_FILE_NAME` varchar(100) default NULL,
  `TEMPLATE_TYPE` int(10) unsigned default NULL,
  `CREATE_ID` int(10) unsigned default NULL,
  `CREATE_TIME` datetime default NULL,
  `attachment_Id` text,
  `attachment_Name` text,
  `station_id` int(10) unsigned default NULL,
  PRIMARY KEY  (`SEQ_ID`)
) ENGINE=MyISAM AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
INSERT INTO `cms_template` (`SEQ_ID`,`TEMPLATE_NAME`,`TEMPLATE_FILE_NAME`,`TEMPLATE_TYPE`,`CREATE_ID`,`CREATE_TIME`,`attachment_Id`,`attachment_Name`,`station_id`) VALUES 
 (20,'文章模板','content',2,0,'2012-02-14 14:56:23','863abb5e8d26c3b28a251f144693cc0f','文章模板.html',11),
 (21,'栏目模板','index',1,0,'2012-02-15 15:52:37','6906f5961f4d7d67dea209d5149cbc80','栏目模板.html',11),
 (24,'首页模板','index',1,1,'2012-02-20 10:44:12','8d1ac7115f8f413d2ae9ddb06369e258','首页模板.html',11),
 (25,'head','head.html',3,1,'2012-02-24 14:11:24','fc9203cd769398df1995f0abe4c23e88','head.html',11),
 (26,'left','left.html',3,1,'2012-02-24 15:35:41','9a3fb0aa559edda8f2e4cb36ed589d20','left.html',11),
 (27,'foot','foot.html',3,1,'2012-02-24 17:09:20','5291664b9db1851ab77d9280516e9cb3','foot.html',11);



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
