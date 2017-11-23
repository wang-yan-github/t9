//修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
function fixDbClick(e){
   var last_click_timer = this.getAttribute('_last_click_timer_'),
   this_click_timer = (new Date).getTime();
   if(this_click_timer - last_click_timer < 1000){       
      e.stopPropagation();
      return false;
   }
   this.setAttribute('_last_click_timer_', this_click_timer);
}

//2012/6/18 3:01:12 lp 检测中文
function isChineseChar(str){     
   var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]$/;  
   return reg.test(str);  
}

function showPageMessage(str){
	//alert(str);
	//$("div[id^='page_']").hide();
    //$("#page_7 > #wrapper_7 > #scroller_7").empty().append(reMakeMessage(str));
    //$("#page_7").show('fast',function(){pageInit(7);});
    //$("#header div[id^='header_']").hide();
    //$("#header_7").show();
}

//保存表单
function saveWorkFlow(){
    //saveSignWorkFlow();
    var data = $("#edit_from").serialize();
    $.ajax({
        type: 'POST',
        url: contextPath + '/t9/mobile/workflow/act/T9PdaHandlerAct/editSubmit.act',
        cache: false,
        async: false,
        data: data + "&sessionid="+p,
        beforeSend: function(){
           //$.ProLoading.show();
        },
        success: function(data){
        	alert("保存成功");
            //$.ProLoading.hide();
            //lp 2012/4/23 15:17:28 如果是从主办转交则保存，不跳转页面
            /**if(a) return;
            $.ajax({
                type: 'GET',
                url: contextPath + '/t9/mobile/workflow/act/T9PdaHandlerAct/sign.act',
                cache: false,
                data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs, 'OP_FLAG': q_op_flag},
                success: function(dataSign){
                    $("#CONTENT").val("");
                    $("#editSignBox").empty().append(dataSign);
                    //showMessage(formsuccess);
                    //oiScroll_2.refresh();
                }
            });**/
        },
        error: function(data){
        	//$.ProLoading.hide();  
        	//showMessage(getfature);
        }
    });
}

function turnWorkFlow(){
    //lp 2012/4/23 15:14:40 转交时增加保存
    saveWorkFlow('ISFORMTURN'); 
    var flow_type = $("input[name='FLOW_TYPE']").val();
    var url = flow_type == 2 ? contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnUser.act' : contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turn.act';
    //var page_num = flow_type == 2 ? '6' : '5';
    //turn_back_page = flow_type == 2 ? '2' : '5';
    //TOP_FLAG = $("input[name='TOP_FLAG']").val(); 
    $.ajax({
        type: 'GET',
        url: url,
        cache: false,
        data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'FLOW_TYPE':flow_type,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs},
        beforeSend: function(){
            //$.ProLoading.show();
        },
        success: function(data){
        	//alert(data);
           //$.ProLoading.hide();
           if(data == "NOEDITPRIV"){
               //showPageMessage(noeditpriv);
               //delete_flow();
               return;  
           }else if(data == "NOSIGNFLOWPRIV"){
               //showPageMessage(nosignflowpriv);
               //delete_flow();
               return;   
           }else if(data == "NORIGHTNEXTPRCS"){
        	   //showPageMessage(norightnextprcs);
        	   return;   
           }else if(data == "NOSETNEWPRCS"){
        	   //showPageMessage(norightnextprcs);
        	   return; 
           }else{
        	   $(".vux-header-left a").attr("href", "#");//头部返回按钮
               $(".vux-header-title").html("转交下一步");//头部中间内容
               $(".vux-header-right").show(); //头部操作按钮
               
               $(".hk-trave-apply").hide();//中间正文内容
               //表单页面
               $("#flow_chooseNext").empty().append(data);
               $("#flow_chooseNext").show();
           }
           //$("#page_"+page_num+" > #wrapper_"+page_num+" > #scroller_"+page_num).empty().append(data);
           //$(".pages").hide();
           //$("#page_"+page_num).show('fast',function(){
           //   pageInit(page_num);
           //   $("#page_"+page_num+" .container .read_detail:last").addClass("endline");
           //});
           //$("#header div[id^='header_']").hide();
           //$("#header_"+page_num).show();
        },
        error: function(data){
        	//$.ProLoading.hide(); 
        	//showMessage(getfature);
        }
	});
}

