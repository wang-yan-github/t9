package t9.subsys.oa.officeProduct.query.act;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.officeProduct.query.logic.T9OfficeQueryLogic;

public class T9OfficeQueryAct {
	private T9OfficeQueryLogic logic = new T9OfficeQueryLogic();

	/**
	 * 办公用品目录树获取(办公用品信息查询) 2011-3-29
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getQueryTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = "";
			if (!T9Utility.isNullorEmpty(id) && !id.equals("0")) {
				String idArry[] = id.split(",");
				if (idArry != null && idArry.length > 0) {
					if (idArry.length > 1 && idArry.length < 3) {
						data = this.logic.getTypeTreeLogic(dbConn, person, idArry[0]);
					} else if (idArry.length > 2 && idArry.length < 4) {
						data = this.logic.getProductsTreeLogic(dbConn, person, idArry[0]);
					}
				}
			} else {
				data = this.logic.getQueryTreeLogic(dbConn, person);
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 办公用品目录树获取((办公用品信息管理) 2011-3-29
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getManageTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = "";
			if (!T9Utility.isNullorEmpty(id) && !id.equals("0")) {
				String idArry[] = id.split(",");
				if (idArry != null && idArry.length > 0) {
					if ("undefindType".equalsIgnoreCase(idArry[0])) {
						data = this.logic.getUndefindTypePro(dbConn);
					} else {
						if (idArry.length > 1 && idArry.length < 3) {
							data = this.logic.getTypeTreeLogic(dbConn, person, idArry[0]);
						} else if (idArry.length > 2 && idArry.length < 4) {
							data = this.logic.getProductsTreeLogic(dbConn, person, idArry[0]);
						}

					}

				}
			} else {
				data = this.logic.getManageTreeLogic(dbConn, person);
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 查询单个办公用品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOneOfficeProductInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String storeId = request.getParameter("storeId");
		String typeId = request.getParameter("typeId");
		String proSeqId = request.getParameter("proSeqId");
		// "storeId="+ <%=storeId%>
		// +"&typeId="+<%=typeId%>+"&proSeqId="+<%=proSeqId%>;
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = "";
			if (!T9Utility.isNullorEmpty(proSeqId)) {
				data = this.logic.getOneOfficeProductInfoLogic(dbConn, person, proSeqId);
			}
			// data = this.logic.getProductsTreeLogic(dbConn, person, idArry[0]);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获得办公用品信息结果（带分页）
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeProductsListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("proName", T9DBUtility.escapeLike(request.getParameter("proName")));
			map.put("proDesc", T9DBUtility.escapeLike(request.getParameter("proDesc")));
			map.put("proCode", T9DBUtility.escapeLike(request.getParameter("proCode")));
			map.put("officeDepository", T9DBUtility.escapeLike(request.getParameter("officeDepository")));
			map.put("officeProtype", T9DBUtility.escapeLike(request.getParameter("officeProtype")));
			String data = "";
			data = this.logic.queryOfficeProductsJsonLogic(dbConn, request.getParameterMap(), map, person);
//			System.out.println(data);
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
	 * 办公用品查询 第二个标签页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryOfficeProductsListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("proName", T9DBUtility.escapeLike(request.getParameter("proName")));
			map.put("proDesc", T9DBUtility.escapeLike(request.getParameter("proDesc")));
			map.put("proCode", T9DBUtility.escapeLike(request.getParameter("proCode")));
			map.put("officeDepository", T9DBUtility.escapeLike(request.getParameter("officeDepository")));
			map.put("officeProtype", T9DBUtility.escapeLike(request.getParameter("officeProtype")));
			String data = "";
			data = this.logic.queryOfficeProductsJsonLogic1(dbConn, request.getParameterMap(), map, person);
//			System.out.println(data);
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
	 * 办公用品导出 第二个标签页
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryOfficeProductsExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		OutputStream ops = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("proName", T9DBUtility.escapeLike(request.getParameter("proName")));
			map.put("proDesc", T9DBUtility.escapeLike(request.getParameter("proDesc")));
			map.put("proCode", T9DBUtility.escapeLike(request.getParameter("proCode")));
			map.put("officeDepository", T9DBUtility.escapeLike(request.getParameter("officeDepository")));
			map.put("officeProtype", T9DBUtility.escapeLike(request.getParameter("officeProtype")));
			String data = "";
			String fileName = URLEncoder.encode("办公用品.xls", "UTF-8");
			fileName = fileName.replaceAll("\\+", "%20");
			response.setHeader("Cache-control", "private");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			ops = response.getOutputStream();
			ArrayList<T9DbRecord> dbL = this.logic.queryOfficeProductsExport(dbConn, request.getParameterMap(), map, person);
			T9JExcelUtil.writeExc(ops, dbL);
		} catch (Exception ex) {
			throw ex;
		} finally {
			ops.close();
		}
		return null;
	}

	/**
	 * 获取单位员工用户名称
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String userIdStr = request.getParameter("userIdStr");
			String data = this.logic.getUserNameLogic(dbConn, userIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 删除办公用品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String deleteOfficeProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			if (!T9Utility.isNullorEmpty(seqIdStr)) {
				this.logic.deleteOfficeProductsLogic(dbConn, seqIdStr);
				this.logic.delOfficeProductsByIdLogic(dbConn, seqIdStr);
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
}
