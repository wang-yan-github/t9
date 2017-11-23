package t9.project.project.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.*;

import javax.servlet.http.HttpServletRequest;

import org.jdom.input.SAXBuilder;
import org.jdom.*;

import t9.core.codeclass.data.T9CodeItem;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.project.file.data.T9ProjFileSort;
import t9.project.project.data.T9ProjProject;
import t9.project.system.data.T9ProjPriv;
import t9.project.task.data.T9ProjTask;

public class T9ProjectLogic {

  public static final String attachmentFolder = "project";

  /**
   * 浮动菜单文件删除
   * 
   * @param dbConn
   * @param attId
   * @param attName
   * @param contentId
   * @throws Exception
   */
  public boolean delFloatFile(Connection dbConn, String attId, String attName,
      int seqId) throws Exception {
    boolean updateFlag = false;
    if (seqId != 0) {
      T9ORM orm = new T9ORM();
      T9News news = (T9News) orm.loadObjSingle(dbConn, T9News.class, seqId);
      String[] attIdArray = {};
      String[] attNameArray = {};
      String attachmentId = news.getAttachmentId();
      String attachmentName = news.getAttachmentName();
      // T9Out.println("attachmentId"+attachmentId+"--------attachmentName"+attachmentName);
      if (!"".equals(attachmentId.trim()) && attachmentId != null
          && attachmentName != null) {
        attIdArray = attachmentId.trim().split(",");
        attNameArray = attachmentName.trim().split("\\*");
      }
      String attaId = "";
      String attaName = "";

      for (int i = 0; i < attIdArray.length; i++) {
        if (attId.equals(attIdArray[i])) {
          continue;
        }
        attaId += attIdArray[i] + ",";
        attaName += attNameArray[i] + "*";
      }
      // T9Out.println("attaId=="+attaId+"--------attaName=="+attaName);
      news.setAttachmentId(attaId.trim());
      news.setAttachmentName(attaName.trim());
      orm.updateSingle(dbConn, news);
    }
    // 处理文件
    String[] tmp = attId.split("_");
    String path = T9SysProps.getAttachPath() + File.separator
        + attachmentFolder + File.separator + tmp[0] + File.separator + tmp[1]
        + "_" + attName;
    File file = new File(path);
    if (file.exists()) {
      file.delete();
    } else {
      // 兼容老的数据
      String path2 = T9SysProps.getAttachPath() + File.separator
          + attachmentFolder + File.separator + tmp[0] + File.separator
          + tmp[1] + "." + attName;
      File file2 = new File(path2);
      if (file2.exists()) {
        file2.delete();
      }
    }
    updateFlag = true;
    return updateFlag;
  }

