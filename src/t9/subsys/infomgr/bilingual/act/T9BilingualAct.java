package t9.subsys.infomgr.bilingual.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.infomgr.bilingual.data.T9Bilingual;
import t9.subsys.infomgr.bilingual.logic.T9BilingualLogic;

public class T9BilingualAct {
  public static final String BILINGUAL_PATH = "\\bilingual";
  
  private T9BilingualLogic logic = new T9BilingualLogic();
   
  /**
   * 增加记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addBilingual(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    
    fileForm.parseUploadRequest(request);
    
    Iterator<String> iKeys = fileForm.iterateFileFields();
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String sp = System.getProperty("file.separator");
      fileForm.saveFile(fieldName, request.getSession().getServletContext().getRealPath(sp) + T9BilingualAct.BILINGUAL_PATH + "\\" + fileName);
    }
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = fileForm.getParamMap();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      T9Bilingual bi = (T9Bilingual) T9FOM.build(map, T9Bilingual.class, "");
      
      bi.setEntryUser(user.getSeqId());
      Date d = new Date(System.currentTimeMillis());
      bi.setEntryDate(d);
      bi.setEnable("0");
      
      this.logic.addBilingual(dbConn, bi);
      request.setAttribute("msg", "添加成功");
    }catch(Exception ex) {
      request.setAttribute("msg", "添加未成功");
      throw ex;
    }finally{
      return "/subsys/infomgr/bilingual/success.jsp";
    }
  }
  
  /**
   * 修改一条记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String modifyBilingual(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    
    fileForm.parseUploadRequest(request);
    
    String fileExists = fileForm.getExists(T9BilingualAct.BILINGUAL_PATH);
    Iterator<String> iKeys = fileForm.iterateFileFields();
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String sp = System.getProperty("file.separator");
      fileForm.saveFile(fieldName, request.getSession().getServletContext().getRealPath(sp) + T9BilingualAct.BILINGUAL_PATH + "\\" + fileName);
    }
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = fileForm.getParamMap();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      T9Bilingual bi = (T9Bilingual) T9FOM.build(map, T9Bilingual.class, "");
      
      //判断是否修改文件
      if(!"on".equals((String)map.get("isSoundFile"))){
        bi.setSoundFile((String)map.get("formfile"));
      }
      bi.setEntryUser(user.getSeqId());
      Date d = new Date(System.currentTimeMillis());
      bi.setEntryDate(d);
      
      this.logic.modifyBilingual(dbConn, bi);
      request.setAttribute("msg", "修改成功");
    }catch(Exception ex) {
      request.setAttribute("msg", "修改未成功");
      throw ex;
    }finally{
      return "/subsys/infomgr/bilingual/success.jsp";
    }
  }
  
  /**
   * 启用/不启用
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setEnable(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    String enable = request.getParameter("enable");
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.setEnable(dbConn, Integer.parseInt(seqId), enable);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"设置成功");
      
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
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
      ",TYPE" +
      ",CN_NAME" +
      ",EN_NAME" +
      ",SOUND_FILE" +
      ",(select USER_NAME from PERSON where SEQ_ID = ENTRY_USER) as ENTRY_USER" +
      ",ENTRY_DATE" +
      ",ENABLE from BILINGUAL" +
      " order by ENTRY_DATE desc"); 
    
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
   * 通过中文名称/英文名称查找记录,并分页
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String searchPage(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    
    String enName = request.getParameter("enName");
    enName = enName == null ? "" : enName;
    
    String type = request.getParameter("type");
    type = type == null ? "" : type;
    
    String cnName = request.getParameter("cnName");
    cnName = cnName == null ? "" : cnName;
    cnName = java.net.URLDecoder.decode(cnName, "utf-8");
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      String sql = "select SEQ_ID" +
        ",TYPE" +
        ",CN_NAME" +
        ",EN_NAME" +
        ",SOUND_FILE" +
        ",(select USER_NAME from PERSON where SEQ_ID = ENTRY_USER) as ENTRY_USER" +
        ",ENTRY_DATE" +
        ",ENABLE from BILINGUAL" +
        " where CN_NAME like '%" + T9DBUtility.escapeLike(cnName) +
        "%'" +
        " and TYPE like '%" + T9DBUtility.escapeLike(type) +
        "%'" +
        " and EN_NAME like '%" + T9DBUtility.escapeLike(enName) +
        "%'" +
        " order by ENTRY_DATE desc";
      
      //System.out.println(sql);
      
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
   * 批量导入
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String batchAdd(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    
    InputStream is = fileForm.getInputStream((String)fileForm.iterateFileFields().next());
    String soundFile = fileForm.getFileName((String)fileForm.iterateFileFields().next());
    
    String type = fileForm.getParameter("type");
    // 通过jxl解析EXCEL
    List<String[]> list = null;
    
    if(soundFile.endsWith(".xlsx")){
      list = this.parseExcelPoixlsx(is);
    }
    else{
      list = this.parseExcelPoixls(is);
    }
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      int count = 0;
      for(String[] s : list){
        T9Bilingual bi = new T9Bilingual();
        bi.setCnName(s[0]);
        bi.setEnName(s[1]);
        
        //待修改
        bi.setType(type);
        bi.setEnable("0");
        bi.setEntryDate(new Date(System.currentTimeMillis()));
        bi.setEntryUser(user.getSeqId());
        this.logic.addBilingual(dbConn, bi);
        count++;
      }
      request.setAttribute("msg","插入" + count + "条记录");
      
    }catch(Exception ex) {
      request.setAttribute("msg", ex.getMessage());
      throw ex;
    }
    
    return "/subsys/infomgr/bilingual/success.jsp";
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
      T9Bilingual bi = (T9Bilingual)this.logic.queryRecord(dbConn, Integer.parseInt(seqId));
      
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
  
  
  
  /**
   * 解析EXCEL,并返回装有中文名称和英文名称的二维数组的list
   * 使用jxl,不支持office2007
   * @param is
   * @param path
   * @return
   * @throws Exception
   */
  private List<String[]> parseExcelJxl(InputStream is) throws Exception{
    Workbook workbook = Workbook.getWorkbook(is);
    Sheet sheet = workbook.getSheet(0);
    List<String[]> list = new ArrayList<String[]>();
    
    int rows = sheet.getRows(); 
    int row = 1;
    while (row < rows){
      String cnName = sheet.getCell(1,row).getContents().trim();
      String enName = sheet.getCell(2,row).getContents().trim();
      if ("".equals(cnName) || "".equals(enName)){
        break;
      }
      
      String[] f = new String[2];
      f[0] = cnName;
      f[1] = enName;
      list.add(f);
      
      row++;
    }
    
    workbook.close();
    is.close();
    return list;
  }
  
