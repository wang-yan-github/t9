package t9.subsys.oa.book.act;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.subsys.oa.book.data.T9BookManage;
import t9.subsys.oa.book.data.T9Page;
import t9.subsys.oa.book.logic.T9BookRuleLogic;
import t9.subsys.oa.book.logic.T9BookSmsLogic;


/**
 * 借还书管理
 * @author qwx110
 *
 */
public class T9BookRuleAct{

  /**
   * 跳转到借书管理页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String index(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;  
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      List<T9BookManage> bmanages = ruleLogic.borrowConfirm(dbConn, user);       //借书确认
      List<T9BookManage> rmanages = ruleLogic.returnConfirm(dbConn, user);       //还书确认
      int total = ruleLogic.findRegCount(dbConn, user);
      String currNo = request.getParameter("currNo");
      int curruntNo = 1;
      if(T9Utility.isNullorEmpty(currNo)){
        curruntNo = 1;
      }else{
        curruntNo = Integer.parseInt(currNo);
      }      
      T9Page page = new T9Page(15, total, curruntNo);  
      List<T9BookManage> regManages = ruleLogic.findRegManages(dbConn, user, page);
      request.setAttribute("bmanages", bmanages);
      request.setAttribute("rmanages", rmanages);
      request.setAttribute("regManages", regManages);
      request.setAttribute("page", page);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/book/borrow_manage/borrow/index.jsp";
  }
  
  /**
   * 同意或者退回借书申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String agreeBorrOrNot(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;  
    try{
      dbConn = requestDbConn.getSysDbConn();
      String flag = request.getParameter("flag");//1.同意, 0.不同意
      String seqId = request.getParameter("seqId");  //book_manage的seqId
      String bookNo = request.getParameter("bookNo");//图书编号
      bookNo = T9Utility.decodeURL(bookNo);
      String toId = request.getParameter("toId");
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      if("1".equalsIgnoreCase(flag)){          //同意
        ruleLogic.agreeToBorr(dbConn, Integer.parseInt(seqId), bookNo);
        String content = user.getUserName() + "同意了你的借书申请， 图书编号："+bookNo;      
        T9BookSmsLogic.sendSms(user, dbConn, content, "", toId, null); //立即发送给用户
        Date returnDate = ruleLogic.getReturnDate(dbConn, Integer.parseInt(seqId));//获得还书时间
        Date remindDate = T9Utility.getDayBefore(returnDate, 2);   //提前2天提醒
        String remind = "你借的图书(编号：" + bookNo +") 于" + returnDate +"到期， 请按时归还！";      
        T9BookSmsLogic.sendSms(user, dbConn, remind, "", toId, remindDate);
      }else if("0".equalsIgnoreCase(flag)){    //不同意
        ruleLogic.notAgreeToBorr(dbConn, Integer.parseInt(seqId), bookNo);
        String content = user.getUserName() + "拒绝了你的借书申请， 图书编号："+bookNo;   
        T9BookSmsLogic.sendSms(user, dbConn, content, "", toId, null);
      }
    } catch (Exception e){     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/t9/subsys/oa/book/act/T9BookRuleAct/index.act";
  }
  
  /**
   * 同意或者退回还书申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String agreeReturnOrNot(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;  
    try{
      dbConn = requestDbConn.getSysDbConn();
      String flag = request.getParameter("flag");//1.同意, 0.不同意
      String seqId = request.getParameter("seqId");  //book_manage的seqId
      String bookNo = request.getParameter("bookNo");//图书编号
      bookNo = T9Utility.decodeURL(bookNo);
      String toId = request.getParameter("toId");
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      if("1".equalsIgnoreCase(flag)){          //同意
        ruleLogic.agreeToReturn(dbConn, Integer.parseInt(seqId), bookNo);
        String content = user.getUserName() + "同意了你的还书申请， 图书编号："+bookNo;      
        T9BookSmsLogic.sendSms(user, dbConn, content, "", toId, null); //立即发送给用户       
      }else if("0".equalsIgnoreCase(flag)){    //不同意
        ruleLogic.notAgreeToBorr(dbConn, Integer.parseInt(seqId));
        String content = user.getUserName() + "拒绝了你的还书申请， 图书编号："+bookNo;   
        T9BookSmsLogic.sendSms(user, dbConn, content, "", toId, null);
      }      
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/t9/subsys/oa/book/act/T9BookRuleAct/index.act";
  }
  
  /**
   * 借书登记(有管理员直接登记)
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
      
      //发内部短信提醒
      //String content = user.getUserName() + "同意了你的借书申请， 图书编号："+bookNo;   
      //T9BookSmsLogic.sendSms(user, dbConn, content, "", toId, null); //立即发送给用户       
      Date remindDate = T9Utility.getDayBefore(manage.getReturnDate(), 2);  
      //T9Out.println(dateFormat(manage.getReturnDate()));
      String remind = "你借的图书(编号:"+bookNo +")将在"+ dateFormat(manage.getReturnDate())+"到期，请及时归还！"; 
      T9BookSmsLogic.sendSms(user, dbConn, remind, "", toId, remindDate); //还书日期前2天提醒 
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
   * 管理员直接点击还书
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String returnBookByAdmin(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    try{
      dbConn = requestDbConn.getSysDbConn();
      String bookNo = request.getParameter("bookNo");//图书编号
      bookNo = T9Utility.decodeURL(bookNo);
      String borrowId = request.getParameter("borrowId");// 借书id
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      int  ok = ruleLogic.returnBookByReg(dbConn, Integer.parseInt(borrowId), bookNo);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/t9/subsys/oa/book/act/T9BookRuleAct/index.act";
  }
  
  /**
   * 历史记录查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findHistory(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null; 
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      String userId = request.getParameter("toId");
      String bookNo = request.getParameter("bookNo");
      String startDate = request.getParameter("borrowDate");
      String endDate = request.getParameter("returnDate");
      String status = request.getParameter("bookStatus");     
      List<T9BookManage>  manages = 
        ruleLogic.findManagerByBookNo(dbConn, userId, bookNo, startDate, endDate, status, String.valueOf(user.getSeqId()));
      request.setAttribute("manages", manages);
      request.setAttribute("toId", userId);
      request.setAttribute("bookNo", bookNo);
      request.setAttribute("startDate", startDate);
      request.setAttribute("endDate", endDate);
      request.setAttribute("status", status);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }    
    return "/subsys/oa/book/borrow_manage/borrow/result.jsp";
  }
  
  /**
   * 彻底删除历史记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteHistory(HttpServletRequest request,  HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9BookRuleLogic ruleLogic = new T9BookRuleLogic();
      int k = ruleLogic.deleteManage(dbConn, Integer.parseInt(seqId));
      if(k!=0){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"删除成功");
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE,1);
        request.setAttribute(T9ActionKeys.RET_MSRG,"删除失败");
      } 
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public static String dateFormat(Date date){
    if(date != null){
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String ds = sdf.format(date);     
      return ds.toString();
    }else{
      return "";
    }    
  }
}
