<%@ page language="java"  import="java.util.*,java.text.SimpleDateFormat,t9.core.funcs.person.data.T9Person,t9.core.funcs.calendar.data.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/core/inc/header.jsp" %>
<%
  Date date = new Date();
  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
  SimpleDateFormat dateFormatWeek = new SimpleDateFormat("E");
  SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
  String week = dateFormatWeek.format(date);
  String dateStr = dateFormat.format(date);
  int year = Integer.parseInt(dateStr.substring(0,4));
  int year1 = Integer.parseInt(dateStr.substring(0,4));
  int month = Integer.parseInt(dateStr.substring(5,7));
  int day = Integer.parseInt(dateStr.substring(8,10));
  Calendar time=Calendar.getInstance(); 
  time.clear(); 
  time.set(Calendar.YEAR,year); //year 为 int 
  time.set(Calendar.MONTH,month-1);//注意,Calendar对象默认一月为0           
  int maxDay = time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数 
  List<T9Calendar> calendarList = new ArrayList<T9Calendar>();
  calendarList= (List<T9Calendar>)request.getAttribute("calendarList");
  String status = "0";
  String yearOnly = request.getParameter("yearOnly");
  String yearStr = request.getParameter("year");
  String monthStr = request.getParameter("month");
  if(yearOnly!=null){
    year1 = Integer.parseInt(yearOnly);
  }
  if(yearStr!=null){
    year = Integer.parseInt(yearStr);
  }
  if(monthStr!=null){
    month = Integer.parseInt(monthStr);
  }
  String weekToDate = request.getParameter("date");
  if(weekToDate!=null){
    year = Integer.parseInt(weekToDate.substring(0,4));
    month = Integer.parseInt(weekToDate.substring(5,7));
  }
  String yearMonth = String.valueOf(year) + "-" + String.valueOf(month);
  T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
  int userId = user.getSeqId();
  
  long daySpace = T9Utility.getDaySpan(dateFormat1.parse(yearMonth + "-01"),dateFormat1.parse(yearMonth + "-" +maxDay))+1;
  //得到到之间的天数数组
  List daysList = new ArrayList();
  String days = "";
  Calendar calendar = new GregorianCalendar();
  for(int i = 0;i<daySpace;i++){
    calendar.setTime(dateFormat1.parse(yearMonth + "-01"));
    calendar.add(Calendar.DATE,+i) ;
    Date dateTemp = calendar.getTime();
    String dateTempStr = dateFormat1.format(dateTemp);
    daysList.add(dateTempStr);
    days = days + dateTempStr + ",";
  }
  if(daySpace>0){
    days = days.substring(0,days.length()-1);
  }
  
  //判断月初是第几周
  time.set(year,month-1,1);
  int beginWeekth = time.get(Calendar.WEEK_OF_YEAR);
  //判断这个月1号是星期几

  int beginWeek = time.get(Calendar.DAY_OF_WEEK);
  
  //判断这个月最后一天是星期几

  time.set(year,month-1,maxDay);
  int endWeek = time.get(Calendar.DAY_OF_WEEK);
  //判断这个月最后一天是第几周

  int endWeekth = time.get(Calendar.WEEK_OF_YEAR);
  //如果这个月的最后一天是星期天的话，那么最未周-1
  if(endWeek==1){
    endWeekth = endWeekth-1;
  }
  //如果这个月的第一天是星期天的话，那么起试周-1；

  if(beginWeek==1){
    beginWeekth = beginWeekth-1;
  }
  if(month==12){
    endWeekth =53;
  }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html class="hiddenRoll">
<head>
<title>手机考勤记录查询</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=cssPath%>/style.css">
<link rel="stylesheet" href="<%=cssPath%>/cmp/Calendar.css">
<script type="text/javascript" src="<%=contextPath %>/core/js/prototype.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/tab.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/datastructs.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/sys.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/smartclient.js"></script>
<script type="text/javascript" src="<%=contextPath %>/core/js/cmp/Calendarfy.js"></script>
<script type="text/Javascript" src="<%=contextPath%>/core/js/orgselect.js" ></script>
<script type="text/javascript">
var year = "<%=year%>";
var month = "<%=month%>";
var days = '<%=days%>';
var userId = '<%=userId%>';
var beginWeek = '<%=beginWeek%>';
var beginWeekth = '<%=beginWeekth%>';
var endWeek = '<%=endWeek%>';
var endWeekth = '<%=endWeekth%>';

