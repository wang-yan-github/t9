package t9.subsys.inforesouce.logic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.personfolder.data.T9FileContent;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.subsys.inforesouce.data.T9SignFile;

/**
 * 批量现在文件，批量转存文件
 * @author Administrator
 *
 */
public class T9BeachDownLoadFileLogic{
  /**
   *记录没有下载下的文档数
   */
  private List<T9SignFile> errorFile = new ArrayList<T9SignFile>();

  public List<T9SignFile> getErrorFile(){
    return errorFile;
  }

  public void setErrorFile(List<T9SignFile> errorFile){
    this.errorFile = errorFile;
  }

  /**
   * 把在loginUser用户可以下载的文件打包成zip文件
   * @param dbConn
   * @param loginUser 登陆的用户
   * @return
   * @throws FileNotFoundException 
   */
  public Map<String, InputStream> toZipInfoMapFile(Connection dbConn, T9Person loginUser) throws Exception{
    HashMap<String, InputStream> fileMap = new HashMap<String, InputStream>();
    try{
      List<File> fileList = getMyFileList(dbConn, loginUser, 1);
      if(fileList != null && fileList.size() > 0){
        for(int i=0; i<fileList.size(); i++){
          String name = getFileName(fileList.get(i));
          FileInputStream result =  new FileInputStream(fileList.get(i));
          fileMap.put(name , result);
        }
      }
      String html = "<html><head><title>" + "文件管理中心下载" + "</title>";
      html += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head>";
      html += "<style>body{font-size:12px;} table{border:1px #000 solid;border-collapse:collapse;} table td{border:1px #000 solid;}</style>";
      html += "<body><table width='70%' align='center'><tr><td align='center' colspan='2'><b><span class='big'>" + "文件管理中心下载说明"
           + "&nbsp;</span></b></td></tr>";
      html += "<tr><td height='250' valign='top' colspan='2'>" + "成功下载："+fileList.size()+"个文档&nbsp;&nbsp;&nbsp;<br>下载失败："+ getErrorFile().size() +"个文档&nbsp;</td></tr>";
      html += "<tr class=small><td width='100'>创建人："+ loginUser.getUserName()+"</td><td width='400'>下载日期：" + T9Utility.getCurDateTimeStr() + "&nbsp;</td></tr></table></body></html>";
      InputStream in = new ByteArrayInputStream(html.getBytes());
      fileMap.put("下载说明.html", in); // 生成的hmtl页面
    } catch (FileNotFoundException e){
      throw e;
    }
    return fileMap;
  }
  
  /**
   * 返回登陆用户所能下载的文档列表
   * @param dbConn
   * @param loginUser
   * @return
   * @throws Exception
   */
  public  List<File> getMyFileList(Connection dbConn, T9Person loginUser) throws Exception{
     List<File> fileList = new ArrayList<File>();
     String [] paths = {"D:\\t9\\attach\\file_folder\\0902\\ea7f31ab6a3ff3b029167161acb2580e_文档 18425.docx", "D:\\t9\\attach\\file_folder\\0902\\ea7f31ab6a3ff3b029167161acb2580e_文档 18426.docx", "D:\\t9\\attach\\file_folder\\0902\\ea7f31ab6a3ff3b029167161acb2580e_文档 18427.docx", "D:\\t9\\attach\\file_folder\\0902\\ea7f31ab6a3ff3b029167161acb2580e_文档 18428.docx"};
     for(int i=0; i<paths.length; i++){
        File afile = new File(paths[i]);
        if(afile.exists()){
          fileList.add(afile);
        }
     }
     return fileList;
  }
  /**
   * 返回登陆用户所能下载的文档列表
   * @param dbConn
   * @param loginUser
   * @param fType 1：文本文件   2:图片文件, 3:视频文件
   * @return
   * @throws Exception
   */
  public List<File> getMyFileList(Connection dbConn, T9Person loginUser, int fType)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql ="select SEQ_ID, FILE_ID, FILE_PATH, FILE_SIZE from sign_files where file_type=" + fType +" order by CREATE_TIME desc ";
    
