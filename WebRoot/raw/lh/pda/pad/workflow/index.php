<?
	include_once("../inc_header.php");
   include_once("inc/utility_all.php");
	
   $query = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and PRCS_FLAG < '3' and DEL_FLAG=0";
   $TOTAL_ITEMS = resultCount($query);
?>
<body>
<script type="text/javascript">
var stype = "workflow";
var pre_page = 0;
var fileReadPage = 1;
var nonewdata = "<?=_('没有新工作')?>";
var newdata = "<?=_('%s个新工作')?>";
var noeditpriv = "<?=_('无办理权限')?>";
var nosubeditpriv = "<?=_('无经办权限')?>";
var noreadflowpriv = "<?=_('无查看表单权限')?>";
var nosignflowpriv = "<?=_('无会签权限')?>";
var norightnextprcs = "<?=_('没有符合条件的下一步骤')?>";
var nosetnewprcs = "<?=_('错误：尚未设置下一步骤')?>";
var workcomplete = "<?=_('工作已结束')?>";
var workdonecomplete = "<?=_('工作办理完成')?>";
var workhasnotgoback = "<?=_('不能退回此工作')?>";
var workhasgoback = "<?=_('工作已经回退')?>";
var notselectedstep = "<?=_('请选择回退步骤')?>";
var workhasturnnext = "<?=_('工作已转交下一步')?>";
var signisnotempty = "<?=_('会签意见不能为空')?>";
var signsuccess = "<?=_('会签意见保存成功')?>";
var formsuccess = "<?=_('表单保存成功')?>";
var getfature = "<?=_('获取失败')?>";
var error = "<?=_('数据不全未能转交')?>";
var errorzbisnotnull = "<?=_('第%s步主办人不能为空')?>";
var errorblisnotnull = "<?=_('第%s步办理人不能为空')?>";
var nocreatepriv = "<?=_('没有该流程新建权限，请与OA管理员联系')?>";
var noflowlist = "<?=_('此分类没有流程！')?>";
var norunname = "<?=_('名称/文号不能为空！')?>";
var noprefix = "<?=_('前缀不能为空！')?>";
var nosuffix = "<?=_('后缀不能为空！')?>";
var namerepeat = "<?=_('输入的工作名称/文号与之前的工作重复，请重新设置。')?>";
var nocreaterun = "<?=_('新建工作失败，请重新创建！')?>";
var nocreaterunpriv = "<?=_('无可办理流程权限！')?>";
var g_pre_page = 1;
var g_now_page = 1;

var q_run_id = 0;
var q_flow_id = 0; 
var q_prcs_id = 0;
var q_flow_prcs = 0;
var q_op_flag = 1;

var now_sort = 0;
var parent = 0;
var force_pre_set = 0;			//是否强制前后缀

/* --- 自定义参数 ---*/
var nomoredata_1 = false;
var noshowPullUp_1 = <? echo ($PAGE_SIZE >= $TOTAL_ITEMS) ? "true" : "false"; ?>;


