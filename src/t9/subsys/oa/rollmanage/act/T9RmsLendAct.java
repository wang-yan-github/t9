package t9.subsys.oa.rollmanage.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.attendance.logic.T9SysParaLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.rollmanage.data.T9RmsLend;
import t9.subsys.oa.rollmanage.logic.T9RmsLendLogic;
import t9.subsys.oa.rollmanage.logic.T9RmsRollLogic;

public class T9RmsLendAct {
	private T9RmsLendLogic logic = new T9RmsLendLogic();

	/**
	 * 借阅查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryLendFileJosn(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("roomName", request.getParameter("roomName"));
		map.put("rollName", request.getParameter("rollName"));
		map.put("fileCode", request.getParameter("fileCode"));
		map.put("fileSubject", request.getParameter("fileSubject"));
		map.put("fileTitle", request.getParameter("fileTitle"));
		map.put("fileTitleo", request.getParameter("fileTitleo"));
		map.put("sendUnit", request.getParameter("sendUnit"));
		map.put("remark", request.getParameter("remark"));
		map.put("sendTimeMin", request.getParameter("sendTimeMin"));
    map.put("sendTimeMax", request.getParameter("sendTimeMax"));
    map.put("fileWord", request.getParameter("fileWord"));
    map.put("fileYear", request.getParameter("fileYear"));
    map.put("issueNum", request.getParameter("issueNum"));
    
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

			String data = this.logic.queryLendFileLogic(dbConn, request.getParameterMap(), person, map);
			PrintWriter pw = response.getWriter();
			pw.println(data);
			pw.flush();
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return null;
	}

	/**
	 * 借阅
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String rmsLendRoll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			int rollId = Integer.parseInt(request.getParameter("rollId"));
			int fileId = Integer.parseInt(request.getParameter("fileId"));
			String manage = this.logic.getRmsLendManage(dbConn, rollId);
			boolean flag = this.logic.isNeedApprove(dbConn, fileId, person);
			T9RmsRollLogic rollLogic = new T9RmsRollLogic();
			boolean rollFlag = rollLogic.isNeedApprove(dbConn, rollId, person);
			
			String type = "37";  //37为档案管理
			Map m = new HashMap();
			m.put("fileId", fileId);
			m.put("userId", String.valueOf(person.getSeqId()));
			m.put("addTime", T9Utility.getCurDateTimeStr());
			m.put("approve", manage);
			if (rollFlag || flag) {
			  m.put("allow", "1");
			  T9ORM orm = new T9ORM();
			  m.put("allowTime", T9Utility.getCurDateTimeStr());
	      orm.saveSingle(dbConn, "rmsLend", m);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "借阅成功");
	      return "/core/inc/rtjson.jsp";
			} else {
			  m.put("allow", "0");
			  T9ORM orm = new T9ORM();
	      orm.saveSingle(dbConn, "rmsLend", m);
			}
      
			T9SysParaLogic t9pl = new T9SysParaLogic();
			String sysRemind = t9pl.selectPara(dbConn, "SMS_REMIND");
			String allowRemind = "2";// 1允许短信提醒2为不允许
      String defaultRemind = "2";// 1为默认提醒2为不默认
      String mobileRemind = "2";// 1为默认提醒2为不默认
      if (sysRemind != null) {
        String[] sysRemindArray = sysRemind.split("\\|");
        if (sysRemindArray.length == 1) {
          String temp = sysRemindArray[0];
          String[] tempArray = temp.split(",");
          for (int i = 0; i < tempArray.length; i++) {
            if (tempArray[i].equals(type)) {
              defaultRemind = "1";
              break;
            }
          }

        }
        if (sysRemindArray.length == 2) {
          String temp1 = sysRemindArray[0];
          String[] tempArray1 = temp1.split(",");
          for (int i = 0; i < tempArray1.length; i++) {
            if (tempArray1[i].equals(type)) {
              defaultRemind = "1";
              break;
            }
          }
          String temp2 = sysRemindArray[1];
          String[] tempArray2 = temp2.split(",");
          for (int i = 0; i < tempArray2.length; i++) {
            if (tempArray2[i].equals(type)) {
              mobileRemind = "1";
              break;
            }
          }
        }
        if (sysRemindArray.length == 3) {
          String temp1 = sysRemindArray[0];
          String[] tempArray1 = temp1.split(",");
          for (int i = 0; i < tempArray1.length; i++) {
            if (tempArray1[i].equals(type)) {
              defaultRemind = "1";
              break;
            }
          }
          String temp2 = sysRemindArray[2];
          String[] tempArray2 = temp2.split(",");
          for (int i = 0; i < tempArray2.length; i++) {
            if (tempArray2[i].equals(type)) {
              allowRemind = "1";
              break;
            }
          }
          String temp3 = sysRemindArray[1];
          String[] tempArray3 = temp3.split(",");
          for (int i = 0; i < tempArray3.length; i++) {
            if (tempArray3[i].equals(type)) {
              mobileRemind = "1";
              break;
            }
          }
        }
      }
			if(("1".equals(allowRemind) || "1".equals(defaultRemind))&& !T9Utility.isNullorEmpty(manage)){
			  String content = "用户向您借阅档案，请审批";
			  String remindUrl = "/subsys/oa/rollmanage/rolllend/confirmManage.jsp";
			  this.logic.doSmsBack(dbConn, content, person.getSeqId(), manage, type, remindUrl);
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "借阅成功");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	

	/**
	 * 批量借阅
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String rmsLendAllRoll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String fileIdStr = request.getParameter("fileIdStr");
			
			int rollId = Integer.parseInt(request.getParameter("rollId"));
      String manage = this.logic.getRmsLendManage(dbConn, rollId);
      T9RmsRollLogic rollLogic = new T9RmsRollLogic();
      boolean rollFlag = rollLogic.isNeedApprove(dbConn, rollId, person);
      
			String[] fileStr = fileIdStr.split(",");
			Map m = new HashMap();
			T9ORM orm = new T9ORM();
			for (int i = 0; i < fileStr.length; i++) {
				String id = fileStr[i];
				if (T9Utility.isNullorEmpty(id))
				  continue;
				m.put("fileId", id);
				m.put("userId", String.valueOf(person.getSeqId()));
				m.put("addTime", T9Utility.getCurDateTimeStr());
				m.put("approve", manage);
				boolean is = this.logic.isNeedApprove(dbConn, Integer.parseInt(id), person);
	      if (rollFlag || is) {
	        m.put("allow", "1");
	        m.put("allowTime", T9Utility.getCurDateTimeStr());
	      } else {
	        m.put("allow", "0");
	      }
				orm.saveSingle(dbConn, "rmsLend", m);
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "借阅成功");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
/**
 * 借阅记录通用方法
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
	public String getApprovalToBorrow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String allow = request.getParameter("allow");
      if(T9Utility.isNullorEmpty(allow)){
        allow = "0";
      }
      String data = this.logic.getApprovalToBorrow(dbConn, person, allow).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
	
	/**
	 * 借阅审批通用方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	 public String getApprovaledLend(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    String userName = "";
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
	      String allow = request.getParameter("allow");
	      String data = this.logic.getApprovaledLend(dbConn, person, allow).toString();
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    }catch(Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
	 
 /**
  * 撤销
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String revocationLend(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //int seqId = person.getSeqId();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ORM t = new T9ORM();
      t.deleteSingle(dbConn,T9RmsLend.class, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
	  
  /**
   * 归还
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String returnLend(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("allow", allow);
      if("3".equals(allow)){
        m.put("returnTime", T9Utility.getCurDateTimeStr());
      }else{
        m.put("allowTime", T9Utility.getCurDateTimeStr());
      }
      T9ORM t = new T9ORM();
      t.updateSingle(dbConn, "rmsLend", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
