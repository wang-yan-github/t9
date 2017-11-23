package t9.core.codeclass.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.codeclass.data.T9CodeClass;
import t9.core.codeclass.data.T9CodeItem;
import t9.core.codeclass.logic.T9CodeClassLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
public class T9CodeClassAct {
  private static Logger log = Logger.getLogger(".raw.ljf.T9CodeClassAct");
  
  public String listCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {   
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
    List<T9CodeClass> codeClassList = null;
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL from CODE_CLASS";
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
    return "/core/codeclass/codeclasslist.jsp";
  }
  
  public String listCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
    List<T9CodeItem> codeList = null;
    String classNo = "";
    String seqId  = request.getParameter("seqId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(seqId!=null&&!seqId.equals("")){
        T9CodeClassLogic tccl = new T9CodeClassLogic();
        T9CodeClass codeClass = tccl.selectCodeClassById(dbConn, seqId);
        classNo = codeClass.getClassNo();
        String classDesc = codeClass.getClassDesc();
        request.setAttribute("classDesc", classDesc);
        classNo = classNo.replace("'", "''");
        String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_CODE from CODE_ITEM where CLASS_NO = '" + classNo + "'";
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(queryStr);
        T9CodeItem codeItem = null;
        codeList = new ArrayList<T9CodeItem>();
        
        while(rs.next()){
          codeItem = new T9CodeItem();
          codeItem.setSeqId(rs.getInt("SEQ_ID"));
          codeItem.setClassNo(rs.getString("CLASS_NO"));
          codeItem.setSortNo(rs.getString("SORT_NO"));
          codeItem.setClassDesc(rs.getString("CLASS_DESC"));
          codeItem.setClassCode(rs.getString("CLASS_CODE"));
          codeList.add(codeItem);
        } 
        
       
      }

    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    request.setAttribute("codeList", codeList);
    return "/core/codeclass/codeitemlist.jsp?classNo=" + classNo+"&seqId="+seqId;
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
      if(sqlId!=null&&!sqlId.equals("")){
        T9CodeClassLogic tccl = new T9CodeClassLogic();
        T9CodeClass codeClass = tccl.selectCodeClassById(dbConn, sqlId);
        if(codeClass!=null){
          classNo = codeClass.getClassNo().replace("'", "''");
          String sql = "delete from CODE_ITEM where CLASS_NO = '" + classNo + "'";
          stmt.executeUpdate(sql);
      
          sql = "delete from CODE_CLASS where SEQ_ID = " + sqlId;
          stmt.executeUpdate(sql);
        }
      }
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除主分类成功！");  
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
 
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String queryStr = "delete from CODE_ITEM where SEQ_ID= " + sqlId;
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
  
  public String getCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      
      String sqlId = request.getParameter("sqlId");
      T9CodeClass codeClass = null;
      
      Connection dbConn = null;
      Statement stmt = null;
      ResultSet rs = null; 
      
