package t9.core.funcs.doc.receive.logic;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.data.T9ExtDept;
import t9.core.esb.client.logic.T9DeptTreeLogic;
import t9.core.funcs.doc.receive.data.T9DocReceive;
import t9.core.funcs.doc.util.T9DocUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9DocReceiveRegLogic{
  /**
   * 取得登记记录
   * @param conn
   * @param request
   * @param user
   * @param type
   * @return
   * @throws Exception
   */
  public StringBuffer getRegList( Connection conn, Map request,T9Person user , String type , String webroot)
  throws Exception {
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      sql = " select " 
        + " SEQ_ID " 
        + " , TITLE " 
        + " , FROMUNITS " 
        + " , OPPDOC_NO " 
        + " , RES_DATE " 
        + " , CREATE_USER_ID " 
        + " , REC_DOC_NAME, REC_DOC_ID " 
        + " , SEND_STATUS " 
        + " , SEND_RUN_ID "
        + ", sponsor"
        + " from doc_receive " 
        + " WHERE  1=1 ";
      if (!T9Utility.isNullorEmpty(type)) {
        sql += " AND SEND_STATUS = '" + type +"' ";
      }
      
      T9DocUtility docUtility = new T9DocUtility();
      if (!docUtility.haveAllRight(user, conn)) {
        String deptIds = T9WorkFlowUtility.getOutOfTail(docUtility.deptRight(user.getSeqId(), conn));
        if (docUtility.usingEsb() && docUtility.haveEsbRecRight(user, conn)) {
          T9EsbClientConfig config = T9EsbClientConfig.builder(webroot + T9EsbConst.CONFIG_PATH) ;
          T9DeptTreeLogic logic = new T9DeptTreeLogic();
          T9ExtDept dept = logic.getDeptByEsbUser(conn, config.getUserId());
          if (!T9Utility.isNullorEmpty(deptIds)) {
            deptIds += ",'" ;
          }
          deptIds +=  dept.getDeptId() + "'";
        }
        if (T9Utility.isNullorEmpty(deptIds)) {
          sql += " AND 1<>1 ";
        } else {
          sql += " AND SPONSOR IN (" + deptIds + ") ";
        }
      }
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        String sponsor = (String) record.getValueByName("fromDept");
        if (!T9Utility.isNullorEmpty(sponsor)) {
          String deptName = this.getDeptName(conn, sponsor);
          if (!T9Utility.isNullorEmpty(deptName)) {
            record.updateField("fromUnits", deptName);
          }
        }
      }
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      throw ex;
    }
    return resualt;
  }
  public String getDeptName(Connection conn , String deptId) throws Exception {
    String deptName = "";
    String query = "select DEPT_NAME FROM department where seq_id = '" + deptId + "'";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        deptName = rs.getString("DEPT_NAME");
        return deptName;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    query = "select DEPT_NAME FROM EXT_DEPT where DEPT_ID = '" + deptId + "'";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        deptName = rs.getString("DEPT_NAME");
        return deptName;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return deptName;
  }
  /**
   * 取得收文记录
   * @param conn
   * @param request
   * @param user
   * @param type
   * @return
   * @throws Exception
   */
  public String getRecReg( Connection conn, int seqId)
  throws Exception {
    String r = "";
    String sql = " select " 
      + " SEQ_ID " 
       + " , DOC_NO"
       + ", DOC_TYPE"
      + " , FROMUNITS " 
      + " , OPPDOC_NO " 
      + " , TITLE " 
      + " , ATTACHNAME, ATTACHID " 
      + " , SPONSOR"
      + ", REC_DOC_ID"
      + ", REC_DOC_NAME"
      + " from doc_receive " 
      + " WHERE  SEQ_ID =  " + seqId;
    Statement stm = null; 
    ResultSet rs = null; 
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(sql); 
      if (rs.next()){ 
        String docNo = rs.getString("DOC_NO");
        String DOC_TYPE = rs.getString("DOC_TYPE");
        String fromUnits = rs.getString("FROMUNITS");
        String attachIds = rs.getString("ATTACHID");
        String attachNames = rs.getString("ATTACHNAME");
        String sponsor = rs.getString("SPONSOR");
        String title = rs.getString("TITLE");
        String oppDocNo = rs.getString("OPPDOC_NO");
        String recDocId = rs.getString("REC_DOC_ID");
        String recDocName = rs.getString("REC_DOC_NAME");
        
        r = "{";
        r += "docType:\"" + T9Utility.null2Empty(DOC_TYPE) + "\"";
        r += ",docNo:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(docNo)) + "\"";
        r += ",fromUnits:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(fromUnits)) + "\"";
        r += ",oppDocNo:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(oppDocNo)) + "\"";
        r += ",title:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(title)) + "\"";
        r += ",sponsor:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(sponsor)) + "\"";
        r += ",attachNames:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(attachNames)) + "\"";
        r += ",attachIds:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(attachIds)) + "\"";
        r += ",recDocId:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(recDocId)) + "\"";
        r += ",recDocName:\"" +  T9Utility.encodeSpecial(T9Utility.null2Empty(recDocName)) + "\"";
        r += "}";
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return r;
  }
  /**
   * 更新收文
   * @param conn
   * @param doc
   * @throws Exception
   */
  public void updateDocReceive(Connection conn, T9DocReceive doc) throws Exception{
    String sql = " update doc_receive set DOC_NO = ? " 
      + " , FROMUNITS  = ? " 
      + " , OPPDOC_NO = ? " 
      + " , TITLE = ? " 
      + " , COPIES = ? " 
      + " , CONF_LEVEL = ? " 
      + " , INSTRUCT = ? " 
      + " , SPONSOR = ? " 
      + " , RECE_USER_ID = ? " 
      + " , DOC_TYPE = ? " 
      + " , STATUS = ? " 
      + " , SEND_STATUS = ? " 
      + " ,ATTACHNAME = ?,ATTACHID = ? where SEQ_ID = " + doc.getSeq_id();
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, doc.getDocNo());
      ps.setString(2, doc.getFromUnits());
      ps.setString(3, doc.getOppdocNo());
      ps.setString(4, doc.getTitle());
      ps.setInt(5, doc.getCopies());
      ps.setInt(6, doc.getConfLevel());
      ps.setString(7, doc.getInstruct());
      ps.setString(8, doc.getSponsor());
      ps.setInt(9, doc.getUserId());
      ps.setInt(10, doc.getDocType());
      ps.setInt(11, 0);
      ps.setInt(12, 0);
      ps.setString(13, doc.getAttachNames());
      ps.setString(14, doc.getAttachIds());
      ps.execute();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, null);
    }
  }
}
