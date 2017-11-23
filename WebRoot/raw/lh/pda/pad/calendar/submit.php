<?
   include_once("../auth.php");
   include_once("inc/check_type.php");
   include_once("inc/utility_all.php");
   ob_clean();

   $CAL_TIME = td_iconv(htmlspecialchars($CAL_TIME), "utf-8", $MYOA_CHARSET);
   $END_TIME = td_iconv(htmlspecialchars($END_TIME), "utf-8", $MYOA_CHARSET);
   $CONTENT = td_iconv(htmlspecialchars($CONTENT), "utf-8", $MYOA_CHARSET);
   
   $CAL_TIME = str_replace(_("��"),":",$CAL_TIME);
   $END_TIME = str_replace(_("��"),":",$END_TIME);
   
   $CUR_DATE=date("Y-m-d",time());
   if($CAL_TIME!="")
      $CAL_TIME=$CUR_DATE." ".$CAL_TIME.":00";
   if($END_TIME!="")
      $END_TIME=$CUR_DATE." ".$END_TIME.":59";
      
		//------------------- ���� -----------------------
		if($CAL_TIME=="" || !is_date_time($CAL_TIME))
		{
		   echo _("��ʼʱ���ʽ���ԣ�Ӧ���� 09:35");
		   exit;
		}
		if($END_TIME=="" || !is_date_time($END_TIME))
		{  
		   echo _("����ʱ���ʽ���ԣ�Ӧ���� 19:23");
		   exit; 
		}
		
		if($CAL_TIME >= $END_TIME)
		{
		   echo _("��ʼʱ�����ڽ���ʱ�䣡");
		   exit;
		}
		
		$CAL_TIME=strtotime($CAL_TIME);
		$END_TIME=strtotime($END_TIME);
		if($CAL_ID=="")
		   $query="insert into CALENDAR(USER_ID,CAL_TIME,END_TIME,CAL_TYPE,CAL_LEVEL,CONTENT,OVER_STATUS) values ('$LOGIN_USER_ID','$CAL_TIME','$END_TIME','$CAL_TYPE','$CAL_LEVEL','$CONTENT','0')";
		else
		   $query="update CALENDAR set CAL_TYPE='$CAL_TYPE',CAL_LEVEL='$CAL_LEVEL',CAL_TIME='$CAL_TIME',END_TIME='$END_TIME',CONTENT='$CONTENT' where CAL_ID='$CAL_ID'";
		if(exequery($connection,$query)) 
		   echo _("OK");
		else
		   echo _("����ʧ��");
		
?>
