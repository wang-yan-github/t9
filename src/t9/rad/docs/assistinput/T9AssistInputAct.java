package t9.rad.docs.assistinput;

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

public class T9AssistInputAct {
  /**
   * 
   * log                                               
   */
  private Log log = null;
  private String[] ss = {"测试","测试222","测试2sss","bbbb","测试22bbbb","测试2ddddd22","rrreeee","测试rrrrr","eee333","eeeeee","测试bbbbbbb"};
  public String getData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      StringBuffer sb = new StringBuffer();
      String w = request.getParameter("w");
      
      sb.append(",lis:[");
      int count = 0;
      for (String s : ss) {
        if (s.contains(w)) {
          sb.append("{string:'"+s+"'},");
          count++;
        }
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      sb.insert(0, "{count:" + count);
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
