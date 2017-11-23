package t9.subsys.oa.finance.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.act.T9CalendarAct;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.finance.data.T9BudgetApply;
import t9.subsys.oa.finance.data.T9BudgetDeptTotal;
import t9.subsys.oa.finance.logic.T9BudgetApplyLogic;
import t9.subsys.oa.finance.logic.T9BudgetDeptTotalLogic;
import t9.subsys.oa.finance.logic.T9ChargeExpenseLogic;

public class T9BudgetDeptTotalAct {
  /**
   * 新建预算年份
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addBudgetTotal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      
    /*    //短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("5");
        sb.setContent("请查看日程安排！内容："+content);
        sb.setRemindUrl("/t9/core/funcs/calendar/mynote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
        sb.setToId(String.valueOf(userId));
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);*/
      T9BudgetDeptTotalLogic tbal = new T9BudgetDeptTotalLogic();
      T9BudgetDeptTotal deptTotal = new T9BudgetDeptTotal();
      tbal.addBudgetTotal(dbConn, deptTotal);
      String data = "{}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询预算总金额情况
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectTotal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String year = request.getParameter("year");
      String deptId = request.getParameter("deptId");
      Calendar cl = Calendar.getInstance();
      int curYear = cl.get(Calendar.YEAR);
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      if(year==null||year.equals("")){
        year = curYear + "";
      }
      if(deptId==null||deptId.equals("")){
        deptId = user.getDeptId()+"";
      }

      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9BudgetDeptTotalLogic tbatl = new T9BudgetDeptTotalLogic();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = { "DEPT_ID='" + deptId + "'", "CUR_YEAR=" + year };
      List<T9BudgetDeptTotal> totalList = tbatl.selectBudgetTotal(dbConn, str);

