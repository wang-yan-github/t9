<?
   include_once("../inc_header.php");
   include_once("inc/utility_all.php");
   
   //$CUR_DATE=date("Y-m-d",strtotime(date("Y-m-d")." - 2 weeks"));
   $CUR_DATE=date("Y-m-d",time());
   $CUR_TIME=date("Y-m-d H:i:s",time());
   $CUR_TIME_U=time();

   //2012/5/18 0:39:54 lp 今日日程
   $query = "SELECT count(*) from CALENDAR where USER_ID='$LOGIN_USER_ID' and to_days(from_unixtime(CAL_TIME))=to_days('$CUR_DATE')";
   $TOTAL_ITEMS_CALENDAR = resultCount($query);
   
   //2012/5/18 0:41:16 lp 查询周期性事务
   $query = "SELECT count(*) from AFFAIR where USER_ID='$LOGIN_USER_ID' and BEGIN_TIME<='$CUR_TIME_U'";
   $TOTAL_ITEMS_AFFAIR = resultCount($query);
   
   $TOTAL_ITEMS = $TOTAL_ITEMS_CALENDAR + $TOTAL_ITEMS_AFFAIR;

?>
<body>
<script type="text/javascript">
var stype = "calendar";
var nonewdata = "<?=_('没有新的日程安排')?>";
var newdata = "<?=_('%s个新的日程安排')?>";
var refreshlist = "<?=_('今日日程已更新')?>";

