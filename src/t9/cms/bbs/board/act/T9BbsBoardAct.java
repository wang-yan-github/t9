package t9.cms.bbs.board.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.bbs.board.data.T9BbsArea;
import t9.cms.bbs.board.data.T9BbsBoard;
import t9.cms.bbs.board.logic.T9BbsBoardLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9BbsBoardAct {

  /**
  * bbs 版块
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String addBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9BbsBoard board = (T9BbsBoard)T9FOM.build(request.getParameterMap());
      T9BbsBoardLogic logic = new T9BbsBoardLogic();
      logic.addBoard(dbConn, board, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
  * bbs 版块
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String modifyBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9BbsBoard board = (T9BbsBoard)T9FOM.build(request.getParameterMap());
      T9BbsBoardLogic logic = new T9BbsBoardLogic();
      logic.modifyBoard(dbConn, board, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
  * bbs 专区
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String addBbsArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9BbsArea area = (T9BbsArea)T9FOM.build(request.getParameterMap());
      T9BbsBoardLogic logic = new T9BbsBoardLogic();
      logic.addArea(dbConn, area, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
  * bbs 专区
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String getBbsArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9BbsBoardLogic logic = new T9BbsBoardLogic();
      String data = logic.getArea(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
  * bbs 专区
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String getBbsBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String areaIdStr = (String)request.getParameter("areaId");
    int areaId = 0;
    if(T9Utility.isInteger(areaIdStr)){
      areaId = Integer.parseInt(areaIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9BbsBoardLogic logic = new T9BbsBoardLogic();
      String data = logic.getBoard(dbConn, areaId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
  * bbs 专区
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String getBbsBoardById(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStr = (String)request.getParameter("seqId");
    int seqId = 0;
    if(T9Utility.isInteger(seqIdStr)){
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      T9BbsBoard board = (T9BbsBoard)orm.loadObjSingle(dbConn, T9BbsBoard.class, seqId);
      StringBuffer data = T9FOM.toJson(board);
      T9BbsBoardLogic logic = new T9BbsBoardLogic();
      String data1 = logic.getBoardById(dbConn, board, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data1);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 获取板块信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getBbsBoardInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    String seqIdStr = (String)request.getParameter("seqId");
	    int seqId = 0;
	    if(T9Utility.isInteger(seqIdStr)){
	      seqId = Integer.parseInt(seqIdStr);
	    }
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9ORM orm = new T9ORM();
	      
	      
	      T9BbsBoard board = (T9BbsBoard)orm.loadObjSingle(dbConn, T9BbsBoard.class, seqId);
	      StringBuffer data = T9FOM.toJson(board);
	      T9BbsBoardLogic logic = new T9BbsBoardLogic();
	      String data1 = logic.getBoardInfoById(dbConn, board, data);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data1);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
  
  
  
  public String getBbsAreaAndBoard(HttpServletRequest request, HttpServletResponse response)throws Exception{
	  Connection dbConn = null;
	  try {
		  T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		  dbConn = requestDbConn.getSysDbConn();
		  T9BbsBoardLogic logic = new T9BbsBoardLogic();
		  String data = logic.getBbsAreaAndBoard(dbConn);
		  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		  request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		  request.setAttribute(T9ActionKeys.RET_DATA, data);
	  } catch (Exception ex) {
		  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		  throw ex;
	  }
	  return "/core/inc/rtjson.jsp";
  }
  
  
  /**
  * bbs 专区
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String getBbsAreaById(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStr = (String)request.getParameter("seqId");
    int seqId = 0;
    if(T9Utility.isInteger(seqIdStr)){
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      
      T9BbsArea area = (T9BbsArea)orm.loadObjSingle(dbConn, T9BbsArea.class, seqId);
      StringBuffer data = T9FOM.toJson(area);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取区域和板块列表树
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getLeftTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    
	    String bid = (String)request.getParameter("bid");
	    System.out.println("bid");
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9BbsBoardLogic bbsBoardLogic = new T9BbsBoardLogic();
	     String data =  bbsBoardLogic.getLeftTree(dbConn, bid);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/cms/inc/rtBBSjson.jsp";
	 }
  
  
  /**
  * bbs 版块
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String modifyBbsArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      
      T9BbsArea area = (T9BbsArea)T9FOM.build(request.getParameterMap());
      orm.updateSingle(dbConn, area);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
  * bbs 版块
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String deleteBbsArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStr = (String)request.getParameter("seqId");
    int seqId = 0;
    if(T9Utility.isInteger(seqIdStr)){
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      
      int flag = 0;
      String filters[] = {" AREA_ID =" + seqId};
      List<T9BbsBoard> boardList = (List<T9BbsBoard>)orm.loadListSingle(dbConn, T9BbsBoard.class, filters);
      if(boardList.size() == 0){
        orm.deleteSingle(dbConn, T9BbsArea.class, seqId);
        flag = 1;
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+flag+"\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
  * bbs 版块
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String deleteBbsBoard(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStr = (String)request.getParameter("seqId");
    int seqId = 0;
    if(T9Utility.isInteger(seqIdStr)){
      seqId = Integer.parseInt(seqIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      
      orm.deleteSingle(dbConn, T9BbsBoard.class, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取帖子的今日数，昨日数及总帖子数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getBbsComments(HttpServletRequest request, HttpServletResponse response) throws Exception{
	  Connection dbConn = null;
	  try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
		  T9BbsBoardLogic logic = new T9BbsBoardLogic();
		  String data = logic.getBbsComments(dbConn);
		  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		  request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		  request.setAttribute(T9ActionKeys.RET_DATA, data);
	  }catch(Exception ex){
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		  ex.printStackTrace();
	  }
	  return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 判斷當前用戶是不是當前板塊的版主
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String isBoardManager (HttpServletRequest request, HttpServletResponse response) throws Exception{
	  Connection dbConn = null;
	  try{
		  T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		  T9BbsBoardLogic logic = new T9BbsBoardLogic();
		  String bid=request.getParameter("bid");
		   int flag=logic.isBoardManager(dbConn, person, bid);
		   String data="{flag:"+flag+"}";
		   request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		   request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		  request.setAttribute(T9ActionKeys.RET_DATA, data);
       }catch(Exception ex){
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			ex.printStackTrace();
  }
  return "/core/inc/rtjson.jsp";
  }
}
