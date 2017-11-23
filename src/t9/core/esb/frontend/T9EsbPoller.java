package t9.core.esb.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import t9.core.autorun.T9AutoRun;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.common.T9DownloadTask;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.frontend.data.T9EsbDownTask;
import t9.core.esb.frontend.logic.T9EsbPollerLogic;
import t9.core.esb.frontend.oa.T9ESBMessageServiceCaller;
import t9.core.funcs.system.info.logic.T9InfoLogic;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.file.T9ZipFileUtility;
import t9.core.util.form.T9FOM;
import t9.user.api.core.db.T9DbconnWrap;

public class T9EsbPoller extends T9AutoRun {
  public static T9DownloadTask task = null;
  public static final String PULL_URL = "/t9/t9/core/esb/server/act/T9EsbServerAct/query.act";
  public static String nowDownTaskGuid = "";
  
  public void doTask() {
    try {
      if (!T9EsbFrontend.isOnline()) {
        T9EsbUtil.debug("d-未登陆,开始登陆");
        int code = T9EsbFrontend.login();
        if (code != 0) {
          T9EsbUtil.debug("d-登陆失败");
          return;
        }
        T9EsbUtil.debug("d-登陆成功");
      } else {
        T9EsbUtil.debug("d-用户已在线");
      }
      T9EsbPollerLogic logic = new T9EsbPollerLogic();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      
      String tasks = "";
      String fileLength ="";
      String contentMd5 ="";
      String fileName ="";
      String fileType="";
      String fromId = "";
      String optGuid = "";
      String message = "";
      HttpGet request = new HttpGet(PULL_URL);
      long t1 = System.currentTimeMillis();
      HttpResponse response = T9EsbFrontend.getHc().execute(host, request);
      long t2 = System.currentTimeMillis();
      T9EsbUtil.debug("d-发送请求<获取信息>" + ":耗时:" + (t2 - t1) );
      InputStream is = response.getEntity().getContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is , "UTF-8"));
      String res = "";
      int readLen;
      char[] charArray = new char[50];
      long t3 = System.currentTimeMillis();
      long t5 = System.currentTimeMillis();
      T9EsbUtil.debug("d-开始读取信息");
      while((readLen = reader.read(charArray)) > 0){ 
        String s = new String(charArray, 0, readLen);
        long t4 = System.currentTimeMillis();
        T9EsbUtil.debug("d-读取："+ s +"，耗时：" + (t4 - t3) );
        res += s;
        t3 = System.currentTimeMillis();
      }
      long t6 = System.currentTimeMillis();
      T9EsbUtil.debug("d-读取："+ res +"，总耗时：" + (t6 - t5));
      
