package t9.lucene.act;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.lucene.data.T9LuceneLog;
import t9.lucene.logic.T9PersonServiceLogic;


public class T9PersonServiceAct {

  T9PersonServiceLogic logic =new T9PersonServiceLogic();   
  public String getLucenceAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String result="/core/inc/rtjson.jsp";
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String rss=request.getParameter("rss");
      String content=request.getParameter("content");
      String host=request.getServerName();
      String size=request.getParameter("size");
      String index=request.getParameter("index");
      String stationId=request.getParameter("stationId");
      int PageSize=10;
      int PageIndex=0;
      if(!T9Utility.isNullorEmpty(size)){
        PageSize=Integer.parseInt(size);
      }
      if(!T9Utility.isNullorEmpty(index)){
        PageIndex=Integer.parseInt(index);
      }
      String data="";
      if(T9Utility.isNullorEmpty(stationId)){
        data=logic.getLuceneLogic(dbConn,content,rss,host,PageSize,PageIndex,request.getRealPath(""),stationId);
      }else{
        data=logic.getLuceneByTopLogic(dbConn,content,rss,host,PageSize,PageIndex,request.getRealPath(""),stationId);
      }
      if(T9Utility.isNullorEmpty(rss)){
       data="{data:["+data+"]}";
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
      else{
        PrintWriter out = response.getWriter();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml;charset=UTF-8");
        response.setHeader("Cache-Control","no-cache");
       // System.out.println(data);
        out.print(data);
        out.flush();
        out.close();
       result=null;
      }
     
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return result;
  }
  
  public String newsAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;

    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String content=request.getParameter("content");
        T9ORM orm=new T9ORM();
        T9News news =new T9News();
        news.setNewsTime(new Date());
        news.setProvider("1");
        news.setContent(content);
        news.setUserId(person.getSeqId()+"");
        news.setSubject(content);
        news.setNewsTime(new Date());
        news.setToId("-1");
       // news.setModuleId("psmessage");
        orm.saveSingle(dbConn, news);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }

 //   request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/core/inc/rtjson.jsp";
  }
  public String getMoreNotice(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String index=request.getParameter("index");
      String size=request.getParameter("size");
      String data=logic.getMoreNotice(dbConn,Integer.parseInt(index),Integer.parseInt(size));
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getHotNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String data=logic.getHotNews(dbConn,request.getRealPath(""));
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getLatestMessageAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String data=logic.getLatestMessageLogic(dbConn);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  

  public String getJionResultAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String str=request.getParameter("str");
      String data=logic.getJionResultLogic(dbConn,str);
    
      request.setAttribute("data", T9Utility.encodeSpecial(data));
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/show/luceneCombine.jsp";
  }
  
  
  public String getMoreMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String index=request.getParameter("index");
      String size=request.getParameter("size");
      String data=logic.getMoreMessage(dbConn,Integer.parseInt(index),Integer.parseInt(size));
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  
  
  
  public String getSubmitAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String id="admin";
      String toId=id;
      String url=request.getParameter("url");
      String name=request.getParameter("name");
      String path=request.getServerName();
      int port=request.getServerPort();
      path+=":"+port;
      url="http://"+path+url;
      logic.getSubmitLogic(person,toId,url,name);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  public String getPersonRssAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String data="";
      String flag="0";
      String userId="";
      if(person!=null){
        flag="1";
        userId=person.getUserName();
        data= logic.getPersonRssLogic(dbConn,person);
        }
      data="{userId:'"+userId+"',flag:'"+flag+"',data:["+data+"]}";     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String AddRssAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String id="admin";
      String toId=id;
      String url=request.getParameter("url");
      String name=request.getParameter("name");
      String path=request.getServerName();
      int port=request.getServerPort();
      path+=":"+port;
     // url="http://"+path+url;
      String flag="0";
      //System.out.println(flag);
      boolean isOUT=false;
      if(person!=null){
        
        if(isOUT){
        flag="1";
        logic.getSubmitLogic(person,toId,url,name);
        
        }else{
         flag="2";  
          logic.outPerson(dbConn, person, url,name);
        }
      }
      String data="{flag:'"+flag+"'}";
    
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  public String deleteRssAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String seqId=request.getParameter("seqId");
      logic.deleteRssLogic(dbConn, seqId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String addLuceneLogAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
   
      
      String str=request.getParameter("content");
      T9LuceneLog llog=new T9LuceneLog();
      if(person!=null){
        llog.setLuUser(person.getSeqId()+"");
      }
      llog.setLuTime(T9Utility.getCurDateTimeStr());
      llog.setLuStr(str);
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, llog);
      
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String getLuceneLogAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();    
      String str=request.getParameter("content");
      String data=logic.getLuceneLog(dbConn, str);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /*
   * 获取推荐
   * */
  public String getRecomendKeyAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();    
      String str=request.getParameter("content");
      String data=logic.getRecomentWord(dbConn, str);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
 
  
}
