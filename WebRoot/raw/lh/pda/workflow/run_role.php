<?
include_once("../header.php");
include_once("inc/utility_all.php");
include_once("inc/utility_org.php");
include_once("inc/utility_cache.php");
include_once("general/workflow/list/turn/condition.php");
ob_clean();

function getMyDept($LOWER=0)
{
	global $LOGIN_DEPT_ID,$SYS_DEPARTMENT;
	foreach($SYS_DEPARTMENT as $DEPT_ID => $DEPT)
	{
		if(isset($FLAG) && $DEPT["DEPT_LEVEL"]<=$FLAG)
		   break;
		if($DEPT_ID!=$LOGIN_DEPT_ID && !isset($FLAG))
		   continue;
		else
		{
			if($DEPT_ID==$LOGIN_DEPT_ID)
			{
			   $FLAG=$DEPT["DEPT_LEVEL"];
			   if($LOWER==1)
			     continue;
			}
			$MY_DEPT_STR .= $DEPT_ID.",";	   
		}
	}
	if($MY_DEPT_STR!="")
	   $MY_DEPT_STR=substr($MY_DEPT_STR,0,-1);
	else
	   $MY_DEPT_STR=0;
  reset($SYS_DEPARTMENT);
	return $MY_DEPT_STR;
}

function attach_macro($ATTACHMENT_ID,$ATTACHMENT_NAME,$MODULE="",$disp_img = true)
{
	global $ATTACH_PRIV;
	if($ATTACHMENT_ID=="")
		return "";

	if($MODULE=="")
		$MODULE=attach_sub_dir();

	$YM=substr($ATTACHMENT_ID,0,strpos($ATTACHMENT_ID,"_"));
	if($YM)
		$ATTACHMENT_ID=substr($ATTACHMENT_ID,strpos($ATTACHMENT_ID,"_")+1);
	$ATTACHMENT_ID_ENCODED=attach_id_encode($ATTACHMENT_ID,$ATTACHMENT_NAME);

	if(is_image($ATTACHMENT_NAME) && $disp_img )
	{
		$IMG_ATTR=td_getimagesize(attach_real_path($ATTACHMENT_ID,$ATTACHMENT_NAME));
		$ATTACH_LINK.="<img src=\"/inc/attach.php?MODULE=".$MODULE."&YM=".$YM."&ATTACHMENT_ID=".$ATTACHMENT_ID_ENCODED."&ATTACHMENT_NAME=".urlencode($ATTACHMENT_NAME)."\" border=\"0\" ".$IMG_ATTR[3]." alt=\""._("文件名：").$ATTACHMENT_NAME."\">";
	}
	else
	{
		//兼容公共附件下载权限
		if(find_id($ATTACH_PRIV,4) || find_id($ATTACH_PRIV,5) || $ATTACH_PRIV=="")
			$DOWN_PRIV_OFFICE='1';
		else
			$DOWN_PRIV_OFFICE='0';
		 
		if($DOWN_PRIV_OFFICE)
			$HREF = "/inc/attach.php?MODULE=".$MODULE."&YM=".$YM."&ATTACHMENT_ID=".$ATTACHMENT_ID_ENCODED."&ATTACHMENT_NAME=".urlencode($ATTACHMENT_NAME)."";
		else
			$HREF = "#";
		$ATTACH_IMAGE=image_mimetype($ATTACHMENT_NAME);

		$ATTACH_LINK.="<img src=\"/general/netdisk/images/".$ATTACH_IMAGE."\" align=\"absmiddle\"> ";
		 
		$ATTACH_LINK.="<a href=\"$HREF\">".htmlspecialchars($ATTACHMENT_NAME)."</a>&nbsp;\n";
	}

	return $ATTACH_LINK;
}



/**
 *实际流程的指定步骤或所有步骤权限判断
 *1-系统管理员
 *2-主办人
 *3-管理与监控人
 *4-经办人
 *5-查询人
 *6-原始委托人
 *返回权限字符串逗号分割
 */
