FCKCommands.RegisterCommand( 'T9Grid', new FCKDialogCommand('T9Grid'
    ,'设置分页表的属性'
    ,FCKPlugins.Items['T9Grid'].Path + 'fck_T9Grid.jsp',500,600
    ));
var oT9GridItem = new FCKToolbarButton( 'T9Grid', '添加分页表' ) ;
oT9GridItem.IconPath = FCKPlugins.Items['T9Grid'].Path + 'T9Grid.jpg' ;
FCKToolbarItems.RegisterItem( 'T9Grid', oT9GridItem ) ;
var FCKT9Grid = new Object() ;
FCKT9Grid.AddT9Grid = function( gridImg ){
  FCK.InsertHtml(gridImg) ;
}
var oDelT9GridCommand = new Object() ;
oDelT9GridCommand.Name = 'DeleteT9Grid' ;
oDelT9GridCommand.Execute = function(){
  if(confirm("确定删除列表吗？")){
    FCK.InsertHtml("") ;
  }
}
oDelT9GridCommand.GetState = function(){
  return FCK_TRISTATE_OFF ;
}
FCKCommands.RegisterCommand( 'DeleteT9Grid', oDelT9GridCommand) ;
var oT9GridWidgetMenuListener = new Object() ;
oT9GridWidgetMenuListener.AddItems = function( contextMenu, tag, tagName ){
if(tagName == 'IMG'
           && tag.getAttribute("widgettype")=='Grid'){
  contextMenu.AddSeparator();
  contextMenu.AddItem( 'T9Grid', '编辑表' ,oT9GridItem.IconPath);
  contextMenu.AddItem( 'DeleteT9Grid' ,'删除表');
  }
}
FCK.ContextMenu.RegisterListener( oT9GridWidgetMenuListener ) ;