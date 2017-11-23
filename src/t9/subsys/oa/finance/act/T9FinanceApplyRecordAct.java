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
import t9.subsys.oa.finance.data.T9FinanceApplyRecord;
import t9.subsys.oa.finance.logic.T9FinanceApplyRecordLogic;
public class T9FinanceApplyRecordAct {
  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String applySelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String claimerName = request.getParameter("claimerName");
      String momey = request.getParameter("money");
      String item = request.getParameter("item");
      String aTime = request.getParameter("statrTime");
      String aEndTime = request.getParameter("endTime");
      String chequeAccount = request.getParameter("chequeAccount");
      String financeDirectorName = request.getParameter("financeDirectorName");

      Date statrTime = null;
      Date endTime = null;
      if (!T9Utility.isNullorEmpty(aTime)) {
        statrTime = Date.valueOf(aTime);
      }
      if (!T9Utility.isNullorEmpty(aEndTime)) {
        endTime = Date.valueOf(aEndTime);
      }
      T9FinanceApplyRecordLogic gift = new T9FinanceApplyRecordLogic();
      String data = gift.applySelect(dbConn,request.getParameterMap(),claimerName,momey,item,statrTime,endTime,chequeAccount,financeDirectorName);
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
  public String applySelect2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String claimerName = request.getParameter("claimerName");
      String momey = request.getParameter("money").toString();
      String item = request.getParameter("item");
      String aTime = request.getParameter("statrTime");
      String aEndTime = request.getParameter("endTime");
      String chequeAccount = request.getParameter("chequeAccount");
      String financeDirectorName = request.getParameter("financeDirectorName");
      String year2 = request.getParameter("year");
      String deptId = request.getParameter("deptId");
      Date statrTime = null;
      Date endTime = null;
      int year = 0;
      if (!T9Utility.isNullorEmpty(aTime)) {
        statrTime = Date.valueOf(aTime);
      }
      if (!T9Utility.isNullorEmpty(aEndTime)) {
        endTime = Date.valueOf(aEndTime);
      }
      if (!T9Utility.isNullorEmpty(year2)) {
        year = Integer.parseInt(year2);
      }

      T9FinanceApplyRecordLogic gift = new T9FinanceApplyRecordLogic();
      String data = gift.applySelect2(dbConn,request.getParameterMap(),claimerName,momey,item,statrTime,endTime,chequeAccount,financeDirectorName,year,deptId);
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
   *根据budgetId查询(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String applySelectByBudgetId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String budgetId = request.getParameter("budgetId");
      if(budgetId==null){
        budgetId = "";
      }
      T9FinanceApplyRecordLogic gift = new T9FinanceApplyRecordLogic();
      String data = gift.applySelectByBudgetId(dbConn,request.getParameterMap(),budgetId);
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
   * 删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delCheque(HttpServletRequest request,
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
      T9FinanceApplyRecord record = new T9FinanceApplyRecord();
      record.setSeqId(seqId);
      orm.deleteSingle(dbConn, record);
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
   * 根据ID查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String chequeDetail(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId")); 
      T9FinanceApplyRecord record = (T9FinanceApplyRecord)orm.loadObjSingle(dbConn,T9FinanceApplyRecord.class,seqId);
      request.setAttribute("record",record);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/finance/cheque/news/chequeDetail.jsp";
  }
  /**
   * 添加数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String addRecord(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9FinanceApplyRecord record = (T9FinanceApplyRecord) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();//orm映射数据库
      orm.saveSingle(dbConn,record);
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
   * 添加数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String printOut(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("printStr");
      T9FinanceApplyRecordLogic recordLogic = new T9FinanceApplyRecordLogic();
      recordLogic.updateExpense(dbConn,seqIdStr);
      List<T9FinanceApplyRecord> records = recordLogic.printSeqId(dbConn,seqIdStr);
      request.setAttribute("records",records);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据失败");
      throw e;
    }
    return "/subsys/oa/finance/cheque/news/print.jsp";
  }
  
  /**
   * 根据ID查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String editCheque(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId")); 
      T9FinanceApplyRecord record = (T9FinanceApplyRecord)orm.loadObjSingle(dbConn,T9FinanceApplyRecord.class,seqId);
      request.setAttribute("record",record);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/subsys/oa/finance/cheque/news/editCheque.jsp";
  }
  
  /**
   * 根据ID修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String editRecord(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      T9FinanceApplyRecord record = (T9FinanceApplyRecord)T9FOM.build(request.getParameterMap());
      orm.updateComplex(dbConn, record);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
