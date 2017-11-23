<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   include_once("inc/utility_file.php");
   ob_clean();
   
   //权限判断
   $ACCESS_PRIV=$MANAGE_PRIV=$DOWN_PRIV=$NEW_PRIV=$DEL_PRIV=$OWNER_PRIV=$MANAGE_SELF_PRIV=0;
   
   if($SORT_ID!=0)
   {
      $query = "SELECT SORT_NAME,SORT_PARENT,USER_ID,MANAGE_USER,DEL_USER,DOWN_USER,NEW_USER,OWNER from FILE_SORT where SORT_ID='$SORT_ID'";
      $cursor= exequery($connection,$query);
      if($ROW=mysql_fetch_array($cursor))
      {
         $USER_ID=$ROW["USER_ID"];
         $MANAGE_USER=$ROW["MANAGE_USER"];
         $DOWN_USER=$ROW["DOWN_USER"];
         $OWNER=$ROW["OWNER"];
   
         $OWNER_PRIV= check_priv($OWNER);
         $ACCESS_PRIV= $USER_ID==$LOGIN_USER_ID || check_priv($USER_ID) || $OWNER_PRIV;
         $MANAGE_PRIV= $USER_ID==$LOGIN_USER_ID || check_priv($MANAGE_USER);
         $DOWN_PRIV= $USER_ID==$LOGIN_USER_ID || check_priv($DOWN_USER);
    
         if($MANAGE_PRIV)
             $DOWN_PRIV=1;
      }
   }
   
   if(!$ACCESS_PRIV)
   {
       echo "NOACCESSPRIV";
       exit;
   }

   $query = "SELECT count(*) from FILE_CONTENT where SORT_ID='$SORT_ID'";
   $TOTAL_ITEMS = resultCount($query);

   $query = "SELECT count(*) from file_sort where SORT_TYPE = 4 and USER_ID='$LOGIN_USER_ID' and SORT_PARENT='$SORT_ID' order by SORT_ID ASC";
   $TOTAL_ITEMS += resultCount($query);
   if($TOTAL_ITEMS < 1)
   {
      echo "NOMOREDATA";
      exit;
   }
   $query = "SELECT SORT_PARENT from file_sort where SORT_ID='$SORT_ID' and USER_ID='$LOGIN_USER_ID'";
   $cursor = exequery($connection,$query);
   if($ROW = mysql_fetch_array($cursor))
   {
      $SORT_PARENT = $ROW['SORT_PARENT'];
   }
         
   ?>
         <script>
         now_sort = <?=$SORT_ID?>;
         last_sort = <?=$SORT_PARENT?>;
         </script>
   <?
       //============================ 显示根目录下文件和目录 =======================================
      $query = "SELECT SORT_NAME,SORT_ID from file_sort where SORT_TYPE = 4 and USER_ID='$LOGIN_USER_ID' and SORT_PARENT='$SORT_ID' order by SORT_ID ASC";
      $cursor= exequery($connection,$query);
      while($ROW=mysql_fetch_array($cursor))
      {
         $SORT_NAME = $ROW["SORT_NAME"];
         $SORT_ID1 = $ROW["SORT_ID"];
   ?>
   		<li class="folder <?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$SORT_ID1?>">
               <img src="../style/images/folder.png" class="ui-li-thumb"/>
               <h3><?=$SORT_NAME?></h3>
               <p class="w100 grapc">&nbsp;</p>
               <span class="ui-icon-rarrow"></span>
         </li>
   <?
      }//while

		 //============================ 显示文件 =======================================
      $query = "SELECT READERS,CONTENT_ID,SUBJECT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME from FILE_CONTENT where SORT_ID='$SORT_ID' order by CONTENT_ID desc";// limit 0,$PAGE_SIZE
      $cursor= exequery($connection,$query);
      while($ROW=mysql_fetch_array($cursor))
      {
         $READERS = $ROW["READERS"];
         $CONTENT_ID = $ROW["CONTENT_ID"];
         $SUBJECT = $ROW["SUBJECT"];
         $SEND_TIME = $ROW["SEND_TIME"];
         $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
         $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
         $SUBJECT=htmlspecialchars($SUBJECT);
?>
		<li class="files <?=$fix_for_pad['list-li-style']?><?=$Class?>" q_id="<?=$CONTENT_ID?>">
            <img src="<?=file_icon($ATTACHMENT_NAME)?>" class="ui-li-thumb"/>
            <h3><?=$SUBJECT?></h3>
            <p class="w100 grapc"><?=timeintval(strtotime($SEND_TIME))?></p>
            <?
               if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
               {
            ?>
               <span class="iconbtn attach_icon"></span>
            <?
               }
            ?>
            <span class="ui-icon-rarrow"></span>
      </li>
<?
      }//while
?>
         
