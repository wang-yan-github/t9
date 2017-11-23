package t9.subsys.oa.profsys.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9Project;

public class T9ProjectLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.profsys.act.T9ProjectLogic");
  public int addProject(Connection dbConn, T9Project project) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, project);
    return T9CalendarLogic.getMaSeqId(dbConn, "PROJECT");
  }
  public void updateProject(Connection dbConn, T9Project project) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, project);
    //return T9CalendarLogic.getMaSeqId(dbConn, "PROJECT");
  }
  public T9Project getProjectById(Connection dbConn, String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9Project project = (T9Project) orm.loadObjSingle(dbConn, T9Project.class, Integer.parseInt(seqId));
    return project;
  }
  /**
   * 修改打印状态

   * 
   * @return
   * @throws Exception
   */
  public static void printOut(Connection dbConn,String printStr) throws Exception {
    String sql = "update PROJECT set print_status='1' WHERE SEQ_ID in (" + printStr + ")";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps,null, log);
    }
  }
  public static List<T9Project> queryProject(Connection dbConn,String[] str) throws Exception{
    T9ORM orm = new T9ORM();
    List<T9Project> project = new ArrayList<T9Project>();
    project = orm.loadListSingle(dbConn,T9Project.class,str);
    return project;
  }
  /**
   * 更新数据库中的文件

   * @param dbConn
   * @param attachmentId
   * @param attachmentName
   * @param seqId
   * @throws Exception
   */
  public void updateFile(Connection dbConn,String tableName,String attachmentId,String attachmentName,String seqId) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    try {
      String sql = "update " + tableName + " set ATTACHMENT_ID = ? ,ATTACHMENT_NAME = ? where SEQ_ID=?"   ;
      pstmt = dbConn.prepareStatement(sql);
      pstmt.setString(1, attachmentId);
      pstmt.setString(2,attachmentName);
      pstmt.setString(3, seqId);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, rs, log);
    }
  }
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String projType) throws Exception{
     String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME, c.CLASS_DESC"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_ARRIVE_TIME,p.PROJ_LEAVE_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM c on p.PROJ_VISIT_TYPE = c.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID where p.PROJ_TYPE = '" + projType + "'";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  /** 
  * deptId串转换成dept_name串 
  * @return 
  * @throws Exception 
  */ 
  public static String deptStr(Connection dbConn,String deptId) throws Exception { 
  String strString = "全体部门";//传入得deptId串必须是：1,2,4 
  PreparedStatement ps = null; 
  ResultSet rs = null; 
  if (!deptId.equals("0") && !deptId.equals("ALL_DEPT") ) { 
    if(deptId.endsWith(",")){
      deptId = deptId.substring(0, deptId.length()-1);
    }
    String sql = "select dept_name from department where seq_id in (" + deptId + ")"; 
    strString = ""; 
    try{ 
      ps = dbConn.prepareStatement(sql); 
      rs = ps.executeQuery(); 
      while (rs.next()) { 
        strString += rs.getString("dept_name") + ","; 
      } 
      if (strString.length() > 0) { 
        strString = strString.substring(0,strString.length()-1); 
      } 
    }catch (Exception e) { 
      throw e; 
    }finally { 
      T9DBUtility.close(ps, rs, log); 
    } 
  } 
  return strString; 
  }
}
