<?

//加入Cookie记录用户终端类型 by JinXin @ 2012/9/12
if($SaveDevType && $DevType){
	setcookie("TD_MOBILE_DEVICE", $DevType, time() + 3600 * 24 * 30 ,'/');
}
//iOS和Android客户端传递的参数为UTF-8编码，转换为系统编码
if($P_VER == "5" || $P_VER == "6")
{
   while(list($KEY, $VALUE) = each($_GET))
      $$KEY = iconv("utf-8", ini_get("default_charset"), $VALUE);
   
   while(list($KEY, $VALUE) = each($_POST))
      $$KEY = iconv("utf-8", ini_get("default_charset"), $VALUE);
}

include_once("inc/conn.php");
include_once("inc/td_core.php");

//-------- 登录检查 -------------
$CLIENT = intval($P_VER) > 0 ? intval($P_VER) : 1;
$LOGIN_MSG=login_check($USERNAME, $PASSWORD, "", "", "", $CLIENT);
if($LOGIN_MSG!="1")
{
   if($P_VER == "6")
   {
?>
<script type="text/javascript">
if(typeof(window.Android) != 'undefined' && typeof(window.Android.loginerror) == 'function')
   window.Android.loginerror();
</script>
<?
   }
   else
   {
		$DevPath = $DevType === 'pad' ? 'pad/' : '';
      header("location: ".$DevPath."index.php?ERROR_NO=1&P_VER=$P_VER");
   }
   
   exit;
}
include_once("inc/cache/cache.php");
$SYS_INTERFACE = $td_cache->get("SYS_INTERFACE");
$_SESSION["LOGIN_IE_TITLE"] = $SYS_INTERFACE["IE_TITLE"];

//判断Android浏览器跳转到frames.php
include_once("pda/inc/funcs.php");
$P_VER_NEW = '';
if(Ag("Android") && $P_VER=="")
   $P_VER_NEW = "6";
else
   $P_VER_NEW = $P_VER;
      
$_SESSION["P_VER"] = $P_VER_NEW;
$_SESSION["P_VER_NEW"] = $P_VER_NEW;

$P=$LOGIN_UID.";".session_id().";".$P_VER_NEW;

$_SESSION["P"] = $P;

//IOS加入客户端版本号
if(isset($C_VER)){
   $_SESSION["C_VER"] = $C_VER;      
}

//2012/5/31 15:01:09 lp 增加网络环境识别
if(isset($C_TYPE)){
   $_SESSION["C_TYPE"] = $C_TYPE;      
}



if($P_VER == "5" || $P_VER == "6")
{
   $url = $DevType === "pad" ? "/pda/pad/main.php?P=$P&LOGIN_OK" : "/pda/main.php?P=$P&LOGIN_OK";

}else{
   if(DeviceAgent() == "IOS")
   {
		$url = $DevType === "pad" ? "/pda/pad/main.php?P=$P&LOGIN_OK" : "/pda/main.php?P=$P&LOGIN_OK";
   }else{
		$url = $DevType === "pad" ? "/pda/pad/main.php?P=$P&LOGIN_OK" : "/pda/frames.php?P=$P&LOGIN_OK";
   }
}

header("location: ".$url);
?>