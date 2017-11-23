package t9.core.funcs.workplan.act;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workplan.data.T9WorkPlan;
import t9.core.funcs.workplan.logic.T9ExportLogic;
import t9.core.funcs.workplan.logic.T9WorkLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9Export {
  public String exportCsv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("工作计划.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9WorkLogic workLogic = new T9WorkLogic();
      //实列实体层plan
      T9WorkPlan plan = new T9WorkPlan();
      //接受对应页面值
      String name = request.getParameter("NAME");
      String content = request.getParameter("CONTENT");
      String deptParentDesc = request.getParameter("deptParent");
      String managerDesc = request.getParameter("manager");
      String leader1Desc = request.getParameter("leader1");
      String leader2Desc = request.getParameter("leader2");
      String type = request.getParameter("WORK_TYPE");
      String REMARK = request.getParameter("REMARK");
      String leader3Desc = request.getParameter("leader3");
      String statrTime = request.getParameter("statrTime");
      String endTime = request.getParameter("endTime");
      
      if (!T9Utility.isNullorEmpty(statrTime)) {
        plan.setStatrTime(Date.valueOf(statrTime));
      }
      if (!T9Utility.isNullorEmpty(endTime)) {
        plan.setEndTime(Date.valueOf(endTime));
      }

      //将接收的数据进行封装到实体中
      plan.setContent(content);
      plan.setName(name);
      plan.setDeptParentDesc(deptParentDesc);
      plan.setManagerDesc(managerDesc);
      plan.setLeader1Desc(leader1Desc);
      plan.setLeader2Desc(leader2Desc);
      plan.setLeader3Desc(leader3Desc);
      plan.setType(type);
      plan.setRemark(REMARK);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //调用worklist方法，返回LIST集合
      List<T9WorkPlan> worklist = workLogic.selectRes(dbConn,plan ,loginUser); 
      T9ExportLogic expl = new T9ExportLogic();
      ArrayList<T9DbRecord > dbL = expl.getDbRecord(worklist,dbConn);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
}
