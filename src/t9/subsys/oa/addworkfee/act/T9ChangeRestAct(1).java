package t9.subsys.oa.addworkfee.act;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.subsys.oa.addworkfee.data.T9ChangeRest;
import t9.subsys.oa.addworkfee.logic.T9ChangeRestLogic;

/**
 * 调休
 * @author Administrator
 *
 */
public class T9ChangeRestAct{
 
  /**
   * 增加
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("deprecation")
  public String addChangeRest(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ChangeRest rest = new T9ChangeRest();
      String year = request.getParameter("year");
      String startTime = request.getParameter("startTime");
      String endTime = request.getParameter("endTime");
      String cType = request.getParameter("cType");
      rest.setYear(Integer.parseInt(year));
      rest.setBeginDate(T9Utility.parseDate(startTime));
      rest.setEndDate(T9Utility.parseDate(endTime));
      rest.setType(Integer.parseInt(cType));
      T9ChangeRestLogic restLogic  = new T9ChangeRestLogic();
      restLogic.addT9ChangeRest(dbConn, rest);
      int dateYear = 1970;
      if(T9Utility.isNullorEmpty(year)){
        dateYear = new Date().getYear()+1900;
      }else{
        dateYear = Integer.parseInt(year);
      }
      List<T9ChangeRest> rests = restLogic.findChangeRestList(dbConn, dateYear+"");
      List<Integer> fYears = restLogic.findYearList(dbConn);
      List<Integer> ints = restLogic.findYearList(dbConn);
      String yearList = list2String(ints);
      request.setAttribute("yearList", yearList);
      request.setAttribute("yearArray", fYears);
      request.setAttribute("rests", rests);
      request.setAttribute("year", dateYear);
      
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/changerest.jsp";
  }
  
  /**
   * 查询节日列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("deprecation")
  public String findFestvialList(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int dateYear = 1970;
      T9ChangeRestLogic restLogic  = new T9ChangeRestLogic();
      String years = request.getParameter("year");
      if(T9Utility.isNullorEmpty(years)){
        dateYear = new Date().getYear()+1900;
      }else{
        dateYear = Integer.parseInt(years);
      }
      List<T9ChangeRest> rests = restLogic.findChangeRestList(dbConn, dateYear+"");
      List<Integer> ints = restLogic.findYearList(dbConn);
      String yearList = list2String(ints);
      request.setAttribute("yearList", yearList);
      request.setAttribute("rests", rests);
      request.setAttribute("year", dateYear);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/changerest.jsp";
  }
  
  /**
   * 删除节日列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delFestvial(HttpServletRequest request, HttpServletResponse response)throws Exception{
    int year = 0;
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ChangeRestLogic restLogic  = new T9ChangeRestLogic();
      String seq_id = request.getParameter("seqId");
      String years = request.getParameter("year");
      int seqId = 0;
      if(!T9Utility.isNullorEmpty(seq_id)){
        seqId = Integer.parseInt(seq_id);
      }
      year = Integer.parseInt(years);
      int k = restLogic.delChangeRest(dbConn, seqId);
      if(k == 0){
        request.setAttribute("msg", "删除失败");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
      List<Integer> ints = restLogic.findYearList(dbConn);
      String yearList = list2String(ints);
      request.setAttribute("yearList", yearList);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return request.getContextPath() + "/subsys/oa/addworkfee/act/T9ChangeRestAct/findFestvialList.act?year=" + year;
  }
  
  /**
   * 编辑某一项
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String editFestival(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9ChangeRestLogic restLogic  = new T9ChangeRestLogic();
    String seq_id = request.getParameter("seqId");
    int seqId = 0;
    if(!T9Utility.isNullorEmpty(seq_id)){
      seqId = Integer.parseInt(seq_id);
    }
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ChangeRest fest = restLogic.findT9ChangeRest(dbConn, seqId);
      List<T9ChangeRest> rests = restLogic.findChangeRestList(dbConn, fest.getYear()+"");
      List<Integer> ints = restLogic.findYearList(dbConn);
      String yearList = list2String(ints);
      request.setAttribute("yearList", yearList);
      request.setAttribute("rests", rests);
      request.setAttribute("year", fest.getYear());
      request.setAttribute("fest", fest);
      request.setAttribute("edit", "_edit");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/subsys/oa/addworkfee/changerest.jsp";
  }
  
  /**
   * 更新节假日
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateFestival(HttpServletRequest request, HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9ChangeRest rest = new T9ChangeRest();
      String year = request.getParameter("year");
      String beginDate = request.getParameter("startTime");
      String endDate = request.getParameter("endTime");
      String seqId = request.getParameter("seqId");
      String cType = request.getParameter("cType");
      rest.setYear(Integer.parseInt(year));
      rest.setBeginDate(T9Utility.parseDate(beginDate));
      rest.setEndDate(T9Utility.parseDate(endDate));
      rest.setType(Integer.parseInt(cType));
      rest.setSeqId(Integer.parseInt(seqId));
      T9ChangeRestLogic restLogic  = new T9ChangeRestLogic();
      int ok = restLogic.updateFestival(dbConn, rest);
      if(ok == 0){
        request.setAttribute("msg", "更新成功失败");
        return "/subsys/inforesource/docmgr/docreceve/msgBox.jsp";
      }
      int dateYear = 1970;
      if(T9Utility.isNullorEmpty(year)){
        dateYear = new Date().getYear()+1900;
      }else{
        dateYear = Integer.parseInt(year);
      }
      List<T9ChangeRest> rests = restLogic.findChangeRestList(dbConn, dateYear+"");
      List<Integer> fYears = restLogic.findYearList(dbConn);
      List<Integer> ints = restLogic.findYearList(dbConn);
      String yearList = list2String(ints);
      request.setAttribute("yearList", yearList);
      request.setAttribute("yearArray", fYears);
      request.setAttribute("rests", rests);
      request.setAttribute("year", dateYear);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    //request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/subsys/oa/addworkfee/changerest.jsp";
  }
  
  private String list2String(List list){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    if(list != null && list.size() >0){
      for(int i=0; i<list.size(); i++){
        sb.append(list.get(i));
        if(i < list.size() -1){
          sb.append(",");
        }
      }
    }
    sb.append("]");
    //T9Out.println(sb.toString());
    return sb.toString();
  }
}
