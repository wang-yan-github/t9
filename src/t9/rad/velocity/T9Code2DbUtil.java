package t9.rad.velocity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.data.T9DsType;
import t9.core.util.db.T9ORM;
import t9.core.util.db.T9StringFormat;
import t9.rad.velocity.metadata.T9JavaBody;
import t9.rad.velocity.metadata.T9JavaField;
import t9.rad.velocity.metadata.T9JavaHeader;
import t9.rad.velocity.metadata.T9JavaMethod;
import t9.rad.velocity.metadata.T9JavaMethodBody;
import t9.rad.velocity.metadata.T9JavaParam;

public class T9Code2DbUtil {

  public void db2JavaCodefName(Connection conn,String tableName){
    
  }
  /**
   * 
   * @param conn
   * @param tableNo
   * @param packageName
   * @return
   * @throws Exception
   */
  public Map<String, Object> db2JavaCodefNo(Connection conn,String tableNo,String packageName) throws Exception{
    //根据tableNo得到数据表的信息
    T9ORM orm = new T9ORM();
    Map<String, Object> filters = new  HashMap<String, Object>();
    filters.put("TABLE_NO", tableNo);
    T9DsTable dst = (T9DsTable) orm.loadObjComplex(conn, T9DsTable.class, filters);
    //System.out.println("dst : " + dst);
    return transMap(dst, packageName);
  }
  
  /**
   * 
   * @param dst
   * @param packageName
   * @return
   */
  public Map<String, Object> transMap(T9DsTable dst,String packageName){
    Map<String, Object> result = new HashMap<String, Object>();
    T9JavaHeader jh = new T9JavaHeader();
    T9JavaBody jb = new T9JavaBody();
    String className = dst.getClassName();//得到类名
    List<T9DsField> dsfs = dst.getFieldList();
    
    jb.setClassName(className);
    
    jh.setPackageName(packageName);
    
    for (T9DsField dsf : dsfs) {
      String fieldName = dsf.getPropName();//得到属性名
      String methodName = fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);//得到get/set方法名
      int typeInt = dsf.getDataType();
      String typeName = getTypeName(typeInt);//得到类型信息
      String importName = getImportValue(typeInt);//如果为非java.lang包下的类则importName不为空
      String defaultValue = dsf.getDefaultValue();
      
      if(importName != null ) {
        jh.addImportNames(importName);
      }
      
      jb.addFields(T9JavaField.get(fieldName, typeName, "private", defaultValue));
      String getName = "get"+methodName;
      String setName = "";
      if(T9DsType.isBitType(typeInt)){
        setName = "is"+methodName;
      }else{
        setName = "set"+methodName;
      }
      jb.addMethods(T9JavaMethod.get(getName, typeName).setMethodBody(T9JavaMethodBody.get("get", "fieldName", fieldName)));
      jb.addMethods(T9JavaMethod
          .get(setName, "void")
          .addArgs(T9JavaParam.addParam(typeName, fieldName))
          .setMethodBody(T9JavaMethodBody.get("set", "fieldName", fieldName)));
    }
    result.put("className", className);
    result.put("head", jh);
    result.put("body", jb);
    result.put("fileName", className+".java");
    return result;
  }
  /**
   * 
   * @param typeInt
   * @return
   */
  public String getTypeName (int typeInt) {
    String typeName = "";
    if(T9DsType.isBitType(typeInt)) {
      typeName = "boolean";
    } else if(T9DsType.isCharType(typeInt)) {
      typeName = "String";
    } else if(T9DsType.isDateType(typeInt)) {
      typeName = "Date";
    } else if(T9DsType.isDecimalType(typeInt)) {
      typeName =" double";
    } else if(T9DsType.isIntType(typeInt)) {
      typeName = "int";
    } else if(T9DsType.isLongType(typeInt)) {
      typeName = "long";
    }
    return typeName;
  }
  /**
   * 
   * @param typeInt
   * @return
   */
  public String getImportValue (int typeInt) {
    String importValue = null;
    if(T9DsType.isBitType(typeInt)) {
      
    } else if(T9DsType.isCharType(typeInt)) {
      
    } else if(T9DsType.isDateType(typeInt)) {
      importValue = "java.util";
    } else if(T9DsType.isDecimalType(typeInt)) {
      
    } else if(T9DsType.isIntType(typeInt)) {
      
    } else if(T9DsType.isLongType(typeInt)) {
      
    }
    return importValue;
  }
  public static String getFields (Connection conn ,String tableNo) throws Exception {
    String sql = "select FIELD_NAME, FIELD_DESC from DS_FIELD WHERE TABLE_NO = '" + tableNo + "' order by FIELD_NO ";
    //System.out.println(sql2);
    PreparedStatement ps = conn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    StringBuffer fi = new StringBuffer();
    while(rs.next()){
      String value = rs.getString(1);
      String value2 = rs.getString(2);
      if("SEQ_ID".equals(value)){
        continue;
      }
      if(!"".equals(fi.toString())){
        fi.append(",");
      }
      fi.append("{fieldName").append(":'").append(value).append("',").append("fieldDesc").append(":'").append(value2).append("'}");
    }
    return "[" + fi.toString() + "]" ;
  }
}
