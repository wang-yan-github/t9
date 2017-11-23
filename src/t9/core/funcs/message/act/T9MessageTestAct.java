package t9.core.funcs.message.act;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.logic.T9ExportLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.message.logic.T9MessageTestLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9MessageTestAct {
  public String notConfirm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9PageDataList data = null;
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9MessageTestLogic smsLogic = new T9MessageTestLogic();
      data = smsLogic.toNewBoxJson(dbConn, request.getParameterMap(), toId,pageNo,pageSize);
      sizeNo = data.getTotalRecord();
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/message/notConMessage2.jsp?sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
  }
  
}
