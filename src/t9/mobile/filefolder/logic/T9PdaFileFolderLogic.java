package t9.mobile.filefolder.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.filefolder.data.T9FileSort;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileString;

public class T9PdaFileFolderLogic {
  /**
   * 显示根目录下目录 针对personnal 的情况
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public List<Map<String, String>> getFileFolderList(Connection dbConn, String sql)
      throws Exception {
    try {
      List<Map<String, String>> data = new ArrayList<Map<String, String>>();
      /* StringBuffer data = new StringBuffer(""); */
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          Map<String, String> map = new HashMap<String, String>();
          map.put("q_id", String.valueOf(rs.getInt("SEQ_ID")));
          map.put("file_type", "folder");
          map.put("name", T9Utility.encodeSpecial(rs.getString("SORT_NAME")));
          map.put("file_ext", "");
          map.put("send_time", "一级目录");
          map.put("has_attachment", "");
          data.add(map);

          /*
           * data.append("{\"q_id\":\"" + rs.getInt("SEQ_ID") + "\"," + "\"file_type\":\"folder\","
           * + "\"name\":\"" + T9Utility.encodeSpecial(rs.getString("SORT_NAME")) + "\"," +
           * "\"file_ext\":\"\"," + "\"send_time\":\"" + "一级目录" + "\"," +
           * "\"has_attachment\":\"\"},");
           */
          //flag = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      /*
      if (flag) {
        data.remove(data.size() - 1);
      }
      */
      return data;
    } catch (Exception e) {
      throw e;
    }
  }

  public List<Map<String,String>> getFileFolderList1(Connection dbConn, String sql, String SORT_PARENT) {
    /*StringBuffer data = new StringBuffer("");*/
    List<Map<String,String>> data = new ArrayList<Map<String,String>>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean flag = false;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        
        String SORT_NAME = rs.getString("SORT_NAME");
        String SORT_ID_PERSONAL = rs.getString("SEQ_ID");
        /*data.append("{\"q_id\":\"" + SORT_ID_PERSONAL + "\"," + "\"file_type\":\"" + "folder"
            + "\"," + "\"now_sort\":\"" + SORT_ID_PERSONAL + "\"," + "\"parent_sort\":\""
            + SORT_PARENT + "\"," + "\"name\":\"" + T9Utility.encodeSpecial(SORT_NAME) + "\","
            + "\"file_ext\":\"" + "" + "\"," + "\"send_time\":\"" + "一级目录" + "\","
            + "\"has_attachment\":\"" + "" + "\"},");*/
        
        Map <String,String> map = new HashMap<String, String>();
        map.put("q_id", SORT_ID_PERSONAL);
        map.put("file_type", "folder");
        map.put("now_sort", SORT_ID_PERSONAL);
        map.put("parent_sort", SORT_PARENT);
        map.put("name", T9Utility.encodeSpecial(SORT_NAME));
        map.put("file_ext", "");
        map.put("send_time", "一级目录");
        map.put("has_attachment", "");
        data.add(map);
        flag = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    /*if (flag) {
      data = data.deleteCharAt(data.length() - 1);
    }*/
    return data;

  }

  /**
   * 针对public 的情况
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public String getPublicFileFolderList(Connection dbConn, String sql, T9Person person)
      throws Exception {
    try {
      StringBuffer data = new StringBuffer("[");
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          String OWNER = rs.getString("OWNER");
          String USER_ID = rs.getString("USER_ID");
          String LOGIN_USER_ID = String.valueOf(person.getSeqId());
          if (USER_ID != String.valueOf(person.getSeqId()) && !check_priv(USER_ID, person)
              && !check_priv(OWNER, person))
            continue;
          /**
           * 判断权限 如果$USER_ID!=$LOGIN_USER_ID if($USER_ID!=$LOGIN_USER_ID && !check_priv($USER_ID) &&
           * !check_priv($OWNER)) continue;
           */
          data.append("{\"q_id\":\"" + rs.getInt("SEQ_ID") + "\"," + "\"file_type\":\"" + "folder"
              + "\"," + "\"name\":\"" + rs.getString("SORT_NAME") + "\"," + "\"file_ext\":\"" + ""
              + "\"," + "\"send_time\":\"" + "一级目录" + "\"," + "\"has_attachment\":\"" + "" + "\"},");
          flag = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      if (flag) {
        data = data.deleteCharAt(data.length() - 1);
      }
      data.append("]");
      return data.toString();
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 显示根目录下文件
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public List<Map<String,String>> getFileList(Connection dbConn, String sql) throws Exception {
    try {
      List<Map<String, String>> data = new ArrayList<Map<String, String>>();
      /* StringBuffer data = new StringBuffer(""); */
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          /**
           * 是否有附件
           */
          int has_attachment = 0;

          String attid = null;
          attid = "";
          attid = rs.getString("ATTACHMENT_ID");

          String attName = null;
          attName = "";
          attName = rs.getString("ATTACHMENT_NAME");
          /**
           * 判断您是否有附件 如果有 为1 如果无 为0
           */
          if (attid != null && !"".equals(attid))
            has_attachment = 1;
          else
            has_attachment = 0;
          String ext = fileExt(attName, "\\*");
          /*data.append("{\"q_id\":\"" + rs.getInt("SEQ_ID") + "\"," + "\"file_type\":\"file\","
              + "\"name\":\"" + T9Utility.encodeSpecial(rs.getString("SUBJECT")) + "\","
              + "\"file_ext\":\"" + ext + "\"," + "\"send_time\":\""
              + T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME")) + "\","
              + "\"has_attachment\":\"" + has_attachment + "\"},");*/

          Map<String, String> map = new HashMap<String, String>();
          map.put("q_id", String.valueOf(rs.getInt("SEQ_ID")));
          map.put("file_type", "file");
          map.put("name", T9Utility.encodeSpecial(rs.getString("SUBJECT")));
          map.put("file_ext", ext);
          map.put("send_time", T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME")));
          map.put("has_attachment", String.valueOf(has_attachment));
          data.add(map);
          flag = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      /*
      if (flag) {
        data = data.deleteCharAt(data.length() - 1);
        data.remove(data.size() - 1);
      }
      */
      return data;
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 公共文件柜： 显示文件
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public String getFileContentList(Connection dbConn, String sql, String SORT_ID_PUB,
      String SORT_PARENT_PUBLIC) throws Exception {
    StringBuffer data = new StringBuffer("");
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean flag = false;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String READERS_PUBLIC = rs.getString("READERS");
        String CONTENT_ID_PUBLIC = rs.getString("SEQ_ID");
        String SUBJECT_PUBLIC = rs.getString("SUBJECT");
        String SEND_TIME_PUBLIC = T9Utility.getDateTimeStrCn("SEND_TIME");;
        String ATTACHMENT_ID_PUBLIC = rs.getString("ATTACHMENT_ID");
        String ATTACHMENT_NAME_PUBLIC = rs.getString("ATTACHMENT_NAME");

        String path_info_public = fileExt(ATTACHMENT_NAME_PUBLIC, "\\*");

        int has_attachment_public = 0;

        if (!T9MobileString.isEmpty(ATTACHMENT_NAME_PUBLIC)
            && !T9MobileString.isEmpty(ATTACHMENT_ID_PUBLIC))
          has_attachment_public = 1;
        else
          has_attachment_public = 0;
        String file_ext = fileExt(ATTACHMENT_NAME_PUBLIC, "\\*");
        data.append("{\"q_id\":\"" + CONTENT_ID_PUBLIC + "\"," + "\"file_type\":\"" + "file"
            + "\"," + "\"now_sort\":\"" + SORT_ID_PUB + "\"," + "\"parent_sort\":\""
            + SORT_PARENT_PUBLIC + "\"," + "\"name\":\"" + SUBJECT_PUBLIC + "\","
            + "\"file_ext\":\"" + file_ext + "\"," + "\"send_time\":\"" + SEND_TIME_PUBLIC + "\","
            + "\"has_attachment\":\"" + has_attachment_public + "\"},");
        flag = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    if (flag) {
      data = data.deleteCharAt(data.length() - 1);
    }
    return data.toString();
  }

  /**
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public List<Map<String,String>> getFileContentList1(Connection dbConn, String sql, String SORT_PARENT) {
    List <Map<String,String>> data = new ArrayList<Map<String,String>>();
    /*StringBuffer data = new StringBuffer("");*/
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean flag = false;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String CONTENT_ID = rs.getString("SEQ_ID");
        String READERS = rs.getString("READERS");
        String SUBJECT = rs.getString("SUBJECT");
        String SEND_TIME =
            T9Utility.getDateTimeStrCn(new Date(rs.getTimestamp("SEND_TIME").getTime()));
        String ATTACHMENT_ID_PUBLIC = rs.getString("ATTACHMENT_ID");
        String ATTACHMENT_NAME_PUBLIC = rs.getString("ATTACHMENT_NAME");

        String path_info_public = fileExt(ATTACHMENT_NAME_PUBLIC, "\\*");

        int has_attachment = 0;

        if (!T9MobileString.isEmpty(ATTACHMENT_NAME_PUBLIC)
            && !T9MobileString.isEmpty(ATTACHMENT_ID_PUBLIC))
          has_attachment = 1;
        else
          has_attachment = 0;
        
        String file_ext = fileExt(ATTACHMENT_NAME_PUBLIC, "\\*");
        /*data.append("{\"q_id\":\"" + CONTENT_ID + "\"," + "\"file_type\":\"file\","
            + "\"now_sort\":\"" + CONTENT_ID + "\"," + "\"parent_sort\":\"" + SORT_PARENT + "\","
            + "\"name\":\"" + T9Utility.encodeSpecial(SUBJECT) + "\"," + "\"file_ext\":\""
            + file_ext + "\"," + "\"send_time\":\"" + SEND_TIME + "\"," + "\"has_attachment\":\""
            + has_attachment + "\"},");*/
        
        Map<String,String> map = new HashMap<String, String>();
        map.put("q_id", CONTENT_ID);
        map.put("file_type", "file");
        map.put("now_sort", CONTENT_ID);
        map.put("parent_sort", SORT_PARENT);
        map.put("name", T9Utility.encodeSpecial(SUBJECT));
        map.put("file_ext", file_ext);
        map.put("send_time", SEND_TIME);
        map.put("has_attachment", String.valueOf(has_attachment));
        data.add(map);
        flag = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    /*if (flag) {
      data = data.deleteCharAt(data.length() - 1);
    }*/
    
    return data;
  }

  public String getFileContent(Connection dbConn, String sql) throws Exception {
    try {
      StringBuffer data = new StringBuffer("[");
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          data.append("{\"SUBJECT\":" + rs.getString("SUBJECT") + "," + "\"SEND_TIME\":\""
              + rs.getString("SEND_TIME") + "\"," + "\"CONTENT\":\"" + rs.getString("CONTENT")
              + "\"," + "\"SORT_ID\":\"" + rs.getInt("SORT_ID") + "\"," + "\"USER_ID\":\""
              + rs.getString("USER_ID") + "\"," + "\"ATTACHMENT_ID\":\""
              + rs.getString("ATTACHMENT_ID") + "\"," + "\"ATTACHMENT_NAME\":\""
              + rs.getString("ATTACHMENT_NAME") + "\"," + "\"USER_ID\":\""
              + rs.getString("USER_ID") + "\"," + "\"SUBJECT\":\"" + rs.getString("SUBJECT")
              + "\"},");
          flag = true;
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      if (flag) {
        data = data.deleteCharAt(data.length() - 1);
      }
      data.append("]");
      return data.toString();
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 取某一表中的某一字段值
   * 
   * @param dbConn
   * @param tableName
   * @param field
   * @return
   * @throws Exception
   */
  public String getDateByField(Connection dbConn, String tableName, String field, String sWhere)
      throws Exception {
    try {
      PreparedStatement ps = null;
      ResultSet rs = null;
      String value = "";
      if (sWhere == null || "".equals(sWhere)) {
        sWhere = " 1=1";
      }
      try {
        ps = dbConn.prepareStatement("select * from " + tableName + " where " + sWhere);
        rs = ps.executeQuery();
        if (rs.next()) {
          value = rs.getString(field);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      return value;
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 获取文件名称后最
   * 
   * @param name
   * @param regex
   * @return
   */
  private String fileExt(String name, String regex) {
    String str[] = null;
    if (T9MobileString.isEmpty(name)) {
      return "";
    }
    /**
     * 滤去 *
     */
    str = name.split(regex);// \\*
    if (str != null && str.length > 0) {
      name = str[str.length - 1];
    }
    /**
     * 取到 最后的 后缀
     */
    str = name.split(".");
    if (str != null && str.length > 0) {
      name = str[str.length - 1];
    }

    return name;
  }

  /**
   * 0男 1女
   * 
   * @param iSex
   * @return
   */
  private String changeSexUtil(int iSex) {
    if (iSex == 0) {
      return "男";
    } else {
      return "女";
    }
  }

  /**
   * 主要是查找 当前登陆用户是否是SHARE_USER
   * 
   * @param SORT_ID
   * @param person
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String share_priv(String SORT_ID, T9Person person, Connection dbConn) throws Exception {

    if (T9MobileString.isEmpty(SORT_ID))
      return "";

    String LOGIN_USER_ID = String.valueOf(person.getSeqId());
    T9FileSort fileSort = null;
    fileSort = getFileSortById(dbConn, SORT_ID);
    if (fileSort == null) {
      return "|";
    }
    String SORT_PARENT = String.valueOf(fileSort.getSortParent());
    String SHARE_USER = fileSort.getShareUser();
    String MANAGE_USER = fileSort.getManageUser();

    if (find_id(SHARE_USER, LOGIN_USER_ID))
      return SHARE_USER + "|" + MANAGE_USER;
    else
      return share_priv(SORT_PARENT, person, dbConn);
  }

  /**
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public T9FileSort getFileSortById(Connection dbConn, String SORT_ID) {

    PreparedStatement ps = null;
    ResultSet rs = null;
    T9FileSort fileSort = null;
    String _sql = "SELECT * from FILE_SORT where SEQ_ID='" + SORT_ID + "'";
    try {
      ps = dbConn.prepareStatement(_sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        fileSort = new T9FileSort();
        fileSort.setSeqId(rs.getInt("SEQ_ID"));
        fileSort.setSortParent(rs.getInt("SORT_PARENT"));
        fileSort.setShareUser(rs.getString("SHARE_USER"));
        fileSort.setManageUser(rs.getString("MANAGE_USER"));
        fileSort.setUserId(rs.getString("USER_ID"));
        fileSort.setDownUser(rs.getString("DOWN_USER"));
        fileSort.setOwner(rs.getString("OWNER"));
        fileSort.setSortName(rs.getString("SORT_NAME"));
        fileSort.setNewUser(rs.getString("NEW_USER"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return fileSort;
  }

  /**
   * php 定制用的 获取json
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public String getFileSortBySql(Connection dbConn, String sql, String SORT_PARENT_PUBLIC,
      T9Person person) {

    StringBuffer data = new StringBuffer("");
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean flag = false;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {

        String SORT_NAME_PUBLIC = rs.getString("SORT_NAME");
        String SORT_ID_PUBLIC = rs.getString("SEQ_ID");
        String USER_ID_PUBLIC = rs.getString("USER_ID");
        String OWNER_PUBLIC = rs.getString("OWNER");
        if (!T9Utility.isNumber(USER_ID_PUBLIC)) {
          USER_ID_PUBLIC = "0";
        }
        if (Integer.parseInt(USER_ID_PUBLIC) != person.getSeqId()
            && !check_priv(USER_ID_PUBLIC, person) && !check_priv(OWNER_PUBLIC, person))
          continue;

        data.append("{\"q_id\":\"" + SORT_ID_PUBLIC + "\"," + "\"file_type\":\"" + "folder" + "\","
            + "\"now_sort\":\"" + SORT_ID_PUBLIC + "\"," + "\"parent_sort\":\""
            + SORT_PARENT_PUBLIC + "\"," + "\"name\":\"" + SORT_NAME_PUBLIC + "\","
            + "\"file_ext\":\"" + "" + "\"," + "\"send_time\":\"" + "一级目录" + "\","
            + "\"has_attachment\":\"" + "" + "\"},");
        flag = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    if (flag) {
      data = data.deleteCharAt(data.length() - 1);
    }
    return data.toString();
  }

  /**
   * 
   * @param dbConn
   * @param sql
   * @return
   * @throws Exception
   */
  public int getFileSortCountBySql(Connection dbConn, String sql, T9Person person) {
    int count = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String USER_ID = rs.getString("USER_ID");
        String MANAGE_USER = rs.getString("MANAGE_USER");
        String DOWN_USER = rs.getString("DOWN_USER");
        String OWNER = rs.getString("OWNER");
        boolean ACCESS_PRIV = false;
        boolean MANAGE_PRIV = false;
        boolean DOWN_PRIV = false;
        boolean OWNER_PRIV = check_priv(OWNER, person);
        /**
         * 判断权限
         */
        ACCESS_PRIV =
            (person.getSeqId() == Integer.parseInt(USER_ID)) || check_priv(USER_ID, person)
                || OWNER_PRIV;
        MANAGE_PRIV =
            (person.getSeqId() == Integer.parseInt(USER_ID)) || check_priv(MANAGE_USER, person);
        DOWN_PRIV =
            (person.getSeqId() == Integer.parseInt(USER_ID)) || check_priv(DOWN_USER, person);

        if (MANAGE_PRIV)
          DOWN_PRIV = true;
        if (!ACCESS_PRIV)
          continue;
        /**
         * 累加
         */
        count++;

      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return count;
  }

  /**
   * 
   * @param dbConn
   * @param sql
   * @param person
   * @return
   * @throws Exception
   */
  public boolean fileSortCheckPriv(Connection dbConn, String sql, T9Person person, String SORT_ID)
      throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean resule = false;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String USER_ID = rs.getString("USER_ID");
        String MANAGE_USER = rs.getString("MANAGE_USER");
        String DOWN_USER = rs.getString("DOWN_USER");
        String OWNER = rs.getString("OWNER");

        String SHARE_PRIV = share_priv(SORT_ID, person, dbConn);
        String SHARE_USER = SHARE_PRIV.substring(0, SHARE_PRIV.indexOf("|"));

        boolean OWNER_PRIV = check_priv(OWNER, person);

        boolean ACCESS_PRIV = false;
        boolean MANAGE_PRIV = false;
        boolean DOWN_PRIV = false;

        /**
         * 判断权限
         */
        ACCESS_PRIV =
            (person.getSeqId() == Integer.parseInt(USER_ID)) || check_priv(USER_ID, person)
                || find_id(SHARE_USER, String.valueOf(person.getSeqId())) || OWNER_PRIV;
        MANAGE_PRIV =
            (person.getSeqId() == Integer.parseInt(USER_ID)) || check_priv(MANAGE_USER, person);
        DOWN_PRIV =
            MANAGE_PRIV || (person.getSeqId() == Integer.parseInt(USER_ID))
                || check_priv(DOWN_USER, person);
        if (!DOWN_PRIV)
          return false;

      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return true;
  }

  /**
   * 
   * @param dbConn
   * @param SEQ_ID
   * @return
   * @throws Exception
   */
  public T9FileContent getFileContentById(Connection dbConn, String SEQ_ID) throws Exception {

    PreparedStatement ps = null;
    ResultSet rs = null;
    T9FileContent fc = null;
    String _sql = "SELECT * from FILE_CONTENT where SEQ_ID='" + SEQ_ID + "'";
    try {
      ps = dbConn.prepareStatement(_sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        fc = new T9FileContent();
        fc.setSeqId(rs.getInt("SEQ_ID"));
        fc.setSubject(rs.getString("SUBJECT"));
        fc.setSendTime(new Date(rs.getTimestamp("SEND_TIME").getTime()));
        fc.setContent(rs.getString("CONTENT"));
        fc.setSortId(rs.getInt("SORT_ID"));
        fc.setUserId(rs.getString("USER_ID"));
        fc.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        fc.setAttachmentName(rs.getString("ATTACHMENT_NAME"));

      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return fc;
  }

  /**
   * 判段id是不是在str里面
   * 
   * @param str
   * @param id
   * @return
   */
  public static boolean find_id(String str, String id) {
    if (str == null || id == null || "".equals(str) || "".equals(id)) {
      return false;
    }
    String[] aStr = str.split(",");
    for (String tmp : aStr) {
      if (tmp.equals(id)) {
        return true;
      }
    }
    return false;
  }

  public boolean check_priv(String PRIV_STR, T9Person person) {
    // int LOGIN_DEPT_ID = person.getDeptId();
    // String LOGIN_USER_PRIV = person.getUserPriv();
    // int LOGIN_USER_ID = person.getSeqId();
    // String LOGIN_USER_PRIV_OTHER = person.getUserPrivOther();
    // String[] PRIV_ARRAY = PRIV_STR.split("\\|");
    // if(PRIV_ARRAY.length < 3){
    // return false;
    // }
    // if("ALL_DEPT".equals(PRIV_ARRAY[0]) ||
    // find_id(PRIV_ARRAY[0],String.valueOf(LOGIN_DEPT_ID))||
    // check_dept_other_priv(PRIV_ARRAY[0],person) ||
    // find_id(PRIV_ARRAY[1],LOGIN_USER_PRIV)||
    // check_id(PRIV_ARRAY[1],LOGIN_USER_PRIV_OTHER,true)!=""||
    // find_id(PRIV_ARRAY[2],String.valueOf(LOGIN_USER_ID)))
    // return true;
    return false;
  }

  public boolean check_dept_other_priv(String PRIV_STR, T9Person person) {
    String LOGIN_DEPT_ID_OTHER = person.getDeptIdOther();
    if (T9MobileString.isEmpty(LOGIN_DEPT_ID_OTHER))
      return false;
    String[] ID_ARRAY = LOGIN_DEPT_ID_OTHER.split(",");
    for (int i = 0; i < ID_ARRAY.length; i++) {
      if (T9MobileString.isEmpty(ID_ARRAY[i]))
        continue;
      if (find_id(PRIV_STR, ID_ARRAY[i]))
        return true;
    }
    return false;
  }

  /**
   * 检查去两个字符串的∩
   * 
   * @param str
   * @param ID
   * @param FLAG
   * @return
   */
  public String check_id(String str, String ID, boolean FLAG) {
    if (T9MobileString.isEmpty(ID))
      return "";
    String[] MY_ARRAY = ID.split(",");
    int ARRAY_COUNT = MY_ARRAY.length;
    String ID_STR = "";
    if (T9MobileString.isEmpty(MY_ARRAY[ARRAY_COUNT - 1]))
      ARRAY_COUNT--;
    for (int i = 0; i < ARRAY_COUNT; i++) {
      if (FLAG) {
        if (find_id(str, MY_ARRAY[i]))
          ID_STR += MY_ARRAY[i] + ",";
      } else {
        if (!find_id(str, MY_ARRAY[i]))
          ID_STR += MY_ARRAY[i] + ",";
      }
    }
    return ID_STR;
  }

}
