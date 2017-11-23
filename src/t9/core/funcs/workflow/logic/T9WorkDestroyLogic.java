package t9.core.funcs.workflow.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.dc.pr.PRError;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.office.ntko.data.T9NtkoCont;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

/**
 * 工作流销毁逻辑层
 * @author Think
 *
 */
public class T9WorkDestroyLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.logic.T9WorkDestroyLogic");
 /**
  * 得到所有流程
  * @param conn
  * @return
  * @throws Exception
  */
  public String getFlow(Connection conn , String sortId) throws Exception{
    String result = "";
    String tmp = "";
    if (!T9Utility.isNullorEmpty(sortId)) {
      sortId = T9WorkFlowUtility.getOutOfTail(sortId);
      tmp = " and FLOW_TYPE.flow_sort in (" + sortId + ") ";
    }
    String sql = "SELECT FLOW_TYPE.SEQ_ID,FLOW_NAME,FREE_OTHER from FLOW_TYPE,FLOW_SORT where FLOW_TYPE.FLOW_SORT = FLOW_SORT.SEQ_ID "+ tmp +" order by SORT_NO,FLOW_NO";
    
    PreparedStatement ps = null ; 
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      
      while(rs.next()){
        int flowId = rs.getInt(1);
        String flowName = rs.getString(2);
        String freeOther = rs.getString(3);
        if(!"".equals(result)){
          result += ",";
        }
        result += "{" 
          + "flowId:" + flowId  + "," 
          + "flowName:\"" + flowName  + "\"," 
          + "flowId:\""  + freeOther  + "\"" 
          + "}";
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return "[" + result + "]";
  }
  
  public StringBuffer getWorkListLogic(Connection conn,T9Person user,Map request) throws Exception{
    StringBuffer result = new StringBuffer();
    try {
      String sql =" SELECT " 
          + " FLOW_RUN.SEQ_ID "
          + " ,RUN_ID "
          + " ,RUN_NAME "
          + " ,BEGIN_TIME "
          + " ,ATTACHMENT_ID "
          + " ,ATTACHMENT_NAME "
          + " ,FLOW_ID "
      		+ " FROM " 
      		+ " FLOW_RUN "
      		+ " ,FLOW_TYPE "
      	  + " ,PERSON"
      		+ " WHERE DEL_FLAG = 1 " 
      	  + " and FLOW_TYPE.SEQ_ID=FLOW_RUN.FLOW_ID "
      	  + " AND PERSON.SEQ_ID = FLOW_RUN.BEGIN_USER";
 
      String where = toSerachWhere(conn,user,request);
      if(!"".equals(where)){
        sql += where;
      }
      String query = " order by RUN_ID desc";
      sql += query;
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      result.append(pageDataList.toJson());
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
  /**
   * 得到所有日志的seqId
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
public StringBuffer getAlldeleteRunId(Connection conn,T9Person user,Map request) throws Exception{
  StringBuffer result = new StringBuffer();
    String sql =" SELECT " 
        + " RUN_ID "
        + " FROM " 
        + " FLOW_RUN "
        + " ,FLOW_TYPE "
        + " ,PERSON"
        + " WHERE DEL_FLAG = 1 "
        + " and FLOW_TYPE.SEQ_ID=FLOW_RUN.FLOW_ID "
        + " AND PERSON.SEQ_ID = FLOW_RUN.BEGIN_USER";
  PreparedStatement ps = null;
  ResultSet rs = null;
  try {
    String where = toSerachWhere(conn,user,request);
    if(!"".equals(where)){
      sql += where;
    }
    ps = conn.prepareStatement(sql);
    rs = ps.executeQuery();
    while(rs.next()){
      int seqId = rs.getInt(1);
      if(!"".equals(result.toString())){
        result.append(",");
      }
      result.append(seqId);
    }
  } catch (Exception e) {
    throw e;
  } finally {
    T9DBUtility.close(ps, rs, log);
  }
  return result;
}
  /**
   * 组装查询条件
   * @param request
   * @return
   * @throws Exception 
   */
  private String toSerachWhere(Connection conn , T9Person user,Map request) throws Exception{
    String whereStr = "";
    String flowId = request.get("flowId") != null ? ((String[])request.get("flowId"))[0] : null;
    String runId = request.get("runId") != null ? ((String[])request.get("runId"))[0] : null;
    String runName = request.get("runName") != null ? ((String[])request.get("runName"))[0] : null;
    String startTime = request.get("startTime") != null ? ((String[])request.get("startTime"))[0] : null;
    String endTime = request.get("endDate") != null ? ((String[])request.get("endDate"))[0] : null;
    String toId = request.get("toId") != null ? ((String[])request.get("toId"))[0] : null;
    String sortId = request.get("sortId") != null ? ((String[])request.get("sortId"))[0] : "";
    
    if(flowId != null && !"".equals(flowId)){
      whereStr +=  " and FLOW_ID=" + flowId +"";     
    }
    if (!T9Utility.isNullorEmpty(sortId)) {
      sortId = T9WorkFlowUtility.getOutOfTail(sortId);
      whereStr +=  " and FLOW_TYPE.FLOW_SORT IN (" + sortId +") ";     
    }
    // 如果流水号 不等于空   加条件
    if(!"".equals(runId) && runId != null){
      whereStr +=   " and RUN_ID=" + runId +""; 
    }
    // 如果文件名称不为空的 话   加条件
    if(!"".equals(runName) && runName != null){
      whereStr +=  " and RUN_NAME like " + "'%" +  T9Utility.encodeLike(runName) + "%' " + T9DBUtility.escapeLike() ;
    }
    // --- “日期范围”条件，对应流程实例的创建时间BEGIN_TIME ---
    if(startTime != null && !"".equals(startTime)){
      startTime +=  " 00:00:00";
      String dbDateF = T9DBUtility.getDateFilter("BEGIN_TIME", startTime, " >= ");
      whereStr += " and " + dbDateF;
    }
    if(endTime != null && !"".equals(endTime)){
      endTime +=  " 23:59:59";
      String dbDateF = T9DBUtility.getDateFilter("BEGIN_TIME", endTime, " <= ");
      whereStr += " and " + dbDateF;
    }
    if(toId != null && !"".equals(toId)){
      whereStr += " AND  " + T9DBUtility.findInSet(toId,"BEGIN_USER");
    }
    String managerSql = getMyManageSql(conn, user);
    if(!"".equals(managerSql)){
      whereStr += " AND (" + managerSql + ")";
    }
    return whereStr;
  }
  /**
   * 得到管理权限
   * @param conn
   * @param user
   * @return
   * @throws Exception
   */
  public String getMyManageSql(Connection conn,T9Person user) throws Exception{
    
    String result = "";
    if(user.isAdmin()){
      return "";
    }
    T9FlowWorkSearchLogic fw = new T9FlowWorkSearchLogic();
    String myDeptstr = fw.getMyDept(conn,user.getDeptId());
    String myRunId = fw.getMyFlowRun(conn,user.getSeqId());
    int loginUserId = user.getSeqId();
    String loginUserDept = String.valueOf(user.getDeptId());
    String loginUserPriv = user.getUserPriv();
    //全局监控权限
    result = T9DBUtility.findInSet(String.valueOf(loginUserId),fw.subStringIndex("|","MANAGE_USER",1,1))
       + " or "
       + T9DBUtility.findInSet(loginUserDept,fw.subStringIndex("|","MANAGE_USER",1,2))
       + " or "
       + T9DBUtility.findInSet("0",fw.subStringIndex("|","MANAGE_USER",1,2))
        + " or "
       + T9DBUtility.findInSet("ALL_DEPT",fw.subStringIndex("|","MANAGE_USER",1,2))
       + " or "
       + T9DBUtility.findInSet(loginUserPriv,fw.subStringIndex("|","MANAGE_USER",1,3));

    //部门监控、查询权限
    result += " or (PERSON.DEPT_ID IN (" + myDeptstr + ") "
      + " AND ("
      + T9DBUtility.findInSet(String.valueOf(loginUserId),fw.subStringIndex("|","MANAGE_USER_DEPT",1,1))
      + " or "
      + T9DBUtility.findInSet(loginUserDept,fw.subStringIndex("|","MANAGE_USER_DEPT",1,2))
      + " or "
      + T9DBUtility.findInSet("0",fw.subStringIndex("|","MANAGE_USER_DEPT",1,2))
       + " or "
      + T9DBUtility.findInSet("ALL_DEPT",fw.subStringIndex("|","MANAGE_USER_DEPT",1,2))
      + " or "
      + T9DBUtility.findInSet(loginUserPriv,fw.subStringIndex("|","MANAGE_USER_DEPT",1,3)) + "))"; 
    
    return result;
  }
 
  /**
   * 销毁工作
   * @throws Exception 
   */
  public int destroyFlowWork(Connection conn , String module,String runIds,int userId,String ip) throws Exception{
    int result = 0;
    if(runIds == null || "".equals(runIds)){
      return result;
    } 
    String[] runIdArray = runIds.split(",");
     for(int i = 0 ; i < runIdArray.length ; i++ ){
       if("".equals(runIdArray[i])){
         continue;
       }
       int runId = Integer.parseInt(runIdArray[i]);
       destroyFlowWork(conn, module, runId, userId ,ip );
       result ++;
     }
     return result;
  }
  public void destroyFlowWork(Connection conn , String module,int runId,int userId,String ip ) throws Exception{
    //工作流日志
    String content = "销毁此工作";
    T9FlowRunLogLogic frll = new T9FlowRunLogLogic();
    frll.runLog(runId, 0, 0, userId, 4, content, ip, conn);
    //删除附件
    deleteAttachByWork(conn, "FLOW_RUN", runId, module);
    //删除会签附件
    deleteAttachByWork(conn, "FLOW_RUN_FEEDBACK", runId, module);
    //删除记录
    deleteFlowWorkUtil(conn, "FLOW_RUN_FEEDBACK", runId);
    if (T9WorkFlowUtility.isSave2DataTable()) {
      T9FlowRunUtility ut = new T9FlowRunUtility();
      int flowId = ut.getFlowId(conn, runId);
      T9FormVersionLogic lo = new T9FormVersionLogic();
      int versionNo = lo.getVersionNo(conn, runId);
      T9FlowRunUtility u = new T9FlowRunUtility();
      int formId = u.getFormId(conn, flowId);
      int formSeqId = lo.getFormSeqId(conn, versionNo, formId);
      String tableName = T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE+ flowId  + "_" + formSeqId;
      deleteFlowWorkUtil(conn, "" +tableName, runId);
    } else {
      deleteFlowWorkUtil(conn, "FLOW_RUN_DATA", runId);
    }
    deleteFlowWorkUtil(conn, "FLOW_RUN_PRCS", runId);
    deleteFlowWorkUtil(conn, "FLOW_RUN", runId);
  }
  
  public void deleteFlowWorkUtil(Connection conn , String tableName, int runId) throws Exception{
    String sql = "delete from " + tableName + " where RUN_ID=" + runId;
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      //throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /**
   * 删除附件 工作附件或者会签附件
   * @param conn
   * @param tableName
   * @param runId
   * @param module
   * @throws Exception
   */
  public void deleteAttachByWork(Connection conn , String tableName , int runId,String module) throws Exception{
    //删除附件
    String sql = "SELECT ATTACHMENT_ID,ATTACHMENT_NAME from " + tableName + " where RUN_ID=" + runId;
    PreparedStatement ps = null;
    ResultSet rs = null; 
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        String attachmentId = rs.getString(1);
        String attachmentName = rs.getString(2);
        if(attachmentId == null || "".equals(attachmentId.trim())){
          continue;
        }
        deleteAttach(conn, module, attachmentId, attachmentName);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
  }
  /**
   * 删除附件 工作附件或者会签附件
   * @param conn
   * @param tableName
   * @param runId
   * @param module
   * @throws Exception
   */
  public int recoverWork(Connection conn  , String runIds,String module,int userId) throws Exception{
    //删除附件
    int result = 0;
    if(runIds == null || "".equals(runIds)){
      return result;
    }
    String sql = "update FLOW_RUN set DEL_FLAG=0 where RUN_ID IN (" + runIds + ")";
    PreparedStatement ps = null;
    ResultSet rs = null; 
    try {
      ps = conn.prepareStatement(sql);
      result = ps.executeUpdate();
      String[] runIdArray = runIds.split(",");
      for (int i = 0; i < runIdArray.length; i++) {
        int runId = Integer.parseInt(runIdArray[i]);
        String content = "还原此工作";
        T9FlowRunLogLogic frll = new T9FlowRunLogLogic();
        frll.runLog(runId, 0, 0, userId, 5, content, "", conn);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  /**
   * 删除单个附件
   * @param conn
   * @param module
   * @param attachmentId
   * @param attachmentName
   * @throws Exception
   */
  public void deleteAttachSing(Connection conn,String module,String attachmentId,String attachmentName) throws Exception{
    String fileName = "";
    String path = "";
    if(attachmentName.trim().endsWith("*")){
      attachmentName = attachmentName.trim().substring(0,attachmentName.trim().length() - 1);
    }
    if(attachmentId.trim().endsWith(",")){
      attachmentId = attachmentId.trim().substring(0,attachmentId.trim().length() - 1);
    }
    if(attachmentId != null && !"".equals(attachmentId)){
      String attIds[] = attachmentId.split("_");
      fileName = attIds[1] + "_" + attachmentName;
      path = T9NtkoCont.ATTA_PATH + File.separator +  module + File.separator +  attIds[0] + File.separator +  fileName;
    }
    File file = new File(path);
    if(file.exists()){
      file.delete();
    }
    String sql = "delete from ATTACHMENT_EIDT where ATTACHMENT_ID='" + attachmentId + "'";
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /**
   * 
   * @param conn
   * @param module
   * @param attachmentId
   * @param attachmentName
   * @throws Exception
   */
  public void deleteAttach(Connection conn,String module,String attachmentId,String attachmentName) throws Exception{
    String[] attachmentIds = attachmentId.split(",");
    String[] attachmentNames = attachmentName.split("\\*");
    for (int i = 0 ; i < attachmentIds.length ; i ++) {
      if("".equals(attachmentIds[i])){
        continue;
      }
      deleteAttachSing(conn, module, attachmentIds[i], attachmentNames[i]);
    }
  }
  
}
