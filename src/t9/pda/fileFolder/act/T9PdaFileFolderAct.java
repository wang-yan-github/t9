package t9.pda.fileFolder.act;

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
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.pda.fileFolder.data.T9PdaFileFolder;

public class T9PdaFileFolderAct {

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
      
      String sql = " SELECT SEQ_ID, SUBJECT, SEND_TIME, ATTACHMENT_ID, ATTACHMENT_NAME, CONTENT "
                 + " from FILE_CONTENT "
                 + " where SORT_ID=0 and USER_ID='"+person.getSeqId()+"' "
                 + " order by CONTENT_NO,SEND_TIME desc ";
      
      List<T9PdaFileFolder> list = new ArrayList<T9PdaFileFolder>();
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      rs = ps.executeQuery();
      rs.last();
      int totalSize = rs.getRow();
      if (totalSize == 0) {
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("thisPage", 0);
        request.setAttribute("totalPage", 0);
        request.setAttribute("fileFolders", list);
        request.getRequestDispatcher("/pda/fileFolder/index.jsp").forward(request, response);
        return;
      }
      rs.absolute((thisPage-1) * pageSize + 1);
      int count = 0;
      while(!rs.isAfterLast()) {
        if(count >= pageSize)
          break;
        T9PdaFileFolder fileFolder = new T9PdaFileFolder();
        fileFolder.setSeqId(rs.getInt("SEQ_ID"));
        fileFolder.setSubject(rs.getString("SUBJECT"));
        fileFolder.setSendTime(rs.getTimestamp("SEND_TIME"));
        fileFolder.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        fileFolder.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
        String content = rs.getString("CONTENT");
        content = content == null ? "" : T9Utility.cutHtml(content);
        fileFolder.setContent(content);
        list.add(fileFolder);
        rs.next();
        count++;
      }
      request.setAttribute("pageSize", pageSize);
      request.setAttribute("thisPage", thisPage);
      request.setAttribute("totalPage", totalSize/pageSize + (totalSize%pageSize == 0 ? 0 : 1));
      request.setAttribute("fileFolders", list);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/fileFolder/index.jsp").forward(request, response);
    return;
  }
}
