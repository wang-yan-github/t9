package t9.subsys.inforesouce.db;

import t9.core.util.T9Utility;

/**
 * 范围过滤器
 * @author yzq
 *
 */
public class T9MetaBTFilter implements T9IMetaFilter {
  //表达式字符串
  private String exprStr = null;
  //左侧边界值
  private String exprStrFrom = null;
  //右侧边界值
  private String exprStrTo = null;
  /**
   * 解析表达式
   * @param exprStr
   */
  public void parse(String exprStr) {
    this.exprStr = exprStr;
    if (T9Utility.isNullorEmpty(exprStr)) {
      return;
    }
    if (exprStr.startsWith(",")) {
      this.exprStrTo = exprStr.substring(1);
    }else if (exprStr.endsWith(",")) {
      this.exprStrFrom = exprStr.substring(exprStr.length() - 1, exprStr.length());
    }else if (exprStr.indexOf(",") > 0) {
      String[] tmpArray = exprStr.split(",");
      this.exprStrFrom = tmpArray[0].trim();
      this.exprStrTo = tmpArray[1].trim();
    }else {
      this.exprStrFrom = exprStr;
    }
    
  }
  /**
   * 是否匹配
   * @param valueStr
   * @return
   */
  public boolean isMatch(String valueStr) {
    if (T9Utility.isNullorEmpty(this.exprStr)) {
      return true;
    }
    if (valueStr == null) {
      valueStr = "";
    }
    if (!T9Utility.isNullorEmpty(this.exprStrFrom)) {
      if (valueStr.compareToIgnoreCase(this.exprStrFrom) <= 0) {
        return false;
      }
    }
    if (!T9Utility.isNullorEmpty(this.exprStrTo)) {
      if (valueStr.compareToIgnoreCase(this.exprStrTo) >= 0) {
        return false;
      }
    }
    return true;
  }
}
