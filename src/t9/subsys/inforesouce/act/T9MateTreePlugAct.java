package t9.subsys.inforesouce.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.inforesouce.data.T9MateType;
import t9.subsys.inforesouce.logic.T9MateTreePlugLogic;
import t9.subsys.inforesouce.util.T9StringUtil;

/**
 * 元数据树形插件
 * @author qwx110
 *
 */
public class T9MateTreePlugAct{
  private T9MateTreePlugLogic plugLogic = new T9MateTreePlugLogic();
  
  /**
   * 取得元数据树状列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findMates(HttpServletRequest request, HttpServletResponse response)throws  Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    String tree = "";
    try{
      dbConn = requestDbConn.getSysDbConn();
      String contextPath = request.getContextPath();
      int id = 0;
      String idStr = request.getParameter("id");
      if(T9StringUtil.isNotEmpty(idStr)){
        id = Integer.parseInt(idStr);
      }
      tree = plugLogic.findMateTree(dbConn, id, contextPath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, tree);
    } catch (Exception e){     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 点击节点后，右侧显示的内容
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMateIndent(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String id = request.getParameter("id");
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try{
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      List<T9MateType> mates = plugLogic.findMateList(dbConn);
      StringBuffer sb = new StringBuffer();
      if (id == null || "".equals(id)) {
        sb = plugLogic.getMateJson(mates, 0);
      } else {
        sb = plugLogic.getMateJson(mates, Integer.parseInt(id));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 默认显示的树右侧
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findDefMate(HttpServletRequest request, HttpServletResponse response)throws Exception {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    StringBuffer sb = new StringBuffer();
    try{
      dbConn = requestDbConn.getSysDbConn();
      List<T9MateType> mates = plugLogic.findMateList(dbConn);    
      sb = plugLogic.getMateJson(mates, 0);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
