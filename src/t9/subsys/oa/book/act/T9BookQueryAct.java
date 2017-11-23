package t9.subsys.oa.book.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.book.data.T9BookInfo;
import t9.subsys.oa.book.data.T9BookManage;
import t9.subsys.oa.book.data.T9BookType;
import t9.subsys.oa.book.data.T9Page;
import t9.subsys.oa.book.logic.T9BookQueryLogic;
import t9.subsys.oa.book.logic.T9BookSmsLogic;
/**
 * 图书查询
 * @author qwx110
 *
 */
public class T9BookQueryAct{

  
  /**
   * 模糊查找
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String findBookNos(HttpServletRequest request,  HttpServletResponse response) throws Exception, SQLException{    
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;   
    try{
      dbConn = requestDbConn.getSysDbConn();
      String search = request.getParameter("condition");
        search = T9Utility.decodeURL(search); //解码
      if(T9Utility.isNullorEmpty(search)){
        search = "";
      }
      String userId = request.getParameter("userId");
      T9Person user = null;
      if(!T9Utility.isNullorEmpty(userId)){
        user = new T9Person();
        user.setSeqId(Integer.parseInt(userId));//从页面中传过来的用户信息
      }else{
        user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      }
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();           
      List<T9BookInfo> books = queryLogic.findBookNos(dbConn, user, search);
      String jsons = toJsons2(books);
      request.setAttribute(T9ActionKeys.RET_DATA, jsons);    
      request.setAttribute(T9ActionKeys.RET_STATE, "0");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 图书类别
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String findBookTypes(HttpServletRequest request,  HttpServletResponse response)throws Exception, SQLException{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;   
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();
      List<T9BookType> types = queryLogic.findBookTypes(dbConn);
      String typeJson = toJsons(types);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, typeJson);      
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询图书
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String findBooks(HttpServletRequest request,  HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    String typeId = request.getParameter("typeId");
    String lend = request.getParameter("lend");
    String bookName = request.getParameter("bookName");
    String bookNo = request.getParameter("bookNo");
    String author = request.getParameter("author");
    String isbn = request.getParameter("isbn");
    String pubHouse = request.getParameter("pub_house");
    String area = request.getParameter("area");
    String orderflag = request.getParameter("orderflag");
    T9BookInfo book = new T9BookInfo();
    if("all".equalsIgnoreCase(typeId)){
      typeId = "0";
    }
    book.setTypeId(Integer.parseInt(typeId));
    book.setLend(lend);
    book.setBookName(bookName);
    book.setBookNo(bookNo);
    book.setAuthor(author);
    book.setIsbn(isbn);
    book.setPubHouse(pubHouse);
    book.setArea(area);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      int total = queryLogic.count(dbConn, book, orderflag, user);
      
      
      String currNo = request.getParameter("currNo");
      int curruntNo = 1;
      if(T9Utility.isNullorEmpty(currNo)){
        curruntNo = 1;
      }else{
        curruntNo = Integer.parseInt(currNo);
      }
      T9Page page = new T9Page(15, total, curruntNo);    
      List<T9BookInfo> findBooks = queryLogic.findBooks(dbConn, book, orderflag, user, page);
      request.setAttribute("books",findBooks);
      request.setAttribute("page",page);
      request.setAttribute("conditon",book);
      request.setAttribute("orderflag",orderflag);
    } catch (SQLException e){   
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }    
    return "/subsys/oa/book/query/list.jsp";
  }
  
  /**
   * 查询图书
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String findBooks2(HttpServletRequest request,  HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    String typeId = request.getParameter("typeId");
    String lend = request.getParameter("lend");
    String bookName = request.getParameter("bookName");
    bookName = URLDecoder.decode(bookName, T9Const.DEFAULT_CODE);
    String bookNo = request.getParameter("bookNo");
    bookNo = T9Utility.decodeURL(bookNo);
    String author = request.getParameter("author");
    author = T9Utility.decodeURL(author);
    String isbn = request.getParameter("isbn");
    isbn = T9Utility.decodeURL(isbn);
    String pubHouse = request.getParameter("pub_house");
    pubHouse = T9Utility.decodeURL(pubHouse);
    String area = request.getParameter("area");
    area = T9Utility.decodeURL(area);
    String orderflag = request.getParameter("orderflag");
    T9BookInfo book = new T9BookInfo();
    if("all".equalsIgnoreCase(typeId)){
      typeId = "0";
    }
    book.setTypeId(Integer.parseInt(typeId));
    book.setLend(lend);
    book.setBookName(bookName);
    book.setBookNo(bookNo);
    book.setAuthor(author);
    book.setIsbn(isbn);
    book.setPubHouse(pubHouse);
    book.setArea(area);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      int total = queryLogic.count(dbConn, book, orderflag, user);
      
      
      String currNo = request.getParameter("currNo");
      int curruntNo = 1;
      if(T9Utility.isNullorEmpty(currNo)){
        curruntNo = 1;
      }else{
        curruntNo = Integer.parseInt(currNo);
      }
      T9Page page = new T9Page(15, total, curruntNo);    
      List<T9BookInfo> findBooks = queryLogic.findBooks(dbConn, book, orderflag, user, page);
      request.setAttribute("books",findBooks);
      request.setAttribute("page",page);
      request.setAttribute("conditon",book);
      request.setAttribute("orderflag",orderflag);
    } catch (SQLException e){   
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }    
    return "/subsys/oa/book/query/list.jsp";
  }
  
  
  /**
   * 图书类型
   * @param types
   * @return
   */
  public String toJsons(List<T9BookType> types){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
      if(types.size()>0){
         for(int i=0; i<types.size(); i++){
            sb.append(types.get(i).toJson());
            if(i < types.size()-1){
              sb.append(",");
            }
         }
      }
    sb.append("]");
    return sb.toString();
  }
  
