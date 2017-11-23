package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowSort;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowSortLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9FlowSortAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.flowsort.act.T9FlowSortAct");
  public String getSortTreeJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowSortLogic flowSort = new T9FlowSortLogic();
      String data = flowSort.getSortTreeJson(dbConn, loginUser, 0 ,  false);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出流程分类数据！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSortName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowSortLogic flowSort = new T9FlowSortLogic();
      String data = flowSort.getSortTreeJson(dbConn, loginUser, 0 , false);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出流程分类数据！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String listFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowSortLogic flowSort = new T9FlowSortLogic();
      String data = flowSort.getSortTreeJson(dbConn, loginUser, 0 , true);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出流程分类数据！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    String data = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
  
      T9FlowSort flowSort = null;
     
      T9ORM orm = new T9ORM();
      flowSort = (T9FlowSort)orm.loadObjSingle(dbConn, T9FlowSort.class, Integer.parseInt(seqId));
      if(flowSort == null) {
        flowSort = new T9FlowSort();
      }

      StringBuffer sb = new StringBuffer("{");
      sb.append("seqId:\"" + flowSort.getSeqId() + "\"");
      sb.append(",sortNo:\"" + flowSort.getSortNo() + "\"");
      sb.append(",sortName:\"" + flowSort.getSortName() + "\"");
      sb.append(",deptId:\"" + flowSort.getDeptId() + "\"");
      sb.append("}");
      data = sb.toString();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception  {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
      T9ORM orm = new T9ORM();
      String sortNo = request.getParameter("sortNo");
      String sortParent = request.getParameter("sortParent");
      String sortName = request.getParameter("sortName");
      String deptId = request.getParameter("deptId");
      
      String haveChild = "0";
      
      if(!sortParent.equals("0")) {
        String no = "1";
        Map map = new HashMap();
        map.put("seqId", sortParent);
        map.put("haveChild", no);
        orm.updateSingle(dbConn, "flowSort", map);
      }
      Map map = new HashMap();
      map.put("sortNo", sortNo);
      map.put("sortParent", sortParent);
      map.put("sortName", sortName);
      map.put("deptId", deptId);
      map.put("haveChild", haveChild);
      
      orm.saveSingle(dbConn, "flowSort", map);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加流程分类");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception  {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      
      T9FlowSort flowSort = (T9FlowSort)T9FOM.build(request.getParameterMap());
      
      T9ORM orm = new T9ORM(); 
      orm.updateSingle(dbConn, flowSort);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"流程分类修改成功!");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String deleteFlowSort(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");  
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("seqId", seqId);
      orm.deleteSingle(dbConn, "flowSort", map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除流程分类！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getGradeTree(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String idStr = request.getParameter("id");
    int id = 0;
    if (idStr != null && !"".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }
    
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
          
      T9ORM orm = new T9ORM();
      HashMap map = new HashMap();
      map.put("DEPT_PARENT", id);
      StringBuffer sb = new StringBuffer("[");
      
      ArrayList<T9Department> deptList = null;
      deptList = (ArrayList<T9Department>)orm.loadListSingle(dbConn, T9Department.class, map);
      
      for(int i = 0; i < deptList.size(); i++) {
        T9Department dept = deptList.get(i);
        sb.append("{");
        sb.append("nodeId:\"" + dept.getSeqId() + "\"");
        sb.append(",name:\"" + dept.getDeptName() + "\"");
        sb.append(",isHaveChild:" + IsHaveSon(request, response, String.valueOf(dept.getSeqId())) + "");
        sb.append(",imgAddress:\""+ request.getContextPath() +"/core/styles/style1/img/dtree/node_dept.gif\"");
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
  /**
   * 主要是流程管理时，右边的滑动菜单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSortList(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String idStr = request.getParameter("id");
    int id = 0;
    if(idStr!=null && !"".equals(idStr)){
      id = Integer.parseInt(idStr);
    }
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       stmt = dbConn.createStatement();
       T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
       String deptIds = user.getDeptId() + ",0";
       String deptOther = user.getDeptIdOther();
       if (!T9Utility.isNullorEmpty(deptOther) ) {
         deptIds = deptIds + "," + deptOther;
         deptIds = T9WorkFlowUtility.getOutOfTail(deptIds);
       }
       int count = 0 ;
       StringBuffer sb = new StringBuffer("[");
       T9FlowTypeLogic ft = new T9FlowTypeLogic();
       T9WorkFlowUtility w = new T9WorkFlowUtility();
       if (id != 0 ) {
         List<T9FlowType> typeList = ft.getFlowTypeList(id , dbConn);
         for(T9FlowType ftTmp : typeList){
           int deptId = ftTmp.getDeptId();
           if (!w.isHaveRight(deptId, user, dbConn)) {
             continue;
           }
           count++;
           int nodeId = ftTmp.getSeqId();
           String name = T9WorkFlowUtility.encodeSpecial(ftTmp.getFlowName());
           sb.append("{");
           sb.append("nodeId:\"T" + nodeId + "\"");
           sb.append(",name:\"" + name + "\"");
           sb.append(",isHaveChild:0");
           sb.append(",imgAddress:\""+ request.getContextPath() +"/core/funcs/workflow/flowtype/img/workflow.gif\"");
           sb.append("},");
         }
       }
       String queryStr = "select SEQ_ID , SORT_NAME , SORT_NO  from FLOW_SORT where SORT_PARENT =" + id ;
       if (!user.isAdminRole()) {
         queryStr += " AND  DEPT_ID IN (" + deptIds + ") " ;
       }
       queryStr += " order by SORT_NO";
       rs = stmt.executeQuery(queryStr);
       while(rs.next()){
          int nodeId = rs.getInt("SEQ_ID");
          
          boolean flag =  IsHaveChild( dbConn ,  nodeId ,  user , ft ,   w ,  deptIds);
          String name = rs.getString("SORT_NAME");
          if (flag) {
            sb.append("{");
            sb.append("nodeId:\"" + nodeId + "\"");
            sb.append(",name:\"" + name + "\"");
            sb.append(",isHaveChild:1");
            sb.append(",imgAddress:\""+ request.getContextPath() +"/core/funcs/workflow/flowtype/img/folder.gif\"");
            sb.append("},");
            count++;
          }
        }
       if (count > 0) {
         sb.deleteCharAt(sb.length() - 1);
       }
        sb.append("]");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建工作时，左边的列表，主要是考虑了权限的问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSortListR(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String idStr = request.getParameter("id");
    int id = 0;
    if(idStr!=null && !"".equals(idStr)){
      id = Integer.parseInt(idStr);
    }
    Statement stmt = null;
    ResultSet rs = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      sb.append("[");
      int count = 0;
      if (id != 0) {
        T9FlowTypeLogic ft = new T9FlowTypeLogic();
        List<T9FlowType> typeList = ft.getFlowTypeList(id , dbConn);
        T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
        for(T9FlowType ftTmp : typeList){
          //如果是自由流程，只判断新建权限，如果是固定流程需判断第一步的经办权限， 如果第一步经办权限没设或没有第一步，只不会显示
          boolean flag = false;
          flag = tru.prcsRole(ftTmp, 0, user, dbConn);
          if(flag){
            int nodeId = ftTmp.getSeqId();
            String name = ftTmp.getFlowName();
            sb.append("{");
            sb.append("nodeId:\"F" + nodeId + "\"");
            sb.append(",name:\"" + name + "\"");
            sb.append(",isHaveChild:0");
            sb.append(",imgAddress:\""+ request.getContextPath() +"/core/funcs/workflow/flowtype/img/workflow.gif\"");
            sb.append("},");
            count++;
          }
        }
      }
      String queryStr = "select SEQ_ID , SORT_NAME , SORT_NO  from FLOW_SORT where SORT_PARENT =" + id ;
      queryStr += " order by SORT_NO";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
         int nodeId = rs.getInt("SEQ_ID");
         String name = rs.getString("SORT_NAME");
         if (this.isHaveFlow(dbConn, nodeId , user)) {
           sb.append("{");
           sb.append("nodeId:\"" + nodeId + "\"");
           sb.append(",name:\"" + name + "\"");
           sb.append(",isHaveChild:1");
           sb.append(",imgAddress:\""+ request.getContextPath() +"/core/funcs/workflow/flowtype/img/folder.gif\"");
           sb.append("},");
           count++;
         }
       }
      if(count > 0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }
  public boolean isHaveFlow(Connection conn  , int sortId , T9Person user ) throws Exception {
    T9FlowTypeLogic ft = new T9FlowTypeLogic();
    T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
    List<T9FlowType> typeList = ft.getFlowTypeList(String.valueOf(sortId) , conn);
    boolean flag = false;
    for(T9FlowType ftTmp : typeList){
      flag = tru.prcsRole(ftTmp, 0, user, conn);
      if (flag) {
        return flag;
      }
    }
    List<Integer> seqIds = new ArrayList<Integer>();
    String queryStr = "select SEQ_ID  from FLOW_SORT where SORT_PARENT =" + sortId ;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
         int nodeId = rs.getInt("SEQ_ID");
         seqIds.add(nodeId);
       }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    for (int id : seqIds) {
      flag = this.isHaveFlow(conn, id, user);
       if (flag) {
          return flag;
       }
    }
    return flag;
  }
  /**
   * 新建工作时，左边的列表，主要是考虑了权限的问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowType(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sSortId = request.getParameter("sortId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      List<T9FlowSort> list  = new ArrayList();
      if (sSortId == null || "".equals(sSortId)) {
        list = fs.getFlowSort(dbConn);
      } else {
        sSortId = T9WorkFlowUtility.getInStr(sSortId);
        list =  fs.getFlowSortByIds(dbConn , sSortId);
      }
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      sb.append("[");
      int count = 0;
      long date1 = System.currentTimeMillis();
      for(T9FlowSort tmp : list){
        List<T9FlowType> typeList = ft.getFlowTypeList(tmp.getSeqId()+ "" , dbConn);
        T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
        for(T9FlowType ftTmp : typeList){
          //如果是自由流程，只判断新建权限，如果是固定流程需判断第一步的经办权限， 如果第一步经办权限没设或没有第一步，只不会显示          boolean flag = false;
          flag = tru.prcsRole(ftTmp, 0, user, dbConn);
          if(flag){
            sb.append("{");
            sb.append("flowName:'" + ftTmp.getFlowName() + "'");
            sb.append(",flowId:'" + ftTmp.getSeqId() +"'}," );
            count++;
          }
        }
      }
      long date2 = System.currentTimeMillis();
      long date3 = date2 - date1;
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
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
  public String getFlowBySortId(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sortId = request.getParameter("sortId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      sb.append("[");
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      List<T9FlowType> typeList = ft.getFlowTypeList(Integer.parseInt(sortId) , dbConn);
      int count = 0 ;
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      for(T9FlowType ftTmp : typeList){
        int deptId = ftTmp.getDeptId();
        if (!w.isHaveRight(deptId, u, dbConn)) {
          continue;
        }
        count ++;
        if(ftTmp.getFlowName() == null){
          sb.append("{title:' '");
        }else{ 
          sb.append("{title:'" + ftTmp.getFlowName() + "'");
        }
       // if(hasIcon){
      sb.append(",icon:imgPath + '/edit.gif'");
        //}
      sb.append(",action:actionFuntion");
      sb.append(",iconAction:iconActionFuntion");
      sb.append(",extData:'" + ftTmp.getSeqId() +":"+ ftTmp.getFlowType() +"'}," );
    }
    if(count > 0){
      sb.deleteCharAt(sb.length() - 1);
    }
      sb.append("]");
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, sortId);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 表单管理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFromSortList(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String idStr = request.getParameter("id");
    int id = 0;
    if(idStr!=null && !"".equals(idStr)){
      id = Integer.parseInt(idStr);
    }
    Statement stmt = null;
    ResultSet rs = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      int count = 0;
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      sb.append("[");
      if (id != 0) {
        T9FlowSortLogic fs = new T9FlowSortLogic();
        int id2 = id;
        if (id2 == -1) {
          id2 = 0;
        } 
        String tb = fs.getSortJson(id2, dbConn , user , request.getContextPath());
        if (!"".equals(tb)) {
          count++;
        }
        sb.append(tb);
      }
      String queryStr = "select SEQ_ID , SORT_NAME , SORT_NO  from FLOW_SORT where SORT_PARENT =" + id ;
      if (!user.isAdminRole()) {
        String deptIds = user.getDeptId() + ",0";
        String deptOther = user.getDeptIdOther();
        if (!T9Utility.isNullorEmpty(deptOther) ) {
          deptIds = deptIds + "," + deptOther;
          deptIds = T9WorkFlowUtility.getOutOfTail(deptIds);
        }
        queryStr += " AND  DEPT_ID IN (" + deptIds + ") " ;
      }
      queryStr += " order by SORT_NO";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while(rs.next()){
         int nodeId = rs.getInt("SEQ_ID");
         String name = rs.getString("SORT_NAME");
         sb.append("{");
         sb.append("nodeId:\"" + nodeId + "\"");
         sb.append(",name:\"" + name + "\"");
         sb.append(",isHaveChild:1");
         sb.append(",imgAddress:\""+ request.getContextPath() +"/core/funcs/workflow/flowtype/img/folder.gif\"");
         sb.append("},");
         count++;
      }
      if (id == 0) {
        sb.append("{");
        sb.append("nodeId:\"-1\"");
        sb.append(",name:\"未分类\"");
        sb.append(",isHaveChild:1");
        sb.append(",imgAddress:\""+ request.getContextPath() +"/core/funcs/workflow/flowtype/img/folder.gif\"");
        sb.append("},");
        count++;
      }
      if(count > 0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } finally {
        T9DBUtility.close(stmt, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSortId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sortName = request.getParameter("sortName");
    String sql = "select seq_id from flow_sort where sort_name = '" + sortName + "'"; 
    int result = 0;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(sql);
        if (rs.next()) {
          result = rs.getInt("seq_id");
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm , rs , null);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, "'" +result + "'");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  public String getSortIds(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sortName = request.getParameter("sortName");
    String sortNamesNew = "";
    if (!T9Utility.isNullorEmpty(sortName)) {
      String[] news = sortName.split(",");
      for (String tmp : news) {
        if (!T9Utility.isNullorEmpty(tmp)) {
          sortNamesNew += "'" + tmp + "',";
        }
      }
    }
    if (sortNamesNew.endsWith(",")) {
      sortNamesNew = sortNamesNew.substring(0, sortNamesNew.length() - 1);
    }
    String result = "";
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if (!"".equals(sortNamesNew)) {
        String sql = "select seq_id from flow_sort where sort_name in (" + sortNamesNew + ")"; 
        Statement stm = null;
        ResultSet rs = null;
        try {
          stm = dbConn.createStatement();
          rs = stm.executeQuery(sql);
          while (rs.next()) {
            result += rs.getInt("seq_id") + ",";
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm , rs , null);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, "'" +result + "'");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  public boolean IsHaveChild(Connection conn , int nodeId , T9Person user ,T9FlowTypeLogic ft ,  T9WorkFlowUtility w , String deptIds) throws Exception {
    List<T9FlowType> typeList = ft.getFlowTypeList(nodeId , conn);
    boolean flag = false;
    for(T9FlowType ftTmp : typeList){
      int deptId = ftTmp.getDeptId();
      if (w.isHaveRight(deptId, user, conn)) {
        flag = true;
        return flag;
      }
    }
    String queryStr2 = "select 1  from FLOW_SORT where SORT_PARENT =" + nodeId ;
    if (!user.isAdminRole()) {
      queryStr2 += " AND  DEPT_ID IN (" + deptIds + ") " ;
    }
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(queryStr2);
      if (rs.next()) {
        flag = true;
        return flag;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return flag;
  }
  public int IsHaveSon(HttpServletRequest request,
      HttpServletResponse response,String id) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("DEPT_PARENT", id);
      List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class, map);
      if(list.size() > 0){
        return 1;
      }else{
        return 0;
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  public String getDeptList(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DeptLogic deptLogic = new T9DeptLogic();
      String deptList =  deptLogic.getDeptTreeJson(u , dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA, deptList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public T9FlowSort mapToFlowSort (Map mapListNext){
    T9FlowSort sort = new T9FlowSort();
    sort.setSeqId((Integer)mapListNext.get("seqId"));
    sort.setSortNo((Integer)mapListNext.get("sortNo"));
    sort.setSortName((String)mapListNext.get("sortName"));
    sort.setSortParent((Integer)mapListNext.get("sortParent"));
    sort.setHaveChild((String)mapListNext.get("haveChild"));
    sort.setDeptId((Integer)mapListNext.get("deptId"));
    return sort;
  }
}
