package t9.core.funcs.menu.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import t9.core.funcs.menu.data.T9SysFunction;
import t9.core.funcs.menu.data.T9SysMenu;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.security.data.T9Security;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9SysMenuLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  
  /**
   * 菜单主分类列表数据
   * @param dbConn
   * @return
   * @throws Exception
   */
  public ArrayList<T9SysMenu> menuList(Connection dbConn) throws Exception{
    Statement stmt = null;
    ResultSet rs = null; 
    ArrayList<T9SysMenu> menuList = null;
    try{
      String queryStr = "select SEQ_ID" 
      		              + ", MENU_ID" 
      		              + ", MENU_NAME" 
      		              + ", IMAGE from SYS_MENU order by MENU_ID";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9SysMenu menu = null;
      menuList = new ArrayList<T9SysMenu>();
      while(rs.next()){
        menu = new T9SysMenu();
        menu.setSeqId(rs.getInt("SEQ_ID"));
        menu.setMenuId(rs.getString("MENU_ID"));
        menu.setMenuName(rs.getString("MENU_NAME"));
        menu.setImage(rs.getString("IMAGE"));
        menuList.add(menu);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    return menuList;
  }
  
  public void deleteSysMenu(Connection conn, String seqId, String menuId) throws Exception{ 
    Statement stmt = null;
    try{
      stmt = conn.createStatement();
      String sql = "delete from SYS_FUNCTION where MENU_ID like '" + menuId + "%'";
      stmt.executeUpdate(sql);
      sql = "delete from SYS_MENU where SEQ_ID = " + seqId;
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void deleteSysFunc(Connection conn, String menuId) throws Exception{ 
    Statement stmt = null;
    try{
      stmt = conn.createStatement();
      String sql = "delete from SYS_FUNCTION where MENU_ID like '" + menuId + "%'";
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, null, log);
    }
  }
  
  public ArrayList<T9SysFunction> listFunction(Connection conn, String menuId) throws Exception{
    Statement stmt = null;
    ResultSet rs = null; 
    ArrayList<T9SysFunction> functionList = null;
    try{
      String queryStr = "select SEQ_ID, MENU_ID, FUNC_NAME, FUNC_CODE from SYS_FUNCTION where MENU_ID like '" + menuId + "%' order by MENU_ID";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9SysFunction function = null;
      functionList = new ArrayList<T9SysFunction>();
      while(rs.next()){
        function = new T9SysFunction();
        function.setSeqId(rs.getInt("SEQ_ID"));
        function.setMenuId(rs.getString("MENU_ID"));
        function.setFuncName(rs.getString("FUNC_NAME"));
        if(T9Utility.isNullorEmpty(rs.getString("FUNC_CODE"))){
          function.setFuncCode("org");
        }else{
          function.setFuncCode(rs.getString("FUNC_CODE"));
        }
        functionList.add(function);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, null, log);
    }
    return functionList;
  }
  
  public int selectMenu(Connection conn, String menuId)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select count(*) from SYS_MENU where MENU_ID='" + menuId + "'"; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      int num = 0;
      if(rs.next()){      
        num = rs.getInt(1);
      }
      return num;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public int selectMenuName(Connection conn, String menuName)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select count(*) from SYS_MENU where MENU_Name='" + menuName + "'"; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      int no = 0;
      if(rs.next()){      
        no = rs.getInt(1);
      }
      return no;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public int selectFunctionNum(Connection conn, String menuAdd, String menuId, String menuSort)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select count(*) from SYS_FUNCTION where MENU_ID = '" + menuId + menuAdd + menuSort + "'";
      //System.out.println(queryStr);
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      int num = 0;
      if(rs.next()){      
        num = rs.getInt(1);
      }
      return num;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public int selectFunction(Connection conn, String menuId, String menuSort)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select count(*) from SYS_FUNCTION where MENU_ID = '" + menuId + menuSort + "'";
      //System.out.println(queryStr);
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      int num = 0;
      if(rs.next()){      
        num = rs.getInt(1);
      }
      return num;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateMenu(Connection conn, String menuId, String menuName, String image, String menuIdOld)throws Exception {
    PreparedStatement pstmt = null;
    try {
      String updateStr = "update SYS_MENU set MENU_NAME = ?, IMAGE = ?, MENU_ID = ? where MENU_ID ='"+menuIdOld+"'";
      pstmt = conn.prepareStatement(updateStr);
      pstmt.setString(1 , menuName);
      pstmt.setString(2 , image);
      pstmt.setString(3 , menuId);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
  
  public void insertMenu(Connection conn, String menuId, String menuName, String image)throws Exception {
    PreparedStatement pstmt = null;
    try {
      String seqStr = "insert into SYS_MENU (MENU_ID, MENU_NAME, IMAGE) values(?, ?, ?)";
      pstmt = conn.prepareStatement(seqStr);
      pstmt.setString(1 , menuId);
      pstmt.setString(2 , menuName);
      pstmt.setString(3 , image);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
  
  public void updateFunction(Connection conn, String menuId, String menuIdOld, String funcMenuId)throws Exception {
    PreparedStatement pstmt = null;
    try {     
      String updateStr = "update SYS_FUNCTION set MENU_ID = ? where MENU_ID = ?";
      pstmt = conn.prepareStatement(updateStr);
      pstmt.setString(1 , menuId + funcMenuId.substring(2));
      pstmt.setString(2 , menuIdOld + funcMenuId.substring(2));
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, null, log);
    }
  }
  
  public void deleteMenu(Connection conn, String menuId) throws Exception {
    Statement stmt = null;
    try {
      //今天stmt对象没有创建，所以出空指针异常
      stmt = conn.createStatement();
      String deleteStr = "delete from SYS_MENU where MENU_ID = '" + menuId + "'";
      stmt.executeUpdate(deleteStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public String getSysfunctionByMenuId(Connection conn, String menuId) throws Exception {
    String menuIdParent = menuId.substring(0,4);
    String funcName = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select FUNC_NAME from SYS_FUNCTION where MENU_ID='" + menuIdParent + "'"; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if(rs.next()){
        funcName = rs.getString("FUNC_NAME");     
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
    return funcName;
  }
  
  /**
   * 获取         cc 20100317
   * @param conn
   * @param menuId
   * @return
   * @throws Exception
   */
  
  public ArrayList<T9SysFunction> getSysFunctionMenuId(Connection dbConn, String menuId) throws Exception{
    Statement stmt = null;
    ResultSet rs = null; 
    ArrayList<T9SysFunction> functionList = null;
    try{
      String queryStr = "select SEQ_ID, MENU_ID from SYS_FUNCTION where MENU_ID like '" + menuId + "%' order by MENU_ID";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9SysFunction function = null;
      functionList = new ArrayList<T9SysFunction>();
      while(rs.next()){
        function = new T9SysFunction();
        function.setSeqId(rs.getInt("SEQ_ID"));
        function.setMenuId(rs.getString("MENU_ID"));
        functionList.add(function);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    return functionList;
  }
  
  /**
   *  获取         cc 20100317
   * @param dbConn
   * @param menuId
   * @return
   * @throws Exception
   */
  
  public ArrayList<T9SysFunction> getSysFunctionMenuId1(Connection dbConn, String menuId) throws Exception{
    Statement stmt = null;
    ResultSet rs = null; 
    ArrayList<T9SysFunction> functionList = null;
    try{
      String queryStr = "select SEQ_ID, MENU_ID from SYS_FUNCTION where MENU_ID like '" + menuId + "%' order by MENU_ID";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9SysFunction function = null;
      functionList = new ArrayList<T9SysFunction>();
      while(rs.next()){
        function = new T9SysFunction();
        function.setSeqId(rs.getInt("SEQ_ID"));
        function.setMenuId(rs.getString("MENU_ID"));
        functionList.add(function);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    return functionList;
  }
  
  /**
   * 查询权限
   * @param dbConn
   * @return
   * @throws Exception
   */
  
  public ArrayList<T9UserPriv> getUserPrivList(Connection dbConn) throws Exception{
    Statement stmt = null;
    ResultSet rs = null; 
    ArrayList<T9UserPriv> userPrivList = null;
    try{
      String queryStr = "select SEQ_ID, FUNC_ID_STR from USER_PRIV";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9UserPriv userPriv = null;
      userPrivList = new ArrayList<T9UserPriv>();
      
      while(rs.next()){
        userPriv = new T9UserPriv();
        userPriv.setSeqId(rs.getInt("SEQ_ID"));
        userPriv.setFuncIdStr(rs.getString("FUNC_ID_STR"));
        userPrivList.add(userPriv);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    return userPrivList;
  }
  
  /**
   * 读取快捷菜单栏每行显示个数
   * @param conn
   * @return
   * @throws Exception
   */
  
  public T9Security getMenuPara(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='TOP_MENU_NUM'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  /**
   * 修改快捷菜单栏每行显示个数
   * @param conn
   * @param topMenuNum
   * @throws Exception
   */
  
  public void updateTopMenuNum(Connection conn, String topMenuNum) throws Exception{
    Statement stmt = null;
    //System.out.println(topMenuNum);
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + topMenuNum + "' where PARA_NAME = 'TOP_MENU_NUM'" ;
      //System.out.println(queryStr);
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  /**
   * 导航菜单
   * @param conn
   * @param sum   显示菜单快捷组、显示Windows快捷组、显示常用网址 串
   * @throws Exception
   */
  
  public void updateMenuNavigation(Connection conn, String sum) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sum + "' where PARA_NAME = 'MENU_DISPLAY'" ;
      //System.out.println(queryStr);
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  /**
   * 同时只能展开一个一级菜单
   * @param conn
   * @param sumSingle  
   * @throws Exception
   */
  
  public void updateMenuSingle(Connection conn, String sumSingle) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumSingle + "' where PARA_NAME = 'MENU_EXPAND_SINGLE'" ;
      //System.out.println(queryStr);
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public boolean checkLevel(Connection dbConn, String menuIdStr)throws Exception {
    try {
      T9SysMenuLogic menuLogic = new T9SysMenuLogic();
      ArrayList<T9SysFunction> functionList = null;
      functionList = menuLogic.getSysFunctionMenuId1(dbConn, menuIdStr);
      for (int i = 0; i < functionList.size(); i++) {
        T9SysFunction sytFuns = functionList.get(i);
        String menuIdStrs = sytFuns.getMenuId();
        if(menuIdStrs.length() == 6){
          return true;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } 
    return false;
  }
}