      if (!"".equals(res.trim())) {
        T9EsbUtil.debug("d-读取的信息转json");
        Map<String, String> map = T9FOM.json2Map(res.trim());
        T9EsbUtil.debug("d-读取的信息转json成功");
        if (map != null) {
           tasks = map.get("tasks");
           fileLength = map.get("fileLength");
           contentMd5 = map.get("contentMd5");
           fileName = map.get("fileName"); 
           fileType = map.get("fileType");
           fromId = map.get("fromId");
           optGuid = map.get("optGuid");
           message = map.get("message2");
           
          String msg = null;
          T9EsbUtil.debug("d-处理信息头");
          
          for (Header h : response.getAllHeaders()) {
            if ("SYS-MSG2".equals(h.getName())) {
              msg = toSrcStr(h.getValue());
              T9EsbUtil.debug("d-获取信息头:" + msg);
              break;
            }
          }
          
          if (!T9Utility.isNullorEmpty(msg)) {
            //更新状态
            Map<String, String> m = T9FOM.json2Map(msg);
            String type = m.get("type");
            if (T9Utility.isNullorEmpty(type)) {
              T9EsbUtil.debug("d-处理发送接收命令");
              try{
                T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
                caller.updateState(m.get("guid"), Integer.parseInt(m.get("code")), m.get("to"));
              } catch (Exception ex) {
                T9EsbUtil.debug("d-处理发送接收命令报错：" + ex.getMessage());
                ex.printStackTrace();
              }
            } else {
              T9EsbUtil.debug("d-处理系统命令");
              try {
                if ("1".equals(type)) {
                  //开启用户日志
                  T9EsbUtil.debug("d-开启系统日志");
                  T9EsbUtil.isDebug = true;
                } else if ("2".equals(type)) {
                  //上传情况
                  T9EsbUtil.debug("d-上传系统信息");
                 // String message2 = getClientInfo();
                  //T9EsbFrontend.reportMessage(message2);
                } else if ("3".equals(type)) {
                  String sql = m.get("sql");
                  T9EsbUtil.debug("d-执行sql:" + sql);
                  this.exeUpdate(sql); 
                }else if ("4".equals(type)) {
                  String sql = m.get("sql");
                  T9EsbUtil.debug("d-执行sql-q:" + sql);
                  this.exeQuery(sql);
                } else if ("5".equals(type)) {
                  String path = m.get("path");
                  T9EsbUtil.debug("d-文件是否存在:" + path);
                  File file = new File(path);
                  if (file.exists()) {
                    T9EsbFrontend.reportMessage("{message:\"文件:"+path +"，文件存在\"}");
                  } else {
                    T9EsbFrontend.reportMessage("{message:\"文件:"+path +"，文件不存在\"}");
                  }
                } else if ("6".equals(type)) {
                  String srcFile = m.get("srcFile");
                  String destFile = m.get("destFile");
                  try {
                    T9ZipFileUtility.doCiphZip(srcFile, destFile);
                    T9EsbFrontend.reportMessage("{message:\"执行压缩成功操作，将文件："+srcFile +"压缩:"+destFile+"\"}");
                  }catch(Exception ex) {
                    ex.printStackTrace();
                    T9EsbFrontend.reportMessage("{message:\"执行压缩操作失败，报错："+ex.getMessage()+"\"}");
                  }
                }else if ("7".equals(type)) {
                  String srcFile = m.get("srcFile");
                  String destDir = m.get("destDir");
                  try {
                    T9ZipFileUtility.unZipCiphFile(srcFile, destDir);
                    T9EsbFrontend.reportMessage("{message:\"执行解压成功操作，源文件："+srcFile+"解压到："+ destDir +"\"}");
                  }catch(Exception ex) {
                    ex.printStackTrace();
                    T9EsbFrontend.reportMessage("{message:\"执行解压操作失败，报错："+ex.getMessage()+"\"}");
                  }
                }else if ("8".equals(type)) {
                  String path = m.get("path");
                  try {
                    File file = new File(path);
                    if (file.exists()) {
                      if (file.isDirectory()) {
                        T9FileUtility.deleteAll(file);
                        T9EsbFrontend.reportMessage("{message:\"删除目录操作成功，目录："+ path +"\"}");
                      } else {
                        file.delete();
                        T9EsbFrontend.reportMessage("{message:\"删除文件操作成功，文件："+ path +"\"}");
                      }
                    } else {
                      T9EsbFrontend.reportMessage("{message:\"删除文件操作失败，文件："+ path +"不存在\"}");
                    }
                  }catch(Exception ex) {
                    ex.printStackTrace();
                    T9EsbFrontend.reportMessage("{message:\"删除文件操作失败，报错："+ex.getMessage()+"\"}");
                  }
                } else if ("9".equals(type)) {
                  String path = m.get("path");
                  try {
                    File file = new File(path);
                    if (file.exists()) {
                      if (file.isDirectory()) {
                        String[] ss = file.list();
                        String sss = "";
                        int count = 0 ;
                        for (String s : ss) {
                          if (count > 20) {
                            break;
                          }
                          sss += T9FileUtility.getFileName(s) + ",";
                          count++;
                        }
                        T9EsbFrontend.reportMessage("{message:\"目录："+ path +"下有："+ ss.length +"个文件,["+ sss +"]\"}");
                      } else {
                        T9EsbFrontend.reportMessage("{message:\"文件操作成功，文件："+ path +"不是目录\"}");
                      }
                    } else {
                      T9EsbFrontend.reportMessage("{message:\"文件操作失败，文件："+ path +"不存在\"}");
                    }
                  }catch(Exception ex) {
                    ex.printStackTrace();
                    T9EsbFrontend.reportMessage("{message:\"文件操作失败，报错："+ex.getMessage()+"\"}");
                  }
                } else if ("10".equals(type)) {
                  String srcFile = m.get("srcFile");
                  String destFile = m.get("destFile");
                  try {
                    T9FileUtility.copyFile(srcFile, destFile);
                    T9EsbFrontend.reportMessage("{message:\"文件复制操作 成功，源文件："+srcFile+"复制到:"+destFile+"\"}");
                  }catch(Exception ex) {
                    ex.printStackTrace();
                    T9EsbFrontend.reportMessage("{message:\"文件复制操作失败，报错："+ex.getMessage()+"\"}");
                  }
                } else if ("11".equals(type)) {
                  String srcDir = m.get("srcDir");
                  String destDir = m.get("destDir");
                  try {
                    T9FileUtility.copyDir(srcDir, destDir);
                    T9EsbFrontend.reportMessage("{message:\"文件目录复制操作 成功，源文件："+srcDir+"复制到:"+destDir+"\"}");
                  }catch(Exception ex) {
                    ex.printStackTrace();
                    T9EsbFrontend.reportMessage("{message:\"文件目录复制操作失败，报错："+ex.getMessage()+"\"}");
                  }
                }
              } catch (Exception ex){
                T9EsbUtil.debug("d-处理系统命令报错：" + ex.getMessage());
                ex.printStackTrace();
              }
            }
          }
        }
      }
      else {
        T9EsbUtil.debug("d-没有下载任务");
      }
      try {
        T9EsbUtil.debug("d-保存下载任务");
        this.saveTasks(tasks, fileLength, contentMd5, fileName, fileType, fromId, optGuid, message);
        T9EsbUtil.debug("d-保存下载任务成功");
      }catch (Exception ex) {
        T9EsbUtil.debug("d-保存下载任务报错：" + ex.getMessage());
        ex.printStackTrace();
      }
      T9EsbDownTask downLoadTask = logic.getDownTaskByStatus("1,-1");
     
