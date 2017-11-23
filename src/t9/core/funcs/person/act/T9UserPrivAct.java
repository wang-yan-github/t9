package t9.core.funcs.person.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.menu.data.T9SysMenu;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.system.data.T9UserPrivCache;
import t9.core.funcs.system.ispirit.n12.org.act.T9IsPiritOrgAct;
import t9.core.funcs.workflow.util.T9FlowFormLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.module.oa.logic.T9OaSyncLogic;
import t9.core.module.report.logic.T9ReportSyncLogic;
import t9.core.module.report.logic.T9UserPrivSyncLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9UserPrivAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String insertUserPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      // if(dl.existsTableNo(dbConn, privNo)){
      // request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      // request.setAttribute(T9ActionKeys.RET_MSRG, "角色排序号以存在，请重新填写！");
      // return "/core/inc/rtjson.jsp";
      // }else{
      // request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      // request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      // }
      T9UserPriv dpt = (T9UserPriv) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, dpt);
      //userPrivNum(request, response);
      
      if (T9ReportSyncLogic.hasSync) {
        int max = T9ReportSyncLogic.getMax(dbConn, "select max(seq_id) from user_priv");
        dpt.setSeqId(max);
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.addUserPriv(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        int max = T9ReportSyncLogic.getMax(dbConn, "select max(seq_id) from user_priv");
        dpt.setSeqId(max);
        Connection reportConn = T9OaSyncLogic.getOAConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.addUserPriv(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      //dbConn.close();
      //request.setAttribute("desc", deptParentDesc);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String insertPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String funcIdStr = request.getParameter("funcIdStr");
      String sSeqId = request.getParameter("seqId");
      int seqId = 0;
      if(sSeqId != null && !"".equals(sSeqId)){
        seqId = Integer.parseInt(sSeqId);
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      T9UserPriv dpt = (T9UserPriv) orm.loadObjSingle(dbConn, T9UserPriv.class, seqId);
      dpt.setSeqId(seqId);
      dpt.setFuncIdStr(funcIdStr);
      orm.updateSingle(dbConn, dpt);
      //dbConn.close();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      
      //生成org.xml文件
      T9UserPrivCache.removeUserPrivCache(seqId);
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
      if (T9ReportSyncLogic.hasSync) {
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.editUserPriv(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        Connection reportConn = T9OaSyncLogic.getOAConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.editUserPriv(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUserPriv(HttpServletRequest request, HttpServletResponse response)
      throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Object obj = orm.loadObjSingle(dbConn, T9UserPriv.class, seqId);
      data = T9FOM.toJson(obj).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String updateUserPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPriv dpt = (T9UserPriv) T9FOM.build(request.getParameterMap());
      dpt.setSeqId(seqId);
      T9ORM orm = new T9ORM();
      //if((privNoOld).equals(privNo)){
      orm.updateSingle(dbConn, dpt);
      T9UserPrivCache.removeUserPrivCache(seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
      //}else{
       // if(userpriv.existsTableNo(dbConn, privNo)){
       //   request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       //   request.setAttribute(T9ActionKeys.RET_MSRG, "角色排序号以存在，请重新填写！");
       //   return "/core/inc/rtjson.jsp";
       // }else{
       //   orm.updateSingle(dbConn, dpt);
       //request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       //request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
       // }
       // }
      
      //生成org.xml文件
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
      if (T9ReportSyncLogic.hasSync) {
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.editUserPriv(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        Connection reportConn = T9OaSyncLogic.getOAConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.editUserPriv(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteUserPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      T9UserPriv dt = T9UserPrivCache.getUserPrivCache(dbConn, seqId);
      dt.setSeqId(seqId);
      orm.deleteComplex(dbConn, dt);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
      
      //生成org.xml文件
      T9UserPrivCache.removeUserPrivCache(seqId);
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
      if (T9ReportSyncLogic.hasSync) {
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.delUserPriv(dt.getSeqId(), reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        Connection reportConn = T9OaSyncLogic.getOAConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.delUserPriv(dt.getSeqId(), reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String userPrivNum(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPrivLogic userPrivLogic = new T9UserPrivLogic(); 
      for(int i = 0; i < userPrivLogic.selectUserPriv(dbConn).size(); i++){
        T9UserPriv up = (T9UserPriv) userPrivLogic.selectUserPriv(dbConn).get(i);
        //T9Person upp = (T9Person) userPrivLogic.selectPerson(dbConn,up.getPrivNo());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getTree(HttpServletRequest request, HttpServletResponse response)
  throws Exception{
    String idStr = request.getParameter("id");
    int id = 0;
    if(idStr != null && !"".equals(idStr)){
      id = Integer.parseInt(idStr);
    }
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Cache-Control","maxage=3600");
    response.setHeader("Pragma","public");
    PrintWriter out = response.getWriter();
    out.print("<?xml version=\'1.0\' encoding=\'utf-8'?>");
    out.print("<menus>");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("deptId", id);
      List<T9Person> list = orm.loadListSingle(dbConn, T9Person.class, map);
      for(T9Person d : list){
        out.print("<menu>");
        out.print("<id>" + d.getSeqId() + "</id>");
        out.print("<name>" + d.getUserId() + "</name>");
        out.print("<parentId>" + d.getDeptId() + "</parentId>");
        out.print("<isHaveChild>0</isHaveChild>");
        out.print("</menu>");
      }
      map.remove("deptId");
      map.put("deptParent", id);
      List<T9Department> deptList = orm.loadListSingle(dbConn, T9Department.class, map);
      for(T9Department t : deptList) {
        out.print("<menu>");
        out.print("<id>" + t.getSeqId() + "</id>");
        out.print("<name>" + t.getDeptName() + "</name>");
        out.print("<parentId>" + t.getDeptParent() + "</parentId>");
        out.print("<isHaveChild>" + IsHaveChild(request, response, String.valueOf(t.getSeqId())) + "</isHaveChild>");
        out.print("</menu>");      
      }    
      out.print("<parentNodeId>" + id + "</parentNodeId>");
      out.print("<count>" + (list.size()+deptList.size()) + "</count>");
      out.print("</menus>");
      out.flush();
      out.close();
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public int IsHaveChild(HttpServletRequest request,
      HttpServletResponse response,String id) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      List funcList = new ArrayList();
      funcList.add("sysFunction");
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      String length = null;
      
      if (dbms.equals("sqlserver")) {
        length = "len";
      }else if (dbms.equals("mysql")) {
        length = "length";
      }else if (dbms.equals("oracle")) {
        length = "length";
      }else {
        throw new SQLException("not accepted dbms");
      }
      
      String[] filters = new String[]{"MENU_ID like '" + id + "%'"," " + length + "(MENU_ID) > " + id.length() + ""};
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      //map.put("menuId", id);
      List<Map> list = (List<Map>) map.get("SYS_FUNCTION");
      //List<T9SysFunction> list = orm.loadListSingle(dbConn, T9SysFunction.class, map);
      if(list.size() > 0){
        return 1;
      }else{
        return 0;
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  
  public String getNoTreeOnce(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int userId = Integer.parseInt(request.getParameter("userId"));
      
      String sPriv = getPrivString(dbConn,userId);
      
      //"10,1077,107710,107733";
      String[] privArray = sPriv.split(",");
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9SysMenu> menuList = null;
      String[] filterMenuId = new String[]{" 1=1 order by MENU_ID"};
      menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, filterMenuId);
      for(int i = 0; i < menuList.size(); i++){
        T9SysMenu menu = menuList.get(i);
        sb.append("{");
        sb.append("nodeId:\"" + menu.getMenuId() + "\"");
        sb.append(",name:\"" + menu.getMenuName() + "\"");
        sb.append(",isHaveChild:" + IsHaveChild(request, response, String.valueOf(menu.getMenuId())));
        sb.append(",isChecked:" + isChecked(privArray,(String) menu.getMenuId()));
        sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/menuIcon/" + menu.getImage() + "\"");
        sb.append("},");
      }       
      List funcList = new ArrayList();
      funcList.add("sysFunction");
      String[] filters = new String[]{};
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("SYS_FUNCTION");
      String contextPath = request.getContextPath();
      for(Map m : list){
        String funcAddress = null;
        String imageAddress = null;
        String fun = (String) m.get("funcCode");
        if (fun == null) {
          continue;
        }
        imageAddress = contextPath + "/core/funcs/display/img/org.gif";
        if (fun.startsWith("/")) {
          funcAddress = contextPath + fun;
        }else {
          funcAddress = contextPath + "/core/funcs/" + fun + "/";
        }
        sb.append("{");
        sb.append("nodeId:\"" + m.get("menuId") + "\"");
        sb.append(",name:\"" + m.get("funcName") + "\"");
        sb.append(",isChecked:" + isChecked(privArray,(String) m.get("menuId")));
        sb.append(",isHaveChild:" + IsHaveChild(request, response, String.valueOf(m.get("menuId"))));
        sb.append(",imgAddress:\"" + imageAddress + "\"");
        sb.append("},");
      } 
      sb.deleteCharAt(sb.length() - 1);       
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getPrivString(Connection dbConn, int userId) throws Exception{
    List<T9UserPriv> positionPrivList = null;
    List funcList = new ArrayList();
    T9ORM orm = new T9ORM();
    String positionPriv = "";
    Map map1 = new HashMap();
    map1.put("SEQ_ID", userId);
    positionPrivList = orm.loadListSingle(dbConn, T9UserPriv.class, map1);
    for(int j = 0; j < positionPrivList.size(); j++){
      T9UserPriv poPriv = positionPrivList.get(j);
      int positionSeqIDD = poPriv.getSeqId();
      positionPriv =  poPriv.getFuncIdStr();
      if(positionPriv == null){
        positionPriv = "";
      }
    }
    return positionPriv;
  }
  
  public boolean isChecked(String[] privArray,String id){
    for(int i = 0 ;i < privArray.length; i++){
      if(id.equals(privArray[i])){
        return true;
      }
    }
    return false;
  }
  
  public String getPrivTreeData(HttpServletRequest request, HttpServletResponse response)
  throws Exception{
    Connection dbConn = null;
    try{
      int privNo = Integer.parseInt(request.getParameter("userId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("SEQ_ID", privNo);
      Object obj = orm.loadObjSingle(dbConn, T9UserPriv.class, map);
      data = T9FOM.toJson(obj).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSelectData(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //int userId = Integer.parseInt(request.getParameter("userId"));
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9UserPriv> perList = null;
      String[] filters = new String[]{"1 = 1 order by PRIV_NO, PRIV_NAME ASC"};
      //perList = (ArrayList<T9UserPriv>)orm.loadListSingle(dbConn, T9UserPriv.class, null);
      List funcList = new ArrayList();
      funcList.add("userPriv");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("USER_PRIV");
      for(Map m : list) {
      //for(int i = 0; i < perList.size(); i++){
      //T9UserPriv menu = perList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + m.get("seqId") + "\"");
        sb.append(",privNo:\"" + m.get("privNo") + "\"");
        sb.append(",privName:\"" + (m.get("privName") == null ? "" : T9Utility.encodeSpecial(String.valueOf(m.get("privName")))) + "\"");
        sb.append("},");
      }       
      sb.deleteCharAt(sb.length() - 1);       
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateSelectPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String priv_str = request.getParameter("privSeqId");
      String no_str = request.getParameter("privNo");
      String[] privSeqId = priv_str.split(",");
      String[] privNo = no_str.split(",");
      T9ORM orm = new T9ORM();
      Map m = new HashMap();
      String privNO = "";
      String seqId = "";
      for(int x = 0; x < privSeqId.length; x++){
        seqId = privSeqId[x];
        privNO = privNo[x];
        m.put("seqId", seqId);
        m.put("privNo", privNO);
        orm.updateSingle(dbConn, "userPriv", m);
        T9UserPrivCache.removeUserPrivCache(Integer.parseInt(seqId));
        if (T9ReportSyncLogic.hasSync) {
          T9UserPriv up = (T9UserPriv)orm.loadObjSingle(dbConn, T9UserPriv.class, Integer.parseInt(seqId));
          Connection reportConn = T9ReportSyncLogic.getReportConn();
          T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
          logic.editUserPriv(up, reportConn);
          if (reportConn != null) {
            reportConn.close();
          }
        }
        if (T9OaSyncLogic.hasSync) {
          T9UserPriv up = (T9UserPriv)orm.loadObjSingle(dbConn, T9UserPriv.class, Integer.parseInt(seqId));
          Connection reportConn = T9OaSyncLogic.getOAConn();
          T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
          logic.editUserPriv(up, reportConn);
          if (reportConn != null) {
            reportConn.close();
          }
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "调整角色排序成功");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUserName(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //int userId = Integer.parseInt(request.getParameter("userId"));
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9UserPriv> perList = null;
      String[] filters = new String[]{"1 = 1 order by PRIV_NO ASC"};
      //perList = (ArrayList<T9UserPriv>)orm.loadListSingle(dbConn, T9UserPriv.class, null);
      List funcList = new ArrayList();
      funcList.add("userPriv");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("USER_PRIV");
      for(Map m : list) {
      //for(int i = 0; i < perList.size(); i++){
      //T9UserPriv menu = perList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + m.get("seqId") + "\"");
        sb.append(",privNo:\"" + m.get("privNo") + "\"");
        sb.append(",privName:\"" + (m.get("privName") == null ? "" : T9Utility.encodeSpecial(String.valueOf(m.get("privName")))) + "\"");
        sb.append("},");
      }       
      sb.deleteCharAt(sb.length() - 1);       
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getPrivTree(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //int userId = Integer.parseInt(request.getParameter("userId"));
      //String sPriv = getPrivString(dbConn,userId);
      //"10,1077,107710,107733";
      //String[] privArray = sPriv.split(",");
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9SysMenu> menuList = null;
      String[] filterMenuId = new String[]{" 1=1 order by MENU_ID"};
      menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, filterMenuId);
      for(int i = 0; i < menuList.size(); i++){
        T9SysMenu menu = menuList.get(i);
        sb.append("{");
        sb.append("nodeId:\"" + menu.getMenuId() + "\"");
        sb.append(",name:\"" + menu.getMenuName() + "\"");
        sb.append(",isHaveChild:" + IsHaveChild(request, response, String.valueOf(menu.getMenuId())));
        //sb.append(",isChecked:" + isChecked(privArray,(String) menu.getMenuId()));
        sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/menuIcon/" + menu.getImage() + "\"");
        sb.append("},");
      }       
      List funcList = new ArrayList();
      funcList.add("sysFunction");
      String[] filters = new String[]{};
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("SYS_FUNCTION");
      String contextPath = request.getContextPath();
      for(Map m : list){
        String funcAddress = null;
        String imageAddress = null;
        String fun = (String) m.get("funcCode");
        if (fun == null) {
          continue;
        }
        imageAddress = contextPath + "/core/funcs/display/img/org.gif";
        if (fun.startsWith("/")) {
          funcAddress = contextPath + fun;
        }else {
          funcAddress = contextPath + "/core/funcs/" + fun + "/";
        }
        sb.append("{");
        sb.append("nodeId:\"" + m.get("menuId") + "\"");
        sb.append(",name:\"" + m.get("funcName") + "\"");
        //sb.append(",isChecked:" + isChecked(privArray,(String) m.get("menuId")));
        sb.append(",isHaveChild:" + IsHaveChild(request, response, String.valueOf(m.get("menuId"))));
        sb.append(",imgAddress:\"" + imageAddress  + "\"");
        sb.append("},");
      } 
      sb.deleteCharAt(sb.length() - 1);       
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String usertAddDeleltPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //int seqId = Integer.parseInt(request.getParameter("treeId"));
      int user_op = Integer.parseInt(request.getParameter("user_op"));
      String treeNo = request.getParameter("treeNo");
      String userPrivSeqId = request.getParameter("userPrivSeqId");
      String funcIdStr = "";
      String userPrivSeqID = "";
      //ArrayStack prayArrId = null;
      boolean isHave = false;
      T9ORM orm = new T9ORM();
      Map m = new HashMap();
      String treeNO[] = treeNo.split(",");
      String userSeqId[] = userPrivSeqId.split(",");
      T9UserPrivLogic upl = new T9UserPrivLogic();
      String str = "";
      if(user_op == 0){
        for(int x = 0; x < userSeqId.length; x++){
          userPrivSeqID = userSeqId[x];
          str = upl.selectUserPrivFun(dbConn, userPrivSeqID);
          if(str == null||str.equals("")){
           // for(int i = 0; i < treeNO.length; i++){
             // funcIdStr = treeNO[i];
            //  if(str != null && str != ""){
            //    str += ",";
             // }
            str = treeNo;
           // }
            m.put("seqId", userPrivSeqID);
            m.put("funcIdStr", str);
            orm.updateSingle(dbConn, "userPriv", m);
          }else{
            String func[] = str.split(",");
            for(int i = 0; i < treeNO.length; i++){
              funcIdStr = treeNO[i];
              for(int y = 0; y < func.length; y++){
                String funcStr = func[y];
                if(funcStr.equals(funcIdStr) && !T9Utility.isNullorEmpty(funcStr)){
                  isHave = true;
                  break;
                }
              }
              if(!isHave){
                if(!T9Utility.isNullorEmpty(str)){
                  str += ",";
                }
                str += funcIdStr;
              }
              isHave = false;
            }
            m.put("seqId", userPrivSeqID);
            m.put("funcIdStr", str);
            orm.updateSingle(dbConn, "userPriv", m);
          }
        }
      }else{
        for(int x = 0; x < userSeqId.length; x++){
          String newStr = "";
          userPrivSeqID = userSeqId[x];
          str = upl.selectUserPrivFun(dbConn, userPrivSeqID);
          if(T9Utility.isNullorEmpty(str)){
            
          }else{
            String func[] = str.split(",");
            for(int i = 0; i < treeNO.length; i++){
              funcIdStr = treeNO[i];
              List<String> tmp = new ArrayList<String>();
              for(int y = 0; y < func.length; y++){
                String funcStr = func[y];
                if(funcIdStr.equals(func[y]) == false){
                  tmp.add(func[y]);
                }
              }
              func = new String[tmp.size()];
              int j = 0 ;
              for(String t : tmp){
                func[j] = t;
                j++;
              }
              tmp = null;
            }
            String funcStr = "";
            for(int i = 0 ; i < func.length ; i++){
              if(!T9Utility.isNullorEmpty(funcStr)){
                funcStr += ",";
              }
              funcStr += func[i];
            }
            m.put("seqId", userPrivSeqID);
            m.put("funcIdStr", funcStr);
            orm.updateSingle(dbConn, "userPriv", m);
          }
        }
      }
      //for(int i = 0; i < lidd.size(); i++){
       //ment =  (T9Department) deptlogic.deleteDeptMul(dbConn, seqId).get(i);
        //String a = (String) lidd.get(i);
       //T9Department deptent = (T9Department) lidd.get(i);
        //String a = (String) lidd.get(i);
        //System.out.println(deptent.getSeqId()+"xxxxxxxxxxxxxxxxxxxxxx");
       // dt = (T9Department)orm.loadObjComplex(dbConn, T9Department.class, deptent.getSeqId());
       // System.out.println("T9Department : " + dt);
       // dt.setSeqId(deptent.getSeqId());
        //orm.deleteComplex(dbConn, dt);
      //}
     // T9Department dt = (T9Department)orm.loadObjComplex(dbConn, T9Department.class, seqId);
      //T9Department dpt = (T9Department) T9FOM.build(request.getParameterMap());
      //dt.setSeqId(seqId);
      //orm.deleteComplex(dbConn, dt);
      if (T9ReportSyncLogic.hasSync) {
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.syncUserPriv(dbConn, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        Connection reportConn = T9OaSyncLogic.getOAConn();
        T9UserPrivSyncLogic logic = new T9UserPrivSyncLogic();
        logic.syncUserPriv(dbConn, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      T9UserPrivCache.removeAll();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功数据库的数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String otherPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int user_op = Integer.parseInt(request.getParameter("user_op"));
      String userPrivs = T9Utility.null2Empty(request.getParameter("treeNo"));
      String users = T9Utility.null2Empty(request.getParameter("userPrivSeqId"));
      T9UserPrivLogic logic = new T9UserPrivLogic();
      if(user_op == 0){
        logic.addUserPriv(dbConn, users, userPrivs);
      }else{
        logic.getOutPriv(dbConn, users, userPrivs);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getAutoData(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ORM orm = new T9ORM();
      HashMap map = null;
      T9FlowFormLogic dtt = new T9FlowFormLogic();
      int seqID = dtt.deleteDeptMul(dbConn, seqId);
      String func = "";
      String dd = dtt.deleteDept(dbConn, seqID);
      String[]str = dd.split(",");
      StringBuffer sb = null;
      List<Map> list = new ArrayList();
      boolean ma = true;
      ArrayList<T9UserPriv> perList = null;
      sb = new StringBuffer("[");
      for(int i = 0; i < str.length; i++){
        func = str[i];
        String[] filters = new String[]{"SEQ_ID="+func};
        List funcList = new ArrayList();
        funcList.add("person");
        map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
        list.addAll((List<Map>) map.get("PERSON"));
      }
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",userName:\"" + m.get("userName") + "\"");
          sb.append(",userPriv:\"" + m.get("userPriv") + "\"");
          sb.append("},");
        }       
        sb.deleteCharAt(sb.length() - 1);       
        sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 权限列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserPrivList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9UserPrivLogic upl = new T9UserPrivLogic();
      String data = upl.getUserPrivList(dbConn,request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String showReader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Notify notify = null;
    String seqId = request.getParameter("seqId");
    String displayAll = request.getParameter("displayAll");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPrivLogic notifyShowLogic = new T9UserPrivLogic();
      String data = notifyShowLogic.showReader(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getAllUsers(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9UserPrivLogic dl = new T9UserPrivLogic();
      String data = String.valueOf(dl.allUsers(dbConn, seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getOtherUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9UserPrivLogic dl = new T9UserPrivLogic();
      String data = String.valueOf(dl.otherUser(dbConn, seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getNotLoginUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9UserPrivLogic dl = new T9UserPrivLogic();
      String data = String.valueOf(dl.notLoginUser(dbConn, seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUserPrivNo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userPriv = request.getParameter("seqId");
      T9UserPrivLogic dl = new T9UserPrivLogic();
      String data = String.valueOf(dl.userPrivNo(dbConn, userPriv));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
