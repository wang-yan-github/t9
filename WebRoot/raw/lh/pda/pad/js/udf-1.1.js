//css3动画完成事件
!function ($) {
  $(function () {
    $.support.transition = (function () {
      var transitionEnd = (function () {
		
        var el = document.createElement('Pad'), 
				transEndEventNames = {
              'WebkitTransition' : 'webkitTransitionEnd',
              'MozTransition'    : 'transitionend',
              'OTransition'      : 'oTransitionEnd otransitionend',
              'transition'       : 'transitionend'
            }, 
				name;

        for (name in transEndEventNames){
          if (el.style[name] !== undefined) {
            return transEndEventNames[name]
          }
        }
		  
      }());

      return transitionEnd && {
        end: transitionEnd
      }

    })()
  })
}(window.jQuery);



//lp 2012/2/26 23:47:59 如果为数据加载则加载 pull loading
$.extend({
      tiScroll: function (options) 
      {
         var defaults = {
            page_id: '1',
            page_type: 'main',
            listType: "listview",
            nomoredata: false,
            noshowPullUp: false,
				refreshCallback : $.noop,
				onPullUp: $.noop,
				onPullDown: $.noop
         };
			var oiScroll,$$page_dom,$$wrapper_dom,isVisible;
         var options = $.extend(true, defaults, options);
         var page_id = options.page_id;
         var page_type = options.page_type;
         var listType = options.listType;
         var nomoredata = options.nomoredata;
         var noshowPullUp = options.noshowPullUp;
       //  eval("window.oiScroll_" + page_type +" = window.oiScroll_"+ page_type + "_" + page_id +" || null");
         window["window.oiScroll_" + page_type ] = window["oiScroll_" + page_type + "_" + page_id ] || null;
			var page_prefix = page_type == "main" ? "mainContentPage_" : "sideContentPage_";  
         
         function setPageId(tid){
            var page_id = tid;
            $$page_dom =  $("#" + page_prefix + page_id);
         }
         
         function init()
         {
            $$page_dom = $("#" + page_prefix + page_id);
            $$wrapper_dom = $("#" + page_prefix + page_id +" .wrapper");  
            
				isVisible = $$page_dom.is(':visible');
				$$page_dom.show();	
				
				
            if(("oiScroll_"+page_type) in window){
					window["oiScroll_"+page_type].destroy();
				}
         
            if(listType == "listview")
            {
               if(noshowPullUp){
                  $$page_dom.find('.pullUp').hide();   
               }
               
               var pullDownEl, pullDownOffset,pullUpEl, pullUpOffset;
            	pullDownEl = $$page_dom.find('.pullDown')[0];
            	pullDownOffset = pullDownEl.offsetHeight;
               pullUpEl = $$page_dom.find('.pullUp')[0];
               pullUpOffset = pullUpEl ? pullUpEl.offsetHeight : $$page_dom.find('.loadingComplete')[0].offsetHeight;
               oiScroll = new iScroll($$wrapper_dom[0], {
            		useTransition: false,
            		topOffset: pullDownOffset,
            		onBeforeScrollStart: function (e) {
            		   if(e.target.nodeName.toLowerCase()!="li"){
            		      if($(e.target).parents("li").length > 0){
            		         var target = $(e.target).parents("li")[0];
            		      }else{
            		         return;      
            		      }    
            		   }else{
            		      var target = e.target;   
            		   }
            			clearTimeout(this.hoverTimeout);
            			while (target.nodeType != 1) target = target.parentNode;
            			this.hoverTimeout = setTimeout(function () {
            				if (!hoverClassRegEx.test(target.className)) target.className = target.className ? target.className + ' iScrollHover' : 'iScrollHover';
            			}, 80);
            			this.hoverTarget = target;
            		},
            		onRefresh: function () {
            			if (pullDownEl.className.match('loading')) {
            				pullDownEl.className = 'pullDown';
            				pullDownEl.querySelector('.pullDownLabel').innerHTML = td_lang.pda.msg_4;
            			} else if (pullUpEl && pullUpEl.className.match('loading')) {
            				pullUpEl.className = 'pullUp';
            				pullUpEl.querySelector('.pullUpLabel').innerHTML = td_lang.pda.msg_6;
            			}
							options.refreshCallback.call(this);
            		},
            		onScrollMove: function () {
            			if (this.y > 5 && !pullDownEl.className.match('flip')) {
            				pullDownEl.className = 'pullDown flip';
            				pullDownEl.querySelector('.pullDownLabel').innerHTML = td_lang.pda.msg_5;
            				this.minScrollY = 0;
            			} else if (this.y < 5 && pullDownEl.className.match('flip')) {
            				pullDownEl.className = 'pullDown';
            				pullDownEl.querySelector('.pullDownLabel').innerHTML = td_lang.pda.msg_7;
            				this.minScrollY = -pullDownOffset;
            			} else if (this.y < (this.maxScrollY - 5) && pullUpEl && !pullUpEl.className.match('flip')) {
            			   if(nomoredata) return;
            				pullUpEl.className = 'pullUp flip';
            				pullUpEl.querySelector('.pullUpLabel').innerHTML = td_lang.pda.msg_7;
            				this.maxScrollY = this.maxScrollY;
            			} else if (this.y > (this.maxScrollY + 5) && pullUpEl && pullUpEl.className.match('flip')) {
            			   if(nomoredata) return;
            				pullUpEl.className = 'pullUp';
            				pullUpEl.querySelector('.pullUpLabel').innerHTML = td_lang.pda.msg_2;
            				this.maxScrollY = pullUpOffset;
            			}
            			removeClass();
            		},
            		onScrollEnd: function () {
            			if (pullDownEl.className.match('flip')) {
            				pullDownEl.className = 'pullDown loading';
            				pullDownEl.querySelector('.pullDownLabel').innerHTML = td_lang.pda.msg_2;				
            				pullAction('down',$$page_dom);
            			} else if (pullUpEl && pullUpEl.className.match('flip')) {
                        if(nomoredata) return;
            				pullUpEl.className = 'pullUp loading';
            				pullUpEl.querySelector('.pullUpLabel').innerHTML = td_lang.pda.msg_2;
            				if(!noshowPullUp){
            				   pullAction('up',$$page_dom);
            				}
            			}
            		},
            		onBeforeScrollEnd: removeClass
            	});   
            }else{
               oiScroll = new iScroll($$wrapper_dom[0],{
                  useTransform: false,
                  onBeforeScrollStart: function (e) {
                    var target = e.target;
                    while (target.nodeType != 1) target = target.parentNode;
                    if (target.tagName != 'SELECT' && target.tagName != 'INPUT' && target.tagName != 'TEXTAREA'){
                        e.preventDefault();
                        e.stopPropagation();
                    }
                  }
               });   
            }
				$$page_dom[ isVisible ? 'show' : 'hide' ]();
            // eval("window.oiScroll_" + page_type + "_" + page_id + "= oiScroll");
            // eval("window.oiScroll_" + page_type + "= oiScroll");
				window["oiScroll_" + page_type + "_" + page_id ] = oiScroll;
				window["oiScroll_" + page_type ] = oiScroll;			
            return oiScroll;
         }
         
			function getElement(){
				return $$page_dom;
			}
			
			function getOIScroll(){
				return window["oiScroll_" + page_type + "_" + page_id ];
			}
			function refresh(){
				window["oiScroll_" + page_type + "_" + page_id ].refresh();
			}
         function destroy(){
            // if(eval("oiScroll_" + page_type))
					// eval("oiScroll_"+page_type+".destroy()");   
				"oiScroll_" + page_type in window && window["oiScroll_" + page_type].destroy();
         }
         
         function pullAction (pullaction,obj) 
         {
            var oUl = obj.find("ul.sideBarSubList");
				
				var func = pullaction != 'down' ? options['onPullUp'] : options['onPullDown'];
				
				if(func.call(this) === false){
               //自定义截断函数
					return;
				}
				
            if(pullaction == 'down')
            {
					var lastedId =  oUl.find("li:first").attr("q_id");
					$.get(
						"inc/getdata.php", 
						{'A':"GetNew", 'STYPE':stype, "LASTEDID": lastedId},
						function(data)
						{
							if(data == "NONEWDATA")
							{
								showMessage(nonewdata);
							}else{
								var size = $("<ul>"+data+"</ul>").find("li").size();
								var osize = oUl.find("li").size();
								
								if(osize == 0)
									$$page_dom.find(".no_msg").hide();
										
								oUl.prepend(data);
								showMessage(sprintf(newdata,size));
							}
							oiScroll.refresh();
						}
					);
                  
            }else{  
               var currIterms = oUl.find("li").size();
               //lp 2012/5/2 0:59:57 增加获取更多时，条件控制
               if(currIterms > 0){
                  var lastGetId = oUl.find("li:last").attr("q_id");    
               }
               $.get(
                  "inc/getdata.php", 
                  {'A':"GetMore", 'STYPE':stype, "P":p, "CURRITERMS": currIterms, "LASTGETID": lastGetId},
                  function(data)
                  {
                     if(data == "NOMOREDATA")
                     {
                        $$page_dom.find(".pullUp").remove();
                        
                        nomoredata = true;
                      //  eval(page_type + "_nomoredata_" + page_id + "= true");
                        window[ page_type + "_nomoredata_" + page_id ] = true;
                        noshowPullUp = true;
                       // eval(page_type + "_noshowPullUp_" + page_id + "= true");
                        window[ page_type + "_noshowPullUp_" + page_id ] = true;
								
                        $$page_dom.find(".scroller").append('<div class="loadingComplete">' + td_lang.pda.msg_8 + '</div>');
                     }else{
                        oUl.append(data);
                        //eval("oiScroll_" + page_type + ".refresh()");
								getOIScroll().refresh();
                     }
                  }
               );   
            }
         }
         var hoverClassRegEx = new RegExp('(^|\\s)iScrollHover(\\s|$)'),
         
         removeClass = function () {
         	if (this.hoverTarget) {
         		clearTimeout(this.hoverTimeout);
         		this.hoverTarget.className = this.hoverTarget.className.replace(hoverClassRegEx, '');
         		this.target = null;
         	}
         },
			// getMainData(url, data, onSuccess, showCallback) or getMainData({ url:url, data:data, type:type, ...  })
			// by JinXin @ 2012/9/4
			getMainData = function(url, data, onSuccess, showCallback) {
				var me = this,
				args = arguments,
				opts,
				defaults = {
					url: '',
					type: 'get',
					cache: true,
					data: {},
					onSuccess: $.noop,
					showCallback: $.noop,
					onBeforeSend: $.noop,
					onError: $.noop
				};
				
				if('string' === typeof args[0]) {
					opts = $.extend(true, defaults, {				
						url: args[0],
						data: args[1],
						onSuccess: args[2],
						showCallback: args[3]
					});	
				} else if('object' === typeof args[0]) {
					opts =  $.extend(true, defaults, args[0]);	
				} else {
					return;
				}
				
				$.ajax({
					url: opts.url,
					data: opts.data,
					type: opts.type,
					cache: opts.cache,
					beforeSend: function(){
						$.ProMainLoading.show();   
						opts.onBeforeSend.apply(this, arguments);
					},
					success: function(data){
						$.ProMainLoading.hide();
						if(false === opts.onSuccess.apply(me, arguments)){
							return
						}
						$(".scroller", $$page_dom).empty().append(data);
						$$page_dom.show();
						opts.showCallback.apply(me, arguments);						
					},
					error: function(){
						$.ProMainLoading.hide();
						opts.onError.apply(this, arguments);
					}				
				});				
			},
			show = function(){
				var typefix = page_type == 'side' ? 'sider' : 'main';
			
				$('#' + typefix + 'header_' + page_id ).show().siblings().hide();
				$('#' + page_type + 'ContentPage_' + page_id ).show().siblings('.' + page_type + 'ContentPage').hide();
				$('#' + typefix + 'footer_' + page_id ).show().siblings().hide();
				refresh();
			},
			hide = function(){
				var typefix = page_type == 'side' ? 'sider' : 'main';
				
				$('#' + typefix + 'header_' + page_id ).hide();
				$('#' + page_type + 'ContentPage_' + page_id ).hide();
				$('#' + typefix + 'footer_' + page_id ).hide();
				refresh();
			},
			getHeader = function(){
				var typefix = page_type == 'side' ? 'sider' : 'main';
				return $('#' + typefix + 'header_' + page_id );
			};

         
         return page_type == 'main' ? {	
														getOIScroll: getOIScroll,
														setPageId: setPageId,
														init: init,
														getMainData: getMainData,
														getElement: getElement, 
														refresh: refresh,
														show: show,
														hide:	hide,
														getHeader: getHeader,
														destroy: destroy
													} : {	
														getOIScroll: getOIScroll,
														setPageId: setPageId,
														init: init,
														getElement: getElement,
														refresh: refresh,
														show: show,
														hide:	hide,
														getHeader: getHeader,
														destroy: destroy
													}
      } 
});


   
function reback(from,to){
   $("#header_"+from).hide();
   $("#header_"+to).show();
   $("#page_"+from).hide();
   $(".pages").hide();
   $("#page_"+to).show();
   //pageInit(to);
   eval("window.oiScroll = window.oiScroll_" + to);
   pageTo(to);
}

