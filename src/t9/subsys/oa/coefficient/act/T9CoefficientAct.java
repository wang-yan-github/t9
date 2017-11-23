package t9.subsys.oa.coefficient.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.coefficient.data.T9Coefficient;
import t9.subsys.oa.coefficient.logic.T9CoefficientLogic;

public class T9CoefficientAct {
	private T9CoefficientLogic logic = new T9CoefficientLogic();

	public String addCoefficient(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			Map<String, String[]> map = request.getParameterMap();
			T9Coefficient coefficient = (T9Coefficient) T9FOM.build(map, T9Coefficient.class, "");
			int count = this.logic.isHaveScoreLogic(dbConn);
			if (count == 0) {
				this.logic.addCoefficientLogic(dbConn, coefficient);
			} else {
				int seqId = this.logic.getSeqIdLogic(dbConn);
				T9Coefficient coefficient2 = this.logic.getCoefficientLogic(dbConn, seqId);
				if (coefficient2 != null) {
					coefficient2.setYearScore(coefficient.getYearScore());
					coefficient2.setMonthScore(coefficient.getMonthScore());
					coefficient2.setChiefScore(coefficient.getChiefScore());
					coefficient2.setCheckScore(coefficient.getCheckScore());
					coefficient2.setAwardScore(coefficient.getAwardScore());
					this.logic.updateCoefficientLogic(dbConn, coefficient2);
				}
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 *获取系数信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getCoefficient(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			int seqId = this.logic.getSeqIdLogic(dbConn);
			T9Coefficient coefficient = this.logic.getCoefficientLogic(dbConn, seqId);
			StringBuffer data = T9FOM.toJson(coefficient);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	
	
}
