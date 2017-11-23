window.onload=function()
{
   var menu=document.getElementById("navMenu");
   if(!menu) return;
   
   var imgNum = 0;
   var closeable = false;
   
   var spanObj = menu.getElementsByTagName("SPAN");
   if(spanObj){
      spanNum = spanObj.length;
      for(var i=0; i< spanNum;i++)
      {
         if(spanObj[i].className == "close")
            closeable = true;continue;      
      }   
   }
   
   var active_count = 0;
   for(var i=0; i<menu.childNodes.length;i++)
   {
     
      if(menu.childNodes[i].tagName!="A")
         continue;
         
      if(menu.childNodes[i].className == "active")
         active_count++;

      menu.childNodes[i].onclick=function()
      {
         var menu=document.getElementById("navMenu");
         for(var i=0; i<menu.childNodes.length;i++)
         {
            if(menu.childNodes[i].tagName!="A")
               continue;
            menu.childNodes[i].className="";
         }
         this.className="active";
         
         if(closeable){
            parent.document.getElementById("nav_main").setAttribute("index",this.href);
         }
      }
   }
   
   if(active_count == 0 && menu.childNodes.length > 0)
      menu.childNodes[0].className = "active";
   
   if(closeable){

      for(var i=0; i< menu.childNodes.length;i++)
      {
         
         if(menu.childNodes[i].nodeName=="#text")
            continue;
         
         menu.childNodes[i].onmouseover=function(){
            var spanObj = this.getElementsByTagName("SPAN");
            for(var j=0; j< spanObj.length;j++)
            {
               if(spanObj[j].className == "close"){
                  spanObj[j].className = "close over";
               }   
            }
         }
         
         menu.childNodes[i].onmouseout=function(){
            var spanObj = this.getElementsByTagName("SPAN");
            for(var j=0; j< spanObj.length;j++)
            {
               if(spanObj[j].className.indexOf("close")!=-1)
                  spanObj[j].className = "close";        
            }
         }
         
         var spanObj = menu.childNodes[i].getElementsByTagName("SPAN");
         for(var j=0; j< spanObj.length;j++)
         {
            
            if(spanObj[j].nodeName=="#text") 
               continue;

            if(spanObj[j].className == "close")
            {
               spanObj[j].onclick = function(e)
               {
                  
                  e  = e || window.event;
                  var thisobj = this.parentNode.parentNode;
                  var pnode = nnode = [];
                  var hasRemoveAblepnode = hasRemoveAblennode = false;

                  if(thisobj.nodeName == "A"){
                     var nnode = thisobj.nextSibling;
                     if(nnode.nodeName == "#text")
                        thisobj.parentNode.removeChild(nnode);
                  }
                  
                  if(thisobj.nextSibling && thisobj.nextSibling.nodeName == "A"){
                     hasRemoveAblennode = true;
                     nnode = thisobj.nextSibling;
                  }
                  
                  if(thisobj.previousSibling && thisobj.previousSibling.nodeName == "#text"){
                     if(thisobj.previousSibling.previousSibling){
                        hasRemoveAblepnode = true;   
                     }else{
                        hasRemoveAblepnode = false;   
                     }
                     pnode = thisobj.previousSibling.previousSibling;   
                  }
                  
                  if(hasRemoveAblennode)
                  {
                     if(thisobj.className == "active"){
                        nnode.className = "active";
                        parent.document.getElementById("nav_main").setAttribute("index",nnode.href);
                        parent.frames['nav_main'].location = nnode.href;
                     }         
                  }else if(hasRemoveAblepnode){
                     if(thisobj.className == "active"){
                        pnode.className = "active";
                        parent.document.getElementById("nav_main").setAttribute("index",pnode.href);
                        parent.frames['nav_main'].location = pnode.href;
                     }   
                  }else{
                     window.top.close();     
                  }
                  
                  thisobj.parentNode.removeChild(thisobj);
                  
                  return false;
               }
            }
         }
      } 
   }
   
   var navScroll = document.getElementById("navScroll");
   if(navScroll)
   {
      navScroll.onclick = function()
      {
         if(menu.scrollTop + menu.clientHeight >= menu.scrollHeight || menu.scrollTop + menu.clientHeight*2 > menu.scrollHeight)
            menu.scrollTop = 0;
         else{
            menu.scrollTop += menu.clientHeight;
            //menu.scrollTop += 30;
         }
      }
      
      var panel = document.getElementById("navPanel");
      panel.onmouseover = function()
      {
         if(menu.scrollHeight >= menu.clientHeight*2)
            navScroll.style.display = '';
      }
      panel.onmouseout  = function()
      {
         navScroll.style.display = 'none';
      }
   }
   
   onresize();
};

window.onresize = function()
{
   var navScrollWidth;
   var navScroll = document.getElementById("navScroll");
   if(navScroll)
   {
   	navScrollWidth = navScroll.clientWidth == 0 ? 26 : navScroll.clientWidth;
      var panel = document.getElementById("navPanel");
      var menu=document.getElementById("navMenu");
      if((navScrollWidth + menu.clientWidth) > panel.clientWidth)
         menu.style.width = panel.clientWidth - navScroll.clientWidth - 100 + "px";
   }
}