<?
require_once 'auth.php';
ob_clean();
$ONLINE_DESC = sprintf(_("共%s人在线"), '<input type="text" id="user_count" size="3">');
?>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<meta name="format-detection" content="telephone=no" /> 
	<title><?=$LOGIN_IE_TITLE?></title>
	<link rel="stylesheet"  href="/pda/style/main.css" />
</head>
<body>
<div id="main_bottom">
   <div class="online"><?=$ONLINE_DESC?></div>
   <a class="ButtonC relogin" target="_top" href="index.php"><span><?=_("重新登录")?></span></a>
   <a class="ButtonC message" href="javascript:;" onclick="parent.ResizeFrame(2);"><span><?=_("微讯")?></span></a>
   <a class="ButtonC message oa" href="javascript:;" onclick="parent.ResizeFrame(1);"><span>OA</span></a>
</div>
</body>
</html>
