--自增字段的存储过程
create or replace procedure pr_CreateIdentityColumn
(tablename varchar2,columnname varchar2)
as 
strsql varchar2(1000);
begin
         strsql := 'create sequence seq_'||tablename||' minvalue 1 maxvalue 999999999999999999 start with 1
increment by 1 nocache';
         execute immediate strsql;
         strsql := 'create or replace trigger trg_'||tablename||' before insert on '||tablename||' for each row begin
select seq_'||tablename||'.nextval into :new.'||columnname||' from dual; end;';
         execute immediate strsql;
end;
--DATA_BASE表的创建
create table DATA_BASE(
  SEQ_ID int PRIMARY KEY,
  DB_NO VARCHAR2(5) UNIQUE NOT NULL,
  DB_NAME  VARCHAR2(45) NOT NULL,
  DB_DESC VARCHAR2(45) ,
  DS_NAME VARCHAR2(45),
  DSMS_NAME VARCHAR2(45)
);
--为DATA_BASE表建立自增字段
exec pr_createidentitycolumn('DATA_BASE','SEQ_ID');
--DS_TABLE表的创建
create table DS_TABLE(
  SEQ_ID INT PRIMARY KEY ,
  TABLE_NO VARCHAR2(5)UNIQUE NOT NULL,
  TABLE_NAME VARCHAR(45) NOT NULL,
  TABLE_DESC VARCHAR2(45),
  CATEGORY_NO VARCHAR2(1),
  DB_NO VARCHAR2(5),
  CLASS_NAME VARCHAR2(45),
  CONSTRAINT DS_TABLE_DB_NO FOREIGN KEY(DB_NO)
  REFERENCES DATA_BASE (DB_NO)
);
--为DS_TABLE表建立自增字段
exec pr_createidentitycolumn('DS_TABLE','SEQ_ID');
--DS_FIELD表的创建
create table DS_FIELD (
 SEQ_ID INT PRIMARY KEY ,
 TABLE_NO VARCHAR2(5) NOT NULL,
 FIELD_NO VARCHAR2(8) UNIQUE NOT NULL,
 FIELD_NAME VARCHAR2(45) NOT NULL,
 PROP_NAME VARCHAR2(45) ,
 FIELD_DESC VARCHAR2(45),
 FK_TABLE_NO VARCHAR2(5),
 FK_TABLE_NO2 VARCHAR2(5),
 FK_RELA_FIELD_NO VARCHAR2(8),
 FK_NAME_FIELD_NO VARCHAR2(8),
 FK_FILTER VARCHAR2(45),
 CODE_CLASS VARCHAR2(45),
 DEFAULT_VALUE VARCHAR2(45),
 FORMAT_MODE VARCHAR2(45),
 FORMAT_RULE VARCHAR2(45),
 ERROR_MSRG VARCHAR2(45),
 FIELD_PRECISION NUMBER(10),
 FIELD_SCALE NUMBER(10),
 DATA_TYPE NUMBER(10),
 IS_IDENTITY VARCHAR2(1),
 DISPLAY_LEN NUMBER(10),
 IS_MUST_FILL VARCHAR2(1),
 IS_PRIMARY_KEY VARCHAR2(1),
 constraint DS_FIELD_FK_TABLE_NO foreign key (FK_TABLE_NO) 
 references DS_TABLE (TABLE_NO),
 constraint DS_FIELD_FK_TABLE_NO2 foreign key (FK_TABLE_NO2) 
 references DS_TABLE (TABLE_NO),
 constraint DS_FIELD_TABLE_NO foreign key (TABLE_NO) 
 references DS_TABLE (TABLE_NO)
);
--为DS_FIELD表建立自增字段
exec pr_createidentitycolumn('DS_FIELD','SEQ_ID');
--CODE_CLASS表的创建
CREATE TABLE CODE_CLASS(
  SEQ_ID int PRIMARY KEY ,
  CLASS_NO varchar2(3) unique NOT NULL ,
  CLASS_DESC varchar2(200) NOT NULL ,
  SORT_NO VARCHAR2(45),
  CLASS_LEVEL VARCHAR2(10)
);
--为CODE_CLASS表建立自增字段
exec pr_createidentitycolumn('CODE_CLASS','SEQ_ID');
--CODE_ITEM表的创建
CREATE TABLE CODE_ITEM(
  SEQ_ID int PRIMARY KEY ,
  CLASS_NO varchar2(3)  NOT NULL ,
  CLASS_CODE varchar2(200)  NOT NULL ,
  CLASS_DESC varchar2(200)  NOT NULL ,
  SORT_NO VARCHAR2(45),
  constraint CODE_ITEM_CLASS_NO foreign key (CLASS_NO) 
  references CODE_CLASS (CLASS_NO)
);
--为CODE_ITEM表建立自增字段
exec pr_createidentitycolumn('CODE_ITEM','SEQ_ID');
--USER_ONLINE表的创建user_online 
create table USER_ONLINE(
  SEQ_ID INT PRIMARY KEY ,
  USER_ID INT NOT NULL,
  SESSION_TOKEN VARCHAR2(50) NOT NULL,
  LOGIN_TIME DATE NOT NULL ,
  USER_STATE CHAR(1) NOT NULL
);
--为DS_TABLE表建立自增字段
exec pr_createidentitycolumn('USER_ONLINE','SEQ_ID');
create table office_task(
  SEQ_ID INT PRIMARY KEY ,
  TASK_TYPE CHAR(1),
  INTERVAL INT,
  EXEC_TIME number(38),
  LAST_EXEC DATE,
  TASK_URL VARCHAR2(200),
  TASK_NAME VARCHAR2(200),
  TASK_DESC VARCHAR2(250),
  TASK_CODE VARCHAR2(20),
  USE_FLAG CHAR(1),
  SYS_TASK CHAR(1)
);

exec pr_createidentitycolumn('office_task','SEQ_ID');