  /**
   * 暂时没用处理多文件上传
   * 
   * 附件批量上传页面处理
   * 
   * @return
   * @throws Exception
   */
  public StringBuffer uploadMsrg2Json(T9FileUploadForm fileForm, String pathP)
      throws Exception {
    StringBuffer sb = new StringBuffer();
    Map<String, String> attr = null;
    String attachmentId = "";
    String attachmentName = "";
    try {
      attr = fileUploadLogic(fileForm, pathP);
      Set<String> attrKeys = attr.keySet();
      for (String key : attrKeys) {
        String fileName = attr.get(key);
        attachmentId += key + ",";
        attachmentName += fileName + "*";
      }
      long size = getSize(fileForm);
      sb.append("{");
      sb.append("'attachmentId':").append("\"").append(attachmentId)
          .append("\",");
      sb.append("'attachmentName':").append("\"").append(attachmentName)
          .append("\",");
      sb.append("'size':").append("").append(size);
      sb.append("}");
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return sb;
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
  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm,
      String pathPx) throws Exception {
    Map<String, String> result = new HashMap<String, String>();
    String filePath = pathPx;
    try {
      Calendar cld = Calendar.getInstance();
      int year = cld.get(Calendar.YEAR) % 100;
      int month = cld.get(Calendar.MONTH) + 1;
      String mon = month >= 10 ? month + "" : "0" + month;
      String hard = year + mon;
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName).replaceAll("\\'", "");
        String fileNameV = fileName;
        // T9Out.println(fileName+"*************"+fileNameV);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = T9DiaryUtil.getRondom();
        fileName = rand + "_" + fileName;

        while (T9DiaryUtil.getExist(filePath + File.separator + hard, fileName)) {
          rand = T9DiaryUtil.getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, filePath + File.separator
            + attachmentFolder + File.separator + hard + File.separator
            + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }

  public long getSize(T9FileUploadForm fileForm) throws Exception {
    long result = 0l;
    Iterator<String> iKeys = fileForm.iterateFileFields();
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      result += fileForm.getFileSize(fieldName);
    }
    return result;
  }

  /**
   * 获取审批人员列表
   * 
   * @param dbConn
   * @param person
   * @param privCode
   * @return
   */
  public String getApproveUser(Connection dbConn, T9Person person,
      String privCode) {
    StringBuffer data = new StringBuffer("[");
    T9ORM orm = new T9ORM();
    try {
      String deptId = String.valueOf(person.getDeptId());
      String[] filters = { "1=1 and PRIV_CODE='" + privCode + "'" };
      List<T9ProjPriv> projPriv = orm.loadListSingle(dbConn, T9ProjPriv.class,
          filters);
      if (projPriv != null && projPriv.size() > 0) {
        for (T9ProjPriv priv : projPriv) {
          String privUser = priv.getPrivUser();
          String privDept = priv.getPrivDept();
          String[] approveUsers = privUser.split(",");
          String[] deptList = privDept.split(",");
          for (int j = 0; j < deptList.length; j++) {
            if (deptList[j].equals(deptId) || "0".equals(deptList[j])) {
              for (int n = 0; n < approveUsers.length; n++) {
                data.append("{\"seqId\":" + approveUsers[n] + ",");
                data.append("\"projManager\":\""
                    + queryUsername(dbConn, approveUsers[n]) + "\"},");
              }
            }
          }
        }
        if (data.length() > 3) {
          data = data.deleteCharAt(data.length() - 1);
        }
        data.append("]");
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return data.toString();
  }

  /**
   * 获取部门名称
   * 
   * @param dbConn
   * @param seqIds
   * @return String
   */
  public String queryDeptname(Connection dbConn, String seqIds)
      throws Exception {
    if (T9Utility.isNullorEmpty(seqIds)) {
      return "";
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select dept_name from department where seq_id in (" + seqIds
        + ")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String deptName = rs.getString("dept_name");
        sb.append(deptName + ",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (sb.length() > 1) {
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  /**
   * 获取人员名称
   * 
   * @param dbConn
   * @param seqIds
   * @return String
   */
  public String queryUsername(Connection dbConn, String seqIds)
      throws Exception {
    if (T9Utility.isNullorEmpty(seqIds)) {
      return "";
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select user_name from person where seq_id in (" + seqIds
        + ")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String userName = rs.getString("user_name");
        sb.append(userName + ",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (sb.length() > 1) {
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
/**
 * 保存数据(修改数据)
 * @param dbConn
 * @param fileForm
 * @param person
 * @throws Exception
 */
  public int  addData(Connection dbConn, HttpServletRequest fileForm, T9Person person) throws Exception {
    int maxId=0;
    T9ORM orm = new T9ORM();
    String seqId=fileForm.getParameter("projId");
    String projNum=fileForm.getParameter("projNum");
    String projName=fileForm.getParameter("projName");
    String projStyle=fileForm.getParameter("projStyle");
    String beginDate=fileForm.getParameter("beginDate");
    String endDate=fileForm.getParameter("endDate");
    String description=fileForm.getParameter("description");
    String projOwnerId=fileForm.getParameter("projOwner");
    String projLeaderId=fileForm.getParameter("projLeader");
    String projManager=fileForm.getParameter("projManager");
    String projViwer=fileForm.getParameter("user");
    String dept=fileForm.getParameter("dept");
    String attachmentId=fileForm.getParameter("attachmentId");
    String attachmentName=fileForm.getParameter("attachmentName");
    String updateTime=T9Utility.getCurDateTimeStr();
    try{
      T9ProjProject project=new T9ProjProject();
      if(seqId!=null &&  !"".equals(seqId)){
         project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(seqId));
      }
      project.setAttachmentName(attachmentName);
      project.setAttachmentId(attachmentId);
      project.setProjDept(dept);
      project.setProjDescription(description);
      project.setProjEndTime(T9Utility.parseDate(endDate));
      project.setProjLeader(projLeaderId);
      project.setProjManager(projManager);
      project.setProjName(projName);
      project.setProjNum(projNum);
      project.setProjOwner(projOwnerId);
      project.setProjStartTime(T9Utility.parseDate(beginDate));
      project.setProjViwer(projViwer);
      project.setProjType(Integer.parseInt(projStyle));
      project.setProjUpdateTime(T9Utility.parseDate(updateTime));
      if(seqId==null || "".equals(seqId)){
        project.setProjStatus(0);
        orm.saveSingle(dbConn, project);
        maxId=this.getMax(dbConn);
      }else{
        orm.updateSingle(dbConn, project);
        maxId=project.getSeqId();
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return maxId;
  }
/**
 * 根据ID获取项目基本信息
 * @param dbConn
 * @param seqId
 * @return
 */
  public String getBasicInfo(Connection dbConn, String seqId) {
    T9ORM orm=new T9ORM();
    StringBuffer basicInfo=new StringBuffer();
    try{
      T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(seqId));

      //basicInfo=T9FOM.toJson(project);
      if(project!=null){
        basicInfo.append("{\"seqId\":\""+project.getSeqId()+"\",");
        basicInfo.append("\"projNum\":\""+project.getProjNum()+"\",");
        basicInfo.append("\"projName\":\""+project.getProjName()+"\",");
        basicInfo.append("\"projType\":\""+getItemName(dbConn,String.valueOf(project.getProjType()))+"\",");
        basicInfo.append("\"projStartTime\":\""+project.getProjStartTime()+"\",");
        basicInfo.append("\"projEndTime\":\""+project.getProjEndTime()+"\",");
        basicInfo.append("\"attachmentId\":\""+project.getAttachmentId()+"\",");
        basicInfo.append("\"attachmentName\":\""+project.getAttachmentName()+"\",");
        basicInfo.append("\"projTime\":\""+getWorktime(project.getProjStartTime().toString(),project.getProjEndTime().toString())+"\",");
        basicInfo.append("\"projManager\":\""+queryUsername(dbConn,project.getProjManager())+"\",");
        basicInfo.append("\"projOwner\":\""+queryUsername(dbConn,project.getProjOwner())+"\",");
        basicInfo.append("\"projDept\":\""+queryDeptname(dbConn,project.getProjDept())+"\",");
        basicInfo.append("\"projDescription\":\""+project.getProjDescription()+"\",");
        String userPriv="";
        String projUser="";
        if(project.getProjPriv()==null || "".equals(project.getProjPriv())){
          userPriv="";
          projUser="";
        }else{
          userPriv=getUserPrivName(dbConn,project.getProjPriv());
          projUser=getUsersName(dbConn,project.getProjUser());
        }
        basicInfo.append("\"projUserPriv\":\""+userPriv+"\",");
        basicInfo.append("\"projUser\":\""+projUser+"\",");
        String costType="";
        if(project.getCostType()!=null && !"null".equals(project.getCostType()) && !"".equals(project.getCostType())){
          costType=project.getCostType().substring(0, project.getCostType().length()-1);
        }
        basicInfo.append("\"projCostType\":\""+getItemName(dbConn,costType)+"\",");
        basicInfo.append("\"projCostMoney\":\""+project.getCostMoney()+"\",");
        basicInfo.append("\"approveLog\":\""+project.getApproveLog()+"\",");
        if(basicInfo.length()>3){
          basicInfo.deleteCharAt(basicInfo.length()-1);
        }
        basicInfo.append("}");
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return basicInfo.toString();
  }
  
  
  public String getBasicInfo2(Connection dbConn, String seqId) {
    T9ORM orm=new T9ORM();
    StringBuffer basicInfo=new StringBuffer();
    try{
      T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(seqId));
      basicInfo=T9FOM.toJson(project);
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return basicInfo.toString();
  }
  
  public String getUserPrivName(Connection dbConn,String userPriv){
    String[] privs=userPriv.split("\\|");
    StringBuffer privNames=new StringBuffer();
    try{
    for(int i=0;i<privs.length;i++){
      String privName=getItemName(dbConn,privs[i]);
      privNames.append(privName+"|");
    }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return privNames.toString();
  }
  
  public String getUsersName(Connection dbConn,String userIds){
    String[] users=userIds.split("\\|");
    StringBuffer userNames=new StringBuffer();
    try{
      for(int i=0;i<users.length;i++){
        String userName="";
        if(users[i]!=null&& !"".equals(users[i])){
          userName=queryUsername(dbConn,users[i].substring(0, users[i].length()-1));
        }
        userNames.append(userName+"|");
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return userNames.toString();
  }
  
  
  /**
   * 获取最新一条数据的seqId
   * @param conn
   * @return
   */
  public int getMax(Connection conn){
    int seqId=0;
    PreparedStatement ps=null;
    ResultSet rs=null;
    String sql="";
    try{
      sql="select max(seq_id) from proj_project";
      ps=conn.prepareStatement(sql);
      rs=ps.executeQuery();
      while(rs.next()){
        seqId=rs.getInt(1);
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return seqId;
  }
  
  /**
   * 判断当前项目是否可以提交审批
   * 1.项目成员是否添加
   * 2.项目任务是否分配
   * @param dbConn
   * @param projId
   * @return
   */
  public String ableApprove(Connection dbConn,String projId){
    T9ORM orm = new T9ORM();
    String flag="0";
    String[] filters={"1=1 and PROJ_ID="+projId};
    try{
     List<T9ProjTask> projTask=orm.loadListSingle(dbConn, T9ProjTask.class, filters);
     T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
     if(projTask==null || projTask.size()==0){
       flag="1";              //1表示没有分配项目任务
     }else if(project.getProjUser()==null || "".equals(project.getProjUser())){
       flag="2";               //2表示没有设置项目成员
     }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return flag;
  }
  
  /**
   * 判断是否分配项目成员
   * 2013-4-9
   * @author ny
   * @param dbConn
   * @param projId
   * @return
   */
  public String isHasProjUser(Connection dbConn,String projId){
    T9ORM orm = new T9ORM();
    String flag="0";
    try{
      T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
      if(project.getProjUser()==null || "".equals(project.getProjUser())){
        flag="1";               //1表示没有设置项目成员
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return flag;
  }
  
  /**
   * 提交审批
   * @param dbConn
   * @param projId
   */
  public void submitApprove(Connection dbConn,T9Person person,String projId,String flag){
    T9ORM orm = new T9ORM();
    try{
        T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
        if("1".equals(flag)){
          project.setProjStatus(2);   //2表示当前项目正在进行之中
          project.setApproveLog("免审批");
        }else{
          project.setProjStatus(1);   //1表示当前项目正在审批之中
        //消息通知审批人审批项目
          T9SmsBack smsBack = new T9SmsBack();
          
          String content = "您有未审批项目，请处理";
          String remindUrl = "/project/approve/noApproveList.jsp";         
          smsBack.setContent(content);
          smsBack.setFromId(person.getSeqId());
          smsBack.setRemindUrl(remindUrl);
          smsBack.setSmsType("88");
          smsBack.setToId(project.getProjManager());
          T9SmsUtil.smsBack(dbConn, smsBack);
        }
        orm.updateSingle(dbConn, project);
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  
  /**
   * 获取经费种类和值
   * @param con
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getCostTypeAndValue(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    StringBuffer sb = new StringBuffer("[");
    T9ProjProject proj = (T9ProjProject) t9orm.loadObjSingle(con, T9ProjProject.class, seqId);
    sb.append("{");
    sb.append("\"costType\":\""+proj.getCostType()+"\",");
    sb.append("\"codeValue\":\""+proj.getCostMoney()+"\"");
    sb.append("},");
    String[] filters={" 1=1 and class_no = 'PROJ_COST_TYPE'"};
    List<T9CodeItem> list = t9orm.loadListSingle(con, T9CodeItem.class, filters);
    if (list != null && list.size() > 0) {
      for (T9CodeItem codeItem : list) {
          sb.append("{");
          sb.append("\"codeSeqId\":\""+codeItem.getSeqId()+"\",");
          sb.append("\"codeName\":\""+codeItem.getClassDesc()+"\"},");
      }
     
  }
    if(sb.length()>3){
      sb=sb.deleteCharAt(sb.length()-1);
  }
  sb.append("]");
    return sb.toString();
    
  }
  /**
   * 修改对象，更新
   * @param con
   * @param project
   * @throws Exception
   */
  public void updateProj(Connection con,T9ProjProject project) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.updateSingle(con, project);
  }
  /**
   * 获取对象byId
   * @param con
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9ProjProject getProj(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    return (T9ProjProject) t9orm.loadObjSingle(con, T9ProjProject.class, seqId);
  }
  
  public String getProjLeftTree(Connection dbConn, Map map, T9Person person, HttpServletRequest request) throws Exception {
    String sql = "";
    String conditionStr = "";
    String projType = request.getParameter("projType");
    String projStatus = request.getParameter("projStatus");
    String startDate = request.getParameter("startDate");
    String endDate = request.getParameter("endDate");

    try {
      if (!T9Utility.isNullorEmpty(projType)) {
        conditionStr = " and proj_type like '%" + T9DBUtility.escapeLike(projType) + "%'";
      }
      if (!T9Utility.isNullorEmpty(projStatus)) {
        conditionStr += " and proj_status like '%" + T9DBUtility.escapeLike(projStatus) + "%'";
      }
      if (!T9Utility.isNullorEmpty(startDate)) {
        conditionStr += " and " + T9DBUtility.getDateFilter("proj_start_time", startDate, ">=");
      }
      if (!T9Utility.isNullorEmpty(endDate)) {
        conditionStr += " and " + T9DBUtility.getDateFilter("proj_end_time", endDate + " 23:59:59", "<=");
      }
      sql = "select seq_id,proj_name,proj_status  from proj_project where 1=1 " + conditionStr + "order by proj_start_time desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  public T9CodeItem getCodeItem(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    return (T9CodeItem) t9orm.loadObjSingle(con, T9CodeItem.class, seqId);
  }
    
  

  /**
   * 获取两个日期之间的天数
   * @param starttime
   * @param endtime
   * @return
   */

 public long getWorktime(String starttime, String endtime) {
    // 设置时间格式
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // 开始日期 
    Date dateFrom=null;
    Date dateTo = null;
    try {
      dateFrom = dateFormat.parse(starttime);
      dateTo = dateFormat.parse(endtime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    long days = 0;
    long quot=dateTo.getTime()-dateFrom.getTime();
    days=quot/1000/60/60/24;
    return days;
  }
 
 
 /**
  * 删除项目
  * @param dbConn
  * @param projId
  * @return
  */
 public void deleteProj(Connection dbConn,String projId){
   T9ORM orm=new T9ORM();
   PreparedStatement ps1=null;
   PreparedStatement ps2=null;
   String sql1="";
   String sql2="";
   try{
     T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
     sql1="delete from proj_task where proj_id="+projId;
     sql2="delete from proj_file_sort where proj_id="+projId;
     ps1=dbConn.prepareStatement(sql1);
     ps2=dbConn.prepareStatement(sql2);
     ps1.execute();
     ps2.execute();
     orm.deleteSingle(dbConn, project);
   }catch(Exception ex){
     ex.printStackTrace();
   }
 }
 
 

  
  
  /**
   * 根据costId 查询costNmae
   * @param dbConn
   * @param costStyle
   * @return
   * @throws Exception
   */
  public String getItemName(Connection dbConn,String costStyleId) throws Exception{
    if (T9Utility.isNullorEmpty(costStyleId)) {
      return "";
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select class_desc from code_item where seq_id in (" + costStyleId
        + ")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String projTypeName = rs.getString("class_desc");
        sb.append(projTypeName + ",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (sb.length() > 1) {
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

 
 /**
  * 根据条件获取项目列表
  * @param dbConn
  * @param request
  * @param person
  * @return
  * @throws Exception
  */
 public String getProjectList(Connection dbConn, Map map, T9Person person,HttpServletRequest request) throws Exception {
   String projNum=request.getParameter("projNum");
   String projName=request.getParameter("projName");
   String projStartTime=request.getParameter("beginDate");
   String projEndTime=request.getParameter("endDate");
   String projOwner=request.getParameter("projOwner");
   String projStyle=request.getParameter("projStyle");
   String projStatus=request.getParameter("projStatus");
   String range=request.getParameter("range");
   try {
     String whereStr="";
     if (!T9Utility.isNullorEmpty(projStyle) && !"-1".equals(projStyle)){
       projNum = projNum.trim();
       whereStr += " and p1.proj_type like '%" + T9DBUtility.escapeLike(projStyle) + "%'";
     }
     if (!T9Utility.isNullorEmpty(projStatus)  && !"-1".equals(projStatus) ){
       projNum = projNum.trim();
       whereStr += " and p1.proj_status like '%" + T9DBUtility.escapeLike(projStatus) + "%'";
     }
     if (!T9Utility.isNullorEmpty(projNum)){
       projNum = projNum.trim();
       whereStr += " and p1.proj_num like '%" + T9DBUtility.escapeLike(projNum) + "%'";
     }
     if (!T9Utility.isNullorEmpty(projName)){
       projName = projName.trim();
       whereStr += " and p1.proj_name like '%" + T9DBUtility.escapeLike(projName) + "%'";
     }
     if (!T9Utility.isNullorEmpty(projOwner)){
       whereStr += " and p1.proj_owner ='" + T9DBUtility.escapeLike(projOwner) + "'";
     }
     if (!T9Utility.isNullorEmpty(projStartTime)) {
       whereStr += " and " + T9DBUtility.getDateFilter("p1.proj_start_time", projStartTime, ">=");
     }
     if (!T9Utility.isNullorEmpty(projEndTime)) {
       whereStr += " and " + T9DBUtility.getDateFilter("p1.proj_end_time", projEndTime + " 23:59:59", "<=");
     }
     if("1".equals(range)){
       whereStr += " and p1.proj_leader ='" + T9DBUtility.escapeLike(String.valueOf(person.getSeqId())) + "'";
     }
     if("2".equals(range)){
       whereStr += " and t.task_user = '"+ T9DBUtility.escapeLike(String.valueOf(person.getSeqId())) + "'";
     }
/*     if("0".equals(range)){
       whereStr += " and t.task_user = '"+ T9DBUtility.escapeLike(String.valueOf(person.getSeqId())) + "' or p1.proj_leader ='"+ T9DBUtility.escapeLike(String.valueOf(person.getSeqId())) + "'";
     }*/
     String sql = "select distinct p1.seq_id,p1.proj_num,p1.proj_name,p2.user_name,p1.proj_start_time,p1.proj_end_time,p1.proj_act_end_time,p1.proj_status "
         +"from proj_project p1 left join proj_task t on p1.seq_id=t.proj_id,person p2 where p1.proj_owner=p2.seq_id and p1.proj_owner="+person.getSeqId()+whereStr +" order by p1.seq_id desc";
     T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
     T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
     return pageDataList.toJson();
   } catch (Exception e) {
     throw e;
   }
 }
 
 /**
  * 结束项目
  * @param dbConn
  * @param projId
  * @return
  */
 public void endProj(Connection dbConn,String projId){
   T9ORM orm=new T9ORM();
   try{
     String endTime=T9Utility.getCurDateTimeStr();
     T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
     project.setProjStatus(3);
     project.setProjActEndTime(T9Utility.parseDate(endTime));
     orm.updateSingle(dbConn, project);
   }catch(Exception ex){
     ex.printStackTrace();
   }
   
 }
 /**
  * 恢复项目
  * @param dbConn
  * @param projId
  * @return
  */
 public void recoveryProj(Connection dbConn,String projId){
   T9ORM orm=new T9ORM();
   try{
     T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
     project.setProjStatus(2);
     project.setProjActEndTime(null);
     orm.updateSingle(dbConn, project);
   }catch(Exception ex){
     ex.printStackTrace();
   }
 }
/** 
 * 获取待审批项目列表
 * @param dbConn
 * @param map
 * @param person
 * @return
 * @throws Exception
 */
public String getNoApproveList(Connection dbConn, Map map, T9Person person) throws Exception {
  try{
    String sql = "select p1.seq_id,p1.proj_num,p1.proj_name,p2.user_name,p1.proj_start_time,p1.proj_end_time,p1.proj_status "
        +"from proj_project p1 left join person p2 on p1.proj_owner=p2.seq_id where p1.proj_status=1  and p1.proj_manager='"+person.getSeqId()+"' order by p1.seq_id desc";
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  } catch (Exception e) {
    throw e;
  }
}

/**
 *获取审批记录
 * @param dbConn
 * @param map
 * @param person
 * @return
 * @throws Exception
 */
public String getApproveList(Connection dbConn, Map map, T9Person person) throws Exception {
  try{
    String sql = "select p1.seq_id,p1.proj_num,p1.proj_name,p2.user_name,p1.proj_start_time,p1.proj_end_time,p1.proj_act_end_time "
        +"from proj_project p1 left join person p2 on p1.proj_owner=p2.seq_id where p1.proj_status!=1 and p1.proj_status!=0  and p1.proj_manager='"+person.getSeqId()+"' order by p1.seq_id desc";
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  } catch (Exception e) {
    throw e;
  }
}

/**
 * 更新审批记录(事务提醒）
 * 2013-3-25
 * @author ny
 * @param dbConn
 * @param request
 * @param projId
 * @param person
 */
public void subApprove(Connection dbConn, HttpServletRequest request,
    String projId, T9Person person) {
    T9ORM orm = new T9ORM();
    String pass=request.getParameter("pass");
    String result="";
    if(pass=="1" || "1".equals(pass)){
      result="<font color=green>通过 </font> by &nbsp;&nbsp;"+person.getUserName()+"("+T9Utility.getCurDateTimeStr()+"):"+request.getParameter("content");
    }else{
      result="<font color=red>驳回 </font> by"+person.getUserName()+"("+T9Utility.getCurDateTimeStr()+"):"+request.getParameter("content");
    }
    try{
      T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
      String oldApprove=project.getApproveLog();
      if(oldApprove==null || "".equals(oldApprove)){
        oldApprove="";
      }
      String newApprove=oldApprove+result+"|*|";
      project.setApproveLog(newApprove);
      if(pass=="1" || "1".equals(pass)){
        project.setProjStatus(2);
        //审批通过，通知立项者
        T9SmsBack smsBack = new T9SmsBack();
        String content = "您的项目已通过，请处理";
        String remindUrl = "/project/proj/projectList.jsp";         
        smsBack.setContent(content);
        smsBack.setFromId(person.getSeqId());
        smsBack.setRemindUrl(remindUrl);
        smsBack.setSmsType("88");
        smsBack.setToId(project.getProjOwner());
        T9SmsUtil.smsBack(dbConn, smsBack);
      }else{
        project.setProjStatus(0);
        //审批没通过，通知立项者 处理
        T9SmsBack smsBack = new T9SmsBack();
        String content = "您的项目未通过，请处理";
        String remindUrl = "/project/proj/projectList.jsp";	         
        smsBack.setContent(content);
        smsBack.setFromId(person.getSeqId());
        smsBack.setRemindUrl(remindUrl);
        smsBack.setSmsType("88");
        smsBack.setToId(project.getProjOwner());
        T9SmsUtil.smsBack(dbConn, smsBack);
      }
      orm.updateSingle(dbConn, project);
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  

/**
 * 判断是否有权限立项
 * 2013-3-25
 * @author ny
 * @param dbConn
 * @param person
 * @return
 */
  public String isHasNewPriv(Connection dbConn,T9Person person){
    String flag="0";
    T9ORM orm=new T9ORM();
    try{
      String curUserId=String.valueOf(person.getSeqId());
      String curDeptId=String.valueOf(person.getDeptId());
      String curRoleId=person.getUserPriv();
      Map filters=new HashMap();
      filters.put("priv_code", "NEW");
      T9ProjPriv newPriv=(T9ProjPriv)orm.loadObjSingle(dbConn, T9ProjPriv.class, filters);
      //判断是否有立项权限
      if(newPriv==null){
        flag="0";
      }else{
        String userId=newPriv.getPrivUser();
        String deptId=newPriv.getPrivDept();
        String roleId=newPriv.getPrivRole();
        if(cmpStr(userId,curUserId) || cmpStr(deptId,curDeptId) || cmpStr(roleId,curRoleId) || "0".equals(deptId)){
          flag="1";
        }else{
          flag="0";
        }
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return flag;
  }   
  /**
   * 判断id 是否存在于ids中
   * 2013-3-25
   * @author ny
   * @param ids
   * @param id
   * @return
   */
  public boolean cmpStr(String ids,String id){
    boolean flag=false;
    String[] idStrs=ids.split(",");
    for(int i=0;i<idStrs.length;i++){
      if(id.equals(idStrs[i])){
        flag=true;
        continue;
      }
    }
    return flag;
  }

  /**
   * 判断是否免签
   * 2013-3-25
   * @author ny
   * @param dbConn
   * @param person
   * @return
   */
  public String isApprove(Connection dbConn, T9Person person) {
    String flag="0";
    T9ORM orm=new T9ORM();
    try{
      String curUserId=String.valueOf(person.getSeqId());
      String curDeptId=String.valueOf(person.getDeptId());
      String curRoleId=person.getUserPriv();
      Map filters=new HashMap();
      filters.put("priv_code", "NOAPPROVE");
      T9ProjPriv noApprove=(T9ProjPriv)orm.loadObjSingle(dbConn, T9ProjPriv.class, filters);
      //判断是否有立项权限
      if(noApprove==null){
        flag="0";
      }else{
        String userId=noApprove.getPrivUser();
        String deptId=noApprove.getPrivDept();
        String roleId=noApprove.getPrivRole();
        if(cmpStr(userId,curUserId) || cmpStr(deptId,curDeptId) || cmpStr(roleId,curRoleId) || "0".equals(deptId)){
          flag="1";
        }else{
          flag="0";
        }
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return flag;
  }
  
  
  /**
   * 项目导出为模板
   * 2013-3-25
   * @author ny
   * @param dbConn
   * @param projId
   * @param modelName
   */
  public void exportProj(Connection dbConn,String projId,String modelName){
    T9ORM orm = new T9ORM();
    try{
      String outXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
      outXml += "<Project>\r\n";
      //项目基本信息
      T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
      outXml+="<BaseInfo>\r\n";
      outXml+="<PROJ_NAME>"+project.getProjName()+"</PROJ_NAME>\r\n";
      outXml+="<PROJ_NUM>"+project.getProjNum()+"</PROJ_NUM>\r\n";
      outXml+="<PROJ_DESCRIPTION>"+project.getProjDescription()+"</PROJ_DESCRIPTION>\r\n";
      outXml+="<PROJ_TYPE>"+project.getProjType()+"</PROJ_TYPE>\r\n";
      outXml+="<PROJ_DEPT>"+project.getProjDept()+"</PROJ_DEPT>\r\n";
      outXml+="<PROJ_START_TIME>"+project.getProjStartTime()+"</PROJ_START_TIME>\r\n";
      outXml+="<PROJ_END_TIME>"+project.getProjEndTime()+"</PROJ_END_TIME>\r\n";
      outXml+="<PROJ_OWNER>"+project.getProjOwner()+"</PROJ_OWNER>\r\n";
      outXml+="<PROJ_LEADER>"+project.getProjLeader()+"</PROJ_LEADER>\r\n";
      outXml+="<PROJ_VIWER>"+project.getProjViwer()+"</PROJ_VIWER>\r\n";
      outXml+="<PROJ_USER>"+project.getProjUser()+"</PROJ_USER>\r\n";
      outXml+="<PROJ_PRIV>"+project.getProjPriv()+"</PROJ_PRIV>\r\n";
      outXml+="<PROJ_MANAGER>"+project.getProjManager()+"</PROJ_MANAGER>\r\n";
      outXml+="<COST_TYPE>"+project.getCostType()+"</COST_TYPE>\r\n";
      outXml+="<COST_MONEY>"+project.getCostMoney()+"</COST_MONEY>\r\n";
      //outXml+="<PROJ_COMMENT>"+project.getProjComment().toString()+"</PROJ_COMMENT>\r\n";
      outXml+="<ATTACHMENT_ID>"+project.getAttachmentId()+"</ATTACHMENT_ID>\r\n";
      outXml+="<ATTACHMENT_NAME>"+project.getAttachmentName()+"</ATTACHMENT_NAME>\r\n";
      outXml+="</BaseInfo>\r\n";
      //项目任务
      String[] filters={"1=1 and proj_id="+Integer.parseInt(projId)};
      List<T9ProjTask> taskList=orm.loadListSingle(dbConn, T9ProjTask.class, filters);
      if(taskList!=null && taskList.size()>0){
        for(T9ProjTask task:taskList){
          outXml+="<Task>\r\n";
          outXml+="<TASK_NO>"+task.getTaskNo()+"</TASK_NO>\r\n";
          outXml+="<TASK_NAME>"+task.getTaskName()+"</TASK_NAME>\r\n";
          outXml+="<TASK_DESCRIPTION>"+task.getTaskDescription()+"</TASK_DESCRIPTION>\r\n";
          outXml+="<TASK_USER>"+task.getTaskUser()+"</TASK_USER>\r\n";
          outXml+="<TASK_START_TIME>"+task.getTaskStartTime()+"</TASK_START_TIME>\r\n";
          outXml+="<TASK_END_TIME>"+task.getTaskEndTime()+"</TASK_END_TIME>\r\n";
          outXml+="<TASK_LEVEL>"+task.getTaskLevel()+"</TASK_LEVEL>\r\n";
          outXml+="<REMARK>"+task.getRemark()+"</REMARK>\r\n";
          outXml+="<PARENT_TASK>"+task.getPreTask()+"</PARENT_TASK>\r\n";
          outXml+="</Task>\r\n";
        }
      }
      //项目文档
      List<T9ProjFileSort> fileSortList=orm.loadListSingle(dbConn, T9ProjFileSort.class, filters);
      if(fileSortList!=null && fileSortList.size()>0){
        for(T9ProjFileSort file :fileSortList){
          outXml+="<FileSort>\r\n";
          outXml+="<SORT_PARENT>"+file.getSortParent()+"</SORT_PARENT>\r\n";
          outXml+="<SORT_NO>"+file.getSortNo()+"</SORT_NO>\r\n";
          outXml+="<SORT_NAME>"+file.getSortName()+"</SORT_NAME>\r\n";
          outXml+="<SORT_TYPE>"+file.getSortType()+"</SORT_TYPE>\r\n";
          outXml+="<VIEW_USER>"+file.getViewUser()+"</VIEW_USER>\r\n";
          outXml+="<NEW_USER>"+file.getNewUser()+"</NEW_USER>\r\n";
          outXml+="<MANAGE_USER>"+file.getManageUser()+"</MANAGE_USER>\r\n";
          outXml+="<MODIFY_USER>"+file.getModifyUser()+"</MODIFY_USER>\r\n";
          outXml+="</FileSort>\r\n";
        }
      }
      outXml+="</Project>\r\n";
      String fileName="";
      if(modelName!=null && !"".equals(modelName)){
        fileName=modelName+".xml";
      }else{
        fileName=project.getProjName()+".xml";
      }
      String  path="proj_model";
      String filePath=T9SysProps.getAttachPath()+File.separator+attachmentFolder+File.separator+path;
      File modelFile=new File(filePath);
      if(!modelFile.exists()){
        modelFile.mkdirs();
      }

      
      writeUTFFile(filePath+File.separator+fileName, outXml);
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  /**
   * 生成UTF-8文件.
   * 如果文件内容中没有中文内容，则生成的文件为ANSI编码格式；
   * 如果文件内容中有中文内容，则生成的文件为UTF-8编码格式。
   * @param fileName 待生成的文件名（含完整路径）
   * @param fileBody 文件内容
   * @return
   */
  public static boolean writeUTFFile(String fileName,String fileBody)
  {
      FileOutputStream fos = null;
      OutputStreamWriter osw = null;
      try {
          fos = new FileOutputStream(fileName);
          osw = new OutputStreamWriter(fos, "UTF-8");
          osw.write(fileBody);
          return true;
      } catch (Exception e) {
          e.printStackTrace();
          return false;
      }finally{
          if(osw!=null){
              try {
                  osw.close();
              } catch (IOException e1) {
                  e1.printStackTrace();
              }
          }
          if(fos!=null){
              try {
                  fos.close();
              } catch (IOException e1) {
                  e1.printStackTrace();
              }
          }
      }
  }
  
  /**
   * 导入项目模块，快速建立新项目
   * 2013-3-25
   * @author ny
   * @param dbConn
   * @param modelId
   * @return
   */
  public String importProj(Connection dbConn,String modelName){
    int curId=0;
    T9ORM orm = new T9ORM();
    SAXBuilder builder=new SAXBuilder();    //解析器
    try{
      String filePath=T9SysProps.getAttachPath()+File.separator+attachmentFolder+File.separator+"proj_model";
      File modelFile=new File(filePath+File.separator+modelName+".xml");
      
      Document docment=builder.build(modelFile);
      Element root=docment.getRootElement();  //根节点     
      //从Xml文件中获取项目基本信息
      List<Element> infos=root.getChildren("BaseInfo");
      for(Element element:infos){
        T9ProjProject project=new T9ProjProject();
        project.setProjName(element.getChildTextTrim("PROJ_NAME"));
        project.setProjNum(element.getChildTextTrim("PROJ_NUM"));
        project.setProjDescription(element.getChildTextTrim("PROJ_DESCRIPTION"));
        project.setProjDept(element.getChildTextTrim("PROJ_DEPT"));
        if(element.getChildTextTrim("PROJ_TYPE")!=null){
          project.setProjType(Integer.parseInt(element.getChildTextTrim("PROJ_TYPE")));
        }
        project.setProjStartTime(T9Utility.parseDate(element.getChildTextTrim("PROJ_START_TIME")));
        project.setProjEndTime(T9Utility.parseDate(element.getChildTextTrim("PROJ_END_TIME")));
        project.setProjViwer(element.getChildTextTrim("PROJ_VIWER"));
        project.setProjUser(element.getChildTextTrim("PROJ_USER"));
        project.setProjOwner(element.getChildTextTrim("PROJ_OWNER"));
        project.setProjLeader(element.getChildTextTrim("PROJ_LEADER"));
        project.setProjPriv(element.getChildTextTrim("PROJ_PRIV"));
        project.setProjManager(element.getChildTextTrim("PROJ_MANAGER"));
        project.setCostType(element.getChildTextTrim("COST_TYPE"));
        project.setCostMoney(element.getChildTextTrim("COST_MONEY"));
        project.setAttachmentId(element.getChildTextTrim("ATTACHMENT_ID"));
        project.setAttachmentName(element.getChildTextTrim("ATTACHMENT_NAME"));
        //保存信息，返回当前项目Id（新建项目）
        orm.saveSingle(dbConn, project);
        curId=this.getMax(dbConn);
        }
      //获取项目任务信息
      List<Element> tasks=root.getChildren("Task");
      for(Element task:tasks){
        T9ProjTask projTask=new T9ProjTask();
        projTask.setProjId(curId);
        projTask.setTaskName(task.getChildTextTrim("TASK_NAME"));
        projTask.setTaskNo(task.getChildTextTrim("TASK_NO"));
        projTask.setTaskDescription(task.getChildTextTrim("TASK_DESCRIPTION"));
        projTask.setTaskUser(task.getChildTextTrim("TASK_USER"));
        projTask.setTaskStartTime(T9Utility.parseDate(task.getChildTextTrim("TASK_START_TIME")));
        projTask.setTaskEndTime(T9Utility.parseDate(task.getChildTextTrim("TASK_END_TIME")));
        projTask.setTaskLevel(task.getChildTextTrim("TASK_LEVEL"));
        projTask.setRemark(task.getChildTextTrim("REMARK"));
        projTask.setParentTask(Integer.parseInt(task.getChildTextTrim("PARENT_TASK")));
        orm.saveSingle(dbConn, projTask);
      }
      
      //获取项目文档信息
      List<Element> fileSort=root.getChildren("FileSort");
      for(Element file:fileSort){
        T9ProjFileSort files=new T9ProjFileSort();
        files.setProjId(curId);
        files.setSortParent(Integer.parseInt(file.getChildTextTrim("SORT_PARENT")));
        files.setSortNo(file.getChildTextTrim("SORT_NO"));
        files.setSortName(file.getChildTextTrim("SORT_NAME"));
        files.setSortType(file.getChildTextTrim("SORT_TYPE"));
        files.setViewUser(file.getChildTextTrim("VIEW_USER"));
        files.setNewUser(file.getChildTextTrim("NEW_USER"));
        files.setManageUser(file.getChildTextTrim("MANAGE_USER"));
        files.setModifyUser(file.getChildTextTrim("MODIFY_USER"));
        orm.saveSingle(dbConn, files);
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return String.valueOf(curId);
  }
  
  
  /**
   * 获取所有的项目模板
   * 2013-3-25
   * @author ny
   * @param dbConn
   * @return
   */
  public String getModelList(Connection dbConn){
    StringBuffer data=new StringBuffer("[");
    String filePath=T9SysProps.getAttachPath()+File.separator+attachmentFolder+File.separator+"proj_model";
    File forder=new File(filePath);
    File[] files=forder.listFiles();
    if(files!=null){
      for(int i=0;i<files.length;i++){
        data.append("{ \"fileName\":\""+files[i].getName().substring(0, files[i].getName().length()-4)+"\"},");
      }
    }
    if(data.length()>3){
      data=data.deleteCharAt(data.length()-1);
    }
    data.append("]");
    return data.toString();
  }

  
  /**
   * 删除模板
   * 2013-3-26
   * @author ny
   * @param dbConn
   * @param fileNames
   */
  public void deleteModel(Connection dbConn, String fileNames) {
    String[] files=fileNames.split("\\|");
    if(files!=null && files.length>0){
      for(int i=0;i<files.length;i++){
        String filePath=T9SysProps.getAttachPath()+File.separator+attachmentFolder+File.separator+"proj_model"+File.separator+files[i]+".xml";
        File deleFile=new File(filePath);
        deleFile.delete();
      }
    }
  }
  
  
  /**
   * 判断当前项目用户角色是否存在，存在返回角色id,否则返回空
   * 2013-3-28
   * @author ny
   * @param dbConn
   * @param privId
   * @param projId
   * @return
   */
  public String[] ruturnPrivId(Connection dbConn,String privId,String projId){
    T9ORM orm = new T9ORM();
    String[] id = new String[2];
    try{
      T9ProjProject project =(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
      String projPriv=project.getProjPriv();
      if(projPriv!=null && !"".equals(projPriv)){
        String[] privs=projPriv.split("\\|");
        for(int i=0;i<privs.length;i++){
          if(privId.equals(privs[i])){
            id[0]=privs[i];
            id[1]=String.valueOf(i);
            continue;
          }
        }
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return id;
  }
  
  /**
   * 判断当前id 是否已经存在
   * 2013-3-28
   * @author ny
   * @param users
   * @param id
   * @return
   */
  public boolean isHasUserId(String users,String id){
    boolean flag=false;
    String[] user=users.split(",");
    for(int i=0;i<user.length;i++){
      if(id.equals(user[i])){
        flag=true;
        continue;
      }
    }
    return flag;
  }
  
  
  
  /**
   * 删除项目成员
   * 2013-3-28
   * @author ny
   * @param dbConn
   * @param privId
   * @param projId
   */
  public void delProjPrivUser(Connection dbConn,String privId,String projId){
    T9ORM orm = new T9ORM();
    try{
      T9ProjProject project =(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, Integer.parseInt(projId));
      String priv=project.getProjPriv();
      String[] privs=priv.split("\\|");
      String user=project.getProjUser();
      String[] users=user.split("\\|");
      int delId=0;
      for(int i=0;i<privs.length;i++){
        if(privId.equals(privs[i])){
          delId=i;
          continue;
        }
      }
      String newPriv="";
      String newUser="";
      if(delId!=0){
        for(int m=0;m<privs.length;m++){
          if(delId==m || "".equals(privs[m])){
            continue;
          }
          newPriv+="|"+privs[m];
          
        }
        for(int n=0;n<users.length;n++){
          if(delId==n || "".equals(users[n])){
            continue;
          }
          newUser+="|"+users[n];
        }
        project.setProjPriv(newPriv);
        project.setProjUser(newUser);
        orm.updateSingle(dbConn, project);
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  /**
   * 根据id判断是否存在数据
   * 2013-4-9
   * @author ny
   * @param dbConn
   * @param costId
   * @return
   */
  public String checkIsExist(Connection dbConn, String costId) {
    T9ORM orm = new T9ORM();
    String flag="0";
    try{
      if(costId==null || "".equals(costId) || "null".equals(costId)){
        costId="0";
      }
      T9CodeItem cost=(T9CodeItem)orm.loadObjSingle(dbConn, T9CodeItem.class, Integer.parseInt(costId));
      if(cost==null){
        flag="0";
      }else{
        flag="1";
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return flag;
  }
}