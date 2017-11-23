<?
include_once ("../header.php");
include_once ("run_role.php");
include_once ("inc/utility_all.php");
include_once ("general/workflow/list/turn/condition.php");
ob_clean ();
$RUN_ID = intval ( $RUN_ID );
$PRCS_ID = intval ( $PRCS_ID );
$FLOW_ID = intval ( $FLOW_ID );
$FLOW_PRCS = intval ( $FLOW_PRCS );
// 判断权限
$RUN_ROLE = run_role ( $RUN_ID, $PRCS_ID );
$query = "select TOP_FLAG FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND PRCS_ID='$PRCS_ID' AND FLOW_PRCS='$FLOW_PRCS' AND USER_ID='$LOGIN_USER_ID'";
$cursor = exequery ( $connection, $query );
if ($ROW = mysql_fetch_array ( $cursor ))
	$TOP_FLAG = $ROW ["TOP_FLAG"];

if ($TOP_FLAG != 2) {
	if (! find_id ( $RUN_ROLE, 2 )) {
		echo "NOEDITPRIV";
		exit ();
	}
} else {
	// 无主办会签
	if (! find_id ( $RUN_ROLE, 4 )) {
		echo "NOSIGNFLOWPRIV";
		exit ();
	}
}

$query = "SELECT * from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
$cursor1 = exequery ( $connection, $query );
if ($ROW = mysql_fetch_array ( $cursor1 )) {
	$FLOW_NAME = $ROW ["FLOW_NAME"];
	$FLOW_TYPE = $ROW ["FLOW_TYPE"];
	$FORM_ID = $ROW ["FORM_ID"];
}

$query = "SELECT RUN_NAME,USER_NAME,PARENT_RUN from FLOW_RUN LEFT JOIN USER ON(FLOW_RUN.BEGIN_USER=USER.USER_ID) WHERE RUN_ID='$RUN_ID'";
$cursor = exequery ( $connection, $query );
if ($ROW = mysql_fetch_array ( $cursor )) {
	$RUN_NAME = $ROW ["RUN_NAME"];
	$BEGIN_USER_NAME = $ROW ["USER_NAME"];
	$PARENT_RUN = $ROW ["PARENT_RUN"];
}

$query = "SELECT PRCS_NAME,PRCS_OUT,PRCS_OUT_SET,SYNC_DEAL,TURN_PRIV,PRCS_TO,USER_LOCK,TOP_DEFAULT,GATHER_NODE,CONDITION_DESC,REMIND_FLAG,VIEW_PRIV from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' and PRCS_ID='$FLOW_PRCS'";
$cursor = exequery ( $connection, $query );
if ($ROW = mysql_fetch_array ( $cursor )) {
	$PRCS_NAME = $ROW ["PRCS_NAME"];
	
	$PRCS_OUT = $ROW ["PRCS_OUT"];
	$PRCS_OUT_SET = $ROW ["PRCS_OUT_SET"];
	$SYNC_DEAL = $ROW ["SYNC_DEAL"]; // 并发
	$TURN_PRIV = $ROW ["TURN_PRIV"]; // 强制转交
	$GATHER_NODE = $ROW ["GATHER_NODE"]; // 强制合并
	$PRCS_TO = $ROW ["PRCS_TO"];
	$PRCS_TO = str_replace ( ",,", ",", $PRCS_TO );
	$CONDITION_DESC = $ROW ["CONDITION_DESC"];
	$CONDITION_ARR = explode ( "\n", $CONDITION_DESC );
	$CONDITION_DESC = $CONDITION_ARR [1];
	$REMIND_FLAG = $ROW ["REMIND_FLAG"];
	$VIEW_PRIV_PRCS = $ROW ["VIEW_PRIV"];
	$AUTO_TYPE = $ROW ["AUTO_TYPE"];
}

// ------------------------------------------- 转出条件检查
// ----------------------------------
$FORM_DATA = get_form ( $FORM_ID, $RUN_ID );
$NOT_PASS = check_condition ( $FORM_DATA, $PRCS_OUT, $PRCS_OUT_SET, $RUN_ID, $PRCS_ID );
if (substr ( $NOT_PASS, 0, 5 ) == "SETOK")
	$NOT_PASS = "";
	
	// --- 监控人强制转交不限制转出条件 ---
if ($OP == "MANAGE")
	$NOT_PASS = "";

if ($NOT_PASS != "") {
	$NOT_PASS = str_replace ( "\n", "<br>", $NOT_PASS );
	echo '<div class="no_msg">' . $NOT_PASS . '</div>';
	exit ();
}
?>
<div class="container">
	<div class="tform tformshow">
		<div class="read_detail">
			<em><?=_("工作名称/文号：")?></em><?=$RUN_NAME?></div>
		<div class="read_detail">
			<em><?=_("发起人：")?></em><?=$BEGIN_USER_NAME?></div>
