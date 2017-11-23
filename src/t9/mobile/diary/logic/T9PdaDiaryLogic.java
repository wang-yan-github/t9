package t9.mobile.diary.logic;

import java.net.URLDecoder;
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
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaDiaryLogic {

    public List<Map<String,String>> refreshList(Connection conn , HttpServletRequest request, T9Person person ,String query , boolean flag , String CURRITERMS) throws Exception {
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
        List<Map<String, String>> list = null;
	
        if (flag) {
            list = T9QuickQuery.quickQueryList(conn, query, CURRITERMS);
        } else {
            list = T9QuickQuery.quickQueryList(conn, query);
        }
        for (Map<String, String> data : list) {
            String DIA_ID = data.get("SEQ_ID");
            
            String CONTENT = T9Utility.encodeSpecial(data.get("CONTENT"));
            CONTENT = CONTENT.replace("<", "&lt");
            CONTENT = CONTENT.replace(">", "&gt");
            
            String SUBJECT = T9Utility.encodeSpecial(data.get("SUBJECT"));
            if ("".equals(SUBJECT))
                SUBJECT = "无标题";
            
            String DIA_TYPE = T9Utility.encodeSpecial(data.get("DIA_TYPE"));
            if ("1".equals(DIA_TYPE)) {
                DIA_TYPE = "工作日志";
            } else {
                DIA_TYPE = "个人日志";
            }
            
            String COMPRESS_CONTENT = T9Utility.encodeSpecial(data.get("COMPRESS_CONTENT"));
            int editable = 0;
            if (CONTENT.equals(COMPRESS_CONTENT))
                editable = 1;

            String ATTACHMENT_ID = data.get("ATTACHMENT_ID");
            String ATTACHMENT_NAME = data.get("ATTACHMENT_NAME");
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
                            + aId[i]  +"&module=diary");
                }
            }

            int has_attachment = 0;
            if (!T9Utility.isNullorEmpty(ATTACHMENT_ID) && !T9Utility.isNullorEmpty(ATTACHMENT_NAME))
                has_attachment = 1;
            else
                has_attachment = 0;
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String DIA_DATE  = data.get("DIA_DATE");
            String DIA_TIME =  data.get("DIA_TIME");
            Date DD = sdf.parse(DIA_DATE);
            Date DT = sdf2.parse(DIA_TIME);

            Map resultMap = new HashMap();
            resultMap.put("q_id", DIA_ID); // 唯一标识
            resultMap.put("subject", SUBJECT); // 日志标题
            resultMap.put("content", CONTENT); // 日志内容
            resultMap.put("dia_type_desc",  DIA_TYPE); // 日志类型说明
            resultMap.put("dia_type", DIA_TYPE); // 日志类型（个人日志、工作日志）
            resultMap.put("dia_date", sdf.format(DD)); // 日志日期
            resultMap.put("dia_time", sdf2.format(DT)); // 日志创建时间
            resultMap.put("editable", editable); // 是否已经保存不带html标签的正文内容
            resultMap.put("attachment_id", T9Utility.encodeSpecial(ATTACHMENT_ID)); // 附件id
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
		return resultList;
    }
	
    public List<Map<String,String>> getDiaryContent(HttpServletRequest request, Connection conn) throws Exception {
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
        String DIA_ID = request.getParameter("Q_ID");
        String query = "SELECT * from DIARY where SEQ_ID='" + DIA_ID + "'";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                String CONTENT = T9Utility.encodeSpecial(rs.getString("CONTENT"));
                String COMPRESS_CONTENT = T9Utility.encodeSpecial(rs.getString("COMPRESS_CONTENT"));

                String SUBJECT = T9Utility.encodeSpecial(rs.getString("SUBJECT"));

                Timestamp DIA_DATE = (Timestamp) rs.getTimestamp("DIA_DATE");
                Timestamp DIA_TIME = (Timestamp) rs.getTimestamp("DIA_TIME");

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String DIA_TYPE = rs.getString("DIA_TYPE");
                if ("1".equals(DIA_TYPE)) {
                    DIA_TYPE = "工作日志";
                } else {
                    DIA_TYPE = "个人日志";
                }

                CONTENT = T9Utility.isNullorEmpty(COMPRESS_CONTENT) ? T9Utility.encodeSpecial(CONTENT)
                        : T9Utility.encodeSpecial(COMPRESS_CONTENT);

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
                                + aId[i]  +"&module=diary");
                    }
                }

                if ("".equals(SUBJECT))
                    SUBJECT = "无标题";

                int has_attachment = 0;
                if (!T9Utility.isNullorEmpty(ATTACHMENT_ID) && !T9Utility.isNullorEmpty(ATTACHMENT_NAME))
                    has_attachment = 1;
                else
                    has_attachment = 0;

                int editable = 0;
                if (CONTENT.equals(COMPRESS_CONTENT))
                    editable = 1;

                Map resultMap = new HashMap();
                resultMap.put("q_id", DIA_ID); // 唯一标识
                resultMap.put("subject", SUBJECT); // 日志标题
                resultMap.put("content", CONTENT); // 日志内容
                resultMap.put("dia_type_desc",  DIA_TYPE); // 日志类型说明
                resultMap.put("dia_type", DIA_TYPE); // 日志类型（个人日志、工作日志）
                resultMap.put("dia_date", sdf.format(DIA_DATE)); // 日志日期
                resultMap.put("dia_time", sdf2.format(DIA_TIME)); // 日志创建时间
                resultMap.put("editable", editable); // 是否已经保存不带html标签的正文内容
                resultMap.put("attachment_id", T9Utility.encodeSpecial(ATTACHMENT_ID)); // 附件id
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
        return resultList;
    }

    public String submit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9FileUploadForm fileForm = new T9FileUploadForm();

            String DIA_TYPE = "";
            String DIA_DATE = "";
            String SUBJECT = "";
            String CONTENT = "";
            String q_id = "";
            String ATTACHMENT_ID = "";
            String ATTACHMENT_NAME = "";

            Date CUR_TIME = new Date();
            Timestamp t = new Timestamp(CUR_TIME.getTime());
            // 注意这里的
            try {
                fileForm.parseUploadRequest(request);
                DIA_TYPE = T9Utility
                        .null2Empty(URLDecoder.decode(fileForm.getParameter("dia_type"), "UTF-8"));
                DIA_DATE = T9Utility
                        .null2Empty(URLDecoder.decode(fileForm.getParameter("dia_date"), "UTF-8"));
                SUBJECT = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("subject"), "UTF-8"));
                CONTENT = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("content"), "UTF-8"));
                q_id = T9Utility.null2Empty(fileForm.getParameter("q_id"));
                if ("".equals(q_id) || q_id == null) {
                    Map<String, String> ATTACHMENTS = T9MobileUtility.uploadAttachment(fileForm, "diary", "",
                            "");
                    ATTACHMENT_ID = T9Utility.null2Empty(ATTACHMENTS.get("id"));
                    ATTACHMENT_NAME = T9Utility.null2Empty(ATTACHMENTS.get("name"));
                } else {
                    String attachment_id_old = T9Utility.null2Empty(fileForm
                            .getParameter("attachment_id_old"));
                    String attachment_name_old = T9Utility.null2Empty(fileForm
                            .getParameter("attachment_name_old"));
                    if (!T9Utility.isNullorEmpty(attachment_id_old)
                            && !T9Utility.isNullorEmpty(attachment_name_old)) {
                        attachment_id_old = URLDecoder.decode(attachment_id_old, "UTF-8");
                        attachment_name_old = URLDecoder.decode(attachment_name_old, "UTF-8");
                    }
                    Map<String, String> ATTACHMENTS = T9MobileUtility.uploadAttachment(fileForm, "diary",
                            attachment_id_old, attachment_name_old);
                    ATTACHMENT_ID = ATTACHMENTS.get("id");
                    ATTACHMENT_NAME = ATTACHMENTS.get("name");
                }
            } catch (Exception ex) {
                DIA_TYPE = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("dia_type"), "UTF-8"));
                DIA_DATE = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("dia_date"), "UTF-8"));
                SUBJECT = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("subject"), "UTF-8"));
                CONTENT = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("content"), "UTF-8"));
                q_id = T9Utility.null2Empty(request.getParameter("q_id"));
                if ("".equals(q_id) || q_id == null) {
                } else {
                    ATTACHMENT_ID = T9Utility.null2Empty(request.getParameter("attachment_id_old"));
                    ATTACHMENT_NAME = T9Utility.null2Empty(request.getParameter("attachment_name_old"));
                }

            }

            if ("工作日志".equals(DIA_TYPE) || "".equals(DIA_TYPE)) {
                DIA_TYPE = "1";
            }
            if ("个人日志".equals(DIA_TYPE)) {
                DIA_TYPE = "2";
            }

            String NOTAGS_CONTENT = CONTENT;
            String COMPRESS_CONTENT = T9DiaryUtil.cutHtml(CONTENT);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dd = sdf.parse(DIA_DATE + " 00:00:00");

            String query = "";

            if ("".equals(q_id) || q_id == null) {
                if ("".equals(CONTENT)) {
                    //T9MobileUtility.output(response, "请填写日志内容");
                	T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "请填写日志内容", null));
                    return null;
                }

                query = "insert into DIARY (USER_ID,DIA_DATE,DIA_TIME,DIA_TYPE,SUBJECT,CONTENT,ATTACHMENT_ID,ATTACHMENT_NAME,COMPRESS_CONTENT) values "
                        + "('" + person.getSeqId() + "',?,?,'" + DIA_TYPE + "',?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setTimestamp(1, new java.sql.Timestamp(dd.getTime()));
                ps.setTimestamp(2, t);
                ps.setString(3, SUBJECT);
                ps.setString(4, NOTAGS_CONTENT);
                ps.setString(5, ATTACHMENT_ID);
                ps.setString(6, ATTACHMENT_NAME);
                ps.setString(7, COMPRESS_CONTENT);

                ps.executeUpdate();
                //T9MobileUtility.output(response, "OK");
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "ok", null));
                return null;
            } else {
                String DIA_ID = q_id;

                // System.out.println(ATTACHMENT_ID + ":" + ATTACHMENT_NAME);
                query = "select USER_ID from DIARY where SEQ_ID='" + DIA_ID + "'";
                Map<String, String> data = T9QuickQuery.quickQuery(conn, query);
                String USER_ID = "";
                if (data != null)
                    USER_ID = data.get("USER_ID");

                if (USER_ID.equals("" + person.getSeqId())) {
                    query = "update DIARY set DIA_TYPE='"
                            + DIA_TYPE
                            + "', CONTENT=?, ATTACHMENT_ID=?, ATTACHMENT_NAME=?, COMPRESS_CONTENT = ?, DIA_DATE=?, SUBJECT=?,  DIA_TIME=? where SEQ_ID='"
                            + DIA_ID + "'";

                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, NOTAGS_CONTENT);
                    ps.setString(2, ATTACHMENT_ID);
                    ps.setString(3, ATTACHMENT_NAME);
                    ps.setString(4, COMPRESS_CONTENT);
                    ps.setTimestamp(5, new java.sql.Timestamp(dd.getTime()));
                    ps.setString(6, SUBJECT);
                    ps.setTimestamp(7, t);

                    ps.executeUpdate();
                    //T9MobileUtility.output(response, "OK");
                    T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "ok", null));
                } else {
                    //T9MobileUtility.output(response, "非法操作");
                	T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "非法操作", null));
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    public String read(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String DIA_ID = request.getParameter("DIA_ID");
            String query = "";
            if (!"".equals(DIA_ID)) {
                query = "SELECT * from DIARY where SEQ_ID='" + DIA_ID + "'";
                Map<String, String> data = T9QuickQuery.quickQuery(conn, query);
                // if(data!=null)
                // {
                // String USER_ID=data.get("USER_ID");
                // String CONTENT=data.get("CONTENT");
                // String SUBJECT=data.get("SUBJECT");
                // DIA_ID=data.get("DIA_ID");
                //
                // String DIA_DATE=data.get("DIA_DATE");
                //
                // String DIA_TIME=data.get("DIA_TIME");
                String DIA_TYPE_DESC = "";
                String DIA_TYPE = data.get("DIA_TYPE");
                if ("1".equals(DIA_TYPE)) {
                    DIA_TYPE_DESC = "工作日志";
                } else {
                    DIA_TYPE_DESC = "个人日志";
                }
                // String DIA_TYPE_DESC=get_code_name($DIA_TYPE,"DIARY_TYPE");
                data.put("DIA_TYPE_DESC", DIA_TYPE_DESC);
                // CONTENT="".equals(data.get("COMPRESS_CONTENT"))? CONTENT :
                // data.get("COMPRESS_CONTENT");

                // String ATTACHMENT_ID = data.get("ATTACHMENT_ID");
                // String ATTACHMENT_NAME = data.get("ATTACHMENT_NAME");
                //
                // if(!"".equals(SUBJECT))
                // SUBJECT = "无标题";

                request.setAttribute("n", data);
                // T9MobileUtility.output(response, sb.toString());
                // }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return "/mobile/diary/read.jsp";
    }

    public String read_share(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String DIA_ID = request.getParameter("DIA_ID");
            String query = "";
            if (!"".equals(DIA_ID)) {
                query = "SELECT * from DIARY where DIA_ID='" + DIA_ID + "' and (TO_ALL = 1 || "
                        + T9DBUtility.findInSet(person.getSeqId() + "", "TO_ID") + " || USER_ID = '"
                        + person.getSeqId() + "')";
                Map<String, String> data = T9QuickQuery.quickQuery(conn, query);
                if (data != null) {
                    String USER_ID = data.get("USER_ID");
                    String CONTENT = data.get("CONTENT");
                    String SUBJECT = data.get("SUBJECT");
                    DIA_ID = data.get("DIA_ID");

                    String DIA_DATE = data.get("DIA_DATE");

                    String DIA_TIME = data.get("DIA_TIME");
                    String DIA_TYPE = data.get("DIA_TYPE");
                    // String
                    // DIA_TYPE_DESC=get_code_name($DIA_TYPE,"DIARY_TYPE");
                    String DIA_TYPE_DESC = "";

                    CONTENT = "".equals(data.get("COMPRESS_CONTENT")) ? CONTENT : data
                            .get("COMPRESS_CONTENT");

                    String ATTACHMENT_ID = data.get("ATTACHMENT_ID");
                    String ATTACHMENT_NAME = data.get("ATTACHMENT_NAME");

                    if (!"".equals(SUBJECT))
                        SUBJECT = "无标题";

                    StringBuffer sb = new StringBuffer();
                    sb.append("<div class='container'>");
                    sb.append("<h3 class=‘read_title fix_read_title’><strong>" + SUBJECT + "</strong></h3>");
                    sb.append("<div class='read_detail fix_read_detail' style='text-align:left;'><span class='grapc'>类型：</span>"
                            + DIA_TYPE_DESC + "</div>");
                    sb.append("<div class='read_detail fix_read_detail' style='text-align:left;'><span class='grapc'>日志时间</span>"
                            + DIA_DATE + "</div>");
                    sb.append("<div class='read_detail fix_read_detail' style='text-align:left;'><span class='grapc'>最后修改时间：</span>"
                            + DIA_TIME + "</div>");
                    sb.append("<div class='read_content'>" + CONTENT + "</div>");
                    // <?
                    // if($ATTACHMENT_ID != "" && $ATTACHMENT_NAME != "")
                    // {
                    // ?>
                    // <div
                    // class="read_attach"><?=attach_link_pda($ATTACHMENT_ID,$ATTACHMENT_NAME,$P,'diary',1,1,1)?></div>
                    // <?
                    // }
                    // ?>
                    sb.append("<input id='SHOW_DIA_ID' type='hidden' value='" + DIA_ID + "' />");
                    sb.append("</div>");
                    // <?
                    // if($ROW["CONTENT"] ==
                    // @gzuncompress($ROW["COMPRESS_CONTENT"]))
                    // {
                    // ?>
                    // <script>$(".editDiary").show();</script>
                    // <? } ?>
                    //T9MobileUtility.output(response, sb.toString());
                    T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, sb.toString()));
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    public String tagsFilter(String str) {
        return str == null ? "" : str.replaceAll("<[/]?[a-z A-Z]+[\\s\\w\\{Punct}'=:;\"#? \u4e00-\u9fa5]+>",
                "");
    }

}
