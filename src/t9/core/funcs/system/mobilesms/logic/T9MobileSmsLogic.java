package t9.core.funcs.system.mobilesms.logic;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9DsTable;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.address.data.T9AddressGroup;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.mobilesms.data.T9Sms2;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9MobileSmsLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.mobilesms.logic.T9MobileSmsLogic");
  
  /**
   * 获取SYS_CODE表中SEQ_ID  读取模块权限备选项名称
   * @param dbConn
   * @param typePrivStr
   * @return
   * @throws Exception
   */
  
  public String getSysCodeSeqId(Connection dbConn, String typePrivStr)
      throws Exception {
    String seqIdStr = "";
    Statement stmt = null;
    ResultSet rs = null;
    String[] func = typePrivStr.split(",");
    for (int i = 0; i < func.length; i++) {
      String funcs = func[i];
      String sql = "SELECT SEQ_ID FROM CODE_ITEM WHERE CLASS_NO = 'SMS_REMIND' and CLASS_CODE = '" + funcs + "'";
      try {
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          seqIdStr += String.valueOf(rs.getInt("SEQ_ID")) + ",";
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stmt, rs, log);
      }
    }
    return seqIdStr;
  }
  
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String getManagePersonList(Connection conn,Map request, String phone, String content, String beginDate, String endDate) throws Exception{
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql = "select SEQ_ID" +
                 ",CONTENT" +
                 ",SEND_TIME from SMS3 WHERE 1=1";
    if(!T9Utility.isNullorEmpty(phone)){ 
      sql = sql + " and PHONE like '%" + phone + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(content)){ 
      sql = sql + " and CONTENT like '%" + content + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql = sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql = sql + " and " + endDate; 
    } 
    sql = sql + " order by SEND_TIME desc";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  
  public boolean existsMobilNo(Connection dbConn, String phone)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM PERSON WHERE MOBIL_NO = '" + phone
          + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == 1) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 短信接收管理批量删除
   * @param conn
   * @param seqIds   PERSON表中的SEQ_ID串（以逗号为分隔）
   * @throws Exception
   */
  
  public void deleteAll(Connection conn, String seqIds) throws Exception {
    String sql = "DELETE FROM SMS3 WHERE SEQ_ID IN(" + seqIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 短信接收管理当前页面删除
   * @param conn
   * @param request
   * @param phone
   * @param content
   * @param beginDate
   * @param endDate
   * @param limit
   * @throws Exception
   */
  
  public void deleteReceiveManage(Connection conn,Map request, String phone, String content, String beginDate, String endDate) throws Exception{
    PreparedStatement pstmt = null;
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql =  "DELETE FROM SMS3 WHERE 1=1";
    if(!T9Utility.isNullorEmpty(phone)){ 
      sql = sql + " and PHONE like '%" + phone + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(content)){ 
      sql = sql + " and CONTENT like '%" + content + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql = sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql = sql + " and " + endDate; 
    } 
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String getSendSearchList(Connection conn,Map request, String sendFlag, String phone, String content, String beginDate, String endDate, String user) throws Exception{
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql =  "select SEQ_ID" +
                  ",FROM_ID" +
                  ",CONTENT" +
                  ",SEND_TIME" +
                  ",SEND_FLAG from SMS2 WHERE 1=1";
    if(!T9Utility.isNullorEmpty(phone)){ 
      sql = sql + " and PHONE like '%" + phone + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(content)){ 
      sql = sql + " and CONTENT like '%" + content + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(sendFlag)){ 
      if(!"ALL".equals(sendFlag.trim())){
        sql = sql + " and SEND_FLAG ='" + sendFlag + "'"; 
      }
    }
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql = sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql = sql + " and " + endDate; 
    } 
    if(!T9Utility.isNullorEmpty(user)){ 
      sql = sql + " and FROM_ID in (" + user + ")"; 
    } 
    sql = sql + " order by SEND_TIME desc";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  
  
  public void deleteSendManage(Connection conn,Map request, String phone, String content, String beginDate, String endDate, String user) throws Exception{
    PreparedStatement pstmt = null;
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql =  "DELETE FROM SMS2 WHERE 1=1";
    if(!T9Utility.isNullorEmpty(phone)){ 
      sql = sql + " and PHONE like '%" + phone + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(content)){ 
      sql = sql + " and CONTENT like '%" + content + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(user)){ 
      sql = sql + " and FROM_ID = '" + user +"'"; 
    } 
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql = sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql = sql + " and " + endDate; 
    } 
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 手机短信查询结果批量删除
   * @param conn
   * @param seqIds   PERSON表中的SEQ_ID串（以逗号为分隔）
   * @throws Exception
   */
  
  public void deleteSelectSms2(Connection conn, String seqIds) throws Exception {
    String sql = "DELETE FROM SMS2 WHERE SEQ_ID IN(" + seqIds + ")";
    //System.out.println(sql+"HHGGIYUIOJIO");
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 按人员统计:手机短信发送统计分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String getReportSearchList(Connection conn,Map request, String beginDate, String endDate) throws Exception{
    String sql =  "select PERSON.SEQ_ID" +
                  ",PERSON.USER_ID" +
                  ",DEPARTMENT.DEPT_NAME" +
                  ",PERSON.USER_NAME from PERSON,USER_PRIV,DEPARTMENT WHERE DEPARTMENT.SEQ_ID=PERSON.DEPT_ID and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and not NOT_LOGIN='1'";
 
    sql = sql +" order by DEPARTMENT.DEPT_NO, USER_PRIV.PRIV_NO, PERSON.USER_NO, PERSON.USER_NAME";
    //System.out.println(sql+"BNMBNMB");
    
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  
  /**
   * 按部门统计 :手机短信发送统计分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String getReportDeptSearchList(Connection conn,Map request, String beginDate, String endDate) throws Exception{
    String sql =  "select PERSON.SEQ_ID" +
                  ",PERSON.USER_ID" +
                  ",DEPARTMENT.DEPT_NAME from PERSON,USER_PRIV,DEPARTMENT WHERE DEPARTMENT.SEQ_ID=PERSON.DEPT_ID and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and not NOT_LOGIN='1'";
 
    sql = sql +" order by DEPARTMENT.DEPT_NO, USER_PRIV.PRIV_NO, PERSON.USER_NO, PERSON.USER_NAME";
    
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  
  public String getReportDeptSearchList1(Connection dbConn, String beginDate, String endDate) throws Exception{
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int deptCount1 = 0;
    int deptCount2 = 0;
    int deptCount3 = 0;
    int deptCount1Bak = 0;
    int deptCount2Bak = 0;
    int deptCount3Bak = 0;
    String temp = "";
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer("[");
    String sql =  "select PERSON.SEQ_ID" +
                  ",PERSON.USER_ID" +
                  ",DEPARTMENT.DEPT_NAME from PERSON,USER_PRIV,DEPARTMENT WHERE DEPARTMENT.SEQ_ID = PERSON.DEPT_ID and PERSON.USER_PRIV = USER_PRIV.SEQ_ID and not NOT_LOGIN = '1'";
 
    sql = sql +" order by DEPARTMENT.DEPT_NO, USER_PRIV.PRIV_NO, PERSON.USER_NO, PERSON.USER_NAME";
    //System.out.println(sql);
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        int seqId = rs.getInt(1);
        String seqIdStr = String.valueOf(seqId);
        String userId = rs.getString(2);
        String deptName = rs.getString(3);
        count1 = Integer.parseInt(getSendSuccess(dbConn, seqIdStr, beginDate, endDate));
        count2 = Integer.parseInt(getSendNo(dbConn, seqIdStr, beginDate, endDate));
        count3 = Integer.parseInt(getsendTimeOut(dbConn, seqIdStr, beginDate, endDate));
        
        if(!temp.trim().equals(deptName.trim())){
          deptCount1Bak = deptCount1;
          deptCount1 = count1;
          deptCount2Bak = deptCount2;
          deptCount2 = count2;
          deptCount3Bak = deptCount3;
          deptCount3 = count3;
         
        }else{
          deptCount1 += count1;
          deptCount2 += count2;
          deptCount3 += count3;
        }
        if(!temp.trim().equals(deptName.trim())){
          if(!"".equals(temp.trim())){
            sb.append("{");
            sb.append("deptName:\"" + temp + "\"");
            sb.append(",count1:\"" + deptCount1Bak + "\"");
            sb.append(",count2:\"" + deptCount2Bak + "\"");
            sb.append(",count3:\"" + deptCount3Bak + "\"");
            sb.append("},");
          }
        }
        temp = deptName;
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return sb.toString();
  }
  
  /**
   * 获取发送成功记录
   * @param dbConn
   * @param seqId      FROM_ID
   * @param beginDate  起始时间
   * @param endDate    截至时间
   * @return
   * @throws Exception
   */
  
  public String getSendSuccess(Connection dbConn ,String seqId, String beginDate, String endDate) throws Exception{
    //SELECT  SEQ_ID FROM EMAIL_BODY WHERE SEQ_ID IN( SELECT BODY_ID FROM EMAIL WHERE BOX_ID = 0)
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql = " SELECT count(*) FROM SMS2 WHERE SEND_FLAG='1' and FROM_ID='" + seqId + "'";
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql=sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql=sql + " and " + endDate; 
    } 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  /**
   * 获取未发送记录
   * @param dbConn
   * @param seqId      FROM_ID
   * @param beginDate  起始时间
   * @param endDate    截至时间
   * @return
   * @throws Exception
   */
  
  public String getSendNo(Connection dbConn ,String seqId, String beginDate, String endDate) throws Exception{
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql = " SELECT count(*) FROM SMS2 WHERE SEND_FLAG='0' and FROM_ID='" + seqId + "'";
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql=sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql=sql + " and " + endDate; 
    } 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  /**
   * 获取发送超时记录
   * @param dbConn
   * @param seqId      FROM_ID
   * @param beginDate  起始时间
   * @param endDate    截至时间
   * @return
   * @throws Exception
   */
  
  public String getsendTimeOut(Connection dbConn ,String seqId, String beginDate, String endDate) throws Exception{
    if(!T9Utility.isNullorEmpty(beginDate)){
      beginDate = T9DBUtility.getDateFilter("SEND_TIME", beginDate, ">=");
    }
    if(!T9Utility.isNullorEmpty(endDate)){
      endDate = T9DBUtility.getDateFilter("SEND_TIME", endDate, "<=");
    }
    String sql = " SELECT count(*) FROM SMS2 WHERE SEND_FLAG='2' and FROM_ID='" + seqId + "'";
    if(!T9Utility.isNullorEmpty(beginDate)){ 
      sql=sql + " and " + beginDate; 
    } 
    if(!T9Utility.isNullorEmpty(endDate)){ 
      sql=sql + " and " + endDate; 
    } 
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  /**
   * 删除该用户的发送记录 
   * @param conn
   * @param seqIds   SMS2表中的FROM_ID
   * @throws Exception
   */
  
  public void deleteSendSign(Connection conn, String fromId) throws Exception {
    String sql = "DELETE FROM SMS2 WHERE FROM_ID ='" + fromId + "'";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  /**
   * 取得部门名称
   * @param conn
   * @param deptId
   * @return
   * @throws Exception
   */
  public String getDeptNameLogic(Connection conn , int deptId) throws Exception{
    String result = "";
    String sql = " select DEPT_NAME from DEPARTMENT where SEQ_ID = " + deptId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  public ArrayList<T9Person> getMobileSmsFunc(Connection conn, String mobilNo)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Person persons = null;
    ArrayList<T9Person> personList = new ArrayList<T9Person>();
    try {
      stmt = conn.createStatement();
      String sql = "select SEQ_ID" 
                + ",USER_NAME"
                + ",MOBIL_NO_HIDDEN from PERSON where MOBIL_NO like '%" + mobilNo
                + "%'" + T9DBUtility.escapeLike();
      rs = stmt.executeQuery(sql);
      //System.out.println(sql + "NMJK");
      while (rs.next()) {
        persons = new T9Person();
        persons.setSeqId(rs.getInt("SEQ_ID"));
        persons.setUserName(rs.getString("USER_NAME"));
        persons.setMobilNoHidden(rs.getString("MOBIL_NO_HIDDEN"));
        personList.add(persons);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return personList;
  }
  
  public ArrayList<T9Sms2> getSms2Phone(Connection conn, int seqId)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Sms2 phone = null;
    ArrayList<T9Sms2> phoneList = new ArrayList<T9Sms2>();
    try {
      stmt = conn.createStatement();
      String sql = "select PHONE from SMS2 where SEQ_ID =" + seqId;
      rs = stmt.executeQuery(sql);
      //System.out.println(sql + "NMJK");
      while (rs.next()) {
        phone = new T9Sms2();
        phone.setPhone(rs.getString("PHONE"));
        phoneList.add(phone);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return phoneList;
  }
  
  public ArrayList<T9Address> getAddressPsnName(Connection conn, String mobilNo)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Address psnName = null;
    ArrayList<T9Address> addressList = new ArrayList<T9Address>();
    try {
      stmt = conn.createStatement();
      String sql = "select PSN_NAME from ADDRESS where MOBIL_NO like '%" + mobilNo + "%'" + T9DBUtility.escapeLike();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        psnName = new T9Address();
        psnName.setPsnName(rs.getString("PSN_NAME"));
        addressList.add(psnName);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return addressList;
  }
  
  
  public ArrayList<T9DbRecord> toExportDeptData(Connection conn) throws Exception{
    ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
    String sql = "SELECT "
      + " SEQ_ID "
      + ",PHONE "
      + ",CONTENT "
      + ",SEND_TIME "
      + " from SMS3";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery() ;
      while (rs.next()) {
        T9DbRecord record = new T9DbRecord();
        int seqId = rs.getInt(1);
        String phone = rs.getString(2);
        String content = rs.getString(3);
        Date sendTime = rs.getTimestamp(4);
        
        record.addField("发信人", getFromUserName(conn, seqId));
        record.addField("手机号码", getPhoneNo(conn, phone, seqId));
        record.addField("内容",  content);
        record.addField("发送时间", T9Utility.getDateTimeStr(sendTime));
        result.add(record);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  public ArrayList<T9DbRecord> exportToExcelSmsPerson(Connection conn, String beginDate, String endDate)
      throws Exception {
    ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
    String sql = "select PERSON.SEQ_ID"
               + ",PERSON.USER_ID"
               + ",DEPARTMENT.DEPT_NAME"
               + ",PERSON.USER_NAME from PERSON,USER_PRIV,DEPARTMENT WHERE DEPARTMENT.SEQ_ID=PERSON.DEPT_ID and PERSON.USER_PRIV=USER_PRIV.SEQ_ID and not NOT_LOGIN='1'";

    sql = sql
        + " order by DEPARTMENT.DEPT_NO, USER_PRIV.PRIV_NO, PERSON.USER_NO, PERSON.USER_NAME";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        T9DbRecord record = new T9DbRecord();
        int seqId = rs.getInt(1);
        String userId = rs.getString(2);
        String deptName = rs.getString(3);
        String userName = rs.getString(4);

        record.addField("部门", deptName);
        record.addField("用户", userName);
        record.addField("发送成功", getSendSuccess(conn, String.valueOf(seqId), beginDate, endDate));
        record.addField("未发送", getSendNo(conn, String.valueOf(seqId), beginDate, endDate));
        record.addField("发送超时", getsendTimeOut(conn, String.valueOf(seqId), beginDate, endDate));
        result.add(record);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  public ArrayList<T9DbRecord> exportToExcelSmsDept(Connection conn, String beginDate, String endDate)
  throws Exception {
    ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int deptCount1 = 0;
    int deptCount2 = 0;
    int deptCount3 = 0;
    int deptCount1Bak = 0;
    int deptCount2Bak = 0;
    int deptCount3Bak = 0;
    String temp = "";
  
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select PERSON.SEQ_ID" +
                  ",PERSON.USER_ID" +
                  ",DEPARTMENT.DEPT_NAME from PERSON,USER_PRIV,DEPARTMENT WHERE DEPARTMENT.SEQ_ID = PERSON.DEPT_ID and PERSON.USER_PRIV = USER_PRIV.SEQ_ID and not NOT_LOGIN = '1'";
    
    sql = sql +" order by DEPARTMENT.DEPT_NO, USER_PRIV.PRIV_NO, PERSON.USER_NO, PERSON.USER_NAME";
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        T9DbRecord record = new T9DbRecord();
        int seqId = rs.getInt(1);
        String seqIdStr = String.valueOf(seqId);
        String userId = rs.getString(2);
        String deptName = rs.getString(3);
        count1 = Integer.parseInt(getSendSuccess(conn, seqIdStr, beginDate, endDate));
        count2 = Integer.parseInt(getSendNo(conn, seqIdStr, beginDate, endDate));
        count3 = Integer.parseInt(getsendTimeOut(conn, seqIdStr, beginDate, endDate));
          
        if(!temp.trim().equals(deptName.trim())){
          deptCount1Bak = deptCount1;
          deptCount1 = count1;
          deptCount2Bak = deptCount2;
          deptCount2 = count2;
          deptCount3Bak = deptCount3;
          deptCount3 = count3;
        }else{
          deptCount1 += count1;
          deptCount2 += count2;
          deptCount3 += count3;
        }
        if(!temp.trim().equals(deptName.trim())){
          if(!"".equals(temp.trim())){
            record.addField("部门", temp);
            record.addField("用户", deptCount1Bak);
            record.addField("发送成功", deptCount2Bak);
            record.addField("未发送", deptCount3Bak);
            result.add(record);
          }
        }
        temp = deptName;
       }
    }catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return result;
  }

  public String getPhoneNo(Connection conn, String mobilNo, int seqId)
  throws Exception {
    String useName = getPersonMobilNo(conn, mobilNo);
    String mobilNoHidden = getMobilNoHidden(conn, mobilNo);
    if(!"".equals(useName) && mobilNoHidden == "1"){
      return "不公开";
    }else{
      String phone = getSms3MobilNo(conn, seqId);
      return phone;
    }
  }
  
  /**
   * 获取发信人
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  
  public String getFromUserName(Connection conn , int seqId) throws Exception{
    String result = "";
    String phone = getSms3MobilNo(conn, seqId);
    String userName = getPersonMobilNo(conn, phone);
    String psnName = getAddressMobilNo(conn, phone);
    if(T9Utility.isNullorEmpty(userName)){
      if(T9Utility.isNullorEmpty(psnName)){
        return "未知";
      }else{
        return psnName;
      }
    }else{
      return userName;
    }
  }
  
  /**
   * 获取用户姓名
   * @param conn
   * @param mobilNo
   * @return
   * @throws Exception
   */
  
  public String getPersonMobilNo(Connection conn, String mobilNo)
      throws Exception {
    String result = "";
    String sql = "select USER_NAME from PERSON where MOBIL_NO like '%" + mobilNo
               + "%'" + T9DBUtility.escapeLike();
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  public String getMobilNoHidden(Connection conn, String mobilNo)
  throws Exception {
    String result = "";
    String sql = "select MOBIL_NO_HIDDEN from PERSON where MOBIL_NO like '%" + mobilNo
           + "%'" + T9DBUtility.escapeLike();
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  /**
   * 获取psn_name
   * @param conn
   * @param mobilNo
   * @return
   * @throws Exception
   */
  
  public String getAddressMobilNo(Connection conn, String mobilNo)
      throws Exception {
    String result = "";
    String sql = "select PSN_NAME from ADDRESS where MOBIL_NO like '%" + mobilNo + "%'" + T9DBUtility.escapeLike();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String psnName = rs.getString(1);
        if (psnName != null) {
          result = psnName;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  /**
   * 获取电话号码
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  
  public String getSms3MobilNo(Connection conn , int seqId) throws Exception{
    String result = "";
    String sql = " select PHONE from SMS3 where SEQ_ID = " + seqId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
}