function pageTo(f){
   tiScroll = new $.tiScroll();
   tiScroll.setPageId(f);
}

function sidereback(from,to)
{
	$("#siderheader_"+from).hide();
   $("#siderheader_"+to).show();
   $("#sideContentPage_"+from).hide();
   $(".sideContentPage").hide();
   $("#sideContentPage_"+to).show();
   pageInit("side", to);
   eval("window.oiScroll_side = window.oiScroll_side_" + to);
}

function fixZoomPageAttachSize(page)
{
   var sw = window.screen.width;
   var titlew = $(".read_attach a span:first").width();
   if((titlew + 86 + 20) > sw)
   {
      if($("#page_" +page + " .read_content").width() <= sw )
      {
         $("#page_" +page + " .scroller").css("width",sw + "px");
      }
      $(".read_attach").css("max-width", (sw - 20) + "px");     
   }
}

//2012/7/24 23:24:30 lp                 edit by JinXin @ 2012/9/7
function showMessage(t,context)
{
	context = context || window.document;
	var $message = $("#message", context),
	$text = $('#text',$message);
   $text.empty().text(t);
   $message
		.show()
		.css({"top":13})
		.animate({top: '43'}, 1000, function(){
			setTimeout(function(){
				$message.animate({top: '13'},800,function(){
					$message.css({"top":13}).hide();
				})
			},800);
   });   
}

