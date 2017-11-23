package t9.core.funcs.filefolder.logic;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import sun.misc.BASE64Encoder;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;

public class T9FileFolderLogic {
  public boolean checkUser( String userId , String pwd , String pwds) {
    T9PassEncrypt pe = new T9PassEncrypt();
    if (!T9Utility.isNullorEmpty(pwds) && pe.isValidPas(pwd, pwds)) {
      return true;
    }
    return false;
  }
  public String userPwd (Connection conn , String userId) throws Exception {
    Statement stm1 = null;
    ResultSet rs1 = null;
    String sql = "select PASSWORD from PERSON WHERE USER_ID = '" + userId + "'";
    String pwd = "";
    try {
      stm1 = conn.createStatement();
      rs1 = stm1.executeQuery(sql);
      if (rs1.next()) {
        pwd = rs1.getString("PASSWORD");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null);
    }
    return pwd;
  }
  public String getFile(Connection conn , String fileId ) throws Exception {
    Statement stm2 = null;
    ResultSet rs2 = null;
    StringBuffer sb = new StringBuffer();
    String sql2 = "select * from file_content WHERE SEQ_ID = '" + fileId + "'";
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(sql2);
      if (rs2.next()) {
        int seqId = rs2.getInt("SEQ_ID");
        String subject = rs2.getString("SUBJECT");
        String content = rs2.getString("CONTENT");
        int fileSort =rs2.getInt("SORT_ID");
        sb.append("{");
        sb.append("\"fileId\":" + seqId);
        sb.append(",\"fileName\":\"" + T9Utility.encodeSpecial(subject) + "\"");
        sb.append(",\"fileParent\":" + fileSort);
        sb.append(",\"content\":\"" + T9Utility.encodeSpecial(content) + "\"");
        sb.append(",\"attachmentId\":\"" + rs2.getString("ATTACHMENT_ID") + "\"");
        sb.append(",\"attachmentName\":\"" + T9Utility.encodeSpecial(rs2.getString("ATTACHMENT_NAME")) + "\"");
        sb.append(",\"isFolder\":false");
        sb.append(",\"attachments\":\"" + this.getAttach(rs2.getString("ATTACHMENT_ID"), rs2.getString("ATTACHMENT_NAME")) + "\"");
        sb.append("}");
      } else {
        sb.append("{}");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null);
    }
    return sb.toString();
  }
  public String getAttach(String attid , String attName) throws Exception {
    T9WorkFlowUtility util = new T9WorkFlowUtility();
    String[] attIdArry = attid.split(",");
    String[] attNameArry = attName.split("\\*");
    
    int count=0;
    StringBuffer file = new StringBuffer();
    if (attIdArry != null && attIdArry.length > 0) {
      for (int i = 0; i < attIdArry.length; i++) {
        String attIdTemp = attIdArry[i];
        String attNameTemp = attNameArry[i];
        String path = util.getAttachPath(attIdTemp, attNameTemp, "file_folder");
        file.append(this.encodeBase64File(path)).append(",");
        count++;
      }
    }
    if (count > 0 ) {
      file.deleteCharAt(file.length() - 1);
    }
    return file.toString();
  }

public static String encodeBase64File(String path) throws Exception {
  File  file = new File(path);
  FileInputStream inputFile = new FileInputStream(path);
  byte[] buffer = new byte[(int)file.length()];
  inputFile.read(buffer);
  inputFile.close();
  return new BASE64Encoder().encode(buffer);
}
  public String getFileSort(Connection conn , String parentSort) throws Exception {
    Statement stm1 = null;
    ResultSet rs1 = null;
    String sql = "select * from file_sort WHERE SORT_TYPE is null AND  SORT_PARENT = '" + parentSort + "'";
    StringBuffer  sb =  new StringBuffer();
    sb.append("[");
    int count = 0 ;
    try {
      stm1 = conn.createStatement();
      rs1 = stm1.executeQuery(sql);
      while (rs1.next()) {
        int seqId = rs1.getInt("SEQ_ID");
        String sortName = rs1.getString("SORT_NAME");
        int sortParent = rs1.getInt("SORT_PARENT");
        String sortNo = T9Utility.null2Empty(rs1.getString("SORT_NO"));
        sb.append("{");
        sb.append("\"fileId\":" + seqId);
        sb.append(",\"fileName\":\"" + T9Utility.encodeSpecial(sortName) + "\"");
        sb.append(",\"fileParent\":" + sortParent);
        sb.append(",\"fileNo\":\"" + sortNo + "\"");
        sb.append(",\"isFolder\":true");
        sb.append("},");
        count++;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null);
    }
    
    Statement stm2 = null;
    ResultSet rs2 = null;
    String sql2 = "select * from file_content WHERE SORT_ID = '" + parentSort + "'";
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(sql2);
      while (rs2.next()) {
        int seqId = rs2.getInt("SEQ_ID");
        String subject = rs2.getString("SUBJECT");
        String content = rs2.getString("CONTENT");
        sb.append("{");
        sb.append("\"fileId\":" + seqId);
        sb.append(",\"fileName\":\"" + T9Utility.encodeSpecial(subject) + "\"");
        sb.append(",\"fileParent\":" + parentSort);
        sb.append(",\"content\":\"" + T9Utility.encodeSpecial(content) + "\"");
        sb.append(",\"attachmentId\":\"" + rs2.getString("ATTACHMENT_ID") + "\"");
        sb.append(",\"attachmentName\":\"" + T9Utility.encodeSpecial(rs2.getString("ATTACHMENT_NAME")) + "\"");
        sb.append(",\"isFolder\":false");
        sb.append("},");
        count++;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null);
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
}
