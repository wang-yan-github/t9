package t9.mobile.email.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.email.data.T9EmailCont;
import t9.core.funcs.email.data.T9Webmail;
import t9.core.funcs.email.data.T9WebmailBody;
import t9.core.funcs.email.logic.T9WebmailLogic;
import t9.core.funcs.email.util.T9MailSmtpUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.mobile.email.logic.T9PdaEmailLogic;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

/**
 * 邮件常用类 这个写完了 但是还有个小问题 需要问下
 * 
 * @author shenhua
 * 
 */
public class T9PdaEmailAct {

    /**
     * 地址信息类
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String getEmailList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            dbConn = requestDbConn.getSysDbConn();

            String ATYPE = request.getParameter("ATYPE");
            String A = request.getParameter("A");
            //String data = "";
            List data;
            String sql = "";
            T9PdaEmailLogic tel = new T9PdaEmailLogic();

            /**
             * 先判断 ATYPE 若为refreshList 标示动作 获取数据 然后再根据 A参数 判断 是全部取出 还是取更新的 或者取
             * 指定的记录数
             */
            if ("refreshList".equals(ATYPE)) {
                // 这是第一个接口 refreshList
                // ============================ 显示根目录下目录
                // =======================================
                if ("loadList".equals(A)) {
                    // String PAGE_SIZE = request.getParameter("PAGE_SIZE");
                    // PAGE_SIZE = T9MobileString.showEmpty(PAGE_SIZE, "0");
                    //
                    // String LOGIN_USER_ID =
                    // request.getParameter("LOGIN_USER_ID");
                    // LOGIN_USER_ID = T9MobileString.showObjNull(LOGIN_USER_ID,
                    // "10");
                    sql = "SELECT EMAIL.SEQ_ID as EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,SEND_TIME,CONTENT,IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,PERSON.USER_NAME,RECV_FROM,RECV_FROM_NAME,RECV_TO,IS_WEBMAIL from EMAIL,EMAIL_BODY left join PERSON on EMAIL_BODY.FROM_ID=PERSON.SEQ_ID where EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and EMAIL.BOX_ID=0 and EMAIL.TO_ID="
                            + person.getSeqId()
                            + " and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') order by SEND_TIME desc ";
                    data = tel.getEmailList(dbConn, sql, "");
                    if (data != null || data.size() > 3) {
                        //T9MobileUtility.output(response, data);
                        //T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, data));
                    	T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                        return null;
                    }
                } else if ("getNew".equals(A)) {
                    String LATEST_ID = request.getParameter("LATEST_ID");
                    LATEST_ID = T9MobileString.showEmpty(LATEST_ID, "0");
                    String _sql1 = "SELECT count(*) from EMAIL,EMAIL_BODY left join PERSON on EMAIL_BODY.FROM_ID=PERSON.SEQ_ID where EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and EMAIL.BOX_ID=0 and EMAIL.TO_ID='"
                            + person.getSeqId()
                            + "' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') AND EMAIL.SEQ_ID > "
                            + LATEST_ID;
                    int count = T9MobileUtility.resultCount(dbConn, _sql1);
                    if (count == 0) {
                        //T9MobileUtility.output(response, "NONEWDATA");
                    	T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "NONEWDATA", null));
                        return null;
                    } else {
                        StringBuffer _sb = new StringBuffer();
                        _sb.append("SELECT EMAIL.SEQ_ID as EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,SEND_TIME ,IMPORTANT,");
                        _sb.append("ATTACHMENT_ID,ATTACHMENT_NAME,PERSON.USER_NAME,RECV_FROM,RECV_FROM_NAME,RECV_TO,");
                        _sb.append("IS_WEBMAIL,EMAIL_BODY.CONTENT from EMAIL,EMAIL_BODY left join PERSON on EMAIL_BODY.FROM_ID=PERSON.SEQ_ID where ");
                        _sb.append("EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and EMAIL.BOX_ID=0 and EMAIL.TO_ID='"
                                + person.getSeqId() + "' ");
                        _sb.append("and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') AND EMAIL.SEQ_ID > 0 order by SEND_TIME DESC");
                        sql = _sb.toString();
                    }