function doOnload(){
  //getData(userId,days);
  if(beginWeek==1){
    beginWeek = 7;  
  }else{
    beginWeek = beginWeek - 1;
  }
  if(endWeek==1){
    endWeek = 7;  
  }else{
    endWeek = endWeek - 1;
  }
  //画图
  getCalendAttend(endWeekth,beginWeek);
  
  //数据
  var URL = "<%=contextPath%>/t9/core/funcs/attendance/personal/act/T9AttendMobileAct/getUserMobileInfo.act?userId="+userId+"&days="+days;
  var rtJson = getJsonRs(URL);
  var prcs= rtJson.rtData;
  if(prcs.length > 0){
      for(var i = 0; i < prcs.length; i ++){
          var prc = prcs[i];
          var seqId = prc.seqId;
          var time = prc.time;
          var location = prc.location;
          var iTime = "";
          var iDay = "";
          if(time.trim != ""){ //日期
              var d = new Date(time.time);
              iTime = d.format('hh:mm:ss');
              iDay = d.format('dd');
              if(iDay.indexOf("0") == 0){
                  iDay = iDay.substring(1);
              }
          }
          var div = new Element('div').update("<div title='"
                  + "'> <span class='CalLevel' title='" + location + "'>"
                  + "签到时间：" + iTime + "</span> "
                  +"<br>"
                  +"</div>");
          $("td_"+iDay).appendChild(div);
      }
  }
}
<%-- 
//得到手机考勤数据 列表
function getListAttend(userId,days){
    //建表
    var table = new Element('table',{"class":"TableList" , "width":"100%" ,"align":"center","id":"TableList"}).update("<tbody id = 'TableHeader'></tbody>");
    $("list_attend").appendChild(table);
    var tr_title = new Element('tr',{"class":"TableHeader","align":"center","id":"tr_title"});
    $("TableHeader").appendChild(tr_title);
    var th_time = new Element('th',{"align":"center","id":"th_time"});
    tr_title.appendChild(th_time);
    th_time.update("日期");
    var th_localhost = new Element('th',{"align":"center","id":"th_localhost"});
    tr_title.appendChild(th_localhost);
    th_localhost.update("地址");
    var th_attach = new Element('th',{"align":"center","id":"th_attach"});
    tr_title.appendChild(th_attach);
    th_attach.update("附件");
    var th_remark = new Element('th',{"align":"center","id":"th_remark"});
    tr_title.appendChild(th_remark);
    th_remark.update("备注");
    //数据
    var URL = "<%=contextPath%>/t9/core/funcs/attendance/personal/act/T9AttendMobileAct/getUserMobileInfo.act?userId="+userId+"&days="+days;
    var rtJson = getJsonRs(URL);
    if(rtJson.rtState == "1"){
      alert(rtJson.rtMsrg); 
      return ;
    }
    var prcs= rtJson.rtData;
    //console.log(prcs);
    if(prcs.length>0){
        var tbody = new Element('tbody',{"align":"center","id":"tbody"});
        table.appendChild(tbody);
        for(var i = 0; i < prcs.length; i ++){
            //console.log(prcs[i]);
            var prc = prcs[i];
            var time = prc.time;
            var location = prc.location;
            var attachmentName = prc.attachmentName;
            var remark = prc.remark;
            var tr_data = new Element('tr',{"class":"TableData"});
            if(time.trim != ""){ //日期
                var td_time = new Element('td',{"align":"center"});
                tr_data.appendChild(td_time);
                var date = new Date(time.time);
                date = date.format('yyyy-MM-dd hh:mm:ss');
                td_time.update(date);
            }
            if(location.trim != ""){ // 地址
                var td_location = new Element('td',{"align":"center"});
                tr_data.appendChild(td_location);
                td_location.update(location);
            }
            if(attachmentName.trim != ""){ // 附件
                var td_attachmentName = new Element('td',{"align":"center"});
                tr_data.appendChild(td_attachmentName);
                td_attachmentName.update(attachmentName);
            }
            if(remark.trim != ""){ // 备注
                var td_remark = new Element('td',{"align":"center"});
                tr_data.appendChild(td_remark);
                td_remark.update(remark);
            }
            tbody.appendChild(tr_data);
        }
    }
}
 --%>
