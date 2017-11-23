package t9.subsys.infomgr.bilingual.act;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jxl.Sheet;
import jxl.Workbook;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.infomgr.bilingual.data.T9Bilingual;
import t9.subsys.infomgr.bilingual.data.T9Postulant;
import t9.subsys.infomgr.bilingual.data.T9PostulantInfo;
import t9.subsys.infomgr.bilingual.logic.T9PostulantLogic;

public class T9PostulantAct {
  public static final String BILINGUAL_PATH = "\\bilingual";
  
  private T9PostulantLogic logic = new T9PostulantLogic();
   
  /**
   * 增加记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String add(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = new HashMap();
      //Map<String,String[]> map = request.getParameterMap();
      String name = request.getParameter("name");
      String strValue = request.getParameter("strValue");
      String introduce = request.getParameter("introduce");
      String principalUnit = request.getParameter("principalUnit");
      String principal = request.getParameter("principal");
      String principalDuty = request.getParameter("principalDuty");
      String principalTel1 = request.getParameter("principalTel1");
      String principalTel2 = request.getParameter("principalTel2");
      String principalTel = "";
      String principalAdd = request.getParameter("principalAdd");
      String principalContact = request.getParameter("principalContact");
      String services = request.getParameter("services");
      String servicesOther = request.getParameter("servicesOther");
      String availTime1 = request.getParameter("availTime1");
      String availTime2 = request.getParameter("availTime2");
      String at1Vlaue = request.getParameter("at1Vlaue");
      String at2Vlaue = request.getParameter("at2Vlaue");
      String recordSource = request.getParameter("recordSource");
      String serveTimeWeekday = "";
      String serveTimeWeekend = "";
      String flag = "";
      if(T9Utility.isNullorEmpty(principalTel2)){
        principalTel = "";
      }else{
        principalTel = principalTel1 + "-" + principalTel2;;
      }
      if("1".equals(availTime1)){
        serveTimeWeekday = availTime1 + at1Vlaue;
      }
      if("1".equals(availTime2)){
        serveTimeWeekend = availTime2 + at2Vlaue;
      }
      String postulantId = T9Guid.getGuid();
      
      map.put("name", name);
      //map.put("languageKind", languageKind);
      //map.put("languageLevel", languageLevel);
      map.put("introduce", introduce);
      map.put("principal", principal);
      map.put("principalDuty", principalDuty);
      map.put("principalUnit", principalUnit);
      map.put("principalTel", principalTel);
      map.put("principalAdd", principalAdd);
      map.put("principalContact", principalContact);
      map.put("services", services);
      map.put("servicesOther", servicesOther);
      map.put("serveTimeWeekday", serveTimeWeekday);
      map.put("serveTimeWeekend", serveTimeWeekend);
      map.put("recordSource", recordSource);
      map.put("flag", "0");
      map.put("postulantId", postulantId);
      
      T9ORM orm = new T9ORM();
      String[] strVal = null;
      String strVal1 = "";
      String strVal2 = "";
      String strVal3 = "";
      orm.saveSingle(dbConn , "postulant" , map);
      //T9Postulant postulant = (T9Postulant) T9FOM.build(map, T9Postulant.class, "");
      //this.logic.add(dbConn, postulant);
      if(strValue.indexOf("=") == -1){
        strVal = strValue.split(",");
        for(int x = 0; x < strVal.length; x++){
          strVal1 = strVal[0];
          strVal2 = strVal[1];
          strVal3 = strVal[2];
        }
        this.logic.addInfo(dbConn, postulantId, strVal1, strVal2, strVal3);
      }else{
        strValue = T9Utility.encodeSpecial(strValue);
        String[] valStr = strValue.split("=");
        String strFunc = "";
        for(int i = 0; i < valStr.length; i++){
          strFunc = valStr[i];
          strVal = strFunc.split(",");
          for(int x = 0; x < strVal.length; x++){
            strVal1 = strVal[0];
            strVal2 = strVal[1];
            strVal3 = strVal[2];
          }
          this.logic.addInfo(dbConn, postulantId, strVal1, strVal2, strVal3);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
//  public String addInfo(HttpServletRequest request,
//      HttpServletResponse response, String postulantId, String languageKind, String languageLevel, String amount) throws Exception{
//    
//    Connection dbConn = null;
//    try {
//      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
//      dbConn = requestDbConn.getSysDbConn();
//      Map map = new HashMap();
//      
//      map.put("amount", amount);
//      map.put("languageKind", languageKind);
//      map.put("languageLevel", languageLevel);
//      map.put("postulantId", postulantId);
//      
//      T9ORM orm = new T9ORM();
//      orm.saveSingle(dbConn , "postulant" , map);
//      
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
//      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
//    } catch (Exception e) {
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
//      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
//      throw e; 
//    }
//    
//    return "/core/inc/rtjson.jsp";
//  }
  
  /**
   * 增加记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String modify(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //Map<String,String[]> maps = request.getParameterMap();
      //T9Postulant postulant = (T9Postulant) T9FOM.build(maps, T9Postulant.class, "");
      //this.logic.modify(dbConn, postulant);
      
      Map map = new HashMap();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String name = request.getParameter("name");
      //String strValue = request.getParameter("strValue");
      String introduce = request.getParameter("introduce");
      String principalUnit = request.getParameter("principalUnit");
      String principal = request.getParameter("principal");
      String principalDuty = request.getParameter("principalDuty");
      String principalTel1 = request.getParameter("principalTel1");
      String principalTel2 = request.getParameter("principalTel2");
      String principalTel = "";
      String principalAdd = request.getParameter("principalAdd");
      String principalContact = request.getParameter("principalContact");
      String services = request.getParameter("services");
      String servicesOther = request.getParameter("servicesOther");
      String availTime1 = request.getParameter("availTime1");
      String availTime2 = request.getParameter("availTime2");
      String at1Vlaue = request.getParameter("at1Vlaue");
      String at2Vlaue = request.getParameter("at2Vlaue");
      String recordSource = request.getParameter("recordSource");
      String serveTimeWeekday = "";
      String serveTimeWeekend = "";
      String flag = request.getParameter("flag");
      
      if(T9Utility.isNullorEmpty(principalTel2)){
        principalTel = "";
      }else{
        principalTel = principalTel1 + "-" + principalTel2;
      }
      if("1".equals(availTime1)){
        serveTimeWeekday = availTime1 + at1Vlaue;
      }
      if("1".equals(availTime2)){
        serveTimeWeekend = availTime2 + at2Vlaue;
      }
      map.put("seqId", seqId);
      map.put("name", name);
      map.put("introduce", introduce);
      map.put("principal", principal);
      map.put("principalDuty", principalDuty);
      map.put("principalUnit", principalUnit);
      map.put("principalTel", principalTel);
      map.put("principalAdd", principalAdd);
      map.put("principalContact", principalContact);
      map.put("services", services);
      map.put("servicesOther", servicesOther);
      map.put("serveTimeWeekday", serveTimeWeekday);
      map.put("serveTimeWeekend", serveTimeWeekend);
      map.put("recordSource", recordSource);
      map.put("flag", flag);
      
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "postulant", map);   //更新主表
      
//      String[] strVal = null;
//      String strVal1 = "";
//      String strVal2 = "";
//      String strVal3 = "";
//      if(strValue.indexOf("=") == -1){
//        strVal = strValue.split(",");
//        for(int x = 0; x < strVal.length; x++){
//          strVal1 = strVal[0];
//          strVal2 = strVal[1];
//          strVal3 = strVal[2];
//        }
//        //this.logic.modifyInfo(dbConn, postulantId, strVal1, strVal2, strVal3);
//      }else{
//        strValue = T9Utility.encodeSpecial(strValue);
//        String[] valStr = strValue.split("=");
//        String strFunc = "";
//        for(int i = 0; i < valStr.length; i++){
//          strFunc = valStr[i];
//          strVal = strFunc.split(",");
//          for(int x = 0; x < strVal.length; x++){
//            strVal1 = strVal[0];
//            strVal2 = strVal[1];
//            strVal3 = strVal[2];
//          }
//          //this.logic.modifyInfo(dbConn, postulantId, strVal1, strVal2, strVal3);
//        }
//      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 查看记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String detail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Postulant postulant = (T9Postulant)this.logic.detail(dbConn, Integer.parseInt(seqId));
      
      if (postulant == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "记录不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(postulant);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查看记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String detailInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("[");
      String data = null;
      String postulantId = this.logic.getPostulantIdLogic(dbConn, Integer.parseInt(seqId));
      //T9PostulantInfo postulant = (T9PostulantInfo)this.logic.detailInfo(dbConn, Integer.parseInt(postulantId));
      
      ArrayList<T9PostulantInfo> orgList = this.logic.getPostulantInfo(dbConn, postulantId);
      
      for(int i = 0; i < orgList.size(); i++){
        T9PostulantInfo org = orgList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + org.getSeqId() + "\"");
        sb.append(",amount:" + org.getAmount() + "");
        sb.append(",languageKind:\"" + org.getLanguageKind() + "\"");
        sb.append(",languageLevel:\"" + org.getLanguageLevel() + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (orgList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 更新记录(录用,不录用)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    String flag = request.getParameter("flag");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.updateFlag(dbConn, Integer.parseInt(seqId), Integer.parseInt(flag));
      
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delete(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String postulantId = this.logic.getPostulantIdLogic(dbConn, Integer.parseInt(seqId));
      this.logic.delete(dbConn, Integer.parseInt(seqId));
      this.logic.deleteInfo(dbConn, postulantId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查看记录(供桌面模块使用)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryModule(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      List<Map<String,String>> list = this.logic.queryModule(dbConn);
      
      StringBuffer sb = new StringBuffer("[");
      
      for (Map<String,String> map : list) {
        sb.append(this.toJson(map));
        sb.append(",");
      }
      
      if (sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      
      sb.append("]");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  

  /** 
  * 取得志愿者报名信息,并分页
  * @param request 
  * @param response 
  * @return 
  * @throws Exception 
  */ 
  public String getPage(HttpServletRequest request, 
  HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      String flag = request.getParameter("flag");
      try {
        flag = " FLAG = " + Integer.parseInt(flag);
      } catch (NumberFormatException e) {
        flag = " 1=1 ";
      }
      String name = this.searchCondition(request.getParameter("name"), "NAME");
      String principal = this.searchCondition(request.getParameter("principal"), "PRINCIPAL");
      String languageKind = this.searchCondition(request.getParameter("languageKind"), "LANGUAGE_KIND");
      String languageLevel = this.searchCondition(request.getParameter("languageLevel"), "LANGUAGE_LEVEL");
      String services = request.getParameter("services");
      String servicesOther = this.searchCondition(request.getParameter("servicesOther"), "SERVICES_OTHER");
      
      String servCondition = "";
      if (services != null){
        for (String service : services.split(",")){
          try {
            Integer.parseInt(service);
          } catch (NumberFormatException e){
            continue;
          }
          if ("".equals(servCondition)){
            servCondition = " and (";
          }
          if ("4".equals(service)){
            servCondition += " " + servicesOther + " or ";
          }
          else {
            servCondition += T9DBUtility.findInSet(service, "SERVICES") + " or ";
          }
        }
      }
      if (!"".equals(servCondition)){
        servCondition.endsWith("or ");
        servCondition = servCondition.substring(0, servCondition.length() - 4);
        servCondition += ") ";
      }
      String sql = "select POSTULANT.SEQ_ID" +
              ",POSTULANT.POSTULANT_ID" +
              ",POSTULANT.NAME" +
              ",POSTULANT_INFO.AMOUNT" +
              ",POSTULANT_INFO.LANGUAGE_KIND" +
              ",POSTULANT_INFO.LANGUAGE_LEVEL" +
              ",POSTULANT.PRINCIPAL" +
              ",POSTULANT.PRINCIPAL_DUTY" +
              ",POSTULANT.PRINCIPAL_UNIT" +
              ",POSTULANT.PRINCIPAL_TEL" +
              ",POSTULANT.PRINCIPAL_CONTACT" +
              ",POSTULANT.SERVICES" +
              ",POSTULANT.SERVE_TIME_WEEKDAY" +
              ",POSTULANT.SERVE_TIME_WEEKEND" +
              ",POSTULANT.RECORD_SOURCE" +
              ",POSTULANT.FLAG" +
              " from POSTULANT, POSTULANT_INFO" +
              " where POSTULANT.POSTULANT_ID = POSTULANT_INFO.POSTULANT_ID AND" + 
              flag +
              servCondition +
              " and" + name +
              " and" + principal +
              " and" + languageKind +
              " and" + languageLevel +
              " order by POSTULANT.SEQ_ID DESC";
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
      queryParam, 
      sql);
      PrintWriter pw = response.getWriter(); 
      pw.println(pageDataList.toJson()); 
      pw.flush(); 
      return null; 
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    } 
  }
  
  
  
  private String searchCondition(String str, String fieldName) throws UnsupportedEncodingException, SQLException{
    if (str == null || "".equals(str)){
      str = "";
    }
    str = java.net.URLDecoder.decode(str, "UTF-8");
    str = " " + fieldName + " like '%" + T9DBUtility.escapeLike(str) + "%'" + T9DBUtility.escapeLike();
    return str;
  }
  
  private String toJson(Map<String,String> m) throws Exception{
    StringBuffer sb = new StringBuffer("{");
    for (Iterator<Entry<String,String>> it = m.entrySet().iterator(); it.hasNext();){
      Entry<String,String> e = it.next();
      sb.append(e.getKey());
      sb.append(":\"");
      sb.append(e.getValue());
      sb.append("\",");
    }
    if (sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("}");
    return sb.toString();
  }
}