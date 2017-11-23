package raw.lh.generate.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import raw.lh.T9Node;
import raw.lh.generate.logic.T9SysTableLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.rad.velocity.T9velocityUtil;
import t9.rad.velocity.createtable.T9CreateTableUtil;

public class T9SysTableAct {
  private static Logger log = Logger
      .getLogger("raw.lh.generate.act.T9SysTableAct");
  public String getTableList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String queryTableName =  request.getParameter("queryTableName");
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      StringBuffer result = myWorkLogic.getTableList(dbConn,queryTableName, request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String delTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String seqIds = T9Utility.null2Empty(request.getParameter("seqId"));
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if (seqIds.endsWith(",")) {
        seqIds = T9WorkFlowUtility.getOutOfTail(seqIds);
      }
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      myWorkLogic.delTable(dbConn, seqIds);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delTableField(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String seqIds = T9Utility.null2Empty(request.getParameter("seqId"));
    String tableId = T9Utility.null2Empty(request.getParameter("tableId"));
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if (seqIds.endsWith(",")) {
        seqIds = T9WorkFlowUtility.getOutOfTail(seqIds);
      }
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      myWorkLogic.delTableField(dbConn, seqIds , tableId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addOrUpdateTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      String tableName = request.getParameter("TABLE_NAME");
      String fieldPre = request.getParameter("NO_FIELD_PRE");
      String isFieldDefault = request.getParameter("NO_FIELD_DEFAULT");
      String fieldLength = request.getParameter("NO_FIELD_LENGTH");
      String fieldStart = request.getParameter("NO_FIELD_START");
      String seqId = T9Utility.null2Empty(request.getParameter("SEQ_ID"));
      if (T9Utility.isNullorEmpty(seqId)) {
        myWorkLogic.addTable(dbConn, tableName, fieldPre, isFieldDefault, fieldLength, fieldStart);
      } else {
        myWorkLogic.updateTable(dbConn , seqId, tableName, fieldPre, isFieldDefault, fieldLength, fieldStart);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getTableInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      String seqId = request.getParameter("seqId");
      String data = myWorkLogic.getTableInfo(dbConn ,  seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getTableField(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String queryTableName =  request.getParameter("seqId");
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      StringBuffer result = myWorkLogic.getTableFieldList(dbConn,queryTableName, request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String addOrUpdateTableField(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9SysTableLogic myWorkLogic = new T9SysTableLogic();
      String FIELD_TYPE_EDIT = request.getParameter("FIELD_TYPE_EDIT");
      String SEQ_ID = request.getParameter("SEQ_ID");
      
      String FIELD_NAME_EDIT = request.getParameter("FIELD_NAME_EDIT");
      String FIELD_LENGTH_EDIT = request.getParameter("FIELD_LENGTH_EDIT");
      String seqId = T9Utility.null2Empty(request.getParameter("fieldSeqId"));
      if (T9Utility.isNullorEmpty(seqId)) {
        myWorkLogic.addTableField(dbConn, FIELD_NAME_EDIT, FIELD_TYPE_EDIT, FIELD_LENGTH_EDIT , SEQ_ID);
      } else {
        myWorkLogic.updateTableField(dbConn , seqId, FIELD_NAME_EDIT, FIELD_TYPE_EDIT, FIELD_LENGTH_EDIT);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String create(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNos = request.getParameter("tableNos");
      String outp = request.getParameter("outpath");
      String templateUrl = request.getParameter("templateUrl").trim();
      String[] dialects = request.getParameterValues("dialect");
      String templateName = "";
      if(templateUrl.endsWith("\\")){
        String str = templateUrl.substring(0, templateUrl.length()-1);
        if(str.endsWith(".vm")){
          int index = str.lastIndexOf("\\");
          templateUrl = str.substring(0,index + 1);
        }
      }
      String[] tableNoArr = tableNos.split(",");
        for (String dia : dialects) {
          String outpath = outp + dia + "\\";
          templateName = "createtable" + dia + ".vm";
          T9velocityUtil.velocity(
              T9CreateTableUtil.createTableById(dbConn,tableNoArr,dia)
              , outpath, templateName, templateUrl);
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成成功！");
    } catch(Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成失败！");
      e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
}
