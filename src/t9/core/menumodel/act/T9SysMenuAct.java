package t9.core.menumodel.act;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.menumodel.data.T9SysFunction;
import t9.core.menumodel.data.T9SysMenu;
import t9.core.menumodel.logic.T9SysMenuLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DTJ;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9SysMenuAct {
  ArrayList<T9SysFunction> functionList = null;
  T9SysMenuLogic menuLogic = new T9SysMenuLogic();
  public String listSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      ArrayList<T9SysMenu> menuList = null;
      menuList = menuLogic.getSysMenuList(dbConn);
      
      request.setAttribute("menuList", menuList);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/menumodel/sysmenulist.jsp";
  }
}