      String total  = "0.00";
      String isDpetTotal = "1";
      DecimalFormat numFormatG = new DecimalFormat("#,##0.00");
      if (totalList.size() > 0) {
       // double totalMoney = totalList.get(0).getTotal();
        total = totalList.get(0).getTotal() +"";//T9Utility.getFormatedStr(totalMoney, 2);
        isDpetTotal = "0";
      } else {
        total = tbal.selectTotal(dbConn, deptId, year);
       // double doubleTotal = 0;
        //if(T9Utility.isNumber(total)){
         // doubleTotal = Double.parseDouble(total);
        //}
        //total = numFormatG.format(doubleTotal);
      }
      T9ChargeExpenseLogic tcel = new T9ChargeExpenseLogic();
      double useMoney =  tcel.sunMoney(dbConn, deptId, Integer.parseInt(year));
      String data = "{year:" + year + ",deptTotal:\"" + total +"\",isDpetTotal:\""+ isDpetTotal+"\",useMoney:\""+useMoney +"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 确认额度
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setBudget(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String year = request.getParameter("year");
      String deptId = request.getParameter("deptId");
      String total = request.getParameter("total");
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9BudgetDeptTotalLogic tbatl = new T9BudgetDeptTotalLogic();
      if(year!=null&&!year.equals("")&&deptId!=null&&!deptId.equals("")&&total!=null&&!total.equals("")){
        String[] str = { "DEPT_ID='" + deptId + "'", "CUR_YEAR=" + year };
        List<T9BudgetDeptTotal> totalList = tbatl.selectBudgetTotal(dbConn, str);
        if (totalList.size() > 0) {

        } else {
          T9BudgetDeptTotal tbdt = new T9BudgetDeptTotal();
          tbdt.setCurYear(Integer.parseInt(year));
          tbdt.setDeptId(deptId);
          total = total.replace(",", "");
          if(T9Utility.isNumber(total)){
            tbdt.setTotal(Double.parseDouble(total));
          }
         
          tbatl.addBudgetTotal(dbConn, tbdt);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "{}");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建预算
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addBudgetApply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMM");
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      Calendar cl = Calendar.getInstance();
      int year = cl.get(Calendar.YEAR);
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      String budgetProposer = fileForm.getParameter("budgetProposer");
      String budgetItem = fileForm.getParameter("budgetItem");
      String deptId = fileForm.getParameter("deptId");
      String budgetAvailabein = fileForm.getParameter("budgetAvailabein");
      String budgetMoney = fileForm.getParameter("budgetMoney");
      String budgetInMoney = fileForm.getParameter("budgetInMoney");
      String notAffair = fileForm.getParameter("notAffair");
      String useArea = fileForm.getParameter("useArea");
      String memo = fileForm.getParameter("memo");
      String ATTACHMENT_ID_OLD = fileForm.getParameter("ATTACHMENT_ID_OLD");
      String ATTACHMENT_NAME_OLD = fileForm.getParameter("ATTACHMENT_NAME_OLD");
      String detailContent = fileForm.getParameter("detailContent");
      String detailContentIn = fileForm.getParameter("detailContentIn");
      String deptAuditDirector = fileForm.getParameter("deptAuditDirector");
      String deptAuditDate = fileForm.getParameter("deptAuditDate");
      String deptAuditContent = fileForm.getParameter("deptAuditConent");
      String budgetYear = fileForm.getParameter("budgetYear");
      String clon = fileForm.getParameter("clon");
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9BudgetDeptTotalLogic tbdtl = new T9BudgetDeptTotalLogic();
      T9BudgetApply budgetApply = new T9BudgetApply();
      budgetApply.setBudgetProposer(budgetProposer);
      budgetApply.setBudgetItem(budgetItem);
      if(deptId==null||deptId.equals("")){
        deptId = user.getDeptId()+"";
      }
      budgetApply.setDeptId(deptId);
      budgetApply.setBudgetDate(new Date());
      if(budgetYear==null||budgetYear.equals("")){
        budgetYear = String.valueOf(year);
      }
      budgetApply.setBudgetYear(Integer.parseInt(budgetYear));
      if (budgetAvailabein != null && !budgetAvailabein.trim().equals("")) {
        budgetApply.setBudgetAvailablein(dateFormat.parse(budgetAvailabein));
      }  
      budgetMoney = budgetMoney.replace(",", "");
      if (budgetMoney != null && T9Utility.isNumber(budgetMoney)) {
        budgetApply.setBudgetMoney(Double.parseDouble(budgetMoney));
      }
      budgetInMoney = budgetInMoney.replace(",", "");
      if (budgetInMoney != null && T9Utility.isNumber(budgetInMoney)) {
        budgetApply.setBudgetInMoney(Double.parseDouble(budgetInMoney));
      }
      budgetApply.setNotAffair(notAffair);
      budgetApply.setUseArea(useArea);
      budgetApply.setMemo(memo);
      budgetApply.setSettleFlag("0");
      budgetApply.setAttachmentId(ATTACHMENT_ID_OLD);
      budgetApply.setAttachmentName(ATTACHMENT_NAME_OLD);
      budgetApply.setDetailContent(detailContent);
      budgetApply.setDetailContentIn(detailContentIn);
      budgetApply.setDeptAuditDirector(deptAuditDirector);
      if (deptAuditDate != null && T9Utility.isDay(deptAuditDate)) {
        budgetApply.setDeptAuditDate(dateFormat.parse(deptAuditDate));
      }
      budgetApply.setDeptAuditContent(deptAuditContent);
      
      
      String attachmentId = "";
      String attachmentName = "";
      Iterator<String> iKeys = fileForm.iterateFileFields();
      String filePath = T9SysProps.getAttachPath()  + File.separator + "finance" + File.separator + dateFormat2.format(new Date()); // T9SysProps.getAttachPath()
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        String regName = fileName;

        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
        String rand = emul.getRandom();
        attachmentId = attachmentId + rand+",";
        attachmentName = attachmentName + fileName+"*";
        fileName = rand + "_" + fileName;
        fileForm.saveFile(fieldName, filePath + File.separator + fileName);

      }
      budgetApply.setAttachmentId(attachmentId);
      budgetApply.setAttachmentName(attachmentName);
      tbal.addBudgetApply(dbConn, budgetApply);
      
      //查询某个部门和年份是不是已确认
      String[] str = {"DEPT_ID='" + deptId + "'", "CUR_YEAR = "+budgetYear };
      List<T9BudgetDeptTotal> totalList =  tbdtl.selectBudgetTotal(dbConn, str);
     //更新确认额度total总金额
      if(totalList.size()>0){
        tbdtl.updateTotal(dbConn, deptId, budgetYear, budgetMoney);
      }

      String path = request.getContextPath();
      
      if(clon!=null&&clon.equals("1")){
       // response.sendRedirect(path
           // + "/subsys/oa/finance/budget/plan/clon_edit.jsp?type=1");
      }else{
        response.sendRedirect(path
            + "/subsys/oa/finance/budget/addBudget.jsp?type=1");
      }
    
      /*
       * //短信smsType, content, remindUrl, toId, fromId T9SmsBack sb = new
       * T9SmsBack(); sb.setSmsType("5"); sb.setContent("请查看日程安排！内容："+content);
       * sb.setRemindUrl("/t9/core/funcs/calendar/mynote.jsp?seqId="+maxSeqId+
       * "&openFlag=1&openWidth=300&openHeight=250");
       * sb.setToId(String.valueOf(userId)); sb.setFromId(userId);
       * T9SmsUtil.smsBack(dbConn, sb);
       */
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "");

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "";
  }
  /**
   * 分页
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
    public String queryDeptBudget(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        T9Person user = (T9Person) request.getSession().getAttribute(
            T9Const.LOGIN_USER);
        int userId = user.getSeqId();
        dbConn = requestDbConn.getSysDbConn();
        String year = request.getParameter("year");
        String deptId = request.getParameter("deptId");
        Calendar cl = Calendar.getInstance();
        
        int curYear = cl.get(Calendar.YEAR);
        if(year==null||year.equals("")){
          year = curYear + "";
        }
        if(deptId==null||deptId.equals("")){
          deptId = user.getDeptId() + "";
        }
  
        T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
        String data = tbal.toSearchData(dbConn, request.getParameterMap(), deptId, year);
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
    public String expenseSelectByBudgetId(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String budgetId = request.getParameter("budgetId");
        if(budgetId==null){
          budgetId = "";
        }
        T9BudgetDeptTotalLogic gift = new T9BudgetDeptTotalLogic();
        List<Map<String,String>> expenseList = gift.expenseSelectByBudgetId(dbConn,request.getParameterMap(),budgetId);
        String data = T9CalendarAct.getJson(expenseList);
  /*      String data = gift.expenseSelectByBudgetId(dbConn,request.getParameterMap(),budgetId);
        PrintWriter pw = response.getWriter();
        pw.println(data);
        pw.flush();*/
       // request.setAttribute("expenseList", expenseList);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
}
