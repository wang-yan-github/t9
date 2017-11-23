package t9.mobile.diary.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import t9.mobile.diary.logic.T9PdaDiaryLogic;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaDiaryAct {
    
    /**
     * 工作日志列表/详情接口
     * @param request
           .列表
           ATYPE           refreshList：列表
           A               loadList：所有数据列表
                           ,与CURRITERMS参数配合使用getNew：ID大于LATEST_ID的数据,CURRITERMS参数无效
                           ,空：默认显示，只显示最新7条数据，与CURRITERMS参数配合使用，最多显示7条
           LATEST_ID       当前数据的ID	大于此ID的数据全部显示
           CURRITERMS      从最新数据开始，向前推移数（可与分页联合使用）

           .详情
           ATYPE           getDiaryContent：单条数据
           Q_ID
     * @param response
           .列表/详情
           q_id             唯一标识
           subject          日志标题
           content          日志内容
           dia_type_desc    日志类型说明
           dia_type         日志类型（个人日志、工作日志）
           dia_date         日志日期
           dia_time         日志创建时间
           editable         是否已经保存不带html标签的正文内容
           attachment_id    附件id
           attachment_name  附件名		有附件时才显示
           file_url         下载附件url	有附件时才显示
           has_attachment   是否有附件：1：有附件 0:没有附件
     * @return
           Json
     * @throws Exception
     */
    public String data(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            String A = request.getParameter("A");
            String ATYPE = request.getParameter("ATYPE");
            String CURRITERMS = request.getParameter("CURRITERMS");

            String query = "";
            List<Map<String,String>> jsonData;

            boolean needflag = true;
            if ("refreshList".equals(ATYPE)) {

                if ("loadList".equals(A)) {
                    query = "SELECT * from DIARY where USER_ID='" + person.getSeqId()
                            + "' order by SEQ_ID desc ";
                } else if ("getNew".equals(A)) {
                    String LATEST_ID = request.getParameter("LATEST_ID");
                    String new_count = "SELECT count(*) from DIARY where USER_ID='" + person.getSeqId()
                            + "' and SEQ_ID > " + LATEST_ID;
                    int count = T9QuickQuery.getCount(conn, new_count);
                    if (count == 0) {
                        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "没有数据", null));
                        return null;
                    } else {
                        needflag = false;
                        query = "SELECT * from DIARY where USER_ID='" + person.getSeqId() + "' AND SEQ_ID > "
                                + LATEST_ID + " order by SEQ_ID desc";
                    }
                } else {
                    query = "SELECT * from DIARY where USER_ID='" + person.getSeqId()
                            + "' order by SEQ_ID desc ";
                    int count = T9QuickQuery.getCountByCursor(conn, query, CURRITERMS);
                    if (count == 0) {
                    	T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "没有数据", null));
                        return null;
                    }
                }
                
                T9PdaDiaryLogic logic = new T9PdaDiaryLogic();
                jsonData = logic.refreshList(conn, request, person, query, needflag, CURRITERMS);
                
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(jsonData)));
                return null;
            } else if ("getDiaryContent".equals(ATYPE))
            {
            	T9PdaDiaryLogic logic = new T9PdaDiaryLogic();
                jsonData = logic.getDiaryContent(request, conn);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(jsonData)));
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
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
