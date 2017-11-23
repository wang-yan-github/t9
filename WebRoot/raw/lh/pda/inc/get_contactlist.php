<?
   include_once('../auth.php');
   include_once("inc/utility_all.php");
   include_once("inc/utility_org.php");
   include_once('funcs.php');
   ob_end_clean();
   
   $P = strip_tags($P);
   $KWORD = td_iconv($KWORD,"UTF-8",$MYOA_CHARSET);
   
   //白名单
   $EXCLUDE_UID_STR=my_exclude_uid();
   
   //2012/6/18 3:25:03 lp 仅供查找对应的索引值
   if($ACTION == "getNameIndex")
   {
      require_once 'inc/mb.php';
      $PREFIX = getChnprefix($KWORD);
      $PREFIX = strtolower($PREFIX);
      echo $PREFIX;
      exit;          
   }

   //2012/1/31 16:15:01  刘a，ab，这种走中文索引搜索，全中文走模糊搜索
   if(preg_match('/^\\w+$/',$KWORD))
   {
      $sql_str = " or USER.USER_NAME_INDEX  REGEXP '^[a-z]*";
      require_once 'inc/mb.php';
      $PREFIX = getChnprefix($KWORD);
      $PREFIX = strtolower($PREFIX);
      $PREFIX_ARR = explode("*",$PREFIX);
      foreach($PREFIX_ARR as $v){
         if($v == "") continue;
         $sql_str .= $v."\\\\*[a-z]*";   
      }
      if(count($PREFIX_ARR) > 0){
         $sql_str = substr($sql_str,0,-9);   
      }
      $sql_str .= "'";   
   }else{
      $sql_str = '';      
   }
   
   $HTML_STR = '';
   $query = "SELECT UID,USER_ID,AVATAR,USER_NAME,USER.DEPT_ID,PRIV_NAME from USER,USER_PRIV,DEPARTMENT where USER.DEPT_ID!=0 and (USER.USER_ID like '%$KWORD%' ".$sql_str." or USER.BYNAME like '%$KWORD%' or USER.USER_NAME like '%$KWORD%') and USER.DEPT_ID=DEPARTMENT.DEPT_ID and USER.USER_PRIV=USER_PRIV.USER_PRIV order by PRIV_NO,USER_NO,USER_NAME";
   $cursor = exequery($connection,$query);
   while($ROW=mysql_fetch_array($cursor))
   {
      $SEX = $ROW["SEX"];
      $UID = $ROW["UID"];
      $AVATAR = $ROW["AVATAR"];
      $USER_ID = $ROW["USER_ID"];
      $USER_NAME = $ROW["USER_NAME"];
      $PRIV_NAME = $ROW["PRIV_NAME"];
      $DEPT_NAME=str_replace("/"," - ",dept_long_name($ROW["DEPT_ID"]));
      $HTML_STR .= '<li class="'.$fix_for_pad['list-li-style'].'" q_id="'.$UID.'" q_name="'.$USER_NAME.'" q_user_id="'.$USER_ID.'">
                     <h3>'.$USER_NAME.'（'.$PRIV_NAME.'）</h3>
                     <p class="grapc">'._("部门：").($DEPT_NAME).'&nbsp;</p>
                     <span class="ui-icon-rarrow"></span>
                  </li>';
   }
   echo $HTML_STR;
?>