function praser2Dom(json,shortModel){
  //var keys = json.items;
  for (var key in json){
    var ELEMENT = json[key];
    var ETAG = ELEMENT.TAG;
    var ECLASS = ELEMENT.CLASS;//CLASS属性
    var ETITLE = ELEMENT.TITLE;//TITLE属性
    var EVALUE = ELEMENT.VALUE;//VALUE属性
    var ECONTENT = ELEMENT.CONTENT;//控件的正文
    var EOUT = ECONTENT;//控件页面输出源
    var ENAME = ELEMENT.NAME;
    var SRC = ELEMENT.SRC;
    var ITEMSTR = "";
   // alert(ECLASS);
    if(ECLASS)
    ECLASS = ECLASS.replace(/\"/g,"")
    if(SRC)
      SRC = SRC.replace(/\"/g,"")
    var ITEMID = (ENAME.split("_"))[1];
    if(ECLASS == "DATE"){ //日期控件
      var values = ETITLE.split(":");
      EVALUE = values[1];
      for (var key2 in json){
        var ELEMENT2 = json[key2];
        var ETITLE2 = ELEMENT2.TITLE;
        var ECLASS2 = ELEMENT2.CLASS;
        var ENAME2 = ELEMENT2.NAME;
       // alert(ETITLE2 + "| : |" + EVALUE);
        if(ECLASS2 == "DATE" || ECLASS2 == "USER")
          continue;
        if(ETITLE2 == EVALUE){
          ITEMSTR = ENAME2;
           break;
         }
       }
      EOUT = "<IMG class=DATE id='"
          + ENAME 
          + "' align=absmiddle title=日期控件:"
          + EVALUE 
          + " style=\"CURSOR: hand;cursor:pointer\" src=\""
          + SRC
          + "\" border=0 onclick=\"tdCalendar('"
          + ITEMSTR 
          + "',this)\">";
    }else if(ECLASS == "USER"){//部门人员控件  部门js函数还没实现 20091223
      
      var SELECTTYPE = ELEMENT.TYPE.replace(/\"/g,"");
      //alert(SELECTTYPE == "0")
      //部门人员控件的ITEM_ID没有，故取OTHER_ID
      //$ITEM_ID = substr($ENAME,strpos($ENAME,"_")+1);
     // alert(ELEMENT.TITLE);
      var values = ETITLE.split(":");
      EVALUE = values[1];
      var ITEMSTR = "";
      for (var key2 in json){
        var ELEMENT2 = json[key2];
        var ETITLE2 = ELEMENT2.TITLE;
        var ECLASS2 = ELEMENT2.CLASS;
        var ENAME2 = ELEMENT2.NAME;
        if(ECLASS2 == "DATE" || ECLASS2 == "USER")
          continue;
        if(ETITLE2 == EVALUE){
          ITEMSTR = ENAME2;
           break;
         }
       }
      if(SELECTTYPE == 0 || SELECTTYPE == "" )
        EOUT = "<input type='hidden' name='USER_"
             + ITEMID 
             + "' value='$USER_ID_STR' ><IMG class=USER align=absmiddle title=部门人员控件："
             + EVALUE 
             + " style=\"CURSOR: hand\" " 
             + "src=\"" + SRC + "\" border=0 onclick=\"SelectUser('USER_" 
             + ITEMID
             + "','"
             + ITEMSTR 
             + "')\">  <input type=\"hidden\" value=\"\" id=\"USER_" 
             + ITEMID
             + "\" name=\"USER_" 
             + ITEMID
             + "\" >";
      else if(SELECTTYPE == 1 )
        EOUT = "<input type='hidden' name='DEPT_"
             + ITEMID  
             + "' value='$DEPT_ID_STR'><IMG class=USER align=absmiddle title=部门人员控件："
             + EVALUE 
             + " style=\"CURSOR: hand\" src=\"" + SRC + "\" border=0 onclick=\"SelectDept('"
             + ITEMSTR
             + "','DEPT_"
             + ITEMID
             + "','"
             + ITEMSTR 
             + "')\"> <input type=\"hidden\" value=\"\" id=\"DEPT_" 
             + ITEMID
             + "\" name=\"DEPT_" 
             + ITEMID
             + "\" >";
    }else if(ETAG == "SELECT" && ECLASS != "AUTO"){//非宏控件的下拉控件
      
    }else if(ECLASS == "CALC"){//计算控件
      ECALC1 = calculate(EVALUE,json); 
      //$ELEMENT_OUT.="\n<script>function calc_$ITEM_ID(){var myvalue=eval(\"$ECALC1\");if(myvalue==Infinity) document.form1.DATA_$ITEM_ID.value=\"无效结果\";else if(!isNaN(myvalue)) {var prec=document.form1.DATA_$ITEM_ID.getAttribute('prec');var vPrec;if(!prec) vPrec=10000;else vPrec=Math.pow(10,prec);var result = new Number(parseFloat(Math.round(myvalue*vPrec)/vPrec));document.form1.DATA_$ITEM_ID.value=result.toFixed(prec);}else document.form1.DATA_$ITEM_ID.value=myvalue;setTimeout(\"calc_$ITEM_ID()\",1000);}setTimeout(\"calc_$ITEM_ID()\",3000);</script>";
      EOUT += "\n<script type=\"text/javascript\">" 
           + "function calc_" 
           + ITEMID
      		 + "(){" 
      		 + "var myvalue= eval(\"" 
      		 + ECALC1
      		 + "\");"
      		 //+ "eval(\"alert('1')\");" 
      		 + "if(myvalue==Infinity) document.form1.DATA_" 
      		 + ITEMID 
      		 + ".value=\"无效结果\";else if(!isNaN(myvalue)) {var prec=document.form1.DATA_"
      		 + ITEMID
      		 + ".getAttribute('prec');var vPrec;if(!prec) vPrec=10000;else vPrec=Math.pow(10,prec);var result = new Number(parseFloat(Math.round(myvalue*vPrec)/vPrec));document.form1.DATA_" 
      		 + ITEMID 
      		 + ".value=result.toFixed(prec);}else document.form1.DATA_" 
      		 + ITEMID
      		 + ".value=myvalue;"
      		 + "window.setTimeout(calc_" 
      		 + ITEMID
      		 + ",1000);}window.setTimeout(calc_"
      		 + ITEMID
           + ",3000);"
      		 + "</script>";
    }else if(ECLASS == "AUTO"){//宏控件
      
      EDATAFLD = ELEMENT.DATAFLD;
      var AUTOVALUE = "";

      if(ETAG == "INPUT") // 宏控件单行输入框
      {
         switch(EDATAFLD)
         {
            case "SYS_DATE":
              AUTOVALUE = curdate(new Date()); //当前日期，形如 1999-01-01
              break;
            case "SYS_DATE_CN":
              AUTOVALUE = formatDate(new Date());//当前日期，形如 2009年1月1日
              break;
            case "SYS_DATE_CN_SHORT1":
              AUTOVALUE = formatDateShort1(new Date());//当前日期，形如 2009年
              break;
            case "SYS_DATE_CN_SHORT2":
              AUTOVALUE = formatDateShort2(new Date());//当前年份，形如 2009
              break;
            case "SYS_DATE_CN_SHORT3":
              AUTOVALUE = formatDateShort3(new Date());//当前年份，形如 2009
              break;
            case "SYS_DATE_CN_SHORT4":
              AUTOVALUE = formatDateShort4(new Date());//当前年份，形如 2009
              break;
            case "SYS_TIME":
              AUTOVALUE = sysTime(new Date());//当前时间
              break;
            case "SYS_DATETIME":
              var date = new Date();
              var sd = curdate(date);
             /// alert(sd);
              sd += " " + sysTime(date)
            //  alert(sd);
              AUTOVALUE = sd;
              break;
            case "SYS_WEEK":
              AUTOVALUE = "星期" + getWeek(new Date());
              break;
            case "SYS_USERID":
              var obj = getUser();
              if(obj && obj.userId){
                AUTOVALUE = obj.userId;
              }else{
                 AUTOVALUE = "";
              }
              break;
            case "SYS_USERNAME":
              var obj = getUser();
              if(obj && obj.userName){
                AUTOVALUE = obj.userName;
              }else{
                 AUTOVALUE = "";
              }
              break;
           case "SYS_USERPRIV":
             var obj = getUser();
             if(obj && obj.userPriv){
               AUTOVALUE = obj.userPriv;
             }else{
                AUTOVALUE = "";
             }
             break;
            case "SYS_USERNAME_DATE":
              var obj = getUser();
              if(obj && obj.userName){
                AUTOVALUE = obj.userName + " " + curdate(new Date()) ;
              }else{
                 AUTOVALUE = "";
              }
              break;
            case "SYS_USERNAME_DATETIME":
              var date = new Date();
              var obj = getUser();
              if(obj && obj.userName){
                AUTOVALUE = obj.userName + " " + curdate(date) + " "  + sysTime(date);
              }else{
                 AUTOVALUE = "";
              }
              break;
            case "SYS_DEPTNAME":
              AUTOVALUE = deptNameLong();
              break;
            case "SYS_DEPTNAME_SHORT":
              AUTOVALUE = deptNameShort();
              break;
            case "SYS_FORMNAME":
              AUTOVALUE = formName;
              break;
          
            case "SYS_RUNNAME":
              AUTOVALUE = runName();
              break;
            case "SYS_RUNDATE":
              AUTOVALUE = beginTime().split(" ")[0];
              break;
            case "SYS_RUNDATETIME":
              AUTOVALUE = beginTime();
                          break;
           case "SYS_RUNID":
              AUTOVALUE = runId();
                          break;
        /* case "SYS_AUTONUM":
              AUTOVALUE = $AUTO_NUM;
                          break;
                          */
            case "SYS_IP":
              AUTOVALUE = getIp();
              break;
         /*   case "SYS_SQL":
                          $query_auto="select PRIV_NO from USER_PRIV where USER_PRIV='$LOGIN_USER_PRIV'";
                          $cursor_auto=exequery($connection,$query_auto);
                          if($ROW=mysql_fetch_array($cursor_auto))
                             $LOGIN_USER_PRIV_NO=$ROW["PRIV_NO"];
           
                          $EDATASRC=$ELEMENT_ARR["DATASRC"];
                          $EDATASRC=str_replace("`","'",$EDATASRC);
                          $EDATASRC=str_replace("&#13;&#10;"," ",$EDATASRC);
                          $EDATASRC=str_replace("[SYS_USER_ID]",$LOGIN_USER_ID,$EDATASRC);
                          $EDATASRC=str_replace("[SYS_DEPT_ID]",$LOGIN_DEPT_ID,$EDATASRC);
                          $EDATASRC=str_replace("[SYS_PRIV_ID]",$LOGIN_USER_PRIV,$EDATASRC);
                          $EDATASRC=str_replace("[SYS_PRIV_NO]",$LOGIN_USER_PRIV_NO,$EDATASRC);
                          $EDATASRC=str_replace("[SYS_RUN_ID]",$RUN_ID,$EDATASRC);
                          $cursor_SYS_SQL = exequery($connection,$EDATASRC);
                          if($ROW=mysql_fetch_array($cursor_SYS_SQL))
                            AUTOVALUE = $ROW[0];
                          break;
             */
             case "SYS_MANAGER1":
                AUTOVALUE = deptLocalInput();
                break;
             case "SYS_MANAGER2":
               AUTOVALUE = deptParentInput();
               break;
             case "SYS_MANAGER3":
               AUTOVALUE = deptFirstInput();
               break;
         }
         EOUT = EOUT.replace(EVALUE,"\""+ AUTOVALUE +"\"");
      }else if(ETAG == "SELECT"){  // 宏控件下拉菜单
        switch(EDATAFLD){
           case "SYS_LIST_USER":
             EOUT += "\n<script type=\"text/javascript\">" 
                  + "getPerson(\"" + ENAME + "\");"
                  + "</script>";
             break;
                       
           case "SYS_LIST_DEPT":
             EOUT += "\n<script type=\"text/javascript\">" 
               + "getDepartment(\"" + ENAME + "\");"
               + "</script>";
              break;
           case "SYS_LIST_PRIV":
             EOUT += "\n<script type=\"text/javascript\">" 
               + "getUserPriv(\"" + ENAME + "\");"
               + "</script>";
              break;
                         /*
           case "SYS_LIST_PRCSUSER1":
                         break;
           case "SYS_LIST_PRCSUSER2":
                         break;
           case "SYS_LIST_SQL":
                         $EDATASRC=$ELEMENT_ARR["DATASRC"];
                         $ELEMENT_OUT=str_replace($EDATASRC,"",$ELEMENT_OUT);

                         $EDATASRC=str_replace("`","'",$EDATASRC);
                         $EDATASRC=str_replace("&#13;&#10;"," ",$EDATASRC);
                         $EDATASRC=str_replace("[SYS_USER_ID]",$LOGIN_USER_ID,$EDATASRC);
                         $EDATASRC=str_replace("[SYS_DEPT_ID]",$LOGIN_DEPT_ID,$EDATASRC);
                         $EDATASRC=str_replace("[SYS_RUN_ID]",$RUN_ID,$EDATASRC);
                         $cursor_SYS_SQL = exequery($connection,$EDATASRC);
                         $ITEM_VALUE_TEXT=$ITEM_VALUE;
                         while($ROW=mysql_fetch_array($cursor_SYS_SQL))
                         {
                            $AUTO_VALUE_SQL=$ROW[0];
                            $AUTO_VALUE.="<option value=\"$AUTO_VALUE_SQL\"";
                            if($ITEM_VALUE==$AUTO_VALUE_SQL)
                               $AUTO_VALUE.=" selected";
                            $AUTO_VALUE.=">$AUTO_VALUE_SQL</option>\n";
                         }
                         break;
                         */
            case "SYS_LIST_MANAGER1":
              EOUT += "\n<script type=\"text/javascript\">" 
                + "deptLocal(\"" + ENAME + "\");"
                + "</script>";
               break;
            case "SYS_LIST_MANAGER2":
              EOUT += "\n<script type=\"text/javascript\">" 
                + "deptParent(\"" + ENAME + "\");"
                + "</script>";
               break;
            case "SYS_LIST_MANAGER3":
              EOUT += "\n<script type=\"text/javascript\">" 
                + "deptFirst(\"" + ENAME + "\");"
                + "</script>";
               break;
       }
     }
    }else if(ECLASS == "LIST_VIEW"){//列表控件
      var LVID = "DATA_" + ITEMID;
      var LVTBID = "LV_" + ITEMID;
  //    alert(ITEMID);
      var LVTITLE = ELEMENT.LV_TITLE;
      var LVSIZE = ELEMENT.LV_SIZE;
      var LVSUM = ELEMENT.LV_SUM;
      var LVCAL = ELEMENT.LV_CAL;
      LVCAL = LVCAL.replace(/\"/g,"");
      LVTITLE = LVTITLE.replace(/\"/g,"");
      LVSUM = LVSUM.replace(/\"/g,"");
      EOUT = "<TABLE name=" + LVID
        + " title=" + ETITLE
        + " id='"+ LVTBID
        + "' class='LIST_VIEW' style='border-collapse:collapse' border=1 cellspacing=0 cellpadding=2 bordercolor='#000000'"
        + " formdata='" + LVSIZE + "'><TR style='font-weight:bold;font-size:14px;' class='LIST_VIEW_HEADER'>\n";

      var MYARRAY = LVTITLE.split("`");
      var ARRAYCOUNT = MYARRAY.length;
      if(MYARRAY[ARRAYCOUNT-1] == "") ARRAYCOUNT--;
      for(var i = 0 ; i < ARRAYCOUNT ; i++)
        EOUT += "<TD nowrap>" + MYARRAY[i] + "</TD>\n";

      EOUT += "<TD>操作</TD></TR></TABLE>\n";
      EOUT += "<input type=button value=新增 onclick=\"tbAddNew('" + LVTBID + "',0,'','" + LVSUM + "','" + LVCAL + "');\">\n";
      EOUT += "<input type=button value=计算 onclick=\"tbCal('" + LVTBID + "','" + LVCAL + "')\">\n";
      //$ELEMENT_OUT.="<input type=hidden name=".$LV_ID.">\n";
      EOUT += "<SCRIPT>\n";
      EOUT += "//setInterval(\"tbCal('" + LVTBID + "','" + LVCAL + "')\",1000);";
      EOUT += "</SCRIPT>";
    }else if(ECLASS == "SIGN"){//签章控件
      
    }else if(ECLASS == "DATA"){//数据选择控件
      
    }else if(ECLASS == "FETCH"){//数据获取控件
      
    }
      shortModel = shortModel.replace("{" + ENAME + "}", EOUT);
  }
    return shortModel;
}
//----日期控件函数

//---计算控件函数
function calculate(str,json){
  //匹配字符串 + - * / ^ ()
  var re = /[^\+\-\/\*\^\(\),]+/g;
  var nstr = str;
  // var r = re.exec(str);
  // alert(re);
  var i = 0;
  var fun = {"ABS":"calcABS","RMB":"calcRMB","MAX":"calcMAX","MIN":"calcMIN","DAY":"calcDAY","HOUR":"calcHOUR","AVG":"calcAVG","DATE":"calcDATE"};
  //alert(fun[0])
  var funHash = new Hash(fun);
  var val = new Hash(); 
  while ((arr = re.exec(str)) != null){
    //alert(arr.index + "-" + arr.lastIndex + "\t" + arr);
   // alert(arr + ":" + $A(fun).indexOf(arr.toString()));
    if($A(funHash.keys()).indexOf(arr.toString()) == -1){
      nstr = nstr.replace(arr,"{D_" + i + "}");
      val.set("D_" + i, arr.toString()); 
    }else{
      nstr = nstr.replace(arr,funHash.get(arr.toString()));
    }
    i ++;
 }
  //判断是控件还是数据
  //alert(nstr + "\n" + val.inspect());
  var keys = $A(val.keys());
  for ( var i = 0; i < keys.length; i++) {
    var ITEMSTR = "";
    var key = keys[i];
    var daval = val.get(key);
    var reout = "";
    for (var key2 in json){
      var ELEMENT = json[key2];
      var ETITLE = ELEMENT.TITLE;
      var ECLASS = ELEMENT.CLASS;
      var ID = ELEMENT.ID;
      if(ECLASS == "DATE" || ECLASS == "USER")
        continue;
      if(ETITLE == daval){
        //alert(ETITLE + ":" + daval);
        ITEMSTR = ID;
         break;
      }
    }
    if(ITEMSTR){
      reout = "calcGetVal('" + ITEMSTR + "')";
      //alert(reout);
    }else {
      reout = daval;
    }
    nstr = nstr.replace("{" + key + "}",reout);
   // alert(nstr);
  }
 // alert(nstr);
  return nstr;
}
//---相关函数
function calcGetVal(val , format){
  var obj = document.getElementsByName(val)[0];
  if(!obj){
    obj = document.getElementById(val);
    if(!obj){
      return 0;
    }
  }
  var dv = obj.value;
  if(dv.indexOf("-") > 0 || format){
    if (dv.indexOf("-") > 0){
      dv = dv.replace(/\-/g,"/");
    } 
    else {
      var url =contextPath + "/t9/core/funcs/workflow/util/T9FlowFormUtilAct/getDate.act";
      var rtJson = getJsonRs(url , "value=" + encodeURIComponent(dv) + "&format=" + encodeURIComponent(format));
      if(rtJson.rtState == "0"){
        dv = rtJson.rtData; 
      }
    }
    var d = new Date(dv);
    return d.getTime()/1000; 
  }else if(dv == "" || isNaN(dv)){
    return 0;
  }
  return parseFloat(dv);
}
//---MAX函数
function calcMAX(){
  if(arguments.length == 0)
  return;
  var maxNum = arguments[0];
  for(var i = 0 ; i < arguments.length ; i ++)
    maxNum = Math.max(maxNum , arguments[i]);
  return parseFloat(maxNum);
} 
//--MIN函数
function calcMIN(){
  if(arguments.length == 0)
  return;
  var minNum = arguments[0];
  for(var i = 0 ; i < arguments.length ;i ++)
  minNum = Math.min(minNum,arguments[i]);
  return parseFloat(minNum);
} 
//--ABS函数
function calcABS(val){
  return Math.abs(parseFloat(val));
} 
//--AVG函数
function calcAVG(){
  if(arguments.length == 0)
    return;
  var sum = 0.0;
  for(var i = 0 ; i < arguments.length ; i++){
    sum += parseFloat(arguments[i]);
  }
  return parseFloat(sum/arguments.length);
}
function calcDAY(val){
  return val == 0?0:Math.floor(val/86400);
}

function calcHOUR(val){
  return val == 0?0:Math.floor(val/3600);
}

function calcDATE(val){
  return (val >= 0) ? Math.floor(val/86400)+"天"+Math.floor((val%86400)/3600)+"小时"+Math.floor((val%3600)/60)+"分"+Math.floor(val%60)+"秒":'日期格式无效';
} 
function calcRMB(currencyDigits){
  // Constants:
  if (currencyDigits) {
    var MAXIMUM_NUMBER = 99999999999.99;
    // Predefine the radix characters and currency symbols for output:
   var CN_ZERO = "零";
   var CN_ONE = "壹";
   var CN_TWO = "贰";
   var CN_THREE = "叁";
   var CN_FOUR = "肆";
   var CN_FIVE = "伍";
   var CN_SIX = "陆";
   var CN_SEVEN = "柒";
   var CN_EIGHT = "捌";
   var CN_NINE = "玖";
   var CN_TEN = "拾";
   var CN_HUNDRED = "佰";
   var CN_THOUSAND = "仟";
   var CN_TEN_THOUSAND = "万";
   var CN_HUNDRED_MILLION = "亿";
   var CN_DOLLAR = "元";
   var CN_TEN_CENT = "角";
   var CN_CENT = "分";
   var CN_INTEGER = "整"; 
   //Variables:
   var integral; // Represent integral part of digit number.
   var decimal; // Represent decimal part of digit number.
   var outputCharacters; // The output result.
   var parts;
   var digits, radices, bigRadices, decimals;
   var zeroCount;
   var i, p, d;
   var quotient, modulus;
  
   // Validate input string:
   currencyDigits = currencyDigits.toString();
   if (currencyDigits == "") {
     return "";
   }
   if (currencyDigits.match(/[^,.\d]/) != null) {
     return "";
   }
   if ((currencyDigits).match(/^((\d{1,3}(,\d{3})*(.((\d{3},)*\d{1,3}))?)|(\d+(.\d+)?))$/) == null) {
     return "";
   }
   // Normalize the format of input digits:
   currencyDigits = currencyDigits.replace(/,/g, ""); // Remove comma delimiters.
   currencyDigits = currencyDigits.replace(/^0+/, ""); // Trim zeros at the beginning.
   // Assert the number is not greater than the maximum number.
   if (Number(currencyDigits) > MAXIMUM_NUMBER) {
     return "";
   }
  
   // Process the coversion from currency digits to characters:
   // Separate integral and decimal parts before processing coversion:
   parts = currencyDigits.split(".");
   if (parts.length > 1) {
     integral = parts[0];
     decimal = parts[1];
   // Cut down redundant decimal digits that are after the second.
     decimal = decimal.substr(0, 2);
   }else {
     integral = parts[0];
     decimal = "";
   }  
   // Prepare the characters corresponding to the digits:
   digits = new Array(CN_ZERO, CN_ONE, CN_TWO, CN_THREE, CN_FOUR, CN_FIVE, CN_SIX, CN_SEVEN, CN_EIGHT, CN_NINE);
   radices = new Array("", CN_TEN, CN_HUNDRED, CN_THOUSAND);
   bigRadices = new Array("", CN_TEN_THOUSAND, CN_HUNDRED_MILLION);
   decimals = new Array(CN_TEN_CENT, CN_CENT);
   // Start processing:
   outputCharacters = "";
   // Process integral part if it is larger than 0:
   if (Number(integral) > 0) {
     zeroCount = 0;
     for (i = 0; i < integral.length; i++) {
       p = integral.length - i - 1;
       d = integral.substr(i, 1);
       quotient = p / 4;
       modulus = p % 4;
       if (d == "0") {
         zeroCount++;
       }else {
         if (zeroCount > 0){
           outputCharacters += digits[0];
         }
        zeroCount = 0;
        outputCharacters += digits[Number(d)] + radices[modulus];
       }
       if (modulus == 0 && zeroCount < 4) {
         outputCharacters += bigRadices[quotient];
       }
     }
     outputCharacters += CN_DOLLAR;
   }
   // Process decimal part if there is:
   if (decimal != "") {
     for (i = 0; i < decimal.length; i++) {
       d = decimal.substr(i, 1);
       if (d != "0") {
         outputCharacters += digits[Number(d)] + decimals[i];
       }
     }
   }
   // Confirm and return the final output string:
   if (outputCharacters == "") {
     outputCharacters = CN_ZERO + CN_DOLLAR;
   }
   if (decimal == "") {
     outputCharacters += CN_INTEGER;
   }
   //outputCharacters = CN_SYMBOL + outputCharacters;
   return outputCharacters; 
  } else {
    return ""; 
  }
}
var radio_count=0;
var datetime = 0;
//--列表控件函数
function tbAddNew(lv_tb_id,read_only,row_value,sum,cal , lvAlign ,lvType ,lvValue
    , is_del
    , hideCol 
    , realonlyCol 
    , bindEventCol , bindEventName , bindFunc
){
 radio_count++;
 var mytable = document.getElementById(lv_tb_id);
 var size_array = "";
  try{
    size_array = mytable.getAttribute("formdata").split("`");
  }catch(e){
    size_array = mytable.formdata.split("`");
  }
  var row_value_array = row_value.split("`");
  var align_vlue_array = lvAlign.split("`");
  
  var sum_flag = 0;
  var cell_html="";
  if(sum != ''){
    var sum_array = sum.split("`");
    for(var i = 0; i < sum_array.length ; i++){
      if(sum_array[i] == 1){
        sum_flag = 1;
        break;
      }
    }
  }
  if(cal != '')
    var cal_array = cal.split("`");
  
  var coltype_array= new Array() ;
  if (lvType){
    coltype_array=lvType.split("`");
  }
  var colvalue_array= new Array() ;
  if (lvValue){
    colvalue_array=lvValue.split("`");
  }
  
  maxcell = mytable.rows[0].cells.length;
  if(mytable.rows.length == 1 || sum_flag == 0){
    mynewrow = mytable.insertRow(-1);
  }else{
    mynewrow = mytable.insertRow(mytable.rows.length-1); 
  }
  
  //标识行id
  var rowId = lv_tb_id+"_r"+mynewrow.rowIndex;
  mynewrow.setAttribute("id", rowId);
  
//序号
  mynewcell=mynewrow.insertCell(-1);
  mynewcell.style.textAlign = "center";
  mynewcell.innerHTML = mynewrow.rowIndex;
  for(var i = 0;i < maxcell-2; i++){
    var align = "";
    if (align_vlue_array.length > i) {
      align = align_vlue_array[i];
    }
    if (!align) {
      align = "left";
    }
    var mynewcell  = mynewrow.insertCell(-1);
    
    if (!hideCol) hideCol= "";
    var hideCols = hideCol.split(",");
    for (var z = 0 ;z < hideCols.length ;z++) {
      if (hideCols[z] && hideCols[z] == mynewcell.cellIndex) {
        mynewcell.style.display = 'none';
        break;
      }
    }
    
  //标识列id
    var cellId = rowId + "_c" + mynewcell.cellIndex
    mynewcell.setAttribute("id", cellId);
    mynewcell.align = align;
    if (row_value_array[i] == undefined)  row_value_array[i] = "";
    if(read_only == '1') {
      mynewcell.innerText = row_value_array[i];
    } else {
      switch(coltype_array[i]) {
          case 'text':
              cell_html="<input type=text ";
              cell_html+=" size="+ size_array[i];
              if(row_value != "") {
                cell_html+=" value=\""+ row_value_array[i]+"\"";
              } else {
                cell_html+=" value=\""+ colvalue_array[i]+"\"";
              }
              if(cal_array && cal_array[i]!='')
                 cell_html+=" readonly class=BigStatic";
              else
                 cell_html+=" class=BigInput";
              cell_html+=">";
              mynewcell.innerHTML=cell_html;
              break;
          case 'textarea':        
              cell_html="<textarea ";
              cell_html+="rows=2 cols="+ size_array[i];
              if(cal_array && cal_array[i]!='')
                  cell_html+=" readonly class=BigStatic";
              else
                  cell_html+=" class=BigInput";
              cell_html += ">";
              if(row_value!="")
                  cell_html+= row_value_array[i];
              else {
                  cell_html+= colvalue_array[i];
              }
              cell_html+="</textarea>";
              cell_html = cell_html.replace(/&lt;br&gt;/g, "\n");
              cell_html = cell_html.replace(/<br>/g, "\n");
              mynewcell.innerHTML=cell_html;
              break;
          case 'select':
              cell_html="<select>";
              if(colvalue_array[i]!='')
              {
                  colvalue_inner_array=colvalue_array[i].split(",");
                  for(var j=0;j<colvalue_inner_array.length;j++)
                  {
                      if(row_value_array[i]==colvalue_inner_array[j])
                          cell_html+="<option value=\""+colvalue_inner_array[j]+"\" selected>"+colvalue_inner_array[j]+"</option>";
                      else
                          cell_html+="<option value=\""+colvalue_inner_array[j]+"\">"+colvalue_inner_array[j]+"</option>";
                  }
              }
              cell_html+="</select>";
              mynewcell.innerHTML=cell_html;
              break;
          case 'radio':
              if(colvalue_array[i]!='')
              {
                  colvalue_inner_array=colvalue_array[i].split(",");
                  for(var j=0;j<colvalue_inner_array.length;j++)
                  {
                      if(row_value_array[i]==colvalue_inner_array[j])
                          cell_html+="<input name='radio"+radio_count+i+"' type=radio value=\""+colvalue_inner_array[j]+"\" checked>"+colvalue_inner_array[j];
                      else
                          cell_html+="<input name='radio"+radio_count+i+"' type=radio value=\""+colvalue_inner_array[j]+"\">"+colvalue_inner_array[j];
                  }
              }
              mynewcell.innerHTML=cell_html;
              break;
          case 'checkbox':
              var flag=0;
              if(row_value!="")
                  value_array=row_value_array[i].split(',');
              if(colvalue_array[i]!='')
              {
                  colvalue_inner_array=colvalue_array[i].split(",");
                  for(var j=0;j<colvalue_inner_array.length;j++)
                  {      
                      if(row_value!="")
                      {                                       
                          for(var k=0;k <value_array.length;k++)
                          {
                              if(value_array[k]==colvalue_inner_array[j].replace(/(^\s*)|(\s*$)/g, ""))
                              {
                                  cell_html+="<input type=checkbox value=\""+colvalue_inner_array[j]+"\" checked>"+colvalue_inner_array[j];
                                  flag = 1;
                              }
                          }
                      }    
                      if(flag==0)    
                           cell_html+="<input type=checkbox value=\""+colvalue_inner_array[j]+"\">"+colvalue_inner_array[j];
                      flag=0;
                  }
              }
              mynewcell.innerHTML=cell_html;
              break;
          case 'datetime':
              cell_html="<input type=text ";
              cell_html+=" size="+ size_array[i];
              if(row_value!="")
                 cell_html+=" value=\""+ row_value_array[i]+"\"";
              if(cal_array && cal_array[i]!='')
                 cell_html+=" readonly class=BigStatic";
              else {
                cell_html+=" class=BigInput ";
                cell_html+=" id='datetime_"+datetime+"' name='datetime_"+datetime+"' ";
              }
              cell_html+=">";
              mynewcell.innerHTML=cell_html;
              
              if(cal_array && cal_array[i]!='') {
                
              } else {
                tdCalendar('datetime_' + datetime);
                datetime++;
              }
              break;
          default:
              cell_html="<input type=text ";
              cell_html+=" size="+ size_array[i];
              if(row_value!="")
                 cell_html+=" value=\""+ row_value_array[i]+"\"";
              if(read_only=="1" || (cal_array && cal_array[i]!=''))
                 cell_html+=" readonly class=BigStatic";
              else
                 cell_html+=" class=BigInput";
              cell_html+=">";
              mynewcell.innerHTML=cell_html;
      }
      if (!realonlyCol)realonlyCol= "";
      var realonlyCols = realonlyCol.split(",");
      for (var z = 0 ;z < realonlyCols.length ;z++) {
        if (realonlyCols[z] && realonlyCols[z] == mynewcell.cellIndex) {
          var child = mynewcell.firstChild;
          if (child) {
            try {
              child.readOnly = true;
              child.className = "BigStatic";
            }catch(e) {
            }
          }
          break;
        }
      }
      cell_html="";
    }
  } 
  LvbindRowEvent(mynewrow, bindEventCol , bindEventName , bindFunc);
  mynewcell = mynewrow.insertCell(-1);
  if(!read_only 
      && is_del != "")
    mynewcell.innerHTML = "<input type=button value=删除  onclick=\"tbDelete('"+lv_tb_id+"',this,"+sum_flag+")\">";
  if(sum_flag == 1 && mytable.rows.length == 2)
    tbAddSum(lv_tb_id,sum,sum_flag , hideCol);
  //增加完量，同时浏览器高度也增加，否则下面会被覆盖
  
} 
function showDate(obj) {
  
}
//合计
 function tbAddSum(lv_tb_id,sum,sum_flag , hideCol){
  var mytable = document.getElementById(lv_tb_id);
  var size_array = "";
  try{
    size_array = mytable.getAttribute("formdata").split("`");
  }catch(e){
    size_array = mytable.formdata.split("`");
  }
  var sum_array = sum.split("`");
  var maxcell = mytable.rows[0].cells.length;
  //增加合计
  sumrow = mytable.insertRow(-1);
  sumrow.insertCell(-1);
  sumrow.setAttribute('id',lv_tb_id+'_sum');
  for(var i = 1;i < maxcell - 1;i++){
    sumcell = sumrow.insertCell(-1);
    sumcell.setAttribute('id',lv_tb_id+'_sc' + i);
    if (!hideCol) hideCol= "";
    var hideCols = hideCol.split(",");
    for (var z = 0 ;z < hideCols.length ;z++) {
      if (hideCols[z] && hideCols[z] == sumcell.cellIndex) {
        sumcell.style.display = 'none';
        break;
      }
    }
    
    if(sum_array && sum_array[i-1] == 1){
      cell_html = "<input type=text style='border:none;background:#ffffff;text-align:right;' size="+size_array[i-1]+" readonly class=BigStatic>";
      sumcell.innerHTML = cell_html;
    } 
  }
  sumcell = sumrow.insertCell(-1);
  sumcell.innerHTML = "<input type=button value=合计  onclick=tbSum('" + lv_tb_id + "','" + sum + "')>";
 
  setInterval("tbSum('" + lv_tb_id + "','" + sum + "')",2000);
 } 
 //删除列表行列
 function tbDelete(lv_tb_id,del_btn,sum){
   var mytable = document.getElementById(lv_tb_id);
   mytable.deleteRow(del_btn.parentNode.parentNode.rowIndex);
   if(mytable.rows.length == 2 && document.all(lv_tb_id + "_sum"))
   mytable.deleteRow(1);
   
    //重新计算序号
   for(var i = 1; i < mytable.rows.length; i++){
       var row_obj = mytable.rows[i];
       
       if (sum != 1 || i != (mytable.rows.length - 1)) {
         row_obj.cells[0].innerHTML = i;
       }
   }
} 
 /**
  * 列表控件的值到隐藏input id = lv_id
  * @param lv_tb_id
  * @return
  */
 function tbOutPut(lv_tb_id){
   var data_str = "";
   var mytable=document.getElementById(lv_tb_id);
   var row_length = mytable.rows.length;
   if(document.getElementById(lv_tb_id + '_sum')) {
     row_length--;
   }
   var coltype_array=mytable.getAttribute("lv_coltype").split("`");
   for (var i = 1; i < row_length; i++){
     for (var j = 1; j < document.getElementById(lv_tb_id).rows[i].cells.length - 1; j++){
       var cell = document.getElementById(lv_tb_id).rows[i].cells[j];
       var flag = false;
       if(coltype_array[j-1]=="radio" 
         || coltype_array[j-1]=="checkbox") {
           var inputs = cell.getElementsByTagName("input");
           if(coltype_array[j-1] == "radio") {
             for (var b = 0 ;b < inputs.length ;b++) {
               var ip = inputs[b];
               if (ip.type == 'radio' && ip.checked) {
                 data_str+= ip.value ;
                 flag = true;
                 break;
               }
             }
           } else {
             for (var b= 0 ;b < inputs.length ;b++) {
               var ip = inputs[b];
               if (ip.type == 'checkbox' && ip.checked) {
                 data_str+= ip.value+",";
                 flag = true;
               }
             }
           }
         }
         if (!flag) {
           if(cell.firstChild == null 
               || cell.firstChild.value == undefined){
             var cellValue = cell.innerHTML;
             cellValue = cellValue.replace(/\n/g, "&lt;br&gt;");
             data_str+= cellValue +"`";
           }else{
             cellValue = cell.firstChild.value;
             cellValue = cellValue.replace(/\n/g, "&lt;br&gt;");
             data_str+= cellValue +"`";
           }
         } else {
           data_str += "`";
         }
       }
       data_str = data_str.replace(/\r\n/g, "&lt;br&gt;");
       data_str+="\n";
    }
  lv_id = "DATA_" + lv_tb_id.substr(3);
  document.getElementById(lv_id).value = data_str;
} 
 /**
  * 保存表单时先调用些方法

  */
 function LVsubmit(){
   var lv_tb_id = "";
   var tables = document.getElementsByTagName("table");
   for (var lv_i = 0;lv_i < tables.length; lv_i++){
     if(tables[lv_i].className == "LIST_VIEW"){
       lv_tb_id = tables[lv_i].id;
       var lv_num = lv_tb_id.substr(3);
       if(isEdit || (document.workFlowForm.readOnlyStr 
           && document.workFlowForm.readOnlyStr.value.indexOf(','+lv_num+',')<0
           && document.workFlowForm.readOnlyStr.value.indexOf(lv_num+',') != 0) || !document.workFlowForm.readOnlyStr)
       {
            if(lv_tb_id != ""){
              tbOutPut(lv_tb_id);
            }                 
       }
     }
  }
} 
 //--
 function tbSum(lv_tb_id,sum){
   
   var mytable = document.getElementById(lv_tb_id);
   if(mytable.rows.length == 1) return;
   var sumrow = mytable.rows[mytable.rows.length-1];
   var sum_array = sum.split("`");
   for(var i = 1;i <= sum_array.length;i++){
     var sum_value=0;
     var sum_valuek="";
     if(sum_array[i-1]==1)
     {
         for(j=1;j<mytable.rows.length-1;j++)
         {
             var child_ojb = mytable.rows[j].cells[i].firstChild
             if(child_ojb && child_ojb.tagName){ 
               if(isNaN(mytable.rows[j].cells[i].firstChild.value)){
                 sum_valuek+="NaN";
               } else {
                 sum_value+=parseFloat(mytable.rows[j].cells[i].firstChild.value==''?0:mytable.rows[j].cells[i].firstChild.value);
               }                             
             } else {
                if(isNaN(mytable.rows[j].cells[i].innerText)){
                   sum_valuek+="NaN";                
                } else {
                  sum_value+=parseFloat(mytable.rows[j].cells[i].innerText==''?0:mytable.rows[j].cells[i].innerText);                  ;
                }                
             }
                
         }
         if(sum_valuek != "" && sum_value ==0)
             sumrow.cells[i].firstChild.value="无效值";
         else
             sumrow.cells[i].firstChild.value=Math.round(sum_value*10000)/10000;
     }
   }
} 
 //--
 function tbCal(lv_tb_id,cal){
   var mytable=document.getElementById(lv_tb_id);
   var coltype_array=mytable.getAttribute("lv_coltype").split("`");
   
   if(mytable.rows.length==1) return;
   if(cal) {
       var cal_array = cal.split("`");
       for(i=1; i< mytable.rows.length; i++) { 
         if(mytable.rows[i].id == lv_tb_id+"_sum")
             continue;
         
         for(k=0;k < cal_array.length-1;k++){
           var cal_str = cal_array[k];
           if(cal_str == "" 
             || !mytable.rows[i].cells[k+1].firstChild.tagName 
               || mytable.rows[i].cells[k+1].firstChild.type == 'button'){
             continue;
           }
           
           for(j = 1;j < mytable.rows[i].cells.length - 1;j++){
             var re = new RegExp("\\["+j+"\\]","ig");
             var cell = mytable.rows[i].cells[j];
             var cell_value = 0;
             
             if(coltype_array[j-1] == "radio" 
               || coltype_array[j-1] == "checkbox"){  
               var inputs = cell.getElementsByTagName("input");
               var flag = false;
               for (var b = 0 ;b < inputs.length ;b++) {
                 var ip = inputs[b];
                 if ((ip.type == 'radio' || ip.type == 'checkbox') 
                       && ip.checked) {
                   cell_value = parseFloat(ip.value);
                   flag = true;
                   break;
                 }
               }
               if (!flag) {
                 cell_value = 0 ;
               }
             }else{
               cell_value=parseFloat(cell.firstChild.value);
               var sss = "";
               if (cell.firstChild && cell.firstChild.value) {
                 sss = cell.firstChild.value + "";
                 sss = sss.indexOf("%");
               }
               if(sss > -1){ //处理%
                 cell_value = cell_value/100;
               }
               if(isNaN(cell_value)){
                 cell_value = 0;
               }
             }
             cal_str = cal_str.replace(re,cell_value);
           }
           mytable.rows[i].cells[k+1].firstChild.value = isNaN(eval(cal_str))?0:Math.round(parseFloat(eval(cal_str))*10000) / 10000;  
         }
      }
   }
}
 //初始化下拉菜单数组
 //var arr_DATA_2 = new Array();
 function initSelect(selstr,parentObj1) {
   var parentObj= $(parentObj1);
   if (!parentObj) {
     parentObj = document.getElementsByName(parentObj1)[0];
   }
   var selArray = selstr.split(",");
 
   for(var i=0;i<selArray.length;i++) {
     if(selArray[i]) {
       var arr = window.eval("arr_" + selArray[i]);//取得数组
       arr[selArray[i]] = new Array();
       var obj = $(selArray[i]);
       if (!obj) {
         obj = document.getElementsByName(selArray[i])[0];
       }
       
       for(var j=0;j<obj.options.length;j++) {
         var str=obj.options[j].value;
         if(str.indexOf("|")>=0) {
             //更新value和text
             obj.options[j].value=str.substring(0,str.indexOf("|"));
             obj.options[j].text=str.substring(0,str.indexOf("|"));
             
             var father=str.substring(str.indexOf("|")+1,str.length);
             var optionValue=str.substring(0,str.indexOf("|"));
             //记录当前选中值
             if(obj.selectedIndex==j) var cur_val=optionValue;
             if(typeof arr[selArray[i]][father]=='undefined')
               arr[selArray[i]][father]="";
             arr[selArray[i]][father]+=optionValue+",";
          }
        }
        //重建子菜单 
        selectChange(parentObj.value,selArray[i],cur_val);   
      }
    }
 }
 /**
  * 重建子菜单 
  * @param parentValue
  * @param child
  * @param cur_val
  * @return
  */
 function selectChange(parentValue,child,cur_val) {
   var childArray = child.split(",");
   for (var i=0 ;i < childArray.length ; i++) {
     if ( childArray[i]) {
       var arr=window.eval("arr_"+childArray[i]);
       var optionStr=arr[childArray[i]][parentValue];
       if (optionStr) {
         var optionArr=optionStr.split(",");
         var obj = $(childArray[i]);
         if (!obj) {
           obj = document.getElementsByName(childArray[i])[0];
         }
         obj.options.length=0;       
         for (var j = 0 ; j < optionArr.length ; j++) {
           if (optionArr[j] != "") {
             //添加option
             var objOption = document.createElement("OPTION");
             objOption.text = optionArr[j];
             objOption.value = optionArr[j]+"|"+parentValue;
             obj.options.add(objOption);
             if(typeof cur_val!='undefined' && cur_val==optionArr[j]) obj.value=cur_val+"|"+parentValue;
           }
         }
       }
     }
   }    
 }
 /**
  * 
  * @param item
  * @param callback - 回掉函数
  * @return
  */
 function show_seal(item,callback)
 {
   URL= contextPath + "/core/funcs/workflow/websign/sel_seal/index.jsp?item=" + item +"&callback=" + callback;
   openDialog(URL,  470, 400);
   //showModalWindow(URL,'选择印章' , "seal" ,300,350 , false);
 }

function radioClick(radio , otherRadios) {
  for (var i = 0 ; i < otherRadios.length ;i++) {
    var ra = otherRadios[i];
    var rad = $(ra);
    if (rad) {
      rad.checked = false;
    }
  }
  radio.checked = true;
}
function getUrlByClass(module) {
  if (module == null) {
    return ;
  }
  module = module.replace(".","/");
  module = "/t9/user/" + module ;
  return module
}
function editModuleContent(module , divId , moduleSeqId , isReadOnly) {
  var param = "q=1";
  if (moduleSeqId) {
    param += "&moduleId=" + moduleSeqId;
  }
  if (isReadOnly) {
    param += "&isReadOnly=" + isReadOnly;
  }
  var url = contextPath + getUrlByClass(module) + "/edit.act";
  var text = getTextRs(url , param);
  if (text) {
    $(divId).update(text);
  }
}
function printModuleContent(module , divId , moduleSeqId) {
  var param = "";
  if (moduleSeqId) {
    param += "moduleId=" + moduleSeqId;
  }
  var url = contextPath + getUrlByClass(module) + "/print.act";
  var text = getTextRs(url , param);
  if (text) {
    $(divId).update(text);
  }
}
function upperCaseWord(str) {
  if (str == "") {
    return str;
  }
  str = str.substring(0, 1).toUpperCase() + str.substring(1);
  return str;
}
function setImgUploadPosition(obj,dataId){
  var dataObj = document.getElementById(dataId);
  dataObj.style.left =  (GetPosition(obj).x) + "px";
  dataObj.style.top = (GetPosition(obj).y )  + "px";
  dataObj.style.height = obj.height  + "px";
}
function GetPosition(obj){
  var left = 0;
  var top  = 0;
  while(obj != document.body){
    left += obj.offsetLeft;
    top  += obj.offsetTop;
    obj = obj.offsetParent;
  }
  return {x:left,y:top};
}

function hideCol(listViewId , col) {
  var cols = col.split(",");
  var lv = $('LV_' + listViewId);
  
  var l = lv.rows.length;
  for (var i = 0 ;i < l ; i++) {
    var c = lv.rows[i].cells.length;
    for (var j = 0 ; j < c ;j++) {
      for (var z = 0 ;z < cols.length ;z++) {
        if (cols[z] && cols[z] == j) {
          var cel = lv.rows[i].cells[j];
          cel.style.display = 'none';
          break;
        }
      }
    }
  }
}

function LvbindEvent(listViewId , row, col , event, func) {
  var cols = col.split(",");
  var events = event.split(",");
  var funcs = func.split(",");
  
  var lv = $('LV_' + listViewId);
  var l = lv.rows.length;
  for (var i = 0 ;i < l ; i++) {
    var c = lv.rows[i].cells.length;
    for (var j = 0 ; j < c ;j++) {
      for (var z = 0 ;z < cols.length ;z++) {
        if (cols[z] && cols[z] == j) {
          var cel = lv.rows[i].cells[j];
          var child = cel.firstChild;
          if (child) {
            try {
              var ev = events[z];
              var fu = funcs[z];
              $(child).observe(ev,function(){eval(fu+ "(this)");});
            }catch(e) {
            }
          }
          break;
        }
      }
    }
  }
}
function LvbindRowEvent(row, col , event, func) {
	if(!row || !col){
		return;
	}
	var cols = col.split(",");
  var events = event.split(",");
  var funcs = func.split(",");
  
  var c = row.cells.length;
  for (var j = 0 ; j < c ;j++) {
    for (var z = 0 ;z < cols.length ;z++) {
      if (cols[z] && cols[z] == j) {
        var cel = row.cells[j];
        var child = cel.firstChild;
        if (child) {
          registEvent(child  , row , cel  , events[z] , funcs[z]);
        }
        break;
      }
    }
  }
}

function registEvent(node  , row , cel  , ev , fu) {
  try {
    $(node).observe(ev
        ,function(){ 
        eval("window."+fu+"(this , row.id , cel.id);");
      }
    );
  }catch(e) {
  }
}

function readOnlyCol(listViewId , col) {
  var cols = col.split(",");
  var lv = $('LV_' + listViewId);
  
  var l = lv.rows.length;
  for (var i = 0 ;i < l ; i++) {
    var c = lv.rows[i].cells.length;
    for (var j = 0 ; j < c ;j++) {
      for (var z = 0 ;z < cols.length ;z++) {
        if (cols[z] && cols[z] == j) {
          var cel = lv.rows[i].cells[j];
          var child = cel.firstChild;
          if (child) {
            try {
              child.readOnly = true;
              child.className = "BigStatic";
            }catch(e) {
            }
          }
          break;
        }
      }
    }
  }
}
