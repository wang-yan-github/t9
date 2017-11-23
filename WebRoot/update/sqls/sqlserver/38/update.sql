
CREATE TABLE doc_from_dept (
   SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  FROM_DEPT_NAME varchar(100) default NULL,
  FROM_DEPT_ID int  DEFAULT 0
);

ALTER TABLE documents_type ADD type_no int;
ALTER TABLE doc_word ADD WORD_NO int;

CREATE TABLE CHAT_GROUP (
   SEQ_ID int  NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  USER_ID int  DEFAULT 0,
  GROUP_NAME VARCHAR(100),
  USERS TEXT
);
CREATE TABLE mobile_seal (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  DEVICE_LIST text,
  SEAL_DATA ntext,	
  DEPT_ID int  DEFAULT 0,
  SEAL_NAME varchar(200) default NULL,
  CREATE_TIME datetime default NULL,
  CREATE_USER text
) ;
CREATE TABLE mobile_device (
  SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  UID int  DEFAULT 0,
  SUBMIT_TIME datetime default NULL,
  DEVICE_TYPE int  DEFAULT 0,
  DEVICE_INFO text,
  MD5_CHECK varchar(32) default NULL,
  DEVICE_NAME varchar(100) default NULL
);
INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES
 ('6018','移动签章设置','/core/funcs/demo/index.jsp','sys.gif','0'); 
