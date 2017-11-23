package t9.core.funcs.doc.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9UpdateAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FlowImportAct");
  
  public String updateSpace(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableName = request.getParameter("tableName");
      String query = "select seq_id,SIGN_DATA , attachment_NAME from  " + tableName;
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        while (rs.next()) {
          String attachmentId = T9WorkFlowUtility.clob2String(rs.getClob("SIGN_DATA"));
          int seqId = rs.getInt("seq_id");
          if (" ".equals(attachmentId)) {
            query = "update  "+ tableName +" SET SIGN_DATA = NULL   WHERE seq_id=" + seqId;
            PreparedStatement stm5 = null;
            try {
              stm5 = dbConn.prepareStatement(query);
              stm5.executeUpdate();
            } catch(Exception ex) {
              throw ex;
            } finally {
              T9DBUtility.close(stm5, null, null); 
            }
          }
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null); 
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String importDelFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String query = "select RUN_ID , DEL_FLAG from "+ T9WorkFlowConst.FLOW_RUN;
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        while (rs.next()) {
          int delFlag = rs.getInt("DEL_FLAG");
          int runId = rs.getInt("RUN_ID");
          String update = "update "+ T9WorkFlowConst.FLOW_RUN_PRCS +" set DEL_FLAG=" + delFlag + " where RUN_ID = " + runId;
          Statement stm2 = null;
          try {
            stm2 = dbConn.createStatement();
            stm2.executeUpdate(update);
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(stm2, null, null); 
          }
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null); 
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
//  public static void main(String[] args) {
//    InputStream in = null;
//    try{
//      File file = new File("C:\\Users\\Think\\Desktop\\督查督办.xml");
//      InputStream is = new FileInputStream(file);
//      in = new BufferedInputStream(is);
//      StringBuffer sb = new StringBuffer();
//      BufferedReader reader =  new  BufferedReader(new  InputStreamReader(is));    
//      String line =  null ;    
//      try {    
//        while((line = reader.readLine()) !=  null ) {    
//          sb.append(line );    
//        }    
//      } catch (IOException e) {    
//        throw e;   
//      } finally{    
//        try{    
//          is.close();    
//        }catch (IOException e) {    
//          throw e;  
//        }    
//      }    
//      StringReader rs = new StringReader(sb.toString());
//      SAXReader saxReader = new SAXReader();   
//      Document document = saxReader.read(rs);
//      
//      Element root =  document.getRootElement();
//      Element flowMsg = root.element("BaseInfo");
//     
//      List<Element> iterator = flowMsg.elements();
//      String query = "update FLOW_TYPE set ";
//      for (Element el : iterator){
//        String name = el.getName();
//        if (name.equals("FLOW_ID") 
//            || name.equals("FLOW_NAME")
//            || name.equals("FLOW_SORT")
//            || name.equals("FORM_ID")) {
//          continue; 
//        }
//        if ("".equals(el.getText())) {
//          query += " " + el.getName() + "=null,";
//        } else {
//          query += " " + el.getName() + "='" + el.getText() + "',";
//        }
//      }
//      query = query.substring(0, query.length() - 1);
//      
//      query = "delete from FLOW_PROCESS where FLOW_ID=" ;
//      
//      System.out.println(query);
////      List<Node> rowList = document.selectNodes("/WorkFlow/Process/ID"); 
////      for (Node node : rowList) {
//        //System.out.println(flowMsg.getName() + ":" + flowMsg.getText());
////      }
//      
//    }catch(Exception ex){
//      
//    }
//  }
  
  public static Connection getconn2() throws Exception{
    Connection conn = null;
    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
    conn = java.sql.DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.126:1521:orcl","TD_OA","test");
    return conn;
  }
  public static String getUserSeqId2(String privStr , Connection conn) throws Exception{
    String result = "";
    if (privStr == null || "".equals(privStr)){
      return privStr;
    }
    String [] arra = privStr.split("\\|");
    String user = "";
    String priv = "";
    String dept = "";
    if (arra.length == 1 ) {
      user = arra[0];
    } else if (arra.length >= 2 ) {
      user = arra[0];
      dept = arra[1];
      if (arra.length == 3) {
        priv = arra[2];
      }
      if ("ALL_DEPT".equals(dept)) {
        dept = "0";
      }
    } 
    T9FlowTypeLogic f = new T9FlowTypeLogic();
    user = f.getUserSeqId(user, conn);
    result = user + "|" + dept + "|" + priv;
    return result;
  }
  public String test() {
    //int flowIdSelected = flowId;
    String query = "SELECT "
      + " a.RUN_ID "
      + " ,a.PRCS_ID "
      + " ,a.FLOW_PRCS "
      + " ,a.PRCS_FLAG "
      + " ,a.OP_FLAG "
      + " ,a.PRCS_TIME "
      + " ,a.CREATE_TIME "
      + " ,b.FREE_OTHER "
      + " , b.FLOW_ID "
      + " ,b.FLOW_NAME "
      + " ,b.FLOW_TYPE "
      + " ,b.LIST_FLDS_STR "
      + " ,b.FORM_ID "
      + " ,c.RUN_NAME "
      + " , c.BEGIN_USER "
      + " , c.END_TIME  "
      + "  from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" AS a, "+ T9WorkFlowConst.FLOW_TYPE +" AS b, "+ T9WorkFlowConst.FLOW_RUN +" AS c  "
      + " WHERE  "
      + " a.RUN_ID=c.RUN_ID and  "
      + "  c.FLOW_ID=b.FLOW_ID and  "
      + " c.DEL_FLAG='0' and  "
      + " a.CHILD_RUN=0 and  "
      + "  a.USER_ID=";
    
    
    int flowCount = 0 ;
    Map userArray  = new HashMap();
    
    return null;
  }
  private String debugPath = "debug";
  public String getDebug(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    try{
      String type = request.getParameter("type");
      String contextRealPath = request.getSession().getServletContext().getRealPath("/");
      String path = contextRealPath + "/"+ debugPath + "/" + type + "/debug.log";
      File debugFile = new File(path);
      if (debugFile.exists()) {
        FileInputStream fi = new FileInputStream(debugFile);
        response.setContentType("application/octet-stream");
        response.setHeader("Cache-control","private");
        response.setHeader("Accept-Ranges","bytes");
        String fileName = type + ".log";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName );  
        OutputStream os = response.getOutputStream();
        byte[] buff = new byte[1024];
        int readLength = 0;
        while ((readLength = fi.read(buff)) > 0) {        
          os.write(buff, 0, readLength);
        }
        os.flush();
        os.close();
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
