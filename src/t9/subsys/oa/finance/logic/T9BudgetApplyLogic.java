package t9.subsys.oa.finance.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.finance.data.T9BudgetApply;

public class T9BudgetApplyLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public int addBudgetApply(Connection dbConn,T9BudgetApply budgetApply) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, budgetApply);
    return 0;//T9CalendarLogic.getMaSeqId(dbConn, "GIFT_INSTOCK");
  }
  public void updateBudgetApply(Connection dbConn,T9BudgetApply budgetApply) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, budgetApply);
  }
  public void delBudgetApplyById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9BudgetApply.class, Integer.parseInt(seqId));
  }
  public List<T9BudgetApply> selectBudgetApply(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9BudgetApply> applyList =orm.loadListSingle(dbConn, T9BudgetApply.class, str);
    return applyList;
  }
  public List selectAllYearByDept(Connection dbConn,String deptId) throws Exception {
    T9ORM orm = new T9ORM();
    Statement stmt = null;
    ResultSet rs = null; 
    List yearList = new ArrayList();
    try {
      String queryStr = "select BUDGET_YEAR from BUDGET_APPLY where DEPT_ID='" + deptId + "' group by BUDGET_YEAR"  ;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        if(!T9Utility.isNullorEmpty(rs.getString("BUDGET_YEAR"))){
          yearList.add(rs.getString("BUDGET_YEAR")); 
        }
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return yearList;
  }
  public String selectTotal(Connection dbConn,String deptId,String year) throws Exception {
    T9ORM orm = new T9ORM();
    Statement stmt = null;
    ResultSet rs = null; 
    String budgetMoneyTotal = "0";
    try {
      String queryStr = "select sum(BUDGET_MONEY) as BUDGET_MONEY_TOTAL from BUDGET_APPLY where DEPT_ID='" + deptId + "' and BUDGET_YEAR = " + year;  ;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        if(rs.getString("BUDGET_MONEY_TOTAL")!=null){
          budgetMoneyTotal = rs.getString("BUDGET_MONEY_TOTAL");
        }
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return budgetMoneyTotal;
  }
  /**
   * 更新数据库中的文件
   * @param dbConn
   * @param attachmentId
   * @param attachmentName
   * @param seqId
   * @throws Exception
   */
  public void updateFile(Connection dbConn,String attachmentId,String attachmentName,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    String budgetMoneyTotal = "0";
    try {
      String sql = "update BUDGET_APPLY set ATTACHMENT_ID = ? ,ATTACHMENT_NAME = ? where SEQ_ID=?"   ;
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
   * 更新已结算状态
   * @param dbConn
   * @param attachmentId
   * @param attachmentName
   * @param seqId
   * @throws Exception
   */
  public void updateBudgetToSettle(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    String budgetMoneyTotal = "0";
    try {
      String sql = "update BUDGET_APPLY set SETTLE_FLAG = '1' where SEQ_ID = ?"   ;
      pstmt = dbConn.prepareStatement(sql);
      pstmt.setString(1, seqId);
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
  public String toSearchData(Connection conn,Map request,String deptId,String year) throws Exception{
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = dateFormat.format(new Date());
    String sql = "select b.SEQ_ID as SEQ_ID , b.BUDGET_MONEY as BUDGET_MONEY , p.USER_NAME as APPLY_NAME"
      + ",b.BUDGET_YEAR as BUDGET_YEAR ,b.DEPT_ID as DEPT_ID,d.DEPT_NAME as DEPT_NAME , b.BUDGET_DATE as BUDGET_DATE, b.BUDGET_ITEM as BUDGET_ITEM"
      +",b.MEMO as MEMO, b.IS_AUDIT as IS_AUDIT,b.SETTLE_FLAG  from BUDGET_APPLY b left outer join PERSON p on b.BUDGET_PROPOSER=p.SEQ_ID left outer join"
      + " DEPARTMENT d on b.DEPT_ID = d.SEQ_ID where 1 = 1";
    if(!deptId.equals("")){
      sql = sql + " and b.DEPT_ID = '" + deptId + "'";
    }
    if(year!=null&&!year.equals("")){
      sql = sql + " and b.BUDGET_YEAR = " + year;
    }
    sql = sql + " order by b.BUDGET_DATE";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  public T9BudgetApply selectBudgetApplyById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9BudgetApply apply =(T9BudgetApply) orm.loadObjSingle(dbConn, T9BudgetApply.class,Integer.parseInt(seqId));
    return apply;
  }
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData2(Connection conn,Map request,String deptId,String notAffair,int userId) throws Exception{
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = dateFormat.format(new Date());
    String sql = "select b.SEQ_ID as SEQ_ID , b.BUDGET_MONEY as BUDGET_MONEY , p.USER_NAME as APPLY_NAME"
      + ",b.BUDGET_YEAR as BUDGET_YEAR ,b.DEPT_ID as DEPT_ID,d.DEPT_NAME as DEPT_NAME , b.BUDGET_DATE as BUDGET_DATE, b.BUDGET_ITEM as BUDGET_ITEM"
      +", b.IS_AUDIT as IS_AUDIT ,b.SETTLE_FLAG as SETTLE_FLAG from BUDGET_APPLY b left outer join PERSON p on b.BUDGET_PROPOSER=p.SEQ_ID left outer join"
      + " DEPARTMENT d on b.DEPT_ID = d.SEQ_ID where b.NOT_AFFAIR = '" + notAffair + "'";
 
    if(notAffair!=null&&notAffair.equals("1")){
      sql = sql + " and " + T9DBUtility.findInSet(userId + "", "USE_AREA");
    }else{
      if(!deptId.equals("")){
        sql = sql + " and b.DEPT_ID = '" + deptId + "'";
      }
    }
    sql = sql + " order by b.BUDGET_DATE";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData3(Connection conn,Map request) throws Exception{
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = dateFormat.format(new Date());
    String sql = "select SEQ_ID , BUDGET_MONEY , BUDGET_ITEM,NOT_AFFAIR,SETTLE_FLAG from BUDGET_APPLY ";
    sql = sql + " order by BUDGET_DATE";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData3(Connection conn,Map request,String budgetItem,String budgetPropose) throws Exception{
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = dateFormat.format(new Date());
    String sql = "select b.SEQ_ID , b.BUDGET_MONEY , b.BUDGET_ITEM,p.USER_NAME,b.NOT_AFFAIR,b.SETTLE_FLAG from BUDGET_APPLY b left outer join PERSON p on b.BUDGET_PROPOSER = p.SEQ_ID where 1=1";
    if(!T9Utility.isNullorEmpty(budgetItem)){
      sql = sql + " and b.BUDGET_ITEM like '%" + T9DBUtility.escapeLike(budgetItem) + "%' " + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(budgetPropose)){
      sql = sql + " and p.USER_NAME like '%" + T9DBUtility.escapeLike(budgetPropose) + "%' " + T9DBUtility.escapeLike();
    }
    sql = sql + " order by b.BUDGET_DATE";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
}