function getCalendAttend(endWeekth,beginWeek){
  var table = new Element('table',{"id":"cal_table","class":"TableBlock","width":"100%","align":"center"}).update("<tbody id = 'tboday'><tr align='center' class='TableHeader'>"
      +"<td width='6%'><b>周数</b></td>"
      +"<td width='14%'><b>星期一</b></td>"
      +"<td width='14%'><b>星期二</b></td>"
      +"<td width='14%'><b>星期三</b></td>"
      +"<td width='14%'><b>星期四</b></td>"
      +"<td width='14%'><b>星期五</b></td>"
      +"<td width='12%'><b>星期六</b></td>"
      +"<td width='12%'><b>星期日</b></td>"
      +"</tr></tbody>");
  $('cal_table').appendChild(table);
  //跨天的tr
  var tr = new Element('tr',{"id":"spanMonth","class":"TableData","align":"left","style":"display:none"}).update("<td class='TableContent' align='center'>跨天</td>"
    +"<td id='spanMonthCalendar' colspan='7'></td>");
  $('tboday').appendChild(tr);
  var monththInt ;
  for(var i=parseInt(beginWeekth,10);i<=parseInt(endWeekth,10);i++){

    var trStr = "";
    if(i==parseInt(beginWeekth)){
      var tdStr = "<td id='tw_' class='TableContent' align='center' ondblclick='newCalendarWeek("+year+","+month+","+i+")'>第"+i+"周</td>";
      for(var j=1;j<=7;j++){
        if(j>=parseInt(beginWeek,10)){
          monththInt = (j-parseInt(beginWeek,10)+1);
          tdStr =  tdStr+"<td id='td_"+monththInt+"' valign='top' ondblclick='newCalendar("+year+","+month+","+monththInt+");'><div align='right' onclick='toDay("+year+","+month+","+monththInt+");' title='' style='cursor:pointer;width: 100%;'><font color='blue'  align='right'><b id='"+monththInt+"'>"+monththInt+"</b></font></div></td>"
        }else{
          tdStr =  tdStr+"<td id='td_' align='top'></td>"
         }
      }
      trStr = trStr + "<tr class='TableData' height='80'>" + tdStr + "</tr>";
    }else if(i==parseInt(endWeekth)){
      var tdStr = "<td id='tw_' class='TableContent' align='center' ondblclick='newCalendarWeek("+year+","+month+","+i+")'>第"+i+"周</td>";
      for(var j=1;j<=7;j++){
        if(j<=parseInt(endWeek,10)){
          monththInt = monththInt+1;
          tdStr =  tdStr+"<td id='td_"+monththInt+"' valign='top' ondblclick='newCalendar("+year+","+month+","+monththInt+");'><div align='right' onclick='toDay("+year+","+month+","+monththInt+");' title='' style='cursor:pointer;width: 100%;'><font color='blue'  align='right'><b id='"+monththInt+"'>"+monththInt+"</b></font></div></td>"
        }else{
          tdStr =  tdStr+"<td id='td_' valign='top'></td>"
        }
      }
      trStr = trStr + "<tr class='TableData' height='80'>" + tdStr + "</tr>";
    }else{
      var tdStr = "<td id='tw_' class='TableContent' align='center' ondblclick='newCalendarWeek("+year+","+month+","+i+")'>第"+i+"周</td>";
          for(var j=1;j<=7;j++){
           monththInt = monththInt+1;
           tdStr =  tdStr+"<td id='td_"+monththInt+"' valign='top' ondblclick='newCalendar("+year+","+month+","+monththInt+");'><div align='right' onclick='toDay("+year+","+month+","+monththInt+");' title='' style='cursor:pointer;width: 100%;'><font color='blue'  align='right'><b id='"+monththInt+"'>"+monththInt+"</b></font></div></td>"
          }
       trStr = trStr + "<tr class='TableData' height='80'>" + tdStr + "</tr>";
    }
    trStr =  $('tboday').innerHTML + trStr ;
    $('tboday').update(trStr);
  } 
  //是当天的TD加颜色，如果当天的（31日）大于指定月的最大天数 ，则默认为最后一天 
  var curDate = new Date();
  var day = curDate.getDate();
  var maxDay = '<%=maxDay%>';
  if(day>maxday){
    day = maxday;
  }
  $("td_"+day).className = "TableRed";
  $("td_"+day).getElementsByTagName("div")[0].className = "TableRed";
}
//判断是否是节假日
function holiday(date){
  var userId = '<%=userId%>';
  var requestURLHoliday = "<%=contextPath%>/t9/core/funcs/attendance/manage/act/T9ManageAttendAct/checkHoliday.act?date="+date;
  var rtJson = getJsonRs(requestURLHoliday);
  var holidayJson = rtJson.rtData;
  var holiday = holidayJson.status;//0为公假日
  if(holiday=="1"){
    return false;
  }
  return true;
}