var calArr = {
   1: "<?=cal_level_desc_fix(1)?>",
   2: "<?=cal_level_desc_fix(2)?>",
   3: "<?=cal_level_desc_fix(3)?>",
   4: "<?=cal_level_desc_fix(4)?>"
};
/* --- 自定义参数 ---*/
var nomoredata_1 = true;
var noshowPullUp_1 = true;
function pageInit(page_id)
{
   if(page_id == 1)
   {
      tiScroll_1 = new $.tiScroll({'page_type':'side',"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1,"onPullDown":pullDown_calendar});
      tiScroll_1.init();
   }
   
   if(page_id == 2)
   {
      tiScroll_2 = new $.tiScroll({'page_type':'side',"page_id": 2, "listType": "readonly"});
      tiScroll_2.init();
   }
   if(page_id == 3)
   {
      tiScroll_3 = new $.tiScroll({'page_type':'side',"page_id": 3, "listType": "readonly"});
      tiScroll_3.init();
   }
   if(page_id == 4)
   {
      tiScroll_4 = new $.tiScroll({'page_type':'side',"page_id": 4, "listType": "readonly"});
      tiScroll_4.init();
   }
}
</script>
<div id="sideContentArea">
<?
   $tHeadData = array(
      "1" => array(
         "l" => array("class" => "","event" => "gohome();", "title" => "首页"),
         "c" => array("title" => "今日日程"),
         "r" => array("class" => "","event" => "newCal();", "title" => "新建")
      ),
      "2" => array(
         "l" => array("class" => "","event" => "reback(2,1);", "title" => "返回"),
         "c" => array("title" => "修改日程"),
         "r" => array("class" => "","event" => "saveCal(\"edit\");", "title" => "保存")
      ),
      "3" => array(
         "l" => array("class" => "","event" => "reback(3,1);", "title" => "返回"),
         "c" => array("title" => "新建日程"),
         "r" => array("class" => "","event" => "saveCal(\"new\");", "title" => "保存")
      ),
      "4" => array(
         "l" => array("class" => "","event" => "reback(4,1);", "title" => "返回"),
         "c" => array("title" => "查看日程"),
         "r" => array("class" => "","event" => "editCal();", "title" => "编辑")
      ),
   );
?>
<?=buildSiderHead($tHeadData);?>
<?=buildMessage();?>
<?=buildSideProLoading();?>
   

	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">    
   <?
         echo buildPullDown();
         echo '<ul class="comm-list sideBarSubList " id="cal_list">';
            
   		//============================ 显示今日日程 =======================================
   	if($TOTAL_ITEMS>0)
   	{
   		$query = "SELECT * from CALENDAR where USER_ID='$LOGIN_USER_ID' and to_days(from_unixtime(CAL_TIME))=to_days('$CUR_DATE') order by CAL_ID desc";
   		$cursor= exequery($connection,$query);
   		while($ROW=mysql_fetch_array($cursor))
   		{
            $CAL_ID=$ROW["CAL_ID"];
            $CAL_TIME=$ROW["CAL_TIME"];
            $CAL_TIME=date("Y-m-d H:i:s",$CAL_TIME);
            $END_TIME=$ROW["END_TIME"];
            $END_TIME=date("Y-m-d H:i:s",$END_TIME);
            $CAL_TIME=strtok($CAL_TIME," ");
            $CAL_TIME=strtok(" ");
            $CAL_TIME=substr($CAL_TIME,0,5);
            
            $END_TIME=strtok($END_TIME," ");
            $END_TIME=strtok(" ");
            $END_TIME=substr($END_TIME,0,5);
            $CAL_LEVEL = $ROW["CAL_LEVEL"];
            $CONTENT=$ROW["CONTENT"];
            $CONTENT=str_replace("<","&lt",$CONTENT);
            $CONTENT=str_replace(">","&gt",$CONTENT);
            $CONTENT=stripslashes($CONTENT);
            $CAL_LEVEL_DESC = cal_level_desc_fix($CAL_LEVEL);
   ?>
      		<li class="<?=$fix_for_pad['list-li-style']?>" q_id="<?=$CAL_ID?>">
                  <h3><?=$CAL_TIME?> - <?=$END_TIME?></h3>
                  <p class="content"><?=$CAL_LEVEL_DESC." ".$CONTENT?></p>
                  <span class="ui-icon-rarrow"></span>
            </li>
   <?
         }//while
	
   		 //============================ 显示日常事务 =======================================
         $query = "SELECT * from AFFAIR where USER_ID='$LOGIN_USER_ID' and BEGIN_TIME<='$CUR_TIME_U' order by REMIND_TIME";
         $cursor= exequery($connection,$query);
         while($ROW=mysql_fetch_array($cursor))
         {
   		    $AFF_ID=$ROW["AFF_ID"];
   		    $USER_ID=$ROW["USER_ID"];
   		    $TYPE=$ROW["TYPE"];
   		    $REMIND_DATE=$ROW["REMIND_DATE"];
   		    $REMIND_TIME=$ROW["REMIND_TIME"];
   		    $CONTENT=$ROW["CONTENT"];
   		
   		    $FLAG=0;
   		    if($TYPE=="2")
   		       $FLAG=1;
   		    elseif($TYPE=="3" && date("w",time())==$REMIND_DATE)
   		       $FLAG=1;
   		    elseif($TYPE=="4" && date("j",time())==$REMIND_DATE)
   		       $FLAG=1;
   		    elseif($TYPE=="5")
   		    {
   		       $REMIND_ARR=explode("-",$REMIND_DATE);
   		       $REMIND_DATE_MON=$REMIND_ARR[0];
   		       $REMIND_DATE_DAY=$REMIND_ARR[1];
   		       if(date("n",time())==$REMIND_DATE_MON && date("j",time())==$REMIND_DATE_DAY)
   		          $FLAG=1;
   		    }
   		    if($FLAG!=1) continue;
		    
   ?>
      		<li class="<?=$fix_for_pad['list-li-style']?>" >
                  <h3><?=substr($REMIND_TIME,0,5)?></h3>
                  <p class="content"><?=_("周期性事务：").$CONTENT?></p>
            </li>
   <?
		    
		   }
         echo "</ul>";
      }else{
         echo "</ul>";
         echo '<div class="no_msg">'._("暂无日程安排").'</div>';
      }
         echo buildPullUp(); 
   ?>
         </div>      
      </div>
   </div>
		

   <!-- edit of read cal -->
   <div id="page_2" class="pages tcontent" style="display:none;">
      <div id="wrapper_2" class="wrapper tform_wrapper">
         <div id="scroller_2" class="scroller"></div>      
      </div>
   </div>
   
   <!-- page of new cal -->
   <div id="page_3" class="pages tcontent" style="display:none;">
      <div id="wrapper_3" class="wrapper tform_wrapper">
         <div id="scroller_3" class="scroller">
            <div class="container">
               <div class="tform">
            		<form action="#"  method="post" name="form1" onsubmit="return false;">
            			<div class="read_detail">
            			   <em><?=_("日程类型：")?></em>
            			   <select id="CAL_TYPE" name="CAL_TYPE">
                           <?=code_list("CAL_TYPE","")?>
                        </select>
                     </div>
                     <div class="read_detail">
            			   <em><?=_("优先级：")?></em>
            			   <select id="CAL_LEVEL" name="CAL_LEVEL">
                           <option selected value=""><?=_("未指定")?></option>
                           <?
                              foreach($CAL_LEVEL_ARRAY as $k => $v)
                              {
                           ?>
                              <option value="<?=$k?>"><?=$v?></option>
                           <? } ?>
                        </select>
                     </div>
                     <div class="read_detail">
                        <em><?=_("起始时间：")?></em>
                        <input id="CAL_TIME" type="text" name="CAL_TIME" value="格式如 09:35" onfocus="if(this.value=='格式如 09:35') this.value='';" onblur="if(this.value=='') this.value='格式如 09:35';"/>
                     </div>
                     <div class="read_detail">
                        <em><?=_("结束时间：")?></em>
                        <input id="END_TIME" type="text" name="END_TIME" value="格式如 19:23" onfocus="if(this.value=='格式如 19:23') this.value='';" onblur="if(this.value=='') this.value='格式如 19:23';"/>
                     </div>
                     <div class="read_detail endline">
            		      <em><?=_("日程内容：")?></em>
            		      <textarea id="CONTENT" name="CONTENT" rows="5" wrap="on"></textarea>
            		   </div>
            		</form>
            	</div>
      	   </div>
         </div>      
      </div>
   </div>
   
   <!-- edit of read cal -->
   <div id="page_4" class="pages tcontent" style="display:none;">
      <div id="wrapper_4" class="wrapper tform_wrapper">
         <div id="scroller_4" class="scroller"></div>      
      </div>
   </div>
</div>  
<script>
$(document).ready(function(){
   pageInit(1);   
});

//自定义下拉刷新功能，截断插件自带刷新
function pullDown_calendar()
{
   var oUl = $("#page_1").find("ul.comm-list");
   var currIterms = oUl.find("li").size();
   var pullDownEl = $("#page_1").find('.pullDown')[0];
	$.ajax({
	   type: "POST",
		url: "/pda/pad/inc/getdata.php",
		data: {'A':"refreshList", 'STYPE':stype, "P":p, "CURRITERMS":currIterms},
		success: function(m)
		{
			if(m == "NONEWDATA")
			{
			   showMessage(nonewdata);
			}else{
				var size = $("<ul>"+m+"</ul>").find("li").size();
            var osize = oUl.find("li").size();         
            if(osize == 0)
               $("#page_1").find(".no_msg").hide();
            oUl.empty().append(m);
            
            //如果数据增加
            if(size > osize){
               showMessage(sprintf(newdata,size - osize));        
            }else{
               showMessage(refreshlist);   
            }
			}
			oiScroll.refresh();
		}
	});
}

function getCalContent(cal_id)
{
   if(!cal_id) return;
   $.ajax({
      type: 'GET',
      url: 'calendar/edit.php',
      cache: true,
      data: {'P': '<?=$P?>','CAL_ID': cal_id, 'TYPE': 'read'},
      beforeSend: function()
      {
         $.ProLoading.show();
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_4 > #wrapper_4 > #scroller_4").empty().append(data);
         $("#page_4").show('fast',function(){pageInit(4);});
         $("#header_1").hide();
         $("#header_4").show();
      }
   });
}

$("ul.comm-list li").live("click tap",function(){
   $$a = $(this);
   getCalContent($$a.attr("q_id"));
});

function resetCal()
{
   $("#SAVE_TYPE").val("");
   $("#CAL_TIME").val("");
   $("#END_TIME").val("");
   $("#CONTENT").val("");
   $("#CAL_ID").val("");
}

function newCal()
{
   $("#header_1").hide();
   $("#header_3").show();
   $("#page_3").show('fast',function(){pageInit(3);});
}

function editCal()
{
   var cal_id = $("#page_4 #SHOW_CAL_ID").val();
   if(!cal_id) return;
   $.ajax({
      type: 'GET',
      url: 'calendar/edit.php',
      cache: true,
      data: {'P': '<?=$P?>','CAL_ID': cal_id, 'TYPE': 'edit'},
      beforeSend: function()
      {
         $.ProLoading.show();
      },
      success: function(data){
         $.ProLoading.hide();
         $("#page_2 > #wrapper_2 > #scroller_2").empty().append(data);
         $("#page_2").show('fast',function(){pageInit(2);});
         $("#page_4").hide();
         $("#header_4").hide();
         $("#header_2").show();
      }
   });
  
   $("#page_2 #CAL_TIME").focus();  
}

function saveCal(stype)
{
   
   if(stype == "new")
   {
      var SAVE_TYPE = $("#SAVE_TYPE").val();
      var CAL_TYPE = $("#CAL_TYPE").val();
      var CAL_LEVEL = $("#CAL_LEVEL").val();
      var CAL_TIME = $("#CAL_TIME").val();
      var END_TIME = $("#END_TIME").val();
      var CONTENT = $("#CONTENT").val();
      var CAL_ID = '';
   }else{
      var SAVE_TYPE = $("#SAVE_TYPE_EDIT").val();
      var CAL_TYPE = $("#CAL_TYPE_EDIT").val();
      var CAL_LEVEL = $("#CAL_LEVEL_EDIT").val();
      var CAL_TIME = $("#CAL_TIME_EDIT").val();
      var END_TIME = $("#END_TIME_EDIT").val();
      var CONTENT = $("#CONTENT_EDIT").val();
      var CAL_ID = $("#CAL_ID_EDIT").val();   
   }
   
   if(CAL_TIME == "格式如 09:35")
   {
      showMessage("<?=_('起始时间不能为空')?>");
      if(stype == "edit")
         $("#CAL_TIME_EDIT").focus();
      else
         $("#CAL_TIME").focus();
      return;
   }
   if(END_TIME == "格式如 19:23")
   {
      showMessage("<?=_('结束时间不能为空')?>");
      if(stype == "edit")
         $("#END_TIME_EDIT").focus();
      else
         $("#END_TIME").focus();
      return;   
   }

   $.ajax({
      type: 'POST',
      url: 'calendar/submit.php',
      cache: true,
      data: {'P': '<?=$P?>','CAL_ID': CAL_ID, 'CONTENT': CONTENT, 'CAL_TYPE': CAL_TYPE, 'CAL_LEVEL': CAL_LEVEL, 'CAL_TIME':CAL_TIME,'END_TIME':END_TIME},
      beforeSend: function()
      {
         $.ProLoading.show("<?=_('保存中...')?>");   
      },
      success: function(data){
         if(data =='OK')
         {
            $.ProLoading.hide();
            if(stype == 'edit')
            {
               showMessage("保存成功");
               reback(2,1);
               renewContent(CAL_ID,CAL_TIME+" - "+END_TIME,CAL_LEVEL,CONTENT); 
            }
            if(stype == 'new')
            {
               pullDown_calendar();
               reback(3,1);
            }
         }else{
            $.ProLoading.hide();
            showMessage(data);
         }
      }
   });
}

function renewContent(id,time,level,content)
{
   var $$cal_list = $("#cal_list li");
   $$cal_list.each(function(){
      if($(this).attr("q_id") == id)
      {
         $(this).find("h3").empty().text(time);
         $(this).find("p").empty().html( level == "" ? content : calArr[level] + " " + content);        
      }   
   });
}
</script>
</body>
</html>
