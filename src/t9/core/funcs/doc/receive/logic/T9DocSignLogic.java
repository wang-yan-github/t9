package t9.core.funcs.doc.receive.logic;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.data.T9ExtDept;
import t9.core.esb.client.logic.T9DeptTreeLogic;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.doc.receive.data.T9DocConst;
import t9.core.funcs.doc.receive.data.T9DocReceive;
import t9.core.funcs.doc.receive.logic.T9DocReceiveLogic;
import t9.core.funcs.doc.receive.logic.T9DocSmsLogic;
import t9.core.funcs.doc.send.logic.T9DocLogic;
import t9.core.funcs.doc.util.T9DocUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
public class T9DocSignLogic{
  public String getSendMesage(T9Person user, Connection conn, Map request , String webroot, String isSign) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      String fromDeptName = request.get("fromDeptName") != null ? ((String[])request.get("fromDeptName"))[0] : null;
      String sendDocNo = request.get("sendDocNo") != null ? ((String[])request.get("sendDocNo"))[0] : null;
      String title = request.get("title") != null ? ((String[])request.get("title"))[0] : null;
      String endTime = request.get("endTime") != null ? ((String[])request.get("endTime"))[0] : null;
      String startTime = request.get("startTime") != null ? ((String[])request.get("startTime"))[0] : null;
      
      T9DocUtility docUtility = new T9DocUtility();
      if (!docUtility.haveAllRight(user, conn)) {
        String deptIds = T9WorkFlowUtility.getOutOfTail(docUtility.deptRight(user.getSeqId(), conn));
        if (docUtility.usingEsb() && docUtility.haveEsbRecRight(user, conn)) {
          T9EsbClientConfig config = T9EsbClientConfig.builder(webroot + T9EsbConst.CONFIG_PATH) ;
          T9DeptTreeLogic logic = new T9DeptTreeLogic();
          T9ExtDept dept = logic.getDeptByEsbUser(conn, config.getUserId());
          if (!T9Utility.isNullorEmpty(deptIds)) {
            deptIds += ",'" ;
          }
          deptIds +=  dept.getDeptId() + "'";
        }
        if (T9Utility.isNullorEmpty(deptIds)) {
          sql += " AND 1<>1 ";
        } else {
          sql += " AND TO_DEPT IN (" + deptIds + ") ";
        }
      }
      if (T9Utility.isNullorEmpty(isSign)) {
        isSign = "0";
      } 
      if ("0".equals(isSign)) {
        sql += " AND STATUS = '" + isSign + "'";
      } else {
        sql += " AND STATUS in  ('1','2') ";
      }
      if (!T9Utility.isNullorEmpty(title)) {
        sql += " and TITLE like '%" + T9DBUtility.escapeLike(title) + "%'";
      }
      if (!T9Utility.isNullorEmpty(sendDocNo)) {
        sql += " and DOC_FLOW_RUN.DOC like '%" + T9DBUtility.escapeLike(sendDocNo) + "%'";
      }
      if (!T9Utility.isNullorEmpty(fromDeptName)) {
        sql += " and (DEPARTMENT.DEPT_NAME like '%" + T9DBUtility.escapeLike(fromDeptName) + "%' OR EXT_DEPT .DEPT_NAME like '%" + T9DBUtility.escapeLike(fromDeptName) + "%' OR SEND_UNIT like '%" + T9DBUtility.escapeLike(fromDeptName) + "%')";
      }
      if(startTime != null && !"".equals(startTime)){
        startTime +=  " 00:00:00";
        String dbDateF = T9DBUtility.getDateFilter("SEND_TIME", startTime, " >= ");
        sql += " and " + dbDateF;
      }
      if(endTime != null && !"".equals(endTime)){
        endTime +=  " 23:59:59";
        String dbDateF = T9DBUtility.getDateFilter("SEND_TIME", endTime, " <= ");
        sql += " and " + dbDateF;
      }
      
      sql = "select"
        + " TITLE"
        + " , SEND_DOC_NO"
        
        + ", DOC_NAME"
        + ", DOC_ID"
        
        + " ,DOC_SEND.SEND_UNIT"
        + " ,SEND_TIME " 
        + " ,SIGN_TIME " 
        + " ,STATUS "
        + " ,IS_OUT "
        + " , DOC_SEND.SEQ_ID" 
        + " ,DOC_SEND.DEPT_ID"
        + " from DOC_SEND left outer join DEPARTMENT ON DEPARTMENT.SEQ_ID = DOC_SEND.DEPT_ID  left outer join EXT_DEPT ON EXT_DEPT.DEPT_ID = DOC_SEND.DEPT_ID  where  IS_CANCEL='0' " +  sql;
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      T9DeptTreeLogic logic2 = new T9DeptTreeLogic();
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        int isOut = T9Utility.cast2Long(record.getValueByName("isOut")).intValue();
        String fromDept = (String)record.getValueByName("fromDeptId");
        if (isOut == 1) {
          fromDept = logic2.getDeptName(conn, fromDept);
          record.updateField("fromDept", fromDept);
        } 
        //else {
          
