package t9.core.data;

import java.util.ArrayList;
import java.util.List;


/**
 * 表描述
 * @author YZQ
 * @version 1.0
 * @date 2006-8-9
 */
public class T9DsTable {
  /**  **/
  private int seqId = 0;
  /** 编码5位长 **/
  private String tableNo = null;
  /**  **/
  private String tableName = null;


  /** Java类名 **/
  private String className = null;
  /**  **/
  private String tableDesc = null;
  /** 1=代码表,2=小编码表,3=参数表,4=数据主表,5=数据从表,6=多对多关系表 **/
  private String categoryNo = null;
  /**  **/
  private String dbNo = null;
  /** 字段列表 **/
 private ArrayList<T9DsField> fieldList = null;
  
 public String getClassName() {
   return className;
 }
 public void setClassName(String className) {
   this.className = className;
 }
  /**
   * 取得对象的个数   */
  public int getFieldCnt() {
    return fieldList.size();
  }
  /**
   * 按索引取值   * @param index   索引
   */
  public T9DsField getField(int index) {
    if (index < 0 || index >= fieldList.size()) {
      return null;
    }
    return (T9DsField)fieldList.get(index);
  }
  /**
   * 增加对象
   * @param field   对象
   */
  public void addField(T9DsField field) {
    fieldList.add(field);
  }
  /**
   * 增加对象
   * @param fieldList   对象
   */
  public void addField(ArrayList<T9DsField> fieldList) {
    fieldList.addAll(fieldList);
  }

  /**
   *
   */
  public int getSeqId() {
    return this.seqId;
  }

  /**
   *
   * @param seqId
   */
  public void setSeqId(String seqId) {
    int seq = Integer.parseInt(seqId);
    this.seqId = seq;
  }
  public void setSeqId(int seqId) {
   
    this.seqId = seqId;
  }

  /**
   *
   */
  public String getTableNo() {
    return this.tableNo;
  }

  /**
   *
   * @param tableNo
   */
  public void setTableNo(String tableNo) {
    this.tableNo = tableNo;
  }

  /**
   *
   */
  public String getTableName() {
    return this.tableName;
  }

  /**
   *
   * @param tableName
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   *
   */
  public String getTableDesc() {
    return this.tableDesc;
  }

  /**
   *
   * @param tableDesc
   */
  public void setTableDesc(String tableDesc) {
    this.tableDesc = tableDesc;
  }

  /**
   *
   */
  public String getDbNo() {
    return this.dbNo;
  }

  /**
   *
   * @param dbNo
   */
  public void setDbNo(String dbNo) {
    this.dbNo = dbNo;
  }

  /**
   *
   */
  public String getCategoryNo() {
    return this.categoryNo;
  }

  /**
   *
   * @param categoryNo
   */
  public void setCategoryNo(String categoryNo) {
    this.categoryNo = categoryNo;
  }
  public ArrayList<T9DsField> getFieldList() {
    return fieldList;
  }
  public void setFieldList(ArrayList<T9DsField> fieldList) {
    this.fieldList = fieldList;
  }
/*  @Override
  public String toString() {
    String tos = "categoryNo : " +this.categoryNo + " className : " +this.className+" dbNo : "+this.dbNo+" seqId : "+this.seqId+" tableName : "+this.tableName+" tableNo : "+this.tableNo;
    ArrayList<T9DsField> l = getFieldList();
    if(l!=null)
    for (T9DsField t9DsField : l) {
      t9DsField.toString();
    }
    return tos;
  }*/
  @Override
  public String toString() {
   
    ArrayList<T9DsField> l = getFieldList();
    if(l!=null)
      for (T9DsField t9DsField : l) {
        //System.out.println(t9DsField.toString());
      }
    return "T9DsTable [categoryNo=" + categoryNo + ", className=" + className
        + ", dbNo=" + dbNo + ", fieldList=" + fieldList + ", seqId=" + seqId
        + ", tableDesc=" + tableDesc + ", tableName=" + tableName
        + ", tableNo=" + tableNo + "]";
  }
}
