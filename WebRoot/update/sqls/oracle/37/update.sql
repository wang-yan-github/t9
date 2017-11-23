ALTER TABLE doc_rec_register ADD (DELETE_FLAG VARCHAR2(5));
INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES
 ('600831','周活动安排设置','/core/funcs/system/active/index.jsp','sys.gif','0'); 

CREATE TABLE ARCHIVE_TABLES (
  SEQ_ID NUMBER primary key,
  MODULE VARCHAR2(45),
  ARCHIVE_DB VARCHAR2(100),
  ARCHIVE_DESC VARCHAR2(100),
  TABLE_POSTFIX VARCHAR2(45),
  DATA_VERSION VARCHAR2(45),
  ARCHIVE_TIME NUMBER DEFAULT 0,
  TABLE_NAME_STR CLOB
);
exec pr_CreateIdentityColumn('ARCHIVE_TABLES','SEQ_ID');
 