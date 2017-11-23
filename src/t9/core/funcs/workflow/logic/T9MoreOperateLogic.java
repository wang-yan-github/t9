package t9.core.funcs.workflow.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.logic.T9AttachmentLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.email.data.T9Email;
import t9.core.funcs.email.data.T9EmailBody;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.notify.logic.T9NotifyManageUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
public class T9MoreOperateLogic {
  public static String NOTIFY_MENU_ID = "0506";
  public static String EMAIL_MENU_ID = "0204";
  public static String ROLL_MENU_ID = "2240";
  public static String SAVE_MENU_ID = "0916";
  public static String filePath = T9SysProps.getAttachPath() + File.separator  + "roll_manage";
  public String[] saveAttachment( T9FileUploadForm fileForm ) throws Exception{
    String[]  ss = new String[2];
    File f1 = new File(filePath);
    if (!f1.exists()) {
      f1.mkdir();
    }
    Calendar cld = Calendar.getInstance();
    int year =  cld.get(Calendar.YEAR)%100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10?month+"":"0"+month;
    String hard = year + mon ;
    Iterator<String> iKeys = fileForm.iterateFileFields();
    String attachmentNameStr = "";
    String attachmentIdStr = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      if (fieldName.startsWith("ATTACHMENT_")) {
        String fileName = fileForm.getFileName(fieldName);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String attachmentId = T9Guid.getRawGuid(); 
        String fileName2 = attachmentId + "_" + fileName;
        File f2 = new File(filePath + File.separator   + hard);
        if (!f2.exists()) {
          f2.mkdir();
        }
        String tmp = filePath + File.separator +hard + File.separator + fileName2;
        fileForm.saveFile(fieldName , tmp);
        
        attachmentNameStr += fileName + "*";
        attachmentIdStr += hard + "_" + attachmentId + ",";
      }
    }
    ss[0] = attachmentIdStr;
    ss[1] = attachmentNameStr;
    return ss;
  }
  
  
  public String[] getAttachment(Connection conn  , String runId) throws Exception {
    String query = "select ATTACHMENT_ID,ATTACHMENT_NAME from FLOW_RUN where run_id='" + runId + "' ";
    Statement stm = null; 
    ResultSet rs = null; 
    String[]  ss = new String[2];
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        ss[0] = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
        ss[1] =T9Utility.null2Empty( rs.getString("ATTACHMENT_NAME"));
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return ss;
  }
  public boolean hasModulePriv(List<String> privs , String module) {
    for (String tmp : privs) {
      if (T9WorkFlowUtility.findId(tmp, module)) {
        return true;
      }
    }
    return false;
  }
  public void saveNotify(Connection conn, T9Notify notify , T9FlowRun run ,String html , T9Person person ,String mailRemind, String mobileRemind) throws Exception {
    T9ORM orm = new T9ORM();
    String ss = T9WorkFlowUtility.copyAttach(run.getAttachmentId(), run.getAttachmentName(), "workflow", "notify");
    notify.setAttachmentId(ss);
    notify.setAttachmentName(run.getAttachmentName());
    
    notify.setCompressContent(html.getBytes(T9Const.DEFAULT_CODE));
    notify.setContent(T9DiaryUtil.cutHtml(html));
    
    notify.setSubjectFont("");
    
    if(notify.getSubject() == null || "".equals(notify.getSubject())){
      String subjectTmp  = "";
      if(!T9Utility.isNullorEmpty(notify.getAttachmentName())) {
        subjectTmp = notify.getAttachmentName().split("\\*")[0];
        subjectTmp = subjectTmp.substring(subjectTmp.indexOf("_") + 1,subjectTmp.lastIndexOf("."));     
      }else{
        subjectTmp = "[无主题]";
      }
      notify.setSubject(T9Utility.decodeURL(subjectTmp));
    }
    
    notify.setFromId(Integer.toString(person.getSeqId()));
    if("".equals(notify.getBeginDate()) || notify.getBeginDate() == null){
      notify.setBeginDate(new Date());
    }

    notify.setSendTime(new Date());
    if(notify.getToId() == null ||  "".equals(notify.getToId())){
      notify.setToId("-1");
    }
    notify.setFromDept(person.getDeptId());
    
    T9NotifyManageUtilLogic notifyManageUtil = new T9NotifyManageUtilLogic();
    orm.saveSingle(conn, notify);//新建保存
    int bId = notifyManageUtil.getBodyId(conn);
    String queryFWStr = "";
    String toIdFW = "";
    if("0".equals(notify.getToId())) {  //全体部门
      queryFWStr = "select SEQ_ID from PERSON where NOT_LOGIN!='1'";
    }else {
      queryFWStr += " select SEQ_ID from PERSON where NOT_LOGIN!='1'";
      String toId = notify.getToId();
      if(toId != null && !"".equals(toId.trim())){
        String[] toIds = toId.split(",");
        toId = "";
        for(int j = 0 ;j < toIds.length ; j++){
          toId += toIds[j] + ",";
        }
        toId = toId.substring(0, toId.length() - 1);
      }
      if(!"".equals(toId)&&toId!=null) {
        queryFWStr = queryFWStr + " and (DEPT_ID in (" + toId + ")";
      }
      String privId = notify.getPrivId();
      if(privId != null && !"".equals(privId.trim())){
        String[] privIds = privId.split(",");
        privId = "";
        for(int j = 0 ;j < privIds.length ; j++){
          privId +=  privIds[j]  + ",";
        }
        privId = privId.substring(0, privId.length() - 1);
      }
      if(!"".equals(privId)&&privId!=null) {
        queryFWStr = queryFWStr + " or USER_PRIV in ("+ privId +")";
      }
      String userId = notify.getUserId();
      if(userId != null && !"".equals(userId.trim())){
        String[] userIds = userId.split(",");
        userId = "";
        for(int j = 0 ;j < userIds.length ; j++){
          userId +=  userIds[j]  + ",";
        }
        userId = userId.substring(0, userId.length() - 1);
      }
      if(!"".equals(userId)&&userId!=null) {
        queryFWStr = queryFWStr + " or SEQ_ID in ("+ userId + ")";
      }
      queryFWStr = queryFWStr + ")";
    }
    Statement stmtFW = null;
    ResultSet rsFW = null;
    try {
      stmtFW = conn.createStatement();
      rsFW = stmtFW.executeQuery(queryFWStr);
      while(rsFW.next()) {
        toIdFW = toIdFW + rsFW.getString("SEQ_ID") + ",";
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(stmtFW, rsFW, null);
    }
    if("1".equals(notify.getPublish())&&("on".equals(mailRemind)||"1".equals(mailRemind))) {//如果需要短信提醒（发布）
      T9SmsBack smsBack = new T9SmsBack();
      
      String content = "请查看公告通知！\n标题：" + notify.getSubject();
      String remindUrl = "/core/funcs/notify/show/readNotify.jsp?seqId="+bId + "&openFlag=1";         
      if("2".equalsIgnoreCase(notify.getPublish())){
        smsBack.setContent(T9Utility.decodeURL(content));
      }else{
        smsBack.setContent(content);
      }
      smsBack.setFromId(person.getSeqId());
      smsBack.setRemindUrl(remindUrl);
      smsBack.setSmsType("1");
      smsBack.setToId(toIdFW);
      if(!"".equals(toIdFW.trim())&&toIdFW!=null&&toIdFW.contains(",")==true){
        T9SmsUtil.smsBack(conn, smsBack);
      }
    }
    if("2".equals(notify.getPublish())&&("on".equals(mailRemind)||"1".equals(mailRemind))) {//如果需要短信提醒（提交审批）
      T9SmsBack smsBack = new T9SmsBack();
      String content = "请查看公告审批！\n标题：" + notify.getSubject();
      String remindUrl = "/core/funcs/notify/auditing/index.jsp?openFlag=0&openWidth=800&openHeight=600"; 
      smsBack.setContent(content);
      smsBack.setFromId(person.getSeqId());
      smsBack.setRemindUrl(remindUrl);
      smsBack.setSmsType("1");
      smsBack.setToId(notify.getAuditer());
      if(!"".equals(toIdFW.trim())&&toIdFW!=null&&toIdFW.contains(",")==true){
        T9SmsUtil.smsBack(conn, smsBack);
      }
    }
    if(("2".equals(notify.getPublish())||"1".equals(notify.getPublish())) && "on".equalsIgnoreCase(mobileRemind)){//发短信
      String content = "";
      if("2".equals(notify.getPublish())){
        content = "请查看公告审批！\n标题：" + notify.getSubject();
      }else if("1".equals(notify.getPublish())){
        content = "请查看公告审批！\n标题：" + notify.getSubject();
      }       
      T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
      String sms2ToId = notify.getAuditer(); 
      ms2l.remindByMobileSms(conn, sms2ToId==null?"":sms2ToId, person.getSeqId(), content, null);
    }
  }
  public boolean hasAttachDownPriv(Connection conn , int flowId , int runId , int userId  ) throws Exception {
    boolean flag = true;
    String query = "select 1 from FLOW_RUN_PRCS,FLOW_PROCESS WHERE FLOW_RUN_PRCS.RUN_ID='"+runId+"' AND FLOW_PROCESS.FLOW_SEQ_ID='"+flowId+"' AND FLOW_RUN_PRCS.FLOW_PRCS=FLOW_PROCESS.PRCS_ID AND FLOW_RUN_PRCS.USER_ID='"+userId+"' AND ATTACH_PRIV<>'' AND " + T9DBUtility.findNoInSet("4", "ATTACH_PRIV");
    Statement stmt=null;
    ResultSet rs=null;
    try{
      stmt=conn.createStatement();
      rs=stmt.executeQuery(query);
      if  (rs.next()) {
        flag = false;
      }   
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    
    return flag;
  }
  public void saveEmail(Connection conn , T9FlowRun run ,String html , T9Person person ,String toId, String copyToID ,String secretToId  , String flowView , boolean flag) throws Exception {
    
    long size = 0  ;
    String attachmentId = "";
    String attachmentName = "";
    
    if (flowView.indexOf("2") != -1 
        && !T9Utility.isNullorEmpty(run.getAttachmentId())) {
      String[] attIds = T9Utility.null2Empty(run.getAttachmentId()).split(",");
      String[] attNames = T9Utility.null2Empty(run.getAttachmentName()).split("\\*");
      for(int i = 0 ;i < attIds.length ;i ++){
        String tmp = attIds[i];
        if ("".equals(tmp)) {
          continue;
        }
        String attN = attNames[i];
        if (T9WorkFlowUtility.isOffice(attN) && !flag) {
          continue;
        }
        String newId = T9WorkFlowUtility.copyAttachSingle(tmp, attN , "workflow" ,  "email");
        attachmentId += newId + ",";
        attachmentName += attN + "*";
        size += T9WorkFlowUtility.getAttachSize(attN, tmp, "workflow");
      }
    }
    
    if (flowView.indexOf("5") != -1) {
      String query = "select ATTACHMENT_ID,ATTACHMENT_NAME from FLOW_RUN_FEEDBACK where run_id='" + run.getRunId() + "' ";
      Statement stm = null; 
      ResultSet rs = null; 
      try { 
        stm = conn.createStatement(); 
        rs = stm.executeQuery(query); 
        while (rs.next()){ 
          String tmpId = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
          String tmpName =T9Utility.null2Empty( rs.getString("ATTACHMENT_NAME"));
          String newId = T9WorkFlowUtility.copyAttach(tmpId, tmpName , "workflow" ,  "email");
          attachmentId += newId;
          attachmentName += tmpName;
          if (!attachmentId.endsWith(",")) {
            attachmentId += ",";
          }
          if (!attachmentName.endsWith("*")) {
            attachmentName += "*";
          }
          size += T9WorkFlowUtility.getAttachSizes(tmpName, tmpId, "workflow");
        } 
      } catch(Exception ex) { 
        throw ex; 
      } finally { 
        T9DBUtility.close(stm, rs, null); 
      } 
    }
    Date date = new Date();
    T9EmailBody body = new T9EmailBody();
    body.setAttachmentId(attachmentId);
    body.setAttachmentName(attachmentName);
    
    body.setCompressContent(String.valueOf(html.getBytes(T9Const.DEFAULT_CODE)));
    body.setContent(html);
    
    body.setCopyToId(copyToID);
    body.setToId(toId);
    body.setSecretToId(secretToId);
    body.setEnsize(size);
    body.setSubject(run.getRunName());
    body.setSendTime(date);
    body.setFromId(person.getSeqId());
    body.setSendFlag("1");
    body.setSmsRemind("1");
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, body);
    T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
    int bodyId = emul.getBodyId(conn);
    ArrayList<String> ids = new ArrayList<String>();
    if (toId != null && !"".equals(toId)) {
      ids = emul.addArray(ids, toId.split(","));
    }
    if (secretToId != null && !"".equals(secretToId)) {
      ids = emul.addArray(ids, secretToId.split(","));
    }
    if (copyToID != null && !"".equals(copyToID)) {
      ids = emul.addArray(ids, copyToID.split(","));
    }
    for (int i = 0; ids != null && i < ids.size(); i++) {
      String id = ids.get(i);
      if ("".equals(id)) {
        continue;
      }
      T9Email em = new T9Email();
      em.setBodyId(bodyId);
      em.setToId(id);
      em.setDeleteFlag("0");
      em.setReadFlag("0");
      
      if (body.getToId() == null
          || "".equals(body.getToId())) {
        em.setToId("-1");
      }
      orm.saveSingle(conn, em);
      int emailId = emul.getBodyId(conn, "EMAIL");
      String subject = run.getRunName();
      subject = " 请查收我的邮件！主题：" + subject;
      String remindUrl = "/core/funcs/email/inbox/read_email/index.jsp?mailId="
          + emailId + "&seqId=" + bodyId;
      T9SmsBack sb = new T9SmsBack();
      sb.setFromId(person.getSeqId());
      sb.setContent(subject);
      sb.setSmsType("2");
      sb.setRemindUrl(remindUrl);
      sb.setToId(id);
      T9SmsUtil.smsBack(conn, sb);
    }
  }
  public String getUserPriv(Connection conn , String userPrivSeqId) throws Exception {
    Map<String,Integer> query = new HashMap<String,Integer>();
    query.put("SEQ_ID",Integer.parseInt(userPrivSeqId));
    T9ORM t = new T9ORM();
    T9UserPriv up = (T9UserPriv) t.loadObjSingle(conn, T9UserPriv.class,
        query);
    String userPriv = up.getFuncIdStr();
    return userPriv;
  }
  public List<String> getUserPriv(Connection conn , String userPrivSeqId , String otherPriv) throws Exception {
    if (!T9Utility.isNullorEmpty(otherPriv)) {
      if (userPrivSeqId.endsWith(",")) {
        userPrivSeqId += otherPriv;
      } else {
        userPrivSeqId += "," + otherPriv;
      }
    }
    userPrivSeqId = T9WorkFlowUtility.getOutOfTail(userPrivSeqId);
    PreparedStatement ps = null ;
    ResultSet rs = null;
    List<String> list =  new ArrayList();
    String sql = "select func_id_str from USER_PRIV WHERE SEQ_ID IN ("+ userPrivSeqId +")";
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String str = rs.getString(1);
        if (!T9Utility.isNullorEmpty(str) ) {
          list.add(str);
        }
      }
    } catch (Exception e) {
     throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return list;
  }
  public static void main(String args[]) {
    
  }
  public String getNotifyType(Connection conn) throws Exception {
    String getNotifyTypeSql = "select SEQ_ID,CLASS_DESC from CODE_ITEM where CLASS_NO='NOTIFY'";
    Statement typeSt = conn.createStatement();
    ResultSet typeRs = typeSt.executeQuery(getNotifyTypeSql);
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int typeNum = 0 ;
    while(typeRs.next()){
      typeNum ++;
      sb.append("{");
      sb.append("typeId:\"" + typeRs.getInt("SEQ_ID") + "\"");//公告类型的id
      sb.append(",typeDesc:\"" + T9WorkFlowUtility.encodeSpecial(typeRs.getString("CLASS_DESC") )+ "\"");//公告类型的名称

      sb.append("},");
    }
    if(typeNum >0) {
      sb.deleteCharAt(sb.length() - 1); 
      }
    sb.append("]");
    return sb.toString();
  }
  public String getAuditingUser(Connection conn) throws Exception {
    T9ConfigLogic logic2 = new T9ConfigLogic();
    String paraValue2 = T9Utility.null2Empty(logic2.getSysPar("NOTIFY_AUDITING_ALL", conn));
    String[] ids = paraValue2.split(",");
    StringBuffer sb = new StringBuffer();
    T9PersonLogic logic = new T9PersonLogic();
    sb.append("[");
    int count = 0 ;
    for (String id : ids) {
      if (!T9Utility.isNullorEmpty(id)) {
        count++;
        sb.append("{id:" + id);
        sb.append(",name:\"" + T9WorkFlowUtility.encodeSpecial(logic.getUserNameLogic(conn, Integer.parseInt(id))) + "\"},");
      }
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String getFlowAction(Connection conn , String userPrivSeqId , String otherPriv) throws Exception {
    List<String> userPrivs = this.getUserPriv(conn, userPrivSeqId, otherPriv);
    T9ConfigLogic logic = new T9ConfigLogic();
    String paraValue = T9Utility.null2Empty(logic.getSysPar("FLOW_ACTION", conn));
    StringBuffer sb = new StringBuffer("[");
    boolean has = false;
    if (this.hasModulePriv(userPrivs, NOTIFY_MENU_ID) 
        && T9WorkFlowUtility.findId(paraValue , "1")) {
      sb.append("{opt:'notify',title:\"公告通知\",img:'notify.gif'},");
      has = true;
    }
    if (this.hasModulePriv( userPrivs, EMAIL_MENU_ID) 
        && T9WorkFlowUtility.findId(paraValue , "2")) {
      sb.append("{opt:'mail_to',title:\"内部邮件\",img:'email.gif'},");
      has = true;
    }
    if ( this.hasModulePriv( userPrivs, SAVE_MENU_ID)
        && T9WorkFlowUtility.findId(paraValue , "3")) {
      sb.append("{opt:'SaveFile',title:\"转存\",img:'file_folder.gif'},");
      has = true;
    }
    if ( this.hasModulePriv( userPrivs, ROLL_MENU_ID)
        && T9WorkFlowUtility.findId(paraValue , "4")) {
      sb.append("{opt:'roll',title:\"归档\",img:'roll_manage.gif'},");
      has = true;
    }
    if (has) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  /**
   * 取得归档数据
   * @param runId
   * @param conn
   * @return
   */
  public String getFlowData(int runId, Connection conn) throws Exception {
    // TODO Auto-generated method stub
    String query = "select " 
      + " form_seq_Id   " 
      + " from  flow_type ,  flow_run where  " 
      + " flow_id = flow_type.seq_id  " 
      + " and run_id=" + runId;
    Statement stm = null;
    ResultSet rs = null;
    int formId = 0 ;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        formId = rs.getInt("form_seq_Id");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    Map queryItem = new HashMap();
    queryItem.put("FORM_ID", formId);
    T9ORM orm = new T9ORM();
    List<T9FlowFormItem> list = orm.loadListSingle(conn, T9FlowFormItem.class, queryItem);
    T9FlowRunUtility runUtility = new T9FlowRunUtility();
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int count = 0;
    int itemId2 = 0;
    for (T9FlowFormItem item : list) {
        itemId2 = item.getItemId();
        String v = T9WorkFlowUtility.encodeSpecial(item.getTitle());
        String value =  T9WorkFlowUtility.encodeSpecial(runUtility.getData(runId, itemId2, conn));
        sb.append("[").append("\"").append(v).append("\",\"").append(value).append("\"],");
        count++;
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String[] storeFormToRoll(String html, Connection conn) throws Exception {
    String res[] = new String[2];
    T9WorkFlowUtility util = new T9WorkFlowUtility();
    String[] newAttach = util.getNewAttachPath("工作流表单.html", "roll_manage");
    T9FileUtility.storBytes2File(newAttach[1], html.getBytes());
    res[1] = "工作流表单.html";
    res[0] = newAttach[0];
    return res;
  }
  public String[] storeToRoll(int runId, Connection conn) throws Exception {
    // TODO Auto-generated method stub
    String query = "select * from flow_run where run_id=" + runId;
    Statement stm = null;
    ResultSet rs = null;
    String attachmentId = "";
    String attachmentName = "";
    String res[] = new String[2];
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        attachmentId = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
        attachmentName = T9Utility.null2Empty(rs.getString("ATTACHMENT_NAME"));
        
        res[1] = attachmentName;
        res[0] = T9WorkFlowUtility.copyAttach(attachmentId, attachmentName, "workflow", "roll_manage");
      } 
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return res;
  }
}
