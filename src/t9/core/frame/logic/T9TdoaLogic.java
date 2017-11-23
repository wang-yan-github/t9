package t9.core.frame.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9TdoaLogic {
  private static Logger log = Logger.getLogger("t9.core.frame.act");
  
  public T9Person updateUserParam(Connection dbConn, String oaItem, T9Person person) throws Exception {
    
    T9ORM orm = new T9ORM();
    person = (T9Person)orm.loadObjSingle(dbConn, T9Person.class, person.getSeqId());
    Map<String, String> param = T9FOM.json2Map(person.getParamSet());
    param.put("oaItem", oaItem);
    
    String sql = " UPDATE person  SET PARAM_SET='"+T9FOM.toJson(param)+"' WHERE SEQ_ID="+person.getSeqId();
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, null, log);
    }
    return (T9Person)orm.loadObjSingle(dbConn, T9Person.class, person.getSeqId());
  }
  

  public String getUserParamOaItem(Connection dbConn, T9Person person,String contextPath) throws Exception {
    
    StringBuffer sb = new StringBuffer("[");
    
    T9ORM orm = new T9ORM();
    person = (T9Person)orm.loadObjSingle(dbConn, T9Person.class, person.getSeqId());
    
    Map<String, String> param = T9FOM.json2Map(person.getParamSet());
    String oaItemSre = param.get("oaItem");
    
    if(!T9Utility.isNullorEmpty(oaItemSre)){
      String oaItemsArr[] =  oaItemSre.split("\\|");
      for(String oaItems : oaItemsArr){
        String oaItemArr[] = oaItems.split(",");
        sb.append("[");
        for(String oaItem : oaItemArr){
          String sql = " SELECT FUNC_CODE,ICON,FUNC_NAME,MENU_ID FROM sys_function s WHERE s.MENU_ID='"+oaItem+"'";
          PreparedStatement ps = null;
          ResultSet rs = null;
          try {
            ps = dbConn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
              sb.append("{");
              String funcCode = rs.getString("FUNC_CODE");
              String icon = rs.getString("ICON");
              String funcName = rs.getString("FUNC_NAME");
              String menuId = rs.getString("MENU_ID");
              
              icon = parseMenuIcon(icon);
              
              /*
               * syl ，将单位、部门、人员菜单代码转换成真实路径
               */
              String url = T9SystemAct.parseMenuUrl(funcCode, contextPath);
              
              sb.append("icon:\""+icon+"\",");
              sb.append("url:\""+url+"\",");
            //  sb.append("url:\""+funcCode+"\",");
              sb.append("text:\""+funcName+"\",");
              sb.append("id:\""+menuId+"\"");
              sb.append("},");
            }
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(ps, rs, log);
          }
        }
        if(sb.toString().endsWith(",")){
          sb = sb.deleteCharAt(sb.length()-1);
        }
        sb.append("],");
      }
      if(sb.toString().endsWith(",")){
        sb = sb.deleteCharAt(sb.length()-1);
      }
    }
    sb.append("]");
    return sb.toString();
  }
  
  public String parseMenuIcon(String icon) {
    String folder = T9SysProps.getWebPath() + "/core/frame/5/styles/style1/css/images/app_icons/";
    File iconFile = new File(folder + icon);
    if (!iconFile.exists() || !iconFile.isFile()) {
      return "default.png";
    }
    else{
      return icon;
    }
  }
  
  public void updateUserParamStyle(Connection dbConn, String oaStyle, T9Person person) throws Exception {
    
    T9ORM orm = new T9ORM();
    person = (T9Person)orm.loadObjSingle(dbConn, T9Person.class, person.getSeqId());
    Map<String, String> param = T9FOM.json2Map(person.getParamSet());
    param.put("oaStyle", oaStyle);
    person.setParamSet(T9FOM.toJson(param).toString());
    
    String sql = " UPDATE person  SET PARAM_SET='"+T9FOM.toJson(param)+"' WHERE SEQ_ID="+person.getSeqId();
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      person = (T9Person)orm.loadObjSingle(dbConn, T9Person.class, person.getSeqId());
      T9DBUtility.close(ps, null, log);
    }
  }
  
  public String getUserParamOaStyle(Connection dbConn, T9Person person) throws Exception {
    
    T9ORM orm = new T9ORM();
    person = (T9Person)orm.loadObjSingle(dbConn, T9Person.class, person.getSeqId());
    
    Map<String, String> param = T9FOM.json2Map(person.getParamSet());
    String oaStyle = param.get("oaStyle");
    
    String sb = "{\"oaStyle\":\""+oaStyle+"\"}";
    return sb;
  }
}
