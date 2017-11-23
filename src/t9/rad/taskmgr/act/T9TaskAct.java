package t9.rad.taskmgr.act;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9Props;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.rad.taskmgr.data.T9Module;
import t9.rad.taskmgr.data.T9Task;
import t9.rad.taskmgr.global.T9TaskConst;
import t9.rad.taskmgr.util.T9TaskUtility;

public class T9TaskAct {
  /**
   * 保存任务
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      HttpSession session = request.getSession();
      String basePathWindow = (String)session.getAttribute("basePathWindow");
      T9Task task = (T9Task)T9FOM.build(request.getParameterMap());
      
      String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
      String filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklist";
      String filePathDone = ctxPath + basePathWindow + "\\sys\\data\\tasklistDone";
      String filePathCancel = ctxPath + basePathWindow + "\\sys\\data\\tasklistCancel";
      //启动或者执行
      if (task.getState().compareTo("9") < 0) {
        T9TaskUtility.remove(filePathDone, task.getTaskPath());
        T9TaskUtility.remove(filePathCancel, task.getTaskPath());
        T9TaskUtility.save(filePath, task);
      //完成
      }else if (task.getState().equals("9")) {
        T9TaskUtility.remove(filePath, task.getTaskPath());
        T9TaskUtility.remove(filePathCancel, task.getTaskPath());
        T9TaskUtility.save(filePathDone, task);
      //取消
      }else {
        T9TaskUtility.remove(filePath, task.getTaskPath());
        T9TaskUtility.remove(filePathDone, task.getTaskPath());
        T9TaskUtility.save(filePathCancel, task);
      }

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存任务成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存任务失败");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      HttpSession session = request.getSession();
      String basePathWindow = (String)session.getAttribute("basePathWindow");
      String taskPath = request.getParameter("taskPath");
      String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
      String filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklist";
      T9Task task = T9TaskUtility.loadTask(filePath, taskPath);
      if (task == null) {
        ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
        filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklistDone";
        task = T9TaskUtility.loadTask(filePath, taskPath);
      }
      if (task == null) {
        ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
        filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklistCancel";
        task = T9TaskUtility.loadTask(filePath, taskPath);
      }
      
      request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(task).toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取任务失败");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 加载任务列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpSession session = request.getSession();
    String basePathWindow = (String)session.getAttribute("basePathWindow");
    String basePath = (String)session.getAttribute("basePath");
    String person = request.getParameter("responsiblePerson");
    String includeDone = request.getParameter("includeDone");
    String includeCancel = request.getParameter("includeCancel");
    String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
    String filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklist";
    List<T9Task> taskList = T9TaskUtility.loadTaskList(filePath);
    filterTaskList(taskList, person);
    if (includeDone != null && includeDone.equals("1")) {
      filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklistDone";
      List taskListDone = T9TaskUtility.loadTaskList(filePath);
      filterTaskList(taskListDone, person);
      taskList.addAll(taskListDone);
    }
    if (includeCancel != null && includeCancel.equals("1")) {
      filePath = ctxPath + basePathWindow + "\\sys\\data\\tasklistCancel";
      List taskListCancel = T9TaskUtility.loadTaskList(filePath);
      filterTaskList(taskListCancel, person);
      taskList.addAll(taskListCancel);
    }
    request.setAttribute(T9ActionKeys.RET_DATA, taskList);
    
    return "/" + basePath + "/sys/taskOfPerson.jsp";
  }
  
  /**
   * 筛选任务列表
   * @param taskList
   * @param person
   */
  private void filterTaskList(List<T9Task> taskList, String person) {
    if (person == null) {
      return;
    }
    for (int i = taskList.size() - 1; i >= 0; i--) {
      T9Task task = taskList.get(i);
      if (!task.getResponsiblePerson().equals(person)) {
        taskList.remove(i);
      }
    }
  }
  
