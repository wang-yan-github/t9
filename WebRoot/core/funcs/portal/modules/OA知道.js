{
  tbar: [{
    id: 'close'
  }, {
    id: 'more',
    preventDefault: true,
    handler: function(e, t, p) {
      openUrl({
        text: "OA知道",
        url: contextPath + "/t9/core/oaknow/act/T9OAKnowAct/OAKnowIndex.act"
      });
    }
  }],
  width: "500px",
  "items":[{
    xtype: "grid",
    loader: {
      url: contextPath + "/t9/core/oaknow/act/T9OAKnowAct/ajaxOaDesk.act",
      dataRender: function(data) {
        return data.rtData;
      }
    },
    rowRender: function(i, e) {
      var a = $('<a href="javascript: void(0)"></a>');
      var a2 = $('<a href="javascript: void(0)"></a>');
      
      a.click(function() {
        //top.dispParts(contextPath + "/t9/core/oaknow/act/T9OAKnowTypeAct/findType.act?showFlag=CONTENT&typeId=" + e.typeId + "&parentId=" + e.pid, 1);
        var URL = contextPath + "/t9/core/oaknow/act/T9OAKnowTypeAct/findType.act?showFlag=CONTENT&typeId=" + e.typeId + "&parentId=" + e.pid;
        window.open(URL, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
      });
      
      a2.click(function() {
        //top.dispParts(contextPath + "/t9/core/oaknow/act/T9OAAskReferenceAct/findAskRef.act?showFlag=CONTENT&askId=" + e.seqId, 1);
        var URL = contextPath + "/t9/core/oaknow/act/T9OAAskReferenceAct/findAskRef.act?showFlag=CONTENT&askId=" + e.seqId;
        window.open(URL, "", "height=800,width=800,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=400 resizable=yes");
      });
      var title = $('<span class="title"></span>');
      title.append("[" + e.categoryName + "]");
      
      var title2 = $('<span class="title"></span>');
      title2.append(e.ask);
      a.append(title);
      a2.append(title2);
      
      var div = $('<div></div>');
      div.append(a).append(a2);
      if (e.status == '1') {
        div.append("<font color='green'>&nbsp;已解决</font>");
      } else {
        div.append("<font color='red'>&nbsp;未解决</font>");
      }
      return div;
    }
  }],
  "xtype": "panel",
  height: "auto",
  cls: "window-height",
  "cmpCls": "jq-window",
  "title": "OA知道"
}