<?
include_once ("../header.php");
include_once ("run_role.php");
include_once ("inc/utility_all.php");
include_once ("general/workflow/list/turn/condition.php");
ob_clean ();
?>
<div class="container">
	<div class="tform">
		<div class="read_detail read_detail_header"><?=_("请选择回退步骤")?></div>
		<div class="read_detail">
<?
$query = "select FLOW_RUN_PRCS.FLOW_PRCS,PRCS_NAME,FLOW_PROCESS.FLOW_ID FROM FLOW_RUN,FLOW_PROCESS,FLOW_RUN_PRCS WHERE FLOW_RUN.RUN_ID='$RUN_ID' AND FLOW_RUN.FLOW_ID=FLOW_PROCESS.FLOW_ID AND FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID AND FLOW_RUN_PRCS.FLOW_PRCS=FLOW_PROCESS.PRCS_ID AND FLOW_RUN_PRCS.FLOW_PRCS!='$FLOW_PRCS' and PRCS_FLAG=4 GROUP BY FLOW_RUN_PRCS.FLOW_PRCS";
$cursor = exequery($connection,$query);
while($ROW=mysql_fetch_array($cursor))
{
	$FLOW_ID=$ROW["FLOW_ID"];
	$FLOW_PRCS1=$ROW["FLOW_PRCS"];
	$PRCS_NAME=$ROW["PRCS_NAME"];
	echo "<input type='radio' name='PRCS' id='$FLOW_PRCS1' value='$FLOW_PRCS1'><label for='$FLOW_PRCS1'>$PRCS_NAME</label>";
}
?>
		</div>
	</div>
	<div class="tform">
		<div class="read_detail read_detail_header"><?=_("请输入会签意见")?></div>
		<div class="read_detail">
			<textarea name="CONTENT_BACK" id="CONTENT_BACK" rows="3" wrap="on"></textarea>
		</div>
	</div>
</div>