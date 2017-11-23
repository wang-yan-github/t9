<?php
include_once ("../header.php");
include_once ("inc/utility_org.php");
include_once ("inc/utility_all.php");
include_once ("inc/utility_flow.php");
/**
 * check_flow_sort
 *
 * 检查流程目录类型
 *
 * @access public
 * @param
 *        	string
 * @return int
 */
function check_child($SORT_ID, $HAVE_CHILD, $MY_FLOW_ARRAY) {
	global $connection;
	$RESULT = 0;
	// 目录下是否有新建权限的流程
	if (array_key_exists ( $SORT_ID, $MY_FLOW_ARRAY )) {
		$RESULT = 1;
	} else if ($HAVE_CHILD == 1) {
		$query = "select SORT_ID,HAVE_CHILD FROM FLOW_SORT WHERE SORT_PARENT='$SORT_ID'";
		$cursor = exequery ( $connection, $query );
		while ( $ROW = mysql_fetch_array ( $cursor ) ) {
			$SORT_ID1 = $ROW ["SORT_ID"];
			$HAVE_CHILD1 = $ROW ["HAVE_CHILD"];
			
			if (check_child ( $SORT_ID1, $HAVE_CHILD1, $MY_FLOW_ARRAY ) > 0) {
				$RESULT = 2;
				break;
			}
		}
	}
	return $RESULT;
}
if (! $PARENT_SORT) {
	$PARENT_SORT = 0;
}
$pagestarttime = microtime ();
// 先列出固定流程，即查询FLOW_PROCESS表每个流程第一步有权限的
$MY_FLOW_ARRAY = array ();
$query = "select PRCS_USER,PRCS_DEPT,PRCS_PRIV,FLOW_TYPE.FLOW_ID,FLOW_SORT from FLOW_PROCESS,FLOW_TYPE where FLOW_TYPE.FLOW_ID=FLOW_PROCESS.FLOW_ID AND FLOW_TYPE.FLOW_TYPE=1 AND PRCS_ID='1' GROUP BY FLOW_TYPE.FLOW_ID";
$cursor = exequery ( $connection, $query );
while ( $ROW = mysql_fetch_array ( $cursor ) ) {
	$PRIV_USER = $ROW ["PRCS_USER"];
	$PRIV_DEPT = $ROW ["PRCS_DEPT"];
	$PRIV_ROLE = $ROW ["PRCS_PRIV"];
	$FLOW_ID = $ROW ["FLOW_ID"];
	$FLOW_SORT = $ROW ["FLOW_SORT"];
	
	if ($PRIV_DEPT == "ALL_DEPT" || find_id ( $PRIV_USER, $LOGIN_USER_ID ) || find_id ( $PRIV_DEPT, $LOGIN_DEPT_ID ) || find_id ( $PRIV_ROLE, $LOGIN_USER_PRIV ) || check_id ( $PRIV_ROLE, $LOGIN_USER_PRIV_OTHER, true ) != "" || check_id ( $PRIV_DEPT, $LOGIN_DEPT_ID_OTHER, true ) != "") {
		$MY_FLOW_ARRAY [$FLOW_SORT] .= $FLOW_ID . ",";
	}
}

// 自由流程权限判断
$query = "select NEW_USER,FLOW_ID,FLOW_SORT from FLOW_TYPE WHERE FLOW_TYPE=2 ORDER BY FLOW_ID";
$cursor = exequery ( $connection, $query );
while ( $ROW = mysql_fetch_array ( $cursor ) ) {
	$NEW_USER = $ROW ["NEW_USER"];
	$FLOW_ID = $ROW ["FLOW_ID"];
	$FLOW_SORT = $ROW ["FLOW_SORT"];
	$PRIV_ARRAY = explode ( "|", $NEW_USER );
	
	$PRIV_USER = $PRIV_ARRAY [0];
	$PRIV_DEPT = $PRIV_ARRAY [1];
	$PRIV_ROLE = $PRIV_ARRAY [2];
	
	if ($PRIV_DEPT == "ALL_DEPT" || find_id ( $PRIV_USER, $LOGIN_USER_ID ) || find_id ( $PRIV_DEPT, $LOGIN_DEPT_ID ) || find_id ( $PRIV_ROLE, $LOGIN_USER_PRIV ) || check_id ( $PRIV_ROLE, $LOGIN_USER_PRIV_OTHER, true ) != "" || check_id ( $PRIV_DEPT, $LOGIN_DEPT_ID_OTHER, true ) != "") {
		$MY_FLOW_ARRAY [$FLOW_SORT] .= $FLOW_ID . ",";
	}
}

