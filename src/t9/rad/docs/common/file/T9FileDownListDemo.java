package t9.rad.docs.common.file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;


public class T9FileDownListDemo {

  public String fileDownListJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String attachmentId = "";
      String attachmentName = "";
      String data = "{attachmentId:\"" + attachmentId + "\",attachmentName:\"" + attachmentName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加载代码失败" + ex.getMessage());
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
}
