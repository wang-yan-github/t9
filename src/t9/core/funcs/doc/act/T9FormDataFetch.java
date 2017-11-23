package t9.core.funcs.doc.act;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9FormDataFetch {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FormDataFetch");
  public String getDataConfig(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String query = "select FORM_SEQ_ID,FLOW_NAME from "+ T9WorkFlowConst.FLOW_TYPE +" order by SEQ_ID";
      Statement stm2 = null;
      ResultSet rs2 = null;
      StringBuffer sb = new StringBuffer();
      sb.append("[");
      int count = 0 ;
      try {
        stm2 = dbConn.createStatement();
        rs2 = stm2.executeQuery(query);
        while (rs2.next()){
          int formId = rs2.getInt("FORM_SEQ_ID");
          String dDesc = rs2.getString("FLOW_NAME");
          if (formId != 0 ) {
            sb.append("{formId:" + formId);
            sb.append(",flowName:\"" + dDesc + "\"");
            sb.append("},");
            count++;
          }
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm2, rs2, null); 
      }
      
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
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
      String query = "select TITLE , ITEM_ID from "+ T9WorkFlowConst.FLOW_FORM_ITEM +" where form_id = '" + dataSrc + "'";
      Statement stm2 = null;
      ResultSet rs2 = null;
      StringBuffer sb = new StringBuffer();
      sb.append("[");
      try {
        stm2 = dbConn.createStatement();
        rs2 = stm2.executeQuery(query);
        while (rs2.next()){
          String dName = rs2.getString("ITEM_ID");
          String dDesc = rs2.getString("TITLE");
          sb.append("{");
          sb.append("itemId:\"" + dName + "\"" );
          sb.append(",title:\"" + dDesc + "\"" );
          sb.append("},");
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
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString() );
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String dataFetch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      String runId = request.getParameter("runId");
      String message = "";
      if (T9Utility.isNullorEmpty(runId) 
          || !T9Utility.isInteger(runId)) {
        message = "error:请输入正确的流水号!";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
        return "/core/inc/rtjson.jsp";
      }
      String dataField = T9Utility.null2Empty(request.getParameter("dataField"));
      String[] dataArray = dataField.split("`");
      String itemId = "";
      for (String s : dataArray) {
        if (!"".equals(s)) {
          itemId += s + ",";
        }
      }
      if (itemId.endsWith(",")) {
        itemId = itemId.substring(0 , itemId.length() - 1);
      }
      String sql = "select FLOW_ID from "+ T9WorkFlowConst.FLOW_RUN +" where RUN_ID=" + runId;
      Statement stm2 = null;
      ResultSet rs2 = null;
      int flowId = 0 ;
      try {
        stm2 = dbConn.createStatement();
        rs2 = stm2.executeQuery(sql);
        if (rs2.next()){
          flowId = rs2.getInt("FLOW_ID");
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm2, rs2, null); 
      }
      String dataSrc = request.getParameter("dataSrc");
      sql = "select * from "+ T9WorkFlowConst.FLOW_TYPE +" where FORM_SEQ_ID='" + dataSrc + "' and SEQ_ID = '" + flowId + "'";
      Statement stm3 = null;
      ResultSet rs3 = null;
      
      try {
        stm3= dbConn.createStatement();
        rs3 = stm3.executeQuery(sql);
        if (!rs3.next()){
          message = "error:无法从未建立映射关系的表单提取数据!";
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, message);
          return "/core/inc/rtjson.jsp";
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm3, rs3, null); 
      }
      String itemStr = T9Utility.null2Empty(request.getParameter("itemStr"));
      String itemArray[] = itemStr.split(",");//当前表单回填字段
      String itemIdArray[] = itemId.split(",");//目标表单ITEM_ID
      Map mapArray = new HashMap();
      for (int i = 0 ;i < itemArray.length ;i++) {
        String s = itemArray[i];
        if (!"".equals(s) && i <= itemIdArray.length) {
          mapArray.put(itemIdArray[i], s);
        }
      }
      String sql2="select ITEM_ID,ITEM_DATA from "+ T9WorkFlowConst.FLOW_RUN_DATA +" WHERE RUN_ID=" +runId + " and ITEM_ID in ("+itemId+")";
      Statement stm4 = null;
      ResultSet rs4 = null;
      int count = 0 ;
      sb.append("{");
      try {
        stm4= dbConn.createStatement();
        rs4 = stm4.executeQuery(sql2);
        while (rs4.next()){
          String itemData = T9Utility.null2Empty(rs4.getString("ITEM_DATA"));
          String itemId2 = rs4.getString("ITEM_ID");
          itemData = T9Utility.encodeSpecial(itemData);
          itemData = itemData.replace("\r\n", "<br>");
          itemData = itemData.replace("\n", "<br>");
          itemData = itemData.replace("\r", "<br>");
          sb.append("\"" + (String)mapArray.get(itemId2) + "\":\"" + itemData + "\",");
          count++;
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm4, rs4, null); 
      }
      if (count > 0 ) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString() );
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
