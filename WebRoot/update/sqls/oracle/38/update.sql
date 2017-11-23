
CREATE TABLE doc_from_dept (
   SEQ_ID NUMBER primary key,
  FROM_DEPT_NAME VARCHAR2(100) default NULL,
  FROM_DEPT_ID NUMBER  DEFAULT 0
);
ALTER TABLE documents_type ADD (type_no NUMBER);
ALTER TABLE doc_word ADD (WORD_NO NUMBER);

CREATE TABLE CHAT_GROUP (
   SEQ_ID NUMBER primary key,
  USER_ID NUMBER  DEFAULT 0,
  GROUP_NAME VARCHAR2(100),
  USERS CLOB
);
CREATE TABLE mobile_seal (
 SEQ_ID NUMBER primary key,
  DEVICE_LIST CLOB,
  SEAL_DATA CLOB,
  DEPT_ID NUMBER  DEFAULT 0,
  SEAL_NAME VARCHAR2(200) default NULL,
  CREATE_TIME DATE default NULL,
  CREATE_USER CLOB
);
CREATE TABLE mobile_device (
  SEQ_ID NUMBER primary key,
  "UID" NUMBER  DEFAULT 0,
  SUBMIT_TIME DATE default NULL,
  DEVICE_TYPE NUMBER  DEFAULT 0,
  DEVICE_INFO CLOB,
  MD5_CHECK VARCHAR2(32) default NULL,
  DEVICE_NAME VARCHAR2(100) default NULL
);
INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES
 ('6018','移动签章设置','/core/funcs/demo/index.jsp','sys.gif','0'); 