function pageInit(page_id)
{
		if(page_id == 1)//初始页
		{
			tiScroll_1 = new $.tiScroll({"page_type":'side',"nomoredata": nomoredata_1, "noshowPullUp":noshowPullUp_1});
			tiScroll_1.init();
		}
		if(page_id == 2)//主办
		{
			tiScroll_2 = new $.tiScroll({"page_id": 2, "listType": "readonly"});
			tiScroll_2.init();
		}
		if(page_id == 3)//表单
		{
			tiScroll_3 = new $.tiScroll({"page_id": 3, "listType": "readonly"});
			tiScroll_3.init();
		}
		if(page_id == 4)//会签
		{
			tiScroll_4 = new $.tiScroll({"page_id": 4, "listType": "readonly"});
			tiScroll_4.init();
		}
		if(page_id == 5)//转交步骤
		{
		try{
			}catch(ex){
			}
			tiScroll_5 = new $.tiScroll({"page_id": 5, "listType": "readonly"});
			tiScroll_5.init();
		}
		if(page_id == 6)//转交选人
		{
			tiScroll_6 = new $.tiScroll({"page_id": 6, "listType": "readonly"});
			tiScroll_6.init();
		}
		if(page_id == 7)//返回列表
		{
			tiScroll_7 = new $.tiScroll({"page_id": 7, "listType": "readonly"});
			tiScroll_7.init();
		}
		if(page_id == 8)//保存表单
		{
			tiScroll_8 = new $.tiScroll({"page_id": 8, "listType": "readonly"});
			tiScroll_8.init();
		}
		if(page_id == 9)//查看原始表单
		{
			tiScroll_9 = new $.tiScroll({"page_id": 9, "listType": "readonly"});
			tiScroll_9.init();
		}
		if(page_id == 10)//查看原始表单
		{
			tiScroll_10 = new $.tiScroll({"page_id": 10, "listType": "readonly"});
			tiScroll_10.init();
		}
		if(page_id == 11)//新建工作
		{
			tiScroll_11 = new $.tiScroll({"page_id": 11, "listType": "readonly"});
			tiScroll_11.init();
		}
		if(page_id == 12)//选择子分类
		{
			tiScroll_12 = new $.tiScroll({"page_id": 12, "listType": "readonly"});
			tiScroll_12.init();
		}
		if(page_id == 13)//新建工作
		{
			tiScroll_13 = new $.tiScroll({"page_id": 13, "listType": "readonly"});
			tiScroll_13.init();
		}
		if(page_id == "attach_read")
		{
			tiScroll_attach_read = new $.tiScroll({"page_id": "attach_read", "listType": "attach_show"});
			tiScroll_attach_read.init();      
		}
}
</script>    
   <span class="mutiMenuLayer" style="display: none;">
		<div class="mutiMenu">
			<em></em>
			<div class="opts"></div>
		</div>
	</span>

<div id="sideContentArea">
<?
   //导航数据
   $tHeadData = array(
      "1" => array(
         "c" => array("title" => "工作流"),
		 	"r" => array("class" => "","event" => "gotoWork(\"new\");", "title" => "新建"),
      )
   ); 

