<?
include_once("auth.php");

if($P_VER == "" || $P_VER_NEW == "6")
   $FRAME_ROWS = "*,0,40";
else
   $FRAME_ROWS = "*";
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
   <script type="text/javascript" src="/inc/js/utility.js"></script>
   <script type="text/javascript" src="/pda/js/frames.js"></script>
   <script type="text/javascript">
   var p = "<?=$P?>";
   var p_ver = "<?=$P_VER?>";
   var p_client = "<?=$P_CLIENT?>";
   var online_ref_sec = "<?=$ONLINE_REF_SEC?>";
   </script>
</head>
<frameset id="frame1" rows="<?=$FRAME_ROWS?>" cols="*" frameborder="no" border="0" framespacing="0">
   <frame name="main" scrolling="auto" noresize src="main.php?P=<?=$P?>" frameborder="0" />
<?
if($P_VER == "" || $P_VER_NEW == "6")
{
?>
   <frame name="message" scrolling="no" noresize src="" frameborder="0" />
   <frame name="foot" scrolling="no" noresize src="foot.php?P=<?=$P?>" frameborder="0" />
<?
}
?>
</frameset>
<noframes><?=_("您的浏览器不支持框架")?></noframes>
</body>
</html>
