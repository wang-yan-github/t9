package t9.project.project.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;

import t9.core.codeclass.data.T9CodeItem;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.file.T9FileUploadForm;
import t9.project.project.data.T9ProjProject;
import t9.project.project.logic.T9ProjectLogic;


public class T9ProjectAct{
	
	
	/**
	   * 附件上传
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception 
	   */
	  public String fileLoad(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    PrintWriter pw = null;
	    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
	    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
	    try {
	      T9FileUploadForm fileForm = new T9FileUploadForm();
	      fileForm.parseUploadRequest(request);
	      T9ProjectLogic logic = new T9ProjectLogic();
	      StringBuffer sb = logic.uploadMsrg2Json(fileForm, T9SysProps.getAttachPath());
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
	   * 单文件附件上传

	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    try{
	      T9FileUploadForm fileForm = new T9FileUploadForm();
	      fileForm.parseUploadRequest(request);
	      Map<String, String> attr = null;
	      String attrId = (fileForm.getParameter("attachmentId") == null ) ? "" : fileForm.getParameter("attachmentId");
	      String attrName = (fileForm.getParameter("attachmentName") == null ) ? "" : fileForm.getParameter("attachmentName");
	      String data = "";
	      T9ProjectLogic logic = new T9ProjectLogic();
	      attr = logic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
	      Set<String> keys = attr.keySet();
	      for (String key : keys){
	        String value = attr.get(key);
	        if(attrId != null && !"".equals(attrId)){
	          if(!(attrId.trim()).endsWith(",")){
	            attrId += ",";
	          }
	          if(!(attrName.trim()).endsWith("*")){
	            attrName += "*";
	          }
	        }
	        attrId += key + ",";
	        attrName += value + "*";
	      }
	      data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    }catch (SizeLimitExceededException ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败：文件需要小于" + T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE) + "兆");
	    } catch (Exception e){
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
	    }
	    return "/core/inc/rtuploadfile.jsp";
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

	      T9ProjectLogic logic = new T9ProjectLogic();
	      boolean updateFlag = logic.delFloatFile(dbConn, attachId, attachName , seqId);
	     
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
	   * 获取审批用户
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  
	  public String getApproveUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		  Connection dbConn = null;
		  String privCode=request.getParameter("privCode");
		  try {
		      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requesttDbConn.getSysDbConn();
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      T9ProjectLogic logic = new T9ProjectLogic();
		      String data=logic.getApproveUser(dbConn,person,privCode);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG, "审批人员获取成功!");
		      request.setAttribute(T9ActionKeys.RET_DATA, data);
		  }catch (Exception e) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
		      throw e;
		    }
		  return "/core/inc/rtjson.jsp";
	  }
	  
	  /**
	   * 保存项目基本信息（修改）
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public String saveData(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	      T9ProjectLogic logic = new T9ProjectLogic();
	      int maxId=logic.addData(dbConn, request, person);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
	      request.setAttribute(T9ActionKeys.RET_DATA, "{\"maxId\":"+maxId+"}");
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp"; 
	  }
	  
	  /**
	   * 获取项目基本信息
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public String getBasicInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String seqId=request.getParameter("projId");
        try {
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
          dbConn = requestDbConn.getSysDbConn();
          T9ProjectLogic logic = new T9ProjectLogic();
          String data=logic.getBasicInfo(dbConn,seqId);
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
       * 获取项目基本信息
       * @param request
       * @param response
       * @return
       * @throws Exception
       */
      public String getBasicInfo2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String seqId=request.getParameter("projId");
        try {
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
          dbConn = requestDbConn.getSysDbConn();
          T9ProjectLogic logic = new T9ProjectLogic();
          String data=logic.getBasicInfo2(dbConn,seqId);
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
	   * 提交审批
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	  public String submitApprove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        String seqId=request.getParameter("projId");
        String flag=request.getParameter("flag");
        try {
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
          dbConn = requestDbConn.getSysDbConn();
          T9ProjectLogic logic = new T9ProjectLogic();
          logic.submitApprove(dbConn,person,seqId,flag);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "成功提交审批！");
        } catch (Exception ex) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
          throw ex;
        }
        return "/core/inc/rtjson.jsp"; 
      }
	  
	  /**
	   * 判断是否可以提交审批
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
	   public String ableSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
	        Connection dbConn = null;
	        String seqId=request.getParameter("projId");
	        try {
	          T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	          T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	          dbConn = requestDbConn.getSysDbConn();
	          T9ProjectLogic logic = new T9ProjectLogic();
	          String data=logic.ableApprove(dbConn,seqId);
	          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	          request.setAttribute(T9ActionKeys.RET_MSRG, "成功提交审批！");
	          request.setAttribute(T9ActionKeys.RET_DATA, "{\"flag\":\""+data+"\"}");
	        } catch (Exception ex) {
	          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	          request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	          throw ex;
	        }
	        return "/core/inc/rtjson.jsp"; 
	      }
	   
	   /**
	    * 获取项目列表
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getProjectList(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data = logic.getProjectList(dbConn, request.getParameterMap(), person,request);
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
	    * 恢复项目
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String recoveryProj(HttpServletRequest request, HttpServletResponse response) throws Exception {
         Connection dbConn = null;
         String seqId=request.getParameter("projId");
         try {
           T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
           T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
           dbConn = requestDbConn.getSysDbConn();
           T9ProjectLogic logic = new T9ProjectLogic();
           logic.recoveryProj(dbConn,seqId);
           request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
           request.setAttribute(T9ActionKeys.RET_MSRG, "成功恢复该项目！");
         } catch (Exception ex) {
           request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
           request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
           throw ex;
         }
         return "/core/inc/rtjson.jsp"; 
       }
	   
	   /**
	    * 结束项目
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String endProj(HttpServletRequest request, HttpServletResponse response) throws Exception {
         Connection dbConn = null;
         String seqId=request.getParameter("projId");
         try {
           T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
           T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
           dbConn = requestDbConn.getSysDbConn();
           T9ProjectLogic logic = new T9ProjectLogic();
           logic.endProj(dbConn,seqId);
           request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
           request.setAttribute(T9ActionKeys.RET_MSRG, "成功结束该项目！");
         } catch (Exception ex) {
           request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
           request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
           throw ex;
         }
         return "/core/inc/rtjson.jsp"; 
       }
	   
	   /**
	    * 删除项目
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String deleteProj(HttpServletRequest request, HttpServletResponse response) throws Exception {
         Connection dbConn = null;
         String seqId=request.getParameter("projId");
         try {
           T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
           T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
           dbConn = requestDbConn.getSysDbConn();
           T9ProjectLogic logic = new T9ProjectLogic();
           logic.deleteProj(dbConn,seqId);
           request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
           request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除该项目！");
         } catch (Exception ex) {
           request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
           request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
           throw ex;
         }
         return "/core/inc/rtjson.jsp"; 
       }
	   /**
	    * 获取经费种类和值
	    * 
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getCostTypeAndValue(HttpServletRequest request,
	       HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       String data = "";
	       T9ProjectLogic logic = new T9ProjectLogic();
	       data = logic.getCostTypeAndValue(dbConn, Integer.parseInt(request
	           .getParameter("seqId")));
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
	       request.setAttribute(T9ActionKeys.RET_DATA, data);
	     } catch (Exception ex) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	       throw ex;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   /**
	    * 更新经费
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String updateCost(HttpServletRequest request,
	       HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       T9ProjProject project = logic.getProj(dbConn, Integer.parseInt(request.getParameter("seqId")));
	       project.setCostType(request.getParameter("costType"));
	       project.setCostMoney(request.getParameter("costMoney"));
	       logic.updateProj(dbConn, project);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
	     } catch (Exception ex) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	       throw ex;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   /**
	    * 判断是否存在
	    * 2013-4-9
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String checkIsExist(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       String costId=request.getParameter("costId");
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data = logic.checkIsExist(dbConn,costId);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
	       request.setAttribute(T9ActionKeys.RET_DATA, "{\"flag\":\""+data+"\"}");
	     } catch (Exception ex) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	       throw ex;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   /**
	    * 获取我的项目左侧项目树
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getProjLeftTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data = logic.getProjLeftTree(dbConn, request.getParameterMap(), person, request);
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
	   public String addProjMember(HttpServletRequest request,
	       HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       T9ProjProject project = logic.getProj(dbConn, Integer.parseInt(request.getParameter("seqId")));
	       String projPriv=project.getProjPriv();
	       String projUser=project.getProjUser();
	       String requestUser = request.getParameter("user");
	       String projId=request.getParameter("seqId");
	       String privId=request.getParameter("projPriv");
	       String[] id=logic.ruturnPrivId(dbConn, privId, projId);
	       if(id[0]!=null){
	         if(privId.equals(id[0])){
	             String[] privUsers=projUser.split("\\|");
	             int oldId=Integer.parseInt(id[1]);
	             String oldUser=privUsers[oldId];
	             String[] curUsers=requestUser.split(",");
	             for(int j=0;j<curUsers.length;j++){
	               if(!logic.isHasUserId(oldUser, curUsers[j])){
	                 privUsers[oldId]=privUsers[oldId]+curUsers[j]+",";
	               }
	             }
	             String newPrivUser="";
	             for(int i=0;i<privUsers.length;i++){
	               if("".equals(privUsers[i])){
	                 continue;
	               }
	               newPrivUser+="|"+privUsers[i];
	             }
	             project.setProjUser(newPrivUser);
	           }
            
	       }else{
	         project.setProjPriv((project.getProjPriv()==null?"":project.getProjPriv())+"|"+privId);
	         project.setProjUser((project.getProjUser()==null?"":project.getProjUser())+"|"+requestUser+",");
	       }
	       logic.updateProj(dbConn, project);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "添加成员成功！");
	     } catch (Exception ex) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	       throw ex;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   /**
	    * 获取用户列表
	    * 2013-3-26
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getUserList(HttpServletRequest request,
         HttpServletResponse response) throws Exception {
       Connection dbConn = null;
       try {
         T9RequestDbConn requestDbConn = (T9RequestDbConn) request
             .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
         dbConn = requestDbConn.getSysDbConn();
         String data = "";
         StringBuffer sb = new StringBuffer("[");
         T9ProjectLogic projectLogic = new T9ProjectLogic();
         T9PersonLogic personLogic = new T9PersonLogic();
         T9ProjProject project = projectLogic.getProj(dbConn, Integer.parseInt(request.getParameter("seqId")));
         String[] users={};
         String[] privRoles={};
         if(project.getProjUser()!=null){
           users = project.getProjUser().split("\\|");
           privRoles= project.getProjPriv().split("\\|");
         }
         for (int i = 1; i < users.length; i++) {
           T9CodeItem codeItem = projectLogic.getCodeItem(dbConn, Integer.parseInt(privRoles[i]));
           String classDesc="";
           if(codeItem!=null && !"".equals(codeItem)){
             classDesc=codeItem.getClassDesc();
           }
           String[] projUser=users[i].split(",");
           sb.append("{");
           sb.append("\"privId\":\""+privRoles[i]+"\",");
           sb.append("\"codeName\":\""+classDesc+"\",");
           sb.append("\"user\":\"");
           for (int j = 0; j < projUser.length; j++) {
             T9Person person = personLogic.getPerson(dbConn,projUser[j]);
             sb.append(""+person.getUserName()+",");
           }
           sb.append("\"},");
         }
         if(sb.length()>3){
           sb=sb.deleteCharAt(sb.length()-1);
         }
         sb.append("]");
         data=sb.toString();
         request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
         request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
         request.setAttribute(T9ActionKeys.RET_DATA, data);
       } catch (Exception ex) {
         request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
         request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
         throw ex;
       }
       return "/core/inc/rtjson.jsp";
     }

    /**
	    * 获取项目审批列表
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getApproveList(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data = logic.getApproveList(dbConn,request.getParameterMap(), person);
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
	    * 获取项目审批列表
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getNoApproveList(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requestDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data = logic.getNoApproveList(dbConn,request.getParameterMap(), person);
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
 * 添加审批意见
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
	   public String subApprove(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       String projId=request.getParameter("projId");
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       logic.subApprove(dbConn,request,projId,person);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   /**
	    * 判断是否有新建（立项）权限
	    * 2013-3-25
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String isHasNewPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       String projId=request.getParameter("projId");
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String flag=logic.isHasNewPriv(dbConn,person);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	       request.setAttribute(T9ActionKeys.RET_DATA, "{\"flag\":\""+flag+"\"}");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   
	   /**
	    * 判断是否免签
	    * 2013-3-25
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String isApprove(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String flag=logic.isApprove(dbConn,person);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	       request.setAttribute(T9ActionKeys.RET_DATA, "{\"flag\":\""+flag+"\"}");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   /**
	    * 判断是否分配了项目人员
	    * 2013-4-9
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String isHasProjUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       String projId=request.getParameter("projId");
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String flag=logic.isHasProjUser(dbConn,projId);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	       request.setAttribute(T9ActionKeys.RET_DATA, "{\"flag\":\""+flag+"\"}");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   
	   /**
	    * 导出项目模板
	    * 2013-3-25
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String exportProj(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     String projId=request.getParameter("projId");
	     String modelName=request.getParameter("modelName");
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       logic.exportProj(dbConn,projId,modelName);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   /**
	    * 获取模板列表
	    * 2013-3-26
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String getModelList(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data=logic.getModelList(dbConn);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	       request.setAttribute(T9ActionKeys.RET_DATA, data);
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   /**
	    * 从模板导入项目信息
	    * 2013-3-26
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String importProj(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     String modelName=request.getParameter("modelName");
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       String data=logic.importProj(dbConn,modelName);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	       request.setAttribute(T9ActionKeys.RET_DATA, "{\"projId\":\""+data+"\"}");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   /**
	    * 删除模板
	    * 2013-3-26
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String deleteModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     String fileNames=request.getParameter("fileNames");
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       logic.deleteModel(dbConn,fileNames);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   
	   /**
	    * 删除项目成员
	    * 2013-3-28
	    * @author ny
	    * @param request
	    * @param response
	    * @return
	    * @throws Exception
	    */
	   public String delProjPrivUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
	     Connection dbConn = null;
	     String privId=request.getParameter("privId");
	     String projId=request.getParameter("projId");
	     try {
	       T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	       dbConn = requesttDbConn.getSysDbConn();
	       T9ProjectLogic logic = new T9ProjectLogic();
	       logic.delProjPrivUser(dbConn,privId,projId);
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	       request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
	     }catch (Exception e) {
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	       throw e;
	     }
	     return "/core/inc/rtjson.jsp";
	   }
	   
	   
}
