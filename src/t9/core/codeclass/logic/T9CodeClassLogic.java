package t9.core.codeclass.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.codeclass.data.T9CodeClass;
import t9.core.codeclass.data.T9CodeItem;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9CodeClassLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public T9CodeClass selectCodeClassById(Connection dbConn,String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    T9CodeClass codeClass = null;
    try {
      String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL from CODE_CLASS where SEQ_ID= " + seqId;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
     
      if(rs.next()){
        codeClass = new  T9CodeClass();
        codeClass = new T9CodeClass();
        codeClass.setSqlId(rs.getInt(1));
        codeClass.setClassNo(rs.getString(2));
        codeClass.setSortNo(rs.getString(3));
        codeClass.setClassDesc(rs.getString(4));
        codeClass.setClassLevel(rs.getString(5));
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
     T9DBUtility.close(stmt, rs, log);
    }
    return codeClass;
  }
  /**
   * 得到所有的CODE_ITEM 根据CODE_CALSS的CLASS_NO
   * @param dbConn
   * @param classNo
   * @return
   * @throws Exception
   */
  public List<T9CodeItem> getCodeItem(Connection dbConn,String classNo) throws Exception {
    Statement stmt = null;
    ResultSet rs = null; 
    T9ORM orm = new T9ORM();
    List<T9CodeItem>   codeList = new ArrayList<T9CodeItem>();
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_CODE from CODE_ITEM where CLASS_NO = (select CLASS_NO from CODE_CLASS where CLASS_NO = '"+classNo+"') order by SORT_NO";
    T9CodeItem codeItem = null;
    try{
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
        codeItem = new T9CodeItem();
        codeItem.setSeqId(rs.getInt("SEQ_ID"));
        codeItem.setClassNo(rs.getString("CLASS_NO"));
        codeItem.setSortNo(rs.getString("SORT_NO"));
        codeItem.setClassDesc(rs.getString("CLASS_DESC"));
        codeItem.setClassCode(rs.getString("CLASS_CODE"));
        codeList.add(codeItem);
      } 
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
   
    return   codeList;
  }
}
