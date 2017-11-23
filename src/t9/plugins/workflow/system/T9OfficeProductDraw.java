package t9.plugins.workflow.system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import t9.core.funcs.workflow.util.T9IWFHookPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9OfficeProductDraw  implements T9IWFHookPlugin{

  @Override
  public String execute(Connection conn, int runId, Map arrayHandler,
      Map formData, boolean agree) throws Exception {
        String PRO_ID =(String)arrayHandler.get("PRO_ID");
        String BORROWER_ID=(String)arrayHandler.get("BORROWER_ID");
        String REMARK=(String)arrayHandler.get("REMARK");
        String FACT_QTY=(String)arrayHandler.get("TRANS_QTY");
        String remove_reason=(String)arrayHandler.get("REASON");
        String TRANS_ID=(String)arrayHandler.get("KEY");
        String query="select a.PRO_UNIT,a.PRO_NAME,a.PRO_STOCK,a.PRO_PRICE,c.PRO_KEEPER from OFFICE_PRODUCTS a left outer join OFFICE_TYPE b on a.OFFICE_PROTYPE=b.SEQ_ID left outer join OFFICE_DEPOSITORY c on b.TYPE_DEPOSITORY=c.SEQ_ID where a.SEQ_ID='"+PRO_ID+"'";
        
        Statement stmt=null;
        ResultSet rs=null;
        try{
        stmt=conn.createStatement();
        rs=stmt.executeQuery(query);
        String PRO_STOCK="";
        String PRO_PRICE="";
        String PRO_KEEPER="";
        String PRO_UNIT ="";   
        String PRO_NAME ="";
        if(rs.next())
        {
            PRO_STOCK=rs.getString("PRO_STOCK");
            PRO_PRICE=rs.getString("PRO_PRICE");
            PRO_KEEPER=rs.getString("PRO_KEEPER");
            PRO_UNIT =rs.getString("PRO_UNIT");   
            PRO_NAME =rs.getString("PRO_NAME");
        }
        if (T9Utility.isNullorEmpty(PRO_PRICE)
            || !T9Utility.isNumber(PRO_PRICE)) {
          PRO_PRICE = "0.0";
        }
        if(agree)
        {   
           if (!T9Utility.isInteger(FACT_QTY)) {
             FACT_QTY = "0";
           }
           
           int FACT_TRANS_QTY=Integer.parseInt(FACT_QTY)*(-1);
           String update="update OFFICE_TRANSHISTORY set FACT_QTY='"+FACT_QTY+"',OPERATOR='"+BORROWER_ID+"',TRANS_STATE='1',TRANS_QTY='"+FACT_TRANS_QTY+"',PRICE='"+PRO_PRICE+"' where SEQ_ID='"+TRANS_ID+"'";
           T9WorkFlowUtility.updateTableBySql(update, conn);

           int NEW_PRO_STOCK=Integer.parseInt(PRO_STOCK)-Integer.parseInt(FACT_QTY);
           
           update="update OFFICE_PRODUCTS set PRO_STOCK ='"+NEW_PRO_STOCK+"' where SEQ_ID='"+PRO_ID+"'";
           T9WorkFlowUtility.updateTableBySql(update, conn);
        }
        else
        {
           String update="update OFFICE_TRANSHISTORY set TRANS_STATE='2',REASON='"+remove_reason+"' where SEQ_ID='"+TRANS_ID+"'";
           T9WorkFlowUtility.updateTableBySql(update, conn);
        }
       }catch(Exception ex) {
         throw ex;
       } finally {
         T9DBUtility.close(stmt, rs, null); 
       }

    return null;
  }

}