    String sql1 = "select seq_id, FILE_ID, FILE_PATH,FILE_SIZE, CREATE_TIME from sign_files where seq_id in (";
           sql1 += "   select sf.SEQ_ID, sf.FILE_ID, sf.FILE_PATH,sf.FILE_SIZE, sf.CREATE_TIME  from file_priv_user fpu , sign_files sf ";
           sql1 += "   where sf.file_type = "+ fType +" and fpu.user_id = "+ loginUser.getSeqId()+"  and fpu.is_load = 1 and fpu.file_seq_id = sf.seq_id ";
           sql1 +=   " UNION ";
           sql1 += "   select sf.SEQ_ID, sf.FILE_ID, sf.FILE_PATH,sf.FILE_SIZE, sf.CREATE_TIME  from file_priv_role fpr , sign_files sf ";
           sql1 += "   where sf.file_type = "+ fType +" and fpr.role_id = "+ loginUser.getPostPriv()+" and fpr.is_load = 1  and fpr.file_seq_id = sf.seq_id ";
           sql1 +=   " UNION ";
           sql1 += "   select sf.SEQ_ID, sf.FILE_ID, sf.FILE_PATH,sf.FILE_SIZE, sf.CREATE_TIME  from file_priv_dept fpd , sign_files sf  ";
           sql1 += "   where sf.file_type = "+ fType +" and fpd.dept_id = "+ loginUser.getDeptId()+" and fpd.is_load = 1  and fpd.file_seq_id = sf.seq_id ";
           sql1 += ")  order by CREATE_TIME desc ";
           
