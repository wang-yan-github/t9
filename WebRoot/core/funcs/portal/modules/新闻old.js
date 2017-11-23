{
  cls: "window-height",
  tbar: [{
    id: 'close'
  }, {
    id: 'more',
    preventDefault: true,
    handler: function(e, t, p) {
      openUrl({
        text: "新闻",
        url: contextPath + "/core/funcs/news/show/index.jsp"
      });
    }
  }],
  width: "500px",
  "items":[{
    xtype: "grid",
    loader: {
      url: contextPath + "/t9/core/funcs/news/act/T9NewsShowAct/getDeskNewsAllList.act"
    },
    rowRender: function(i, e) {
      function deskNotify(seqId){
        var img = document.getElementById("img_"+seqId+"");
        if(img){
          document.getElementById("img_"+seqId+"").style.display="none";
        }
        var URL = contextPath + "/core/funcs/news/show/readNews.jsp?seqId="+seqId;
        myleft=(screen.availWidth-500)/2;
        window.open(URL,"deskNews","height=600,width=600,status=1,toolbar=no,menubar=no,location=no,scrollbars=yes,top=100,left="+myleft+",resizable=yes");
      }
      var a = $("<a href='javascript:void(0)'></a>");
       a.click(function() {
        deskNotify(e.seqId)
      });
       
      var title = $('<span class="title"></span>').html(e.subject);
      var desc = $('<span class="desc"></span>').html("(评论: " + e.commentCount + ")");
      var time = $('<span class="time"></span>').html(e.newsTIme);
      var icon;
      if (e.iread == "no") {
        icon = $('<img class="icon"></img>');
        icon.attr("src", imgPath + "/email_new.gif");
      }
      
      return a.append(icon).append(title).append(desc).append(time);
    }
  }],
  "xtype": "panel",
  height: "auto",
  "cmpCls": "jq-window",
  "title": "新闻"
}
       