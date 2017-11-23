<?
include_once("inc/td_config.php");
include_once("inc/cache/cache.php");

$SYS_INTERFACE = $td_cache->get("SYS_INTERFACE");
if(!is_array($SYS_INTERFACE))
{
   include_once("inc/utility_all.php");
   cache_interface();
}

$IE_TITLE=$SYS_INTERFACE["IE_TITLE"];

$USER_NAME_COOKIE=$_COOKIE["USER_NAME_COOKIE"];
if($USER_NAME_COOKIE=="")
   $FOCUS="USERNAME";
else
   $FOCUS="PASSWORD";
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><?=$IE_TITLE?></title>
<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1" />
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<link rel="stylesheet" type="text/css" href="/pda/style/index.css" />
</head>
<body>
<div id="logo">
   <div id="form">
      <form name="form1" method="post" action="login.php?P_VER=<?=$P_VER?>">
      <div id="form_input">
         <div class="user"><input type="text" class="text" name="USERNAME" maxlength="20" value="<?=$USER_NAME_COOKIE?>" /></div>
         <div class="pwd"><input type="password" class="text" name="PASSWORD" value="" /></div>
      </div>
      <div id="form_submit">
         <input type="submit" class="submit" title="<?=_("登录")?>" value=" " />
      </div>
		<input type="hidden" name="SaveDevType" value="<?=$save?>" />
		<input type="hidden" name="DevType" value="mobi" />
      </form>
   </div>
   <div id="msg">
<?
   if($ERROR_NO==1)
      echo "<div>".sprintf(_("用户名或密码错误或禁止该用户登录%s请重新登录"),  "<br />")."</div>";
?>
   </div>
</div>
</body>
</html>
