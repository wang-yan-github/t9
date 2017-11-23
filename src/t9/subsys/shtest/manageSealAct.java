package t9.subsys.shtest;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.logic.T9AddressLogic;
import t9.core.funcs.message.data.T9MessageBody;
import t9.core.funcs.message.logic.T9MessageLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.inforesouce.util.T9StringUtil;

public class manageSealAct {

	String shibaizhang = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCADcANwDASIA AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3 ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3 uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD5/ooo oAKKKKAHDp1qe0tZLu5jgiGXdgoz0Hufals7O4v7uK2tY2lmlbaiL1JrvLbRoLGRbDTv31yyKtxN /tYBYA/3Q2eeMgDIzWVWqqa8z0MBgJ4upZaRW7M3T9DsVtLj7Rbi4kiAfeWZfbGAelYGpacbaQyx r+6JJwoOF56c59R1NeqWNilrC8FvD9oO0mdtudw7/QVz+r6QsCNLFlrWTK+6Z7H/ABrihiJqV27n 0uIymjOlyRik+j/z9Tzf60VoahYm1kLAZjJ4x2rPr0IyUldHx9ajOjNwmrNCUUUVRkFFFFABRRRQ AUUUUALmrthY3Gp3kdnaRGSeTOFyAAAMkknhVABJYkAAEkgCo7W3e7uIbePZ5krhFLuqLk8DLMQA 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";

	 public String getNameIndex(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
		      
		      
		      PrintWriter pw = response.getWriter();
		      pw.println("");
		      pw.flush();
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return null;
		  }
	 
	 public String getsealform(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		  //  request.setCharacterEncoding("utf-8");
		    String seqId = request.getParameter("seqId");
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
		      T9SealTestdate as = null;
		      as = new T9SealTestdate();
		      T9ORM orm = new T9ORM();
		      int max = SealLogic.getmax(dbConn);
		      System.out.println("max:"+max);
		      as = (T9SealTestdate) orm.loadObjSingle(dbConn, as.getClass(), max);
		      StringBuffer data = new StringBuffer();
		      data.append("{'content':'"+as.getContent()+"',");
		      data.append("'title':'"+as.getTitle()+"',");
		      data.append("'fileno':'"+as.getFileno()+"',");
		      data.append("'yijian':'"+as.getYijian()+"',");
		      data.append("'md5':'"+as.getMd5()+"',");
		      String md5 = as.getMd5();
		      CriptDuke_MD5 jiami = new CriptDuke_MD5();
		      String currMd5 = jiami.encryptToMD5(as.getYijian());
		      if(currMd5.equals(md5)){
		    	  data.append("'zhang':'"+as.getZhang()+"'}");
		      }else{
		    	  System.out.println("cuode");
		    	  data.append("'zhang':'"+shibaizhang+"'}");
		      }
		      
		      System.out.println(data.toString());
		      System.out.println(data);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"成功！");
		      request.setAttribute(T9ActionKeys.RET_DATA,data.toString());
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
	 
	 public String addSeal(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    CriptDuke_MD5 jiami = new CriptDuke_MD5();
		    try {
		      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
		      int seqId = person.getSeqId();
		      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      String title = request.getParameter("title");
		      title = StringUtil.isShowNULL(title);
		      String content = request.getParameter("content");
		      content = StringUtil.isShowNULL(content);
		      String yijian = request.getParameter("yijian");
		      yijian = StringUtil.isShowNULL(yijian);
		      String zhang = request.getParameter("zhang");
		      zhang = StringUtil.isShowNULL(zhang);
		      String fileno = request.getParameter("fileno");
		      fileno = StringUtil.isShowNULL(fileno);
		      int UID = person.getSeqId();
		      String md5 = jiami.encryptToMD5(yijian);
		     String sql = "insert into seal_testdate(title,content,UID,yijian,zhang,fileno,md5)values('"+title+"','"+content+"','"+UID+"','"+yijian+"','"+zhang+"','"+fileno+"','"+md5+"')";
		    System.out.println(sql);
		     SealLogic.updateSql(dbConn, sql);

		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
	 
	 public String getUserseal(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
		      String data = "";
		      String strDate = SealLogic.getsealdate(dbConn, person.getUserName());
		      BASE64Decoder decoder = new BASE64Decoder();
		      byte[] bytes = decoder.decodeBuffer(strDate);
		     
		      StringBuffer sb2 = new StringBuffer();
		      data = new String(bytes);
		      data = data.replaceAll("\n", "");
		      data = data.replaceAll("\r", "");
		     
		    String res = "{'zhang':'"+strDate+"'}";
		    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出微讯内容！");
		      request.setAttribute(T9ActionKeys.RET_DATA,data);
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
	 public String getUserseal111(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
		      
		      String strDate = SealLogic.getsealdate(dbConn, person.getUserName());
		      StringBuffer sb = new StringBuffer();
		      sb.append("{\"zhang\":");
		      sb.append("\""+strDate+"\"");
		      sb.append("}");
		    String res = "{'zhang':'"+strDate+"'}";
		    //  System.out.println(sb.toString());
		    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出微讯内容！");
		      request.setAttribute(T9ActionKeys.RET_DATA,sb.toString());
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
}
