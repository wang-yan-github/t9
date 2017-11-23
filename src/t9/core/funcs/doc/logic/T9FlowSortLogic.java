package t9.core.funcs.doc.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowFormType;
import t9.core.funcs.doc.data.T9DocFlowSort;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9FlowSortLogic {
  public List<T9DocFlowSort> getFlowSort(Connection conn) throws Exception{
    List<T9DocFlowSort> list = new ArrayList();
    String query = "select * from "+ T9WorkFlowConst.FLOW_SORT +" ORDER BY SORT_NO ";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        int sortNo = rs.getInt("SORT_NO");
        String sortName = rs.getString("SORT_NAME");
        int sortParent = rs.getInt("SORT_PARENT");
        int deptId = rs.getInt("DEPT_ID");
        String haveChild = rs.getString("HAVE_CHILD");
        T9DocFlowSort flowSort = new T9DocFlowSort();
        flowSort.setSeqId(seqId);
        flowSort.setDeptId(deptId);
        flowSort.setSortNo(sortNo);
        flowSort.setSortParent(sortParent);
        flowSort.setSortName(sortName);
        flowSort.setHaveChild(haveChild);
        list.add(flowSort);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
  public T9DocFlowSort getFlowSortById(Connection conn , int sortId) throws Exception{
    String query = "select * from "+ T9WorkFlowConst.FLOW_SORT +" where seq_id="+ sortId +" ORDER BY SORT_NO ";
    Statement stm = null;
    ResultSet rs = null;
    T9DocFlowSort flowSort = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if  (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        int sortNo = rs.getInt("SORT_NO");
        String sortName = rs.getString("SORT_NAME");
        int sortParent = rs.getInt("SORT_PARENT");
        int deptId = rs.getInt("DEPT_ID");
        String haveChild = rs.getString("HAVE_CHILD");
        
        flowSort = new T9DocFlowSort();
        flowSort.setSeqId(seqId);
        flowSort.setDeptId(deptId);
        flowSort.setSortNo(sortNo);
        flowSort.setSortParent(sortParent);
        flowSort.setSortName(sortName);
        flowSort.setHaveChild(haveChild);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return flowSort;
  }
  public List<T9DocFlowSort> getFlowSortByIds(Connection conn , String sortId) throws Exception{
    String query = "select * from "+ T9WorkFlowConst.FLOW_SORT +" where seq_id in ("+ sortId +") ORDER BY SORT_NO ";
    Statement stm = null;
    ResultSet rs = null;
    ArrayList<T9DocFlowSort> list   = new ArrayList();
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while  (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        int sortNo = rs.getInt("SORT_NO");
        String sortName = rs.getString("SORT_NAME");
        int sortParent = rs.getInt("SORT_PARENT");
        int deptId = rs.getInt("DEPT_ID");
        String haveChild = rs.getString("HAVE_CHILD");
        
        T9DocFlowSort flowSort = new T9DocFlowSort();
        flowSort.setSeqId(seqId);
        flowSort.setDeptId(deptId);
        flowSort.setSortNo(sortNo);
        flowSort.setSortParent(sortParent);
        flowSort.setSortName(sortName);
        flowSort.setHaveChild(haveChild);
        list.add(flowSort);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
  public List<T9DocFlowSort> getFlowSortByDept(Connection conn , T9Person u) throws Exception{
    List<T9DocFlowSort> list = new ArrayList();
    T9WorkFlowUtility w = new T9WorkFlowUtility();
    String query = "select * from "+ T9WorkFlowConst.FLOW_SORT +" ";
    query += " ORDER BY SORT_NO ";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        int deptId = rs.getInt("DEPT_ID");
        if (!w.isHaveSortRight(deptId, u, conn)) {
          continue;
        }
        int seqId = rs.getInt("SEQ_ID");
        int sortNo = rs.getInt("SORT_NO");
        String sortName = rs.getString("SORT_NAME");
        int sortParent = rs.getInt("SORT_PARENT");
        String haveChild = rs.getString("HAVE_CHILD");
        T9DocFlowSort flowSort = new T9DocFlowSort();
        flowSort.setSeqId(seqId);
        flowSort.setDeptId(deptId);
        flowSort.setSortNo(sortNo);
        flowSort.setSortParent(sortParent);
        flowSort.setSortName(sortName);
        flowSort.setHaveChild(haveChild);
        list.add(flowSort);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
  public List<T9DocFlowSort> getFlowSortByDept(Connection conn , int deptId2) throws Exception{
    List<T9DocFlowSort> list = new ArrayList();
    String query = "select * from "+ T9WorkFlowConst.FLOW_SORT +" where DEPT_ID = "+ deptId2 +" ORDER BY SORT_NO ";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        int sortNo = rs.getInt("SORT_NO");
        String sortName = rs.getString("SORT_NAME");
        int sortParent = rs.getInt("SORT_PARENT");
        int deptId = rs.getInt("DEPT_ID");
        String haveChild = rs.getString("HAVE_CHILD");
        T9DocFlowSort flowSort = new T9DocFlowSort();
        flowSort.setSeqId(seqId);
        flowSort.setDeptId(deptId);
        flowSort.setSortNo(sortNo);
        flowSort.setSortParent(sortParent);
        flowSort.setSortName(sortName);
        flowSort.setHaveChild(haveChild);
        list.add(flowSort);
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
  public T9DocFlowSort getFlowSortById(int sortId , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    return (T9DocFlowSort)orm.loadObjSingle(conn, T9DocFlowSort.class, sortId);
  }
  /**
   * 组织导航菜单
   * @param id
   * @return
   * @throws Exception
   */
  public StringBuffer getSortJson(int id , Connection conn , T9Person u) throws Exception{
    StringBuffer sb = new StringBuffer();
    T9FlowFormLogic ffl = new T9FlowFormLogic();
    String seqIds = ffl.getIdBySort(conn, id);
    ArrayList<T9DocFlowFormType> typeList = (ArrayList<T9DocFlowFormType>) ffl.getFlowFormType(conn, seqIds);
    int count = 0;
    T9WorkFlowUtility w = new T9WorkFlowUtility();
    for(T9DocFlowFormType ftTmp : typeList){
      int deptId = ftTmp.getDeptId();
      if (!w.isHaveRight(deptId, u, conn)) {
        continue;
      }
      count ++;
      if(ftTmp.getFormName() == null){
        sb.append("{title:' '");
      }else{ 
        sb.append("{title:'" + ftTmp.getFormName() + "                                      '");
      }
      sb.append(",icon:imgPath + '/edit.gif'");
      sb.append(",action:actionFuntion");
      sb.append(",iconAction:iconActionFuntion");
      sb.append(",sortId:'" + id +"'");
      sb.append(",extData:'" + ftTmp.getSeqId() +"'}," );
    }
    if(count > 0){
      sb.deleteCharAt(sb.length() - 1);
    }  
    return sb;
  }
  /**
   * 根据流程分类id取得下面的流程数量
   * @param flowShort
   * @param conn
   * @return
   * @throws Exception 
   */
  public int getFlowTypeCount (int flowShort , Connection conn) throws Exception {
    int count = 0 ;
    String query = "select count(*) as count from "+ T9WorkFlowConst.FLOW_TYPE +" where"
      + " FLOW_SORT = " + flowShort;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        count = rs.getInt("count");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return count;
  }
}