function My_Submit(){
  var year = document.getElementById("year").value;
  var month = document.getElementById("month").value;
  window.location="<%=contextPath%>/core/funcs/attendance/personal/selectAttendMobile.jsp?year="+year+"&month="+month;
}

function set_month(index){
  var year = document.getElementById("year").value;
  var month = document.getElementById("month").value;
  if(parseInt(month,10)+index<=0){
    year = parseInt(year)-1;
    month = 12;
  }else if(parseInt(month,10)+index>12){
    year = parseInt(year)+1;
    month = 1;
  }else{
    month = parseInt(month,10)+index;
  }
  window.location="<%=contextPath%>/core/funcs/attendance/personal/selectAttendMobile.jsp?year="+year+"&month="+month;
}

function set_year(index){
  var year = document.getElementById("year").value;
  var month = document.getElementById("month").value;
  if(parseInt(year)<=2000){
    year = parseInt(year);
  }else if(parseInt(year)>=2049){
    year = parseInt(year);
  }else{
    year = parseInt(year)+parseInt(index);
  }
  window.location="<%=contextPath%>/core/funcs/attendance/personal/selectAttendMobile.jsp?year="+year+"&month="+month;
}
function remark(seqId){
  var URL="/t9/core/funcs/attendance/personal/dutyRemark.jsp?seqId=" + seqId ;
  myleft=(screen.availWidth-650)/2;
  window.open(URL,"formul_edit","height=250,width=450,status=0,toolbar=no,menubar=no,location=no,scrollbars=no,top=150,left="+myleft+",resizable=yes");
}
</script>
</head>
<body class="" topmargin="5" onload="doOnload();">
<table id="selectTable" border="0" width="100%" cellspacing="0" cellpadding="3" class="small">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"><span class="big3"> &nbsp;手机考勤记录查询</span><br>
    </td>
  </tr>
</table>
<table id="resultTable" border="0" width="100%" cellspacing="0" cellpadding="3" class="small" style="display:none">
  <tr>
    <td class="Big"><img src="<%=imgPath%>/views/attendance.gif" WIDTH="22" HEIGHT="20" align="absmiddle"/><span class="big3">&nbsp;上下班查询结果 [<span id="returndate1"></span> 至<span id="returndate2"></span> 共<span id="daySpace"></span>天]</span><br>
    </td>
  </tr>
</table>
<br>
<a href="javascript:set_year(-1)";  title="上一年"><img  src="<%=imgPath%>/previouspage.gif"></img></a>
   <select id="year" name="year" style="height:22px;FONT-SIZE: 11pt;" onchange="My_Submit();">
     <%
       for(int i = 2000; i < 2050; i++){
         if(i == year){
     %>
     <option value="<%=i %>" selected="selected"><%=i %>年</option>
       <%}else{ %>
     <option value="<%=i %>"><%=i %>年</option>
       <%
           }
        }
       %>
   </select><a href="javascript:set_year(1);" class="ArrowButtonR" title="下一年"><img src="<%=imgPath%>/nextpage.gif"></img></a>
<!-- 月  -->
   <a href="javascript:set_month(-1);" class="ArrowButtonL" title="上一月"><img src="<%=imgPath%>/previouspage.gif"></img></a><select id="month"  style="height:22px;FONT-SIZE: 11pt;"  name="month" onchange="My_Submit();">
     <%
       for(int i = 1; i < 13; i++){
         if(i >= 10){
          if(i == month){
     %>
     <option value="<%=i %>" selected="selected"><%=i %>月</option>
        <%}else{ %>
     <option value="<%=i %>"><%=i %>月</option>
       <%
          }    
        }else{
          if(i == month){
       %>
       <option value="0<%=i %>" selected="selected">0<%=i %>月</option>
        <%}else{ %>
     <option value="0<%=i %>">0<%=i %>月</option>
       <%
        }
      }
    }
       %>
   </select><a href="javascript:set_month(1);" class="ArrowButtonR" title="下一月"><img src="<%=imgPath%>/nextpage.gif"></img></a><span class="big3"></span>
<br>
<br>
<div id="list_table" align="center" style="display:inline;"></div>
<br>
<div id="cal_table" align="center" style="display:inline;">
</div>
<div id="returnDiv" align="center" style="display:none">
<br></br>
  <input type="button" value="返回" class="BigButton" onclick="window.location.reload();"></input>
</div>
</body>
</html>