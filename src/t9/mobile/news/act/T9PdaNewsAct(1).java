package t9.mobile.news.act;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.news.logic.T9PdaCommentLogic;
import t9.mobile.news.logic.T9PdaNewsLogic;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaNewsAct {
    /**
     * 新闻列表接口
     * @param request
           ATYPE             refreshList：列表
           A                 loadList：所有数据列表，与CURRITERMS参数配合使用 getNew：ID大于LATEST_ID的数据,CURRITERMS参数无效。空：默认显示，只显示最新7条数据，与CURRITERMS参数配合使用，最多显示7条
           LATEST_ID         大于此ID的数据全部显示
           CURRITERMS        从最新数据开始，向前推移数（可与自定义分页联合使用）
     * @param response
           q_id              唯一标识
           read_flag         阅读标记
           subject           标题
           subject_color     标题颜色
           send_time         发布时间
           from_name         发布人
           click_count       点击量
           comment_number    评论数
           attachment_id     附件id
           down_file         下载附件
           attachment_name   附件名
           file_url          附件url
           has_attachment    是否有附件：1：有附件 0:没有附件
     * @return
     *     JSON
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
            T9PdaNewsLogic logic = new T9PdaNewsLogic();
            String deptOther = person.getDeptIdOther();
            String userPrivOther = person.getUserPrivOther();

            boolean flag = true;
            if ("refreshList".equals(ATYPE)) {
                if ("loadList".equals(A)) {
                    query = "SELECT LIKES,READERS,CLICK_COUNT,SUBJECT_FONT,SEQ_ID,PROVIDER,SUBJECT,NEWS_TIME,FORMAT,TYPE_ID,ATTACHMENT_ID,ATTACHMENT_NAME from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or TO_ID='0' or "
                            + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                            + T9MobileUtility.privOtherSql("TO_ID", deptOther)
                            + " or "
                            + T9DBUtility.findInSet(person.getUserPriv() + "", "PRIV_ID")
                            + T9MobileUtility.privOtherSql("PRIV_ID", userPrivOther)
                            + " or "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                            + " ) order by SEQ_ID desc  ";
                } else if ("getNew".equals(A)) {
                    String LATEST_ID = request.getParameter("LATEST_ID");
                    String new_count = "SELECT count(*) from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or TO_ID='0'  or "
                            + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                            + T9MobileUtility.privOtherSql("TO_ID", deptOther)
                            + " or "
                            + T9DBUtility.findInSet(person.getUserPriv() + "", "PRIV_ID")
                            + T9MobileUtility.privOtherSql("PRIV_ID", userPrivOther)
                            + " or "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                            + ") and SEQ_ID > "
                            + LATEST_ID;
                    int count = T9QuickQuery.getCount(conn, new_count);
                    if (count == 0) {
                        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "NONEWDATA", null));
                        return null;
                    } else {
                        flag = false;
                        query = "SELECT LIKES,READERS,CLICK_COUNT,SUBJECT_FONT,SEQ_ID,PROVIDER,SUBJECT,NEWS_TIME,FORMAT,TYPE_ID,ATTACHMENT_ID,ATTACHMENT_NAME from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or TO_ID='0'  or "
                                + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                                + T9MobileUtility.privOtherSql("TO_ID", deptOther)
                                + " or "
                                + T9DBUtility.findInSet(person.getUserPriv() + "", "PRIV_ID")
                                + T9MobileUtility.privOtherSql("PRIV_ID", userPrivOther)
                                + " or "
                                + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                                + ") and SEQ_ID > " + LATEST_ID + " order by SEQ_ID desc";
                    }
                } else {
                    CURRITERMS = request.getParameter("CURRITERMS");
                    query = "SELECT LIKES,READERS,CLICK_COUNT,SUBJECT_FONT,SEQ_ID,PROVIDER,SUBJECT,NEWS_TIME,FORMAT,TYPE_ID,ATTACHMENT_ID,ATTACHMENT_NAME from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or TO_ID='0'  or "
                            + T9DBUtility.findInSet(person.getDeptId() + "", "TO_ID")
                            + T9MobileUtility.privOtherSql("TO_ID", deptOther)
                            + " or "
                            + T9DBUtility.findInSet(person.getUserPriv() + "", "PRIV_ID")
                            + T9MobileUtility.privOtherSql("PRIV_ID", userPrivOther)
                            + " or "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                            + ") order by SEQ_ID desc ";
                    int count = T9QuickQuery.getCountByCursor(conn, query, CURRITERMS);
                    if (count == 0) {
                        T9MobileUtility
                                .output(response, T9MobileUtility.getResultJson(1, "NOMOREDATA", null));
                        return null;
                    }
                }
                
                List<Map<String,String>> result = logic.refreshList(conn, request,person, query, flag, CURRITERMS);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(result)));
            } else {
                T9MobileUtility.getResultJson(1, null, null);
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * 阅读新闻接口
     * 
     * @param request
          NEWS_ID 新闻唯一标识
     * @param response
          seqId                    唯一标识
          content                  内容
          typeName                 新闻类型
          anonymityYn              是否允许匿名评论
          attachmentName           附件名
          newsTime                 发布时间
          clickCount               点击数
          attachmentId             附件id
          url                      下载附件url
          commentNumber            评论数
          subject                  新闻标题
          format                   新闻格式
          fromName                 发布人
     * @return
     *    JSON
     * @throws Exception
     */
    public String read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String NEWS_ID = T9Utility.null2Empty(request.getParameter("NEWS_ID"));
            T9PdaNewsLogic logic = new T9PdaNewsLogic();
            Map map = logic.getNewsMap(conn, request,person, NEWS_ID);
            if ("2".equals(map.get("format"))) {
                String content = (String) map.get("content");
                map.put("content", content);
                response.sendRedirect(content);
            } else {
                request.setAttribute("n", map);
            }
            T9MobileUtility.output(response,
                    T9MobileUtility.getResultJson(1, null, T9MobileUtility.mapToJson(map)));
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * 点赞
     */
    public String addLikes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            boolean flag = true;
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            T9PdaNewsLogic logic = new T9PdaNewsLogic();
            String updateSql = "";
            String newsId = request.getParameter("NEWS_ID"); 
            int userId = person.getSeqId();
            Map jsonData = logic.saveLikes(conn, newsId, userId);
	        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.mapToJson(jsonData)));
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

}