function run_role($RUN_ID,$PRCS_ID)
{
    global $connection,$LOGIN_USER_ID,$LOGIN_USER_PRIV,$LOGIN_DEPT_ID;
    
    $RUN_ROLE="";
    $query = "SELECT * from FLOW_RUN where RUN_ID='$RUN_ID'";
    $cursor = exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
       $FLOW_ID=$ROW["FLOW_ID"];
    else
       return $RUN_ROLE;
    
    $query_str=" and RUN_ID='$RUN_ID'";
    if($PRCS_ID!="0" && $PRCS_ID!="")
       $query_str.=" and PRCS_ID='$PRCS_ID'";
    
    if($LOGIN_USER_PRIV=="1")
       $RUN_ROLE.="1,";
    
    $query = "SELECT * from FLOW_RUN_PRCS where USER_ID='$LOGIN_USER_ID' AND OP_FLAG=1".$query_str;
    $cursor = exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
       $RUN_ROLE.="2,";
    
    //获取发起人部门
    $query = "SELECT DEPT_ID from FLOW_RUN,USER where FLOW_RUN.BEGIN_USER=USER.USER_ID and RUN_ID='$RUN_ID'";
    $cursor = exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
       $BEGIN_DEPT = $ROW["DEPT_ID"];
    
    //获取我有权限的部门范围
    $MY_DEPT_STR=getMyDept();
    
    $query = "SELECT MANAGE_USER,MANAGE_USER_DEPT,QUERY_USER,QUERY_USER_DEPT from FLOW_TYPE where FLOW_ID='$FLOW_ID'";
    $cursor = exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
    {
      foreach($ROW as $PRIV_NAME => $PRIV_STR)
      {
        $PRIV_ARRAY=explode("|",$PRIV_STR);
        $PRIV_USER=$PRIV_ARRAY[0];
        $PRIV_DEPT=$PRIV_ARRAY[1];
        $PRIV_ROLE=$PRIV_ARRAY[2];
         	
    	  if(find_id($PRIV_USER,$LOGIN_USER_ID) 
                || find_id($PRIV_ROLE,$LOGIN_USER_PRIV)
                || find_id($PRIV_ROLE,$LOGIN_USER_PRIV_OTHER)
                || find_id($PRIV_DEPT,$LOGIN_DEPT_ID)
                || find_id($PRIV_DEPT,$LOGIN_DEPT_ID_OTHER) || $PRIV_DEPT=="ALL_DEPT")
          {
        	if($PRIV_NAME == "MANAGE_USER" || (find_id($MY_DEPT_STR,$BEGIN_DEPT) && $PRIV_NAME == "MANAGE_USER_DEPT"))
    	       $RUN_ROLE.="3,";
    	    elseif($PRIV_NAME == "QUERY_USER" || (find_id($MY_DEPT_STR,$BEGIN_DEPT) && $PRIV_NAME == "QUERY_USER_DEPT"))
    	       $RUN_ROLE.="5,"; 
    	  }
    	}
    }
    
    $query = "SELECT * from FLOW_RUN_PRCS where USER_ID='$LOGIN_USER_ID'".$query_str;
    $cursor = exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
       $RUN_ROLE.="4,";
    
    $query = "SELECT 1 from FLOW_RUN_PRCS where OTHER_USER='$LOGIN_USER_ID'".$query_str;
    $cursor = exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
       $RUN_ROLE.="6,";
    
    return $RUN_ROLE;
}


