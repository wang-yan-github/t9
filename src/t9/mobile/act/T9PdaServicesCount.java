package t9.mobile.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.message.BasicNameValuePair;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.logic.T9PdaLoginLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaServicesCount {
  private static String wrapDateField(String fieldName, String dbms) throws SQLException  {
    if (dbms.startsWith("sqlserver")) {
      return "CONVERT(varchar, " + fieldName + ", 20)";
    }else if (dbms.startsWith("mysql")) {
      return "date_format("+fieldName +", \'%Y-%m-%d %H:%i:%S\')";
    }else if (dbms.startsWith("oracle")) {
      return "to_char(" + fieldName + ", \'yyyy-MM-dd hh24:mi:ss\')";
    }else {
      throw new SQLException("not accepted dbms");
    }
  }
  public String services(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String UID = request.getParameter("UID");
      Map<String,  String> map = T9QuickQuery.quickQuery(dbConn, "select SEQ_ID , USER_ID,DEPT_ID,USER_PRIV,USER_PRIV_OTHER, DEPT_ID_OTHER FROM PERSON WHERE USER_ID = '" + UID + "'");
      String LAST_UPDATE =T9Utility.getCurDateTimeStr();
      String MODULES = request.getParameter("MODULES");
      
      if (T9Utility.isNullorEmpty(MODULES)) {
        T9MobileUtility.output(response, "未定义需要查询的模块参数");
        return null;
      }
      int totalItems = 0 ;
      String moduleArrayCountStr = "";
      
      String deptOther = map.get("DEPT_ID_OTHER");
      String userPrivOther = map.get("USER_PRIV_OTHER");
      String seqId = map.get("SEQ_ID");
      String deptId = map.get("DEPT_ID");
      String userPriv = map.get("USER_PRIV");
      
      if ("email".equals(MODULES) || "INIT".equals(MODULES)) {
        String query = "SELECT count(*) from EMAIL,EMAIL_BODY where EMAIL_BODY.SEQ_ID=EMAIL.BODY_ID and BOX_ID=0 and EMAIL.TO_ID='"+seqId+"' and SEND_FLAG='1' and (DELETE_FLAG='' or DELETE_FLAG='0' or DELETE_FLAG='2') and READ_FLAG!='1'";
        totalItems = T9QuickQuery.getCount(dbConn, query);
        moduleArrayCountStr += "\"email\":\"" + totalItems + "\",";
      }
      
      if ("notify".equals(MODULES) || "INIT".equals(MODULES)) {
        String query = "SELECT count(*) from NOTIFY where (TO_ID='ALL_DEPT' or TO_ID='0'  or "+T9DBUtility.findInSet(deptId+"", "TO_ID") + T9MobileUtility.privOtherSql("TO_ID", deptOther) +" or "+T9DBUtility.findInSet(userPriv+"", "PRIV_ID") + T9MobileUtility.privOtherSql("PRIV_ID", userPrivOther) +" or "+T9DBUtility.findInSet(seqId+"", "USER_ID")+") and  "
            + "( READERS is  null or (READERS is not null and  " + T9DBUtility.findNoInSet(String.valueOf(seqId), "READERS") + "))"
            +" and BEGIN_DATE<=? and (END_DATE>=? or END_DATE is null) and PUBLISH='1' ";  
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
          stmt = dbConn.prepareStatement(query);
          stmt.setTimestamp(1, new Timestamp(new Date().getTime()) );
          stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
          rs = stmt.executeQuery();
          if(rs.next()){
            totalItems = rs.getInt(1);
          }
        }catch(Exception e){
          throw e;
        }finally{
          T9DBUtility.close(stmt, rs, null);
        }

        moduleArrayCountStr += "\"notify\":\"" + totalItems + "\",";
      }
      
      if ("workflow".equals(MODULES) || "INIT".equals(MODULES)) {
        String query = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and USER_ID='"+seqId+"' and FLOW_RUN.DEL_FLAG='0' and PRCS_FLAG in (1,2) and CHILD_RUN='0'";  
        totalItems = T9QuickQuery.getCount(dbConn, query);
        moduleArrayCountStr += "\"workflow\":\"" + totalItems + "\",";
      }
      
      if ("news".equals(MODULES) || "INIT".equals(MODULES)) {
        String query =  "SELECT count(*) from NEWS where PUBLISH='1' and (TO_ID='ALL_DEPT' or TO_ID='0'  or "
      +T9DBUtility.findInSet(deptId+"", "TO_ID")
      + T9MobileUtility.privOtherSql("TO_ID", deptOther) 
      +" or "+T9DBUtility.findInSet(userPriv+"", "PRIV_ID")
      + T9MobileUtility.privOtherSql("PRIV_ID", userPrivOther) 
      +" or "+T9DBUtility.findInSet(seqId+"", "USER_ID")+") and  "
       + "( READERS is  null or (READERS is not null and  " + T9DBUtility.findNoInSet(String.valueOf(seqId), "READERS") + "))"; 
        totalItems = T9QuickQuery.getCount(dbConn, query);
        moduleArrayCountStr += "\"news\":\"" + totalItems + "\",";
      }
     
      if ("calendar".equals(MODULES) || "INIT".equals(MODULES)) {
        String query = "SELECT COUNT(*) from CALENDAR where USER_ID='"+seqId+"' and "+T9DBUtility.getDateFilter("CAL_TIME", T9Utility.getCurDateTimeStr(), "<")+" and OVER_STATUS=0";
        String CUR_DATE = T9Utility.getDateTimeStr(new Date());
        if(CUR_DATE == null || "".equals(CUR_DATE)){
          CUR_DATE = "0000-00-00";
        }
        String _strDate = CUR_DATE.substring(0,10);
        String _stime = _strDate + " "+"00:00:00";
        String _etime = _strDate + " "+"23:59:59";
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(*) from CALENDAR where USER_ID='"+seqId+"' and (");
        sb.append(T9DBUtility.getDateFilter("CAL_TIME",_stime , ">="));
        sb.append(") and (");
        sb.append(T9DBUtility.getDateFilter("CAL_TIME",_etime , "<="));
        sb.append(") order by SEQ_ID desc");
        
        totalItems = T9QuickQuery.getCount(dbConn, sb.toString());
        moduleArrayCountStr += "\"calendar\":\"" + totalItems + "\",";
      }
      
      if ("message".equals(MODULES) || "INIT".equals(MODULES)) {
        String query = "SELECT COUNT(*) from message where TO_ID='"+ seqId +"' and DELETE_FLAG!='1' and REMIND_FLAG = 1";
        totalItems = T9QuickQuery.getCount(dbConn, query);
        moduleArrayCountStr += "\"message\":\"" + totalItems + "\",";
      }
      if (moduleArrayCountStr.endsWith(",")) {
        moduleArrayCountStr = moduleArrayCountStr.substring(0, moduleArrayCountStr.length() -1);
      }
      String str = "{\"module\":[{"+moduleArrayCountStr+"}], \"last_update\": \""+LAST_UPDATE+"\"}";
      T9MobileUtility.output(response, str);
      //System.out.println(str);
      return null;
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }
  }
 
}
