package t9.core.servlet;

import java.util.Map;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

/**
 * 请求监听者

 * @author yzq
 *
 */
public class T9ServletRequestListener implements ServletRequestListener {
  public void requestInitialized(ServletRequestEvent sre) {
    HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
    try {
      if (T9ServletUtility.isGbkCode(request)) {
        request.setCharacterEncoding("GBK");
      }else {
        request.setCharacterEncoding(T9Const.DEFAULT_CODE);
      }
    }catch(Exception ex) {      
    }
    T9RequestDbConn requestDbConn = new T9RequestDbConn("");
    request.setAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR, requestDbConn);
    request.getSession().setAttribute(T9BeanKeys.CURR_REQUEST_FLAG, true);
    request.getSession().setAttribute(T9BeanKeys.CURR_REQUEST_ADDRESS, request.getRemoteAddr());
    
    
    Map<String, String[]> paramMap = request.getParameterMap();
    String kiloSplitHidden = request.getParameter("kiloSplitCntrlIds");
    if (kiloSplitHidden != null && kiloSplitHidden.length() > 1) {
      if (kiloSplitHidden.startsWith(",")) {
        kiloSplitHidden = kiloSplitHidden.substring(1);
      }
      if (kiloSplitHidden.endsWith(",")) {
        kiloSplitHidden = kiloSplitHidden.substring(0, kiloSplitHidden.length() - 1);
      }
      String[] kiloSplitArray = kiloSplitHidden.split(",");
      for (int i = 0; i < kiloSplitArray.length; i++) {
        String inputName = kiloSplitArray[i].trim();
        if (T9Utility.isNullorEmpty(inputName)) {
          continue;
        }
        String[] paramValueArray = paramMap.get(inputName);
        if (paramValueArray != null) {
          String paramValue = paramValueArray[0];
          if (!T9Utility.isNullorEmpty(paramValue) && paramValue.indexOf(",") > 0) {
            paramValueArray[0] = paramValue.replace(",", "");
          }
        }
      }
    }
  }
  public void requestDestroyed(ServletRequestEvent sre) {    
    HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
    request.getSession().removeAttribute(T9BeanKeys.CURR_REQUEST_FLAG);
    request.getSession().removeAttribute(T9BeanKeys.CURR_REQUEST_ADDRESS);
    
    T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    if (requestDbConn != null) {
      requestDbConn.closeAllDbConns();
    }
  }
}
