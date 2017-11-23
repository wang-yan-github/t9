package t9.project.task.logic;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.project.project.data.T9ProjProject;
import t9.project.task.data.T9ProjTaskLog;


public class T9ProjTaskLogLogic{
  public static final String MODULE = "proj_file";
  public static final String ATT_PATCH = T9SysProps.getAttachPath();
  /**
   * 处理上传附件，返回附件id，附件名称
   * 
   * 
   * @param request
   *          HttpServletRequest
   * @param
   * @return Map<String, String> ==> {id = 文件名}
   * @throws Exception
   */
  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm, String pathPx) throws Exception {
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
        String fileName = fileForm.getFileName(fieldName);
        String fileNameV = fileName;
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = this.getRondom();

        fileName = rand + "_" + fileName;

        while (getExist(filePath + "\\" + hard, fileName)) {
          rand = getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, filePath + "\\" + MODULE + "\\" + hard + "\\" + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }

  public String getRondom() throws NoSuchAlgorithmException {
    String result = "";
    // Random rand = new Random();
    // result = rand.nextLong();
    // System.out.println(result);
    result = T9Guid.getRawGuid();
    return result;
  }

  /**
   * 判断 文件是否存在
   * 
   * @param savePath
   * @param fileExtName
   * @return
   * @throws IOException
   */
  public boolean getExist(String savePath, String fileExtName) throws IOException {
    String filePath = savePath + "\\" + fileExtName;
    if (new File(filePath).exists()) {
      return true;
    }
    return false;
  }
  /**
   * 根据taskId 查询任务进度日志信息
   * @param dbConn
   * @param taskId
   * @return
   */
  public String getTaskLogDetail(Connection dbConn, String taskId) {
    T9ORM orm = new T9ORM();
    StringBuffer data= new StringBuffer("[");
    try{
      if(taskId==null || "".equals(taskId)){
        taskId="0";
      }
      String[] filters={"1 = 1 and TASK_ID="+Integer.parseInt(taskId)};
      List<T9ProjTaskLog> logList=orm.loadListSingle(dbConn, T9ProjTaskLog.class, filters);
      if(logList!=null && logList.size()>0){
       for(T9ProjTaskLog log:logList){
         data.append("{\"seqId\":"+log.getSeqId()+",");
         data.append("\"logUser\":\""+log.getLogUser()+"\",");
         data.append("\"logContent\":\""+log.getLogContent()+"\",");
         data.append("\"attachmentId\":\""+log.getAttachmentId()+"\",");
         data.append("\"attachmentName\":\""+log.getAttachmentName()+"\",");
         data.append("\"percent\":\""+log.getPercent()+"\",");
         data.append("\"taskId\":\""+log.getTaskId()+"\",");
         data.append("\"logTime\":\""+log.getLogTime()+"\"},");
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
   * 获得任务进度日志树
   * @author zq
   * 2013-3-27
   * @param dbConn
   * @param map
   * @param person
   * @param request
   * @return
   * @throws Exception
   */
  public String getTaskLogTree(Connection dbConn, Map map, T9Person person, HttpServletRequest request) throws Exception {
    String sql = "";
    String conditionStr = "";
    String  taskId = request.getParameter("taskId");
    try{
      sql = "select proj_task_log.seq_id,log_user,log_content,attachment_id,attachment_name,log_time,percent from proj_task_log,proj_task where 1=1 and proj_task.seq_id= "+taskId+" and proj_task.seq_id=proj_task_log.task_id order by log_time desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  /**
   * 获取进度百分比
   * @author zq
   * 2013-4-8
   * @param con
   * @param taskId
   * @return
   * @throws Exception
   */
  public String getPercent(Connection con,int taskId)throws Exception {
      String percent="";
      PreparedStatement ps=null;
      ResultSet rs=null;
      String sql="";
      try{
        sql="select max(percent) from proj_task_log where task_id="+taskId;
        ps=con.prepareStatement(sql);
        rs=ps.executeQuery();
        while(rs.next()){
          percent=rs.getInt(1)+"";
         // System.out.println("percent="+percent);
        }
      }catch(Exception ex){
        ex.printStackTrace();
        throw ex;
      }
      return percent;
  }
  public void updateLog(Connection con,T9ProjTaskLog log) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.updateSingle(con, log);
  }
  public T9ProjTaskLog getTaskBySeqId(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
//    System.out.println(seqId);
    return (T9ProjTaskLog) t9orm.loadObjSingle(con, T9ProjTaskLog.class, seqId);
  }
  public void addLog(Connection con,T9ProjTaskLog log) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.saveSingle(con, log);
  }

  /**
   * 删除任务日志
   * 2013-4-10
   * @author ny
   * @param dbConn
   * @param taskId
   */
  public void delTaskLog(Connection dbConn, String taskId) {
    T9ORM orm=new T9ORM();
    PreparedStatement ps1=null;
    String sql1="";
    try{
      sql1="delete from proj_task_log where seq_id="+taskId;
      ps1=dbConn.prepareStatement(sql1);
      ps1.execute();
    }catch(Exception ex){
      ex.printStackTrace();
    }
    
  }
}
