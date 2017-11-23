package t9.project.bug.logic;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.project.bug.data.T9ProjBug;
import t9.project.project.data.T9ProjProject;




public class T9ProjBugLogic{

  /**
   * 根据项目id 获取当前项目的所有问题
   * @param dbConn
   * @param projId
   * @return
   */
  public String getBugInfoList(Connection dbConn, String projId) {
    T9ORM orm = new T9ORM();
    StringBuffer data=new StringBuffer("[");
    try{
      String[] filters={" 1=1 and PROJ_ID="+projId};
      List<T9ProjBug> bugList=orm.loadListSingle(dbConn, T9ProjBug.class, filters);
      if(bugList!=null && bugList.size()>0){
        for(T9ProjBug bug:bugList){
          data.append("{\"seqId\":"+bug.getSeqId()+",");
          data.append("\"projId\":"+bug.getProjId()+",");
          data.append("\"bugName\":\""+bug.getBugName()+"\",");
          data.append("\"beginUser\":\""+bug.getBeginUser()+"\",");
          data.append("\"dealUser\":\""+bug.getDealUser()+"\",");
          data.append("\"result\":\""+bug.getResult()+"\",");
          data.append("\"deadLine\":\""+bug.getDeadLine()+"\",");
          data.append("\"level\":\""+bug.getLevel()+"\",");
          data.append("\"status\":\""+bug.getStatus()+"\"},");
        }
      }
      if(data.length()>3){
        data=data.deleteCharAt(data.length()-1);
      }
      data.append("]");
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return data.toString();
  }
  /**
   * 根据项目id 获取当前项目的所有问题
   * @param dbConn
   * @param projId
   * @return
   */
  public String getBugInfoListByUserId(Connection dbConn, T9Person person,String flag) {
    T9ORM orm = new T9ORM();
    StringBuffer data=new StringBuffer("[");
    try{
      String[] filters={" 1=1 and deal_user="+person.getSeqId()+" and status ="+Integer.parseInt(flag)+" order by level desc "};
      List<T9ProjBug> bugList=orm.loadListSingle(dbConn, T9ProjBug.class, filters);
      if(bugList!=null && bugList.size()>0){
        for(T9ProjBug bug:bugList){
          data.append("{\"seqId\":"+bug.getSeqId()+",");
          data.append("\"projId\":"+bug.getProjId()+",");
          data.append("\"projName\":\""+getProjName(dbConn,bug.getProjId())+"\",");
          data.append("\"bugName\":\""+bug.getBugName()+"\",");
          data.append("\"beginUser\":\""+bug.getBeginUser()+"\",");
          data.append("\"dealUser\":\""+bug.getDealUser()+"\",");
          data.append("\"result\":\""+bug.getResult()+"\",");
          data.append("\"deadLine\":\""+bug.getDeadLine()+"\",");
          data.append("\"level\":\""+bug.getLevel()+"\",");
          data.append("\"status\":\""+bug.getStatus()+"\"},");
        }
      }
      if(data.length()>3){
        data=data.deleteCharAt(data.length()-1);
      }
      data.append("]");
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return data.toString();
  }
/**
 * 根据bugId 查询问具体信息
 * @param dbConn
 * @param bugId
 * @return
 */
  public String getBugInfo(Connection dbConn, String bugId) {
    T9ORM orm = new T9ORM();
    StringBuffer data=new StringBuffer();
    try{
      T9ProjBug projBug=(T9ProjBug)orm.loadObjSingle(dbConn, T9ProjBug.class, Integer.parseInt(bugId));
      if(projBug!=null){
        data=T9FOM.toJson(projBug);
      }
    }catch(Exception ex){
     ex.printStackTrace(); 
    }
    return data.toString();
  }
  
  /**
   * 保存项目问题
   * @param dbConn
   * @param request
   * @param person
   */
public void addBugInfo(Connection dbConn, HttpServletRequest request, T9Person person) {
  T9ORM orm = new T9ORM();
  String flag=request.getParameter("flag");
  String bugId=request.getParameter("bugId");
  String bugName=request.getParameter("bugName");
  String taskId=request.getParameter("taskId");
  String projId=request.getParameter("projId");
  String level=request.getParameter("level");
  String bugDesc=request.getParameter("bugDesc");
  String attachmentId=request.getParameter("attachmentId");
  String attachmentName=request.getParameter("attachmentName");
  String deadLine=request.getParameter("deadLine");
  String dealUser=request.getParameter("dealUser");
  String sysRemind=request.getParameter("sysRemind");
  String curDate=T9Utility.getCurDateTimeStr();
  try{
    T9ProjBug bug=new T9ProjBug();
    if(bugId!=null && !"".equals(bugId)){
      bug=(T9ProjBug)orm.loadObjSingle(dbConn, T9ProjBug.class, Integer.parseInt(bugId));
    }
    bug.setAttachmentId(attachmentId);
    bug.setAttachmentName(attachmentName);
    bug.setBeginUser(person.getUserName());
    bug.setBugDesc(bugDesc);
    bug.setBugName(bugName);
    bug.setCreatTime(T9Utility.parseDate(curDate));
    bug.setDeadLine(T9Utility.parseDate(deadLine));
    bug.setDealUser(dealUser);
    bug.setLevel(Integer.parseInt(level));
    bug.setProjId(Integer.parseInt(projId));
    bug.setTaskId(Integer.parseInt(taskId));
    if("1".equals(flag) || flag=="1"){
      bug.setStatus(1);
      if("1".equals(sysRemind)||("on".equals(sysRemind))) {//提交项目问题
        T9SmsBack smsBack = new T9SmsBack();
        String content = "请查看未处理项目问题";
        String remindUrl = "/project/bug/noSolveBug.jsp";         
        smsBack.setContent(content);
        smsBack.setFromId(person.getSeqId());
        smsBack.setRemindUrl(remindUrl);
        smsBack.setSmsType("88");
        smsBack.setToId(bug.getDealUser());
        T9SmsUtil.smsBack(dbConn, smsBack);
      }
    }else{
      bug.setStatus(0);
    }
    if(bugId==null || "".equals(bugId)){
      orm.saveSingle(dbConn, bug);
    }else{
      orm.updateSingle(dbConn, bug);
    }
  }catch(Exception ex){
    ex.printStackTrace();
  }
}

/**
 * 根据任务Id获取项目问题
 * @param dbConn
 * @param taskId
 * @return
 */
  public String getBugList(Connection dbConn, String taskId) {
    T9ORM orm = new T9ORM();
    StringBuffer data=new StringBuffer("[");
    try{
      String[] filters={" 1=1 and TASK_ID="+taskId};
      List<T9ProjBug> bugList=orm.loadListSingle(dbConn, T9ProjBug.class, filters);
      if(bugList!=null && bugList.size()>0){
        for(T9ProjBug bug:bugList){
          data.append("{\"seqId\":"+bug.getSeqId()+",");
          data.append("\"projId\":"+bug.getProjId()+",");
          data.append("\"bugName\":\""+bug.getBugName()+"\",");
          data.append("\"beginUser\":\""+bug.getBeginUser()+"\",");
          data.append("\"dealUser\":\""+bug.getDealUser()+"\",");
          data.append("\"result\":\""+bug.getResult()+"\",");
          data.append("\"deadLine\":\""+bug.getDeadLine()+"\",");
          data.append("\"level\":\""+bug.getLevel()+"\",");
          data.append("\"status\":\""+bug.getStatus()+"\"},");
        }
      }
      if(data.length()>3){
        data=data.deleteCharAt(data.length()-1);
      }
      data.append("]");
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return data.toString();
  }
  
  /**
   * 根据Id删除bug
   * @param dbConn
   * @param bugId
   */
  public void delBugInfo(Connection dbConn, String bugId) {
    T9ORM orm = new T9ORM();
    try{
      if(bugId!=null && !"".equals(bugId)){
        orm.deleteSingle(dbConn, T9ProjBug.class, Integer.parseInt(bugId));
      }
    }catch(Exception ex){
     ex.printStackTrace(); 
    }
    
  }
  /**
   * 更新项目状态
   * @param dbConn
   * @param bugId
   */
  public void subBug(Connection dbConn, T9Person person,String bugId,String sysRemind) {
    T9ORM orm = new T9ORM();
    try{
      T9ProjBug bug=(T9ProjBug)orm.loadObjSingle(dbConn, T9ProjBug.class, Integer.parseInt(bugId));
      bug.setStatus(1);
      orm.updateSingle(dbConn, bug);
      //判断是否需要内部消息提醒问题处理人
      if("1".equals(sysRemind)||("on".equals(sysRemind))) {//提交项目问题
        T9SmsBack smsBack = new T9SmsBack();
        
        String content = "请查看未处理项目问题";
        String remindUrl = "/project/bug/noSolveBug.jsp";         
        smsBack.setContent(content);
        smsBack.setFromId(person.getSeqId());
        smsBack.setRemindUrl(remindUrl);
        smsBack.setSmsType("88");
        smsBack.setToId(bug.getDealUser());
        T9SmsUtil.smsBack(dbConn, smsBack);
      }
    }catch(Exception ex){
     ex.printStackTrace(); 
    }
    
  }
  
  /**
   * 更新返回结果
   * @param dbConn
   * @param request
   * @param bugId
   * @param person 
   */
  public void subResult(Connection dbConn, HttpServletRequest request,
      String bugId, T9Person person) {
    T9ORM orm = new T9ORM();
    String result=person.getUserName()+"("+T9Utility.getCurDateTimeStr()+")"+"<font color=red>退回</font> :"+request.getParameter("result");
    try{
      T9ProjBug bug=(T9ProjBug)orm.loadObjSingle(dbConn, T9ProjBug.class, Integer.parseInt(bugId));
      String oldResult=bug.getResult();
      if(oldResult==null || "".equals(oldResult)){
        oldResult="";
      }
      String newResult=oldResult+result+"|*|";
      bug.setResult(newResult);
      bug.setStatus(1);
      orm.updateSingle(dbConn, bug);
      T9SmsBack smsBack = new T9SmsBack();
      //退回提醒
      String content = "问题被退回，请重新处理";
      String remindUrl = "/project/bug/noSolveBug.jsp";         
      smsBack.setContent(content);
      smsBack.setFromId(person.getSeqId());
      smsBack.setRemindUrl(remindUrl);
      smsBack.setSmsType("88");
      smsBack.setToId(bug.getDealUser());
      T9SmsUtil.smsBack(dbConn, smsBack);
      
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  /**
   * 提交处理结果
   * @param dbConn
   * @param request
   * @param bugId
   * @param person 
   */
  public void subSolveResult(Connection dbConn, HttpServletRequest request,
      String bugId, T9Person person) {
    T9ORM orm = new T9ORM();
    String result=person.getUserName()+"("+T9Utility.getCurDateTimeStr()+")"+"<font color=green>处理结果</font> :"+request.getParameter("result");
    try{
      T9ProjBug bug=(T9ProjBug)orm.loadObjSingle(dbConn, T9ProjBug.class, Integer.parseInt(bugId));
      String oldResult=bug.getResult();
      if(oldResult==null || "".equals(oldResult)){
        oldResult="";
      }
      String newResult=oldResult+result+"|*|";
      bug.setResult(newResult);
      bug.setStatus(2);
      orm.updateSingle(dbConn, bug);
      
      String projId=String.valueOf(bug.getProjId());
      String taskId=String.valueOf(bug.getTaskId());
      
      //通知提交人，问题已处理
      T9SmsBack smsBack = new T9SmsBack();
      String content = "项目问题已处理，请查看";
      String remindUrl = "/project/task/projBug.jsp?projId="+projId+"&taskId="+taskId;         
      smsBack.setContent(content);
      smsBack.setFromId(person.getSeqId());
      smsBack.setRemindUrl(remindUrl);
      smsBack.setSmsType("88");
      Map filters=new HashMap();
      filters.put("USER_NAME", bug.getBeginUser());
      T9Person beginUser=(T9Person)orm.loadObjSingle(dbConn, T9Person.class, filters);
      smsBack.setToId(String.valueOf(beginUser.getSeqId()));
      T9SmsUtil.smsBack(dbConn, smsBack);
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  /**
   * 根据项目Id得到项目名称
   * @param dbConn
   * @param projId
   * @return
   */
  public String getProjName(Connection dbConn,int projId){
    String projName="";
    T9ORM orm=new T9ORM();
    try{
      T9ProjProject project=(T9ProjProject)orm.loadObjSingle(dbConn, T9ProjProject.class, projId);
      projName=project.getProjName();
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return projName;
  }
  
}