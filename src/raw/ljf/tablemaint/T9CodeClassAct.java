package raw.ljf.tablemaint;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9CodeClassAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9CodeClassAct");
   
  public String listCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
    List<T9CodeClass> codeClassList = null;
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL from CODE_CLASS";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9CodeClass codeClass = null;
      codeClassList = new ArrayList<T9CodeClass>();
      
      while (rs.next()){
        codeClass = new T9CodeClass();
        codeClass.setSqlId(rs.getInt("SEQ_ID"));
        codeClass.setClassNo(rs.getString("CLASS_NO"));
        codeClass.setSortNo(rs.getString("SORT_NO"));
        codeClass.setClassDesc(rs.getString("CLASS_DESC"));
        codeClass.setClassLevel(rs.getString("CLASS_LEVEL"));
        codeClassList.add(codeClass);
      }
  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    
    request.setAttribute("codeClassList", codeClassList);
    return "/raw/ljf/tablemaintjsp/listcodeclass.jsp";
  }
  
   
  public String listCodeItems(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
    List<T9CodeItem> codeList = null;
    String classNo = request.getParameter("classNo");
    
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_CODE from CODE_ITEM where CLASS_NO = '" + classNo + "'";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9CodeItem codeItem = null;
      codeList = new ArrayList<T9CodeItem>();
      
      while(rs.next()){
        codeItem = new T9CodeItem();
        codeItem.setSqlId(rs.getInt("SEQ_ID"));
        codeItem.setClassNo(rs.getString("CLASS_NO"));
        codeItem.setSortNo(rs.getString("SORT_NO"));
        codeItem.setClassDesc(rs.getString("CLASS_DESC"));
        codeItem.setClassCode(rs.getString("CLASS_CODE"));
        codeList.add(codeItem);
      }
  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    request.setAttribute("codeList", codeList);
    return "/raw/ljf/tablemaintjsp/listcodeitem.jsp?classNo=" + classNo;
  }
  
  
  public String addCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    //classNo不能重复，对classNo进行判断
    String classNo = request.getParameter("classNo");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");
    String classLevel = request.getParameter("classLevel");

    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 

    PreparedStatement pstmt = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      String sql = "select count(*) from CODE_CLASS where CLASS_NO = '" + classNo + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if(rs.next()){      
        count = rs.getLong(1);
      }
      if(count == 1) {
        System.out.println("classNo重复, classNo不能重复");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "classNo重复, classNo不能重复");
        return "/core/inc/rtjson.jsp";
      }
      
      String queryStr = "insert into CODE_CLASS (CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(queryStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, sortNo);
      pstmt.setString(3, classDesc);
      pstmt.setString(4, classLevel);
      pstmt.executeUpdate();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "主分类添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt,null,log);
    }    
    return "/core/inc/rtjson.jsp";
  }
  
  public String addCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String classNo = request.getParameter("classNo");
    String classCode = request.getParameter("classCode");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");
    
    Connection dbConn = null;
    Statement stmt = null; 
    ResultSet rs = null; 
    PreparedStatement pstmt = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sql = "select count(*) from CODE_ITEM where CLASS_CODE = '" + classCode + "' and CLASS_NO = '" + classNo +"'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      long count = 0;
      if(rs.next()){      
        count = rs.getLong(1);
      }
      if(count == 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "classCode重复, classCode不能重复");
        return "/core/inc/rtjson.jsp";
      }
      
      String queryStr = "insert into CODE_ITEM(CLASS_NO, CLASS_CODE, SORT_NO, CLASS_DESC) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(queryStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classCode);
      pstmt.setString(3, sortNo);
      pstmt.setString(4, classDesc);
      pstmt.executeUpdate();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "codeitem添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(pstmt,null,log);
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String findCodeClassById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sqlId = request.getParameter("sqlId");
    T9CodeClass codeClass = null;
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
 
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL from CODE_CLASS where SEQ_ID= " + sqlId;
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
     
      if(rs.next()){
        codeClass = new T9CodeClass();
        codeClass.setSqlId(rs.getInt(1));
        codeClass.setClassNo(rs.getString(2));
        codeClass.setSortNo(rs.getString(3));
        codeClass.setClassDesc(rs.getString(4));
        codeClass.setClassLevel(rs.getString(5));
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }

    request.setAttribute("codeClass", codeClass);
    return "/raw/ljf/tablemaintjsp/updatecodeclass.jsp";
  }
  
  public String findCodeItemById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sqlId = request.getParameter("sqlId");
    T9CodeItem codeItem = null;
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
 
    String queryStr = "select * from CODE_ITEM where SEQ_ID= " + sqlId;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      
      if (rs.next()) {
        codeItem = new T9CodeItem();
        codeItem.setSqlId(rs.getInt("SEQ_ID"));
        codeItem.setClassNo(rs.getString("CLASS_NO"));
        codeItem.setClassCode(rs.getString("CLASS_CODE"));
        codeItem.setSortNo(rs.getString("SORT_NO"));
        codeItem.setClassDesc(rs.getString("CLASS_DESC"));
      }
  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    
   request.setAttribute("codeItem", codeItem);
    return "/raw/ljf/tablemaintjsp/updatecodeitem.jsp?classNo=" + codeItem.getClassNo();
  }
  
  
  public String updateCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String classNofirst = request.getParameter("classNofirst"); 
    String classNo = request.getParameter("classNo");
    String classDesc = request.getParameter("classDesc");
    String sortNo = request.getParameter("sortNo");
    String classLevel = request.getParameter("classLevel");
    
    Connection dbConn = null;
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null; 

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(classNo.equals(classNofirst)) {
        String updateStr = "update CODE_CLASS set SORT_NO = ?, CLASS_DESC = ? , CLASS_LEVEL = ? where CLASS_NO = ?";
        pstmt = dbConn.prepareStatement(updateStr);
        pstmt.setString(1, sortNo);
        pstmt.setString(2, classDesc);
        pstmt.setString(3, classLevel);
        pstmt.setString(4, classNo);
        pstmt.executeUpdate();
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "修改主表成功！");  
        return "/core/inc/rtjson.jsp";
      }
        String sql = "select count(*) from CODE_CLASS where CLASS_NO = '" + classNo + "'";
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        long count = 0;
        if(rs.next()){
          count = rs.getLong(1);
        }       
        if(count > 0) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "classNo重复, classNo不能重复");
          return "/core/inc/rtjson.jsp";
        }
      String sqlStr = "insert into CODE_CLASS (CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(sqlStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classDesc);
      pstmt.setString(3, sortNo);
      pstmt.setString(4, classLevel);
      pstmt.executeUpdate();
      
      String updateStr = "update CODE_ITEM set CLASS_NO = ? where CLASS_NO = ?";
      pstmt = dbConn.prepareStatement(updateStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classNofirst);
      pstmt.executeUpdate();
          
      //原来的classNo   
      String deleteStr = "delete from CODE_CLASS where CLASS_NO = '" + classNofirst + "'";
      stmt.executeUpdate(deleteStr);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改主子表成功！");  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(pstmt,null,log);
    }    
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String updateCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    
    String sqlId = request.getParameter("sqlId");  
    String classNo = request.getParameter("classNo");
    String classCode = request.getParameter("classCode");
    String classCodeOld = request.getParameter("classCodeOld");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        if(classCodeOld.equals(classCode)) {
          String updateStr = "update CODE_ITEM set CLASS_NO = ?, SORT_NO = ?, CLASS_DESC = ? where CLASS_CODE = ?";
          
          pstmt = dbConn.prepareStatement(updateStr);
          pstmt.setString(1, classNo);
          pstmt.setString(2, sortNo);
          pstmt.setString(3, classDesc);
          pstmt.setString(4, classCode);
          pstmt.executeUpdate();
          
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "修改codeItem成功！");  
          return "/core/inc/rtjson.jsp";
        }
       
        String sql = "select count(*) from CODE_ITEM where CLASS_CODE = '" + classCode + "' and CLASS_NO = '" + classNo +"'";
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        long count = 0;
        if(rs.next()){      
          count = rs.getLong(1);
        }
        if(count >= 1) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "classCode重复, classCode不能重复");
          return "/core/inc/rtjson.jsp";
        }
        
        String updateStr = "update CODE_ITEM set CLASS_NO = ?, CLASS_CODE = ?, SORT_NO = ?, CLASS_DESC = ? where SEQ_ID = ?";
        
        pstmt = dbConn.prepareStatement(updateStr);
        pstmt.setString(1, classNo);
        pstmt.setString(2, classCode);
        pstmt.setString(3, sortNo);
        pstmt.setString(4, classDesc);
       
        pstmt.setInt(5, Integer.parseInt(sqlId));
        pstmt.executeUpdate();
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "改classCode后修改codeItem成功！");  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(pstmt,null,log);
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String deleteCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    String sqlId = request.getParameter("sqlId");
    String classNo = request.getParameter("classNo");
    Connection dbConn = null;
    Statement stmt = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();

      String sql = "delete from CODE_ITEM where CLASS_NO = '" + classNo + "'";
      stmt.executeUpdate(sql);
  
      sql = "delete from CODE_CLASS where SEQ_ID = " + sqlId;
      stmt.executeUpdate(sql);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除classCode成功！");  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, null, log);
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sqlId = request.getParameter("sqlId");
    Connection dbConn = null;
    Statement stmt = null;
 
    String queryStr = "delete from CODE_ITEM where SEQ_ID= " + sqlId;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();

      stmt.executeUpdate(queryStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, null, log);
    } 
    return "/core/inc/rtjson.jsp";
  }
}
