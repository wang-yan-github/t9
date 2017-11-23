{
  tbar: [{
    id: 'close'
  }, {
    id: 'more',
    preventDefault: true,
    handler: function(e, t, p) {
      openUrl({
        text: '#title#',
        url: contextPath + "/core/funcs/deptnotify/show/index.jsp?portName=" + '#portName#'
      });
    }
  }],
  width: "500px",
  getIts:function(t) {
    var url = contextPath + '/t9/core/funcs/deptnotify/act/T9NotifyHandleAct/getnotifyType.act';
    t.items = [];
    var text = jQuery.ajax({
    type: "GET",
    dataType: "text",
    url: url,
    async : false
    }).responseText;
    var json = T9.parseJson(text);
      if(json.rtState == "0"){        
        var rtData = json.rtData;
        var typeData = rtData.typeData;
        t.items[0] = t.getIt("","");
        if(typeData.length > 0){
          for(var i = 0 ;i < typeData.length ;i ++){
            var optionStr = typeData[i];
            t.items[i+1] = t.getIt(optionStr.typeId,optionStr.typeDesc);
          }
        }
    　　} else {
      t.items[0] = t.getIt("","");
    　　}
  },
  getIt:function(type , desc){
    var obj = {
        xtype: "grid",
        tabBtn: {
          btnText: '全部',
          xtype: 'button',
          normalCls: 'tab-normal',
          activeCls: 'tab-active'
        },
        loader: {
          url: contextPath + "/t9/core/funcs/deptnotify/act/T9NotifyShowAct/getdeskPrivList.act?type=" + type + "&portId=#portId#"
        },
        rowRender: function(i, e) {
          function deskNotify(seqId){
            var img = document.getElementById("img_notify_" + seqId );
            if(img){
              document.getElementById("img_notify_" + seqId).style.display= "none";
            }
            var URL = contextPath + "/core/funcs/deptnotify/show/readNotify.jsp?seqId="+seqId;
            myleft= (screen.availWidth-500) / 2;
            window.open(URL, "deskNotify", "height=600,width=600,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left=" + myleft + ", resizable=yes");    
          }
          var a = $("<a href='javascript:void(0)'></a>");
           a.click(function() {
            deskNotify(e.seqId)
          });
           
          var title = $('<span class="title"></span>').html(e.subject);
          var desc = $('<span class="desc"></span>').html("(访问量: " + e.readerCount + ")");
          var time = $('<span class="time"></span>').html(e.sendTime);
          var icon;
          if (e.iread == "no") {
            icon = $('<img id="img_notify_' + e.seqId +'" class="icon"></img>');
            icon.attr("src", imgPath + "/email_new.gif");
          }
          
          return a.append(icon).append(title).append(desc).append(time);
        }
      };
    if (desc) {
      obj.tabBtn.btnText = desc;
    }
    return obj;
  },
  "xtype": "panel",
  layout: "cardlayout",
  layoutCfg: {
    tabs: true,
    fit: false
  },
  height: "auto",
  "cmpCls": "jq-window",
  "title": '#title#',
  listeners: {
    initComponent: {
      before: function(e, t) {
         t.getIts(t);
      }
    }
  }
}