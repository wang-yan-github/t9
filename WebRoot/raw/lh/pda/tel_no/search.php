<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   ob_clean();
   mysql_select_db("BUS", $connection);
   $AREA = td_iconv($AREA, "utf-8", $MYOA_CHARSET);
   $TEL_NO = td_iconv($TEL_NO, "utf-8", $MYOA_CHARSET);
   $POST_NO = td_iconv($POST_NO, "utf-8", $MYOA_CHARSET);

   $query = "SELECT count(*) from POST_TEL where 1=1 ";
   if($AREA!="")
      $query.= " and (CITY like '%$AREA%' or COUNTY like '%$AREA%' or TOWN like '%$AREA%')";
   if($TEL_NO!="")
      $query.= " and TEL_NO like '%$TEL_NO%'";
   if($POST_NO!="")
      $query.= " and POST_NO like '%$POST_NO%'";
   $cursor= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor))
      $TOTAL_ITEMS=$ROW[0];
?>

<div class="container">

		<div data-role="content" id="user_info-list" class="comm-list">
   <?
   if($TOTAL_ITEMS > 0)
   {
		$query = "SELECT * from POST_TEL where 1=1 ";
		if($AREA!="")
		   $query.= " and (CITY like '%$AREA%' or COUNTY like '%$AREA%' or TOWN like '%$AREA%')";
		if($TEL_NO!="")
		   $query.= " and TEL_NO like '%$TEL_NO%'";
		if($POST_NO!="")
		   $query.= " and POST_NO like '%$POST_NO%'";
      $query .= " limit 0,20";
		$cursor= exequery($connection,$query);
		while($ROW=mysql_fetch_array($cursor))
		{
		   $PROVINCE=$ROW["PROVINCE"];
		   $CITY=$ROW["CITY"];
		   $COUNTY =$ROW["COUNTY"];
		   $TOWN=$ROW["TOWN"];
		   $TEL_NO=$ROW["TEL_NO"];
		   $POST_NO=$ROW["POST_NO"];
		?>
		  <li class="<?=$fix_for_pad['list-li-style']?>" data-icon="false">
            <a href="javascript:void(0)" rel="external" data-transition="slide" ajax-data="false">
               <h3><?=$PROVINCE?><? if($AREA!="") echo "-".$AREA; ?></h3>
               <p class="w100 grapc"><?=_("ʡ(ֱϽ��/������)")?>:<?=$PROVINCE?></p>
               <p class="w100 grapc"><?=_("����")?>:<?=$CITY?></p>
               <p class="w100 grapc"><?=_("��/��")?>:<?=$COUNTY?></p>
               <p class="w100 grapc"><?=_("�ֵ�")?>:<?=$TOWN?></p>
               <p class="w100 grapc"><?=_("����")?>:<?=$TEL_NO?></p>
               <p class="w100 grapc"><?=_("�ʱ�")?>:<?=$POST_NO?></p>
            </a>
        </li>
		<?
		}//while
      
	}else{
      echo '<div class="no_msg">'._("�޷��������ļ�¼").'</div>';
   }
   ?>
   </div>

</div>

