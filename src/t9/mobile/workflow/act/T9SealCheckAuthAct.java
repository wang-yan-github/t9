package t9.mobile.workflow.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.XXTEA;
import t9.mobile.workflow.logic.T9PdaTurnLogic;
import t9.pda.mobilseal.util.Base64Util;

public class T9SealCheckAuthAct {
	public String data(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      
      URLDecoder decoder = new URLDecoder();
      BASE64Decoder base64Decoder = new BASE64Decoder();
      
      
      String authData = request.getParameter("authData");
      String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
      if(!T9MobileString.isEmpty(authData)){
        /*
        byte[] bytes = Base64Util.decode(authData);
         String result = new String(bytes);
         
         byte[] ret = XXTEA.decrypt(bytes, key.getBytes());
         String datas = new String(ret);
       */
       String md5_check = "";
       md5_check = T9DigestUtility.md5Hex(authData.getBytes());
       
       
       String sql = "select 1 FROM MOBILE_DEVICE WHERE MD5_CHECK='"+md5_check+"' and DEVICE_TYPE='1'";
       int rsCount = T9MobileUtility.resultSetCount(conn, sql);
       if (rsCount > 0) {
         T9MobileUtility.output(response, "+OK");
       }
      }
       return null;
    }  catch (Exception ex) {
       throw ex;
    }
  }
	
}