  /**
   * 解析EXCEL,并返回装有中文名称和英文名称的二维数组的list
   * 使用POI,支持中文文件名,不支持office2007
   * @param is
   * @param path
   * @return
   * @throws IOException 
   */
  private List<String[]> parseExcelPoixlsx(InputStream is) throws IOException{
    
    //POIFSFileSystem fs=new POIFSFileSystem(is);
    XSSFWorkbook wb = new XSSFWorkbook(is);
    XSSFSheet sheet = wb.getSheetAt(0); 
    List<String[]> list = new ArrayList<String[]>();
    for(Iterator it = sheet.rowIterator();it.hasNext();){
      XSSFRow r = (XSSFRow)it.next();
      if (r.getCell(1) != null && r.getCell(2) != null){
        String cnName = r.getCell(1).getStringCellValue();
        String enName = r.getCell(2).getStringCellValue();
        list.add(new String[]{cnName,enName});
      }
      else {
        continue;
      }
    }
    return list;
  }
  
  private List<String[]> parseExcelPoixls(InputStream is) throws IOException{
    HSSFWorkbook wb = new HSSFWorkbook(is);
    HSSFSheet sheet = wb.getSheetAt(0); 
    List<String[]> list = new ArrayList<String[]>();
    for(Iterator it = sheet.rowIterator();it.hasNext();){
      HSSFRow r = (HSSFRow)it.next();
      if (r.getCell((short) 1) != null && r.getCell((short) 2) != null){
        String cnName = r.getCell((short) 1).getStringCellValue();
        String enName = r.getCell((short) 2).getStringCellValue();
        list.add(new String[]{cnName,enName});
      }
      else {
        continue;
      }
    }
    return list;
  }
}