package t9.core.oaknow.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.logic.T9OAKnowMyPanelLogic;
import t9.core.oaknow.logic.T9OAKnowTypeLogic;
import t9.core.oaknow.util.T9PageUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Out;

/**
 * 分类查找
 * 
 * @author qwx110
 * 
 */
public class T9OAKnowTypeAct{
  private static Logger     log   = Logger.getLogger("t9.core.act.T9OAKnowAct");
  private T9OAKnowTypeLogic logic = new T9OAKnowTypeLogic();
  private T9OAKnowMyPanelLogic panelLogic = new T9OAKnowMyPanelLogic();
  private  T9PageUtil pu = new T9PageUtil();
  public String findTypeAjax(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      int typeId = Integer.parseInt(request.getParameter("typeId").trim());
      int flag = Integer.parseInt(request.getParameter("flag").trim());        //区分；：1 全部，2 已解决，0 未解决       
      int count = logic.findAllCount(dbConn, typeId, flag);
      String no  = request.getParameter("currNo");
      int crrNo = 1;
      if(T9StringUtil.isNotEmpty(no)){
        crrNo = Integer.parseInt(no);
      }
      pu.setElementsCount(count);
      pu.setPageSize(10);
      pu.setCurrentPage(crrNo);
      String data = logic.findAskByType(dbConn, typeId, flag, pu);     
      //request.setAttribute(T9ActionKeys.RET_DATA, data);
      PrintWriter pw = response.getWriter();
      String rtData = "{rtData:"+data+", currNo:"+ crrNo+", totalNo:"+ pu.getPagesCount() +"}";
      //T9Out.println(rtData+"&&&&&&&&&&&&&&&&&&&&");
      pw.println(rtData);
      pw.flush();
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;   
    }
    return null;
  }
  /**
   * 根据分类id查找
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String findType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    T9CategoriesType type = new T9CategoriesType();
    try{
      dbConn = requestDbConn.getSysDbConn();
      int typeId = Integer.parseInt(request.getParameter("typeId").trim());
      int parentId = Integer.parseInt(request.getParameter("parentId").trim());
      //T9Out.println("typeId="+typeId+"---------------"+"parentId="+parentId);
      type = logic.findTypeByTypeId(dbConn, typeId, parentId);// 取分类
     // List<T9OAAsk> askList = logic.findAllByTypeId(dbConn, typeId, 1); 
     // T9Out.println("=============================="+askList.size());
      request.setAttribute("aType", type); // 存分类
     // request.setAttribute("askList", askList);  //某个分类下的所有的问题 
      String showFlag = request.getParameter("showFlag");
      if(T9StringUtil.isNotEmpty(showFlag)){
        request.setAttribute("showFlag", showFlag);
      }
      request.setAttribute("selfId", typeId);
      String oaName = panelLogic.findOAName(dbConn).trim();        
      request.setAttribute("oaName", oaName);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/oaknow/oaknowshowType.jsp";
  }
}