    List<File> fileList = new ArrayList<File>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      int cnt = 0;
      while(rs.next() && ++ cnt <= 1000){
        T9SignFile file = new T9SignFile();
        file.setSeqId(rs.getInt("SEQ_ID"));
        file.setFileId(rs.getString("FILE_ID"));
        file.setFilePath(rs.getString("FILE_PATH"));
        file.setFile_Sizes(rs.getLong("FILE_SIZE"));
        File extFile = new File(file.getFilePath());
        if(extFile.exists()){
          fileList.add(extFile);
        }else{
         errorFile.add(file);
        }
      }
    } catch (Exception e){
      throw e;
    }
    return fileList;
  }
  
  public String getFileName(File file){
    String name = file.getName();
    name = name.substring(name.indexOf("_")+1);
    return name;
  }
  
  
  /**
   * 得到该文件的文件夹名 兼老数据

   * 
   * @param key
   * @return
   */
  public String getAttFolderName(String key) {
    String folder = "";
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyMM");
    String currDate = format.format(date);

    if (key != null && !"".equals(key)) {

      if (key.indexOf('_') != -1) {
        String[] str = key.split("_");
        for (int i = 0; i < str.length; i++) {
          folder = str[0];
        }
      } else {
        folder = "all";
      }

    }
    return folder;
  }
  
  
  /**
   * 得到附件的Id 兼老数据

   * 
   * @param keyId
   * @return
   */
  public String getAttaId(String keyId) {
    String attaId = "";
    if (keyId != null && !"".equals(keyId)) {
      if (keyId.indexOf('_') != -1) {
        String[] ids = keyId.split("_");
        if (ids.length > 0) {
          attaId = ids[1];
        }

      } else {
        attaId = keyId;
      }
    }
    return attaId;
  }

  /**
   * 批量转存
   * @param dbConn
   * @param loginUserSeqId
   * @param sortId
   * @param signFiles
   * @param filePath
   * @param folderPath
   * @return
   * @throws Exception 
   */
  public boolean transferFolder(Connection dbConn, int loginUserSeqId, int sortId, List<T9SignFile> signFiles,
       String folderPath) throws Exception{ 
    boolean flag = false;
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyMM");
    String currDate = format.format(date);
    T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
    List<T9FileContent> fileContents = new ArrayList<T9FileContent>();
    if(signFiles == null || signFiles.size()==0){
      return false;
    }
    try{
      for(int i=0; i<signFiles.size(); i++){
        T9FileContent fileContent = new T9FileContent();
        String rand;
        rand = emut.getRandom();
       // String attachId = signFiles.get(i).getFileId();
        String attachName = signFiles.get(i).getFileName();
        attachName = attachName.substring(attachName.indexOf("_")+1);
        String newAttaName = rand + "_" + attachName;
        File file = new File(signFiles.get(i).getFilePath());
        //File oldFile = new File(fileFolder.trim() + "/" + oldFileName);
        String newPath = folderPath + File.separator + currDate + File.separator + newAttaName; //拷贝到的目录里
        if (file.exists()) {
          T9FileUtility.copyFile(file.getAbsolutePath(), newPath);
          fileContent.setSortId(sortId);
          fileContent.setSubject(attachName.substring(0, attachName.indexOf(".")));
          fileContent.setSendTime(T9Utility.parseTimeStamp());
          fileContent.setAttachmentId(currDate + "_" + rand + ",");
          fileContent.setAttachmentName(attachName + "*");
          fileContent.setUserId(String.valueOf(loginUserSeqId));
          fileContents.add(fileContent);
        }
      }
      insertFile2DB(dbConn, fileContents, String.valueOf(loginUserSeqId));
      flag = true;
    } catch (Exception e){
      throw e;
    }
    return flag;
  }
 
  /**
   * 批量处理，批量保存文件到数据库
   * @param dbConn
   * @param fileContents
   * @throws Exception
   */
  public void insertFile2DB(Connection dbConn, List<T9FileContent> fileContents, String userId) throws Exception{
    PreparedStatement ps = null;
    String sql = "insert into FILE_CONTENT(SORT_ID,SUBJECT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,USER_ID ) values(?,?,?,?,?,?)";
    try{
      dbConn.setAutoCommit(false);
      ps = dbConn.prepareStatement(sql);
      for(int i=0; i<fileContents.size(); i++){
        ps.setInt(1, fileContents.get(i).getSortId());
        ps.setString(2, fileContents.get(i).getSubject());
        ps.setDate(3, new java.sql.Date(fileContents.get(i).getSendTime().getTime()));
        ps.setString(4, fileContents.get(i).getAttachmentId());
        ps.setString(5, fileContents.get(i).getAttachmentName());
        ps.setString(6, userId);
        ps.addBatch();
      }
      ps.executeBatch();
      dbConn.commit();
    } catch (SQLException e){
      dbConn.rollback();   
      throw e;
    }finally{
      dbConn.close();
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 返回登陆用户所能下载的文档列表
   * @param dbConn
   * @param loginUser
   * @param fType 1：文本文件   2:图片文件, 3:视频文件
   * @return
   * @throws Exception
   */
  public List<T9SignFile> getMySignFileList(Connection dbConn, T9Person loginUser, int fType)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql ="select SEQ_ID, FILE_ID, FILE_PATH, FILE_SIZE, MODULE_NO from sign_files where file_type=" + fType +" order by CREATE_TIME desc ";
    
    String sql1 = "select seq_id, FILE_ID, FILE_PATH,FILE_SIZE, CREATE_TIME, MODULE_NO from sign_files where seq_id in (";
           sql1 += "   select sf.SEQ_ID, sf.FILE_ID, sf.FILE_PATH,sf.FILE_SIZE, sf.CREATE_TIME, sf.MODULE_NO  from file_priv_user fpu , sign_files sf ";
           sql1 += "   where sf.file_type = "+ fType +" and fpu.user_id = "+ loginUser.getSeqId()+"  and fpu.is_load = 1 and fpu.file_seq_id = sf.seq_id ";
           sql1 +=   " UNION ";
           sql1 += "   select sf.SEQ_ID, sf.FILE_ID, sf.FILE_PATH,sf.FILE_SIZE, sf.CREATE_TIME, sf.MODULE_NO  from file_priv_role fpr , sign_files sf ";
           sql1 += "   where sf.file_type = "+ fType +" and fpr.role_id = "+ loginUser.getPostPriv()+" and fpr.is_load = 1  and fpr.file_seq_id = sf.seq_id ";
           sql1 +=   " UNION ";
           sql1 += "   select sf.SEQ_ID, sf.FILE_ID, sf.FILE_PATH,sf.FILE_SIZE, sf.CREATE_TIME, sf.MODULE_NO  from file_priv_dept fpd , sign_files sf  ";
           sql1 += "   where sf.file_type = "+ fType +" and fpd.dept_id = "+ loginUser.getDeptId()+" and fpd.is_load = 1  and fpd.file_seq_id = sf.seq_id ";
           sql1 += ")  order by CREATE_TIME desc ";
           
    List<T9SignFile> fileList = new ArrayList<T9SignFile>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      int cnt = 0;
      while(rs.next() && ++ cnt <= 1000){
        T9SignFile file = new T9SignFile();
        file.setSeqId(rs.getInt("SEQ_ID"));
        file.setFileId(rs.getString("FILE_ID"));
        file.setFilePath(rs.getString("FILE_PATH"));
        file.setFile_Sizes(rs.getLong("FILE_SIZE"));
        file.setModuleNo(rs.getString("MODULE_NO"));
        File extFile = new File(file.getFilePath());
        if(extFile.exists()){
          file.setFileName(extFile.getName());
          fileList.add(file);
        }else{
         errorFile.add(file);
        }
      }
    } catch (Exception e){
      throw e;
    }
    return fileList;
  }
  
  /**
   * 批量转存入口
   * @param dbConn
   * @param loginUser 登陆用户id
   * @param sortId    文件id
   * @param fType     文件类型id
   * @param filePath  
   * @param folderPath
   * @return
   * @throws Exception
   */
  public boolean transferFolder(Connection dbConn,T9Person loginUser, int sortId, int fType,String folderPath) throws Exception{
    List<T9SignFile> files  = getMySignFileList(dbConn, loginUser, fType);
    boolean isSave = transferFolder(dbConn, loginUser.getSeqId(), sortId, files,  folderPath);
    return isSave;
  }
  
  
  /**
   * 转存文件到网络硬盘(copy自文件到网络硬盘)
   * @param dbConn
   * @param diskPath
   * @param attachId
   * @param attachName
   * @param filePath
   * @throws Exception
   */
  public String transferNetdisk(Connection dbConn, String diskPath, String attachId, String attachName, String subject, String filePath)
      throws Exception {
    boolean flag = false;
    boolean exist = false;
    String dirName = "";
    String existName = "";

    String fileDir = filePath + File.separator + this.getAttFolderName(attachId); // D:\project\t9\attach\file_folder\\1007
    String fileName = this.getOldAttaId(attachId) + "_" + attachName.trim();      // a4af7859d19ce575b052dda815fa1d3f_新建文档sd^.ppt
    String oldFileName = this.getOldAttaId(attachId) + "." + attachName.trim();   // a4af7859d19ce575b052dda815fa1d3f.新建文档sd^.ppt
    File file2 = new File(diskPath);                                              // d:\bjfaoitc
    dirName = file2.getName();
    File oldFromFile = new File(fileDir.trim() + "/" + oldFileName.trim());
    File fromFile = new File(fileDir.trim() + "/" + fileName.trim());
    if (fromFile.exists()) {
      File diskFile = new File(file2.getAbsoluteFile() + "/" + attachName.trim());
      String newNameStr = "";
      String newFileName = "";
      String type = attachName.substring(attachName.lastIndexOf(".")); // .doc
      if (!"".equals(subject.trim()) && subject != null) { // 新建文档sd^
        newNameStr = subject;
      } else {
        newNameStr = attachName.substring(0, attachName.lastIndexOf(".")); // 用文件的名字
      }

      newFileName = newNameStr + type;

      if (!diskFile.exists()) {
        T9FileUtility.copyFile(fromFile.getAbsolutePath(), file2.getAbsolutePath() + "/" + newFileName);
        flag = true;
      } else {
        existName = attachName.trim();
        exist = true;
      }

    } else if (oldFromFile.exists()) {

      File diskFile = new File(file2.getAbsoluteFile() + "/" + attachName.trim());
      String newNameStr = "";
      String newFileName = "";
      String type = attachName.substring(attachName.lastIndexOf(".")); // .doc
      if (!"".equals(subject.trim()) && subject != null) { // 新建文档sd^
        newNameStr = subject;
      } else {
        newNameStr = attachName.substring(0, attachName.lastIndexOf(".")); // 用文件的名字
      }

      newFileName = newNameStr + type;

      if (!diskFile.exists()) {
        T9FileUtility.copyFile(fromFile.getAbsolutePath(), file2.getAbsolutePath() + "/" + newFileName);
        flag = true;
      } else {
        existName = attachName.trim();
        exist = true;
      }
    }
    String data = "{flag:\"" + flag + "\",exist:\"" + exist + "\",dirName:\"" + dirName + "\",existName:\"" + existName + "\"}";
    return data;
  }

  /**
   * 得到附件的Id 兼老数据

   * 
   * 
   * @param keyId
   * @return
   */
  public String getOldAttaId(String keyId) {
    String attaId = "";
    if (keyId != null && !"".equals(keyId)) {
      if (keyId.indexOf('_') != -1) {
        String[] ids = keyId.split("_");
        if (ids.length > 0) {
          attaId = ids[1];
        }

      } else {
        attaId = keyId;
      }
    }
    return attaId;
  }
  
