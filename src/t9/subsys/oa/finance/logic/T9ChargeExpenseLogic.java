package t9.subsys.oa.finance.logic;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.finance.data.T9ChargeExpense;

public class T9ChargeExpenseLogic {
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String expenseSelect(Connection dbConn,Map request,String deptId,String chargeUser,Date statrTime,Date endTime,String chargeItem,String financeAuditUser,String chargeMoney,String expense) throws Exception {   
    String sql = "select p.SEQ_ID,b.DEPT_NAME,sn.user_name,bu.BUDGET_ITEM,p.CHARGE_MONEY,p.CHARGE_DATE,"
      + "p.RUN_ID,p.IS_PRINT,p.EXPENSE,p.OF_EX,p.CHARGE_ITEM,p.CHARGE_MEMO,p.DEPT_AUDIT_USER,p.DEPT_AUDIT_DATE,"
      + "p.DEPT_AUDIT_CONTENT,p.FINANCE_AUDIT_USER,p.FINANCE_AUDIT_DATE,p.FINANCE_AUDIT_CONTENT,"
      + "p.CHARGE_YEAR,p.COST_ID,p.PROJ_SIGN,p.BUDGET_ID,p.MAKE_WASTE,p.SETTLE_FLAG"
      + " from charge_expense p left outer join department b on p.DEPT_ID=b.SEQ_ID "
      + " left outer join person sn on sn.SEQ_ID=p.CHARGE_USER "
      + " left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where 1=1 ";
    if (!T9Utility.isNullorEmpty(expense) && !expense.equals("3")) {
      sql += " and p.EXPENSE='" + expense + "'";
    }
    if (expense.equals("3")) {
      sql += " and p.OF_EX='1'";
    }
    if (!T9Utility.isNullorEmpty(deptId)) {
      sql += " and p.DEPT_ID='" + deptId + "'";
    }
    if (!T9Utility.isNullorEmpty(chargeItem)) {
      sql += " and bu.BUDGET_ITEM like '%" + T9Utility.encodeLike(chargeItem) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(chargeUser)) {
      sql += " and p.CHARGE_USER='" + chargeUser + "'";
    }
    if (!T9Utility.isNullorEmpty(financeAuditUser)) {
      sql += " and p.FINANCE_AUDIT_USER='" + financeAuditUser + "'";
    }
    if (!T9Utility.isNullorEmpty(chargeMoney)) {
      sql += " and p.CHARGE_MONEY >=" + chargeMoney;
    }
    if (statrTime != null) {
      String str =  T9DBUtility.getDateFilter("p.CHARGE_DATE", T9Utility.getDateTimeStr(statrTime), ">=");
      sql += " and " + str;
    }
    if (endTime != null) {
      String str =  T9DBUtility.getDateFilter("p.CHARGE_DATE", T9Utility.getDateTimeStr(endTime), "<=");
      sql += " and " + str;
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String expenseSelect2(Connection dbConn,Map request,String deptId,String chargeUser,Date statrTime,Date endTime,String chargeItem,String financeAuditUser,String chargeMoney,String expense,int year) throws Exception {
    String sql = "select p.SEQ_ID,b.DEPT_NAME,sn.user_name,bu.BUDGET_ITEM,p.CHARGE_MONEY,p.CHARGE_DATE,"
      + "p.RUN_ID,p.IS_PRINT,p.EXPENSE,p.OF_EX,p.CHARGE_ITEM,p.CHARGE_MEMO,p.DEPT_AUDIT_USER,p.DEPT_AUDIT_DATE,"
      + "p.DEPT_AUDIT_CONTENT,p.FINANCE_AUDIT_USER,p.FINANCE_AUDIT_DATE,p.FINANCE_AUDIT_CONTENT,"
      + "p.CHARGE_YEAR,p.COST_ID,p.PROJ_SIGN,p.BUDGET_ID,p.MAKE_WASTE,p.SETTLE_FLAG"
      + " from charge_expense p left outer join department b on p.DEPT_ID=b.SEQ_ID "
      + " left outer join person sn on sn.SEQ_ID=p.CHARGE_USER " 
      + " left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where 1=1 ";
    //    if (!T9Utility.isNullorEmpty(expense) && !expense.equals("3")) {
    //      sql += " and EXPENSE=" + expense;
    //    }
    //    if (expense.equals("3")) {
    //      sql += " and OF_EX=1";
    //    }
    if (!T9Utility.isNullorEmpty(deptId)) {
      sql += " and p.DEPT_ID='" + deptId + "'";
    }
    if (!T9Utility.isNullorEmpty(chargeItem)) {
      sql += " and bu.BUDGET_ITEM like '%" + T9Utility.encodeLike(chargeItem) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(chargeUser)) {
      sql += " and p.CHARGE_USER='" + chargeUser + "'";
    }
    if (!T9Utility.isNullorEmpty(financeAuditUser)) {
      sql += " and p.FINANCE_AUDIT_USER='" + financeAuditUser + "'";
    }
    if (!T9Utility.isNullorEmpty(chargeMoney)) {
      sql += " and p.CHARGE_MONEY >=" + chargeMoney;
    }
    if (statrTime != null) {
      String str =  T9DBUtility.getDateFilter("p.CHARGE_DATE", T9Utility.getDateTimeStr(statrTime), ">=");
      sql += " and " + str;
    }
    if (endTime != null) {
      String str =  T9DBUtility.getDateFilter("p.CHARGE_DATE", T9Utility.getDateTimeStr(endTime), "<=");
      sql += " and " + str;
    }
    if (year > 0) {
      sql += " and p.CHARGE_YEAR=" + year;
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件修改状态数据
   * @return
   * @throws Exception 
   */
  public void updateExpense(Connection dbConn,String seqIdStr) throws Exception {
    String seqId = "";
    for (int i = 0 ; i < seqIdStr.split(",").length; i++) {
      if (i == seqIdStr.split(",").length - 1) {
        seqId += seqIdStr.split(",")[i];
      } else {
        seqId += seqIdStr.split(",")[i] + ",";
      }
    }
    String sql = "update CHARGE_EXPENSE set expense='1' where SEQ_ID in (" + seqId + ")";
    ResultSet rs = null;
    PreparedStatement stmt = null ;                                                                 
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  /***
   * 根据条件修改状态数据
   * @return
   * @throws Exception 
   */
  public void makeWaste(Connection dbConn,String seqIdStr) throws Exception {
    String seqId = "";
    for (int i = 0 ; i < seqIdStr.split(",").length; i++) {
      if (i == seqIdStr.split(",").length - 1) {
        seqId += seqIdStr.split(",")[i];
      } else {
        seqId += seqIdStr.split(",")[i] + ",";
      }
    }
    String sql = "update CHARGE_EXPENSE set OF_EX='1' where SEQ_ID in (" + seqId + ")";
    ResultSet rs = null;
    PreparedStatement stmt = null ;                                                                 
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }

  /***
   * 根据条件修改状态数据
   * @return
   * @throws Exception 
   */
  public void updatePrint(Connection dbConn,String seqIdStr) throws Exception {
    String seqId = "";
    for (int i = 0 ; i < seqIdStr.split(",").length; i++) {
      if (i == seqIdStr.split(",").length - 1) {
        seqId += seqIdStr.split(",")[i];
      } else {
        seqId += seqIdStr.split(",")[i] + ",";
      }
    }
    String sql = "update CHARGE_EXPENSE set IS_PRINT='1' where SEQ_ID in (" + seqId + ")";
    ResultSet rs = null;
    PreparedStatement stmt = null ;                                                                 
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  } 
  /***
   * 总金额
   * @return
   * @throws Exception 
   */
  public double sunMoney(Connection dbConn,String deptId,int year) throws Exception {
    String sql = "select sum(charge_money) from charge_expense where dept_id=? and charge_year=?";
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    double money = 0.00;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,deptId);
      stmt.setInt(2,year);
      rs = stmt.executeQuery();
      if(rs.next()){
        money = rs.getDouble(1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return money;
  }
  /***
   * 根据budgetId得到总金额

   * @return
   * @throws Exception 
   */
  public double sunMoneyByBudgetId(Connection dbConn,String budgetId) throws Exception {
    String sql = "select sum(charge_money) from charge_expense where BUDGET_ID=? and (MAKE_WASTE = '0' or MAKE_WASTE is null )";
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    double money = 0.00;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,budgetId);
      rs = stmt.executeQuery();
      if(rs.next()){
        money = rs.getDouble(1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return money;
  }

  /***
   * 根据条件查询数据
   * @return
   * @throws Exception 
   */
  public List<T9ChargeExpense> printSeqId(Connection dbConn,String seqIdStr) throws Exception {
    String seqId = "";
    for (int i = 0 ; i < seqIdStr.split(",").length; i++) {
      if (i == seqIdStr.split(",").length - 1) {
        seqId += seqIdStr.split(",")[i];
      } else {
        seqId += seqIdStr.split(",")[i] + ",";
      }
    }
    String sql = "select p.SEQ_ID as seqId,b.SEQ_ID,b.DEPT_NAME as beptName,sn.SEQ_ID,sn.user_name as userName,"
      + "bu.seq_Id,bu.BUDGET_ITEM as budgetItem,p.CHARGE_DATE as chargeDate,p.CHARGE_ITEM as chargeItem,p.BUDGET_ID"
      + " from charge_expense p "
      + " left outer join department b on p.DEPT_ID=b.SEQ_ID "
      + " left outer join person sn  on sn.SEQ_ID=p.CHARGE_USER " 
      + " left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where p.SEQ_ID in (" + seqId +")";
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9ChargeExpense expense = null;
    List<T9ChargeExpense> list = new ArrayList<T9ChargeExpense>();                                                                  
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        expense = new T9ChargeExpense(); 
        expense.setSeqId(rs.getInt("seqId"));
        expense.setDeptId(rs.getString("beptName"));
        expense.setChargeDate(rs.getDate("chargeDate"));
        expense.setChargeUser(rs.getString("userName"));
        expense.setChargeMemo(rs.getString("budgetItem"));
        expense.setChargeItem(rs.getString("chargeItem"));
        list.add(expense);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return list;
  }
  /***
   * 工作流


   * @return
   * @throws Exception 
   */
  public void addFlow(Connection dbConn,T9ChargeExpense expense) {
    String sql = "insert into charge_expense(DEPT_ID,CHARGE_USER,CHARGE_DATE,CHARGE_ITEM,CHARGE_MONEY," 
      + "CHARGE_MEMO,DEPT_AUDIT_USER,DEPT_AUDIT_DATE,DEPT_AUDIT_CONTENT,"
      + "FINANCE_AUDIT_USER,FINANCE_AUDIT_DATE,FINANCE_AUDIT_CONTENT,RUN_ID,"
      + "CHARGE_YEAR,PROJ_ID,COST_ID,PROJ_SIGN,IS_PRINT,EXPENSE,OF_EX,BUDGET_ID,MAKE_WASTE,SETTLE_FLAG)" 
      + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    ResultSet rs = null;
    PreparedStatement stmt = null;
    try {
      stmt= dbConn.prepareStatement(sql);
      stmt.setString(1,expense.getDeptId());
      stmt.setString(2,expense.getChargeUser());
      stmt.setDate(3,(Date)expense.getChargeDate());
      stmt.setString(4,expense.getChargeItem());
      stmt.setDouble(5,expense.getChargeMoney());
      stmt.setString(6,expense.getChargeMemo());
      stmt.setString(7,expense.getDeptAuditUser());
      stmt.setDate(8,(Date)expense.getDeptAuditDate());
      stmt.setString(9,expense.getDeptAuditContent());
      stmt.setString(10,expense.getFinanceAuditUser());
      stmt.setDate(11,(Date)expense.getFinanceAuditDate());
      stmt.setString(12,expense.getFinanceAuditContent());
      stmt.setInt(13,expense.getRunId());
      stmt.setInt(14,expense.getChargeYear());
      stmt.setInt(15,expense.getProjId());
      stmt.setInt(16,expense.getCostId());
      stmt.setInt(17,expense.getProjSign());
      stmt.setString(18,"0");
      stmt.setString(19,expense.getExpense());
      stmt.setString(20,"0");
      stmt.setString(21,expense.getBudgetId());
      stmt.setString(22,"0");
      stmt.setString(23,"0");
      stmt.executeUpdate();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
}
