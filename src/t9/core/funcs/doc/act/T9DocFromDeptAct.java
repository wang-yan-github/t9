package t9.core.funcs.doc.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.logic.T9DocFromDeptLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9DocFromDeptAct {

  public String addFromDept(HttpServletRequest request,  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String deptName = request.getParameter("fromDeptName");
      String deptId = request.getParameter("fromDeptId");
     
      T9DocFromDeptLogic logic = new T9DocFromDeptLogic();
      if (!logic.hasDept(dbConn , deptName)) {
        logic.addFromDept(dbConn , deptName , deptId);
      } else {
        logic.updateFromDept(dbConn , deptName , deptId);
      }
    
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功!");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String delFromDept(HttpServletRequest request,  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String id = request.getParameter("id");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      T9DocFromDeptLogic logic = new T9DocFromDeptLogic();
      logic.delFromDept(dbConn,id);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getAllDept(HttpServletRequest request,  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptName = request.getParameter("fromDeptName");
      T9DocFromDeptLogic logic = new T9DocFromDeptLogic();
      StringBuffer result = logic.getAllDept(dbConn, request.getParameterMap(), deptName);
      PrintWriter pw = response.getWriter();
      pw.println(result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getDepts(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      String w = request.getParameter("w");
      T9DocFromDeptLogic logic = new T9DocFromDeptLogic();
      List<String> ss = logic.getDepts(dbConn, w);
      sb.append(",lis:[");
      int count = 0;
      for (String s : ss) {
        if (s.contains(w)) {
          sb.append("{string:\""+T9Utility.encodeSpecial(s)+"\"},");
          count++;
        }
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      sb.insert(0, "{count:" + count);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
