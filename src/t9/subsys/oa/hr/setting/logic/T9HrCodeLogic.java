package t9.subsys.oa.hr.setting.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.hr.setting.data.T9HrCode;

public class T9HrCodeLogic {
  private static Logger log = Logger
      .getLogger("t9.subsys.oa.hr.setting.logic.T9HrCodeLogic");

  /**
   * 新建
   * 
   * @param dbConn
   * @param code
   * @return
   * @throws Exception
   */
  public static int addCode(Connection dbConn, T9HrCode code) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, code);
    return 0;
  }

  /**
   * 查询ById
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static T9HrCode getCodeById(Connection dbConn, String seqId)
      throws Exception {
    try {
      T9ORM orm = new T9ORM();
      T9HrCode code = (T9HrCode) orm.loadObjSingle(dbConn, T9HrCode.class,
          Integer.parseInt(seqId));
      return code;
    } catch (NumberFormatException e) {
      return null;
    } catch (Exception e) {
      throw e;
    } finally {
      
    }
  }

  /**
   * 查询父级
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static List<T9HrCode> getCode(Connection dbConn, String[] str)
      throws Exception {
    T9ORM orm = new T9ORM();
    List<T9HrCode> codeList = new ArrayList<T9HrCode>();
    codeList = orm.loadListSingle(dbConn, T9HrCode.class, str);
    return codeList;
  }

  /**
   * 查询下一级
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public static List<T9HrCode> getChildCode(Connection dbConn, String parentNo)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    List<T9HrCode> codeList = new ArrayList<T9HrCode>();
    if (T9Utility.isNullorEmpty(parentNo)) {
      parentNo = "";
    }
    parentNo = parentNo.replaceAll("'", "''");
    String sql = "SELECT * from HR_CODE where parent_no in (select code_no from hr_code where code_no = '"+ parentNo + "' and  (parent_no is null or parent_no =''))";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        T9HrCode code = new T9HrCode();
        code.setSeqId(rs.getInt("SEQ_ID"));
        code.setCodeNo(rs.getString("CODE_NO"));
        code.setCodeName(rs.getString("CODE_NAME"));
        code.setCodeOrder(rs.getString("code_order"));
        code.setCodeFlag(rs.getString("code_flag"));
        codeList.add(code);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return codeList;
  }

  /**
   * 删除ById
   * 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delCodeById(Connection dbConn, String seqId)
      throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9HrCode.class, Integer.parseInt(seqId));
  }

  /**
   * 检查代码编号有没有重复--父级
   * 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static boolean checkCodeNo(Connection dbConn, String codeNo,
      String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    if (T9Utility.isNullorEmpty(codeNo)) {
      codeNo = "";
    }
    codeNo = codeNo.replaceAll("'", "''");
    String sql = "SELECT * from HR_CODE where CODE_NO='" + codeNo
        + "' and (PARENT_NO='' or PARENT_NO is null)";
    if (T9Utility.isInteger(seqId)) {
      sql = sql + " and SEQ_ID <> " + seqId;
    }
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return true;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return false;
  }

  /**
   * 检查代码编号有没有重复--子级
   * 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static boolean checkCodeNo(Connection dbConn, String codeNo,
      String parentNo, String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    if (T9Utility.isNullorEmpty(codeNo)) {
      codeNo = "";
    }
    codeNo = codeNo.replaceAll("'", "''");
    if (T9Utility.isNullorEmpty(parentNo)) {
      parentNo = "";
    }
    parentNo = parentNo.replaceAll("'", "''");
    String sql = "SELECT * from HR_CODE where CODE_NO='" + codeNo
        + "' and PARENT_NO='" + parentNo + "'";
    if (T9Utility.isInteger(seqId)) {
      sql = sql + " and SEQ_ID <> " + seqId;
    }
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return true;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return false;
  }

  /**
   * 更新
   * 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateCode(Connection dbConn, String seqId, String codeNo,
      String codeName, String codeOrder, String codeFlag) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean b = false;
    String sql = "update HR_CODE set CODE_NO = ?, CODE_NAME = ?,CODE_ORDER=?,CODE_FLAG = ? where SEQ_ID = "
        + seqId;
    try {
      pstmt = dbConn.prepareStatement(sql);
      pstmt.setString(1, codeNo);
      pstmt.setString(2, codeName);
      pstmt.setString(3, codeOrder);
      pstmt.setString(4, codeFlag);
      pstmt.executeUpdate();

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(pstmt, rs, log);
    }
  }
  
  /**
   * 更新
   * 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateChildCode(Connection dbConn, String oldCodeNo,String codeNo) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean b = false;
    String sql = "update HR_CODE set PARENT_NO = ? where PARENT_NO = ? ";
    try {
      pstmt = dbConn.prepareStatement(sql);
      pstmt.setString(1, codeNo);
      pstmt.setString(2, oldCodeNo);
      pstmt.executeUpdate();

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(pstmt, rs, log);
    }
  }
}
