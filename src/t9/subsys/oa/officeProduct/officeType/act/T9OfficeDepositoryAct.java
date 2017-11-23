package t9.subsys.oa.officeProduct.officeType.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.officeProduct.commentOffice;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeDepository;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeType;
import t9.subsys.oa.officeProduct.officeType.logic.T9OfficeDepositoryLogic;

/**
 * 增加办公用品库
 * @author Administrator
 *
 */
public class T9OfficeDepositoryAct {
	T9OfficeDepositoryLogic officeDepository = new T9OfficeDepositoryLogic();
	commentOffice comment = new commentOffice();
	public String addOfficeDepository(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String contexPath = request.getContextPath();
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		T9Person user = (T9Person) request.getSession().getAttribute(
				"LOGIN_USER");// 获得登陆用户
		Connection dbConn = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			officeDepository.setOfficeInfoValueLogic(dbConn, fileForm, user);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH,
					"/core/inc/error.jsp");
			throw e;
		}
		response.sendRedirect(contexPath + "/subsys/oa/officeProduct/officeType/addOK.jsp");
		return null;
	}
   /**
    * 查询办公用品库
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
	public String findOfficeDepositoryInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		Connection dbConn = null;
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute(
					"LOGIN_USER");// 获得登陆用户
			
			List<T9OfficeDepository> findOfficeDep = officeDepository.findOfficeDepositorySet(dbConn,
					user);
			
			request.setAttribute("findOfficeDepS", findOfficeDep);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH,
					"/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/officeType/index.jsp";
	}
	/**
	 * 删除办公用品库设置
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delOfficeDepositoryInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(
					"LOGIN_USER");
			String noHiddenId = request.getParameter("HiddenId");

			int ok = officeDepository.delOfficeDepository(dbConn, person, Integer
					.parseInt(noHiddenId));
			if (ok != 0) {
				return "/t9/subsys/oa/officeProduct/officeType/act/T9OfficeDepositoryAct/findOfficeDepositoryInfo.act";
			}
			// request.setAttribute("booktype", booktype);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH,
					"/core/inc/error.jsp");
			throw e;
		}
		return "/t9/subsys/oa/officeProduct/officeType/act/T9OfficeDepositoryAct/findOfficeDepositoryInfo.act";
	}
	/**
	 * 修改办公用品库之前先进行查询
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String upOfficeDepositoryInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		Connection dbConn = null;
		String deptName ="";
		String managerName ="";
		String keeperName ="";
		
		try {
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute(
					"LOGIN_USER");// 获得登陆用户
			String officeId = request.getParameter("officeSeId");

			T9OfficeDepository findOffice = officeDepository.officeDepositoryInfo(dbConn,
					user, Integer.parseInt(officeId));
			List<T9OfficeType> officeType = findOfficeType(dbConn, user,findOffice.getSeqId());//通过办公用品库表的seq_id 获取办公用品类型type_nam
			if(!T9Utility.isNullorEmpty(findOffice.getDeptId())){
			    deptName = comment.findDept(dbConn, user, findOffice.getDeptId());
			}
			if(!T9Utility.isNullorEmpty(findOffice.getManager())){
			    managerName = comment.findManager(dbConn, user, findOffice.getManager());
			}
			if(!T9Utility.isNullorEmpty(findOffice.getManager())){
			    keeperName = comment.findProKeeper(dbConn, user, findOffice.getProKeeper());
			}
			//String deptId = findOffice.getDeptId();
			//System.out.println(deptId);
			/*String seqId ="";
			for(int i=0; i<findlicenses.size(); i++){
			   seqId = findlicenses.get(i).getStaffName();
			}
			if(!T9Utility.isNullorEmpty(seqId)){
			    String userName =	workTrans.getUserName(dbConn,Integer.valueOf(seqId));
			    request.setAttribute("userName", userName);
			}*/
			request.setAttribute("officeDepositoryInfo", findOffice);
			request.setAttribute("deptNames", deptName);
			request.setAttribute("managerNames", managerName);
			request.setAttribute("keeperNames", keeperName);
			request.setAttribute("officeType", officeType);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH,
					"/core/inc/error.jsp");
			throw e;
		}
		return "/subsys/oa/officeProduct/officeType/updateOfficeDepository.jsp";
	}
	/**
	 * 通过办公用品库查找办公用品类型名称
	 * @param dbConn
	 * @param user
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public List<T9OfficeType> findOfficeType(Connection dbConn, T9Person user,int seqId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String managers="";
		String sql = "select SEQ_ID,TYPE_NAME from office_type where type_depository="+ seqId;
		List<T9OfficeType> officeTypes = new ArrayList<T9OfficeType>();
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				T9OfficeType officeType = new T9OfficeType();
				officeType.setSeqId(rs.getInt("SEQ_ID"));
				officeType.setTypeName(rs.getString("TYPE_NAME")); 
				officeTypes.add(officeType);
			}

			
		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return officeTypes;
	}
	
	/**
	 * 修改办公用品库设置
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateOfficeDepositoryInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		T9RequestDbConn requestDbConn = (T9RequestDbConn) request
				.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		T9Person user = (T9Person) request.getSession().getAttribute(
				"LOGIN_USER");// 获得登陆用户
		Connection dbConn = null;
		String seqid = request.getParameter("seqid");
		String depositoryName = request.getParameter("DEPOSITORY_NAME"); // 库名称
		
		String deptId = request.getParameter("dept");// 所属部门
		String userManager = request.getParameter("user"); // 库管理员
		String user1 = request.getParameter("user1");// 物品调度员
		String[] typeName = request.getParameterValues("typeName");//类别名称
		String typeNameStr="";
		if(typeName!=null && typeName.length > 0){
	        for(int i = 0 ;i < typeName.length ; i++){
	        	typeNameStr +=  typeName[i] + ",";
	        }
	        typeNameStr = typeNameStr.substring(0, typeNameStr.length() - 1);
		}
		T9OfficeDepository office = new T9OfficeDepository();
		office.setSeqId(Integer.valueOf(seqid));
		 office.setDepositoryName(depositoryName);
		 office.setDeptId(deptId);
		 office.setManager(userManager);
		 office.setProKeeper(user1);
		 if(!T9Utility.isNullorEmpty(typeNameStr)){
		  office.setOfficeTypeId(typeNameStr);
		 }
		try {
			dbConn = requestDbConn.getSysDbConn();
			//officeDepository.setUpOfficeInfoLogic(dbConn, fileForm, user);
		 int ok =	officeDepository.updateLicenseInfo(dbConn, user,office);
		 if (ok != 0) { 
				return "/t9/subsys/oa/officeProduct/officeType/act/T9OfficeDepositoryAct/findOfficeDepositoryInfo.act";
			}
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			request.setAttribute(T9ActionKeys.FORWARD_PATH,
					"/core/inc/error.jsp");
			throw e;
		}
		//response.sendRedirect(contexPath + "/t9/subsys/oa/officeProduct/act/T9OfficeDepositoryAct/findOfficeDepositoryInfo.act");
		return "/t9/subsys/oa/officeProduct/officeType/act/T9OfficeDepositoryAct/findOfficeDepositoryInfo.act";
	}
	
}
