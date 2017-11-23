package t9.custom.attendance.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.custom.attendance.data.T9PersonAnnualPara;
import t9.custom.attendance.logic.T9AnnualLeaveLogic;
import t9.custom.attendance.logic.T9PersonAnnualParaLogic;

public class T9PersonAnnualParaAct {
  /**
   * 
   * 查询用户年假ByUserId
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAnnualLeavePara(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      T9PersonAnnualParaLogic logic = new T9PersonAnnualParaLogic();
      Calendar cal = Calendar.getInstance();
      int year = cal.get(Calendar.YEAR);
      if(T9Utility.isNullorEmpty(userId)){
        userId = "";
      }
      String[] str = {"USER_ID = '" + userId + "'"};
      List< T9PersonAnnualPara> leaveList = logic.selectAnnualLeavePara(dbConn, str);
      String data = "";
      String type = "0";
      T9PersonAnnualPara annualPara = null;
      if(leaveList.size()>0){
        annualPara = leaveList.get(0);
        data = data + T9FOM.toJson(annualPara).toString();
        type = "1";
      }else{
        data = "{}";
      }
      T9AnnualLeaveLogic personanLogic = new T9AnnualLeaveLogic();
      String annualSumDays =  personanLogic.selectPersonAnnualDays(dbConn, userId, year + "");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,annualSumDays);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 更新或者新建年休假设置
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUpdateAnnualLeavePara(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      String annualDays = request.getParameter("annualDays");
      String changeDate = request.getParameter("changeDate");
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      T9PersonAnnualParaLogic logic = new T9PersonAnnualParaLogic();

      if(!T9Utility.isNullorEmpty(userId)){
        String[] str = {"USER_ID = '" + userId + "'"};
        List< T9PersonAnnualPara> leaveList = logic.selectAnnualLeavePara(dbConn, str);
        T9PersonAnnualPara annualPara =  new T9PersonAnnualPara();
        if(leaveList.size()>0){
          annualPara = leaveList.get(0);
          if(!T9Utility.isNullorEmpty(changeDate)){
            annualPara.setChangeDate(format.parse(changeDate));
          }
          if(!T9Utility.isNullorEmpty(annualDays)&&T9Utility.isInteger(annualDays)){
            annualPara.setAnnualDays(Integer.parseInt(annualDays));
          }
          logic.updateAnnualLeavePara(dbConn, annualPara);
        }else{
          if(!T9Utility.isNullorEmpty(changeDate)){
            annualPara.setChangeDate(format.parse(changeDate));
          }
          if(!T9Utility.isNullorEmpty(annualDays)&&T9Utility.isInteger(annualDays)){
            annualPara.setAnnualDays(Integer.parseInt(annualDays));
          }
          annualPara.setUserId(userId);
          logic.addAnnualLeavePara(dbConn, annualPara);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "{}");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
