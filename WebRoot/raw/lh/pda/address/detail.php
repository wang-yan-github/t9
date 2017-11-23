<?
   include_once("../header.php");
   include_once("inc/utility_all.php");
   ob_clean();
   
   $ADD_ID = intval($ADD_ID);
   $query = "SELECT * from ADDRESS where ADD_ID = '$ADD_ID'";
	$cursor= exequery($connection,$query);
	if($ROW=mysql_fetch_array($cursor))
	{
	   $GROUP_ID=$ROW["GROUP_ID"];
      $PSN_NAME=$ROW["PSN_NAME"];
      $SEX=$ROW["SEX"];
      $BIRTHDAY=$ROW["BIRTHDAY"];

      $NICK_NAME=$ROW["NICK_NAME"];
      $MINISTRATION=$ROW["MINISTRATION"];
      $MATE=$ROW["MATE"];
      $CHILD=$ROW["CHILD"];

      $DEPT_NAME=$ROW["DEPT_NAME"];
      $ADD_DEPT=$ROW["ADD_DEPT"];
      $POST_NO_DEPT=$ROW["POST_NO_DEPT"];
      $TEL_NO_DEPT=$ROW["TEL_NO_DEPT"];
      $FAX_NO_DEPT=$ROW["FAX_NO_DEPT"];

      $ADD_HOME=$ROW["ADD_HOME"];
      $POST_NO_HOME=$ROW["POST_NO_HOME"];
      $TEL_NO_HOME=$ROW["TEL_NO_HOME"];
      $MOBIL_NO=$ROW["MOBIL_NO"];
      $BP_NO=$ROW["BP_NO"];
      $EMAIL=$ROW["EMAIL"];
      $OICQ_NO=$ROW["OICQ_NO"];
      $ICQ_NO=$ROW["ICQ_NO"];
      $PSN_NO=$ROW["PSN_NO"];
      $NOTES=$ROW["NOTES"];
    
	   if(Ag("iPhone"))
         $TEL_NO_DEPT = '<a href="tel:'.$TEL_NO_DEPT.'">'.$TEL_NO_DEPT.'</a>';
      
      if(Ag("iPhone"))
         $TEL_NO_HOME = '<a href="tel:'.$TEL_NO_HOME.'">'.$TEL_NO_HOME.'</a>';
      
      if(Ag("iPhone"))
         $MOBIL_NO = '<a href="tel:'.$MOBIL_NO.'">'.$MOBIL_NO.'</a>';
	
	   $query1 = "select GROUP_NAME from ADDRESS_GROUP where GROUP_ID='$GROUP_ID'";
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

      $RETURN .='<div class="container">';
         $RETURN .='<div class="tform tformshow">';
         $RETURN .='<div class="read_detail fix_read_title"><em>'.$GROUP_NAME.' '.$PSN_NAME._("(").$SEX._(")").'</em></div>';
         $RETURN .='<div class="read_detail read_detail_header">'._("联系人信息").'</em>'.'</div>';
         $RETURN .='<div class="read_detail"><em>'._("部门：").'</em>'.$DEPT_LONG_NAME.'</div>';
         $RETURN .='<div class="read_detail"><em>'._("单位：").'</em>'.$DEPT_NAME.'</div>';
	      $RETURN .='<div class="read_detail"><em>'._("职务：").'</em>'.$MINISTRATION.'</div>';
	      $RETURN .='<div class="read_detail"><em>'._("生日：").'</em>'.$BIRTHDAY.'</div>';
	     	$RETURN .='<div class="read_detail"><em>'._("工作电话：").'</em>'.$TEL_NO_DEPT.'</div>';
	      $RETURN .='<div class="read_detail"><em>'._("工作传真：").'</em>'.$FAX_NO_DEPT.'</div>';
	      $RETURN .='<div class="read_detail"><em>'._("家庭电话：").'</em>'.$TEL_NO_HOME.'</div>';
	      $RETURN .='<div class="read_detail"><em>'._("手机：").'</em>'.$MOBIL_NO.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("昵称：").'</em>'.$NICK_NAME.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("QQ：").'</em>'.$OICQ_NO.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("MSN：").'</em>'.$ICQ_NO.'</div>';
	      $RETURN .='<div class="read_detail"><em>'._("Email：").'</em>'.$EMAIL.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("配偶：").'</em>'.$MATE.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("子女：").'</em>'.$CHILD.'</div>';
         $RETURN .='<div class="read_detail read_detail_header"><em>'._("工作单位信息").'</em>'.'</div>';
	      $RETURN .='<div class="read_detail endline"><em>'._("单位名称：").'</em>'.$DEPT_NAME.'</div>';
	      $RETURN .='<div class="read_detail endline"><em>'._("单位地址：").'</em>'.$ADD_DEPT.'</div>';
	      $RETURN .='<div class="read_detail endline"><em>'._("单位邮编：").'</em>'.$POST_NO_DEPT.'</div>';
         $RETURN .='<div class="read_detail read_detail_header">'._("家庭信息").'</em>'.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("家庭住址：").'</em>'.$ADD_HOME.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("家庭邮编：").'</em>'.$POST_NO_HOME.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("家庭电话：").'</em>'.$TEL_NO_HOME.'</div>';
	      $RETURN .='<div class="read_detail "><em>'._("备注：").'</em>'.$NOTES.'</div>';
	      $RETURN .='</div>';
      $RETURN .='</div>';
   }
   echo $RETURN;
?>      
