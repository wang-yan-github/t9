package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.data.T9FlowPrintTpl;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9FlowPrintLogic {
  /**
   * 解析宏标记


   * @param flowRun
   * @param ft
   * @param form
   * @param conn
   * @return
   * @throws Exception
   */
  public String analysisAutoFlag (int runId , int flowId , String modelShort , Connection conn ) throws Exception {
    T9FlowRunLogic frl = new T9FlowRunLogic();
    T9FlowRun flowRun = frl.getFlowRunByRunId(runId , conn);
    T9FlowTypeLogic ftl = new T9FlowTypeLogic();
    T9FlowType ft = ftl.getFlowTypeById(flowId, conn);
    Date beginTime = flowRun.getBeginTime();
    String date = new SimpleDateFormat("yyyy-MM-dd").format(beginTime);
    String time = new SimpleDateFormat("HH:mm:ss").format(beginTime);
    String runName = flowRun.getRunName();
    runName = T9WorkFlowUtility.getRunName(runName);
    runName = runName.replace("$", "\\$");
    modelShort = modelShort.replaceAll("#\\[文号\\]", runName);
    modelShort = modelShort.replaceAll("#\\[时间\\]", "日期：" + date);
    modelShort = modelShort.replaceAll("#\\[流水号\\]", flowRun.getRunId() + "");
    modelShort = modelShort.replaceAll("#\\[文号计数器\\]", ft.getAutoNum() + "");
    //---------附件链接宏标记------------------
    T9WorkFlowUtility ut = new T9WorkFlowUtility();
    if ( modelShort.indexOf("#[会签意见") != -1) {
      modelShort = ut.getSignInfo(modelShort, flowRun, conn);
    }
    return modelShort;
  }  
  public String savePrintTpl(Connection conn,String tName,String content,String tType,String flowId) throws Exception{
     String seq_id="";
    try{    
      T9FlowPrintTpl tpl=new  T9FlowPrintTpl();
      tpl.setFlowId(Integer.parseInt(flowId));
      tpl.setTName(tName);
      tpl.setContent(content);
      tpl.setTType(tType);
      
      T9ORM orm = new T9ORM();
      orm.saveSingle(conn, tpl);
      T9FlowHookUtility ut = new T9FlowHookUtility();
      int attendEvectionId = ut.getMax(conn, " select max(SEQ_ID) FROM flow_print_tpl ");
      seq_id=attendEvectionId+"";
      
    }catch( Exception ex){  
      throw ex;
      }
      return seq_id;
  }
  
  
  public void updatePrintTpl(Connection conn,String seqId,String prcsStr,String tName,String content,String tType,String flowId) throws Exception{
    T9ORM orm=new T9ORM();
    try{    
      
      
      T9FlowPrintTpl tpl=new  T9FlowPrintTpl();
      tpl=(T9FlowPrintTpl)orm.loadObjSingle(conn,T9FlowPrintTpl.class , Integer.parseInt(seqId));
   
      tpl.setTName(tName);
      tpl.setContent(content);
      tpl.setTType(tType);
      tpl.setFlowPrcs(prcsStr);
      orm.updateSingle(conn, tpl);

    }catch( Exception ex){  
      throw ex;
      }finally{
        //T9DBUtility.close(stmt, rs, log)
      }
    
  }
  
  
  
    public void delTplLogic(Connection conn,String seq_id) throws Exception{
         
       try{
         T9ORM orm =new T9ORM();
        orm.deleteSingle(conn, T9FlowPrintTpl.class, Integer.parseInt(seq_id));
       }catch(Exception ex){
         throw ex;
       }finally{
       //  T9DBUtility.close(stmt, rs, null);
       }
     
    }
  

    public String loadAip(Connection conn,String seq_id) throws Exception{
        T9FlowPrintTpl tpl=new  T9FlowPrintTpl();
     try{
       T9ORM orm =new T9ORM();
   
       tpl=(T9FlowPrintTpl)orm.loadObjSingle(conn, T9FlowPrintTpl.class, Integer.parseInt(seq_id));
     }catch(Exception ex){
       throw ex;
     }finally{
     //  T9DBUtility.close(stmt, rs, null);
     }
    return T9FOM.toJson(tpl).toString();
  }

public void updateAip(Connection conn,String seqId , String runId , String attachmentId) throws Exception{
  Statement stmt=null;
  ResultSet rs=null;
  String sql = "select AIP_FILES from  FLOW_RUN WHERE RUN_ID='"+runId+"'";
  String aipFiles = "";
  try{
     stmt=conn.createStatement();
     rs=stmt.executeQuery(sql);
     if(rs.next()){   
       aipFiles = rs.getString("AIP_FILES");
     }
  }catch(Exception ex){
    throw ex;
  }finally{
    T9DBUtility.close(stmt, rs, null);
  }
  aipFiles += seqId + ":"+attachmentId+"\n";
  Statement stmt1=null;
  String sql2 = "update  FLOW_RUN set AIP_FILES = '"+ aipFiles +"' WHERE RUN_ID='"+runId+"'";
  try{
     stmt1=conn.createStatement();
     stmt1.executeUpdate(sql2);
  }catch(Exception ex){
    throw ex;
  }finally{
    T9DBUtility.close(stmt1, null, null);
  }
}

    
    public String getTplList(Connection conn,String flow_id) throws Exception{
         Statement stmt=null;
         ResultSet rs=null;
         String data="";
       try{
          String sql=" select * from  flow_print_tpl where flow_id='"+flow_id+"'";
          stmt=conn.createStatement();
          rs=stmt.executeQuery(sql);
          while(rs.next()){
            int seqId=rs.getInt("seq_id");
            String  tName=rs.getString("t_name");
            String tType=rs.getString("t_type");
            String flowPrcs=rs.getString("flow_prcs");
            flowPrcs=getPrcs(conn,flowPrcs,flow_id);
          data+="{seqId:'"+seqId+"',tName:'"+tName+"',tType:'"+tType+"',flowPrcs:'"+flowPrcs+"'}";
          data+=",";
          }
       
       }catch(Exception ex){
         throw ex;
       }finally{
         T9DBUtility.close(stmt, rs, null);
       }
       if(data.endsWith(",")){
         data=data.substring(0,data.length()-1);
       }
       
      return data;
    }
  
    public String getPrcs(Connection conn,String flowPrcs,String flow_id) throws Exception{
      Statement stmt=null;
      ResultSet rs=null;
      String data="";
      if(T9Utility.isNullorEmpty(flowPrcs)){
        return "";
      }
      String prcs[]=flowPrcs.split(",");
    try{
      for(int i=0;i<prcs.length;i++){
         String flowPcs=prcs[i];
         if(!T9Utility.isNullorEmpty(flowPcs)){
         String sql=" select * from  FLOW_PROCESS where flow_seq_id='"+flow_id+"' and prcs_id='"+flowPcs+"' order by prcs_id asc";
         stmt=conn.createStatement();
         rs=stmt.executeQuery(sql);
         if(rs.next()){         
           data+=rs.getString("prcs_name");
           data+=",";
         }
        }
      }
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    
    if(data.endsWith(",")){
      data=data.substring(0,data.length()-1);
    }
   return data;
 }
    
    public String getDisSelectByFlowIdLogic(Connection conn,String flow_id) throws Exception{
      Statement stmt=null;
      ResultSet rs=null;
      String data="";
    try{
       String sql=" select * from  FLOW_PROCESS where flow_seq_id='"+flow_id+"' order by prcs_id asc";
       stmt=conn.createStatement();
       rs=stmt.executeQuery(sql);
       while(rs.next()){
         int prcsId=rs.getInt("prcs_id");
         String  prcsName=rs.getString("prcs_name");
        
       data+="{value:'"+prcsId+"',text:'"+prcsName+"'}";
       data+=",";
       }
    
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0,data.length()-1);
    }
    
   return data;
 }
    
    
    public String getSelectByFlowIdLogic(Connection conn,String seq_id) throws Exception{
      Statement stmt=null;
      ResultSet rs=null;
      String data="";
    try{
       String sql=" select * from  FLOW_PRINT_TPL where seq_id='"+seq_id+"' ";
       stmt=conn.createStatement();
       rs=stmt.executeQuery(sql);
       if(rs.next()){
         int flow_id=rs.getInt("flow_id");
         String  prcsId=rs.getString("flow_prcs");
         if(!T9Utility.isNullorEmpty(prcsId)){
         String prcs[]=prcsId.split(",");
         for(int i=0;i<prcs.length;i++){
           if(!"".equals(prcs[i])){
            data+="{value:'"+prcs[i]+"',text:'"+this.getPrcs(conn,prcs[i] ,flow_id+"" )+"'}";
            data+=",";
         }
         }
       }
      }
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0,data.length()-1);
    }
    
   return data;
 }
    
    public String getTempOptionLogic(Connection conn,T9Person person,String flowId,String runId) throws Exception{
      Statement stmt=null;
      ResultSet rs=null;
      Statement stmt1=null;
      ResultSet rs1=null;
      String data="";
    try{
      String sql = "select SEQ_ID,T_NAME,FLOW_PRCS FROM FLOW_PRINT_TPL WHERE FLOW_ID='"+flowId+"' and T_TYPE = '1'";
     stmt=conn.createStatement();
     rs=stmt.executeQuery(sql);
      while(rs.next())
      {
          String flowPrcs = rs.getString("FLOW_PRCS");
          int seqId=rs.getInt("seq_id");
          String tName=rs.getString("t_name");
          if(T9Utility.isNullorEmpty(flowPrcs)){
            flowPrcs = "0";
          }
           if(flowPrcs.endsWith(",")){
             flowPrcs=flowPrcs.substring(0, flowPrcs.length()-1);
           }
         sql = "select * from FLOW_RUN_PRCS WHERE RUN_ID='"+runId+"' and USER_ID='"+person.getSeqId()+"' and FLOW_PRCS IN ("+flowPrcs+")";
          stmt1=conn.createStatement();
          rs1=stmt1.executeQuery(sql);
          if(rs1.next()){
            data+="{seqId:'"+seqId+"',tName:'"+tName+"'}";
            data+=",";
          }
            
      }     
    }catch(Exception ex){
      ex.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
      T9DBUtility.close(stmt1, rs1, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0,data.length()-1);
    }
    
   return data;
 }
   
    
    public String getFlowItemData(Connection conn,String flowId,String runId) throws Exception{
      Statement stmt=null;
      ResultSet rs=null;
      String data="";
      T9FlowRunUtility util =new T9FlowRunUtility();
    try{
     String sql = "select f.title,f.name  FROM FLOW_TYPE t,FLOW_FORM_ITEM f WHERE t.form_seq_id=f.form_id and t.seq_id='"+flowId+"'";
     stmt=conn.createStatement();
     rs=stmt.executeQuery(sql);
      while(rs.next())
      {
          String title = T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("title")));
          String name= T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("name")));
          name =T9Utility.encodeSpecial(util.getData(conn, Integer.parseInt(flowId), Integer.parseInt(runId), title)) ;
          if(T9Utility.isNullorEmpty(name)){
            name="";
          }
          data+="{name:\""+title+"\",data:\""+name+"\"}";
          data+=",";
      }   
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    if(data.endsWith(",")){
      data=data.substring(0,data.length()-1);
    }
    
   return data;
 }
    
    public String getDataByFidAndItem(Connection conn,String rId,String Item) throws Exception{
      String data="";
      Statement stmt=null;
      ResultSet rs=null;
      if("run_id".equals(Item) || "RUN_ID".equals(Item)){
        return rId;
      }
      try{
        String sql="select * from flow_run_data where run_id="+rId+" and item_id="+Integer.parseInt(Item.substring(Item.indexOf("_")+1,Item.length()));
        stmt=conn.createStatement();
        rs=stmt.executeQuery(sql);
        if(rs.next()){
          data=T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("item_data")));
        }
      }catch(Exception e){
        throw e;
      }finally{
        T9DBUtility.close(stmt, rs, null);
      }
      
      return data;
    }
    
    
}
