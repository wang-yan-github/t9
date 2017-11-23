<?php
include_once ("../header.php");
include_once("run_role.php");
include_once("inc/utility_all.php");

if(!prcs_role($FLOW_ID,1))
{
	echo "NOCREATEPRIV";
   exit;
}

//--- 自动编号---
$CUR_TIME=date("Y-m-d H:i:s",time());

$query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
$cursor1= exequery($connection,$query);
if($ROW=mysql_fetch_array($cursor1))
{
	$FLOW_NAME=$ROW["FLOW_NAME"];
	$FLOW_TYPE=$ROW["FLOW_TYPE"];
	$FORM_ID=$ROW["FORM_ID"];
	$AUTO_NAME = $ROW["AUTO_NAME"];
	$AUTO_NUM = $ROW["AUTO_NUM"];
	$AUTO_LEN = $ROW["AUTO_LEN"];
	$AUTO_EDIT = $ROW["AUTO_EDIT"];
	$FORCE_PRE_SET = $ROW["FORCE_PRE_SET"];
	$FLOW_SORT = $ROW["FLOW_SORT"];
	$FLOW_DESC = $ROW["FLOW_DESC"];
	//$FLOW_DESC=str_replace("\n","<br>",$FLOW_DESC);
	$ATTACHMENT_ID   = $ROW["ATTACHMENT_ID"];
	$ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
}

if($AUTO_NAME=="")
{
	$RUN_NAME=$FLOW_NAME."(".$CUR_TIME.")";
}
else
{
	$RUN_NAME=$AUTO_NAME;
	$CUR_YEAR=date("Y",time());
	$CUR_MON=date("m",time());
	$CUR_DAY=date("d",time());
	$CUR_HOUR = date('H');
	$CUR_MINITE = date('i');
	$CUR_SECOND = date('s');

	$query = "SELECT USER_NAME from USER WHERE USER_ID='$LOGIN_USER_ID'";
	$cursor1= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor1))
		$USER_NAME=$ROW["USER_NAME"];

	$query = "SELECT SORT_NAME from FLOW_SORT WHERE SORT_ID='$FLOW_SORT'";
	$cursor1= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor1))
		$SORT_NAME=$ROW["SORT_NAME"];

	$query = "SELECT DEPT_NAME from DEPARTMENT WHERE DEPT_ID='$LOGIN_DEPT_ID'";
	$cursor1= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor1))
		$DEPT_NAME=$ROW["DEPT_NAME"];

	$LONG_DEPT_NAME=dept_long_name($LOGIN_DEPT_ID);

	$query = "SELECT PRIV_NAME from USER_PRIV WHERE USER_PRIV='$LOGIN_USER_PRIV'";
	$cursor1= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor1))
		$PRIV_NAME=$ROW["PRIV_NAME"];

	$AUTO_NUM++;
	$AUTO_NUM = str_pad($AUTO_NUM,$AUTO_LEN, "0",STR_PAD_LEFT);

	/*
	 * {Y}： 表示年
	* {M}： 表示月
	* {D}： 表示日
	* {H}： 表示时
	* {I}： 表示分
	* {S}： 表示秒
	* {F}： 表示流程名
	* {FS}：表示流程分类名
	* {U}： 表示用户姓名
	* {SD}：表示短部门名
	* {LD}：表示长部门名
	* {R}： 表示角色
	* {N}： 表示编号
	* {RUN}:表示流水号
	*/
	$SEARCH_ARRAY = array("{Y}","{M}","{D}","{H}","{I}","{S}","{F}","{FS}","{U}","{SD}","{LD}","{R}","{N}");
	$REPLACE_ARRAY = array($CUR_YEAR,$CUR_MON,$CUR_DAY,$CUR_HOUR,$CUR_MINITE,$CUR_SECOND,$FLOW_NAME,$SORT_NAME,$USER_NAME,$DEPT_NAME,$LONG_DEPT_NAME,$PRIV_NAME,$AUTO_NUM);
	$RUN_NAME=str_replace($SEARCH_ARRAY,$REPLACE_ARRAY,$RUN_NAME);
}
ob_clean();
?>
<div class="container">
   <div class="tform tformshow">
   		<form action="new_submit.php" method="post" name="form1" id="new_from" onsubmit="return false;">
   		<?
if($AUTO_EDIT==2||$AUTO_EDIT==4)
{
?>
				<div class="read_detail read_detail_header"><?=_("前缀：")?></div>
				<div class="read_detail">
					<input type="text" name="RUN_NAME_LEFT"><br />
				</div>
<?
}
?>
   			<div class="read_detail read_detail_header"><?=_("填写该工作的名称或文号")?></div>
   			<div class="read_detail">
					<input type="text" value="<?=$RUN_NAME?>" name="RUN_NAME" style="width:80%" <?if($AUTO_EDIT!=1)echo "readOnly";?>>
<?
if($AUTO_EDIT==1)
{
?>
        			<span class="ccombtn" onclick="document.form1.RUN_NAME.value=''"><span><?=_("清空")?></span></span>
<?
}
?>
   			</div>
<?
if($AUTO_EDIT==3||$AUTO_EDIT==4)
{
?>
					<div class="read_detail read_detail_header"><?=_("后缀：")?></div>
					<div class="read_detail">
        				<input type="text" name="RUN_NAME_RIGHT">
        			</div>
<?
}
?>
			<input type='hidden' value="<?=$FLOW_ID?>" name="FLOW_ID">
			<input type='hidden' value="1" name="AUTO_NEW">
   		</form>
   	</div>
</div>
<script>
force_pre_set = '<?=$FORCE_PRE_SET?>';
</script>