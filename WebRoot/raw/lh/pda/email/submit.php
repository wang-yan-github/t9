<?
   include_once("../auth.php");
   include_once("inc/utility_all.php");
   include_once("inc/utility_sms1.php");
   ob_clean();
   
   $TO_ID = td_iconv(htmlspecialchars($TO_ID), "utf-8", $MYOA_CHARSET);
   $CS_ID = td_iconv(htmlspecialchars($CS_ID), "utf-8", $MYOA_CHARSET);
   $WEBMAIL = td_iconv(htmlspecialchars($TO_NAME2), "utf-8", $MYOA_CHARSET);
   $SUBJECT = td_iconv(htmlspecialchars($SUBJECT), "utf-8", $MYOA_CHARSET);
   $CONTENT = td_iconv(htmlspecialchars($CONTENT), "utf-8", $MYOA_CHARSET);

   $SEND_TIME=time();

   //发外网邮件
   if($WEBMAIL!="")
   {
     $query = "SELECT * from WEBMAIL where USER_ID='$LOGIN_USER_ID' and EMAIL_PASS!='' limit 1";
     $cursor= exequery($connection,$query);
     if($ROW=mysql_fetch_array($cursor))
     {
       $EMAIL=$ROW["EMAIL"];
       $SMTP_SERVER=$ROW["SMTP_SERVER"];
       $LOGIN_TYPE=$ROW["LOGIN_TYPE"];
       $SMTP_PASS=$ROW["SMTP_PASS"];
       $SMTP_PORT=$ROW["SMTP_PORT"];
       $SMTP_SSL=$ROW["SMTP_SSL"]=="1" ? "ssl":"";
       $EMAIL_PASS=$ROW["EMAIL_PASS"];
       $EMAIL_PASS=td_authcode($EMAIL_PASS,"DECODE");
   
       if($LOGIN_TYPE=="1")
         $SMTP_USER = substr($EMAIL,0,strpos($EMAIL,"@")); // SMTP username
       else
         $SMTP_USER =$EMAIL;
       if($SMTP_PASS=="yes")
         $SMTP_PASS = $EMAIL_PASS; // SMTP password
       else
         $SMTP_PASS = "";
   
       $result=send_mail($EMAIL,$WEBMAIL,$SUBJECT,$CONTENT,$SMTP_SERVER,$SMTP_USER,$SMTP_PASS,true,$LOGIN_USER_NAME,$REPLY_TO,$CC,$BCC,$ATTACHMENT,true,$SMTP_PORT,$SMTP_SSL);
       if($result===true)
       {
         echo _("外部邮件发送成功");
         exit;
       }
       else
       {
         $query="update EMAIL_BODY set SEND_FLAG='0' where BODY_ID=".intval($BODY_ID);
         exequery($connection,$query);
         echo _("外部邮件发送失败");
         exit;
       }
     }
     else
       echo _("您没有定义Internet邮箱！");
       exit;
   }

   /*$TO_NAME_ARRAY1=explode(",",$TO_NAME1);
   while(list(,$value)=each($TO_NAME_ARRAY1))
   {
     if($value)
     {
       $query="select USER_ID from USER WHERE USER_NAME='$value'";
       $cursor=exequery($connection,$query);
       while($ROW=mysql_fetch_array($cursor))
         $TO_ID.=$ROW["USER_ID"].",";
     }
   }*/
   
   if($TO_ID=="")
   {
     echo _("无此OA用户！");
     exit;
   }else
   {
     //-------------------edit by ljc 2012-0601 -----------------------
      $CONTENT = stripslashes($CONTENT);
      $CONTENT = str_replace("\n", "<br>", $CONTENT);
      $CONTENT = str_replace("\r", "<br>", $CONTENT);
      $CONTENT_STRIP = strip_tags($CONTENT); //这个是存储的内容已经过滤html标签了
      $COMPRESS_CONTENT = bin2hex(gzcompress($CONTENT));
      //存储内容、压缩内容存储 edit  2012-05-29.....
      $CONTENT_SIZE=strlen($CONTENT);
      $CONTENT_SIZE1=strlen($CONTENT_STRIP);  
      $COMPRESS_CONTENT_SIZE=strlen($COMPRESS_CONTENT);
      if($CONTENT_SIZE<($CONTENT_SIZE1+$COMPRESS_CONTENT_SIZE)) //如果正文大于过滤掉html标签与压缩内容之合
      {
         $CONTENT_STRIP=mysql_escape_string($CONTENT);
         $COMPRESS_CONTENT="''";
      }
      else
      {
         $CONTENT_STRIP=mysql_escape_string($CONTENT_STRIP);
         $COMPRESS_CONTENT='0x'.$COMPRESS_CONTENT;
      }
      //...............
     $query="insert into EMAIL_BODY(FROM_ID,TO_ID2,COPY_TO_ID,SUBJECT,CONTENT,SEND_TIME,SEND_FLAG,SMS_REMIND,FROM_WEBMAIL,TO_WEBMAIL,COMPRESS_CONTENT) values ('$LOGIN_USER_ID','$TO_ID','$CS_ID','$SUBJECT','$CONTENT_STRIP','$SEND_TIME','1','1','$EMAIL','$WEBMAIL',$COMPRESS_CONTENT)";
     exequery($connection,$query);
     $BODY_ID=mysql_insert_id();
     $TO_ID.=$CS_ID.",";
     $TOK=strtok($TO_ID,",");
     while($TOK!="")
     {
       if($TOK=="")
       {
          $TOK=strtok(",");
          continue;
       }
   
       $query="insert into EMAIL(TO_ID,READ_FLAG,DELETE_FLAG,BODY_ID) values ('$TOK','0','0','$BODY_ID')";
       exequery($connection,$query);
       $ROW_ID=mysql_insert_id();
   
       $REMIND_URL="email/inbox/read_email/read_email.php?BOX_ID=0&BTN_CLOSE=1&FROM=1&EMAIL_ID=".$ROW_ID;
       $SMS_CONTENT=sprintf(_("请查收我的邮件！%s主题："), "\n").csubstr($SUBJECT1,0,100);
       send_sms("",$LOGIN_USER_ID,$TOK,2,$SMS_CONTENT,$REMIND_URL);
   
       $TOK=strtok(",");
     }//while
      echo _("邮件发送成功");
      exit;
   }
?>