ob_end_clean ();

// --------------------------------------------
if (! empty ( $MY_FLOW_ARRAY )) {
	if ($SORT_ID == "") {
		$SORT_ID = 0;
	}
	
	// 查询出分类目录
	$COUNT = 0;
	$LIST_ARRAY = array();
	$query = "SELECT SORT_ID,SORT_NAME,HAVE_CHILD,DEPT_ID from FLOW_SORT where SORT_PARENT='$SORT_ID' order by DEPT_ID,SORT_NO";
	$cursor = exequery ( $connection, $query );
	while ( $ROW = mysql_fetch_array ( $cursor ) ) {
		$COUNT ++;
		$SORT_ID1 = $ROW ["SORT_ID"];
		$SORT_NAME = $ROW ["SORT_NAME"];
		$SORT_NAME = htmlspecialchars ( $SORT_NAME );
		$HAVE_CHILD = $ROW ["HAVE_CHILD"];
		$DEPT_ID = $ROW ["DEPT_ID"];
		
		$DISP_CHILD = check_child ( $SORT_ID1, $HAVE_CHILD, $MY_FLOW_ARRAY );
		
		if ($DISP_CHILD > 0) {
			$URL = "tree.php?SORT_ID=$SORT_ID1&rand=" . mt_rand ();
			$LIST_ARRAY [] = array (
					"title" => $SORT_NAME,
					"isFolder" => true,
					"isLazy" => true,
					"key" => $SORT_ID1,
					"json" => $URL 
			);
		}
	}
	// 显示流程
	if (array_key_exists ( $SORT_ID, $MY_FLOW_ARRAY )) {
		$query = "SELECT FLOW_ID, FLOW_NAME from FLOW_TYPE where FLOW_SORT='$SORT_ID' and FLOW_ID IN(" . rtrim ( $MY_FLOW_ARRAY [$SORT_ID], "," ) . ") order by FLOW_NO,FORM_ID";
		$cursor = exequery ( $connection, $query );
		while ( $ROW = mysql_fetch_array ( $cursor ) ) {
			$COUNT ++;
			$FLOW_ID = $ROW ["FLOW_ID"];
			$FLOW_NAME = $ROW ["FLOW_NAME"];
			$FLOW_NAME = htmlspecialchars ( $FLOW_NAME );
			
			$URL = "edit.php?FLOW_ID=$FLOW_ID";
			
			$LIST_ARRAY [] = array (
					"title" => $FLOW_NAME,
					"isFolder" => false,
					"isLazy" => false,
					"key" => $FLOW_ID,
					"url" => $URL 
			);
		}
	}
}

if(count($LIST_ARRAY) == 0)
{
	echo "NOCREATERUNPRIV";
	exit;
}
foreach ( $LIST_ARRAY as $val ) 
{
	if ($val ['isFolder'] == 1) 
	{
		?>
<li class="folder <?=$fix_for_pad['list-li-style']?><?=$Class?>"
	q_id="<?=$val['key']?>" q_name="<?=$val['title']?>"><img
	src="../style/images/folder.png" class="ui-li-thumb" />
	<h3><?=$val['title']?></h3>
	<p class="w100 grapc">&nbsp;</p> <span class="ui-icon-rarrow"></span></li>
<?
	}
	else 
	{
?>
<li class="files <?=$fix_for_pad['list-li-style']?><?=$Class?>"
	q_id="<?=$val['key']?>"><img src="../style/images/file.png"
	class="ui-li-thumb" />
	<h3><?=$val['title']?></h3>
	<p class="w100 grapc">&nbsp;</p>
    <span class="ui-icon-rarrow"></span>
</li>
<?
	}
}

if(!$SORT_NAME_TMP)
{
	$query = "select SORT_NAME, SORT_PARENT from FLOW_SORT where SORT_ID = '$SORT_ID'";
	$cursor = exequery ( $connection, $query );
	if($ROW = mysql_fetch_array($cursor))
	{
		$SORT_NAME_TMP = $ROW['SORT_NAME'];
		$PARENT_SORT = $ROW['SORT_PARENT'];
	}
}
else
{
	$SORT_NAME_TMP = td_iconv($SORT_NAME_TMP, 'utf-8', $MYOA_CHARSET);
}
?>
<script>
now_sort = '<?=$SORT_ID?>';
parent = '<?=$PARENT_SORT?>';
$("#header_12").find(".t").text('<?=$SORT_NAME_TMP?>');
</script>