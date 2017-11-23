package t9.subsys.oa.book.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.book.data.T9BookManager;
/**
 * 设置管理员

 * @author qwx110
 *
 */
public class T9SetBookManagerLogic{
   
  /**
   * 增加新的管理员

   * @param aManager
   * @throws Exception 
   */
  public int  newManager(Connection dbConn, T9BookManager aManager) throws Exception{    
    PreparedStatement ps = null;    
    String sql = "insert into book_manager(MANAGER_ID, MANAGE_DEPT_ID) values(?,?)";     
    int k = 0;
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, aManager.getManagerId());
      ps.setString(2, aManager.getManageDeptId());
      k = ps.executeUpdate();
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
    return k;
  }
  
  /**
   * 删除管理员

   * @param aManager
   * @return
   * @throws Exception 
   */
  public int delManager(Connection dbConn, int seqId) throws Exception{
    PreparedStatement ps = null;
    String sql = "delete from book_manager where SEQ_ID =" + seqId; 
    int k = 0;
    try{
      ps = dbConn.prepareStatement(sql);
      k = ps.executeUpdate();
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }    
    return k;
  }
  
  /**
   * 查找所有的管理员

   * @return
   * @throws SQLException 
   */
  public List<T9BookManager> findAllManager(Connection dbConn) throws Exception{
    List<T9BookManager> managers = new ArrayList<T9BookManager>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select SEQ_ID, MANAGER_ID, MANAGE_DEPT_ID from book_manager order by SEQ_ID desc"; 
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        T9BookManager manager = new T9BookManager();
        manager.setSeqId(rs.getInt("SEQ_ID"));
        manager.setManagerId(rs.getString("MANAGER_ID"));
        manager.setManageDeptId(rs.getString("MANAGE_DEPT_ID"));
        manager.setManagerNames(findAllManagerName(dbConn, rs.getString("MANAGER_ID")));
        manager.setDeptNames(findDeptNames(dbConn, rs.getString("MANAGE_DEPT_ID")));
        managers.add(manager);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
    return managers;
  }
  
  /**
   * 返回部门名字
   * @param dbConn
   * @param deptIds
   * @return
   * @throws Exception
   */
  public String findDeptNames(Connection dbConn, String  deptIds) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    if(T9Utility.isNullorEmpty(deptIds)){
      deptIds = "-1";
    }
    String sql = "select DEPT_NAME from department where  seq_id in (" + deptIds +")"; 
   // T9Out.println(sql);
    String deptNames="";
    if("0".equalsIgnoreCase(deptIds) || "ALL_DEPT".equalsIgnoreCase(deptIds)){
      deptNames = "全体部门,";
    }else{
        try{
          ps = dbConn.prepareStatement(sql);
          rs = ps.executeQuery();
          
          while(rs.next()){
            deptNames += rs.getString("DEPT_NAME")+",";
          }
          
        } catch (SQLException e){
          throw e;
        }finally{
          T9DBUtility.close(ps, rs, null);
        }
    }
    if(!T9Utility.isNullorEmpty(deptNames)){
      deptNames = deptNames.substring(0, deptNames.lastIndexOf(","));  
    }
    return deptNames;    
  }
  
  /**
   * 获得所有的管理员列表

   * @param dbConn
   * @return
   * @throws Exception
   */
  public String findAllManagerName(Connection dbConn, String managerIds) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    if(T9Utility.isNullorEmpty(managerIds)){
      managerIds = "0";
    }
    //T9Out.println(managerIds);
    String sql = "select USER_NAME from person where seq_id in (" + managerIds +")"; 
   // T9Out.println(sql);
    String managerNames = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        managerNames += rs.getString("USER_NAME") + ",";
      }
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    if(!T9Utility.isNullorEmpty(managerNames)){
      managerNames = managerNames.substring(0, managerNames.lastIndexOf(","));
    }
    return managerNames;
  }
  
  /**
   * 编辑管理员(更新)
   * @param aManager
   * @throws Exception 
   */
  public T9BookManager editManager(Connection dbConn, int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "select SEQ_ID, MANAGER_ID, MANAGE_DEPT_ID from book_manager where SEQ_ID=" + seqId;
    T9BookManager aManager = new T9BookManager();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();      
      while(rs.next()){
        aManager.setSeqId(rs.getInt("SEQ_ID"));
        aManager.setManagerId(rs.getString("MANAGER_ID"));
        aManager.setManageDeptId(rs.getString("MANAGE_DEPT_ID"));
        aManager.setManagerNames(findAllManagerName(dbConn, rs.getString("MANAGER_ID")));
        aManager.setDeptNames(findDeptNames(dbConn, rs.getString("MANAGE_DEPT_ID")));
      }
    } catch (SQLException e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return aManager;
  }  
 
  /**
   * 更新管理员信息

   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int  updateManager(Connection dbConn, T9BookManager aManager) throws Exception{
    int k =0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "update book_manager set MANAGER_ID=? , MANAGE_DEPT_ID=? where SEQ_ID = ?";
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, aManager.getManagerId());
      ps.setString(2, aManager.getManageDeptId());
      ps.setInt(3, aManager.getSeqId());
      k = ps.executeUpdate();
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return k;
  }
}
