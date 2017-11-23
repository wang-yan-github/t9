package t9.subsys.oa.hr.score.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.score.data.T9ScoreGroup;
import t9.subsys.oa.hr.score.logic.T9ScoreGroupLogic;

public class T9ScoreGroupAct {
  public static final String attachmentFolder = "scoreGroup";
  private T9ScoreGroupLogic logic = new T9ScoreGroupLogic();
  
  /**
   * 新建考核指标集--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addScoreGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      String diaryId = request.getParameter("diaryId");
      String calendarId = request.getParameter("calendarId");
      String userPriv = request.getParameter("role");
      String groupRefer = "";
      T9ScoreGroup scoreGroup = (T9ScoreGroup) T9FOM.build(map, T9ScoreGroup.class, "");
      if(!T9Utility.isNullorEmpty(diaryId)){
        groupRefer = diaryId + ",";
      }
      if(!T9Utility.isNullorEmpty(calendarId)){
        groupRefer += calendarId + ",";
      }
      
      scoreGroup.setGroupRefer(groupRefer);
      scoreGroup.setUserPriv(userPriv);
      this.logic.addScoreGroup(dbConn, scoreGroup);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 考核指标集管理列表--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreGroupList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = this.logic.getScoreGroupList(dbConn, request.getParameterMap());
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
   * 删除一条考核指标集记录--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.deleteSingle(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取考核指标集管理一条记录 --cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreGroupDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      T9ScoreGroup paper = (T9ScoreGroup)this.logic.getScoreGroupDetail(dbConn, Integer.parseInt(seqId));
      if (paper == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "该考核指标集不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(paper);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑考核指标集管理 --cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateScoreGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      String diaryId = request.getParameter("diaryId");
      String calendarId = request.getParameter("calendarId");
      String userPriv = request.getParameter("role");
      String groupRefer = "";
      if(!T9Utility.isNullorEmpty(diaryId)){
        groupRefer = diaryId + ",";
      }
      if(!T9Utility.isNullorEmpty(calendarId)){
        if(!T9Utility.isNullorEmpty(groupRefer)){
          groupRefer += calendarId + ",";
        }
      }
      T9ScoreGroup scoreGroup = (T9ScoreGroup) T9FOM.build(map, T9ScoreGroup.class, "");
      scoreGroup.setGroupRefer(groupRefer);
      scoreGroup.setUserPriv(userPriv);
      this.logic.updateScoreGroup(dbConn, scoreGroup);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }

}
