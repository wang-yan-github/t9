alter table notify ALTER COLUMN TO_ID NVARCHAR(3000) ;
alter table notify ALTER COLUMN SUBJECT NVARCHAR(3000) ;
alter table notify ALTER COLUMN ATTACHMENT_ID NVARCHAR(3000) ;
alter table notify ALTER COLUMN ATTACHMENT_NAME NVARCHAR(3000) ;
alter table notify ALTER COLUMN READERS ntext ;
alter table notify ALTER COLUMN [USER_ID] NVARCHAR(3000) ;
alter table notify ALTER COLUMN PRIV_ID NVARCHAR(3000) ;


alter table email_body ALTER COLUMN SUBJECT NVARCHAR(3000) ;
alter table email_body ALTER COLUMN ATTACHMENT_ID NVARCHAR(3000) ;
alter table email_body ALTER COLUMN ATTACHMENT_NAME NVARCHAR(3000) ;
alter table email_body ALTER COLUMN TO_WEBMAIL NVARCHAR(3000) ;


alter table diary ALTER COLUMN ATTACHMENT_ID NVARCHAR(3000) ;
alter table diary ALTER COLUMN ATTACHMENT_NAME NVARCHAR(3000) ;
alter table diary ALTER COLUMN COMPRESS_CONTENT ntext ;
alter table diary ALTER COLUMN CONTENT ntext ;

alter table sms2_priv ALTER COLUMN TYPE_PRIV ntext ;
alter table sms2_priv ALTER COLUMN REMIND_PRIV ntext ;
alter table sms2_priv ALTER COLUMN OUT_PRIV ntext ;
alter table sms2_priv ALTER COLUMN SMS2_REMIND_PRIV ntext;

alter table news ALTER COLUMN ATTACHMENT_ID NVARCHAR(3000) ;
alter table news ALTER COLUMN ATTACHMENT_NAME NVARCHAR(3000) ;
alter table news ALTER COLUMN SUBJECT NVARCHAR(500) ;
alter table news ALTER COLUMN [USER_ID] NVARCHAR(3000) ;
alter table news ALTER COLUMN PRIV_ID NVARCHAR(3000) ;
alter table news ALTER COLUMN TO_ID NVARCHAR(3000) ;
alter table news ALTER COLUMN READERS ntext ;

alter table file_content ALTER COLUMN READERS ntext ;
alter table file_sort ALTER COLUMN USER_ID ntext ;
alter table file_sort ALTER COLUMN NEW_USER ntext ;
alter table file_sort ALTER COLUMN MANAGE_USER ntext ;
alter table file_sort ALTER COLUMN DOWN_USER ntext ;
alter table file_sort ALTER COLUMN SHARE_USER ntext ;
alter table file_sort ALTER COLUMN OWNER ntext ;
alter table file_sort ALTER COLUMN DEL_USER ntext ;
alter table active ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table active ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;

alter table bind_users ALTER COLUMN USER_ID_OTHER NVARCHAR(4000) ;
alter table bind_users ALTER COLUMN USER_DESC_OTHER NVARCHAR(4000) ;

alter table BOOK_INFO ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table BOOK_INFO ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table BOOK_MANAGER ALTER COLUMN MANAGER_ID NVARCHAR(4000) ;
alter table BOOK_MANAGER ALTER COLUMN MANAGE_DEPT_ID NVARCHAR(4000) ;
alter table BUDGET_APPLY ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table BUDGET_APPLY ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;

alter table confidential_content ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table confidential_content ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table confidential_content ALTER COLUMN ATTACHMENT_DESC NVARCHAR(4000) ;
alter table confidential_content ALTER COLUMN READERS NVARCHAR(4000) ;

alter table confidential_sort ALTER COLUMN MANAGE_USER NVARCHAR(4000) ;
alter table confidential_sort ALTER COLUMN DOWN_USER NVARCHAR(4000) ;
alter table confidential_sort ALTER COLUMN SHARE_USER NVARCHAR(4000) ;
alter table confidential_sort ALTER COLUMN OWNER NVARCHAR(4000) ;
alter table confidential_sort ALTER COLUMN NEW_USER NVARCHAR(4000) ;
alter table confidential_sort ALTER COLUMN USER_ID NVARCHAR(4000) ;

