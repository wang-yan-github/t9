package t9.core.module.report.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9PersonSyncLogic {
  public void editPerson(T9Person o , Connection conn) throws Exception {
    String cols = "update USER SET  USER_ID=?, USER_NAME=?, BYNAME=?, USEING_KEY=?, PASSWORD=?, KEY_SN=?, SECURE=?, USER_PRIV=?, POST_PRIV=?, POST_DEPT=?, DEPT_ID=?, DEPT_ID_OTHER=?, SEX=?, BIRTHDAY=?, IS_LUNAR=?, TEL_NO_DEPT=?, FAX_NO_DEPT=?, ADD_HOME=?, POST_NO_HOME=?, TEL_NO_HOME=?, MOBIL_NO=?, BP_NO=?, EMAIL=?, OICQ=?, ICQ=?, MSN=?, NICK_NAME=?, AUATAR=?, CALL_SOUND=?, BBS_SIGNATURE=?, BBS_COUNTER=?, DUTY_TYPE=?, LAST_VISIT_TIME=?, SMS_ON=?, MENU_TYPE=?, UIN=?, PIC_ID=?, AUTHORIZE=?, CANBROADCAST=?, DISABLED=?, MOBILE_SP=?, MOBILE_PS1=?, MOBILE_PS2=?, LAST_PASS_TIME=?, THEME=?, SHORTCUT=?, PANEL=?, ON_LINE=?, ON_STATUS=?, USER_DEFINE=?, MOBIL_NO_HIDDEN=?, MYTABLE_LEFT=?, MYTABLE_RIGHT=?, EMAIL_CAPACITY=?, FOLDER_CAPACITY=?, USER_PRIV_OTHER=?, USER_NO=?, NOT_LOGIN=?, NOT_VIEW_USER=?, NOT_VIEW_TABLE=?, BKGROUND=?, BIND_IP=?, LAST_VISIT_IP=?, MENU_IMAGE=?, WEATHER_CITY=?, SHOW_RSS=?, MY_RSS=?, REMARK=?, MENU_EXPAND=?, WEBMAIL_CAPACITY=?, WEBMAIL_NUM=?, MY_STATUS=?, SCORE=?, TDER_FLAG=?, LIMIT_LOGIN=?, CONCERN_USER=?, NEV_MENU_OPEN=?, UNIQUE_ID=?, DEFAULT_PORTAL=?, PARAM_SET=?, IM_RANGE=?, PHOTO=? WHERE UID=?";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setString(1,o.getUserId());
      stm.setString(2,o.getUserName());
      stm.setString(3,o.getByname());
      stm.setString(4,o.getUseingKey());
      stm.setString(5,o.getPassword());
      stm.setString(6,o.getKeySn());
      stm.setString(7,o.getSecure());
      stm.setString(8,o.getUserPriv());
      stm.setString(9,o.getPostPriv());
      stm.setString(10,o.getPostDept());
      stm.setInt(11,o.getDeptId());
      stm.setString(12,o.getDeptIdOther());
      stm.setString(13,o.getSex());
      stm.setTimestamp(14, o.getBirthday() != null ? new Timestamp(o.getBirthday().getTime()) : new Timestamp(new Date().getTime()));
      stm.setString(15,o.getIsLunar());
      stm.setString(16,o.getTelNoDept());
      stm.setString(17,o.getFaxNoDept());
      stm.setString(18,o.getAddHome());
      stm.setString(19,o.getPostNoHome());
      stm.setString(20,o.getTelNoHome());
      
      stm.setString(21,o.getMobilNo());
      stm.setString(22,o.getBpNo());
      stm.setString(23,o.getEmail());
      
      stm.setString(24,o.getOicq());
      stm.setString(25,o.getIcq());
      stm.setString(26,o.getMsn());
      stm.setString(27,o.getNickName());
      stm.setString(28,o.getAuatar());
      stm.setString(29,o.getCallSound());
      stm.setString(30,o.getBbsSignature());
      stm.setInt(31,o.getBbsCounter());
      stm.setInt(32,o.getDutyType());
      stm.setTimestamp(33,o.getLastVisitTime() != null ? new Timestamp(o.getLastVisitTime().getTime()) : new Timestamp(new Date().getTime()));
      stm.setString(34,o.getSmsOn());
      stm.setString(35,o.getMenuType());
      stm.setInt(36,o.getUin());
      stm.setInt(37,o.getPicId());
      stm.setInt(38,o.getAuthorize());
      stm.setInt(39,o.getCanbroadcast());
      stm.setInt(40,o.getDisabled());
      stm.setString(41,o.getMobileSp());
      stm.setString(42,o.getMobilePs1());
      stm.setString(43,o.getMobilePs2());
      stm.setTimestamp(44,o.getLastPassTime() != null ? new Timestamp(o.getLastPassTime().getTime()) : new Timestamp(new Date().getTime()));
      stm.setString(45,o.getTheme());
      stm.setString(46,o.getShortcut());
      stm.setString(47,o.getPanel());
      stm.setInt(48,o.getOnLine());
      stm.setString(49,o.getOnStatus());
      stm.setString(50,o.getUserDefine());
      stm.setString(51,o.getMobilNoHidden());
      
      
      stm.setString(52,o.getMytableLeft());
      stm.setString(53,o.getMytableRight());
      stm.setInt(54,o.getEmailCapacity());
      stm.setInt(55,o.getFolderCapacity());
      stm.setString(56,o.getUserPrivOther());
      stm.setInt(57,o.getUserNo());
      stm.setString(58,o.getNotLogin());
      stm.setString(59,o.getNotViewUser());
      stm.setString(60,o.getNotViewTable());
      stm.setString(61,o.getBkground());
      stm.setString(62,o.getBindIp());
      
      stm.setString(63,o.getLastVisitIp());
      stm.setString(64,o.getMenuImage());
      stm.setString(65,o.getWeatherCity());
      stm.setString(66,o.getShowRss());
      stm.setString(67,o.getMyRss());
      stm.setString(68,o.getRemark());
      stm.setString(69,o.getMenuExpand());
      stm.setInt(70,o.getWebmailCapacity());
      stm.setInt(71,o.getWebmailNum());
      stm.setString(72,o.getMyStatus());
      
      stm.setInt(73,o.getScore());
      stm.setString(74,o.getTderFlag());
      stm.setString(75,o.getLimitLogin());
      stm.setString(76,o.getConcernUser());
      stm.setString(77,o.getNevMenuOpen());
      stm.setString(78,o.getUniqueId());
      stm.setInt(79,o.getDefaultPortal());
      stm.setString(80,o.getParamSet());
      stm.setInt(81,o.getImRange());
      stm.setString(82,o.getPhoto());
      stm.setInt(83,o.getSeqId());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void addPerson(T9Person o , Connection conn) throws Exception {
    String cols = "insert into USER (UID, USER_ID, USER_NAME, BYNAME, USEING_KEY, PASSWORD, KEY_SN, SECURE, "
        +"USER_PRIV, POST_PRIV, POST_DEPT, DEPT_ID, DEPT_ID_OTHER, SEX, BIRTHDAY, IS_LUNAR, TEL_NO_DEPT"
        +", FAX_NO_DEPT, ADD_HOME, POST_NO_HOME, TEL_NO_HOME, MOBIL_NO, BP_NO, EMAIL"
        +", OICQ, ICQ, MSN, NICK_NAME, AUATAR, CALL_SOUND, BBS_SIGNATURE, BBS_COUNTER"
        +", DUTY_TYPE, LAST_VISIT_TIME, SMS_ON, MENU_TYPE, UIN, PIC_ID, AUTHORIZE"
        +", CANBROADCAST, DISABLED, MOBILE_SP, MOBILE_PS1, MOBILE_PS2, LAST_PASS_TIME"
        +", THEME, SHORTCUT, PANEL, ON_LINE, ON_STATUS, USER_DEFINE, MOBIL_NO_HIDDEN"
        +", MYTABLE_LEFT, MYTABLE_RIGHT, EMAIL_CAPACITY, FOLDER_CAPACITY, USER_PRIV_OTHER"
        +", USER_NO, NOT_LOGIN, NOT_VIEW_USER, NOT_VIEW_TABLE, BKGROUND, BIND_IP"
        +", LAST_VISIT_IP, MENU_IMAGE, WEATHER_CITY, SHOW_RSS, MY_RSS, REMARK"
        +", MENU_EXPAND, WEBMAIL_CAPACITY, WEBMAIL_NUM, MY_STATUS, SCORE, TDER_FLAG"
        +", LIMIT_LOGIN, CONCERN_USER, NEV_MENU_OPEN, UNIQUE_ID, DEFAULT_PORTAL, PARAM_SET"
        +", IM_RANGE, PHOTO) "
        +" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setInt(1,o.getSeqId());
      stm.setString(2,o.getUserId());
      stm.setString(3,o.getUserName());
      stm.setString(4,o.getByname());
      stm.setString(5,o.getUseingKey());
      stm.setString(6,o.getPassword());
      stm.setString(7,o.getKeySn());
      stm.setString(8,o.getSecure());
      stm.setString(9,o.getUserPriv());
      stm.setString(10,o.getPostPriv());
      stm.setString(11,o.getPostDept());
      stm.setInt(12,o.getDeptId());
      stm.setString(13,o.getDeptIdOther());
      stm.setString(14,o.getSex());
      stm.setTimestamp(15, o.getBirthday() != null ? new Timestamp( o.getBirthday().getTime()) : new Timestamp(new Date().getTime()));
      stm.setString(16,o.getIsLunar());
      stm.setString(17,o.getTelNoDept());
      stm.setString(18,o.getFaxNoDept());
      stm.setString(19,o.getAddHome());
      stm.setString(20,o.getPostNoHome());
      stm.setString(21,o.getTelNoHome());
      
      stm.setString(22,o.getMobilNo());
      stm.setString(23,o.getBpNo());
      stm.setString(24,o.getEmail());
      
      stm.setString(25,o.getOicq());
      stm.setString(26,o.getIcq());
      stm.setString(27,o.getMsn());
      stm.setString(28,o.getNickName());
      stm.setString(29,o.getAuatar());
      stm.setString(30,o.getCallSound());
      stm.setString(31,o.getBbsSignature());
      stm.setInt(32,o.getBbsCounter());
      stm.setInt(33,o.getDutyType());
      stm.setTimestamp(34,o.getLastVisitTime() != null ? new Timestamp(o.getLastVisitTime().getTime()) : new Timestamp(new Date().getTime()));
      stm.setString(35,o.getSmsOn());
      stm.setString(36,o.getMenuType());
      stm.setInt(37,o.getUin());
      stm.setInt(38,o.getPicId());
      stm.setInt(39,o.getAuthorize());
      stm.setInt(40,o.getCanbroadcast());
      stm.setInt(41,o.getDisabled());
      stm.setString(42,o.getMobileSp());
      stm.setString(43,o.getMobilePs1());
      stm.setString(44,o.getMobilePs2());
      stm.setTimestamp(45,o.getLastPassTime() != null ? new Timestamp(o.getLastPassTime().getTime()) : new Timestamp(new Date().getTime()));
      stm.setString(46,o.getTheme());
      stm.setString(47,o.getShortcut());
      stm.setString(48,o.getPanel());
      stm.setInt(49,o.getOnLine());
      stm.setString(50,o.getOnStatus());
      stm.setString(51,o.getUserDefine());
      stm.setString(52,o.getMobilNoHidden());
      
      
      stm.setString(53,o.getMytableLeft());
      stm.setString(54,o.getMytableRight());
      stm.setInt(55,o.getEmailCapacity());
      stm.setInt(56,o.getFolderCapacity());
      stm.setString(57,o.getUserPrivOther());
      stm.setInt(58,o.getUserNo());
      stm.setString(59,o.getNotLogin());
      stm.setString(60,o.getNotViewUser());
      stm.setString(61,o.getNotViewTable());
      stm.setString(62,o.getBkground());
      stm.setString(63,o.getBindIp());
      
      stm.setString(64,o.getLastVisitIp());
      stm.setString(65,o.getMenuImage());
      stm.setString(66,o.getWeatherCity());
      stm.setString(67,o.getShowRss());
      stm.setString(68,o.getMyRss());
      stm.setString(69,o.getRemark());
      stm.setString(70,o.getMenuExpand());
      stm.setInt(71,o.getWebmailCapacity());
      stm.setInt(72,o.getWebmailNum());
      stm.setString(73,o.getMyStatus());
      
      stm.setInt(74,o.getScore());
      stm.setString(75,o.getTderFlag());
      stm.setString(76,o.getLimitLogin());
      stm.setString(77,o.getConcernUser());
      stm.setString(78,o.getNevMenuOpen());
      stm.setString(79,o.getUniqueId());
      stm.setInt(80,o.getDefaultPortal());
      stm.setString(81,o.getParamSet());
      stm.setInt(82,o.getImRange());
      stm.setString(83,o.getPhoto());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void updatePersonPwd(String seqId , Connection conn , String newPwd) throws Exception {
    String cols = "update  user set password = ? where UID in (" + seqId + ")";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setString(1, newPwd);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void updatePerson(String setStr , Connection conn , String uid) throws Exception {
    if (T9Utility.isNullorEmpty(setStr)
        && setStr.endsWith(",")) {
      setStr = setStr.substring(0 , setStr.length() - 1);
    }
    String cols = "update  user set "+setStr + " where not USER_ID = 'admin' and UID in (" + uid + ")";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void delPersonByDept(int deptId , Connection conn , boolean isAdmin) throws Exception {
    String cols = "UPDATE user set dept_id = '0' where DEPT_ID = '" + deptId + "'";
    if (!isAdmin) 
      cols = "delete from  user where DEPT_ID = '" + deptId + "'";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  
  public void delPerson(int seqId , Connection conn) throws Exception {
    String cols = "delete from user where UID = " + seqId;
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void delPerson(String str , Connection conn) throws Exception {
    String cols = "delete from user where " + str;
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void updatePersonSex( Connection conn , String sex , int seqId) throws Exception {
    String cols = "update  user set SEX=? WHERE uid='" + seqId + "'";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setString(1, sex);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void personLeave( Connection conn , int seqId) throws Exception {
    String cols = "update  user set DEPT_ID='0' , NOT_LOGIN='1' WHERE uid='" + seqId + "'";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void personReinstatement( Connection conn , int seqId , int deptId) throws Exception {
    String cols = "update  user set DEPT_ID='"+deptId+"' , NOT_LOGIN='0' WHERE uid='" + seqId + "'";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void updatePerson( Connection conn , String sex , String userPriv , int seqId ) throws Exception {
    String cols = "update  user set SEX=?,USER_PRIV=? WHERE uid='" + seqId + "'";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setString(1, sex);
      stm.setString(2, userPriv);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void delPerson( Connection conn) throws Exception {
    String cols = "delete from user";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void syncPerson(Connection conn , Connection reportConn) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9Person> list = (List<T9Person>)orm.loadListSingle(conn, T9Person.class, new HashMap());
    this.delPerson(reportConn);
    for (T9Person u : list) {
      this.addPerson(u, reportConn);
    }
  }

}
