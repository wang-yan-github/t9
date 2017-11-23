package t9.core.funcs.doc.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9DocFromDeptLogic {

  public boolean hasDept(Connection conn, String deptName) throws Exception {
    // TODO Auto-generated method stub
    String query = "select * from DOC_FROM_DEPT where FROM_DEPT_NAME='" + deptName + "'";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        return true;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return false;
  }

  public void addFromDept(Connection dbConn, String deptName, String deptId) throws Exception {
    // TODO Auto-generated method stub
    if (T9Utility.isNullorEmpty(deptId)) {
      deptId = "0";
    }
    String insert = "insert into  DOC_FROM_DEPT (FROM_DEPT_NAME, FROM_DEPT_ID) values ('" + deptName +"' , '" + deptId +"')";
    T9WorkFlowUtility.updateTableBySql(insert, dbConn);
  }

  public void updateFromDept(Connection dbConn, String deptName, String deptId) throws Exception {
    // TODO Auto-generated method stub
    if (!T9Utility.isNullorEmpty(deptId)) {
      String update = "update  DOC_FROM_DEPT set FROM_DEPT_ID = '"+deptId+"' where FROM_DEPT_NAME = '" + deptName +"'";
      T9WorkFlowUtility.updateTableBySql(update, dbConn);
    } 
  }

  public void delFromDept(Connection dbConn, String id) throws Exception {
    // TODO Auto-generated method stub
    String del = "delete from DOC_FROM_DEPT where seq_id = '" + id +"'";
    T9WorkFlowUtility.updateTableBySql(del, dbConn);
  }

  public StringBuffer getAllDept(Connection conn, Map request,
      String deptName) throws Exception {
    StringBuffer result = new StringBuffer();
    try {
      String query2 = "select SEQ_ID, FROM_DEPT_NAME, FROM_DEPT_ID FROM DOC_FROM_DEPT WHERE 1=1 ";
      if (!T9Utility.isNullorEmpty(deptName)) {
        query2 += " and FROM_DEPT_NAME like '%" + T9Utility.encodeLike(deptName) + "%'";
      }
      
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,query2);
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        //T9DbRecord record = pageDataList.getRecord(i);
      }
      result.append(pageDataList.toJson());
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return result;
  }

  public List<String> getDepts(Connection conn, String w) throws Exception {
    // TODO Auto-generated method stub
    String query = "select FROM_DEPT_NAME from DOC_FROM_DEPT where FROM_DEPT_NAME like '" + w + "%'";
    Statement stm = null;
    ResultSet rs = null;
    List<String> list = new ArrayList();
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        list.add(T9Utility.null2Empty(rs.getString(1)));
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
}