alter table dimension ALTER COLUMN MANAGE_USER NVARCHAR(4000) ;
alter table dimension ALTER COLUMN DOWN_USER NVARCHAR(4000) ;
alter table dimension ALTER COLUMN SHARE_USER NVARCHAR(4000) ;
alter table dimension ALTER COLUMN OWNER NVARCHAR(4000) ;
alter table dimension ALTER COLUMN NEW_USER NVARCHAR(4000) ;
alter table dimension ALTER COLUMN USER_ID NVARCHAR(4000) ;

alter table file_content ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table file_content ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table file_content ALTER COLUMN ATTACHMENT_DESC NVARCHAR(4000) ;
alter table file_content ALTER COLUMN READERS NVARCHAR(4000) ;

alter table file_sort ALTER COLUMN MANAGE_USER NVARCHAR(4000) ;
alter table file_sort ALTER COLUMN DOWN_USER NVARCHAR(4000) ;
alter table file_sort ALTER COLUMN SHARE_USER NVARCHAR(4000) ;
alter table file_sort ALTER COLUMN OWNER NVARCHAR(4000) ;
alter table file_sort ALTER COLUMN NEW_USER NVARCHAR(4000) ;
alter table file_sort ALTER COLUMN USER_ID NVARCHAR(4000) ;
alter table file_sort ALTER COLUMN DEL_USER NVARCHAR(4000) ;

alter table guest ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table guest ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_manager ALTER COLUMN DEPT_HR_MANAGER NVARCHAR(4000) ;

alter table hr_staff_care ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_care ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;

alter table hr_staff_contract ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_contract ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_incentive ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_incentive ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_info ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_info ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_labor_skills ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_labor_skills ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_learn_experience ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_learn_experience ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_leave ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_leave ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_license ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_license ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_reinstatement ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_reinstatement ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_relatives ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_relatives ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_staff_work_experience ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_staff_work_experience ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;
alter table hr_training_plan ALTER COLUMN ATTACHMENT_ID NVARCHAR(4000) ;
alter table hr_training_plan ALTER COLUMN ATTACHMENT_NAME NVARCHAR(4000) ;

alter table im_group_msg alter column ATTACHMENT_ID NVARCHAR(4000);
alter table im_group_msg alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table interface alter column ATTACHMENT_ID NVARCHAR(4000);
alter table interface alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table meeting_comment alter column ATTACHMENT_ID NVARCHAR(4000);
alter table meeting_comment alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table netdisk  alter column NEW_USER NVARCHAR(4000);
alter table netdisk  alter column MANAGE_USER NVARCHAR(4000);
alter table netdisk  alter column USER_ID NVARCHAR(4000);
alter table netdisk  alter column DOWN_USER NVARCHAR(4000);
alter table port  alter column USER_ID NVARCHAR(4000);
alter table port  alter column DEPT_ID NVARCHAR(4000);
alter table port  alter column PRIV_ID NVARCHAR(4000);
alter table source_nation alter column ATTACHMENT_ID NVARCHAR(4000);
alter table source_nation alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table source_org alter column ATTACHMENT_ID NVARCHAR(4000);
alter table source_org alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table source_person alter column ATTACHMENT_ID NVARCHAR(4000);
alter table source_person alter column ATTACHMENT_NAME NVARCHAR(4000);

alter table work_plan alter column ATTACHMENT_ID NVARCHAR(4000);
alter table work_plan alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table work_plan alter column ATTACHMENT_COMMENT NVARCHAR(4000);
alter table work_plan alter column REMARK NVARCHAR(4000);
alter table vehicle alter column ATTACHMENT_ID NVARCHAR(4000);
alter table vehicle alter column ATTACHMENT_NAME NVARCHAR(4000);
alter table vote_title alter column ATTACHMENT_ID NVARCHAR(4000);
alter table vote_title  alter column ATTACHMENT_NAME NVARCHAR(4000);

alter table webmail_body alter column TO_MAIL NVARCHAR(4000);
alter table webmail_body  alter column CC_MAIL NVARCHAR(4000);

alter table work_person alter column ATTACHMENT_ID NVARCHAR(4000);
alter table work_person  alter column ATTACHMENT_NAME NVARCHAR(4000);
