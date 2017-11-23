package t9.mobile.workflow.act;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.act.T9WorkTurnAct;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.data.T9FlowRunFeedback;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FeedbackLogic;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9FreeFlowLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.mobile.news.logic.T9PdaNewsLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;
import t9.mobile.workflow.logic.T9PdaBackLogic;
import t9.mobile.workflow.logic.T9PdaHandlerLogic;
import t9.mobile.workflow.logic.T9PdaTurnLogic;
import t9.mobile.workflow.logic.T9PdaWorkFlowLogic;
import t9.pda.mobilseal.util.XXTEA;

public class T9SealDataShowAct {
 
	public String data(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      
      String flowIdStr = request.getParameter("FLOW_ID");
      String runIdStr = request.getParameter("RUN_ID");
      String item = T9Utility.null2Empty(request.getParameter("ITEM_ID"));
      item = item.replace("DATA_", "");
      int itemId =Integer.parseInt(item);
      
      
      int runId = Integer.parseInt(runIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      
      String CHECK_FIELD = T9Utility.null2Empty(request.getParameter("CHECK_FIELD"));
      
      T9FlowRunUtility u = new T9FlowRunUtility();
      String[] CHECK_FIELDS =  CHECK_FIELD.split(",");
      
      String values = "";
      for (String s : CHECK_FIELDS) {
        if (T9Utility.isNullorEmpty(s)) continue;
        s = s.replace("DATA_", "");
        T9FlowRunData data = u.getFlowRunData(conn, runId, Integer.parseInt(s), flowId);
        
        values += data.getItemData() + ",";
      }
      
      T9FlowRunData sealData = u.getFlowRunData(conn, runId, itemId, flowId);
      
      
      String sealFile = sealData.getItemData();
      String[] p = sealFile.split("\\*");
      T9WorkFlowUtility util = new T9WorkFlowUtility();
      String path = util.getAttachPath(p[1], p[0], "workflow");
      byte[] bytes1 = T9FileUtility.loadFile2Bytes(path);
      String seal = new String(bytes1);
      
      BASE64Decoder decoder = new BASE64Decoder();
      if (seal != null) {
        seal = new String(XXTEA.decrypt(decoder.decodeBuffer(seal), values.getBytes()));
      }
      
      String result = new String(decoder.decodeBuffer(seal));
      
      Map m = T9FOM.json2Map(result);
      String sealData2 =(String) m.get("SealData");
      String height =T9Utility.null2Empty((String) m.get("SealHeight"));
      String width =T9Utility.null2Empty((String) m.get("SealWidth"));
      
      
      InputStream is =   new ByteArrayInputStream(decoder.decodeBuffer(sealData2));
      
      response.setContentType("application/octet-stream");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("height", height);
      response.setHeader("width", width);
      response.setHeader("Content-Disposition", "attachment;filename=" + itemId);
      OutputStream out = null;
      try {      
        out = response.getOutputStream();
        byte[] buff = new byte[1024];
        int readLength = 0;
        while ((readLength = is.read(buff)) > 0) {        
           out.write(buff, 0, readLength);
        }
        out.flush();
      }catch(Exception ex) {
        ex.printStackTrace();
      }finally {
        try {
          if (is != null) {
            is.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
        try {
          if (out != null) {
            out.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }

      
    }  catch (Exception ex) {
       throw ex;
    }
    return null;
  }
	public String show(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      
      String id = request.getParameter("id");
      
      String sealFile = "";
      String name = "";
      
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = conn.prepareStatement("select SEAL_DATA , SEAL_NAME FROM MOBILE_SEAL WHERE SEQ_ID = " + id);
        rs = ps.executeQuery();
        if (rs.next()){
          sealFile = rs.getString(1);
          name = rs.getString(2);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
      
      String seal = new String(sealFile.getBytes());
      
      
      BASE64Decoder decoder = new BASE64Decoder();
      String result = new String(decoder.decodeBuffer(seal));
        
      
      Map m = T9FOM.json2Map(result);
      String sealData2 =(String) m.get("SealData");
      String height =T9Utility.null2Empty((String) m.get("SealHeight"));
      String width =T9Utility.null2Empty((String) m.get("SealWidth"));
      
      InputStream is =   new ByteArrayInputStream(decoder.decodeBuffer(sealData2));
      
      response.setContentType("application/octet-stream");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("height", height);
      response.setHeader("width", width);
      response.setHeader("Content-Disposition", "attachment;filename=" + name);
      OutputStream out = null;
      try {      
        out = response.getOutputStream();
        byte[] buff = new byte[1024];
        int readLength = 0;
        while ((readLength = is.read(buff)) > 0) {        
           out.write(buff, 0, readLength);
        }
        out.flush();
      }catch(Exception ex) {
        ex.printStackTrace();
      }finally {
        try {
          if (is != null) {
            is.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
        try {
          if (out != null) {
            out.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }

      
    }  catch (Exception ex) {
       throw ex;
    }
    return null;
  }
	public static void main(String[] arg) throws Exception {
	  String ssss= "fdasssssssssssssssss";
	  String key = ",,";
	  BASE64Decoder decoder = new BASE64Decoder();
	  BASE64Encoder encoder = new BASE64Encoder();
	  
	  String s = new String(encoder.encode(XXTEA.encrypt(ssss.getBytes(), key.getBytes())));
	  System.out.println(s);
	  
	  String s2 = new String(XXTEA.decrypt(decoder.decodeBuffer(s), key.getBytes()));
    System.out.println(s2);
	}
}
