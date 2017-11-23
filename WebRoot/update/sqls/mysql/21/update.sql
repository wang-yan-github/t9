ALTER TABLE flow_form_type ADD  COLUMN VERSION_NO INT(11);
ALTER TABLE flow_form_type ADD  COLUMN VERSION_TIME DATETIME;
ALTER TABLE flow_form_type ADD  COLUMN FORM_ID INT(11);
ALTER TABLE flow_run ADD  COLUMN FORM_VERSION INT(11);
 

update flow_form_type set VERSION_NO = 1;
update flow_form_type set FORM_ID = 0;
update flow_run set FORM_VERSION = 1;