package t9.core.funcs.news.act;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.news.data.T9NewsComment;
import t9.core.funcs.news.logic.T9NewsManageLogic;
import t9.core.funcs.news.logic.T9NewsShowLogic;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.notify.logic.T9NotifyManageLogic;
import t9.core.funcs.notify.logic.T9NotifyShowLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
/**
 * 
 * @author qwx110
 *
 */
public class T9NewsShowAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
  private T9NewsShowLogic newLogic = new T9NewsShowLogic();
  private static final int MAX = 10;
  public boolean findToId(String object,String object2){
    boolean temp = false;
    if(object != null && !"".equals(object)){
      String[] toIds = object.split(",");
      for(int j = 0 ;j < toIds.length ; j++){
        String toIdTemp =  toIds[j];
        if(toIdTemp.equals(object2)){
          temp = true;
          break;
        }
      }
    }
    return temp;
  }
  
  /**
   * 增加新的评论
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addNewsComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9ORM orm = new T9ORM();
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String newsId = "";
    String authorName = T9Utility.null2Empty(request.getParameter("authorName")); 
    String content = request.getParameter("content");
    String userName = request.getParameter("userName"); 
    String nickName = request.getParameter("nickName"); 
    newsId = request.getParameter("newsId"); 
   
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(authorName.equals("nickName") && ("".equals(nickName)||nickName==null||"null".equals(nickName))) {
        nickName = "匿名用户";
      }
      T9NewsComment newsComment = new T9NewsComment();
//      newsComment.setContent(content);
      newsComment.setNewsId(Integer.parseInt(newsId));     
      newsComment.setNickName(nickName);
      newsComment.setReTime(new Date());
      newsComment.setUserId(String.valueOf(person.getSeqId()));
      newsComment.setContent(content);
      newLogic.saveComment(dbConn, newsComment);
      //orm.saveSingle(dbConn, newsComment);
//      String sql = "insert into NEWS_COMMENT(NEWS_ID,USER_ID,NICK_NAME,CONTENT,RE_TIME) values ('"+newsId+"','"+
//                   +person.getSeqId() + "','" + nickName + "','" + content + "','" + new Date() +"')";
//      st = dbConn.createStatement();
//      st.execute(sql);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/core/funcs/news/show/reNews.jsp?seqId="+newsId+"&userName="+userName;
  }
  
  /**
   * 查询出列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getnewsShowList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NewsShowLogic newShowLogic = new T9NewsShowLogic();
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    T9ORM orm = new T9ORM();
    T9News news = null;
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数
    String seqId = request.getParameter("seqId");//主键
//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      String data = "";
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      news = (T9News)orm.loadObjSingle(dbConn, T9News.class, Integer.parseInt(seqId));
      if (news != null && !T9Utility.null2Empty(news.getFormat()).equals("2")) {
        byte[] byteContent = news.getCompressContent();
    	  if (byteContent == null) {
    		  news.setContent("");
    	  }else {
    	    news.setContent(new String(byteContent, "UTF-8"));
    	  }
      }
      
      if(news==null) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG,"该新闻已删除。");
          return "/core/inc/rtjson.jsp";
       }
      if(!"1".equals(loginUser.getUserPriv())&&!news.getProvider().equals(String.valueOf(loginUser.getSeqId()))&&!"0".equals(news.getToId())
          &&!"ALL_DEPT".equalsIgnoreCase(news.getToId())&&!findToId(news.getToId(), Integer.toString(loginUser.getDeptId()))
          &&!findToId(news.getPrivId(),loginUser.getUserPriv())&&!findToId(news.getUserId(),Integer.toString(loginUser.getSeqId()))){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,"您无权限查看该新闻。");
        return "/core/inc/rtjson.jsp";
      }
     
      data = newShowLogic.getnewsShowList(dbConn, loginUser, Integer.parseInt(showLenStr),
                                          Integer.parseInt(pageIndexStr),seqId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      return "/core/inc/rtjson.jsp";
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      return "/core/inc/rtjson.jsp";
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc

  }
  /**
   * 管理评论加载时调用的方法
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getnewsManagerList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NewsShowLogic newShowLogic = new T9NewsShowLogic();
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String subject = "";
    String format = "";
    String anonymityYn = "";
    String publish = "";
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数
    String seqId = request.getParameter("seqId");//主键
//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      String data = "";
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String queryNewsSql = "SELECT SUBJECT,ANONYMITY_YN,FORMAT, PUBLISH from NEWS where SEQ_ID='"+ seqId +"' ";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryNewsSql);
      if(rs.next()){
        subject = T9Utility.encodeSpecial(rs.getString("SUBJECT"));
        format = rs.getString("FORMAT");
        anonymityYn = rs.getString("ANONYMITY_YN");
        publish = rs.getString("PUBLISH");
      }else {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,"此新闻已删除");
        return "/core/inc/rtjson.jsp";
      }
      
      if(T9Utility.isNullorEmpty(publish) || "0".equalsIgnoreCase(publish)){
    	  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG,"此新闻未发布");
          return "/core/inc/rtjson.jsp";
      }else  if( "2".equalsIgnoreCase(publish)){
    	  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG,"此新闻已终止");
          return "/core/inc/rtjson.jsp";
      }
      
      if("2".equals(anonymityYn)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,"此新闻禁止评论");
        return "/core/inc/rtjson.jsp";
      }
      
     
      data = newShowLogic.getnewsManagerList(dbConn, loginUser, Integer.parseInt(showLenStr),subject,format,anonymityYn, 
                                          Integer.parseInt(pageIndexStr),Integer.parseInt(seqId ));
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除所有的新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAllnews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      boolean success =newsManageLogic.deleteAllnews(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除所选的新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteCheckNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String pageIndex = request.getParameter("pageIndex");
    String showLength = request.getParameter("showLength");
    String deleteStr = request.getParameter("delete_str");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
      boolean success =newsManageLogic.deleteChecknews(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv, deleteStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查看未读新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getnewsNoReadList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NewsShowLogic newsShowLogic = new T9NewsShowLogic();
    Connection dbConn = null;
    String type = request.getParameter("type");//下拉框中类型
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段
    
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数    if(pageIndexStr == null || pageIndexStr.trim() == ""|| pageIndexStr.replaceAll(" ", "").length() <1) {
      pageIndexStr = "1";
    }
//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      String data = "";
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
    
        data = newsShowLogic.getnewsNoReadList(dbConn, loginUser, Integer.parseInt(showLenStr),type,ascDesc,field, 
            Integer.parseInt(pageIndexStr));
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  //我的办公桌的取出所有新闻数据
  public String getnewsAllList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NewsShowLogic newsShowLogic = new T9NewsShowLogic();
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String format = "";
    String anonymityYn = "";
    String type = request.getParameter("type");//下拉框中类型
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段
    String sendTime = request.getParameter("sendTime");
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数    if(pageIndexStr == null || pageIndexStr.trim() == ""|| pageIndexStr.replaceAll(" ", "").length() <1) {
      pageIndexStr = "1";
    }
//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      String data = "";
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        data = newsShowLogic.getnewsAllList(dbConn, loginUser, Integer.parseInt(showLenStr),type,ascDesc,field, 
            Integer.parseInt(pageIndexStr), sendTime);
        //T9Out.println(data);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 桌面显示新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String getDeskNewsAllList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer("[");
    int count = 0;
    String queryNotifySql = "";
    String type = request.getParameter("type");//下拉框中类型
    
//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    int i =0;
    List<T9News> list = new ArrayList<T9News>();
    try {
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request
              .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          int loginDeptId = loginUser.getDeptId();
          String userPriv = loginUser.getUserPriv();
          int seqUserId = loginUser.getSeqId();
          String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
          if(loginUser.isAdmin()){//如果是管理员，则可以看到所有的新闻
            if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
              queryNotifySql = "SELECT SEQ_ID,TO_ID,SUBJECT,[TOP],PRIV_ID,USER_ID,READERS,ANONYMITY_YN,"
                +"TYPE_ID,NEWS_TIME,CLICK_COUNT,FORMAT from NEWS where PUBLISH='1'";
              
            }else {
              queryNotifySql = "SELECT SEQ_ID,TO_ID,SUBJECT,TOP,PRIV_ID,USER_ID,READERS,ANONYMITY_YN,"
                +"TYPE_ID,NEWS_TIME,CLICK_COUNT,FORMAT from NEWS where PUBLISH='1'";
            }
          }else{    //如果不是则在发布范围之内的人才能看到  
            if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
              queryNotifySql = "SELECT SEQ_ID,TO_ID,SUBJECT,[TOP],PRIV_ID,USER_ID,READERS,ANONYMITY_YN,"
                +"TYPE_ID,NEWS_TIME,CLICK_COUNT,FORMAT from NEWS where PUBLISH='1'and ("
                + T9DBUtility.findInSet(Integer.toString(loginDeptId), "TO_ID")
                + " or " + T9DBUtility.findInSet(userPriv,"PRIV_ID") +" or " 
                + T9DBUtility.findInSet(Integer.toString(seqUserId),"USER_ID") + " or "
                + T9DBUtility.findInSet("0", "TO_ID") +") ";
              
            }else {
              queryNotifySql = "SELECT SEQ_ID,TO_ID,SUBJECT,TOP,PRIV_ID,USER_ID,READERS,ANONYMITY_YN,"
                +"TYPE_ID,NEWS_TIME,CLICK_COUNT,FORMAT from NEWS where PUBLISH='1'and ("
                + T9DBUtility.findInSet(Integer.toString(loginDeptId), "TO_ID")
                + " or " + T9DBUtility.findInSet(userPriv,"PRIV_ID") +" or " 
                + T9DBUtility.findInSet(Integer.toString(seqUserId),"USER_ID") + " or "
                + T9DBUtility.findInSet("0", "TO_ID") +") ";
            }
          }
          
          if (!T9Utility.isNullorEmpty(type)) {
            if ( !"0".equals(type)) {
              queryNotifySql +=  " and TYPE_ID='"+ type + "' ";
            } else {
              queryNotifySql = queryNotifySql + " and (TYPE_ID='' or TYPE_ID=' ' or TYPE_ID is null)";
            }
          } 
         
          queryNotifySql += " order by NEWS_TIME desc";
          
          //System.out.println(queryNotifySql);
          
          stmt = dbConn.createStatement();
          rs = stmt.executeQuery(queryNotifySql);
          
          while(rs.next() && ++ i < MAX) {
            /*if("ALL_DEPT".equals(rs.getString("TO_ID"))||
                "0".equals(rs.getString("TO_ID"))||
                findToId(rs.getString("TO_ID"),Integer.toString(loginDeptId))==true||
                findToId(rs.getString("PRIV_ID"),userPriv)==true||
                findToId(rs.getString("USER_ID"),Integer.toString(seqUserId))==true){*/
            Statement stmts = null;
            ResultSet rss = null;
            int commentCount = 0;
            String queryCountSql = "SELECT count(*) from NEWS_COMMENT where NEWS_ID='" + rs.getInt("SEQ_ID") + "'";
            stmts = dbConn.createStatement();
            rss = stmts.executeQuery(queryCountSql);
            if(rss.next()) {
              commentCount = rss.getInt(1);
            } 
              count ++;
              sb.append("{");
              sb.append("seqId:" + rs.getInt("SEQ_ID"));
              sb.append(",subject:\"" + T9Utility.encodeSpecial(rs.getString("SUBJECT")) + "\"");
              sb.append(",clickCount:\"" + rs.getInt("CLICK_COUNT") + "\"");
              sb.append(",newsTIme:\"" + rs.getDate("NEWS_TIME") + "\"");
              sb.append(",commentCount:\"" + commentCount + "\"");
              sb.append(",iread:\"" + newLogic.haveRead(dbConn, loginUser.getSeqId(), rs.getInt("SEQ_ID")) + "\"");
              sb.append("},");
           /* }*/
          }
          if(count>0) {
            sb.deleteCharAt(sb.length() - 1); 
            }
          sb.append("]");
          //T9Out.println(sb.toString());
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
          request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String changeNoReadAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String changeSql = "update NEWS set READERS ="+dbLikeLast("READERS",String.valueOf(loginUser.getSeqId()))+",CLICK_COUNT=CLICK_COUNT+1 where PUBLISH='1' and "+ dbLikePre("READERS","','") +" not like '%,"+loginUser.getSeqId()+",%' or READERS is null";
      //T9Out.println(changeSql);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(changeSql);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    String foward = request.getParameter(T9ActionKeys.RET_METHOD_FORWARD);
    if (T9Utility.null2Empty(foward).equalsIgnoreCase("all")) {
      return "/core/funcs/news/show/newsAll.jsp";
    }else {
      return "/core/funcs/news/show/newsNoRead.jsp";
    }
  }
  
  public static String dbLikeLast(String fieldName, String value) throws SQLException {
    String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
          return "ISNULL("+fieldName+",'')" + "+ '" + value+",' ";
        }else if (dbms.equals("mysql")) {
          return "concat(IFNULL("+fieldName+",''),'" + value + ",')";
        }else if (dbms.equals("oracle")) {
          return fieldName + "||'" + value+",'";
        }else {
          throw new SQLException("not accepted dbms");
        } 
    }
    
    public static String dbLikePre(String fieldName, String fix) throws SQLException {
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
          return fix +"+" +" ISNULL("+ fieldName +",'')";
        }else if (dbms.equals("mysql")) {
          return "concat("+fix+",IFNULL("+fieldName+",''))";
        }else if (dbms.equals("oracle")) {
          return fix + "||" + fieldName;
        }else {
          throw new SQLException("not accepted dbms");
        } 
    }
  
  public String queryNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NewsShowLogic newsShowLogic = new T9NewsShowLogic();
    Connection dbConn = null;
    String data = "";
    Statement stmt = null;
    ResultSet rs = null;
    List<T9News> list = new ArrayList<T9News>();
    T9News news = (T9News) T9FOM.build(request.getParameterMap());
    String pageIndex = request.getParameter("pageIndex");
    String showLen = request.getParameter("showLength");
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段
    String beginDate = request.getParameter("beginDate");
    String endDate = request.getParameter("endDate");
    T9Person loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      int temp = 0;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        data = newsShowLogic.queryNews(dbConn,news,loginUser,beginDate,endDate,Integer.parseInt(showLen),Integer.parseInt(pageIndex),ascDesc,field);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage().substring(ex.getMessage().lastIndexOf(":")+1,ex.getMessage().length()),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);    
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String relayComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9ORM orm = new T9ORM();
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String parentId = request.getParameter("parentId");
    String newsId = request.getParameter("newsId");
    String authorName = request.getParameter("authorName"); 
    String content = request.getParameter("content");
    String userName = request.getParameter("userName"); 
    String nickName = request.getParameter("nickName"); 
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(authorName.equals("nickName") && ("".equals(nickName)||nickName==null||"null".equals(nickName))) {
        nickName = "匿名用户";
      }
      T9NewsComment newsComment = new T9NewsComment();
      newsComment.setNewsId(Integer.parseInt(newsId));
      newsComment.setNickName(nickName);
      newsComment.setReTime(new Date());
      newsComment.setParentId(Integer.parseInt(parentId));
      newsComment.setUserId(String.valueOf(person.getSeqId()));
      newsComment.setContent(content);
       
      orm.saveSingle(dbConn, newsComment);
