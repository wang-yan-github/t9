package t9.rad.velocity.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DsTable;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.db.T9StringFormat;
import t9.core.util.form.T9FOM;
import t9.rad.velocity.T9Code2DbUtil;
import t9.rad.velocity.T9CodeUtil;
import t9.rad.velocity.T9velocityUtil;
import t9.rad.velocity.createtable.T9CreateTableUtil;
import t9.rad.velocity.createtable.T9DBDialectUtil;
import t9.rad.velocity.metadata.T9Field;
import t9.rad.velocity.metadata.T9GridField;

public class T9CodeUtilAct {
  public String loginCheck(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    String url = "D:\\project\\t9\\src\\t9\\core\\act";
    String templateName = "ActTemplate.vm";
    T9velocityUtil.velocity(request.getParameterMap(), url, templateName, "");
    return "/core/inc/rtjson.jsp";
  }
  
  public String create(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNos = request.getParameter("tableNos");
      String outp = request.getParameter("outpath");
      String templateUrl = request.getParameter("templateUrl").trim();
      String[] dialects = request.getParameterValues("dialect");
      String templateName = "";
      if(templateUrl.endsWith("\\")){
        String str = templateUrl.substring(0, templateUrl.length()-1);
        if(str.endsWith(".vm")){
          int index = str.lastIndexOf("\\");
          templateUrl = str.substring(0,index + 1);
        }
      }
      String[] tableNoArr = tableNos.split(",");
        for (String dia : dialects) {
          String outpath = outp + dia + "\\";
          templateName = "createtable" + dia + ".vm";
          T9velocityUtil.velocity(
              T9CreateTableUtil.createTableById(dbConn,tableNoArr,dia)
              , outpath, templateName, templateUrl);
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成成功！");
    } catch(Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成失败！");
      e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }

  public String showField(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tabNo = request.getParameter("tableNo").trim();
      //System.out.println(tabNo);
      String data = T9Code2DbUtil.getFields(dbConn, tabNo);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "chenggong！");
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      //System.out.println(data);
    } catch(Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成失败！");
      e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String code2java(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tabNo = request.getParameter("tableNo").trim();
      String filedsVal = request.getParameter("filedsVal").trim();
      String[] selectFilter = request.getParameterValues("selectFilter");
      String[] radioFilter = request.getParameterValues("radioFilter");
      ArrayList< T9GridField> gridFields = new ArrayList<T9GridField>();
      ArrayList< T9Field> fields = new ArrayList<T9Field>();
      String[] fieldsVals = filedsVal.split("/");
      for (String fieldValue : fieldsVals){
        String[] fie = fieldValue.split(",");
        String header = fie[1].trim(); 
        String realName = fie[0].trim(); 
        String name = T9StringFormat.unformat(fie[0].trim()); 
        String hidden = ""; 
        if("0".equals(fie[3].trim())){
          hidden = "true";
        } else {
          hidden = "false";
        }
        String with = fie[2].trim();
        
        String fkTableName = "";
        String fkRelaFieldName = "";
        String fkNameFieldName = "";
        String fkTableNo = "";
        
        String fkTableName2 = "";
        String fkFilterName = "";
        String codeClass = "";
        String fkNameFieldName2 = "";
        String sql = " SELECT d1.TABLE_NAME FK_TABLE_NAME, d2.FIELD_NAME FK_RELA_FIELD_NAME, d3.FIELD_NAME FK_NAME_FIELD_NAME, d1.TABLE_NO "
                   + "      , d4.TABLE_NAME FK_TABLE_NAME2, d5.FIELD_NAME FK_FILTER_NAME, d.CODE_CLASS, d6.FIELD_NAME FK_NAME_FIELD_NAME2 "
                   + " FROM DS_FIELD d "
                   + " left join DS_TABLE d1 on d.FK_TABLE_NO = d1.TABLE_NO "
                   + " left join DS_FIELD d2 on d.FK_RELA_FIELD_NO = d2.FIELD_NO "
                   + " left join DS_FIELD d3 on d.FK_NAME_FIELD_NO = d3.FIELD_NO "
                   + " left join DS_TABLE d4 on d.FK_TABLE_NO2 = d4.TABLE_NO "
                   + " left join DS_FIELD d5 on d.FK_FILTER = d5.FIELD_NO "
                   + " left join DS_FIELD d6 on d.FK_NAME_FIELD_NO2 = d6.FIELD_NO "
                   + " where d.TABLE_NO = '"+ tabNo +"' and d.FIELD_NAME = '"+ realName +"'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
          ps = dbConn.prepareStatement(sql);
          rs = ps.executeQuery();
          while (rs.next()) {
            fkTableName = rs.getString("FK_TABLE_NAME");
            fkRelaFieldName = rs.getString("FK_RELA_FIELD_NAME");
            fkNameFieldName = rs.getString("FK_NAME_FIELD_NAME");
            fkTableNo = rs.getString("TABLE_NO");
            
            fkTableName2 = rs.getString("FK_TABLE_NAME2");
            fkFilterName = rs.getString("FK_FILTER_NAME");
            codeClass = rs.getString("code_Class");
            fkNameFieldName2 = rs.getString("FK_NAME_FIELD_NAME2");
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          T9DBUtility.close(ps, rs, null);
        }
        
        T9GridField gf = new T9GridField(header, name, realName, hidden, with, fkTableName, fkRelaFieldName, fkNameFieldName);
        if(!T9Utility.isNullorEmpty(fkTableName2)){
          gf = new T9GridField(header, name, realName, hidden, with, fkTableName2, "SEQ_ID", fkNameFieldName2);
        }
        gridFields.add(gf);
        
        boolean isMust = false;
        if("1".equals(fie[4].trim())){
          isMust = true;
        }
        T9Field f = new T9Field(name,header,isMust,fie[5].trim());
        if(!T9Utility.isNullorEmpty(fkTableName)){
          f.setHidden(true);
        }
        if(!T9Utility.isNullorEmpty(fkTableName2)){
          f.setFkTableName2(fkTableName2);
          f.setFkFilterName(T9StringFormat.unformat(fkFilterName));
          f.setCodeClass(codeClass);
          f.setFkNameFieldName2(T9StringFormat.unformat(fkNameFieldName2));
        }
        fields.add(f);
        if(!T9Utility.isNullorEmpty(fkTableName)){
          T9Field f0 = new T9Field(name + "Desc",header,isMust,fie[5].trim()
              , fkTableName, T9StringFormat.unformat(fkRelaFieldName), T9StringFormat.unformat(fkNameFieldName), fkTableNo
              , fkTableName2, T9StringFormat.unformat(fkFilterName), codeClass, T9StringFormat.unformat(fkNameFieldName2));
          fields.add(f0);
        }
      }
      Map m = new HashMap();
      Map req = request.getParameterMap();
      Set<String> keys = req.keySet();
      for (String key : keys){
        String value = ((String[]) req.get(key))[0];
        m.put(key, value);
      }
      ArrayList<String> sf = new ArrayList<String>();
      if(selectFilter != null){
        for (String string : selectFilter){
          sf.add(string);
        }
      }
      ArrayList<String> rf = new ArrayList<String>();
      if(radioFilter != null){
        for (String string : radioFilter){
          rf.add(string);
        }
      }
      m.put("rf", rf);
      m.put("sf", sf);
      m.put("listFields", gridFields);
      m.put("inputFields", fields);
      T9CodeUtil.autoCode(dbConn, tabNo,m );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成成功！");
    } catch(Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "代码生成失败,请检查数据字典是否配置正确！");
      e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
  public String test(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String fieldsVal = request.getParameter("filedsVal").trim();
    //System.out.println(fieldsVal);
    return "/core/inc/rtjson.jsp";
  }
}
