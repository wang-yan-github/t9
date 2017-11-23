package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowFormReglex;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import t9.core.funcs.workflow.logic.T9FormVersionLogic;
import t9.core.funcs.workflow.logic.T9WorkflowSave2DataTableLogic;
import t9.core.funcs.workflow.praser.T9FormPraser;
import t9.core.funcs.workflow.util.T9FlowFormUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

public class T9FormVersionAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.workflow.act.T9FormVersionAct");
  public String getVersionByFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowId = request.getParameter("flowId");
      int id = Integer.parseInt(flowId);
      
      T9FlowRunUtility ut = new T9FlowRunUtility();
      int formId = ut.getFormId(dbConn, id);
      
      T9FormVersionLogic logic = new T9FormVersionLogic();
      String versions =  logic.getVersionNoByForm(dbConn, formId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + versions + "\"");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 采用新版本占新号
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String genFormVersion(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      int id = Integer.parseInt(seqId);
      
      T9ORM orm = new T9ORM();
      T9FlowFormType form = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, id);
      
      String printModel = request.getParameter("printModel");
      String itemMax = request.getParameter("itemMax");
      printModel = printModel.replaceAll("\"", "\\\\\"");
      printModel = printModel.replaceAll("\r\n", "");
      T9FlowFormType form2 = new T9FlowFormType();
      form2.setFormName(form.getFormName());
      form2.setItemMax(Integer.parseInt(itemMax));
      form2.setPrintModel(printModel);
      form2.setDeptId(form2.getDeptId());
      
      String printModelNew = "";
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(printModel);
      Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
      printModelNew = T9FormPraser.toShortString(m1, printModel, T9FlowFormReglex.CONTENT);
      form2.setPrintModelShort(printModelNew);
      form2.setFormId(0);
      form2.setVersionNo(form.getVersionNo() + 1);
      form2.setVersionTime(new Date());
      //创建新的数据
      orm.saveSingle(dbConn, form2);
      T9FormVersionLogic logic =new T9FormVersionLogic();
      int newId = logic.getMaxFormId(dbConn);
      T9FlowFormUtility ffu = new T9FlowFormUtility();
      ffu.cacheForm(newId, dbConn);
      
      T9WorkflowSave2DataTableLogic logic4 = new T9WorkflowSave2DataTableLogic();
      
      //创建表结构
      String flowTypes = logic4.getFlowTypeByFormId(dbConn, seqId + "");
      logic4.createFlowFormTable(dbConn, newId, flowTypes);
      //更新flow关联
      logic4.updateFlowTypeByFormId(dbConn, seqId, newId);
      //修改formId
      logic4.updateFormTypeByFormId(dbConn, seqId, newId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, newId + "");
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 采用老版本占新号
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String genFormVersion1(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      int id = Integer.parseInt(seqId);
      
      T9ORM orm = new T9ORM();
      T9FlowFormType form = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, id);
      
      T9FlowFormType form2 = new T9FlowFormType();
      form2.setCss(form.getCss());
      form2.setScript(form.getScript());
      form2.setFormName(form.getFormName());
      form2.setItemMax(form.getItemMax());
      form2.setPrintModel(form.getPrintModel());
      form2.setPrintModelShort(form.getPrintModelShort());
      form2.setFormId(Integer.parseInt(seqId));
      form2.setVersionNo(form.getVersionNo());
      form2.setVersionTime(form.getVersionTime());
      orm.saveSingle(dbConn, form2);
      
      T9FormVersionLogic logic =new T9FormVersionLogic();
      int newId = logic.getMaxFormId(dbConn);
      
      //logic.updateFormId(dbConn, newId, id);
      logic.updateFormItem(dbConn , id , newId);
      logic.changeTableName(dbConn, id, newId);
      
      String printModel = request.getParameter("printModel");
      String itemMax = request.getParameter("itemMax");
      T9FlowFormLogic logic2 =new T9FlowFormLogic();
      printModel = printModel.replaceAll("\"", "\\\\\"");
      printModel = printModel.replaceAll("\r\n", "");
      logic2.updateForm(dbConn, id , printModel , itemMax , false);
      
      logic.updateFormVersion(dbConn, id, 0, form.getVersionNo() + 1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String replay1(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String formId = request.getParameter("formId");
      int id = Integer.parseInt(formId);
      
      T9ORM orm = new T9ORM();
      T9FlowFormType form3 = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, Integer.parseInt(seqId));
      T9FlowFormType form = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, id);
      T9FlowFormType form2 = new T9FlowFormType();
      form2.setCss(form.getCss());
      form2.setScript(form.getScript());
      form2.setFormName(form.getFormName());
      form2.setItemMax(form.getItemMax());
      form2.setPrintModel(form.getPrintModel());
      form2.setPrintModelShort(form.getPrintModelShort());
      form2.setFormId(id);
      form2.setVersionNo(form.getVersionNo());
      form2.setVersionTime(form.getVersionTime());
      orm.saveSingle(dbConn, form2);
      
      T9FormVersionLogic logic =new T9FormVersionLogic();
      int newId = logic.getMaxFormId(dbConn);
      
      //logic.updateFormId(dbConn, newId, id);
      logic.updateFormItem(dbConn , id , newId);
      logic.changeTableName(dbConn, id, newId);
      
      String printModel = form3.getPrintModel();
      String itemMax = form3.getItemMax() + "";
      T9FlowFormLogic logic2 =new T9FlowFormLogic();
      logic2.updateForm(dbConn, id , printModel , itemMax , false);
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      ffl.updateFlowForm(dbConn, id, new String[]{"CSS","SCRIPT"},  new String[]{form3.getCss() , form3.getScript()});
      logic.updateFormVersion(dbConn, id, 0, form.getVersionNo() + 1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String replay(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String formId = request.getParameter("formId");
      int id = Integer.parseInt(formId);
      
      T9ORM orm = new T9ORM();
      T9FlowFormType form3 = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, Integer.parseInt(seqId));
      T9FlowFormType form = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, id);
      T9FlowFormType form2 = new T9FlowFormType();
      form2.setCss(form3.getCss());
      form2.setScript(form3.getScript());
      form2.setFormName(form3.getFormName());
      form2.setItemMax(form3.getItemMax());
      form2.setPrintModel(form3.getPrintModel());
      form2.setPrintModelShort(form3.getPrintModelShort());
      form2.setFormId(0);
      form2.setDeptId(form3.getDeptId());
      form2.setVersionNo(form.getVersionNo() + 1);
      form2.setVersionTime(new Date());
      orm.saveSingle(dbConn, form2);
      
      T9FormVersionLogic logic =new T9FormVersionLogic();
      int newId = logic.getMaxFormId(dbConn);
      T9FlowFormUtility ffu = new T9FlowFormUtility();
      ffu.cacheForm(newId, dbConn);
      
      T9WorkflowSave2DataTableLogic logic4 = new T9WorkflowSave2DataTableLogic();
      //创建表结构
      String flowTypes = logic4.getFlowTypeByFormId(dbConn, formId + "");
      logic4.createFlowFormTable(dbConn, newId, flowTypes);
      //更新flow关联
      logic4.updateFlowTypeByFormId(dbConn, formId, newId);
      //修改formId
      logic4.updateFormTypeByFormId(dbConn, formId, newId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, newId +"");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