function get_prcs_user($FLOW_ID,$PRCS_ID)
{
    global $connection,$RUN_ID,$LOGIN_DEPT_ID,$ELEMENT_ARRAY;
    
    $USER_ARR = array("OP" => array(), "OTHER" => array() );
    $query = "SELECT * from FLOW_PROCESS where FLOW_ID='$FLOW_ID' and PRCS_ID = '$PRCS_ID'";
    $cursor= exequery($connection,$query);
    if($ROW=mysql_fetch_array($cursor))
    {
        $PRCS_USER=$ROW["PRCS_USER"];
        $PRCS_DEPT=$ROW["PRCS_DEPT"];
        $PRCS_PRIV=$ROW["PRCS_PRIV"];
        
        $AUTO_TYPE=$ROW["AUTO_TYPE"];
        $AUTO_USER_OP=$ROW["AUTO_USER_OP"];
        $AUTO_USER=$ROW["AUTO_USER"];
        $AUTO_BASE_USER=$ROW["AUTO_BASE_USER"];
        $USER_LOCK=$ROW["USER_LOCK"];
        $TOP_DEFAULT=$ROW["TOP_DEFAULT"];
        
        if ($AUTO_TYPE == 1) {
        	//发起人信息
        	$query = "SELECT BEGIN_USER from FLOW_RUN where RUN_ID='$RUN_ID'";
        	$cursor = exequery ( $connection, $query );
        	if ($ROW = mysql_fetch_array ( $cursor )) {
        		$USER_ID = $ROW ["BEGIN_USER"];
        			
        		$query1 = "SELECT * from USER where USER_ID='$USER_ID'";
        		$cursor1 = exequery ( $connection, $query1 );
        		if ($ROW = mysql_fetch_array ( $cursor1 )) {
        			$PRCS_NEW_USER_ID = $USER_ID;
        			$PRCS_NEW_USER_NAME = $ROW ["USER_NAME"];
        			$PRCS_NEW_DEPT_ID = $ROW ["DEPT_ID"];
        			$PRCS_NEW_USER_PRIV = $ROW ["USER_PRIV"];
        			$PRCS_NEW_USER_PRIV_OTHER = $ROW ["USER_PRIV_OTHER"];
        		}
        	}
        
        	//检查该发起人是否有经办权限
        	if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $PRCS_NEW_USER_ID ) || find_id ( $PRCS_DEPT, $PRCS_NEW_DEPT_ID ) || find_id ( $PRCS_PRIV, $PRCS_NEW_USER_PRIV ) || priv_other ( $PRCS_PRIV, $PRCS_NEW_USER_PRIV_OTHER )) {
        		$PRCS_OP_USER = $PRCS_NEW_USER_ID;
        		$PRCS_OP_USER_NAME = $PRCS_NEW_USER_NAME;
        		$PRCS_USER_AUTO = $PRCS_NEW_USER_ID . ",";
        		$PRCS_USER_NAME = $PRCS_NEW_USER_NAME . ",";
        	}
        }
        elseif ($AUTO_TYPE == 2 || $AUTO_TYPE == 4 || $AUTO_TYPE == 5 || $AUTO_TYPE == 6 || $AUTO_TYPE == 9 || $AUTO_TYPE == 10 || $AUTO_TYPE == 11) {
        	if ($AUTO_BASE_USER != 0) {
        		//基准对象
        		$query = "select USER_ID FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND FLOW_PRCS='$AUTO_BASE_USER' AND OP_FLAG=1 ORDER BY PRCS_ID LIMIT 1";
        		$cursor = exequery ( $connection, $query );
        		if ($ROW = mysql_fetch_array ( $cursor ))
        			$BASE_USER_ID = $ROW ["USER_ID"];
        			
        		$query = "select DEPT_ID FROM USER WHERE USER_ID='$BASE_USER_ID' and NOT_LOGIN <> 1  and USER.DEPT_ID != 0 LIMIT 1";
        		$cursor = exequery ( $connection, $query );
        		if ($ROW = mysql_fetch_array ( $cursor ))
        			$BASE_DEPT_ID = $ROW ["DEPT_ID"];
        		$AUTO_DEPT_ID = $BASE_DEPT_ID;
        	} else
        		$AUTO_DEPT_ID = $LOGIN_DEPT_ID;
        
        	if ($AUTO_TYPE == 2 || $AUTO_TYPE == 9 || $AUTO_TYPE == 10)
        		$TMP_DEPT_ID = $AUTO_DEPT_ID;
        	elseif ($AUTO_TYPE == 4 || $AUTO_TYPE == 6)
        	$TMP_DEPT_ID = get_dept_parent ( $AUTO_DEPT_ID );
        	elseif ($AUTO_TYPE == 5 || $AUTO_TYPE == 11)
        	$TMP_DEPT_ID = get_dept_parent ( $AUTO_DEPT_ID, 1 );
        	$query3 = "SELECT MANAGER FROM DEPARTMENT WHERE DEPT_ID='$TMP_DEPT_ID'";
        	$cursor3 = exequery ( $connection, $query3 );
        	if ($ROW = mysql_fetch_array ( $cursor3 ))
        		$MANAGER = $ROW ["MANAGER"];
        	if ($AUTO_TYPE == 9) {
        		$query3 = "SELECT ASSISTANT_ID FROM DEPARTMENT WHERE DEPT_ID='$TMP_DEPT_ID'";
        		$cursor3 = exequery ( $connection, $query3 );
        		if ($ROW = mysql_fetch_array ( $cursor3 ))
        			$MANAGER = $ROW ["ASSISTANT_ID"];
        	}
        
        	if ($AUTO_TYPE == 4 || $AUTO_TYPE == 6) {
        		$query3 = "SELECT LEADER1,LEADER2 FROM DEPARTMENT WHERE DEPT_ID='$AUTO_DEPT_ID'";
        		$cursor3 = exequery ( $connection, $query3 );
        		if ($ROW = mysql_fetch_array ( $cursor3 )) {
        			$LEADER1 = $ROW ["LEADER1"];
        			$LEADER2 = $ROW ["LEADER2"];
        		}
        		if ($LEADER1 != "" && $AUTO_TYPE == 4)
        			$MANAGER = $LEADER1;
        		if ($LEADER2 != "" && $AUTO_TYPE == 6)
        			$MANAGER = $LEADER2;
        	}
        
        	if ($MANAGER != "") {
        		$query3 = "SELECT USER_ID,DEPT_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER FROM USER WHERE find_in_set(USER_ID,'$MANAGER')  and NOT_LOGIN <> 1  and USER.DEPT_ID != 0 order by USER_NO,USER_NAME";
        		$cursor3 = exequery ( $connection, $query3 );
        		while ( $ROW = mysql_fetch_array ( $cursor3 ) ) {
        			$USER_ID = $ROW ["USER_ID"];
        			$DEPT_ID = $ROW ["DEPT_ID"];
        			$USER_PRIV = $ROW ["USER_PRIV"];
        			$USER_NAME = $ROW ["USER_NAME"];
        			$USER_PRIV_OTHER = $ROW ["USER_PRIV_OTHER"];
        
        			if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $USER_ID ) || find_id ( $PRCS_DEPT, $DEPT_ID ) || find_id ( $PRCS_PRIV, $USER_PRIV ) || priv_other ( $PRCS_PRIV, $USER_PRIV_OTHER )) {
        				$PRCS_USER_AUTO .= $USER_ID . ",";
        				$PRCS_USER_NAME .= $USER_NAME . ",";
        			}
        		}
        		if ($PRCS_USER_AUTO != "") {
        			$PRCS_OP_USER = strtok ( $PRCS_USER_AUTO, "," );
        			$PRCS_OP_USER_NAME = strtok ( $PRCS_USER_NAME, "," );
        		}
        	} else {
        		$query3 = "SELECT USER_ID,USER_NAME,USER_PRIV_OTHER,USER_PRIV.USER_PRIV from USER,USER_PRIV where USER.USER_PRIV=USER_PRIV.USER_PRIV and DEPT_ID='$TMP_DEPT_ID' and USER_ID!='$LOGIN_USER_ID'  and USER.NOT_LOGIN <> 1  and USER.DEPT_ID != 0 order by PRIV_NO,USER_NO,USER_NAME";
        		$cursor3 = exequery ( $connection, $query3 );
        		$USER_PRIV_MAX = "";
        		while ( $ROW = mysql_fetch_array ( $cursor3 ) ) {
        			$USER_ID = $ROW ["USER_ID"];
        			$USER_NAME = $ROW ["USER_NAME"];
        			$USER_PRIV = $ROW ["USER_PRIV"];
        			$USER_PRIV_OTHER = $ROW ["USER_PRIV_OTHER"];
        
        			if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $USER_ID ) || find_id ( $PRCS_DEPT, $LOGIN_DEPT_ID ) || find_id ( $PRCS_PRIV, $USER_PRIV ) || priv_other ( $PRCS_PRIV, $USER_PRIV_OTHER )) {
        				if ($USER_PRIV_MAX == "") {
        					$PRCS_OP_USER = $USER_ID;
        					$PRCS_OP_USER_NAME = $USER_NAME;
        					$PRCS_USER_AUTO .= $USER_ID . ",";
        					$PRCS_USER_NAME .= $USER_NAME . ",";
        					$USER_PRIV_MAX = $USER_PRIV;
        				} elseif ($USER_PRIV == $USER_PRIV_MAX) {
        					$PRCS_USER_AUTO .= $USER_ID . ",";
        					$PRCS_USER_NAME .= $USER_NAME . ",";
        				}
        			}
        		}
        	}
	        if ($AUTO_TYPE == 10 || $AUTO_TYPE == 11) //本部门内符合条件所有人员 及 本一级部门内符合条件的所有人员
			{
				$USER_ARR = array();
				$query3 = "SELECT USER_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER FROM USER WHERE DEPT_ID = '$TMP_DEPT_ID' and USER.DEPT_ID != 0";
				$cursor3 = exequery ( $connection, $query3 );
				while ( $ROW = mysql_fetch_array ( $cursor3 ) ) {
					$USER_ID = $ROW ["USER_ID"];
			
					$USER_ARR [$USER_ID] ["USER_NAME"] = $ROW ["USER_NAME"];
					$USER_ARR [$USER_ID] ["DEPT_ID"] = $TMP_DEPT_ID;
					$USER_ARR [$USER_ID] ["USER_PRIV"] = $ROW ["USER_PRIV"];
					$USER_ARR [$USER_ID] ["USER_PRIV_OTHER"] = $ROW ["USER_PRIV_OTHER"];
				}
			
				//-----重新按顺序进行排序-----
				if ($USER_ARR) {
					uasort ( $USER_ARR, "USER_SORT" );
				}
				$PRCS_USER_AUTO = $PRCS_USER_NAME = "";
				if(!empty($USER_ARR))	{
	  			foreach ( $USER_ARR as $K => $V ) {
	  				if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $K ) || find_id ( $PRCS_DEPT, $V ["DEPT_ID"] ) || find_id ( $PRCS_PRIV, $V ["USER_PRIV"] ) || priv_other ( $PRCS_PRIV, $V ["USER_PRIV_OTHER"] )) {
	  					$PRCS_USER_AUTO .= $K . ",";
	  					$PRCS_USER_NAME .= $V ["USER_NAME"] . ",";
	  				}
	  			}
	  		}
				if ($PRCS_USER_AUTO != "") {
					$PRCS_OP_USER = strtok ( $PRCS_USER_AUTO, "," );
					$PRCS_OP_USER_NAME = strtok ( $PRCS_USER_NAME, "," );
				}
			}
        }
        elseif ($AUTO_TYPE == 3) {
        	if ($AUTO_USER != "") {
        		//默认主办人
        		$query3 = "SELECT USER_ID,DEPT_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER from USER where USER_ID='$AUTO_USER_OP' and NOT_LOGIN <> 1  and USER.DEPT_ID != 0 limit 1";
        		$cursor3 = exequery ( $connection, $query3 );
        		if ($ROW = mysql_fetch_array ( $cursor3 )) {
        			$USER_ID = $ROW ["USER_ID"];
        			$DEPT_ID = $ROW ["DEPT_ID"];
        			$USER_PRIV = $ROW ["USER_PRIV"];
        			$USER_NAME = $ROW ["USER_NAME"];
        			$USER_PRIV_OTHER = $ROW ["USER_PRIV_OTHER"];
        
        			if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $USER_ID ) || find_id ( $PRCS_DEPT, $DEPT_ID ) || find_id ( $PRCS_PRIV, $USER_PRIV ) || priv_other ( $PRCS_PRIV, $USER_PRIV_OTHER )) {
        				$PRCS_OP_USER = $USER_ID;
        				$PRCS_OP_USER_NAME = $USER_NAME;
        			}
        		}
        			
        		//默认经办人
        		$query3 = "SELECT USER_ID,DEPT_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER from USER where find_in_set(USER_ID,'$AUTO_USER')  and NOT_LOGIN <> 1  and USER.DEPT_ID != 0";
        		$cursor3 = exequery ( $connection, $query3 );
        		while ( $ROW = mysql_fetch_array ( $cursor3 ) ) {
        			$USER_ID = $ROW ["USER_ID"];
        			$DEPT_ID = $ROW ["DEPT_ID"];
        			$USER_PRIV = $ROW ["USER_PRIV"];
        			$USER_NAME = $ROW ["USER_NAME"];
        			$USER_PRIV_OTHER = $ROW ["USER_PRIV_OTHER"];
        
        			if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $USER_ID ) || find_id ( $PRCS_DEPT, $DEPT_ID ) || find_id ( $PRCS_PRIV, $USER_PRIV ) || priv_other ( $PRCS_PRIV, $USER_PRIV_OTHER )) {
        				$PRCS_USER_AUTO .= $USER_ID . ",";
        				$PRCS_USER_NAME .= $USER_NAME . ",";
        			}
        		}
        	}
        }
        elseif ($AUTO_TYPE == 7) //从表单选择
        {
        	if (is_numeric ( $AUTO_USER )) {
        		$query_flow_id = "SELECT FLOW_ID from FLOW_RUN where RUN_ID='$RUN_ID'";
        		$cursor_flow_id = exequery ( $connection, $query_flow_id );
        		if ($ROW_FLOW_ID = mysql_fetch_array ( $cursor_flow_id )) {
        			$FLOW_ID = $ROW_FLOW_ID ['FLOW_ID'];
        		}
        		$tbl_name = "flow_data_" . $FLOW_ID;
        		$ITEM_FIELD = "data_" . $AUTO_USER;
        		$query3 = "SELECT $ITEM_FIELD from $tbl_name where RUN_ID='$RUN_ID'";
        		$cursor3 = exequery ( $connection, $query3 );
        		if ($ROW = mysql_fetch_array ( $cursor3 ))
        			$ITEM_DATA = $ROW [$ITEM_FIELD];
        		$TITLE = $ELEMENT_ARRAY["DATA_" . $AUTO_USER]["TITLE"];
        		$TAG = $ELEMENT_ARRAY["DATA_" . $AUTO_USER]["TAG"];
        		$IS_CHILD = 0;
        		foreach($ELEMENT_ARRAY as $ENAME => $ELEMENT_ARR)
        		{
        			$CHILD = $ELEMENT_ARR["CHILD"];
        			if($CHILD == $TITLE){
        				$IS_CHILD++;
        				$PARENT_NAME = strtolower($ELEMENT_ARR["NAME"]);
        			}
        		}
        		if($TAG == "SELECT" && $IS_CHILD != 0){
        			$query3 = "SELECT $PARENT_NAME from $tbl_name where RUN_ID='$RUN_ID'";
        			$cursor3 = exequery ( $connection, $query3 );
        			if ($ROW = mysql_fetch_array ( $cursor3 ))
        				$PARENT_ITEM_DATA = $ROW [$PARENT_NAME];
        			 
        			$ITEM_DATA = str_replace  ("|".$PARENT_ITEM_DATA,"", $ITEM_DATA);
        		}
        			
        		$ITEM_DATA = str_replace ( "，", ",", $ITEM_DATA );
        		$USER_ARR = array ();
        			
        		$query3 = "SELECT USER_ID,DEPT_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER FROM USER WHERE FIND_IN_SET(USER_NAME,'$ITEM_DATA')  and USER.DEPT_ID != 0 and USER.NOT_LOGIN <> 1";
        		$cursor3 = exequery ( $connection, $query3 );
        		while ( $ROW = mysql_fetch_array ( $cursor3 ) ) {
        			$USER_ID = $ROW ["USER_ID"];
        
        			$USER_ARR [$USER_ID] ["USER_NAME"] = $ROW ["USER_NAME"];
        			$USER_ARR [$USER_ID] ["DEPT_ID"] = $ROW ["DEPT_ID"];
        			$USER_ARR [$USER_ID] ["USER_PRIV"] = $ROW ["USER_PRIV"];
        			$USER_ARR [$USER_ID] ["USER_PRIV_OTHER"] = $ROW ["USER_PRIV_OTHER"];
        		}
        			
        		if (! $USER_ARR) //按USER_ID查询
        		{
        			$query3 = "SELECT USER_ID,DEPT_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER FROM USER WHERE FIND_IN_SET(USER_ID,'$ITEM_DATA')  and USER.DEPT_ID != 0 and USER.NOT_LOGIN <> 1";
        			$cursor3 = exequery ( $connection, $query3 );
        			while ( $ROW = mysql_fetch_array ( $cursor3 ) ) {
        				$USER_ID = $ROW ["USER_ID"];
        					
        				$USER_ARR [$USER_ID] ["USER_NAME"] = $ROW ["USER_NAME"];
        				$USER_ARR [$USER_ID] ["DEPT_ID"] = $ROW ["DEPT_ID"];
        				$USER_ARR [$USER_ID] ["USER_PRIV"] = $ROW ["USER_PRIV"];
        				$USER_ARR [$USER_ID] ["USER_PRIV_OTHER"] = $ROW ["USER_PRIV_OTHER"];
        			}
        		}
        		//-----重新按顺序进行排序-----
        		if ($USER_ARR) {
        			$FORM_USER_STR = $ITEM_DATA;
        			uasort ( $USER_ARR, "USER_SORT" );
        		}
        			
        		$PRCS_USER_AUTO = $PRCS_USER_NAME = "";
        			
        		foreach ( $USER_ARR as $K => $V ) {
        			if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $K ) || find_id ( $PRCS_DEPT, $V ["DEPT_ID"] ) || find_id ( $PRCS_PRIV, $V ["USER_PRIV"] ) || priv_other ( $PRCS_PRIV, $V ["USER_PRIV_OTHER"] )) {
        				$PRCS_USER_AUTO .= $K . ",";
        				$PRCS_USER_NAME .= $V ["USER_NAME"] . ",";
        			}
        		}
        		if ($PRCS_USER_AUTO != "") {
        			$PRCS_OP_USER = strtok ( $PRCS_USER_AUTO, "," );
        			$PRCS_OP_USER_NAME = strtok ( $PRCS_USER_NAME, "," );
        		}
        	}
        }
        elseif ($AUTO_TYPE == 8 && is_numeric ( $AUTO_USER )) //自动选择指定步骤主办人
        {
        	$query3 = "select USER_ID FROM FLOW_RUN_PRCS WHERE RUN_ID='$RUN_ID' AND FLOW_PRCS='$AUTO_USER' AND OP_FLAG=1 ORDER BY PRCS_ID LIMIT 1";
        	$cursor3 = exequery ( $connection, $query3 );
        	if ($ROW = mysql_fetch_array ( $cursor3 )) {
        		$USER_ID = $ROW ["USER_ID"];
        		$query3 = "SELECT USER_ID,DEPT_ID,USER_PRIV,USER_NAME,USER_PRIV_OTHER from USER where USER_ID='$USER_ID' limit 1";
        		$cursor3 = exequery ( $connection, $query3 );
        		if ($ROW = mysql_fetch_array ( $cursor3 )) {
        			$DEPT_ID = $ROW ["DEPT_ID"];
        			$USER_PRIV = $ROW ["USER_PRIV"];
        			$USER_NAME = $ROW ["USER_NAME"];
        			$USER_PRIV_OTHER = $ROW ["USER_PRIV_OTHER"];
        
        			if ($PRCS_DEPT == "ALL_DEPT" || find_id ( $PRCS_USER, $USER_ID ) || find_id ( $PRCS_DEPT, $DEPT_ID ) || find_id ( $PRCS_PRIV, $USER_PRIV ) || priv_other ( $PRCS_PRIV, $USER_PRIV_OTHER )) {
        				$PRCS_OP_USER = $USER_ID;
        				$PRCS_USER_AUTO = $USER_ID . ",";
        				$PRCS_OP_USER_NAME = $PRCS_USER_NAME = $USER_NAME;
        			}
        		}
        	}
        }
        elseif ($PRCS_USER != "" && $PRCS_DEPT == "" && $PRCS_PRIV == "") //非自动选择时，如只有一个经办人
		{
			$PRCS_USER_ARRAY = explode ( ",", $PRCS_USER );
			$PRCS_USER_COUNT = sizeof ( $PRCS_USER_ARRAY ) - 1;
			
			if ($PRCS_USER_COUNT == 1) {
				$PRCS_USER_AUTO = $PRCS_USER;
				if (substr ( $PRCS_USER_AUTO, - 1 ) == ",")
					$PRCS_OP_USER = substr ( $PRCS_USER_AUTO, 0, - 1 );
				else
					$PRCS_OP_USER = $PRCS_USER_AUTO;
				$query3 = "SELECT USER_NAME from USER where USER_ID='" . $PRCS_USER_ARRAY [0] . "'";
				$cursor3 = exequery ( $connection, $query3 );
				if ($ROW = mysql_fetch_array ( $cursor3 )) {
					$PRCS_USER_NAME = $ROW ["USER_NAME"] . ",";
					$PRCS_OP_USER_NAME = $ROW ["USER_NAME"];
				}
			}
		}
        
		$query = "SELECT UID,USER_ID,USER_NAME_INDEX,USER_NAME,DEPT_ID FROM USER WHERE 1=1 and DEPT_ID!=0";
		if ($PRCS_DEPT != "ALL_DEPT") 
		{
			$query .= " and (find_in_set(USER_ID,'$PRCS_USER') OR (find_in_set(DEPT_ID,'$PRCS_DEPT')) OR (find_in_set(USER_PRIV,'$PRCS_PRIV'))";
			// 兼容辅助角色
			$PRCS_PRIV_ARR = explode ( ",", $PRCS_PRIV );
			foreach ( $PRCS_PRIV_ARR as $PRIV ) 
			{
				if ($PRIV) 
				{
					$query .= " OR (find_in_set('$PRIV',USER_PRIV_OTHER))";
				}
			}
			$query .= ")";
		}
		$cursor = exequery ( $connection, $query );
		while ( $ROW = mysql_fetch_array ( $cursor ) ) 
		{
			if($PRCS_OP_USER == $ROW ["USER_ID"])
			{
				$USER_ARR ["OP_ZB"] [$ROW ["UID"]] = array (
						"UID" => $ROW ["UID"],
						"USER_ID" => $ROW ["USER_ID"],
						"USER_NAME" => $ROW ["USER_NAME"],
						"DEPT_ID" => $ROW ["DEPT_ID"],
						"USER_NAME_INDEX" => $ROW ["USER_NAME_INDEX"]
				);
			}
			
			// $USER_ARR["OP"][] = $ROW[0];
			$USER_ARR ["OP_USER"] [$ROW ["UID"]] = array (
					"UID" => $ROW ["UID"],
					"USER_ID" => $ROW ["USER_ID"],
					"USER_NAME" => $ROW ["USER_NAME"],
					"DEPT_ID" => $ROW ["DEPT_ID"],
					"USER_NAME_INDEX" => $ROW ["USER_NAME_INDEX"] 
			);
			
		}
		if($PRCS_USER_AUTO)
		{
			$PRCS_USER_AUTO_ARR = UserId2Uid($PRCS_USER_AUTO, true);
			
			$OP_CB = array();
			foreach($PRCS_USER_AUTO_ARR as $key=>$val)
			{
				if(is_array($USER_ARR ["OP_USER"] [$val]))
				{
					$OP_CB[$val] = $USER_ARR ["OP_USER"] [$val];
				}
			}
		
			$USER_ARR ["OP_CB"] = $OP_CB;
		}
		$USER_ARR ["CFG"]["ALLOW_CHANGE"] = $USER_LOCK;
		$USER_ARR ["CFG"]["ALLOW_ZB_ISNULL"] = $TOP_DEFAULT;
    }
    return $USER_ARR;
}


