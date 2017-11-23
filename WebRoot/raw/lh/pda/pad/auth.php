<?
include_once("inc/session.php");
session_start();
ob_start();
include_once("inc/conn.php");
include_once("inc/utility.php");
if($P==""){
   $P = $_SESSION["P"];
}
$MY_ARRAY=explode(";",$P);
$PDA_UID=$MY_ARRAY[0];
$PDA_SID=trim($MY_ARRAY[1]);
$P_VER=trim($MY_ARRAY[2]);
$P_CLIENT = intval($P_VER) > 0 ? intval($P_VER) : 1;

if($PDA_UID == "" || $PDA_SID == "")
{
   relogin();
}

if($_SESSION['LOGIN_USER_ID'] == "" || $_SESSION['LOGIN_UID'] == "" || $PDA_UID != $LOGIN_UID)
{
   relogin();
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><?=$LOGIN_IE_TITLE?></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
</head>
<?
function relogin()
{
   global $P_VER;
   include_once("inc/td_config.php");
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<meta name="viewport" content="width=device-width" />
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
</head>
<body>
<br><br>
<center><?=_("用户未登录，请重新登录！")?><br><br><a href="/pda/pad/login.php?P_VER=<?=$P_VER?>"><?=_("重新登录")?></a></center>
</body>
</html>
<?
   exit;
}
?>