      try {
        String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO,CLASS_DESC, CLASS_LEVEL from CODE_CLASS where SEQ_ID= " + sqlId;
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
        String data = "{sqlId:" + codeClass.getSqlId() + ", classNo:\"" + T9Utility.encodeSpecial(codeClass.getClassNo()) + "\", sortNo:\""+  T9Utility.encodeSpecial(codeClass.getSortNo()) +"\", classDesc:\"" + T9Utility.encodeSpecial(codeClass.getClassDesc())+ "\", classLevel:\"" +T9Utility.encodeSpecial(codeClass.getClassLevel()) +"\"}";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功获取主分类的数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }finally {
        T9DBUtility.close(stmt, rs, log);
      }
      return "/core/inc/rtjson.jsp";
    }

  public String getCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      
      String sqlId = request.getParameter("sqlId");
      T9CodeItem codeItem = null;
      
      Connection dbConn = null;
      Statement stmt = null;
      ResultSet rs = null; 
      
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        
        String queryStr = "select SEQ_ID, CLASS_NO, CLASS_CODE, SORT_NO, CLASS_DESC from CODE_ITEM where SEQ_ID= " + sqlId;
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(queryStr);
       
        if (rs.next()) {
          codeItem = new T9CodeItem();
          codeItem.setSeqId(rs.getInt("SEQ_ID"));
          codeItem.setClassNo(rs.getString("CLASS_NO"));
          codeItem.setClassCode(rs.getString("CLASS_CODE"));
          codeItem.setSortNo(rs.getString("SORT_NO"));
          codeItem.setClassDesc(rs.getString("CLASS_DESC"));
        }
        String data = "{sqlId:" + codeItem.getSeqId() + ", classNo:\"" + T9Utility.encodeSpecial(codeItem.getClassNo()) + "\", classCode:\""+ T9Utility.encodeSpecial(codeItem.getClassCode()) + "\", sortNo:\""+ codeItem.getSortNo() +"\", classDesc:\"" +T9Utility.encodeSpecial(codeItem.getClassDesc()) +"\"}";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功获取代码项的数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }finally {
        T9DBUtility.close(stmt, rs, log);
      }
      return "/core/inc/rtjson.jsp";
  }
  
  public String addCodeClass(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String classNo = request.getParameter("classNo");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");
    String classLevel = request.getParameter("classLevel");

    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 

    if(classNo == null || "".equals(classNo)) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(sortNo == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(classDesc == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    PreparedStatement pstmt = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      stmt = dbConn.createStatement();
      classNo = classNo.replace("'", "''");
      String sql = "select count(*) from CODE_CLASS where CLASS_NO = '" + classNo + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if(rs.next()){      
        count = rs.getLong(1);
      }
      if(count == 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "编码重复, 编码不能重复");
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
    
    if(classCode == null) {
      return "/core/inc/rtjson.jsp";
    }
    if(sortNo == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(classDesc == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //classCode = classCode.replace("'", "''");
      //classNo = classNo.replace("'", "''");
      String sql = "select count(*) from CODE_ITEM where CLASS_CODE = '" +  classCode.replace("'", "''") + "' and CLASS_NO = '" + classNo.replace("'", "''") +"'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      long count = 0;
      if(rs.next()){      
        count = rs.getLong(1);
      }
      if(count == 1) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "代码编号重复, 代码编号不能重复");
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
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码项添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(pstmt,null,log);
    }
    return "/core/inc/rtjson.jsp";
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

    if(classNo == null || "".equals(classNo)) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(sortNo == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(classDesc == null) {
      return "/core/inc/rtjson.jsp";
    }
    
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
        request.setAttribute(T9ActionKeys.RET_MSRG, "修改主分类成功！");  
        return "/core/inc/rtjson.jsp";
      }
      String sql = "select count(*) from CODE_CLASS where CLASS_NO = '" + classNo.replace("'", "''") + "'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      long count = 0;
      if(rs.next()){
        count = rs.getLong(1);
      }       
      if(count > 0) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "编码重复, 编码不能重复");
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
      classNofirst = classNofirst.replace("'", "'"); 
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


    if(classCode == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(sortNo == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    if(classDesc == null) {
      return "/core/inc/rtjson.jsp";
    }
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      if(T9Utility.isInteger(sqlId)){
        if(classCode.equals(classCodeOld)) {
          String updateStr = "update CODE_ITEM set CLASS_NO = ?, SORT_NO = ?, CLASS_DESC = ? where SEQ_ID = ?";
          pstmt = dbConn.prepareStatement(updateStr);
          pstmt.setString(1, classNo);
          pstmt.setString(2, sortNo);
          pstmt.setString(3, classDesc);
          pstmt.setInt(4, Integer.parseInt(sqlId));
          pstmt.executeUpdate();
          
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "修改代码项成功！");  
          return "/core/inc/rtjson.jsp";
        }
         
        String sql = "select count(*) from CODE_ITEM where CLASS_CODE = '" + classCode.replace("'", "''") + "' and CLASS_NO = '" + classNo.replace("'", "''") +"'";
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        long count = 0;
        if(rs.next()){      
          count = rs.getLong(1);
        }
        if(count == 1) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "代码编号重复, 代码编号不能重复");
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
        
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "改代码编号后修改代码项成功！");  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(pstmt,null,log);
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 

   * 获取代码   by No
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCodeItemByNo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String parentNo = request.getParameter("parentNo") == null ? "" :  request.getParameter("parentNo");
      T9CodeClassLogic codeLogic = new T9CodeClassLogic();
      String data = "[";
      List<T9CodeItem> itemList = new ArrayList<T9CodeItem>();
      itemList = codeLogic.getCodeItem(dbConn, parentNo);
      for (int i = 0; i < itemList.size(); i++) {
        T9CodeItem item = itemList.get(i);
        data = data + T9FOM.toJson(item) + ",";
      }
      if (itemList.size() > 0) {
        data = data.substring(0, data.length() - 1);
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