// 2012/3/26 17:42:31 lp loading效果
$.ProMainLoading = {
   show:function(msg){
      var $$msg = $("#main-ui-loader h1");
      org_msg = $$msg.text();
      if(msg != "")
         $$msg.text(msg);
      $("#mainoverlay").show();
      $("#main-ui-loader").show();       
   },
   hide:function(){
      $("#main-ui-loader h1").empty().text(td_lang.pda.msg_2);
      $("#mainoverlay").hide();
      $("#main-ui-loader").hide();   
   }   
};
$.mutiMenu = {
   init:function(menu){
      var $$mitiMenu = $(".mutiMenuLayer");
      var $$opts = $$mitiMenu.find(".opts");
      $$opts.empty().append(menu);      
   },
   show:function(){
      $("#overlay").addClass("overlayGray").fadeIn("fast");
      $(".mutiMenuLayer").show();
   },
   hide:function(){
      $("#overlay").removeClass("overlayGray").hide();
      $(".mutiMenuLayer").hide();  
   }
}

$("#overlay").live("click",function(e){
   e.stopPropagation();
   if($(this).hasClass("overlayGray"))
   {
      $.mutiMenu.hide();      
   }  
});
  
//lp 扩展搜索
$.extend({
   tSearch: function (options) 
   {
      var url = '/pda/inc/get_contactlist.php';              
      var input = options.input;
      var list = options.list;
      var appendDom = options.appendDom;
      
      var $$input = $(input);
      var $$appendDom = $(appendDom);
      var $$list = $(list);
      var posFix = options.posFix || function(o){	o.top -= 10;	 return o; };
      var _tmp_key;
      var searchInterval = null;
      var searchHtml = '';
      searchHtml  = '<div id="wrapper_plist" class="wrapper wrapper_contact hasshadow" style="display:none;">';
      searchHtml +=    '<div id="scroller_plist" class="scroller">';
      searchHtml +=       '<ul class="comm-list contact-list"></ul>';
      searchHtml +=    '</div>';
      searchHtml += '</div>';
      
      function init()
      {
         $$input.focus(function(e){
            e.stopPropagation(); 
            searchInterval = null;
            searchInterval = setInterval(search_name,1000);
            $(this).addClass("autoInputWidth");
         });
         
         $$input.blur(function(){
            $(this).removeClass("autoInputWidth");   
         });
         
         $$input.keydown(function(event){
            var keyCode = event.which;
            if(keyCode == 8)
            {
               if($(this).val() == "")
               {
                  var oem = $$appendDom.find("em");
                  if(oem.length >= 0)
                  {
                     var lastem = $$appendDom.find("em:last");
                     if(!lastem.hasClass("active"))
                        lastem.addClass("active");
                     else
                        lastem.remove();   
                  }         
               }   
            }
         });
      }
      
      function search_name()
      {
         var key = $$input.val();
         var is_mail_m = $$input.parent(".read_detail");
         
         if(key!="")
         {
            if(key!=_tmp_key)
            {
               _tmp_key = key;

               $.get(url,{"KWORD":key,"P":p},function(data){
                  if(data=="") return;
                  $$list.html(searchHtml).find("ul.contact-list").empty().append(data);
                  
                  //点击查询数据，添加联系人
                  oli = $$list.find(".contact-list li");
                  oli.live("click",function()
                  {
                     var _oSelect_name = $(this).attr("q_name");
                     var _oSelect_uid = $(this).attr("q_id");
                     var _oSelect_user_id = $(this).attr("q_user_id");
                     var _selected = false;
                     
                     if($$appendDom.html()!="")
                     {
                        $$appendDom.find("em").each(function()
                        {
                           var uid = $(this).attr("uid");
                           if(_oSelect_uid == uid){
                              _selected = true;
                              return false;
                           } 
                        });      
                     }
                     
                     if(!_selected)
                     {
                        $$appendDom.append("<em uid='"+_oSelect_uid+"' userid='"+_oSelect_user_id+"'>" + _oSelect_name +"</em>");
                     }
                     $$input.val("");
                     clearInterval(searchInterval);
                     searchInterval = null;
                     is_mail_m.removeClass("hasnoborder");
                     $$input.blur();
                     $$list.empty();
                     return;
                  });
                
                  if(is_mail_m.length > 0)
                     is_mail_m.addClass("hasnoborder");
                     
                  var offset = $$input.offset();
			
                  $$list.find("#wrapper_plist").css("top",posFix(offset).top).show();
                  var Scroll_plist = new iScroll($$list.find("#wrapper_plist").get(0));
               });
            } 
         }else{
            _tmp_key = key;
            
            if(Scroll_plist)
               Scroll_plist.destroy();
            $$list.find("#wrapper_plist").hide();
            is_mail_m.removeClass("hasnoborder"); 
            $$list.empty();   
         }   
      }
      return{
         init: init    
      }
   }
});
   
