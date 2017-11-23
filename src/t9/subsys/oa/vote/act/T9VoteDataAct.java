package t9.subsys.oa.vote.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.vote.data.T9VoteData;
import t9.subsys.oa.vote.data.T9VoteTitle;
import t9.subsys.oa.vote.logic.T9VoteDataLogic;
import t9.subsys.oa.vote.logic.T9VoteItemLogic;
import t9.subsys.oa.vote.logic.T9VoteTitleLogic;

public class T9VoteDataAct {
  /**
   * 新建
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String itemId = request.getParameter("itemId");
      String fieldName = request.getParameter("fieldName");
      String fieldData = request.getParameter("fieldData");
      T9VoteDataLogic dataLogic = new T9VoteDataLogic();
      if(T9Utility.isInteger(itemId)){
        T9VoteData data = new T9VoteData();
        data.setFieldName(fieldName);
        data.setItemId(Integer.parseInt(itemId));
        data.setFieldData(fieldData);
        dataLogic.addData(dbConn, data);
      }
     // request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getDataByItemId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String itemId = request.getParameter("itemId");
      String fieldName = request.getParameter("fieldName");
      if(T9Utility.isNullorEmpty(fieldName)){
        fieldName = "0";
      }
      T9VoteDataLogic dataLogic = new T9VoteDataLogic();
      String dataStr = "[";
      if(T9Utility.isInteger(itemId)){
        List<T9VoteData> dataList = dataLogic.selectDataByItemId(dbConn, itemId,fieldName);
        for (int i = 0; i < dataList.size(); i++) {
          dataStr = dataStr + T9FOM.toJson(dataList.get(i)).toString() + ",";
        }
        if(!dataStr.equals("[")){
          dataStr = dataStr.substring(0, dataStr.length()-1);
        }
      }
      dataStr = dataStr + "]";
      request.setAttribute(T9ActionKeys.RET_DATA,dataStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询投票人数情况
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String selectReadersInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String userPriv = user.getUserPriv();
      if(T9Utility.isNullorEmpty(userPriv)){
        userPriv = "";
      }
      String seqId = request.getParameter("seqId");
      T9VoteItemLogic itemLogic = new T9VoteItemLogic();
      T9VoteTitleLogic titleLogic = new  T9VoteTitleLogic(); 
      T9VoteDataLogic dataLogic = new  T9VoteDataLogic(); 
      String data = "";
      String personCount = "0";//所有投票的人数
      String readerCount = "0";//已经投票的人数
      if(T9Utility.isInteger(seqId)){
        T9VoteTitle title = titleLogic.selectVoteById(dbConn, Integer.parseInt(seqId));
        if(title !=null){
          String deptId = T9Utility.isNullorEmpty(title.getToId()) ? "" : title.getToId();
          String privId = T9Utility.isNullorEmpty(title.getPrivId()) ? "" : title.getPrivId(); 
          String userId = T9Utility.isNullorEmpty(title.getUserId()) ? "" : title.getUserId(); 
          
          personCount = titleLogic.getPersonCount(dbConn, userId, deptId, privId);
          if(!T9Utility.isNullorEmpty(title.getReaders())){
            String[] readersArray = title.getReaders().split(",");
            readerCount = readersArray.length + "";
          }
        }
      }
      data = "{personCount:\"" +personCount + "\",readerCount:\"" + readerCount +"\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, user.getSeqId()+"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
