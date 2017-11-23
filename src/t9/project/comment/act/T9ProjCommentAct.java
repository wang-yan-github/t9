package t9.project.comment.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.project.comment.data.T9ProjComment;
import t9.project.comment.logic.T9ProjCommentLogic;
import t9.project.project.data.T9ProjProject;
import t9.project.project.logic.T9ProjectLogic;

public class T9ProjCommentAct {
  /**
   * 获取分页
   * 
   * @param request
   * @param response
   * @return
   */
  public String getPages(HttpServletRequest request, HttpServletResponse response) {
    Connection con = null;
    try {
      T9RequestDbConn dbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      con = dbConn.getSysDbConn();
      String data = T9ProjCommentLogic.getPages(con, request);
      PrintWriter writer = response.getWriter();
      writer.println(data);
      writer.flush();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取分页信息");
    } catch (Exception e) {
      // TODO: handle exception
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    }
    return null;
  }
  /**
   * 新增批注
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjCommentLogic logic = new T9ProjCommentLogic();
      T9ProjComment comment = new T9ProjComment();
      comment.setWriter(person.getUserName());
      comment.setContent(request.getParameter("content"));
      comment.setProjId(Integer.parseInt(request.getParameter("projId")));
      comment.setWriteTime(new Date());
      logic.addComment(dbConn, comment);
      T9ProjectLogic projectLogic = new T9ProjectLogic();
      T9ProjProject proj = projectLogic.getProj(dbConn, Integer.parseInt(request.getParameter("projId")));
      
      if("1".equals(request.getParameter("sendSms"))){
        	String content = "["+proj.getProjName()+"]有新的批注，请查看";
        	String remindUrl = "/project/proj/commentSmsList.jsp?projId="+proj.getSeqId();         
        	T9SmsBack smsBack = new T9SmsBack();
        	smsBack.setContent(content);
        	smsBack.setFromId(person.getSeqId());
        	smsBack.setRemindUrl(remindUrl);
        	smsBack.setSmsType("88");
        	smsBack.setToId(proj.getProjOwner());
        	T9SmsUtil.smsBack(dbConn, smsBack);
        }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加批注成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjCommentLogic logic = new T9ProjCommentLogic();
      T9ProjComment comment = logic.getById(dbConn, Integer.parseInt(request.getParameter("seqId")));
      comment.setContent(request.getParameter("content"));
      logic.updateComment(dbConn, comment);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新批注成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除批注
   * @author zq
   * 2013-3-21
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjCommentLogic logic = new T9ProjCommentLogic();
      logic.deleteComment(dbConn, Integer.parseInt(request.getParameter("seqId")));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除批注成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改页面获取信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9ProjCommentLogic logic = new T9ProjCommentLogic();
      T9ProjComment comment = logic.getById(dbConn, Integer.parseInt(request.getParameter("seqId")));
      data +="[{\"seqId\":"+comment.getSeqId()
      +",\"writeTime\":\""+comment.getWriteTime()
      +"\",\"content\":\""+comment.getContent()
      +"\"}]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
