package t9.core.exps;

/**
 * 无效的数据表异常
 * @author YZQ
 * @version 1.0
 * @date 2006-8-14
 */
public class T9InvalidTableException extends Exception {
  /**
   * 构造方法
   * @param msrg
   */
  public T9InvalidTableException(String msrg) {
    super(msrg);
  }
}
