package t9.subsys.oa.book.act;
import java.net.URLDecoder;
import java.sql.Connection;
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
import t9.subsys.oa.book.data.T9BookManage;
import t9.subsys.oa.book.logic.T9BookRuleLogic;
import t9.subsys.oa.book.logic.T9BookSmsLogic;
import t9.subsys.oa.book.logic.T9ReturnBookSelectLogic;


/**
 *
 */
public class T9ReturnBookManageAct{
 /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String regBorrowBook(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    try{
      dbConn = requestDbConn.getSysDbConn();
      String toId = request.getParameter("toId");
      String bookNo = request.getParameter("bookNo");
      String borrowDate = request.getParameter("borrowDate"); //借书日期
      String returnDate = request.getParameter("returnDate"); //还书日期
      String remark = request.getParameter("remark");
      T9BookManage manage = new T9BookManage();
      manage.setBookNo(bookNo);
      manage.setBorrowRemark(remark);
      manage.setBuserId(toId);
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
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
        Date browDate = T9Utility.parseDate("yyyy-MM-dd",borrowDate);   
        manage.setBorrowDate(browDate);
        String boDate = T9Utility.getDateTimeStr(browDate).split(" ")[0];
        if(T9Utility.isNullorEmpty(returnDate)){
          Date afterDate = T9Utility.getDayAfter(boDate, 30);
          String afDate = T9Utility.getDateTimeStr(afterDate).split(" ")[0];       
          manage.setReturnDate(T9Utility.parseDate("yyyy-MM-dd",afDate));
        }else{
          manage.setReturnDate(T9Utility.parseDate("yyyy-MM-dd",returnDate));
        } 
      }
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      ruleLogic.regBookByAdmin(dbConn, user, manage);         //存数据库
      String content = user.getUserName() + "同意了你的借书申请， 图书编号："+bookNo;   
      T9BookSmsLogic.sendSms(user, dbConn, content, "", toId, null); //立即发送给用户       
      Date remindDate = T9Utility.getDayBefore(manage.getReturnDate(), 2);  
       request.setAttribute("message", "保存成功");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/subsys/oa/book/borrow_manage/return/msg.jsp";
  }
  /**
   * 还书管理查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String returnBookManage(HttpServletRequest request,  HttpServletResponse response)throws Exception{
       Connection dbConn = null; 
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String toId = request.getParameter("toId");
      String bookNo = request.getParameter("bookNo");
      String beginDate = request.getParameter("borrowDate");
      String endDate = request.getParameter("returnDate");
      String bookStatus = request.getParameter("bookStatus");
      T9BookManage manage = new T9BookManage();
      manage.setBuserId(toId);//借书人ID
      manage.setBookNo(bookNo);
      manage.setBookStatus(bookStatus);
      T9ReturnBookSelectLogic returnbook = new T9ReturnBookSelectLogic();
      List<T9BookManage> manages = returnbook.returnBookSelect(dbConn,person,beginDate,endDate, manage);
      request.setAttribute("manage", manages);
      request.setAttribute("toId", toId);
      request.setAttribute("bookNo", bookNo);
      request.setAttribute("startDate", beginDate);
      request.setAttribute("endDate", endDate);
      request.setAttribute("status", bookStatus);
    }catch (Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw e;
      }
    return "/subsys/oa/book/borrow_manage/borrow/rebooksearch.jsp";
  }
  /**
   * 点击还书 图书信息状态改变
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateBookManage(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    Connection dbConn = null; 
    String url = "";
    try{
   T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   dbConn = requestDbConn.getSysDbConn();
   T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
   int okManage = 0;
   int okInfo = 0;
   String toId = request.getParameter("userId");
   String bookNo = request.getParameter("bookNo");
   String beginDate = request.getParameter("startDate");
   String endDate = request.getParameter("endDate");
   String bookStatus = request.getParameter("status");
   String seqId = request.getParameter("seqId");
   String bookNo1 = request.getParameter("bookNo1");
   bookNo1 = URLDecoder.decode(bookNo1, "UTF-8");
   T9ReturnBookSelectLogic updatebook = new T9ReturnBookSelectLogic();
   okManage = updatebook.updateBookManage(dbConn, seqId);
   okInfo = updatebook.updateBookInfo(dbConn, bookNo1);
   url = "toId="+toId+"&bookNo="+bookNo +"&beginDate="+beginDate+"&endDate="+endDate+"&bookStatus="+bookStatus;
   if(okManage!=0 && okInfo!=0)
      return "/t9/subsys/oa/book/act/T9ReturnBookManageAct/returnBookManage.act?"+url;
   
   /*T9BookManage manage = new T9BookManage();
   manage.setBuserId(toId);//借书人ID
   manage.setBookNo(bookNo);
   manage.setBookStatus(bookStatus);
   T9ReturnBookSelectLogic returnbook = new T9ReturnBookSelectLogic();
   List<T9BookManage> manages = returnbook.returnBookSelect(dbConn,person,beginDate,endDate, manage);
   request.setAttribute("manage", manages);
   request.setAttribute("toId", toId);
   request.setAttribute("bookNo", bookNo);
   request.setAttribute("startDate", beginDate);
   request.setAttribute("endDate", endDate);
   request.setAttribute("status", bookStatus);*/
 }catch (Exception e){
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
     request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
     throw e;
   }
 return "/t9/subsys/oa/book/act/T9ReturnBookManageAct/returnBookManage.act?"+url;
}
  /**
   * 删除并保存历史记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSaveBook(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    Connection dbConn = null; 
    String url ="";
    try{
   T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   dbConn = requestDbConn.getSysDbConn();
   T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
   int okdelSave = 0;
   int okInfo = 0;
   String toId = request.getParameter("userId");
   String bookNo = request.getParameter("bookNo");
   String beginDate = request.getParameter("startDate");
   String endDate = request.getParameter("endDate");
   String bookStatus = request.getParameter("status");
   String seqId = request.getParameter("seqId");
   String bookNo2 = request.getParameter("bookNo2");
   bookNo2 = T9Utility.decodeURL(bookNo2);
   //System.out.println(seqId);
   T9ReturnBookSelectLogic deleteSavebook = new T9ReturnBookSelectLogic();
   okdelSave = deleteSavebook.deleteSaveBook(dbConn, seqId);
  
    url = "toId="+toId+"&bookNo="+bookNo +"&beginDate="+beginDate+"&endDate="+endDate+"&bookStatus="+bookStatus;
   if(okdelSave!=0)
      return "/t9/subsys/oa/book/act/T9ReturnBookManageAct/returnBookManage.act?"+url;
   
 
 }catch (Exception e){
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
     request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
     throw e;
   }
 return "/t9/subsys/oa/book/act/T9ReturnBookManageAct/returnBookManage.act?"+url;
}
}
