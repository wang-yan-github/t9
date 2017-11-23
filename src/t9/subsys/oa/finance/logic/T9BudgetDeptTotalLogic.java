package t9.subsys.oa.finance.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.finance.data.T9BudgetDeptTotal;

public class T9BudgetDeptTotalLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public int addBudgetTotal(Connection dbConn,T9BudgetDeptTotal budgetTotal) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, budgetTotal);
    return 0;//T9CalendarLogic.getMaSeqId(dbConn, "GIFT_INSTOCK");
  }
  public List<T9BudgetDeptTotal> selectBudgetTotal(Connection dbConn,String str[]) throws Exception {
    List<T9BudgetDeptTotal> totalList = new ArrayList<T9BudgetDeptTotal>();
    T9ORM orm = new T9ORM();
    totalList = orm.loadListSingle(dbConn, T9BudgetDeptTotal.class,str );
    return totalList;//T9CalendarLogic.getMaSeqId(dbConn, "GIFT_INSTOCK");
  }
  public void updateTotal(Connection dbConn,String deptId,String year,String money) throws Exception {
    T9ORM orm = new T9ORM();
    Statement stmt = null;
    ResultSet rs = null; 
    String budgetMoneyTotal = "0";
    money = money.replace(",", "");
    try {
      String sql = "update BUDGET_DEPT_TOTAL set TOTAL = TOTAL + "+money +" where DEPT_ID='" + deptId + "' and CUR_YEAR = " + year;  ;
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页根据预算的SEQ_ID (BUDGET_ID)
   * @return
   * @throws Exception 
   */
  public  List<Map<String,String>> expenseSelectByBudgetId(Connection dbConn,Map request,String budgetId) throws Exception {
    List<Map<String,String>> expenseList = new ArrayList<Map<String,String>>();
    String sql = "select p.SEQ_ID,b.DEPT_NAME,sn.USER_NAME,bu.BUDGET_ITEM,p.CHARGE_MONEY,p.CHARGE_DATE"
      + ",snp.USER_NAME,p.RUN_ID,p.IS_PRINT,p.EXPENSE,p.CHARGE_MEMO"
      + " from charge_expense p left outer join department b on p.DEPT_ID=b.SEQ_ID "
      + " left outer join person sn on sn.SEQ_ID=p.CHARGE_USER " 
      + " left outer join person snp on snp.SEQ_ID=p.FINANCE_AUDIT_USER " 
      + " left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where p.BUDGET_ID = '" + budgetId + "' ";//and (MAKE_WASTE = '0' or MAKE_WASTE is null)";
   // T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
  // T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    //return pageDataList.toJson();
    Statement stmt = null;
    ResultSet rs = null; 
    String budgetMoneyTotal = "0";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        Map<String,String> map = new HashMap<String,String>();
        map.put("seqId",rs.getString(1));
        map.put("deptName",rs.getString(2));
        map.put("userName",rs.getString(3));
        map.put("budgetItem",rs.getString(4));
        map.put("chargeMoney",rs.getString(5));
        map.put("chargeDate",T9Utility.getDateTimeStr(rs.getTimestamp(6)));
        map.put("financeAuditUser",rs.getString(7));
        map.put("runId",rs.getString(8));
        map.put("isPrint",rs.getString(9));
        map.put("expense",rs.getString(10));
        map.put("chargeMemo",rs.getString(11));
        expenseList.add(map);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }

    return expenseList;
  }
}
