package t9.core.funcs.search.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.search.logic.T9FrameSearchLogic;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

/**
 * 
 * @author Think
 * 搜索
 */
public class T9FrameSerach {
  /**
   * 搜索用户信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String keyWord = request.getParameter("keyWord");
      String pageStartStr = request.getParameter("pageStart");
      String pageNumStr = request.getParameter("pageNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FrameSearchLogic fsl = new T9FrameSearchLogic();
      String data = fsl.getUserInfo(dbConn, keyWord, Integer.valueOf(pageStartStr), Integer.valueOf(pageNumStr));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 搜索email数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchEmail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String keyWord = request.getParameter("keyWord");
      String pageStartStr = request.getParameter("pageStart");
      String pageNumStr = request.getParameter("pageNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FrameSearchLogic fsl = new T9FrameSearchLogic();
      String data = fsl.getEmailInfo(dbConn, keyWord, Integer.valueOf(pageStartStr), Integer.valueOf(pageNumStr),person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 搜索公告通知
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String keyWord = request.getParameter("keyWord");
      String pageStartStr = request.getParameter("pageStart");
      String pageNumStr = request.getParameter("pageNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FrameSearchLogic fsl = new T9FrameSearchLogic();
      String data = fsl.getNotifyInfo(dbConn, keyWord, Integer.valueOf(pageStartStr), Integer.valueOf(pageNumStr),person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 通讯薄
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchAddress(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String keyWord = request.getParameter("keyWord");
      String pageStartStr = request.getParameter("pageStart");
      String pageNumStr = request.getParameter("pageNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FrameSearchLogic fsl = new T9FrameSearchLogic();
      String data = fsl.getAddressInfo(dbConn, keyWord, Integer.valueOf(pageStartStr), Integer.valueOf(pageNumStr),person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 文件柜
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchFileFolder(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String keyWord = request.getParameter("keyWord");
      String pageStartStr = request.getParameter("pageStart");
      String pageNumStr = request.getParameter("pageNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FrameSearchLogic fsl = new T9FrameSearchLogic();
      String data = fsl.getFileFloderInfo(dbConn, keyWord, Integer.valueOf(pageStartStr), Integer.valueOf(pageNumStr),person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 工作流搜索
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchWorkFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String keyWord = request.getParameter("keyWord");
      String pageStartStr = request.getParameter("pageStart");
      String pageNumStr = request.getParameter("pageNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FrameSearchLogic fsl = new T9FrameSearchLogic();
      String data = fsl.getWorkFlowInfo(dbConn, keyWord, Integer.valueOf(pageStartStr), Integer.valueOf(pageNumStr),person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
