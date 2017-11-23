package raw.lh.generate.act;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import raw.lh.generate.logic.T9GenerateLogic;
import raw.lh.generate.logic.T9SysTableLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;

public class T9GenerateAct {
  public String createTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      String seqId  = request.getParameter("seqId");
      String tableName =  request.getParameter("tableName");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9GenerateLogic logic = new  T9GenerateLogic();
      List<Map> fields = logic.getFields(dbConn , seqId);
      String type = T9SysProps.getProp("db.jdbc.dbms");
      String sql = "";
      if ("oracle".equals(type)) {
        sql = logic.getOracleSql(fields ,  tableName);
      } else if ("sqlserver".endsWith(type)) {
        sql = logic.getMssqlSql(fields ,  tableName);
      } else {
        sql = logic.getMysqlSql(fields ,  tableName);
      }
      T9SysTableLogic logic2 = new T9SysTableLogic();
      logic2.exSql(dbConn, sql);
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_MSRG, "表生成成功！");
    } catch(Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "表生成失败！");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String explortSql(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream out = null;
    try{
      String seqId  = request.getParameter("seqId");
      String tableName =  request.getParameter("tableName");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9GenerateLogic logic = new  T9GenerateLogic();
      List<Map> fields = logic.getFields(dbConn , seqId);
      String[] dialect= request.getParameterValues("dialect");
      Map<String , InputStream> map = new HashMap();
      for (String dia:dialect) {
        String sql  = "";
        if ("Oracle".equals(dia)) {
          sql = logic.getOracleSql(fields ,  tableName);
        }
        if ("MySql".equals(dia)) {
          sql = logic.getMysqlSql(fields ,  tableName);
        }
        if ("MsSql".equals(dia)) {
          sql = logic.getMssqlSql(fields ,  tableName);
        }
        map.put(dia + ".sql", new ByteArrayInputStream(sql.getBytes("UTF-8")));
      }
      response.setContentType("application/octet-stream");
      response.setHeader("Cache-control","private");
      response.setHeader("Accept-Ranges","bytes");
      String fileName = URLEncoder.encode(tableName + ".zip", "UTF-8");
      response.setHeader("Content-Disposition", "attachment; filename=" + fileName );
      out = response.getOutputStream();
      Set<String> key = map.keySet();
      org.apache.tools.zip.ZipOutputStream zipout = new org.apache.tools.zip.ZipOutputStream(out);
      zipout.setEncoding("GBK");
      for (String tmp : key) {
        InputStream in = map.get(tmp);
        this.output(in, zipout, tmp);
      }
      zipout.flush();
      //out.flush();
      zipout.close();
      //out.close();
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } 
    return null;
  }
  public  void output(InputStream in ,  org.apache.tools.zip.ZipOutputStream out  , String fileName) throws IOException {
    byte[] buf = new byte[1024];
    try {
      org.apache.tools.zip.ZipEntry ss =  new org.apache.tools.zip.ZipEntry(fileName);
      out.putNextEntry(ss);
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      out.closeEntry();
      out.flush();
      in.close();
    } catch (IOException e) {
      throw e;
    }
  }
}
