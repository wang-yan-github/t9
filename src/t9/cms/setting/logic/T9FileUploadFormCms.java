package t9.cms.setting.logic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;

public class T9FileUploadFormCms {
  /**
   * log                                               
   */
  private static Logger log = Logger.getLogger("yzq.t9.core.util.file.T9FileUploadForm");
  /**
   * 普通表单参数 
   */
  public HashMap<String, String> paramMap = new HashMap();
  /**
   * 文件列表
   */
  public List fileList = new ArrayList();
  /**
   * 文件哈希表
   * {文件表单名，FileItem}
   */
  public HashMap fileMap = new HashMap();
  /**
   * Request
   */
  public HttpServletRequest request = null;

  
  /**
   * 取得表单参数哈希表
   * @return
   */
  public Map<String, String> getParamMap() {
    return paramMap;
  }
  
  /**
   * 默认解析协议
   * @param request    客户端请求       
   * @throws Exception
   */  
  public void parseUploadRequest(HttpServletRequest request) throws Exception {
    this.request = request;
    File tmpFile = new File(T9SysProps.getUploadCatchPath());
    if (!tmpFile.exists()) {
      tmpFile.mkdirs();
    }
    parseUploadRequest(request,
        T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE),
        10 * T9Const.K,
        T9SysProps.getString("fileUploadTempDir"),
        T9Const.DEFAULT_CODE);
  }
  /**
   * 
   * @param request
   * @param maxSize
   * @param buffSize
   * @param tempPath
   * @throws Exception
   */  
  public void parseUploadRequest(HttpServletRequest request,
      int maxSize,
      int buffSize,
      String tempPath,
      String charSet) throws Exception {
    
    DiskFileUpload fu = new DiskFileUpload();
    fu.setHeaderEncoding(charSet);
    // 设置允许用户上传文件大小,单位:字节
    fu.setSizeMax(maxSize * T9Const.M);
    // maximum size that will be stored in memory?
    // 设置最多只允许在内存中存储的数据,单位:字节
    fu.setSizeThreshold(buffSize);
    // 设置一旦文件大小超过getSizeThreshold()的值时数据存放在硬盘的目录
    if (!T9Utility.isNullorEmpty(tempPath)) {
      fu.setRepositoryPath(tempPath);
    }
    //开始读取上传信息
    List fieldList = fu.parseRequest(request);
    
    Iterator iter = fieldList.iterator();
    while (iter.hasNext()) {
      FileItem item = (FileItem) iter.next();
      //文件字段
      if (!item.isFormField()) {
        fileList.add(item);
        fileMap.put(item.getFieldName(), item);
      //普通表单字段
      }else {
        if (charSet != null) {
          paramMap.put(item.getFieldName(), item.getString(charSet));
        }else {
          paramMap.put(item.getFieldName(), item.getString());
        }
      }
    }
  }
  
  /**
   * 取得参数
   * @param paramName
   * @return
   */
  public String getParameter(String paramName) {
    return (String)paramMap.get(paramName);
  }
  
  /**
   * 取得文件字段名称列表
   */
  public Iterator iterateFileFields() {
    return fileMap.keySet().iterator();
  }
  
  /**
   * 取得普通字段名称列表
   */
  public Iterator iterateParamFiels() {
    return paramMap.keySet().iterator();
  }
  
  /**
   * 用字段名称取得文件项目
   */
  public FileItem getFileItem(String fieldName) {
    return (FileItem)fileMap.get(fieldName);
  }
  
  /**
   * 取得文件的输入流
   * @param fieldName
   * @return
   */
  public InputStream getInputStream() throws IOException {
    if (fileList == null) {
      return null;
    }
    FileItem fileItem = (FileItem)fileList.get(0);
    return fileItem.getInputStream();
  }
  /**
   * 取得文件的输入流
   * @param fieldName
   * @return
   */
  public InputStream getInputStream(String fieldName) throws IOException {
    FileItem item = getFileItem(fieldName);
    if (item != null) {
      return item.getInputStream();
    }
    return null;
  }
  
  /**
   * 取得文件的扩展名
   * @return
   * @throws IOExeption
   */
  public String getFileExt(FileItem fileItem) throws IOException {
    return T9FileUtility.getFileExtName(fileItem.getName());
  }
  
  /**
   * 取得文件的扩展名
   * @return
   * @throws IOExeption
   */
  public String getFileExt(String fieldName) throws IOException {
    return getFileExt(getFileItem(fieldName));
  }
  
  /**
   * 取得第一个文件的扩展名
   * @return
   * @throws IOExeption
   */
  public String getFileExt() throws IOException {
    if (fileList == null) {
      return null;
    }
    FileItem fileItem = (FileItem)fileList.get(0);
    return getFileExt(fileItem);
  }
  
  /**
   * 取得第一个文件的扩展名
   * @return
   * @throws IOExeption
   */
  public String getFileNameNoExt() throws IOException {
    if (fileList == null) {
      return null;
    }
    String fileName = getFileName();
    return T9FileUtility.getFileNameNoExt(fileName);
  }
  
  /**
   * 取得文件的名称
   * @return
   * @throws IOExeption
   */
  public String getFileName(FileItem fileItem) throws IOException {
    return T9FileUtility.getFileName(fileItem.getName());
  }
  
  /**
   * 取得文件的名称
   * @param fieldName
   * @return
   */
  public String getFileName(String fieldName) throws IOException {
    return getFileName(getFileItem(fieldName));
  }
  
  /**
   * 取得文件的大小
   * @param fieldName
   * @return
   */
  public long getFileSize(String fieldName) throws IOException {
    FileItem fileItem = getFileItem(fieldName);
    if (fileItem == null) {
      return 0;
    }
    return fileItem.getSize();
  }
  
  /**
   * 取得第一个文件的名称
   * @return
   */
  public String getFileName() throws IOException {
    if (fileList == null) {
      return null;
    }
    FileItem fileItem = (FileItem)fileList.get(0);
    return getFileName(fileItem);
  }
  
  /**
   * 保存上传文件
   * @param fileItem      文件项目
   * @param savePath      保存文件的全路径
   * @throws IOException
   */ 
  private void saveFile(FileItem fileItem,
      String savePath) throws IOException {
    if (fileItem == null) {
      return;
    }
    if (savePath == null) {
      return;
    }
    File outFile = new File(savePath);
    File outPath = outFile.getParentFile();
    if (!outPath.exists()) {
      outPath.mkdirs();
    }
    if (!outFile.exists()) {
      outFile.createNewFile();
    }
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new BufferedInputStream(fileItem.getInputStream());
      out = new BufferedOutputStream(
          new FileOutputStream(outFile));
      byte[] buff = new byte[T9Const.M];
      int length = 0;
      while ((length = in.read(buff)) > 0) {
        out.write(buff, 0, length);
        out.flush();
      }
    }catch(IOException ex) {
      throw ex;
    }finally {
      try {
        if (in != null) {
          in.close();
        }
      }catch(Exception ex) {        
      }
      try {
        if (out != null) {
          out.close();
        }
      }catch(Exception ex) {        
      }
    }
    //保存到数据库
    String tmpStr = null;
    int tmpIndex = savePath.lastIndexOf("\\");
    if (tmpIndex >= 4) {
      tmpStr = savePath.substring(tmpIndex - 4);
      tmpStr = tmpStr.replace("\\", "_");
    }
    if (tmpStr != null && Pattern.matches("^\\d{4}_[0-9a-z]{32}_.*$", tmpStr)) {
      tmpIndex = tmpStr.indexOf("_", 5);
      String fileId = tmpStr.substring(0, tmpIndex);
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9ORM orm = new T9ORM();
        Map valueMap = new HashMap();
        valueMap.put("fileId", fileId);
        valueMap.put("filePath", savePath);
        valueMap.put("createTime", T9Utility.getCurDateTimeStr());
        //orm.saveSingle(dbConn, "SignFiles", valueMap);
      }catch(Exception ex) {
        ex.printStackTrace();
        throw new IOException(ex);
      }
    }
  }
  
  /**
   * 保存指定文件
   * @param fieldName            表单字段名称
   * @param savePath             保存文件的全路径
   * @throws IOException
   */
  public void saveFile(String fieldName,
      String savePath) throws IOException {
    saveFile(getFileItem(fieldName), savePath);
  }
  
  /**
   * 保存列表中第一个文件
   * @param savePath
   * @throws IOException
   */
  public void saveFile(String savePath) throws IOException {
    if (fileList == null) {
      return;
    }
    FileItem fileItem = (FileItem)fileList.get(0);
    saveFile(fileItem, savePath);
  }
  
  /**
   * 保存列表中第一个文件
   * @param savePath
   * @throws IOException
   */
  public String saveFileAll(String savePath) throws IOException {
    String filePrefix = null;
    filePrefix = T9Utility.getRandomName();
    return saveFileAll(savePath, filePrefix);
  }
  
  /**
   * 保存列表中第一个文件
   * @param savePath
   * @throws IOException
   */
  public String saveFileAll(String savePath, String filePrefix) throws IOException {
    if (fileList == null) {
      return null;
    }
    if (filePrefix == null) {
      filePrefix = "";
    }
    for (int i = 0; i < fileList.size(); i++) {
      FileItem fileItem = (FileItem)fileList.get(i);
      String fileExtName = getFileName(fileItem);
      if (T9Utility.isNullorEmpty(fileExtName)) {
        continue;
      }
      String filePath = savePath + "\\";
      if (!T9Utility.isNullorEmpty(filePrefix)) {
        filePath += filePrefix + "\\";
      }
      filePath += fileExtName;
      saveFile(fileItem, filePath);
    }
    return filePrefix;
  }
  
  /**
   * 判断文件是否已经存在
   * @param savePath
   * @throws IOException
   */
  public String getExists(String savePath) throws IOException {
    if (fileList == null) {
      return null;
    }
    for (int i = 0; i < fileList.size(); i++) {
      FileItem fileItem = (FileItem)fileList.get(i);
      String fileExtName = getFileName(fileItem);
      if (T9Utility.isNullorEmpty(fileExtName)) {
        continue;
      }
      String filePath = savePath + "\\" + fileExtName;
      if (new File(filePath).exists()) {
        return fileExtName;
      }
    }
    return null;
  }
}
