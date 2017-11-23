package t9.core.funcs.doc.act;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.logic.T9ConfigLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9DoClearAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9DoClearAct");
  public String doPrcsClear(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      String prcsIdStr = request.getParameter("prcsId");
      String flowPrcsStr = request.getParameter("flowPrcs");
      
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      doPrcsClear(dbConn ,  runId,  flowId ,  prcsId ,  flowPrcs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute( T9ActionKeys.RET_DATA,  "" );
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doEndClear(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String runIdStr = request.getParameter("runId");
      int runId = Integer.parseInt(runIdStr);
      doEndClear(dbConn ,  runId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute( T9ActionKeys.RET_DATA,  "" );
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public static void doPrcsClear(Connection conn , int runId, int flowId , int prcsId , int flowPrcs) throws Exception {
    String queryTmp = "delete from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" where RUN_ID=" + runId  + " and PRCS_ID > " + prcsId;
    T9WorkFlowUtility.updateTableBySql(queryTmp, conn);
    String updateStr =  "update "+ T9WorkFlowConst.FLOW_RUN_PRCS +" set DELIVER_TIME=NULL,PRCS_FLAG='2' WHERE RUN_ID="+runId+" and PRCS_ID="+prcsId+" and FLOW_PRCS='"+flowPrcs+"' and PRCS_FLAG in ('3','4')";
    T9WorkFlowUtility.updateTableBySql(updateStr, conn);
  }
  public static void doEndClear(Connection conn , int runId) throws Exception{
    String query = "select max(a.PRCS_ID) from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" as a where a.RUN_ID = '"+runId+"'";
    Statement stm = null;
    ResultSet rs = null;
    int prcsId = 0;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if(rs.next()){
        prcsId = rs.getInt(1);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    String updateFlag = "update "+ T9WorkFlowConst.FLOW_RUN_PRCS +" set  "
      + " PRCS_FLAG='2'"
      + " WHERE "
      + " RUN_ID='"+runId+"'  "
      + " and PRCS_ID= " + prcsId;
    T9WorkFlowUtility.updateTableBySql(updateFlag, conn);
    //更新当前主办人的转交时间为当前系统时间，仅更新当前步骤中当前主办人的记录（一条）091015
    String updateTime = "update "+ T9WorkFlowConst.FLOW_RUN_PRCS +" set  "
      + " DELIVER_TIME=NULL  "
      + " WHERE  "
      + " RUN_ID='" + runId + "'"
      + " and PRCS_ID=" + prcsId + " and OP_FLAG = 1";
    T9WorkFlowUtility.updateTableBySql(updateTime, conn);
  }
  public static void main(String[] args) {
    Connection dbConn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3396/t9?characterEncoding=UTF8","root" , "myoa888");
      //doPrcsClear(dbConn ,  79,  568 ,  1 ,  1);
      doEndClear(dbConn , 79);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        dbConn.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
  public String clearTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      for (String t : T9WorkFlowConst.T) {
        if (!"DOC_RUN_SEQ_ID".equals(t)) {
          String sql = "delete from " + t;
          t9.core.funcs.doc.util.T9WorkFlowUtility.updateTableBySql(sql, dbConn);
        }
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
