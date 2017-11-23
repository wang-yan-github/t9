package t9.rad.flowform.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.util.T9FlowFormUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.rad.flowform.data.T9FlowFormReglex;
import t9.rad.flowform.data.T9FlowFormType;
import t9.rad.flowform.logic.T9FlowFormLogic;
import t9.rad.flowform.praser.T9FormPraser;

public class T9FlowFormAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String insertFlowForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int deptId = Integer.parseInt(request.getParameter("deptId"));
      T9FlowFormType form = (T9FlowFormType) T9FOM.build(request
          .getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, form);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getFlowForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Object obj = orm.loadObjSingle(dbConn, T9FlowFormType.class, seqId);
      data = T9FOM.toJson(obj).toString();
      T9FlowFormLogic logic = new T9FlowFormLogic();
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "{formData:" +data + ",noDelete:" + logic.isExistFlowRun(seqId, dbConn)+"}");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getFormView(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
    //  String printModel = request.getParameter("printModel");
    //  String[] str = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      Map map = ffl.selectFlowForm(dbConn, seqId, "PRINT_MODEL");
      data = toJs(map).toString();
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
  
  public StringBuffer toJs(Map map) throws Exception {
    StringBuffer rtBuf = new StringBuffer("{");
    Iterator it = map.entrySet().iterator();
    while (it.hasNext()){
      Map.Entry entry = (Map.Entry) it.next();
      Object key = entry.getKey();
      Object value = entry.getValue();
      rtBuf.append("'");
      rtBuf.append(key);
      rtBuf.append("'");
      rtBuf.append(":");
      rtBuf.append("\"");
      rtBuf.append(value);
      rtBuf.append("\"");
      //System.out.println(key+":"+value+"ffffffff");
    }
    rtBuf.append("}");
    //System.out.println(rtBuf+"eeeeeeeee");
    return rtBuf;
  }
  
  public String updateForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqStr = request.getParameter("seqId");
      String printModel = request.getParameter("printModel");
      String printModelNew = "";
      //System.out.println(seqStr);
      int seqId = Integer.parseInt(seqStr.trim());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //T9FlowFormType form = (T9FlowFormType) T9FOM.build(request.getParameterMap());
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      printModel = printModel.replaceAll("\"", "\\\\\"");
      printModel = printModel.replaceAll("\r\n", "");
      //System.out.println( "printModel : " + printModel);
      HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(printModel);
      Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
      String data = T9FormPraser.toJson(m1).toString();
      printModelNew = T9FormPraser.toShortString(m1, printModel, T9FlowFormReglex.CONTENT);
      ffl.updateFlowForm(dbConn, seqId, new String[]{"PRINT_MODEL","PRINT_MODEL_SHORT"},  new String[]{printModel,printModelNew});
      //form.setSeqId(seqId);
      //T9ORM orm = new T9ORM();
      //orm.updateSingle(dbConn, form);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateFlowForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqStr = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqStr.trim());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormType form = (T9FlowFormType) T9FOM.build(request.getParameterMap());
      form.setSeqId(seqId);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, form);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String deleteForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqId = request.getParameter("seqId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("seqId", seqId);
      orm.deleteSingle(dbConn, "flowFormType", map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateDesign(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormType form = (T9FlowFormType) T9FOM.build(request.getParameterMap());
      form.setSeqId(seqId);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, form);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 表单分类管理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listBySort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String data = "";
    int sortId = 0 ;
    try{
      sortId = Integer.parseInt(request.getParameter("sortId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      StringBuffer sb = ffl.flowFormType2Json(dbConn, sortId);
      data = "{flowList:" + sb.toString() + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //System.out.println(data);
    return "/core/inc/rtjson.jsp";
  }
  public String search(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String data = "";
    try{
      String search = request.getParameter("searchKey");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      StringBuffer sb = ffl.search(dbConn, search);
      data = "{flowList:" + sb.toString() + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //System.out.println(data);
    return "/core/inc/rtjson.jsp";
  }
}
