package t9.rad.flowform.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.rad.flowform.logic.T9FlowFormLogic;
import t9.rad.flowform.praser.T9FormPraser;

public class T9FlowFormViewAct{
  
  public String showFormView(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqIdStr = request.getParameter("seqId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      int seqId = Integer.parseInt(seqIdStr);
      Map map = ffl.selectFlowForm(dbConn, seqId, new String[]{"PRINT_MODEL","PRINT_MODEL_SHORT","FORM_NAME"});
      String html = (String) map.get("PRINT_MODEL");
      String shortModel = (String) map.get("PRINT_MODEL_SHORT");
      String formName = (String) map.get("FORM_NAME");
      if(shortModel == null){
        shortModel = "";
      }
      if(html == null){
        html = "";
      }
      StringBuffer data = T9FormPraser.toJson(html);
      StringBuffer newdata = new StringBuffer();
      newdata.append("{'seqId':").append(seqId).append(",")
        .append("'printModel' :").append(data).append(",")
        .append("'printModelShort':\"").append(shortModel).append("\"").append(",")
        .append("'formName':\"").append(formName).append("\"").append("}");
      //System.out.println(newdata.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
      request.setAttribute(T9ActionKeys.RET_DATA, newdata.toString());
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getIpAdd(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String ip = request.getRemoteAddr();
    if("".equals(ip) || ip == null){
      ip = "127.0.0.1";
    }
    String data = "{'ip':" + "\"" + ip +"\"}";
    //System.out.println(data);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    return "/core/inc/rtjson.jsp";
  }
}