/**
 * 获取选择下一步骤界面内容（进入选人界面）
 */
function goOnWorkFlow(){
    //判断是否设置了强制转交
    var NOT_ALL_FINISH = $("input[name='NOT_ALL_FINISH']").val(); //未办理完毕的经办人
    var TURN_PRIV = $("input[name='TURN_PRIV']").val(); //强制转交
    if(TURN_PRIV != 1 && NOT_ALL_FINISH !=""){
        alert("经办人["+NOT_ALL_FINISH+"]尚未办理完毕，不能结束流程！");
        return;
    }
    if(TURN_PRIV == 1 && NOT_ALL_FINISH !=""){
        if(confirm('经办人['+NOT_ALL_FINISH+']尚未办理完毕，确认要结束流程吗？')){
        }else{
            return ;
        }
    }
    action = $("input[name='turn_action']").val();
    var prcs_id_next = $("input[name='NEW_PRCS_ID_NEXT']").val();
    var flow_type = $("input[name='FLOW_TYPE']").val();
    if((prcs_id_next == '' || typeof(prcs_id_next) == 'undefined') &&  flow_type != 2){return ;}
    $.ajax({
        type: 'GET',
        url: action,
        cache: false,
        data: {'sessionid': p, 'RUN_ID': q_run_id,'FLOW_ID': q_flow_id,'PRCS_ID': q_prcs_id,'FLOW_PRCS': q_flow_prcs, 'PRCS_ID_NEXT': prcs_id_next, 'FLOW_TYPE': flow_type},
        beforeSend: function(){
            //$.ProLoading.show();       
        },
        success: function(data){
        	//alert(data);
        	//$.ProLoading.hide(); 
        	if(data == "NONEXTPRCS"){
        		//showPageMessage(nonextprcs);
        		return;   
        	}else if(data == "NOEDITPRIV"){
        		//showPageMessage(noeditpriv);
        		return;  
        	}else if(data == "NOSIGNFLOWPRIV"){
        		//showPageMessage(nosignflowpriv);
        		return;
        	}else if(data == "WORKCOMPLETE"){
        		//showPageMessage(workcomplete);
        		//delete_flow();
        		return;   
        	}
	        if(prcs_id_next == 0){
	            //showPageMessage(data); 
	        }else{
	            $(".vux-header-left a").attr("href", "#");//头部返回按钮
                $(".vux-header-title").html("转交选人");//头部中间内容
                $(".vux-header-right").show(); //头部操作按钮
                $(".hk-trave-apply").hide();//中间正文内容
                //表单页面
                $("#flow_chooseNext").hide();
                $("#flow_chooseUser").empty().append(data);
                $("#flow_chooseUser").show();
	            //$("#page_6 > #wrapper_6 > #scroller_6").empty().append(data);
	            //$("#page_6").show('fast',function(){pageInit(6);});
	            //$("#header div[id^='header_']").hide();
	            //$("#header_6").show();
	         }
        },
        error: function(data){
        	//$.ProLoading.hide();  
        	//showMessage(getfature);
        }
    });
}

/**
 * 获取选择下一步骤办理人界面内容
 */
