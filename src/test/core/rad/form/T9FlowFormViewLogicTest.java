package test.core.rad.form;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import t9.core.funcs.workflow.data.T9FlowFormReglex;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import t9.core.funcs.workflow.logic.T9FlowFormViewLogic;
import t9.core.funcs.workflow.praser.T9FormPraser;
import test.core.util.db.TestDbUtil;

public class T9FlowFormViewLogicTest{

  public void testPraserHTML(){
    String html = "<DIV class=title align=center> "
      + "<STRONG> "
      + "<FONT size=6>#[表单]</FONT> "
      + "</STRONG> "
      + "</DIV> "
      + "<TABLE class=Big width=500 align=center border=0> "
      + "<TBODY> "
      + "<TR align=right> "
      + "<TD>文号：#[文号]<BR>#[时间] </TD> "
      + "</TR> "
      + "</TBODY> "
      + "</TABLE> "
      + "<TABLE class=\"TableBlock\" width=500 align=center> "
      + "<TBODY> "
      + "<TR class=\"TableData\"> "
      + "<TD colSpan=2> "
      + "<STRONG>收文时间： "
      + "<INpUT NAME=\"DATA_1\" dataFld=SYS_DATE class=AUTO title=收文时间 dataSrc=\"\" value={宏控件}> "
      + "<IMG SRC=\"/module/html_editor/editor/images/calendar.gif\" NAME=\"OTHER_1\" class=DATE title=日期控件：收文时间 style=\"CURSOR: hand\"  align=absMiddle  border=0  value=\"收文时间\"/> "
      + "&nbsp;密级： "
      + "<SELECT NAME=\"DATA_2\" title=密级> "
      + "<OPTION value=普通 selected>普通</OPTION> "
      + "<OPTION value=秘密>秘密</OPTION> "
      + "<OPTION value=机密>机密</OPTION> "
      + "<OPTION value=绝密>绝密</OPTION> "
      + "</SELECT> "
      + "<BR>来文单位：</STRONG> "
      + "<INPUT NAME=\"DATA_3\" title=来文单位 style=\"WIDTH: 348px; HEIGHT: 21px\" size=48> "
      + "</TD> "
      + "</TR> "
      + "<TR class=\"TableData\"> "
      + "<TD colSpan=2><STRONG>文件名： "
      + "<INPUT NAME=\"DATA_4\" title=文件名称 style=\"WIDTH: 349px; HEIGHT: 21px\" size=45> "
      + "<BR><B>主题词：</B> "
      + "<INPUT NAME=\"DATA_5\" title=主题词 size=45>  "
      + "<BR><STRONG>页码：　 "
      + "<INPUT NAME=\"DATA_6\" title=页码 style=\"WIDTH: 74px; HEIGHT: 21px\" size=9> "
      + "</STRONG></STRONG> "
      + "</TD> "
      + "</TR> " 
      + "<TR class=\"TableData\"> "
      + "<TD colSpan=2> "
      + "<P><STRONG>摘要：<BR> "
      + "<TEXTAREA NAME=\"DATA_7\" title=摘要 style=\"WIDTH: 479px; HEIGHT: 68px\" rows=4 cols=64>df</TEXTAREA> "
      + "&nbsp;</P></STRONG> "
      + "</TD> "
      + "</TR> "
      + "<TR class=\"TableData\"> "
      + "<TD colSpan=2> "
      + "<STRONG>拟办意见：<BR></STRONG> "
      + "<DIV align=left> "
      + "<TEXTAREA NAME=\"DATA_8\" title=拟办意见 style=\"WIDTH: 479px; HEIGHT: 79px\" rows=5 cols=64>ss</TEXTAREA> "
      + "&nbsp; "
      + "</DIV> "
      + "</TD> "
      + "</TR> "
      + "<TR class=\"TableData\"> "
      + "<TD colSpan=2> "
      + "<STRONG>领导批阅意见：<BR></STRONG> "
      + "<DIV align=left> "
      + "<TEXTAREA NAME=\"DATA_9\" title=领导批示 style=\"WIDTH: 481px; HEIGHT: 49px\" rows=3 cols=64>sss</TEXTAREA> "
      + "&nbsp; "
      + "</DIV> "
      + "</TD> "
      + "</TR> "
      + "<TR class=\"TableData\"> "
      + "<TD colSpan=2> "
      + "<DIV align=left><STRONG>归档人： "
      + "<INPUT NAME=\"DATA_10\" dataFld=SYS_USERNAME class=AUTO title=归档人 style=\"WIDTH: 124px; HEIGHT: 21px\" dataSrc=\"\" size=16 value={宏控件}> "
      + "</STRONG> "
      + "<img title=\"safd\" src=\"/module/html_editor/editor/images/listview.gif\" align=\"absMiddle\" border=\"0\" name=\"DATA_5\" lv_title=\"sdf`sdfe`ssssss`\" lv_size=\"10`10`10`\" lv_sum=\"0`0`0`\" lv_cal=\"```\" class=\"LIST_VIEW\" style=\"cursor: hand\" alt=\"\" />"
      + "</DIV> "
      + "</TD> "
      + "</TR> "
      + "</TBODY> "
      + "s</TABLE> ";
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      e.printStackTrace();
    }
    T9FlowFormLogic ffl = new T9FlowFormLogic();
    try{
     // Map map = ffl.selectFlowForm(dbConn, 53, "PRINT_MODEL");
      
      
     // html = (String) map.get("PRINT_MODEL");
      html = html.replaceAll("\"", "");
    } catch (Exception e){
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(html);
    Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
    String data = T9FormPraser.toJson(m1).toString();
    html = T9FormPraser.toShortString(m1, html, T9FlowFormReglex.CONTENT);
    System.out.println(html);
    System.out.println(m1.size());
    System.out.println(data);
   // System.out.println(hm);
  /* List<String> l1 = T9FormPraser.praserHTML(T9FlowFormReglex.INPUT, T9FlowFormReglex.NOREND2, html);
  List<String> l2 = T9FormPraser.praserHTML(T9FlowFormReglex.SELECT, T9FlowFormReglex.SELECTEND, html);
   List<String> l3 = T9FormPraser.praserHTML(T9FlowFormReglex.TEXTAREA, T9FlowFormReglex.TEXTAREAEND, html);
   List<String> l4 = T9FormPraser.praserHTML(T9FlowFormReglex.BUTTON, T9FlowFormReglex.BUTTONEND, html);
   List<String> l5 = T9FormPraser.praserHTML(T9FlowFormReglex.IMG, T9FlowFormReglex.NOREND2, html);
   System.out.println("input输出格式为 ： " + l1);
   System.out.println("SELECT输出格式为 ： " + l2);
   System.out.println("TEXTAREA输出格式为 ： " + l3);
   System.out.println("BUTTON输出格式为 ： " + l4);
   System.out.println("输出格式为 ： " + l5);*/
  }
  public void testPraserHTML2(){
    String html = " title=sad type=checs kbox value=on name=DATA_2";
    String regex = " \\S+=\\S+";
    T9FlowFormViewLogic.praserHTMLT(regex, html);
  }
}
