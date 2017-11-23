package t9.cms.permissions.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.permissions.logic.T9PermissionsLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9PermissionsAct {

  /**
   * 获取cms人员权限

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonIdStr(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn;
    String userType = request.getParameter("userType");
    String seqId = request.getParameter("seqId");
    if(T9Utility.isNullorEmpty(seqId)){
      seqId = "0";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PermissionsLogic logic = new T9PermissionsLogic();
      String data = logic.getPermissions(dbConn, userType, seqId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 设置cms人员权限

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setVisitById(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn;
    String stringInfo = request.getParameter("stringInfo");
    String userType = request.getParameter("userType");
    String seqId = request.getParameter("seqId");
    String override = request.getParameter("override");
    if(T9Utility.isNullorEmpty(seqId)){
      seqId = "0";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PermissionsLogic logic = new T9PermissionsLogic();
      logic.setPermissions(dbConn, userType, seqId, stringInfo);
      if(!T9Utility.isNullorEmpty(override)){
        logic.setPermissionsChild(dbConn, userType, seqId, stringInfo);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取cms人员权限-栏目

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonIdStrColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn;
    String userType = request.getParameter("userType");
    String seqId = request.getParameter("seqId");
    if(T9Utility.isNullorEmpty(seqId)){
      seqId = "0";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PermissionsLogic logic = new T9PermissionsLogic();
      String data = logic.getPermissionsColumn(dbConn, userType, seqId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 设置cms人员权限-栏目

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setVisitByIdColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn;
    String stringInfo = request.getParameter("stringInfo");
    String userType = request.getParameter("userType");
    String seqId = request.getParameter("seqId");
    String override = request.getParameter("override");
    if(T9Utility.isNullorEmpty(seqId)){
      seqId = "0";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PermissionsLogic logic = new T9PermissionsLogic();
      logic.setPermissionsColumn(dbConn, userType, seqId, stringInfo);
      if(!T9Utility.isNullorEmpty(override)){
        logic.setPermissionsChildColumn(dbConn, userType, seqId, stringInfo);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取CMS树型结构-无权限

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getColumnTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String id = request.getParameter("id");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9PermissionsLogic logic = new T9PermissionsLogic();
      String data = "";
      if (!T9Utility.isNullorEmpty(id) && !id.equals("0")) {
        String idArry[] = id.split(",");
        if (idArry != null && idArry.length > 0) {
          data = logic.getColumnTree(dbConn, idArry[0], idArry[1], person);
        }
      } else {
        data = logic.getStationTree(dbConn, person);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 设置cms人员权限-栏目

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setContentPermissions(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn;
    String stringInfo = request.getParameter("stringInfo");
    String userType = request.getParameter("userType");
    String seqId = request.getParameter("seqId");
    String override = request.getParameter("override");
    if(T9Utility.isNullorEmpty(seqId)){
      seqId = "0";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PermissionsLogic logic = new T9PermissionsLogic();
      logic.setPermissionsColumn(dbConn, userType, seqId, stringInfo);
      if(!T9Utility.isNullorEmpty(override)){
        logic.setPermissionsChildColumn(dbConn, userType, seqId, stringInfo);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  /**
   * 批量设置站点或者栏目权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
	public String setBatchPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		String seqString = request.getParameter("seqId");	
		String setIdStr = request.getParameter("idStr");		//设置的id串	16||
		String check = request.getParameter("check");				//要设置的选项   OWNER,
		String opt = request.getParameter("opt");						//添加或删除操作  addPriv
		String override = request.getParameter("override");
		String flag=request.getParameter("flag");
		String checks = check.substring(0, check.length() - 1);
		int seqId = 0;
		if (seqString != "") {
			seqId = Integer.parseInt(seqString);
		}

		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9PermissionsLogic logic = new T9PermissionsLogic();
			if("column".equals(flag)){         //栏目权限管理
			if ("addPriv".equals(opt)) {		//添加权限
				if (checks != "") {
					String[] checkStrs = checks.split(",");
					for (int i = 0; i < checkStrs.length; i++) {
						if ("VISIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "VISIT_USER",flag);
						}
						if ("EDIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "EDIT_USER",flag);
						}
						if ("DEL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "DEL_USER",flag);
            }
						if ("NEW_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "NEW_USER",flag);
						}
						if ("REL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "REL_USER",flag);
						}
						
						if(!T9Utility.isNullorEmpty(override)){				//是否把当前栏目的权限应用于其所有子栏目
							if ("VISIT_USER".equals(checkStrs[i])) {
								logic.updateColumnChildAdd(dbConn, seqId, setIdStr, "VISIT_USER",flag);
							}
							if ("EDIT_USER".equals(checkStrs[i])) {
								logic.updateColumnChildAdd(dbConn, seqId, setIdStr, "EDIT_USER",flag);
							}
							if ("DEL_USER".equals(checkStrs[i])) {
								logic.updateColumnChildAdd(dbConn, seqId, setIdStr, "DEL_USER",flag);
							}
							if ("NEW_USER".equals(checkStrs[i])) {
								logic.updateColumnChildAdd(dbConn, seqId, setIdStr, "NEW_USER",flag);
							}
							if ("REL_USER".equals(checkStrs[i])) {
								logic.updateColumnChildAdd(dbConn, seqId, setIdStr, "REL_USER",flag);
							}
						  }
					}
				}

			} else if ("delPriv".equals(opt)) {         //删除权限
				if (checks != "") {
					String[] checkStrs = checks.split(",");
					for (int i = 0; i < checkStrs.length; i++) {
						if ("VISIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "VISIT_USER",flag);
						}
						if ("EDIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "EDIT_USER",flag);
						}
						if ("DEL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "DEL_USER",flag);
						}
						if ("NEW_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "NEW_USER",flag);
						}
						if ("REL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "REL_USER",flag);
						}
						
						if(!T9Utility.isNullorEmpty(override)){        //是否把当前栏目的权限应用于其所有子栏目
							if ("VISIT_USER".equals(checkStrs[i])) {
								logic.updateColumnChildDel(dbConn, seqId, setIdStr, "VISIT_USER",flag);
							}
							if ("EDIT_USER".equals(checkStrs[i])) {
								logic.updateColumnChildDel(dbConn, seqId, setIdStr, "EDIT_USER",flag);
							}
							if ("DEL_USER".equals(checkStrs[i])) {
								logic.updateColumnChildDel(dbConn, seqId, setIdStr, "DEL_USER",flag);
							}
							if ("NEW_USER".equals(checkStrs[i])) {
								logic.updateColumnChildDel(dbConn, seqId, setIdStr, "NEW_USER",flag);
							}
							if ("REL_USER".equals(checkStrs[i])) {
								logic.updateColumnChildDel(dbConn, seqId, setIdStr, "REL_USER",flag);
							}
						  }
					}
				}
			}
		}else if("station".equals(flag)){       //站点权限管理
			if ("addPriv".equals(opt)) {        //添加权限
				if (checks != "") {
					String[] checkStrs = checks.split(",");
					for (int i = 0; i < checkStrs.length; i++) {
						if ("VISIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "VISIT_USER",flag);
						}
						if ("EDIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "EDIT_USER",flag);
						}
						if ("DEL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "DEL_USER",flag);
            }
						if ("NEW_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "NEW_USER",flag);
						}
						if ("REL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "REL_USER",flag);
						}
						if(!T9Utility.isNullorEmpty(override)){     //是否把当前栏目的权限应用于其所有子栏目
							if ("VISIT_USER".equals(checkStrs[i])) {
								logic.updateStationChildAdd(dbConn, seqId, setIdStr, "VISIT_USER",flag);
							}
							if ("EDIT_USER".equals(checkStrs[i])) {
								logic.updateStationChildAdd(dbConn, seqId, setIdStr, "EDIT_USER",flag);
							}
							if ("DEL_USER".equals(checkStrs[i])) {
								logic.updateStationChildAdd(dbConn, seqId, setIdStr, "DEL_USER",flag);
							}
							if ("NEW_USER".equals(checkStrs[i])) {
								logic.updateStationChildAdd(dbConn, seqId, setIdStr, "NEW_USER",flag);
							}
							if ("REL_USER".equals(checkStrs[i])) {
								logic.updateStationChildAdd(dbConn, seqId, setIdStr, "REL_USER",flag);
							}
						  }
					}
				}

			} else if ("delPriv".equals(opt)) {                   //删除权限
				if (checks != "") {
					String[] checkStrs = checks.split(",");
					for (int i = 0; i < checkStrs.length; i++) {
						if ("VISIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "VISIT_USER",flag);
						}
						if ("EDIT_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "EDIT_USER",flag);
						}
						if ("DEL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "DEL_USER",flag);
						}
						if ("NEW_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "NEW_USER",flag);
						}
						if ("REL_USER".equals(checkStrs[i])) {
							logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "REL_USER",flag);
						}
						if(!T9Utility.isNullorEmpty(override)){             //是否把当前栏目的权限应用于其所有子栏目
							if ("VISIT_USER".equals(checkStrs[i])) {
								logic.updateStationChildDel(dbConn, seqId, setIdStr, "VISIT_USER",flag);
							}
							if ("EDIT_USER".equals(checkStrs[i])) {
								logic.updateStationChildDel(dbConn, seqId, setIdStr, "EDIT_USER",flag);
							}
							if ("DEL_USER".equals(checkStrs[i])) {
								logic.updateStationChildDel(dbConn, seqId, setIdStr, "DEL_USER",flag);
							}
							if ("NEW_USER".equals(checkStrs[i])) {
								logic.updateStationChildDel(dbConn, seqId, setIdStr, "NEW_USER",flag);
							}
							if ("REL_USER".equals(checkStrs[i])) {
								logic.updateStationChildDel(dbConn, seqId, setIdStr, "REL_USER",flag);
							}
						  }
					}
				}
			}
		}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "批量设置权限成功");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	  /**
	   * 批量设置文章权限
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
		public String setContentBatchPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
			Connection dbConn = null;
			String seqString = request.getParameter("seqId");	
			String setIdStr = request.getParameter("idStr");		//设置的id串	16||
			String check = request.getParameter("check");				//要设置的选项   OWNER,
			String opt = request.getParameter("opt");						//添加或删除操作  addPriv
			String override = request.getParameter("override");
			String flag=request.getParameter("flag");
			String checks = check.substring(0, check.length() - 1);
			int seqId = 0;
			if (seqString != "") {
				seqId = Integer.parseInt(seqString);
			}

			try {
				T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
				dbConn = requestDbConn.getSysDbConn();
				T9PermissionsLogic logic = new T9PermissionsLogic();
				if("content".equals(flag)){         //栏目权限管理
				if ("addPriv".equals(opt)) {		//添加权限
					if (checks != "") {
						String[] checkStrs = checks.split(",");
						for (int i = 0; i < checkStrs.length; i++) {
							if ("EDIT_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "EDIT_USER_CONTENT",flag);
							}
							if ("APPROVAL_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "APPROVAL_USER_CONTENT",flag);
							}
							if ("RELEASE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "RELEASE_USER_CONTENT",flag);
	            }
							if ("RECEVIE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "RECEVIE_USER_CONTENT",flag);
							}
							if ("ORDER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideAdd(dbConn, seqId, setIdStr, "ORDER_CONTENT",flag);
							}
							
							if(!T9Utility.isNullorEmpty(override)){				//是否把当前栏目的权限应用于其所有子栏目
								if ("EDIT_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentAdd(dbConn, seqId, setIdStr, "EDIT_USER_CONTENT",flag);
								}
								if ("APPROVAL_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentAdd(dbConn, seqId, setIdStr, "APPROVAL_USER_CONTENT",flag);
								}
								if ("RELEASE_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentAdd(dbConn, seqId, setIdStr, "RELEASE_USER_CONTENT",flag);
								}
								if ("RECEVIE_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentAdd(dbConn, seqId, setIdStr, "RECEVIE_USER_CONTENT",flag);
								}
								if ("ORDER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentAdd(dbConn, seqId, setIdStr, "ORDER_CONTENT",flag);
								}
							  }
						}
					}

				} else if ("delPriv".equals(opt)) {         //删除权限
					if (checks != "") {
						String[] checkStrs = checks.split(",");
						for (int i = 0; i < checkStrs.length; i++) {
							if ("EDIT_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "EDIT_USER_CONTENT",flag);
							}
							if ("APPROVAL_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "APPROVAL_USER_CONTENT",flag);
							}
							if ("RELEASE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "RELEASE_USER_CONTENT",flag);
							}
							if ("RECEVIE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "RECEVIE_USER_CONTENT",flag);
							}
							if ("ORDER_CONTENT".equals(checkStrs[i])) {
								logic.updateVisitOverrideDel(dbConn, seqId, setIdStr, "ORDER_CONTENT",flag);
							}
							
							if(!T9Utility.isNullorEmpty(override)){        //是否把当前栏目的权限应用于其所有子栏目
								if ("EDIT_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentDel(dbConn, seqId, setIdStr, "EDIT_USER_CONTENT",flag);
								}
								if ("APPROVAL_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentDel(dbConn, seqId, setIdStr, "APPROVAL_USER_CONTENT",flag);
								}
								if ("RELEASE_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentDel(dbConn, seqId, setIdStr, "RELEASE_USER_CONTENT",flag);
								}
								if ("RECEVIE_USER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentDel(dbConn, seqId, setIdStr, "RECEVIE_USER_CONTENT",flag);
								}
								if ("ORDER_CONTENT".equals(checkStrs[i])) {
									logic.updateContentDel(dbConn, seqId, setIdStr, "ORDER_CONTENT",flag);
								}
							  }
						}
					}
				}
		}	else if("station".equals(flag)){       //站点权限管理
			if ("addPriv".equals(opt)) {		//添加权限
				if (checks != "") {
					String[] checkStrs = checks.split(",");
					for (int i = 0; i < checkStrs.length; i++) {
						if(!T9Utility.isNullorEmpty(override)){				//是否把当前栏目的权限应用于其所有子栏目
							if ("EDIT_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationAdd(dbConn, seqId, setIdStr, "EDIT_USER_CONTENT",flag);
							}
							if ("APPROVAL_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationAdd(dbConn, seqId, setIdStr, "APPROVAL_USER_CONTENT",flag);
							}
							if ("RELEASE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationAdd(dbConn, seqId, setIdStr, "RELEASE_USER_CONTENT",flag);
							}
							if ("RECEVIE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationAdd(dbConn, seqId, setIdStr, "RECEVIE_USER_CONTENT",flag);
							}
							if ("ORDER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationAdd(dbConn, seqId, setIdStr, "ORDER_CONTENT",flag);
							}
						  }
					}
				}

			} else if ("delPriv".equals(opt)) {         //删除权限
				if (checks != "") {
					String[] checkStrs = checks.split(",");
					for (int i = 0; i < checkStrs.length; i++) {
						if(!T9Utility.isNullorEmpty(override)){        //是否把当前栏目的权限应用于其所有子栏目
							if ("EDIT_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationDel(dbConn, seqId, setIdStr, "EDIT_USER_CONTENT",flag);
							}
							if ("APPROVAL_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationDel(dbConn, seqId, setIdStr, "APPROVAL_USER_CONTENT",flag);
							}
							if ("RELEASE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationDel(dbConn, seqId, setIdStr, "RELEASE_USER_CONTENT",flag);
							}
							if ("RECEVIE_USER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationDel(dbConn, seqId, setIdStr, "RECEVIE_USER_CONTENT",flag);
							}
							if ("ORDER_CONTENT".equals(checkStrs[i])) {
								logic.updateContentStationDel(dbConn, seqId, setIdStr, "ORDER_CONTENT",flag);
							}
						  }
					}
				}
			}

			}
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
				request.setAttribute(T9ActionKeys.RET_MSRG, "批量设置权限成功");
			} catch (Exception ex) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
				throw ex;
			}
			return "/core/inc/rtjson.jsp";
		}
}
