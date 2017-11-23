package t9.core.funcs.jexcel.act;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.filefolder.data.T9FileSort;
import t9.core.funcs.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.jexcel.logic.T9ExportLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.office.ntko.data.T9NtkoStream;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.system.extuser.logic.T9ExtUserLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9Export {
  public String export(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("test.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9ExportLogic expl = new T9ExportLogic();
      ArrayList<T9DbRecord > dbL = expl.getDbRecord();
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
  
  public String exportCsv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("test.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9ExportLogic expl = new T9ExportLogic();
      ArrayList<T9DbRecord > dbL = expl.getDbRecord();
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String importCsv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    InputStream is = null;
    Connection conn = null;
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      String fileName = fileForm.getFileName();
      is = fileForm.getInputStream();
      ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is);
      fileName = URLEncoder.encode(fileName + "_2.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9CSVUtil.CVSWrite(response.getWriter(), drl);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportToTxt(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    PrintWriter pw = null;
    try {
      String fileName = URLEncoder.encode("test.txt","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      pw = response.getWriter();
      String txtStr = "sssssssssss";
      pw.write(txtStr);
      pw.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      pw.close();
    }
    return null;
  }
}
