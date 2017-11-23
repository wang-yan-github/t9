<?
include_once("auth.php");
include_once("./inc/funcs.php");
include_once("inc/utility_all.php");

$CUR_TIME = time();
$CUR_DATE=date("Y-m-d",time());
$MODULE_ARRAY_COUNT = array();
$MODULE_ARRAY_COUNT_STR = '';
if(find_id($LOGIN_FUNC_STR, "3"))
{
   $TOTAL_ITEMS = 0;
   $query = "SELECT count(*) from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME' and REMIND_FLAG!='0'";
   $TOTAL_ITEMS = resultCount($query);
   $MODULE_ARRAY_COUNT['sms'] = $TOTAL_ITEMS;
   $MODULE_ARRAY_COUNT_STR .= '"sms":"'.$TOTAL_ITEMS.'",';
}

if(find_id($LOGIN_FUNC_STR, "1"))
{
   $TOTAL_ITEMS = 0;
   $query = "SELECT count(*) from EMAIL,EMAIL_BODY where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') and READ_FLAG!='1'";
   $TOTAL_ITEMS = resultCount($query);
   $MODULE_ARRAY_COUNT['email'] = $TOTAL_ITEMS;
   $MODULE_ARRAY_COUNT_STR .= '"email":"'.$TOTAL_ITEMS.'",';
}

if(find_id($LOGIN_FUNC_STR, "4"))
{
   $TOTAL_ITEMS = 0;
   $query = "SELECT count(*) from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and BEGIN_DATE<='$CUR_DATE' and (END_DATE>='$CUR_DATE' or END_DATE='0000-00-00') and PUBLISH='1' and not find_in_set('$LOGIN_USER_ID',READERS)";
   $TOTAL_ITEMS = resultCount($query);
   $MODULE_ARRAY_COUNT['notify'] = $TOTAL_ITEMS;
   $MODULE_ARRAY_COUNT_STR .= '"notify":"'.$TOTAL_ITEMS.'",';
}

if(find_id($LOGIN_FUNC_STR, "5"))
{
   $TOTAL_ITEMS = 0;
   $query = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and DEL_FLAG='0' and PRCS_FLAG in (1,2) and CHILD_RUN='0'";
   $TOTAL_ITEMS = resultCount($query);
   $MODULE_ARRAY_COUNT['workflow'] = $TOTAL_ITEMS;
   $MODULE_ARRAY_COUNT_STR .= '"workflow":"'.$TOTAL_ITEMS.'",';
}

if(find_id($LOGIN_FUNC_STR, "147"))
{
   $TOTAL_ITEMS = 0;
   $query = "SELECT count(*) from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and not find_in_set('$LOGIN_USER_ID',READERS)";
   $TOTAL_ITEMS = resultCount($query);
   $MODULE_ARRAY_COUNT['news'] = $TOTAL_ITEMS;
   $MODULE_ARRAY_COUNT_STR .= '"news":"'.$TOTAL_ITEMS.'",';
}
   
if(find_id($LOGIN_FUNC_STR, "8"))
{
   $TOTAL_ITEMS = 0;
   $query="SELECT COUNT(*) from CALENDAR where USER_ID='$LOGIN_USER_ID' and CAL_TIME<'$CUR_TIME' and to_days(CAL_TIME)<=to_days('$CUR_DATE') and to_days(END_TIME)>=to_days('$CUR_DATE') and OVER_STATUS=0";
   $TOTAL_ITEMS = resultCount($query);
   $MODULE_ARRAY_COUNT['calendar'] = $TOTAL_ITEMS;
   $MODULE_ARRAY_COUNT_STR .= '"calendar":"'.$TOTAL_ITEMS.'",';
}

//2012/5/28 2:24:54 lp 供定时查询使用
if($ACTION == "GetCount")
{
   ob_end_clean();
   echo '{'.td_trim($MODULE_ARRAY_COUNT_STR).'}';
}
?>