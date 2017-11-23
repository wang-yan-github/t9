ALTER TABLE doc_rec_register ADD DELETE_FLAG VARCHAR(5);

INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES
 ('600831','周活动安排设置','/core/funcs/system/active/index.jsp','sys.gif','0'); 

CREATE TABLE ARCHIVE_TABLES (
 SEQ_ID int   NOT NULL IDENTITY(1, 1) PRIMARY KEY,
  MODULE VARCHAR(45),
  ARCHIVE_DB VARCHAR(100),
  ARCHIVE_DESC VARCHAR(100),
  TABLE_POSTFIX VARCHAR(45),
  DATA_VERSION VARCHAR(45),
  ARCHIVE_TIME int DEFAULT 0,
  TABLE_NAME_STR TEXT
);
 