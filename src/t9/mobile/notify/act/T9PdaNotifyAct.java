package t9.mobile.notify.act;

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
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.diary.logic.T9PdaDiaryLogic;
import t9.mobile.notify.logic.T9PdaNotifyLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaNotifyAct {
    
    /**
     * 通知公告列表/详情接口

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
     * @param response
           .列表
           q_id            唯一标识
           read_flag       阅读标记
           subject         标题
           subject_color   标题颜色
           top             是否置顶
           send_time       发布时间
           from_name       发布人
           attachment_id   附件Id
           down_file       下载附件
           attachment_name 附件名
           file_url        附件url
           has_attachment  是否有附件：1：有附件 0:没有附件
           .详情 
           q_id            唯一标识
           type_name       公告类型
           from_name       发布人
           send_time       发布日期
           subject         标题
           subject_color   标题颜色
           begin_date      有效期
           content         正文内容
           attachment_id   附件id
           attachment_name 附件名
           file_url        下载附件url
           has_attachment  是否有附件：1：有附件 0:没有附件

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
            String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
            T9PdaNotifyLogic logic = new T9PdaNotifyLogic();
            if ("refreshList".equals(ATYPE)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String CUR_DATE = sdf.format(new Date()) + " 00:00:00";
                boolean flag = true;
                if ("loadList".equals(A)) {
                    query = "SELECT SEQ_ID,FROM_ID,SUBJECT_FONT,SUBJECT,TOP,TYPE_ID,READERS,BEGIN_DATE,ATTACHMENT_ID,ATTACHMENT_NAME from NOTIFY where (TO_ID='ALL_DEPT' or TO_ID='0' or "
                            + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                            + " or "
                            + fetch_other_sql(T9Utility.null2Empty(person.getDeptIdOther()), "TO_ID")
                            + " or "
                            + T9DBUtility.findInSet(person.getUserPriv(), "PRIV_ID")
                            + " or "
                            + fetch_other_sql(person.getUserPrivOther(), "PRIV_ID")
                            + " or "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                            + ") and "
                            + T9DBUtility.getDateFilter("begin_date", CUR_DATE, "<=")
                            + " and ("
                            + T9DBUtility.getDateFilter("end_date", CUR_DATE, ">=")
                            + " or end_date is null) and PUBLISH='1'  ";
                    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
                        query = query + " order by [TOP] desc,BEGIN_DATE desc,SEND_TIME desc";
                    } else {
                        query = query + " order by TOP desc,BEGIN_DATE desc,SEND_TIME desc";
                    }
                } else if ("getNew".equals(A)) {
                    String LATEST_ID = request.getParameter("LATEST_ID");
                    String new_count = "SELECT count(*) from NOTIFY where (TO_ID='ALL_DEPT' or TO_ID='0' or "
                            + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID") + " or "
                            + fetch_other_sql(person.getDeptIdOther(), "TO_ID") + " or "
                            + T9DBUtility.findInSet(person.getUserPriv(), "PRIV_ID") + " or "
                            + fetch_other_sql(person.getUserPrivOther(), "PRIV_ID") + " or "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID") + ") and "
                            + T9DBUtility.getDateFilter("begin_date", CUR_DATE, "<=") + " and ("
                            + T9DBUtility.getDateFilter("end_date", CUR_DATE, ">=")
                            + " or end_date is null) and not "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "READERS")
                            + " and PUBLISH='1' and SEQ_ID > " + LATEST_ID;
                    int count = T9QuickQuery.getCount(conn, new_count);
                    if (count == 0) {
                        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "没数据", null));
                        return null;
                    } else {
                        flag = false;
                        query = "SELECT SEQ_ID,FROM_ID,SUBJECT_FONT,SUBJECT,TOP,TYPE_ID,BEGIN_DATE,ATTACHMENT_ID,ATTACHMENT_NAME from NOTIFY where (TO_ID='ALL_DEPT' or TO_ID='0' or "
                                + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                                + " or "
                                + fetch_other_sql(person.getDeptIdOther(), "TO_ID")
                                + " or "
                                + T9DBUtility.findInSet(person.getUserPriv(), "PRIV_ID")
                                + " or"
                                + fetch_other_sql(person.getUserPrivOther(), "PRIV_ID")
                                + " or "
                                + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                                + ") and "
                                + T9DBUtility.getDateFilter("begin_date", CUR_DATE, "<=")
                                + " and SEQ_ID > "
                                + LATEST_ID
                                + " and ("
                                + T9DBUtility.getDateFilter("end_date", CUR_DATE, ">=")
                                + " or end_date is null) and not "
                                + T9DBUtility.findInSet(person.getSeqId() + "", "READERS")
                                + " and PUBLISH='1' ";
                        if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
                            query = query + " order by [TOP] desc,BEGIN_DATE desc,SEND_TIME desc";
                        } else {
                            query = query + " order by TOP desc,BEGIN_DATE desc,SEND_TIME desc";
                        }
                    }
                } else {
                    query = "SELECT SEQ_ID,FROM_ID,SUBJECT,TOP,TYPE_ID,BEGIN_DATE,ATTACHMENT_ID,ATTACHMENT_NAME,READERS from NOTIFY where (TO_ID='ALL_DEPT' or TO_ID='0' or "
                            + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                            + " or "
                            + fetch_other_sql(person.getDeptIdOther(), "TO_ID")
                            + " or "
                            + T9DBUtility.findInSet(person.getUserPriv() + "", "PRIV_ID")
                            + " or "
                            + fetch_other_sql(person.getUserPrivOther(), "PRIV_ID")
                            + " or "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                            + ") and "
                            + T9DBUtility.getDateFilter("begin_date", CUR_DATE, "<=")
                            + " and ("
                            + T9DBUtility.getDateFilter("end_date", CUR_DATE, ">=")
                            + " or end_date is null) and PUBLISH='1' ";
                    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
                        query = query + " order by [TOP] desc,BEGIN_DATE desc,SEND_TIME desc";
                    } else {
                        query = query + " order by TOP desc,BEGIN_DATE desc,SEND_TIME desc";
                    }
                    int count = T9QuickQuery.getCountByCursor(conn, query, CURRITERMS);
                    if (count == 0) {
                    	T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "没数据", null));
                        return null;
                    }
                }
                if (flag) {
                    logic.refreshList(request, response, conn, person, query, CURRITERMS);
                } else {

                    logic.refreshList(request, response, conn, person, query);
                }
                return null;
            } else if ("getNotifyContent".equals(ATYPE)) {
                logic.getNotifyContent(request, response, conn, person);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
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
