package t9.core.funcs.userinfo.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.diary.data.T9Diary;
import t9.core.funcs.diary.data.T9DiaryLock;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.manage.staffInfo.act.T9HrStaffInfoAct;
import t9.subsys.oa.hr.salary.welfare_manager.data.T9HrWelfareManage;

public class T9UserInfoLogic {

  private static Logger log = Logger
      .getLogger("t9.core.act.action.T9SysMenuLog");

  /**
   * 获取登录用户菜单权限
   * 
   * @param dbConn
   * @param userPriv
   * @return
   * @throws Exception
   */
  public String getFuncStrLogic(Connection dbConn, String userPriv)
      throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;
    stmt = dbConn.createStatement();
    String sql = "select FUNC_ID_STR from user_priv where SEQ_ID=" + userPriv;
    rs = stmt.executeQuery(sql);
    String func_id_str = "";
    if (rs.next()) {
      func_id_str = rs.getString(1);
    }
    return func_id_str.trim();
  }

  /**
   * 获取查看用户的详细信息
   * 
   * 
   * @param dbConn
   * @param person
   * @param userId
   * @return
   * @throws Exception
   * @throws Exception
   */
  public T9Person getUserDetailLogic(Connection dbConn, T9Person person,
      String userId) throws Exception {

    int seqId = Integer.parseInt(userId);

    try {
      T9ORM orm = new T9ORM();
      return (T9Person) orm.loadObjSingle(dbConn, T9Person.class, seqId);
    } catch (Exception ex) {
      throw ex;
    }

  }

  /**
   * 判断userId是否在login_user的管理范围内
   * 
   * @param dbConn
   * @param person
   * @param userId
   * @throws SQLException
   * */
  public boolean is_user_priv(Connection dbConn, T9Person person, String userId)
      throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;
    stmt = dbConn.createStatement();

    // 获取Login_user的权限
    String RIV_NO_FLAG = "0";
    String MODULE_ID = "2";
    String DEPT_PRIV = "";
    String ROLE_PRIV = "";
    String DEPT_ID_STR = "";
    String PRIV_ID_STR = "";
    String USER_ID_STR = "";
    String MY_PRIV_NO = "";
    ROLE_PRIV = "";
    String sql = "";
    String login_user_id = person.getUserId();
    String login_user_priv = person.getUserPriv();

    sql = "SELECT * from MODULE_PRIV where USER_SEQ_ID='" + login_user_id
        + "' and MODULE_ID='" + MODULE_ID + "'";
    rs = stmt.executeQuery(sql);
    if (rs.next()) {
      DEPT_PRIV = rs.getString("DEPT_PRIV");
      ROLE_PRIV = rs.getString("ROLE_PRIV");
      DEPT_ID_STR = rs.getString("DEPT_ID");
      PRIV_ID_STR = rs.getString("PRIV_ID");
      USER_ID_STR = rs.getString("USER_ID");
    } else {
      DEPT_PRIV = "1";
      ROLE_PRIV = "2";
    }

    if (ROLE_PRIV == "0" || ROLE_PRIV == "1") {
      sql = "select PRIV_NO from USER_PRIV where USER_PRIV='" + login_user_id
          + "'";
      rs = stmt.executeQuery(sql);
      if (rs.next())
        MY_PRIV_NO = rs.getString("PRIV_NO");
    }
    // 获取userId的信息
    String DEPT_ID = "";
    String USER_PRIV = "";
    String PRIV_NO = "";

    if (DEPT_PRIV.equals("1")) {
      return true;
    }

    sql = "SELECT DEPT_ID,person.USER_PRIV,PRIV_NO from person,USER_PRIV where person.SEQ_ID="
        + login_user_id + " and person.USER_PRIV=USER_PRIV.SEQ_ID";
    rs = stmt.executeQuery(sql);
    if (rs.next()) {
      DEPT_ID = rs.getString("DEPT_ID");
      USER_PRIV = rs.getString("USER_PRIV");
      PRIV_NO = rs.getString("PRIV_NO");
    }

    /*
     * if(DEPT_PRIV.equals("4") && userId!=login_user_id ||
     * DEPT_PRIV.equals("0") && !is_dept_priv(DEPT_ID,DEPT_PRIV,DEPT_ID_STR) ||
     * DEPT_PRIV.equals("2") && !find_id(DEPT_ID_STR,DEPT_ID) ||
     * DEPT_PRIV.equals("3") && !find_id(USER_ID_STR, userId) ||
     * ROLE_PRIV.equals("0") && MY_PRIV_NO>=PRIV_NO || ROLE_PRIV.equals("1") &&
     * MY_PRIV_NO> PRIV_NO || ROLE_PRIV.equals("3") && !find_id(PRIV_ID_STR,
     * USER_PRIV)) return false;
     */
    return true;
  }

  public boolean find_id(String findstr, String str) {
    if (findstr.indexOf(str) != -1)
      return true;
    else {
      return false;
    }

  }

  public boolean is_dept_priv(String dept_id, String dept_priv,
      String dept_id_str) {
    /*
     * if(dept_id.equals(dept_priv) || dept_id_str.indexOf(dept_id)!=-1){ return
     * true; } if(is_dept_parent(dept_id,)) }
     */
    return true;
  }

  /**
   * 取得共享日志
   * 
   * @param conn
   * @param userId
   * @param type
   *          1 代表工作日志，2代表工作日志查询
   * @return
   * @throws Exception
   */
  public List<T9Diary> getDiaryShareLogic(Connection conn, int login_user_Id,
      String userId) throws Exception {
    ArrayList<T9Diary> diaList = null;
    ArrayList<T9Diary> result = new ArrayList<T9Diary>();
    T9ORM orm = new T9ORM();

    String[] filters = null;
    // $query =
    // "SELECT DIA_ID from DIARY where USER_ID='$USER_ID' and find_in_set('$LOGIN_USER_ID',TO_ID)";
    filters = new String[] { "USER_ID='" + userId + "' and "
        + T9DBUtility.findInSet(login_user_Id + "", "TO_ID")
        + " ORDER BY DIA_DATE DESC ,DIA_TIME DESC " };

    try {
      diaList = (ArrayList<T9Diary>) orm.loadListSingle(conn, T9Diary.class,
          filters);

      result = diaList;

    } catch (Exception e) {
      throw e;
    }
    return result;
  }

  public String getUserPrivLogic(Connection conn, String userPriv)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String privName = "";
    try {
      stmt = conn.createStatement();
      String sql = "select PRIV_NAME from USER_PRIV where SEQ_ID=" + userPriv;
      rs = stmt.executeQuery(sql);
      if (rs.next())
        privName = rs.getString("PRIV_NAME");
    } catch (Exception e) {
      throw e;
    }
    return privName;
  }

  public String getDeptNameLogic(Connection conn, String deptId)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String deptName = "";
    try {
      stmt = conn.createStatement();
      String sql = "select * from DEPARTMENT where SEQ_ID=" + deptId;
      rs = stmt.executeQuery(sql);
      if (rs.next())
        deptName = rs.getString("DEPT_NAME");
    } catch (Exception e) {
      throw e;
    }
    return deptName;
  }

  public String getAvatarLogic(Connection conn, String userId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String hrms_photo = "";
    try {
      stmt = conn.createStatement();
      String sql = "";

      sql = "select PHOTO_NAME from HR_STAFF_INFO where USER_ID='" + userId
          + "'";
      rs = stmt.executeQuery(sql);
      if (rs.next())
        hrms_photo = rs.getString("PHOTO_NAME");

    } catch (Exception e) {
      throw e;
    }
    return hrms_photo;
  }

  public String getDeptNoLogic(Connection conn, String deptId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String deptNo = "";
    try {
      stmt = conn.createStatement();
      String sql = "";

      sql = "select DEPT_NO from DEPARTMENT where SEQ_ID=" + deptId;
      rs = stmt.executeQuery(sql);
      if (rs.next())
        deptNo = rs.getString("DEPT_NO");

    } catch (Exception e) {
      throw e;
    }
    return deptNo;
  }
  public String[] getDeptTelNoLogic(Connection conn, String deptId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String[] str = new String[2];
    try {
      stmt = conn.createStatement();
      String sql = "";

      sql = "select TEL_NO,FAX_NO from DEPARTMENT where SEQ_ID=" + deptId;
      rs = stmt.executeQuery(sql);
      if (rs.next())
        str[0] = rs.getString("TEL_NO");
        str[1] = rs.getString("FAX_NO");
    } catch (Exception e) {
      throw e;
    }
    return str;
  }
  public String getOnStatusLogic(Connection conn, String userId)
      throws Exception {
    String onStatus = "";
    synchronized(T9SystemService.onlineSync) {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      String sql = "";

      sql = "select distinct * from USER_ONLINE where USER_ID=" + userId;
      rs = stmt.executeQuery(sql);
      if (!rs.next()) {
        onStatus = "0";

      }

    } catch (Exception e) {
      throw e;
    }
    conn.commit();
    }
    return onStatus;
  }

  public String getUserPrivOtherNameLogic(Connection conn, String Id)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String name = "";
    try {
      if (!"".equals(Id)) {
        stmt = conn.createStatement();
        String sql = "";
        if (',' == Id.charAt(Id.length() - 1)) {
          Id = Id.substring(0, Id.length() - 1);
        }

        sql = "select PRIV_NAME from USER_PRIV where SEQ_ID in (" + Id + ")";

        rs = stmt.executeQuery(sql);
        while (rs.next())
          name += rs.getString("PRIV_NAME") + "，";
      }
    } catch (Exception e) {
      throw e;
    }
    return name;
  }

  public String getAuatarExitLogic(String photo) throws Exception {

    String exit = "0";
    try {

      File file = new File(photo);
      if (file.exists()) {
        exit = "1";
      }

    } catch (Exception e) {
      throw e;
    }
    return exit;
  }

  /**
   * 获取日程安排
   * */
  public List<T9Calendar> selectCalendarByTerm(Connection dbConn, String userId)
      throws Exception {
    List<T9Calendar> calendarList = new ArrayList<T9Calendar>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    long dateTime = date.getTime();

    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.add(GregorianCalendar.DATE, 10); // 获取之后10天的日程安排
    Date date1 = cal.getTime();
    String dateStr1 = dateFormat.format(new Date());
    String dateStr2 = dateFormat.format(date1);
    dateStr1 = dateStr1 + " 00:00:00";
    dateStr2 = dateStr2 + " 23:59:59";

    String sql = "select * from calendar where cal_type!='2' ";
    if (!"".equals(userId)) {
      sql += " and USER_ID='" + userId + "'";
    }

    sql += " and (";
    sql += T9DBUtility.getDateFilter("cal_time", dateStr1, ">=") + " and "
        + T9DBUtility.getDateFilter("end_time", dateStr2, "<=");
    sql += " or ";
    sql += T9DBUtility.getDateFilter("cal_time", dateStr1, "<=") + " and "
        + T9DBUtility.getDateFilter("end_time", dateStr1, ">=");
    sql += " or ";
    sql += T9DBUtility.getDateFilter("cal_time", dateStr2, "<=") + " and "
        + T9DBUtility.getDateFilter("end_time", dateStr2, ">=");
    sql += ")";
    sql += " order by cal_time ";
    // System.out.println(sql);
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        T9Calendar calendar = new T9Calendar();
        calendar.setSeqId(rs.getInt("SEQ_ID"));
        calendar.setUserId(rs.getString("USER_ID"));
        calendar.setCalType(rs.getString("CAL_TYPE"));
        if (rs.getString("CAL_TIME") != null
            && !rs.getString("CAL_TIME").equals("")) {
          calendar.setCalTime(dateFormat.parse(rs.getString("CAL_TIME")));
        }
        if (rs.getString("END_TIME") != null
            && !rs.getString("END_TIME").equals("")) {
          calendar.setEndTime(dateFormat.parse(rs.getString("END_TIME")));
        }
        calendar.setContent(rs.getString("CONTENT"));
        calendar.setCalLevel(rs.getString("CAL_LEVEL"));
        calendar.setOverStatus(rs.getString("OVER_STATUS"));
        calendar.setManagerId(rs.getString("MANAGER_ID"));
        calendarList.add(calendar);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return calendarList;
  }
}
