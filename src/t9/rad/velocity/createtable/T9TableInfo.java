package t9.rad.velocity.createtable;

import java.util.ArrayList;
import java.util.List;

public class T9TableInfo {

  private String tableName;
  private String autoIncreaseField;
  private ArrayList<T9TableColumn> columns = new ArrayList<T9TableColumn>();;
  
  public T9TableInfo() {
   
  }
  public String getTableName() {
    return tableName;
  }
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
  public String getAutoIncreaseField() {
    return autoIncreaseField;
  }
  public void setAutoIncreaseField(String autoIncreaseField) {
    this.autoIncreaseField = autoIncreaseField;
  }
  public ArrayList<T9TableColumn> getColumns() {
    return columns;
  }
  public void setColumns(ArrayList<T9TableColumn> columns) {
    this.columns = columns;
  }
  public void addColumn(T9TableColumn tc) {
    this.columns.add(tc);
  }
}
