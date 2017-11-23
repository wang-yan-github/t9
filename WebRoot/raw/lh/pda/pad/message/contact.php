<?
	include_once("header.php");
	include_once("user.php");
	include_once("inc/utility_all.php");
	include_once("inc/department.php");
   ob_clean();
?>
            <ul id="contactList" class="main-comm-list preViewList comm-pic-list">

         <? 
				if(isset($_COOKIE['CookieArray'])) 
				{
						$CONTACT_USER_ARRAY = array_reverse($_COOKIE['CookieArray'], TRUE);
				    foreach ($CONTACT_USER_ARRAY as $k => $v) 
				    {
				      if($v==$LOGIN_UID or $USER_ARRAY[$v]=="") continue;
			?>
 			   <li class="<?=$fix_for_pad['list-li-style']?>" q_id="<?=$v?>" q_name="<?=$USER_ARRAY[$v]['NAME']?>">
 			      <img src="<?=showAvatar($USER_ARRAY[$v]['AVATAR'],$USER_ARRAY[$v]['SEX'])?>" class="ui-li-thumb"/>
 			      <h3><?=$USER_ARRAY[$v]['NAME']?></h3>
               <p class="content"><?=_("部门：")?><?=$SYS_DEPARTMENT[$USER_ARRAY[$v]['DEPT_ID']]['DEPT_NAME'];?></p>
               <span class="ui-icon-rarrow"></span>
 			   </li>
 			<?     }
 			      echo "</ul>";
 		      }else
            {
               echo "</ul>";    
               echo "<div class='no_msg'>"._("暂无常用联系人")."</div>";
            } 
         ?>
         