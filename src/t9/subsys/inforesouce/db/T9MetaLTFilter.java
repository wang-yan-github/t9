package t9.subsys.inforesouce.db;

import t9.core.util.T9Utility;

/**
 * 小于过滤器
 * @author yzq
 *
 */
public class T9MetaLTFilter implements T9IMetaFilter {
  private String exprStr = null;
  /**
   * 解析表达式
   * @param exprStr
   */
  public void parse(String exprStr) {
    this.exprStr = exprStr;
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
    return valueStr.compareToIgnoreCase(this.exprStr) <= 0;
  }
}
