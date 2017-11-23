{
  cls: "window-height",
  tbar: [{
    id: 'close'
  }, {
    id: 'more',
    preventDefault: true,
    handler: function(e, t, p) {
      openUrl({
        text: "待办发文",
        url: contextPath + "/core/funcs/doc/flowrun/list/index.jsp?type=1"
      });
    }
  }],
  width: "500px",
  "items":[{
    xtype: "grid",
    loader: {
	  dataRender: function(data) {
	    if (data.rtState == '0' && data.rtData) {
	      return data.rtData.listData;
	    }
  	  },
      url: contextPath + "/t9/core/funcs/doc/act/T9MyWorkAct/getMyWorkList.act?showLength=10&pageIndex=1&typeStr=6&isDesk=1"
    },
    rowRender: function(i, e) {
    	
      /**
       * 打印表单
       * @param runId
       * @param flowId
       * @return
       */
   	  function formView(runId , flowId) {
   	    var url = contextPath + "/core/funcs/doc/flowrun/list/print/index.jsp?runId="+runId+"&flowId="+flowId;
   	    window.open(url ,"","status=0,toolbar=no,menubar=no,width="+(screen.availWidth-12)+",height="+(screen.availHeight-38)+",location=no,scrollbars=yes,resizable=yes,left=0,top=0");
   	  }
   	  
      var a = $("<a href='javascript:void(0)'></a>");
       a.click(function() {
    	   var par = "runId=" + e.runId + "&flowId=" + e.flowId + "&prcsId=" + e.prcsId + "&flowPrcs=" + e.flowPrcs + "&sortId=" + e.sortId ;
    	   //top.dispParts("/t9/core/funcs/doc/flowrun/list/inputform/index.jsp?" + par, 1);
    	   var URL = "/t9/core/funcs/doc/flowrun/list/inputform/index.jsp?" + par;
    	   window.open(URL, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
      });

      var title = $('<span class="title"></span>').html(e.title);
      var nowUser = $('<span class="title"></span>').html(e.nowUser);
      
      return a.append(title).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(nowUser);
    }
  }],
  "xtype": "panel",
  height: "auto",
  "cmpCls": "jq-window",
  "title": "待办发文"
}
