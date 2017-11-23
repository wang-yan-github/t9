package t9.subsys.inforesouce.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.inforesouce.data.T9MetaFilterConf;
import t9.subsys.inforesouce.data.T9SignFile;

/**
 * 文件元数据数据库辅助类
 * @author yzq
 *
 */
public class T9MetaDbHelper {
  //log
  private static Logger log = Logger.getLogger("yzq.t9.subsys.inforesouce.db.T9MetaUpdate"); 
  //过滤器配置
  private static Map<String, Class> filterMap = new HashMap<String, Class>();
  //缺省过滤器
  private static Class defaultFilter = null;
  
  /**
   * 加载过滤器配置信息
   */
  private static void loadFilterConf() {
    String confFile = T9SysProps.getWebInfPath() + "\\config\\inforesconfig.properties";
    Map<String, String> rawConfMap = new HashMap<String, String>();
    try {
      T9FileUtility.load2Map(confFile, rawConfMap);
      Map<String, String> confMap = T9Utility.startsWithMap(rawConfMap, "metaFilter");
      
      List confList = new ArrayList();
      Iterator<String> iKeys = confMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String filterConfJson = confMap.get(key).trim();
        if (T9Utility.isNullorEmpty(filterConfJson)) {
          continue;
        }
        try {
          confList.add((T9MetaFilterConf)T9FOM.json2Obj(filterConfJson, T9MetaFilterConf.class));
        }catch(Exception ex) {
          log.debug(ex.getMessage(), ex);
        }
      }
      for (int i = 0; i < confList.size(); i++) {
        T9MetaFilterConf filterConf = (T9MetaFilterConf)confList.get(i);
        String typeNoStr = filterConf.getTypeNos();
        String clsStr = filterConf.getFilter();
        if (T9Utility.isNullorEmpty(typeNoStr)) {
          defaultFilter = Class.forName(clsStr);
        }else {
          String[] typeNoArray = typeNoStr.split(",");
          try {
            Class filterClass = Class.forName(clsStr);
            for (int j = 0; j < typeNoArray.length; j++) {
              String typeNo = typeNoArray[j].trim();
              if (!T9Utility.isNullorEmpty(typeNo)) {
                filterMap.put(typeNo, filterClass);
              }
            }
          }catch(Exception ex) {            
          }
        }
      } 
      if (defaultFilter == null) {
        try {
          defaultFilter = Class.forName("t9.subsys.inforesouce.db.T9MetaEqualFilter");
        }catch(Exception ex) {
          log.debug(ex.getMessage(), ex);
        }
      }
    }catch(Exception ex) {
      log.debug(ex.getMessage(), ex);
    }
  }
  
  static {
    loadFilterConf();
  }
  /**
   * 更新元数据，元数据不存在，则插入，存在则更新
   * 要求该文件已经在文件中心中已经存在，否则不更新
   * @param dbConn
   * @param guid           
   * @param dataMap {M1:"", M2:"", MEX101:"", MEX102:""}
   * @throws Exception
   */
  public void updateMetadata(Connection dbConn,
      String guid,
      String filePath,
      Map<String, String> dataMap, String zhaiYao, String fileType, int pseqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID from SIGN_FILES"
        + " where FILE_ID='" + guid + "'";
      int seqId = 0;
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        seqId = rs.getInt(1);
      }
      if (seqId < 1 && !T9Utility.isNullorEmpty(filePath)) {
        insertMainData(dbConn, guid, filePath, zhaiYao,fileType, pseqId);
        sql = "select SEQ_ID from SIGN_FILES"
          + " where FILE_ID='" + guid + "'";
        seqId = 0;
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
          seqId = rs.getInt(1);
        }
        if (seqId < 1) {
          return;
        }
      }
      List<Map> mapList = splitRawData(dataMap);
      Map dataMap1 = mapList.get(0);
      Map dataMap2 = mapList.get(1);
      
      doUpdateAttr(stmt, "FILE_ATTRS01", seqId, dataMap1);
      doUpdateAttr(stmt, "FILE_ATTRS02", seqId, dataMap2);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 更新元数据，元数据不存在，则插入，存在则更新
   * 要求该文件已经在文件中心中已经存在，否则不更新
   * @param dbConn
   * @param guid           
   * @param dataMap {M1:"", M2:"", MEX101:"", MEX102:""}
   * @throws Exception
   */
  public void genTestMetadata(Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, TITLE from SIGN_FILES";
      List seqIdList = new ArrayList();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        seqIdList.add(new Object[]{rs.getInt(1), rs.getString(2)});
      }
      String[] attrNames = new String[]{
          "ATTR_034"   //文种
          }; //摘要
      String[][] attrArray = new String[][]{
          {"函"},
          {"批复"},
          {"报告"},
          {"请示"},
          {"通报"},
          {"通知"}
      };
      Map dataMap = new HashMap();
      for (int i = 0; i < seqIdList.size(); i++) {
        Object[] record = (Object[])seqIdList.get(i);
        int seqId = (Integer)record[0];
        String title = (String)record[1]; 
        dataMap.clear();
        String[] attrValueArray = attrArray[i % attrArray.length];
        for (int j = 0; j < attrNames.length; j++) {
          dataMap.put(attrNames[j], attrValueArray[j]);
        }
        dataMap.put("ATTR_022", title);
        dataMap.put("ATTR_023", title);
        doUpdateAttr(stmt, "FILE_ATTRS01", seqId, dataMap);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 删除文件注册
   * @param dbConn
   * @param guid
   * @throws Exception
   */
  public void deleteMetaData(Connection dbConn,
      String guid) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID from SIGN_FILES"
        + " where FILE_ID='" + guid + "'";
      int seqId = 0;
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        seqId = rs.getInt(1);
      }
      if (seqId < 1) {
        return;
      }
      sql = "delete from SIGN_FILES where SEQ_ID=" + seqId;
      stmt.executeUpdate(sql);
      sql = "delete from FILE_ATTRS01 where FILE_SEQ_ID=" + seqId;
      stmt.executeUpdate(sql);
      sql = "delete from FILE_ATTRS02 where FILE_SEQ_ID=" + seqId;
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 加载
   * @param dbConn
   * @param guidFilter
   * @return
   * @throws Exception
   */
  public T9SignFile loadFile(Connection dbConn, String guidFilter, int fileType) throws Exception {
    List fileList = searchFileList(dbConn, null, null, null, guidFilter, fileType);
    if (fileList.size() > 0) {
      return (T9SignFile)fileList.get(0);
    }
    return null;
  }
  
  /**
   * 搜索文件
   * @param dbConn
   * @param moduleList             模块编码筛选条件，编码列表
   * @param metaFilters            元数据筛选条件，哈希表
   * @return
   * @throws Exception
   */
  public List searchFileList(Connection dbConn,
      List<String> moduleList,
      Map metaFilters) throws Exception {

    return searchFileList(dbConn, moduleList, metaFilters,null, null, 1);
  }
  /**
   * 搜索文件
   * @param dbConn
   * @param moduleList             模块编码筛选条件，编码列表
   * @param metaFilters            元数据筛选条件，哈希表

   * @return
   * @throws Exception
   */
  public List searchImageList(Connection dbConn,
      List<String> moduleList,
      Map metaFilters) throws Exception {

    return searchFileList(dbConn, moduleList, metaFilters,null, null, 2);
  }
  /**
   * 查找文件列表
   * @param dbConn
   * @param moduleList             模块编码筛选条件，编码列表
   * @param metaFilters            元数据筛选条件，哈希表
   * @param idFilter               流水号串，以逗号分隔，单个认为是相等
   * @param guidFilter             guid筛选条件，以逗号分隔，单个认为是相等
   * @return
   * @throws Exception
   */
  public List searchFileList(Connection dbConn,
      List<String> moduleList,
      Map metaFilters,
      String idFilter,
      String guidFilter, int fileType) throws Exception {

    List rtList = new ArrayList();
    
    Statement stmt = null;
    PreparedStatement preStmt1 = null;
    PreparedStatement preStmt2 = null;
    PreparedStatement preStmtMain = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      StringBuffer filterBuf = new StringBuffer();
      
      if (!T9Utility.isNullorEmpty(idFilter)) {
        idFilter = idFilter.trim();
        if (idFilter.startsWith(",")) {
          idFilter = idFilter.substring(1);
        }
        if (idFilter.endsWith(",")) {
          idFilter = idFilter.substring(0, idFilter.length() - 1);
        }
        idFilter = idFilter.trim();
        if (!T9Utility.isNullorEmpty(idFilter) && idFilter.indexOf(",") > 0) {
          filterBuf.append("SEQ_ID in (" + idFilter + ") and ");
        }else {
          filterBuf.append("SEQ_ID=" + idFilter + " and ");
        }
      }
      
      if (!T9Utility.isNullorEmpty(guidFilter)) {
        guidFilter = guidFilter.trim();
        if (guidFilter.startsWith(",")) {
          guidFilter = guidFilter.substring(1);
        }
        if (guidFilter.endsWith(",")) {
          guidFilter = guidFilter.substring(0, guidFilter.length() - 1);
        }
        guidFilter = guidFilter.trim();
        if (!T9Utility.isNullorEmpty(guidFilter) && guidFilter.indexOf(",") > 0) {
          filterBuf.append("FILE_ID in (" + guidFilter + ") and ");
        }else {
          filterBuf.append("FILE_ID='" + guidFilter + "' and ");
        }
      }
      
      StringBuffer moduleFilter = new StringBuffer();
      if (moduleList != null) {
        for (int i = 0; i < moduleList.size(); i++) {
          String module = moduleList.get(i);
          moduleFilter.append("'" + module + "'");
          moduleFilter.append(",");
        }
      }
      if (moduleFilter.length() > 0) {
        moduleFilter.delete(moduleFilter.length() - 1, moduleFilter.length());
        filterBuf.append("MODULE_NO in (");
        filterBuf.append(moduleFilter);
        filterBuf.append(") and ");
      }
      if (filterBuf.length() > 5) {
        filterBuf.delete(filterBuf.length() - 5, filterBuf.length());
      }
      String sql = "select SEQ_ID from SIGN_FILES where FILE_TYPE =" + fileType;
      if (filterBuf.length() > 0) {
        sql = sql + " and " + filterBuf.toString();
      }
      sql = sql + " order by UPDATE_TIME DESC";      
      rs = stmt.executeQuery(sql);
      List<Integer> idList = new ArrayList<Integer>();
      int cnt = 0;
      while (rs.next()) {
        idList.add(new Integer(rs.getInt(1)));
        cnt++;
        //最多取前200条
        if (cnt >= 100) {
          break;
        }
      }
      if (idList.size() < 1) {
        return rtList;
      }
      
      Map metaFilterMap = getMetaFilters(metaFilters);
      preStmtMain = createPreStmtMain(dbConn, fileType);
      preStmt1 = createPreStmtMeta(dbConn, "FILE_ATTRS01", 1);
      preStmt2 = createPreStmtMeta(dbConn, "FILE_ATTRS02", 101);
      for (int i = 0; i < idList.size(); i++) {
        int seqId = idList.get(i).intValue();
        if (metaFilters == null || metaFilters.size() < 1) {
          Map fileMetas = loadMetaData(preStmt1, preStmt2, seqId, metaFilterMap);
          T9SignFile file = loadMainData(preStmtMain, seqId);
          file.putAttr(fileMetas);
          rtList.add(file);
        }else {
          Map fileMetas = loadMetaData(preStmt1, preStmt2, seqId, metaFilterMap);
          if (fileMetas.size() < 1) {
            continue;
          }
          T9SignFile file = loadMainData(preStmtMain, seqId);
          file.putAttr(fileMetas);
          rtList.add(file);
        }
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
      T9DBUtility.close(preStmt1, rs, log);
      T9DBUtility.close(preStmt2, rs, log);
      T9DBUtility.close(preStmtMain, rs, log);
    }
    
    return rtList;
  }

  /**
   * 将文件插入文件中心
   * @param dbConn
   * @param guid
   * @param moduleNo
   */
  private void insertMainData(Connection dbConn, String guid, String filePath, String zhaiYao,String fileType, int seqId) throws Exception {
    PreparedStatement preStmt = null;
    String temp = "";
    try {
      if (T9Utility.isNullorEmpty(filePath)) {
        return;
      }
      File file = new File(filePath);
      if (!file.isFile() || !file.exists()) {
        return;
      }
      if (guid.indexOf("_") != 4) {       
        return;
      }else{
        temp = guid.substring(guid.indexOf("_")+1, guid.length());
      }
      int tmpInt = filePath.indexOf("\\" + guid.substring(0, 4) + "\\" + guid.substring(5));
      if (tmpInt < 4) {
        return;
      }
      String tmpStr = filePath.substring(0, tmpInt);
      String moduleNo = tmpStr.substring(tmpStr.lastIndexOf("\\") + 1);
      preStmt = dbConn.prepareStatement("insert into SIGN_FILES (MODULE_NO,RECORD_ID,FILE_ID,FILE_PATH,CREATE_TIME,UPDATE_TIME,TITLE,ABSTRACT,FILE_SIZE, FILE_TYPE) values (?,?,?,?,?,?,?,?,?,?)");
      preStmt.setString(1, moduleNo);
      preStmt.setInt(2, seqId);
      preStmt.setString(3, guid);
      preStmt.setString(4, file.getAbsolutePath());
      preStmt.setTimestamp(5, T9Utility.parseTimeStamp(file.lastModified()));
      preStmt.setTimestamp(6, T9Utility.parseTimeStamp(file.lastModified()));
      preStmt.setString(7,filePath.substring(filePath.indexOf(temp)+1+temp.length(), filePath.lastIndexOf(".")));
      preStmt.setString(8, zhaiYao);
      preStmt.setLong(9, file.length());
      preStmt.setString(10, fileType);     
      preStmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(preStmt, null, log);
    }
  }
  /**
   * 取得元数据过滤器
   * @param metaFilters
   * @return
   */
  private Map<String, T9IMetaFilter> getMetaFilters(Map<String, String> metaFilters) {
    Map<String, T9IMetaFilter> rtMap = new HashMap<String, T9IMetaFilter>();
    
    if (metaFilters == null) {
      return rtMap;
    }
    Iterator<String> iKeys = metaFilters.keySet().iterator();
    while (iKeys.hasNext()) {
      String typeNo = iKeys.next();
      String valueStr = metaFilters.get(typeNo);
      if (valueStr != null) {
        valueStr = valueStr.trim();
      }
      if (T9Utility.isNullorEmpty(valueStr)) {
        continue;
      }
      Class filterClass = filterMap.get(typeNo);
      if (filterClass == null) {
        filterClass = defaultFilter;
      }
      try {
        T9IMetaFilter filter = (T9IMetaFilter)filterClass.newInstance();
        filter.parse(valueStr);
        if (typeNo.startsWith("MEX")) {
          rtMap.put("ATTR_" + T9Utility.getFixLengthStringFront(typeNo.substring(3), 3), filter);
        }else if (typeNo.startsWith("M")) {
          rtMap.put("ATTR_" + T9Utility.getFixLengthStringFront(typeNo.substring(1), 3), filter);
        }
      }catch(Exception ex) {
        log.debug(ex);
      }
    }
    return rtMap;
  }
  
  /**
   * 构造查询文件主表数据的语句
   * @param dbConn
   * @return
   * @throws Exception
   */
  private PreparedStatement createPreStmtMain(Connection dbConn, int flag) throws Exception {
    String sql = "select SEQ_ID, MODULE_NO, RECORD_ID, FILE_ID, FILE_PATH"
      + ", CREATE_TIME, UPDATE_TIME, SIGN_FLAG, LAST_SIGN_TIME, SIGN_TYPE, FULL_TEXT_FLAG, FULL_TEXT_TIME, TITLE,FILE_SIZE,ABSTRACT"
      + " from SIGN_FILES where SEQ_ID=? and FILE_TYPE = " + flag;
    return dbConn.prepareStatement(sql);
  }
  
  /**
   * 构造查询元数据语句
   * @param dbConn
   * @param tableName
   * @param startIndex           1=FILE_ATTRS01;      101=FILE_ATTRS02
   * @return
   */
  private PreparedStatement createPreStmtMeta(Connection dbConn,
      String tableName,
      int startIndex) throws Exception {
    
    StringBuffer fieldBuf = new StringBuffer();
    for (int i = 0; i < 100; i++) {
      fieldBuf.append("ATTR_" + T9Utility.getFixLengthStringFront(String.valueOf(i + startIndex), 3));
      fieldBuf.append(",");
    }
    fieldBuf.delete(fieldBuf.length() - 1, fieldBuf.length());
    String sql = "select " + fieldBuf + " from " + tableName
      + " where FILE_SEQ_ID=?";
    
    return dbConn.prepareStatement(sql);
  }
  
  /**
   * 加载主表数据
   * @param stmt
   * @param seqId
   * @return
   * @throws Exception
   */
  private T9SignFile loadMainData(PreparedStatement stmt, int seqId) throws Exception {
    T9SignFile rtFile = new T9SignFile();
    ResultSet rs = null;
    try {
      stmt.clearParameters();
      stmt.setInt(1, seqId);
      rs = stmt.executeQuery();
      if (rs.next()) {
        int fieldCnt = 0;
        rtFile.setSeqId(rs.getInt(++fieldCnt));
        rtFile.setModuleNo(rs.getString(++fieldCnt));
        rtFile.setRecordId(rs.getString(++fieldCnt));
        rtFile.setFileId(rs.getString(++fieldCnt));
        rtFile.setFilePath(rs.getString(++fieldCnt));
        rtFile.setCreateTime(rs.getTimestamp(++fieldCnt));
        rtFile.setUpdateTime(rs.getTimestamp(++fieldCnt));
        rtFile.setSignFlag(rs.getString(++fieldCnt));
        rtFile.setLastSignTime(rs.getTimestamp(++fieldCnt));
        rtFile.setSignType(rs.getString(++fieldCnt));        
        rtFile.setFullTextFlag(rs.getString(++fieldCnt));
        rtFile.setFullTextTime(rs.getTimestamp(++fieldCnt)); 
        rtFile.setTitle(rs.getString(++fieldCnt));
        rtFile.setFile_Sizes(Long.parseLong(rs.getString(++fieldCnt)));
        rtFile.setAbstracts(rs.getString(++fieldCnt));
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(null, rs, log);
    }
    
    return rtFile;
  }
  
  /**
   * 数据是否匹配
   * @param filterMap
   * @param metaMap
   * @return
   */
  private boolean isMatch(Map<String, T9IMetaFilter> filterMap, Map<String, String> metaMap) {
    if (filterMap == null || filterMap.size() < 1) {
      return true;
    }
    Iterator<String> iKeys = filterMap.keySet().iterator();
    while (iKeys.hasNext()) {
      String filterName = iKeys.next();
      T9IMetaFilter filter = filterMap.get(filterName);
      String valueStr = metaMap.get(filterName);
      if (!filter.isMatch(valueStr)) {
        return false;
      }
    }
    return true;
  }
  /**
   * 提取文件元数据
   */
  private Map loadMetaData(PreparedStatement stmt1,
      PreparedStatement stmt2,
      int seqId,
      Map filterMap) throws Exception {
    Map rtMap = new HashMap();
    
    ResultSet rs = null;
    try {
      stmt1.clearParameters();
      stmt1.setInt(1, seqId);
      rs = stmt1.executeQuery();
      if (rs.next()) {
        for (int i = 0; i < 100; i++) {
          String value = rs.getString(i + 1);
          if (value == null) {
            continue;
          }
          value = value.trim();
          if (value.length() < 1) {
            continue;
          }
          rtMap.put("ATTR_" + T9Utility.getFixLengthStringFront(String.valueOf((i + 1)), 3), value);
        }
      }
      stmt2.clearParameters();
      stmt2.setInt(1, seqId);
      rs = stmt2.executeQuery();
      if (rs.next()) {
        for (int i = 0; i < 100; i++) {
          String value = rs.getString(i + 1);
          if (value == null) {
            continue;
          }
          value = value.trim();
          if (value.length() < 1) {
            continue;
          }
          rtMap.put("ATTR_" + T9Utility.getFixLengthStringFront(String.valueOf((i + 101)), 3), value);
        }
      }
      if (filterMap!= null && filterMap.size() > 0 && !isMatch(filterMap, rtMap)) {
        return new HashMap();
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(null, rs, log);
    }
    return rtMap;
  }
  
  /**
   * 更新属性表
   * @param stmt
   * @param tableName
   * @param dataMap
   * @throws Exception
   */
  private void doUpdateAttr(Statement stmt, String tableName, int fileSeqId, Map<String, String> dataMap) throws Exception {
    if (fileSeqId < 1 || dataMap == null || dataMap.size() < 1) {
      return;
    }
    ResultSet rs = null;
    try {
      int recordCnt = 0;
      String sql = "select count(SEQ_ID) from " + tableName
        + " where FILE_SEQ_ID=" + fileSeqId;      
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        recordCnt = rs.getInt(1);
      }
      //已经存在该文件的元数据
      if (recordCnt < 1) {
        StringBuffer fieldBuf = new StringBuffer();
        StringBuffer valueBuf = new StringBuffer();
        Iterator<String> iKeys = dataMap.keySet().iterator();
        while (iKeys.hasNext()) {
          String fieldName = iKeys.next();
          String value = dataMap.get(fieldName);
          fieldBuf.append(fieldName);
          fieldBuf.append(",");
          valueBuf.append("'");
          valueBuf.append(value);
          valueBuf.append("',");
        }
        if (fieldBuf.length() > 0) {
          fieldBuf.delete(fieldBuf.length() - 1, fieldBuf.length());
        }
        if (valueBuf.length() > 0) {
          valueBuf.delete(valueBuf.length() - 1, valueBuf.length());
        }
        sql = "insert into " + tableName
          + "(FILE_SEQ_ID," + fieldBuf + ")"
          + " values (" + fileSeqId + "," + valueBuf + ")";
        stmt.executeUpdate(sql);
      //该文件的元数据还不存在
      }else {
        StringBuffer setBuf = new StringBuffer();
        Iterator<String> iKeys = dataMap.keySet().iterator();
        while (iKeys.hasNext()) {
          String fieldName = iKeys.next();
          String value = dataMap.get(fieldName);
          setBuf.append(fieldName);
          setBuf.append("=");
          setBuf.append("'");
          setBuf.append(value);
          setBuf.append("',");
        }
        if (setBuf.length() > 0) {
          setBuf.delete(setBuf.length() - 1, setBuf.length());
        }
        sql = "update " + tableName
          + " set " + setBuf
          + " where FILE_SEQ_ID=" + fileSeqId;
        stmt.executeUpdate(sql);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(null, rs, log);
    }
  }

  /**
   * 把原始的数据进行分离，分离成两个表需要的数据，同时将编码代表的Key转换成字段名称
   * @param dataMap
   * @return
   */
  private List<Map> splitRawData(Map<String, String> dataMap) {
    List<Map> rtList = new ArrayList<Map>();
    Map map1 = new HashMap();
    Map map2 = new HashMap();
    rtList.add(map1);
    rtList.add(map2);
    
    Iterator<String> iKeys = dataMap.keySet().iterator();
    while(iKeys.hasNext()) {
      String key = iKeys.next();
      String value = dataMap.get(key);
      if (key.startsWith("MEX")) {
        map2.put("ATTR_" + T9Utility.getFixLengthStringFront(key.substring(3), 3) , value);
      }else if (key.startsWith("M")) {
        map1.put("ATTR_" + T9Utility.getFixLengthStringFront(key.substring(1), 3), value);
      }
    }
    return rtList;
  }
}