  /**
   * 图书编码
   * @param books
   * @return
   */
  public String toJsons2(List<T9BookInfo> books){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
      if(books.size()>0){
         for(int i=0; i<books.size(); i++){
            sb.append(books.get(i).toJson());
            if(i < books.size()-1){
              sb.append(",");
            }
         }
      }
    sb.append("]");
    return sb.toString();
  }
  
  /**
   * 点击借阅
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toRead(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;   
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();      
      String buserId = request.getParameter("toId");//借书人id
      String bookNo = request.getParameter("bookNo");
      String borrowDate = request.getParameter("borrowDate");
      String returnDate = request.getParameter("returnDate");
      String bRemark = request.getParameter("bRemark");   
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      T9BookManage manage = new T9BookManage();
      manage.setBookNo(bookNo);
      manage.setBuserId(buserId);
      manage.setBorrowRemark(bRemark);
      if(T9Utility.isNullorEmpty(borrowDate)){
        Date now = new Date();
        String boDate = T9Utility.getDateTimeStr(now).split(" ")[0];
        manage.setBorrowDate(T9Utility.parseDate("yyyy-MM-dd",boDate));
        if(T9Utility.isNullorEmpty(returnDate)){
          Date afterDate = T9Utility.getDayAfter(boDate, 30);
          String afDate = T9Utility.getDateTimeStr(afterDate).split(" ")[0];       
          manage.setReturnDate(T9Utility.parseDate("yyyy-MM-dd",afDate));
        }else{
          manage.setReturnDate(T9Utility.parseDate("yyyy-MM-dd",returnDate));
        }        
      }else{
        manage.setBorrowDate(T9Utility.parseDate("yyyy-MM-dd",borrowDate));
        Date browDate = T9Utility.parseDate("yyyy-MM-dd",borrowDate);
        String boDate = T9Utility.getDateTimeStr(browDate).split(" ")[0];
        if(T9Utility.isNullorEmpty(returnDate)){
          Date afterDate = T9Utility.getDayAfter(boDate, 30);
          String afDate = T9Utility.getDateTimeStr(afterDate).split(" ")[0];       
          manage.setReturnDate(T9Utility.parseDate("yyyy-MM-dd",afDate));
        }else{
          manage.setReturnDate(T9Utility.parseDate("yyyy-MM-dd",returnDate));
        } 
      }
      int k =  queryLogic.toReadStatus(dbConn,  manage, user);
      if(k!=0 && k != -10){        
        String toId = queryLogic.findManagerIds(dbConn, bookNo);       
        String content = user.getUserName()+"提交了借书申请，请审批！";
        String url = request.getContextPath() + "/subsys/oa/book/act/T9BookRuleAct/index.act";
        T9BookSmsLogic.sendSms(user, dbConn, content, url, toId, null);
        request.setAttribute("message", "保存成功");
      } 
      if(k == -10){
        request.setAttribute("message", "此书已借出");
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }     
    return "/subsys/oa/book/query/message.jsp";
  }
  
  /**
   * 待批借阅
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toAllow(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    try{
      dbConn = requestDbConn.getSysDbConn();
      String status = request.getParameter("stauts");
      if(T9Utility.isNullorEmpty(status)){
        status = "0";
      }
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();    
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      List<T9BookManage> manages = queryLogic.findBooksNoAllow(dbConn, user, status);
      request.setAttribute("manages", manages);
      request.setAttribute("status", status);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/book/query/status.jsp";
  }
  
  /**
   * 借阅待批，借阅已准 删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteManage(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    String status = "0";
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();    
      String seqId = request.getParameter("seqId");
      status = request.getParameter("status");
      int k = queryLogic.deleteManage(dbConn, Integer.parseInt(seqId));      
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/t9/subsys/oa/book/act/T9BookQueryAct/toAllow.act?stauts=" + status;
  }
  
  /**
   * 点击还书(还书审批),给借书审批人发短信
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String returnBooks(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    String status = "1";
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();    
      String seqId = request.getParameter("seqId");
      status = request.getParameter("status");
      int ok = queryLogic.returnBook(dbConn, Integer.parseInt(seqId));
      if(ok != 0){
        T9BookManage aManage = queryLogic.findRUserIds(dbConn, Integer.parseInt(seqId));
        T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        String content = user.getUserName()+"归还了所借的图书，编号为:"+ aManage.getBookNo();
        String url = "";
        T9BookSmsLogic.sendSms(user, dbConn, content, url, aManage.getRuserId(), null);
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/t9/subsys/oa/book/act/T9BookQueryAct/toAllow.act?stauts=" + status;
  }
  
  /**
   * 还书批准删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteFlag(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    String status = "1";
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();    
      String seqId = request.getParameter("seqId");
      status = request.getParameter("status");  
      String flag = request.getParameter("delFlag");
      int ok = queryLogic.deleteFlagByFlag(dbConn, Integer.parseInt(seqId), flag);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/t9/subsys/oa/book/act/T9BookQueryAct/toAllow.act?stauts=" + status;
  } 
  
  /**
   * 点击详情
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String detail(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    try{
      dbConn = requestDbConn.getSysDbConn();    
      String bookId = request.getParameter("bookId");
      T9BookQueryLogic queryLogic = new T9BookQueryLogic();    
      T9BookInfo aBook = queryLogic.findABook(dbConn, Integer.parseInt(bookId));
      List<T9BookManage> daipi =  queryLogic.findBookConditionByBookId(dbConn, aBook.getBookNo(), 0);//待批
      List<T9BookManage> weihuan =  queryLogic.findBookConditionByBookId(dbConn, aBook.getBookNo(), 1);//未还
      request.setAttribute("aBook", aBook);
      request.setAttribute("daipi", daipi);
      request.setAttribute("weihuan",weihuan);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/book/query/detail.jsp";
  }
}
