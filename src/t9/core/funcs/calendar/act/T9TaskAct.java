package t9.core.funcs.calendar.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.data.T9Task;
import t9.core.funcs.calendar.logic.T9TaskLogic;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9TaskAct {
  /**
   * 新建任务
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addTask(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      int userId = user.getSeqId();
      T9Task task = new T9Task();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String remindTime = request.getParameter("remindTime");
      String finishTime = request.getParameter("finishTime");
      String content = request.getParameter("content");
      String taskNo = request.getParameter("taskNo");
      String totalTime = request.getParameter("totalTime");
      String useTime = request.getParameter("useTime");
      String rate = request.getParameter("rate");
      String color = request.getParameter("color");
      String important = request.getParameter("important");
      String taskType = request.getParameter("taskType");
      String taskStatus = request.getParameter("taskStatus");
      String subject = request.getParameter("subject");
      String smsRemind = request.getParameter("smsRemind");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      if (rate.equals("")) {
        rate = "0";
      }
      if (!beginDate.equals("")) {
        task.setBeginDate(dateFormat1.parse(beginDate));
      }
      if (!endDate.equals("")) {
        task.setEndDate(dateFormat1.parse(endDate));
      }
      if (!finishTime.equals("")) {
        task.setFinishTime(dateFormat.parse(finishTime));
      }
      task.setUserId(String.valueOf(userId));
      task.setColor(color);
      task.setImportant(important);
      task.setRate(rate);
      task.setContent(content);
      task.setTaskStatus(taskStatus);
      task.setTaskType(taskType);
      task.setSubject(subject);
      if (taskNo.equals("")) {
        taskNo = "0";
      }
      task.setTaskNo(Integer.parseInt(taskNo));
      if (totalTime.equals("")) {
        totalTime = "0";
      }
      task.setTotalTime(totalTime);
      if (useTime.equals("")) {
        useTime = "0";
      }
      task.setUseTime(useTime);
      T9TaskLogic ttl = new T9TaskLogic();
      int maxSeqId = ttl.addTask(dbConn, task);
      // 短信smsType, content, remindUrl, toId, fromId
      if (smsRemind != null && !T9Utility.isNullorEmpty(remindTime)) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("5");
        sb.setSendDate(T9Utility.parseDate(remindTime));
        sb.setContent("请查看我的任务！标题：" + subject);
        sb.setRemindUrl("/core/funcs/calendar/tasknote.jsp?seqId=" + maxSeqId
            + "&openFlag=1&openWidth=300&openHeight=250");
        sb.setToId(String.valueOf(userId));
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if (moblieSmsRemind != null && !T9Utility.isNullorEmpty(remindTime)) {
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn, String.valueOf(userId), userId,
            "我的任务：" + content, new Date());
      }
      // request.setAttribute(T9ActionKeys.RET_DATA, "data");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path + "/core/funcs/calendar/task.jsp");
    // return "/core/funcs/calendar/task.jsp";
    return null;

  }

  /*
   * 修改任务
   */
  public String updateTask(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      int userId = user.getSeqId();
      T9Task task = new T9Task();
      String seqId = request.getParameter("seqId");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String remindTime = request.getParameter("remindTime");
      String finishTime = request.getParameter("finishTime");
      String content = request.getParameter("content");
      String taskNo = request.getParameter("taskNo");
      String totalTime = request.getParameter("totalTime");
      String useTime = request.getParameter("useTime");
      String rate = request.getParameter("rate");
      String color = request.getParameter("color");
      String important = request.getParameter("important");
      String taskType = request.getParameter("taskType");
      String taskStatus = request.getParameter("taskStatus");
      String subject = request.getParameter("subject");
      String smsRemind = request.getParameter("smsRemind");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      if (rate.equals("")) {
        rate = "0";
      }
      if (!beginDate.equals("")) {
        task.setBeginDate(dateFormat1.parse(beginDate));
      }
      if (!endDate.equals("")) {
        task.setEndDate(dateFormat1.parse(endDate));
      }
      if (!finishTime.equals("")) {
        task.setFinishTime(dateFormat.parse(finishTime));
      }
      task.setSeqId(Integer.parseInt(seqId));
      task.setUserId(String.valueOf(userId));
      task.setColor(color);
      task.setImportant(important);
      task.setRate(rate);
      task.setContent(content);
      task.setTaskStatus(taskStatus);
      task.setTaskType(taskType);
      task.setSubject(subject);
      if (taskNo.equals("")) {
        taskNo = "0";
      }
      task.setTaskNo(Integer.parseInt(taskNo));
      if (totalTime.equals("")) {
        totalTime = "0";
      }
      task.setTotalTime(totalTime);
      if (useTime.equals("")) {
        useTime = "0";
      }
      task.setUseTime(useTime);
      T9TaskLogic ttl = new T9TaskLogic();
      ttl.updateTask(dbConn, task);
      // 短信smsType, content, remindUrl, toId, fromId
      if (smsRemind != null && !T9Utility.isNullorEmpty(remindTime)) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("5");
        sb.setSendDate(T9Utility.parseDate(remindTime));
        sb.setContent("请查看我的任务！标题：" + subject);
        sb.setRemindUrl("/core/funcs/calendar/tasknote.jsp?seqId=" + seqId
            + "&openFlag=1&openWidth=300&openHeight=250");
        sb.setToId(String.valueOf(userId));
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if (moblieSmsRemind != null && !T9Utility.isNullorEmpty(remindTime)) {
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn, String.valueOf(userId), userId,
            "我的任务：" + content, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      // request.setAttribute(T9ActionKeys.RET_DATA, "data");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 修改任务 日程安排查询
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateTaskByUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      int userSeqId = user.getSeqId();
      T9Task task = new T9Task();
      String seqId = request.getParameter("seqId");
      String userId = request.getParameter("userId");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String remindTime = request.getParameter("remindTime");
      String finishTime = request.getParameter("finishTime");
      String content = request.getParameter("content");
      String taskNo = request.getParameter("taskNo");
      String totalTime = request.getParameter("totalTime");
      String useTime = request.getParameter("useTime");
      String rate = request.getParameter("rate");
      String color = request.getParameter("color");
      String important = request.getParameter("important");
      String taskType = request.getParameter("taskType");
      String taskStatus = request.getParameter("taskStatus");
      String subject = request.getParameter("subject");
      String smsRemind = request.getParameter("smsRemind");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      if (seqId != null && !seqId.equals("")) {
        if (rate.equals("")) {
          rate = "0";
        }
        if (!beginDate.equals("")) {
          task.setBeginDate(dateFormat1.parse(beginDate));
        }
        if (!endDate.equals("")) {
          task.setEndDate(dateFormat1.parse(endDate));
        }
        if (!finishTime.equals("")) {
          task.setFinishTime(dateFormat.parse(finishTime));
        }
        T9TaskLogic ttl = new T9TaskLogic();
        task.setSeqId(Integer.parseInt(seqId));
        task.setUserId(userId);
        task.setColor(color);
        task.setImportant(important);
        task.setRate(rate);
        task.setContent(content);
        task.setTaskStatus(taskStatus);
        task.setTaskType(taskType);
        task.setSubject(subject);
        task.setManagerId(String.valueOf(userSeqId));
        if (taskNo.equals("")) {
          taskNo = "0";
        }
        task.setTaskNo(Integer.parseInt(taskNo));
        if (totalTime.equals("")) {
          totalTime = "0";
        }
        task.setTotalTime(totalTime);
        if (useTime.equals("")) {
          useTime = "0";
        }
        task.setUseTime(useTime);

        ttl.updateTask(dbConn, task);
        if (smsRemind != null && !T9Utility.isNullorEmpty(remindTime)) {
          // 短信smsType, content, remindUrl, toId, fromId
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("5");
          sb.setSendDate(T9Utility.parseDate(remindTime));
          sb.setContent("请查看" + user.getUserName() + "安排的任务！标题：" + subject);
          sb.setRemindUrl("/core/funcs/calendar/tasknote.jsp?seqId=" + seqId
              + "&openFlag=1&openWidth=300&openHeight=250");
          sb.setToId(userId);
          sb.setFromId(userSeqId);
          T9SmsUtil.smsBack(dbConn, sb);
        }
        String moblieSmsRemind = request.getParameter("moblieSmsRemind");
        if (moblieSmsRemind != null) {
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn, userId, userSeqId, "任务安排："
              + content, new Date());
        }
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      // request.setAttribute(T9ActionKeys.RET_DATA, "data");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /*
   * 查询所有任务
   */
  public String selectTask(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9TaskLogic ttl = new T9TaskLogic();
      List<Map<String, String>> taskList = ttl.selectTask(dbConn, userId);
      StringBuffer buffer = new StringBuffer("[");
      for (Map<String, String> equipmentsMap : taskList) {
        buffer.append("{");
        Set<String> keySet = equipmentsMap.keySet();
        for (String mapStr : keySet) {
          // System.out.println(mapStr + ":>>>>>>>>>>>>" +
          // equipmentsMap.get(mapStr));
          String name = equipmentsMap.get(mapStr);
          if (name != null) {
            name = name.replace("\\", "\\\\").replace("\"", "\\\"").replace(
                "\r", "").replace("\n", "");
          }
          buffer.append(mapStr + ":\"" + (name == null ? "" : name) + "\",");
        }
        buffer.deleteCharAt(buffer.length() - 1);
        buffer.append("},");
      }
      buffer.deleteCharAt(buffer.length() - 1);
      if (taskList.size() > 0) {
        buffer.append("]");
      } else {
        buffer.append("[]");
      }
      String data = buffer.toString();
      // System.out.println(data);

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
   * 任务分页查询
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectTaskByPage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9TaskLogic dl = new T9TaskLogic();
      String data = dl.toSearchData(dbConn, request.getParameterMap(), userId);
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

  /*
   * 查询任务ById
   */
  public String selectTaskById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9TaskLogic ttl = new T9TaskLogic();
      String data = "";
      T9PersonLogic personLogic = new T9PersonLogic();
      if (seqId != null && !seqId.equals("")) {
        Map<String, String> task = ttl.selectTaskById(dbConn, seqId);
        if (task != null) {
          StringBuffer buffer = new StringBuffer("{");
          Set<String> keySet = task.keySet();
          String managerName = "";
          for (String mapStr : keySet) {
            // System.out.println(mapStr + ":>>>>>>>>>>>>" + task.get(mapStr));
            String name = task.get(mapStr);
            if (name != null) {
              name = name.replace("\\", "\\\\").replace("\"", "\\\"").replace(
                  "\r", "").replace("\n", "");
            }

            if (mapStr != null && mapStr.equals("managerId")) {
              if (name != null && !name.trim().equals("")) {
                managerName = personLogic.getNameBySeqIdStr(name, dbConn);
                if (managerName != null && !managerName.equals("")) {
                  managerName = T9Utility.encodeSpecial(managerName);
                }
              }
            }
            buffer.append(mapStr + ":\"" + (name == null ? "" : name) + "\",");
          }
          buffer.append("managerName:\"" + managerName + "\",");
          buffer.deleteCharAt(buffer.length() - 1);
          buffer.append("}");
          data = buffer.toString();
        }

      }
      if (data.equals("")) {
        data = "{}";
      }
      // System.out.println(data);
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

  /*
   * 按条件查询所有任务
   */
  public String selectTaskByTerm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String minDate = request.getParameter("minDate");
      String maxDate = request.getParameter("maxDate");
      String taskType = request.getParameter("taskType");
      String taskStatus = request.getParameter("taskStatus");
      String content = request.getParameter("content");
      String important = request.getParameter("important");
      T9TaskLogic ttl = new T9TaskLogic();
      List<Map<String, String>> taskList = ttl.selectTaskByTerm(dbConn, userId,
          minDate, maxDate, taskType, taskStatus, content, important);
      request.setAttribute("taskList", taskList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      // request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/calendar/querytask.jsp";
  }

  /*
   * 删除任务ById
   */
  public String deleteTaskById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      // System.out.println(seqId);
      T9TaskLogic ttl = new T9TaskLogic();
      ttl.deleteTaskById(dbConn, seqId);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   *           删除多条任务
   */
  public String deleteTask(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIds = request.getParameter("seqIds");
      T9TaskLogic ttl = new T9TaskLogic();
      ttl.deleteTask(dbConn, seqIds);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/calendar/task.jsp";
  }
}