function turnUserWorkFlow(){
	var prcs_id_next = $("input[name='NEW_PRCS_ID_NEXT']").val();
	var preset = $("input[name='PRESET']").val();
	var NOT_ALL_FINISH = $("input[name='NOT_ALL_FINISH_NEXT']").val(); //未办理完毕的经办人
	var TURN_PRIV = $("input[name='TURN_PRIV']").val(); //强制转交
	var FLOW_TYPE = $("input[name='FLOW_TYPE']").val(); 
	if(prcs_id_next == "") {
		showMessage(error);
		return;
	}else{
		prcs_id_next = decodeURIComponent(prcs_id_next);   //添加URL解码，兼容部分浏览器  
	}
	var POST_STR = "RUN_ID="+q_run_id+"&FLOW_ID="+q_flow_id+"&PRCS_ID="+q_prcs_id+"&FLOW_PRCS="+q_flow_prcs+"&PRCS_ID_NEXT="+prcs_id_next+"&PRESET="+preset + "&FLOW_TYPE=" + FLOW_TYPE;
	var prcs_id_next_arr = prcs_id_next.split(",");
	var _continue = true;
	var _error_step = 0;
	$.each(prcs_id_next_arr, function(key, val){
		if(val){
			var _zbems = $("#USER_ZB_" + val).find("em");
			var _cbems = $("#USER_CB_" + val).find("em");
			if($("#TOP_DEFAULT_" + val).val() != undefined){
				POST_STR += "&TOP_DEFAULT_" + val + "=" + $("#TOP_DEFAULT_" + val).val();
			}
			//判断是否允许主办为空的情况
			if(eval("typeof(allow_zb_isnull_"+val+") !=\"undefined\"")) {
				if(eval("allow_zb_isnull_"+val+" == '0'")) {
					if(_zbems.length == 0) {
						_continue = false;
						_error_step = val;
						errorblmsg = errorzbisnotnull;
					}
				}else{
					if(_zbems.length == 0 && _cbems.length == 0) {
						_continue = false;
						_error_step = val;
						errorblmsg = errorblisnotnull;
					}
				}
			}
			if(_zbems.length > 0) {
				//新版拼接主办人字符串
				POST_STR += "&PRCS_USER_OP_" + val + "=" + $("#USER_ZB_" + val).find("em").attr("uid");
			}
			//新版经办人拼接
			var PRCS_USER_TMP = "";
			if(_cbems.length > 0) {
				$("#USER_CB_" + val).find("em").each(function() {
					PRCS_USER_TMP += $(this).attr("uid") + ",";
				});
				POST_STR += "&PRCS_USER_" + val + "=" + PRCS_USER_TMP;
			}
		}
	});
	if(!_continue) {
		showMessage(sprintf(errorblmsg,_error_step));
		return;   
	}
    if(TURN_PRIV != 1 && NOT_ALL_FINISH !=""){
    	alert("经办人["+NOT_ALL_FINISH+"]尚未办理完毕，不能转交流程！");
    	return;
    }
    if(TURN_PRIV == 1 && NOT_ALL_FINISH !=""){
    	if(confirm('经办人['+NOT_ALL_FINISH+']尚未办理完毕，确认要转交下一步骤吗？')){
    	
    	}else{
        	return ;
    	}
    }
    $.ajax({
    	type: 'POST',
    	url: contextPath + '/t9/mobile/workflow/act/T9PdaTurnAct/turnSubmit.act',
    	cache: false,
    	data: POST_STR + "&sessionid=" + p,
    	beforeSend: function(){
    		//$.ProLoading.show();
    	},
    	success: function(data){
    		alert(data);
    		//$.ProLoading.hide();
    		if(data == "NOEDITPRIV"){
    			//showPageMessage(noeditpriv);
    			return;  
    		}else if(data == "NOSIGNFLOWPRIV"){
    			//showPageMessage(nosignflowpriv);
    			return;   
    		}else if(data == "WORKCOMPLETE"){
    			//showPageMessage(workcomplete);
    			//delete_flow();
    			return;   
    		}else if(data == "WORKHASTURNNEXT"){
    			//showPageMessage(workhasturnnext);
    			//delete_flow();
    			return; 
    		} else {
    			alert(data);
    		}
    	},
    	error: function(data){
    		//$.ProLoading.hide();  
    		//showMessage(getfature);
    	}
    });
}

