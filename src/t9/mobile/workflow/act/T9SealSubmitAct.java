package t9.mobile.workflow.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaTurnLogic;
import t9.pda.mobilseal.util.XXTEA;

public class T9SealSubmitAct {
	public String data(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      
      String flowIdStr = request.getParameter("FLOW_ID");
      String runIdStr = request.getParameter("RUN_ID");
      
      String DATA_ID = T9Utility.null2Empty(request.getParameter("sealCurItem"));
      String id = DATA_ID.replace("DATA_", "");
      
      String sealId = request.getParameter("sealId");
      String sealPassword = T9Utility.null2Empty(request.getParameter("sealPassword"));
      String sealItemCheck = request.getParameter("sealItemCheck");
      
      String data = "";
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = conn.prepareStatement("select SEAL_DATA FROM MOBILE_SEAL WHERE SEQ_ID = " + sealId);
        rs = ps.executeQuery();
        while(rs.next()){
          data = rs.getString(1);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
    BASE64Decoder decoder = new BASE64Decoder();
    String result = new String(decoder.decodeBuffer(data));
      
    Map m = T9FOM.json2Map(result);
    String sealPwd =(String) m.get("SealPwd");
    boolean checkpass = T9PassEncrypt.isValidPas(sealPassword, sealPwd);
    if (!checkpass) {
      T9MobileUtility.output(response, "印章密码错误！请重试");
      return null;
    }
      
    int runId = Integer.parseInt(runIdStr);
    int flowId = Integer.parseInt(flowIdStr);
    
    String[] sealItemChecks = sealItemCheck.split(",");
    String value = "";
    T9FlowRunUtility u =new T9FlowRunUtility();
    for (String s : sealItemChecks) {
      s= s.replace("DATA_", "");
      if (T9Utility.isInteger(s)) {
        T9FlowRunData frd =  u.getFlowRunData(conn, runId, Integer.parseInt(s), flowId);
        value += frd.getItemData() + ",";
      }
    }
    
    BASE64Encoder encoder = new BASE64Encoder();
    String crypt = encoder.encode(XXTEA.encrypt(data.getBytes(), value.getBytes()));
      
    
    String fileName = runId + "-" + id + ".data";
      T9WorkFlowUtility u1 = new T9WorkFlowUtility();
      String[] paths = u1.getNewAttachPath(fileName, "workflow");
      String dataStr = fileName + "*" + paths[0];
      T9FileUtility.storeString2File(paths[1], crypt);
      
      if (T9Utility.isInteger(id)) {
        T9WorkFlowUtility.updateFormData(conn, runId, flowId, Integer.parseInt(id), dataStr);
      }
      T9MobileUtility.output(response, "+OK");
      return null;
    }  catch (Exception ex) {
       throw ex;
    }
  }
	
}
