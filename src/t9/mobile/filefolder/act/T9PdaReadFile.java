package t9.mobile.filefolder.act;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.filefolder.data.T9FileSort;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.mobile.filefolder.logic.T9PdaFileFolderLogic;
import t9.mobile.mobileseal.logic.T9PdaMobileSealLogic;
import t9.mobile.util.T9MobileString;

public class T9PdaReadFile {

	
	public String readFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	     String seqId = request.getParameter("CONTENT_ID");
	     seqId = T9MobileString.showObjNull(seqId, "0");
	     
	     T9PdaFileFolderLogic tff = new T9PdaFileFolderLogic();
	     Map data = null;
	     data = new HashMap<String, String>();
	     T9ORM orm = new T9ORM();
	     T9FileContent fc = (T9FileContent)orm.loadObjSingle(dbConn, T9FileContent.class, Integer.parseInt(seqId));
	     
	     if(fc == null){
	    	return null; 
	     }
	     
	     String SUBJECT = fc.getSubject();
	     Date SEND_TIME = fc.getSendTime();
	     String CONTENT = fc.getContent();
	     int SORT_ID = fc.getSortId();
	     String USER_ID = fc.getUserId();
	     
	     String ATTACHMENT_ID = fc.getAttachmentId();
	     String ATTACHMENT_NAME = fc.getAttachmentName();
	     
	     String temsql = "SELECT USER_ID,DOWN_USER,MANAGE_USER,OWNER from FILE_SORT where SEQ_ID='"+SORT_ID+"'";
	     if(Integer.parseInt(USER_ID) != person.getSeqId() ){
	    	 boolean result = tff.fileSortCheckPriv(dbConn, temsql, person, String.valueOf(SORT_ID));
	    	 return null;
	     }
	     
	     data.put("SUBJECT", SUBJECT);
	     data.put("SEND_TIME", SEND_TIME);
	     data.put("CONTENT", CONTENT);
	     data.put("ATTACHMENT_ID", ATTACHMENT_ID);
	     data.put("ATTACHMENT_NAME", ATTACHMENT_NAME);
	      
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取移动印章数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/mobile/filefolder/read_file.jsp";
	}
}