/**
 *判断流程设计权限
 *1-系统管理员
 *2-办理权限
 *3-管理
 *4-查询人
 *5-监控权限
 */
function prcs_role($FLOW_ID,$PRCS_ID)
{
	global $connection,$LOGIN_USER_ID,$LOGIN_DEPT_ID,$LOGIN_USER_PRIV,$LOGIN_USER_PRIV_OTHER,$LOGIN_DEPT_ID_OTHER,$td_cache,$FLOW_LIST,$PRCS_ARRAY;

	$PRCS_ROLE="";

	if($LOGIN_USER_PRIV == 1)
		$PRCS_ROLE .= "1,";
	include_once("inc/workflow_list.php");

	//判断是否是自由流程
	if(is_array($FLOW_LIST[$FLOW_ID]))
	{
		if($FLOW_LIST[$FLOW_ID]['FLOW_TYPE'] == 2)
		{
			if($PRCS_ID!=1)//自由流程非第一步骤
			{
				$PRCS_ROLE .= "2,";
			}
			else
			{
				$NEW_USER=$FLOW_LIST[$FLOW_ID]['NEW_USER']; //自由流程第一步骤，检查新建权限
				$PRIV_ARRAY=explode("|",$NEW_USER);
				 
				$PRIV_USER=$PRIV_ARRAY[0];
				$PRIV_DEPT=$PRIV_ARRAY[1];
				$PRCS_PRIV=$PRIV_ARRAY[2];
				 
				if($PRIV_DEPT=="ALL_DEPT" || find_id($PRIV_USER,$LOGIN_USER_ID) || find_id($PRIV_DEPT,$LOGIN_DEPT_ID) || find_id($PRCS_PRIV,$LOGIN_USER_PRIV))
					$PRCS_ROLE .= "2,";
			}
		}
	}
	//固定流程
	include_once("inc/workflow_flow.php");
	$PRCS_ARRAY = $td_cache->get("workflow/flow/$FLOW_ID");
	if(!is_array($PRCS_ARRAY))
	{
		cache_workflow($FLOW_ID);
		$PRCS_ARRAY = $td_cache->get("workflow/flow/$FLOW_ID");
	}
	if($PRCS_ID!=0)
	{
		$PRCS_ARR[$PRCS_ID] = $PRCS_ARRAY[$PRCS_ID];
	}
	else
	{
		$PRCS_ARR = $PRCS_ARRAY;
	}
	if(is_array($PRCS_ARR))
	{
		foreach($PRCS_ARR as $FLOW_PRCS_CFG)
		{
			$PRCS_USER=$FLOW_PRCS_CFG["PRCS_USER"];
			$PRCS_DEPT=$FLOW_PRCS_CFG["PRCS_DEPT"];
			$PRCS_PRIV=$FLOW_PRCS_CFG["PRCS_PRIV"];
			if( find_id($PRCS_USER,$LOGIN_USER_ID)
					|| find_id($PRCS_DEPT,$LOGIN_DEPT_ID)
					|| find_id($PRCS_PRIV,$LOGIN_USER_PRIV)
					|| check_id($PRCS_PRIV,$LOGIN_USER_PRIV_OTHER,true)!=""
					|| check_id($PRCS_DEPT,$LOGIN_DEPT_ID_OTHER,true)!=""
					|| $PRCS_DEPT=="ALL_DEPT" )
			{
				$PRCS_ROLE .= "2,";
				break;
			}
		}//while
	}
	//判断是否为监控人
	if($PRCS_ID==0 && is_array($FLOW_LIST[$FLOW_ID]) && is_array($FLOW_LIST[$FLOW_ID]['PRIV']))
	{
		//获取我有权限的部门范围
		$MY_DEPT_STR=getMyDept();
		/*
		 //判断管理权限
		$query = "select USER, DEPT, ROLE from FLOW_PRIV where FLOW_ID = '$FLOW_ID' and PRIV_TYPE ='1'";
		$cursor = exequery($connection, $query);
		*/
		foreach($FLOW_LIST[$FLOW_ID]['PRIV'] as $PRIV_ARR)
		{
			if($PRIV_ARR['PRIV_TYPE'] == 1)
			{
				$PRIV_USER = $PRIV_ARR['USER'];
				$PRIV_DEPT = $PRIV_ARR['DEPT'];
				$PRCS_PRIV = $PRIV_ARR['ROLE'];
				if( find_id($PRIV_USER,trim(GetUidByUserID($LOGIN_USER_ID),","))
						|| find_id($PRCS_PRIV,$LOGIN_USER_PRIV)
						|| check_id($PRCS_PRIV,$LOGIN_USER_PRIV_OTHER,true)!=""
						|| find_id($PRIV_DEPT,$LOGIN_DEPT_ID)
						|| check_id($PRCS_DEPT,$LOGIN_DEPT_ID_OTHER,true)!=""
						|| $PRIV_DEPT=="ALL_DEPT" )
				{
					$PRCS_ROLE.="3,";
					break;
				}
			}
		}

		//判断监控权限
		/*
		 $query = "select USER, DEPT, ROLE from FLOW_PRIV where FLOW_ID = '$FLOW_ID' and PRIV_TYPE ='2'";
		$cursor = exequery($connection, $query);
		*/
		foreach($FLOW_LIST[$FLOW_ID]['PRIV'] as $PRIV_ARR)
		{
			if($PRIV_ARR['PRIV_TYPE'] == 2)
			{
				$PRIV_USER = $PRIV_ARR['USER'];
				$PRIV_DEPT = $PRIV_ARR['DEPT'];
				$PRCS_PRIV = $PRIV_ARR['ROLE'];
				if( find_id($PRIV_USER,trim(GetUidByUserID($LOGIN_USER_ID),","))
						|| find_id($PRCS_PRIV,$LOGIN_USER_PRIV)
						|| check_id($PRCS_PRIV,$LOGIN_USER_PRIV_OTHER,true)!=""
						|| find_id($PRIV_DEPT,$LOGIN_DEPT_ID)
						|| check_id($PRCS_DEPT,$LOGIN_DEPT_ID_OTHER,true)!=""
						|| $PRIV_DEPT=="ALL_DEPT" )
				{
					$PRCS_ROLE.="5,";
					break;
				}
			}
		}
		//判断查询权限
		/*
		 $query = "select USER, DEPT, ROLE from FLOW_PRIV where FLOW_ID = '$FLOW_ID' and PRIV_TYPE ='3'";
		$cursor = exequery($connection, $query);
		*/
		foreach($FLOW_LIST[$FLOW_ID]['PRIV'] as $PRIV_ARR)
		{
			if($PRIV_ARR['PRIV_TYPE'] == 3)
			{
				$PRIV_USER = $PRIV_ARR['USER'];
				$PRIV_DEPT = $PRIV_ARR['DEPT'];
				$PRCS_PRIV = $PRIV_ARR['ROLE'];
				if( find_id($PRIV_USER,trim(GetUidByUserID($LOGIN_USER_ID),","))
						|| find_id($PRCS_PRIV,$LOGIN_USER_PRIV)
						|| check_id($PRCS_PRIV,$LOGIN_USER_PRIV_OTHER,true)!=""
						|| find_id($PRIV_DEPT,$LOGIN_DEPT_ID)
						|| check_id($PRCS_DEPT,$LOGIN_DEPT_ID_OTHER,true)!=""
						|| $PRIV_DEPT=="ALL_DEPT" )
				{
					$PRCS_ROLE.="4,";
					break;
				}
			}
		}
	}
	return $PRCS_ROLE;
}
?>