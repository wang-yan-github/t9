package t9.core.funcs.menu.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.menu.data.T9SysFunction;
import t9.core.funcs.menu.data.T9SysMenu;
import t9.core.funcs.menu.logic.T9SysMenuLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.data.T9MenuCache;
import t9.core.funcs.system.data.T9UserPrivCache;
import t9.core.funcs.system.security.data.T9Security;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9SysMenuAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.menu.act.T9SysMenuAct");
  
  T9SysMenuLogic menuLogic = new T9SysMenuLogic();
  ArrayList<T9SysFunction> functionList = null;
  public String listSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      ArrayList<T9SysMenu> menuList = null;
      T9ORM orm = new T9ORM();
      List<Map> list = new ArrayList();
      Map fi = null;
      //menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, fi);
      menuList = menuLogic.menuList(dbConn);
      request.setAttribute("menuList", menuList);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/menu/sysmenulist.jsp";
  }
  
  public String listSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String menuId = request.getParameter("menuId");
    if(menuId.length() > 2) {
      menuId = menuId.substring(0, 2);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      functionList = menuLogic.listFunction(dbConn, menuId);
      request.setAttribute("functionList", functionList);
      String sql = "select IMAGE, MENU_NAME from SYS_MENU where MENU_ID = '" + menuId + "'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()) {
        String image = rs.getString("IMAGE");
        String menuName = rs.getString("MENU_NAME");
        request.setAttribute("image", image);
        request.setAttribute("menuName", menuName);
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/menu/sysfunctionlist.jsp?menuId=" + menuId;
  }
  
  public String getSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysMenu menu = null;
      String data = null;
      T9ORM orm = new T9ORM();
      menu = (T9SysMenu)orm.loadObjSingle(dbConn, T9SysMenu.class, Integer.parseInt(seqId));
      if(menu == null) {
        menu = new T9SysMenu();
      }
      data = T9FOM.toJson(menu).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding("UTF-8");
    Connection dbConn = null;
    String data = null;
    String seqId = request.getParameter("seqId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String query = "select * from SYS_FUNCTION where SEQ_ID = " + seqId;
      Statement stm1 = null;
      ResultSet rs1 = null;
      StringBuffer  d = new StringBuffer();
      d.append("{");
      try {
        stm1 = dbConn.createStatement();
        rs1 = stm1.executeQuery(query);
        if (rs1.next()) {
          d.append("seqId:" + rs1.getInt("SEQ_ID"));
          d.append(",menuId:'" + rs1.getString("MENU_ID") + "'");
          d.append(",funcName:'" + T9Utility.encodeSpecial(rs1.getString("FUNC_NAME")) + "'");
          d.append(",funcCode:'" + T9Utility.encodeSpecial(rs1.getString("FUNC_CODE")) + "'");
          d.append(",icon:'" + T9Utility.encodeSpecial(rs1.getString("ICON")) + "'");
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm1, rs1, null);
      }
      d.append("}");
      data = d.toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String menuId = null;
    String menuName = null;
    String image = null;
    int num = 0;
    int no = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      T9SysMenu menu = (T9SysMenu)T9FOM.build(request.getParameterMap());
      menuId = menu.getMenuId();
      if(T9Utility.isNullorEmpty(menuId)) {
        return "/core/inc/rtjson.jsp";
      }
      menuName = menu.getMenuName();
      if(T9Utility.isNullorEmpty(menuName)) {
        return "/core/inc/rtjson.jsp";
      }
      image = menu.getImage();
      if(T9Utility.isNullorEmpty(image)) {
        return "/core/inc/rtjson.jsp";
      }
      no = menuLogic.selectMenuName(dbConn, menuName);
      if(no >= 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "菜单名称已存在, 请重新填写");
        return "/core/inc/rtjson.jsp";
      }
      num = menuLogic.selectMenu(dbConn, menuId);
      if(num >= 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "主菜单分类代码已存在, 请重新填写");
        return "/core/inc/rtjson.jsp";
      }
      
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, menu);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加主菜单分类");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String menuId = null;
    int num = 0;
    String menuSort = null;
    String funcName = "";
    String funcCode = "";
    String icon = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      T9SysFunction function = (T9SysFunction)T9FOM.build(request.getParameterMap());
      menuId = function.getMenuId();
      menuSort = function.getMenuSort();
      if(T9Utility.isNullorEmpty(menuSort)) {
        return "/core/inc/rtjson.jsp";
      }
      if(menuSort.length() != 2) {
        return "/core/inc/rtjson.jsp";
      }
      funcName = function.getFuncName();
      if(T9Utility.isNullorEmpty(funcName)) {
        return "/core/inc/rtjson.jsp";
      }
      funcCode = function.getFuncCode();
      if(T9Utility.isNullorEmpty(funcCode)){
        funcCode = "org";
      }else{
        funcCode = function.getFuncCode();
      }
      icon = function.getIcon();
      if(T9Utility.isNullorEmpty(icon)){
        icon = "edit.gif";
      }else{
        icon = function.getIcon();
      }
      num = menuLogic.selectFunction(dbConn, menuId, menuSort);
      if(num >= 1) {
        String flagSrc = menuId + menuSort;
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "子菜单代码" + menuSort + "已存在,请重新填写！");
        return "/core/inc/rtjson.jsp";
      }
      Map map = new HashMap();
      map.put("menuId", "" + function.getMenuId() + function.getMenuSort() + "");
      map.put("funcName", function.getFuncName());
      map.put("funcCode", funcCode);
      map.put("icon", icon);
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, "sysFunction", map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加子菜单项");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 菜单菜单主分类：编辑  cc 20100317
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
   
  public String updateSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    ArrayList<T9UserPriv> userPrivList = null;
    String menuIdOld = null;
    String menuId = null;
    String menuName = null;
    String image = null;
    String menuIdStr = "";
    int num = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginUserSeqId = person.getSeqId();
      String loginUserRole = person.getUserPriv();
      
      T9SysMenu menu = (T9SysMenu)T9FOM.build(request.getParameterMap());
      menuIdOld = request.getParameter("menuIdOld");
      menuId = menu.getMenuId();
      if(menuId == null || "".equals(menuId)) {
        return "/core/inc/rtjson.jsp";
      }
      menuName = menu.getMenuName();
      if(menuName == null || "".equals(menuName)) {
        return "/core/inc/rtjson.jsp";
      }
      image = menu.getImage();
      if(image == null || "".equals(image)) {
        return "/core/inc/rtjson.jsp";
      }
      
      if(menuIdOld.equals(menuId)) {
        menuLogic.updateMenu(dbConn, menuId, menuName, image, menuIdOld);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改主菜单分类");  
        return "/core/inc/rtjson.jsp";
      }else {
        String funcMenuId = null;
        num = menuLogic.selectMenu(dbConn, menuId);
        if(num >= 1) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "主菜单分类代码已存在, 请重新填写");
          return "/core/inc/rtjson.jsp";
        }
        T9ORM orm = new T9ORM();
        functionList = menuLogic.getSysFunctionMenuId(dbConn, menuIdOld);
        for(int i = 0; i < functionList.size(); i++){
          T9SysFunction sytFuns = functionList.get(i);
          int seqId = 0;
          if(sytFuns.getMenuId().length() <= 6){
            seqId = sytFuns.getSeqId();
            menuIdStr = sytFuns.getMenuId();
            String subFunc = menuIdStr.substring(2, menuIdStr.length());
            String menuIdsss = menuId + subFunc;
            Map m =new HashMap();
            m.put("seqId", seqId);
            m.put("menuId", menuIdsss);
            orm.updateSingle(dbConn, "sysFunction", m);
          }
        }
        menuLogic.updateMenu(dbConn, menuId, menuName, image, menuIdOld);
        upUserPriv(menuIdOld, menuId, dbConn);
        
        T9UserPrivCache.removeAll();
        T9MenuCache.removeAll();
