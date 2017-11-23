package t9.subsys.oa.hr.score.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.score.data.T9ScoreItem;
import t9.subsys.oa.hr.score.logic.T9ScoreItemLogic;

public class T9ScoreItemAct {
  public static final String attachmentFolder = "scoreFlow";
  private T9ScoreItemLogic logic = new T9ScoreItemLogic();
  
  /**
   * 获取考核指标集明细列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      int seqId = 0;
      if(!T9Utility.isNullorEmpty(seqIdStr)){
        seqId = Integer.parseInt(seqIdStr);
      }
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"GROUP_ID=" + seqId};
      List funcList = new ArrayList();
      funcList.add("scoreItem");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("SCORE_ITEM"));
      
      for(Map ms : list){
        String itemName = (String) ms.get("itemName");
        if(!T9Utility.isNullorEmpty(itemName)){
          itemName = itemName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",itemName:\"" + (ms.get("itemName") == null ? "" : itemName) + "\"");
        sb.append(",groupId:\"" + (ms.get("groupId") == null ? "" : ms.get("groupId")) + "\"");
        sb.append(",min:\"" + (ms.get("min") == null ? "" : ms.get("min")) + "\"");
        sb.append(",max:\"" + (ms.get("max") == null ? "" : ms.get("max")) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if (list.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb);
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
   * 添加考核指标集明细记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addScoreItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      String seqId = request.getParameter("seqId");
      String itemName = request.getParameter("itemName");
      T9ScoreItem scoreFlow = (T9ScoreItem) T9FOM.build(map, T9ScoreItem.class, "");
      scoreFlow.setGroupId(Integer.parseInt(seqId));
      this.logic.addScoreFlow(dbConn, scoreFlow);
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
   * 删除考核指标集明细一条记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.deleteItem(dbConn, Integer.parseInt(seqId));
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
   * 修改考核指标集明细--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateScoreItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      String seqId = request.getParameter("seqId");
      String groupId = request.getParameter("groupId");
      String itemName = request.getParameter("itemName");
      String min = request.getParameter("min");
      String max = request.getParameter("max");
      T9ScoreItem scoreItem = (T9ScoreItem) T9FOM.build(map, T9ScoreItem.class, "");
      scoreItem.setSeqId(Integer.parseInt(seqId));
      scoreItem.setGroupId(Integer.parseInt(groupId));
      scoreItem.setMin(Double.parseDouble(min));
      scoreItem.setMax(Double.parseDouble(max));
      scoreItem.setItemName(itemName);
      this.logic.updateScoreItem(dbConn, scoreItem);
      
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
