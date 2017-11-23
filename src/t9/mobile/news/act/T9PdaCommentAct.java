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
import t9.core.util.db.T9DBUtility;
import t9.mobile.news.logic.T9PdaCommentLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaCommentAct {
    
    public String data(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String A = request.getParameter("A");
            String NEWS_ID = request.getParameter("NEWS_ID");
            String ATYPE = request.getParameter("ATYPE");

            String query = "";
            T9PdaCommentLogic logic = new T9PdaCommentLogic();

            boolean flag = true;
            if ("refreshList".equals(ATYPE)) {
                if ("loadList".equals(A)) {
                    query = "SELECT SEQ_ID,PARENT_ID,NEWS_ID,NICK_NAME,CONTENT,RE_TIME,USER_ID from NEWS_COMMENT where ("
                    		+ T9DBUtility.findInSet(NEWS_ID + "", "NEWS_ID")
                    		+ " and "
                            + T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID")
                            + ") order by SEQ_ID desc  ";
                }
                List<Map<String, String>> jsonData = logic.refreshList(conn, person, query, flag, null);
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(jsonData)));
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    public String submit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
        	boolean flag = true;
        	T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        			.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        	T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        	conn = requestDbConn.getSysDbConn();
        	T9PdaCommentLogic logic = new T9PdaCommentLogic();
        	String query = "";
            String newsId = request.getParameter("NEWS_ID"); 
            String content = request.getParameter("CONENT");
            String nickName = request.getParameter("nickName"); 
            if(person.getUserName().equals("nickName") && ("".equals(nickName)||nickName==null||"null".equals(nickName))) {
                nickName = "匿名用户";
              }
            query = "insert into NEWS_COMMENT(NEWS_ID,USER_ID,NICK_NAME,CONTENT,RE_TIME) values ('"+newsId+"','"+
            +person.getSeqId() + "','" + nickName + "','" + content + "','" + new Date(System.currentTimeMillis()) +"')";
            Map jsonData = logic.save(conn, person, query, flag);
	        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.mapToJson(jsonData)));
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }
    
    public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Connection conn = null;
        try {
        	T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        			.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        	T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        	conn = requestDbConn.getSysDbConn();
        	String query = "";
        	T9PdaCommentLogic logic = new T9PdaCommentLogic();
            String Q_ID = request.getParameter("Q_ID");
            
            query = "delete  from NEWS_COMMENT  where"
            		+ T9DBUtility.findInSet(Q_ID + "", "SEQ_ID")
            		+ " and "
            		+ T9DBUtility.findInSet(person.getSeqId() + "", "USER_ID");
            Map jsonData = logic.delete(conn, person, query, true);
            T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.mapToJson(jsonData)));
        }catch(Exception e){
        	throw e;
        }
    	return null;
    }

}
