package t9.subsys.oa.book.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.jexcel.util.T9CVSWriter;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9ExportlogLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.asset.data.T9CpAssetType;
import t9.subsys.oa.asset.logic.T9CpAssetTypeLogic;
import t9.subsys.oa.book.data.T9BookInfo;
import t9.subsys.oa.book.data.T9BookType;
import t9.subsys.oa.book.data.T9Page;
import t9.subsys.oa.book.logic.T9BookQueryLogic;
import t9.subsys.oa.book.logic.T9BookTypeEnterLogic;
import t9.subsys.oa.book.logic.T9BookTypeLogic;
import t9.subsys.oa.vehicle.act.T9ExportAct;

public class T9BookTypeAct { 
  private static Logger log = Logger.getLogger(" t9.core.funcs.system.syslog.act.T9SysLogAct");
  public String addBookType(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> paramMap = fileForm.getParamMap();
    String typeName = paramMap.get("typeName");
    
        //String typeName = request.getParameter("typeName");
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        List<T9BookType> booktype  = T9BookTypeLogic.selectBook(dbConn, person, typeName);
        request.setAttribute("booktype", booktype);
        String book = "";
        for(int i = 0; i<booktype.size(); i++){
         book = String.valueOf(booktype.get(i));
        }
        if(book.equals("null"))
          return "/subsys/oa/book/type/add.jsp?typeName="+typeName;
        else
          return "/subsys/oa/book/type/index.jsp";
          
  }
  /**
   * 图书类型查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          List<T9BookType> booktype  = T9BookTypeLogic.findBookType(dbConn, person);
          //System.out.println(booktype);
          request.setAttribute("booktype", booktype);
          return "/subsys/oa/book/type/index.jsp";     
    }
  /**
   * (查出图书类型进行修改)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String editBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      String bookId = request.getParameter("bookId");
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          T9BookType bookType = T9BookTypeLogic.editBookType(dbConn, person,bookId);
          request.setAttribute("bookType", bookType);
          return "/subsys/oa/book/type/edit.jsp";     
    }
  /**
   * 修改图书类型
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      String typeId = request.getParameter("typeId");
      String typeName = request.getParameter("typeName");
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          int bookType = T9BookTypeLogic.updateBookType(dbConn, person,Integer.parseInt(typeId),typeName);
         // request.setAttribute("bookType", bookType);
         // if(bookType == 0)
            //return "/subsys/oa/book/type/add.jsp?typeName="+typeName;
         // else
           return "/t9/subsys/oa/book/act/T9BookTypeAct/findBookType.act";     
    }
  /**
   * 删除图书类型名称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      String bookId = request.getParameter("bookId");
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          int flag = T9BookTypeLogic.deleteBookType(dbConn, person,Integer.parseInt(bookId));
         // request.setAttribute("bookType", bookType);
        return "/t9/subsys/oa/book/act/T9BookTypeAct/findBookType.act";
    }
  /**
   * 用jsons 查询图书类别
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          String loginName = person.getUserName();
          List<T9BookType> booktype  = T9BookTypeLogic.selectBookType(dbConn, person);
          request.setAttribute("booktype", booktype);
          request.setAttribute("loginName", loginName);
          return "/subsys/oa/book/manage/newbook.jsp";     
    }
  
   /**
    * 图书录入 查询图书类型
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
  public String jinruBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          List<T9BookType> booktype  = T9BookTypeLogic.findBookType(dbConn, person);
          //System.out.println(booktype);
          request.setAttribute("booktype", booktype);
          return "/subsys/oa/book/manage/index.jsp";     
    }
  /**
   * 图书录入查询（带分页的）
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
     if(T9Utility.isNullorEmpty(orderflag)){
       orderflag="DEPT";
     }
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
      T9Page page = new T9Page(15, total, curruntNo);//此js方法只要填写每页显示的条数，总数据，当前显示的页数，就可以显示，首页上一页，下一页，尾页，转到第几页。  
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
    return "/subsys/oa/book/manage/list.jsp";
  }
  /**修改图书的基本信息(book_info)
   * 修改之前先进行查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectBookTypeId(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
      Connection dbConn = null;
      String bookSeqId = request.getParameter("bookSeqId");
      //bookSeqId.equalsIgnoreCase(arg0)
      try{
        String str = "";
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
         T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
         List<T9BookInfo> bookinfo = T9BookTypeLogic.selectBookTypeId(dbConn, person,Integer.parseInt(bookSeqId));
         List<T9BookType> booktype  = T9BookTypeLogic.selectBookType(dbConn, person);
         request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);//RET_STATE返回状态  RETURN_OK正确返回
         request.setAttribute(T9ActionKeys.RET_MSRG, "图书模糊查询成功");//RET_MSRG 返回消息
         request.setAttribute(T9ActionKeys.RET_DATA, str);//RET_DATA 返回数据
         request.setAttribute("bookinfo", bookinfo);
         request.setAttribute("booktype", booktype);
      }catch(Exception ex){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
    return "/subsys/oa/book/manage/edit.jsp";
  } 
  
  /**
   * 编辑 图书类型查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String editFindBookType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          List<T9BookType> booktype  = T9BookTypeLogic.findBookType(dbConn, person);
          //System.out.println(booktype);
          request.setAttribute("booktype", booktype);
          return "/subsys/oa/book/manage/edit.jsp";     
    }
  
  public String deleteBookInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
          String noHiddenId =  request.getParameter("HiddenId");
         int ok = T9BookTypeLogic.deleteBookInfo(dbConn, person ,Integer.parseInt(noHiddenId));
          //System.out.println(booktype);
          //request.setAttribute("booktype", booktype);
         String url = "typeId="+"0"+"&lend="+""+"&bookName="+""+"&bookNo="+""+"&author="+""+"&isbn="+""+"&pub_house="+""+"&area="+"";
         if(ok!=0){
            return "/t9/subsys/oa/book/act/T9BookTypeAct/findBooks.act?"+url;
          }
         return "/t9/subsys/oa/book/act/T9BookTypeAct/findBooks.act?"+url;
    }
  /**
   *图书导出
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String SysExport(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);  
    OutputStream ops = null;
    Connection conn = null;
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
    try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        T9Person personLogin = (T9Person) request.getSession().getAttribute(
        "LOGIN_USER");
        conn = requestDbConn.getSysDbConn();
        String fileName = URLEncoder.encode("图书信息导出.csv", "UTF-8");
        
        fileName = fileName.replaceAll("\\+", "%20");
        response.setHeader("Cache-control", "private");
        response.setContentType("text/text; charset=GBK");
        response.setHeader("Cache-Control","maxage=3600");
        response.setHeader("Pragma","public");
        response.setHeader("Content-disposition", "attachment; filename=\""
            + fileName + "\"");
        T9BookTypeLogic exportlog = new T9BookTypeLogic();
        ArrayList<T9DbRecord> dbL = exportlog.bookExportlog(book, orderflag, conn, personLogin);
         T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      throw ex;
    }
    return null;
}
  /**
   * 图书导入
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String importBookTypeinfo (HttpServletRequest request,HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    OutputStream ops = null;
    Connection conn = null;
    try{
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      List<T9BookInfo> bookinfo = T9BookTypeLogic.importBookTypeInfo(conn,person,request);
      
      request.setAttribute("bookinfo", bookinfo);
    } catch (Exception ex) {
    	 request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
         request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
         request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    return "/subsys/oa/book/manage/importOkOn.jsp";
   //return "/subsys/oa/asset/manage/mgs.jsp?num=" + num + "&numOne=" + numOne;
  } 
  /**
   * 导出图书模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
public String templetImport(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
  response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
  Connection conn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    T9Person personLogin = (T9Person) request.getSession().getAttribute(
    "LOGIN_USER");
    conn = requestDbConn.getSysDbConn();
    String fileName = URLEncoder.encode("图书信息导出.csv", "UTF-8");
    fileName = fileName.replaceAll("\\+", "%20");
    response.setHeader("Cache-control", "private");
    response.setCharacterEncoding("GBK");
    response.setContentType("text/text; charset=GBK");
    response.setHeader("Cache-Control","maxage=3600");
    response.setHeader("Pragma","public");
    response.setHeader("Content-disposition", "attachment; filename=\""
        + fileName + "\"");
    T9BookTypeLogic exportlog = new T9BookTypeLogic();
    ArrayList<T9DbRecord> dbL = exportlog.templetbookExportlog(conn, personLogin);
    T9CSVUtil.CVSWrite(response.getWriter(), dbL);
  } catch (Exception ex) {
    throw ex;
  }
  return null;
}
  /**
   * 删除图书所选择的图书
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSelectBook(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
      Connection dbConn = null;
      try{
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
         T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
         String deleteStr = request.getParameter("deleteStr");
         T9BookTypeLogic deleteBook = new T9BookTypeLogic();
         int str =  deleteBook.deleteSelectBook(dbConn,deleteStr);
         request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);//RET_STATE返回状态  RETURN_OK正确返回
         request.setAttribute(T9ActionKeys.RET_MSRG, "图书删除成功");//RET_MSRG 返回消息
        // request.setAttribute(T9ActionKeys.RET_DATA, str);//RET_DATA 返回数据
           }catch(Exception ex){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
     return "/core/inc/rtjson.jsp";
  }
  
}
