package t9.subsys.oa.vmeet.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.vmeet.logic.T9VMeetLogic;

public class T9VMeetAct {
 private T9VMeetLogic logic=new T9VMeetLogic();
  
 /**
  * 新建视频会议
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String addVMeetInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {

   String contexPath = request.getContextPath();
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     dbConn = requestDbConn.getSysDbConn();
     String inviteUsers=request.getParameter("toId");
     String content=request.getParameter("content");
     this.logic.addVMeetInfoLogic(dbConn, person,inviteUsers,content);
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
  * 新建视频会议
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String editUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     dbConn = requestDbConn.getSysDbConn();
     String inviteUsers=request.getParameter("toId");
     String seqId=request.getParameter("seqId");
     String content=request.getParameter("content");
     this.logic.editUsersLogic(dbConn, person,inviteUsers,content,seqId);
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
  * 新建视频会议
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String setVMeetPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {

   String contexPath = request.getContextPath();
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     String toIds=request.getParameter("toId");
   
     this.logic.setVMeetPrivLogic(dbConn,toIds);
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
  * 获取视频会议的权限
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String getVMeetPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {

   String contexPath = request.getContextPath();
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     dbConn = requestDbConn.getSysDbConn();
     String inviteUsers=request.getParameter("inviteusers");
     String content=request.getParameter("content");
     String data=this.logic.getVMeetPriv(dbConn,person);
  
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
     request.setAttribute(T9ActionKeys.RET_DATA,data);
   } catch (Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";                             
 } 
 
 public String getLastBeginMeetAct(HttpServletRequest request, HttpServletResponse response) throws Exception {

   String contexPath = request.getContextPath();
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     dbConn = requestDbConn.getSysDbConn();
     String data=this.logic.getLastBeginMeet(dbConn, person);
     data="["+data+"]";
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
     request.setAttribute(T9ActionKeys.RET_DATA,data);
   } catch (Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";                             
 } 
 
 public String deleteVMeetAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     String seqId=request.getParameter("seqId");
     this.logic.deleteVMeet(dbConn, seqId);
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
   } catch (Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";                             
 } 
 
 
 
 
 public String getLastInvitedMeetAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     dbConn = requestDbConn.getSysDbConn();
     String data=this.logic.getLastInvitedMeet(dbConn, person);
     data="["+data+"]";
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
     request.setAttribute(T9ActionKeys.RET_DATA,data);
   } catch (Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";                             
 } 
 
 public String getVMeetByIdAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     dbConn = requestDbConn.getSysDbConn();
     String seqId=request.getParameter("seqId");
     String data=this.logic.getVMeetByIdLogic(dbConn, person,seqId);
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
     request.setAttribute(T9ActionKeys.RET_DATA,data);
   } catch (Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";                             
 } 
 
}