/**
 * 网络硬盘批量转存
 * @param dbConn
 * @param diskPath
 * @param signFiles
 * @return
 * @throws Exception
 */
  public String   transferNetdisk(Connection dbConn, String diskPath, List<T9SignFile> signFiles) throws Exception{
    String dirName = "";
    int suceSave = 0;      //成功转存多少个
    int failSave = 0;      //失败转存多少个（文件已存在）
    
    File dir = new File(diskPath);//获得存储的目录
    if(dir.isDirectory()){
      dirName = dir.getName();
    }
    String data = "{'ok':'"+ suceSave +"', 'fail':'"+ failSave +"','fcount':'0','dirName':'"+ T9Utility.encodeSpecial(dirName) +"'}";
    if(signFiles == null || signFiles.size()==0){
      return data;
    }
    try{
      for(int i=0; i<signFiles.size(); i++){
        File sourceFile = new File(signFiles.get(i).getFilePath());//源文件，这里源文件一定存在
        String fName = sourceFile.getName();
        fName = fName.substring(fName.indexOf("_")+1);
        File desFile  = new File(dir.getAbsolutePath() + File.separator + fName);
        if(!desFile.exists()){
          T9FileUtility.copyFile(sourceFile.getAbsolutePath(), dir.getAbsolutePath() + File.separator + fName);
          suceSave ++ ;
        }else{
          failSave ++;
        }
      }
      data = "{'ok':'"+ suceSave +"', 'fail':'"+ failSave +"','fcount':'"+ signFiles.size() +"','dirName':'"+ T9Utility.encodeSpecial(dirName) +"'}";
    } catch (Exception e){
      throw e;
    }
    return data;
  }
  
  public String   transferNetdisk(Connection dbConn, String diskPath,T9Person loginUser,int fType) throws Exception{
    List<T9SignFile> signFiles = getMySignFileList(dbConn, loginUser, fType);
    return  transferNetdisk(dbConn, diskPath, signFiles);
  }
}
