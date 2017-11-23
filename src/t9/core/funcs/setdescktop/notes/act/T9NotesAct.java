package t9.core.funcs.setdescktop.notes.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.form.T9FOM;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.fav.logic.T9FavLogic;
import t9.core.funcs.setdescktop.notes.logic.T9NotesLogic;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.funcs.system.url.logic.T9UrlLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.menu.data.T9SysMenu;

public class T9NotesAct {
  private T9NotesLogic logic = new T9NotesLogic();
  
  /**
   * 获取Note
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getNotes(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      String data = this.logic.getNotes(dbConn, user.getSeqId());
      
      if (data != null){
        data = data.replace("\r\n", "&#13;&#10;").replace("\n", "&#13;&#10;").replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "&#13;&#10;");
      }
      
      data = "\"" + (data == null || "null".equals(data) ? "" : data) + "\"";
      
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 保存Note
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveNote(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    String note = request.getParameter("note");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      this.logic.saveNote(dbConn, user.getSeqId(), note);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
}