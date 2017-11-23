package t9.rad.docs.act;


import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.util.form.T9FOM;

public class T9TestPageAction {
  private static Logger log = Logger.getLogger("yzq.test.core.act.T9TestPageAction");
  
  /**
   * 取得页面数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap());
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,
          queryParam,
          //"select SEQ_ID,FROM_ID,SEND_TIME,ATTACHMENT_NAME from EMAIL_BODY where SEQ_ID=0");
          "select SEQ_ID,FROM_ID,SEND_TIME,ATTACHMENT_NAME , ATTACHMENT_ID from EMAIL_BODY");
      
      PrintWriter pw = response.getWriter();
      pw.println(pageDataList.toJson());
      pw.flush();
      
      return null;
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
  }
}
