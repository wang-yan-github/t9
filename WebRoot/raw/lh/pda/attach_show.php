<?

$MY_ARRAY=explode(";",$P);
$PDA_UID=$MY_ARRAY[0];
$PDA_SID=trim($MY_ARRAY[1]);
$P_VER=trim($MY_ARRAY[2]);
$P_CLIENT = intval($P_VER) > 0 ? intval($P_VER) : 1;
if($PDA_UID == "" || $PDA_SID == "")
{
   relogin();
}
ob_start();
include_once("inc/session.php");
include_once("inc/conn.php");
include_once("inc/utility.php");

session_id($PDA_SID);
session_start();

if($_SESSION['LOGIN_USER_ID'] == "" || $_SESSION['LOGIN_UID'] == "" || $PDA_UID != $LOGIN_UID)
{
   relogin();
}

include_once("inc/utility_file.php");

function Ag($device){
   return stripos($_SERVER['HTTP_USER_AGENT'],$device) >= 0;
}
$AID = intval($AID);

$ATTACHMENT_NAME = pack('H*', $ATTACHMENT_NAME);
$ATTACHMENT_ID=attach_id_decode($ATTACHMENT_ID,$ATTACHMENT_NAME);

if(stristr($MODULE,"/") || stristr($MODULE,"\\") || stristr($YM,"/") || stristr($YM,"\\")
   || stristr($ATTACHMENT_ID,"/") || stristr($ATTACHMENT_ID,"\\") || stristr($ATTACHMENT_NAME,"/") || stristr($ATTACHMENT_NAME,"\\"))
{
    Message(_("错误"),_("参数含有非法字符。"));
    exit;
}

if($AID > 0)
   $ATTACHMENT_ID_LONG = $AID."@".$YM."_".$ATTACHMENT_ID;
else if($YM != "")
   $ATTACHMENT_ID_LONG = $YM."_".$ATTACHMENT_ID;
else
   $ATTACHMENT_ID_LONG = $ATTACHMENT_ID;

$FILE_PATH = attach_real_path($ATTACHMENT_ID_LONG, $ATTACHMENT_NAME, $MODULE);

if($FILE_PATH === FALSE)
{
    Message(_("错误"),sprintf(_("文件[%s]不存在"), htmlspecialchars($ATTACHMENT_NAME)));
    exit;
}

if(is_office($ATTACHMENT_NAME))
	oc_log($ATTACHMENT_ID_LONG, $ATTACHMENT_NAME, 3);

$FILE_EXT=strtolower(substr($ATTACHMENT_NAME,strrpos($ATTACHMENT_NAME,".")+1));

/*if($DIRECT_VIEW || $FILE_EXT==".mht")
{
  switch($FILE_EXT)
  {
    case ".jpg":
    case ".bmp":
    case ".gif":
    case ".png":
    case ".wmv":
    case ".html":
    case ".htm":
    case ".wav":
    case ".mid":
    case ".mht":
                 $COTENT_TYPE=0;
                 $COTENT_TYPE_DESC="application/octet-stream";
                 break;
    case ".pdf":
                 $COTENT_TYPE=0;
                 $COTENT_TYPE_DESC="application/pdf";
                 break;
    case ".swf":
                 $COTENT_TYPE=0;
                 $COTENT_TYPE_DESC="application/x-shockwave-flash";
                 break;
    default:
                 $COTENT_TYPE=1;
                 $COTENT_TYPE_DESC="application/octet-stream";
                 break;
  }
}
else
{
  $COTENT_TYPE=1;
  $COTENT_TYPE_DESC="application/octet-stream";
}
*/
$COTENT_TYPE=0;
$COTENT_TYPE_DESC = mime_type($ATTACHMENT_NAME);
if(Ag("Android") or $P_VER == 6)
{
   $COTENT_TYPE=1;
   $COTENT_TYPE_DESC="application/octet-stream";
}
//lp 2011/12/29 1:15:44 如果文件名包含中文的话，就生成随机文件名
if(!preg_match("/^[A-Za-z0-9_.]+$/",$ATTACHMENT_NAME))
{
   $RandomName = genRandomString(7);
   if($RandomName){
      if($FILE_EXT)
         $ATTACHMENT_NAME = date("Ymd",time())."_".$RandomName.".".$FILE_EXT;
      else
         $ATTACHMENT_NAME = date("Ymd",time())."_".$RandomName;
   }
}

td_download($FILE_PATH, $ATTACHMENT_NAME, $COTENT_TYPE, $COTENT_TYPE_DESC);
?>