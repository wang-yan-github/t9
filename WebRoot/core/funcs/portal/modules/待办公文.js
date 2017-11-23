{
  tbar: [{
    id: 'close'
  }, {
    id: 'more',
    preventDefault: true,
    handler: function(e, t, p) {
      openUrl({
        text: "待办公文",
        url: contextPath + "/core/funcs/doc/flowrun/list/index.jsp?sortName=%2525E5%25258F%252591%2525E6%252596%252587"
      });
    }
  }],
  width: "500px",
  layout: "cardlayout",
  layoutCfg: {
    tabs: true,
    fit: false
  },
  "items":[{
    xtype: "grid",
    title: '待办发文',
    tabBtn: {
      btnText: '待办发文',
      xtype: 'button',
      normalCls: 'tab-normal',
      activeCls: 'tab-active'
    },
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
         if (!e.sortId) {
           e.sortId = "";
         }
         var par = "runId=" + e.runId + "&flowId=" + e.flowId + "&prcsId=" + e.prcsId + "&flowPrcs=" + e.flowPrcs + "&sortId=" + e.sortId ;
         //top.dispParts(contextPath + "/core/funcs/doc/flowrun/list/inputform/index.jsp?" + par, 0);
         var URL = contextPath + "/core/funcs/doc/flowrun/list/inputform/index.jsp?" + par;
         window.open(URL, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
      });

      var title = $('<span class="title"></span>').html(e.title);
      var nowUser = $('<span class="title"></span>').html(e.nowUser);
      
      return a.append(title).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(nowUser);
    }
  }, {
    title: '在办发文',
    tabBtn: {
      btnText: '在办发文',
      xtype: 'button',
      normalCls: 'tab-normal',
      activeCls: 'tab-active'
    },
    xtype: "grid",
    loader: {
      dataRender: function(data) {
        if (data.rtState == '0' && data.rtData) {
          return data.rtData.listData;
        }
        },
        url: contextPath + "/t9/core/funcs/doc/act/T9MyWorkAct/getMyWorkList.act?showLength=10&pageIndex=1&typeStr=3&isDesk=1"
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
         formView(e.runId , e.flowId );
      });
      var title = $('<span class="title"></span>').html(e.title);
      var nowUser = $('<span class="title"></span>').html(e.nowUser);
      var prcsNameNext = $('<span class="title"></span>').html(e.prcsNameNext);
      return a.append(title).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(nowUser).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(prcsNameNext);
    }
  }, {
    title: '待办收文',
    tabBtn: {
      btnText: '待办收文',
      xtype: 'button',
      normalCls: 'tab-normal',
      activeCls: 'tab-active'
    },
    xtype: "grid",
    loader: {
      dataRender: function(data) {
        if (data.rtState == '0' && data.rtData) {
          return data.rtData.listData;
        }
        },
        url: contextPath + "/t9/core/funcs/doc/flowrunRec/act/T9MyWorkAct/getMyWorkList.act?showLength=10&pageIndex=1&flowId=&typeStr=6&isDesk=1"
      },
      rowRender: function(i, e) {
        var status = "";
        var prcsFlag = e.prcsFlag;
        var img = $('<img class="icon"></img>');
        if (prcsFlag ==  '1') {
          img.attr("src", imgPath + "/email_close.gif");
          img.attr("alt", '未接收');
        } else if ( prcsFlag == '2' ) {
          img.attr("src", imgPath + "/email_open.gif");
          img.attr("alt", '已接收');
        } 
        var a = $('<a href="javascript: void(0)"></a>');
        var title = $('<span class="title"></span>');
        title.append("[" + e.flowName + "]");
        a.append(title);
        
        var tmp = e.runName+'  '+e.prcsName;
        var content = $('<span class="title"></span>');
        content.append(tmp);
        var ac = $('<a href="javascript: void(0)"></a>');
        ac.append("&nbsp;&nbsp;");
        ac.append(img);
        ac.append(content);
        
        ac.click(function() {
          //top.dispParts(contextPath + '/core/funcs/doc/flowrunRec/list/inputform/index.jsp?skin=receive&runId=' + e.runId + '&flowId=' + e.flowId + '&prcsId=' + e.prcsId + '&flowPrcs=' + e.flowPrcs, 0);
          var URL = contextPath + '/core/funcs/doc/flowrunRec/list/inputform/index.jsp?skin=receive&runId=' + e.runId + '&flowId=' + e.flowId + '&prcsId=' + e.prcsId + '&flowPrcs=' + e.flowPrcs;
          window.open(URL, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
        });
        
        return $('<div></div>').append(a).append(ac);
      }
  }, {
    title: '在办收文',
    tabBtn: {
      btnText: '在办收文',
      xtype: 'button',
      normalCls: 'tab-normal',
      activeCls: 'tab-active'
    },
    xtype: "grid",
    loader: {
      dataRender: function(data) {
        if (data.rtState == '0' && data.rtData) {
          return data.rtData.listData;
        }
        },
        url: contextPath + "/t9/core/funcs/doc/flowrunRec/act/T9MyWorkAct/getMyWorkList.act?showLength=10&pageIndex=1&typeStr=3&isDesk=1"
      },
      rowRender: function(i, e) {
        var status = "";
        var prcsFlag = e.prcsFlag;
        
        var img = $('<img class="icon"></img>');
        img.attr("src", imgPath + "/flow_next.gif");
        img.attr("alt", '已办理');
        var a = $('<a href="javascript: void(0)"></a>');
        var title = $('<span class="title"></span>');
        title.append("[" + e.flowName + "]");
        a.append(title);
        
        var tmp = e.runName+'  '+e.prcsName;
        var content = $('<span class="title"></span>');
        content.append(tmp);
        var ac = $('<a href="javascript: void(0)"></a>');
        ac.append("&nbsp;&nbsp;");
        ac.append(img);
        ac.append(content);
        
        ac.click(function() {
            var url = contextPath + "/core/funcs/doc/flowrunRec/list/print/index.jsp?runId=" + e.runId + "&flowId=" + e.flowId;
            window.open(url ,"","status=0,toolbar=no,menubar=no,width="+(screen.availWidth-12)+",height="+(screen.availHeight-38)+",location=no,scrollbars=yes,resizable=yes,left=0,top=0");
        });
        
        return $('<div></div>').append(a).append(ac);
      }
  }, {
    title: '未签收文',
    tabBtn: {
      btnText: '未签收文',
      xtype: 'button',
      normalCls: 'tab-normal',
      activeCls: 'tab-active'
    },
    xtype: "grid",
    loader: {
      dataRender: function(data) {
        if (data.rtState == '0' && data.rtData) {
          return data.rtData.listData;
        }
        },
        url: contextPath + "/t9/core/funcs/doc/receive/act/T9DocSignAct/getRegListDesktop.act?showLength=10&pageIndex=0"
      },
      rowRender: function(i, e) {
        var title1 = e.title;
        var sendDocNo = e.sendDocNo;
        var sendUnit = e.sendUnit;
        var seqId = e.seqId;
        var a = $('<a href="javascript: void(0)"></a>');
        var title = $('<span class="title"></span>');
        title.append( sendDocNo);
        a.append(title);
        
        var content = $('<span class="title"></span>');
        content.append( title1);
        var ac = $('<a href="javascript: void(0)"></a>');
        ac.append("&nbsp;&nbsp;");
        ac.append(content);
        
        var content2 = $('<span class="title"></span>');
        content2.append(sendUnit);
        var ac2 = $('<a href="javascript: void(0)"></a>');
        ac2.append("&nbsp;&nbsp;");
        ac2.append(content2);
        
        a.click(function() {
            var url = contextPath + "/core/funcs/doc/receive/sign/signInfo.jsp?seqId=" + e.seqId;
            //top.dispParts(url);
            window.open(url, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
        });
        
        ac.click(function() {
          var url = contextPath + "/core/funcs/doc/receive/sign/signInfo.jsp?seqId=" + e.seqId;
          //top.dispParts(url);
          window.open(url, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
      });
        ac2.click(function() {
          var url = contextPath + "/core/funcs/doc/receive/sign/signInfo.jsp?seqId=" + e.seqId;
          //top.dispParts(url);
          window.open(url, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
      });
        return $('<div></div>').append(a).append(ac).append(ac2);
      }
  }],
  "xtype": "panel",
  height: "auto",
  "cmpCls": "jq-window",
  "title": "待办公文"
}