      if (downLoadTask != null) {
        String s = downLoadTask.getGuid();
        T9EsbUtil.debug("d-开始下载任务：" + s + ",标题：" + T9Utility.null2Empty(downLoadTask.getMessage()));
        if (s != null && !"".equals(s.trim())) {
          try {
            DefaultHttpClient hc = T9EsbFrontend.getHc();
            task = new T9DownloadTask(host, new File(ClientPropertiesUtil.getCacheDir()), s , downLoadTask.getOptGuid(), downLoadTask.getMessage() , this);
            
            T9EsbUtil.debug("d-开始下载任务：" + s);
            boolean flag = task.initialize(hc);
            T9EsbUtil.debug("d-任务：" + s + "初始化完成 ，返回：" + flag);
            if (flag) {
              flag = task.transfer(hc);
              T9EsbUtil.debug("d-任务：" + s + "传输完成 ，返回：" + flag);
              if (flag) {
                task.complete(hc);
                T9EsbUtil.debug("d-任务：" + s + "下载完成");
              }
            }
          }catch(Exception ex) {
            T9EsbUtil.debug("d-下载:"+s +"时报错：" + ex.getMessage());
            ex.printStackTrace();
          }finally {
            task = null;
          }
        }
      }
      else {
        T9EsbUtil.debug("d-无下载任务");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  public void saveTasks(String guids , String fileLength ,
     String contentMd5 
     , String fileName
     , String fileType 
     , String fromId 
     , String optGuid 
     , String message) {
    T9EsbUtil.debug("saveTasks-1");
    String[] fileNames = fileName.split(",");
    String[] fromIds = fromId.split(",");
    String[] optGuids = optGuid.split(",");
    String[] gs = guids.split(",");
    String[] messages = message.split(",");
    T9EsbUtil.debug("saveTasks-2");
    File dir = new File(ClientPropertiesUtil.getCacheDir());
    if (!dir.exists()) {
      dir.mkdirs();
    }
    T9EsbUtil.debug("saveTasks-3");
    for (int i  = 0;i < gs.length ; i++) {
      String s = gs[i];
      T9EsbUtil.debug("saveTasks-4:" + s);
      if (s != null && !"".equals(s.trim())) {
        T9EsbPollerLogic logic = new T9EsbPollerLogic();
        if (!logic.hasEsbDownTask(s)) {
          T9EsbUtil.debug("saveTasks-5:新任务:" + s );
          File dir2 = new File(dir.getAbsolutePath() + File.separator + s);
          if (!dir2.exists()) {
            dir2.mkdirs();
          }
          String n = "";
          if (fileNames.length > i) {
            n = fileNames[i];
          }
          try {
            n = java.net.URLDecoder.decode(n, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          String fId = "";
          if (fromIds.length > i) {
            fId = fromIds[i];
          }
          String opt = "";
          if (optGuids.length > i) {
            opt = optGuids[i];
          }
          String mes = "";
          if (messages.length > i) {
            try {
              mes = messages[i];
              mes = toSrcStr(mes);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          String name = dir.getAbsolutePath() + File.separator + n;
          try{
            T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
            caller.doMessage(fId, mes);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          
          try {
            logic.addEsbDownTask(name, s, 1, fId , opt , mes);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else {
          logic.updateStatus(s, "1");
          T9EsbUtil.debug("saveTasks-６:老任务:" + s );
        }
      }
    }
  }
  public String getTasks(List<T9EsbDownTask> list  , String tasks) {
    for (T9EsbDownTask d : list) {
      if (!this.findId(tasks, d.getGuid())){
        if (T9Utility.isNullorEmpty(tasks) || tasks.endsWith(",")) {
          tasks += d.getGuid() + ",";
        } else {
          tasks += "," + d.getGuid();
        }
      }
    }
    return tasks;
  }
  public  T9EsbDownTask  getDownTask(List<T9EsbDownTask> list,String guid)  {
    for (T9EsbDownTask d : list) {
      if (d.getGuid() != null && d.getGuid().equals(guid)){
        return d;
      }
    }
    return null;
  }
  public  boolean findId(String str, String id) {
    if(str == null || id == null || "".equals(str) || "".equals(id)){
      return false;
    }
    String[] aStr = str.split(",");
    for(String tmp : aStr){
      if(tmp.equals(id)){
        return true;
      }
    }
    return false;
  }
  public void exeQuery(String sql) {
    // TODO Auto-generated method stub
    PreparedStatement ps = null;
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection conn2 = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer();
    int count = 0 ;
    try {
      conn2 = dbUtil.getSysDbConn();
      ps = conn2.prepareStatement(sql);
      rs =  ps.executeQuery();
      
      sb.append("[");
      ResultSetMetaData rsmt = rs.getMetaData();
      int numberOfColumns = rsmt.getColumnCount(); 
      while ((count < 10 && rs.next())) {
        if (numberOfColumns > 1) {
          sb.append("{");
          for (int i = 1 ; i<= numberOfColumns ; i++) {
            sb.append(rs.getObject(i) + ",");
          }
          sb.deleteCharAt(sb.length() - 1);
          sb.append("},");
        } else {
          sb.append(rs.getObject(1) + ",");
        }
        count++;
      }
      if (count >0 ) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      T9EsbFrontend.reportMessage("{message:\"执行sql:"+sql+"成功，返回值："+ sb.toString() +"\"}");
    } catch (Exception e) {
      e.printStackTrace();
      T9EsbFrontend.reportMessage("{message:\"执行sql:"+sql+"成功，报错："+e.getMessage()+"\"}");
    } finally {
      T9DBUtility.close(ps, null, null);
      T9DBUtility.closeDbConn(conn2, null);
    }
  }
  public int exeUpdate(String sql) {
    // TODO Auto-generated method stub
    PreparedStatement ps = null;
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection conn2 = null;
    try {
      conn2 = dbUtil.getSysDbConn();
      ps = conn2.prepareStatement(sql);
      int count =  ps.executeUpdate();
      T9EsbFrontend.reportMessage("{message:\"执行sql:"+sql+"成功，影响行："+count+"\"}");
      return count;
    } catch (Exception e) {
      e.printStackTrace();
      T9EsbFrontend.reportMessage("{message:\"执行sql:"+sql+"成功，报错："+e.getMessage()+"\"}");
    } finally {
      T9DBUtility.close(ps, null, null);
      T9DBUtility.closeDbConn(conn2, null);
    }
    return 0;
  }
  /*抓取客户端信息
   * @param request
   * @param response
   * @return
   * @throws Exception

  public  String getClientInfo() throws Exception {
    String data =  "";
    try {
      T9DbconnWrap dbUtil = new T9DbconnWrap();
      Connection dbConn = dbUtil.getSysDbConn();
      
      //控制发送公文开关
      T9SysPara hall = new T9SysPara() ;
      String paraName = "DOC_SECURITY_TYPE";
      String docSecurityTypeValue = "0";
      String docSecurityTypeValueDesc = "正常";
      T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, "DOC_SECURITY_TYPE");//
      if (sysPara != null) {
        docSecurityTypeValue =  sysPara.getParaValue();
      }
      //系统版本
      T9InfoLogic info = new T9InfoLogic();
      Map<String,String> version = info.getVersion(dbConn);
      String ver  = "";//当前版本
      if(version != null){
         ver = version.get("ver");
      }
      
      // 获取esb本地配置单位
      T9EsbClientConfig config = T9EsbClientConfig.builder(T9SysProps
          .getWebPath() + T9EsbConst.CONFIG_PATH);
      String unit = config.getUserId();
      if(docSecurityTypeValue.equals("1")){
        docSecurityTypeValueDesc = "不允许发送公文";
      }
      data = "{unidCode:\"" + unit + "\",version:\"" + ver + "\",SEND_DOC_TYPE:\""  + docSecurityTypeValueDesc+ "\"}";
    } catch (Exception e) {
       throw e;
    }
    return data;
  }
  
 */
  /**
   * 16进制字符串转成原来的字符串
   * @param srcStr
   * @return
   * @throws Exception
   */
  private static String toSrcStr(String srcStr) throws Exception {
    return new String(hexStr2Bytes0(srcStr), "UTF-8");
  }
  /**
   * 16进制字符串转化成二进制数组
   * @param srcStr
   * @return
   * @throws Exception
   */
  private static byte[] hexStr2Bytes0(String srcStr) throws Exception {
    int len = srcStr.length();
    int bufLen = len / 2;
    byte[] rtBuf = new byte[bufLen];
    for (int i = 0; i < bufLen; i++) {
      byte heigh = (byte)Integer.parseInt(srcStr.substring(i * 2, i * 2 + 1), 16);
      byte low = (byte)Integer.parseInt(srcStr.substring(i * 2 + 1, i * 2 + 2), 16);
      heigh = (byte)((byte)heigh << 4);
      rtBuf[i] = (byte)((byte)(heigh & 0xF0) + (byte)(low & 0x0F));
    }
    return rtBuf;
  }
  public static  void stopDownlondRun() {
    T9DownloadTask downLoadTask = T9EsbPoller.task; 
    if (downLoadTask != null) {
      try {
        downLoadTask.setStopDownloadFlag();
      }catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  public static  void runDownlond() {
    T9DownloadTask downLoadTask = T9EsbPoller.task; 
    if (downLoadTask != null && downLoadTask.getEsbPoller() != null) {
      try {
        downLoadTask.getEsbPoller().menuStartRun();
      }catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
