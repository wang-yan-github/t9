CREATE TABLE  proj_bug (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PROJ_ID int  NOT NULL DEFAULT 0,
  TASK_ID int ,
  DEAL_USER varchar(100),
  BEGIN_USER varchar(100) ,
  BUG_NAME varchar(200) ,
  DEAD_LINE datetime ,
  CREAT_TIME datetime ,
  LEVEL int  DEFAULT 0,
  BUG_DESC ntext,
  STATUS int  ,
  ATTACHMENT_ID ntext,
  ATTACHMENT_NAME ntext,
  RESULT ntext
) ;

CREATE TABLE  proj_comment (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PROJ_ID int NOT NULL DEFAULT 0,
  WRITER varchar(40) ,
  WRITE_TIME datetime ,
  CONTENT ntext
) ;

CREATE TABLE  proj_file (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PROJ_ID int NOT NULL DEFAULT 0,
  SORT_ID int DEFAULT 0,
  FILE_TYPE int DEFAULT 0,
  SUBJECT varchar(200) ,
  ATTACHMENT_ID ntext,
  ATTACHMENT_NAME ntext,
  FILE_DESC ntext,
  UPLOAD_USER varchar(100) ,
  VERSION varchar(20) ,
  UPDATE_TIME datetime ,
  HISTORY varchar(20) ,
  ACTIVE varchar(20) 
) ;

CREATE TABLE  proj_file_log (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  FILE_ID int DEFAULT 0,
  ACTION int ,
  USER_ID int  ,
  ACTION_TIME datetime 
) ;

CREATE TABLE  proj_file_sort (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  SORT_PARENT int DEFAULT 0,
  PROJ_ID int NOT NULL DEFAULT 0,
  SORT_NO varchar(20) ,
  SORT_NAME varchar(200) ,
  SORT_TYPE varchar(20) ,
  VIEW_USER ntext,
  NEW_USER ntext,
  MANAGE_USER ntext,
  MODIFY_USER ntext
) ;

CREATE TABLE  proj_forum (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PROJ_ID int NOT NULL DEFAULT 0,
  USER_ID varchar(20) ,
  SUBJECT varchar(200) ,
  CONTENT ntext,
  ATTACHMENT_ID ntext,
  ATTACHMENT_NAME ntext,
  SUBMIT_TIME datetime ,
  REPLY_CONT int DEFAULT 0,
  PARENT int DEFAULT 0,
  OLD_SUBMIT_TIME datetime 
) ;

CREATE TABLE  proj_priv (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PRIV_CODE varchar(40) ,
  PRIV_USER ntext,
  PRIV_ROLE ntext,
  PRIV_DEPT ntext
) ;

CREATE TABLE  proj_project (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PROJ_NAME varchar(255) NOT NULL DEFAULT '',
  PROJ_NUM varchar(40) NOT NULL DEFAULT '',
  PROJ_DESCRIPTION ntext,
  PROJ_TYPE int DEFAULT 0,
  PROJ_DEPT varchar(40) ,
  PROJ_UPDATE_TIME datetime ,
  PROJ_START_TIME datetime ,
  PROJ_END_TIME datetime ,
  PROJ_ACT_END_TIME datetime ,
  PROJ_OWNER varchar(255) ,
  PROJ_LEADER varchar(255) ,
  PROJ_VIWER ntext,
  PROJ_USER ntext,
  PROJ_PRIV ntext,
  PROJ_MANAGER varchar(255) ,
  PROJ_COMMENT ntext,
  PROJ_STATUS int ,
  PROJ_PERCENT_COMPLETE int DEFAULT 0,
  COST_TYPE ntext,
  COST_MONEY ntext,
  APPROVE_LOG ntext,
  ATTACHMENT_ID ntext,
  ATTACHMENT_NAME ntext
) ;

CREATE TABLE  proj_task (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  PROJ_ID int NOT NULL DEFAULT 0,
  TASK_NO varchar(40) NOT NULL DEFAULT '',
  TASK_NAME varchar(100) ,
  TASK_DESCRIPTION ntext,
  TASK_USER varchar(40) ,
  TASK_MILESTONE int ,
  TASK_START_TIME datetime ,
  TASK_END_TIME datetime ,
  TASK_ACT_END_TIME datetime  ,
  TASK_TIME int DEFAULT 0,
  TASK_LEVEL varchar(40) DEFAULT '1',
  PRE_TASK int DEFAULT 0,
  TASK_PERCENT_COMPLETE int DEFAULT 0,
  REMARK ntext,
  FLOW_ID_STR varchar(200) ,
  RUN_ID_STR varchar(200) ,
  TASK_STATUS int DEFAULT 0,
  TASk_CONSTRAIN int ,
  PARENT_TASK int 
) ;

CREATE TABLE  proj_task_log (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  LOG_TYPE int NOT NULL DEFAULT 0,
  TASK_ID int NOT NULL DEFAULT 0,
  LOG_USER varchar(255) ,
  LOG_CONTENT ntext,
  LOG_TIME datetime ,
  [PERCENT] int DEFAULT 0,
  ATTACHMENT_ID ntext,
  ATTACHMENT_NAME ntext
) ;

INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES 
('SMS_REMIND','88','项目管理','88',NULL); 

INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3501','我的项目','/project/proj/projectList.jsp','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3505','我的任务','/project/task/index.jsp','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3510','项目审批','/project/approve/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3515','项目文档','/project/doc/index.jsp','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3520','项目问题','/project/bug/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('3525','基础参数设置','org','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('352501','项目权限设置','/project/setting/priv/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('352505','项目代码设置','/project/setting/code/','edit.gif','0'); 
INSERT INTO sys_function (MENU_ID,FUNC_NAME,FUNC_CODE,ICON,OPEN_FLAG) VALUES 
('352510','项目模板管理','/project/setting/template/','edit.gif','0'); 
INSERT INTO sys_menu (MENU_ID,MENU_NAME,IMAGE) VALUES 
('35','项目管理','sys.gif');