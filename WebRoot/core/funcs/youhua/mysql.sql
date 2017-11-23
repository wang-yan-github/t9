ALTER TABLE flow_run_data ADD INDEX Index_3(RUN_ID);
ALTER TABLE flow_run ADD INDEX FLOW_RUN_Index(RUN_ID, FLOW_ID, BEGIN_USER, DEL_FLAG);
ALTER TABLE flow_run_log ADD INDEX flow_run_log_INDEX(RUN_ID, FLOW_ID, PRCS_ID, USER_ID);
ALTER TABLE flow_process ADD INDEX flow_process_index(FLOW_SEQ_ID, PRCS_ID);
ALTER TABLE flow_form_item ADD INDEX flow_form_item_index(FORM_ID, ITEM_ID);
ALTER TABLE flow_type ADD INDEX FLOW_TYPE_INDEX(FLOW_SORT, FLOW_TYPE);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS(RUN_ID, PRCS_FLAG, PRCS_ID, OP_FLAG, USER_ID, FLOW_PRCS, CHILD_RUN);


ALTER TABLE flow_run ADD INDEX FLOW_RUN_runId_Index(RUN_ID);
ALTER TABLE flow_run ADD INDEX FLOW_RUN_flowId_Index(FLOW_ID);
ALTER TABLE flow_run ADD INDEX FLOW_RUN_beginUser_Index(BEGIN_USER);
ALTER TABLE flow_run ADD INDEX FLOW_RUN_delFlag_Index( DEL_FLAG);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_runId(RUN_ID);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_prcsFlag( PRCS_FLAG);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_opFlag(OP_FLAG);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_prcsId(PRCS_ID);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_userId( USER_ID);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_flowPrcs(FLOW_PRCS);
ALTER TABLE flow_run_prcs ADD INDEX FLOW_RUN_PRCS_childRun( CHILD_RUN);
ALTER TABLE flow_type ADD INDEX FLOW_TYPE_flowSort_INDEX(FLOW_SORT);
ALTER TABLE flow_type ADD INDEX FLOW_TYPE_flowType_INDEX(FLOW_TYPE);
ALTER TABLE flow_run_log ADD INDEX flow_run_log_runId_INDEX(RUN_ID);
ALTER TABLE flow_run_log ADD INDEX flow_run_log_flowId_INDEX( FLOW_ID);
ALTER TABLE flow_run_log ADD INDEX flow_run_log_prcsId_INDEX(PRCS_ID);
ALTER TABLE flow_run_log ADD INDEX flow_run_log_userId_INDEX( USER_ID);
ALTER TABLE flow_process ADD INDEX flow_process_seqId_index(FLOW_SEQ_ID);
ALTER TABLE flow_process ADD INDEX flow_process_prcsId_index(PRCS_ID);
ALTER TABLE flow_form_item ADD INDEX flow_form_item_formId_index(FORM_ID);
ALTER TABLE flow_form_item ADD INDEX flow_form_item_itemId_index(ITEM_ID);

ALTER TABLE doc_flow_run_data ADD INDEX doc_flow_run_Index_3(RUN_ID);
ALTER TABLE doc_run ADD INDEX DOC_RUN_Index(RUN_ID, FLOW_ID, BEGIN_USER, DEL_FLAG);
ALTER TABLE doc_flow_run_prcs ADD INDEX doc_FLOW_RUN_PRCS(RUN_ID, PRCS_FLAG, PRCS_ID, OP_FLAG, USER_ID, FLOW_PRCS, CHILD_RUN);
ALTER TABLE doc_flow_type ADD INDEX doc_FLOW_TYPE_INDEX(FLOW_SORT, FLOW_TYPE);
ALTER TABLE doc_flow_run_log ADD INDEX doc_flow_run_log_INDEX(RUN_ID, FLOW_ID, PRCS_ID, USER_ID);
ALTER TABLE doc_flow_process ADD INDEX doc_flow_process_index(FLOW_SEQ_ID, PRCS_ID);
ALTER TABLE doc_flow_form_item ADD INDEX doc_flow_form_item_index(FORM_ID, ITEM_ID);

create index email_delete_flag on email (DELETE_FLAG);
create index BODY_ID on email (BODY_ID);
create index email_from_id on email_body (FROM_ID);
create index email_to_id on email (TO_ID);
create index email_to_id on email (TO_ID);
create index email_send_time on email_body (SEND_TIME);
create index email_is_webmail on email_body (IS_WEBMAIL);
create index email_from_webmail on email_body (FROM_WEBMAIL);
create index webmail_body_bodyId on webmail_body (BODY_ID);
create index webmail_body_deleteFlag on webmail_body (DELETE_FLAG);

create index index_sms_body_formid on sms_body (FROM_ID);
create index index_sms_body_smstype on sms_body (SMS_TYPE);
create index index_sms_body_sendtime on sms_body (SEND_TIME);

create index index_sms_toid on sms (TO_ID);
create index index_sms_remindflag on sms (REMIND_FLAG);
create index index_sms_deleteflag on sms (DELETE_FLAG);
create index index_sms_bodyseqid on sms (BODY_SEQ_ID);
create index index_sms_remindtime on sms (REMIND_TIME);


ALTER TABLE notify ADD INDEX publish_index(PUBLISH),
 ADD INDEX begin_date_index(BEGIN_DATE),
 ADD INDEX end_date_index(END_DATE),
 ADD INDEX type_id_index(TYPE_ID);
 ALTER TABLE news ADD INDEX publish_index(PUBLISH);

 ALTER TABLE message ADD INDEX TO_ID_INDEX(TO_ID),
 ADD INDEX REMIND_FLAG_INDEX(REMIND_FLAG),
 ADD INDEX DELETE_FLAG_INDEX(DELETE_FLAG),
 ADD INDEX BODY_SEQ_ID_INDEX(BODY_SEQ_ID),
 ADD INDEX REMIND_TIME_INDEX(REMIND_TIME);
ALTER TABLE message_body ADD INDEX SEND_TIME_INDEX(SEND_TIME),
 ADD INDEX FROM_ID_INDEX(FROM_ID);