package t9.subsys.oa.rollmanage.act;


import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.rollmanage.logic.T9RmsStatisticLogic;
public class T9RmsStatisticAct {
  private T9RmsStatisticLogic logic = new T9RmsStatisticLogic();
  private static Logger log = Logger.getLogger("t9.core.act.action.T9TestAct");

  /**
   * 取得文件列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsStatisticJosn(HttpServletRequest request, HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getRmsFileJosn(dbConn, request.getParameterMap(), person);

      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 获取借阅次数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsLendCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String data = "";
      data = String.valueOf(this.logic.getRmsLendCount(dbConn, seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取文件个数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String data = "";
      data = String.valueOf(this.logic.getRmsRollCount(dbConn, seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取卷库select下拉框数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsStatisticSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters2 = null;
      List funcList = new ArrayList();
      funcList.add("rmsRollRoom");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("RMS_ROLL_ROOM"));
      int flag = 0;
      for(Map ms : list){
        String roomName = (String) ms.get("roomName");
        if(!T9Utility.isNullorEmpty(roomName)){
          roomName = T9Utility.encodeSpecial(roomName);
        }
        String roomCode = (String) ms.get("roomCode");
        if(!T9Utility.isNullorEmpty(roomCode)){
          roomCode = T9Utility.encodeSpecial(roomCode);
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",roomCode:\"" + (ms.get("roomCode") == null ? "" : roomCode) + "\"");
        sb.append(",rommName:\"" + (ms.get("roomName") == null ? "" : roomName) + "\"");
        sb.append("},");
      }
      if(sb.length() > 1){
        sb.deleteCharAt(sb.length() - 1); 
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  /**
   * 获取卷库管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsStatisticRollJosn(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getRmsStatisticJson(dbConn,request.getParameterMap(), seqId);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