?>
   
   <?=buildSiderHead($tHeadData);?>
   <?=buildMessage();?>
   <?=buildSideProLoading();?>
   

	<!-- list of workflow -->

	<div id="sideContentPage_1" class="sideContentPage">
		<div id="sideContentWrapper_1" class="wrapper">
			<div id="contentScroller_1" class="scroller">
         <?=buildPullDown();?>
         <ul class="comm-list sideBarSubList preViewList" id="workflow_list">
      <?
         if($TOTAL_ITEMS > 0)
         {
            $query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.FLOW_ID and USER_ID='$LOGIN_USER_ID' and DEL_FLAG=0 and PRCS_FLAG<'3' order by FLOW_RUN_PRCS.CREATE_TIME desc limit 0,$PAGE_SIZE";
            $cursor= exequery($connection,$query);
            while($ROW=mysql_fetch_array($cursor))
            {
               $PRCS_ID = $ROW["PRCS_ID"];
               $RUN_ID = $ROW["RUN_ID"];
               $FLOW_ID = $ROW["FLOW_ID"];
               $PRCS_FLAG = $ROW["PRCS_FLAG"];
               $FLOW_PRCS = $ROW["FLOW_PRCS"];
               $OP_FLAG = $ROW["OP_FLAG"];
               $CREATE_TIME = $ROW["CREATE_TIME"];
            
                if($OP_FLAG=="1")
                   $OP_FLAG_DESC=_("主办");
                else
                	 $OP_FLAG_DESC=_("会签");
            
               if($PRCS_FLAG=="1")
               {
                  //LP 2012/4/18 10:58:25 增加未接收工作高亮处理
                  $STATUS=_("未接收");
                  $Class = " active";
               }else if($PRCS_FLAG=="2")
               {
                  $STATUS=_("已接收");
                  $Class = "";
               }
            
               $query = "SELECT FLOW_ID,RUN_NAME from FLOW_RUN WHERE RUN_ID='$RUN_ID'";
               $cursor1= exequery($connection,$query);
               if($ROW=mysql_fetch_array($cursor1))
               {
                  $FLOW_ID=$ROW["FLOW_ID"];
                  $RUN_NAME=$ROW["RUN_NAME"];
               }
            
               $query = "SELECT FLOW_NAME,FLOW_TYPE from FLOW_TYPE WHERE FLOW_ID='$FLOW_ID'";
               $cursor1= exequery($connection,$query);
               if($ROW=mysql_fetch_array($cursor1))
               {
                  $FLOW_NAME=$ROW["FLOW_NAME"];
                  $FLOW_TYPE=$ROW["FLOW_TYPE"];
               }
            
               if($FLOW_TYPE=="1")
               {
                 $query = "SELECT PRCS_NAME,FEEDBACK from FLOW_PROCESS WHERE FLOW_ID='$FLOW_ID' AND PRCS_ID='$FLOW_PRCS'";
                 $cursor1= exequery($connection,$query);
                 if($ROW=mysql_fetch_array($cursor1))
                 {
                    $PRCS_NAME=sprintf(_("第%s步："), $PRCS_ID).$ROW["PRCS_NAME"];
                    $FEEDBACK=$ROW["FEEDBACK"];
                 }
               }
               else
               {
                 $PRCS_NAME=sprintf(_("第%s步"), $PRCS_ID);
                 $FEEDBACK = 0;
               }
      ?>
            <li class="<?=$fix_for_pad['list-li-style'].$Class?>"
						q_id="<?=$CREATE_TIME?>" q_run_id="<?=$RUN_ID?>"
						q_flow_id="<?=$FLOW_ID?>" q_prcs_id="<?=$PRCS_ID?>"
						q_flow_prcs="<?=$FLOW_PRCS?>" q_op_flag="<?=$OP_FLAG?>">
						<h3>[<?=$RUN_ID?>] - <?=$FLOW_NAME?> - <?=$RUN_NAME?></h3>
						<p class="content"><?=$PRCS_NAME?> <?=$OP_FLAG_DESC?></p> <span
						class="ui-icon-rarrow"></span>
					</li>
         <?
            }//while
            echo '</ul>';
         }else{
            echo '</ul>';
            echo '<div class="no_msg">'._("暂无待处理工作流！").'</div>';
         }
         ?>	 
            <?=buildPullUp();?>
         
			
			
			
			</div>
		</div>
	</div>

</div>
<div id="mainContentArea">	
<?
   //导航数据
   $tHeadData = array(
      "2" => array(//主办
         "c" => array("title" => "工作办理"),
         "r" => array("class" => "", "event" => "showMenu(\"edit_opts\");", "title" => "操作")
      ),
      "3" => array(//表单
         "l" => array("class" => "","event" => "reback(3,2);", "title" => "返回"),
         "c" => array("title" => "查看表单"),
         "r" => array("class" => "", "event" => "showMenu(\"form_opts\");", "title" => "操作")
      ),
      "4" => array(//会签
         "l" => array("class" => "","event" => "reback(4,pre_page);", "title" => "返回"),
         "c" => array("title" => "会签"),
         "r" => array("class" => "saveSign", "event" => "", "title" => "")
      ),
      "5" => array(//转交步骤
         "l" => array("class" => "","event" => "reback(5,pre_page);", "title" => "返回"),
         "c" => array("title" => "转交下一步"),
         "r" => array("class" => "", "event" => "goOnWorkFlow();", "title" => "继续")
      ),
      "6" => array(//转交选人
         "l" => array("class" => "","event" => "reback(6,5);", "title" => "返回"),
         "c" => array("title" => "转交选人"),
         "r" => array("class" => "", "event" => "turnUserWorkFlow();", "title" => "提交")
      ),
      "7" => array(//返回列表
         "l" => array("class" => "","event" => "reback(7,2);", "title" => "返回"),
         "c" => array("title" => "办理结束")
      ),
      "8" => array(//保存表单
         "l" => array("class" => "","event" => "reback(8,2);", "title" => "返回"),
         "c" => array("title" => "保存表单"),
         "r" => array("class" => "", "event" => "showMenu(\"save_opts\");", "title" => "操作")
      ),
      "9" => array(//查看原始表单
         "l" => array("class" => "","event" => "reback(9,2);", "title" => "返回"),
         "c" => array("title" => "原始表单查看"),
      ),
      "10" => array(//查看原始表单
      		"l" => array("class" => "","event" => "reback(10,2);", "title" => "返回"),
      		"c" => array("title" => "回退"),
      		"r" => array("class" => "", "event" => "goOnSelBackWorkFlow();", "title" => "确认")
      ),
      "11" => array(//新建工作
      		"l" => array("class" => "","event" => "reback(11,2);", "title" => "返回"),
      		"c" => array("title" => "选择工作流分类"),
      ),
      "12" => array(//选择子分类
      		"l" => array("class" => "","event" => "reback(12,11);", "title" => "返回"),
      		"c" => array("title" => ""),
      ),
      "13" => array(//新建工作
      		"l" => array("class" => "","event" => "reback(13, 12);", "title" => "返回"),
      		"c" => array("title" => "新建工作"),
      		"r" => array("class" => "", "event" => "gotoWork(\"new_save\");", "title" => "确认")
      ),
      "attach_read" => array(
         "l" => array("class" => "","event" => "reback(\"attach_read\",g_pre_page);", "title" => "返回"),
         "c" => array("title" => "查看附件")
      )
   ); 

