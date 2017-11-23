package t9.rad.taskmgr.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import t9.core.data.T9SysOperator;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.rad.taskmgr.data.T9RadUser;
import t9.rad.taskmgr.data.T9Task;

public class T9TaskUtility {
  /**
   * 加载任务列表
   * @param filePath
   * @return 任务列表对象
   * @throws Exception
   */
  public static List loadTaskList(String filePath) throws Exception {
    
    List<String> lineList = new ArrayList<String>();
    T9FileUtility.loadLine2Array(filePath, lineList);
    List<T9Task> taskList = new ArrayList<T9Task>();
    for (String lineStr : lineList) {
      if (lineStr.trim().length() < 1) {
        continue;
      }
      taskList.add((T9Task)T9FOM.json2Obj(lineStr.trim(), T9Task.class));
    }
    return taskList;
  }
  /**
   * 加载任务
   * @param filePath
   * @return 任务
   * @throws Exception
   */
  public static T9Task loadTask(String filePath, String taskPath) throws Exception {
    if (taskPath == null) {
      return null;
    }
    List<T9Task> taskList = loadTaskList(filePath);
    for (T9Task task : taskList) {
      if (task.getTaskPath().equals(taskPath)) {
        return task;
      }
    }
    return null;
  }
  /**
   * 保存任务列表
   * @param taskList
   * @throws Exception
   */
  public static void save(String filePath, List<T9Task> taskList) throws Exception {
    List<String> lineList = new ArrayList<String>();
    for (T9Task task : taskList) {
      lineList.add(T9FOM.toJson(task).toString());
    }
    T9FileUtility.storeArray2Line(filePath, lineList);
  }
  /**
   * 保存
   * @param filePath
   * @param task
   * @throws Exception
   */
  public static void save(String filePath, T9Task task) throws Exception {
    List<T9Task> taskList = loadTaskList(filePath);
    T9Task oldTask = null;
    int index = 0;
    for (int i = 0; i < taskList.size(); i++) {
      T9Task taskInFile = taskList.get(i);
      if (taskInFile.getTaskPath().equals(task.getTaskPath())) {
        oldTask = taskInFile;
        break;
      }
      index++;
    }
    if (oldTask == null) {
      taskList.add(task);
    }else {
      taskList.set(index, task);
    }
    save(filePath, taskList);
  }
  
  /**
   * 删除任务
   * @param filePath
   * @param taskPath
   * @throws Exception
   */
  public static void remove(String filePath, String taskPath) throws Exception {
    List<T9Task> taskList = loadTaskList(filePath);
    boolean deleted = false;
    for (int i = 0; i < taskList.size(); i++) {
      T9Task taskInFile = taskList.get(i);
      if (taskInFile.getTaskPath().equals(taskPath)) {
        taskList.remove(i);
        deleted = true;
        break;
      }
    }
    if (deleted) {
      save(filePath, taskList);
    }
  }
  
  /**
   * 加载用户
   * @param filePath
   * @return
   * @throws Exception
   */
  public static List<T9RadUser> loadUserList(String filePath) throws Exception {
    List<T9RadUser> userList = new ArrayList();
    
    List<String> lineList = new ArrayList<String>();
    T9FileUtility.loadLine2Array(filePath, lineList);

    for (String lineStr : lineList) {
      if (lineStr.trim().length() < 1) {
        continue;
      }
      userList.add((T9RadUser)T9FOM.json2Obj(lineStr.trim(), T9RadUser.class));
    }
    return userList;
  }
  
  /**
   * 验证是否有效的用户
   * @param userList
   * @param name
   * @param pass
   * @return
   * @throws Exception
   */
  public static T9SysOperator getUser(List<T9RadUser> userList,
      String name,
      String pass) throws Exception  {
    
    //没有配置用户列表，不需要进行用户验证
    if (userList == null || userList.size() < 1) {
      throw new Exception("没有配置用户列表！");
    }
    if (name == null) {
      throw new Exception("没有传递用户名！");
    }
    if (pass == null) {
      pass = "";
    }
    for (int i = 0; i < userList.size(); i++) {
      T9RadUser user = userList.get(i);
      if (!user.getName().equals(name)) {
        continue;
      }
      if (!user.getPass().equals(pass)) {
        throw new Exception("无效的密码！");
      }
      T9SysOperator opt = new T9SysOperator();
      opt.setName(user.getName());
      opt.setFullName(user.getFullName());
      return opt;
    }
    return null;
  }
  
  /**
   * 取得模块的数量
   * @param moduleDir
   * @return
   */
  public static int getModuleCnt(String parentDir) {
    int rtCnt = 0;
    File parentFile = new File(parentDir);
    if (!parentFile.exists() || !parentFile.isDirectory()) {
      return rtCnt;
    }
    String[] fileArray = parentFile.list();
    for (int i = 0; i < fileArray.length; i++) {
      String tmpName = fileArray[i];
      File tmpFile = new File(parentDir + "\\" + tmpName);
      if (!tmpFile.isDirectory()) {
        continue;
      }
      tmpFile = new File(parentDir + "\\" + tmpName + "\\info.text");
      if (!tmpFile.exists()) {
        continue;        
      }
      rtCnt++;
    }
    return rtCnt;
  }
}
