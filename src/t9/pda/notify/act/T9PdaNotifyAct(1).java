package t9.pda.notify.act;

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
import t9.pda.notify.data.T9PdaNotify;

public class T9PdaNotifyAct {

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
      
      String sql = " SELECT n.SEQ_ID, FROM_ID, p.USER_NAME, SUBJECT, ";
      if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
        sql = sql + "[TOP] ";
      }else {
        sql = sql + "TOP";
      }
      
      String isClob = "=";
      if(dbms.equals(T9Const.DBMS_ORACLE)){
        isClob = "like";
      }
      sql = sql  + " , n.TYPE_ID, c.CLASS_DESC, BEGIN_DATE, ATTACHMENT_ID, ATTACHMENT_NAME, CONTENT "
                 + " from NOTIFY n "
                 + " left join code_item c on c.SEQ_ID = n.TYPE_ID "
                 + " join PERSON p on p.SEQ_ID = FROM_ID "
                 + " where (TO_ID " + isClob + " '0' "
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "TO_ID")
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getDeptIdOther()), "TO_ID")
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getUserPriv()), "PRIV_ID") 
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getUserPrivOther()), "PRIV_ID") 
                 + " or " + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "n.USER_ID") +")"
                 + " and " + T9DBUtility.getDateFilter("begin_date", T9Utility.getDateTimeStr(null).substring(0, 10)+" 00:00:00", "<=")
                 + " and (" + T9DBUtility.getDateFilter("end_date", T9Utility.getDateTimeStr(null).substring(0, 10)+" 00:00:00", ">=") +" or end_date is null) "
                 + " and PUBLISH='1' ";
      if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
        sql = sql + " order by [TOP] desc,BEGIN_DATE desc,SEND_TIME desc ";
      }else {
        sql = sql + " order by TOP desc,BEGIN_DATE desc,SEND_TIME desc ";
      }
      
      List<T9PdaNotify> list = new ArrayList<T9PdaNotify>();
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      rs = ps.executeQuery();
      rs.last();
      int totalSize = rs.getRow();
      if (totalSize == 0) {
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("thisPage", 0);
        request.setAttribute("totalPage", 0);
        request.setAttribute("notifies", list);
        request.getRequestDispatcher("/pda/notify/index.jsp").forward(request, response);
        return;
      }
      rs.absolute((thisPage-1) * pageSize + 1);
      int count = 0;
      while(!rs.isAfterLast()) {
        if(count >= pageSize)
          break;
        T9PdaNotify notify = new T9PdaNotify();
        notify.setSeqId(rs.getInt("SEQ_ID"));
        notify.setFromId(rs.getString("FROM_ID"));
        notify.setUserName(rs.getString("USER_NAME"));
        notify.setSubject(rs.getString("SUBJECT"));
        notify.setTop(rs.getString("TOP"));
        notify.setTypeId(rs.getString("TYPE_ID"));
        notify.setClassDesc(rs.getString("CLASS_DESC"));
        notify.setBeginDate(rs.getTimestamp("BEGIN_DATE"));
        notify.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        notify.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        String content = rs.getString("CONTENT");
        content = content == null ? "" : T9Utility.cutHtml(content);
        notify.setContent(content);
        list.add(notify);
        rs.next();
        count++;
      }
      request.setAttribute("pageSize", pageSize);
      request.setAttribute("thisPage", thisPage);
      request.setAttribute("totalPage", totalSize/pageSize + (totalSize%pageSize == 0 ? 0 : 1));
      request.setAttribute("notifies", list);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/notify/index.jsp").forward(request, response);
    return;
  }
}