          //deptName = logic.getDeptNameById(conn, Integer.parseInt(fromDept));
        //}
        
      }
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      throw ex;
    }
    return resualt.toString();
  }
  public void sign(Connection dbConn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    Timestamp time =  new  Timestamp(new Date().getTime());
    String update = "UPDATE DOC_SEND SET STATUS = '1' , SIGN_TIME=?  WHERE SEQ_ID = " + seqId ;
    PreparedStatement stm = null; 
    try { 
      stm = dbConn.prepareStatement(update);
      stm.setTimestamp(1, time);
      stm.executeUpdate();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, null, null); 
    } 
  }
  public boolean isOut(Connection conn, String seqId) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    String query = "select 1 from doc_send where seq_id = '" + seqId + "' and is_out = '1'";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        return true;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return false;
  }
  public void comeback(Connection conn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    //if (!isOut(conn , seqId)) {
      String update = "UPDATE DOC_SEND SET IS_CANCEL = '1' WHERE SEQ_ID = " + seqId ;
      T9WorkFlowUtility.updateTableBySql(update, conn);
    //}
  }
  public String getSendMesageDesktop(T9Person user, Connection conn,
      Map request, String webroot) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    String sql = "";
    Statement stm =null;
    ResultSet rs = null;//结果集


    int showLen = 10;
    int pageIndex = 1;
    try {
      
      T9DocUtility docUtility = new T9DocUtility();
      if (!docUtility.haveAllRight(user, conn)) {
        String deptIds = T9WorkFlowUtility.getOutOfTail(docUtility.deptRight(user.getSeqId(), conn));
        if (docUtility.usingEsb() && docUtility.haveEsbRecRight(user, conn)) {
          T9EsbClientConfig config = T9EsbClientConfig.builder(webroot + T9EsbConst.CONFIG_PATH) ;
          T9DeptTreeLogic logic = new T9DeptTreeLogic();
          T9ExtDept dept = logic.getDeptByEsbUser(conn, config.getUserId());
          if (!T9Utility.isNullorEmpty(deptIds)) {
            deptIds += ",'" ;
          }
          deptIds +=  dept.getDeptId() + "'";
        }
        if (T9Utility.isNullorEmpty(deptIds)) {
          sql += " AND 1<>1 ";
        } else {
          sql += " AND TO_DEPT IN (" + deptIds + ") ";
        }
      }
      sql += " AND STATUS = '0'";
      sql = "select"
        + " DOC_SEND.SEQ_ID,TITLE"
        + " , SEND_DOC_NO"
        + ", DOC_NAME"
        + ", DOC_ID"
        + " ,DOC_SEND.SEND_UNIT"
        + " ,SEND_TIME " 
        + " ,SIGN_TIME " 
        + " ,STATUS "
        + " ,IS_OUT "
        + " , DOC_SEND.SEQ_ID" 
        + " ,DOC_SEND.DEPT_ID"
        + " from DOC_SEND left outer join DEPARTMENT ON DEPARTMENT.SEQ_ID = DOC_SEND.DEPT_ID  left outer join EXT_DEPT ON EXT_DEPT.DEPT_ID = DOC_SEND.DEPT_ID   where  IS_CANCEL='0' " +  sql;
      stm = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
      rs = stm.executeQuery(sql);
      sb.append("{");
      sb.append("listData:[");
      int count = 0 ;
      T9DeptTreeLogic logic2 = new T9DeptTreeLogic();
      for (int i = 0; i < showLen && rs.next(); i++) { 
        sb.append("{");
        sb.append("title:\"" +   T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("TITLE"))) + "\"");
        int isOut = rs.getInt("IS_OUT");
        String fromDept = rs.getString("DEPT_ID");
        if (isOut == 1) {
          fromDept = logic2.getDeptName(conn, fromDept);
        } else {
          fromDept = rs.getString("SEND_UNIT");
        }
        sb.append(",sendDocNo:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("SEND_DOC_NO")))+ "\"");
        sb.append(",sendUnit:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(fromDept)) + "\"");
        sb.append(",seqId:" + rs.getInt("SEQ_ID"));
        sb.append("},");
        count++ ;
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    //结束索引
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);;
    }
    sb.append("}");
    return sb.toString();
  }
}
