<?
   include_once("../header.php");
   include_once("inc/utility_file.php");
   ob_clean();

	$query = "SELECT * from FILE_CONTENT where CONTENT_ID='$CONTENT_ID'";
	$cursor= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor))
	{
	   $SUBJECT = $ROW["SUBJECT"];
	   $SEND_TIME = $ROW["SEND_TIME"];
	   $CONTENT=$ROW["CONTENT"];
	   $SORT_ID=$ROW["SORT_ID"];
	   $USER_ID=$ROW["USER_ID"];

	   $ATTACHMENT_ID = $ROW["ATTACHMENT_ID"];
	   $ATTACHMENT_NAME = $ROW["ATTACHMENT_NAME"];
	
	   $SUBJECT=htmlspecialchars($SUBJECT);
      

	}
	else
	   exit;
         
   function share_priv($SORT_ID)
   {
      if($SORT_ID==0)
         return "";

      global $LOGIN_USER_ID,$connection;
      $query = "SELECT SORT_PARENT,SHARE_USER,MANAGE_USER from FILE_SORT where SORT_ID='$SORT_ID'";
      $cursor= exequery($connection,$query);
      if($ROW=mysql_fetch_array($cursor))
      {
         $SORT_PARENT=$ROW["SORT_PARENT"];
         $SHARE_USER=$ROW["SHARE_USER"];
         $MANAGE_USER=$ROW["MANAGE_USER"];
      }

      if(find_id($SHARE_USER,$LOGIN_USER_ID))
         return $SHARE_USER."|".$MANAGE_USER;
      else
         return share_priv($SORT_PARENT);
   }
   
   if($USER_ID != $LOGIN_USER_ID )
   {
      $ACCESS_PRIV=$DOWN_PRIV=0;
      $query = "SELECT USER_ID,DOWN_USER,MANAGE_USER,OWNER from FILE_SORT where SORT_ID='$SORT_ID'";
      $cursor= exequery($connection,$query);
      if(($ROW=mysql_fetch_array($cursor)))
      {
         $USER_ID=$ROW["USER_ID"];
         $DOWN_USER=$ROW["DOWN_USER"];
         $MANAGE_USER=$ROW["MANAGE_USER"];
         $OWNER=$ROW["OWNER"];

         $SHARE_PRIV=share_priv($SORT_ID);
         $SHARE_USER=substr($SHARE_PRIV, 0, strpos($SHARE_PRIV, "|"));

         $OWNER_PRIV= check_priv($OWNER);
         $ACCESS_PRIV=$USER_ID==$LOGIN_USER_ID || check_priv($USER_ID) || find_id($SHARE_USER,$LOGIN_USER_ID) || $OWNER_PRIV;
         $MANAGE_PRIV=$USER_ID==$LOGIN_USER_ID || check_priv($MANAGE_USER);
         $DOWN_PRIV=$MANAGE_PRIV || $USER_ID==$LOGIN_USER_ID || check_priv($DOWN_USER);
         
         if(!$DOWN_PRIV) 
            exit;
      }
   }
?>
<div class="container">
   <h3 class="read_title fix_read_title"><?=$SUBJECT?></h3>
   <p class="read_detail fix_read_detail"><?=date("Y"._("Äê")."m"._("ÔÂ")."d"._("ÈÕ")." H:i",strtotime($SEND_TIME))?></p>
   <div class="read_content"><?=$CONTENT?></div>
   <?
   if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
   {
   ?>
      <div class="read_attach"><?=attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'',1,1,1)?></div>
   <?
   }
   ?>
</div>
