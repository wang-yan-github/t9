package t9.user.api.core.data;

import t9.core.util.T9ReflectUtility;

public class T9PersonWrap {
  //用户对象
  private Object personObj = null;
  /**
   * 构造函数
   * @param personObj
   */
  public T9PersonWrap(Object personObj) {
    this.personObj = personObj;
  }
  
  /**
   * 取得流水号
   * @return
   */
  public int getSeqId() {
    try {
      return ((Integer)T9ReflectUtility.getValue(personObj, "seqId")).intValue();
    }catch(Exception ex) {
      return 0;
    }
  }
  
  /**
   * 取得用户ID
   * @return
   */
  public String getUserId() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "userId"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得用户名
   * @return
   */
  public String getUserName() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "userName"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得别名
   * @return
   */
  public String getByname() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "byname"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得用户角色
   * @return
   */
  public String getUserPriv() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "userPriv"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得辅助角色串
   * @return
   */
  public String getUserPrivOther() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "userPrivOther"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得管理范围
   * @return
   */
  public String getPostPriv() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "postPriv"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得管理范围-指定部门
   * @return
   */
  public String getPostDept() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "postDept"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得部门ID
   * @return
   */
  public int getDeptId() {
    try {
      return ((Integer)T9ReflectUtility.getValue(personObj, "deptId")).intValue();
    }catch(Exception ex) {
      return 0;
    }
  }
  
  /**
   * 取得辅助部门
   * @return
   */
  public String getDeptIdOther() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "deptIdOther"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得昵称
   * @return
   */
  public String getNickName() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "nickName"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 取得用户编码
   * @return
   */
  public int getUserNo() {
    try {
      return ((Integer)T9ReflectUtility.getValue(personObj, "userNo")).intValue();
    }catch(Exception ex) {
      return 0;
    }
  }
  
  /**
   * 取得唯一标识
   * @return
   */
  public String getUniqueId() {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, "uniqueId"));
    }catch(Exception ex) {
      return "";
    }
  }
  
  /**
   * 按字段名字取值
   * @return
   */
  public String getString(String fieldName) {
    try {
      return String.valueOf(T9ReflectUtility.getValue(personObj, fieldName));
    }catch(Exception ex) {
      return "";
    }
  }
}
