package t9.rad.dsdef.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9StringFormat;
import t9.rad.dsdef.logic.T9DsDef2AutoCodeLogic;
import t9.rad.velocity.T9CodeUtil;
import t9.rad.velocity.metadata.T9Field;
import t9.rad.velocity.metadata.T9GridField;

/**
 * 用于生成数据字典的物理结构
 * @author Think
 *
 */
public class T9DsDef2AutoCode {

  /**
   *生成物理结构
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String parserTemp(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9DsDef2AutoCodeLogic dda = new T9DsDef2AutoCodeLogic();
      String data = dda.fileTemp2Json();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String parserTempXml(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9DsDef2AutoCodeLogic dda = new T9DsDef2AutoCodeLogic();
      String xmlName = request.getParameter("tempName");
      String data = dda.xmlTemp2Json(xmlName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String autoCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9DsDef2AutoCodeLogic dda = new T9DsDef2AutoCodeLogic();
      String xmlName = request.getParameter("tempName");
      String data = dda.xmlTemp2Json(xmlName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String code2java(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNo = request.getParameter("tableNo").trim();
      String pojectName = request.getParameter("pojectName").trim();
      String tempPoj = request.getParameter("tempPoj").trim();

      T9DsDef2AutoCodeLogic dsac = new T9DsDef2AutoCodeLogic();
      dsac.autoCode(dbConn, tableNo, pojectName, tempPoj);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成成功！");
    } catch(Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成失败,请检查数据字典是否配置正确！");
      e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
}
