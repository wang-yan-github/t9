package t9.core.funcs.connswitch.act;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.funcs.connswitch.logic.T9ConnSwitchLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.setup.util.T9ERPSetupUitl;

public class T9ConnSwitchAct {
  private T9ConnSwitchLogic logic=new T9ConnSwitchLogic();
  public String getConnectingDbms(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.getConnectingDbms();
      data="{dbms:'"+data+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSwitchDbms(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String dbms=request.getParameter("dbms");
      String rootPath = request.getRealPath("/");
      logic.getSwitchDbms(dbms,rootPath);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getOraConnInfo(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.getOraConnInfo();
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSqlConnInfo(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.getSqlConnInfo();
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getMysqlConnInfo(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.getMysqlConnInfo();
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String testOraConnect(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.testOraConnect(request.getParameterMap());
     data="{test:'"+data+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String testSqlConnect(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.testSqlConnect(request.getParameterMap());
     data="{test:'"+data+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String testMysqlConnect(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      String data="";
      data=logic.testMysqlConnect(request.getParameterMap());
     data="{test:'"+data+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  public String saveOraConnect(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {
      logic.saveOraConnect(request.getParameterMap());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);

    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String saveSqlConnect(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {

      logic.saveSqlConnect(request.getParameterMap());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String saveMysqlConnect(HttpServletRequest request
      ,HttpServletResponse response) throws Exception{
    try {

     logic.saveMysqlConnect(request.getParameterMap());

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,  ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
