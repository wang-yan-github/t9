<?php
include_once("../header.php");
include_once("inc/utility_all.php");
ob_clean();
$SEARCH_NAME = iconv("utf-8", $MYOA_CHARSET."//IGNORE",$SEARCH_NAME);
$query = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and PRCS_FLAG < '5' and DEL_FLAG=0 and FLOW_RUN.RUN_NAME like '%$SEARCH_NAME%'";
$TOTAL_ITEMS = resultCount ( $query );
if ($TOTAL_ITEMS > 0) {
	if(!$CURRITERMS) {
		$CURRITERMS = 0;
	}
	$query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and DEL_FLAG=0 and PRCS_FLAG<'5' and FLOW_RUN.RUN_NAME like '%$SEARCH_NAME%' order by FLOW_RUN_PRCS.CREATE_TIME desc limit $CURRITERMS,$PAGE_SIZE";
	$cursor = exequery ( $connection, $query );
	while ( $ROW = mysql_fetch_array ( $cursor ) ) {
		$PRCS_ID = $ROW ["PRCS_ID"];
		$RUN_ID = $ROW ["RUN_ID"];
		$FLOW_ID = $ROW ["FLOW_ID"];
		$PRCS_FLAG = $ROW ["PRCS_FLAG"];
		$FLOW_PRCS = $ROW ["FLOW_PRCS"];
		$OP_FLAG = $ROW ["OP_FLAG"];
		$CREATE_TIME = $ROW ["CREATE_TIME"];
		$FLOW_ID = $ROW ["FLOW_ID"];
		$RUN_NAME = $ROW ["RUN_NAME"];
		$RUN_NAME = str_replace($SEARCH_NAME, "<font color='red'>$SEARCH_NAME</font>", $RUN_NAME);
		$FLOW_NAME = $ROW ["FLOW_NAME"];
		$FLOW_TYPE = $ROW ["FLOW_TYPE"];
		
		if ($OP_FLAG == "1")
			$OP_FLAG_DESC = _ ( "主办" );
		else
			$OP_FLAG_DESC = _ ( "会签" );
		
		if ($PRCS_FLAG == "1") {
			// LP 2012/4/18 10:58:25 增加未接收工作高亮处理
			$STATUS = _ ( "未接收" );
			$COLOR = "#FFBC18";
			$Class = " active";
		} else if ($PRCS_FLAG == "2") {
			$STATUS = _ ( "已接收" );
			$COLOR = "#50C625";
			$Class = "received";
		} else if ($PRCS_FLAG == "3") {
			$STATUS = _ ( "已转交" );
			$COLOR = "#F4A8BD";
			$Class = "referred";
		} else if ($PRCS_FLAG == "4") {
			$STATUS = _ ( "已办结" );
			$COLOR = "#F4A8BD";
			$Class = "gone";
		}
		
		$STATUS = "<span style='color:".$COLOR."'>$STATUS</span>";
		
		if ($FLOW_TYPE == "1") {
			$query = "SELECT PRCS_NAME,FEEDBACK from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS'";
			$cursor1 = exequery ( $connection, $query );
			if ($ROW = mysql_fetch_array ( $cursor1 )) {
				$PRCS_NAME = sprintf ( _ ( "第%s步：" ), $PRCS_ID ) . $ROW ["PRCS_NAME"];
				$FEEDBACK = $ROW ["FEEDBACK"];
			}
		} else {
			$PRCS_NAME = sprintf ( _ ( "第%s步" ), $PRCS_ID );
			$FEEDBACK = 0;
		}
		?>
<li class="<?=$fix_for_pad['list-li-style'].$Class?>"
	q_id="<?=$CREATE_TIME?>" q_run_id="<?=$RUN_ID?>"
	q_flow_id="<?=$FLOW_ID?>" q_prcs_id="<?=$PRCS_ID?>"
	q_flow_prcs="<?=$FLOW_PRCS?>" q_op_flag="<?=$OP_FLAG?>">
	<h3>[<?=$RUN_ID?>] - <?=$FLOW_NAME?> - <?=$RUN_NAME?></h3>
	<p class="grapc"><?=$STATUS?> <?=$PRCS_NAME?> <?=$OP_FLAG_DESC?></p> <span
	class="ui-icon-rarrow"></span>
</li>
<?
	} // while
	//echo '</ul>';
} else {
	//echo '</ul>';
	echo '<div class="no_msg">' . _ ( "暂无待处理工作流！" ) . '</div>';
}
?>
<script>
nomoredata_15 = <? echo $PAGE_SIZE >= ($TOTAL_ITEMS - $CURRITERMS) ? "true" : "false"; ?>;
noshowPullUp_15 = <? echo $PAGE_SIZE >= ($TOTAL_ITEMS - $CURRITERMS) ? "true" : "false"; ?>;
</script>