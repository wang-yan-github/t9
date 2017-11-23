 /**
  *自己写的分页插件 感觉还可以
  *t9 BBS
  *author:zp
  */
 (function($) {
 
 $.fn.T9Bbsfenyebar = function(options) {
		var settings = $.extend({
			
		}, options || {});

	//初始化變數
	var pagesize = settings.pagesize || 10;//每页显示条数
	var currpage = settings.currpage || 1;//当前页
	var maxpage = settings.maxpage || 1;//最大页数
	var allowmaxpage = settings.allowmaxpage || 10;//最大允许显示页数
	var basepara = settings.basepara || "";//最大页数
	var maxcount = settings.maxcount || 0;//最大记录数
	var url = settings.url || "";//连接地址
	var maxNumpage ;
	var startpage = 1;
	var ntxpage = currpage +1;
	if(currpage >allowmaxpage){
		var cunp = parseInt(currpage/allowmaxpage);
		startpage = cunp* allowmaxpage + 1;

	}
	
	var maxPage = ((maxcount % pagesize) == 0)?(maxcount / pagesize):((maxcount / pagesize) +1)//计算出总的页数
	maxPage = parseInt(maxPage);
	var _divc = $("<div>").addClass("pg").attr("id","pgdiv");
	maxNumpage = startpage + parseInt(allowmaxpage - 1); 
	if(maxNumpage > maxPage){
		maxNumpage = maxPage;
	}


	if(maxcount == 0){
		$(_divc).append($("<span class='pb'  ><a href=''>返&nbsp;回</a></span>"));	
		$(_divc).append(
						$("<span>").html("<strong>1</strong>")
					)
	}else{
		$(_divc).append($("<span class='pb'  ><a href=''>返&nbsp;回</a></span>"));
			for(var i=startpage; i<= maxNumpage;i++){
				
				if(i == currpage){
					$(_divc).append(
						$("<span>").html("<strong>"+i+"</strong>")
					)
				}else{
						$(_divc).append(
							$("<a>").attr("href",url+"?currpage="+i+"&pagesize="+pagesize+"&allowmaxpage"+allowmaxpage+"&"+basepara).text(i)
						);
					}
			}
		
			if(currpage != maxPage){
				$(_divc).append(
					$("<a>").attr("href",url+"?currpage="+ntxpage+"&pagesize="+pagesize+"&allowmaxpage"+allowmaxpage+"&"+basepara).addClass("nxt").text("下一页")
				);
			}
			
			
	}
			
				
			$(_divc).appendTo($(this));
			
		
  }
//自訂function 結束
})(jQuery)