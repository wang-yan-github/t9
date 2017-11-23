package t9.pda.diary.act;

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
import t9.core.util.T9RegexpUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.pda.diary.data.T9PdaDiary;

public class T9PdaDiaryAct {

  public void doint(HttpServletRequest request, HttpServletResponse response) throws Exception{
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
      
      String sql = " SELECT d.SEQ_ID , d.DIA_DATE , d.DIA_TYPE , d.CONTENT from DIARY d "
                 + " where d.USER_ID='"+person.getSeqId()+"' order by d.SEQ_ID desc ";
      
      List<T9PdaDiary> list = new ArrayList<T9PdaDiary>();
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      rs = ps.executeQuery();
      rs.last();
      int totalSize = rs.getRow();
      if (totalSize == 0) {
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("thisPage", 0);
        request.setAttribute("totalPage", 0);
        request.setAttribute("diarys", list);
        request.getRequestDispatcher("/pda/diary/index.jsp").forward(request, response);
        return;
      }
      rs.absolute((thisPage-1) * pageSize + 1);
      int count = 0;
      while(!rs.isAfterLast()) {
        if(count >= pageSize)
          break;
        T9PdaDiary diary = new T9PdaDiary();
        diary.setSeqId(rs.getInt("SEQ_ID"));
        diary.setDiaDate(rs.getTimestamp("DIA_DATE"));
        diary.setDiaType(rs.getInt("DIA_TYPE"));
        diary.setContent(T9RegexpUtility.cutHtml(T9Utility.null2Empty(rs.getString("CONTENT"))));
        list.add(diary);
        rs.next();
        count++;
      }
      request.setAttribute("pageSize", pageSize);
      request.setAttribute("thisPage", thisPage);
      request.setAttribute("totalPage", totalSize/pageSize + (totalSize%pageSize == 0 ? 0 : 1));
      request.setAttribute("diarys", list);
    }
    catch(Exception ex){
      ex.printStackTrace();
      return ;
    } finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/diary/index.jsp").forward(request, response);
    return;
  }
  
  public void edit(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    try{
      String seqId = (String)request.getParameter("seqId");
      String diaType = (String)request.getParameter("diaType");
      String content = (String)request.getParameter("content");
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String sql = " update DIARY set DIA_TYPE ="+ diaType +",CONTENT ='"+content+"' where SEQ_ID = "+seqId;
      ps = dbConn.prepareStatement(sql);
      int flag = ps.executeUpdate();
      request.setAttribute("flag", flag);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
    request.getRequestDispatcher("/pda/diary/send.jsp").forward(request, response);
    return;
  }
  
  public void newDiary(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    try{
      String diaType = (String)request.getParameter("diaType");
      String content = (String)request.getParameter("content");
      String date = (String)request.getParameter("day");
      
      if (T9Utility.isNullorEmpty(date))
        date = T9Utility.getDateTimeStr(null).substring(0, 10);
      
      if (!T9Utility.isDay(date)) {
        request.getRequestDispatcher("/pda/diary/new.jsp").forward(request, response);
        return;
      }
      
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String sql = " insert into DIARY(USER_ID , DIA_TYPE , content , DIA_DATE , DIA_TIME) values("+person.getSeqId()+" , "+diaType+",'"+content+"',?,?)";
      ps = dbConn.prepareStatement(sql);
      
      
      ps.setTimestamp(1, T9Utility.parseTimeStamp(T9Utility.parseDate(date+" 00:00:00").getTime()));
      ps.setTimestamp(2, T9Utility.parseTimeStamp());
      int flag = ps.executeUpdate();
      request.setAttribute("flag", flag);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
    request.getRequestDispatcher("/pda/diary/send.jsp").forward(request, response);
    return;
  }
}
