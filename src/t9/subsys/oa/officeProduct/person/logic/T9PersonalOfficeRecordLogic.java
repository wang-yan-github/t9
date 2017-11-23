package t9.subsys.oa.officeProduct.person.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.book.data.T9Page;
import t9.subsys.oa.officeProduct.commentOffice;
import t9.subsys.oa.officeProduct.officeType.data.T9OfficeDepository;
import t9.subsys.oa.officeProduct.person.data.T9OfficeProducts;
import t9.subsys.oa.officeProduct.person.data.T9OfficeTranshistory;

public class T9PersonalOfficeRecordLogic {
	private static Logger log = Logger.getLogger("t9.subsys.subsys.oa.officeProduct.person.logic.T9PersonalOfficeRecordLogic.java");
	commentOffice comment = new commentOffice();

	/**
	 * 查询办公用品库的名称
	 * 
	 * @param dbConn
	 * @param user
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<T9OfficeDepository> findOfficeDepInfo(Connection dbConn, T9Person user, T9Page page) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String workSql = "select SEQ_ID,DEPOSITORY_NAME,OFFICE_TYPE_ID,DEPT_ID,MANAGER,PRO_KEEPER from office_depository where 1=1";
		List<T9OfficeDepository> offices = new ArrayList<T9OfficeDepository>();
		try {
			ps = dbConn.prepareStatement(workSql);
			rs = ps.executeQuery();
			while (rs.next()) {
				T9OfficeDepository office = new T9OfficeDepository();
				office.setSeqId(rs.getInt("SEQ_ID"));
				office.setDepositoryName(rs.getString("DEPOSITORY_NAME"));
				offices.add(office);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return offices;
	}

	/**
	 * 通过办公用品库 获得办公类型
	 * 
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static String getOfficeType(Connection conn, T9Person user, String officeId) throws Exception {
		StringBuffer sb = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int SysTen = 0;
		String findSeq = " select SEQ_ID, TYPE_NAME from OFFICE_TYPE where type_depository = " + officeId + "";
		try {
			sb.append("{");
			sb.append("listData:[");
			ps = conn.prepareStatement(findSeq);
			rs = ps.executeQuery();
			List<Map> list = new ArrayList();
			while (rs.next()) {
				String seqId = rs.getString("SEQ_ID");
				String typeName = rs.getString("TYPE_NAME");
				Map mapTmp = new HashMap();
				mapTmp.put("seqId", seqId);
				mapTmp.put("typeName", typeName);
				list.add(mapTmp);
			}
			for (int j = 0; j < list.size(); j++) {
				Map tmpMap = list.get(j);
				sb.append("{");
				sb.append("seqId:" + tmpMap.get("seqId"));
				sb.append(",typeName:\"" + tmpMap.get("typeName") + "\"");
				sb.append("},");
			}
			if (list.size() > 0)
				sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			sb.append("}");
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return sb.toString();
	}

	/**
	 * 通过办公用品类型 获得办公用品
	 * 
	 * @param conn
	 * @param user
	 * @param officeId
	 * @return
	 * @throws Exception
	 */
	public static String getOfficeProducts(Connection conn, T9Person user, String officeId) throws Exception {
		StringBuffer sb = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int SysTen = 0;
		// String findSeq =
		// " select SEQ_ID, TYPE_NAME from OFFICE_TYPE where type_depository = "+officeId+"";
		String findSql = "select SEQ_ID, PRO_NAME from office_products where OFFICE_PROTYPE =" + officeId + "";
		try {
			sb.append("{");
			sb.append("listData:[");
			ps = conn.prepareStatement(findSql);
			rs = ps.executeQuery();
			List<Map> list = new ArrayList();
			while (rs.next()) {
				String seqId = rs.getString("SEQ_ID");
				String proName = rs.getString("PRO_NAME");
				Map mapTmp = new HashMap();
				mapTmp.put("seqId", seqId);
				mapTmp.put("proName", proName);
				list.add(mapTmp);
			}
			for (int j = 0; j < list.size(); j++) {
				Map tmpMap = list.get(j);
				sb.append("{");
				sb.append("seqId:" + tmpMap.get("seqId"));
				sb.append(",proName:\"" + tmpMap.get("proName") + "\"");
				sb.append("},");
			}
			if (list.size() > 0)
				sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			sb.append("}");
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return sb.toString();
	}

	/**
	 * 模糊查询名称
	 * 
	 * @param conn
	 * @param user
	 * @param officeId
	 * @return
	 * @throws Exception
	 */
	public static String getOfficeProductNameLogic(Connection conn, T9Person user, String proNames) throws Exception {
		StringBuffer sb = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int SysTen = 0;
		// String findSeq =
		// " select SEQ_ID, TYPE_NAME from OFFICE_TYPE where type_depository = "+officeId+"";
		String findSql = "select a.PRO_NAME,a.PRO_STOCK,a.SEQ_ID from OFFICE_PRODUCTS a left outer join OFFICE_TYPE b on a.OFFICE_PROTYPE=b.seq_id left outer join "
				+ " OFFICE_DEPOSITORY c on b.TYPE_DEPOSITORY=c.seq_id where a.PRO_NAME like '%" + proNames + "%'";
		/*
		 * + " and (" + T9DBUtility.findInSet(user.getDeptId()+"", "a.DEPT_ID") +
		 * " or " + T9DBUtility.findInSet("ALL","a.DEPT_ID") + " or " +
		 * T9DBUtility.findInSet("0","a.DEPT_ID") + " or " +
		 * T9DBUtility.findInSet(user.getSeqId() + "", "a.PRO_MANAGER") + ")" +
		 * " and (" + T9DBUtility.findInSet(user.getDeptId()+"", "c.DEPT_ID") +
		 * " or " + T9DBUtility.findInSet("ALL","c.DEPT_ID") + " or " +
		 * T9DBUtility.findInSet("0","c.DEPT_ID") + ")" ;
		 */
		try {
			sb.append("{");
			sb.append("listData:[");
			ps = conn.prepareStatement(findSql);
			rs = ps.executeQuery();
			List<Map> list = new ArrayList();
			while (rs.next()) {
				String seqId = rs.getString("SEQ_ID");
				String proName = rs.getString("PRO_NAME");
				String proStock = rs.getString("PRO_STOCK");
				Map mapTmp = new HashMap();
				mapTmp.put("seqId", seqId);
				mapTmp.put("proName", proName);
				mapTmp.put("proStock", proStock);
				list.add(mapTmp);
			}
			for (int j = 0; j < list.size(); j++) {
				Map tmpMap = list.get(j);
				sb.append("{");
				sb.append("seqId:" + tmpMap.get("seqId"));
				sb.append(",proName:\"" + tmpMap.get("proName") + "\"");
				sb.append(",proStock:\"" + tmpMap.get("proStock") + "\"");
				sb.append("},");
			}
			if (list.size() > 0)
				sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			sb.append("}");
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return sb.toString();
	}

	/**
	 * 新建个人办公用品登记
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @throws Exception
	 */

	public void setPersonOfficeRecordLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
		T9ORM orm = new T9ORM();
		String transFlag = fileForm.getParameter("TRANS_FLAG");// 登记类型
		String officeDepository = fileForm.getParameter("OFFICE_DEPOSITORY");// 办公用品库
		String officeProtype = fileForm.getParameter("OFFICE_PROTYPE");// 办公用品类别
		String proId = fileForm.getParameter("PRO_ID");// 办公用品
		String proName = fileForm.getParameter("PRO_NAME");// 模糊名称
		String transQty = fileForm.getParameter("TRANS_QTY");// 申请数量
		String remark = fileForm.getParameter("REMARK");// 备注
		String smsRemind = fileForm.getParameter("SMS_REMIND");// 短信提醒
		smsRemind = "1";
		String userName = person.getUserName();// 新建办公用品登记人 相对应于borrower 这个字段
		int userId = person.getSeqId();
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime); // 系统登录时间

		try {
			T9OfficeTranshistory officeTrans = new T9OfficeTranshistory();
			if (!T9Utility.isNullorEmpty(proName)) {
				if (proName.indexOf("/") != -1) {// 查询办公用品中的名称，如果有名称就像office_transhistory表增条数据，
					String proName1 = proName.substring(0, proName.indexOf("/"));
					String proStrock = proName.substring(proName.indexOf("/库存") + 3);
					int proSeqId = getOfficeProductsName(dbConn, person, proName1, proStrock);// 获得办公用品ID
					// 增加关联表office_transhistory一条数据
					officeTrans.setProId(proSeqId);
					officeTrans.setTransFlag(transFlag);
					if (!T9Utility.isNullorEmpty(transQty)) {
						officeTrans.setTransQty(Integer.valueOf(transQty));
					}
					officeTrans.setRemark(remark);
					if (!T9Utility.isNullorEmpty(dateString)) {// 系统登录时间
						officeTrans.setTransDate(T9Utility.parseDate("yyyy-MM-dd", dateString));
					}
					officeTrans.setTransState("0");// 待批准状态
					insertOfficeTrans(dbConn, person, officeTrans);// 当选择模糊名称 增加一条数据
				} else {
					officeTrans.setProId(0);// 没有和办公用品表对应的id 默认为0表示（此物品已删除）
					officeTrans.setTransFlag(transFlag);
					if (!T9Utility.isNullorEmpty(transQty)) {
						officeTrans.setTransQty(Integer.valueOf(transQty));
					}
					officeTrans.setRemark(remark);
					if (!T9Utility.isNullorEmpty(dateString)) {// 系统登录时间
						officeTrans.setTransDate(T9Utility.parseDate("yyyy-MM-dd", dateString));
					}
					officeTrans.setTransState("0");
					insertOfficeTrans(dbConn, person, officeTrans);// 当选择模糊名称 增加一条数据()
				}
			} else {

				if (!T9Utility.isNullorEmpty(proId)) {// 办公用品
					officeTrans.setProId(Integer.valueOf(proId));
				}
				if (!T9Utility.isNullorEmpty(String.valueOf(userId))) {// 新建办公用品登记人
					// 相对应于borrower
					// 这个字段
					officeTrans.setBorrower(String.valueOf(userId));
				}
				officeTrans.setTransFlag(transFlag);// 登记类型
				if (!T9Utility.isNullorEmpty(transQty)) {// 申请数量
					officeTrans.setTransQty(Integer.valueOf(transQty));
				}
				officeTrans.setPrice(0);// 暂不知这个字段意思？？？
				officeTrans.setRemark(remark);//
				if (!T9Utility.isNullorEmpty(dateString)) {// 系统登录时间
					officeTrans.setTransDate(T9Utility.parseDate("yyyy-MM-dd", dateString));
				}
				officeTrans.setOperator(String.valueOf(userId));// 操作者？？？目前用系统登录人
				officeTrans.setTransState("0");// 新建个人登记办公用品设置 为 0
				officeTrans.setDeptId(person.getDeptId());// 部门id
				orm.saveSingle(dbConn, officeTrans);
				findSmsSendOfficeInfo(dbConn, person, officeTrans.getProId(), transFlag ,officeTrans);
			}
			/*
			 * if(!T9Utility.isNullorEmpty(remindTime)){
			 * T9NewLicenseInfoLogic.sendSms(person, dbConn, licenseInfo, null,
			 * userId,T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", remindTime)); }
			 */
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String setPersonOfficeRecordLogic2(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception{
		String transFlag = fileForm.getParameter("TRANS_FLAG");// 登记类型
		String officeDepository = fileForm.getParameter("OFFICE_DEPOSITORY");// 办公用品库
		String officeProtype = fileForm.getParameter("OFFICE_PROTYPE");// 办公用品类别
		String proIdStr = fileForm.getParameter("PRO_ID");// 办公用品
		String proName = fileForm.getParameter("PRO_NAME");// 模糊名称
		String transQtyStr = fileForm.getParameter("TRANS_QTY");// 申请数量
		String remark = fileForm.getParameter("REMARK");// 备注
		String smsRemind = fileForm.getParameter("SMS_REMIND");// 短信提醒
		smsRemind = "1";
		
		int transQty = 0;
		if (T9Utility.isNumber(transQtyStr)) {
			transQty = Integer.parseInt(transQtyStr);
		}
		int proId = 0;
		if (T9Utility.isNumber(proIdStr)) {
			proId = Integer.parseInt(proIdStr);
		}
		T9ORM orm = new T9ORM();
		try {
			int dbTransQty = transQty;
			if ("1".equalsIgnoreCase(transFlag) || "2".equalsIgnoreCase(transFlag)) {
				transQty = transQty *(-1);
			}
			T9OfficeTranshistory officeTrans = new T9OfficeTranshistory();
			officeTrans.setProId(proId);
			officeTrans.setBorrower(String.valueOf(person.getSeqId()));
			officeTrans.setTransFlag(transFlag);
			officeTrans.setTransQty(transQty);
			officeTrans.setPrice(0); //暂时不清楚从哪传来
			officeTrans.setRemark(remark);
			officeTrans.setTransDate(T9Utility.parseTimeStamp());
			officeTrans.setOperator(String.valueOf(person.getSeqId()));
			officeTrans.setTransState("0");
			officeTrans.setDeptId(person.getDeptId());
			orm.saveSingle(dbConn, officeTrans);
			return this.findSmsSendOfficeInfo(dbConn, person, officeTrans.getProId(), transFlag ,officeTrans);
		} catch (Exception e) {
			throw e;
		}
	}
	
	

	/**
	 * 目的是获得办公用品ID 增加关联表office_transhistory一条数据
	 * 
	 * @param conn
	 * @param user
	 * @param proName1
	 * @param proStrock
	 * @return
	 * @throws Exception
	 */
	public int getOfficeProductsName(Connection conn, T9Person user, String proName1, String proStrock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String findSql = "select SEQ_ID, PRO_NAME from office_products where PRO_NAME ='" + proName1 + "' and PRO_STOCK =" + proStrock;
		List<T9OfficeProducts> products = new ArrayList<T9OfficeProducts>();
		try {
			ps = conn.prepareStatement(findSql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("SEQ_ID");
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return 0;
	}

	/**
	 * 当选择模糊名称 增加一条数据
	 * 
	 * @param conn
	 * @param user
	 * @param officeTrans
	 * @throws Exception
	 */
	public void insertOfficeTrans(Connection conn, T9Person user, T9OfficeTranshistory officeTrans) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String insertSql = "insert into office_transhistory(PRO_ID,TRANS_FLAG,TRANS_QTY,REMARK,TRANS_DATE,TRANS_STATE) values (?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(insertSql);
			ps.setInt(1, officeTrans.getProId());
			ps.setString(2, officeTrans.getTransFlag());
			ps.setInt(3, officeTrans.getTransQty());
			ps.setString(4, officeTrans.getRemark());
			ps.setDate(5, new java.sql.Date(officeTrans.getTransDate().getTime()));
			ps.setString(6, officeTrans.getTransState());
			ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
	}

	/**
	 * 新建个人办公用品登记 成功后，发内部短信
	 * 
	 * @param dbConn
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public String findSmsSendOfficeInfo(Connection dbConn, T9Person user, int id, String transFlag ,T9OfficeTranshistory officeTrans) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String proAuditer = "";
		String proName = "";
		String manager = "";
		String recordType = "";
		String smsContent = "";
		String proKeeper = "";
		String workSql = "select a.PRO_AUDITER,a.PRO_NAME,c.MANAGER,c.PRO_KEEPER from OFFICE_PRODUCTS a left outer join OFFICE_TYPE b on a.OFFICE_PROTYPE=b.seq_id "
				+ " left outer join OFFICE_DEPOSITORY c on b.TYPE_DEPOSITORY= c.seq_id where a.seq_id = " + id;
		try {
			ps = dbConn.prepareStatement(workSql);
			rs = ps.executeQuery();
			while (rs.next()) {
			  proKeeper = rs.getString("PRO_KEEPER");
				proAuditer = rs.getString("PRO_AUDITER");
				proName = rs.getString("PRO_NAME");
				manager = rs.getString("MANAGER");
			}
			String module = "";
      if("1".equals(transFlag))
      {
        recordType ="领用";
        module ="office_product_draw";
      }  
      if("2".equals(transFlag))
      {
        recordType ="借用";
        module ="office_product_borrow";
      }   
      if("3".equals(transFlag))
      {
        recordType ="归还";
        module ="office_product_return";
      }
		  Map dataArray = new HashMap();
		  T9FlowHookUtility ut = new T9FlowHookUtility();
      int seqId = ut.getMax(dbConn, "select max(SEQ_ID) FROM OFFICE_TRANSHISTORY");
      
			dataArray.put("KEY", seqId + "");
      dataArray.put("FIELD", "TRANS_ID");
      dataArray.put("PRO_ID", id + "");
      dataArray.put("PRO_NAME", proName);
      dataArray.put("BORROWER", user.getUserName());
      dataArray.put("BORROWER_ID", user.getSeqId() + "");
      dataArray.put("REMARK", officeTrans.getRemark());
      int data = officeTrans.getTransQty();
      if (data < 0) {
        data = data * -1;
      }
      dataArray.put("TRANS_QTY",  data + "");
      
      String url = ut.runHook(dbConn,user, dataArray,  module );
      if (!"".equals(url)) {
        return url;
      }
			smsContent = "请审批" + user.getUserName() + "的办公用品" + recordType + "申请。";
			url = "/subsys/oa/officeProduct/manage/transInfo.jsp";
			if (!T9Utility.isNullorEmpty(proAuditer)) {
				T9PersonalOfficeRecordLogic.sendSms(user, dbConn, smsContent, url, proAuditer, null, "43");
			}
			if (T9Utility.isNullorEmpty(proAuditer) && !T9Utility.isNullorEmpty(manager)) {
				T9PersonalOfficeRecordLogic.sendSms(user, dbConn, smsContent, url, manager, null, "43");
			}
//      if (!T9Utility.isNullorEmpty(proKeeper)) {
//        T9PersonalOfficeRecordLogic.sendSms(user, dbConn, smsContent, url, proKeeper, null, "43");
//      }
		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return null;
	}

	/**
	 * 内部短信提醒
	 * 
	 * @param user
	 * @param dbConn
	 * @param content
	 * @param url
	 * @param toId
	 * @param date
	 * @throws Exception
	 */
	public static void sendSms(T9Person user, Connection dbConn, String content, String url, String toId, Date date) throws Exception {
		T9SmsBack smsBack = new T9SmsBack();
		smsBack.setContent(content);
		smsBack.setFromId(user.getSeqId());
		smsBack.setRemindUrl(url);
		smsBack.setSmsType("0");
		smsBack.setToId(toId);
		if (date != null) {
			smsBack.setSendDate(date);
		}
		T9SmsUtil.smsBack(dbConn, smsBack);
	}
	
	 /**
   * 内部短信提醒
   * 
   * @param user
   * @param dbConn
   * @param content
   * @param url
   * @param toId
   * @param date
   * @throws Exception
   */
  public static void sendSms(T9Person user, Connection dbConn, String content, String url, String toId, Date date, String type) throws Exception {
    T9SmsBack smsBack = new T9SmsBack();
    smsBack.setContent(content);
    smsBack.setFromId(user.getSeqId());
    smsBack.setRemindUrl(url);
    smsBack.setSmsType(type);
    smsBack.setToId(toId);
    if (date != null) {
      smsBack.setSendDate(date);
    }
    T9SmsUtil.smsBack(dbConn, smsBack);
  }

	/**
	 * 通过办公用品类型 获得办公用品
	 * 
	 * @param conn
	 * @param user
	 * @param officeId
	 * @return
	 * @throws Exception
	 */
	public static List<T9OfficeProducts> getOfficeProductsInfo(Connection conn, T9Person user, String officeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String findSql = "select SEQ_ID, PRO_NAME from office_products where OFFICE_PROTYPE =" + officeId + "";
		List<T9OfficeProducts> products = new ArrayList<T9OfficeProducts>();
		try {
			ps = conn.prepareStatement(findSql);
			rs = ps.executeQuery();

			while (rs.next()) {
				T9OfficeProducts product = new T9OfficeProducts();
				product.setSeqId(rs.getInt("SEQ_ID"));
				product.setProName(rs.getString("PRO_NAME"));
				products.add(product);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return products;
	}

	/**
	 * 获取办公用品库(返回库字段OFFICE_TYPE_ID)新建办公用品登记页面
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getDepositoryNamesLogic(Connection dbConn, T9Person person, String extData) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select OFFICE_TYPE_ID,DEPOSITORY_NAME from OFFICE_DEPOSITORY where "
				+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "DEPT_ID") + " or DEPT_ID = '' or DEPT_ID = 'ALL_DEPT' or DEPT_ID = '0'";
		boolean isHave = false;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String typeIdStr = T9Utility.null2Empty(rs.getString("OFFICE_TYPE_ID"));
				String depositoryName = T9Utility.null2Empty(rs.getString("DEPOSITORY_NAME"));
				int selectFlag = 0;
				if (!T9Utility.isNullorEmpty(extData)) {
					boolean isHaveFlag = this.isFindInSet(typeIdStr.split(","), extData);
					if (isHaveFlag) {
						selectFlag = 1;
					}
				}
				buffer.append("{");
				buffer.append("typeId:\"" + T9Utility.encodeSpecial(typeIdStr) + "\"");
				buffer.append(",name:\"" + T9Utility.encodeSpecial(depositoryName) + "\"");
				buffer.append(",selectFlag:\"" + selectFlag + "\"");
				buffer.append("},");
				isHave = true;
			}
			if (isHave) {
				buffer = buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return buffer.toString();
	}

	/**
	 * 判断数组中是否有该值
	 * 
	 * @param str
	 * @param deptIdStr
	 * @return
	 */
	public boolean isFindInSet(String[] str, String deptIdStr) {
		if (T9Utility.isNullorEmpty(deptIdStr)) {
			return false;
		}
		for (int y = 0; y < str.length; y++) {
			if (str[y].equals(deptIdStr)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

}
