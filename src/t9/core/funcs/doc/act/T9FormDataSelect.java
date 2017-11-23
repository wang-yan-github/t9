package t9.core.funcs.doc.act;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.logic.T9PluginLogic;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;

public class T9FormDataSelect {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FormDataSelect");
  public String getDataConfig(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String contextRealPath = request.getSession().getServletContext().getRealPath("/");
      String path = contextRealPath + T9WorkFlowConst.MODULE_CONTEXT_PATH + "/flowform/editor/plugins/NDataSelect/config.properties";
      Properties pro = new Properties();
      Properties p = new Properties();
      p.load(new InputStreamReader(new FileInputStream(new File(path)) , "UTF-8"));
      StringBuffer sb = new StringBuffer();
      Set<Object> keys = p.keySet();
      sb.append("{");
      int count = 0 ;
      for (Object o : keys) {
        String s = (String)o;
        String value = p.getProperty(s);
        sb.append(s + ":" + value + ",");
        count++;
      }
    
      
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("},selfDef:{");
    //自定义数据源
      count = 0 ;
      String query = "select d_name,d_desc from data_src order by seq_id";
      Statement stm2 = null;
      ResultSet rs2 = null;
      try {
        stm2 = dbConn.createStatement();
        rs2 = stm2.executeQuery(query);
        while (rs2.next()){
          String dName = rs2.getString("d_name");
          String dDesc = rs2.getString("d_desc");
          sb.append("\"DATA_" + dName + "\":\""  + dDesc + "\",");
          count++;
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm2, rs2, null); 
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString() );
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String dataSrc = request.getParameter("dataSrc");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int count = 0 ;
      String tableName  = "data_" + dataSrc;
      String query  = "SHOW FULL FIELDS FROM " + tableName;
      Statement stm2 = null;
      ResultSet rs2 = null;
      StringBuffer sb = new StringBuffer();
      sb.append("{");
      try {
        stm2 = dbConn.createStatement();
        rs2 = stm2.executeQuery(query);
        while (rs2.next()){
          String dName = rs2.getString("Field");
          String dDesc = rs2.getString("Comment");
          sb.append("\"" + dName + "\":\""  + dDesc + "\",");
          count++;
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm2, rs2, null); 
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString() );
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String selectData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sql = request.getParameter("sqlId");
      String findStr = request.getParameter("findStr");
      T9PluginLogic logic = new T9PluginLogic();
      StringBuffer result = logic.getSelectData(dbConn, request.getParameterMap(), findStr, sql);
      PrintWriter pw = response.getWriter();
      pw.println(result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
