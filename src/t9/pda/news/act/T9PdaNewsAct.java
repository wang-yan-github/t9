package t9.pda.news.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.pda.news.data.T9PdaNews;

public class T9PdaNewsAct {

  public void search(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      int pageSize = Integer.parseInt(request.getParameter("pageSize") == "" || request.getParameter("pageSize") == null ? "5" : request.getParameter("pageSize"));
      int thisPage = Integer.parseInt(request.getParameter("thisPage") == "" || request.getParameter("thisPage") == null ? "1" : request.getParameter("thisPage"));
      //int totalPage = Integer.parseInt(request.getParameter("totalPage") == "" || request.getParameter("totalPage") == null ? "1" : request.getParameter("totalPage"));
      
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
      String isClob = "=";
      if(dbms.equals(T9Const.DBMS_ORACLE)){
        isClob = "like";
      }
      String sql = " SELECT n.SEQ_ID, PROVIDER, SUBJECT, NEWS_TIME, FORMAT,TYPE_ID, ATTACHMENT_ID, ATTACHMENT_NAME, p.USER_NAME, c.CLASS_DESC, CONTENT "
                 + " from NEWS n"
                 + " left join code_item c on c.SEQ_ID = n.TYPE_ID "
                 + " join PERSON p on p.SEQ_ID = PROVIDER "
                 + " where PUBLISH='1' "
                 + " and (TO_ID " + isClob + " '0' or " + T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "TO_ID") 
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getUserPriv()), "PRIV_ID") 
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "n.USER_ID") +") "
                 + " order by NEWS_TIME desc ";
      
      List<T9PdaNews> list = new ArrayList<T9PdaNews>();
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      rs = ps.executeQuery();
      rs.last();
      int totalSize = rs.getRow();
      if (totalSize == 0) {
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("thisPage", 0);
        request.setAttribute("totalPage", 0);
        request.setAttribute("news", list);
        request.getRequestDispatcher("/pda/news/index.jsp").forward(request, response);
        return;
      }
      rs.absolute((thisPage-1) * pageSize + 1);
      int count = 0;
      while(!rs.isAfterLast()) {
        if(count >= pageSize)
          break;
        T9PdaNews news = new T9PdaNews();
        news.setSeqId(rs.getInt("SEQ_ID"));
        news.setProvider(rs.getString("PROVIDER"));
        news.setUserName(rs.getString("USER_NAME"));
        news.setSubject(rs.getString("SUBJECT"));
        news.setNewsTime(rs.getTimestamp("NEWS_TIME"));
        news.setFormat(rs.getString("FORMAT"));
        news.setTypeId(rs.getString("TYPE_ID"));
        news.setClassDesc(rs.getString("CLASS_DESC"));
        news.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        news.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        String content = rs.getString("CONTENT");
        content = content == null ? "" : T9Utility.cutHtml(content);
        news.setContent(content);
        list.add(news);
        rs.next();
        count++;
      }
      request.setAttribute("pageSize", pageSize);
      request.setAttribute("thisPage", thisPage);
      request.setAttribute("totalPage", totalSize/pageSize + (totalSize%pageSize == 0 ? 0 : 1));
      request.setAttribute("news", list);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/news/index.jsp").forward(request, response);
    return;
  }
}
