package t9.subsys.oa.finance.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.codeclass.data.T9CodeItem;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.finance.data.T9BudgetApply;
import t9.subsys.oa.finance.data.T9BudgetDeptTotal;
import t9.subsys.oa.finance.logic.T9BudgetApplyLogic;
import t9.subsys.oa.finance.logic.T9BudgetDeptTotalLogic;
import t9.subsys.oa.finance.logic.T9ChargeExpenseLogic;
import t9.subsys.oa.finance.logic.T9FinanceApplyRecordLogic;
import t9.subsys.oa.giftProduct.instock.logic.T9GiftInstockLogic;

public class T9BudgetApplyAct {
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
      int curYear = cl.get(Calendar.YEAR);
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      String budgetYear = fileForm.getParameter("budgetYear");
      if(budgetYear==null||!T9Utility.isInteger(budgetYear)){
        budgetYear = curYear + "";
      }
      // 保存从文件柜、网络硬盘选择附件
      T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "finance");
      String attIdStr = sel.getAttachIdToString(",");
      String attNameStr = sel.getAttachNameToString("*");
      
      String budgetProposer = fileForm.getParameter("budgetProposer");
      String budgetItem = fileForm.getParameter("budgetItem");
      String deptId = fileForm.getParameter("deptId");
      String budgetAvailabein = fileForm.getParameter("budgetAvailabein");
      String budgetMoney = fileForm.getParameter("budgetMoney");
      if(budgetMoney!=null){
        budgetMoney = budgetMoney.replace(",", "");
      }
      String budgetInMoney = fileForm.getParameter("budgetInMoney");
      if(budgetInMoney!=null){
        budgetInMoney = budgetInMoney.replace(",", "");
      }
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
      String seqId = fileForm.getParameter("seqId");
      String clon = request.getParameter("clon");
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9BudgetApply budgetApply = new T9BudgetApply();
      String attIdStrTemp  = "";
      String attNameStrTemp = "";
      if(!T9Utility.isNullorEmpty(seqId)&&T9Utility.isInteger(seqId)&&clon!=null&&clon.equals("1")){
        T9BudgetApply budgetApplyTemp  = tbal.selectBudgetApplyById(dbConn, seqId);  
        if(budgetApplyTemp!=null){
          attIdStrTemp = budgetApplyTemp.getAttachmentId();
          attNameStrTemp = budgetApplyTemp.getAttachmentName(); 
        }
      }

      budgetApply.setBudgetProposer(budgetProposer);
      budgetApply.setBudgetItem(budgetItem);
      budgetApply.setDeptId(deptId);
      budgetApply.setBudgetYear(Integer.parseInt(budgetYear));
      budgetApply.setDeptId(deptId);
      budgetApply.setSettleFlag("0");
      budgetApply.setBudgetDate(new Date());
      if (budgetAvailabein != null && !budgetAvailabein.trim().equals("")) {
        budgetApply.setBudgetAvailablein(dateFormat.parse(budgetAvailabein));
      }
      if (budgetMoney != null && T9Utility.isNumber(budgetMoney)) {
        budgetApply.setBudgetMoney(Double.parseDouble(budgetMoney));
      }
      if (budgetInMoney != null && T9Utility.isNumber(budgetInMoney)) {
        budgetApply.setBudgetInMoney(Double.parseDouble(budgetInMoney));
      }
      budgetApply.setNotAffair(notAffair);
      budgetApply.setUseArea(useArea);
      budgetApply.setMemo(memo);
      budgetApply.setAttachmentId(ATTACHMENT_ID_OLD);
      budgetApply.setAttachmentName(ATTACHMENT_NAME_OLD);
      budgetApply.setDetailContent(detailContent);
      budgetApply.setDetailContentIn(detailContentIn);
      budgetApply.setDeptAuditDirector(deptAuditDirector);
      if (deptAuditDate != null && T9Utility.isDay(deptAuditDate)) {
        budgetApply.setDeptAuditDate(dateFormat.parse(deptAuditDate));
      }
      budgetApply.setDeptAuditContent(deptAuditContent);
      
      
      
      Iterator<String> iKeys = fileForm.iterateFileFields();
      String filePath = T9SysProps.getAttachPath()  + File.separator + "finance" + File.separator + dateFormat2.format(new Date()); // T9SysProps.getAttachPath()
      String attachmentId = "";
      String attachmentName = "";
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        String regName = fileName;

        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
        String rand = emul.getRandom();
        attachmentId =  dateFormat2.format(new Date()) + "_" + attachmentId + rand+",";
        attachmentName = attachmentName + fileName+"*";
        fileName = rand + "_" + fileName;
        fileForm.saveFile(fieldName, filePath + File.separator + fileName);
      }
      attachmentId = attachmentId + attIdStr;
      attachmentName = attachmentName + attNameStr;
      if(!T9Utility.isNullorEmpty(attIdStrTemp)){
        attachmentId = attachmentId + "," + attIdStrTemp;
      }
      if(!T9Utility.isNullorEmpty(attachmentName)){
        attachmentName = attachmentName + "*" + attNameStrTemp;
      }
      budgetApply.setAttachmentId(attachmentId);
      budgetApply.setAttachmentName(attachmentName);
      
      tbal.addBudgetApply(dbConn, budgetApply);
   
  
      String path = request.getContextPath();
      
      if(clon!=null&&clon.equals("1")){
        response.sendRedirect(path
            + "/subsys/oa/finance/budget/plan/clon_edit.jsp?type=1");
      }else{
        response.sendRedirect(path
            + "/subsys/oa/finance/budget/plan/addbudget.jsp?type=1");
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
   * 更新预算
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateBudgetApply(HttpServletRequest request,
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
      // 保存从文件柜、网络硬盘选择附件
      T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "finance");
      String attIdStr = sel.getAttachIdToString(",");
      String attNameStr = sel.getAttachNameToString("*");
     
      String budgetProposer = fileForm.getParameter("budgetProposer");
      String budgetItem = fileForm.getParameter("budgetItem");
      String deptId = fileForm.getParameter("deptId");
      String budgetAvailabein = fileForm.getParameter("budgetAvailabein");
      String budgetMoney = fileForm.getParameter("budgetMoney");
      if(budgetMoney!=null){
        budgetMoney = budgetMoney.replace(",", "");
      }
      String budgetInMoney = fileForm.getParameter("budgetInMoney");
      if(budgetInMoney!=null){
        budgetInMoney = budgetInMoney.replace(",", "");
      }
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
      if(budgetYear==null||!T9Utility.isInteger(budgetYear)){
        budgetYear = year + "";
      }
      String seqId = fileForm.getParameter("seqId");
      if(seqId!=null&&T9Utility.isInteger(seqId)){
        T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
        T9BudgetApply budgetApply = new T9BudgetApply();
        //先查出数据库的附件，然后加上
        String attIdStrTemp = "";
        String attNameStrTemp = "";
        T9BudgetApply budgetApplyTemp  = tbal.selectBudgetApplyById(dbConn, seqId);  
        if(budgetApplyTemp!=null){
          attIdStrTemp  = budgetApplyTemp.getAttachmentId();
          attNameStrTemp = budgetApplyTemp.getAttachmentName();
        }

        
        budgetApply.setSeqId(Integer.parseInt(seqId));
        budgetApply.setBudgetProposer(budgetProposer);
        budgetApply.setBudgetItem(budgetItem);
        budgetApply.setDeptId(deptId);
        budgetApply.setBudgetYear(Integer.parseInt(budgetYear));
        budgetApply.setDeptId(deptId);
        budgetApply.setBudgetDate(new Date());
        if (budgetAvailabein != null && !budgetAvailabein.trim().equals("")) {
          budgetApply.setBudgetAvailablein(dateFormat.parse(budgetAvailabein));
        }
        if (budgetMoney != null && T9Utility.isNumber(budgetMoney)) {
          budgetApply.setBudgetMoney(Double.parseDouble(budgetMoney));
        }
        if (budgetInMoney != null && T9Utility.isNumber(budgetInMoney)) {
          budgetApply.setBudgetInMoney(Double.parseDouble(budgetInMoney));
        }
        budgetApply.setNotAffair(notAffair);
        budgetApply.setUseArea(useArea);
        budgetApply.setMemo(memo);
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
          attachmentId =  dateFormat2.format(new Date()) + "_" + attachmentId + rand+",";
          attachmentName = attachmentName + fileName+"*";
          fileName = rand + "_" + fileName;
          fileForm.saveFile(fieldName, filePath + File.separator + fileName);
        }
        
        attachmentId = attachmentId + attIdStr;
        attachmentName = attachmentName + attNameStr;
        if(attIdStrTemp!=null&&!attIdStrTemp.equals("")){
          attachmentId = attIdStrTemp  + "," + attachmentId;
        }
        if(attNameStrTemp!=null&&!attNameStrTemp.equals("")){
          attachmentName = attNameStrTemp  + "*" + attachmentName;
        }

        budgetApply.setAttachmentId(attachmentId);
        budgetApply.setAttachmentName(attachmentName);
        tbal.updateBudgetApply(dbConn, budgetApply);
      }
      

  
      String path = request.getContextPath();
      response.sendRedirect(path
          + "/subsys/oa/finance/budget/plan/clon_edit.jsp?type=1");
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
   * 查询预算情况
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectBudgetApply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();

      /*
       * //短信smsType, content, remindUrl, toId, fromId T9SmsBack sb = new
       * T9SmsBack(); sb.setSmsType("5"); sb.setContent("请查看日程安排！内容："+content);
       * sb.setRemindUrl("/t9/core/funcs/calendar/mynote.jsp?seqId="+maxSeqId+
       * "&openFlag=1&openWidth=300&openHeight=250");
       * sb.setToId(String.valueOf(userId)); sb.setFromId(userId);
       * T9SmsUtil.smsBack(dbConn, sb);
       */
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9ManageOutLogic tmol = new T9ManageOutLogic();
      T9PersonLogic tpl = new T9PersonLogic();
      List<T9BudgetApply> applyList = new ArrayList<T9BudgetApply>();
      String data = "[";
      if (user.getUserPriv() != null && user.getUserPriv().equals("1")
          && user.getUserId().equals("admin")) {
        String[] str = {};
        applyList = tbal.selectBudgetApply(dbConn, str);
      } else {
        String[] str = { "DEPT_ID = " + user.getDeptId() };
        applyList = tbal.selectBudgetApply(dbConn, str);
      }
      for (int i = 0; i < applyList.size(); i++) {
        T9BudgetApply ba = applyList.get(i);
        String applyName = "";
        String deptName = "";
        if (ba.getDeptId() != null) {
          deptName = T9Utility.encodeSpecial(tmol.selectByUserIdDept(dbConn, ba
              .getDeptId()));
        }
        if (ba.getBudgetProposer() != null) {
          applyName = T9Utility.encodeSpecial(tpl.getNameBySeqIdStr(ba
              .getBudgetProposer(), dbConn));
        }
        data = data
            + T9FOM.toJson(ba).substring(0, T9FOM.toJson(ba).length() - 1)
            + ",applyName:\"" + applyName + "\",deptName:\"" + deptName
            + "\"},";
      }
      if (applyList.size() > 0) {
        data = data.substring(0, data.length() - 1);
      }
      data = data + "]";

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
   * 删除预算byID
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  deleteBudgetById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      if(seqId!=null&&T9Utility.isInteger(seqId)){
        tbal.delBudgetApplyById(dbConn, seqId);
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
   * 查询预算总金额情况
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectBudget(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String year = request.getParameter("year");
      Calendar cl = Calendar.getInstance();
      int curYear = cl.get(Calendar.YEAR);
      if(year==null||year.equals("")){
        year = curYear + "";
      }
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
  
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9BudgetDeptTotalLogic tbatl = new T9BudgetDeptTotalLogic();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = { "DEPT_ID='" + user.getDeptId() + "'", "CUR_YEAR=" + year };
      List<T9BudgetDeptTotal> totalList = tbatl.selectBudgetTotal(dbConn, str);

      String total = "0";
      String isDpetTotal = "1";
      if (totalList.size() > 0) {
        total = totalList.get(0).getTotal() +"";
        total = T9Utility.getFormatedStr(total, 2);
        isDpetTotal = "0";
      } else {
        total = tbal.selectTotal(dbConn, String.valueOf(user.getDeptId()), year);
        total = T9Utility.getFormatedStr(total, 2);
      }
      String data = "{year:" + year + ",deptTotal:\"" + total +"\",isDpetTotal:"+isDpetTotal+"}";
      request.setAttribute("isDpetTotal",isDpetTotal);
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
      dbConn = requestDbConn.getSysDbConn();
      String year = request.getParameter("year");
      Calendar cl = Calendar.getInstance();
      int curYear = cl.get(Calendar.YEAR);
      if(year==null||year.equals("")){
        year = curYear + "";
      }
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      String data = tbal.toSearchData(dbConn, request.getParameterMap(), String
          .valueOf(user.getDeptId()), year);
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
   * 分页
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
    public String queryBudgetList(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
        String data = tbal.toSearchData3(dbConn, request.getParameterMap());
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
     * 分页业务管理系统
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
      public String queryBudgetListToProject(HttpServletRequest request,
          HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request
              .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
          String budgetItem = request.getParameter("budgetItem");
          String budgetPropose = request.getParameter("budgetPropose");
          String data = tbal.toSearchData3(dbConn, request.getParameterMap(),budgetItem,budgetPropose);
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
   * 费用管理分页
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
    public String queryBudget(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String year = request.getParameter("year");
        String notAffair = request.getParameter("notAffair");
        if(notAffair!=null&&notAffair.equals("")){
          notAffair = "0";
        }
        Calendar cl = Calendar.getInstance();
        T9Person user = (T9Person) request.getSession().getAttribute(
            T9Const.LOGIN_USER);
        int userId = user.getSeqId();
        T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
        String data = tbal.toSearchData2(dbConn, request.getParameterMap(), String
            .valueOf(user.getDeptId()), notAffair,userId);
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
   * 得到有效的费用预算金额=财务人员确定额度-本部门本年的预算金额的总额
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAvailable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptId = request.getParameter("deptId");
      String year = request.getParameter("year");

      Calendar cl = Calendar.getInstance();
      int curYear = cl.get(Calendar.YEAR);
      if (year == null&&year.equals("")) {
        year = curYear + "";
      }
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9BudgetDeptTotalLogic tbatl = new T9BudgetDeptTotalLogic();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = { "DEPT_ID='" + user.getDeptId() + "'",
          "CUR_YEAR=" + year };
      List<T9BudgetDeptTotal> totalList = tbatl.selectBudgetTotal(dbConn, str);

      String DeptTotal = "0";
      String type = "1";// 可以新增预算
      double available = 0;
      if (totalList.size() > 0) {
        type = "2";
        DeptTotal = totalList.get(0).getTotal() + "";
        String budgetTotal = tbal.selectTotal(dbConn, String.valueOf(user
            .getDeptId()), year);
        available = Double.parseDouble(DeptTotal)
            - Double.parseDouble(budgetTotal);
      }
      String data = "{type:" + type + ",availableMoney:" + available + "}";
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
   * 得到预算项目的所有类型
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      // String seqId = request.getParameter("seqId");
      String GIFT_PROTYPE = request.getParameter("GIFT_PROTYPE");
      // 根据seqId（codeClass） 得到所有的codeItem

      T9GiftInstockLogic giftLogic = new T9GiftInstockLogic();
      String data = "[";
      List<T9CodeItem> itemList = new ArrayList<T9CodeItem>();
      if (GIFT_PROTYPE != null && !GIFT_PROTYPE.equals("")) {
        // T9CodeClass codeClass = giftLogic.getCodeClass(dbConn,
        // Integer.parseInt(seqId));
        // if(codeClass!=null&&codeClass.getClassNo()!=null&&!codeClass.getClassNo().trim().equals("")){
        // String[] str = {"CLASS_NO = '" + codeClass.getClassNo() +
        // "' order by SORT_NO"};
        itemList = giftLogic.getCodeItem(dbConn, GIFT_PROTYPE);
        for (int i = 0; i < itemList.size(); i++) {
          T9CodeItem item = itemList.get(i);
          data = data + T9FOM.toJson(item) + ",";
        }

      }

      if (itemList.size() > 0) {
        data = data.substring(0, data.length() - 1);
      }
      data = data + "]";
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
   * 查询预算ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectBudgetById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    String type = request.getParameter("type");//1为查看详情2为克隆3为编辑，4为打印预览
    if(type==null){
      type = "";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String data = "{";
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9PersonLogic personLogic = new T9PersonLogic();
      T9BudgetApply tba = null;
      if(seqId!=null&&!seqId.equals("")){
       tba = tbal.selectBudgetApplyById(dbConn, seqId);
      }
      request.setAttribute("budgetApply", tba);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    if(type.equals("1")){
      return "/subsys/oa/finance/budget/plan/querybudget.jsp";
    }
    if(type.equals("2")){
      return "/subsys/oa/finance/budget/plan/clon_edit.jsp?clon_edit="+type;
    }
    if(type.equals("3")){
      return "/subsys/oa/finance/budget/plan/clon_edit.jsp?clon_edit="+type;
    }
    if(type.equals("4")){
      return "/subsys/oa/finance/budget/plan/printbudget.jsp";
    }
    
    return "";
  }
  /***
   * 根据budgetId得到领用和报销总金额

   * @return
   * @throws Exception 
   */
  public String chequeTotalByBudgetId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String budgetId = request.getParameter("budgetId");
    String type = request.getParameter("type");
    if(budgetId==null){
      budgetId = "";
    }
    if(type==null){
      type = "";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FinanceApplyRecordLogic tfarl = new T9FinanceApplyRecordLogic();
      T9ChargeExpenseLogic tcel = new T9ChargeExpenseLogic();
      String total = "0.00";
       if(type.equals("2")){//已报销总金额
         double  expenseTotal= tcel.sunMoneyByBudgetId(dbConn, budgetId);
         total = expenseTotal + "";//T9Utility.getFormatedStr(expenseTotal,2);
       }else{
         total  = tfarl.chequeTotalByBudgetId(dbConn, budgetId);
         //total = T9Utility.getFormatedStr(chequeTotal, 2);
       }
      String data = "{total:\""+total+"\"}";
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
  /***
   * 更新预算的附件ById

   * @return
   * @throws Exception 
   */
  public String deleleFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String attachId = request.getParameter("attachId");
      String attachName = request.getParameter("attachName");
      if(seqId==null){
        seqId = "";
      }
      if(attachId==null){
        attachId = "";
      }
      if(attachName==null){
        attachName = "";
      }
      T9BudgetApply tba = null;
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      String updateFlag = "0";
      if(seqId!=null&&!seqId.equals("")){
       tba = tbal.selectBudgetApplyById(dbConn, seqId);
       if(tba!=null){
         String attachmentId = tba.getAttachmentId();
         String attachmentName = tba.getAttachmentName();
         if(attachmentId==null){
           attachmentId = "";
         }
         if(attachmentName==null){
           attachmentName = "";
         }
         String[] attachmentIdArray = attachmentId.split(",");
         String[] attachmentNameArray = attachmentName.split("\\*");
         String newAttachmentId = "";
         String newAttachmentName = "";
         for (int i = 0; i < attachmentIdArray.length; i++) {
           if(!attachmentIdArray[i].equals(attachId)){
             newAttachmentId = newAttachmentId +attachmentIdArray[i] + ",";
           }
         }
         for (int i = 0; i < attachmentNameArray.length; i++) {
           if(!attachmentNameArray[i].equals(attachName)){
             newAttachmentName = newAttachmentName +attachmentNameArray[i] + "*";
           }
         }
         
         tbal.updateFile(dbConn, newAttachmentId, newAttachmentName, seqId);
         updateFlag = "1";
       }
      }
      String data = "{updateFlag:"+updateFlag+"}";
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
   * //得到每个部门的所有年份的详细预算信息
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAllYearBudgetByDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9BudgetApplyLogic tbal = new T9BudgetApplyLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String deptId = request.getParameter("deptId");//得到部门id如果为空则为当前用户的部门 ID
      if(T9Utility.isNullorEmpty(deptId)){
        deptId = user.getDeptId() + "";
      }
      //得到部门申请过预算的所有年份
      List yearList = new ArrayList();
      yearList = tbal.selectAllYearByDept(dbConn, deptId);
      String data = "[";
      //循环所有的年份
      for (int i = 0; i < yearList.size(); i++) {
        String year = (String) yearList.get(i);
        data = data + "{year:" + year + "},";
      }
      if(yearList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
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
