CREATE TABLE DATA_SRC (
  SEQ_ID NUMBER,
  TID NUMBER(12, 0),
  FLOW_ID NUMBER(12, 0),
  d_name VARCHAR2(100),
  d_desc VARCHAR2(100),
  d_creator VARCHAR2(100),
  d_dept CLOB,
  d_create_time DATE
);
CREATE TABLE FLOW_PRINT_TPL(
 SEQ_ID NUMBER primary key,
 FLOW_ID NUMBER,
  T_TYPE VARCHAR2(20),
 T_NAME VARCHAR2(100),
  CONTENT BLOB,
 FLOW_PRCS VARCHAR2(20)
)
;
CREATE TABLE secure_key (
  SEQ_ID NUMBER primary key,
  KEY_SN VARCHAR2(200),
  KEY_INFO VARCHAR2(1000)
);
CREATE TABLE V_MEET
(
  SEQ_ID NUMBER NOT NULL,
  BEGIN_USER VARCHAR2(20),
  CONTENT VARCHAR2(500),
  INVITE_USERS VARCHAR2(20),
  ADD_TIME DATE,
  vmeet VARCHAR2(20),
  vt VARCHAR2(20),
  vck VARCHAR2(20)
, CONSTRAINT V_MEET_PK PRIMARY KEY
  (
    SEQ_ID
  )
  ENABLE
)
;
CREATE TABLE ZL_PPT
(
  SEQ_ID NUMBER NOT NULL,
  NAME VARCHAR2(100),
  FOLDER VARCHAR2(20),
  TOTAL_FRAME NUMBER,
  ROOMID VARCHAR2(20),
  CREATE_DATE VARCHAR2(20)
, CONSTRAINT ZL_PPT_PK PRIMARY KEY
  (
    SEQ_ID
  )
  ENABLE
)
;
CREATE TABLE ZL_FILE
(
  SEQ_ID NUMBER NOT NULL,
  NAME VARCHAR2(100),
  FILE_NAME VARCHAR2(100),
  FSIZE VARCHAR2(20),
  ROOMID VARCHAR2(20),
  CREATE_DATE VARCHAR2(20)
, CONSTRAINT ZL_FILE_PK PRIMARY KEY
  (
    SEQ_ID
  )
  ENABLE
)
;
exec pr_CreateIdentityColumn('ZL_FILE','SEQ_ID');
exec pr_CreateIdentityColumn('ZL_PPT','SEQ_ID');
exec pr_CreateIdentityColumn('V_MEET','SEQ_ID');
exec pr_CreateIdentityColumn('FLOW_PRINT_TPL','SEQ_ID');
exec pr_CreateIdentityColumn('DATA_SRC','SEQ_ID');
exec pr_CreateIdentityColumn('secure_key','SEQ_ID');
ALTER TABLE flow_form_item ADD (DATA_TYPE VARCHAR2(10));
ALTER TABLE flow_form_item ADD (RADIO_CHECK VARCHAR2(10)); 
ALTER TABLE flow_form_item ADD  (RADIO_FIELD CLOB);
ALTER TABLE flow_form_item ADD  (DATE_FORMAT  VARCHAR2(100));
ALTER TABLE flow_form_item ADD  (HIDDEN  VARCHAR2(10));
ALTER TABLE rms_file ADD  (FILE_WORD  VARCHAR2(45));
ALTER TABLE rms_file ADD  (FILE_YEAR  VARCHAR2(45));
ALTER TABLE rms_file ADD  (ISSUE_NUM  VARCHAR2(45));
UPDATE code_class SET CLASS_DESC='短信提醒类型' where CLASS_NO = 'SMS_REMIND';
INSERT INTO code_class (CLASS_NO,CLASS_DESC,SORT_NO,CLASS_LEVEL) VALUES ('FILE_YEAR','公文年号','2','0');
INSERT INTO code_class (CLASS_NO,CLASS_DESC,SORT_NO,CLASS_LEVEL) VALUES ('FILE_WORD','公文字','1','0');
INSERT INTO code_class (CLASS_NO,CLASS_DESC,SORT_NO,CLASS_LEVEL) VALUES ('ISSUE_NUM','公文期号','3','0');
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('FILE_WORD','3','外发','3',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('ISSUE_NUM','1','1期','1',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('ISSUE_NUM','2','2期','2',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('FILE_WORD','2','港发','2',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('FILE_WORD','1','明林发','1',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('FILE_YEAR','3','2012','3',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('FILE_YEAR','4','2013','4',NULL);
INSERT INTO code_item (CLASS_NO,CLASS_CODE,CLASS_DESC,SORT_NO,CODE) VALUES ('FILE_YEAR','2','2011','2',NULL);

insert into sys_function (MENU_ID , FUNC_NAME , FUNC_CODE ,ICON ,OPEN_FLAG  )
   values ('0230' , '我的视频会议' , '/subsys/oa/vmeet/index.jsp', 'edit.gif' , '0');