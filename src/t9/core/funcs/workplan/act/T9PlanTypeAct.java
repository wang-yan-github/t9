package t9.core.funcs.workplan.act;
import java.sql.Connection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workplan.data.T9PlanType;
import t9.core.funcs.workplan.logic.T9PlanTypeLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

public class T9PlanTypeAct {
  /**
   * 查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String planType(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      //调用selectType无条件，返回list
      List<T9PlanType> list = pLogic.selectType(dbConn);
      //遍历返回的list，将数据保存到Json中
      String data = "[";

      T9PlanType type = new T9PlanType(); 
      for (int i = 0; i < list.size(); i++) {
        type = list.get(i);
        data = data + T9FOM.toJson(type).toString()+",";
      }
      if(list.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data.replaceAll("\\n", "");
      data = data.replaceAll("\\r", "");
      data = data + "]";
      //将遍历数据保存到request
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 添加数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String addType(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      T9PlanType type = new T9PlanType();
      type.setTypeName(request.getParameter("TYPE_NAME").replaceAll("\"","\\\""));
      type.setTypeNO(Integer.parseInt(request.getParameter("TYPE_NO")));
      pLogic.addType(dbConn,type);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String deleteType(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      pLogic.deleteType(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/core/funcs/workplan/type/index.jsp";
  }
  /**
   * 删除所有数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String deleteTypeAll(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      pLogic.deleteTypeAll(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/core/funcs/workplan/type/index.jsp";
  }
  
  /**
   * 修改数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String updateType(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      T9PlanType type = new T9PlanType();
      type.setSeqId(Integer.parseInt(request.getParameter("seqId")));
      type.setTypeName(request.getParameter("TYPE_NAME"));
      type.setTypeNO(Integer.parseInt(request.getParameter("TYPE_NO")));
      pLogic.updateType(dbConn, type);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/core/funcs/workplan/type/index.jsp";
  }
  /**
   * 根据ID查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String selectId(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9PlanType type = pLogic.selectId(dbConn, seqId);
      request.setAttribute("type",type);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/core/funcs/workplan/type/editor.jsp";
  }
  /**
   * 根据typeName查询数据
   * @param request
   * @param response
   * @throws Exception
   */
  public String selectTypeName(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //实列化T9PlanTypeLogic
      T9PlanTypeLogic pLogic = new T9PlanTypeLogic();
      String typeName = request.getParameter("typeName");
      T9PlanType planType = pLogic.selectTypeName(dbConn, typeName);
    //定义数组将数据保存到Json中
      String data = "[";
      if(planType != null) {
        data = data + T9FOM.toJson(planType);
        //data = data.substring(0, data.length()-1);
        data = data.replaceAll("\\n", "");
        data = data.replaceAll("\\r", "");
        //System.out.println(data);
      }
      data = data + "]";
      //保存查询数据是否成功，保存date
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