<?

if ($FLOW_TYPE == 1) {
	if ($PRCS_ID_NEXT == "") {
		?>
   <div class="read_detail read_detail_header"><?=_("请选择下一步骤：")?></div>
<?
		$query = "SELECT PRCS_NAME,PRCS_TO,REMIND_FLAG from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' and PRCS_ID='$FLOW_PRCS'";
		$cursor = exequery ( $connection, $query );
		if ($ROW = mysql_fetch_array ( $cursor )) {
			$PRCS_NAME = $ROW ["PRCS_NAME"];
			$PRCS_TO = $ROW ["PRCS_TO"];
			$PRCS_TO = str_replace ( ",,", ",", $PRCS_TO );
			$PRCS_TO = rtrim ( $PRCS_TO, "," );
			$REMIND_FLAG = $ROW ["REMIND_FLAG"];
		}
		
		// 未定义下一步骤,自动判断
		if ($PRCS_TO == "") {
			$query = "SELECT MAX(PRCS_ID) from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID'";
			$PRCS_MAX = 0;
			$cursor = exequery ( $connection, $query );
			if ($ROW = mysql_fetch_array ( $cursor ))
				$PRCS_MAX = $ROW [0];
			
			if ($FLOW_PRCS != $PRCS_MAX)
				$PRCS_TO = $FLOW_PRCS + 1;
			else
				$PRCS_TO = "0";
		}
		
		if ($GATHER_NODE == 1) {
			$PRE_PRCS_ID = $PRCS_ID - 1;
			$cout = "0";
			// 并发合并算法：递归在FLOW_RUN_PRCS表中寻找距上一个并发节点路径，未能到达所有并发步骤节点 则不能进行转交
			$query = "select PRCS_ID FROM FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND ( FIND_IN_SET('$FLOW_PRCS',PRCS_TO) || (PRCS_ID=" . ($FLOW_PRCS - 1) . " AND PRCS_TO=''))";
			$cursor = exequery ( $connection, $query );
			while ( $ROW = mysql_fetch_array ( $cursor ) ) {
				$query1 = "select PRCS_FLAG from FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND FLOW_PRCS='$ROW[PRCS_ID]' and OP_FLAG=1";
				$cursor1 = exequery ( $connection, $query1 );
				if ($ROW1 = mysql_fetch_array ( $cursor1 )) {
					$PRCS_FLAG1 = $ROW1 ["PRCS_FLAG"];
					if ($PRCS_FLAG1 <= 2)
						$CANNOT_TURN = true;
				} else
					$CANNOT_TURN = false;
				
				if ($cout == "1")
					continue;
				
				if (! find_id ( $PARENT_STR, $ROW ["PRCS_ID"] ) && $CANNOT_TURN) {
					echo '<div class="no_msg">' . _ ( "此步骤为强制合并步骤，尚有步骤未转交至此步骤，您不能继续转交下一步！" ) . '</div>';
					$cout = "1";
					exit ();
				}
			} // end while
		} // end if
		
		$count = 0;
		if ($PRCS_TO == 0) 		// 结束流程
		{
			echo '<form action="#" method="post" name="form1" onsubmit="return false">';
			echo '<div class="read_detail"><input type="checkbox" name="PRCS_ID_NEXT" onclick="select_turn(this)" id="prcs_0" value="0" checked><label for="prcs_0">' . _ ( "结束流程" ) . '</label></div>';
			$count ++;
			$COUNT_PRCS_OK ++;
		} else {
			echo '<form action="#" method="post" name="form1" onsubmit="return false">';
			$query1 = "SELECT * from FLOW_PROCESS where FLOW_ID='$FLOW_ID' and PRCS_ID IN ($PRCS_TO)";
			$cursor1 = exequery ( $connection, $query1 );
			$COUNT_PRCS_OK = 0;
			while ( $ROW = mysql_fetch_array ( $cursor1 ) ) {
				$count ++;
				$PRCS_ID_NEXT = $ROW ["PRCS_ID"];
				$PRCS_NAME = $ROW ["PRCS_NAME"];
				$PRCS_IN = $ROW ["PRCS_IN"];
				$PRCS_IN_SET = $ROW ["PRCS_IN_SET"];
				$CONDITION_DESC = $ROW ["CONDITION_DESC"];
				$CONDITION_ARR = explode ( "\n", $CONDITION_DESC );
				$CONDITION_DESC = $CONDITION_ARR [0];
				
				$USER_LOCK = $ROW ["USER_LOCK"];
				$TOP_DEFAULT = $ROW ["TOP_DEFAULT"];
				$CHILD_FLOW = $ROW ["CHILD_FLOW"];
				$AUTO_BASE_USER = $ROW ["AUTO_BASE_USER"];
				
				$PRCS_IN_DESC = str_replace ( "\n", "<br>", $PRCS_IN );
				$PRCS_IN_DESC = str_replace ( "'include'", _ ( "'包含'" ), $PRCS_IN_DESC );
				$PRCS_IN_DESC = str_replace ( "'exclude'", _ ( "'不包含'" ), $PRCS_IN_DESC );
				$PRCS_IN_DESC = str_replace ( "''", _ ( "'空'" ), $PRCS_IN_DESC );
				$PRCS_IN_DESC = str_replace ( "'", " ", $PRCS_IN_DESC );
				$PRCS_IN_DESC = str_replace ( "'=='", _ ( "类型为" ), $PRCS_IN_DESC );
				$PRCS_IN_DESC = str_replace ( "'!=='", _ ( "类型不能为" ), $PRCS_IN_DESC );
				$NOT_PASS = check_condition ( $FORM_DATA, $PRCS_IN, $PRCS_IN_SET, $RUN_ID, $PRCS_ID );
				
				if (substr ( $NOT_PASS, 0, 5 ) == "SETOK") {
					$PRCS_IN_DESC = substr ( $NOT_PASS, 5 );
					$NOT_PASS = "";
				}
				// 条件符合
				if ($NOT_PASS == "") {
					if ($COUNT_PRCS_OK == 0) {
						echo '<div class="read_detail">';
					}
					?>          	
                	<input type="checkbox" name="PRCS_ID_NEXT"
			id="prcs_<?=$PRCS_ID_NEXT?>" onclick="select_turn(this)"
			value="<?=$PRCS_ID_NEXT?>" <? if($count==1 || $SYNC_DEAL == 2) echo " checked";?>> <label
			for="prcs_<?=$PRCS_ID_NEXT?>"><?=$PRCS_NAME?></label>
<?
					$COUNT_PRCS_OK ++;
				} 				// 条件不符
				else {
					?>
            		<label for="prcs_<?=$PRCS_ID_NEXT?>"><?=$PRCS_NAME?> <?=$NOT_PASS?></label>
<?
				}
			}
			// 结束流程步骤
			if (find_id ( $PRCS_TO, "0" )) {
				echo '<input type="checkbox" name="PRCS_ID_NEXT" id="prcs_0" onclick="select_turn(this)" value="0"><label for="prcs_0">' . _ ( "结束流程" ) . '</label><br />';
				$COUNT_PRCS_OK ++;
				$count ++;
			}
			
			if ($COUNT_PRCS_OK > 0) {
				echo '</div>';
			}
		}
		if ($COUNT_PRCS_OK == 0) {
			
			// echo 'NORIGHTNEXTPRCS';
			// exit;
		}
		
		if ($count == 0) {
			ob_clean ();
			echo 'NOSETNEWPRCS';
			exit ();
		}
		?>
<input type="hidden" name="TOP_FLAG" value="<?=$TOP_FLAG?>">
<?
	}
} else // 自由流程转交
{
}
?>
<input type="hidden" name="turn_action" value=""> <input type="hidden"
			name="NEW_PRCS_ID_NEXT" value="">
		</form>

		<div id="turn_opts" class="turn_opts" style="display: none;">
			<span class="combtn rbtn" onclick='turnWorkFlow();'> <span><?=_('继续')?></span>
			</span> <span class="combtn rbtn" onclick='turnWorkFlow();'> <span><?=_('提交')?></span>
			</span>
		</div>
		<script>
