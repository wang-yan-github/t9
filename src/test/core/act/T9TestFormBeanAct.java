package test.core.act;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;
import test.core.dto.T9InnerBean1;
import test.core.dto.T9InnerBean11;
import test.core.dto.T9TestBean;

public class T9TestFormBeanAct {
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testEncode(HttpServletRequest request,  HttpServletResponse response) throws Exception {
    //response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    PrintWriter pw = response.getWriter();
    pw.print("中文您好");
    return null;
  }
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9InnerBean11 innerBean11 = (T9InnerBean11)T9FOM.build(request.getParameterMap());
    
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    PrintWriter pw = response.getWriter();
    pw.print(T9FOM.toJson(innerBean11));
    return null;
  }
  
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testMulDetl(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9InnerBean1 innerBean1 = (T9InnerBean1)T9FOM.build(request.getParameterMap());
    
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    PrintWriter pw = response.getWriter();
    pw.print(T9FOM.toJson(innerBean1));
    return null;
  }
  
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testRecurve(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9TestBean testBean = (T9TestBean)T9FOM.build(request.getParameterMap());
    
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    PrintWriter pw = response.getWriter();
    pw.print(T9FOM.toJson(testBean));
    return null;
  }
}
