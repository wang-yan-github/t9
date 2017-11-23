package t9.subsys.oa.officeProduct.manage.act;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import t9.subsys.oa.officeProduct.manage.data.T9OfficeTranshistory;
import t9.subsys.oa.officeProduct.manage.logic.T9OfficeTranshistoryLogic;

public class T9OfficeTranshistoryAct {
	private T9OfficeTranshistoryLogic logic = new T9OfficeTranshistoryLogic();

	/**
	 * 登记审批列表 -wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTranshistoryListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getTranshistoryListLogic(dbConn, request.getParameterMap(), person);
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
	 * 获取人员名称
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userIdStr = request.getParameter("userIdStr");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String userNameStr = this.logic.getUserNameLogic(dbConn, userIdStr);
			String data = "{userName:\"" + T9Utility.encodeSpecial(userNameStr) + "\"}";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取详情
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTransDetailById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tranSeqIdStr = request.getParameter("tranSeqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getTransDetailByIdLogic(dbConn, tranSeqIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	 /**
   * 获取批量处理详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTransDetailByCycleNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String cycleNo = request.getParameter("cycleNo");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = this.logic.getTransDetailByCycleNoLogic(dbConn, cycleNo);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

	/**
	 * 处理transDetail
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String transHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String proIdStr = request.getParameter("proId");
		String transIdStr = request.getParameter("transId");
		String transQtyStr = request.getParameter("transQty");
		String factQtyStr = request.getParameter("factQty");
		String smsRemind1 = request.getParameter("smsRemind1");
		String smsRemind = request.getParameter("smsRemind");
		String borrower = request.getParameter("borrower");
		String removeReason = request.getParameter("removeReason");
		String setPriv = request.getParameter("setPriv");
		String transFlag = request.getParameter("transFlag");

		int transId = 0;
		int factQty = 0;
		int transQty = 0;
		int proId = 0;

		if (T9Utility.isNumber(transIdStr)) {
			transId = Integer.parseInt(transIdStr);
		}
		if (T9Utility.isNumber(factQtyStr)) {
			factQty = Integer.parseInt(factQtyStr);
		}
		if (T9Utility.isNumber(transQtyStr)) {
			transQty = Integer.parseInt(transQtyStr);
		}
		if (T9Utility.isNumber(proIdStr)) {
			proId = Integer.parseInt(proIdStr);
		}

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("transId", transId);
		map.put("factQty", factQty);
		map.put("transQty", transQty);
		map.put("smsRemind1", smsRemind1);
		map.put("smsRemind", smsRemind);
		map.put("borrower", borrower);
		map.put("removeReason", removeReason);
		map.put("setPriv", setPriv);
		map.put("proId", proId);
		map.put("transFlag", transFlag);

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			this.logic.transHandleLogic(dbConn, map, person);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
			// request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	 /**
   * 处理transDetailCycle
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String transHandleCycle(HttpServletRequest request, HttpServletResponse response) throws Exception {
    
    String len = request.getParameter("len");
    int length = 0;
    if(T9Utility.isInteger(len)){
      length = Integer.parseInt(len);
    }
    String smsRemind1 = request.getParameter("smsRemind1");
    String smsRemind = request.getParameter("smsRemind");
    String borrower = request.getParameter("borrower");
    
    for(int i = 0; i < length; i++){
      String proIdStr = request.getParameter("proId"+i);
      String transIdStr = request.getParameter("transId"+i);
      String transQtyStr = request.getParameter("transQty"+i);
      String factQtyStr = request.getParameter("factQty"+i);
      String removeReason = request.getParameter("removeReason"+i);
      String setPriv = request.getParameter("setPriv"+i);
      String transFlag = request.getParameter("transFlag"+i);
  
      int transId = 0;
      int factQty = 0;
      int transQty = 0;
      int proId = 0;
  
      if (T9Utility.isNumber(transIdStr)) {
        transId = Integer.parseInt(transIdStr);
      }
      if (T9Utility.isNumber(factQtyStr)) {
        factQty = Integer.parseInt(factQtyStr);
      }
      if (T9Utility.isNumber(transQtyStr)) {
        transQty = Integer.parseInt(transQtyStr);
      }
      if (T9Utility.isNumber(proIdStr)) {
        proId = Integer.parseInt(proIdStr);
      }
  
      Map<Object, Object> map = new HashMap<Object, Object>();
      map.put("transId", transId);
      map.put("factQty", factQty);
      map.put("transQty", transQty);
      map.put("smsRemind1", smsRemind1);
      map.put("smsRemind", smsRemind);
      map.put("borrower", borrower);
      map.put("removeReason", removeReason);
      map.put("setPriv", setPriv);
      map.put("proId", proId);
      map.put("transFlag", transFlag);
  
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        this.logic.transHandleLogic(dbConn, map, person);
      } catch (Exception e) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e;
      }
    }
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
      // request.setAttribute(T9ActionKeys.RET_DATA, data);
    return "/core/inc/rtjson.jsp";
  }

	/**
	 * 获取办公用品库(返回类别seq_id)产品编辑页面
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
			String data = this.logic.getproEditDepositoryNamesLogic(dbConn, person, extData);
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
	 * 获取办公用品名称(库存登记)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getProductsNamesById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String extData = request.getParameter("extData");
		String idStr = request.getParameter("idStr");

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getProductsNamesById(dbConn, person, extData, idStr);
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
	 * 获取办公用品名称(库存登记,不需id)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getProductsNames(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getProductsNames(dbConn, person);
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
	 * 获取办公用品名称根据ProName
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getProductsByProName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String nameStr = request.getParameter("name");

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getProductsByProNameLogic(dbConn, person, T9Utility.null2Empty(nameStr));
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
	 * 新建库存登记
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addOfficeTrans(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String transFlag = request.getParameter("TRANS_FLAG");
		String borrower = request.getParameter("borrower");
		String price = request.getParameter("PRICE");
		String band = request.getParameter("BAND");
		String company = request.getParameter("COMPANY");
		String officeDepository = request.getParameter("OFFICE_DEPOSITORY");
		String officeProtype = request.getParameter("officeProtype");
		String officePro = request.getParameter("officePro");
		String transQty = request.getParameter("TRANS_QTY");
		String repTime = request.getParameter("REP_TIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String proIdText = request.getParameter("PRO_ID_TEXT");

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("transFlag", transFlag);
		map.put("borrower", borrower);
		map.put("price", price);
		map.put("band", band);
		map.put("company", company);
		map.put("officeDepository", officeDepository);
		map.put("officeProtype", officeProtype);
		map.put("officePro", officePro);
		map.put("transQty", transQty);
		map.put("repTime", repTime);
		map.put("remark1", remark1);
		map.put("remark2", remark2);
		map.put("proIdText", proIdText);

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			this.logic.addOfficeTransLogic(dbConn, person, map);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			// request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 更新库存登记
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateOfficeTrans(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String transIdStr = request.getParameter("transId");
		int transId = 0;
		if (T9Utility.isNumber(transIdStr)) {
			transId = Integer.parseInt(transIdStr);
		}

		String transFlag = request.getParameter("TRANS_FLAG");
		String borrower = request.getParameter("borrower");
		String price = request.getParameter("PRICE");
		String band = request.getParameter("BAND");
		String company = request.getParameter("COMPANY");
		String officeDepository = request.getParameter("OFFICE_DEPOSITORY");
		String officeProtype = request.getParameter("officeProtype");
		String officePro = request.getParameter("officePro");
		String transQty = request.getParameter("TRANS_QTY");
		String repTime = request.getParameter("REP_TIME");
		String remark1 = request.getParameter("REMARK1");
		String remark2 = request.getParameter("REMARK2");
		String proIdText = request.getParameter("PRO_ID_TEXT");

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("transFlag", transFlag);
		map.put("borrower", borrower);
		map.put("price", price);
		map.put("band", band);
		map.put("company", company);
		map.put("officeDepository", officeDepository);
		map.put("officeProtype", officeProtype);
		map.put("officePro", officePro);
		map.put("transQty", transQty);
		map.put("repTime", repTime);
		map.put("remark1", remark1);
		map.put("remark2", remark2);
		map.put("proIdText", proIdText);
		map.put("transId", transId);

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			this.logic.updateOfficeTransLogic(dbConn, person, map);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			// request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 今日操作查看
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getProductsByToday(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getProductsByTodayLogic(dbConn, person, dateFormat.format(new Date()));
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
	 *办公用品管理明细
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTransInfoListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		String personIdStr = request.getParameter("personId");
		int personId = 0;
		if (T9Utility.isNumber(personIdStr)) {
			personId = Integer.parseInt(personIdStr);
		}
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getTransInfoListJsonLogic(dbConn, request.getParameterMap(), person, personId);
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
			String fileName = URLEncoder.encode("办公用品登记模板.csv", "UTF-8");
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
	 * 导入办公用品登记数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String impTransInfoToCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
			StringBuffer buffer = new StringBuffer("[");
			Map<Object, Object> returnMap = this.logic.impTransInfoToCsvLogic(dbConn, fileForm, person, buffer);

			int isCount = (Integer) returnMap.get("isCount");
			int updateCount = (Integer) returnMap.get("updateCount");

			if (buffer.charAt(buffer.length() - 1) == ',') {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
			String data = buffer.toString();
			request.setAttribute("isCount", isCount);
			request.setAttribute("updateCount", updateCount);
			request.setAttribute("contentList", data);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "导入数据失败");
			throw e;
		}
		return "/subsys/oa/officeProduct/manage/query/import.jsp";

	}

	/**
	 * 办公用品登记查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTransDetailListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("transFlag", T9Utility.null2Empty(request.getParameter("transFlag")));
		map.put("dept", T9Utility.null2Empty(request.getParameter("dept")));
		map.put("user", T9Utility.null2Empty(request.getParameter("user")));
		map.put("officeDepository", T9Utility.null2Empty(request.getParameter("officeDepository")));
		map.put("officeProtype", T9Utility.null2Empty(request.getParameter("officeProtype")));
		map.put("officePro", T9Utility.null2Empty(request.getParameter("officePro")));
		map.put("proName", T9Utility.null2Empty(request.getParameter("proName")));
		map.put("beginDate", T9Utility.null2Empty(request.getParameter("beginDate")));
		map.put("endDate", T9Utility.null2Empty(request.getParameter("endDate")));

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getTransDetailListLogic(dbConn, request.getParameterMap(), person, map);
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
	 * 根据查询条件导出数据到CSV文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String exportToCSV(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("transFlag", T9Utility.null2Empty(request.getParameter("transFlag")));
		map.put("dept", T9Utility.null2Empty(request.getParameter("dept")));
		map.put("user", T9Utility.null2Empty(request.getParameter("user")));
		map.put("officeDepository", T9Utility.null2Empty(request.getParameter("officeDepository")));
		map.put("officeProtype", T9Utility.null2Empty(request.getParameter("officeProtype")));
		map.put("officePro", T9Utility.null2Empty(request.getParameter("officePro")));
		map.put("proName", T9Utility.null2Empty(request.getParameter("proName")));
		map.put("beginDate", T9Utility.null2Empty(request.getParameter("beginDate")));
		map.put("endDate", T9Utility.null2Empty(request.getParameter("endDate")));
		response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String fileName = URLEncoder.encode("办公用品登记信息.csv", "UTF-8");
			fileName = fileName.replaceAll("\\+", "%20");
			response.setHeader("Cache-control", "private");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			ArrayList<T9DbRecord> dbL = this.logic.exportToCSVLogic(dbConn, map, person);
			T9CSVUtil.CVSWrite(response.getWriter(), dbL);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return null;
	}

	/**
	 * 放弃操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String deleteTransInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String transId = request.getParameter("transId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			this.logic.deleteTransInfoLogic(dbConn, person, transId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功！");
			// request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTransInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String transIdStr = request.getParameter("transId");
		int transId = 0;
		if (T9Utility.isNumber(transIdStr)) {
			transId = Integer.parseInt(transIdStr);
		}
		Connection dbConn = null;
		T9ORM orm = new T9ORM();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			T9OfficeTranshistory transhistory = (T9OfficeTranshistory) orm.loadObjSingle(dbConn, T9OfficeTranshistory.class, transId);
			if (transhistory == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, "数据不存在");
				return "/core/inc/rtjson.jsp";
			}
			StringBuffer data = T9FOM.toJson(transhistory);
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
	 * 删除
	 * 2011-4-1
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String deleteOfficeTranshistory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			if (!T9Utility.isNullorEmpty(seqIdStr)) {
				this.logic.deleteOfficeTranshistoryLogic(dbConn, seqIdStr);
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
	 * 离职人员/外部人员
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getNotRecordDeptList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getNotRecordDeptListLogic(dbConn, person);
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	

}
