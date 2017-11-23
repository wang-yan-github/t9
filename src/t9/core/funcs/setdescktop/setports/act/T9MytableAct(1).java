package t9.core.funcs.setdescktop.setports.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.portal.data.T9Port;
import t9.core.funcs.setdescktop.setports.logic.T9DesktopDefineLogic;
import t9.core.funcs.setdescktop.setports.logic.T9MytableLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9MytableAct {
    
  private T9MytableLogic mytableLogic = new T9MytableLogic();
  private T9DesktopDefineLogic ddLogic = new T9DesktopDefineLogic();  
    /**
   * 按左侧右侧获取桌面模块
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getModuleList(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
    String side = request.getParameter("side");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      List<T9Port> list = mytableLogic.listSide(dbConn,side);
      
      //String data = T9FOM.toJson(list).toString();
      
      //暂时使用
      StringBuffer sb = new StringBuffer("{\"total\":");
      sb.append(list.size());
      sb.append(",\"records\":[");
      
      //VIEW_TYPE信息的转换,和显示颜色的设置
      for(T9Port m : list){
        
        if("1".equals(m.getViewType())){
          m.setViewType("用户可选");
        }else if("2".equals(m.getViewType())){
          m.setFileName("<font color=\"red\">" + m.getFileName() + "</font>" );
          m.setViewType("<font color=\"red\">用户必选</font>");
        }else if("3".equals(m.getViewType())){
          m.setFileName("<font color=\"gray\">" + m.getFileName() + "</font>" );
          m.setViewType("<font color=\"gray\">暂停显示</font>");
        }
        
        sb.append(T9FOM.toJson(m));
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      
      PrintWriter pw = response.getWriter();
      
      pw.println(sb.toString().trim());
      pw.flush();
      pw.close();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "";
  }
  
  /**
   * 设置模块的显示属性   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setViewType(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
      
    String type = request.getParameter("type");
    String seqId = request.getParameter("seqId");
    
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Port m = new T9Port();
      m.setSeqId(Integer.parseInt(seqId));
      m.setViewType(type);
      
      mytableLogic.modifyType(dbConn,m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改默认位置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setPos(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String pos = request.getParameter("pos");
    String seqId = request.getParameter("seqId");
    
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Port m = new T9Port();
      m.setSeqId(Integer.parseInt(seqId));
      m.setModulePos(pos);
      
      mytableLogic.modifyPos(dbConn, m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 列出数据库中MYTABLE的文件名,为页面初始化select标签时提供数据   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listFiles(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
      
    Connection dbConn = null;
    try {
        
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      List<String> list = this.mytableLogic.listFiles(dbConn);
      
      //暂时使用
      StringBuffer sb = new StringBuffer("[");
      
      for(String o : list){
        sb.append("{");
        sb.append("\"url\":\"");
        sb.append(o);
        sb.append("\",");
        sb.append("\"content\":\"");
        sb.append(o.replaceFirst(".jsp", ""));
        sb.append("\"}");
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
        
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询MYTABLE中单条记录的详细属性   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String detail(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    
    Connection dbConn = null;
    try {
        
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Port m = this.mytableLogic.detail(dbConn,seqId);
      StringBuffer data = T9FOM.toJson(m);
      
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功设置属性");
        
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获得桌面各模块的属性
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDesktopPorts(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      List<T9Port> list = mytableLogic.getAllRequiredMytable(dbConn);
      
      if ("default".equalsIgnoreCase(user.getMytableLeft())) {
        mytableLogic.setDefaultMytableLeft(user, dbConn);
      }
      
      if ("default".equalsIgnoreCase(user.getMytableRight())) {
        mytableLogic.setDefaultMytableRight(user, dbConn);
      }
      
      String mytableLeft = user.getMytableLeft();
      String mytableRight = user.getMytableRight();
      
      
      if (mytableLeft != null){
        for (String s : mytableLeft.split(",")){
          try{
            T9Port mytable = this.mytableLogic.getDesktopPort(dbConn, Integer.parseInt(s));
            if (mytable != null){
              mytable.setModulePos("l");
              if (list.contains(mytable)){
                list.remove(mytable);
              }
              list.add(mytable);
            }
          }catch(NumberFormatException ex){
            
          }
        }
      }
      
      if (mytableRight != null){
        for (String s : mytableRight.split(",")){
          try{
            T9Port mytable = this.mytableLogic.getDesktopPort(dbConn, Integer.parseInt(s));
            
            if (mytable != null){
              mytable.setModulePos("r");
              if (list.contains(mytable)){
                list.remove(mytable);
              }
              list.add(mytable);
            }
          }catch(NumberFormatException ex){
            
          }
        }
      }
      
      String leftWidth = this.mytableLogic.getDesktopLeftWidth(dbConn);
      
      String properties = this.ddLogic.getDesktopProperties(dbConn);
      
      StringBuffer sb = new StringBuffer("{\"properties\":");
      sb.append(properties);
      sb.append(",\"total\":");
      sb.append(list.size());
      sb.append(",\"leftWidth\":");
      sb.append(leftWidth);
      sb.append(",\"records\":[");
      
      for(T9Port m : list){
        if (T9Utility.isNullorEmpty(m.getModulePos())) {
          m.setModulePos("l");
        }
        
        sb.append(T9FOM.toJson(m));
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString().trim());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功获取属性");
        
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取模块的路径(为了避免路径上出现中文)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String viewDesktopPort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    //String file = new String(request.getParameter("file").getBytes("utf-8"),"utf-8");
    String file = request.getParameter("file");
    String lines = request.getParameter("lines");
    String scroll = request.getParameter("scroll");
    
    return "/core/funcs/setdescktop/mytable/" + file + "?lines=" + lines + "&scroll" + scroll;
  }
  
  /**
   * 获取模块的路径(为了避免路径上出现中文JQUERY版本桌面模块使用)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String viewDesktopPortJquery(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    //String file = new String(request.getParameter("file").getBytes("utf-8"),"utf-8");
    String file = request.getParameter("file");
    request.setAttribute("file", file);
    return "/core/funcs/portal/modules/" + file;
  }
  
  /**
   * 管理模块的编辑功能函数

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String add4All(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
    int seqId = Integer.parseInt(request.getParameter("seqId"));
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      T9Port port = this.mytableLogic.getDesktopPort(dbConn, seqId);
      
      String modulePos = "l";
      if (port != null ) {
        modulePos = port.getModulePos();
        if (modulePos == null) {
          modulePos = "l";
        }
      }
      
      String table = user.getMytableLeft() + "," + user.getMytableRight();
      
      if (!Arrays.asList(table.split(",")).contains(String.valueOf(seqId))){
        if ("l".equalsIgnoreCase(modulePos)){
          user.setMytableLeft(user.getMytableLeft() + seqId + ",");
        }
        else {
          user.setMytableRight(user.getMytableRight() + seqId + ",");
        }
      }
        
      this.mytableLogic.addToAllPerson(dbConn, seqId, modulePos);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功设置属性");
        
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
