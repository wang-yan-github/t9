package t9.core.menumodel.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import t9.core.menumodel.data.T9SysMenu;
import t9.core.util.db.T9ORM;
public class T9SysMenuLogic {
  public ArrayList<T9SysMenu> getSysMenuList(Connection conn) throws Exception{
    ArrayList<T9SysMenu> menuList = null;
    T9ORM orm = new T9ORM();
    Map m = null;
    menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(conn, T9SysMenu.class, m);
    return menuList;
  } 
}
