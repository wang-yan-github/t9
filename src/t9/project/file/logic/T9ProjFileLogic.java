package t9.project.file.logic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.filefolder.data.T9FileSort;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9LogConst;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.project.file.data.T9ProjFile;
import t9.project.file.data.T9ProjFileSort;

public class T9ProjFileLogic {
  public static String COPYPATH = File.separator + "core" + File.separator
      + "funcs" + File.separator + "filefolder" + File.separator + "fileUtil";

  /**
   * 显示翻页
   * 
   * @param con
   * @param paraMap
   * @param request
   * @return
   * @throws Exception
   */
  public String getPages(Connection con, HttpServletRequest request)
      throws Exception {
    String sql = "select proj_file.seq_id,subject,user_name,attachment_id,attachment_name,update_time from proj_file,person where upload_user=person.seq_id ";
    try {
      sql += " and sort_id = " + request.getParameter("sortId")
          + " order by update_time desc";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request
          .getParameterMap());
      T9PageDataList dataList = T9PageLoader.loadPageList(con, queryParam, sql);
      return dataList.toJson();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * 通过id查找对象
   * 
   * @author zq 2013-3-25
   * @param con
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9ProjFile getFileInfoById(Connection con, int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
    return (T9ProjFile) t9orm.loadObjSingle(con, T9ProjFile.class, seqId);

  }

  /**
   * 更新
   * 
   * @author zq 2013-3-25
   * @param con
   * @param file
   * @throws Exception
   */
  public void updatefile(Connection con, T9ProjFile file) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.updateSingle(con, file);
  }

