package raw.cy.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.db.T9StringFormat;

/**
 * 通用的data-to-json转换工具
 * 
 * @author TTlang
 * 
 */
public class T9DTJ {
  //private Map<String, String>
  /**
   * 通过tableNo组装成sql语句
   * @param conn
   * @param tableNo
   * @return
   * @throws Exception
   */
  public StringBuffer toSqlString(Connection conn,String tableNo) throws Exception{
    StringBuffer sqlBuff = new  StringBuffer();
    StringBuffer fieldBuff = new StringBuffer();//查询到字段
    StringBuffer fromBuff = new StringBuffer();//查询的表
    StringBuffer whereBuff = new  StringBuffer();//查询的条件
    T9DsTable dsTable = null;//数据字典中的表信息
    T9DsField dsField = null;
    List<T9DsField> dsFieldList = null;//字段集合
    int tableIndex = 0;//定义table索引
    String tablePre = "T";//定义表别名的前缀
    String tableAile = tablePre + tableIndex;//组装table的别名  如：T0
    T9ORM orm = new T9ORM();
    //1.得到数据字典中的表定义dsTable
    Map<String, Object> filters = new HashMap<String, Object>();
    filters.put("tableNo", tableNo);
    dsTable = (T9DsTable) orm.loadObjComplex(conn, T9DsTable.class, filters);
    //2.数据库表名
    String tableName = dsTable.getTableName();
   //7.组装查询的表字段
    fromBuff.append(tableName).append(" ").append(tableAile);
    //3.所有字段
    dsFieldList = dsTable.getFieldList();
    Iterator<T9DsField> dsFielditer = dsFieldList.iterator();
    //4.遍历所有字段
   while(dsFielditer.hasNext()){
      //5.得到字段名
      dsField = dsFielditer.next();
      String fieldName = dsField.getFieldName();
      fieldBuff.append(tableAile).append(".").append(fieldName);
      //8.判断是否有外键
      if ("".equals(dsField.getFkTableNo()) || dsField.getFkTableNo() == null) {
        //6.组装查询字段
      }else{
        tableIndex++;//如果是外键则table索引++
        String tableAile1 = tablePre + tableIndex;//T1
        //外表的信息
        Map<String, Object> foreFilters = new HashMap<String, Object>();
        foreFilters.put("tableNo", dsField.getFkTableNo());
        T9DsTable foreDsTable = (T9DsTable) orm.loadObjComplex(conn, T9DsTable.class, foreFilters);//数据字典中的表信息
        List<T9DsField> foreDsFieldList = foreDsTable.getFieldList();//字段集合
        String fkNameField = null;
        String fkRelaField = null;
        for (T9DsField foreDsField : foreDsFieldList) {
          
          if(dsField.getFkNameFieldNo().equals(foreDsField.getFieldNo())){
           fkNameField = foreDsField.getFieldName(); 
          }
          if(dsField.getFkRelaFieldNo().equals(foreDsField.getFieldNo())){
            fkRelaField = foreDsField.getFieldName(); 
           }
        }
        //得到外键的表名
        String foreTableName = foreDsTable.getTableName();
        //组装查询的表字段
        if("".equals(fromBuff.toString())){
          fromBuff.append(foreTableName).append(" ").append(tableAile1);
        }else{
          fromBuff.append(",").append(foreTableName).append(" ").append(tableAile1);
        }
        if("".equals(fieldBuff.toString())){
          fieldBuff.append(tableAile1).append(".").append(fkNameField).append(" ").append(fieldName).append("_Desc");
        }else{
          fieldBuff.append(",").append(tableAile1).append(".").append(fkNameField).append(" ").append(fieldName).append("_Desc");
        }
        if(!"".equals(whereBuff.toString())){
          whereBuff.append(" and ");
        }
        whereBuff.append(tableAile1).append(".").append(fkRelaField)
        .append(" = ").append(tableAile).append(".").append(fieldName);
        //判断是否为小编码
        if (!(dsField.getCodeClass() == null) &&! "".equals(dsField.getCodeClass())) {
            //得到小编码类型
          String codeClass = dsField.getCodeClass();
          if(!"".equals(whereBuff.toString())){
            whereBuff.append(" and ");
          }
          whereBuff.append(tableAile1).append(".").append("CLASS_NO")
                    .append(" = '").append(codeClass).append("'");
          //where T0.fieldName = T1.fkNameField and T1.codeClass = codeClass;
        }
      }
      if(dsFielditer.hasNext()){
        fieldBuff.append(",");
      }
    }
   if(!"".equals(fieldBuff.toString())){
     sqlBuff.append("select ").append(fieldBuff);
     if(!"".equals(fromBuff.toString())){
       sqlBuff.append(" from ").append(fromBuff);
       if(!"".equals(whereBuff.toString())){
         sqlBuff.append(" where ").append(whereBuff);
       }
     }
   }
   System.out.println("sql : "+sqlBuff.toString());
    return sqlBuff;
  }
  /**
   * 得到数据库中的数据
   * @param conn
   * @param tableNo
   * @return
   * @throws Exception 
   */
   public ResultSet loadData(Connection conn,String tableNo) throws Exception{
     String sql = toSqlString(conn, tableNo).toString();
     PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
         ResultSet.CONCUR_READ_ONLY);
     ResultSet rs = ps.executeQuery();
     return rs;
   }
   
   /**
    * 将rs得到的数据转换成json格式的数据
    * @param rs
    * @param pageNum
    * @param pageRows
    * @return
   * @throws Exception 
    */
   public StringBuffer toJson(Connection conn,String tableNo) throws Exception{
     ResultSet rs = null;
     rs = loadData(conn, tableNo);
     while(rs.next()){
       
     }
     return null;
   }
   /**
    * 将rs得到的数据转换成json格式的数据
    * @param rs
    * @param pageNum
    * @param pageRows
    * @return
   * @throws Exception 
    */
   public StringBuffer toJson(Connection conn,String tableNo,Integer pageNum,Integer pageRows) throws Exception{
     ResultSet rs = null;
     rs = loadData(conn, tableNo);
     ResultSetMetaData rsmt = rs.getMetaData();
     StringBuffer json = new StringBuffer("{");
     if((pageNum != null && pageNum >= 0 )
         && (pageRows != null && pageRows > 0 )){//分页操作
       rs.last();
       int total = rs.getRow();
       // System.out.println();
       System.out.println(total);
       json = new StringBuffer("{'total':" + total + ",'records':[");
       System.out.println(pageNum * pageRows + 1);
       rs.absolute(pageNum * pageRows + 1);
       int index = (total - pageNum * pageRows) < pageRows ? (total - pageNum
           * pageRows) : pageRows;
       System.out.println(index);
       for (int j = 0; j < index; j++) {
         json.append("{ ");
         System.out.println("====================");
         for (int i = 1; i <= rsmt.getColumnCount(); i++) {
           String dbFieldName = rsmt.getColumnName(i);
           Object value = rs.getObject(i);
           if (int.class.isInstance(value)
               || Integer.class.isInstance(value)
               || double.class.isInstance(value)
               || Double.class.isInstance(value)) {
             value = T9Utility.null2Empty(value.toString());
           } else {
             System.out.println(value);
             if (value == null) {
               value = "\"" + T9Utility.null2Empty(null) + "\"";
             } else {
               value = "\"" + T9Utility.null2Empty(value.toString()) + "\"";
             }
           }
           if (value == null) {
             value = "";
           }
           System.out.println(dbFieldName+" : "+ rsmt.getTableName(i));
           String fieldName = T9StringFormat.unformat(dbFieldName);
           json.append(fieldName).append(":").append(value);
           if (i <= rsmt.getColumnCount() - 1) {
             json.append(",");
           }
         }
         json.append(" }");
         if (j < index - 1) {
           json.append(",");
         }
         rs.next();
       }
       json.append(" ]");
     }
     json.append(" }");
     return json;
   }
   /**
    * 将rs得到的数据转换成json格式的数据
    * @param rs
    * @param pageNum
    * @param pageRows
    * @return
   * @throws Exception 
    */
   public StringBuffer toJson2Flex(Connection conn,String tableNo,Integer pageNum,Integer pageRows) throws Exception{
     ResultSet rs = null;
     rs = loadData(conn, tableNo);
     ResultSetMetaData rsmt = rs.getMetaData();
     StringBuffer json = new StringBuffer("{");
     if((pageNum != null && pageNum >= 0 )
         && (pageRows != null && pageRows > 0 )){//分页操作
       rs.last();
       int total = rs.getRow();
       // System.out.println();
       System.out.println(total);
       json = new StringBuffer("{'total':" + total + ",'records':[");
       System.out.println(pageNum * pageRows + 1);
       rs.absolute(pageNum * pageRows + 1);
       int index = (total - pageNum * pageRows) < pageRows ? (total - pageNum
           * pageRows) : pageRows;
       System.out.println(index);
       for (int j = 0; j < index; j++) {
         json.append("{ ");
         System.out.println("====================");
         for (int i = 1; i <= rsmt.getColumnCount(); i++) {
           String dbFieldName = rsmt.getColumnName(i);
           Object value = rs.getObject(i);
           if (int.class.isInstance(value)
               || Integer.class.isInstance(value)
               || double.class.isInstance(value)
               || Double.class.isInstance(value)) {
             value = T9Utility.null2Empty(value.toString());
           } else {
             System.out.println(value);
             if (value == null) {
               value = "\"" + T9Utility.null2Empty(null) + "\"";
             } else {
               value = "\"" + T9Utility.null2Empty(value.toString()) + "\"";
             }
           }
           if (value == null) {
             value = "";
           }
           System.out.println(dbFieldName+" : "+ rsmt.getTableName(i));
           String fieldName = T9StringFormat.unformat(dbFieldName);
           json.append(fieldName).append("_").append(j).append(":").append(value);
           if (i <= rsmt.getColumnCount() - 1) {
             json.append(",");
           }
         }
         json.append(" }");
         if (j < index - 1) {
           json.append(",");
         }
         rs.next();
       }
       json.append(" ]");
     }
     json.append(" }");
     return json;
   }
}
