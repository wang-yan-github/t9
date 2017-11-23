package t9.core.funcs.doc.act;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.logic.T9FlowFormLogic;
import t9.core.funcs.doc.praser.T9FormPraser;
import t9.core.funcs.doc.util.T9PraseData2FormView;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

public class T9FlowFormViewAct{
  
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FlowFormViewAct");
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
  public String getFormView(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqIdStr = request.getParameter("seqId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      int seqId = Integer.parseInt(seqIdStr);
      Map map = ffl.selectFlowForm(dbConn, seqId, new String[]{"PRINT_MODEL","PRINT_MODEL_SHORT","FORM_NAME" , "SCRIPT" , "CSS"});
      String html = (String) map.get("PRINT_MODEL");
      String shortModel = (String) map.get("PRINT_MODEL_SHORT");
      String formName = (String) map.get("FORM_NAME");
      String js = (String)map.get("SCRIPT");
      String css = (String)map.get("CSS");
      if(shortModel == null){
        shortModel = "";
      }
      if (js == null) {
        js = "";
      }
      if (css == null) {
        css = "";
      }
      if(html == null){
        html = "";
      }
      StringBuffer sb = new StringBuffer();
      Map formItemQuery = new HashMap();
      formItemQuery.put("FORM_ID", seqId);
      T9ORM orm = new T9ORM();
      List<T9DocFlowFormItem> list = orm.loadListSingle(dbConn, T9DocFlowFormItem.class , formItemQuery);
      T9PraseData2FormView pf = new T9PraseData2FormView();
      String form =  pf.parseForm(loginUser, shortModel, list ,request.getRemoteAddr(), dbConn);
      form = form.replaceAll("\'", "\\\\'");
      form = form.replaceAll("\\\n", "");
      js = js.replaceAll("\'", "\\\\'");
      js = js.replaceAll("[\n-\r]", "");
      css = css.replaceAll("\'", "\\\\'");
      css = css.replaceAll("[\n-\r]", "");
      
      sb.append("{js:'"+ js +"'");
      sb.append(",css:'" + css + "'");
      sb.append(",form:'"+ form +"'");
      sb.append("}");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getAllSeal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowFormLogic logic = new T9FlowFormLogic();
      String result = logic.getSeals(loginUser.getSeqId(), dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
      request.setAttribute(T9ActionKeys.RET_DATA, result);
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSeal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sId = request.getParameter("id");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic logic = new T9FlowFormLogic();
      byte[] data = logic.getSealData(Integer.parseInt(sId), dbConn);
      //response.setCharacterEncoding("GB2312");
      OutputStream out = null;
      try {
        out = response.getOutputStream();
        out.write(data, 0, data.length);
        out.flush();
      }catch(Exception ex2) {
        throw ex2;
      }finally {
        if (out != null) {
          out.close();
        }
      }
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
