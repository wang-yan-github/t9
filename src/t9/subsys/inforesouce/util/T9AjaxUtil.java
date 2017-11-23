package t9.subsys.inforesouce.util;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9Const;
/**
 * ajax工具类
 * @author qwx110
 *
 */
public class T9AjaxUtil{
  public static void ajax(int id,  HttpServletResponse response) throws Exception{
    response.setContentType("text/html");
    response.setCharacterEncoding(T9Const.DEFAULT_CODE); 
    PrintWriter pw = response.getWriter();
    String rtData = "";
    if(id != 0){
       rtData = "{rtState:'0'}";           
    }else{
       rtData = "{rtState:'1'}";      
    }
    pw.println(rtData);
    pw.flush();
  }
  
  public static void ajax(String str, HttpServletResponse response) throws Exception{
    response.setContentType("text/html");
    response.setCharacterEncoding(T9Const.DEFAULT_CODE); 
    PrintWriter pw = response.getWriter();    
    String rtData = str;
    pw.println(rtData);    
    pw.flush();
  }
}
