package t9.mobile.notify.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaNotifyLogic {

    public void getNotifyContent(HttpServletRequest request, HttpServletResponse response, Connection conn,
            T9Person person) throws Exception {
        String NOTIFY_ID = request.getParameter("Q_ID");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String CUR_DATE = sdf.format(new Date()) + " 00:00:00";

        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
        String query = "SELECT * from NOTIFY where SEQ_ID='" + NOTIFY_ID
                + "' and (TO_ID='ALL_DEPT' or TO_ID='0' or "
                + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID") + " or "
                + T9DBUtility.findInSet(person.getUserPriv() + "", "PRIV_ID") + " or "
                + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID") + ") and "
                + T9DBUtility.getDateFilter("begin_date", CUR_DATE, "<=") + " and ("
                + T9DBUtility.getDateFilter("end_date", CUR_DATE, ">=")
                + " or end_date is null) and PUBLISH='1'";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map resultMap = new HashMap();
                String FROM_ID = rs.getString("FROM_ID");
                String SUBJECT_COLOR = rs.getString("SUBJECT_FONT");
                String SUBJECT = rs.getString("SUBJECT");
                String FORMAT = rs.getString("FORMAT");
                String CONTENT = rs.getString("CONTENT");
                String COMPRESS_CONTENT = rs.getString("COMPRESS_CONTENT");
                
                if (!"".equals(COMPRESS_CONTENT) && "2".equals(FORMAT))
                    CONTENT = COMPRESS_CONTENT;
                else
                    CONTENT = rs.getString("CONTENT");
                String ATTACHMENT_ID = rs.getString("ATTACHMENT_ID");
                String ATTACHMENT_NAME = rs.getString("ATTACHMENT_NAME");
                
                String aId[] = null;
                String aName[] = null;
                List<String> DOWN_FILE_URL = new ArrayList<String>();
                if(!"".equals(ATTACHMENT_ID) && !"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_ID && null != ATTACHMENT_NAME){
                    if(!"".equals(ATTACHMENT_ID) && null != ATTACHMENT_ID){
                        ATTACHMENT_ID = ATTACHMENT_ID.substring(0, ATTACHMENT_ID.length()-1);
                        aId = ATTACHMENT_ID.split(",");
                    }
                    if(!"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_NAME){
                        ATTACHMENT_NAME = ATTACHMENT_NAME.substring(0,ATTACHMENT_NAME.length()-1);
                        aName = ATTACHMENT_NAME.split("\\*");
                    }
                    
                    StringBuffer url = request.getRequestURL();
                    String localhostUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
                    for (int i = 0; i < aName.length; i++) {
                        DOWN_FILE_URL.add(localhostUrl + "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
                                + aName[i]+"&attachmentId="
                                + aId[i]  +"&module=notify");
                    }
                }
                
                FORMAT = rs.getString("FORMAT");
                String READERS = rs.getString("READERS");
                SUBJECT = tagsFilter(SUBJECT);
                String TYPE_ID = rs.getString("TYPE_ID");
                String TYPE_NAME = T9MobileUtility.get_code_name(conn, TYPE_ID, "NOTIFY");
                String BEGIN_DATE = sdf.format(rs.getTimestamp("BEGIN_DATE"));
                String SEND_TIME = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
                String query1 = "SELECT USER_NAME from PERSON where SEQ_ID='" + FROM_ID + "'";
                Map<String, String> data = T9QuickQuery.quickQuery(conn, query1);
                String FROM_NAME = "";
                if (data != null)
                    FROM_NAME = data.get("USER_NAME");
                else
                    FROM_NAME = FROM_ID;
                if ("2".equals(FORMAT))
                    CONTENT = "<a href='" + CONTENT + "'>" + CONTENT + "</a>";
                if (!find_id(READERS, person.getSeqId() + "")) {
                    READERS += person.getSeqId() + ",";
                    query = "update NOTIFY set READERS='" + READERS + "' where SEQ_ID='" + NOTIFY_ID + "'";
                    T9QuickQuery.update(conn, query);

                    query = "insert into APP_LOG (USER_ID,TIME,MODULE,OPP_ID,TYPE) values ('"
                            + person.getSeqId() + "',?,'4','" + NOTIFY_ID + "','1')";
                    PreparedStatement ps = null;
                    try {
                        ps = conn.prepareStatement(query);
                        ps.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
                        ps.executeUpdate();
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(ps, null, null);
                    }
                }
                int has_attachment = 0;
                if (!T9Utility.isNullorEmpty(ATTACHMENT_ID) && !T9Utility.isNullorEmpty(ATTACHMENT_NAME))
                    has_attachment = 1;
                else
                    has_attachment = 0;

                if ("2".equals(FORMAT)) {
                    resultMap.put("format", 2);
                    resultMap.put("content", T9Utility.encodeSpecial(T9Utility.null2Empty(CONTENT)));
                } else {
                    resultMap.put("q_id", NOTIFY_ID);
                    resultMap.put("type_name", TYPE_NAME);
                    resultMap.put("from_name", T9Utility.encodeSpecial(T9Utility.null2Empty(FROM_NAME)));
                    resultMap.put("send_time", SEND_TIME);
                    resultMap.put("subject", T9Utility.encodeSpecial(T9Utility.null2Empty(SUBJECT)));
                    resultMap.put("subject_color", SUBJECT_COLOR);
                    resultMap.put("begin_date", BEGIN_DATE);
                    resultMap.put("content", T9Utility.encodeSpecial(T9Utility.null2Empty(CONTENT)));
                    List<Map<String, String>> downFile = new ArrayList<Map<String,String>>(); // 下载附件
                    for (int i = 0; i < DOWN_FILE_URL.size(); i++) {
                        Map <String,String> fileUrl = new HashMap();
                        fileUrl.put("attachmentName", aName[i]); // 附件名
                        fileUrl.put("fileUrl", DOWN_FILE_URL.get(i)); // 附件url
                        downFile.add(fileUrl);
                    }
                    resultMap.put("down_file", downFile);
                    resultMap.put("has_attachment", has_attachment); // 是否有附件：1：有附件 0:没有附件
                    resultList.add(resultMap);
                }
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(resultList)));
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
    }

    public void refreshList(HttpServletRequest request, HttpServletResponse response, Connection conn,
            T9Person person, String query) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map resultMap = new HashMap();
                String READERS = rs.getString("READERS");
                String NOTIFY_ID = rs.getString("SEQ_ID");
                String SUBJECT_COLOR = rs.getString("SUBJECT_FONT");
                String FROM_ID = rs.getString("FROM_ID");
                String SUBJECT = rs.getString("SUBJECT");
                String TOP = rs.getString("TOP");
                String TYPE_ID = rs.getString("TYPE_ID");
                String ATTACHMENT_ID = rs.getString("ATTACHMENT_ID");
                String ATTACHMENT_NAME = rs.getString("ATTACHMENT_NAME");
                
                String aId[] = null;
                String aName[] = null;
                List<String> DOWN_FILE_URL = new ArrayList();
                if(!"".equals(ATTACHMENT_ID) && !"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_ID && null != ATTACHMENT_NAME){
                    if(!"".equals(ATTACHMENT_ID) && null != ATTACHMENT_ID){
                        ATTACHMENT_ID = ATTACHMENT_ID.substring(0, ATTACHMENT_ID.length()-1);
                        aId = ATTACHMENT_ID.split(",");
                    }
                    if(!"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_NAME){
                        ATTACHMENT_NAME = ATTACHMENT_NAME.substring(0,ATTACHMENT_NAME.length()-1);
                        aName = ATTACHMENT_NAME.split("\\*");
                    }
                    
                    StringBuffer url = request.getRequestURL();
                    String localhostUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
                    for (int i = 0; i < aName.length; i++) {
                        DOWN_FILE_URL.add(localhostUrl + "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
                                + aName[i]+"&attachmentId="
                                + aId[i]  +"&module=notify");
                    }
                }
                
                SUBJECT = SUBJECT.replace("<", "&lt");
                SUBJECT = SUBJECT.replace(">", "&gt");

                Timestamp BEGIN_DATE = rs.getTimestamp("BEGIN_DATE");

                Map<String, String> rcd = T9QuickQuery.quickQuery(conn,
                        "select class_desc from code_item where seq_id='" + TYPE_ID + "'");
                String TYPE_NAME = T9Utility.null2Empty(rcd.get("class_desc"));

                if (!"".equals(TYPE_NAME))
                    SUBJECT = "[" + TYPE_NAME + "]" + SUBJECT;

                String query1 = "SELECT USER_NAME from PERSON where SEQ_ID='" + FROM_ID + "'";
                Map<String, String> data1 = T9QuickQuery.quickQuery(conn, query1);
                String FROM_NAME = "";
                if (data1 != null)
                    FROM_NAME = data1.get("USER_NAME");
                else
                    FROM_NAME = FROM_ID;

                // lp 附件判断
                int has_attachment = 0;
                if (!T9Utility.isNullorEmpty(ATTACHMENT_ID) && !T9Utility.isNullorEmpty(ATTACHMENT_NAME))
                    has_attachment = 1;
                else
                    has_attachment = 0;

                int read_flag = 0;
                if (!T9WorkFlowUtility.findId(READERS, person.getSeqId() + "")) {
                    read_flag = 0;
                } else {
                    read_flag = 1;
                }

                resultMap.put("q_id", NOTIFY_ID); // 唯一标识
                resultMap.put("read_flag", read_flag); // 阅读标记
                resultMap.put("subject", T9Utility.encodeSpecial(T9Utility.null2Empty(SUBJECT))); // 标题
                resultMap.put("subject_color", SUBJECT_COLOR); // 标题颜色
                resultMap.put("top", T9Utility.encodeSpecial(TOP)); // 是否置顶
                resultMap.put("send_time", T9Utility.getDateTimeStr(BEGIN_DATE)); // 发布时间
                resultMap.put("from_name", T9Utility.encodeSpecial(FROM_NAME)); // 发布人
                resultMap.put("attachment_id", T9Utility.encodeSpecial(ATTACHMENT_ID)); // 附件Id
                List<Map<String, String>> downFile = new ArrayList<Map<String,String>>(); // 下载附件
                for (int i = 0; i < DOWN_FILE_URL.size(); i++) {
                    Map <String,String> fileUrl = new HashMap();
                    fileUrl.put("attachmentName", aName[i]); // 附件名
                    fileUrl.put("fileUrl", DOWN_FILE_URL.get(i)); // 附件url
                    downFile.add(fileUrl);
                }
                resultMap.put("down_file", downFile);
                resultMap.put("has_attachment", has_attachment); // 是否有附件：1：有附件 0:没有附件
                resultList.add(resultMap);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {

            T9DBUtility.close(stmt, rs, null);
        }

        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(resultList)));
    }

    public void refreshList(HttpServletRequest request, HttpServletResponse response, Connection conn,
            T9Person person, String query, String CURRITERMS) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();

        int c = T9MobileUtility.getCURRITERMS(CURRITERMS);
        int j = 0;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map resultMap = new HashMap();
                if (j < c) {
                    j++;
                    continue;
                }
                if (j >= T9MobileConfig.PAGE_SIZE + c)
                    break;

                String READERS = rs.getString("READERS");
                String NOTIFY_ID = rs.getString("SEQ_ID");
                String FROM_ID = rs.getString("FROM_ID");
                String SUBJECT = rs.getString("SUBJECT");
                String TOP = rs.getString("TOP");
                String TYPE_ID = rs.getString("TYPE_ID");
                String ATTACHMENT_ID = rs.getString("ATTACHMENT_ID");
                String ATTACHMENT_NAME = rs.getString("ATTACHMENT_NAME");
                
                String aId[] = null;
                String aName[] = null;
                List<String> DOWN_FILE_URL = new ArrayList();
                if(!"".equals(ATTACHMENT_ID) && !"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_ID && null != ATTACHMENT_NAME){
                    if(!"".equals(ATTACHMENT_ID) && null != ATTACHMENT_ID){
                        ATTACHMENT_ID = ATTACHMENT_ID.substring(0, ATTACHMENT_ID.length()-1);
                        aId = ATTACHMENT_ID.split(",");
                    }
                    if(!"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_NAME){
                        ATTACHMENT_NAME = ATTACHMENT_NAME.substring(0,ATTACHMENT_NAME.length()-1);
                        aName = ATTACHMENT_NAME.split("\\*");
                    }
                    
                    StringBuffer url = request.getRequestURL();
                    String localhostUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
                    for (int i = 0; i < aName.length; i++) {
                        DOWN_FILE_URL.add(localhostUrl + "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
                                + aName[i]+"&attachmentId="
                                + aId[i]  +"&module=notify");
                    }
                }
                
                SUBJECT = SUBJECT.replace("<", "&lt");
                SUBJECT = SUBJECT.replace(">", "&gt");

                Timestamp BEGIN_DATE = rs.getTimestamp("BEGIN_DATE");

                Map<String, String> rcd = T9QuickQuery.quickQuery(conn,
                        "select class_desc from code_item where seq_id='" + TYPE_ID + "'");
                String TYPE_NAME = T9Utility.null2Empty(rcd.get("class_desc"));

                if (!"".equals(TYPE_NAME))
                    SUBJECT = "[" + TYPE_NAME + "]" + SUBJECT;

                String query1 = "SELECT USER_NAME from PERSON where SEQ_ID='" + FROM_ID + "'";
                Map<String, String> data1 = T9QuickQuery.quickQuery(conn, query1);
                String FROM_NAME = "";
                if (data1 != null)
                    FROM_NAME = data1.get("USER_NAME");
                else
                    FROM_NAME = FROM_ID;

                // lp 附件判断
                int has_attachment = 0;
                if (!T9Utility.isNullorEmpty(ATTACHMENT_ID) && !T9Utility.isNullorEmpty(ATTACHMENT_NAME))
                    has_attachment = 1;
                else
                    has_attachment = 0;

                int read_flag = 0;
                if (!T9WorkFlowUtility.findId(READERS, person.getSeqId() + "")) {
                    read_flag = 0;
                } else {
                    read_flag = 1;
                }

                resultMap.put("q_id", NOTIFY_ID); // 唯一标识
                resultMap.put("read_flag", read_flag); // 阅读标记
                resultMap.put("subject", T9Utility.encodeSpecial(T9Utility.null2Empty(SUBJECT))); // 标题
                resultMap.put("subject_color", ""); // 标题颜色
                resultMap.put("top", T9Utility.encodeSpecial(TOP)); // 是否置顶
                resultMap.put("send_time", T9Utility.getDateTimeStr(BEGIN_DATE)); // 发布时间
                resultMap.put("from_name", T9Utility.encodeSpecial(FROM_NAME)); // 发布人
                resultMap.put("attachment_id", T9Utility.encodeSpecial(ATTACHMENT_ID)); // 附件Id
                List<Map<String, String>> downFile = new ArrayList<Map<String,String>>(); // 下载附件
                for (int i = 0; i < DOWN_FILE_URL.size(); i++) {
                    Map <String,String> fileUrl = new HashMap();
                    fileUrl.put("attachmentName", aName[i]); // 附件名
                    fileUrl.put("fileUrl", DOWN_FILE_URL.get(i)); // 附件url
                    downFile.add(fileUrl);
                }
                resultMap.put("down_file", downFile);
                resultMap.put("has_attachment", has_attachment); // 是否有附件：1：有附件 0:没有附件
                resultList.add(resultMap);
                j++;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {

            T9DBUtility.close(stmt, rs, null);
        }
        
        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(resultList)));
    }

    public String read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String NOTIFY_ID = request.getParameter("NOTIFY_ID");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String CUR_DATE = sdf.format(new Date()) + " 00:00:00";

            String query = "SELECT * from NOTIFY where SEQ_ID='" + NOTIFY_ID + "'";
            // +"' and (TO_ID='ALL_DEPT' or TO_ID='0' or "
            // +T9DBUtility.findInSet(person.getDeptId()+"", "TO_ID")
            // +" or "+T9DBUtility.findInSet(person.getUserPriv()+"", "PRIV_ID")
            // +" or "+T9DBUtility.findInSet(person.getSeqId()+"", "USER_ID")
            // +") and "+T9DBUtility.getDateFilter("begin_date", CUR_DATE,
            // "<=")+" and ("+T9DBUtility.getDateFilter("end_date", CUR_DATE,
            // ">=")+" or end_date is null) and PUBLISH='1'";

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    String FROM_ID = rs.getString("FROM_ID");
                    String SUBJECT_COLOR = rs.getString("SUBJECT_FONT");
                    String SUBJECT = T9Utility.null2Empty(rs.getString("SUBJECT"));
                    String FORMAT = T9Utility.null2Empty(rs.getString("FORMAT"));
                    String CONTENT = T9Utility.null2Empty(rs.getString("CONTENT"));
                    String COMPRESS_CONTENT = T9Utility.null2Empty(rs.getString("COMPRESS_CONTENT"));
                    if (!"".equals(COMPRESS_CONTENT) && "2".equals(FORMAT))
                        CONTENT = COMPRESS_CONTENT;
                    else
                        CONTENT = rs.getString("CONTENT");

                    String ATTACHMENT_ID = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
                    String ATTACHMENT_NAME = T9Utility.null2Empty(rs.getString("ATTACHMENT_NAME"));
                    FORMAT = T9Utility.null2Empty(rs.getString("FORMAT"));
                    String READERS = T9Utility.null2Empty(rs.getString("READERS"));
                    String TYPE_ID = T9Utility.null2Empty(rs.getString("TYPE_ID"));
                    Map<String, String> rcd = T9QuickQuery.quickQuery(conn,
                            "select class_desc from code_item where seq_id='" + TYPE_ID + "'");
                    String TYPE_NAME = T9Utility.null2Empty(rcd.get("class_desc"));

                    if (!"".equals(TYPE_NAME))
                        SUBJECT = "[" + TYPE_NAME + "]" + SUBJECT;

                    String BEGIN_DATE = sdf.format(rs.getTimestamp("BEGIN_DATE"));
                    String SEND_TIME = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));

                    String query1 = "SELECT USER_NAME from PERSON where SEQ_ID='" + FROM_ID + "'";
                    Map<String, String> data = T9QuickQuery.quickQuery(conn, query1);
                    String FROM_NAME = "";
                    if (data != null)
                        FROM_NAME = data.get("USER_NAME");
                    else
                        FROM_NAME = FROM_ID;

                    if ("2".equals(FORMAT))
                        CONTENT = "<a href='" + CONTENT + "'>" + CONTENT + "</a>";

                    if (!find_id(READERS, person.getSeqId() + "")) {
                        READERS += person.getSeqId() + ",";
                        query = "update NOTIFY set READERS='" + READERS + "' where SEQ_ID='" + NOTIFY_ID
                                + "'";
                        T9QuickQuery.update(conn, query);

                        query = "insert into APP_LOG (USER_ID,TIME,MODULE,OPP_ID,TYPE) values ('"
                                + person.getSeqId() + "',?,'4','" + NOTIFY_ID + "','1')";
                        PreparedStatement ps = null;
                        try {
                            ps = conn.prepareStatement(query);
                            ps.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
                            ps.executeUpdate();
                        } catch (Exception ex) {
                            throw ex;
                        } finally {
                            T9DBUtility.close(ps, null, null);
                        }
                    }

                    Map m = new HashMap();
                    m.put("SUBJECT", SUBJECT);
                    m.put("TYPE_NAME", TYPE_NAME);
                    m.put("FROM_NAME", FROM_NAME);
                    m.put("SEND_TIME", SEND_TIME);
                    m.put("BEGIN_DATE", BEGIN_DATE);
                    m.put("CONTENT", CONTENT);
                    m.put("ATTACHMENT_ID", ATTACHMENT_ID);
                    m.put("ATTACHMENT_NAME", ATTACHMENT_NAME);
                    request.setAttribute("m", m);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stmt, rs, null);
            }
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/notify/read.jsp;JSESSIONID=" + sid;
    }

    private String fetch_other_sql(String value, String field) throws Exception {
        value = T9Utility.null2Empty(value);
        String sp[] = value.split(",");
        String sql = "";
        for (int i = 0; i < sp.length; i++) {
            sql += T9DBUtility.findInSet(sp[i], field);
            if (i != sp.length - 1) {
                sql += " or ";
            }
        }
        return sql;
    }

    private boolean find_id(String s, String t) {
        s = T9Utility.null2Empty(s);
        String sp[] = s.split(",");
        for (String id : sp) {
            if (id.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public String tagsFilter(String str) {
        return str == null ? "" : str.replaceAll("<[/]?[a-z A-Z]+[\\s\\w\\{Punct}'=:;\"#? \u4e00-\u9fa5]+>",
                "");
    }

}
