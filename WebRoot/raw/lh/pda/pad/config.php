<?
	
	require_once 'inc/td_config.php';
	
   //2012/2/16 14:58:31 lp 修正PDA微讯准确判断
   if($_SESSION["P_VER"]){
      $C['msg_type'] = $_SESSION["P_VER"];          
   }else{
      $C['msg_type'] = 3;      
   }
   
   //首页图标提醒刷新时间
   $C['MAIN_PAGE_REF_SEC'] = 60;
	
	//列表页短信刷新时间
	$C['MSG_LIST_REF_SEC'] = $SMS_REF_SEC;
	
	//短信刷新时间
	$C['MSG_DIOG_REF_SEC'] = 5;
	
	//获取查询值时间
	$C['SEARCH_REF_SEC'] = 1;
	
	//iPad上的优化
	$C['optimizeiPad']['sms-list-content-li'] = 'foripad';
	$C['optimizeiPad']['sms-list-show-num'] = 20;
	$C['optimizeiPad']['sms-diog-show-num'] = 8;
	
	//pda界面每页多少个图标
   $PAGE_APP_COUNT = 9;
   
	//基于ipad上的优化
	$C['optimizeiPad']['list-li-style'] = 'foripad';
	$C['optimizeiPad']['list-show-num'] = 10;
	
?>