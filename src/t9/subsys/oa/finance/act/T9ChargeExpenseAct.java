package t9.subsys.oa.finance.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.finance.data.T9ChargeExpense;
import t9.subsys.oa.finance.logic.T9ChargeExpenseLogic;

public class T9ChargeExpenseAct {
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String expenseSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String deptId = request.getParameter("deptId");
      String chargeUser = request.getParameter("chargeUser");
      String chargeDate = request.getParameter("chargeDate");
      String chargeDate2 = request.getParameter("chargeDate2");
      String chargeItem = request.getParameter("chargeItem");
      String financeAuditUser = request.getParameter("financeAuditUser");
      String momey = request.getParameter("chargeMoney");
      String expense = request.getParameter("expense");
      
      Date statrTime = null;
      Date endTime = null;
      if (!T9Utility.isNullorEmpty(chargeDate)) {
        statrTime = Date.valueOf(chargeDate);
      }
      if (!T9Utility.isNullorEmpty(chargeDate2)) {
        endTime = Date.valueOf(chargeDate2);
      }

      T9ChargeExpenseLogic gift = new T9ChargeExpenseLogic();
      String data = gift.expenseSelect(dbConn,request.getParameterMap(),deptId,chargeUser,statrTime,endTime,chargeItem,financeAuditUser,momey,expense);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  } 
  
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String expenseSelect2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String deptId = request.getParameter("deptId");
      String chargeUser = request.getParameter("chargeUser");
      String chargeDate = request.getParameter("chargeDate");
      String chargeDate2 = request.getParameter("chargeDate2");
      String chargeItem = request.getParameter("chargeItem");
      String financeAuditUser = request.getParameter("financeAuditUser");
      String momey = request.getParameter("chargeMoney");
      String expense = request.getParameter("expense");
      String year2 = request.getParameter("year");
      Date statrTime = null;
      Date endTime = null;
      int year = 0;

      if (!T9Utility.isNullorEmpty(chargeDate)) {
        statrTime = Date.valueOf(chargeDate);
      }
      if (!T9Utility.isNullorEmpty(chargeDate2)) {
        endTime = Date.valueOf(chargeDate2);
      }
      if (!T9Utility.isNullorEmpty(year2)) {
        year = Integer.parseInt(year2);
      }

      T9ChargeExpenseLogic gift = new T9ChargeExpenseLogic();
      String data = gift.expenseSelect2(dbConn,request.getParameterMap(),deptId,chargeUser,statrTime,endTime,chargeItem,financeAuditUser,momey,expense,year);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  } 
  /**
   * 修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String expenseOut(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("printStr");
      T9ChargeExpenseLogic expense = new T9ChargeExpenseLogic();
      expense.updateExpense(dbConn,seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/subsys/oa/finance/expense/charge/news/noDone.jsp";
  }
  /**
   * 修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String makeWaste(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("printStr");
      T9ChargeExpenseLogic expense = new T9ChargeExpenseLogic();
      expense.makeWaste(dbConn,seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/subsys/oa/finance/expense/charge/news/done.jsp";
  }
  /**
   * 修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String updatePrint(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("printStr");
      T9ChargeExpenseLogic expense = new T9ChargeExpenseLogic();
      expense.updatePrint(dbConn,seqIdStr);
      List<T9ChargeExpense> list = expense.printSeqId(dbConn, seqIdStr);
      request.setAttribute("list",list);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/subsys/oa/finance/expense/charge/news/print.jsp";
  }
  
  /**
   * 删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delCharge(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ChargeExpense expense = new T9ChargeExpense();
      expense.setSeqId(seqId);
      orm.deleteSingle(dbConn,expense);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑，查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectCharge(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId")); 
      T9ChargeExpense expense = (T9ChargeExpense)orm.loadObjSingle(dbConn,T9ChargeExpense.class,seqId);
      request.setAttribute("expense",expense);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/finance/expense/charge/news/expense.jsp";
  }
  /**
   * 编辑，查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectMoney(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptId = request.getParameter("deptId");
      int year = Integer.parseInt(request.getParameter("year")); 
      T9ChargeExpenseLogic expense = new T9ChargeExpenseLogic();
      double money = expense.sunMoney(dbConn,deptId,year);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "";
  }
  /**
   * 根据ID修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String editExpense(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      T9ChargeExpense expense = (T9ChargeExpense)T9FOM.build(request.getParameterMap());
      orm.updateComplex(dbConn,expense);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 添加数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String addExpense(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ChargeExpense expense = (T9ChargeExpense) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();//orm映射数据库
      orm.saveSingle(dbConn,expense);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 编辑，查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectExpense(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId")); 
      T9ChargeExpense expense = (T9ChargeExpense)orm.loadObjSingle(dbConn,T9ChargeExpense.class,seqId);
      request.setAttribute("expense",expense);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/finance/expense/charge/news/chargeDetai.jsp";
  }
}