  /**
   * 保存模块
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveModule(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      HttpSession session = request.getSession();
      String basePathWindow = (String)session.getAttribute("basePathWindow");
      String isUpdate = request.getParameter("isUpdate");
      T9Module module = (T9Module)T9FOM.build(request.getParameterMap());
      String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
      String basePath = ctxPath + basePathWindow;
      String parentPath = module.getParentPath();
      String entryDir = module.getEntryDir();
      String currPath = (T9Utility.isNullorEmpty(parentPath) ? "" : parentPath.replace(".", "\\") + "\\")
        + entryDir.replaceAll(".", "\\");
      String filePath = basePath + "\\" + currPath;
      String infoPath = filePath + "\\info.text";
      File file = new File(filePath);      
      if (file.exists() && !T9Utility.null2Empty(isUpdate).equals("1")) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "保存失败: 目录已经存在" + entryDir);
        return "/core/inc/rtjson.jsp";
      }
      List lineList = new ArrayList<String>();
      lineList.add("entryDesc=" + module.getEntryDesc());
      String detlFlag = module.getIsDetl();
      if (detlFlag != null && detlFlag.equals("1")) {
        lineList.add("isDetl=1");
      }
      String sortNo = module.getSortNo();
      if (!T9Utility.isNullorEmpty(sortNo)) {
        lineList.add("sortNo=" + sortNo);
      }
      T9FileUtility.storeArray2Line(infoPath, lineList);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");      
      return "/core/inc/rtjson.jsp";
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  
  /**
   * 取得动态数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpSession session = request.getSession();
    String basePathWindow = (String)session.getAttribute("basePathWindow");
    response.setContentType("text/xml");    
    response.setHeader("Cache-Control", "no-cache");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    out.print("<?xml version=\'1.0\' encoding=\'utf-8'?>");
    out.print("<menus>");
    try {
      String ctxPath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH);
      String basePath = ctxPath + basePathWindow + "";
      String idStr = request.getParameter("id");
      if (idStr == null || idStr.equals("0")) {
        idStr = "";
      }
      String modulePath = basePath;
      if (idStr.length() > 0) {
        modulePath += "\\" + idStr.replaceAll(".", "\\");
      }
      T9Props moduleProps = getProps(modulePath);
      short sortNo = getSortNo(moduleProps);
      if (sortNo < 2) {
        out.print("<count>0</count>");
        out.print("</menus>");
        return null;
      }
      File moduleFile = new File(modulePath);
      String[] fileArray = moduleFile.list();
      int cnt = 0;
      StringBuffer buff = null;
      Map<String, StringBuffer> sortMap = new TreeMap<String, StringBuffer>();
      for (int i = 0; i < fileArray.length; i++ ){
        String currName = fileArray[i];
        String currFilePath = modulePath + "\\" + currName + "";
        T9Props currProps = getProps(currFilePath);
        short currSortNo = getSortNo(currProps);
        if (currSortNo < 1) {
          continue;
        }
        cnt++;
        buff = new StringBuffer();
        String currId = idStr.length() < 1 ? currName : (idStr + "." + currName);
        String key = currProps.get("sortNo");
        if (key == null) {
          key = "0";
        }
        String sortNoStr = T9Utility.getFixLengthStringFront(key, 3);
        key = sortNoStr + "." + currId;
        buff.append("<menu>");
        buff.append("<id>" + currId + "</id>");
        buff.append("<name>" + currProps.get(T9TaskConst.ENTRY_DESC) + "</name>");
        buff.append("<parentId>" + idStr + "</parentId>");
        buff.append("<isHaveChild>" + (currSortNo > 1 ? 1 : 0) + "</isHaveChild>");
        buff.append("</menu>");
        sortMap.put(key, buff);
      }
      Iterator<StringBuffer> iBuff = sortMap.values().iterator();
      while (iBuff.hasNext()) {
        buff = iBuff.next();
        out.print(buff.toString());
      }
      out.print("<parentNodeId>" + idStr + "</parentNodeId>");
      out.print("<count>" + cnt + "</count>");
      out.print("</menus>");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      out.flush();
      out.close();
    }
    return null;
  }
  
  /**
   * 取得属性编码
   * @param infoProps
   * @return
   */
  private short getSortNo(T9Props infoProps) {
    if (infoProps == null) {
      return 0;
    }
    String detlStr = infoProps.get(T9TaskConst.IS_DETL);
    if (detlStr != null && detlStr.equals("1")) {
      return 1;
    }
    return 2;
  }
  /**
   * 取得属性对象
   * @param filePath
   * @return
   * @throws Exception
   */
  private T9Props getProps(String filePath) throws Exception {
    File moduleFile = new File(filePath);  
    String infoPath = filePath + "\\info.text";
    File infoFile = new File(infoPath);
    if (!moduleFile.exists() || !moduleFile.isDirectory() || !infoFile.exists()) {
      return null;
    }
    
    T9Props infoProps = new T9Props();
    infoProps.loadProps(infoPath);
    return infoProps;
  }
}
