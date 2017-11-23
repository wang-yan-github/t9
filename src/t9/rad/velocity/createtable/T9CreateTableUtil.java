package t9.rad.velocity.createtable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.data.T9DsType;
import t9.core.util.db.T9ORM;

public class T9CreateTableUtil {

  private static final String CONSTRAINTS = "";
  private static final String PRIMARYkEY  = "PRIMARY KEY";
  private static final String FOREIGNKEY  = "FOREIGN KEY";
  private static final String REFERENCES  = "REFERENCES";
  private static final String NOTNULL     = "NOT NULL";
  private static final String SPACE       = " ";

  /**
   * 通过表名称创建数据库表
   * 
   * @param tableName
   */
  public static void createTableByName(Connection conn, String[] tableName) {

  }

  /**
   * 通过表编码创建数据表
   * 
   * @param tableNo
   * @throws Exception 
   */
  public static T9TableInfo[] createTableById(Connection conn, String[] tableNo,String dialect) throws Exception {
    /*
     * create table
     */
    T9TableInfo[] tis = new T9TableInfo[tableNo.length];
    for (int i = 0 ; i < tableNo.length ; i++ ) {
      T9TableInfo ti = createTableById(conn, tableNo[i], dialect);
      tis[i] = ti;
    }
    return tis;
  }

  /**
   * 通过表编码创建数据表
   * 
   * @param conn
   * @param tableNo
   * @throws Exception
   */
  public static T9TableInfo createTableById(Connection conn, String tableNo,String dialect)
      throws Exception {
    /*
     * create table ${tableName} ( ${fieldName} ${type}(${数据长度}) ${约束条件} )
     */
    T9ORM orm = new T9ORM();
    Map<String, Object> filters = new HashMap<String, Object>();
    filters.put("TABLE_NO", tableNo);
    T9DsTable dsTable = (T9DsTable) orm.loadObjComplex(conn, T9DsTable.class,
        filters);
    T9TableInfo ti = new T9TableInfo();
   
    String tableName = dsTable.getTableName();// 表名称
    ti.setTableName(tableName);
    List<T9DsField> dsFields = dsTable.getFieldList();

    for (T9DsField dsField : dsFields) {
      T9TableColumn tc = new T9TableColumn();
      String fieldName = dsField.getFieldName();// 字段名
      // 组织类似于 ： CONSTRAINT DS_TABLE_DB_NO FOREIGN KEY(DB_NO) REFERENCES
      // DATA_BASE (DB_NO)
      tc.setColumnName(fieldName);
      StringBuffer sbf = new StringBuffer();//外键关联
      StringBuffer sb = new StringBuffer();//字段信息
      StringBuffer sbc = new StringBuffer();//完整性约束
      
      String fkTableNo = dsField.getFkTableNo();// 外键参照表
      String fkRealNameNo = dsField.getFkRelaFieldNo();// 外键参照编码字段编码
      String fkfilter = dsField.getFkFilter();// 外键参照筛选条件
      if (fkTableNo != null || "".equals(fkTableNo)) {
        // 组织外键关联
        String fkTableName = getTableNameByNo(conn, fkTableNo);
        String fkRealName = getFieldNameByNo(conn, fkRealNameNo);
        String foreKeyStr = "(" + fieldName + ")";
        String foreStr = fkTableName + "(" + fkRealName + ")";
        sbf.append("CONSTRAINT").append(SPACE)
            .append(tableName).append("_").append(fieldName).append(SPACE);
        sbf.append(FOREIGNKEY).append(SPACE)
            .append(foreKeyStr).append(SPACE)
            .append(REFERENCES).append(SPACE) 
            .append(foreStr);
      }

      // 组织 类似于: SEQ_ID NUMBER(38) DEFAULT 1 ;
      String defaultValue = dsField.getDefaultValue();// 缺省值
      int fieldPrecision = dsField.getFieldPrecision();// 数位长度
      int fieldScale = dsField.getFieldScale();// 小数位数
      int dataType = dsField.getDataType();// 数据类型
      
      String typeName = T9DBDialectUtil.getDialect(dialect).getTypeName(dataType);
      String precisionStr = "";
      String defaultStr = "";
      sb.append(typeName);

      if (fieldPrecision != 0) {
        if (T9DsType.isDecimalType(dataType) && fieldScale != 0) {
          precisionStr = "(" + fieldPrecision + "," + fieldScale + ")";
        } else {
          precisionStr = "(" + fieldPrecision + ")";
        }
        sb.append(precisionStr).append(SPACE);
      }
      if (defaultValue != null) {
        defaultStr = "DEFAULT" + SPACE + defaultValue;
        sb.append(defaultStr).append(SPACE);
      }
      // 组织 类似于 : PRIMARY KEY NOT NULL UNIQUE
      // MYSQL 数据库中的自增字段 AUTO_INCREMENT;
      // Oracle 数据库中执行存储过程 exec pr_createidentitycolumn('DS_TABLE','SEQ_ID');

      String isPrimaryKey = dsField.getIsPrimaryKey();// 是否是主键 PRIMARY KEY
      String isIdentity = dsField.getIsIdentity();// 是否自增 AUTO_INCREMENT
      String isMustFill = dsField.getIsMustFill();// 是否必填 NOT NULL
   
      if (isPrimaryKey != null && "1".equals(isPrimaryKey)) {
        sbc.append(PRIMARYkEY).append(SPACE);
      }
      if (isMustFill != null && "1".equals(isMustFill)) {
        sbc.append(NOTNULL).append(SPACE);
      }
      if ("1".equals(isIdentity)) {
        ti.setAutoIncreaseField(fieldName);
        tc.setAutoIncrease(true);
      }
      //System.out.println( "sbf : " + sbf.toString());
      //System.out.println(" sb : " + sb.toString());
      //System.out.println(" sbc : " + sbc.toString() );
      tc.setConstraint(sbc.toString());
      tc.setForegin( sbf.toString());
      tc.setType(sb.toString());
      ti.addColumn(tc);
    }
    return ti;
  }

  private static String getTableNameByNo(Connection conn, String filter)
      throws Exception {
    String sql = "select TABLE_NAME FROM DS_TABLE WHERE TABLE_NO = '" + filter
        + "'";
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(sql);
    String result = null;
    while (rs.next()) {
      result = rs.getString(1);
    }
    return result;
  }

  private static String getFieldNameByNo(Connection conn, String filter)
      throws Exception {
    String sql = "select FIELD_NAME FROM DS_FIELD WHERE FIELD_NO = '" + filter
        + "'";
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(sql);
    String result = null;
    while (rs.next()) {
      result = rs.getString(1);
    }
    return result;
  }
}
