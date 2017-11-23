package t9.core.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.dto.T9CodeLoadParam;
import t9.core.dto.T9CodeLoadParamSet;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9CodeLoader;
import t9.core.logic.T9CodeSelectLogic;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9SelectDataAct {
  /**
   * log                                   
   */
  private static Logger log = Logger.getLogger("yzq.t9.core.act.T9SelectDataAct");
  
  /**
   * 加载下拉框数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CodeLoadParamSet paramSet = (T9CodeLoadParamSet)T9FOM.build(request.getParameterMap());

      T9CodeSelectLogic logic = new T9CodeSelectLogic();
      String rtSt = logic.loadSelectData(dbConn, paramSet);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, rtSt);
      //然后处理联动加载的数据
      
//      StringBuffer rtBuf = new StringBuffer("{");
//      String dsDef = request.getParameter("dsDef");
//      String[] dsDefArray = dsDef.split(";");
//      for (int i = 0; i < dsDefArray.length; i++) {
//        String[] tableDefArray = dsDefArray[i].split(".");
//        String propName = tableDefArray[0];
//        String tableName = tableDefArray[1];
//        String codeField = tableDefArray[2];
//        String nameField = tableDefArray[3];
//        String filter = null;
//        if (tableDefArray.length > 4) {
//          filter = tableDefArray[4];
//        }
//        StringBuffer jsonBuf = loadDataTable(dbConn, tableName, codeField, nameField, filter);
//        if (i > 0) {
//          rtBuf.append(",");
//        }
//        rtBuf.append(propName);
//        rtBuf.append(":");
//        rtBuf.append(jsonBuf);
//      }
//      rtBuf.append("}");
//      request.setAttribute(T9ActionKeys.RET_DATA, rtBuf.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "加载代码失败" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
