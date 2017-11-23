package t9.rad.docs.iframe.c2s;


import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;

public class T9Client2ServerDemo {

  /**
   * ajax方式
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String ajax1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    try {
      String data = "连接成功后返回的数据.";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "服务端异常");
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  /**
   * ajax方式
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String ajax2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String data = "连接成功后返回的数据.";
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception e) {
      throw e;
    }
    return null;
  }
  /**
   * 传统的处理方式,非ajax方式
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String tradition(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      request.setCharacterEncoding(T9Const.DEFAULT_CODE);
      response.setCharacterEncoding(T9Const.DEFAULT_CODE);
      request.setAttribute("rtData", "连接成功后返回的数据.");
    } catch (Exception e) {
      throw e;
    }
    return "/rad/docs/module/c2s/rtTest.jsp";
  }
}
