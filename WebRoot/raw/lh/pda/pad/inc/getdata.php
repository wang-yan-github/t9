<?
include_once('pda/pad/auth.php');
include_once("inc/utility_all.php");
include_once('funcs.php');
ob_clean();

$A = strip_tags($A);
$P = strip_tags($P);
$STYPE = strip_tags($STYPE);

$CURRITERMS = intval($CURRITERMS);       


$RETURN = '';

if($STYPE == "sms")
{
   $CUR_TIME = time();
   if($A=="GetNew")
   {
      if($LASTEDID == "")
         $LASTEDID = 0;
      $new_count = "SELECT count(*) from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME' and SMS_ID > $LASTEDID order by SEND_TIME desc";
      $count = resultCount($new_count);
      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }else{
         $query = "SELECT SMS_ID,FROM_ID,SEND_TIME,SMS_TYPE,CONTENT,USER_NAME,AVATAR,UID,REMIND_FLAG from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME' AND SMS_ID > $LASTEDID order by SEND_TIME desc";
      }
   }else{
      $query = "SELECT SMS_ID,FROM_ID,SEND_TIME,SMS_TYPE,CONTENT,USER_NAME,AVATAR,UID,REMIND_FLAG from SMS,SMS_BODY,USER where SMS.BODY_ID=SMS_BODY.BODY_ID and USER.USER_ID=SMS_BODY.FROM_ID and TO_ID='$LOGIN_USER_ID' and DELETE_FLAG!='1' and SEND_TIME<='$CUR_TIME' order by SEND_TIME desc limit ".$CURRITERMS.",$PAGE_SIZE"; 
      $count = resultCountByROW($query);
      if($count == 0)
      {
         echo "NOMOREDATA";
         exit;      
      }
   }
   
   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $SMS_COUNT++;
      $SMS_ID=$ROW["SMS_ID"];
      $FROM_ID=$ROW["FROM_ID"];
      $SEND_TIME=$ROW["SEND_TIME"];
      $REMIND_FLAG=$ROW["REMIND_FLAG"];
      $SMS_TYPE=$ROW["SMS_TYPE"];
      $CONTENT=$ROW["CONTENT"];
      $FROM_NAME=$ROW["USER_NAME"];
      $AVATAR=$ROW["AVATAR"];
      $SEX=$ROW["SEX"];
   
      $CONTENT=str_replace("<","&lt",$CONTENT);
      $CONTENT=str_replace(">","&gt",$CONTENT);
      $CONTENT=stripslashes($CONTENT);
      
      if($REMIND_FLAG!=0){
         $Class = " active";$unread = ' unread="1"';
      }else{
         $Class = "";$unread = "";
      }
      $RETURN .= '<li class="'.$fix_for_pad['list-li-style'].$Class.'" q_id="'.$SMS_ID.'"'.$unread.'>
                  <img src="'.showAvatar($AVATAR,$SEX).'" class="ui-li-thumb"/>
                  <h3><span class="time">'.timeintval($SEND_TIME).'</span>'.$FROM_NAME.'</h3>
                  <p class="content">'.strip_tags($CONTENT).'&nbsp;</p>
                  <span class="ui-icon-rarrow"></span>
               </li>';
   }
   echo $RETURN;
}else if($STYPE=="email")
{
   if($A=="GetNew" || $A=="GetNewDraft" || $A=="GetNewSend" || $A=="GetNewDel")
   {
		if($LASTEDID == "")
         $LASTEDID = 0;   
      
		switch($A){
			case "GetNew": //收件箱
			$new_count = "SELECT count(*) from EMAIL,EMAIL_BODY left join USER on EMAIL_BODY.FROM_ID=USER.USER_ID where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') AND EMAIL_ID > $LASTEDID";
			$query = "SELECT EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,from_unixtime(SEND_TIME) as SEND_TIME,IMPORTANT,CONTENT,ATTACHMENT_ID,ATTACHMENT_NAME,USER.USER_NAME from EMAIL,EMAIL_BODY left join USER on EMAIL_BODY.FROM_ID=USER.USER_ID where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') AND EMAIL_ID > $LASTEDID order by SEND_TIME DESC,EMAIL_ID desc ";   
			break;	
			case "GetNewDraft":
			$new_count = "SELECT count(*) from EMAIL_BODY where FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='0' and BODY_ID > $LASTEDID";	
			$query = "SELECT BODY_ID as EMAIL_ID,COPY_TO_ID,SECRET_TO_ID,SUBJECT,IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,CONTENT,SIZE,from_unixtime(SEND_TIME) as SEND_TIME from EMAIL_BODY where FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='0' and BODY_ID > $LASTEDID order by SEND_TIME desc,BODY_ID desc  ";
			break;
			case "GetNewSend":
			$new_count = "SELECT count(*) from EMAIL_BODY,EMAIL LEFT JOIN USER ON USER.USER_ID = EMAIL.TO_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and DELETE_FLAG!='2' and DELETE_FLAG!='4' and EMAIL_ID > $LASTEDID";
			$query = "SELECT EMAIL_ID,TO_ID,READ_FLAG,DELETE_FLAG,EMAIL_BODY.BODY_ID,TO_ID2,COPY_TO_ID,TO_WEBMAIL,SUBJECT,from_unixtime(SEND_TIME) as SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,IMPORTANT,SIZE,IS_WEBMAIL,WEBMAIL_FLAG,USER_NAME,DEPT_ID from EMAIL_BODY,EMAIL LEFT JOIN USER ON USER.USER_ID = EMAIL.TO_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and DELETE_FLAG!='2' and DELETE_FLAG!='4' and EMAIL_ID > $LASTEDID group by EMAIL.BODY_ID order by SEND_TIME DESC,EMAIL_ID desc ";
			break;
			case "GetNewDel":
			$new_count = "SELECT count(*) from EMAIL,EMAIL_BODY LEFT JOIN USER ON USER.USER_ID = EMAIL_BODY.FROM_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='3' or DELETE_FLAG='4') and EMAIL_ID > $LASTEDID";
			$query = "SELECT EMAIL_BODY.BODY_ID,EMAIL_ID,TO_ID,READ_FLAG,FROM_ID,TO_ID2,COPY_TO_ID,SUBJECT,from_unixtime(SEND_TIME) as SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,IMPORTANT,SIZE,USER_NAME,AVATAR,DEPT_ID,IS_WEBMAIL,RECV_TO,RECV_TO_ID,RECV_FROM,RECV_FROM_NAME,IS_WEBMAIL from EMAIL,EMAIL_BODY LEFT JOIN USER ON USER.USER_ID = EMAIL_BODY.FROM_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='3' or DELETE_FLAG='4') and EMAIL_ID > $LASTEDID order by SEND_TIME DESC,EMAIL_ID desc ";
			break;
			default:
			break;
		
		}
	
      //$new_count = "SELECT count(*) from EMAIL,EMAIL_BODY left join USER on EMAIL_BODY.FROM_ID=USER.USER_ID where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') AND EMAIL_ID > $LASTEDID";
      $count = resultCount($new_count);
      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }
		
   }else{

		switch($A){
			case "GetMore":  //收件箱
			$query = "SELECT EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,from_unixtime(SEND_TIME) as SEND_TIME,IMPORTANT,CONTENT,ATTACHMENT_ID,ATTACHMENT_NAME,USER.USER_NAME from EMAIL,EMAIL_BODY left join USER on EMAIL_BODY.FROM_ID=USER.USER_ID where EMAIL_BODY.BODY_ID=EMAIL.BODY_ID and BOX_ID=0 and TO_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') order by SEND_TIME DESC,EMAIL_ID desc limit ".$CURRITERMS.",$PAGE_SIZE"; 
			break;	
			case "GetMoreDraft":
			$query = "SELECT BODY_ID as EMAIL_ID,FROM_ID,COPY_TO_ID,SECRET_TO_ID,SUBJECT,IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,CONTENT,SIZE,from_unixtime(SEND_TIME) as SEND_TIME from EMAIL_BODY where FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='0' order by SEND_TIME DESC,BODY_ID desc  limit ".$CURRITERMS.",$PAGE_SIZE";
			break;
			case "GetMoreSend":
			$query = "SELECT EMAIL_ID,FROM_ID,TO_ID,READ_FLAG,DELETE_FLAG,EMAIL_BODY.BODY_ID,TO_ID2,COPY_TO_ID,TO_WEBMAIL,SUBJECT,from_unixtime(SEND_TIME) as SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,IMPORTANT,SIZE,IS_WEBMAIL,WEBMAIL_FLAG,USER_NAME,DEPT_ID from EMAIL_BODY,EMAIL LEFT JOIN USER ON USER.USER_ID = EMAIL.TO_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and FROM_ID='$LOGIN_USER_ID' and SEND_FLAG='1' and DELETE_FLAG!='2' and DELETE_FLAG!='4' group by EMAIL.BODY_ID order by SEND_TIME DESC,EMAIL_ID desc limit ".$CURRITERMS.",$PAGE_SIZE";
			break;
			case "GetMoreDel":
			$query = "SELECT EMAIL_BODY.BODY_ID,EMAIL_ID,TO_ID,READ_FLAG,FROM_ID,TO_ID2,COPY_TO_ID,SUBJECT,from_unixtime(SEND_TIME) as SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,IMPORTANT,SIZE,USER_NAME,AVATAR,DEPT_ID,IS_WEBMAIL,RECV_TO,RECV_TO_ID,RECV_FROM,RECV_FROM_NAME,IS_WEBMAIL from EMAIL,EMAIL_BODY LEFT JOIN USER ON USER.USER_ID = EMAIL_BODY.FROM_ID where EMAIL.BODY_ID=EMAIL_BODY.BODY_ID and TO_ID='$LOGIN_USER_ID' and (DELETE_FLAG='3' or DELETE_FLAG='4') order by SEND_TIME DESC,EMAIL_ID desc  limit ".$CURRITERMS.",$PAGE_SIZE";
			break;
			default:
			break;
		}
		

		$count = resultCountByROW($query);
      
      if($count == 0)
      {
         echo "NOMOREDATA";
         exit;      
      }
        
   }
   
   $cursor= exequery($connection,$query);
   while($ROW = mysql_fetch_array($cursor))
   {
      $COUNT++;
      $EMAIL_ID=$ROW["EMAIL_ID"];
      $FROM_ID=$ROW["FROM_ID"];
      $SUBJECT=$ROW["SUBJECT"];
      $SEND_TIME=$ROW["SEND_TIME"];
      $IMPORTANT=$ROW["IMPORTANT"];
      $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
      $FROM_NAME=$ROW["USER_NAME"];
      $READ_FLAG=$ROW["READ_FLAG"];
		$CONTENT = $ROW["CONTENT"];
      
      $SUBJECT=htmlspecialchars($SUBJECT);
      if($FROM_NAME == "")
      $FROM_NAME=$FROM_ID;
		
		if($A == "GetNewDraft" || $A == "GetMoreDraft")
		$FROM_NAME = _('草稿');
		
		
      if($IMPORTANT=='0' || $IMPORTANT=="")
      $IMPORTANT_DESC="";
      else if($IMPORTANT=='1')
      $IMPORTANT_DESC="<font color=red>"._("重要")."</font>";
      else if($IMPORTANT=='2')
         $IMPORTANT_DESC="<font color=red>"._("非常重要")."</font>";
         
      if($READ_FLAG!=1 && ($A == "GetNew" || $A == "GetMore"))
      {
         $Class = " unread";
      }else{
         $Class = "";
      }
      
      if($SUBJECT=="")
         $SUBJECT = _("无标题");

		$RETURN .= '<li class="'.$Class.'" q_id="'.$EMAIL_ID.'">
						<h3><span class="time">'.timeintval(strtotime($SEND_TIME)).'</span>'.$FROM_NAME.'</h3>
						'.$IMPORTANT_DESC.'<p class="title">'.$SUBJECT.'</p>
						<p class="content">'.strip_tags($CONTENT).'</p>
						<div class="multi-check"></div>';
							
      if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
      {
         $RETURN .= '<span class="iconbtn attach_icon"></span>';
      }
         $RETURN .= '<span class="ui-icon-rarrow"></span>';
      $RETURN .= '</li>';
   }
   echo $RETURN;
}else if($STYPE=="notify")
{
   $CUR_DATE=date("Y-m-d",time());
   if($A=="GetNew")
   {
      if($LASTEDID == "")
         $LASTEDID = 0; 
      $new_count = "SELECT count(*) from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and begin_date<='$CUR_DATE' and (end_date>='$CUR_DATE' or end_date is null) and not find_in_set('$LOGIN_USER_ID',READERS) and PUBLISH='1' and NOTIFY_ID > $LASTEDID ";
      $count = resultCount($new_count);
      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }else{
         $query = "SELECT NOTIFY_ID,FROM_ID,SUBJECT_COLOR,SUBJECT,TOP,TYPE_ID,BEGIN_DATE,ATTACHMENT_ID,ATTACHMENT_NAME,FORMAT,CONTENT from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and begin_date<='$CUR_DATE' and NOTIFY_ID > $LASTEDID and (end_date>='$CUR_DATE' or end_date is null) and not find_in_set('$LOGIN_USER_ID',READERS) and PUBLISH='1' order by TOP desc,BEGIN_DATE desc,SEND_TIME desc";
      }
   }else{
      $query = "SELECT NOTIFY_ID,FROM_ID,SUBJECT,TOP,TYPE_ID,BEGIN_DATE,ATTACHMENT_ID,ATTACHMENT_NAME,READERS,FORMAT,CONTENT from NOTIFY where (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID)".dept_other_sql("TO_ID")." or find_in_set('$LOGIN_USER_PRIV',PRIV_ID)".priv_other_sql("PRIV_ID")." or find_in_set('$LOGIN_USER_ID',USER_ID)) and begin_date<='$CUR_DATE' and (end_date>='$CUR_DATE' or end_date is null) and PUBLISH='1' order by TOP desc,BEGIN_DATE desc,SEND_TIME desc limit ".$CURRITERMS.",$PAGE_SIZE";
      $count = resultCountByROW($query);
      
      if($count == 0)
      {
         echo "NOMOREDATA";
         exit;      
      }
   }

   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $READERS=$ROW["READERS"];
      $NOTIFY_ID=$ROW["NOTIFY_ID"];
      $SUBJECT_COLOR=$ROW["SUBJECT_COLOR"];
      $FROM_ID=$ROW["FROM_ID"];
      $SUBJECT=$ROW["SUBJECT"];
      $TOP=$ROW["TOP"];
      $TYPE_ID=$ROW["TYPE_ID"];
      $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
   
      $SUBJECT=str_replace("<","&lt",$SUBJECT);
      $SUBJECT=str_replace(">","&gt",$SUBJECT);
      $SUBJECT=stripslashes($SUBJECT);
   	  
	   $FORMAT=$ROW["FORMAT"];
	   $COMPRESS_CONTENT=@gzuncompress($ROW["COMPRESS_CONTENT"]);	  
	   $CONTENT= $COMPRESS_CONTENT!=""&&$FORMAT!="2" ? $COMPRESS_CONTENT : $ROW["CONTENT"];
		
      $BEGIN_DATE=$ROW["BEGIN_DATE"];
      $BEGIN_DATE=strtok($BEGIN_DATE," ");
   
      $TYPE_NAME=get_code_name($TYPE_ID,"NOTIFY");
      if($TYPE_NAME!="")
         $SUBJECT="[".$TYPE_NAME."]".$SUBJECT;
      $SUBJECT="<font color='".$SUBJECT_COLOR."'>".$SUBJECT."</font>";
   
      $query1 = "SELECT USER_NAME from USER where USER_ID='$FROM_ID'";
      $cursor1= exequery($connection,$query1);
      if($ROW=mysql_fetch_array($cursor1))
      $FROM_NAME=$ROW["USER_NAME"];
      else
      $FROM_NAME=$FROM_ID;
   
      if($TOP=='1')
      $IMPORTANT_DESC="<img src='../style/images/top.png' />";
      else
      $IMPORTANT_DESC="";
      
      if(!find_id($READERS,$LOGIN_USER_ID))
      {
         $Class = " active";$unread = ' unread="1"';
      }else{
         $Class = "";$unread = "";
      }
      
		$RETURN .= '<li class="'.$fix_for_pad['list-li-style'].$Class.'" q_id="'.$NOTIFY_ID.'" '.$unread.'>';
		$RETURN .= '<h3>'.$FROM_NAME.'<span class="time">'.$BEGIN_DATE.'</span></h3>';
		$RETURN .= '<p class="title">'.$SUBJECT.$IMPORTANT_DESC.'</p>';
		$RETURN .= '<p class="content">'.$CONTENT.'</p>';
		
		if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
		{
			$RETURN .= '<span class="iconbtn attach_icon"></span>';
		}
		
		$RETURN .= '</li>';
   }
   echo $RETURN;
}
else if($STYPE=="news")
{
   if($A=="GetNew")
   {
      if($LASTEDID == "")
         $LASTEDID = 0;
         
      $new_count = "SELECT count(*) from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID)) and NEWS_ID > $LASTEDID order by NEWS_ID desc";

      $count = resultCount($new_count);
      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }else{
         $query = "SELECT READERS,CLICK_COUNT,SUBJECT_COLOR,NEWS_ID,PROVIDER,SUBJECT,NEWS_TIME,LAST_EDIT_TIME,FORMAT,TYPE_ID,ATTACHMENT_ID,ATTACHMENT_NAME from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID)) and NEWS_ID > $LASTEDID order by NEWS_ID desc ";
      }
   }else{
      $CUR_DATE=date("Y-m-d",time());
      $query = "SELECT READERS,CLICK_COUNT,SUBJECT_COLOR,NEWS_ID,PROVIDER,SUBJECT,NEWS_TIME,LAST_EDIT_TIME,FORMAT,TYPE_ID,ATTACHMENT_ID,ATTACHMENT_NAME from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or find_in_set('$LOGIN_USER_PRIV',PRIV_ID) or find_in_set('$LOGIN_USER_ID',USER_ID)) order by NEWS_ID desc ";
      $query .= "limit ".$CURRITERMS.",$PAGE_SIZE";
      $count = resultCountByROW($query);
         if($count == 0)
         {
            echo "NOMOREDATA";
            exit;      
         }
   }

   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $NEWS_ID=$ROW["NEWS_ID"];
      $PROVIDER=$ROW["PROVIDER"];
      $SUBJECT=$ROW["SUBJECT"];
      $NEWS_TIME=$ROW["NEWS_TIME"];
      $LAST_EDIT_TIME=$ROW["LAST_EDIT_TIME"];
      $FORMAT=$ROW["FORMAT"];
      $TYPE_ID=$ROW["TYPE_ID"];
      $ATTACHMENT_ID=$ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME=$ROW["ATTACHMENT_NAME"];
      $READERS=$ROW["READERS"];
      $SUBJECT_COLOR=$ROW["SUBJECT_COLOR"];

      $CLICK_COUNT=$ROW["CLICK_COUNT"];
   
      $SUBJECT=htmlspecialchars($SUBJECT);
   
      $TYPE_NAME=get_code_name($TYPE_ID,"NEWS");
      if($TYPE_NAME!="")
         $SUBJECT="[".$TYPE_NAME."]".$SUBJECT;
      $SUBJECT="<font color='".$SUBJECT_COLOR."'>".$SUBJECT."</font>";
      
      $query1 = "SELECT USER_NAME from USER where USER_ID='$PROVIDER'";
      $cursor1= exequery($connection,$query1);
      if($ROW=mysql_fetch_array($cursor1))
         $FROM_NAME=$ROW["USER_NAME"];
      else
         $FROM_NAME=$FROM_ID;
         
      if(!find_id($READERS,$LOGIN_USER_ID))
      {
         $Class = " active";$unread = ' unread="1"';
      }else{
         $Class = "";$unread = "";
      }
      
      if($LAST_EDIT_TIME == "0000-00-00 00:00:00")
      {
         if(strtotime($NEWS_TIME) > time()){
            $TIME = substr($NEWS_TIME,0,10);      
         }else{
            $TIME = timeintval(strtotime($NEWS_TIME));          
         }         
      }else{
         $TIME = timeintval(strtotime($LAST_EDIT_TIME));      
      }
      
      $RETURN .= '<li class="'.$fix_for_pad['list-li-style'].$Class.'" q_id="'.$NEWS_ID.'" '.$unread.' >
      <h3>'.$SUBJECT.'</h3>
      <p class="content">'.$FROM_NAME.' - '.$TIME.'</p>';
      if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
      {
         $RETURN .= '<span class="iconbtn attach_icon"></span>';
      }
      $RETURN .= '<span class="ui-icon-rarrow"></span>';
      $RETURN .= '</li>';
   }
   echo $RETURN;
}else if($STYPE=="calendar")
{
   if($A=="refreshList")
   {
      $CURRITERMS = intval($CURRITERMS);
      
      $CUR_DATE=date("Y-m-d",time());
      $query = "SELECT count(*) from CALENDAR where USER_ID='$LOGIN_USER_ID' and to_days(from_unixtime(CAL_TIME))=to_days('$CUR_DATE')";
      $count_calendar = resultCount($query);
      
      $CUR_TIME_U=time();
      $query = "SELECT count(*) from AFFAIR where USER_ID='$LOGIN_USER_ID' and BEGIN_TIME<='$CUR_TIME_U'";
      $count_affair = resultCount($query);

      $count = $count_calendar + $count_affair;
      
      if(($count == 0) or ($count == $CURRITERMS))
      {
         echo "NONEWDATA";
         exit;
      }else
      {
         //2012/5/18 1:27:12 lp 日常事务
         if($count_calendar > 0)
         {
            $query = "SELECT * from CALENDAR where USER_ID='$LOGIN_USER_ID' and to_days(from_unixtime(CAL_TIME))=to_days('$CUR_DATE') order by CAL_ID desc";
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
               $CAL_ID=$ROW["CAL_ID"];
               $CAL_TIME=$ROW["CAL_TIME"];
               $CAL_TIME=date("Y-m-d H:i:s",$CAL_TIME);
               $END_TIME=$ROW["END_TIME"];
               $END_TIME=date("Y-m-d H:i:s",$END_TIME);
               $CAL_TIME=strtok($CAL_TIME," ");
               $CAL_TIME=strtok(" ");
               $CAL_TIME=substr($CAL_TIME,0,5);
               
               $END_TIME=strtok($END_TIME," ");
               $END_TIME=strtok(" ");
               $END_TIME=substr($END_TIME,0,5);
               $CAL_LEVEL = $ROW["CAL_LEVEL"];
               $CONTENT=$ROW["CONTENT"];
               $CONTENT=str_replace("<","&lt",$CONTENT);
               $CONTENT=str_replace(">","&gt",$CONTENT);
               $CONTENT=stripslashes($CONTENT);
               $CAL_LEVEL_DESC = cal_level_desc_fix($CAL_LEVEL);
               
               $RETURN .= '<li class="'.$fix_for_pad['list-li-style'].'" q_id="'.$CAL_ID.'" >
                                 <h3>'.$CAL_TIME.' - '.$END_TIME.'</h3>
                                 <p class="content">'.$CAL_LEVEL_DESC." ".$CONTENT.'</p>
                                 <span class="ui-icon-rarrow"></span>
                           </li>'; 
            }
         }
         
         //2012/5/18 1:27:12 lp 周期性事务
         if($count_affair > 0)
         {
            $query = "SELECT * from AFFAIR where USER_ID='$LOGIN_USER_ID' and BEGIN_TIME<='$CUR_TIME_U' order by REMIND_TIME";
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
      		    $AFF_ID=$ROW["AFF_ID"];
      		    $USER_ID=$ROW["USER_ID"];
      		    $TYPE=$ROW["TYPE"];
      		    $REMIND_DATE=$ROW["REMIND_DATE"];
      		    $REMIND_TIME=$ROW["REMIND_TIME"];
      		    $CONTENT=$ROW["CONTENT"];
      		
      		    $FLAG=0;
      		    if($TYPE=="2")
      		       $FLAG=1;
      		    elseif($TYPE=="3" && date("w",time())==$REMIND_DATE)
      		       $FLAG=1;
      		    elseif($TYPE=="4" && date("j",time())==$REMIND_DATE)
      		       $FLAG=1;
      		    elseif($TYPE=="5")
      		    {
      		       $REMIND_ARR=explode("-",$REMIND_DATE);
      		       $REMIND_DATE_MON=$REMIND_ARR[0];
      		       $REMIND_DATE_DAY=$REMIND_ARR[1];
      		       if(date("n",time())==$REMIND_DATE_MON && date("j",time())==$REMIND_DATE_DAY)
      		          $FLAG=1;
      		    }
      		    if($FLAG!=1) continue;
             $RETURN .= '<li class="'.$fix_for_pad['list-li-style'].'">
                                 <h3>'.substr($REMIND_TIME,0,5).'</h3>
                                 <p class="content">'._("周期性事务：").$CONTENT.'</p>
                           </li>';    
            }
         }
         
         echo $RETURN;
      }
   }
     

}else if($STYPE=="diary")
{
   if($A=="GetNew")
   {
      if($LASTEDID == "")
         $LASTEDID = 0;
         
      $new_count = "SELECT count(*) from DIARY where USER_ID='$LOGIN_USER_ID' and DIA_ID > $LASTEDID";
      $count = resultCount($new_count);
      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }else{
         $query = "SELECT * from DIARY where USER_ID='$LOGIN_USER_ID' AND DIA_ID > $LASTEDID order by DIA_ID desc";
      }
   }else{
      $query = "SELECT * from DIARY where USER_ID='$LOGIN_USER_ID' order by DIA_ID desc limit ".$CURRITERMS.",$PAGE_SIZE"; 
      $count = resultCount($query);
      if($count == 0)
      {
         echo "NOMOREDATA";
         exit;      
      }
   }

   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $DIA_ID=$ROW["DIA_ID"];
      $DIA_DATE=$ROW["DIA_DATE"];
      $DIA_DATE=strtok($DIA_DATE," ");
      $DIA_TYPE=$ROW["DIA_TYPE"];
      $CONTENT=$ROW["CONTENT"];
      $SUBJECT=$ROW["SUBJECT"];
      //$DIA_TYPE_DESC=get_code_name($DIA_TYPE,"DIARY_TYPE");
      
      $CONTENT=str_replace("<","&lt",$CONTENT);
      $CONTENT=str_replace(">","&gt",$CONTENT);
      $CONTENT=stripslashes($CONTENT);
      
      $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
      
      if($SUBJECT=="")
         $SUBJECT = _("无标题");
      $RETURN .= '<li class="'.$fix_for_pad['list-li-style'].'" q_id="'.$DIA_ID.'">
                        <h3>'.$SUBJECT.'</h3>
                        <p class="content">'.strip_tags($CONTENT).'&nbsp;</p>';
      if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
      {
         $RETURN .= '<span class="iconbtn attach_icon"></span>';
      }
      $RETURN .=  '<span class="ui-icon-rarrow"></span>';
      $RETURN .= '</li>';
   }
   echo $RETURN;
}else if($STYPE=="file_folder")
{
   $WHERE_STR = " where SORT_ID=0 and USER_ID='$LOGIN_USER_ID' ";
   $ORDERBY = "order by  CONTENT_ID desc ";
   if($A=="GetNew")
   {
      $new_count = "SELECT count(*) from FILE_CONTENT ".$WHERE_STR." and CONTENT_ID > $LASTEDID ".$ORDERBY;
      $count = resultCount($new_count);

      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }else{
         $query = "SELECT * from FILE_CONTENT ".$WHERE_STR." and CONTENT_ID > $LASTEDID ".$ORDERBY;
      }
   }else{//不返回更多
      $CUR_DATE=date("Y-m-d",time());
      $LIMIT = "limit ".$CURRITERMS.",$PAGE_SIZE";
      $query = "SELECT * from FILE_CONTENT ".$WHERE_STR.$ORDERBY.$LIMIT;
      $count = resultCountByROW($query);
         if($count == 0)
         {
            echo "NOMOREDATA";
            exit;      
         }
   }

   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $CONTENT_ID = $ROW["CONTENT_ID"];
      $SUBJECT = $ROW["SUBJECT"];
      $SEND_TIME = $ROW["SEND_TIME"];
      $READERS = $ROW["READERS"];
      $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
      $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
      $SUBJECT=htmlspecialchars($SUBJECT);
      // if(!find_id($READERS,$LOGIN_USER_ID))
      // {
         // $Class = " active";$unread = ' unread="1"';
      // }else{
         $Class = "";$unread = "";
      // }
      if($SUBJECT=="")
         $SUBJECT = _("无标题");
         
      $RETURN.= '<li class="'.$fix_for_pad['list-li-style'].$Class.'" q_id="'.$CONTENT_ID.'"'.$unread.'>
                        <h3>'.$SUBJECT.'</h3>
                        <p class="content">'.timeintval(strtotime($SEND_TIME)).'</p>'; 
      if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
      {
         $RETURN .= '<span class="iconbtn attach_icon"></span>';
      }
      $RETURN .= '<span class="ui-icon-rarrow"></span></li>';
                  
   }
   echo $RETURN;
}else if($STYPE=="user_info"){

   if($A=="GetNew")
   {
      echo "NONEWDATA";
      exit;
   }else{//不返回更多
      $WHERE_STR = " where DEPARTMENT.DEPT_ID!=0 and USER.USER_PRIV=USER_PRIV.USER_PRIV and USER.DEPT_ID=DEPARTMENT.DEPT_ID ";
      $ORDERBY = " order by PRIV_NO,USER_NO,USER_NAME ";
      $CUR_DATE=date("Y-m-d",time());
      $LIMIT = "limit ".$CURRITERMS.",$PAGE_SIZE";
      $query = "SELECT UID,SEX,USER_ID,USER_NAME,USER.DEPT_ID,PRIV_NAME from USER,USER_PRIV,DEPARTMENT ".$WHERE_STR.$ORDERBY.$LIMIT;
      $count = resultCountByROW($query);
         if($count == 0)
         {
            echo "NOMOREDATA";
            exit;      
         }

		$cursor= exequery($connection,$query);

		while($ROW=mysql_fetch_array($cursor))
		{
		   $UID=$ROW["UID"];
		   $USER_NAME=$ROW["USER_NAME"];
		   $PRIV_NAME=$ROW["PRIV_NAME"];
		   $DEPT_ID=$ROW["DEPT_ID"];
		   $SEX=$ROW["SEX"];
         $AVATAR=$ROW["AVATAR"];

		   $DEPT_LONG_NAME=dept_long_name($DEPT_ID);
		
		   if($SEX==0)
		      $SEX=_("男");
		   else
		      $SEX=_("女");

      $RETURN .= '<li class="'.$fix_for_pad['list-li-style'].'" q_id="'.$UID.'" >
                     <img src="'.showAvatar($AVATAR,$SEX).'" class="ui-li-thumb"/>
                     <h3>'.$USER_NAME._("(").$SEX._(")").'</h3>
                     <p class="content">'._("部门：").$DEPT_LONG_NAME.' '._("角色：").$PRIV_NAME.'</p>
                     <span class="ui-icon-rarrow"></span>
                  </li>'; 
		}//while
   echo $RETURN;
      }
}else if($STYPE=="address"){
   
   if($A == "getPsnList")
   {
      $PSN_NAME = td_iconv($PSN_NAME, "utf-8", $MYOA_CHARSET);
      $DEPT_NAME = td_iconv($PSN_NAME, "utf-8", $MYOA_CHARSET);
      
      $GROUP_ID_STR = '';
      $query = "select GROUP_ID from ADDRESS_GROUP where USER_ID='$LOGIN_USER_ID' or  (USER_ID='' and (find_in_set('$LOGIN_USER_ID',PRIV_USER) or PRIV_DEPT='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',PRIV_DEPT) or find_in_set('$LOGIN_USER_PRIV',PRIV_ROLE)))";
      $cursor= exequery($connection,$query);
      while($ROW=mysql_fetch_array($cursor))
      {
         $GROUP_ID=$ROW["GROUP_ID"];
         $GROUP_ID_STR.=$GROUP_ID.",";
      }
      $GROUP_ID_STR=$GROUP_ID_STR."0";
      
      $query = "SELECT * from ADDRESS where GROUP_ID in ($GROUP_ID_STR)";
      if($PSN_NAME!="")
         $query .= " and PSN_NAME like '%$PSN_NAME%'";
      if($DEPT_NAME!="")
         $query .= " and DEPT_NAME like '%$DEPT_NAME%'";
      
      $count = resultCountByROW($query);
      if($count == 0)
      {
         echo "NOFINDDATA";
         exit;
      }
         
      $cursor= exequery($connection,$query);
      while($ROW=mysql_fetch_array($cursor))
      {
         $ADD_ID = $ROW["ADD_ID"];
         $GROUP_ID=$ROW["GROUP_ID"];
         $PSN_NAME=$ROW["PSN_NAME"];
         $SEX=$ROW["SEX"];
         $BIRTHDAY=$ROW["BIRTHDAY"];
         $MINISTRATION=$ROW["MINISTRATION"];
         $DEPT_NAME=$ROW["DEPT_NAME"];
         $TEL_NO_DEPT=$ROW["TEL_NO_DEPT"];
         $TEL_NO_HOME=$ROW["TEL_NO_HOME"];
         $MOBIL_NO=$ROW["MOBIL_NO"];
         $EMAIL=$ROW["EMAIL"];
      
         $query1 = "select * from ADDRESS_GROUP where GROUP_ID='$GROUP_ID'";
         $cursor1= exequery($connection,$query1);
         if($ROW1=mysql_fetch_array($cursor1))
            $GROUP_NAME=$ROW1["GROUP_NAME"];
         if($GROUP_ID==0)
            $GROUP_NAME=_("默认");
            
         $GROUP_NAME = "[".$GROUP_NAME."]";
      
         switch($SEX)
         {
            case "0":$SEX=_("男");break;
            case "1":$SEX=_("女");break;
         }
         
         $RETURN.= '<li class="'.$fix_for_pad['list-li-style'].'" q_id="'.$ADD_ID.'">
         <h3>'.$GROUP_NAME.' '.$PSN_NAME._("(").$SEX._(")").'</h3>
         <p class="content">'._("单位").':'.$DEPT_NAME.'</p>
         <span class="ui-icon-rarrow"></span>
         </li>';
      }
      echo $RETURN;
      exit;
   }
}else if($STYPE=="tel_no"){
   if($A == "getTelNOList")
   {
      mysql_select_db("BUS", $connection);
      $AREA = td_iconv($AREA, "utf-8", $MYOA_CHARSET);
      $TEL_NO = td_iconv($TEL_NO, "utf-8", $MYOA_CHARSET);
      $POST_NO = td_iconv($POST_NO, "utf-8", $MYOA_CHARSET);
      
      $query = "SELECT * from POST_TEL where 1=1 ";
      if($AREA!="")
         $query.= " and (CITY like '%$AREA%' or COUNTY like '%$AREA%' or TOWN like '%$AREA%')";
      if($TEL_NO!="")
         $query.= " and TEL_NO like '%$TEL_NO%'";
      if($POST_NO!="")
         $query.= " and POST_NO like '%$POST_NO%'";
      
      $count = resultCountByROW($query);
      if($count == 0)
      {
         echo "NOFINDDATA";
         exit;
      }
         
      $cursor= exequery($connection,$query);
      while($ROW=mysql_fetch_array($cursor))
      {
         $NO = $ROW["NO"];
         $PROVINCE=$ROW["PROVINCE"];
         $CITY=$ROW["CITY"];
         $COUNTY =$ROW["COUNTY"];
         $TOWN=$ROW["TOWN"];
         $TEL_NO=$ROW["TEL_NO"];
         $POST_NO=$ROW["POST_NO"];
         
         $RETURN.= '<li class="'.$fix_for_pad['list-li-style'].'" q_id="'.$NO.'">
         <h3 class="title">'.$PROVINCE.$_AREA.'</h3>
         <p class="grapc">'._("省(直辖市/自治区)").'：'.$PROVINCE.'</p>
         <p class="grapc">'._("城市").'：'.$CITY.'</p>
         <p class="grapc">'._("区/县").'：'.$COUNTY.'</p>
         <p class="grapc">'._("街道").'：'.$TOWN.'</p>
         <p class="grapc">'._("区号").'：'.$TEL_NO.'</p>
         <p class="grapc">'._("邮编").'：'.$POST_NO.'</p>
         </li>';
      }
      echo '<ul class="comm-list sideBarSubList preViewList">'.$RETURN.'</ul>';
      exit;
   }
}else if($STYPE=="workflow"){
   
   $WHERE_STR = " WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and DEL_FLAG=0 and PRCS_FLAG<'3' and not (TOP_FLAG='1' and PRCS_FLAG=1) ";
   $ORDERBY = " order by FLOW_RUN_PRCS.CREATE_TIME desc ";   
  
   if($A=="GetNew")
   {
      $new_count = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE ".$WHERE_STR." and FLOW_RUN_PRCS.CREATE_TIME > '$LASTEDID' ".$ORDERBY;
      $count = resultCount($new_count);
      if($count == 0)
      {
         echo "NONEWDATA";
         exit;      
      }else{
         $query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE ".$WHERE_STR." and FLOW_RUN_PRCS.CREATE_TIME > '$LASTEDID' ".$ORDERBY;
      }
   }else
   {
      $LIMIT = "limit ".$CURRITERMS.",$PAGE_SIZE";
      $query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE ".$WHERE_STR." and FLOW_RUN_PRCS.CREATE_TIME < '$LASTGETID' ".$ORDERBY.$LIMIT;
      $count = resultCountByROW($query);
      if($count == 0)
      {
         echo "NOMOREDATA";
         exit;      
      }
   }
   
   $cursor= exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $PRCS_ID = $ROW["PRCS_ID"];
      $RUN_ID = $ROW["RUN_ID"];
      $FLOW_ID = $ROW["FLOW_ID"];
      $PRCS_FLAG = $ROW["PRCS_FLAG"];
      $FLOW_PRCS = $ROW["FLOW_PRCS"];
      $OP_FLAG = $ROW["OP_FLAG"];
      $CREATE_TIME = $ROW["CREATE_TIME"];
      
      if($OP_FLAG=="1")
      	$OP_FLAG_DESC=_("主办");
      else
      	$OP_FLAG_DESC=_("会签");
      
      if($PRCS_FLAG=="1")
      {
         //LP 2012/4/18 10:58:25 增加未接收工作高亮处理
         $STATUS=_("未接收");
         $Class = " active";
      }else if($PRCS_FLAG=="2")
      {
         $STATUS=_("已接收");
         $Class = "";
      }
      
      $query = "SELECT FLOW_ID,RUN_NAME from FLOW_RUN WHERE RUN_ID='$RUN_ID'";
      $cursor1= exequery($connection,$query);
      if($ROW=mysql_fetch_array($cursor1))
      {
         $FLOW_ID=$ROW["FLOW_ID"];
         $RUN_NAME=$ROW["RUN_NAME"];
      }
      
      $query = "SELECT FLOW_NAME,FLOW_TYPE from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
      $cursor1= exequery($connection,$query);
      if($ROW=mysql_fetch_array($cursor1))
      {
         $FLOW_NAME=$ROW["FLOW_NAME"];
         $FLOW_TYPE=$ROW["FLOW_TYPE"];
      }
      
      if($FLOW_TYPE=="1")
      {
         $query = "SELECT PRCS_NAME,FEEDBACK from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS'";
         $cursor1= exequery($connection,$query);
         if($ROW=mysql_fetch_array($cursor1))
         {
            $PRCS_NAME=sprintf(_("第%s步："), $PRCS_ID).$ROW["PRCS_NAME"];
            $FEEDBACK=$ROW["FEEDBACK"];
         }
      }
      else
      {
         $PRCS_NAME=sprintf(_("第%s步"), $PRCS_ID);
         $FEEDBACK = 0;
      }

      $RETURN.='<li class="'.$fix_for_pad['list-li-style'].$Class.'" q_id="'.$CREATE_TIME.'" q_run_id="'.$RUN_ID.'"  q_flow_id="'.$FLOW_ID.'"  q_prcs_id="'.$PRCS_ID.'"  q_flow_prcs="'.$FLOW_PRCS.'" q_op_flag="'.$OP_FLAG.'">
            <h3>['.$RUN_ID.'] - '.$FLOW_NAME.' - '.$RUN_NAME.'</h3>
            <p class="content">'.$PRCS_NAME.' '.$OP_FLAG_DESC.'</p>
            <span class="ui-icon-rarrow"></span>
      </li>';
   }     
   echo $RETURN;
}else if($STYPE=="message"){
   
   //仅供手动刷新使用
   if($A == "getNewListSmsNumAndContent")
   {
      $TO_UID = intval($TO_UID);
      $query = "SELECT count(*) from message where TO_UID='$LOGIN_UID' and DELETE_FLAG!='1' and REMIND_FLAG ='1'";   
      $num = resultCount($query);
      if($num == 0)
      {
         echo "NONEWDATA";
         exit;  
      }else{
         echo $num;
         exit;     
      }
   }else if($A == "refreshList")
   {
      include_once('pda/message/user.php');
      $TO_UID = intval($TO_UID);
		$new_data = array();
		
		$MSG_USER_LIST = array();
      $MSG_LIST = array();
      $MSG_COUNT = array();
		$new_msg = $str = '';
		
      $query = "SELECT FROM_UID,TO_UID,REMIND_FLAG,SEND_TIME,CONTENT from message where (TO_UID='$LOGIN_UID' or FROM_UID='$LOGIN_UID') and DELETE_FLAG!='1' order by MSG_ID DESC";
		$cursor= exequery($connection,$query);
      while($ROW = mysql_fetch_array($cursor))
      {
         $FROM_UID = $ROW['FROM_UID'];
         $TO_UID = $ROW['TO_UID'];
         $SEND_TIME = $ROW['SEND_TIME'];
         $REMIND_FLAG = $ROW['REMIND_FLAG'];
         $CONTENT = $ROW['CONTENT'];
         
         //计算给我发的新的未读消息条数
         if(($TO_UID == $LOGIN_UID) && ($REMIND_FLAG == 1))
         {
            if(!$MSG_COUNT["USER_".$FROM_UID])
               $MSG_COUNT["USER_".$FROM_UID] = 0;
            $MSG_COUNT["USER_".$FROM_UID]++;
         }
            
         if(($TO_UID == $LOGIN_UID) and (!array_key_exists("USER_".$FROM_UID,$MSG_USER_LIST)))
         {
            $MSG_USER_LIST["USER_".$FROM_UID] = 1;
            $MSG_LIST["USER_".$FROM_UID] = array($FROM_UID,$SEND_TIME,($REMIND_FLAG == 1 ? 1 : 0),$CONTENT);         
         }
         
         if(($FROM_UID == $LOGIN_UID) and (!array_key_exists("USER_".$TO_UID,$MSG_USER_LIST)))
         {
            $MSG_USER_LIST["USER_".$TO_UID] = "USER_".$TO_UID;
            $MSG_LIST["USER_".$TO_UID] = array($TO_UID,$SEND_TIME,0,$CONTENT);      
         }
         
         if(count($MSG_LIST) >= $PAGE_SIZE) break;
      }
      
      $MSG_NUM = count($MSG_LIST);
		if($MSG_NUM <= 0){
			echo "NO";
			exit;
		}
		
		foreach($MSG_LIST as $k => $v)
		{
	      if($USER_ARRAY[$v[0]]['NAME']=="") continue;
			$new_msg = ($v[2] > 0) ? ' active' : '';
			$str .= '<li class="'.$fix_for_pad['list-li-style'].$new_msg.'" q_id="'.$v[0].'" q_name="'.$USER_ARRAY[$v[0]]['NAME'].'">';
			$str .= 		'<img src="'.showAvatar($USER_ARRAY[$v[0]]['AVATAR'],$USER_ARRAY[$v[0]]['SEX']).'" class="ui-li-thumb"/>';
			$str .= 			'<h3><span class="time">'.timeintval($v[1]).'</span>'.$USER_ARRAY[$v[0]]['NAME'].'</h3>';
			$str .= 			'<p class="content">'.strip_tags($v[3]).'&nbsp;</p>';
			if(array_key_exists("USER_".$v[0],$MSG_COUNT))
			{
			   $str .=     '<span class="ui-icon-num">'.$MSG_COUNT['USER_'.$v[0]].'</span>';
		   }
			$str .= '</li>';
      }	
	   echo $str;   
      
   }else if($A == "sendSignleMsg"){
      
      $MSG = td_iconv(htmlspecialchars($MSG), "utf-8", $MYOA_CHARSET);
		$TO_UID = intval($TO_UID);
		$str = '';
		
		$SEND_TIME = timeintval(time());
		include_once("inc/utility_msg.php");
		include_once("pda/message/user.php");
		
		send_msg($LOGIN_UID,$TO_UID,$C['msg_type'],$MSG);
		$str .= '<div class="mycust-list line2 clear" style="display:none;">';
		$str .= 		'<div class="mycust-dialogbox-time">'.$SEND_TIME.'</div>';
		$str .=        '<div class="mycust-dialogbox">';
		$str .= 			   '<a href="#" class="mycust-avatar"><img src="'.showAvatar($USER_ARRAY[$LOGIN_UID]['AVATAR'],$USER_ARRAY[$LOGIN_UID]['SEX']).'" /></a>';
		$str .= 		      '<div class="mycust-diobox">';
		$str .=              '<div class="cl"></div>';
		$str .=			      '<div class="mycust-list-msg">'.$MSG.'</div>';
		$str .=		      '</div>';
		$str .=        '</div>';
		$str .=  '</div>'; 
		echo $str;
   }else if($A == "getSingleNewMsg"){
      
      include_once("pda/message/user.php");
      include_once("inc/utility_msg.php");
		   
      $_FROM_UID = intval($TO_UID);
		
		$id_str_prefix = $id_str = $final_str = $_str = $online_type = $str = $msg_type_name =  '';
	
		$query = "SELECT MSG_ID,FROM_UID,SEND_TIME,CONTENT,MSG_TYPE from message where (TO_UID='$LOGIN_UID' and FROM_UID='$_FROM_UID') and DELETE_FLAG!='1' and REMIND_FLAG = 1 order by MSG_ID desc";
		$cursor= exequery($connection,$query);
		$rc = mysql_affected_rows();
		if($rc > 0)
		{
   		while($ROW=mysql_fetch_array($cursor))
   		{
   			$MSG_ID = $ROW['MSG_ID'];
   			$FROM_UID = $ROW['FROM_UID'];
   			$TO_UID = $ROW['TO_UID'];
   			$SEND_TIME = $ROW['SEND_TIME'];
   			$CONTENT = $ROW['CONTENT'];
   			$MSG_TYPE = $ROW['MSG_TYPE'];
   			$SEND_TIME = timeintval($SEND_TIME);
   			$count++;
   			$line_style = $FROM_UID == $LOGIN_UID ? "line2" : "line1";
   			$id_str.= $id_str_prefix.$MSG_ID;
   			$id_str_prefix = ",";
   			$msg_type_name = ($FROM_UID != $LOGIN_UID && $MSG_TYPE !=0) ? " - "._("来自").get_msg_type_name($MSG_TYPE) : '';
   			$str .= '<div class="mycust-list '.$line_style.' clear" style="display:none;">';
      		$str .= 		'<div class="mycust-dialogbox-time">'.$SEND_TIME.$msg_type_name.'</div>';
      		$str .=        '<div class="mycust-dialogbox">';
      		$str .= 			   '<a href="#" class="mycust-avatar"><img src="'.showAvatar($USER_ARRAY[$FROM_UID]['AVATAR'],$USER_ARRAY[$FROM_UID]['SEX']).'" class="ui-li-thumb"/></a>';
      		$str .= 		      '<div class="mycust-diobox">';
      		$str .=              '<div class="cl"></div>';
      		$str .=			      '<div class="mycust-list-msg">'.$CONTENT.'</div>';
      		$str .=		      '</div>';
      		$str .=        '</div>';
      		$str .=  '</div>';       
         }  
         echo $str;  
         $query1 = "UPDATE message SET REMIND_FLAG = 2 WHERE MSG_ID IN (".$id_str.")";
			exequery($connection,$query1);
			
         exit;
      }else{
         echo "NO";
         exit;   
      }
   
   }else if($A == "mutisend"){
      require_once 'inc/utility_msg.php';
      $CONTENT = td_iconv(htmlspecialchars($CONTENT), "utf-8", $MYOA_CHARSET);
		$str = '';
		$SEND_TIME = date("H:i:s",time());
		send_msg($LOGIN_UID,$TO_UID,$C['msg_type'],$CONTENT);
		echo "+OK";
		exit;
   }         
   
}
?>