package t9.subsys.oa.officeProduct.person.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.book.data.T9Page;
import t9.subsys.oa.giftProduct.outstock.logic.T9GiftOutstockLogic;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeDepository;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeType;
import t9.subsys.oa.officeProduct.officeType.logic.T9OfficeDepositoryLogic;
import t9.subsys.oa.officeProduct.person.data.T9OfficeProducts;
import t9.subsys.oa.officeProduct.person.data.T9OfficeTranshistory;
import t9.subsys.oa.officeProduct.person.logic.T9PersonalOfficeRecordLogic;

public class T9PersonalOfficeRecordAct {
	T9OfficeDepositoryLogic officeDepository = new T9OfficeDepositoryLogic();
	T9PersonalOfficeRecordLogic record = new T9PersonalOfficeRecordLogic();
	hrPublicIdTransName publicClass = new hrPublicIdTransName();
	T9PersonalOfficeRecordLogic logic = new T9PersonalOfficeRecordLogic();

	/**
	 * 查询办公用品库的名称
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findOfficeDepositoryInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		Connection dbConn = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户

			List<T9OfficeDepository> findOfficeDep = officeDepository.findOfficeDepositorySet(dbConn, user);

			request.setAttribute("findOfficeDepS", findOfficeDep);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/personal/record.jsp";
	}

	public String OfficeDepositoryInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		String depId = request.getParameter("DEPOSITORY");
		String typeId = request.getParameter("TYPE_ID");
		String proSeqId = request.getParameter("PRO_ID");

		// System.out.println(depId+"=="+typeId+"==="+proSeqId);
		Connection dbConn = null;
		T9OfficeDepository oneFindOfficeDep = null;
		List<Map<String, String>> list = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
			if (!T9Utility.isNullorEmpty(depId)) {
				list = officeDepository.findOfficeDepository(dbConn, user, Integer.valueOf(depId), typeId, proSeqId);
			}
			request.setAttribute("listOneOffice", list);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/personal/record.jsp";
	}

	/**
	 * 通过办公用品库 获得办公类型
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			String str = "";
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
			String officeId = request.getParameter("officeId");
			str = record.getOfficeType(dbConn, user, officeId);
			// str = syslog.getMySysTenLog(dbConn, user);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出办公用品类别");
			request.setAttribute(T9ActionKeys.RET_DATA, str);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 通过办公用品类型 获得办公用品
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			String str = "";
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
			String officeId = request.getParameter("officeId");
			str = record.getOfficeProducts(dbConn, user, officeId);
			// str = syslog.getMySysTenLog(dbConn, user);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出办公用品");
			request.setAttribute(T9ActionKeys.RET_DATA, str);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 模糊查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeProductName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			String str = "";
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
			String proName = request.getParameter("proName");
			str = record.getOfficeProductNameLogic(dbConn, user, proName);
			// str = syslog.getMySysTenLog(dbConn, user);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出办公用品");
			request.setAttribute(T9ActionKeys.RET_DATA, str);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 新建个人办公用品登记
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addPersonOfficeRecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String contexPath = request.getContextPath();
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
		Connection dbConn = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			String url = record.setPersonOfficeRecordLogic2(dbConn, fileForm, user);
			if (!T9Utility.isNullorEmpty(url)) {
        String path = request.getContextPath();
        response.sendRedirect(path+ url);
        return null;
      }
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		response.sendRedirect(contexPath + "/subsys/oa/officeProduct/personal/addOK.jsp");
		return null;
	}
	
	
	
	/**
	 * 新建个人办公用品登记 -wyw
	 * 2011-3-30
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addPersonOfficeRecord2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("transFlag", request.getParameter("TRANS_FLAG"));//登记类型
		map.put("officeDepository", request.getParameter("OFFICE_DEPOSITORY"));//办公用品库
		map.put("officeProtype", request.getParameter("officeProtype"));//办公用品类别
		map.put("officePro", request.getParameter("officePro"));//办公用品
		map.put("proIdText", request.getParameter("PRO_ID_TEXT"));//模糊查询的办公用品id
		map.put("transQty", request.getParameter("TRANS_QTY"));//申请数量
		map.put("remark", request.getParameter("REMARK"));//备注

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
			
//			this.logic.addPersonOfficeRecord2Logic(dbConn,map);

			String data = "{}";
			request.setAttribute(T9ActionKeys.RET_DATA, data);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "ok");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, "失败");
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	
	

	/**
	 * 获得所有办公用品库设置
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getOfficeDepositoryInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		Connection dbConn = null;
		String depName = "";
		int depId;
		List<T9OfficeType> officeTypes = null;
		int typeId;
		String typeName = "";
		List<T9OfficeProducts> officeProducts = null;
		int produtsId;
		String productName = "";
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户

			List<T9OfficeDepository> findOfficeDep = officeDepository.findOfficeDepositoryInfo(dbConn, user);
			/*
			 * for(int i = 0; i<findOfficeDep.size() && findOfficeDep.size()>0; i++){
			 * depName = findOfficeDep.get(i).getDepositoryName(); depId =
			 * findOfficeDep.get(i).getSeqId();
			 * if(!T9Utility.isNullorEmpty(String.valueOf(depId))){ officeTypes =
			 * record.getOfficeTypeInfo(dbConn,user,String.valueOf(depId)); for(int j
			 * =0; j< officeTypes.size() && officeTypes.size()>0; j++){ typeId =
			 * officeTypes.get(j).getSeqId(); typeName =
			 * officeTypes.get(j).getTypeName();
			 * if(!T9Utility.isNullorEmpty(String.valueOf(typeId))){ officeProducts =
			 * record.getOfficeProductsInfo(dbConn, user, String.valueOf(typeId));
			 * for(int m =0; m<officeProducts.size()&&officeProducts.size()>0; m++){
			 * produtsId = officeProducts.get(m).getSeqId(); productName =
			 * officeProducts.get(m).getProName(); } } } } }
			 */
			request.setAttribute("findOfficeDeps", findOfficeDep);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/personal/listView.jsp";
	}

	/**
	 * 增加办公用品批量审批
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addOfficeProducts(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
		Connection dbConn = null;
		try {
			int ok = 0;
			dbConn = requestDbConn.getSysDbConn();
			String officeProductId[] = request.getParameterValues("officeProductId");
			String transFlag = request.getParameter("recordType");
			String[] transQty = request.getParameterValues("transQty");// 申请数量

			String[] checkBox = request.getParameterValues("checkBox");

			String[] proName = request.getParameterValues("proNames");
			T9OfficeTranshistory transhiStory = new T9OfficeTranshistory();
			if (checkBox != null && !checkBox.equals("")) {
			  int cycleNo = officeDepository.getCycleNo(dbConn, user) + 1;// 申请标记
				for (int i = 0; i < checkBox.length; i++) {
					String sum = checkBox[i];
					if (!T9Utility.isNullorEmpty(sum)) {
						Date currentTime = new Date();
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String dateString = formatter.format(currentTime);
						// proid = Integer.valueOf(sum)-1;
						//System.out.println(proName[Integer.valueOf(sum) - 1]);
						transhiStory.setProId(Integer.valueOf(officeProductId[Integer.valueOf(sum) - 1]));// 办公用品
						transhiStory.setBorrower(String.valueOf(user.getSeqId()));// 新建办公用品登记人
						transhiStory.setTransFlag(transFlag);// 登记类型
						if("1".equals(transFlag) || "2".equals(transFlag)){
						  transhiStory.setTransQty(-Integer.valueOf(transQty[Integer.valueOf(i)]));// 申请数量
						}
						else{
						  transhiStory.setTransQty(Integer.valueOf(transQty[Integer.valueOf(i)]));// 申请数量
						}
						transhiStory.setPrice(0);//
						transhiStory.setRemark("");// 备注
						transhiStory.setTransDate(T9Utility.parseDate("yyyy-MM-dd", dateString));
						transhiStory.setOperator(String.valueOf(user.getSeqId()));// 操作者
						transhiStory.setTransState("0");
						transhiStory.setDeptId(user.getDeptId());
						transhiStory.setCycleNo(cycleNo);// 申请标记
						transhiStory.setCycle("1");
						transhiStory.setFactQty(0);
						ok = officeDepository.newOfficeProducts(dbConn, user, transhiStory);

					}
				}
			}
			if (ok != 0) {
				return "/subsys/oa/officeProduct/personal/addOfficeProductOK.jsp";
			}
			// record.setPersonOfficeRecordLogic(dbConn, fileForm, user);

		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		// response.sendRedirect(contexPath +
		// "/subsys/oa/officeProduct/personal/addOfficeProductOK.jsp");
		return "/subsys/oa/officeProduct/personal/addOfficeProductOK.jsp";
	}

	/**
	 * 查询办公用品登记信息(带分页的)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findofficeRecordInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		Connection dbConn = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
			int total = officeDepository.count(dbConn, user);
			String currNo = request.getParameter("currNo");
			int curruntNo = 1;
			if (T9Utility.isNullorEmpty(currNo)) {
				curruntNo = 1;
			} else {
				curruntNo = Integer.parseInt(currNo);
			}
			T9Page page = new T9Page(5, total, curruntNo);

			Map transhistory = officeDepository.findofficeRecordInfoLogic(dbConn, user, page);
			
			request.setAttribute("transhistoryList", transhistory);
			request.setAttribute("page", page);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/personal/transInfo.jsp";
	}

	/**
	 * 删除办公用品登记信息（单条信息）
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String deleteofficeRecordInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
			String noHiddenId = request.getParameter("HiddenId");

			int ok = officeDepository.deleteofficeRecordInfoLogic(dbConn, person, Integer.parseInt(noHiddenId));
			if (ok != 0) {
				return "/t9/subsys/oa/officeProduct/person/act/T9PersonalOfficeRecordAct/findofficeRecordInfo.act";
			}
			// request.setAttribute("booktype", booktype);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		return "/t9/subsys/oa/officeProduct/person/act/T9PersonalOfficeRecordAct/findofficeRecordInfo.act";
	}

	/**
	 * 查询办公用品登记信息的详细信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findOfficeXxInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		Connection dbConn = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");// 获得登陆用户
			String officeId = request.getParameter("officeId");

			List<T9OfficeTranshistory> findOffices = officeDepository.findOfficeXxInfoLogic(dbConn, user, Integer.parseInt(officeId));
			String borrowerId = "";
			String operatorId = "";
			String officeProtypeId = "";
			String typeName = "";// 办公用品类型名称
			String proId = ""; // 和办公用品中的seq_id 对应
			String transFlag = "";// 登记类型
			String transName = "";// 登记类型名称
			String proName = "";// 办公用品名称
			if (findOffices != null) {
				for (int i = 0; i < findOffices.size(); i++) {
					borrowerId = findOffices.get(i).getBorrower();// 申请人id
					operatorId = findOffices.get(i).getOperator();// 审批人 id
					proId = String.valueOf(findOffices.get(i).getProId());
					transFlag = findOffices.get(i).getTransFlag();// 登记类型
					if (transFlag.equals("1")) {
						transName = "领用";
					}
					if (transFlag.equals("2")) {
						transName = "借用";
					}
					if (transFlag.equals("3")) {
						transName = "归还";
					}
				}
			}
			if (!T9Utility.isNullorEmpty(borrowerId)) {
				String borrowerName = publicClass.getUserName(dbConn, Integer.valueOf(borrowerId));// 申请人名称
				request.setAttribute("borrowerName", borrowerName);
			}
			if (!T9Utility.isNullorEmpty(operatorId)) {
				String operatorName = publicClass.getUserName(dbConn, Integer.valueOf(operatorId));// 审批人名称
				request.setAttribute("operatorName", operatorName);
			}
			if (!T9Utility.isNullorEmpty(proId)) { // 通过OFFICE_TRANSHISTORY 表中的pro_id
				// 查询出办公用品表（office_products）中的详细信息
				List<T9OfficeProducts> findOfficeProducts = officeDepository.findOfficeProductsXxInfoLogic(dbConn, user, Integer.parseInt(proId));
				if (findOfficeProducts != null) {
					for (int j = 0; j < findOfficeProducts.size(); j++) {
						officeProtypeId = findOfficeProducts.get(j).getOfficeProtype();
						proName = findOfficeProducts.get(j).getProName();
					}
				}
			}
			if (!T9Utility.isNullorEmpty(officeProtypeId)) {// 通过office_products
				// 表中的OFFICE_PROTYPE
				// 查询出办公用品类型表（type_name）中的详细信息
				T9OfficeType type = officeDepository.findOfficeTypesXxInfoLogic(dbConn, user, Integer.valueOf(officeProtypeId));// 审批人名称
				if (type != null) {
					typeName = type.getTypeName();
				}
				request.setAttribute("typeName", typeName);
			}
			request.setAttribute("transName", transName);
			request.setAttribute("proName", proName);// 办公用品名称
			request.setAttribute("onefindOffices", findOffices);

		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/personal/officeXxInfo.jsp";
	}

	/**
	 * 根据用户的管理权限得到所有部门（考勤统计）
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String selectDeptToAttendance(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			int userId = user.getSeqId();
			String userPriv = user.getUserPriv();// 角色
			String postpriv = user.getPostPriv();// 管理范围
			String postDept = user.getPostDept();// 管理范围指定部门
			int userDeptId = user.getDeptId();
			T9DeptLogic deptLogic = new T9DeptLogic();
			String userDeptName = deptLogic.getNameByIdStr(String.valueOf(userDeptId), dbConn);
			String data = "";
			if (userPriv != null && userPriv.equals("1") && user.getUserId().trim().equals("admin")) {// 假如是系统管理员的都快要看得到.而且是ADMIN用户
				data = deptLogic.getDeptTreeJson(0, dbConn);

			} else {
				if (postpriv.equals("0")) {
					// data = "[{text:\"" + userDeptName + "\",value:" + userDeptId +
					// "}]";
					String[] postDeptArray = { String.valueOf(userDeptId) };
					data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray) + "]";
				}
				if (postpriv.equals("1")) {
					data = deptLogic.getDeptTreeJson(0, dbConn);
				}
				if (postpriv.equals("2")) {
					if (postDept == null || postDept.equals("")) {
						data = "[]";
					} else {
						String[] postDeptArray = postDept.split(",");
						data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray) + "]";

					}
				}
			}
			if (data.equals("")) {
				data = "[]";
			}
			data = data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "").replace("\n", "").replace("\r", "");
			// System.out.println(data);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId) + "," + postpriv);
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 部门领用汇总
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getGiftOutByDeptId(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String deptId = request.getParameter("deptId");

			T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			int userId = user.getSeqId();
			T9GiftOutstockLogic giftLogic = new T9GiftOutstockLogic();
			String data = "";
			if (deptId != null && !deptId.equals("")) {
				List<Map<String, String>> list = officeDepository.getGiftOutByDeptId(dbConn, deptId);
				data = getJson(list);
			}
			if (data.equals("")) {
				data = "[]";
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

	public String getJson(List<Map<String, String>> mapList) {
		StringBuffer buffer = new StringBuffer("[");
		for (Map<String, String> equipmentsMap : mapList) {
			buffer.append("{");
			Set<String> keySet = equipmentsMap.keySet();
			for (String mapStr : keySet) {
				// System.out.println(mapStr + ":>>>>>>>>>>>>" +
				// equipmentsMap.get(mapStr));
				String name = equipmentsMap.get(mapStr);
				if (name != null) {
					name = name.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
				}
				/*
				 * if(mapStr!=null&&mapStr.equals("seqId")){
				 * 
				 * }
				 */
				buffer.append(mapStr + ":\"" + (name == null ? "" : name) + "\",");
			}
			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append("},");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		if (mapList.size() > 0) {
			buffer.append("]");
		} else {
			buffer.append("[]");
		}
		String data = buffer.toString();
		// System.out.println(data);
		return data;
	}

	/**
	 * 获取办公用品库(返回类别seq_id)新建办公用品登记页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getDepositoryNames(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String extData = request.getParameter("extData");
		if (T9Utility.isNullorEmpty(extData)) {
			extData = "";
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getDepositoryNamesLogic(dbConn, person, extData);
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