?>
   
   <?=buildMainHead($tHeadData);?>
   <?=buildMessage();?>
   <?=buildMainProLoading();?>
	<!-- page of edit workflow -->
	<div id="mainContentPage_2" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_2" class="wrapper">
			<div id="contentScroller_2" class="scroller"></div>
		</div>
	</div>

	<!-- page of read form -->
	<div id="mainContentPage_3" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_3" class="wrapper">
			<div id="contentScroller_3" class="scroller"></div>
		</div>
	</div>

	<!-- page of sign -->
	<div id="mainContentPage_4" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_4" class="wrapper">
			<div id="contentScroller_4" class="scroller"></div>
		</div>
	</div>

	<!-- page of turn1 -->
	<div id="mainContentPage_5" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_5" class="wrapper">
			<div id="contentScroller_5" class="scroller"></div>
		</div>
	</div>

	<!-- page of turn2 -->
	<div id="mainContentPage_6" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_6" class="wrapper">
			<div id="contentScroller_6" class="scroller"></div>
		</div>
	</div>

	<!-- page of end -->
	<div id="mainContentPage_7" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_7" class="wrapper">
			<div id="contentScroller_7" class="scroller"></div>
		</div>
	</div>

	<!-- page of save form -->
	<div id="mainContentPage_8" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_8" class="wrapper">
			<div id="contentScroller_8" class="scroller"></div>
		</div>
	</div>

	<!-- page of form -->
	<div id="mainContentPage_9" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_9" class="wrapper tzoom">
			<div id="contentScroller_9" class="scroller"></div>
		</div>
	</div>

	<!-- page of sel_back -->
	<div id="mainContentPage_10" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_10" class="wrapper">
			<div id="contentScroller_10" class="scroller"></div>
		</div>
	</div>

	<!-- page of new_flow folder-->
	<div id="mainContentPage_11" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_11" class="wrapper">
			<div id="contentScroller_11" class="scroller"></div>
				<ul class="comm-list comm-pic-list">

				</ul>
			</div>
		</div>

	<!-- page of new_flow files-->
	<div id="mainContentPage_12" class="mainContentPage" style="display: none;">
		<div id="mainContentWrapper_12" class="wrapper">
			<div id="contentScroller_12" class="scroller"></div>
				<ul class="comm-list comm-pic-list">

				</ul>
			</div>
		</div>
	
	<div id="mainContentPage_13" class="mainContentPage tcontent" style="display: none;">
		<div id="mainContentWrapper_13" class="wrapper tform_wrapper">
			<div id="contentScroller_13" class="scroller"></div>
		</div>
	</div>

	<!-- page of attach_file -->
	<div id="mainContentPage_attach_read" class="mainContentPage tcontent"
		style="display: none;">
		<div id="mainContentWrapper_attach_read" class="wrapper">
			<div id="contentScroller_attach_read" class="scroller"
				style="position: relative; width: 100%; height: 100%;">
				<div id="layer"
					style="position: absolute; left: 0; top: 0; height: 100%; width: 100%;"></div>
				<iframe id="file_iframe" name="file_iframe" class="attach_iframe"
					src=""></iframe>
			</div>
		</div>
	</div>
