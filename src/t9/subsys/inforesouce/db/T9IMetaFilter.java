package t9.subsys.inforesouce.db;

/**
 * 元数据筛选器
 * @author yzq
 *
 */
public interface T9IMetaFilter {
  /**
   * 解析表达式
   * @param exprStr
   */
  public void parse(String exprStr);
  /**
   * 是否匹配
   * @param valueStr
   * @return
   */
  public boolean isMatch(String valueStr);
}
