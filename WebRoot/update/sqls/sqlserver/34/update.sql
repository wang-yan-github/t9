ALTER TABLE flow_form_item ADD IMG_HEIGHT VARCHAR(200);
ALTER TABLE flow_form_item ADD IMG_WIDTH VARCHAR(200);
insert into port (FILE_NAME, DEPT_ID, STATUS, VIEW_TYPE, MODULE_LINES, MODULE_SCROLL) values ('工作日志.js', 0, 0, 3, 5, 0);
insert into sys_para (para_name , para_value) values ('FLOW_ACTION', '1,2');
 
