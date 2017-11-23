{
  cls: "window-height",
  width: "500px",
  tbar: [{
    id: 'close'
  }, {
    id: 'more',
    preventDefault: true,
    handler: function(e, t, p) {
      openUrl({
        text: "图片新闻",
        url: contextPath + "/core/funcs/news/show/index.jsp"
      });
    }
  }],
  "items":[{
   "xtype":"imgbox",
   "loader":{"url":"/t9/t9/core/funcs/news/act/T9ImgNewsAct/leatImagNews.act",
      "param":{"publicPath":"d:/test/","type":"1"
    },
    "dataRender":
    function (data) {
      if (data.rtState == "0") {
          return data.rtData.records;
      }
  }}}],"xtype":"panel","height":"auto","width":"650px","cmpCls":"jq-window","title":"图片新闻"}