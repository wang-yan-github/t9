package t9.core.funcs.doc.act;

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
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowSort;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowSortLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9FlowSortAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.doc.flowsort.act.T9FlowSortAct");
  
  public String getSortName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      ArrayList<T9DocFlowSort> sortList = null;
      T9DocFlowSort flowSort = null;
      T9ORM orm = new T9ORM();
      StringBuffer sb = new StringBuffer("[");
      String data = "";
      Map m = null;
      sortList = (ArrayList<T9DocFlowSort>)orm.loadListSingle(dbConn, T9DocFlowSort.class, m);
      if(sortList.size() > 0) {
        for(int i = 0; i < sortList.size(); i++) {
          flowSort = sortList.get(i);
          if ((!loginUser.isAdminRole() 
              && flowSort.getDeptId() != loginUser.getDeptId()) 
              && flowSort.getDeptId() != 0) {
            continue;
          }
          if(flowSort.getSortParent() == 0) {
            sb.append("{");
            sb.append("seqId:\"" + flowSort.getSeqId() + "\"");
            sb.append(",sortName:\"" + flowSort.getSortName() + "\"");
            sb.append("},");          
          }
          
        }
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      data = sb.toString();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出流程父分类数据！");
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
      T9ORM orm = new T9ORM();
      HashMap map = null;
      HashMap flowSortMap = null;
      ArrayList<T9DocFlowSort> flowSortList = new ArrayList<T9DocFlowSort>();
           
      List sortList = new ArrayList();
      sortList.add("docFlowSort");
      Map map1 =new HashMap();
      //map1.put("SORT_PARENT", "0") ;
      //map = (HashMap)orm.loadDataSingle(dbConn, sortList, filters);
      List<T9DocFlowSort> list = orm.loadListSingle(dbConn, T9DocFlowSort.class , map1);
      Map countMap = new HashMap();
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      T9FlowSortLogic  logic = new T9FlowSortLogic();
      
      for(int i = 0; i < list.size(); i++) {
        T9DocFlowSort mapList = list.get(i);
//        if(mapList.get("haveChild") != null 
//            && mapList.get("haveChild").equals("1")) {
//          sort = new T9DocFlowSort();
//          flowSortList.add(this.mapToFlowSort(mapList));
//          int seqId = (Integer)mapList.get("seqId");
//          
//          countMap.put(mapList.get("seqId"), logic.getFlowTypeCount(seqId, dbConn));
//          flowSortList.add(sort);
//          String[] rules = new String[]{"SORT_PARENT = " + seqId + " order by SORT_NO"};
//          
//          List sortListNext = new ArrayList();
//          sortListNext.add("docFlowSort");
//          
//          flowSortMap = (HashMap)orm.loadDataSingle(dbConn, sortListNext, rules);
//          List<HashMap> sortMapList = (List<HashMap>) flowSortMap.get("DOC_FLOW_SORT");
//          for(int j = 0; j < sortMapList.size(); j++) {
//            HashMap mapListNext = sortMapList.get(j);
//            seqId = (Integer)mapListNext.get("seqId");
//            int deptId = (Integer)mapList.get("deptId");
//            if (!w.isHaveSortRight(deptId, loginUser, dbConn)) {
//              continue;
//            }
//            countMap.put(mapList.get("seqId"), logic.getFlowTypeCount(seqId, dbConn));
//            flowSortList.add(this.mapToFlowSort(mapListNext));
//          }         
//        }else {
          int seqId = mapList.getSeqId();
          int deptId = mapList.getDeptId();
          if (!w.isHaveSortRight(deptId, loginUser, dbConn)) {
            continue;
          }
          countMap.put(seqId, logic.getFlowTypeCount(seqId, dbConn));
          flowSortList.add(mapList);
       // }
      }
      request.setAttribute("countMap", countMap);
      request.setAttribute("flowSortList", flowSortList);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return T9WorkFlowConst.MODULE_CONTEXT_PATH + "/flowsort/flowsortlist.jsp";
  }
  
  public String getFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    String data = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
  
      T9DocFlowSort flowSort = null;
     
      T9ORM orm = new T9ORM();
      flowSort = (T9DocFlowSort)orm.loadObjSingle(dbConn, T9DocFlowSort.class, Integer.parseInt(seqId));
      if(flowSort == null) {
        flowSort = new T9DocFlowSort();
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
        String sql = "update  " + T9WorkFlowConst.FLOW_SORT + " set HAVE_CHILD = '1' WHERE SEQ_ID = " + sortParent;
        T9WorkFlowUtility.updateTableBySql(sql, dbConn);
      }
      T9DocFlowSort flowSort = new T9DocFlowSort();
      flowSort.setSortNo(Integer.parseInt(sortNo));
      flowSort.setSortParent(Integer.parseInt(sortParent));
      flowSort.setSortName(sortName);
      flowSort.setDeptId(Integer.parseInt(deptId));
      flowSort.setHaveChild(haveChild);
      orm.saveSingle(dbConn, flowSort);
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
      
      T9DocFlowSort flowSort = (T9DocFlowSort)T9FOM.build(request.getParameterMap());
      
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
    String haveChild = request.getParameter("haveChild"); 
    String sortParent = request.getParameter("sortParent"); 
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      
      Map map = new HashMap();
      ArrayList<T9DocFlowSort> sortList = null;
      if(haveChild.equals("0") && Integer.parseInt(sortParent) == 0) {
        String sql = "delete from  " + T9WorkFlowConst.FLOW_SORT + " WHERE SEQ_ID = " + seqId;
        T9WorkFlowUtility.updateTableBySql(sql, dbConn);
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除流程分类！");
        
        return "/core/inc/rtjson.jsp";
      }else if(haveChild.equals("0") && Integer.parseInt(sortParent) != 0) {
        String sql2 = "delete from  " + T9WorkFlowConst.FLOW_SORT + " WHERE SEQ_ID = " + seqId;
        T9WorkFlowUtility.updateTableBySql(sql2, dbConn);
        
        Map mapId = new HashMap();
        mapId.put("SORT_PARENT", sortParent);
        sortList = (ArrayList<T9DocFlowSort>)orm.loadListSingle(dbConn, T9DocFlowSort.class, mapId);
        if(sortList.size() == 0) {
          String sql = "update  " + T9WorkFlowConst.FLOW_SORT + " set HAVE_CHILD = '0' WHERE SEQ_ID = " + sortParent;
          T9WorkFlowUtility.updateTableBySql(sql, dbConn);
        }
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除流程分类！");
        
        return "/core/inc/rtjson.jsp";
      }else if(haveChild.equals("1")) {
        map.put("SORT_PARENT", seqId);
        sortList = (ArrayList<T9DocFlowSort>)orm.loadListSingle(dbConn, T9DocFlowSort.class, map);
        for(int i = 0; i < sortList.size(); i++) {
          T9DocFlowSort sortListModel = sortList.get(i);
          int seqIdSort = sortListModel.getSeqId();
          orm.deleteSingle(dbConn, T9DocFlowSort.class, seqIdSort);
        }
        
        int deleteId = Integer.parseInt(seqId);
        orm.deleteSingle(dbConn, T9DocFlowSort.class, deleteId);
      }
    
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
    Connection dbConn = null;
    String noIcon = request.getParameter("noIcon");
    boolean hasIcon = true;
    if(noIcon != null && !"".equals(noIcon)){
      hasIcon  = false;
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9DocFlowSort> list = fs.getFlowSortByDept(dbConn , u);
      /*
       * {title:'ddd',icon:'',action:getList,extData:'d',data:[
        {title:'ddd',icon:imgPath + '/edit.gif',iconAction:iconActionFuntion ,action:actionFuntion,extData:'2'}
        ,{title:'ddd',icon:imgPath + '/edit.gif',iconAction:iconActionFuntion ,action:actionFuntion,extData:'2'}
       */
      sb.append("[");
      String actionUrl = request.getContextPath() + T9WorkFlowConst.MODULE_SRC_PATH + "/act/T9FlowSortAct/getFlowBySortId.act?sortId=";
      for(T9DocFlowSort tmp : list){
        if(tmp.getSortName() == null){
          sb.append("{title:' '");
        }else{
          sb.append("{title:'" + tmp.getSortName() + "'");
        }
        sb.append(",action:getList");
        sb.append(",extData:'" + tmp.getSeqId() +"'" );
        sb.append(",data:[");
       // List<T9FlowType> typeList = ft.getFlowTypeList(tmp.getSeqId() , dbConn);
//        int count = 0 ;
//        T9WorkFlowUtility w = new T9WorkFlowUtility();
//        for(T9FlowType ftTmp : typeList){
//          int deptId = ftTmp.getDeptId();
//          if (!w.isHaveRight(deptId, u, dbConn)) {
//            continue;
//          }
//          count ++;
//          if(ftTmp.getFlowName() == null){
//            sb.append("{title:' '");
//          }else{ 
//            sb.append("{title:'" + ftTmp.getFlowName() + "'");
//          }
//          if(hasIcon){
//            sb.append(",icon:imgPath + '/edit.gif'");
//          }
//          sb.append(",action:actionFuntion");
//          sb.append(",iconAction:iconActionFuntion");
//          sb.append(",extData:'" + ftTmp.getSeqId() +":"+ ftTmp.getFlowType() +"'}," );
//        }
//        if(count > 0){
//          sb.deleteCharAt(sb.length() - 1);
//        }
        sb.append("],actionUrl:'"+actionUrl + tmp.getSeqId() +"'},");
      }
      if(list.size() > 0){
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
  
  /**
   * 主要是流程管理时，右边的滑动菜单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String id = request.getParameter("sortId");
    String noIcon = request.getParameter("noIcon");
    boolean hasIcon = true;
    if(noIcon != null && !"".equals(noIcon)){
      hasIcon  = false;
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb2 = new StringBuffer("[");
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9DocFlowType> typeList = ft.getFlowTypeList(id , dbConn);
      
      T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
      int flowTypeCount = 0;
      for(T9DocFlowType ftTmp : typeList){
        //如果是自由流程，只判断新建权限，如果是固定流程需判断第一步的经办权限， 如果第一步经办权限没设或没有第一步，只不会显示
        boolean flag = false;
        flag = tru.prcsRole(ftTmp, 0, user, dbConn);
        if(flag){
          if(ftTmp.getFlowName() == null){
            sb2.append("{title:''");
          }else{ 
            sb2.append("{title:'" + ftTmp.getFlowName() + "'");
          }
          if(hasIcon){
            sb2.append(",icon:imgPath + '/edit.gif'");
          }
          sb2.append(",action:actionFuntion");
          sb2.append(",iconAction:iconActionFuntion");
          sb2.append(",extData:'" + ftTmp.getSeqId() +"'}," );
          flowTypeCount++;
        }
      }
      if(flowTypeCount > 0){
        sb2.deleteCharAt(sb2.length() - 1);
      }
      sb2.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb2.toString());      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
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
    String noIcon = request.getParameter("noIcon");
    boolean hasIcon = true;
    if(noIcon != null && !"".equals(noIcon)){
      hasIcon  = false;
    }
    String sSortId = request.getParameter("sortId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      List<T9DocFlowSort> list  = new ArrayList();
      if (sSortId == null || "".equals(sSortId)) {
        list = fs.getFlowSort(dbConn);
      } else {
        sSortId = T9WorkFlowUtility.getInStr(sSortId);
        list =  fs.getFlowSortByIds(dbConn , sSortId);
      }
      String url = request.getContextPath()  + T9WorkFlowConst.MODULE_SRC_PATH + "/act/T9FlowSortAct/getFlowTypeList.act?noIcon=1&sortId=";
      //T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      sb.append("[");
      int count = 0;
      
      for(T9DocFlowSort tmp : list){
        count ++ ;
        if(tmp.getSortName() == null){
          sb.append("{title:' '");
        }else{
          sb.append("{title:'" + tmp.getSortName() + "'");
        }
        sb.append(",action:getList");
        sb.append(",extData:'" + tmp.getSeqId() +"'" );
        sb.append(",data:[");
        sb.append("],actionUrl:'"+url+ tmp.getSeqId() +"'},");
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
  public String getFlowType(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sSortId = request.getParameter("sortId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      List<T9DocFlowSort> list  = new ArrayList();
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
      for(T9DocFlowSort tmp : list){
        List<T9DocFlowType> typeList = ft.getFlowTypeList(tmp.getSeqId()+ "" , dbConn);
        T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
        for(T9DocFlowType ftTmp : typeList){
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
      List<T9DocFlowType> typeList = ft.getFlowTypeList(Integer.parseInt(sortId) , dbConn);
      int count = 0 ;
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      for(T9DocFlowType ftTmp : typeList){
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
  public String getFromBySortId(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sortId = request.getParameter("sortId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      sb.append("[");
      StringBuffer tb = fs.getSortJson(Integer.parseInt(sortId), dbConn , u);
      sb.append(tb);
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
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9DocFlowSort> list = fs.getFlowSortByDept(dbConn , u);
      /*
       * {title:'ddd',icon:'',action:getList,extData:'d',data:[
        {title:'ddd',icon:imgPath + '/edit.gif',iconAction:iconActionFuntion ,action:actionFuntion,extData:'2'}
        ,{title:'ddd',icon:imgPath + '/edit.gif',iconAction:iconActionFuntion ,action:actionFuntion,extData:'2'}
                                                                             ]}
       */
      String actionUrl = request.getContextPath()  + T9WorkFlowConst.MODULE_SRC_PATH + "/act/T9FlowSortAct/getFromBySortId.act?sortId=";
      sb.append("[");
      for(T9DocFlowSort tmp : list){
        if(tmp.getSortName() == null){
          sb.append("{title:' '");
        }else{
          sb.append("{title:'" + tmp.getSortName() + "'");
        }
        sb.append(",action:getList");
        sb.append(",extData:'" + tmp.getSeqId() +"'" );
        sb.append(",data:[");
        //StringBuffer tb = fs.getSortJson(tmp.getSeqId(), dbConn , u);
        //sb.append(tb);
        sb.append("], actionUrl:'"+actionUrl+ tmp.getSeqId() +"'},");
      }
      //其他分类
      sb.append("{title:'" + "未分类" + "'");
      sb.append(",action:getList");
      sb.append(",extData:'" + 0 +"'" );
      sb.append(",data:[");
     // StringBuffer tb2 = fs.getSortJson(0 , dbConn ,u);
      //sb.append(tb2);
      sb.append("], actionUrl:'"+actionUrl+"0'},");
      if(sb.charAt(sb.length() - 1) == ','){
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
  public String getSortId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sortName = request.getParameter("sortName");
    String sql = "select seq_id from "+ T9WorkFlowConst.FLOW_SORT +" where sort_name = '" + sortName + "'"; 
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
        String sql = "select seq_id from "+ T9WorkFlowConst.FLOW_SORT +" where sort_name in (" + sortNamesNew + ")"; 
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
  
  public T9DocFlowSort mapToFlowSort (Map mapListNext){
    T9DocFlowSort sort = new T9DocFlowSort();
    sort.setSeqId((Integer)mapListNext.get("seqId"));
    sort.setSortNo((Integer)mapListNext.get("sortNo"));
    sort.setSortName((String)mapListNext.get("sortName"));
    sort.setSortParent((Integer)mapListNext.get("sortParent"));
    sort.setHaveChild((String)mapListNext.get("haveChild"));
    sort.setDeptId((Integer)mapListNext.get("deptId"));
    return sort;
  }
}
