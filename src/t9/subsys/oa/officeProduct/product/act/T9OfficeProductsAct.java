package t9.subsys.oa.officeProduct.product.act;

import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.officeProduct.product.data.T9OfficeProducts;
import t9.subsys.oa.officeProduct.product.logic.T9OfficeProductsLogic;
import t9.subsys.oa.officeProduct.query.data.T9OfficeType;

public class T9OfficeProductsAct {
	private T9OfficeProductsLogic logic = new T9OfficeProductsLogic();

	/**
	 * 获取办公用品库(返回类别seq_id)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeDepositoryNames(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getOfficeDepositoryNamesLogic(dbConn, person);
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
	 * 获取办公用品库(返回类别seq_id)产品编辑页面,如与extData值相等则选中
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getproEditDepositoryNames(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String extData = request.getParameter("extData");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getproEditDepositoryNamesLogic(dbConn, person,extData);
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
	 * 获取办公用品库(返回库seq_id)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeDepositoryName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getOfficeDepositoryNameLogic(dbConn, person);
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
	 * 获取办公用品类别
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeTypeNamesById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("typeId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getOfficeTypeNamesByIdLogic(dbConn, typeIdStr);
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
	 * 增加办公用品
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addOfficeProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String proName = request.getParameter("proName");
		String officeProtype = request.getParameter("officeProtype");

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			Map<String, String[]> map = request.getParameterMap();
			T9OfficeProducts products = (T9OfficeProducts) T9FOM.build(map, T9OfficeProducts.class, "");
			int isHave = 0;
			int maxSeqId = 0;
			int counter = this.logic.isHaveValue(dbConn, officeProtype, proName,"");
			if (counter > 0) {
				isHave = 1;
			} else {
				maxSeqId = this.logic.addOfficeProductsLogic(dbConn, products);
			}
			String proNameStr = "";
			if (!T9Utility.isNullorEmpty(products.getProName())) {
				proNameStr = T9Utility.encodeSpecial(products.getProName());
			}
			String data = "{isHave:\"" + isHave + "\",maxSeqId:\"" + maxSeqId + "\",proName:\"" + proNameStr + "\"}";
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
	 * 获取办公用品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeProductsById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String proSeqIdStr = request.getParameter("proSeqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9OfficeProducts products = this.logic.getOfficeProductsById(dbConn, proSeqIdStr);
			if (products == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
				request.setAttribute(T9ActionKeys.RET_MSRG, "0");
				return "/core/inc/rtjson.jsp";
			}
			StringBuffer data = T9FOM.toJson(products);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 更新办公用品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateOfficeProductsById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String proSeqIdStr = request.getParameter("proSeqId");
		int proSeqId = 0;
		if (T9Utility.isNumber(proSeqIdStr)) {
			proSeqId = Integer.parseInt(proSeqIdStr);
		}

		String proName = request.getParameter("proName");
		String proDesc = request.getParameter("proDesc");
//		String officeDepository = request.getParameter("officeDepository");
		String officeProtype = request.getParameter("officeProtype");
		String proCode = request.getParameter("proCode");
		String proUnit = request.getParameter("proUnit");
		String proPriceStr = request.getParameter("proPrice");
		String proSupplier = request.getParameter("proSupplier");
		String proLowstockStr = request.getParameter("proLowstock");
		String proMaxstockStr = request.getParameter("proMaxstock");
		String proStockStr = request.getParameter("proStock");
		String proCreator = request.getParameter("proCreator");
		String proAuditer = request.getParameter("proAuditer");
		String proManager = request.getParameter("proManager");
		String proDept = request.getParameter("proDept");
		
		
		double proPrice = 0;
		if (T9Utility.isNumber(proPriceStr)) {
			proPrice = Double.parseDouble(proPriceStr);
		}
		int proLowstock = 0;
		if (T9Utility.isNumber(proLowstockStr)) {
			proLowstock = Integer.parseInt(proLowstockStr);
		}
		int proMaxstock = 0;
		if (T9Utility.isNumber(proMaxstockStr)) {
			proMaxstock = Integer.parseInt(proMaxstockStr);
		}
		int proStock = 0;
		if (T9Utility.isNumber(proStockStr)) {
			proStock = Integer.parseInt(proStockStr);
		}

		Connection dbConn = null;
		T9ORM orm = new T9ORM();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			int isHave = 0;
			String proNameStr = "";
			int counter = this.logic.isHaveValue(dbConn, officeProtype, proName,proSeqIdStr);
			if (counter > 0) {
				isHave = 1;
			}else {
				T9OfficeProducts products = this.logic.getOfficeProductsById(dbConn, proSeqIdStr);
				if (products != null) {
					products.setProName(proName);
					products.setProDesc(proDesc);
					products.setProUnit(proUnit);
					products.setProCode(proCode);
					products.setProSupplier(proSupplier);
					products.setProPrice(proPrice);
					products.setProLowstock(proLowstock);
					products.setProMaxstock(proMaxstock);
					products.setProStock(proStock);
					products.setProDept(proDept);
					products.setProManager(proManager);
					products.setProCreator(proCreator);
					products.setOfficeProtype(officeProtype);
					products.setProAuditer(proAuditer);
					orm.updateSingle(dbConn, products);
					
					if (!T9Utility.isNullorEmpty(products.getProName())) {
						proNameStr = T9Utility.encodeSpecial(products.getProName());
					}
				}
			} 
			String data = "{isHave:\"" + isHave + "\",maxSeqId:\"" + proSeqId + "\",proName:\"" + proNameStr + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			 request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
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
	public String delOfficeProductsById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String proSeqIdStr = request.getParameter("proSeqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			if (!T9Utility.isNullorEmpty(proSeqIdStr)) {
				this.logic.delTranshistoryLogic(dbConn,proSeqIdStr);
				this.logic.delOfficeProductsByIdLogic(dbConn,proSeqIdStr);
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
	/**
	 * 新增办公用品类型定义
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addOfficeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String depository = request.getParameter("depository");
		String typeNameStr = request.getParameter("typeName");
		String typeOrder = request.getParameter("typeOrder");
		
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			boolean isHaveFlag = this.logic.isHaveOfficeTypeLogic(dbConn,depository,typeNameStr);
			int isHave = 0;
			if (isHaveFlag) {
				isHave = 1;
			}else {
				this.logic.addOfficeTypeLogic(dbConn,depository,typeNameStr,typeOrder);
			}
			String data = "{isHave:\"" + isHave + "\",typeName:\"" + T9Utility.encodeSpecial(typeNameStr) + "\"}";
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
	 * 下载CSV模板
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String downCSVTemplet(HttpServletRequest request, HttpServletResponse response) throws Exception {
	  response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
	  try {
			String fileName = URLEncoder.encode("办公用品信息模板.csv", "UTF-8");
			fileName = fileName.replaceAll("\\+", "%20");
			response.setHeader("Cache-control", "private");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			ArrayList<T9DbRecord> dbL = this.logic.downCSVTempletLogic();
			T9CSVUtil.CVSWrite(response.getWriter(), dbL);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return null;
	}
	/**
	 * 导入办公用品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String impOfficeProToCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
			StringBuffer buffer = new StringBuffer("[");
			Map<Object, Object> returnMap =  this.logic.impOfficeProToCsvLogic(dbConn, fileForm, person,buffer );
			if (buffer.charAt(buffer.length() - 1) == ',') {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
			String data = buffer.toString();
			
			int isCount = (Integer) returnMap.get("isCount");
			request.setAttribute("isCount", isCount);
			request.setAttribute("contentList", data);
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据成功！");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/subsys/oa/officeProduct/product/proImport.jsp";
	}
	/**
	 * 获取办公用品类别
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getOfficeTypeLogic(dbConn);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA,data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据失败");
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	public String getOfficeTypeById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("typeId");
		int typeId = 0;
		if (T9Utility.isNumber(typeIdStr)) {
			typeId = Integer.parseInt(typeIdStr);
		}
		T9ORM orm = new T9ORM();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9OfficeType officeType = (T9OfficeType) orm.loadObjSingle(dbConn, T9OfficeType.class, typeId);
			StringBuffer data = T9FOM.toJson(officeType);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA,data.toString());
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据失败");
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	/**
	 * 更新类别
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateTypeName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("codeId");
		String depositoryIdStr = request.getParameter("depository");
		String codeNameStr = request.getParameter("codeName");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			boolean isHaveFlag = this.logic.isHaveOfficeType2Logic(dbConn, depositoryIdStr, codeNameStr);
			int isHave = 0;
			if (isHaveFlag) {
				isHave = 1;
			}else {
				this.logic.updateOfficeTypeLogic(dbConn,depositoryIdStr,codeNameStr,typeIdStr);
			}
			String data = "{isHave:\"" + isHave + "\",typeName:\"" + T9Utility.encodeSpecial(codeNameStr) + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA,data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据失败");
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	/**
	 * 删除类别
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delTypeName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String typeIdStr = request.getParameter("typeId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.delTypeNameLogic(dbConn,typeIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据成功！");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据失败");
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 获取办公用品类别(根据库的id)
	 * 2011-3-29
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	public String getTypeNamesByStoreId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String storeIdStr = request.getParameter("storeId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getTypeNamesByStoreIdLogic(dbConn, storeIdStr);
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
	
	
	

}