//      String sql = "insert into NEWS_COMMENT(NEWS_ID,USER_ID,NICK_NAME,CONTENT,RE_TIME) values ('"+newsId+"','"+
//                   +person.getSeqId() + "','" + nickName + "','" + content + "','" + new Date() +"')";
//      st = dbConn.createStatement();
//      st.execute(sql);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/funcs/news/show/reNews.jsp?seqId="+newsId+"&userName="+userName;
  }
  
  public String deleteComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9ORM orm = new T9ORM();
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String commentId = request.getParameter("commentId");
    String newsId = request.getParameter("newsId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Statement st = dbConn.createStatement();
      if(!T9Utility.isNullorEmpty(commentId)){
        String deleteSql = "delete from NEWS_COMMENT where NEWS_ID='"+newsId+"' and seq_id in ("+newLogic.findIds(dbConn, Integer.parseInt(commentId))+")";
        //T9Out.println(deleteSql);
        st.execute(deleteSql);
        
        String deleteSelfSql = "delete from NEWS_COMMENT where NEWS_ID='"+newsId+"' and SEQ_ID='"+commentId+"'";
        st.execute(deleteSelfSql);
//      String sql = "insert into NEWS_COMMENT(NEWS_ID,USER_ID,NICK_NAME,CONTENT,RE_TIME) values ('"+newsId+"','"+
//                   +person.getSeqId() + "','" + nickName + "','" + content + "','" + new Date() +"')";
//      st = dbConn.createStatement();
//      st.execute(sql);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/core/funcs/news/show/reNews.jsp?seqId="+newsId+"&userName="+person.getUserName();
  }
  
  public String deleteReader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9NewsManageLogic newsManageLogic = new T9NewsManageLogic();
    T9Person loginUser = null;
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");    
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
     Statement st = null;
     ResultSet rs = null;
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       String updateSql = "update NEWS set READERS='' where SEQ_ID='"+seqId+"'";
       st = dbConn.createStatement();
      st.executeUpdate(updateSql);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG,"终止生效状态已修改");
  } catch (Exception ex) {
    String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
    throw ex;
  }
  //return "/core/funcs/dept/deptinput.jsp";
  //?deptParentDesc=+deptParentDesc
  return "/core/funcs/news/manage/showReader.jsp";
  }
  /**
   * 点赞
  */
public String likes(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    try {
	      Connection dbConn = null;
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	       
	      int seqId = Integer.parseInt(request.getParameter("seqId"));//主键
	      int userId = loginUser.getSeqId();
	      
	      T9NewsShowLogic newShowLogic = new T9NewsShowLogic();
	      String data = "";
	      data = newShowLogic.saveLikes(dbConn,seqId,userId);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return "/core/inc/rtjson.jsp";
  }
  
}
