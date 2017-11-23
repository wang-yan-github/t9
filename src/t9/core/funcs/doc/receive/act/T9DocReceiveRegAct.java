package t9.core.funcs.doc.receive.act;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.doc.receive.data.T9DocReceive;
import t9.core.funcs.doc.receive.logic.T9DocReceiveRegLogic;
import t9.core.funcs.doc.util.T9DocUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

/**
 * 收文登记
 * @author lh
 *
 */
public class T9DocReceiveRegAct{
  /**
   * 登记收文
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String updateReceive(HttpServletRequest request, HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9DocReceive doc = new T9DocReceive();
      String docNo = request.getParameter("docNo");
      String fromUnits = request.getParameter("fromUnits");
      String oppDocNo = request.getParameter("oppDocNo");
      String title = request.getParameter("title");
      String copies = request.getParameter("copies");
      String confLevel = request.getParameter("confLevel");
      String instruct = request.getParameter("instruct");
      String docType = request.getParameter("docType");
      String sponsor = request.getParameter("deptId");
      String personId = request.getParameter("user");
      String alarm = request.getParameter("alarm");
      String attachName = request.getParameter("attachmentName");
      String attachId = request.getParameter("attachmentId");
      String seqId = request.getParameter("seqId");

      doc.setSeq_id(Integer.parseInt(seqId));
      int userId = Integer.parseInt(personId);
      doc.setDocNo(docNo);
      doc.setFromUnits(fromUnits);
      doc.setOppdocNo(oppDocNo);
      doc.setTitle(title);
      doc.setCopies(Integer.parseInt(copies));
      doc.setConfLevel(Integer.parseInt(confLevel));
      doc.setInstruct(instruct);
      doc.setDocType(Integer.parseInt(docType));
      doc.setSponsor(sponsor);
      doc.setUserId(userId);
      doc.setAttachNames(attachName);
      doc.setAttachIds(attachId);
      doc.setSendStauts(0);
      T9DocReceiveRegLogic logic = new T9DocReceiveRegLogic();
      logic.updateDocReceive(dbConn, doc);
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      String content = user.getUserName() + "提醒：请签收您的收文!收文文号:" + docNo;  
      String url =  "/core/funcs/doc/receive/readdocindex.jsp";
//      if(!T9Utility.isNullorEmpty(alarm)){
//        T9DocSmsLogic.sendSms(user, dbConn, content, url, recipient, null);
//      }
      request.setAttribute("msg", "登记成功！");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e; 
    }
    return "/core/funcs/doc/receive/msgBox2.jsp";
  }
  /**
   * 取得未登记的记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getNReg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9DocReceiveRegLogic logic = new T9DocReceiveRegLogic();
      StringBuffer result = logic.getRegList(dbConn, request.getParameterMap(), loginUser, "2" , request.getRealPath("/"));
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 根据seq_id取得未登记的记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRecReg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String webroot = request.getRealPath("/");
      T9DocReceiveRegLogic logic = new T9DocReceiveRegLogic();
      String str = logic.getRecReg(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据用户有登记权限的部门

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9DocUtility util = new T9DocUtility();
      T9DeptLogic logic = new T9DeptLogic();
      String data = "";
      if (!util.haveAllRight(user, dbConn)) {
        String dept = util.deptRight(userId, dbConn);
        data = "[";
        if (!T9Utility.isNullorEmpty(dept)) {
          String name = logic.getNameByIdStr(dept, dbConn);
          String[] depts = dept.split(",");
          String[] names = name.split(",");
          
          for (int i = 0 ;i < depts.length ; i++) {
            String tmp = depts[i];
            String tmp2 = names[i];
            data += "{";
            data += "value:\"" + tmp + "\"";
            data += ",text:\"" + T9Utility.encodeSpecial(tmp2) + "\"";
            data += "},";
          }
          if (data.endsWith(",")) {
            data = T9WorkFlowUtility.getOutOfTail(data);
          }
        }
        data += "]";
      } else {
        //查询所有的部门
        data = logic.getDeptTreeJson(0, dbConn);
      }
      if (T9Utility.isNullorEmpty(data)) {
        data = "[]";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
