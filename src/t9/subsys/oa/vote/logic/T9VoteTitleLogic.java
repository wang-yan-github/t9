package t9.subsys.oa.vote.logic;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.vote.data.T9VoteTitle;

public class T9VoteTitleLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.vote.logic.T9VoteTitleLogic");
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String selectTitle(Connection dbConn,Map request) throws Exception {
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select SEQ_ID,PARENT_ID,FROM_ID,TO_ID"
        + ",PRIV_ID,USER_ID,SUBJECT,CONTENT"
        + ",TYPE,MAX_NUM,MIN_NUM,ANONYMITY,VIEW_PRIV"
        + ",SEND_TIME,BEGIN_DATE,END_DATE,PUBLISH,READERS"
        + ",VOTE_NO,ATTACHMENT_ID,ATTACHMENT_NAME,[TOP]";
    }else {
      sql = "select SEQ_ID,PARENT_ID,FROM_ID,TO_ID"
        + ",PRIV_ID,USER_ID,SUBJECT,CONTENT"
        + ",TYPE,MAX_NUM,MIN_NUM,ANONYMITY,VIEW_PRIV"
        + ",SEND_TIME,BEGIN_DATE,END_DATE,PUBLISH,READERS"
        + ",VOTE_NO,ATTACHMENT_ID,ATTACHMENT_NAME,TOP";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String selectVote(Connection dbConn,Map request,int seqId,String userPriv) throws Exception {
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select vo.SEQ_ID,USER_NAME,de.DEPT_NAME,vo.TO_ID"
        + ",vo.PRIV_ID,vo.USER_ID,vo.SUBJECT_MAIN,vo.TYPE,ANONYMITY"
        + ",vo.BEGIN_DATE,vo.END_DATE,vo.PUBLISH,vo.[TOP],MAX_NUM,vo.MIN_NUM"
        + ",vo.SEND_TIME,vo.READERS,vo.VIEW_PRIV,vo.CONTENT"
        + ",vo.VOTE_NO,vo.ATTACHMENT_ID,vo.ATTACHMENT_NAME,vo.PARENT_ID FROM VOTE_TITLE vo "
        + " left outer join person son on son.seq_id = vo.FROM_ID "
        + " left outer join department de on de.seq_id = son.DEPT_ID"
        + " where vo.PARENT_ID=0 ";
      if (!userPriv.equals("1")) {
        sql += " and vo.FROM_ID='" + seqId + "'";
      }
      sql += " order by vo.[TOP] desc,vo.BEGIN_DATE desc,vo.SEND_TIME desc ";
    }else {
      sql = "select vo.SEQ_ID,USER_NAME,de.DEPT_NAME,vo.TO_ID"
        + ",vo.PRIV_ID,vo.USER_ID,vo.SUBJECT_MAIN,vo.TYPE,ANONYMITY"
        + ",vo.BEGIN_DATE,vo.END_DATE,vo.PUBLISH,vo.TOP,MAX_NUM,vo.MIN_NUM"
        + ",vo.SEND_TIME,vo.READERS,vo.VIEW_PRIV,vo.CONTENT"
        + ",vo.VOTE_NO,vo.ATTACHMENT_ID,vo.ATTACHMENT_NAME,vo.PARENT_ID FROM VOTE_TITLE vo "
        + " left outer join person son on son.seq_id = vo.FROM_ID "
        + " left outer join department de on de.seq_id = son.DEPT_ID"
        + " where vo.PARENT_ID=0 ";
      if (!userPriv.equals("1")) {
        sql += " and vo.FROM_ID='" + seqId + "'";
      }
      sql += " order by vo.TOP desc,vo.BEGIN_DATE desc,vo.SEND_TIME desc ";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 按条件查询
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static List<T9VoteTitle> selectTitle(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9VoteTitle>  itemList = new ArrayList<T9VoteTitle>();
    itemList = orm.loadListSingle(dbConn, T9VoteTitle.class, str);
    return itemList;
  }
  /**
   * 删除ByVoteIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delTitleBySeqIds(Connection dbConn,String seqIds) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from VOTE_TITLE where seq_id in(" + seqIds + ")";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  /**
   * 删除全部
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delAllTitle(Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from VOTE_TITLE";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  /**
   * 清空按钮 更新数据库
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateTitleBySeqIds(Connection dbConn,String seqIds) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "update VOTE_TITLE set READERS = '' where SEQ_ID in(" + seqIds + ")";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  /**
   * 取消置顶 更新数据库
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateNoTopBySeqIds(Connection dbConn,String seqIds) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "update VOTE_TITLE set [top] = '0' where SEQ_ID in(" + seqIds + ")";
    }else {
      sql = "update VOTE_TITLE set top = '0' where SEQ_ID in(" + seqIds + ")";
    }
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--syl 个人事务
   * @return
   * @throws Exception 
   */
  public static String selectVoteToCurrent(Connection dbConn,Map request,int seqId,int deptId,String userPriv) throws Exception {
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    String top = "TOP";
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      top = "[TOP]";
    }
    String sql = "select vt.SEQ_ID,vt.FROM_ID,de.DEPT_NAME,p.USER_NAME"
      + ",vt.SUBJECT_MAIN,vt.ANONYMITY,vt.BEGIN_DATE,vt.END_DATE"
      +",vt.TYPE,vt.VIEW_PRIV,vt.PUBLISH,vt.READERS,vt." + top
      + " FROM VOTE_TITLE vt"
      + " left outer join person p on vt.FROM_ID = p.SEQ_ID"
      + " left outer join department de on de.seq_id = p.DEPT_ID"
      + " where PARENT_ID=0 and PUBLISH='1' " 
      + " and ("
      + T9DBUtility.findInSet("ALL_DEPT","vt.TO_ID")
      + " or " + T9DBUtility.findInSet("0","vt.TO_ID")
      + " or " + T9DBUtility.findInSet(deptId+"","vt.TO_ID")
      + " or " + T9DBUtility.findInSet(seqId+"", "vt.USER_ID")
      + " or " + T9DBUtility.findInSet(userPriv, "vt.PRIV_ID") + ")"

      + " and " + T9DBUtility.getDateFilter("vt.BEGIN_DATE", T9Utility.getCurDateTimeStr(), "<=")
      + " and " + "(" +T9DBUtility.getDateFilter("vt.END_DATE", T9Utility.getCurDateTimeStr(), ">")
      +" or vt.END_DATE is null)";
    
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql += " order by vt.[TOP] desc,vt.BEGIN_DATE desc,vt.SEND_TIME desc ";
    }else {
      sql += " order by vt.TOP desc,vt.BEGIN_DATE desc,vt.SEND_TIME desc ";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--syl 个人事务
   * @return
   * @throws Exception 
   */
  public static String selectVoteToHistory(Connection dbConn,Map request,int seqId,int deptId,String userPriv) throws Exception {
    String sql = "select vt.SEQ_ID,vt.FROM_ID,de.DEPT_NAME,p.USER_NAME"
      + ",vt.SUBJECT,vt.ANONYMITY,vt.BEGIN_DATE,END_DATE"
      +",vt.TYPE,vt.VIEW_PRIV,vt.PUBLISH,vt.READERS"
      + " FROM VOTE_TITLE vt"
      + " left outer join person p on vt.FROM_ID = p.SEQ_ID"
      + " left outer join department de on de.seq_id = p.DEPT_ID"
      + " where PARENT_ID=0 and PUBLISH='1' " 
      + " and ("
      + T9DBUtility.findInSet("ALL_DEPT","vt.TO_ID")
      + " or " + T9DBUtility.findInSet("0","vt.TO_ID")
      + " or " + T9DBUtility.findInSet(deptId+"","vt.TO_ID")
      + " or " + T9DBUtility.findInSet(seqId+"", "vt.USER_ID")
      + " or " + T9DBUtility.findInSet(userPriv, "vt.PRIV_ID") + ")"
      + " and "  +T9DBUtility.getDateFilter("vt.END_DATE", T9Utility.getCurDateTimeStr(), "<=")
      +" and vt.END_DATE is not null";
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql += " order by vt.[TOP] desc,vt.BEGIN_DATE desc,vt.SEND_TIME desc ";
    }else {
      sql += " order by vt.TOP desc,vt.BEGIN_DATE desc,vt.SEND_TIME desc ";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /**
   * 新建项目
   * 
   * @return
   * @throws Exception
   */
  public static String addVote(Connection dbConn,T9VoteTitle title) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, title);
    return getMaSeqId(dbConn,"VOTE_TITLE");
  }

  /**
   *返回项目seqId
   * 
   * @return
   * @throws Exception
   */
  public static String getMaSeqId(Connection dbConn,String tableName)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String maxSeqId = "0";
    int seqId = 0;
    String sql = "select max(SEQ_ID) as SEQ_ID from " + tableName;
    try{
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      if(rs.next()){
        seqId = rs.getInt("SEQ_ID");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    maxSeqId = String.valueOf(seqId);
    return maxSeqId;
  }
  /**
   * 处理上传附件，返回附件id，附件名称
   * 
   * @param request
   *          HttpServletRequest
   * @param
   * @return Map<String, String> ==> {id = 文件名}
   * @throws Exception
   */
  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm) throws Exception {
    Map<String, String> result = new HashMap<String, String>();
    try {
      Calendar cld = Calendar.getInstance();
      int year = cld.get(Calendar.YEAR) % 100;
      int month = cld.get(Calendar.MONTH) + 1;
      String mon = month >= 10 ? month + "" : "0" + month;
      String hard = year + mon;
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        String fileNameV = fileName;
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = T9DiaryUtil.getRondom();
        fileName = rand + "_" + fileName;
        while (T9DiaryUtil.getExist(T9SysProps.getAttachPath() + File.separator + hard, fileName)) {
          rand = T9DiaryUtil.getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, T9SysProps.getAttachPath()  + File.separator + "vote" + File.separator +  hard  + File.separator + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
  /**
   * seqId串转换成privName,userName,deptName串
   * 
   * @return
   * @throws Exception
   */ 
  public static String strString(Connection dbConn,String seqId,String tableName,String tdName) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select " + tdName + " from " + tableName + " where seq_id in (" + seqId + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String strString = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        strString += rs.getString(tdName) + ",";
      }
      if (strString.length() > 0) {
        strString = strString.substring(0,strString.length()-1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
    return strString;
  }

  /**
   * seqId串
   * 
   * @return
   * @throws Exception
   */ 
  public static String strSeqId(Connection dbConn,String seqId,String deptId,String privId) throws Exception {
    String sql = null;
    if (deptId.equals("0") || deptId.equals("ALL_DEPT")) {
      sql = "select SEQ_ID from person ";
    } else {
      sql = "select SEQ_ID from person where 1=1 ";
      String fildIdStr  = ""; 
      if(!T9Utility.isNullorEmpty(seqId)){
        if(seqId.endsWith(",")){
          seqId = seqId.substring(0, seqId.length()-1);
        }
        fildIdStr = fildIdStr + " or SEQ_ID in (" + seqId + ")";
      }
      String newDeptId = getNewSeqId(deptId);
      if(!T9Utility.isNullorEmpty(newDeptId)){
        if(newDeptId.endsWith(",")){
          newDeptId = newDeptId.substring(0, newDeptId.length()-1);
        }
        fildIdStr = fildIdStr + " or DEPT_ID in (" + newDeptId + ")";
      }
      String newPrivId = getNewSeqId(privId);
      if(!T9Utility.isNullorEmpty(newPrivId)){
        if(newPrivId.endsWith(",")){
          newPrivId = newPrivId.substring(0, newPrivId.length()-1);
        }
        fildIdStr = fildIdStr + " or USER_PRIV in (" + newPrivId + ")";
      }
      if(!T9Utility.isNullorEmpty(fildIdStr)){
        if(fildIdStr.startsWith(" or")){
          fildIdStr = fildIdStr.substring(3, fildIdStr.length());
        }
        sql = sql + " and (" + fildIdStr + ")";
      }
    }
    PreparedStatement ps = null;
    ResultSet rs = null;
    String strSeqId = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        strSeqId += rs.getString("SEQ_ID") + ",";
      }
      if (strSeqId.length() > 0) {
        strSeqId = strSeqId.substring(0,strSeqId.length()-1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
    return strSeqId;
  }  
  
  /**
   * 得到投票人数（排除禁止登录用户）

   * 
   * @return
   * @throws Exception
   */ 
  public static String getPersonCount(Connection dbConn,String userId,String deptId,String privId) throws Exception {
    String sql = null;
    if (deptId.equals("0") || deptId.equals("ALL_DEPT")) {
      sql = "select count(*) from person where NOT_LOGIN <> '1'";
    } else {
      sql = "select count(*) from person where 1 =1 ";
      String fildIdStr  = ""; 
      if(!T9Utility.isNullorEmpty(userId)){
        if(userId.endsWith(",")){
          userId = userId.substring(0, userId.length()-1);
        }
        fildIdStr = fildIdStr + " or SEQ_ID in (" + userId + ")";
      }
      String newDeptId = getNewSeqId(deptId);
      if(!T9Utility.isNullorEmpty(newDeptId)){
        if(newDeptId.endsWith(",")){
          newDeptId = newDeptId.substring(0, newDeptId.length()-1);
        }
        fildIdStr = fildIdStr + " or DEPT_ID in (" + newDeptId + ")";
      }
      String newPrivId = getNewSeqId(privId);
      if(!T9Utility.isNullorEmpty(newPrivId)){
        if(newPrivId.endsWith(",")){
          newPrivId = newPrivId.substring(0, newPrivId.length()-1);
        }
        fildIdStr = fildIdStr + " or USER_PRIV in (" + newPrivId + ")";
      }
      if(!T9Utility.isNullorEmpty(fildIdStr)){
        if(fildIdStr.startsWith(" or")){
          fildIdStr = fildIdStr.substring(3, fildIdStr.length());
        }
        sql = sql + " and (" + fildIdStr + ")";
      }
      sql = sql + " and  NOT_LOGIN <> '1'";
    }
    PreparedStatement ps = null;
    ResultSet rs = null;
    String count = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        if(!T9Utility.isNullorEmpty(rs.getString(1))){
          count = rs.getString(1);
        }
      }
 
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
    return count;
  }

  public static String getNewSeqId(String seqId)  {
    String newSeqId = "";
    if(!T9Utility.isNullorEmpty(seqId)){
      if(seqId.endsWith(",")){
        seqId = seqId.substring(0, seqId.length()-1);
      }
      String[] seqIdArray = seqId.split(",");
      for (int i = 0; i < seqIdArray.length; i++) {
        newSeqId = newSeqId + "'" + seqIdArray[i] + "',";
      }
      newSeqId = newSeqId.substring(0, newSeqId.length()-1);
    }
    return newSeqId;
  }
  /**
   *详细信息
   * 
   * @return
   * @throws Exception
   */
  public static T9VoteTitle showDetail(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    return (T9VoteTitle)orm.loadObjComplex(dbConn,T9VoteTitle.class,seqId);
  }
  /**
   * 修改项目
   * 
   * @return
   * @throws Exception
   */
  public static void updateVote(Connection dbConn,T9VoteTitle title) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, title);
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String showVote(Connection dbConn,Map request,String parentId) throws Exception {
    String sql = "select SEQ_ID,PARENT_ID,SUBJECT,TYPE,MAX_NUM,MIN_NUM,ANONYMITY FROM VOTE_TITLE "
      + " WHERE PARENT_ID='" + parentId + "' order by VOTE_NO  ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 立即生效,立即终止,恢复终止
   * @return
   * @throws Exception
   */
  public static void updateBeginDate(Connection dbConn,int seqId,String tdName,Date beginDate) throws Exception {
    String sql = "update VOTE_TITLE set " + tdName + "=? where SEQ_ID=?";
    PreparedStatement ps = null;
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setDate(1,beginDate);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps,null, log);
    }
  }
  /**
   * 立即发布
   * @return
   * @throws Exception
   */
  public static void updatePublish(Connection dbConn,int seqId,String publish , int loginUser) throws Exception {
    String sql = "update VOTE_TITLE set PUBLISH=? where SEQ_ID=?";
    PreparedStatement ps = null;
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1,publish);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps,null, log);
    }
    T9ORM orm = new T9ORM();
    T9VoteTitle voteTitle = (T9VoteTitle)orm.loadObjSingle(dbConn, T9VoteTitle.class, seqId);
    
    if (voteTitle == null) {
      return;
    }
    String query2 ="select * from SYS_PARA where PARA_NAME='SMS_REMIND'";
    Statement stm = null;
    ResultSet rs = null;
    String paraValue = "";
    try {
      stm = dbConn.createStatement();
      rs = stm.executeQuery(query2);
      if(rs.next()) {
        paraValue = rs.getString("PARA_VALUE");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    String[] ss = paraValue.split("\\|");
    String sms = "";
    String sms2 = "";
    if (ss.length < 2) {
      sms = ss[0];
    } else {
      sms = ss[0];
      sms2 = ss[1];
    }
    String s =  voteTitle.getSubject();
    if (s.length() > 100) {
      s = s.substring(0 , 100);
    }
    
    java.util.Date now = new java.util.Date();
    java.util.Date sendTime = new java.util.Date();
    if (now.getTime() < voteTitle.getBeginDate().getTime()) {
      sendTime = voteTitle.getBeginDate();
    }
    String query3 = "";
    if ("ALL_DEPT".equals(voteTitle.getToId()) || "0".equals(voteTitle.getToId())) {
      query3 = "select SEQ_ID from PERSON";
    } else {
      query3 = "select SEQ_ID from PERSON where 1<> 1 ";
      if (!T9Utility.isNullorEmpty(voteTitle.getPrivId())) {
        query3 += " or USER_PRIV in (" + T9WorkFlowUtility.getOutOfTail(voteTitle.getPrivId()) + ")";
      }
      if (!T9Utility.isNullorEmpty(voteTitle.getUserId())) {
        query3 += " or SEQ_ID in (" + T9WorkFlowUtility.getOutOfTail(voteTitle.getUserId()) + ")";
      }
      if (!T9Utility.isNullorEmpty(voteTitle.getToId())) {
        query3 += " or DEPT_ID in (" + T9WorkFlowUtility.getOutOfTail(voteTitle.getToId()) + ")";
      }
    }
    Statement stm3 = null;
    ResultSet rs3 = null;
    String userId = "";
    try {
      stm3 = dbConn.createStatement();
      rs3 = stm3.executeQuery(query3);
      while (rs3.next()) {
        userId += rs3.getInt("SEQ_ID") + ",";
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm3, rs3, null);
    }
    
    if (t9.core.funcs.doc.util.T9WorkFlowUtility.findId(sms, "11")) {
      
      String sContent = "请查看投票！\n标题：" + s;
      
      String url = "/subsys/oa/vote/show/readVote.jsp?seqId=" + voteTitle.getSeqId() + "&openFlag=1&width=800&height=600";
      if (!"".equals(userId)) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("11");
        sb.setContent(sContent);
        sb.setFromId(loginUser);
        sb.setToId(userId);
        sb.setRemindUrl(url);
        sb.setSendDate(sendTime);
        T9SmsUtil.smsBack(dbConn, sb);
      }
    }
    if (t9.core.funcs.doc.util.T9WorkFlowUtility.findId(sms2, "11")) {
      String query4 =  "select USER_NAME from PERSON where SEQ_ID='"+ loginUser +"'";
      Statement stm4 = null;
      ResultSet rs4= null;
      String userName = "";
      try {
        stm4 = dbConn.createStatement();
        rs4 = stm4.executeQuery( query4);
        if (rs4.next()) {
          userName = rs4.getString("USER_NAME") ;
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm4, rs4, null);
      }
      if (!"".equals(userId)) {
        String content ="OA投票,来自"+ userName +":" + s;
        T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
        ms2l.remindByMobileSms(dbConn, userId ,loginUser, content, sendTime);
      }
    }
  }

  /**
   * 更新数据投票人ID
   * @return
   * @throws Exception
   */
  public static void updateReaders(Connection dbConn,int seqId,String readers) throws Exception {
    String sql = "update VOTE_TITLE set READERS=? where SEQ_ID=?";
    PreparedStatement ps = null;
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setString(1,readers);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps,null, log);
    }
  }
  /***
   * BySeqId
   * @return
   * @throws Exception 
   */
  public static T9VoteTitle selectVoteById(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9VoteTitle title = (T9VoteTitle) orm.loadObjSingle(dbConn, T9VoteTitle.class, seqId);
    return title;
  }
  /***
   * 数据导出
   * @return
   * @throws Exception 
   * @throws Exception 
   */
  public static ArrayList<T9DbRecord> getDbRecord(Connection dbConn,List<T9VoteTitle> list) throws Exception{
    ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
    T9VoteTitle title = new T9VoteTitle();
    int sunNum = 0;
    for (int i = 0; i < list.size(); i++) {
      T9DbRecord dbrec = new T9DbRecord();
      title = list.get(i);
      if (!T9Utility.isNullorEmpty(title.getSubject())) {
        sunNum ++;
        dbrec.addField("标题  ",sunNum + "、" +title.getSubject());
      }else {
        dbrec.addField("标题  ","");
      }
      dbrec.addField("选项  ",title.getContent());  
      dbrec.addField("票数",title.getParentId());
      if (!T9Utility.isNullorEmpty(title.getFromId())) {
        dbrec.addField("投票人",T9VoteTitleLogic.strString(dbConn,title.getFromId(),"PERSON","USER_NAME"));
      }
      if (T9Utility.isNullorEmpty(title.getFromId())) {
        dbrec.addField("投票人","");
      }
      dbL.add(dbrec);
    }
    return dbL;
  }
}