//        String menuIdNew = "";
//        String funcIdStr = "";
//        for(int z = 0; z < userPrivList.size(); z++){
//          String strs = "";
//          T9UserPriv userPrivStr = userPrivList.get(z);
//          String privStrs = userPrivStr.getFuncIdStr();
//          int seqId = userPrivStr.getSeqId();
//          String privStr[] = privStrs.split(",");
//          for(int x = 0; x < privStr.length; x++){
//            funcIdStr = privStr[x];
//            if(funcIdStr.startsWith(menuIdOld)){
//              if(funcIdStr.length() == 2){
//                funcIdStr = menuId;
//              }else{
//                funcIdStr.substring(2, funcIdStr.length());
//                funcIdStr = menuId + funcIdStr.substring(2, funcIdStr.length());
//              }
//            }
//            strs += funcIdStr+",";
//          }
//          Map m =new HashMap();
//          m.put("seqId", seqId);
//          m.put("funcIdStr", strs);
//          orm.updateSingle(dbConn, "userPriv", m);
//        }
        
//        menuLogic.insertMenu(dbConn, menuId, menuName, image);
//        functionList = menuLogic.listFunction(dbConn, menuIdOld);
//        for(int i = 0; i < functionList.size(); i++) {
//          T9SysFunction function  = functionList.get(i);
//          funcMenuId = function.getMenuId();
//          menuLogic.updateFunction(dbConn, menuId, menuIdOld, funcMenuId);
//        }
//        menuLogic.deleteMenu(dbConn, menuIdOld);
        
        
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功修改主菜单分类");
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";    
  }
  
  /**
   * 编辑菜单主分类在权限表USER_PRIV中修改menuId 例如：8111,21,2110,211010,2112,3110,310101
   *   修改后（把21改成71）：8111,71，2110，211010，2112，3110，311010
   * @param menuIdOld  原menuId
   * @param menuId     改后的menuId
   * @param dbConn
   */
  
  public void upUserPriv(String menuIdOld, String menuId, Connection dbConn) {
    String menuIdNew = "";
    String funcIdStr = "";
    T9ORM orm = new T9ORM();
    ArrayList<T9UserPriv> userPrivList = null;
    try {
      userPrivList = menuLogic.getUserPrivList(dbConn);
      for (int z = 0; z < userPrivList.size(); z++) {
        String strs = "";
        T9UserPriv userPrivStr = userPrivList.get(z);
        String privStrs = userPrivStr.getFuncIdStr();
        int seqId = userPrivStr.getSeqId();
        String privStr[] = privStrs.split(",");
        for (int x = 0; x < privStr.length; x++) {
          funcIdStr = privStr[x];
          if (funcIdStr.startsWith(menuIdOld)) {
            if (funcIdStr.length() == 2) {
              funcIdStr = menuId;
            } else {
              funcIdStr.substring(2, funcIdStr.length());
              funcIdStr = menuId + funcIdStr.substring(2, funcIdStr.length());
            }
          }
          strs += funcIdStr + ",";
        }
        Map m = new HashMap();
        m.put("seqId", seqId);
        m.put("funcIdStr", strs);
        orm.updateSingle(dbConn, "userPriv", m);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  /**
   * 子菜单设置编辑第一级菜单（4位）时，对权限表中对映的4位串同时进行修改 
   * 例如：USER_PRIV表 FUNC_ID_STR字段中4位的串进行修改，7110，7112，711010，711210等 ，
   *       因为4位的下一级是6位，所以对6位的中间2位同时进行修改
   *       将10替换为15，结果：7115，7112，711510，711210
   * @param menuIdStr  对映的menuId SYS_FUNCTION表
   * @param dbConn
   * @param menuId     SYS_MENU表中的menuId
   * @param menuSort   新的menuId串（客户端修改后的）
   */
  
  public void upUserPriv4(String menuIdStr, Connection dbConn, String menuId,
      String menuSort, String menuId4) {
    String menuIdNew = "";
    String funcIdStr = "";
    T9ORM orm = new T9ORM();
    ArrayList<T9UserPriv> userPrivList = null;
    try {
      userPrivList = menuLogic.getUserPrivList(dbConn);
      for (int z = 0; z < userPrivList.size(); z++) {
        String strs = "";
        T9UserPriv userPrivStr = userPrivList.get(z);
        String privStrs = userPrivStr.getFuncIdStr();
        int seqId = userPrivStr.getSeqId();
        String privStr[] = privStrs.split(",");
        for (int x = 0; x < privStr.length; x++) {
          funcIdStr = privStr[x];
          if (funcIdStr.startsWith(menuIdStr)) {
            if (funcIdStr.length() == 4) {
              funcIdStr = menuId4 + menuSort; //menuIdStr.substring(2, menuIdStr.length());
            } else if (funcIdStr.length() == 6) {
              funcIdStr = menuId4 + funcIdStr.substring(2, funcIdStr.length());
            }
          }
          strs += funcIdStr + ",";
        }
        Map m = new HashMap();
        m.put("seqId", seqId);
        m.put("funcIdStr", strs);
        orm.updateSingle(dbConn, "userPriv", m);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  public void updateChangeUserPriv4(String menuIdStr, Connection dbConn, String menuId,
      String menuSort, String menuId4) {
    String menuIdNew = "";
    String funcIdStr = "";
    T9ORM orm = new T9ORM();
    ArrayList<T9UserPriv> userPrivList = null;
    try {
      userPrivList = menuLogic.getUserPrivList(dbConn);
      for (int z = 0; z < userPrivList.size(); z++) {
        String strs = "";
        T9UserPriv userPrivStr = userPrivList.get(z);
        String privStrs = userPrivStr.getFuncIdStr();
        int seqId = userPrivStr.getSeqId();
        String privStr[] = privStrs.split(",");
        for (int x = 0; x < privStr.length; x++) {
          funcIdStr = privStr[x];
          if (funcIdStr.startsWith(menuIdStr)) {
            if (funcIdStr.length() == 4) {
              funcIdStr = menuId4 + menuSort; //menuIdStr.substring(2, menuIdStr.length());
            } else if (funcIdStr.length() == 6) {
              funcIdStr = menuId4 + menuSort + funcIdStr.substring(4, funcIdStr.length());
            }
          }
          strs += funcIdStr + ",";
        }
        Map m = new HashMap();
        m.put("seqId", seqId);
        m.put("funcIdStr", strs);
        orm.updateSingle(dbConn, "userPriv", m);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  /**
   * 判断原menuId是6位的 修改权限表中对映的menuId
   * @param menuIdStr
   * @param dbConn
   * @param menuId
   * @param menuSort
   * @param menuId4
   */
  
  public void upUserPriv46(String menuIdStr, Connection dbConn, String menuId,
      String menuSort, String menuId4) {
    String menuIdNew = "";
    String funcIdStr = "";
    T9ORM orm = new T9ORM();
    ArrayList<T9UserPriv> userPrivList = null;
    try {
      userPrivList = menuLogic.getUserPrivList(dbConn);
      for (int z = 0; z < userPrivList.size(); z++) {
        String strs = "";
        T9UserPriv userPrivStr = userPrivList.get(z);
        String privStrs = userPrivStr.getFuncIdStr();
        int seqId = userPrivStr.getSeqId();
        String privStr[] = privStrs.split(",");
        for (int x = 0; x < privStr.length; x++) {
          funcIdStr = privStr[x];
          if (funcIdStr.startsWith(menuIdStr)) {
            if (funcIdStr.length() == 6) {
              funcIdStr = menuId4 + funcIdStr.substring(4, funcIdStr.length());
            }
          }
          strs += funcIdStr + ",";
        }
        Map m = new HashMap();
        m.put("seqId", seqId);
        m.put("funcIdStr", strs);
        orm.updateSingle(dbConn, "userPriv", m);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  /**
   * 子菜单设置编辑第二级菜单（6位）时，对权限表中对映的6位串同时进行修改 
   * 例如;711010，711210, 将711010 的10修改成11，结果：711011，711210，
   * @param menuIdStr   对映的menuId SYS_FUNCTION表
   * @param dbConn
   * @param menuId      SYS_MENU表中的menuId
   * @param menuSort    新的menuId串（客户端修改后的）
   */
  
  public void upUserPriv6(String menuIdStr, Connection dbConn, String menuId,
      String menuSort, String menuId4) {
    //System.out.println(menuSort+"UIYTY");
    String menuIdNew = "";
    String funcIdStr = "";
    T9ORM orm = new T9ORM();
    ArrayList<T9UserPriv> userPrivList = null;
    try {
      userPrivList = menuLogic.getUserPrivList(dbConn);
      for (int z = 0; z < userPrivList.size(); z++) {
        String strs = "";
        T9UserPriv userPrivStr = userPrivList.get(z);
        String privStrs = userPrivStr.getFuncIdStr();
        int seqId = userPrivStr.getSeqId();
        String privStr[] = privStrs.split(",");
        for (int x = 0; x < privStr.length; x++) {
          funcIdStr = privStr[x];
          if (funcIdStr.startsWith(menuIdStr)) {
            funcIdStr.substring(0, funcIdStr.length() - 2);
            if(menuId4.length() == 2){
              funcIdStr = menuId4 + menuIdStr.substring(2, menuIdStr.length());
            }else if(menuId4.length() == 4 && menuIdStr.length() != 4){
              //funcIdStr = menuId4 + menuIdStr.substring(4, menuIdStr.length());
              funcIdStr = menuId4 + menuSort;
            }else if(menuId4.length() == 4 && menuIdStr.length() == 4){
              funcIdStr = menuId4 + menuIdStr.substring(2, menuIdStr.length());
            }
          }
          strs += funcIdStr + ",";
        }
        Map m = new HashMap();
        m.put("seqId", seqId);
        m.put("funcIdStr", strs);
        orm.updateSingle(dbConn, "userPriv", m);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  /**
   * 子菜单项:编辑  cc 20100317
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = null;
    String menuSortOld = request.getParameter("menuSortOld");
    String menuLast = request.getParameter("menuLast");          //原上级菜单编码
    String menuIdStr = request.getParameter("menuIdStr");       //旧的原来的 本身menuId
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    String menuIdFlagStr = request.getParameter("menuId"); //新的menuId或者是旧的        
    String menuIdFlag = menuIdFlagStr.substring(0,2);     //修改时 可能把原来的3级菜单编辑成2级菜单  menuIdFlag用来获取新菜单的menuId(获取前2位)
    String menuIdOld = request.getParameter("menuIdOld"); //没修改之前的menuId
    String menuSortMid = request.getParameter("menuSort");
    String menuId4 = request.getParameter("menuId");    //上级菜单menuId
    String menuIdOverAll = request.getParameter("menuIdOverAll");   //新的上级菜单menuId
    ArrayList<T9UserPriv> userPrivList = null;
    String menuId = null;
    String menuSort = null;
    String menuAdd = null;
    String funcName = null;
    String funcCode = null;
    String sum = null;
    String last = null;
    String lastTwo = null;
    String menuIdSum = null;
    String iconStr = "";
    int num = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      T9SysFunction function = (T9SysFunction) T9FOM.build(request.getParameterMap());
      menuId = function.getMenuId();
      menuSort = function.getMenuSort(); // new
      if (menuSort == null || "".equals(menuSort)) {
        return "/core/inc/rtjson.jsp";
      }
      if (menuSort.length() != 2) {
        return "/core/inc/rtjson.jsp";
      }
      funcName = function.getFuncName();
      if (funcName == null || "".equals(funcName)) {
        return "/core/inc/rtjson.jsp";
      }
      menuAdd = function.getMenuAdd();
      if (menuAdd == null) {
        menuAdd = "";
      }
      if(T9Utility.isNullorEmpty(function.getIcon())){
        iconStr = "edit.gif";
      }else{
        iconStr = function.getIcon();
      }
      if(T9Utility.isNullorEmpty(function.getFuncCode())){
        funcCode = "org";
      }else{
        funcCode = function.getFuncCode();
      }
      String menuIdOverStr = menuIdOverAll.substring(0,4);
      if(menuIdOverAll.length() == 4){
        menuIdOverStr = menuIdOverAll.substring(0,2);
      }
      if(menuIdOverStr.equals(menuId4)){
        if (menuSortOld.equals(String.valueOf(menuSort)) && menuLast.equals(menuIdFlagStr)) {
          map = new HashMap();
          map.put("seqId", "" + function.getSeqId());
          //map.put("menuId", "" + menuIdOld + menuSortOld + "");//function.getMenuSort() +
          map.put("funcName", function.getFuncName());
          map.put("funcCode", funcCode);
          map.put("icon", iconStr);
          orm.updateSingle(dbConn, "sysFunction", map);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改子菜单项");
          return "/core/inc/rtjson.jsp";
        } else {
          num = menuLogic.selectFunctionNum(dbConn, menuId, menuAdd, menuSort);
           if (num >= 1) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "子菜单代码已存在, 请重新填写");
            return "/core/inc/rtjson.jsp";
          }
          menuId = menuIdFlag;                                            //把新的menuId替换旧的
          if (menuId4.length() == 2) {                                   //判断上级菜单menuId是2位
            functionList = menuLogic.getSysFunctionMenuId1(dbConn, menuIdStr);
            for (int i = 0; i < functionList.size(); i++) {
              T9SysFunction sytFuns = functionList.get(i);
              int seqIds = sytFuns.getSeqId();
              String menuIdStrs = sytFuns.getMenuId();                   //原menuId串
              if(menuIdStrs.length() == 4){
                last = menuSortMid; //menuIdStrs.substring(2, menuIdStrs.length());
                sum = menuId + last;
              }else if(menuIdStrs.length() == 6){
                last = menuIdStrs.substring(4, menuIdStrs.length());
                sum = menuId4 + menuSortMid + last;
              }
              Map m = new HashMap();
              m.put("seqId", seqIds);
              m.put("menuId", sum);
              orm.updateSingle(dbConn, "sysFunction", m);
            }
            if(menuIdStr.length() == 4){                                  //刚开始修改的原menuId是4位
              updateChangeUserPriv4(menuIdStr, dbConn, menuId, menuSort, menuId4);  //修改权限串
            }else if(menuIdStr.length() == 6){                            //刚开始修改的原menuId是6位（4、6位相当于是个标记，截取字符串的位数不同（截取后2位或者是后4位））
              upUserPriv46(menuIdStr, dbConn, menuId, menuSort, menuId4);
            }
          } else if (menuId4.length() == 4) {                             //判断上级菜单menuId是4位            if(menuIdStr.length() == 4){
              lastTwo = menuIdStr.substring(2, menuIdStr.length());
              menuIdSum = menuId4 + lastTwo;
            }else if(menuIdStr.length() == 6){
              //lastTwo = menuIdStr.substring(4, menuIdStr.length());
              lastTwo = menuSort;
              menuIdSum = menuId4 + lastTwo;
            }
            map = new HashMap();
            map.put("seqId", "" + function.getSeqId());
            map.put("menuId", menuIdSum);
            map.put("funcName", function.getFuncName());
            map.put("funcCode", funcCode);
            map.put("icon", iconStr);
            orm.updateSingle(dbConn, "sysFunction", map);
            upUserPriv6(menuIdStr, dbConn, menuId, menuSort, menuId4);
          }
        }

      } else {
        if (menuIdStr.length() != 6) {
          if (menuId.length() != 2) {
            if (menuLogic.checkLevel(dbConn, menuIdStr)) {
              request
                  .setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
              request.setAttribute(T9ActionKeys.RET_MSRG,
                  "该菜单下含有子目录不能添加到2级菜单中，请从新选择！");
              return "/core/inc/rtjson.jsp";
            }
          }
        }
        if (menuSortOld.equals(String.valueOf(menuSort)) && menuLast.equals(menuIdFlagStr)) {
          map = new HashMap();
          map.put("seqId", "" + function.getSeqId());
          //map.put("menuId", "" + menuIdOld + menuSortOld + "");//function.getMenuSort() +
          map.put("funcName", function.getFuncName());
          map.put("funcCode", funcCode);
          map.put("icon", iconStr);
          orm.updateSingle(dbConn, "sysFunction", map);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改子菜单项");
          return "/core/inc/rtjson.jsp";
        } else {
          num = menuLogic.selectFunctionNum(dbConn, menuId, menuAdd, menuSort);
           if (num >= 1) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "子菜单代码已存在, 请重新填写");
            return "/core/inc/rtjson.jsp";
          }
          menuId = menuIdFlag;                                            //把新的menuId替换旧的
          if (menuId4.length() == 2) {                                   //判断上级菜单menuId是2位            functionList = menuLogic.getSysFunctionMenuId1(dbConn, menuIdStr);
            for (int i = 0; i < functionList.size(); i++) {
              T9SysFunction sytFuns = functionList.get(i);
              int seqIds = sytFuns.getSeqId();
              String menuIdStrs = sytFuns.getMenuId();                   //原menuId串
              if(menuIdStr.length() == 4){
                last = menuIdStrs.substring(2, menuIdStrs.length());
                sum = menuId + last;
              }else if(menuIdStr.length() == 6){
                last = menuIdStrs.substring(4, menuIdStrs.length());
                sum = menuId4 + last;
              }
              Map m = new HashMap();
              m.put("seqId", seqIds);
              m.put("menuId", sum);
              orm.updateSingle(dbConn, "sysFunction", m);
            }
            if(menuIdStr.length() == 4){                                  //刚开始修改的原menuId是4位              upUserPriv4(menuIdStr, dbConn, menuId, menuSort, menuId4);  //修改权限串            }else if(menuIdStr.length() == 6){                            //刚开始修改的原menuId是6位（4、6位相当于是个标记，截取字符串的位数不同（截取后2位或者是后4位））              upUserPriv46(menuIdStr, dbConn, menuId, menuSort, menuId4);
            }
          } else if (menuId4.length() == 4) {                             //判断上级菜单menuId是4位            if(menuIdStr.length() == 4){
              lastTwo = menuIdStr.substring(2, menuIdStr.length());
              menuIdSum = menuId4 + lastTwo;
            }else if(menuIdStr.length() == 6){
              lastTwo = menuIdStr.substring(4, menuIdStr.length());
              menuIdSum = menuId4 + lastTwo;
            }
            map = new HashMap();
            map.put("seqId", "" + function.getSeqId());
            map.put("menuId", menuIdSum);
            map.put("funcName", function.getFuncName());
            map.put("funcCode", funcCode);
            map.put("icon", iconStr);
            orm.updateSingle(dbConn, "sysFunction", map);
            upUserPriv6(menuIdStr, dbConn, menuId, menuSort, menuId4);
          }
        }
      }
      
      T9UserPrivCache.removeAll();
      T9MenuCache.removeAll();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改子菜单项");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteSysMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    String seqId = request.getParameter("seqId");
    String menuId = request.getParameter("menuId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      menuLogic.deleteSysMenu(dbConn, seqId, menuId);
      deleteUserPriv(menuId, dbConn);
      
      T9UserPrivCache.removeAll();
      T9MenuCache.removeAll();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除主菜单分类");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteSysFunction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    String menuId = request.getParameter("menuId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      menuLogic.deleteSysFunc(dbConn, menuId);
      deleteUserPriv(menuId, dbConn);
      
      T9UserPrivCache.removeAll();
      T9MenuCache.removeAll();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除子菜单项");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除权限表中对映的menuId串
   * @param menuId   表中(SYS_MENU或SYS_FUNCTION)对映的menuId字段
   * @param dbConn
   */
  
  public void deleteUserPriv(String menuId, Connection dbConn) {
    String menuIdNew = "";
    String funcIdStr = "";
    T9ORM orm = new T9ORM();
    ArrayList<T9UserPriv> userPrivList = null;
    try {
      userPrivList = menuLogic.getUserPrivList(dbConn);
      for (int z = 0; z < userPrivList.size(); z++) {
        String strs = "";
        T9UserPriv userPrivStr = userPrivList.get(z);
        String privStrs = userPrivStr.getFuncIdStr();
        int seqId = userPrivStr.getSeqId();
        String privStr[] = privStrs.split(",");
        for (int x = 0; x < privStr.length; x++) {
          funcIdStr = privStr[x];
          if (funcIdStr.startsWith(menuId)) {
           continue;
          }
          strs += funcIdStr + ",";
        }
        Map m = new HashMap();
        m.put("seqId", seqId);
        m.put("funcIdStr", strs);
        orm.updateSingle(dbConn, "userPriv", m);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  public String getNoTree(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    HashMap map = null;
    String num = request.getParameter("num");
    String length = request.getParameter("length");
    if(num==null||num.equals("0")){
      num = "";
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      StringBuffer sb = new StringBuffer("[");
      if(Integer.parseInt(length) == 2) {
        ArrayList<T9SysMenu> menuList = null;
        
        //暂时的处理办法 1 = 1 order by ....
        menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, new String[]{" 1 = 1 order by MENU_ID"});
        for(int i = 0; i < menuList.size(); i++) {
          T9SysMenu menu = menuList.get(i);
          sb.append("{");
          sb.append("nodeId:\"" + menu.getMenuId() + "\"");
          sb.append(",name:\"" + menu.getMenuName() + "\"");
          sb.append(",isHaveChild:" + 1 + "");
          sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/menuIcon/" + menu.getImage() + "\"");
          sb.append("},");
        }       
        sb.deleteCharAt(sb.length() - 1);       
      } else {
        List funcList = new ArrayList();
        funcList.add("sysFunction");
        String[] filters = new String[]{"MENU_ID like '" + num + "%'"," length(MENU_ID) = " + length + "  order by MENU_ID"};
        map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
        List<Map> list = (List<Map>) map.get("SYS_FUNCTION");
        String contextPath = request.getContextPath();
        for(Map m : list) {
          String funcAddress = null;
          String imageAddress = null;
          String fun = (String) m.get("funcCode");
          imageAddress = contextPath + "/core/funcs/display/img/org.gif";
          if (fun.startsWith("/")) {
            funcAddress = contextPath + fun;
          }else {
            funcAddress = contextPath + "/core/funcs/" + fun + "/";
          }
          sb.append("{");
          sb.append("nodeId:\"" + m.get("menuId") + "\"");
          sb.append(",name:\"" + m.get("funcName") + "\"");
          sb.append(",isHaveChild:" + 0);
          sb.append(",imgAddress:\"" + imageAddress + "\"");
          sb.append("},");
        } 
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 快捷菜单栏每行显示个数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getMenuPara(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysMenuLogic orgLogic = new T9SysMenuLogic();
      T9Security org = null;
      String data = null;
      org = orgLogic.getMenuPara(dbConn);
      if (org == null) {
        org = new T9Security();
      }
      data = T9FOM.toJson(org).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取同时只能展开一个一级菜单的value（表SYS_PARA）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getMenuExpandSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"PARA_NAME = 'MENU_EXPAND_SINGLE'"};
      List funcList = new ArrayList();
      funcList.add("sysPara");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("SYS_PARA"));
      for(Map ms : list){
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",paraValue:\"" + (ms.get("paraValue") == null ? "" : ms.get("paraValue")) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if (list.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 读取显示菜单快捷组、显示Windows快捷组、显示常用网址  MENU_DISPLAY（表SYS_PARA字段）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getMenuDisplay(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[] { "PARA_NAME = 'MENU_DISPLAY'" };
      List funcList = new ArrayList();
      funcList.add("sysPara");
      map = (HashMap) orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("SYS_PARA"));
      for (Map ms : list) {
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",paraValue:\"" + (ms.get("paraValue") == null ? "" : ms.get("paraValue")) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (list.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改快捷菜单栏每行显示个数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateTopMenuNum(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      String topMenuNum = request.getParameter("topMenuNum");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      menuLogic.updateTopMenuNum(dbConn, topMenuNum);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改显示导航菜单（菜单设置）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateMenuNavigation(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sum = "";
      String sumSingle = "";
      String menuShorcut = request.getParameter("menuShorcut");
      String menuWinexe = request.getParameter("menuWinexe");
      String menuUrl = request.getParameter("menuUrl");
      String menuExpandSingle = request.getParameter("menuExpandSingle");
      if (menuShorcut.equals("true")) {
        sum += "SHORTCUT,";
      }
      if (menuWinexe.equals("true")) {
        sum += "WINEXE,";
      }
      if (menuUrl.equals("true")) {
        sum += "URL,";
      }
      if (menuExpandSingle.equals("true")) {
        sumSingle = "1";
      } else {
        sumSingle = "0";
      }
      menuLogic.updateMenuNavigation(dbConn, sum);
      menuLogic.updateMenuSingle(dbConn, sumSingle);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
