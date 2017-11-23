package t9.core.funcs.diary.act;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.diary.data.T9Diary;
import t9.core.funcs.diary.data.T9DiaryComment;
import t9.core.funcs.diary.data.T9DiaryCommentReply;
import t9.core.funcs.diary.logic.T9DiaryCommentLogic;
import t9.core.funcs.diary.logic.T9DiaryLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.smartform.act.T9FormAct;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9DiaryCommentAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.diary.act.T9DiaryCommentAct");
  /**
   * 保存用户评论
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      dcl.saveCommentLogic(dbConn, userId, request.getParameterMap(),request.getContextPath());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存用户评论
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      String diaIdstr = request.getParameter("diaId");
      int diaId = Integer.parseInt(diaIdstr);
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      ArrayList<T9DiaryComment> dclist = (ArrayList<T9DiaryComment>) dcl.listCommentLogic(dbConn, diaId);
      StringBuffer field = new StringBuffer();
      StringBuffer data = new StringBuffer();
      for (int i = 0 ; i < dclist.size(); i++) {
        T9DiaryComment dc = dclist.get(i);
        int isLoginUser = 0;
        ArrayList<T9DiaryCommentReply> dclistre = (ArrayList<T9DiaryCommentReply>) dcl.listCommentReplyLogic(dbConn, dc.getSeqId());
        StringBuffer dsbuff = T9FOM.toJson(dc);
        StringBuffer dscr = dcl.toJsonFCommentReply(dclistre);
        if(dc.getUserId() == userId){
          isLoginUser = 1;
        }
        if(!"".equals(field.toString())){
          field.append(",");
        }
        field.append("{")
          .append("comment:").append(dsbuff)
          .append(",commentReply:").append(dscr)
          .append(",isLoginUser:").append(isLoginUser)
          .append("}");
      }
      data.append("[").append(field).append("]");
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除评论
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
      String commentIdstr = request.getParameter("commentId");
      int commentId = Integer.parseInt(commentIdstr);
      
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      dcl.deleteCommentLogic(dbConn, commentId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 评论标记为已读
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String commentReaded(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String commentIdstr = request.getParameter("commentId");
      int commentId = Integer.parseInt(commentIdstr);
      
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      dcl.commentReadedLogic(dbConn, commentId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存当前用户的评论的回复
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveCommentReply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int userId = person.getSeqId();
      
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      dcl.saveCommentReplyLogic(dbConn, userId, request.getParameterMap(),request.getContextPath());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除评论回复
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteCommentReply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String replyId = request.getParameter("replyId");
      
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      dcl.deleteReplyLogic(dbConn, replyId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
/**
 * 取得指定ID日志信息
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String getDiaCommentDetaile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String commentId = request.getParameter("commentId");
      T9ORM orm = new T9ORM();
      T9DiaryComment dia = (T9DiaryComment) orm.loadObjSingle(dbConn, T9DiaryComment.class, Integer.parseInt(commentId));
      StringBuffer dia2Json = T9FOM.toJson(dia);
 
      //System.out.println("ByIdDIARY:" + dia2Json.toString());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, dia2Json.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
/**
 * 删除评论回复
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String deleteReply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String replyId = request.getParameter("replyId");
      T9ORM orm = new T9ORM();
      orm.deleteSingle(dbConn, T9DiaryCommentReply.class, Integer.valueOf(replyId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
/**
 * 删除评论回复
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String getCommentReply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String replyIdstr = request.getParameter("replyId");
      int replyId =  Integer.valueOf(replyIdstr);
      T9ORM orm = new T9ORM();
      T9DiaryCommentReply dcr = (T9DiaryCommentReply) orm.loadObjSingle(dbConn, T9DiaryCommentReply.class, replyId);
      StringBuffer data = T9FOM.toJson(dcr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA ,data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 编辑日志回复
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateCommentReply(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DiaryCommentLogic dcl = new T9DiaryCommentLogic();
      dcl.updateCommentReplyLogic(dbConn, request.getParameterMap());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
