<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   ob_clean();
?>
<div class="container">
   <?
		$query = "SELECT * from USER where UID = '$UID' ";
		$cursor= exequery($connection,$query);
		if($ROW=mysql_fetch_array($cursor))
		{
		   $USER_NAME=$ROW["USER_NAME"];
		   $USER_PRIV=$ROW["USER_PRIV"];
		   $DEPT_ID=$ROW["DEPT_ID"];
		   $SEX=$ROW["SEX"];
		   $TEL_NO_DEPT=$ROW["TEL_NO_DEPT"];
		   $MOBIL_NO=$ROW["MOBIL_NO"];
		   $EMAIL=$ROW["EMAIL"];
		   $MOBIL_NO_HIDDEN=$ROW["MOBIL_NO_HIDDEN"];
		   $OICQ_NO=$ROW["OICQ_NO"];
		   $REMARK=$ROW["REMARK"];
		   $AVATAR = $ROW["AVATAR"];
		   
		   $query1 = "SELECT * from USER_PRIV where USER_PRIV='$USER_PRIV'";
		   $cursor1= exequery($connection,$query1);
		   if($ROW=mysql_fetch_array($cursor1))
		        $USER_PRIV=$ROW["PRIV_NAME"];
		
		   $DEPT_LONG_NAME=dept_long_name($DEPT_ID);
		
		   if($SEX==0)
		      $SEX=_("男");
		   else
		      $SEX=_("女");
		?>
      	<div class="tform tformshow">
      			<div class="read_detail">
      			   <img src="<?=showAvatar($AVATAR,$SEX)?>" style="width:40px;"/>
               </div>
               <div class="read_detail">
                  <span class="read_detail_title"><?=_("姓名：")?></span><?=$USER_NAME?><?=_("(")?><?=$SEX?><?=_(")")?>
               </div>
               <div class="read_detail">
                  <span class="read_detail_title"><?=_("部门：")?></span><?=$DEPT_LONG_NAME?>
               </div>
               <div class="read_detail">
      		      <span class="read_detail_title"><?=_("角色：")?></span><?=$USER_PRIV?>
      		   </div>
      		   <div class="read_detail">
      		      <span class="read_detail_title"><?=_("QQ：")?></span><?=$OICQ_NO?>
      		   </div>
      		   <?
            		if($TEL_NO_DEPT != "")
            		{
         		?>
         		   <div class="read_detail">
         		      <span class="read_detail_title"><?=_("工作电话：")?></span>
         		      <? if(Ag("iPhone")){ ?>
         		         <a href="tel:<?=$TEL_NO_DEPT?>"><?=$TEL_NO_DEPT?></a>
         		      <? }else{ ?>
         		         <?=$TEL_NO_DEPT?>
         		      <? } ?>
         		   </div>
         		<?
         		   }
         		?>
         		
         		<?
            		if($MOBIL_NO != "")
            		{
         		?>
                  <div class="read_detail">
                     <span class="read_detail_title"><?=_("手机：")?></span>
                     <? 
                        if($MOBIL_NO_HIDDEN!="1")
                        {
                           if(Ag("iPhone"))
                              echo '<a href="tel:'.$MOBIL_NO.'">'.$MOBIL_NO.'</a>';
                           else
                              echo $MOBIL_NO;
                        }else{ 
                           echo _("不公开"); 
                        }
                     ?>
                  </div>
         		<?
         		   }
         		?>
         		
         		<?
            		if($EMAIL!="")
            		{
         		?>
            		<div class="read_detail">
                     <span class="read_detail_title"><?=_("Email：")?></span><?=$EMAIL?>
                  </div>
         		<?
         		   }
         		?>
               <div class="read_detail endline">
                  <span class="read_detail_title"><?=_("个性签名：")?></span><?=$REMARK?>
               </div>
      	</div>
   <?
		}else{	
         echo '<div class="no_msg">'._("无符合条件的人员").'</div>';
      }
   ?>
</div>

