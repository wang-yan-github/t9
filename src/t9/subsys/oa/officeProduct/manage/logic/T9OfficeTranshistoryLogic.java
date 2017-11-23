package t9.subsys.oa.officeProduct.manage.logic;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.officeProduct.manage.data.T9OfficeTranshistory;
import t9.subsys.oa.officeProduct.product.data.T9OfficeProducts;
import t9.subsys.oa.officeProduct.query.data.T9OfficeType;

public class T9OfficeTranshistoryLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.officeProduct.manage.logic.T9OfficeTranshistoryLogic.java");

	public String getTranshistoryListLogic(Connection dbConn, Map request, T9Person person) throws Exception {

		String conditionStr = " and (b.PRO_AUDITER like '' or b.PRO_AUDITER is null or " + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "b.PRO_AUDITER") + " or " + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "d.MANAGER") + ")" ;
		String dbTempStr = "";
    String dbms = T9SysProps.getProp("db.jdbc.dbms");
    if (dbms.equals("sqlserver")) {
    }else if (dbms.equals("mysql")) {
    }else if (dbms.equals("oracle")) {
      dbTempStr = "||''";
    }
		try {
			String sql = "";
			sql = "(select " 
				+ " a.SEQ_ID" 
				+ ", PRO_NAME" 
				+ ", TRANS_FLAG" 
				+ ", BORROWER" 
				+ ", TRANS_QTY" +dbTempStr
				+ ", TRANS_DATE" 
				+ ", PRO_ID"
				+ ", CYCLE_NO"
				+ ",1,1"
				+ " from  OFFICE_TRANSHISTORY a, OFFICE_PRODUCTS b,OFFICE_TYPE c,OFFICE_DEPOSITORY d " 
				+ " where (a.cycle !='1' or a.cycle='' or a.cycle is null)"
				+ " and a.TRANS_STATE = '0' " 
				+ " and b.OFFICE_PROTYPE=c.SEQ_ID " 
				+ " and c.TYPE_DEPOSITORY=d.SEQ_ID " 
				+ " and a.PRO_ID=b.SEQ_ID "
				+ conditionStr + ")"
				+ "  union all  "
				+ " (select 0 SEQ_ID, '批量申请' PRO_NAME, TRANS_FLAG, BORROWER,'-' TRANS_QTY, TRANS_DATE, 0 PRO_ID, CYCLE_NO,1,1 "
				+ " from  OFFICE_TRANSHISTORY a, OFFICE_PRODUCTS b,OFFICE_TYPE c,OFFICE_DEPOSITORY d "
				+ " where a.cycle ='1' "
				+ " and a.TRANS_STATE = '0' "
				+ " and b.OFFICE_PROTYPE=c.SEQ_ID "
				+ " and c.TYPE_DEPOSITORY=d.SEQ_ID "
				+ " and a.PRO_ID=b.SEQ_ID "
				+ conditionStr
				+ " group by TRANS_FLAG, BORROWER, TRANS_DATE, CYCLE_NO )"	;
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
			 T9FlowHookUtility fu = new T9FlowHookUtility();
       T9FlowRunUtility ru = new T9FlowRunUtility();
			for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
			  T9DbRecord r = pageDataList.getRecord(i);
			  int seqId = T9Utility.cast2Long(r.getValueByName("seqId")).intValue();
			  int runId = fu.isRunHook(dbConn, "TRANS_ID", seqId + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        r.addField("flowId", flowId);
        r.addField("runId", runId);
			}
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 取得人员名称
	 * 
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getUserNameLogic(Connection dbConn, String seqIdStr) throws Exception {
		String result = "";
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			return result;
		}
		if (seqIdStr.endsWith(",")) {
			seqIdStr = seqIdStr.substring(0, seqIdStr.length() - 1);
		}
		String sql = " select USER_NAME from PERSON where SEQ_ID in(" + seqIdStr + ")";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result += toId + ",";
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * 获取详情
	 * 
	 * @param dbConn
	 * @param tranSeqIdStr
	 * @return
	 * @throws Exception
	 */
	public String getTransDetailByIdLogic(Connection dbConn, String tranSeqIdStr) throws Exception {
		T9ORM orm = new T9ORM();

		int tranSeqId = 0;
		if (T9Utility.isNumber(tranSeqIdStr)) {
			tranSeqId = Integer.parseInt(tranSeqIdStr);
		}
		StringBuffer buffer = new StringBuffer();
		try {
			T9OfficeTranshistory transhistory = (T9OfficeTranshistory) orm.loadObjSingle(dbConn, T9OfficeTranshistory.class, tranSeqId);
			double proPrice = 0;
			int proLowstock = 0;
			int proMaxstock = 0;
			int proStock = 0;
			int transqty = 0;
			String proName = "";
			int officeProtypeId = 0;
			String typeName = "";

			String transFlag = "";
			String remark = "";
			String transDate = "";
			String transState = "";
			String borrowerName = "";
			String transName = "";
			int dbProId = 0;
			if (transhistory != null) {
				dbProId = transhistory.getProId();
				transFlag = T9Utility.null2Empty(transhistory.getTransFlag());
				transqty = transhistory.getTransQty();
				remark = T9Utility.null2Empty(transhistory.getRemark());
				transDate = T9Utility.getDateTimeStr(transhistory.getTransDate());
				transState = T9Utility.null2Empty(transhistory.getTransState());
				String borrower = T9Utility.null2Empty(transhistory.getBorrower());

				borrowerName = this.getUserNameLogic(dbConn, borrower);
				if (T9Utility.isNullorEmpty(borrowerName)) {
					borrowerName = "已不存在该用户";
				}
				T9OfficeProducts products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, dbProId);
				if (products != null) {
					proName = T9Utility.null2Empty(products.getProName());
					String officeProtype = T9Utility.null2Empty(products.getOfficeProtype());
					proPrice = products.getProPrice();
					proLowstock = products.getProLowstock();
					proMaxstock = products.getProMaxstock();
					proStock = products.getProStock();
					if (T9Utility.isNumber(officeProtype)) {
						officeProtypeId = Integer.parseInt(officeProtype);
					}
				} else {
					proName = "此物品已删除";
				}

				T9OfficeType officeType = (T9OfficeType) orm.loadObjSingle(dbConn, T9OfficeType.class, officeProtypeId);
				if (officeType != null) {

					typeName = T9Utility.null2Empty(officeType.getTypeName());
				} else {
					typeName = "此类别已删除";
				}
				if ("1".equals(transFlag)) {
					transName = "领用";
				}
				if ("2".equals(transFlag)) {
					transName = "借用";
				}
				if ("3".equals(transFlag)) {
					transName = "归还";
				}
			}
			buffer.append("{");
			buffer.append("transName:\"" + T9Utility.encodeSpecial(transName) + "\"");
			buffer.append(",borrowerName:\"" + T9Utility.encodeSpecial(borrowerName) + "\"");
			buffer.append(",borrower:\"" + transhistory.getBorrower() + "\"");
			buffer.append(",transDate:\"" + T9Utility.encodeSpecial(transDate) + "\"");
			buffer.append(",typeName:\"" + T9Utility.encodeSpecial(typeName) + "\"");
			buffer.append(",proName:\"" + T9Utility.encodeSpecial(proName) + "\"");
			buffer.append(",proStock:\"" + proStock + "\"");
			buffer.append(",proMaxstock:\"" + proMaxstock + "\"");

			buffer.append(",proLowstock:\"" + proLowstock + "\"");
			buffer.append(",proPrice:\"" + proPrice + "\"");
			buffer.append(",transqty:\"" + transqty + "\"");
			buffer.append(",remark:\"" + T9Utility.encodeSpecial(remark) + "\"");
			buffer.append(",transFlag:\"" + T9Utility.encodeSpecial(transFlag) + "\"");
			buffer.append(",proId:\"" + dbProId + "\"");
			buffer.append("}");

		} catch (Exception e) {
			throw e;
		}
		return buffer.toString();
	}
	
	
	 /**
   * 获取详情
   * 
   * @param dbConn
   * @param tranSeqIdStr
   * @return
   * @throws Exception
   */
  public String getTransDetailByCycleNoLogic(Connection dbConn, String cycleNoStr) throws Exception {
    T9ORM orm = new T9ORM();

    int cycleNo = 0;
    if (T9Utility.isNumber(cycleNoStr)) {
      cycleNo = Integer.parseInt(cycleNoStr);
    }
    StringBuffer buffer = new StringBuffer("[");
    try {
      Map map = new HashMap();
      map.put("cycle_no", cycleNo);
      List list = new ArrayList();
      list = orm.loadListSingle(dbConn, T9OfficeTranshistory.class, map);
      for(int i = 0;i<list.size();i++){
        T9OfficeTranshistory transhistory = (T9OfficeTranshistory) list.get(i);
        int transId = transhistory.getSeqId();
        double proPrice = 0;
        int proLowstock = 0;
        int proMaxstock = 0;
        int proStock = 0;
        int transqty = 0;
        String proName = "";
        int officeProtypeId = 0;
        String typeName = "";
  
        String transFlag = "";
        String remark = "";
        String transDate = "";
        String transState = "";
        String borrowerName = "";
        String transName = "";
        int dbProId = 0;
        if (transhistory != null) {
          dbProId = transhistory.getProId();
          transFlag = T9Utility.null2Empty(transhistory.getTransFlag());
          transqty = transhistory.getTransQty();
          remark = T9Utility.null2Empty(transhistory.getRemark());
          transDate = T9Utility.getDateTimeStr(transhistory.getTransDate());
          transState = T9Utility.null2Empty(transhistory.getTransState());
          String borrower = T9Utility.null2Empty(transhistory.getBorrower());
  
          borrowerName = this.getUserNameLogic(dbConn, borrower);
          if (T9Utility.isNullorEmpty(borrowerName)) {
            borrowerName = "已不存在该用户";
          }
          T9OfficeProducts products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, dbProId);
          if (products != null) {
            proName = T9Utility.null2Empty(products.getProName());
            String officeProtype = T9Utility.null2Empty(products.getOfficeProtype());
            proPrice = products.getProPrice();
            proLowstock = products.getProLowstock();
            proMaxstock = products.getProMaxstock();
            proStock = products.getProStock();
            if (T9Utility.isNumber(officeProtype)) {
              officeProtypeId = Integer.parseInt(officeProtype);
            }
          } else {
            proName = "此物品已删除";
          }
  
          T9OfficeType officeType = (T9OfficeType) orm.loadObjSingle(dbConn, T9OfficeType.class, officeProtypeId);
          if (officeType != null) {
  
            typeName = T9Utility.null2Empty(officeType.getTypeName());
          } else {
            typeName = "此类别已删除";
          }
          if ("1".equals(transFlag)) {
            transName = "领用";
          }
          if ("2".equals(transFlag)) {
            transName = "借用";
          }
          if ("3".equals(transFlag)) {
            transName = "归还";
          }
        }
        buffer.append("{");
        buffer.append("transId:\"" + transId + "\"");
        buffer.append(",transName:\"" + T9Utility.encodeSpecial(transName) + "\"");
        buffer.append(",borrowerName:\"" + T9Utility.encodeSpecial(borrowerName) + "\"");
        buffer.append(",borrower:\"" + transhistory.getBorrower() + "\"");
        buffer.append(",transDate:\"" + T9Utility.encodeSpecial(transDate) + "\"");
        buffer.append(",typeName:\"" + T9Utility.encodeSpecial(typeName) + "\"");
        buffer.append(",proName:\"" + T9Utility.encodeSpecial(proName) + "\"");
        buffer.append(",proStock:\"" + proStock + "\"");
        buffer.append(",proMaxstock:\"" + proMaxstock + "\"");
  
        buffer.append(",proLowstock:\"" + proLowstock + "\"");
        buffer.append(",proPrice:\"" + proPrice + "\"");
        buffer.append(",transqty:\"" + transqty + "\"");
        buffer.append(",remark:\"" + T9Utility.encodeSpecial(remark) + "\"");
        buffer.append(",transFlag:\"" + T9Utility.encodeSpecial(transFlag) + "\"");
        buffer.append(",proId:\"" + dbProId + "\"");
        buffer.append("},");
      }
      buffer.deleteCharAt(buffer.length()-1);
      buffer.append("]");
    } catch (Exception e) {
     e.printStackTrace();
    }
    return buffer.toString();
  }


	/**
	 * 处理transDetail
	 * 
	 * @param dbConn
	 * @param transFlag
	 * @param setPriv
	 * @param proIdStr
	 * @param person
	 * @throws Exception
	 */
	public void transHandleLogic(Connection dbConn, Map<Object, Object> infoMap, T9Person person) throws Exception {
		int transId = (Integer) infoMap.get("transId"); //登记表id
		int factQty = (Integer) infoMap.get("factQty");//实际数量
		int transQty = (Integer) infoMap.get("transQty"); //申请数量
		int proId = (Integer) infoMap.get("proId");  //产品id
		String smsRemind1 = (String) infoMap.get("smsRemind1");//提醒物品调度员
		String smsRemind = (String) infoMap.get("smsRemind");//提醒申请人
		String borrower = (String) infoMap.get("borrower"); //申请人
		String removeReason = (String) infoMap.get("removeReason"); //不同意理由
		String setPriv = (String) infoMap.get("setPriv");  //1为同意，2为不同意
		String transFlag = (String) infoMap.get("transFlag"); //0采购入库,1领用,2借用,3归还,4报废,5维护
		String transFlagStr = "";
		switch(Integer.parseInt(transFlag)){
		  case 0 : transFlagStr = "采购入库";break;
		  case 1 : transFlagStr = "领用";break;
		  case 2 : transFlagStr = "借用";break;
		  case 3 : transFlagStr = "归还";break;
		  case 4 : transFlagStr = "报废";break;
		  case 5 : transFlagStr = "维护";break;
		}


		T9ORM orm = new T9ORM();
		try {
			Map<Object, Object> map = this.getProInfoById(dbConn, proId);
			int proStock = (Integer) map.get("proStock");
			double proPrice = (Double) map.get("proPrice");
			String manager = (String) map.get("manager");
			String proKeeper = (String) map.get("proKeeper");
			String proUnit = (String) map.get("proUnit");
			String proName = (String) map.get("proName");

			T9OfficeTranshistory transhistory = (T9OfficeTranshistory) orm.loadObjSingle(dbConn, T9OfficeTranshistory.class, transId);
			String userName = person.getUserName();
			
			if ("1".equals(setPriv)) {

				T9OfficeProducts products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, proId);
				if (!"3".equals(transFlag)) {  //不是归还操作
				  int factTransQty = factQty * (-1);
					
					if (transhistory != null) {
						transhistory.setFactQty(factQty);
						transhistory.setOperator(String.valueOf(person.getSeqId()));
						transhistory.setTransState("1");
						transhistory.setTransQty(factTransQty);
						transhistory.setPrice(proPrice);
						orm.updateSingle(dbConn, transhistory);
					}
					if (products != null) {
						int newProStock = proStock - factQty;  //新的库存 = 库存总数-实际申请数
						products.setProStock(newProStock);
						orm.updateSingle(dbConn, products);
					}
				} else {
					transhistory.setTransQty(transQty);
					transhistory.setOperator(String.valueOf(person.getSeqId()));
					transhistory.setTransState("1");
					transhistory.setPrice(proPrice);
					orm.updateSingle(dbConn, transhistory);
					if (products != null) {
						int newProStock = proStock + transQty; //新的库存 = 库存总数+实际申请数
						products.setProStock(newProStock);
						orm.updateSingle(dbConn, products);
					}
				}

				int proStockr = 0;
				int proLowstockr = 0;
				int proMaxstockr = 0;
				String proNamer = "";
				if (products != null) {

					proStockr = products.getProStock();
					proLowstockr = products.getProLowstock();
					proMaxstockr = products.getProMaxstock();
					proNamer = T9Utility.null2Empty(products.getProName());
				}
				if (!T9Utility.isNullorEmpty(manager)) {
					if (proStockr < proLowstockr) {
						// sendSms();
					}
				}

				if ("1".equals(smsRemind1) && !T9Utility.isNullorEmpty(proKeeper)) {
				  String borrowerName = "";
				  if(T9Utility.isInteger(borrower)){
				    T9Person borrowerPerson = (T9Person)orm.loadObjSingle(dbConn, T9Person.class, Integer.parseInt(borrower));
				    borrowerName = borrowerPerson.getUserName();
				  }
				  
				  doSmsBackTime(dbConn, "["+userName+"]同意了["+borrowerName+"]"+transFlagStr+factQty+proUnit+proName+"的申请，请准备物品", person.getSeqId(), proKeeper, "0", "", new Date());
				}
				if ("1".equals(smsRemind) && !T9Utility.isNullorEmpty(borrower)) {
				  doSmsBackTime(dbConn, "["+userName+"]同意了您"+transFlagStr+factQty+proUnit+proName+"的申请", person.getSeqId(), borrower, "0", "", new Date());
				}
			} else {
				transhistory.setTransState("2");
				transhistory.setReason(removeReason);
				orm.updateSingle(dbConn, transhistory);
				if ("1".equals(smsRemind) && !T9Utility.isNullorEmpty(borrower)) {
				  doSmsBackTime(dbConn, "["+userName+"]没有同意您"+transFlagStr+transQty+proUnit+proName+"的申请", person.getSeqId(), borrower, "0", "", new Date());
				}
			}

		} catch (Exception e) {
			throw e;
		}

	}

	public Map<Object, Object> getProInfoById(Connection dbConn, int proId) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select PRO_UNIT ,PRO_NAME,PRO_STOCK,PRO_PRICE,PRO_KEEPER,MANAGER  from OFFICE_PRODUCTS a left outer join OFFICE_TYPE b on a.OFFICE_PROTYPE=b.SEQ_ID left outer join OFFICE_DEPOSITORY c on b.TYPE_DEPOSITORY=c.SEQ_ID where a.SEQ_ID =?";
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, proId);
			rs = stmt.executeQuery();
			int proStock = 0;
			double proPrice = 0;
			String manager = "";
			String proKeeper = "";
			String proUnit = "";
			String proName = "";
			if (rs.next()) {
				proStock = rs.getInt("PRO_STOCK");
				proPrice = rs.getDouble("PRO_PRICE");
				manager = T9Utility.null2Empty(rs.getString("MANAGER"));
				proKeeper = T9Utility.null2Empty(rs.getString("PRO_KEEPER"));
				proUnit = T9Utility.null2Empty(rs.getString("PRO_UNIT"));
				proName = T9Utility.null2Empty(rs.getString("PRO_NAME"));
			}
			map.put("proStock", proStock);
			map.put("proPrice", proPrice);
			map.put("manager", manager);
			map.put("proKeeper", proKeeper);
			map.put("proUnit", proUnit);
			map.put("proName", proName);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return map;
	}

	/**
	 * 获取办公用品库(返回库字段OFFICE_TYPE_ID)产品编辑页面
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getproEditDepositoryNamesLogic(Connection dbConn, T9Person person, String extData) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select OFFICE_TYPE_ID,DEPOSITORY_NAME from OFFICE_DEPOSITORY";
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
	 * 获取办公用品名称
	 * 
	 * @param dbConn
	 * @param person
	 * @param extData
	 * @param idStr
	 * @return
	 * @throws Exception
	 */
	public String getProductsNamesById(Connection dbConn, T9Person person, String extData, String idStr) throws Exception {
		int id = 0;
		if (T9Utility.isNumber(idStr)) {
			id = Integer.parseInt(idStr);
		}
		StringBuffer buffer = new StringBuffer("[");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String conditionStr = "";
		if (!person.isAdminRole()) {
			conditionStr = "(" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT")
					+ " or( PRO_MANAGER like '' and PRO_DEPT like '' ) or PRO_DEPT like 'ALL_DEPT')";
		} else {
			conditionStr = " 1=1";
		}
		String sql = "select SEQ_ID,PRO_NAME,PRO_STOCK from OFFICE_PRODUCTS where OFFICE_PROTYPE ='" + id + "' and " + conditionStr;
		boolean isHave = false;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int proId = rs.getInt("SEQ_ID");
				String proName = T9Utility.null2Empty(rs.getString("PRO_NAME"));
				int proStock = rs.getInt("PRO_STOCK");
				// boolean isHaveFlag = this.isFindInSet(typeIdStr.split(","), extData);
				// int selectFlag = 0;
				// if (isHaveFlag) {
				// selectFlag = 1;
				// }
				buffer.append("{");
				buffer.append("proId:\"" + proId + "\"");
				buffer.append(",proName:\"" + T9Utility.encodeSpecial(proName) + "\"");
				buffer.append(",proStock:\"" + proStock + "\"");
				// buffer.append(",selectFlag:\"" + selectFlag + "\"");
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
	 * 获取办公用品名称(库存登记,不需id)
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getProductsNames(Connection dbConn, T9Person person) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String conditionStr = "";
		if (!person.isAdminRole()) {
			conditionStr = " and (" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT")
					+ " or( PRO_MANAGER like '' and PRO_DEPT like '' ) or PRO_DEPT like 'ALL_DEPT')";
		}
		String sql = "select SEQ_ID,PRO_NAME,PRO_STOCK,PRO_UNIT from OFFICE_PRODUCTS where 1=1 " + conditionStr;
		boolean isHave = false;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int proId = rs.getInt("SEQ_ID");
				String proName = T9Utility.null2Empty(rs.getString("PRO_NAME"));
				int proStock = rs.getInt("PRO_STOCK");
				String proUnit = T9Utility.null2Empty(rs.getString("PRO_UNIT"));
				buffer.append("{");
				buffer.append("proId:\"" + proId + "\"");
				buffer.append(",proName:\"" + T9Utility.encodeSpecial(proName) + "\"");
				buffer.append(",proStock:\"" + proStock + "\"");
				buffer.append(",proUnit:\"" + T9Utility.encodeSpecial(proUnit) + "\"");
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
	 * 获取办公用品名称根据ProName
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getProductsByProNameLogic(Connection dbConn, T9Person person, String nameStr) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String conditionStr = "";
		if (!person.isAdminRole()) {
			conditionStr = " (" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT")
					+ " or( PRO_MANAGER like '' and PRO_DEPT like '' ) or PRO_DEPT like 'ALL_DEPT')";
		} else {
			conditionStr = " 1=1";
		}
		String sql = "select SEQ_ID,PRO_NAME,PRO_STOCK from OFFICE_PRODUCTS where PRO_NAME like '%" + T9DBUtility.escapeLike(nameStr) + "%' " + " and"
				+ conditionStr;
		boolean isHave = false;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int proId = rs.getInt("SEQ_ID");
				String proName = T9Utility.null2Empty(rs.getString("PRO_NAME"));
				int proStock = rs.getInt("PRO_STOCK");
				buffer.append("{");
				buffer.append("proId:\"" + proId + "\"");
				buffer.append(",proName:\"" + T9Utility.encodeSpecial(proName) + "\"");
				buffer.append(",proStock:\"" + proStock + "\"");
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
		for (int y = 0; y < str.length; y++) {
			if (str[y].equals(deptIdStr)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	/**
	 * 新建库存登记
	 * 
	 * @param dbConn
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void addOfficeTransLogic(Connection dbConn, T9Person person, Map<Object, Object> map) throws Exception {
		String transFlag = (String) map.get("transFlag");  //登记类型
		String borrower = (String) map.get("borrower"); //领用人、借用人、归还人
		String price = (String) map.get("price");//单价
		String band = (String) map.get("band");  //品牌
		String company = (String) map.get("company"); //厂家
//		String officeDepository = (String) map.get("officeDepository");  //办公用品库
//		String officeProtype = (String) map.get("officeProtype");  //类别
		String officePro = (String) map.get("officePro");  //办公用品id
		String transQtyStr = (String) map.get("transQty");  //申请数量
		String repTime = (String) map.get("repTime");  //维护日期
		String remark1 = (String) map.get("remark1");  //备注
		String remark2 = (String) map.get("remark2");  //维护内容
		String proIdText = (String) map.get("proIdText"); //模糊查询的办公用品id
		int transQty = 0;
		int proId = 0;
		if (T9Utility.isNumber(transQtyStr)) {
			transQty = Integer.parseInt(transQtyStr);
		}
		if (T9Utility.isNumber(proIdText)) {
			proId = Integer.parseInt(proIdText);
		}
		if (T9Utility.isNumber(officePro)) {
			proId = Integer.parseInt(officePro);

		}
		if (T9Utility.isNullorEmpty(price)) {
			price = "0";
		}

		T9ORM orm = new T9ORM();
		try {

			double proPrice = 0;  //原用品库存单价
			int proStock = 0;  //原用品库存数量
			String manager = "";  //库管理员
			String proKeeper = "";
			String proUnit = "";  //计量单位
			String proName = "";  //用品名
			if (proId !=0) {
				Map<Object, Object> proMap = this.getOfficeProById(dbConn, person, proId);
				proPrice = (Double) proMap.get("proPrice");
				proStock = (Integer) proMap.get("proStock");
				manager = (String) proMap.get("manager");
				proKeeper = (String) proMap.get("proKeeper");
				proUnit = (String) proMap.get("proUnit"); 
				proName = (String) proMap.get("proName");
			}

			double newPrice = 0; //新价格
			int factQty = 0;  //实际数量 ,发送短信用
//			int newTransQty = 0;
			if (!T9Utility.isNullorEmpty(price) && "0".equals(transFlag)) {//0采购入库
				newPrice = Double.parseDouble(price);//输入的价格
			} else {
				newPrice = proPrice; //否则用原办公用品库存单价 
			}
			if ("1".equals(transFlag) || "2".equals(transFlag) || "4".equals(transFlag)) { //1领用、2借用、4报废
				factQty = transQty;  //实际数量 = 输入的申请数量
				transQty = transQty * (-1);  //以负号标记 申请数量
			}
			String remark = "";
			Date transDate = new Date();
			if ("5".equals(transFlag)) {  //是维护
				remark = remark2;  //维护内容
				if (!T9Utility.isNullorEmpty(repTime)) {
					transDate = T9Utility.parseDate(repTime);  //维护日期
				}
			} else {
				remark = remark1;  //备注
			}

			int deptId = 0;
			T9Person user = null;
			if (T9Utility.isNumber(borrower)) {
				user = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, Integer.parseInt(borrower));
				deptId = user.getDeptId();
			}
			T9OfficeTranshistory officeTranshistory = new T9OfficeTranshistory();
			officeTranshistory.setProId(proId);
			officeTranshistory.setBorrower(T9Utility.null2Empty(borrower));
			officeTranshistory.setTransFlag(transFlag);
			officeTranshistory.setTransQty(transQty);
			officeTranshistory.setPrice(newPrice);
			officeTranshistory.setCompany(company);
			officeTranshistory.setBand(band);
			officeTranshistory.setRemark(remark);
			officeTranshistory.setTransDate(transDate);
			officeTranshistory.setOperator(String.valueOf(person.getSeqId()));
			officeTranshistory.setDeptId(deptId);
			officeTranshistory.setTransState("1");
			orm.saveSingle(dbConn, officeTranshistory);
			
			//对办公用品表操作
			if (!"5".equals(transFlag)) {  //不是维护
				double oldSum = proStock * proPrice;  // OLD_SUM = 原办公用品库存数量 * 原办公用品库存单价 
				int newProStock = proStock + transQty;  // 新的 库存数 = 原办公用品库存数量 + 申请数（有负号）

//				double newPriced = 1;
//				if (T9Utility.isNumber(price)) {
//					newPriced = Double.parseDouble(price);
//				}
//				double newSum = 1;
//				double newProPrice = 1;
				T9OfficeProducts products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, proId);
				if ("0".equals(transFlag)) {  //采购入库
					double newSum = Double.parseDouble(price) * transQty;  //NEW_SUM = 输入的价格 + 原办公用品库存数量
					double newProPrice = (oldSum + newSum) / newProStock;
					newProPrice = Double.parseDouble(T9Utility.getFormatedStr(newProPrice, 1));    // Math.round(newProPrice); // 1 不分组，保留两位小数
					if (products != null) {
						products.setProStock(newProStock);
						products.setProPrice(newProPrice);
						orm.updateSingle(dbConn, products);
					}
				} else {
					if (products != null) {
						products.setProStock(newProStock);
						orm.updateSingle(dbConn, products);
					}
				}

				if (products != null) {// 短信提醒
				  if("1".equals(transFlag)){//1领用
				    doSmsBackTime(dbConn, "["+person.getUserName()+"]同意了["+user.getUserName()+"]"+"领用"+factQty+proUnit+proName+"的申请，请准备物品", person.getSeqId(), manager, "0", "", new Date());
				    doSmsBackTime(dbConn, "["+person.getUserName()+"]同意了["+user.getUserName()+"]"+"领用"+factQty+proUnit+proName+"的申请，请准备物品", person.getSeqId(), proKeeper, "0", "", new Date());
				  }
				  else if("2".equals(transFlag)){//2借用
				    doSmsBackTime(dbConn, "["+person.getUserName()+"]同意了["+user.getUserName()+"]"+"借用"+factQty+proUnit+proName+"的申请，请准备物品", person.getSeqId(), manager, "0", "", new Date());
				    doSmsBackTime(dbConn, "["+person.getUserName()+"]同意了["+user.getUserName()+"]"+"领用"+factQty+proUnit+proName+"的申请，请准备物品", person.getSeqId(), proKeeper, "0", "", new Date());
				  }
				}
			}

		} catch (Exception e) {
			throw e;
		}

	}
	/**
	 *更改库存登记
	 * @param dbConn
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void updateOfficeTransLogic(Connection dbConn, T9Person person, Map<Object, Object> map) throws Exception {

	  int transId = (Integer) map.get("transId");
		String transFlag = (String) map.get("transFlag");  //登记类型
		String borrower = (String) map.get("borrower");  //领用人、借用人、归还人
		String price = (String) map.get("price");  //单价
		String band = (String) map.get("band");  //品牌
		String company = (String) map.get("company");  //厂家
		String officeDepository = (String) map.get("officeDepository"); //办公用品库
		String officeProtype = (String) map.get("officeProtype");  //类别
		String officePro = (String) map.get("officePro");  //办公用品id
		String transQtyStr = (String) map.get("transQty");  //申请数量
		String repTime = (String) map.get("repTime");  //维护日期
		String remark1 = (String) map.get("remark1");  //备注
		String remark2 = (String) map.get("remark2");  //维护内容
		String proIdText = (String) map.get("proIdText");  //模糊查询的办公用品id
		int transQty = 0;
		int proId = 0;
		if (T9Utility.isNumber(transQtyStr)) {
			transQty = Integer.parseInt(transQtyStr);
		}
		if (T9Utility.isNumber(proIdText)) {
			proId = Integer.parseInt(proIdText);
		}else if (T9Utility.isNumber(officePro)) {
			proId = Integer.parseInt(officePro);
		}
		
		T9ORM orm = new T9ORM();
		try {
			
			double proPrice = 0;  //原用品库存单价
			int proStock = 0;  //原用品库存数量
			String manager = "";  //库管理员
			String proKeeper = "";
			String proUnit = "";  //计量单位
			String proName = "";  //用品名
			if (proId!=0) {
				Map<Object, Object> proMap = this.getOfficeProById(dbConn, person, proId);
				proPrice = (Double) proMap.get("proPrice");
				proStock = (Integer) proMap.get("proStock");  
				manager = (String) proMap.get("manager");
				proKeeper = (String) proMap.get("proKeeper");
				proUnit = (String) proMap.get("proUnit");
				proName = (String) proMap.get("proName");
			}
			
			double newPrice = 0.0;  //新价格
			int factQty = 0;  //实际数量 ,发送短信用
//			int newTransQty = 0;
			if (!T9Utility.isNullorEmpty(price) && "0".equals(transFlag)) {
				newPrice = Double.parseDouble(price);
			} else {
				newPrice = proPrice;
			}
			if ("1".equals(transFlag) || "2".equals(transFlag) || "4".equals(transFlag)) {
				factQty = transQty;
				transQty = transQty * (-1);
			}
			String remark = "";
			Date transDate = new Date();
			if ("5".equals(transFlag)) {
				remark = remark2;
				if (!T9Utility.isNullorEmpty(repTime)) {
					transDate = T9Utility.parseDate(repTime);
				}
			} else {
				remark = remark1;
			}
			
			int deptId = 0;
			if (T9Utility.isNumber(borrower)) {
				T9Person user = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, Integer.parseInt(borrower));
				deptId = user.getDeptId();
			}
			T9OfficeTranshistory officeTranshistory = (T9OfficeTranshistory) orm.loadObjSingle(dbConn, T9OfficeTranshistory.class, transId);
			if (officeTranshistory!=null) {
				int modStock = officeTranshistory.getTransQty()*(-1); 
				officeTranshistory.setProId(proId);
				officeTranshistory.setBorrower(T9Utility.null2Empty(borrower));
				officeTranshistory.setTransFlag(transFlag);
				officeTranshistory.setTransQty(transQty);
				officeTranshistory.setFactQty(factQty);
				officeTranshistory.setPrice(newPrice);
				officeTranshistory.setCompany(company);
				officeTranshistory.setBand(band);
				officeTranshistory.setRemark(remark);
				officeTranshistory.setTransDate(transDate);
				officeTranshistory.setOperator(String.valueOf(person.getSeqId()));
				officeTranshistory.setDeptId(deptId);
				orm.updateSingle(dbConn, officeTranshistory);
				if (!"5".equals(transFlag)) {
					double oldSum = proStock * proPrice;  // OLD_SUM = 原办公用品库存数量 * 原办公用品库存单价 
					
//					int newProStock = proStock + transQty + modStock;
					int newProStock = proStock + transQty + modStock;  // 新的 库存数 = 原办公用品库存数量 + 申请数（有负号）+ 该用品之前的申请数的绝对值（没负号）
//					double newPriced = 1;
//					if (T9Utility.isNumber(price)) {
//						newPriced = Double.parseDouble(price);
//					}
					
					T9OfficeProducts products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, proId);
					if ("0".equals(transFlag)) {
						double newSum = Double.parseDouble(price) * transQty;  //NEW_SUM = 输入的价格 + 原办公用品库存数量
						double newProPrice = (oldSum + newSum) / newProStock;
						newProPrice = Double.parseDouble(T9Utility.getFormatedStr(newProPrice, 1));    // Math.round(newProPrice);
						
						if (products != null) {
							products.setProStock(newProStock);
							products.setProPrice(proPrice);
							orm.updateSingle(dbConn, products);
						}
					} else {
						if (products != null) {
							products.setProStock(newProStock);
							orm.updateSingle(dbConn, products);
						}
					}
					
					if (products != null) {// 短信提醒
						
					}
				}
				
				
			}
			
			
		} catch (Exception e) {
			throw e;
		}
		
	}

	public Map<Object, Object> getOfficeProById(Connection dbConn, T9Person person, int proId) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();

		PreparedStatement stmt = null;
		ResultSet rs = null;
		String conditionStr = "";
		if (!person.isAdminRole()) {
			conditionStr = " (" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT")
					+ " or( PRO_MANAGER like '' and PRO_DEPT like '' ) or PRO_DEPT like 'ALL_DEPT')";
		} else {
			conditionStr = " 1=1";
		}
		String sql = "select PRO_STOCK,PRO_PRICE,PRO_UNIT,PRO_NAME,PRO_KEEPER,MANAGER from OFFICE_PRODUCTS a left outer join OFFICE_TYPE b on a.OFFICE_PROTYPE=b.SEQ_ID left outer join OFFICE_DEPOSITORY c on b.TYPE_DEPOSITORY=c.SEQ_ID where a.SEQ_ID="
				+ proId + " and" + conditionStr;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int proStock = 0;
			double proPrice = 0;
			String manager = "";
			String proKeeper = "";
			String proUnit = "";
			String proName = "";
			if (rs.next()) {
				proStock = rs.getInt("PRO_STOCK");
				proPrice = rs.getDouble("PRO_PRICE");
				manager = rs.getString("MANAGER");
				proKeeper = rs.getString("PRO_KEEPER");
				proUnit = rs.getString("PRO_UNIT");
				proName = rs.getString("PRO_NAME");
			}

			map.put("proStock", proStock);
			map.put("proPrice", proPrice);
			map.put("manager", manager);
			map.put("proKeeper", proKeeper);
			map.put("proUnit", proUnit);
			map.put("proName", proName);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return map;
	}

	/**
	 * 今日操作查看
	 * 
	 * @param dbConn
	 * @param today
	 * @return
	 * @throws Exception
	 */
	public String getProductsByTodayLogic(Connection dbConn, T9Person person, String today) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		PreparedStatement stmt = null;
		ResultSet rs = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		String conditionStr = "";
		String begin = T9DBUtility.getDateFilter("TRANS_DATE", today, ">=");
		String end = T9DBUtility.getDateFilter("TRANS_DATE", today + " 23:59:59", "<=");

		String sql = "SELECT SEQ_ID,PRO_ID,BORROWER,TRANS_FLAG,TRANS_QTY,PRICE,TRANS_DATE,OPERATOR ,COMPANY,BAND from OFFICE_TRANSHISTORY where TRANS_FLAG != 6 and OPERATOR='"
				+ person.getSeqId()
				+ "' and "
				+ begin
				+ " and "
				+ end
				+ " and (TRANS_STATE='' OR TRANS_STATE is null or TRANS_STATE='1') order by SEQ_ID desc";
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			boolean isHave = false;
			while (rs.next()) {
				int transId = rs.getInt("SEQ_ID");
				int proId = rs.getInt("PRO_ID");
				String borrower = T9Utility.null2Empty(rs.getString("BORROWER"));
				String transFlag = T9Utility.null2Empty(rs.getString("TRANS_FLAG"));
				int transQty = rs.getInt("TRANS_QTY");
				double price = rs.getDouble("PRICE");
				String transDate = T9Utility.getDateTimeStr(rs.getDate("TRANS_DATE"));
				String operator = T9Utility.null2Empty(rs.getString("OPERATOR"));
				String company = T9Utility.null2Empty(rs.getString("COMPANY"));
				String band = T9Utility.null2Empty(rs.getString("BAND"));

				Map<Object, Object> proMap = this.getProductsById(dbConn, person, proId);
				String proName = (String) proMap.get("proName");
				int proStock = (Integer) proMap.get("proStock");
				double proPrice = (Double) proMap.get("proPrice");
				String transName = "";
				String borrowerName = "";
				if ("0".equals(transFlag)) {
					transName = "采购入库";
					borrowerName = "";
				}
				if ("1".equals(transFlag)) {
					transName = "领用";
					transQty = transQty * (-1);
					price = proPrice;
					borrowerName = this.getUserNameLogic(dbConn, borrower);
				}
				if ("2".equals(transFlag)) {
					transName = "借用";
					transQty = transQty * (-1);
					price = proPrice;
					borrowerName = this.getUserNameLogic(dbConn, borrower);
				}
				if ("3".equals(transFlag)) {
					transName = "归还";
					transQty = transQty * (-1);
					price = proPrice;
					borrowerName = this.getUserNameLogic(dbConn, borrower);
				}
				if ("4".equals(transFlag)) {
					transName = "报废";
					price = proPrice;
					borrowerName = "";
				}
				if ("5".equals(transFlag)) {
					transName = "维护";
					borrowerName = "";
				}
				buffer.append("{");
				buffer.append("transId:\"" + transId + "\"");
				buffer.append(",proName:\"" + T9Utility.encodeSpecial(proName) + "\"");
				buffer.append(",transName:\"" + T9Utility.encodeSpecial(transName) + "\"");
				buffer.append(",borrowerName:\"" + T9Utility.encodeSpecial(borrowerName) + "\"");
				if ("5".equals(transFlag)) {
					buffer.append(",transQty:\"" + "-" + "\"");
				} else {
					buffer.append(",transQty:\"" + transQty + "\"");
				}
				buffer.append(",proStock:\"" + proStock + "\"");
				buffer.append(",price:\"" + price + "\"");
				buffer.append(",transDate:\"" + transDate + "\"");
				buffer.append(",operatorName:\"" + T9Utility.encodeSpecial(person.getUserName()) + "\"");
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

	public Map<Object, Object> getProductsById(Connection dbConn, T9Person person, int proId) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String conditionStr = "";
		if (!person.isAdminRole()) {
			conditionStr = " (" + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT")
					+ " or( PRO_MANAGER like '' and PRO_DEPT like '' ) or PRO_DEPT like 'ALL_DEPT' or PRO_DEPT like '0')";
		} else {
			conditionStr = " 1=1";
		}
		String sql = "select PRO_STOCK,PRO_PRICE,PRO_NAME from OFFICE_PRODUCTS  where SEQ_ID=" + proId + " and" + conditionStr;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int proStock = 0;
			double proPrice = 0;
			String proName = "";
			if (rs.next()) {
				proName = rs.getString("PRO_NAME");
				proStock = rs.getInt("PRO_STOCK");
				proPrice = rs.getDouble("PRO_PRICE");
			}

			map.put("proStock", proStock);
			map.put("proPrice", proPrice);
			map.put("proName", proName);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return map;
	}

	/**
	 * 办公用品管理明细
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @param deptId
	 * @return
	 * @throws Exception
	 */
	public String getTransInfoListJsonLogic(Connection dbConn, Map request, T9Person person, int personId) throws Exception {
		try {
			String sql = "select " 
				+ " a.SEQ_ID" 
				+ ", b.PRO_NAME" 
				+ ", a.TRANS_FLAG" 
				+ ", a.BORROWER" 
				+ ", a.TRANS_QTY" 
				+ ", a.PRICE" 
				+ ", a.TRANS_DATE"
				+ ", a.REMARK"
				
					+ ", b.PRO_UNIT" + ", b.PRO_PRICE as PRO_PRICE" + " from OFFICE_TRANSHISTORY a"
					+ " LEFT OUTER JOIN OFFICE_PRODUCTS b ON a.PRO_ID = b.SEQ_ID " + " LEFT OUTER JOIN PERSON  c ON a.OPERATOR = c.SEQ_ID "
					+ " where a.BORROWER='" + personId + "'";

			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 下载CSV模板
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList<T9DbRecord> downCSVTempletLogic() throws Exception {
		ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
		T9DbRecord record = new T9DbRecord();
		try {
			record.addField("办公用品名称", "");
			record.addField("所属库", "");
			record.addField("登记类型", "");
			record.addField("申请人", "");
			record.addField("数量", "");
			record.addField("单价", "");
			record.addField("操作日期", "");
			record.addField("备注", "");
			result.add(record);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 导入办公用品登记数据
	 * 2011-3-31
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	public Map<Object, Object> impTransInfoToCsvLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, StringBuffer buffer)
			throws Exception {
		T9ORM orm = new T9ORM();
		Map<Object, Object> returnMap = new HashMap<Object, Object>();
		int isCount = 0;
		int updateCount = 0;
		try {
			Map<Object, Object> bufferMap = new HashMap<Object, Object>();
			String infoStr = "";
			InputStream is = fileForm.getInputStream();
			ArrayList<T9DbRecord> dbRecords = T9CSVUtil.CVSReader(is, T9Const.CSV_FILE_CODE);
			
			String newTransFlag = "";
			int borrowerId = 0;
			int dbProId = 0;
			double dbProPrice = 0;
			int dbProStock = 0;
			String dbDepositoryName = "";
			
			boolean success = true;
			
			for (T9DbRecord record : dbRecords) {
				String proId = (String) record.getValueByName("办公用品名称");
				String depository = (String) record.getValueByName("所属库");
				String transFlag = (String) record.getValueByName("登记类型");
				String borrower = (String) record.getValueByName("申请人");
				String transQty = (String) record.getValueByName("数量");
				String price = (String) record.getValueByName("单价");
				String transDate = (String) record.getValueByName("操作日期");
				String remark = (String) record.getValueByName("备注");

				if (T9Utility.isNullorEmpty(proId)) {
					infoStr = "办公用品名称为空，未导入";
					bufferMap.put("proId", proId);
					bufferMap.put("transFlag", transFlag);
					bufferMap.put("borrower", borrower);
					bufferMap.put("transQty", transQty);
					bufferMap.put("price", price);
					bufferMap.put("transDate", transDate);
					bufferMap.put("remark", remark);
					bufferMap.put("infoStr", infoStr);
					bufferMap.put("color", "red");
					sbStrJson(buffer, bufferMap);
					success =false;
					continue;
				}else {
					
					Map<Object, Object> depoMap = this.getImpDepositoryInfoByName(dbConn, proId, depository);
					dbProId = (Integer)depoMap.get("dbProId");
					dbProPrice = (Double)depoMap.get("proPrice");
					dbProStock = (Integer)depoMap.get("proStock");
					dbDepositoryName = (String)depoMap.get("depositoryName");
					boolean dbDepoFlag = (Boolean)depoMap.get("depoFlag");
					if (!dbDepoFlag) {
						infoStr = "办公用品名称错误或未指定所属库，未导入";
						bufferMap.put("proId", proId);
						bufferMap.put("transFlag", transFlag);
						bufferMap.put("borrower", borrower);
						bufferMap.put("transQty", transQty);
						bufferMap.put("price", price);
						bufferMap.put("transDate", transDate);
						bufferMap.put("remark", remark);
						bufferMap.put("infoStr", infoStr);
						bufferMap.put("color", "red");
						sbStrJson(buffer, bufferMap);
						success =false;
						continue;
					}
				}
				if (T9Utility.isNullorEmpty(transFlag)) {
					infoStr = "登记类型为空，未导入";
					bufferMap.put("proId", proId);
					bufferMap.put("transFlag", transFlag);
					bufferMap.put("borrower", borrower);
					bufferMap.put("transQty", transQty);
					bufferMap.put("price", price);
					bufferMap.put("transDate", transDate);
					bufferMap.put("remark", remark);
					bufferMap.put("infoStr", infoStr);
					bufferMap.put("color", "red");
					sbStrJson(buffer, bufferMap);
					success =false;
					continue;
				}else {
					if (!"采购入库".equals(transFlag) &&!"领用".equals(transFlag) &&!"借用".equals(transFlag) &&!"归还".equals(transFlag) &&!"报废".equals(transFlag) &&!"维护".equals(transFlag)) {
						infoStr = "登记类型错误，未导入";
						bufferMap.put("proId", proId);
						bufferMap.put("transFlag", transFlag);
						bufferMap.put("borrower", borrower);
						bufferMap.put("transQty", transQty);
						bufferMap.put("price", price);
						bufferMap.put("transDate", transDate);
						bufferMap.put("remark", remark);
						bufferMap.put("infoStr", infoStr);
						bufferMap.put("color", "red");
						sbStrJson(buffer, bufferMap);
						success =false;
						continue;
					}
					
					if ("采购入库".equals(transFlag)) {
						newTransFlag = "0";
					}
					if ("领用".equals(transFlag)) {
						newTransFlag = "1";
					}
					if ("借用".equals(transFlag)) {
						newTransFlag = "2";
					}
					if ("归还".equals(transFlag)) {
						newTransFlag = "3";
					}
					if ("报废".equals(transFlag)) {
						newTransFlag = "4";
					}
					if ("维护".equals(transFlag)) {
						newTransFlag = "5";
					}
				}
				
				if (T9Utility.isNullorEmpty(transQty) && !"5".equals(transFlag)) {
					infoStr = "数量为空，未导入";
					bufferMap.put("proId", proId);
					bufferMap.put("transFlag", transFlag);
					bufferMap.put("borrower", borrower);
					bufferMap.put("transQty", transQty);
					bufferMap.put("price", price);
					bufferMap.put("transDate", transDate);
					bufferMap.put("remark", remark);
					bufferMap.put("infoStr", infoStr);
					bufferMap.put("color", "red");
					sbStrJson(buffer, bufferMap);
					success =false;
					continue;
				}
				
				if (success) {
					int newTransQty = 0;
					int factQty = 0;
					double newPrice = 0;
					if (T9Utility.isNumber(price)) {
						newPrice = Double.parseDouble(price);
					}
					
					if ( T9Utility.isNumber(transQty)) {
						newTransQty = Integer.parseInt(transQty);
					}
					if ("0".equals(newTransFlag) || "3".equals(newTransFlag)) {
						if (newTransQty<0) {
							newTransQty = newTransQty * (-1);
						}
					}else {
						if (newTransQty>0) {
							newTransQty = newTransQty * (-1);
						}
					}
					if (newTransQty<0) {
						factQty = newTransQty * (-1);
					}else {
						factQty = newTransQty;
					}
					
					if (T9Utility.isNullorEmpty(transDate)) {
						transDate = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
					}
					if (!T9Utility.isNullorEmpty(borrower)) {
						borrowerId = this.getImpUserSeqIdLogic(dbConn, borrower);
					}
					
					
					T9OfficeTranshistory transhistory = new T9OfficeTranshistory();
					transhistory.setProId(dbProId);
					transhistory.setTransFlag(newTransFlag);
					transhistory.setBorrower(String.valueOf(borrowerId));
					transhistory.setTransQty(newTransQty);
					transhistory.setPrice(newPrice);
					transhistory.setTransDate(T9Utility.parseDate(transDate));
					transhistory.setRemark(remark);
					transhistory.setOperator(String.valueOf(person.getSeqId()));
					transhistory.setTransState("1");
					transhistory.setFactQty(factQty);
					orm.saveSingle(dbConn, transhistory);
					isCount++;
					
					int newProStock = dbProStock + newTransQty; //最新总数
					T9OfficeProducts products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, dbProId);
					if ("0".equals(transFlag) && T9Utility.isNumber(price)) {
						dbProPrice = (dbProStock * dbProPrice + newTransQty*Double.parseDouble(price))/ (dbProStock + newTransQty);
						dbProPrice = Math.round(dbProPrice);
						
						products.setProStock(newProStock);
						products.setProPrice(dbProPrice);
						orm.updateSingle(dbConn, products);
					}else {
						products.setProStock(newProStock);
						orm.updateSingle(dbConn, products);
					}
				}

			}
			returnMap.put("isCount", isCount);
			returnMap.put("updateCount", updateCount);
			return returnMap;
		} catch (Exception e) {
			throw e;
		}
	}

	public String sbStrJson(StringBuffer sb, Map<Object, Object> map) throws Exception {
		String proId = (String) map.get("proId");
		String transFlag = (String) map.get("transFlag");
		String borrower = (String) map.get("borrower");
		String transQty = (String) map.get("transQty");
		String price = (String) map.get("price");
		String transDate = (String) map.get("transDate");
		String remark = (String) map.get("remark");
		String infoStr = (String) map.get("infoStr");
		String color = (String) map.get("color");
		try {
			sb.append("{");
			sb.append("proName:\"" + T9Utility.null2Empty(proId) + "\""); 
			sb.append(",transFlag:\"" + T9Utility.null2Empty(transFlag) + "\"");
			sb.append(",borrower:\"" + T9Utility.null2Empty(borrower) + "\"");
			sb.append(",transQty:\"" + T9Utility.null2Empty(transQty) + "\"");
			sb.append(",price:\"" + T9Utility.null2Empty(price) + "\"");
			sb.append(",transDate:\"" + T9Utility.null2Empty(transDate) + "\"");
			sb.append(",remark:\"" + T9Utility.null2Empty(remark) + "\"");
			sb.append(",infoStr:\"" + T9Utility.null2Empty(infoStr) + "\"");
			sb.append(",color:\"" + T9Utility.null2Empty(color) + "\"");
			sb.append("},");
			return sb.toString();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Map<Object, Object> getImpDepositoryInfoByName(Connection dbConn,String proName, String deposName) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if ("默认库".equals(deposName)) {
			deposName = "0";
		}
		String sql = "select a.SEQ_ID as PRO_ID,PRO_PRICE,PRO_STOCK,DEPOSITORY_NAME from OFFICE_PRODUCTS a left outer join OFFICE_TYPE b on a.OFFICE_PROTYPE=b.SEQ_ID left outer join OFFICE_DEPOSITORY c on b.TYPE_DEPOSITORY=c.SEQ_ID  where a.PRO_NAME=? and c.DEPOSITORY_NAME =?";
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, proName);
			stmt.setString(2, deposName);
			rs = stmt.executeQuery();
			int dbProId = 0;
			double proPrice = 0;
			int proStock = 0;
			String depositoryName = "";
			boolean depoFlag = false;
			if (rs.next()) {
				dbProId = rs.getInt("PRO_ID");
				proPrice = rs.getDouble("PRO_PRICE");
				proStock = rs.getInt("PRO_STOCK");
				depositoryName = T9Utility.null2Empty(rs.getString("DEPOSITORY_NAME"));
				depoFlag = true;
			}
			map.put("dbProId", dbProId);
			map.put("proPrice", proPrice);
			map.put("proStock", proStock);
			map.put("depositoryName", depositoryName);
			map.put("depoFlag", depoFlag);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return map;
	}
	
	public int getImpUserSeqIdLogic(Connection dbConn,String userName) throws Exception{
		int userId = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = " select SEQ_ID from PERSON where USER_NAME = ?";
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, userName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				userId = rs.getInt("SEQ_ID");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
		return userId;
	}
	
	
	
	/**
	 * 办公用品登记查询 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String getTransDetailListLogic(Connection dbConn, Map request, T9Person person,Map<Object, Object> map) throws Exception {
		String transFlag = (String) map.get("transFlag");
		String dept = (String) map.get("dept");
		String user = (String) map.get("user");
		String officeDepository = (String) map.get("officeDepository");
		String officeProtype = (String) map.get("officeProtype");
		String officePro = (String) map.get("officePro");
		String proName = (String) map.get("proName");
		String beginDate = (String) map.get("beginDate");
		String endDate = (String) map.get("endDate");
		
		try {
			String conditionStr = "";
			if (!T9Utility.isNullorEmpty(transFlag) && !"-1".equals(transFlag)) {
				conditionStr += " and a.TRANS_FLAG='" + T9DBUtility.escapeLike(transFlag) + "'";
			}
			if (!T9Utility.isNullorEmpty(officePro) && !"-1".equals(officePro)) {
				conditionStr += " and a.PRO_ID='" + T9DBUtility.escapeLike(officePro) + "'";
			}
			if (!T9Utility.isNullorEmpty(officeDepository)) {
				conditionStr += " and b.OFFICE_PROTYPE in (" + T9DBUtility.escapeLike(officeDepository) + ")";
			}
			if (!T9Utility.isNullorEmpty(officeProtype.trim()) && !"-1".equals(officeProtype)) {
				conditionStr += " and b.OFFICE_PROTYPE='" + T9DBUtility.escapeLike(officeProtype) + "'";
			}
			if (!T9Utility.isNullorEmpty(proName.trim())) {
				conditionStr += " and b.PRO_NAME like '%" + T9DBUtility.escapeLike(proName) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(beginDate)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("a.TRANS_DATE", beginDate, ">=");
			}
			if (!T9Utility.isNullorEmpty(endDate)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("a.TRANS_DATE", endDate+ " 23:59:59", "<=");
			}
			
			if (!T9Utility.isNullorEmpty(user.trim())) {
				conditionStr += " and " + T9DBUtility.findInSet(user,"a.BORROWER") ;
			}
			if (!T9Utility.isNullorEmpty(dept.trim()) && !"0".equals(dept)&& !"ALL_DEPT".equals(dept)) {
				conditionStr += " and " + T9DBUtility.findInSet(dept,"a.DEPT_ID") ;
			}
			
			String postPriv = T9Utility.null2Empty(person.getPostPriv());
			if (!"1".equals(postPriv)) {
				String isPrivDeptIdStr = this.getDeptPrivStr(dbConn, person, dept);
				conditionStr += " and c.DEPT_ID in (" + T9DBUtility.escapeLike(isPrivDeptIdStr) + ")";
			}
			conditionStr += " and (a.TRANS_STATE='' or a.TRANS_STATE is null or a.TRANS_STATE='1')";
			
			String sql = "";
			sql = "select " 
				+ " a.SEQ_ID" 
				+ ", b.PRO_NAME" 
				+ ", a.TRANS_FLAG" 
				+ ", a.BORROWER" 
				+ ", a.TRANS_QTY" 
				+ ", a.PRICE"
				+ ", a.TRANS_DATE"
				+ ", a.OPERATOR"
				+ ", a.REMARK"
				
				+ ", b.PRO_UNIT" 				
				+ ", a.PRO_ID"
				+ ", b.PRO_PRICE"
				+ ", a.COMPANY"
				+ ", a.BAND"
				+ " from  OFFICE_TRANSHISTORY a "
				+	" LEFT OUTER JOIN OFFICE_PRODUCTS b ON a.PRO_ID = b.SEQ_ID " 
				+ " LEFT OUTER JOIN PERSON  c ON a.OPERATOR = c.SEQ_ID  where TRANS_FLAG != 6 " + conditionStr + " order by a.TRANS_FLAG,a.TRANS_DATE DESC";
			
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getDeptPrivStr(Connection dbConn,T9Person person,String deptStr) throws Exception{
		String str = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String  sql = "select SEQ_ID from DEPARTMENT";
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()){
				int deptId = rs.getInt("SEQ_ID");
				T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, person , null,2);
				boolean isPriv = T9PrivUtil.isDeptPriv(dbConn, deptId, mp, person);
				if (isPriv) {
					str += deptId + ","; 
				}
				
			}
			String tmpStr = "";
			if (!T9Utility.isNullorEmpty(str)) {
				tmpStr = ",";
			}
			if (!T9Utility.isNullorEmpty(deptStr)) {
				
				if (deptStr.endsWith(",")) {
					str = deptStr+str;
				}else {
					str = deptStr + tmpStr +str;
				}
			}
			if (str.endsWith(",")) {
				str = str.substring(0, str.length()-1);
			}
			if (T9Utility.isNullorEmpty(str.trim())) {
				str = "-1";
			}
			return str;
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
	}
	
	/**
	 * 导出信息
	 * @param dbConn
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public ArrayList<T9DbRecord> exportToCSVLogic(Connection dbConn,Map<Object, Object> map,T9Person person ) throws Exception {
		ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
		
		String transFlag = (String) map.get("transFlag");
		String dept = (String) map.get("dept");
		String user = (String) map.get("user");
		String officeDepository = (String) map.get("officeDepository");
		String officeProtype = (String) map.get("officeProtype");
		String officePro = (String) map.get("officePro");
		String proName = (String) map.get("proName");
		String beginDate = (String) map.get("beginDate");
		String endDate = (String) map.get("endDate");
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String conditionStr = "";
			if (!T9Utility.isNullorEmpty(transFlag) && !"-1".equals(transFlag)) {
				conditionStr += " and a.TRANS_FLAG='" + T9DBUtility.escapeLike(transFlag) + "'";
			}
			if (!T9Utility.isNullorEmpty(officePro) && !"-1".equals(officePro)) {
				conditionStr += " and a.PRO_ID='" + T9DBUtility.escapeLike(officePro) + "'";
			}
			if (!T9Utility.isNullorEmpty(officeDepository)) {
				conditionStr += " and b.OFFICE_PROTYPE in (" + T9DBUtility.escapeLike(officeDepository) + ")";
			}
			if (!T9Utility.isNullorEmpty(officeProtype.trim()) && !"-1".equals(officeProtype)) {
				conditionStr += " and b.OFFICE_PROTYPE='" + T9DBUtility.escapeLike(officeProtype) + "'";
			}
			if (!T9Utility.isNullorEmpty(proName.trim())) {
				conditionStr += " and b.PRO_NAME like '%" + T9DBUtility.escapeLike(proName) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(beginDate)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("a.TRANS_DATE", beginDate, ">=");
			}
			if (!T9Utility.isNullorEmpty(endDate)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("a.TRANS_DATE", endDate, "<=");
			}
			
			if (!T9Utility.isNullorEmpty(user.trim())) {
				conditionStr += " and " + T9DBUtility.findInSet(user,"a.BORROWER") ;
			}
			if (!T9Utility.isNullorEmpty(dept.trim()) && !"0".equals(dept)&& !"ALL_DEPT".equals(dept)) {
				conditionStr += " and " + T9DBUtility.findInSet(dept,"a.DEPT_ID") ;
			}
			
			String postPriv = T9Utility.null2Empty(person.getPostPriv());
			if (!"1".equals(postPriv)) {
				String isPrivDeptIdStr = this.getDeptPrivStr(dbConn, person, dept);
				conditionStr += " and c.DEPT_ID in (" + T9DBUtility.escapeLike(isPrivDeptIdStr) + ")";
			}
			conditionStr += "and (TRANS_STATE='' or TRANS_STATE is null or TRANS_STATE='1')";
			
			sql = "select " 
				+ " a.SEQ_ID as transId" 
				+ ", b.PRO_NAME as PRO_NAME" 
				+ ", a.TRANS_FLAG as TRANS_FLAG" 
				+ ", a.BORROWER as BORROWER" 
				+ ", a.TRANS_QTY as TRANS_QTY" 
				+ ", a.PRICE as PRICE"
				+ ", a.TRANS_DATE as TRANS_DATE"
				+ ", a.OPERATOR as OPERATOR"
				+ ", a.REMARK as REMARK"
				
				+ ", b.PRO_UNIT as PRO_UNIT" 				
				+ ", a.PRO_ID as PRO_ID"
				+ ", b.PRO_PRICE as PRO_PRICE"
				+ ", a.COMPANY as COMPANY"
				+ ", a.BAND as BAND"
				+ " from  OFFICE_TRANSHISTORY a "
				+	" LEFT OUTER JOIN OFFICE_PRODUCTS b ON a.PRO_ID = b.SEQ_ID " 
				+ " LEFT OUTER JOIN PERSON  c ON a.OPERATOR = c.SEQ_ID  where 1=1 " + conditionStr + " order by a.TRANS_FLAG,a.TRANS_DATE DESC";
			
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			boolean isHaveFlag = false;
			while(rs.next()){
				T9DbRecord record = new T9DbRecord();
				int transId = rs.getInt("transId");
				String proNameDb = T9Utility.null2Empty(rs.getString("PRO_NAME"));
				String transFlagDb = T9Utility.null2Empty(rs.getString("TRANS_FLAG"));
				
				String borrower = T9Utility.null2Empty(rs.getString("BORROWER"));
				int transQty = rs.getInt("TRANS_QTY");
				double price = rs.getDouble("PRICE");				
				Date transDateDb = rs.getDate("TRANS_DATE");
				String operator = T9Utility.null2Empty(rs.getString("OPERATOR"));
				
				String remark = T9Utility.null2Empty(rs.getString("REMARK"));
				String proUnit = T9Utility.null2Empty(rs.getString("PRO_UNIT"));
				int proId = rs.getInt("PRO_ID");
				double proPrice = rs.getDouble("PRO_PRICE");
				String company = T9Utility.null2Empty(rs.getString("COMPANY"));
				String companyDb = T9Utility.null2Empty(rs.getString("COMPANY"));
				
				
				String operatorName = "";
				String transName = "";
				String borrowerName = "";
				
				String transDate = "";
				if (transDateDb!=null) {
					transDate = new SimpleDateFormat("yyyy-MM-dd").format(transDateDb);
				}
				if (!String.valueOf(person.getSeqId()).equals(operator)) {
					operatorName = this.getUserNameLogic(dbConn, operator);
				}else {
					operatorName = person.getUserName();
				}
				
				if ("0".equals(transFlagDb)) {
					transName = "采购入库";
					borrowerName= "";
				}
				if ("1".equals(transFlagDb)) {
					transName = "领用";
					transQty = transQty *(-1);
					borrowerName = this.getUserNameLogic(dbConn, borrower);
				}
				if ("2".equals(transFlagDb)) {
					transName = "借用";
					transQty = transQty *(-1);
					borrowerName = this.getUserNameLogic(dbConn, borrower);
				}
				if ("3".equals(transFlagDb)) {
					transName = "归还";
					borrowerName = this.getUserNameLogic(dbConn, borrower);
				}
				if ("4".equals(transFlagDb)) {
					transName = "报废";
					borrowerName = "";
					transQty = transQty *(-1);
				}
				
				record.addField("办公用品名称", proNameDb);
				record.addField("登记类型", transName);
				record.addField("申请人", borrowerName);
				record.addField("数量", transQty);
				record.addField("计量单位", proUnit);
				record.addField("单价", price);
				record.addField("操作日期", transDate);
				record.addField("操作员", operatorName);
				record.addField("备注", remark);
				result.add(record);
				isHaveFlag = true;
			}
			
			if (!isHaveFlag) {
				T9DbRecord record = new T9DbRecord();
				record.addField("办公用品名称", "");
				record.addField("登记类型", "");
				record.addField("申请人", "");
				record.addField("数量", "");
				record.addField("计量单位", "");
				record.addField("单价", "");
				record.addField("操作日期", "");
				record.addField("操作员", "");
				record.addField("备注", "");
				result.add(record);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
		return result;
	}
	
	
	/**
	 * 放弃操作 
	 * @param dbConn
	 * @param person
	 * @param transIdStr
	 * @throws Exception 
	 */
	public void deleteTransInfoLogic(Connection dbConn,T9Person person,String transIdStr) throws Exception{
		int transId = 0;
		if (T9Utility.isNumber(transIdStr)) {
			transId = Integer.parseInt(transIdStr);
		}
		T9ORM orm = new T9ORM(); 
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			T9OfficeTranshistory transhistory = (T9OfficeTranshistory) orm.loadObjSingle(dbConn, T9OfficeTranshistory.class, transId);
			if (transhistory!=null) {
				int proId = transhistory.getProId();
				int modStock = transhistory.getTransQty()*(-1);
				String transFlag = T9Utility.null2Empty(transhistory.getTransFlag());
				double price = transhistory.getPrice();
				String conditionStr = "";
				if (!person.isAdminRole()) {
					conditionStr = T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "PRO_MANAGER") + " or " + T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "PRO_DEPT");
				}else {
					conditionStr = " 1=1";
				}
				String sql = "select PRO_STOCK,PRO_PRICE from OFFICE_PRODUCTS where SEQ_ID=? and " + conditionStr ;
				stmt = dbConn.prepareStatement(sql);
				stmt.setInt(1, proId);
				rs = stmt.executeQuery();
				if (rs.next()) {
					int proStock = rs.getInt("PRO_STOCK");
					double proPrice = rs.getDouble("PRO_PRICE");
					double oldSum = proStock * proPrice;
						
					int newProStock = proStock + modStock;
					
					T9OfficeProducts  products = (T9OfficeProducts) orm.loadObjSingle(dbConn, T9OfficeProducts.class, proId);
					if (products!=null) {
						products.setProStock(newProStock);
						orm.updateSingle(dbConn, products);
						if ("0".equals(transFlag)) {
							int newModStock = modStock*(-1);
							double newProPrice = (oldSum-newModStock* price)/newProStock;
//							String newProPriceStr = T9Utility.getFormatedStr(newProPrice, 1);
//							newProPrice = Double.parseDouble(newProPriceStr);
							newProPrice = Double.parseDouble(T9Utility.getFormatedStr(newProPrice, 1));
							
							if (newProPrice<0) {
								newProPrice = 0;
							}
							products.setProPrice(newProPrice);
							orm.updateSingle(dbConn, products);
						}
						orm.deleteSingle(dbConn, transhistory);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
	}
	
	
	public void deleteOfficeTranshistoryLogic(Connection dbConn, String SeqIdStr) throws Exception {
		if (T9Utility.isNullorEmpty(SeqIdStr)) {
			SeqIdStr = "0";
		}
		if (SeqIdStr.endsWith(",")) {
			SeqIdStr = SeqIdStr.substring(0, SeqIdStr.length() - 1);
		}
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "delete from OFFICE_TRANSHISTORY where seq_id in(" + SeqIdStr + ")";
		try {
			stmt = dbConn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}
	
	/**
	 * 离职人员/外部人员
	 * 2011-4-1
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	
	public String getNotRecordDeptListLogic(Connection dbConn,T9Person person) throws Exception{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT PERSON.USER_ID as userId," +
				" PERSON.SEQ_ID as personSeqId," +
				" USER_NAME," +
				" PRIV_NAME " +
				" from USER_PRIV,PERSON " +
				" where PERSON.DEPT_ID=0 and PERSON.USER_PRIV=USER_PRIV.SEQ_ID ";
		try {
			int privNo = this.getPrivNo(dbConn, person); 
			String conditionStr = "";
			if (!person.isAdminRole()) {
				conditionStr  = " and USER_PRIV.PRIV_NO>" + privNo  + " and USER_PRIV.SEQ_ID!=1";
			}
			String orderByStr = " order by PRIV_NO,USER_NO,USER_NAME";
			sql = sql+ conditionStr +orderByStr;  
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			StringBuffer buffer = new StringBuffer();
			boolean isHave = false;
			buffer.append("[");
			while(rs.next()){
				String dbUserId = T9Utility.null2Empty(rs.getString("userId"));
				String dbUserName = T9Utility.null2Empty(rs.getString("USER_NAME"));
				String dbPrivName = T9Utility.null2Empty(rs.getString("PRIV_NAME"));
				String dbPersonSeqId = T9Utility.null2Empty(rs.getString("personSeqId"));
				
				buffer.append("{");
				buffer.append("userId:\"" + T9Utility.encodeSpecial(dbUserId) + "\"");
				buffer.append(",userName:\"" + T9Utility.encodeSpecial(dbUserName) + "\"");
				buffer.append(",privName:\"" +  T9Utility.encodeSpecial(dbPrivName) + "\"");
				buffer.append(",personId:\"" +  T9Utility.encodeSpecial(dbPersonSeqId) + "\"");
				buffer.append("},");
				isHave = true;
			}
			if (isHave) {
				buffer.deleteCharAt(buffer.length()-1);
			}
			buffer.append("]");
			return buffer.toString();
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
	}
	
	/**
	 * 获取角色编号
	 * 2011-4-2
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public int  getPrivNo(Connection dbConn,T9Person person) throws Exception{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT PRIV_NO from USER_PRIV where SEQ_ID=?";
		int priv = 0;
		int privNo = 0;
		if (T9Utility.isNumber(person.getUserPriv())) {
			priv = Integer.parseInt(person.getUserPriv());
		}
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, priv);
			rs = stmt.executeQuery();
			if (rs.next()) {
				privNo = rs.getInt("PRIV_NO");
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
		return privNo;
		
	}
	
	
	/**
	 * 获取模块权限
	 * 2011-4-2
	 * @param dbConn
	 * @param moduleId
	 * @param loginUserId
	 * @return
	 * @throws Exception
	 */
	public T9ModulePriv getModulePriv(Connection dbConn,int moduleId,int loginUserId) throws Exception{
		T9ModulePriv modulePriv = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select SEQ_ID,MODULE_ID,DEPT_PRIV,ROLE_PRIV,DEPT_ID,PRIV_ID,USER_ID,USER_SEQ_ID from MODULE_PRIV WHERE MODULE_ID =? AND USER_SEQ_ID=?";
		
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setInt(1, moduleId);
			stmt.setInt(2, loginUserId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				modulePriv = new T9ModulePriv();
				modulePriv.setSeqId(rs.getInt("SEQ_ID"));
				modulePriv.setModuleId(rs.getInt("MODULE_ID"));
				modulePriv.setDeptPriv(rs.getString("DEPT_PRIV"));
				modulePriv.setRolePriv(rs.getString("ROLE_PRIV"));
				modulePriv.setDeptId(rs.getString("DEPT_ID"));
				modulePriv.setPrivId(rs.getString("PRIV_ID"));
				modulePriv.setUserId(rs.getString("USER_ID"));
				modulePriv.setUserSeqId(rs.getInt("USER_SEQ_ID"));
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
		return modulePriv;
	}
	
  /**
   * 短信提醒(带时间)
   * 
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @param sendDate
   * @throws Exception
   */
  public void doSmsBackTime(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
      throws Exception {
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    sb.setSendDate(sendDate);
    T9SmsUtil.smsBack(conn, sb);
  }
	

}
