package t9.subsys.infomgr.bilingual.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9DsType;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.picture.act.T9ImageUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.generics.T9SQLParamHepler;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.core.util.mail.T9MailSenderInfo;
import t9.core.util.mail.T9SimpleMailSender;
import t9.subsys.infomgr.bilingual.data.T9BilingualCorrection;
import t9.subsys.infomgr.bilingual.logic.T9CorrectionLogic;

public class T9CorrectionAct {
  public static final String CORRECTION_PATH = "\\bilingual\\correction";
  
  private T9CorrectionLogic logic = new T9CorrectionLogic();
   
  /**
   * 增加记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addCorrection(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    
    fileForm.parseUploadRequest(request);
    
    Iterator<String> iKeys = fileForm.iterateFileFields();
    
    T9ImageUtil iu = new T9ImageUtil();
    String fileName = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String sp = System.getProperty("file.separator");
      String filePath = request.getSession().getServletContext().getRealPath(sp) + T9CorrectionAct.CORRECTION_PATH + "\\";
      fileName = T9Guid.getRawGuid() + "_" + fileName;
      fileForm.saveFile(fieldName, filePath + fileName);
      iu.saveImageAsJpg(filePath + fileName, filePath + "thumb-" + fileName, 100, 100);
    }
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = fileForm.getParamMap();
      T9BilingualCorrection bi = (T9BilingualCorrection) T9FOM.build(map, T9BilingualCorrection.class, "");
      bi.setPicture(fileName);
      bi.setFlag("0");
      this.logic.addCorrection(dbConn, bi);
      
      request.setAttribute("msg", "添加成功");
    }catch(Exception ex) {
      request.setAttribute("msg", "添加未成功");
      throw ex;
    }finally{
      
    }
    return "/subsys/infomgr/bilingual/success.jsp";
  }
  
  /**
   * 增加记录网站使用
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String add4website(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    return addCorrection(request, response);
    /*
    T9FileUploadForm fileForm = new T9FileUploadForm();
    
    fileForm.parseUploadRequest(request);
    
    Iterator<String> iKeys = fileForm.iterateFileFields();
    
    T9ImageUtil iu = new T9ImageUtil();
    String fileName = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String sp = System.getProperty("file.separator");
      String filePath = request.getSession().getServletContext().getRealPath(sp) + T9CorrectionAct.CORRECTION_PATH + "\\";
      fileName = T9Guid.getRawGuid() + "_" + fileName;
      fileForm.saveFile(fieldName, filePath + fileName);
      iu.saveImageAsJpg(filePath + fileName, filePath + "thumb-" + fileName, 100, 100);
    }
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = fileForm.getParamMap();
      T9BilingualCorrection bi = (T9BilingualCorrection) T9FOM.build(map, T9BilingualCorrection.class, "");
      bi.setFlag("0");
      bi.setPicture(fileName);
      this.logic.addCorrection(dbConn, bi);
      request.setAttribute("msg", "感谢您的参与!");
    }catch(Exception ex) {
      request.setAttribute("msg", "添加未成功");
      throw ex;
    }finally{
      return "/subsys/infomgr/correction/success.jsp";
    }*/
  }
  
  /**
   * 修改一条记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String modifyCorrection(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = request.getParameterMap();
      T9BilingualCorrection bi = (T9BilingualCorrection) T9FOM.build(map, T9BilingualCorrection.class, "");
      
      this.logic.modifyCorrection(dbConn, bi);
      request.setAttribute("msg", "修改成功");
    }catch(Exception ex) {
      request.setAttribute("msg", "修改未成功");
      ex.printStackTrace();
      throw ex;
    }finally{
      return "/subsys/infomgr/bilingual/success.jsp";
    }
  }
  
  /** 
  * 取得双语标示的信息,并分页  * @param request 
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
    
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
      queryParam, 
      "select SEQ_ID" +
      ",CONTENT" +
      ",PICTURE" +
      ",CHANGES" +
      ",TYPE" +
      ",LOCATION" +
      ",CORRECT_DATE" +
      ",CORRECTER" +
      ",WORKPLACE" +
      ",EMAIL" +
      ",ADDRESS" +
      ",TEL" +
      ",FLAG" +
      " from BILINGUAL_CORRECTION" +
      " order by CORRECT_DATE desc"); 
    
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
  
  /** 
   * 取得双语标示的信息,并分页   * @param request 
   * @param response 
   * @return 
   * @throws Exception 
   */ 
  public String getPage4WebSite(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
          queryParam, 
          "select SEQ_ID" +
          ",CONTENT" +
          ",PICTURE" +
          ",ADDRESS" +
          ",CHANGES" +
          " from BILINGUAL_CORRECTION" +
          " where FLAG = '1'" +
      " order by CORRECT_DATE desc"); 
      
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
  
  /** 
   * 取得双语标示未确认的信息,并分页
   * @param request 
   * @param response 
   * @return 
   * @throws Exception 
   */ 
  public String getPageNotConfirm(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
          queryParam, 
          "select SEQ_ID" +
          ",CONTENT" +
          ",PICTURE" +
          ",CHANGES" +
          ",TYPE" +
          ",LOCATION" +
          ",CORRECT_DATE" +
          ",CORRECTER" +
          ",WORKPLACE" +
          ",EMAIL" +
          ",ADDRESS" +
          ",TEL" +
          ",FLAG" +
          " from BILINGUAL_CORRECTION" +
          " where FLAG = '0'" +
          " order by CORRECT_DATE desc"); 
      
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
  
  public String getNotConfirm(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
    List<T9BilingualCorrection> corrs = logic.getNotConfirmSqServer(dbConn);
    StringBuffer sb = new StringBuffer();
    int len = corrs.size();
    sb.append("[");
    for(int i=0 ; i<len; i++){
      if(i < len-1){
       sb.append("{ id:").append(corrs.get(i).getSeqId()).append(", location:'").append(T9Utility.encodeSpecial(corrs.get(i).getLocation())).append("', picture: '").append(T9Utility.encodeSpecial(corrs.get(i).getPicture())).append("', content:'").append(T9Utility.encodeSpecial(corrs.get(i).getContent())).append("'},");
      }else{
       sb.append("{ id:").append(corrs.get(len-1).getSeqId()).append(", location:'").append(T9Utility.encodeSpecial(corrs.get(len-1).getLocation())).append("', picture:'").append(T9Utility.encodeSpecial(corrs.get(len-1).getPicture())).append("', content:'").append(T9Utility.encodeSpecial(corrs.get(len-1).getContent())).append("'}");         
      }
    }
    sb.append("]");
    //T9Out.println(sb.toString());
    PrintWriter pw = response.getWriter();    
    String rtData = "{rtState:'0',rtData:"+sb.toString()+"}";
    pw.println(rtData);    
    pw.flush(); 
    return null;
  }
  
  /**
   * 通过条件查找记录,并分页
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchPage(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    
    String location = request.getParameter("location");
    location = location == null ? "" : location;
    
    String correctDate = request.getParameter("correctDate");
    
    Map map = new HashMap();
    int i = 1;
    if (correctDate == null || "".equals(correctDate)){
      correctDate = "";
    }
    else {
      map.put(i, correctDate);
      correctDate = " and CORRECT_DATE = ?";
    }
    
    String type = request.getParameter("type");
    type = type == null ? "" : type;
    
    String tel = request.getParameter("tel");
    tel = tel == null ? "" : tel;
    
    String content = request.getParameter("content");
    content = content == null ? "" : content;
    content = java.net.URLDecoder.decode(content, "utf-8");
    
    String address = request.getParameter("address");
    address = address == null ? "" : address;
    address = java.net.URLDecoder.decode(address, "utf-8");
    
    String correcter = request.getParameter("correcter");
    correcter = correcter == null ? "" : correcter;
    correcter = java.net.URLDecoder.decode(correcter, "utf-8");
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      String sql = "select SEQ_ID" +
      ",CONTENT" +
      ",PICTURE" +
      ",CHANGES" +
      ",TYPE" +
      ",LOCATION" +
      ",CORRECT_DATE" +
      ",CORRECTER" +
      ",WORKPLACE" +
      ",EMAIL" +
      ",ADDRESS" +
      ",TEL" +
      ",FLAG" +
      " from BILINGUAL_CORRECTION" +
      " where LOCATION like '%" + T9DBUtility.escapeLike(location) +
      "%'" +
      correctDate +
      " and TEL like '%" + T9DBUtility.escapeLike(tel) +
      "%'" +
      " and CONTENT like '%" + T9DBUtility.escapeLike(content) +
      "%'" +
      " and ADDRESS like '%" + T9DBUtility.escapeLike(address) +
      "%'" +
      " and CORRECTER like '%" + T9DBUtility.escapeLike(correcter) +
      "%'" +
      " and TYPE like '%" + T9DBUtility.escapeLike(type) +
      "%'" +
      " order by CORRECT_DATE desc";
      
     // String dbms = T9SysProps.getProp("db.jdbc.dbms");
      
      //System.out.println(sql);
      
      T9PageQueryParam queryParam = new T9PageQueryParam();
      if (!"asc".equals(request.getParameter("direct"))) {
        queryParam.setDirect("desc");
      }
      String pageSize = request.getParameter("pageSize");
      if (T9Utility.isInteger(pageSize)) {
        queryParam.setPageSize(Integer.parseInt(pageSize));
      }
      String pageIndex = request.getParameter("pageIndex");
      if (T9Utility.isInteger(pageIndex)) {
        queryParam.setPageIndex(Integer.parseInt(pageIndex));
      }
      String sortColumn = request.getParameter("sortColumn");
      
      T9PageDataList pageDataList = this.loadPageList(dbConn, 
          queryParam, 
          sql , map); 
      
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
  /**
   * 加载分页数据
   * @param dbConn
   * @param pageSize
   * @param pageIndex
   * @param sql
   * @return
   * @throws Exception
   */
  public static T9PageDataList loadPageList(Connection dbConn,
      T9PageQueryParam queryParam,
      String sql , Map<Integer , String> valueMap) throws Exception {
    
    int pageSize = queryParam.getPageSize();
    int pageIndex = queryParam.getPageIndex();
    String sortColumn = queryParam.getSortColumn();
    String direct = queryParam.getDirect();
    
    
    String[] nameList = T9Utility.null2Empty(queryParam.getNameStr()).split(",");
    
    //设置排序字段
    if (!T9Utility.isNullorEmpty(sortColumn)) {
      StringBuilder sb = new StringBuilder();
      for (char c : sortColumn.toCharArray()) {
        if (Character.isUpperCase(c)) {
          sb.append('_');
        }
        sb.append(c);
      }
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
        Pattern p = Pattern.compile("order {1,}by.{1,}");
        Matcher m = p.matcher(sql);
        if (m.find()) {
          String sel = sql.substring(0, m.start());
          String ord = sql.substring(m.start(), m.end());
          String other = sql.substring(m.end(), sql.length());
          
          Pattern op = Pattern.compile("order {1,}by ", Pattern.CASE_INSENSITIVE);
          Matcher om = p.matcher(ord);
          String order = om.replaceAll("order by " + sb + " ");
          
          Pattern op2 = Pattern.compile("(desc|asc)", Pattern.CASE_INSENSITIVE);
          Matcher om2 = p.matcher(ord);
          order += om.replaceAll(direct);
          
          sql = sel + order + other;
        }
        else {
          sql += " order by " + sb + " " + direct;
        }
        
        
      } else if (dbms.equals("mysql")) {
        //按照gbk编码的排序

        sql = "select *" +
        " from (" + sql + ")" + " PAGE_TEMP_TABLE";
        sql += " order by CONVERT(" + sb + " USING gbk) COLLATE gbk_chinese_ci " + direct;
      } else if (dbms.equals("oracle")) {
        sql = "select *" +
        " from (" + sql + ")" + " PAGE_TEMP_TABLE";
        sql += " order by " + sb + " " + direct;
      } else {
        throw new SQLException("not accepted dbms");
      }
    }
    
    T9PageDataList rtList = new T9PageDataList();

    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      Set<Integer> keys = valueMap.keySet();
      for (Integer key : keys) {
        String value = valueMap.get(key);
        stmt.setString(key, value);
      }
      rs = stmt.executeQuery();
      //总记录数
      rs.last();
      int recordCnt = rs.getRow();
      if (recordCnt == 0) {
        return rtList;
      }
      rtList.setTotalRecord(recordCnt);
      //总页数

      int pageCnt = recordCnt / pageSize;
      if (recordCnt % pageSize != 0) {
        pageCnt++;
      }
      if (pageIndex < 0) {
        pageIndex = 0;
      }
      if (pageIndex > pageCnt - 1) {
        pageIndex = pageCnt - 1;
      }
      rs.absolute(pageIndex * pageSize + 1);
      
      int fieldCnt = nameList.length;
      
      ResultSetMetaData meta = rs.getMetaData();
      int[] typeArray = new int[fieldCnt];
      int[] scale = new int[fieldCnt];
      for (int i = 0; i < fieldCnt; i++) {
        typeArray[i] = meta.getColumnType(i + 1);
        scale[i] = meta.getScale(i + 1);
      }
      //记录取出记录的条数

      for (int i = 0; i < pageSize && !rs.isAfterLast(); i++) {
        T9DbRecord record = new T9DbRecord();
        rtList.addRecord(record);
        for (int j = 0; j < fieldCnt; j++) {
          String name = nameList[j];
          Object value = null;
          int typeInt = typeArray[j];
          if (T9DsType.isDecimalType(typeInt)) {
            if (scale[j] == 0) {
              value = new Integer(rs.getInt(j + 1));
            }else {
              value = new Double(rs.getDouble(j + 1));
            }
          }else if (T9DsType.isIntType(typeInt)) {
            value = new Integer(rs.getInt(j + 1));
          }else if (T9DsType.isLongType(typeInt)) {
            value = new Long(rs.getLong(j + 1));
          }else if (T9DsType.isDateType(typeInt)) {
            value = rs.getTimestamp(j + 1);
          }else if (typeInt == Types.CLOB) {
            value = T9SQLParamHepler.clobToString(rs.getClob(j + 1));
          }else {
            value = rs.getString(j + 1);
          }
          record.addField(name, value);
        }
        rs.next();
      }
      return rtList;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  /**
   * 删除记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteRecord(HttpServletRequest request,
          HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.deleteRecord(dbConn, Integer.parseInt(seqId));
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除记录");
      
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 确定记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String confirmRecord(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.confirmRecord(dbConn, Integer.parseInt(seqId));
      
      T9BilingualCorrection bi = this.logic.queryRecord(dbConn, Integer.parseInt(seqId));
      String toAddress = bi.getEmail();
      if (toAddress != null) {
        if (checkEmail(toAddress)) {
          sendMail(toAddress);
        }
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除记录");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 按id查询单条记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryRecord(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9BilingualCorrection bi = (T9BilingualCorrection)this.logic.queryRecord(dbConn, Integer.parseInt(seqId));
      
      request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(bi).toString()); 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage()); 
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  private void sendMail(String toAddress, String content, String subject) throws Exception {
    // 这个类主要是设置邮件
    T9MailSenderInfo mailInfo = T9MailSenderInfo.build("mailConfBiligual");
    
    if (mailInfo == null) {
      //System.out.println("请检查邮件配置信息是否正确！");
      return;
    }
    
    mailInfo.setToAddress(toAddress);
    mailInfo.setSubject(content);
    mailInfo.setContent(subject);
    
    // 这个类主要来发送邮件
    T9SimpleMailSender sms = new T9SimpleMailSender();
    sms.sendTextMail(mailInfo);// 发送文体格式

    //System.out.println("发送邮件成功！");
  }
  
  private static void sendMail(String toAddress) throws Exception {
    // 这个类主要是设置邮件
    T9MailSenderInfo mailInfo = T9MailSenderInfo.build("mailConfBiligual");
    
    if (mailInfo == null) {
      //System.out.println("请检查邮件配置信息是否正确！");
      return;
    }
    
    mailInfo.setToAddress(toAddress);
    
    // 这个类主要来发送邮件
    T9SimpleMailSender sms = new T9SimpleMailSender();
    sms.sendTextMail(mailInfo);// 发送文体格式
    
    //System.out.println("发送邮件成功！");
  }
  
  private boolean checkEmail(String email) {
    
    Pattern pattern = Pattern.compile("^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$");
    Matcher matcher = pattern.matcher(email);
    if (matcher.matches()) {
        return true;
    }
    return false;
  }
}
