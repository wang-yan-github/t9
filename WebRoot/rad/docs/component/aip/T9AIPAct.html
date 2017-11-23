package test.core.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.exps.T9InvalidParamException;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;

public class T9AIPAct {
  private String uploadPath = "D:\\tmp\\upload";
  /**
   * 上传AIP文件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadAip(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      
      String tName = request.getParameter("T_NAME");
      String content = request.getParameter("CONTENT");
      
      if (T9Utility.isNullorEmpty(tName)) {
        throw new T9InvalidParamException("没有传递模板名称");
      }
      if (T9Utility.isNullorEmpty(content)) {
        throw new T9InvalidParamException("没有传递模板内容");
      }
      String filePath = uploadPath + "\\" + tName + ".aip";
      T9FileUtility.storeString2File(filePath, content);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "AIP 文件保存成功");
    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "AIP 文件保存失败");
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 加载AIP文件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadAip(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String tName = request.getParameter("T_NAME");
      String filePath = uploadPath + "\\" + tName + ".aip";
      
      String content = T9FileUtility.loadLine2Buff(filePath).toString();      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "AIP 文件保存成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + content + "\"");
    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "AIP 文件保存失败");
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
//      request.setCharacterEncoding("UTF-8");
      String tName = request.getParameter("test1");
//      tName = new String(tName.getBytes("ISO8859-1"), "UTF-8");
      System.out.println(tName);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "AIP 文件保存成功");
    } catch (Exception e) {
      e.printStackTrace();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "AIP 文件保存失败");
    }
    return "/core/inc/rtjson.jsp";
  }
}