$.extend({
   tSearch2: function (options) 
   {
      var url = 'inc/get_contactlist.php';              
      var input = options.input;
      var list = options.list;
      var page_id = options.page_id;
		var onSuccess = options.onSuccess || function(){		pageInit(page_id);		};

      
      var $$input = $(input);
      var $$list = $(list);
      
      var _tmp_key;
      var searchInterval = null;

		console.log( $$input, $$list);
		
      function init()
      {
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
      }
      
      function search_name()
      {
         var key = $$input.val();
         
         if(key!="")
         {
            if(key!=_tmp_key)
            {
               _tmp_key = key;
               $.ajax({
                  type: 'GET',
                  url: url,
                  cache: false,
                  data: {"KWORD":key,"P":p},
                  beforeSend: function(){
                   //  $.ProLoading.show();   
                  },
                  success: function(data){
                     //$.ProLoading.hide();
                     if(data==""){
                        $$list.empty();   
                     }else{
                        $$list.empty().append(data);
                     }
                     //点击查询数据，添加联系人
                     oli = $$list.find("li");
                     oli.live("click",function()
                     {
                        var _oSelect_name = $(this).attr("q_name");
                        var _oSelect_uid = $(this).attr("q_id");
                        var _oSelect_user_id = $(this).attr("q_user_id");
                        return;
                     });
                     onSuccess.apply(this, arguments);
							
                  }
               });
            }
            return;
         }else{
            _tmp_key = key;
            $$list.empty();   
         }   
      }
      return{
         init: init    
      }
   }
});

