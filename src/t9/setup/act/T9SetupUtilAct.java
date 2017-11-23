package t9.setup.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.setup.util.T9ERPSetupUitl;

public class T9SetupUtilAct {
  /**
   * 查找还没有安装的系统
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findNotStalledSys(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String erpcontextPath = "t9erp";
      String setupContentPath = "setup";
      String installPath = T9SysProps.getRootPath();
      T9ERPSetupUitl easu = new T9ERPSetupUitl();
      String data = easu.getErpInstallInfo(installPath, setupContentPath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 测试数据库连接
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String testDbConn(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      boolean isActive = T9ERPSetupUitl.testDbConn(request.getParameterMap(), "sqlserver");
      String testRt = isActive ? "1" : "0";
      String data = "{testRt:\"" + testRt + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
