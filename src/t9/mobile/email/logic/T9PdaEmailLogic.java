package t9.mobile.email.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaEmailLogic {
    /**
     * 显示根目录下目录
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public List getEmailList(Connection dbConn, String sql, String CURRITERMS) throws Exception {
        try {
        	//StringBuffer data = new StringBuffer("[");
            List data = new ArrayList();
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean flag = false;
            int c = T9MobileUtility.getCURRITERMS(CURRITERMS);
            int j = 0;
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (j < c) {
                        j++;
                        continue;
                    }
                    if (j >= T9MobileConfig.PAGE_SIZE + c)
                        break;
                    /**
                     * 取出所有数据 然后做判断 拼接json
                     */
                    String FROM_ID = rs.getString("FROM_ID");
                    String SUBJECT = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("SUBJECT")));
                    String SEND_TIME = "";
                    if (rs.getTimestamp("SEND_TIME") != null)
                        SEND_TIME = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
                    String IMPORTANT = rs.getString("IMPORTANT");
                    String ATTACHMENT_ID = rs.getString("ATTACHMENT_ID");
                    String ATTACHMENT_NAME = rs.getString("ATTACHMENT_NAME");
                    String FROM_NAME = T9Utility.null2Empty(rs.getString("USER_NAME"));
                    int READ_FLAG = Integer.parseInt(rs.getString("READ_FLAG"));
                    String IS_WEBMAIL = rs.getString("IS_WEBMAIL");
                    String RECV_FROM = T9Utility.null2Empty(rs.getString("RECV_FROM"));
                    String RECV_FROM_NAME = T9Utility.null2Empty(rs.getString("RECV_FROM_NAME"));
                    int has_attachment = 0;

                    if (FROM_NAME == null || "".equals(FROM_NAME))
                        FROM_NAME = FROM_ID;
                    if (IS_WEBMAIL != null && !"0".equals(IS_WEBMAIL)) {
                        FROM_NAME = RECV_FROM_NAME + RECV_FROM;
                    }
                    if (SUBJECT == null || "".equals(SUBJECT))
                        SUBJECT = "无标题";

                    if (ATTACHMENT_ID != null && !"".equals(ATTACHMENT_NAME))
                        has_attachment = 1;
                    else
                        has_attachment = 0;

                    if (READ_FLAG != 1) {
                        READ_FLAG = 0;
                    } else {
                        READ_FLAG = 1;
                    }
                    /*data.append("{\"q_id\":" + rs.getInt("EMAIL_ID") + "," + "\"read_flag\":\"" + READ_FLAG
                            + "\"," + "\"subject\":\"" + SUBJECT + "\"," + "\"important_desc\":\""
                            + IMPORTANT + "\"," + "\"send_time\":\"" + SEND_TIME + "\"," + "\"from_name\":\""
                            + FROM_NAME + "\"," + "\"has_attachment\":\"" + has_attachment + "\"},");*/

                    Map map = new HashMap();
                    map.put("q_id", rs.getInt("EMAIL_ID"));
                    map.put("read_flag", READ_FLAG);
                    map.put("subject", SUBJECT);
                    map.put("important_desc", IMPORTANT);
                    map.put("send_time", SEND_TIME);
                    map.put("from_name", FROM_NAME);
                    map.put("has_attachment", has_attachment);
                    data.add(map);
                    flag = true;
                    j++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            /*if (flag) {
                data = data.deleteCharAt(data.size() - 1);
            }
            data.append("]");
            return data.toString();*/
            return data;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 显示根目录下目录
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public List getEmailList(Connection dbConn, String sql) throws Exception {
        try {
            //StringBuffer data = new StringBuffer("[");
        	List data = new ArrayList();
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean flag = false;
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    /**
                     * 取出所有数据 然后做判断 拼接json
                     */
                    String FROM_ID = rs.getString("FROM_ID");
                    String SUBJECT = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("SUBJECT")));
                    String SEND_TIME = "";
                    if (rs.getTimestamp("SEND_TIME") != null)
                        SEND_TIME = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
                    String IMPORTANT = rs.getString("IMPORTANT");
                    String ATTACHMENT_ID = rs.getString("ATTACHMENT_ID");
                    String ATTACHMENT_NAME = rs.getString("ATTACHMENT_NAME");
                    String FROM_NAME = T9Utility.null2Empty(rs.getString("USER_NAME"));
                    int READ_FLAG = Integer.parseInt(rs.getString("READ_FLAG"));
                    String IS_WEBMAIL = rs.getString("IS_WEBMAIL");
                    String RECV_FROM = T9Utility.null2Empty(rs.getString("RECV_FROM"));
                    String RECV_FROM_NAME = T9Utility.null2Empty(rs.getString("RECV_FROM_NAME"));
                    int has_attachment = 0;

                    if (FROM_NAME == null || "".equals(FROM_NAME))
                        FROM_NAME = FROM_ID;
                    if (IS_WEBMAIL != null && !"0".equals(IS_WEBMAIL)) {
                        FROM_NAME = RECV_FROM_NAME + RECV_FROM;
                    }
                    if (SUBJECT == null || "".equals(SUBJECT))
                        SUBJECT = "无标题";

                    if (ATTACHMENT_ID != null && !"".equals(ATTACHMENT_NAME))
                        has_attachment = 1;
                    else
                        has_attachment = 0;

                    if (READ_FLAG != 1) {
                        READ_FLAG = 0;
                    } else {
                        READ_FLAG = 1;
                    }
                    /*data.append("{\"q_id\":" + rs.getInt("EMAIL_ID") + "," + "\"read_flag\":\"" + READ_FLAG
                            + "\"," + "\"subject\":\"" + SUBJECT + "\"," + "\"important_desc\":\""
                            + IMPORTANT + "\"," + "\"send_time\":\"" + SEND_TIME + "\"," + "\"from_name\":\""
                            + FROM_NAME + "\"," + "\"has_attachment\":\"" + has_attachment + "\"},");*/

                    Map map = new HashMap();
                    map.put("q_id", rs.getInt("EMAIL_ID"));
                    map.put("read_flag", READ_FLAG);
                    map.put("subject", SUBJECT);
                    map.put("important_desc", IMPORTANT);
                    map.put("send_time", SEND_TIME);
                    map.put("from_name", FROM_NAME);
                    map.put("has_attachment", has_attachment);
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
            }
            data.append("]");*/
            //return data.toString();
            return data;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取邮件具体内容
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public List getEmailContent(Connection dbConn, String sql, String emailId) throws Exception {
        try {
            //StringBuffer data = new StringBuffer("");
        	List data = new ArrayList();
            PreparedStatement ps = null;
            ResultSet rs = null;
            boolean flag = false;
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {

                    String FROM_ID = rs.getString("FROM_ID");
                    String SUBJECT = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("SUBJECT")));
                    String CONTENT = T9Utility.encodeSpecial(T9Utility.null2Empty(rs
                            .getString("COMPRESS_CONTENT") == "" ? rs.getString("CONTENT") : rs
                            .getString("COMPRESS_CONTENT")));
                    String SEND_TIME = "";
                    if (rs.getTimestamp("SEND_TIME") != null)
                        SEND_TIME = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
                    String IMPORTANT = T9Utility.null2Empty(rs.getString("IMPORTANT"));
                    String ATTACHMENT_ID = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
                    String ATTACHMENT_NAME = T9Utility.null2Empty(rs.getString("ATTACHMENT_NAME"));
                    String READ_FLAG = T9Utility.null2Empty(rs.getString("READ_FLAG"));
                    String IS_WEBMAIL = T9Utility.null2Empty(rs.getString("IS_WEBMAIL"));

                    int has_attachment = 0;
                    /**
                     * 用下面的方法 就不用拼这条sql 了 直接用 getDateByField 方法 能获取到 字段数值
                     * 只是还不够强大 String _sql =
                     * "SELECT SEQ_ID,USER_NAME from PERSON where USER_ID='"
                     * +FROM_ID+"'";
                     */
                    String FROM_NAME = getDateByField(dbConn, "PERSON", "USER_NAME", "SEQ_ID = '" + FROM_ID
                            + "'");
                    FROM_NAME = T9MobileString.showObjNull(FROM_NAME, FROM_ID);
                    /**
                     * 获取 UID
                     */
                    // String UID = getDateByField(dbConn, "PERSON", "SEQ_ID",
                    // "SEQ_ID = '" + FROM_ID + "'");
                    // UID = T9MobileString.showObjNull(UID, "0");
                    /**
                     * 增加已阅状态更新
                     */
                    if ("0".equals(READ_FLAG)) {
                        String _sql = "update EMAIL set READ_FLAG = 1 where SEQ_ID='" + emailId + "'";
                        T9MobileUtility.updateSql(dbConn, _sql);
                    }
                    if (!T9MobileString.isEmpty(ATTACHMENT_ID))
                        has_attachment = 1;
                    else
                        has_attachment = 0;
                    /*data.append("{\"q_id\":" + emailId + "," + "\"subject\":\"" + SUBJECT + ","
                            + "\"send_time\":\"" + SEND_TIME + "," + "\"from_name\":\"" + FROM_NAME + "\","
                            + "\"from_user_id\":\"" + FROM_ID + "\"," + "\"is_webmail\":\"" + IS_WEBMAIL
                            + "\"," + "\"content\":\"" + CONTENT + "\"," + "\"has_attachment\":\""
                            + has_attachment + "\"," + "\"attachment_id\":\"" + ATTACHMENT_ID + "\","
                            + "\"attachment_name\":\"" + ATTACHMENT_NAME + "\"},");*/
                    Map map = new HashMap();
                    map.put("q_id", emailId);
                    map.put("subject", SUBJECT);
                    map.put("send_time", SEND_TIME);
                    map.put("from_name", FROM_NAME);
                    map.put("from_user_id", FROM_ID);
                    map.put("is_webmail", IS_WEBMAIL);
                    map.put("content", CONTENT);
                    map.put("has_attachment", has_attachment);
                    map.put("attachment_id", ATTACHMENT_ID);
                    map.put("attachment_name", ATTACHMENT_NAME);
                    data.add(map);
                    flag = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }/*
            if (flag) {
                data = data.deleteCharAt(data.length() - 1);
            }
            return data.toString();*/
            return data;
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
     * 读取邮件
     * 
     * @param dbConn
     * @param sql
     * @return
     * @throws Exception
     */
    public Map readEmail(Connection dbConn, String sql, String EMAIL_ID) throws Exception {
        Map map = new HashMap<String, String>();
        try {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = dbConn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    String FROM_ID = rs.getString("FROM_ID");
                    String SUBJECT = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("SUBJECT")));
                    String CONTENT = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("CONTENT")));
                    String SEND_TIME = "";
                    if (rs.getTimestamp("SEND_TIME") != null)
                        SEND_TIME = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
                    String IMPORTANT = T9Utility.null2Empty(rs.getString("IMPORTANT"));
                    String ATTACHMENT_ID = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
                    String ATTACHMENT_NAME = T9Utility.null2Empty(rs.getString("ATTACHMENT_NAME"));
                    String READ_FLAG = T9Utility.null2Empty(rs.getString("READ_FLAG"));
                    String RECV_FROM = T9Utility.null2Empty(rs.getString("RECV_FROM"));
                    String RECV_FROM_NAME = T9Utility.null2Empty(rs.getString("RECV_FROM_NAME"));
                    String RECV_TO = T9Utility.null2Empty(rs.getString("RECV_TO"));
                    String IS_WEBMAIL = rs.getString("IS_WEBMAIL") != null ? rs.getString("IS_WEBMAIL") : "0";
                    String FROM_MAIL = "";
                    String TO_MAIL = "";
                    String IMPORTANT_DESC = "";
                    if (!"0".equals(IS_WEBMAIL)) {
                        FROM_MAIL = RECV_FROM_NAME + RECV_FROM;
                        TO_MAIL = RECV_TO;
                    }
                    if ("0".equals(IS_WEBMAIL) || "".equals(IMPORTANT))
                        IMPORTANT_DESC = "";
                    else if ("1".equals(IMPORTANT))
                        IMPORTANT_DESC = "<font color='red'>重要</font>";
                    else if ("2".equals(IMPORTANT))
                        IMPORTANT_DESC = "<font color='red'>非常重要</font>";
                    String mapStrArray[] = { "SEQ_ID", "USER_NAME", "DEPT_ID" };
                    Map rsMap = T9MobileUtility.getDateByField(dbConn, "PERSON", mapStrArray, " SEQ_ID ='"
                            + FROM_ID + "'");

                    String FROM_NAME = "";
                    String UID = "";
                    String DEPT_ID = "";
                    String DEPT_NAME = "";
                    if (rsMap.size() > 0) {
                        UID = (String) rsMap.get("SEQ_ID");
                        FROM_NAME = (String) rsMap.get("USER_NAME");
                        DEPT_ID = (String) rsMap.get("DEPT_ID");
                        DEPT_ID = T9MobileString.showNull(DEPT_ID, "0");
                        DEPT_NAME = T9MobileUtility.getDateByField(dbConn, "department", "DEPT_NAME",
                                " SEQ_ID ='" + DEPT_ID + "'");
                    } else {
                        FROM_NAME = FROM_ID;
                        DEPT_NAME = "用户已删除";
                    }

                    if (IS_WEBMAIL != null && !"0".equals(IS_WEBMAIL)) {
                        FROM_NAME = FROM_MAIL;
                        DEPT_NAME = "";
                    }
                    /**
                     * 更新邮件 状态为 已读
                     */
                    if ("0".equals(READ_FLAG)) {
                        String _tem_sql = "update EMAIL set READ_FLAG = 1 where SEQ_ID='" + EMAIL_ID + "'";
                        T9MobileUtility.updateSql(dbConn, _tem_sql);
                    }
                    map.put("FROM_ID", FROM_ID);
                    map.put("UID", UID);
                    map.put("SUBJECT", SUBJECT);
                    map.put("IMPORTANT_DESC", IMPORTANT_DESC);
                    map.put("FROM_NAME", FROM_NAME);
                    map.put("DEPT_NAME", DEPT_NAME);
                    map.put("SEND_TIME", SEND_TIME);
                    map.put("CONTENT", CONTENT);
                    map.put("ATTACHMENT_ID", ATTACHMENT_ID);
                    map.put("ATTACHMENT_NAME", ATTACHMENT_NAME);
                    map.put("IS_WEBMAIL", IS_WEBMAIL);
                    map.put("TO_MAIL", TO_MAIL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                T9DBUtility.close(ps, rs, null);
            }
            return map;
        } catch (Exception e) {
            throw e;
        }
    }
}
