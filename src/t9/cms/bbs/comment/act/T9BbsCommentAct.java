package t9.cms.bbs.comment.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.area.logic.T9AreaLogic;
import t9.cms.bbs.comment.logic.T9BbsCommentLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;



public class T9BbsCommentAct{
	/**
	 * 获取我的帖子信息
	 * @param request
	 * @param response
	 * @return
	 */
	public String getMyComment(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			String data=logic.getMyComment(dbConn,person);
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
	public String getCommentsByBid(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			String did = request.getParameter("did");
			String currpage = request.getParameter("currpage");
			String pagesize = request.getParameter("pagesize");
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			String data=logic.getCommentsByDid(dbConn,did,Integer.parseInt(pagesize),Integer.parseInt(currpage));
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
	public String getCommentsMaxCount(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			String did = request.getParameter("did");
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			int  maxcount=logic.getCommentsCountByDid(dbConn,did);
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
	 * 添加新帖子信息
	 * @param request
	 * @param response
	 */
	public void addComment(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			T9FileUploadForm fileForm = new T9FileUploadForm();
			fileForm.parseUploadRequest(request);
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String style=request.getParameter("style");
			String bid=fileForm.getParameter("boardId");
			dbConn = requestDbConn.getSysDbConn();
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			logic.addComment(dbConn,person,fileForm);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			if(style.equals("0")){
				response.sendRedirect("/t9/cms/bbs/portal/myContent.jsp");
			}
			else if(style.equals("1")){
				response.sendRedirect("/t9/cms/bbs/category/index.jsp?bid="+bid);
			}else {
				response.sendRedirect("/t9/cms/bbs/category/index.jsp?bid="+bid);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
	}
	/**
	 * 附件上传
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
  public String fileLoad(HttpServletRequest request, HttpServletResponse response) throws Exception{
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        Connection dbConn = requestDbConn.getSysDbConn();
	    PrintWriter pw = null;
	    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
	    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
	    try {
	      T9FileUploadForm fileForm = new T9FileUploadForm();
	      fileForm.parseUploadRequest(request);
	      T9BbsCommentLogic logic = new T9BbsCommentLogic();
	      StringBuffer sb = logic.uploadMsrg2Json(dbConn,fileForm);
	      String data = "{'state':'0','data':" + sb.toString() + "}";
	      pw = response.getWriter();
	      pw.println(data.trim());
	      pw.flush();
	    }catch(Exception e){
	      pw = response.getWriter();
	      pw.println("{'state':'1'}".trim());
	      pw.flush();
	    } finally {
	      pw.close();
	    }
	    return null;
	  }
  
  /**
   * 浮动菜单文件删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String attachId = request.getParameter("attachId");
    String attachName = request.getParameter("attachName");
    String sSeqId = request.getParameter("seqId");
    //T9Out.println(sSeqId);
    if (attachId == null) {
      attachId = "";
    }
    if (attachName == null) {
      attachName = "";
    }
    int seqId = 0 ;
    if (sSeqId != null && !"".equals(sSeqId)) {
      seqId = Integer.parseInt(sSeqId);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();

      T9BbsCommentLogic Logic = new T9BbsCommentLogic();

      boolean updateFlag = Logic.delFloatFile(dbConn, attachId, attachName , seqId);
     
      String isDel="";
      if (updateFlag) {
        isDel ="isDel"; 

      }
      String data = "{updateFlag:\"" + isDel + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 添加回复
   * @param request
   * @param response
   */
  public void addComment1(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			T9FileUploadForm fileForm = new T9FileUploadForm();
			fileForm.parseUploadRequest(request);
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			String did = fileForm.getParameter("did");
			String content = fileForm.getParameter("content");
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			logic.addComment1(dbConn,did,content,person);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			response.sendRedirect("/t9/cms/bbs/detail/index.jsp?did="+did);
		}catch(Exception ex){
			ex.printStackTrace();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
	}
  
	public void updateLookNums(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			String  seqId=request.getParameter("seqId");
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			logic.updateLookNums(dbConn, Integer.parseInt(seqId));
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功更新数据");
		}catch(Exception ex){
			ex.printStackTrace();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
	}
	/**
	 * 
	 * @param request
	 * @param response
	 */
	public void updateCommentById(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			String  seqId=request.getParameter("cid");
			String  did=request.getParameter("did");
			String  content=request.getParameter("content");
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			StringBuffer sb = new StringBuffer();
			sb.append("update bbs_comment set COMMENT_CONTENT ='"+content+"' where seq_id = '"+seqId+"'");
			logic.updateCommentById(dbConn, sb.toString());
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功更新数据");
			response.sendRedirect("/t9/cms/bbs/detail/index.jsp?did="+did);
		}catch(Exception ex){
			ex.printStackTrace();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
	}
	/**
	 * 修改帖子内容
	 * @param request
	 * @param response
	 */
	public void updateComment1ById(HttpServletRequest request,HttpServletResponse response){
		Connection dbConn;
		try{
			String  did=request.getParameter("did");
			String  content=request.getParameter("content");
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			T9BbsCommentLogic logic=new T9BbsCommentLogic();
			StringBuffer sb = new StringBuffer();
			sb.append("update bbs_comment set COMMENT_CONTENT ='"+content+"' where seq_id = '"+did+"'");
			logic.updateCommentById(dbConn, sb.toString());
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功更新数据");
			response.sendRedirect("/t9/cms/bbs/detail/index.jsp?did="+did);
		}catch(Exception ex){
			ex.printStackTrace();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		}
	}
}