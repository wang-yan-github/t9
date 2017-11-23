package t9.subsys.oa.profsys.act.out;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.oa.profsys.data.T9ProjectCalendar;
import t9.subsys.oa.profsys.logic.out.T9OutProjectCalendarLogic;

public class T9OutProjectCalendarAct {
  /**
   * 项目日程查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelectCalendar(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      T9ProjectCalendar calendar = new T9ProjectCalendar();
      String activeType = request.getParameter("activeType");
      String activeContent = request.getParameter("activeContent");
      String activeLeader = request.getParameter("activeLeader");
      String activePartner = request.getParameter("activePartner");
      String startTime = request.getParameter("startTime");
      String startTime1 = request.getParameter("startTime1");
      String endTime = request.getParameter("endTime");
      String endTime1 = request.getParameter("endTime1");
      String projCalendarType = request.getParameter("projCalendarType");
      calendar.setActiveType(activeType);
      calendar.setActiveContent(activeContent);
      calendar.setActiveLeader(activeLeader);
      calendar.setActivePartner(activePartner);
      calendar.setProjCalendarType(projCalendarType);
     
      //String projId = T9OutProjectCalendarLogic.profsysSelectCalendar(dbConn,calendar, startTime, startTime1,endTime,endTime1);
      //通用查询数据
      String data = T9OutProjectCalendarLogic.profsysCalendarList(dbConn,request.getParameterMap(),projCalendarType,calendar,startTime,startTime1,endTime,endTime1);
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
}
