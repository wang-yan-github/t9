$(function (){ 
			var form = $("#dailogForm").Validform();
        	form.config({tiptype:4});
    		//form.tipmsg.s="非空";
    		//form.tipmsg.r=" ";
        	$("#formSubmit").bind("click",function(){
        		var flag = form.check();
        		if(flag){
        			if(confirm("确认提交吗？")){
						ajaxdoFormSubmit('dailogForm');
            		}
    			}else{
					alert("信息校验没有通过，请检查填写的信息");
				}
        	});
});