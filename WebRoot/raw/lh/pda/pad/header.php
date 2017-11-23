<?
	require_once 'auth.php';
	require_once 'inc/funcs.php';
	ob_clean();
?>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=gbk">
	<meta name="viewport" content="width=device-width, minimum-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	<meta name="format-detection" content="telephone=no" /> 
	<title><?=$LOGIN_IE_TITLE?></title>
   <script type="text/javascript" src="./js/jquery-1.8.0.min.js.gz"></script>
	<script type="text/javascript" src="./js/iscroll.min.js.gz"></script>
	<link rel="stylesheet"  href="./style/pad.css?v=2012.7.2" />
	<script type="text/javascript">
<? 
   echo "var td_lang = {};
         td_lang.pda = {
            msg_1:'"._("暂无更多信息")."',
            msg_2:'"._("加载中...")."',
            msg_3:'"._("页面加载错误")."',
            msg_4:'"._("下拉刷新...")."',
            msg_5:'"._("释放立即刷新...")."',
            msg_6:'"._("上拉加载更多...")."',
            msg_7:'"._("释放加载更多...")."',
            msg_8:'"._("已全部加载完毕")."',
            msg_9:'"._("读取附件中...")."',
         };";
?>
   var C_VER = "<?=$_SESSION['C_VER']?>";
   var P_VER = "<?=$P_VER?>";
	var P = p = "<?=$P?>";
   var isIDevice = (/iphone|ipad/gi).test(navigator.appVersion);
</script>
</head>