//2012/6/18 2:52:29 lp 工作流选人扩展搜索
$.extend({
	workFlowSearch: function (options) {
		var defaults = {
				url: ""
		};
		var options = $.extend(true, defaults, options);
		var input = options.input;
		var list = options.list;
		var appendDom_top = options.appendDom_top;
		var appendDom_zb = options.appendDom_zb;
		var appendDom_cb = options.appendDom_cb;
		var showbtn = options.showbtn;
		var nodate = options.nodate;
		var pageScroll = options.pageScroll;
		var url = options.url;
		if(typeof(mobile_contactlisturl) != "undefined")
			url = mobile_contactlisturl;
		var $$input = $(input);
		var $$list = $(list);
		var orgHtml = $(list).html();
		var $$showbtn = $(showbtn);
		var $$nodate = $(nodate);
   
		var _tmp_key;
		var searchInterval = null;
   
		function init(){
			$$input.focus(function(e){
				e.stopPropagation(); 
				searchInterval = null;
				searchInterval = window.setInterval(search_name,1000);
				$(this).addClass("hasNoBackGround");
			});
			$$input.blur(function(){
			if($(this).val() == '')
				$(this).removeClass("hasNoBackGround");
				window.clearInterval(searchInterval);
				searchInterval = null;
			});
			//2012/6/24 13:24:11 lp 绑定主办按钮点击事件
			$$list.find("a.ui-li-text-a").off("click").on("click",function(e) {
				//修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
				if(false === fixDbClick.call(this, e)){
					return false;
				}
				e.stopPropagation();
				if($(this).hasClass("current")) {
					$(this).removeClass("current");
					$(this).parents("li").removeClass("active");
					remove_user("zb",$(this).parents("li"));
					return;  
				}else{
					if($(appendDom_zb).find("em").length > 0) {
						var uid = $(appendDom_zb).find("em").attr("uid");
						remove_user("onlyzb",uid);
						//$$list.find("a.current").parents("li").removeClass("active");
						$$list.find("a.current").removeClass("current");   
					}
					$(this).parents("li").addClass("active");
					$(this).addClass("current");
					add_user("zb",$(this).parents("li"));
					return;
				}
			});
			//2012/6/24 13:24:11 lp 绑定列表点击事件
			$$list.find("li").off("click").on("click",function(e) {            
				//修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
				if(false === fixDbClick.call(e.target, e)){
					return false;
				}
				if($(this).hasClass("active")) {
					$(this).removeClass("active");
					currentA = $(this).find("a.current");
					if(currentA.length > 0) {
						currentA.removeClass("current");
						remove_user("zb", $(this));
						return;                 
					}else{
						remove_user("cb", $(this));
						return;      
					}
				}else{
					$(this).addClass("active");
					that = $(this);
					_uid = that.attr("q_id");
					var haszb = hascb = false;
					if($(appendDom_zb).find("em").length > 0) {
						$(appendDom_zb).find("em").each(function(){
							if($(this).attr("uid") == _uid) {
								that.addClass("active");
								that.find("a.ui-li-text-a").addClass("current");
								haszb = true;
								return false;   
							}
						});
						$(appendDom_cb).find("em").each(function(){
							if($(this).attr("uid") == _uid) {
								that.addClass("active");
								hascb = true; 
								return false;
								return;
							}   
						});
						//主办和从办都没有选择该人的时候，加入该人
						if(!hascb && !hascb) {
							add_user("cb", $(this));
						}
						return;
					}else{
						$(this).find("a.ui-li-text-a").addClass("current");
						add_user("zb", $(this));
						return;  
					}   
				}      
			});
			//2012/6/24 22:14:06 lp 绑定主办人的删除操作
			var appendDom_zb_oems = $(appendDom_zb).find("em");
			var appendDom_zb_ospans = $(appendDom_zb).find("em span");
			appendDom_zb_oems.off("click").on("click",function(e) {            
				//修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
				if(false === fixDbClick.call(this, e)){
					return false;
				}
				e.stopPropagation();
				if(!$(this).hasClass("active")) {
					$(appendDom_zb).find("em").removeClass("active");
					$(appendDom_zb).find("em span").animate({width: '0'},{complete: function(){$(this).hide();}, duration: 200 });
					$(this).addClass("active");
					$(this).find("span").animate({width: '16'},{complete: function(){$(this).show();}, duration: 200 });
				}else{
					$(this).removeClass("active");
					$(this).find("span").animate({width: '0'},{complete: function(){$(this).hide();}, duration: 200 });
				}
			});
			appendDom_zb_ospans.off("click").on("click",function(e) {
				//修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
				if(false === fixDbClick.call(this, e)){
					return false;
				}
				e.stopPropagation();
				var emP = $(this).parent("em");
				emP.remove();
				//同时删除列表数据中主办对应的颜色
				var uid = emP.attr("uid");
				$$list.find("li").each(function() {
					if($(this).attr("q_id") == uid) {
						$(this).find("a.ui-li-text-a").removeClass("current");
						return false;
					} 
				});
				return;     
			});
			//2012/6/24 22:14:06 lp 绑定经办人的删除操作
			var appendDom_cb_oems = $(appendDom_cb).find("em");
			var appendDom_cb_ospans = $(appendDom_cb).find("em").find("span");
			appendDom_cb_oems.off("click").on("click",function(e) {
				//修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
				if(false === fixDbClick.call(this, e)){
					return false;
				}
				e.stopPropagation();
				if(!$(this).hasClass("active")) {
					$(appendDom_cb).find("em").removeClass("active");
					$(appendDom_cb).find("em span").animate({width: '0'},{complete: function(){$(this).hide();}, duration: 200 });
					$(this).addClass("active");
					$(this).find("span").animate({width: '16'}, {complete: function(){$(this).show();}, duration: 200 });
				}else{
					$(this).removeClass("active");
					$(this).find("span").animate({width: '0'},{complete: function(){$(this).hide();}, duration: 200 });
				}
			});
      appendDom_cb_ospans.off("click").on("click",function(e)
      {
         //修复chrome下单击触发两次的bug by JinXin @ 2012/10/15
         if(false === fixDbClick.call(this, e)){
             return false;
         }
         e.stopPropagation();
         var emP = $(this).parent("em");
         emP.remove();
         
         var uid = emP.attr("uid");
         $$list.find("li").each(function()
         {
            if($(this).attr("q_id") == uid)
            {
               //删除经办的时候同时如果是主办，则删除主办
               if($(this).find("a.current").length > 0)
               {
                  $(this).find("a").removeClass("current");
                  $(appendDom_zb).find("em").each(function()
                  {
                     if($(this).attr("uid") == uid)
                     {
                        $(this).remove();
                        return false;
                     }
                  }); 
               }
               $(this).removeClass("active");
               return false;
            } 
         });
         return;     
      });
    }
		
    function add_user(t, o){
    	str = "";
    	_oSelect_uid = o.attr("q_id");
    	_oSelect_name = o.attr("q_name");
    	_oSelect_user_id = o.attr("q_user_id");
    	str = "<em uid='"+_oSelect_uid+"' userid='"+_oSelect_user_id+"'>" + _oSelect_name +"<span>—</span></em>";
    	if(t == "zb") {
    		if($(appendDom_top).val() == 0 || $(appendDom_top).val() == undefined) {
    			$(appendDom_zb).append(str);
    		} else  {
    			$$list.find("a.ui-li-text-a").removeClass("current");
    		}
         //判断有无从办
         var cb_has = false;
         if($(appendDom_cb).find("em").length > 0)
         {
            $(appendDom_cb).find("em").each(function(){
               if($(this).attr("uid") == _oSelect_uid){
                  cb_has = true;
                  return false;         
               }   
            });     
         }
         
         if(!cb_has)
            $(appendDom_cb).append(str);
      }else{
         $(appendDom_cb).append(str);
      }
   }
   
   function remove_user(t, o)
   {
      _oSelect_uid = typeof(o) == "object" ? o.attr("q_id") : o;
      if(t == "zb")
      {
         $(appendDom_zb).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid)
               $(this).remove();
            else
               return true;
         });
         
         $(appendDom_cb).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid)
               $(this).remove();
            else
               return true;
         });
      }else if(t == "cb"){
         $(appendDom_cb).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid)
               $(this).remove();
            else
               return true;
         });            
      }else{
         $(appendDom_zb).find("em").each(function(){
            if($(this).attr("uid") == _oSelect_uid)
               $(this).remove();
            else
               return true;
         });
      }
   }
   
   //2012/6/26 14:13:15 lp
   function refreshListStatu()
   {
      var zb_oems = $(appendDom_zb).find("em");
      var cb_oems = $(appendDom_cb).find("em");
      if(zb_oems.length > 0)
      {
         var zb_cell_id = zb_oems.attr("uid");  
         $(list).find("li").each(function(){
            if($(this).attr("q_id") == zb_oems.attr("uid"))
            {
               $(this).find("a.ui-li-text-a").addClass("current");
               return false;
            }
         });
      }
      
      if(cb_oems.length > 0)
      {
         cb_oems.each(function()
         {
            var cb_ceil_id = $(this).attr("uid");
            $(list).find("li").each(function()
            {
               if($(this).attr("q_id") == cb_ceil_id)
               {
                  $(this).addClass("active");
                  return false;
               }
            });
         });   
      }
   }
   
   function search_name()
   {
      var key = $$input.val();
      if(key!="")
      {
         if(key!=_tmp_key)
         {
            $$showbtn.hide();
            _tmp_key = key;
            
            if(/^[A-Za-z0-9]+$/.test(key))
            {
               var _key_len = key.length;
               if(_key_len > 1)
               {
                  for(var i = 0;i < key.length;i++)
                  {
                     reg = key.charAt(i) + "(.*)";
                  } 
               }else{
                  reg = key + "(.*)";      
               } 
               
               eval("reg = /(.*)" + reg + "/");
               _orgObj = $("<ul>"+orgHtml+"</ul>");
               _orgObj.find("li").each(function(){
                  q_name_index = $(this).attr("q_name_index");
                  if(reg.test(q_name_index))
                     return true;
                  else
                     $(this).remove();
               });
                                   
            }else if(!isChineseChar(key))
            {
               $.ajax({
                  type: 'GET',
                  url: url,
                  cache: false,
                  data: {"KWORD":key, "P":p ,"ACTION": "getNameIndex"},
                  beforeSend: function(){
                     //$.ProLoading.show();   
                  },
                  success: function(data)
                  {
                     //$.ProLoading.hide();
                     var nameArr = [];
                     nameArr = data.split("*");
                     eval("reg = /(.*)" + nameArr.join("\\*(.*)") + "/");
                     _orgObj = $("<ul>"+orgHtml+"</ul>");
                     _orgObj.find("li").each(function(){
                        q_name_index = $(this).attr("q_name_index");
                        //console.log(q_name_index + " " + reg);
                        if(reg.test(q_name_index))
                           return true;
                        else
                           $(this).remove();
                     });
                  }
               });
            }else{
               //如果为纯中文，则直接搜索结果列表
               _orgObj = $("<ul>"+orgHtml+"</ul>");
               var _key_len = key.length;
               
               var partten = '';
               //如果包括多个汉字
               if(_key_len > 1)
               {
                  for(var i = 0;i < key.length;i++)
                  {
                     if(key.charCodeAt(i) > 128)
                     {
                        var partten = partten + key.charAt(i) + "(.*?)";
                     }
                  } 
               } 
               _orgObj.find("li").each(function(){
                  q_name = $(this).attr("q_name");
                  //执行数组循环判断
                  if(_key_len > 1)
                  {
                     if(eval("/" + partten + "/.test(q_name)"))
                        return true;
                     else
                        $(this).remove();
                  }else{
                     //单个汉字
                     if(q_name.indexOf(key) > -1)
                        return true;
                     else
                        $(this).remove();   
                  } 
               });
            }
            
            li_len = _orgObj.find("li").size();
            if(li_len > 0 )
            {
               $$nodate.hide();
               $$list.empty().append(_orgObj).find("li:hidden").show();
               if($(showbtn).length == 0 || li_len == 1){
                  $$list.find("li:last").css("border-bottom","none");
               }
            }else{
               $$list.empty();$$nodate.show();   
            }  
         }
         
         refreshListStatu();
         return;
      }else{
         if(_tmp_key == key && key == "")
            return;
            
         _tmp_key = key;
         //如果为点击删掉的，则全部显示列表

         $$list.empty().append(orgHtml);
         refreshListStatu();
         if($(showbtn).length == 0){
            $$list.find("li:hidden").show();
             $$list.find("li:last").css("border-bottom","none"); 
         }
         $$showbtn.show();
         $$nodate.hide();
         eval(pageScroll+".refresh()");
      }   
   }
   return{
      init: init,
      refresh: refreshListStatu    
   }
}
});