                    data = tel.getEmailList(dbConn, sql);
                    if (data != null || data.size() > 3) {
                        //T9MobileUtility.output(response, data);
                        //T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, data));
                        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                        return null;
                    }
                } else {
                    // 最后一个else 前面两种 既不是取列表 也不是获取最新 那么就是当前操作
                    String CURRITERMS = request.getParameter("CURRITERMS");
                    CURRITERMS = T9MobileString.showEmpty(CURRITERMS, "0");

                    // String PAGE_SIZE = request.getParameter("PAGE_SIZE");
                    // PAGE_SIZE = T9MobileString.showEmpty(PAGE_SIZE, "0");

                    StringBuffer _sb = new StringBuffer();
                    _sb.append("SELECT EMAIL.SEQ_ID as EMAIL_ID,FROM_ID,SUBJECT,READ_FLAG,SEND_TIME ,");
                    _sb.append("IMPORTANT,ATTACHMENT_ID,ATTACHMENT_NAME,PERSON.USER_NAME,RECV_FROM,RECV_FROM_NAME,RECV_TO,");
                    _sb.append("IS_WEBMAIL from EMAIL,EMAIL_BODY left join PERSON on EMAIL_BODY.FROM_ID=PERSON.SEQ_ID where ");
                    _sb.append("EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and EMAIL.BOX_ID=0 and EMAIL.TO_ID='"
                            + person.getSeqId() + "' and ");
                    _sb.append("(DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') order by SEND_TIME desc ");
                    sql = _sb.toString();
                    data = tel.getEmailList(dbConn, sql, CURRITERMS);
                    if (data != null || data.size() > 3) {
                        //T9MobileUtility.output(response, data);
                        //T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, data));
                        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                        return null;
                    }

                }

            } else if ("getEmailContent".equals(ATYPE)) {
                /**
                 * 接收邮件内容ID 根据ID获取内容
                 */
                String EMAIL_ID = request.getParameter("Q_ID");
                EMAIL_ID = T9MobileString.showNull(EMAIL_ID, "0");
                StringBuffer sb1 = new StringBuffer();
                sb1.append("SELECT * from EMAIL,EMAIL_BODY where EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and EMAIL.SEQ_ID='"
                        + EMAIL_ID + "' ");
                sb1.append("and EMAIL.TO_ID='" + person.getSeqId()
                        + "' and (EMAIL.DELETE_FLAG='' or EMAIL.DELETE_FLAG='0' or EMAIL.DELETE_FLAG='2')");

                data = tel.getEmailContent(dbConn, sb1.toString(), EMAIL_ID);
                //T9MobileUtility.output(response, data);
                //T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, data));
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * 发送邮件
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String submit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

           // T9FileUploadForm fileForm = new T9FileUploadForm();
            boolean isFileForm = true;

            String TO_ID = request.getParameter("to_id");
            String CS_ID = request.getParameter("cs_id");
            String WEBMAIL = request.getParameter("webmail");
            String CONTENT = request.getParameter("content");
            String SUBJECT = request.getParameter("subject");
            String ATTACHMENT_ID = request.getParameter("attachMent_id");
            String ATTACHMENT_NAME = request.getParameter("attachMent_name");
            
            TO_ID = T9Utility.null2Empty(URLDecoder.decode(TO_ID, "UTF-8"));
            CS_ID = T9Utility.null2Empty(URLDecoder.decode(CS_ID, "UTF-8"));
            WEBMAIL = T9Utility.null2Empty(URLDecoder.decode(WEBMAIL, "UTF-8"));
            CONTENT = T9Utility.null2Empty(URLDecoder.decode(CONTENT, "UTF-8"));
            SUBJECT = T9Utility.null2Empty(URLDecoder.decode(SUBJECT, "UTF-8"));
            ATTACHMENT_ID = T9Utility.null2Empty(URLDecoder.decode(ATTACHMENT_ID, "UTF-8"));
            ATTACHMENT_NAME = T9Utility.null2Empty(URLDecoder.decode(ATTACHMENT_NAME, "UTF-8"));
            // 注意这里的
            /*try {
                fileForm.parseUploadRequest(request);

                TO_ID = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("to_id"), "UTF-8"));
                CS_ID = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("cs_id"), "UTF-8"));
                WEBMAIL = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("webmail"), "UTF-8"));
                CONTENT = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("content"), "UTF-8"));
                SUBJECT = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("subject"), "UTF-8"));
                String attachment_id_old = T9Utility.null2Empty(fileForm.getParameter("attachment_id_old"));
                String attachment_name_old = T9Utility.null2Empty(fileForm
                        .getParameter("attachment_name_old"));
                if (!T9Utility.isNullorEmpty(attachment_id_old)
                        && !T9Utility.isNullorEmpty(attachment_name_old)) {
                    attachment_id_old = URLDecoder.decode(attachment_id_old);
                    attachment_name_old = URLDecoder.decode(attachment_name_old);
                }
                Map<String, String> ATTACHMENTS = T9MobileUtility.uploadAttachment(fileForm, "email",
                        attachment_id_old, attachment_name_old);
                ATTACHMENT_ID = ATTACHMENTS.get("id");
                ATTACHMENT_NAME = ATTACHMENTS.get("name");
            } catch (Exception ex) {
                ex.printStackTrace();
                TO_ID = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("to_id"), "UTF-8"));
                CS_ID = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("cs_id"), "UTF-8"));
                WEBMAIL = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("webmail"), "UTF-8"));
                CONTENT = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("content"), "UTF-8"));
                SUBJECT = T9Utility.null2Empty(URLDecoder.decode(request.getParameter("subject"), "UTF-8"));
                String attachment_id_old = T9Utility.null2Empty(request.getParameter("attachment_id_old"));
                String attachment_name_old = T9Utility
                        .null2Empty(request.getParameter("attachment_name_old"));

                ATTACHMENT_ID = attachment_id_old;
                ATTACHMENT_NAME = attachment_name_old;
            }*/

            Date CUR_TIME = new Date();

            if (!T9MobileString.isEmpty(TO_ID)) {
                TO_ID = T9MobileUtility.getNameBySeqIdStr(TO_ID, conn);
            }
            if (!T9MobileString.isEmpty(CS_ID)) {
                CS_ID = T9MobileUtility.getNameBySeqIdStr(CS_ID, conn);
            }

            String COMPRESS_CONTENT = T9DiaryUtil.cutHtml(CONTENT);

            String _sql = "";

            T9ORM orm = new T9ORM();
            Map m = new HashMap();
            m.put("USER_ID", person.getSeqId());
            String email = "";
            T9Webmail wm = (T9Webmail) orm.loadObjSingle(conn, T9Webmail.class, m);
            if (wm != null) {
                email = wm.getEmail();
            }

            String dbms = T9SysProps.getProp("db.jdbc.dbms");
            if (dbms.startsWith("sqlserver")) {
                _sql = "insert into EMAIL_BODY (FROM_ID,TO_ID,COPY_TO_ID,[SUBJECT],CONTENT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,SEND_FLAG,SMS_REMIND,FROM_WEBMAIL,TO_WEBMAIL,COMPRESS_CONTENT) values (?,?,?,?,?,?,?,?,'1','1',?,?,?)";
            }
            _sql = "insert into EMAIL_BODY (FROM_ID,TO_ID,COPY_TO_ID,SUBJECT,CONTENT,SEND_TIME,ATTACHMENT_ID,ATTACHMENT_NAME,SEND_FLAG,SMS_REMIND,FROM_WEBMAIL,TO_WEBMAIL,COMPRESS_CONTENT) values (?,?,?,?,?,?,?,?,'1','1',?,?,?)";
            PreparedStatement ps = conn.prepareStatement(_sql);
            ps.setInt(1, person.getSeqId());
            ps.setString(2, TO_ID);
            ps.setString(3, CS_ID);
            ps.setString(4, SUBJECT);
            ps.setString(5, CONTENT);
            ps.setTimestamp(6, new java.sql.Timestamp(CUR_TIME.getTime()));
            ps.setString(7, ATTACHMENT_ID);
            ps.setString(8, ATTACHMENT_NAME);
            ps.setString(9, email);
            ps.setString(10, WEBMAIL);
            ps.setString(11, COMPRESS_CONTENT);
            ps.executeUpdate();

            int BODY_ID = T9MobileUtility.getMaxSeqIdCount(conn, "select max(SEQ_ID) from EMAIL_BODY");
            TO_ID += "," + CS_ID + ",";
            String TOK[] = TO_ID.split(",");

            String sms = "请查收我的邮件！主题：" + SUBJECT;
            String ids = "";
            for (int i = 0; i < TOK.length; i++) {
                if (T9MobileString.isEmpty(TOK[i])) {
                    continue;
                }
                ids += TOK[i] + ",";
                String temSql = "";
                temSql = "insert into EMAIL(TO_ID,READ_FLAG,DELETE_FLAG,BODY_ID) values ('" + TOK[i]
                        + "','0','0','" + BODY_ID + "')";
                T9MobileUtility.updateSql(conn, temSql);
                int maxId = T9MobileUtility.getMaxSeqIdCount(conn, "select max(SEQ_ID) from EMAIL");
                String REMIND_URL = "/core/funcs/email/inbox/read_email/index.jsp?seqId=" + maxId;

                T9SmsBack tsb = new T9SmsBack();
                tsb.setContent(sms);
                tsb.setFromId(person.getSeqId());
                tsb.setRemindUrl(REMIND_URL);
                tsb.setSendDate(new Date());
                tsb.setSmsType("2");
                tsb.setToId(TO_ID);
                T9SmsUtil.smsBack(conn, tsb);
            }
            if (ids.endsWith(",")) {
                ids = ids.substring(0, ids.length() - 1);
            }
            T9MsgPusher.mobilePushNotification(ids, sms, "email");

            if (!T9Utility.isNullorEmpty(WEBMAIL) && wm != null) {
                try {
                    this.sendWebMail(conn, BODY_ID, wm, ATTACHMENT_ID, ATTACHMENT_NAME, CONTENT, SUBJECT,
                            WEBMAIL);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            //T9MobileUtility.output(response, "OK");
            T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "ok", null));
            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 发送外部邮件
     * 
     * 
     * @param conn
     * @param eb
     * @param bodyId
     * @throws Exception
     * @throws NumberFormatException
     */
    public void sendWebMail(Connection conn, int bodyId, T9Webmail wm, String aid, String aname,
            String content, String subject, String toWebmail) throws NumberFormatException, Exception {
        T9WebmailBody wmb = new T9WebmailBody();
        T9ORM orm = new T9ORM();
        wmb.setAttachmentId(aid);
        wmb.setAttachmentName(aname);
        wmb.setBodyId(bodyId);
        wmb.setContentHtml(content);
        wmb.setFromMail(wm.getEmail());
        wmb.setIsHtml("1");
        wmb.setSendDate(new Date());
        wmb.setSubject(subject);
        wmb.setToMail(toWebmail);
        wmb.setToMailCopy("");
        wmb.setToMailSecret("");

        T9WebmailLogic l = new T9WebmailLogic();
        int seqId = l.getWebSeqId(conn, bodyId);
        if (seqId > 0) {
            wmb.setSeqId(seqId);
            orm.updateSingle(conn, wmb);
        } else {
            orm.saveSingle(conn, wmb);
        }

        T9MailSmtpUtil.sendWebMail(wm, wmb, T9EmailCont.UPLOAD_HOME);
    }

    public String read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String EMAIL_ID = T9MobileString.showEmpty(request.getParameter("EMAIL_ID"), "0");

            String sql = "SELECT * from EMAIL,EMAIL_BODY where EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and EMAIL.SEQ_ID='"
                    + EMAIL_ID
                    + "' and EMAIL.TO_ID='"
                    + person.getSeqId()
                    + "' and (EMAIL.DELETE_FLAG='' or EMAIL.DELETE_FLAG='0' or EMAIL.DELETE_FLAG='2')";

            T9PdaEmailLogic el = new T9PdaEmailLogic();
            Map map = el.readEmail(conn, sql, EMAIL_ID);

            //request.setAttribute("n", map);
            if ("2".equals(map.get("format"))) {
                String content = (String) map.get("content");
                map.put("content", content);
                response.sendRedirect(content);
            } else {
                request.setAttribute("n", map);
            }
            //T9MobileUtility.output(response, "[" + T9MobileUtility.mapToJson(map) + "]");
            T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.mapToJson(map)));
        } catch (Exception ex) {
            throw ex;
        }
        //return "/mobile/email/read.jsp";
        return null;
    }

}
