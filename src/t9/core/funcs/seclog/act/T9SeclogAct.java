package t9.core.funcs.seclog.act;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.seclog.logic.T9SecLogUtil;
import t9.core.funcs.seclog.logic.T9SeclogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.setting.data.T9HrCode;
import t9.subsys.oa.hr.setting.logic.T9HrCodeLogic;

public class T9SeclogAct {
  T9SeclogLogic logic = new T9SeclogLogic(); 
  
  /**
   * 通用列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getLogListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getLogLogic(dbConn, request.getParameterMap(), person);
      PrintWriter pw = response.getWriter();
      //System.out.println(data);
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  
  
  /**
   * 通用列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String doExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      ArrayList<T9DbRecord> data = this.logic.doExportLogic(dbConn, request.getParameterMap(), person);
      String fileName = URLEncoder.encode("安全日志.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      
      T9JExcelUtil.writeExc(response.getOutputStream(), data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  
  
  /**
   * 获取日志类型
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSecLogType(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String type=request.getParameter("sec_type");
      String data = this.logic.getSecLogType(dbConn, type);
      data=T9Utility.null2Empty(data);
      data="{logName:'"+data+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取日志类型
   * */
  public String selectChildCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String parentNo = request.getParameter("parentNo");
      if(T9Utility.isNullorEmpty(parentNo)){
        parentNo = "";
      }
      String seqId = request.getParameter("seqId");
      T9SeclogLogic codeLogic = new T9SeclogLogic();
      String data = "[";
      List<T9HrCode> codeList = new ArrayList<T9HrCode>();
      codeList = codeLogic.getChildCode(dbConn, parentNo);
      for (int i = 0; i < codeList.size(); i++) {
        T9HrCode code = codeList.get(i);
        data = data + T9FOM.toJson(code) + ",";
      }
      if(codeList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 日志归档 
   * 
   **/
  public String doArchive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data="1";
      try{
        Statement stmt=null;
      //  ResultSet rs=null;
        String dbms=T9SysProps.getProp("db.jdbc.dbms");
        String dataStr=T9Utility.getCurDateTimeStr("yyyyMMdd");
        if(dbms.equals("mysql")){
          String sql=""
              +"CREATE TABLE `seclog"+dataStr+"` ("
              +"  `SEQ_ID` int(10) unsigned NOT NULL auto_increment,"
              +"  `USER_SEQ_ID` varchar(200) default NULL,"
              +"  `OP_TIME` datetime default NULL,"
              +"  `CLIENT_IP` varchar(20) default NULL,"
              +"  `OP_TYPE` varchar(10) default NULL,"
              +"  `OP_OBJECT` text,"
              +"  `OP_DESC` text,"
              +"  `user_name` varchar(200) default NULL,"
              +"  `op_result` varchar(45) default NULL,"
              +"  PRIMARY KEY  (`SEQ_ID`)"
              +") ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;";
          stmt=dbConn.createStatement();
          stmt.execute(sql);//新建表
          sql=" insert into seclog"+dataStr+"  select * from seclog  ";
          stmt.execute(sql);//归档
          sql=" truncate table seclog ";
          stmt.execute(sql);
        }
        
        // add  seclog
        try{
           
           T9SecLogUtil.log(dbConn, person,request.getRemoteAddr(), "220","执行日志归档","1", "归档日志");
        }catch(Exception ex){
          
        }
        
      }catch(Exception e){
        e.printStackTrace();
        // add  seclog
        try{
           
           T9SecLogUtil.log(dbConn, person,request.getRemoteAddr(), "220","执行日志归档","0", "归档日志");
        }catch(Exception ex){
          
        }
        data="0";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
}
