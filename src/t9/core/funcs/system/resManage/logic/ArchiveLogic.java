package t9.core.funcs.system.resManage.logic;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class ArchiveLogic {
  public boolean hasArchive(Connection dbConn, String query) throws Exception {
    PreparedStatement stm1 = null;
    ResultSet rs1 = null;
    try {
      stm1 = dbConn.prepareStatement(query);
      rs1 = stm1.executeQuery();
      if (rs1.next()) {
        return true;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null);
    }
    return false;
  }
  public boolean exeSql(Connection dbConn, String query) throws Exception {
    PreparedStatement stm1 = null;
    try {
      stm1 = dbConn.prepareStatement(query);
      stm1.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, null, null);
    }
    return true;
  }
  public String archiveTable(Connection dbConn , String tableSrc , String whereStr , boolean delSrcData,  String tableDest) throws Exception {
    if (T9Utility.isNullorEmpty(tableSrc)) {
      return "请指定要归档的表名";
    }
    if (T9Utility.isNullorEmpty(tableDest)) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
      tableDest = tableSrc + "_" + sdf.format(new Date());
    } 
    /*
    String queryTable = "show tables from t9 like '"+ tableDest +"'";
    if (this.hasArchive(dbConn, queryTable)) {
      return "数据库[t9]中已存在名称为["+ tableDest +"]的表";
    }*/
    String createTable  = "create table "+ tableDest +" like  "+tableSrc;
    try{
      this.exeSql(dbConn, createTable);
    }catch(Exception ex) {
      return "数据库表“"+tableDest+"”失败";
    }
    String query = "insert into " + tableDest +" select * from " + tableSrc;
    if (T9Utility.isNullorEmpty(whereStr)) {
      query += " where " + whereStr;
    }
    try{
      this.exeSql(dbConn, query);
    }catch(Exception ex) {
      String drop = "drop table if exists " + tableDest;
      this.exeSql(dbConn, drop);
      return "复制表数据错误";
    }
    if (delSrcData) {
      String query1 = "delete from "+tableSrc;
      if (!T9Utility.isNullorEmpty(whereStr)) {
        query1 +=  " where " + whereStr;
      } 
      this.exeSql(dbConn, query1);
    }
    return "";
  }
  public String archive(Connection dbConn, Date date, String[] tableNames) throws Exception {
    // TODO Auto-generated method stub
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
    for (String name : tableNames) {
      String timeStr = sdf.format(date);
      String newName = name + "_" + timeStr;
         
      /*
      String queryTable = "show tables from t9 like '"+ newName +"'";
      if (this.hasArchive(dbConn, queryTable)) {
        return "["+ newName +"]同一天不能归档两次";
      }
      */
      if ("EMAIL_BODY".equals(name)) {
        String emailBodyNew = "EMAIL_BODY_" + timeStr;
         String emailNew = "EMAIL_" + timeStr;
         
         String whereEmailBody = " " +T9DBUtility.getDateFilter("SEND_TIME", sdf2.format(date), "<=");
        String wehereEmail = " BODY_ID IN (SELECT EMAIL_BODY.SEQ_ID FROM EMAIL_BODY WHERE " + whereEmailBody + ")";
        
        this.archiveTable(dbConn, "EMAIL", wehereEmail, true, emailNew);
        this.archiveTable(dbConn, "EMAIL_BODY", whereEmailBody, true, emailBodyNew);
        
        //归档邮件内容表
        String archiveTables ="INSERT INTO ARCHIVE_TABLES (MODULE, ARCHIVE_DESC, ARCHIVE_DB, TABLE_POSTFIX, DATA_VERSION, ARCHIVE_TIME, TABLE_NAME_STR) VALUES "
            +"('email', '"+ timeStr +"', 't9', '"+ timeStr +"', '', '0', 'EMAIL,EMAIL_BODY')";
        this.exeSql(dbConn, archiveTables);
      } else if ("DIARY_COMMENT_REPLY".equals(name)) {
        String diaryNew = "DIARY_" + timeStr;
        String diaryCommentNew = "DIARY_COMMENT_" + timeStr;
        String diaryCommentReplyNew = "DIARY_COMMENT_REPLY_" + timeStr;
        
        String diaryWhere = " " +T9DBUtility.getDateFilter("DIA_TIME", sdf2.format(date), "<="); 
        String diaryCommentWhere = " DIA_ID in (SELECT DIA_ID FROM DIARY WHERE " + diaryWhere + " ) ";
        String diaryCommentReplyWhere = " COMMENT_ID in (SELECT COMMENT_ID FROM DIARY_COMMENT WHERE DIA_ID in (SELECT DIA_ID FROM DIARY WHERE DIA_TIME<= " + diaryWhere + ")) ";
        
        this.archiveTable(dbConn, "DIARY_COMMENT_REPLY", diaryCommentReplyWhere, true, diaryCommentReplyNew);
        this.archiveTable(dbConn, "DIARY_COMMENT", diaryCommentWhere, true, diaryCommentNew);
        this.archiveTable(dbConn, "DIARY", diaryWhere, true, diaryNew);
        
        String archiveTables ="INSERT INTO ARCHIVE_TABLES (MODULE, ARCHIVE_DESC, ARCHIVE_DB, TABLE_POSTFIX, DATA_VERSION, ARCHIVE_TIME, TABLE_NAME_STR) VALUES "
            +"('diary', '"+ timeStr +"', 't9', '"+ timeStr +"', '', '0', 'DIARY,DIARY_COMMENT,DIARY_COMMENT_REPLY')";
        this.exeSql(dbConn, archiveTables);
      }else if ("MESSAGE".equals(name)){
         String  where = " " +T9DBUtility.getDateFilter("SEND_TIME", sdf2.format(date), "<="); 
         String  messageWhere = " BODY_SEQ_ID IN (SELECT MESSAGE_BODY.SEQ_ID FROM MESSAGE_BODY WHERE " + where + ")";
           String module ="message";
           String tableName = "MESSAGE,MESSAGE_BODY";
           this.archiveTable(dbConn, "MESSAGE", messageWhere, true, "MESSAGE_" + timeStr);
           this.archiveTable(dbConn, "MESSAGE_BODY", where, true, "MESSAGE_BODY_" + timeStr);
           String archiveTables ="INSERT INTO ARCHIVE_TABLES (MODULE, ARCHIVE_DESC, ARCHIVE_DB, TABLE_POSTFIX, DATA_VERSION, ARCHIVE_TIME, TABLE_NAME_STR) VALUES "
               +"('"+module +"', '"+ timeStr +"', 't9', '"+ timeStr +"', '', '0', '"+ tableName +"')";
           this.exeSql(dbConn, archiveTables);
        } else if  ("SYS_LOG".equals(name)) {
          String where = " "  +T9DBUtility.getDateFilter("TIME", sdf2.format(date), "<=");
          String module ="sys_log";
          String tableName = "SYS_LOG";
          this.archiveTable(dbConn, name, where, true, newName);
          String archiveTables ="INSERT INTO ARCHIVE_TABLES (MODULE, ARCHIVE_DESC, ARCHIVE_DB, TABLE_POSTFIX, DATA_VERSION, ARCHIVE_TIME, TABLE_NAME_STR) VALUES "
              +"('"+module +"', '"+ timeStr +"', 't9', '"+ timeStr +"', '', '0', '"+ tableName +"')";
          this.exeSql(dbConn, archiveTables);
      }
    }
    return "归档成功";
  }
  public String getArchive(Connection dbConn) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer("[");
    sb.append(this.getArchive(dbConn, "email")).append(",");
    sb.append(this.getArchive(dbConn, "diary")).append(",");
    sb.append(this.getArchive(dbConn, "message")).append(",");
    sb.append(this.getArchive(dbConn, "sys_log")).append("]");
    return sb.toString();
  }
  public String getArchive(Connection dbConn , String module ) throws Exception {
    String query = "select ARCHIVE_DESC from ARCHIVE_TABLES where module = '"+module+"'" ;
    PreparedStatement stm1 = null;
    ResultSet rs1 = null;
    StringBuffer sb = new StringBuffer("{module:'"+ module.toUpperCase() + "',data:[");
    int count = 0;
    try {
      stm1 = dbConn.prepareStatement(query);
      rs1 = stm1.executeQuery();
      while (rs1.next()) {
        String time = T9Utility.null2Empty(rs1.getString("ARCHIVE_DESC"));
        sb.append("\"").append(time).append("\",");
        count++;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null);
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]}");
   return sb.toString();
  }
  public String dropArchive(Connection dbConn, String[] delete) throws Exception {
    // TODO Auto-generated method stub
    for (String del : delete) {
      if (T9Utility.isNullorEmpty(del)) {
        continue;
      }
      if (del.startsWith("EMAIL")) {
        String time = del.replace("EMAIL_", "");
        String dropTable = "drop table EMAIL_" + time;
        try{
          this.exeSql(dbConn, dropTable);
        }catch(Exception ex){}
        String dropTable2 = "drop table EMAIL_BODY_" + time;
        try{
          this.exeSql(dbConn, dropTable2);
        }catch(Exception ex){}
        String deleteStr =  "delete from ARCHIVE_TABLES where MODULE = 'email' and ARCHIVE_DESC='" + time + "'";
        this.exeSql(dbConn, deleteStr);
      } else if (del.startsWith("DIARY")) {
        String time = del.replace("DIARY_", "");
        String dropTable = "drop table DIARY_" + time;
        try{
          this.exeSql(dbConn, dropTable);
        }catch(Exception ex){}
        String dropTable2 = "drop table DIARY_COMMENT_" + time;
        try{
          this.exeSql(dbConn, dropTable2);
        }catch(Exception ex){}
        String dropTable3 = "drop table DIARY_COMMENT_REPLY_" + time;
        try{
          this.exeSql(dbConn, dropTable3);
        }catch(Exception ex){}
        String deleteStr =  "delete from ARCHIVE_TABLES where MODULE = 'diary' and ARCHIVE_DESC='" + time + "'";
        this.exeSql(dbConn, deleteStr);
      } else if (del.startsWith("MESSAGE")) {
        String time = del.replace("MESSAGE_", "");
        String dropTable = "drop table MESSAGE_" + time;
        try{
          this.exeSql(dbConn, dropTable);
        }catch(Exception ex){}
        String dropTable2 = "drop table MESSAGE_BODY_" + time;
        try{
          this.exeSql(dbConn, dropTable2);
        }catch(Exception ex){}
        String deleteStr =  "delete from ARCHIVE_TABLES where MODULE = 'message' and ARCHIVE_DESC='" + time + "'";
        this.exeSql(dbConn, deleteStr);
      }else if (del.startsWith("SYS_LOG")) {
        String time = del.replace("SYS_LOG_", "");
        String dropTable = "drop table SYS_LOG_" + time;
        try{
          this.exeSql(dbConn, dropTable);
        }catch(Exception ex){}
        String deleteStr =  "delete from ARCHIVE_TABLES where MODULE = 'sys_log' and ARCHIVE_DESC='" + time + "'";
        this.exeSql(dbConn, deleteStr);
      }
    }
    return null;
  }
}
