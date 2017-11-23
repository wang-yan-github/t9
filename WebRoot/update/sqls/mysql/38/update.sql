
CREATE TABLE doc_from_dept (
  SEQ_ID int(10) unsigned NOT NULL auto_increment,
  FROM_DEPT_NAME varchar(100) default NULL,
  FROM_DEPT_ID int(10) unsigned default NULL,
  PRIMARY KEY  (SEQ_ID)
);
ALTER TABLE documents_type ADD COLUMN type_no INTEGER UNSIGNED DEFAULT 0 ;
ALTER TABLE doc_word ADD COLUMN WORD_NO INTEGER UNSIGNED DEFAULT 0 ;
CREATE TABLE CHAT_GROUP (
  SEQ_ID INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  USER_ID INTEGER UNSIGNED,
  GROUP_NAME VARCHAR(100),
  USERS TEXT,
  PRIMARY KEY (SEQ_ID)
)
ENGINE = InnoDB;
CREATE TABLE mobile_seal (
  SEQ_ID int(11) NOT NULL auto_increment,
  DEVICE_LIST text,
  SEAL_DATA mediumtext,
  DEPT_ID int(11) default NULL,
  SEAL_NAME varchar(200) default NULL,
  CREATE_TIME datetime default NULL,
  CREATE_USER text,
  PRIMARY KEY  (SEQ_ID)
) ENGINE=InnoDB;
CREATE TABLE mobile_device (
  SEQ_ID int(11) NOT NULL auto_increment,
  UID int(11) default NULL,
  SUBMIT_TIME datetime default NULL,
  DEVICE_TYPE int(1) default NULL,
  DEVICE_INFO text,
  MD5_CHECK char(32) default NULL,
  DEVICE_NAME varchar(100) default NULL,
  PRIMARY KEY  (SEQ_ID),
  KEY DEVICE_NAME (DEVICE_NAME)
)ENGINE=InnoDB;
INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES
 ('6018','移动签章设置','/core/funcs/demo/index.jsp','sys.gif','0'); 
