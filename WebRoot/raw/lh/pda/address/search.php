<?
include_once("../header.php");
include_once("inc/utility_all.php");
ob_clean();
$PSN_NAME = td_iconv($PSN_NAME, "utf-8", $MYOA_CHARSET);
$DEPT_NAME = td_iconv($DEPT_NAME, "utf-8", $MYOA_CHARSET);


$GROUP_ID_STR = '';
$query = "select GROUP_ID from ADDRESS_GROUP where USER_ID='$LOGIN_USER_ID' or  (USER_ID='' and (find_in_set('$LOGIN_USER_ID',PRIV_USER) or PRIV_DEPT='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',PRIV_DEPT) or find_in_set('$LOGIN_USER_PRIV',PRIV_ROLE)))";
$cursor= exequery($connection,$query);
$GROUP_COUNT=1;
while($ROW=mysql_fetch_array($cursor))
{
   $GROUP_ID=$ROW["GROUP_ID"];
   $GROUP_ID_STR.=$GROUP_ID.",";
}
$GROUP_ID_STR=$GROUP_ID_STR."0";

if(!isset($TOTAL_ITEMS))
{
   $query = "SELECT count(*) from ADDRESS where GROUP_ID in ($GROUP_ID_STR)";
    if($PSN_NAME!="")
       $query .= " and PSN_NAME like '%$PSN_NAME%'";
    if($DEPT_NAME!="")
       $query .= " and DEPT_NAME like '%$DEPT_NAME%'";
   
   $cursor= exequery($connection,$query);
   if($ROW=mysql_fetch_array($cursor))
      $TOTAL_ITEMS=$ROW[0];
}
?>
		<?
      
		if($TOTAL_ITEMS > 0)
      {
      		$query = "SELECT * from ADDRESS where GROUP_ID in ($GROUP_ID_STR)";
      		if($PSN_NAME!="")
      		   $query .= " and PSN_NAME like '%$PSN_NAME%'";
      		if($DEPT_NAME!="")
      		   $query .= " and DEPT_NAME like '%$DEPT_NAME%'";
      		
      		$query .= " limit 0,20";	
      		$cursor= exequery($connection,$query);
      		while($ROW=mysql_fetch_array($cursor))
      		{
   		   
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
      		      $GROUP_NAME=_("Ĭ��");
      		
      		   switch($SEX)
      		   {
      		    case "0":
      		        $SEX=_("��");
      		        break;
      		    case "1":
      		        $SEX=_("Ů");
      		        break;
      		   }
   		?>
   		   <li class="<?=$fix_for_pad['list-li-style']?>">
               <h3><?=$GROUP_NAME?> <?=$PSN_NAME?><?=_("(")?><?=$SEX?><?=_(")")?></h3>
               <p class="w100 grapc"><?=_("���ţ�")?><?=$DEPT_LONG_NAME?></p>
               <p class="w100 grapc"><?=_("��λ")?>:<?=$DEPT_NAME?></p>
   		      <p class="w100 grapc"><?=_("ְ��")?>:<?=$MINISTRATION?></p>
   		      <p class="w100 grapc"><?=_("����")?>:<?=$BIRTHDAY?></p>
   		     	<p class="w100 grapc"><?=_("�����绰")?>:<?=$TEL_NO_DEPT?></p>
   		      <p class="w100 grapc"><?=_("��ͥ�绰")?>:<?=$TEL_NO_HOME?></p>
   		      <p class="w100 grapc"><?=_("�ֻ�")?>:<?=$MOBIL_NO?></p>
   		      <p class="w100 grapc">Email:<?=$EMAIL?></p>
            </li>
   		<?
   		}//while
		}else{
		   echo '<div class="no_msg">'._("�޷��������ļ�¼").'</div>';
      }
		?>