</div>
	
<script type="text/javascript" src="/pda/pad/js/workflow.js.gz"></script>
<script type="text/javascript">

$(document).ready(function(){
	tPad.changeLayout();
	var i = 14;
	while(i--) pageInit(i);
	pageInit('attach_read'); 
	
});

//点击列表主办该工作
$("#workflow_list li").live("click", function(){
   var $$a = $(this);
   //全局变量
   q_run_id      = $$a.attr("q_run_id");
   q_flow_id     = $$a.attr("q_flow_id");
   q_prcs_id     = $$a.attr("q_prcs_id");
   q_flow_prcs   = $$a.attr("q_flow_prcs");
   q_op_flag     = $$a.attr("q_op_flag");
   
   editWorkFlow();
   /*
   if($$a.attr("q_op_flag") == 1)
   {
   	editWorkFlow();
	}
	else
	{
		pre_page = 1;
		signWorkFlow();
	}
	*/     
});


function triggerFlowEvt(){
   $(".read_flow").bind("tap click",function(){gotoWork('form');});
   $(".stop_flow").bind("tap click",function(e){gotoWork('stop'); });
   $(".save_flow").bind("tap click",function(e){gotoWork('save'); });
   $(".sign_flow").bind("tap click",function(e){gotoWork('sign'); });
   $(".turn_flow").bind("tap click",function(e){gotoWork('turn'); });
   $(".sign_save_flow").bind("tap click",function(e){gotoWork('sign_save_flow'); });
   $(".continueEdit_flow").bind("tap click",function(e){gotoWork('continueEdit'); });
   $(".show_original_form").bind("tap click",function(e){gotoWork('show_original'); });
   $(".sel_flow").bind("tap click",function(e){gotoWork('sel'); });
   $("#overlay").bind("click",function(e){
      e.stopPropagation();
      if($(this).hasClass("overlayGray"))
         $.mutiMenu.hide();
   })   
}

function gotoWork(WORK_TYPE)
{   
   if(WORK_TYPE == "form")
   {
      getflowContent(); 
   }else if(WORK_TYPE == "turn")
   {
      turnWorkFlow();
   }else if(WORK_TYPE == "sign")
   {
      signWorkFlow();
   }else if(WORK_TYPE == "save")
   {
      saveWorkFlow();   
   }else if(WORK_TYPE == "continueEdit")
   {
      continueEditFlow();   
   }else if(WORK_TYPE == "stop")
   {
		stopWorkFlow();   
   }else if(WORK_TYPE == "sign_save_flow"){
		saveSignWorkFlow();		
   }else if(WORK_TYPE == "show_original"){
		showOriginalForm();
   }
   else if(WORK_TYPE == "sel"){
		selWorkFlow();
   }
   else if(WORK_TYPE == "new"){
		newFlow();
   }
   else if(WORK_TYPE == "new_save"){
		saveNewWorkFlow();
	}
   $.mutiMenu.hide();
}

function showMenu(opts_type)
{
   if($("#"+opts_type).length > 0)
   {
      var menu = $("#"+opts_type).html();
      $.mutiMenu.init(menu);
      $.mutiMenu.show();
      triggerFlowEvt();
   }   
}
function reback(from, to){
	window['tiScroll_' + to].show();
	pageInit(to);
	pre_page = to;
}
//处理返回操作
function new_reback()
{
	if(now_sort == parent)
		return ;
	if(parent == 0){
		reback(12,11);
		now_sort = 0;
		return ;
	}
	getFlowNewlist(parent);
}

$("ul.comm-list li").live("click tap",function(){
	   var $this = $(this);
	   if($this.hasClass("files"))
	   {
	      getFlowNew($this.attr("q_id"));
	   }
	   else if($this.hasClass("folder"))
	   {
		   getFlowNewlist($this.attr("q_id"),$this.attr("q_name"),now_sort);
	   }
});

//2012/4/25 16:26:41 lp 自定义下拉刷新功能，截断插件自带刷新

</script>
</body>
</html>