var SYNC_DEAL = '<?=$SYNC_DEAL?>';
function select_turn(obj)
{
	if(obj.id == "prcs_0" && obj.checked == true)
	{
		$("input[id^='prcs_']").attr("checked",false);
		$("#prcs_0").attr("checked",true);
	}
	//强制并发
	else if(obj.id != "prcs_0" && SYNC_DEAL == 2)
	{
		$("input[id^='prcs_']").attr("checked",true);
		$("#prcs_0").attr("checked",false);
	}
	//禁止并发
	else if(obj.id != "prcs_0" && obj.checked == true && SYNC_DEAL == 0)
	{
		$("input[id^='prcs_']").attr("checked",false);
		obj.checked = true;
		$("#prcs_0").attr("checked",false);
	}
	//允许并发
	else if(obj.id != "prcs_0" && obj.checked == true)
	{
		$("#prcs_0").attr("checked",false);
	}
	var prcs_str = "";
	$("input[name='PRCS_ID_NEXT']").each(function(i){
		if(this.checked == true)
		{
			if(this.id == "prcs_0")
			{
				$("input[name='turn_action']").val("turn_submit.php");
				prcs_str = 0;
			}
			else
			{
			    prcs_str += this.value + ",";
			    $("input[name='turn_action']").val("turn_user.php");
			}
		}
	});
	$("input[name='NEW_PRCS_ID_NEXT']").val(prcs_str);
}
$(document).ready(function(){
	select_turn("");
});
</script>
	</div>
</div>