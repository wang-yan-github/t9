package t9.subsys.oa.asset.act;
import java.sql.Connection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.asset.data.T9CpAssetType;
import t9.subsys.oa.asset.logic.T9CpAssetTypeLogic;


public class T9CpAssetTypeAct {
  /**
   * 查询类别代码
   * @param request
   * @param response
   * @throws Exception
   */
  public String assetTypeId(HttpServletRequest request,
      HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    try {   
      //数据库的连接
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //无条件，返回list
      List<T9CpAssetType> list = T9CpAssetTypeLogic.specList(dbConn);
      
      //遍历返回的list，将数据保存到Json中
      String data = "[";

      T9CpAssetType cp = new T9CpAssetType(); 
      for (int i = 0; i < list.size(); i++) {
        cp = list.get(i);
        data = data + T9FOM.toJson(cp).toString()+",";
      }
      if(list.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data.replaceAll("\\n", "");
      data = data.replaceAll("\\r", "");
      data = data + "]";
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
      T9ORM orm = new T9ORM();//orm映射数据库
      T9CpAssetType type = (T9CpAssetType) T9FOM.build(request.getParameterMap());
      //添加数据
      orm.saveSingle(dbConn,type);
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
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9CpAssetType cp = new T9CpAssetType();
      cp.setSeqId(seqId);
      orm.deleteSingle(dbConn,cp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
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
      T9CpAssetTypeLogic.deleteAll(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
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
      T9ORM orm = new T9ORM();
      T9CpAssetType type = (T9CpAssetType) T9FOM.build(request.getParameterMap());
      orm.updateSingle(dbConn, type);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
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
      T9CpAssetTypeLogic pLogic = new T9CpAssetTypeLogic();
      String typeName = request.getParameter("typeName");
      T9CpAssetType planType = pLogic.selectTypeName(dbConn,typeName);
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
  
  /**
   * 删除所有数据
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
      //将表单form1映射到T9Test实体类
      T9ORM orm = new T9ORM();//orm映射数据库
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9CpAssetType cp = (T9CpAssetType)orm.loadObjSingle(dbConn, T9CpAssetType.class,seqId);
      request.setAttribute("cp",cp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据失败");
      throw e;
    }
    return "/subsys/oa/asset/assetType/editor.jsp";
  }
}
