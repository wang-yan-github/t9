package t9.rad.docs.menu;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mx4j.log.Log;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9MenuAct {
  /**
   * log                                               
   */
  private Log logc = null;
  public String getData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
       StringBuffer sb = new StringBuffer();
       sb.append("[{name:'新增',action:add,icon:imgPath + '/cmp/rightmenu/addStep.gif' , extData:\"menu1\"}"
                      + ",{name:'修改',action:update,icon:imgPath + '/cmp/rightmenu/addStep.gif', extData:\"menu1\"}"
                      + ",{name:'删除',action:del,icon:imgPath + '/cmp/rightmenu/addStep.gif', extData:\"menu1\"}]");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
