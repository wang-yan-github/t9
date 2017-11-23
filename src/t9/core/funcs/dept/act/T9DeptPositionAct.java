package t9.core.funcs.dept.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.data.T9DeptPosition;
import t9.core.funcs.dept.data.T9PositionPerson;
import t9.core.funcs.dept.data.T9PositionPriv;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.menu.data.T9SysMenu;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9DeptPositionAct{
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String insertDp(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptSeqId = request.getParameter("deptSeqId");
      String positionNo = request.getParameter("positionNo");
      //System.out.println(deptSeqId+"sssssssssss");
      T9DeptLogic dl = new T9DeptLogic();
      if(dl.existsDeptPosition(dbConn, positionNo)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "岗位编码以存在，请重新填写！");
        return "/core/inc/rtjson.jsp";
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      T9DeptPosition dp = (T9DeptPosition) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, dp);
//      dbConn.close();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String insertPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String positionPriv = request.getParameter("positionPriv");
      int positionSeqId = Integer.parseInt(request.getParameter("positionSeqId"));
      //int userId = Integer.parseInt(request.getParameter("userId"));
      String sSeqId = request.getParameter("seqId");
      int seqId = 0;
      if(sSeqId != null && !"".equals(sSeqId)){
        seqId = Integer.parseInt(sSeqId);
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PositionPriv priv = new T9PositionPriv(); 
      //(T9PositionPriv) T9FOM.build(request.getParameterMap());
      priv.setPositionPriv(positionPriv);
      priv.setPositionSeqId(positionSeqId);
      priv.setSeqId(seqId);
      T9ORM orm = new T9ORM();
      orm.deleteSingle(dbConn, priv);
      orm.saveSingle(dbConn, priv);
//      dbConn.close();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getDept(HttpServletRequest request, HttpServletResponse response)
      throws Exception{
    Connection dbConn = null;
    try{
      int positionSeqId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("SEQ_ID", positionSeqId);
      Object obj = orm.loadObjSingle(dbConn, T9DeptPosition.class, map);
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
  
  public String getUserPosition(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int deptSeqId = Integer.parseInt(request.getParameter("deptSeqId"));
      String data = null;
      List<T9DeptPosition> userPositionList = null;
      StringBuffer sb = new StringBuffer("[");
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("DEPT_SEQ_ID", deptSeqId);
      userPositionList = orm.loadListSingle(dbConn, T9DeptPosition.class, map);
      if(userPositionList.size() > 0){
        for(int i = 0; i < userPositionList.size(); i++){
          T9DeptPosition userPriv = userPositionList.get(i);
          sb.append("{");
          sb.append("seqId:\"" + userPriv.getSeqId() + "\"");
          //sb.append("deptSeqId:\"" + userPriv.getDeptSeqId() + "\"");
          sb.append(",positionName:\"" + userPriv.getPositionName() + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        data = sb.toString();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未定义岗位");
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  public String getPersonPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int treeId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      List<T9Person> personPrivList = null;
      StringBuffer sb = new StringBuffer("[");
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("DEPT_ID", treeId);
      personPrivList = orm.loadListSingle(dbConn, T9Person.class, map);
      if(personPrivList.size() > 0) {
        for(int i = 0; i < personPrivList.size(); i++){
          T9Person userPriv = personPrivList.get(i);
          sb.append("{");
          //sb.append("seqId:\"" + userPriv.getSeqId() + "\"");
          sb.append("seqId:\"" + userPriv.getSeqId() + "\"");
          sb.append(",userName:\"" + userPriv.getUserName() + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        data = sb.toString();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未定义岗位权限");
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  public String getPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      List<T9Department> personList = null;
      StringBuffer sb = new StringBuffer("[");
      T9ORM orm = new T9ORM();
      personList = orm.loadListSingle(dbConn, T9Department.class, new HashMap());
      for(int i = 0; i < personList.size(); i++) {
        T9Department userPriv = personList.get(i);
        sb.append("{");
        //sb.append("seqId:\"" + userPriv.getSeqId() + "\"");
        sb.append("seqId:\"" + userPriv.getSeqId() + "\"");
        sb.append(",deptName:\"" + userPriv.getDeptName() + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      sb.append("]");
      data = sb.toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 

  public String getTree(HttpServletRequest request, HttpServletResponse response)
  throws Exception{
    Connection dbConn = null;
    try{
      int menuId = Integer.parseInt(request.getParameter("menuId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("POSITION_SEQ_ID", menuId);
      Object obj = orm.loadObjSingle(dbConn, T9PositionPriv.class, map);
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
  
  public String updateDp(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String positionFlag = request.getParameter("positionFlag");
      int treeId = Integer.parseInt(request.getParameter("treeId"));
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String positionNo = request.getParameter("positionNo");
      String positionNoOld = request.getParameter("positionNoOld");
      T9DeptLogic dl = new T9DeptLogic();
      T9DeptPosition dpt = (T9DeptPosition) T9FOM.build(request.getParameterMap());
      //dpt.setDeptSeqId(treeId);
      dpt.setSeqId(seqId);
      String data = null;
      T9ORM orm = new T9ORM();
      if((positionNoOld).equals(positionNo)){
        orm.updateSingle(dbConn, dpt);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
      }else{
        if(dl.existsDeptPosition(dbConn, positionNo)){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "岗位编码以存在，请重新填写！");
          return "/core/inc/rtjson.jsp";
        }else{
          orm.updateSingle(dbConn, dpt);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
        }
      }
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deletePosition(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      T9DeptPosition dt = (T9DeptPosition)orm.loadObjComplex(dbConn, T9DeptPosition.class, seqId);
      //T9DeptPosition dpt = (T9DeptPosition) T9FOM.build(request.getParameterMap());
      dt.setSeqId(seqId);
      orm.deleteComplex(dbConn, dt);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
//  public int IsHaveChild(HttpServletRequest request,
//      HttpServletResponse response,String id) throws Exception {
//    Connection dbConn = null;
//    try {
//      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
//          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
//      dbConn = requestDbConn.getSysDbConn();
//      T9ORM orm = new T9ORM();
//      Map map = new HashMap();
//      map.put("deptParent", id);
//      List<T9DeptPosition> list = orm.loadListSingle(dbConn, T9DeptPosition.class, map);
//      if(list.size()!=-1){
//        return 1;
//      }else{
//        return 0;
//      }
//    } catch (Exception ex) {
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
//      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
//      throw ex;
//    }
//  }
  
  public String selectDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String treeId = request.getParameter("treeId");
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
    String deptLocal = request.getParameter("deptLocal");
    String managerList = request.getParameter("deptList");
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      ArrayList<T9Person> personList = null;
      Map map = new HashMap();
      map.put("DEPT_ID", treeId);
      personList = (ArrayList<T9Person>)orm.loadListSingle(dbConn, T9Person.class, map);
      request.setAttribute("personList", personList);
//      dbConn.close();
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/funcs/dept/user.jsp?TO_ID="+TO_ID+"&TO_NAME="+TO_NAME+"&deptLocalg="+deptLocal;
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
      Map m1 = null;
      menuList = (ArrayList<T9SysMenu>)orm.loadListSingle(dbConn, T9SysMenu.class, m1);
      for(int i = 0; i < menuList.size(); i++){
        T9SysMenu menu = menuList.get(i);
        sb.append("{");
        sb.append("nodeId:\"" + menu.getMenuId() + "\"");
        sb.append(",name:\"" + menu.getMenuName() + "\"");
        sb.append(",isHaveChild:" + 1);
        sb.append(",isChecked:" + isChecked(privArray,(String) menu.getMenuId()));
        sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/" + menu.getImage() + "\"");
        sb.append("},");
      }       
      List funcList = new ArrayList();
      funcList.add("sysFunction");
      String[] filters = new String[]{};
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("SYS_FUNCTION");
      for(Map m : list){
        sb.append("{");
        sb.append("nodeId:\"" + m.get("menuId") + "\"");
        sb.append(",name:\"" + m.get("funcName") + "\"");
        sb.append(",isChecked:" + isChecked(privArray,(String) m.get("menuId")));
        sb.append(",isHaveChild:" + IsHaveChild(request, response, String.valueOf(m.get("menuId"))));
        sb.append(",imgAddress:\"" + request.getContextPath() + "/core/styles/imgs/" + m.get("funcCode") + "\"");
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
  
  public String getSelestData(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //int userId = Integer.parseInt(request.getParameter("userId"));
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9Person> perList = null;
      Map m = null;
      perList = (ArrayList<T9Person>)orm.loadListSingle(dbConn, T9Person.class, m);
      for(int i = 0; i < perList.size(); i++){
        T9Person menu = perList.get(i);
        sb.append("{");
        sb.append("value:\"" + menu.getSeqId() + "\"");
        sb.append(",name:\"" + menu.getUserName() + "\"");
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
  
  public String getDeptSelect(HttpServletRequest request, HttpServletResponse response)
  throws Exception{
    Connection dbConn = null;
    try{
      int positionSeqId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("POSITION_SEQ_ID", positionSeqId);
      Object obj = orm.loadObjSingle(dbConn, T9PositionPerson.class, map);
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
  
  public String getPrivTreeData(HttpServletRequest request, HttpServletResponse response)
  throws Exception{
    Connection dbConn = null;
    try{
      int positionSeqId = Integer.parseInt(request.getParameter("userId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("POSITION_SEQ_ID", positionSeqId);
      Object obj = orm.loadObjSingle(dbConn, T9PositionPriv.class, map);
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
  
  public String updatePersonDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int personSeqId = Integer.parseInt(request.getParameter("personSeqId"));
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptNo = request.getParameter("deptNo");
      String deptNoOld = request.getParameter("deptNoOld");
      T9PositionPerson dpt = (T9PositionPerson) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      if(seqId == 0){
        dpt.setSeqId(personSeqId);
        orm.saveSingle(dbConn, dpt);
      }else{
        dpt.setSeqId(seqId);
        orm.deleteSingle(dbConn, dpt);
        orm.saveSingle(dbConn, dpt);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getComPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int userId = Integer.parseInt(request.getParameter("userId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      String data = null;
      List<T9PositionPerson> userPrivList = null;
      List<T9PositionPriv> positionPrivList = null;
      List funcList = new ArrayList();
      T9ORM orm = new T9ORM();
      //String[] filters = new String[]{"POSITION_USERS = '" + userId + "'"};
      //map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      map.put("POSITION_USERS", userId);
      userPrivList = orm.loadListSingle(dbConn, T9PositionPerson.class, map);
      int positionSeqId = 0;
      String positionPriv = "";
      for(int i = 0; i < userPrivList.size(); i++){
        T9PositionPerson userPriv = userPrivList.get(i);
        int seqId = userPriv.getSeqId();
        positionSeqId = userPriv.getPositionSeqId();
        // String positionUsers = userPriv.getPositionUsers();
        map.put("POSITION_SEQ_ID", positionSeqId);
        positionPrivList = orm.loadListSingle(dbConn, T9PositionPriv.class, map);
        for(int j = 0; j < userPrivList.size(); j++){
          T9PositionPriv poPriv = positionPrivList.get(i);
          int positionSeqIDD = poPriv.getPositionSeqId();
          positionPriv +=  poPriv.getPositionPriv() + ",";
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
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
      String[] filters = new String[]{"MENU_ID like '" + id + "%'"," length(MENU_ID) > " + id.length() + ""};
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
  
  public String getPrivString(Connection dbConn, int userId) throws Exception{
    //String data = null;
    List<T9PositionPerson> userPrivList = null;
    List<T9PositionPriv> positionPrivList = null;
    List funcList = new ArrayList();
    T9ORM orm = new T9ORM();
    //String[] filters = new String[]{"POSITION_USERS = '" + userId + "'"};
    //map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
    //Map map = new HashMap();
    //map.put("POSITION_USERS", String.valueOf(userId));
    //userPrivList = orm.loadListSingle(dbConn, T9PositionPerson.class, map);
    //int positionSeqId=0;
    String positionPriv = "";
    //for(int i = 0; i < userPrivList.size(); i++) {
    //T9PositionPerson userPriv = userPrivList.get(i);
    //int seqId = userPriv.getSeqId();
    //positionSeqId = userPriv.getPositionSeqId();
    //String positionUsers = userPriv.getPositionUsers();
    Map map1 = new HashMap();
    map1.put("POSITION_SEQ_ID", userId);
    positionPrivList = orm.loadListSingle(dbConn, T9PositionPriv.class, map1);
    for(int j = 0; j < positionPrivList.size(); j++){
      T9PositionPriv poPriv = positionPrivList.get(j);
      int positionSeqIDD = poPriv.getPositionSeqId();
     //positionPriv +=  poPriv.getPositionPriv() + ",";
      positionPriv =  poPriv.getPositionPriv();
      if(positionPriv == null){
        positionPriv = "";
      }
    }
    //}
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
}