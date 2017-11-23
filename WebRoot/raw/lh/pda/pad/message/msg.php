<?
	include_once('header.php');
	include_once('inc/utility_all.php');
	include_once('user.php');
	include_once('inc/utility_msg.php');
	ob_clean();

	$FROM_UID = $DIALOG_FROM_UID = intval($FROM_UID);
	
	//所有消息已阅
	$query = "UPDATE message SET REMIND_FLAG = 2 WHERE (TO_UID='$LOGIN_UID' and FROM_UID='$FROM_UID') and DELETE_FLAG!='1' and REMIND_FLAG = 1";
	exequery($connection,$query);
	
	//将该人记录进入常用联系人	
	if($FROM_UID!=$LOGIN_UID){
	   if(!isset($_COOKIE['CookieArray'])){
	      setcookie("CookieArray[".$FROM_UID."]", $FROM_UID , time() + 3600*24*30);
	   }else if(!in_array($FROM_UID,$_COOKIE['CookieArray'])){
	      setcookie("CookieArray[".$FROM_UID."]", $FROM_UID , time() + 3600*24*30);   
	   }
	}
	
?>
	<div class="container">
		<div id="mycust-dialogue-list">
		<?
			$count = 0;$str = $_str = $sfinal_str = $msg_type_name = $line_style = '';
			$query_num = Ag("iPad") ? $C['optimizeiPad']['sms-diog-show-num'] : 10;
			$query = "SELECT MSG_ID,TO_UID,FROM_UID,SEND_TIME,CONTENT,MSG_TYPE from message where ((TO_UID='$LOGIN_UID' and FROM_UID='$FROM_UID') or (TO_UID='$FROM_UID' and FROM_UID='$LOGIN_UID')) and DELETE_FLAG!='1' order by MSG_ID DESC limit ".$query_num;
			$cursor= exequery($connection,$query);
			while($ROW=mysql_fetch_array($cursor))
			{
			   
				$_FROM_UID = $ROW['FROM_UID'];
				$MSG_ID = $ROW['MSG_ID'];
				$TO_UID = $ROW['TO_UID'];
				$SEND_TIME = $ROW['SEND_TIME'];
				$CONTENT = $ROW['CONTENT'];
				$MSG_TYPE = $ROW['MSG_TYPE'];
				$SEND_TIME = timeintval($SEND_TIME);
				$line_style = $_FROM_UID == $LOGIN_UID ? "line2" : "line1";
				$msg_type_name = ($_FROM_UID != $LOGIN_UID && $MSG_TYPE !=0) ? " - "._("来自").get_msg_type_name($MSG_TYPE) : '';

	         $str .= '<div class="mycust-list '.$line_style.' clear">';
      		$str .= 		'<div class="mycust-dialogbox-time">'.$SEND_TIME.$msg_type_name.'</div>';
      		$str .=        '<div class="mycust-dialogbox">';
      		$str .= 			   '<a href="#" class="mycust-avatar"><img src="'.showAvatar($USER_ARRAY[$_FROM_UID]['AVATAR'],$USER_ARRAY[$FROM_UID]['SEX']).'" /></a>';
      		$str .= 		      '<div class="mycust-diobox">';
      		$str .=              '<div class="cl"></div>';
      		$str .=			      '<div class="mycust-list-msg">'.$CONTENT.'&nbsp;</div>';
      		$str .=		      '</div>';
      		$str .=        '</div>';
      		$str .=  '</div>';
      		$sfinal_str = $str.$sfinal_str;
      		$str = ''; 
		   }
		   
		   echo $sfinal_str;
		?>
		
	   </div>
	   <div class="clear"></div>
	   <input type="hidden" id="dialog_form" value="<?=$DIALOG_FROM_UID?>" />
   </div>