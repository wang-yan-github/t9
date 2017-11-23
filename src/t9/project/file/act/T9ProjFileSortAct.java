package t9.project.file.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.project.file.data.T9ProjFileSort;
import t9.project.file.logic.T9ProjFileSortLogic;
import t9.project.project.data.T9ProjProject;
import t9.project.project.logic.T9ProjectLogic;

public class T9ProjFileSortAct {
  /**
   * 新增数据
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addProjFileSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjFileSort fileSort = new T9ProjFileSort();
      fileSort.setSortName(request.getParameter("sortName"));
      fileSort.setSortNo(request.getParameter("sortNo"));
      fileSort.setProjId(Integer.parseInt(request.getParameter("projId")));

      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      logic.addFileSort(dbConn, fileSort);
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
   * 删除
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteProjFileSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      logic.deleteFileSort(dbConn, Integer.parseInt(request
          .getParameter("seqId")));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSortList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      String data = "";
      data = logic.getStyleList(dbConn, Integer.parseInt(request
          .getParameter("projId")));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取列表成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取单个目录信息by seqId
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSingleById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      T9ProjFileSort fileSort = logic.getById(dbConn, Integer.parseInt(request
          .getParameter("seqId")));
      data = "{seqId:" + fileSort.getSeqId() + ", projId:\""
          + fileSort.getProjId() + "\", sortNo:\"" + fileSort.getSortNo()
          + "\", sortName:\"" + T9Utility.encodeSpecial(fileSort.getSortName())
          + "\"}";
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
   * 修改目录排序号和名称
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatefileSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String sortNo = request.getParameter("sortNo");
    String sortName = request.getParameter("sortName");
    if (sortNo == null) {
      return "/core/inc/rtjson.jsp";
    }

    if (sortName == null) {
      return "/core/inc/rtjson.jsp";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      T9ProjFileSort fileSort = logic.getById(dbConn, Integer.parseInt(request
          .getParameter("seqId")));
      fileSort.setSortNo(sortNo);
      fileSort.setSortName(sortName);
      logic.updateFileSort(dbConn, fileSort);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 修改目录权限
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatefileSortPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      T9ProjFileSort fileSort = logic.getById(dbConn, Integer.parseInt(request
          .getParameter("seqId")));
      fileSort.setViewUser(request.getParameter("view_user"));
      fileSort.setNewUser(request.getParameter("new_user"));
      fileSort.setManageUser(request.getParameter("manage_user"));
      fileSort.setModifyUser(request.getParameter("modify_user"));
      logic.updateFileSort(dbConn, fileSort);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取项目成员和文档权限
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPriAndUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      data = logic.getPriAndUser(dbConn, Integer.parseInt(request
          .getParameter("seqId")), Integer.parseInt(request
          .getParameter("projId")));
//      System.out.println(data);
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
   * 
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
      T9ProjProject project = logic.getProj(dbConn, Integer.parseInt(request
          .getParameter("seqId")));
      project.setCostType(request.getParameter("costType"));
      project.setCostMoney(request.getParameter("costMoney"));
      logic.updateProj(dbConn, project);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取文档树，有任务名
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAllTree(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String idStr = request.getParameter("id");
    int flag= Integer.parseInt(request.getParameter("flag"));
    int id = 0;
    if (idStr != null && !"".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String queryStr = "";
    try {
      if (id == 0) {
        queryStr = "select  *  from  proj_project";
        if(flag==1){
          queryStr +=" where proj_status=3";
        }else{
          queryStr +=" where proj_status=2";
        }
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(queryStr);
        StringBuffer sb = new StringBuffer("[");
        while (rs.next()) {
          int seqId = rs.getInt("SEQ_ID");
          String sortName = rs.getString("proj_name");
          String extData="isProj";
          int isHaveChild = this.IsHaveChild(dbConn, seqId);
          sb.append("{");
          sb.append("nodeId:\"" + seqId + "\"");
          sb.append(",name:\"" + sortName + "\"");
          sb.append(",isHaveChild:" + isHaveChild + "");
          sb.append(",imgAddress:\"" + request.getContextPath()
              + "/project/images/project.gif\"");
          sb.append(",extData:\"" + extData + "\"");
          sb.append("},");
        }
        if(sb.length()>1){
        	sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());

      } else {
        queryStr = "select * from proj_file_sort where proj_id="+id;
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(queryStr);
        StringBuffer sb = new StringBuffer("[");
        while (rs.next()) {
          int seqId = rs.getInt("SEQ_ID");
          String sortName = rs.getString("sort_name");
          
          sb.append("{");
          sb.append("nodeId:\"" + seqId + "\"");
          sb.append(",name:\"" + sortName + "\"");
          sb.append(",isHaveChild:" + 0 + "");
          sb.append(",imgAddress:\"" + request.getContextPath()
              + "/core/styles/style1/img/dtree/folder.gif\"");
          
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 
   * @author zq 2013-3-27
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String getTree(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
    String projId = request.getParameter("projId");
    int id = 0;
    if (idStr != null && !"".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer("[");
    String queryStr = "select  *  from  proj_file_sort  where  proj_id=  "
        + projId;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        String sortName = rs.getString("sort_name");
        int isHaveChild = this.IsHaveChild(dbConn, seqId);
        sb.append("{");
        sb.append("nodeId:\"" + seqId + "\"");
        sb.append(",name:\"" + sortName + "\"");
        sb.append(",isHaveChild:" + 0 + "");
        sb.append(",imgAddress:\"" + request.getContextPath()
            + "/core/styles/style1/img/dtree/folder.gif\"");
        sb.append("},");
      }
      if(sb.length()>1){
    	  sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
//      System.out.println(sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查找是否有子目录
   * 
   * @author zq 2013-3-27
   * @param conn
   * @param id
   * @return
   * @throws Exception
   */
  public int IsHaveChild(Connection conn, int id) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    try {
      String str = "select * from proj_file_sort WHERE proj_id = " + id;
      stm = conn.createStatement();
      rs = stm.executeQuery(str);
      if (rs.next()) {
        return 1;
      } else {
        return 0;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
  }
  /**
   * 获取文件夹权限
   * @author zq
   * 2013-4-3
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSortPrivById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9ProjFileSortLogic logic = new T9ProjFileSortLogic();
      T9ProjFileSort fileSort=logic.getSortPrivById(dbConn, Integer.parseInt(request.getParameter("seqId")));
      data+="[{newUsers:\""+fileSort.getNewUser()+"\","
      +"viewUsers:\""+fileSort.getViewUser()+"\","
      +"editUsers:\""+fileSort.getModifyUser()+"\","
      +"delUsers:\""+fileSort.getManageUser()+"\"}]";
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
	 * 通过id递归获取文件夹名
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getSortNameById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9ProjFileSortLogic fileSortLogic = new T9ProjFileSortLogic();
		int seqId = Integer.parseInt(request.getParameter("seqId"));
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			//System.out.println("dbConn>>>>>>>>>>>>>>..." + dbConn + "    seqId>>>>" + seqId);
			StringBuffer buffer = new StringBuffer();
			fileSortLogic.getSortNamePath(dbConn, seqId, buffer);
			String sortName = buffer.toString();
			String sortNames[] = sortName.split(",");
			StringBuffer sb = new StringBuffer();
			for (int i = sortNames.length - 1; i >= 0; i--) {
				sb.append(sortNames[i]);
			}
			sb.deleteCharAt(sb.length() - 1);

			String data = "[{sortName:\"" + sb.toString() + "\"}]";
			// System.out.println("data>>>>:"+data);
			T9ProjFileSort fileSort = fileSortLogic.getSortNameById(dbConn, seqId);
			int sortParent=0;
			if (fileSort!=null) {
				sortParent=fileSort.getSortParent();
			}
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(sortParent));
			request.setAttribute(T9ActionKeys.RET_DATA, data);

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

}
