package t9.core.funcs.person.act;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9SecureKey;
import t9.core.funcs.person.logic.T9SecureCardLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import seamoonotp.seamoonapi;

public class T9SecureCardAct {
  T9SecureCardLogic logic = new T9SecureCardLogic();

  public String importSecureCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    InputStream is = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      is = fileForm.getInputStream();
      
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      int count = 0;
      String temp;
      while(!T9Utility.isNullorEmpty(temp = br.readLine())){
        String secureKeyStr[] = temp.split(",");
      
        T9ORM orm = new T9ORM();
        T9SecureKey secureCard = new T9SecureKey();
        secureCard.setKeySn(secureKeyStr[0]);
        secureCard.setKeyInfo(secureKeyStr[1]);
        
        if(!this.logic.isExist(dbConn, secureCard.getKeySn())){
          orm.saveComplex(dbConn, secureCard);
          count++;
        }
      }
      return "/core/funcs/person/importSecureCard.jsp?flag="+count;
    }
    catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      // ex.printStackTrace();
      throw ex;
    }
  }
  
  
  /**
   * 动态密保卡 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSecureCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getSecureCard(dbConn, request.getParameterMap(), person);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String bindUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userId = request.getParameter("userId");
    String keySn = request.getParameter("keySn");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.bindUser(dbConn, userId, keySn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteSecureCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStrs = request.getParameter("seqIds");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.deleteSecureCard(dbConn, seqIdStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String unBindSecureCard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStrs = request.getParameter("seqIds");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.unBindSecureCard(dbConn, seqIdStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String passwordsyn(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    String password = request.getParameter("password");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sninfo = this.logic.getKeySn(dbConn, seqId);
      seamoonapi sc=new seamoonapi();
      String keyInfo = sc.passwordsyn(sninfo, password);
      String data = "";
      if(keyInfo.length() > 3){
        data = "同步成功";
        T9ORM orm = new T9ORM();
        T9SecureKey secureCard = new T9SecureKey();
        secureCard.setSeqId(Integer.parseInt(seqId));
        secureCard.setKeyInfo(keyInfo);
        orm.updateSingle(dbConn, secureCard);
      }
      else if("0".equals(keyInfo)){
        data = "动态密码错误";
      }
      else if("-1".equals(keyInfo)){
        data = "未知内部错误";
      }
      else if("-2".equals(keyInfo)){
        data = "动态加密字符串有错";
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 下载CSV模板
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String downCSVTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String fileName = URLEncoder.encode("批量绑定动态密码卡模板.csv", "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
      T9DbRecord record = new T9DbRecord();
      record.addField("用户名", "");
      record.addField("卡号", "");
      dbL.add(record);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  
  /**
   * 导入CSV批量绑定密码卡
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String impSecureCardInfoToCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
      int count = this.logic.impSecureCardInfoToCsv(dbConn, fileForm, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据成功！");
      return "/core/funcs/person/importBindCard.jsp?flag=1&count="+count;
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据失败");
      throw e;
    }
  }
}
