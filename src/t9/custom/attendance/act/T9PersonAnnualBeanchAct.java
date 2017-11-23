package t9.custom.attendance.act;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.custom.attendance.logic.T9PersonAnnualBeachLogic;

/**
 * 年休假批量设置
 * @author Administrator
 *
 */
public class T9PersonAnnualBeanchAct{
  private T9PersonAnnualBeachLogic logic = new T9PersonAnnualBeachLogic();
  /**
   * 年休假批量设置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String insertBeanch(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
    
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIds = request.getParameter("userId");
      String annualDays = request.getParameter("annualDays");
      String changeDate = request.getParameter("changeDate");
      Date d = null;
      if(!T9Utility.isNullorEmpty(changeDate)){
        d = T9Utility.parseDate(changeDate);
      }
      int id = 0;
      if(!T9Utility.isNullorEmpty(annualDays)){
        id = Integer.parseInt(annualDays);
      }
      logic.insertBeanch(dbConn, userIds, id, d);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
