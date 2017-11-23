package t9.core.funcs.workflow.act;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowFormReglex;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9FormVersionLogic;
import t9.core.funcs.workflow.logic.T9WorkflowSave2DataTableLogic;
import t9.core.funcs.workflow.praser.T9FormPraser;
import t9.core.funcs.workflow.util.T9FlowFormUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;

public class T9FlowImportAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.workflow.act.T9FlowImportAct");
  public String importForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    InputStream in = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      //注意这里的      int newId = 0 ;
      fileForm.parseUploadRequest(request);
      int type = 0 ;
      String formId = fileForm.getParameter("formId");
      int seqId = Integer.parseInt(formId);
      String ext = fileForm.getFileExt("htmlFile");
      if ("txt".equals(ext) 
          || "html".equals(ext)
          || "htm".equals(ext)) {
        FileItem fileItem =  fileForm.getFileItem("htmlFile");
        InputStream is = fileItem.getInputStream();
        String isOa = fileForm.getParameter("isOa");
        String utf8 = "utf-8";
        if ("on".equals(isOa)) {
          utf8 = "GBK";
        }
        BufferedReader reader =  new  BufferedReader( new  InputStreamReader(is, utf8));    
        StringBuffer sb = new StringBuffer();
        String line =  null ;    
        try {    
            while((line = reader.readLine()) !=  null ) {    
              sb.append(line );    
           }    
        } catch (IOException e) {    
          throw e;   
        } finally{    
          try{    
            is.close();    
          }catch (IOException e) {    
            throw e;  
          }    
        }    
        String printModel = sb.toString();
        if (printModel == null) {
          printModel = "";
        }
        printModel = printModel.replaceAll("[\n-\r]", "");
        printModel = getOutCss(printModel , seqId , dbConn);
        printModel = getOutScript(printModel , Integer.parseInt(formId ) , dbConn);
        printModel = printModel.replaceAll("\"", "\\\\\"");
        
        
        T9ORM orm = new T9ORM();
        T9FlowFormType form = (T9FlowFormType) orm.loadObjSingle(dbConn, T9FlowFormType.class, seqId);
        
        T9FlowFormType form2 = new T9FlowFormType();
        form2.setFormName(form.getFormName());
        form2.setItemMax(form.getItemMax());
        form2.setPrintModel(printModel);
        form2.setDeptId(form.getDeptId());
        
        String printModelNew = "";
        T9FlowFormLogic ffl = new T9FlowFormLogic();
        HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(printModel);
        Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
        printModelNew = T9FormPraser.toShortString(m1, printModel, T9FlowFormReglex.CONTENT);
        form2.setPrintModelShort(printModelNew);
        form2.setFormId(0);
        form2.setVersionNo(form.getVersionNo() + 1);
        form2.setVersionTime(new Date());
        //创建新的数据
        orm.saveSingle(dbConn, form2);
        T9FormVersionLogic logic =new T9FormVersionLogic();
         newId = logic.getMaxFormId(dbConn);
        T9FlowFormUtility ffu = new T9FlowFormUtility();
        ffu.cacheForm(newId, dbConn);
        T9WorkflowSave2DataTableLogic logic4 = new T9WorkflowSave2DataTableLogic();
        logic4.updateItemMax(dbConn, newId);
        //创建表结构
        String flowTypes = logic4.getFlowTypeByFormId(dbConn, seqId + "");
        logic4.createFlowFormTable(dbConn, newId, flowTypes);
        //更新flow关联
        logic4.updateFlowTypeByFormId(dbConn, seqId + "", newId);
        //修改formId
        logic4.updateFormTypeByFormId(dbConn, seqId + "", newId);
        type = 1;
      } else {
        type = 0 ;
      }
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/html");
      response.setHeader("Cache-Control", "no-cache");  
      PrintWriter out = response.getWriter();
      out.print("<body onload=\"window.parent.tooltip("+type +" ,null, "+newId +")\"/>");
      out.flush();
      out.close();
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  } 
  public String importFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    InputStream in = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      int type = 0 ;
      String flowId = fileForm.getParameter("flowId");
      String sUserOn = fileForm.getParameter("userOn");
      boolean isUserOn = false;
      if ("on".equals(sUserOn)) {
        isUserOn = true;
      }
      int seqId = Integer.parseInt(flowId);
      String ext = fileForm.getFileExt("attachment");
      if ("xml".equals(ext)) {
        FileItem fileItem =  fileForm.getFileItem("attachment");
        InputStream is = fileItem.getInputStream();
        BufferedReader reader =  new  BufferedReader(new  InputStreamReader(is , "UTF-8") );    
        StringBuffer sb = new StringBuffer(); 
        String line =  null ;    
        try {    
          while((line = reader.readLine()) !=  null ) {    
            sb.append(line );    
          }    
        } catch (IOException e) {    
          throw e;   
        } finally{    
          try{    
            is.close();    
          }catch (IOException e) {    
            throw e;  
          }    
        }    
        StringReader rs = new StringReader(sb.toString());
        SAXReader saxReader = new SAXReader();   
        Document document = saxReader.read(rs);
        T9FlowTypeLogic logic = new T9FlowTypeLogic();
        Element root =  document.getRootElement();
        logic.importFlow(root, Integer.parseInt(flowId), isUserOn, dbConn);
        type = 1;
      } else {
        type = 0 ;
      }
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/html");
      response.setHeader("Cache-Control", "no-cache");  
      PrintWriter out = response.getWriter();
      out.print("<body onload=\"window.parent.tooltip("+type +")\"/>");
      out.flush();
      out.close();
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public static void main(String[] args) {
    InputStream in = null;
    try{
      File file = new File("C:\\Users\\Think\\Desktop\\督查督办.xml");
      InputStream is = new FileInputStream(file);
      in = new BufferedInputStream(is);
      StringBuffer sb = new StringBuffer();
      BufferedReader reader =  new  BufferedReader(new  InputStreamReader(is));    
      String line =  null ;    
      try {    
        while((line = reader.readLine()) !=  null ) {    
          sb.append(line );    
        }    
      } catch (IOException e) {    
        throw e;   
      } finally{    
        try{    
          is.close();    
        }catch (IOException e) {    
          throw e;  
        }    
      }    
      StringReader rs = new StringReader(sb.toString());
      SAXReader saxReader = new SAXReader();   
      Document document = saxReader.read(rs);
      
      Element root =  document.getRootElement();
      Element flowMsg = root.element("BaseInfo");
     
      List<Element> iterator = flowMsg.elements();
      String query = "update FLOW_TYPE set ";
      for (Element el : iterator){
        String name = el.getName();
        if (name.equals("FLOW_ID") 
            || name.equals("FLOW_NAME")
            || name.equals("FLOW_SORT")
            || name.equals("FORM_ID")) {
          continue; 
        }
        if ("".equals(el.getText())) {
          query += " " + el.getName() + "=null,";
        } else {
          query += " " + el.getName() + "='" + el.getText() + "',";
        }
      }
      query = query.substring(0, query.length() - 1);
      
      query = "delete from FLOW_PROCESS where FLOW_ID=" ;
      
      //System.out.println(query);
//      List<Node> rowList = document.selectNodes("/WorkFlow/Process/ID"); 
//      for (Node node : rowList) {
        //System.out.println(flowMsg.getName() + ":" + flowMsg.getText());
//      }
      
    }catch(Exception ex){
      
    }
  }
  public  String getOutCss(String printModel , int formId , Connection conn) throws Exception {
    String css = "";
    int index1 =  printModel.indexOf("<style>");
    if ( index1 != -1) {
      printModel = printModel.substring(index1 + "<style>".length());
      int index = printModel.indexOf("</style>");
      if (index != -1){
        css = printModel.substring(0 , index);
        printModel = printModel.substring(index + "</style>".length());
      }
    }
    css = css.replaceAll("'", "''");
    String query = "update FLOW_FORM_TYPE set CSS='"+ css +"' where SEQ_ID=" + formId;
    PreparedStatement stm3 = null;
    try {
      stm3 = conn.prepareStatement(query);
      stm3.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm3, null, null); 
    }
    return printModel;
  }
  public  String getOutScript(String printModel , int formId , Connection conn) throws Exception {
    String script = "";
    int index1 =  printModel.indexOf("<script>");
    if ( index1 != -1) {
      printModel = printModel.substring(index1 + "<script>".length());
      int index = printModel.indexOf("</script>");
      if (index != -1){
        script = printModel.substring(0 , index);
        printModel = printModel.substring(index + "</script>".length());
      }
    }
    script = script.replaceAll("'", "''");
    String query = "update FLOW_FORM_TYPE set SCRIPT='"+ script +"' where SEQ_ID=" + formId;
    PreparedStatement stm3 = null;
    try {
      stm3 = conn.prepareStatement(query);
      stm3.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm3, null, null); 
    }
    return printModel;
  }
  
}