//2012/4/10 16:04:40 lp 0409 IOS客户端打开附件
function readAttach(obj,from_page)
{
   //alert(P_VER);
   //默认获取当前连接的顶级pages
   // if(!from_page){
      // var oPid = obj.parents(".pages").attr("id");
      // if(oPid!="")
         // from_page = oPid.substr(5);
   // }
  // alert(P_VER);
   //兼容附件在不同模块从不同页面返回的Page_id记录
   if(typeof(g_pre_page) != "undefined")
      g_pre_page = from_page;  
   //IOS客户端打开
   if(P_VER == 5)
   {
      //如果为新版本的 0409
    
         var is_image = obj.attr("is_image");
         var url="message:" + is_image + ":" +obj.attr("_href");
         document.location = url;   
         return false;
     
   }else{
      //IOS浏览器中打开
      if(isIDevice){
         $.ProLoading.show(td_lang.pda.msg_9);
         browserView(obj);
         $("#header_"+from_page).hide();
         $("#header_attach_read").show();         
      }
   }
}

function sprintf()
{
    var arg = arguments,
        str = arg[0] || '',
        i, n;
    for (i = 1, n = arg.length; i < n; i++) {
        str = str.replace(/%s/, arg[i]);
    }
    return str;
}
function getCookie(name)
{
	 var arr = document.cookie.split("; ");
	 for(i=0;i<arr.length;i++)
		 if (arr[i].split("=")[0] == name)
			return unescape(arr[i].split("=")[1]);
	 return null;
}
function setCookie(name,value, paras) {
   var today = new Date();
   var expires = new Date();
   expires.setTime(today.getTime() + 1000*60*60*24*2000);
   
   var path = null;
   if(typeof(paras) == "object")
   {
      if(typeof(paras.expires) != "undefined")
         expires = paras.expires;
      if(typeof(paras.path) != "undefined")
         path = paras.path;
   }
   value === '' && expires.setTime(today.getTime() -10000); //传空值删除cookie
   document.cookie = name + "=" + escape(value) + "; expires=" + expires.toGMTString() + (path ? '; path=' + path : '');

}
function echoCookie(){
   var c = unescape(getCookie("city_cookie"));
   c = (c != '' && c != 'null' )? c :"　";
   if(c != ''){
      return c;
   }
}
