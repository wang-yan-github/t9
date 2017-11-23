package t9.cms.bbs.documentinfo.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.bbs.board.data.T9BbsBoard;
import t9.cms.bbs.board.logic.T9BbsBoardLogic;
import t9.cms.bbs.comment.logic.T9BbsCommentLogic;
import t9.cms.bbs.documentinfo.logic.T9BbsDocumentLogic;
import t9.cms.column.logic.T9ColumnLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9BbsDocumentInfoAct {

	/**
	 * 获取帖子列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	 public String getDocumentsByBoardId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    String bid = request.getParameter("bid");
			String currpage = request.getParameter("currpage");
			String pagesize = request.getParameter("pagesize");
			
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9BbsDocumentLogic logic = new T9BbsDocumentLogic();
		      String data = logic.getDocumentInfoList(dbConn, bid,Integer.parseInt(pagesize),Integer.parseInt(currpage));
		      
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG, "获取帖子列表成功");
		      request.setAttribute(T9ActionKeys.RET_DATA, data);
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/cms/inc/rtrootjson.jsp";
	}
	 /**
	  * 
	  * @param request
	  * @param response
	  * @return
	  * @throws Exception
	  */
	 public String getDocumentsTopsByBoardId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    String bid = request.getParameter("bid");
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9BbsDocumentLogic logic = new T9BbsDocumentLogic();
		      String data = logic.getDocumentInfoTopList(dbConn, bid);
		      
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG, "获取帖子列表成功");
		      request.setAttribute(T9ActionKeys.RET_DATA, data);
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/cms/inc/rtrootjson.jsp";
	}
	 
	 /**
	  * 获取文章 通过文章id
	  * @param request
	  * @param response
	  * @return
	  * @throws Exception
	  */
	 public String getDocumentById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      dbConn = requestDbConn.getSysDbConn();
		      String did = request.getParameter("did");
		      T9BbsDocumentLogic logic = new T9BbsDocumentLogic();
		      String data = logic.getDocumentById(dbConn, did);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取帖子数据");
		      request.setAttribute(T9ActionKeys.RET_DATA, data);
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
	}
	 /**
	  * 
	  * @param request
	  * @param response
	  * @return
	  */
	 public String getDocumentsMaxCount(HttpServletRequest request,HttpServletResponse response){
			Connection dbConn;
			try{
				T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
				T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
				dbConn = requestDbConn.getSysDbConn();
				String bid = request.getParameter("bid");
				T9BbsDocumentLogic logic = new T9BbsDocumentLogic();
				int  maxcount=logic.getDocumentsCountByDid(dbConn, bid);
				String data = "{maxcount:"+maxcount+"}";
				dbConn = requestDbConn.getSysDbConn();
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
				request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
				request.setAttribute(T9ActionKeys.RET_DATA, data);
			}catch(Exception ex){
				ex.printStackTrace();
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			}
			return "/core/inc/rtjson.jsp";
		}
	 /**
	  * 管理文章
	  * @param request
	  * @param response
	  * @return
	  * @throws Exception
	  */
	 public String manageDocumentInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      dbConn = requestDbConn.getSysDbConn();
		      String ids = request.getParameter("ids");
		      String method = request.getParameter("method");
		      String stat = request.getParameter("stat");
		      System.out.println("啊哈哈哈哈哈哈");
		      T9BbsDocumentLogic logic = new T9BbsDocumentLogic();
		      int result = logic.manageDocument(dbConn, ids,stat,method);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取帖子数据");
		     //  request.setAttribute(T9ActionKeys.RET_DATA, data);
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
	}
}