  /**
   * 保存
   * 
   * @author zq 2013-3-25
   * @param con
   * @param file
   * @throws Exception
   */
  public void saveSingleObj(Connection con, T9ProjFile file) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.saveSingle(con, file);
  }

  /**
   * 新建文件
   * 
   * @author zq 2013-3-25
   * @param fileType
   * @param fileName
   * @param webrootPath
   * @return
   * @throws Exception
   */
  public String createFile(String fileType, String fileName, String webrootPath)
      throws Exception {
    SimpleDateFormat format = new SimpleDateFormat("yyMM");
    String currDate = format.format(new Date());
    String separator = File.separator;
    String filePath = T9SysProps.getAttachPath() + separator + "proj_file"
        + separator + currDate + separator;

    T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
    String rand = emut.getRandom();
    String newFileName = rand + "_" + fileName + "." + fileType;
    String tmp = filePath + newFileName;

    String type = fileType.trim();
    if ("xls".equals(type)) {
      String srcFile = webrootPath + this.COPYPATH + File.separator
          + "copy.xls";
      T9FileUtility.copyFile(srcFile, tmp);
    } else if ("ppt".equals(type)) {
      String srcFile = webrootPath + this.COPYPATH + File.separator
          + "copy.ppt";
      T9FileUtility.copyFile(srcFile, tmp);
    } else if ("doc".equals(type)) {
      String srcFile = webrootPath + this.COPYPATH + File.separator
          + "copy.doc";
      T9FileUtility.copyFile(srcFile, tmp);
    } else {
      File file = new File(filePath);
      if (!file.exists()) {
        file.mkdirs();
      }
      String createPath = file.getPath().replace("\\", "/");
      File createFile = new File(createPath + "/" + newFileName);
      createFile.createNewFile();
    }

    return rand;

  }

  /**
   * 获取最大的SeqId值
   * 
   * 
   * 
   * @param dbConn
   * @return
   */
  public T9ProjFile getMaxSeqId(Connection dbConn) {
    String sql = "select SEQ_ID from proj_file where SEQ_ID=(select MAX(SEQ_ID) from proj_file ) ";
    T9ProjFile content = null;
    int seqId = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        content = new T9ProjFile();
        seqId = rs.getInt("SEQ_ID");
        content.setSeqId(seqId);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return content;
  }

  /**
   * 根据人员id字符串得到name字符串
   * 
   * 
   * 
   * @param dbConn
   * @param ids
   * @return
   * @throws Exception
   */
  public String getPersonNamesByIds(Connection conn, String ids)
      throws Exception {
    String names = "";
    if (ids != null && !"".equals(ids.trim())) {
      if (ids.endsWith(",")) {
        ids = ids.substring(0, ids.length() - 1);
      }
      String query = "select USER_NAME from PERSON where SEQ_ID in (" + ids
          + ")";
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
        while (rs.next()) {
          names += rs.getString("USER_NAME") + ",";
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
    }
    if (names.endsWith(",")) {
      names = names.substring(0, names.length() - 1);
    }
    return names;
  }

  /**
   * 复制文件操作
   * 
   * @param dbConn
   * @param seqIdStrs
   * @throws Exception
   * @throws NumberFormatException
   */
  public void copyFile(Connection dbConn, String seqIdStrs, String sortId,
      String filePath) throws NumberFormatException, Exception {
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyMM");
    String currDate = format.format(date);
    T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();

    T9ORM orm = new T9ORM();

    String[] seqIdStr = seqIdStrs.split(",");
    if (!"".equals(seqIdStrs) && seqIdStrs.split(",").length > 0) {
      // 遍历要选择附件id串
      for (String seqId : seqIdStr) {
        String randFlag = "";
        String newAttName = "";
        boolean isHave = false;
        T9ProjFile projfile = this.getFileInfoById(dbConn, Integer
            .parseInt(seqId));
        if (projfile != null) {
          String subject = T9Utility.null2Empty(projfile.getSubject());
          boolean haveFile = this.isExistFile(dbConn, Integer.parseInt(sortId),
              subject);
          if (haveFile) {
            StringBuffer buffer = new StringBuffer();
            this.copyExistFile(dbConn, buffer, Integer.parseInt(sortId),
                subject);
            String newSubject = buffer.toString().trim();
            projfile.setSubject(newSubject);
          }

          String attachmentId = T9Utility
              .null2Empty(projfile.getAttachmentId());
          String attachmentName = T9Utility.null2Empty(projfile
              .getAttachmentName());
          String[] attIdArray = {};
          String[] attNameArray = {};
          if (attachmentId != null && attachmentName != null) {
            attIdArray = attachmentId.split(",");
            attNameArray = attachmentName.split("\\*");
          }
          for (int i = 0; i < attIdArray.length; i++) {
            Map<String, String> map = this.getFileName(attIdArray[i],
                attNameArray[i]);
            // 遍历Set集合
            if (map.size() != 0) {
              Set<String> set = map.keySet();
              for (String keySet : set) {
                String rand = emut.getRandom();
                String key = keySet;
                String keyValue = map.get(keySet);
                String attaIdStr = this.getAttaId(keySet);
                String newAttaName = rand + "_" + keyValue;
                String fileNameValue = attaIdStr + "_" + keyValue;
                String fileFolder = this.getFilePathFolder(key);

                File file = new File(filePath + File.separator + fileFolder
                    + File.separator + fileNameValue);
                if (file != null && file.exists()) {
                  T9FileUtility.copyFile(filePath + File.separator + fileFolder
                      + File.separator + fileNameValue, filePath
                      + File.separator + currDate + File.separator
                      + newAttaName);
                  randFlag += currDate + "_" + rand + ",";
                  newAttName += keyValue + "*";
                  isHave = true;
                  break;
                }
              }
            }
          }
          if (isHave) {
            // 保存到数据库
            // fileContent.setUserId(String.valueOf(loginUserSeqId));
            // fileContent.setCreater(String.valueOf(loginUserSeqId));
            projfile.setSortId(Integer.parseInt(sortId));
            projfile.setUpdateTime(T9Utility.parseTimeStamp());
            projfile.setAttachmentId(randFlag);
            projfile.setAttachmentName(newAttName.trim());
            orm.saveSingle(dbConn, projfile);
          } else {
            projfile.setSortId(Integer.parseInt(sortId));
            projfile.setUpdateTime(T9Utility.parseTimeStamp());
            orm.saveSingle(dbConn, projfile);
          }

        }
      }
    }
  }

  /**
   * 判断库是否已有文件
   * 
   * 
   * @param dbConn
   * @param sortId
   * @param subject
   * @return
   * @throws Exception
   */
  public boolean isExistFile(Connection dbConn, int sortId, String subject)
      throws Exception {
    boolean flag = false;
    int counter = 0;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select count(SEQ_ID) from proj_file where SORT_ID = ? and SUBJECT=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, sortId);
      stmt.setString(2, subject);
      rs = stmt.executeQuery();
      if (rs.next()) {
        counter = rs.getInt(1);
      }
      if (counter > 0) {
        flag = true;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return flag;
  }

  /**
   * 文件夹里文件已存在的处理方法
   * 
   * @param dbConn
   * @param buffer
   * @param sortId
   * @param subject
   * @throws Exception
   */
  public void copyExistFile(Connection dbConn, StringBuffer buffer, int sortId,
      String subject) throws Exception {
    try {
      String temp = subject + " - 复件";
      String subjectSuffix = temp;
      int repeat = 1;
      while (this.isExistFile(dbConn, sortId, subjectSuffix)) {
        repeat++;
        subjectSuffix = temp + "(" + repeat + ")";
      }
      buffer.append(subjectSuffix);
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 拼接附件Id与附件名
   * 
   * @param attachmentId
   * @param attachmentName
   * @return
   */
  public Map<String, String> getFileName(String attachmentId,
      String attachmentName) {
    Map<String, String> map = new HashMap<String, String>();
    if (attachmentId == null || attachmentName == null) {
      return map;
    }
    if (!"".equals(attachmentId.trim()) && !"".equals(attachmentName.trim())) {

      String attachmentIds[] = attachmentId.split(",");
      String attachmentNames[] = attachmentName.split("\\*");
      if (attachmentIds.length != 0 && attachmentNames.length != 0) {
        for (int i = 0; i < attachmentIds.length; i++) {
          map.put(attachmentIds[i], attachmentNames[i]);
        }

      }
    }
    return map;
  }

  /**
   * 得到附件的Id
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
   * 得到该文件的文件夹名
   * 
   * @param key
   * @return
   */
  public String getFilePathFolder(String key) {
    String folder = "";

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
   * 剪切文件操作
   * 
   * @param dbConn
   * @param seqIdStrs
   * @param sortId
   * @param filePath
   * @throws NumberFormatException
   * @throws Exception
   */
  public void cutFile(Connection dbConn, String seqIdStrs, String sortId,
      String filePath) throws NumberFormatException, Exception {
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyMM");
    String currDate = format.format(date);
    T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();

    String randFlag = "";
    String newAttName = "";
    boolean isHave = false;

    T9ORM orm = new T9ORM();
    String[] seqIdStr = seqIdStrs.split(",");
    if (!T9Utility.isNullorEmpty(seqIdStrs) && seqIdStrs.split(",").length > 0) {
      // 遍历选择的附件Id串
      for (String seqId : seqIdStr) {
        T9ProjFile fileContent = this.getFileInfoById(dbConn, Integer
            .parseInt(seqId));
        if (fileContent != null) {
          String subject = T9Utility.null2Empty(fileContent.getSubject());
          boolean haveFile = this.isExistFile(dbConn, Integer.parseInt(sortId),
              subject);
          if (haveFile) {
            StringBuffer buffer = new StringBuffer();
            this.copyExistFile(dbConn, buffer, Integer.parseInt(sortId),
                subject);
            String newSubject = buffer.toString().trim();
            fileContent.setSubject(newSubject);
          }
          String attachmentId = T9Utility.null2Empty(fileContent
              .getAttachmentId());
          String attachmentName = T9Utility.null2Empty(fileContent
              .getAttachmentName());
          T9ProjFile delContent = new T9ProjFile();
          String[] attIdArray = {};
          String[] attNameArray = {};
          if (!T9Utility.isNullorEmpty(attachmentId)
              && !T9Utility.isNullorEmpty(attachmentName)) {
            attIdArray = attachmentId.split(",");
            attNameArray = attachmentName.split("\\*");
          }
          for (int i = 0; i < attIdArray.length; i++) {
            Map<String, String> map = this.getFileName(attIdArray[i],
                attNameArray[i]);
            if (map.size() != 0) {
              Set<String> set = map.keySet();
              // 遍历Set集合
              for (String keySet : set) {
                String rand = emut.getRandom();
                String key = keySet;
                String keyValue = map.get(keySet);
                String attaIdStr = this.getAttaId(keySet);
                String fileNameValue = attaIdStr + "_" + keyValue;
                String newAttaName = rand + "_" + keyValue;
                String fileFolder = this.getFilePathFolder(key);

                File file = new File(filePath + File.separator + fileFolder
                    + File.separator + fileNameValue);
                if (file != null && file.exists()) {
                  T9FileUtility.xcopyFile(filePath + File.separator
                      + fileFolder + File.separator + fileNameValue, filePath
                      + File.separator + currDate + File.separator
                      + newAttaName);
                  randFlag += currDate + "_" + rand + ",";
                  newAttName += keyValue + "*";
                  isHave = true;
                  break;
                }
              }
            }
          }
          if (isHave) {
            delContent.setSeqId(fileContent.getSeqId());
            // 删除旧信息

            orm.deleteSingle(dbConn, delContent);
            // 插入新信息

            fileContent.setSortId(Integer.parseInt(sortId));
            fileContent.setUpdateTime(T9Utility.parseTimeStamp());
            fileContent.setAttachmentId(randFlag.trim());
            fileContent.setAttachmentName(newAttName.trim());
            orm.saveSingle(dbConn, fileContent);
          } else {
            delContent.setSeqId(fileContent.getSeqId());
            orm.deleteSingle(dbConn, delContent);
            fileContent.setSortId(Integer.parseInt(sortId));
            fileContent.setUpdateTime(T9Utility.parseTimeStamp());
            orm.saveSingle(dbConn, fileContent);
          }

        }

      }
    }
  }

  /**
   * 判断文件名是否已存在
   * 
   * @param dbConn
   * @param subjectName
   * @return
   * @throws Exception
   */
  public String checkSubjectName(Connection dbConn, int seqId,
      int subContentId, String subjectName) throws Exception {
    String data = "";
    boolean isHave = false;
    int isHaveFlag = 0;

    Map map = new HashMap();
    map.put("SORT_ID", seqId);
    try {
      List<T9ProjFile> contentList = this.getFileContentsInfo(dbConn, map);
      if (subContentId != 0) {
        if (contentList != null && contentList.size() > 0) {
          for (T9ProjFile content : contentList) {
            String subject = T9Utility.null2Empty(content.getSubject());
            int contentId = content.getSeqId();
            if (subContentId != contentId) {
              if (subjectName.trim().equals(subject.trim())) {
                isHave = true;
                break;
              }
            }
          }
        }
      } else {
        if (contentList != null && contentList.size() > 0) {
          for (T9ProjFile content : contentList) {
            String subject = T9Utility.null2Empty(content.getSubject());
            if (subjectName.trim().equals(subject.trim())) {
              isHave = true;
              break;
            }
          }
        }
      }

      if (isHave) {
        isHaveFlag = 1;
      }
      data = "{isHaveFlag:\"" + isHaveFlag + "\" }";

    } catch (Exception e) {
      throw e;
    }
    return data;
  }

  public List<T9ProjFile> getFileContentsInfo(Connection dbConn, Map map)
      throws Exception {
    T9ORM orm = new T9ORM();
    return orm.loadListSingle(dbConn, T9ProjFile.class, map);

  }

  /**
   * 浮动菜单文件删除
   * 
   * @param dbConn
   * @param attId
   * @param attName
   * @param contentId
   * @throws Exception
   */
  public boolean delFloatFile(Connection dbConn, String attId, String attName,
      int contentId) throws Exception {
    boolean updateFlag = false;
    T9ProjFile fileContent = this.getFileContentInfoById(dbConn, contentId);
    String[] attIdArray = {};
    String[] attNameArray = {};
    String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
    String attachmentName = T9Utility.null2Empty(fileContent
        .getAttachmentName());
    if (!"".equals(attachmentId.trim()) && attachmentId != null
        && attachmentName != null) {
      attIdArray = attachmentId.trim().split(",");
      attNameArray = attachmentName.trim().split("\\*");
    }
    String attaId = "";
    String attaName = "";
    for (int i = 0; i < attIdArray.length; i++) {
      if (attId.equals(attIdArray[i])) {
        continue;
      }
      attaId += attIdArray[i] + ",";
      attaName += attNameArray[i] + "*";
    }

    fileContent.setAttachmentId(attaId.trim());
    fileContent.setAttachmentName(attaName.trim());
    this.updataFileInfoByObj(dbConn, fileContent);
    updateFlag = true;
    return updateFlag;

  }

  public void updataFileInfoByObj(Connection dbConn, T9ProjFile content)
      throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, content);
  }

  public T9ProjFile getFileContentInfoById(Connection dbConn, int contentId)
      throws Exception {
    T9ORM orm = new T9ORM();
    return (T9ProjFile) orm.loadObjSingle(dbConn, T9ProjFile.class, contentId);
  }

  public void updateSingleObj(Connection dbConn, T9ProjFile fileContent)
      throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, fileContent);
  }
  

  /**
   * 批量上传文件
   */
  public String uploadFileLogic(Connection dbConn, T9FileContent content,
      T9FileUploadForm fileForm, T9Person loginUser, String seqId,
      String remoteAddr, String filePath) throws Exception {
    T9ORM orm = new T9ORM();
    // 获取登录用户信息
    int loginUserSeqId = loginUser.getSeqId();
    int loginUserDeptId = loginUser.getDeptId();
    String loginUserRoleId = loginUser.getUserPriv();

    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyMM");
    String currDate = format.format(date);
    int sortId = 0;
    if (seqId != null) {
      sortId = Integer.parseInt(seqId);
    }
    String smsPerson = fileForm.getParameter("smsPerson");
    if (smsPerson == null) {
      smsPerson = "";
    }
    String mobileSmsPerson = fileForm.getParameter("mobileSmsPerson");
    if (mobileSmsPerson == null) {
      mobileSmsPerson = "";
    }
    String folderPath = fileForm.getParameter("folderPath");
    if (folderPath == null) {
      folderPath = "";
    }
    String subjectStr = "";
    try {
      Iterator<String> keysIte = fileForm.iterateFileFields();
      while (keysIte.hasNext()) {
        String fieldName = keysIte.next();
        String fileName = fileForm.getFileName(fieldName);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String nameTitle = "";
        String newSubject = "";
        if (fileName.lastIndexOf(".") != -1) {
          nameTitle = fileName.substring(0, fileName.lastIndexOf("."));
        }
        boolean haveFile = this.isExistFile(dbConn, sortId, nameTitle);
        if (haveFile) {
          StringBuffer buffer = new StringBuffer();
          this.copyExistFile(dbConn, buffer, sortId, nameTitle);
          newSubject = buffer.toString().trim();
        } else {
          newSubject = nameTitle;
        }

        T9InnerEMailUtilLogic emut = new T9InnerEMailUtilLogic();
        content.setSortId(sortId);
        content.setSendTime(T9Utility.parseTimeStamp());
        content.setAttachmentName(fileName.trim() + "*");
        // String[] fName = fileName.split("\\.");
        content.setSubject(newSubject);
        subjectStr = newSubject; // J01786看舒

        String rand = emut.getRandom();
        fileName = rand + "_" + fileName;
        fileForm.saveFile(fieldName, filePath + File.separator + fileName);

        content.setAttachmentId(currDate + "_" + String.valueOf(rand) + ",");
        content.setCreater(String.valueOf(loginUserSeqId));
      }
      orm.saveSingle(dbConn, content);
      T9ProjFile maxContent = this.getMaxSeqId(dbConn);
      int contentId = maxContent.getSeqId();
      // 系统日志
      String remark = "新建文件,名称:" + subjectStr;
      T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark,
          loginUserSeqId, remoteAddr);

      // 短信提醒
      // T9SmsUtil sms=new T9SmsUtil();
      T9SmsBack sms = new T9SmsBack();
      String loginName = this.getPersonNamesByIds(dbConn, String
          .valueOf(loginUserSeqId));
      String smsContent = loginName + " 在公共文件柜" + folderPath + " 下建立新文件:"
          + subjectStr;
      String remindUrl = "/core/funcs/filefolder/read.jsp?sortId=" + sortId
          + "&contentId=" + contentId + "&newFileFlag=1&openFlag=1";
      if ("allPrivPerson".equals(smsPerson)) {
        T9FileSortLogic logic = new T9FileSortLogic();
        T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String
            .valueOf(sortId));
        String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
        String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
        String personIdStr = logic
            .selectManagerIds(dbConn, fileSort, "USER_ID");

        if (!"".equals(personIdStr)) {
          personIdStr += ",";
        }
        // 判断这个部门是否有权限
        // String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn,
        // loginUserDeptId, deptIdStr);
        // String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn,
        // Integer.parseInt(loginUserRoleId), roleIdStr);
        // // 如有权限，获取该部门下的所有人员id串
        // String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId,
        // deptPrivIdStrs, dbConn);
        // String rolePersonIdStr =
        // logic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId),
        // rolePrivIdStrs, dbConn);

        String deptPersonIdStr = "";
        String rolePersonIdStr = "";
        if (!T9Utility.isNullorEmpty(deptIdStr)) {
          deptPersonIdStr = logic.getDeptPersonIds(deptIdStr, dbConn);
        }
        if (!T9Utility.isNullorEmpty(roleIdStr)) {
          rolePersonIdStr = logic.getRolePersonIds(roleIdStr, dbConn);
        }

        String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
        String allpersonStr = "";
        ArrayList al = new ArrayList();
        String[] arr = allPersonIdStr.split(",");
        for (int i = 0; i < arr.length; i++) {
          if (al.contains(arr[i]) == false) {
            al.add(arr[i]);
            allpersonStr += arr[i] + ",";
          }
        }
        if (allpersonStr != null && !"".equals(allpersonStr)) {
          sms.setFromId(loginUserSeqId);
          sms.setToId(allpersonStr.trim());
          sms.setContent(smsContent);
          sms.setSendDate(T9Utility.parseTimeStamp());
          sms.setSmsType(T9LogConst.FILE_FOLDER);
          sms.setRemindUrl(remindUrl);
          T9SmsUtil.smsBack(dbConn, sms);
        }

      } else if (!"".equals(smsPerson)) {
        sms.setFromId(loginUserSeqId);
        sms.setToId(smsPerson);
        sms.setContent(smsContent);
        sms.setSendDate(T9Utility.parseTimeStamp());
        sms.setSmsType(T9LogConst.FILE_FOLDER);
        sms.setRemindUrl(remindUrl);
        T9SmsUtil.smsBack(dbConn, sms);
      }
      // 手机短信提醒
      String mobileSmsContent = loginName + " 在公共文件柜 " + folderPath
          + " 下建立新文件:" + subjectStr;
      T9MobileSms2Logic mobileSms = new T9MobileSms2Logic();
      if ("allPrivPerson".equals(mobileSmsPerson.trim())) {
        T9FileSortLogic logic = new T9FileSortLogic();
        T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String
            .valueOf(sortId));
        String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
        String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
        String personIdStr = logic
            .selectManagerIds(dbConn, fileSort, "USER_ID");
        if (!"".equals(personIdStr)) {
          personIdStr += ",";
        }
        // 判断这个部门是否有权限
        String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId,
            deptIdStr);
        String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer
            .parseInt(loginUserRoleId), roleIdStr);
        // 如有权限，获取该部门下的所有人员id串
        String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId,
            deptPrivIdStrs, dbConn);
        String rolePersonIdStr = logic.getRolePersonIdStr(Integer
            .parseInt(loginUserRoleId), rolePrivIdStrs, dbConn);
        String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
        String allpersonStr = "";
        ArrayList al = new ArrayList();
        String[] arr = allPersonIdStr.split(",");
        for (int i = 0; i < arr.length; i++) {
          if (al.contains(arr[i]) == false) {
            al.add(arr[i]);
            allpersonStr += arr[i] + ",";
          }
        }
        if (allpersonStr != null && !"".equals(allpersonStr)) {
          mobileSms.remindByMobileSms(dbConn, allpersonStr, loginUserSeqId,
              mobileSmsContent, null);
        }
      } else if (!"".equals(mobileSmsPerson.trim())) {
        mobileSms.remindByMobileSms(dbConn, mobileSmsPerson, loginUserSeqId,
            mobileSmsContent, null);
      }
    } catch (Exception e) {
      throw e;
    }
    return null;
  }

  /**
   * 根据seqId串删除文件

   * 
   * 
   * @param dbConn
   * @param seqIdStrs
   * @param filePath
   * @throws NumberFormatException
   * @throws Exception
   */
  public void delFile(Connection dbConn, String seqIdStrs, String filePath, int loginUserSeqId, String ipStr, String recycle, String recyclePath)
          throws NumberFormatException, Exception {
      T9ORM orm = new T9ORM();

      String[] seqIdStr = seqIdStrs.split(",");
      if (!"".equals(seqIdStrs) && seqIdStrs.split(",").length > 0) {
          // 遍历要选择删除的附件id串

          for (String seqId : seqIdStr) {
              T9ProjFile fileContent = this.getFileContentInfoById(dbConn, Integer.parseInt(seqId));
              String attachmentId = T9Utility.null2Empty(fileContent.getAttachmentId());
              String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName());
              String[] attIdArray = {};
              String[] attNameArray = {};
              if (!"".equals(attachmentId) && attachmentId != null && attachmentName != null) {
                  attIdArray = attachmentId.trim().split(",");
                  attNameArray = attachmentName.trim().split("\\*");
              }
              for (int i = 0; i < attIdArray.length; i++) {
                  Map<String, String> map = this.getFileName(attIdArray[i], attNameArray[i]);
                  if (map.size() != 0) {
                      Set<String> set = map.keySet();
                      // 遍历Set集合
                      for (String keySet : set) {
                          String key = keySet;
                          String keyValue = map.get(keySet);
                          String attaIdStr = this.getAttaId(keySet);
                          String fileNameValue = attaIdStr + "_" + keyValue;
                          String fileFolder = this.getFilePathFolder(key);
                          String oldFileNameValue = attaIdStr + "." + keyValue;
                          File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
                          File oldFile = new File(filePath + File.separator + fileFolder + File.separator + oldFileNameValue);

                          if (file.exists()) {
                              if ("1".equals(recycle.trim())) {
                                  T9FileUtility.xcopyFile(file.getAbsolutePath(), recyclePath + File.separator + fileNameValue);
                              } else {
                                  T9FileUtility.deleteAll(file.getAbsoluteFile());
                              }
                          } else if (oldFile.exists()) {
                              if ("1".equals(recycle.trim())) {
                                  T9FileUtility.xcopyFile(oldFile.getAbsolutePath(), recyclePath + File.separator + fileNameValue);
                              } else {
                                  T9FileUtility.deleteAll(oldFile.getAbsoluteFile());
                              }
                          }
                      }
                  }
              }
              // 删除数据库信息
              T9ProjFile delContent = new T9ProjFile();
              delContent.setSeqId(fileContent.getSeqId());
              orm.deleteSingle(dbConn, delContent);

              // 写入系统日志
              String remark = "删除文件,名称:" + fileContent.getSubject();
              T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark, loginUserSeqId, ipStr);

          }
      }
  }
  /**
   * 判断文件名是否已存在
   * 
   * @param dbConn
   * @param subjectName
   * @return
   * @throws Exception
   */
  public String checkEditSubjectName(Connection dbConn, int seqId, int subContentId, String subjectName) throws Exception {
      String data = "";
      boolean isHave = false;
      int isHaveFlag = 0;

      Map map = new HashMap();
      map.put("SORT_ID", seqId);
      try {
          List<T9ProjFile> contentList = this.getFileContentsInfo(dbConn, map);
          if (contentList != null && contentList.size() > 0) {
              for (T9ProjFile content : contentList) {
                  String subject = T9Utility.null2Empty(content.getSubject());
                  int contentId = content.getSeqId();
                  if (subContentId != contentId) {
                      if (subjectName.trim().equals(subject.trim())) {
                          isHave = true;
                          break;
                      }
                  }
              }
          }
          if (isHave) {
              isHaveFlag = 1;
          }
          data = "{isHaveFlag:\"" + isHaveFlag + "\" }";

      } catch (Exception e) {
          throw e;
      }
      return data;
  }
  /**
   * 
   * @param attachmentName
   * @param attachmentId
   * @param module
   * @return
   * @throws Exception
   */
  public HashMap toZipInfoMapFile(Connection dbConn, int sortId, String seqIds, String module, T9Person loginUser)
          throws Exception {
      HashMap result = new HashMap();
//      System.out.println(sortId+","+seqIds+","+module);
      T9ORM orm = new T9ORM();
      if (seqIds == null || "".equals(seqIds.trim())) {
          return result;
      }
      if (seqIds.trim().endsWith(",")) {
          seqIds = seqIds.trim().substring(0, seqIds.trim().length() - 1);
      }
      String[] filters = { "SEQ_ID IN(" + seqIds + ")" };
      ArrayList<T9ProjFile> fileContents = (ArrayList<T9ProjFile>) orm.loadListSingle(dbConn, T9ProjFile.class, filters);
      HashMap<String, Integer> subjectNames = new HashMap<String, Integer>();
      T9NtkoLogic ntkoLogic = new T9NtkoLogic();
      for (int j = 0; j < fileContents.size(); j++) {
        T9ProjFile fileContent = fileContents.get(j);
          String[] attachmentArray = T9Utility.isNullorEmpty(fileContent.getAttachmentName()) ? new String[0] : fileContent.getAttachmentName().split("\\*");
          String[] attachmentIdArray = T9Utility.isNullorEmpty(fileContent.getAttachmentId()) ? new String[0] : fileContent.getAttachmentId().split(",");
          String subject = fileContent.getSubject();
          if (subjectNames.keySet().contains(subject.trim())) {
              int count = subjectNames.get(subject.trim());
              subject = subject + "_" + count;
              subjectNames.put(subject.trim(), count + 1);
          } else {
              subjectNames.put(subject.trim(), 1);
          }
          HashMap<String, Integer> filesName = new HashMap<String, Integer>();

          T9ProjFileSort fileSort = new T9ProjFileSort();

          for (int i = 0; i < attachmentIdArray.length; i++) {
              if ("".equals(attachmentIdArray[i].trim()) || "".equals(attachmentArray[i].trim())) {
                  continue;
              }

              String fileType = T9FileUtility.getFileExtName(attachmentArray[i].trim());
              // 判断是否为office文件
              boolean isOffice = this.isOfficeFile("." + fileType);
              if (isOffice) {
                  continue;
              }

              String attachName = attachmentArray[i].trim();
              String temp = ntkoLogic.getAttachBytes(attachName, attachmentIdArray[i].trim(), module);
              String fileName = "";
              if (temp != null) {
                  String preName = attachName.substring(0, attachName.lastIndexOf("."));
                  if (filesName.keySet().contains(attachName.trim())) {
                      int count = filesName.get(attachName.trim());
                      String extName = attachName.substring(attachName.lastIndexOf("."), attachName.length());
                      fileName = preName + "_" + count + extName;
                      filesName.put(attachName.trim(), count + 1);
                  } else {
                      filesName.put(attachName.trim(), 1);
                      fileName = attachName;
                  }
                  result.put(subject + "/" + "附件" + "/" + fileName, temp); // 附件内容
              }
              result.put(subject + "/" + "附件" + "/", null); // 标题为文件夹下的附件文件夹

          }

          result.put(subject + "/", null); // 以标题为文件夹名，

          String createName = this.getPersonNamesByIds(dbConn, String.valueOf(fileContent.getUploadUser()));

          String html = "<html><head><title>" + subject + "</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8'></head>";
          html += "<style>body{font-size:12px;} table{border:1px #000 solid;border-collapse:collapse;} table td{border:1px #000 solid;}</style>";
          html += "<body><table width='70%' align='center'><tr><td align='center' colspan='2'><b><span class='big'>" + subject
                  + "&nbsp;</span></b></td></tr>";
          html += "<tr><td height='250' valign='top' colspan='2'>" + T9Utility.null2Empty(fileContent.getFileDesc()) + "&nbsp;</td></tr>";
          html += "<tr class=small><td width='100'>创建人：</td><td width='400'>" + createName + "&nbsp;</td></tr></table></body></html>";
          /* FileInputStream htmlIn = new FileInputStream( html.getBytes()); */
          InputStream in = new ByteArrayInputStream(html.getBytes("UTF-8"));
          result.put(subject + "/" + subject + ".html", in); // 生成的hmtl页面
      }

      return result;
  }
  /**
   * 判断是否为office文件
   * 
   * @param fileName
   * @return
   * @throws Exception
   */
  public boolean isOfficeFile(String fileType) throws Exception {
      boolean flag = false;
      try {
          if (fileType != null && !"".equals(fileType.trim())) {
              if (".doc".equals(fileType) || ".xls".equals(fileType) || ".ppt".equals(fileType) || ".pps".equals(fileType) || ".docx".equals(fileType)
                      || ".xlsx".equals(fileType) || ".pptx".equals(fileType) || ".ppsx".equals(fileType) || "wps".equals(fileType) || ".et".equals(fileType)
                      || ".ett".equals(fileType)) {
                  flag = true;
              }
          }
      } catch (Exception e) {
          throw e;
      }
      return flag;
  }
}
